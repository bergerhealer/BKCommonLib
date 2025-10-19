package com.bergerkiller.bukkit.common.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.bases.DeferredSupplier;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.gson.MapResourcePackDeserializer;
import com.bergerkiller.bukkit.common.map.gson.types.ResourcePackDescription;
import com.bergerkiller.bukkit.common.map.util.ItemModel;
import com.bergerkiller.mountiplex.logic.TextValueSequence;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.error.YAMLException;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.internal.resources.ResourceOverrides;
import com.bergerkiller.bukkit.common.internal.resources.builtin.GeneratedModel;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackArchive;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackAutoArchive;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackClientArchive;
import com.bergerkiller.bukkit.common.map.util.ModelInfoLookup;
import com.bergerkiller.bukkit.common.map.util.BlockModelState;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.map.util.ModelInfo;
import com.bergerkiller.bukkit.common.map.util.VanillaResourcePack;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.utils.DebugUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;
import com.bergerkiller.bukkit.common.wrappers.ItemRenderOptions;
import com.bergerkiller.bukkit.common.wrappers.RenderOptions;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;

/**
 * Resource Management class that loads models and textures from the Vanilla Minecraft jar or zip resource pack archives.<br>
 * <br>
 * <b>Before using, call {@link VanillaResourcePack#load() VANILLA.load()} when enabling your plugin to download
 * and install this Minecraft client jar. This downloading is only performed once.</b><br>
 * <br>
 * The Minecraft client resources are owned by Mojang. These assets are installed separately from BKCommonLib.
 * BKCommonLib, nor its developers, distribute (illegal) copies of the Minecraft client.
 * We assume you have already accepted the Minecraft EULA to run the server. If you have not,
 * please read <a href="https://account.mojang.com/documents/minecraft_eula">https://account.mojang.com/documents/minecraft_eula.</a>
 */
public class MapResourcePack {
    /**
     * The default VANILLA resource pack consists of the default Minecraft
     * models and textures. These resources are provided by the Minecraft client jar.
     * Resource packs extend or override these default resources.<br>
     * <br>
     * <b>Before using, call {@link VanillaResourcePack#load() VANILLA.load()} when enabling your plugin to download
     * and install this Minecraft client jar. This downloading is only performed once. Doing this here prevents
     * long lag pauses the first time your plugin accesses this resource pack.
     * </b><br>
     * <br>
     * The Minecraft client resources are owned by Mojang. These assets are installed separately from BKCommonLib.
     * BKCommonLib, nor its developers, distribute (illegal) copies of the Minecraft client.
     * We assume you have already accepted the Minecraft EULA to run the server. If you have not,
     * please read <a href="https://account.mojang.com/documents/minecraft_eula">https://account.mojang.com/documents/minecraft_eula.</a>
     */
    public static final VanillaResourcePack VANILLA = new VanillaResourcePack();

    /**
     * The SERVER resource pack consists of the default Minecraft models and textures,
     * unless a special resource pack is defined in server.properties. Then the resource pack
     * defined there is downloaded and used for providing the textures and models.<br>
     * <br>
     * <b>Before using, call {@link MapResourcePack#load() SERVER.load()} when enabling your plugin to download
     * and install this Minecraft client jar, and the resource pack of the server if it is defined.
     * This downloading is only performed once. Doing this here prevents long lag pauses the first time
     * your plugin accesses this resource pack.</b>
     */
    public static final MapResourcePack SERVER = builder().resourcePackPath("server").build();

    private final MapResourcePack baseResourcePack;
    private final PackVersion preferredPackVersion;
    protected MapResourcePackArchive archive;
    protected Metadata metadata = null; // loaded in load()
    private final Map<String, MapTexture> textureCache = new HashMap<String, MapTexture>();
    private final Map<String, ConfigurationNode> yamlCache = new HashMap<String, ConfigurationNode>();
    private final Map<String, ItemModel> itemModelCache = new HashMap<>();
    private final Map<String, Model> modelCache = new HashMap<String, Model>();
    private final Map<BlockRenderOptions, Model> blockModelCache = new HashMap<BlockRenderOptions, Model>();
    private BlockRenderProvider currProvider = null;
    private MapResourcePackDeserializer deserializer = null;
    private boolean loaded = false;

    /**
     * Returns a new {@link Builder} for creating a new {@link MapResourcePack}. Configure the
     * resource pack path and hash or other settings to change how the resource pack behaves.
     * The builder offers more fine-tuned control over behavior than the constructors do.<br>
     * <br>
     * Call {@link Builder#build()} after configuring to create the MapResourcePack.
     * It uses the {@link #VANILLA} resource pack as a base pack by default.
     *
     * @return Builder
     */
    public static Builder builder() {
        return new Builder(VANILLA);
    }

    /**
     * Creates a new resource pack, extending the default {@link #VANILLA} resource pack
     * 
     * @param resourcePackPath of the resource pack to load. File or URL.
     */
    public MapResourcePack(String resourcePackPath) {
        this(builder().resourcePackPath(resourcePackPath));
    }

    /**
     * Creates a new resource pack, extending the default {@link #VANILLA} resource pack
     * 
     * @param resourcePackPath of the resource pack to load. File or URL.
     * @param resourcePackHash SHA1 hash of the resource pack to detect changes (when URL)
     */
    public MapResourcePack(String resourcePackPath, String resourcePackHash) {
        this(builder().resourcePackPath(resourcePackPath)
                .resourcePackHash(resourcePackHash));
    }

    /**
     * Creates a new resource pack, extending another one
     * 
     * @param baseResourcePack to extend
     * @param resourcePackPath of the resource pack to load. File path or URL.
     */
    public MapResourcePack(MapResourcePack baseResourcePack, String resourcePackPath) {
        this(baseResourcePack, resourcePackPath, "");
    }

    /**
     * Creates a new resource pack, extending another one
     *
     * @param baseResourcePack to extend
     * @param resourcePackPath of the resource pack to load. File path or URL.
     * @param resourcePackHash SHA1 hash of the resource pack to detect changes (when URL)
     */
    public MapResourcePack(MapResourcePack baseResourcePack, String resourcePackPath, String resourcePackHash) {
        this(new Builder(baseResourcePack)
                .resourcePackPath(resourcePackPath)
                .resourcePackHash(resourcePackHash));
    }

    /**
     * Creates a new resource pack making use of the configuration set in the {@link Builder}
     * 
     * @param builder Build configuration for the new resource pack
     */
    protected MapResourcePack(Builder builder) {
        this.baseResourcePack = builder.baseResourcePack;
        this.preferredPackVersion = builder.preferredPackVersion;
        this.archive = null;

        // Detect the appropriate archive to use

        // Server-defined. Take over the options from the server.properties
        String resourcePackPath = builder.resourcePackPath;
        String resourcePackHash = builder.resourcePackHash;
        if (resourcePackPath != null && resourcePackPath.equalsIgnoreCase("server")) {
            if (CommonBootstrap.isTestMode()) {
                resourcePackPath = "vanilla";
            } else {
                MinecraftServerHandle mcs = MinecraftServerHandle.instance();
                resourcePackPath = mcs.getResourcePack();
                resourcePackHash = mcs.getResourcePackHash();
            }
        }

        // Vanilla Minecraft client, without any other resource packs applied
        if (resourcePackPath == null || resourcePackPath.isEmpty() ||
            resourcePackPath.equalsIgnoreCase("vanilla") || resourcePackPath.equalsIgnoreCase("default")
        ) {
            // Vanilla client resource pack.
            // If a base resource pack is already defined, don't use any archive
            // Otherwise, use the client archive
            if (this.baseResourcePack == null) {
                this.archive = new MapResourcePackClientArchive();
            } else {
                this.archive = null;
            }
            return;
        }

        // Auto-detect the right way to use it
        this.archive = new MapResourcePackAutoArchive(resourcePackPath, resourcePackHash);
    }

    /**
     * Gets the contents of this resource pack's pack.mcmeta file. If this file did not exist or
     * fails to load, returns a fallback.
     *
     * @return Pack metadata
     */
    public Metadata getMetadata() {
        this.handleLoad(true, false);
        return this.metadata;
    }

    /**
     * Gets the base (parent) resource pack that this resource pack extends. If this resource pack
     * is the lowest level, such as {@link #VANILLA}, then null is returned.
     *
     * @return Base resource pack of this resource pack, or null if this is the lowest layer
     */
    public MapResourcePack getBase() {
        return this.baseResourcePack;
    }

    /**
     * Initializes the resource pack and all underlying resource packs.
     * This is called automatically once the resource packs are first accessed.
     * To initialize up-front and avoid lazy initialization at runtime, call this method
     * at the very beginning.
     */
    public void load() {
        handleLoad(false, false);
    }

    protected void handleLoad(boolean lazy, boolean recurse) {
        if (this.loaded) {
            return;
        }
        this.loaded = true;
        this.metadata = Metadata.fallback("Failed to load resource pack");
        this.metadata.preferredPackVersion = this.preferredPackVersion;
        if (lazy && !recurse) {
            Logging.LOGGER_MAPDISPLAY.warning("[Developer] You must call MapResourcePack.load() when enabling your plugin!");
            Logging.LOGGER_MAPDISPLAY.warning("[Developer] This avoids stalling the server while downloading large resource packs/Minecraft client.");
            Logging.LOGGER_MAPDISPLAY.warning("[Developer] Potential plugins that caused this: " + DebugUtil.getPluginCauses());
        }
        if (this.archive != null) {
            this.archive.load(lazy);

            if (this.deserializer == null) {
                this.deserializer = MapResourcePackDeserializer.create();
            }
            this.metadata = this.archive.tryLoadMetadata(this.deserializer);
            this.metadata.preferredPackVersion = this.preferredPackVersion;
        }

        // Recursively load the parent packs too
        if (this.baseResourcePack != null) {
            this.baseResourcePack.handleLoad(lazy, true);
        }
    }

    /**
     * Clears all cached models and textures, forcing a reload from the pack archive(s)
     */
    public void clearCache() {
        textureCache.clear();
        modelCache.clear();
        blockModelCache.clear();
    }

    /**
     * Obtains the model for a particular Block in the World. Block-specific rendering
     * options are efficiently handled here.
     * 
     * @param block
     * @return block model
     */
    public final Model getBlockModel(Block block) {
        return getBlockModel(WorldUtil.getBlockData(block).getRenderOptions(block));
    }

    /**
     * Obtains the model for a particular Block in the World. Block-specific rendering
     * options are efficiently handled here.
     * 
     * @param world of the block
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @return block model
     */
    public final Model getBlockModel(World world, int x, int y, int z) {
        return getBlockModel(WorldUtil.getBlockData(world, x, y, z).getRenderOptions(world, x, y, z));
    }

    /**
     * Obtains the model for a particular Block by Block Material Data, specific to that Block's data value.
     * 
     * @param blockMaterial of the block
     * @return block model
     */
    public final Model getBlockModel(Material blockMaterial) {
        return getBlockModel(BlockData.fromMaterial(blockMaterial));
    }

    /**
     * Obtains the model for a particular Block by Block Material Data, specific to that Block's data value.
     * 
     * @param blockMaterial of the block
     * @param data of the block
     * @return block model
     */
    @SuppressWarnings("deprecation")
    public final Model getBlockModel(Material blockMaterial, int data) {
        return getBlockModel(BlockData.fromMaterialData(blockMaterial, data));
    }

    /**
     * Obtains the model for a particular Block by Block Material Data, specific to that Block's data value.
     * 
     * @param blockData of the block
     * @return block model
     */
    public Model getBlockModel(BlockData blockData) {
        return getBlockModel(blockData.getDefaultRenderOptions());
    }

    /**
     * Obtains the model for a particular Block, specific to that Block's data value and render options.
     * This is the method of choice for fastest lookup.
     * 
     * @param blockRenderOptions
     * @return block model
     */
    public Model getBlockModel(BlockRenderOptions blockRenderOptions) {
        Model model = blockModelCache.get(blockRenderOptions);
        if (model == null) {
            if (blockRenderOptions.getBlockData() != null) {
                model = this.loadBlockModel(blockRenderOptions);
            }
            if (model == null) {
                model = Model.createPlaceholderModel(blockRenderOptions);
            }
            blockModelCache.put(blockRenderOptions, model);
        }
        return model;
    }

    /**
     * Loads a JSON-syntax model from this resource pack and decodes only the
     * metadata of the model. No information is loaded that is needed for rendering,
     * such as textures and boxes. This can be used to display information about
     * models stored in this resource pack.<br>
     * <br>
     *  If the model could not be found or failed to be decoded, a placeholder
     *  is returned which can be checked with {@link ModelInfo#isPlaceholder()}
     *
     * @param path Path to the model. Must not be null.
     * @return the model information
     */
    public ModelInfo getModelInfo(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Input path is null");
        }

        // Try cache first, just in case
        // Makes stuff easier
        {
            Model model = modelCache.get(path);
            if (model != null) {
                return model;
            }
        }

        // Load from resource pack. Do not cache (?)
        ModelInfo info = openGsonObject(ModelInfo.class, ResourceType.MODELS, path);
        if (info != null) {
            info.setName(path);
            return info;
        } else {
            return ModelInfo.createPlaceholder(path);
        }
    }

    /**
     * Loads a JSON-syntax item model from this resource pack, which contains
     * details about the override predicates configured. This information can
     * then be used to find unique items that are tied to certain model names.<br>
     * <br>
     * If the item model does not exist, returns a dummy one that only returns
     * {@link ItemModel.MinecraftModel#NOT_SET}. This can be checked using
     * {@link ItemModel#hasValidModels()}, which will return false in that case.
     *
     * @param itemStack ItemStack whose unique item model configuration to retrieve.
     *                  Ignores properties of the item other than its name.
     * @return the item model configuration
     */
    public ItemModel getItemModelConfig(ItemStack itemStack) {
        return getItemModelConfig(CommonItemStack.of(itemStack));
    }

    /**
     * Loads a JSON-syntax item model from this resource pack, which contains
     * details about the override predicates configured. This information can
     * then be used to find unique items that are tied to certain model names.<br>
     * <br>
     * If the item model does not exist, returns a dummy one that only returns
     * {@link ItemModel.MinecraftModel#NOT_SET}. This can be checked using
     * {@link ItemModel#hasValidModels()}, which will return false in that case.
     *
     * @param itemStack ItemStack whose unique item model configuration to retrieve.
     *                  Ignores properties of the item other than its name.
     * @return the item model configuration
     */
    public ItemModel getItemModelConfig(CommonItemStack itemStack) {
        return getItemModelConfig(ModelInfoLookup.lookupItem(itemStack));
    }

    /**
     * Loads a JSON-syntax item model from this resource pack, which contains
     * details about the override predicates configured. This information can
     * then be used to find unique items that are tied to certain model names.<br>
     * <br>
     * If the item model does not exist, returns a dummy one that only returns
     * {@link ItemModel.MinecraftModel#NOT_SET}. This can be checked using
     * {@link ItemModel#hasValidModels()}, which will return false in that case.
     *
     * @param itemName Name of the item, e.g. "golden_pickaxe". Is allowed to
     *                 contain namespaces.
     * @return the item model configuration
     */
    public ItemModel getItemModelConfig(String itemName) {
        {
            ItemModel cached = itemModelCache.get(itemName);
            if (cached != null) {
                return cached;
            }
        }

        // Look up the vanilla item that will display this item model name
        CommonItemStack baseItemStack = ModelInfoLookup.findItemStackByModelName(itemName).orElse(null);

        // Attempt to load the item model details. This could fail if files are corrupted or invalid.
        ItemModel.Root root;
        if (getMetadata().hasItemModels()) {
            root = this.openGsonObject(ItemModel.Root.class, ResourceType.ITEMS, itemName);
        } else {
            ItemModel.MinecraftModel vanillaModel = ItemModel.MinecraftModel.of("item/" + itemName);
            root = new ItemModel.Root();
            root.model = vanillaModel;
            if (getMetadata().hasItemPredicateOverrides()) {
                ItemModel.Overrides overrides = openGsonObject(ItemModel.Overrides.class, ResourceType.MODELS, vanillaModel.model);
                if (overrides != null) {
                    overrides.fallback = vanillaModel;
                    if (baseItemStack != null) {
                        for (ItemModel.Overrides.OverriddenModel override : overrides.overrides) {
                            override.itemStack = override.tryMakeMatching(baseItemStack).orElse(null);
                        }
                    }
                    root.model = overrides;
                }
            }
        }

        // If parsing failed, produce a fallback model
        if (root == null) {
            root = new ItemModel.Root();
            root.model = ItemModel.MinecraftModel.NOT_SET;
        }

        // Provide a base item, which is used by the override listing
        root.baseItemStack = baseItemStack;

        // Cache for next time
        itemModelCache.put(itemName, root);

        return root;
    }

    /**
     * Lists all the {@link ResourceType#ITEMS item models} stored in this resource pack
     * that have been overridden compared to the {@link MapResourcePack#VANILLA vanilla} resource
     * pack. If this is the vanilla resource pack, returns an empty set.<br>
     * <br>
     * For resource packs for version 1.21.4, it reads these files in the items assets folder.
     * On versions before that, it decodes the predicates stored in the models/item assets folder.
     *
     * @return Set of names of items without extension that are overridden by this resource pack.
     *         These names can be directly used with {@link #getItemModelConfig(String)}
     *         to read what overrides have been configured. This set is unmodifiable.
     */
    public Set<String> listOverriddenItemModelNames() {
        Set<String> allOverridenModels = new LinkedHashSet<>();
        for (MapResourcePack p = this; p != null && p != MapResourcePack.VANILLA; p = p.getBase()) {
            for (String namespace : p.listNamespaces(false)) {
                SearchOptions searchOptions = SearchOptions.create()
                        .setResourceType(ResourceType.ITEMS)
                        .setNamespace(namespace)
                        .setIncludingParentPacks(false)
                        .setDeep(true)
                        .setPrependNamespace(!namespace.equals("minecraft"));

                p.forAllResources(searchOptions, allOverridenModels::add);
            }
        }
        return Collections.unmodifiableSet(allOverridenModels);
    }

    /**
     * Loads a JSON-syntax model from this resource pack.
     * Path can be prefixed with namespace: to find non-minecraft models.
     *
     * @param path to the model, e.g. "block/stone"
     * @return the model, a placeholder cube model if it could not be found
     */
    public Model getModel(String path) {
        Model model = modelCache.get(path);
        if (model != null) {
            return model;
        }

        model = this.loadModel(path);
        if (model == null) {
            model = Model.createPlaceholderModel(BlockData.AIR.getDefaultRenderOptions()); // failed to load or find
            model.setName(path);
        }
        modelCache.put(path, model);
        return model;
    }

    /**
     * Loads the model to be displayed for items displayed in the world, held in a player's hand
     * or shown in a GUI item slot.
     *
     * @param item Bukkit ItemStack to get the model for, can be null
     * @return item model for the item
     */
    public Model getItemModel(ItemStack item) {
        return getItemModel(CommonItemStack.of(item));
    }

    /**
     * Loads the model to be displayed for items displayed in the world, held in a player's hand
     * or shown in a GUI item slot.
     * 
     * @param item CommonItemStack to get the model for, may not be null
     * @return item model for the item
     */
    public Model getItemModel(CommonItemStack item) {
        ItemModel itemModel = getItemModelConfig(item);
        List<ItemModel.MinecraftModel> models = itemModel.resolveModels(item);
        if (models.isEmpty()) {
            models = ItemModel.MinecraftModel.NOT_SET_LIST;
        }

        //TODO: Add support for multiple models composited together
        //      For now we just show the first model only

        ItemRenderOptions options = ModelInfoLookup.lookupItemRenderOptions(item);
        Model m = this.loadModel(models.get(0).model, options);
        if (m != null) {
            m.buildBlock(options);
            m.buildQuads();
        }
        if (m == null) {
            m = Model.createPlaceholderModel(options);
        }
        return m;
    }

    /**
     * Renders the item gui slot texture of an item
     *
     * @param item to render
     * @param width of the produced icon image
     * @param height of the produced icon image
     * @return rendered item slot image
     */
    public MapTexture getItemTexture(ItemStack item, int width, int height) {
        return getItemTexture(CommonItemStack.of(item), width, height);
    }

    /**
     * Renders the item gui slot texture of an item
     * 
     * @param item to render
     * @param width of the produced icon image
     * @param height of the produced icon image
     * @return rendered item slot image
     */
    public MapTexture getItemTexture(CommonItemStack item, int width, int height) {
        Model model = this.getItemModel(item);
        if (model == null || model.isPlaceholder()) {
            return Model.createPlaceholderTexture(width, height);
        }

        MapTexture texture = MapTexture.createEmpty(width, height);
        Matrix4x4 transform = new Matrix4x4();
        if (width != 16 || height != 16) {
            transform.scale((double) width / 16.0, 1.0, (double) height / 16.0);
        }
        Model.Display display = model.display.get("gui");
        if (display != null) {
            display.apply(transform);
            texture.setLightOptions(0.0f, 1.0f, new Vector3(-1, 1, -1));
        } else {
            //System.out.println("GUI DISPLAY ELEMENT NOT FOUND");
        }

        texture.drawModel(model, transform);
        return texture;
    }

    /**
     * Lists all the resources found inside a folder of the given resource type. As some resources
     * like models sit in a specific root directory, the folder path is relative to that directory.
     * For example, to list textures, the <i>assets/minecraft/textures</i> prefix can be omitted.<br>
     * <br>
     * Lists only default minecraft namespace assets. Also lists resources of parent resource packs.
     *
     * @param type Type of resources to find
     * @param folder Folder relative to the resource type root to look for files
     * @return Set of files matching this resource type found in the folder. Without extension.
     *         These paths can be directly used with methods like {@link #getTexture(String)}
     *         and {@link #getConfig(String)}.
     */
    public Set<String> listResources(ResourceType type, String folder) {
        return listResources(type, folder, true);
    }

    /**
     * Lists all the resources found inside a folder of the given resource type. As some resources
     * like models sit in a specific root directory, the folder path is relative to that directory.
     * For example, to list textures, the <i>assets/minecraft/textures</i> prefix can be omitted.<br>
     * <br>
     * Lists only default minecraft namespace assets. If recurse is true, also lists resources
     * of parent resource packs.
     *
     * @param type Type of resources to find
     * @param folder Folder relative to the resource type root to look for files
     * @param includingParentPacks Whether to include resources found in parent resource packs in the results
     * @return Set of files matching this resource type found in the folder. Without extension.
     *         These paths can be directly used with methods like {@link #getTexture(String)}
     *         and {@link #getConfig(String)}.
     */
    public Set<String> listResources(ResourceType type, String folder, boolean includingParentPacks) {
        return listResources(type, "minecraft", folder, includingParentPacks);
    }

    /**
     * Lists all the resources found inside a folder of the given resource type. As some resources
     * like models sit in a specific root directory, the folder path is relative to that directory.
     * For example, to list textures, the <i>assets/minecraft/textures</i> prefix can be omitted.<br>
     * <br>
     * Also lists resources found in the parent resource packs.
     *
     * @param type Type of resources to find
     * @param namespace Namespace, the default is "minecraft" for vanilla assets
     * @param folder Folder relative to the resource type root to look for files
     * @return Set of files matching this resource type found in the folder. Without extension.
     *         These paths can be directly used with methods like {@link #getTexture(String)}
     *         and {@link #getConfig(String)}.
     */
    public Set<String> listResources(ResourceType type, String namespace, String folder) {
        return listResources(type, namespace, folder, true);
    }

    /**
     * Lists all the resources found inside a folder of the given resource type. As some resources
     * like models sit in a specific root directory, the folder path is relative to that directory.
     * For example, to list textures, the <i>assets/minecraft/textures</i> prefix can be omitted.<br>
     * <br>
     * If recurse is true, also lists resources found in the parent resource packs.
     *
     * @param type Type of resources to find
     * @param namespace Namespace, the default is "minecraft" for vanilla assets
     * @param folder Folder relative to the resource type root to look for files
     * @param includingParentPacks Whether to also list resources found in parent resource packs
     * @return Set of files matching this resource type found in the folder. Without extension.
     *         These paths can be directly used with methods like {@link #getTexture(String)}
     *         and {@link #getConfig(String)}.
     */
    public Set<String> listResources(ResourceType type, String namespace, String folder, boolean includingParentPacks) {
        return listResources(SearchOptions.create()
                .setResourceType(type)
                .setNamespace(namespace)
                .setFolder(folder)
                .setIncludingParentPacks(includingParentPacks));
    }

    /**
     * Lists all the resources found inside a folder of the given resource type. As some resources
     * like models sit in a specific root directory, the folder path is relative to that directory.
     * For example, to list textures, the <i>assets/minecraft/textures</i> prefix can be omitted.<br>
     * <br>
     * If recurse is true, also lists resources found in the parent resource packs.
     *
     * @param searchOptions Search Options
     * @return Set of files matching this resource type found in the folder. Without extension.
     *         These paths can be directly used with methods like {@link #getTexture(String)}
     *         and {@link #getConfig(String)}.
     */
    public Set<String> listResources(SearchOptions searchOptions) {
        Set<String> result = new LinkedHashSet<>();
        forAllResources(searchOptions, result::add);
        return result;
    }

    /**
     * Iterates all the resources found inside a folder of the given resource type, and calls the
     * callback function with them. As some resources like models sit in a specific root directory,
     * the folder path is relative to that directory. For example, to list textures, the
     * <i>assets/minecraft/textures</i> prefix can be omitted.<br>
     * <br>
     * If recurse is true, also lists resources found in the parent resource packs.
     *
     * @param searchOptions Search Options
     * @param callback Callback called with every item found
     */
    public void forAllResources(SearchOptions searchOptions, Consumer<String> callback) {
        if (searchOptions.getResourceType() == null) {
            throw new IllegalArgumentException("Resource Type is not set");
        }

        // Must end with / to be a valid zip directory 'file'
        String folder = searchOptions.getFolder();
        if (!folder.endsWith("/")) {
            folder += "/";
        }

        // If type is ITEMS and this is not supported by this pack, list models/item instead
        // Do note that this does not support custom namespaces or subdirectories at all, just vanilla item names
        if (searchOptions.getResourceType() == ResourceType.ITEMS && !getMetadata().hasItemModels()) {
            //TODO: Utility?
            if (folder.equals("/")) {
                folder = "item";
            } else if (folder.startsWith("/")) {
                folder = "item" + folder;
            } else {
                folder = "item/" + folder;
            }

            forAllResources(searchOptions.clone()
                    .setResourceType(ResourceType.MODELS)
                    .setFolder(folder)
                    .setDeep(false)
                    .setPrependNamespace(false),
                    path -> {
                        // Omit item/ prefix
                        callback.accept(path.substring(5));
                    });
            return;
        }

        this.forAllArchiveEntries(
                searchOptions.getResourceType(),
                searchOptions.getFullArchivePath(),
                false,
                searchOptions.isIncludingParentPacks(),
                searchOptions.isDeep(),
                path -> callback.accept(searchOptions.populatePathPrefix(path)));
    }

    /**
     * Lists all namespaces declared by this resource pack and its parents
     *
     * @return namespaces
     */
    public Set<String> listNamespaces() {
        return listNamespaces(true);
    }

    /**
     * Lists all namespaces declared by this resource pack. If recurse is true,
     * also lists namespaces of parent resource packs.
     *
     * @param recurse Whether to recursively check parent resource packs as well for namespaces
     * @return namespaces
     */
    public Set<String> listNamespaces(boolean recurse) {
        Set<String> result = new HashSet<>();
        this.forAllArchiveEntries(null, "assets/", true, recurse, false, result::add);
        return result;
    }

    /**
     * Lists all the sub-directories storing resources of a given resource type. The resource
     * type is used to decide the root path, same as {@link #listResources(ResourceType, String)}.<br>
     * <br>
     * Lists only default minecraft namespace assets. Also lists directories found in parent resource packs.
     *
     * @param type Type of resources to list directories of in a folder
     * @param folder Folder relative to the resource type root to look for files
     * @return Set of directory paths that are child of the folder path specified
     */
    public Set<String> listDirectories(ResourceType type, String folder) {
        return listDirectories(type, folder, true);
    }

    /**
     * Lists all the sub-directories storing resources of a given resource type. The resource
     * type is used to decide the root path, same as {@link #listResources(ResourceType, String)}.<br>
     * <br>
     * Lists only default minecraft namespace assets. If recurse is true also lists directories
     * found in parent resource packs.
     *
     * @param type Type of resources to list directories of in a folder
     * @param folder Folder relative to the resource type root to look for files
     * @param includingParentPacks Whether to include directories found in parent resource packs
     * @return Set of directory paths that are child of the folder path specified
     */
    public Set<String> listDirectories(ResourceType type, String folder, boolean includingParentPacks) {
        return listDirectories(type, "minecraft", folder, includingParentPacks);
    }

    /**
     * Lists all the sub-directories storing resources of a given resource type. The resource
     * type is used to decide the root path, same as {@link #listResources(ResourceType, String)}.
     * Includes directories found in parent resource packs.
     *
     * @param type Type of resources to list directories of in a folder
     * @param namespace Namespace, the default is "minecraft" for vanilla models
     * @param folder Folder relative to the resource type root to look for files
     * @return Set of directory paths that are child of the folder path specified
     */
    public Set<String> listDirectories(ResourceType type, String namespace, String folder) {
        return listDirectories(type, namespace, folder, true);
    }

    /**
     * Lists all the sub-directories storing resources of a given resource type. The resource
     * type is used to decide the root path, same as {@link #listResources(ResourceType, String)}.
     *
     * @param type Type of resources to list directories of in a folder
     * @param namespace Namespace, the default is "minecraft" for vanilla models
     * @param folder Folder relative to the resource type root to look for files
     * @param includingParentPacks Whether to include directories found in parent resource packs
     * @return Set of directory paths that are child of the folder path specified
     */
    public Set<String> listDirectories(ResourceType type, String namespace, String folder, boolean includingParentPacks) {
        return listDirectories(SearchOptions.create()
                .setResourceType(type)
                .setNamespace(namespace)
                .setFolder(folder)
                .setIncludingParentPacks(includingParentPacks));
    }

    /**
     * Lists all the sub-directories storing resources of a given resource type. The resource
     * type is used to decide the root path, same as {@link #listResources(SearchOptions)}.
     *
     * @param searchOptions Search Options
     * @return Set of directory paths that are child of the folder path specified
     */
    public Set<String> listDirectories(SearchOptions searchOptions) {
        if (searchOptions.getResourceType() == null) {
            throw new IllegalArgumentException("Resource Type is not set");
        }

        Set<String> result = new HashSet<>();
        this.forAllArchiveEntries(
                searchOptions.getResourceType(),
                searchOptions.getFullArchivePath(),
                true,
                searchOptions.isIncludingParentPacks(),
                searchOptions.isDeep(),
                path -> result.add(searchOptions.populatePathPrefix(path)));
        return result;
    }

    /**
     * Loads a YAML file stored in the resource pack at the path specified.
     * If not found, returns an empty configuration node.<br>
     * <br>
     * Please make sure not to make modifications to the returned configuration node
     * without first {@link ConfigurationNode#clone() cloning} it. As the returned
     * configuration is cached, the changes will persist.
     *
     * @param path Path in the resource pack to look for YAML-encoded file (.yml extension excluded)
     * @return configuration node of the decoded YAML file found at this path
     * @throws YAMLException If an error occurs reading or decoding the YAML file (and the file exists)
     */
    public ConfigurationNode getConfig(String path) throws YAMLException {
        ConfigurationNode result = yamlCache.get(path);
        if (result == null) {
            result = new ConfigurationNode();
            try {
                try (InputStream inputStream = openFileStream(ResourceType.YAML, path)) {
                    if (inputStream != null) {
                        result.loadFromStream(inputStream);
                    }
                }
            } catch (YAMLException ex) {
                throw ex;
            } catch (IOException ex) {
                throw new YAMLException("Failed to open YAML file stream at " + path, ex);
            }

            // Cache for next time
            yamlCache.put(path, result);
        }
        return result;
    }

    /**
     * Loads a texture from this resource pack.
     * Path can be prefixed with namespace: to find non-minecraft textures.
     * 
     * @param path to the texture, e.g. "blocks/stone"
     * @return the texture, a placeholder texture if it could not be found
     */
    public MapTexture getTexture(String path) {
        MapTexture result = textureCache.get(path);
        if (result == null) {
            // Shortcut, this happens often for stuff in parents of children defining textures
            if (path.startsWith("#")) {
                result = Model.createPlaceholderTexture();
                textureCache.put(path, result);
                return result;
            }

            try {
                try (InputStream inputStream = openFileStream(ResourceType.TEXTURES, path)) {
                    if (inputStream != null) {
                        result = MapTexture.fromStream(inputStream);
                    }
                }
            } catch (IOException ex) {
                throw new MapTexture.TextureLoadException("Failed to open image stream at " + path, ex);
            }

            if (result == null) {
                Logging.LOGGER_MAPDISPLAY.once(Level.WARNING, "Failed to load texture: " + path);
                result = Model.createPlaceholderTexture();
            }

            // Animated textures: when height is a multiple of width
            // Find the mcmeta. If one does not exist, fail.
            // Otherwise, load the first frame (wxw) area.
            int num_frames = result.getHeight() / result.getWidth();
            if (num_frames > 1 && ((num_frames * result.getWidth()) == result.getHeight())) {
                InputStream metaStream = openFileStream(ResourceType.TEXTURES_META, path);
                if (metaStream == null) {
                    Logging.LOGGER_MAPDISPLAY.once(Level.WARNING, "Failed to load animated texture (missing mcmeta): " + path);
                    result = Model.createPlaceholderTexture();
                } else {
                    result = result.getView(0, 0, result.getWidth(), result.getWidth()).clone();
                    try {
                        metaStream.close();
                    } catch (IOException e) {}
                }
            }

            textureCache.put(path, result);
        }
        return result;
    }

    /**
     * Loads a block model, always fetching it from the resource pack instead of the cache
     * 
     * @param blockRenderOptions of the block
     * @return the model, or <i>null</i> if not found
     */
    protected final Model loadBlockModel(BlockRenderOptions blockRenderOptions) {
        if (blockRenderOptions.getBlockData().isType(Material.AIR)) {
            return new Model(); // air. No model.
        }

        BlockRenderProvider oldProvider = this.currProvider;
        try {
            // Some blocks are handled by providers
            this.currProvider = BlockRenderProvider.get(blockRenderOptions.getBlockData());
            if (this.currProvider != null) {
                Model model = this.currProvider.createModel(this, blockRenderOptions);
                if (model != null) {
                    return model;
                }
            }

            String blockName = blockRenderOptions.lookupModelName();

            // Find the blockstate
            BlockModelState state = this.openGsonObject(BlockModelState.class, ResourceType.BLOCKSTATES, blockName);

            // Find out the variant that is used
            List<BlockModelState.Variant> variants;
            if (state != null) {
                // Figure out from the blockstate what variant to use
                variants = state.findVariants(blockRenderOptions);
            } else {
                // Default variant based on block name
                BlockModelState.Variant variant = new BlockModelState.Variant();
                variant.modelName = "block/" + blockName;
                variants = Arrays.asList(variant);
            }

            // If no variants are found, render nothing (AIR)
            if (variants.isEmpty()) {
                return new Model();
            }

            // Not multipart, then simply load the one variant
            if (variants.size() == 1) {
                return this.loadBlockVariant(variants.get(0), blockRenderOptions);
            }

            // Add all variant elements to the model
            Model result = new Model();
            boolean succ = true;
            for (BlockModelState.Variant variant : variants) {
                Model subModel = this.loadBlockVariant(variant, blockRenderOptions);
                if (subModel != null) {
                    result.elements.addAll(subModel.elements);
                } else {
                    succ = false;
                }
            }
            if (!succ && result.elements.isEmpty()) {
                return null;
            } else {
                return result;
            }
        } finally {
            this.currProvider = oldProvider; // restore
        }
    }

    private Model loadBlockVariant(BlockModelState.Variant variant, BlockRenderOptions blockRenderOptions) {
        Model model = this.loadModel(variant.modelName, blockRenderOptions);
        if (model == null) {
            return null;
        }
        model.buildBlock(blockRenderOptions);
        variant.update(model);
        model.buildQuads();
        return model;
    }

    /**
     * Loads a model, always fetching it from the resource pack instead of the cache
     * 
     * @param path to find the model at
     * @return the model, or <i>null</i> if not found
     */
    protected final Model loadModel(String path) {
        return this.loadModel(path, BlockData.AIR.getDefaultRenderOptions());
    }

    /**
     * Loads a model, always fetching it from the resource pack instead of the cache
     *
     * @param path to find the model at
     * @param options to apply when building the model
     * @return the model, or <i>null</i> if not found
     */
    protected final Model loadModel(String path, RenderOptions options) {
        // Builtin models
        if (path.equals("builtin/generated")) {
            return new GeneratedModel();
        }

        // Builtin error model
        if (path.equals(ItemModel.MinecraftModel.NOT_SET.model)) {
            return Model.createPlaceholderModel(options);
        }

        Model model = openGsonObject(Model.class, ResourceType.MODELS, path);
        if (model == null) {
            Logging.LOGGER_MAPDISPLAY.once(Level.WARNING, "Failed to load model " + path);
            return null;
        }

        // Handle overrides first
        //TODO: Fixme!
        for (Model.ModelOverride override : model.getOverrides()) {
            if (override.matches(options) && !override.model.equals(path)) {
                //System.out.println("MATCH " + override.model + "  " + options);
                return this.loadModel(override.model, options);
            }
        }

        // On Minecraft 1.8-1.8.8 the base block/block model is not defined, here we add it ourselves
        // This fixes several display render bugs
        String parentModelName = model.getParentName();
        if (parentModelName == null &&
            !CommonCapabilities.RESOURCE_PACK_MODEL_BASE_TRANSFORMS &&
            !path.equals("block/block"))
        {
            parentModelName = "block/block";
        }

        // Insert the parent model as required
        if (parentModelName != null) {
            Model parentModel = this.loadModel(parentModelName, options);
            if (parentModel == null || parentModel.isPlaceholder()) {
                Logging.LOGGER_MAPDISPLAY.once(Level.WARNING, "Parent of model " + path + " not found: " + model.getParentName());
                return null;
            }
            model.loadParent(parentModel);
        }

        // Make all texture paths absolute
        model.build(this, options);
        return model;
    }

    /**
     * Called by the main listResources() function to fill a set of strings with files
     * found in a folder.
     *
     * @param type Resource type, defining the root directory to look in
     * @param rootArchivePath Folder in the archive to search in
     * @param directories Whether to list directories instead of files
     * @param recurse Whether to also look at base resource packs of this resource pack
     * @param deep Whether to include contents of subdirectories in the results
     * @param callback Callback function called for every result provided
     */
    protected void forAllArchiveEntries(
            ResourceType type,
            String rootArchivePath,
            boolean directories,
            boolean recurse,
            boolean deep,
            Consumer<String> callback
    ) {
        // =null: failed to load resource pack file
        this.handleLoad(true, false);
        if (this.archive != null) {
            try {
                if (directories) {
                    for (String file : this.archive.listFiles(rootArchivePath, deep)) {
                        if (file.endsWith("/")) {
                            callback.accept(file.substring(0, file.length() - 1));
                        }
                    }
                } else {
                    for (String file : this.archive.listFiles(rootArchivePath, deep)) {
                        if (type.isExtension(file)) {
                            callback.accept(file.substring(0, file.length() - type.getExtension().length()));
                        }
                    }
                }
            } catch (IOException ex) {
            }
        }

        // Ask base pack as well!
        if (recurse && this.baseResourcePack != null) {
            this.baseResourcePack.forAllArchiveEntries(type, rootArchivePath, directories, recurse, false, callback);
        }
    }

    /**
     * Attempts to find a file in the resource pack and open it for reading
     * 
     * @param type of resource to find
     * @param path of the resource (relative)
     * @return InputStream to read the file from, null if not found
     */
    protected InputStream openFileStream(ResourceType type, String path) {
        if (type == null) {
            throw new IllegalArgumentException("Input resource type is null");
        }
        if (path == null) {
            throw new IllegalArgumentException("Input path is null");
        }

        // Create full path
        String fullPath = type.makePath(path);

        // Check overrided
        if (!ResourceOverrides.isResourceOverrided(fullPath)) {
            // =null: failed to load resource pack file
            this.handleLoad(true, false);
            if (this.archive != null) {
                try {
                    InputStream stream = this.archive.openFileStream(fullPath);
                    if (stream == null) {
                        stream = this.archive.openFileStream(fullPath.toLowerCase(Locale.ENGLISH));
                    }
                    if (stream != null) {
                        return stream;
                    }
                } catch (IOException ex) {
                }
            }

            // Fallback: try the underlying resource pack (usually Vanilla)
            if (this.baseResourcePack != null) {
                InputStream stream = this.baseResourcePack.openFileStream(type, path);
                if (stream != null) {
                    return stream;
                }
            }

            // Fallback: ask provider (if available)
            if (this.currProvider != null) {
                try {
                    return this.currProvider.openResource(type, path);
                } catch (IOException ex) {
                }
            }
        }

        // Fallback: load from BKCommonLib built-in resources
        // This handles many block models such as signs
        InputStream bkc_stream = Common.class.getResourceAsStream(type.makeBKCPath(path));
        if (bkc_stream != null) {
            return bkc_stream;
        }

        // FAILED
        return null;
    }

    /**
     * Attempts to open and load a JSON file, deserializing it into a certain class type
     * 
     * @param <T> Object Type
     * @param objectType to deserialize the JSON as
     * @param type of resource
     * @param path of the resource
     * @return loaded Gson object, or <i>null</i> if not found or not loadable
     */
    protected final <T> T openGsonObject(Class<T> objectType, ResourceType type, String path) {
        if (type == null) {
            throw new IllegalArgumentException("Input resource type is null");
        }
        if (path == null) {
            throw new IllegalArgumentException("Input path is null");
        }
        return readGsonObject(objectType, this.openFileStream(type, path), path);
    }

    /**
     * Reads a resource file stream and deserializes it into a certain class type.
     * Supports the standard JSON class types like Model.
     *
     * @param <T> Object Type
     * @param objectType to deserialize the JSON as
     * @param inputStream Stream to read from
     * @return loaded Gson object, or <i>null</i> if not found or not loadable
     */
    public final <T> T readGsonObject(Class<T> objectType, InputStream inputStream) {
        return readGsonObject(objectType, inputStream, null);
    }

    private <T> T readGsonObject(Class<T> objectType, InputStream inputStream, String optPath) {
        if (deserializer == null) {
            deserializer = MapResourcePackDeserializer.create();
        }
        return deserializer.readGsonObject(objectType, inputStream, optPath);
    }

    /**
     * A type of resource that can be read from a Resource Pack
     */
    public static enum ResourceType {
        /** Models found in <b>assets/minecraft/models/</b> */
        MODELS("/models/", ".json"),
        /**
         * Item (gui) models found in <b>assets/minecraft/items</b>, used since 1.21.4.
         * On versions before 1.21.4 gets and lists items in <b>assets/minecraft/models/item</b> instead.
         * */
        ITEMS("/items/", ".json"),
        /** Block States found in <b>assets/minecraft/blockstates/</b> */
        BLOCKSTATES("/blockstates/", ".json"),
        /** Textures found in <b>assets/minecraft/textures/</b> */
        TEXTURES("/textures/", ".png"),
        /** Texture metadata found in <b>assets/minecraft/textures/</b> */
        TEXTURES_META("/textures/", ".png.mcmeta"),
        /** YAML configuration files stored anywhere under <b>assets/</b> */
        YAML("/", ".yml");

        private final String root;
        private final String ext;

        private ResourceType(String root, String ext) {
            this.root = root;
            this.ext = ext;
        }

        public String getRoot(String namespace) {
            return "assets/" + namespace + this.root;
        }

        public String getExtension() {
            return this.ext;
        }

        public boolean isExtension(String filePath) {
            int extIdx = filePath.indexOf('.');
            return extIdx != -1 && filePath.substring(extIdx).equals(this.ext);
        }

        public String makePath(String path) {
            int namespaceIndex = path.indexOf(':');
            if (namespaceIndex != -1) {
                String namespace = path.substring(0, namespaceIndex);
                path = path.substring(namespaceIndex + 1);
                if (!namespace.isEmpty()) {
                    return "assets/" + namespace + this.root + path + this.ext;
                }
            }

            return "assets/minecraft" + this.root + path + this.ext;
        }

        public String makeBKCPath(String path) {
            return "/com/bergerkiller/bukkit/common/internal/resources/assets/minecraft" + this.root + stripNS(path) + this.ext;
        }

        private static String stripNS(String path) {
            int namespaceIndex = path.indexOf(':');
            return (namespaceIndex == -1) ? path : path.substring(namespaceIndex+1);
        }
    }

    /**
     * The major and minor version as Mojang started using for resource packs
     * since Minecraft 1.21.9. For older packs minor version is always ignored.
     */
    public static final class PackVersion {
        /** Helper version for major version 0, acting as a lower bound that always matches */
        public static final PackVersion MINIMUM = of(0);
        /** Helper version for major version max, acting as an upper bound that always matches */
        public static final PackVersion MAXIMUM = of(Integer.MAX_VALUE);

        private static final NavigableMap<TextValueSequence, PackVersion> BY_VERSION = new TreeMap<>();
        static {
            BY_VERSION.put(TextValueSequence.parse("1.6.1"), of(1));
            BY_VERSION.put(TextValueSequence.parse("1.9"), of(2));
            BY_VERSION.put(TextValueSequence.parse("1.11"), of(3));
            BY_VERSION.put(TextValueSequence.parse("1.13"), of(4));
            BY_VERSION.put(TextValueSequence.parse("1.15"), of(5));
            BY_VERSION.put(TextValueSequence.parse("1.16.2"), of(6));
            BY_VERSION.put(TextValueSequence.parse("1.17"), of(7));
            BY_VERSION.put(TextValueSequence.parse("1.18"), of(8));
            BY_VERSION.put(TextValueSequence.parse("1.19"), of(9));
            BY_VERSION.put(TextValueSequence.parse("1.19.3"), of(12));
            BY_VERSION.put(TextValueSequence.parse("1.19.4"), of(13));
            BY_VERSION.put(TextValueSequence.parse("1.20"), of(15));
            BY_VERSION.put(TextValueSequence.parse("1.20.2"), of(18));
            BY_VERSION.put(TextValueSequence.parse("1.20.3"), of(22));
            BY_VERSION.put(TextValueSequence.parse("1.20.5"), of(32));
            BY_VERSION.put(TextValueSequence.parse("1.21"), of(34));
            BY_VERSION.put(TextValueSequence.parse("1.21.2"), of(42));
            BY_VERSION.put(TextValueSequence.parse("1.21.4"), of(46));
            BY_VERSION.put(TextValueSequence.parse("1.21.5"), of(55));
            BY_VERSION.put(TextValueSequence.parse("1.21.6"), of(63));
            BY_VERSION.put(TextValueSequence.parse("1.21.7"), of(64));
            BY_VERSION.put(TextValueSequence.parse("1.21.9"), of(69));
        }

        /**
         * The highest known pack version that has been compiled into BKCommonLib
         */
        public static final PackVersion HIGHEST_KNOWN = BY_VERSION.descendingMap().values().iterator().next();

        /**
         * The preferred resource pack version that the server supports, if the client and
         * server used the exact same version. BKCommonLib will honor this version if possible.
         * If not, it will support an older pack format that is supported. If none exists,
         * then uses newer formats on a best-attempt basis.
         */
        public static final PackVersion SERVER = byGameVersion(CommonBootstrap.initCommonServer().getMinecraftVersion());

        private final int major;
        private final int minor;
        private final boolean anyMinor;

        private PackVersion(int major, int minor, boolean anyMinor) {
            this.major = major;
            this.minor = minor;
            this.anyMinor = anyMinor;
        }

        public static PackVersion of(int major, int minor) {
            return new PackVersion(major, minor, false);
        }

        public static PackVersion of(int major) {
            return new PackVersion(major, 0, true);
        }

        /**
         * Major pack format version
         *
         * @return Major pack format
         */
        public int major() {
            return major;
        }

        /**
         * Whether this pack format version matches any minor version,
         * because none was specified.
         *
         * @return True if any minor version matches
         */
        public boolean anyMinor() {
            return anyMinor;
        }

        /**
         * Minor pack format version. Usually 0.
         *
         * @return Minor pack format
         * @see #anyMinor()
         */
        public int minor() {
            return minor;
        }

        /**
         * Gets whether this pack version is equal to or greater than the
         * version specified. If either version allows any minor version, then
         * will return true if the major versions match. If not, the minor versions
         * must also be greater or equal to one another.
         *
         * @param other Other PackVersion to compare against
         * @return True if this &gt;= other
         */
        public boolean isAtLeast(PackVersion other) {
            if (this.major > other.major) {
                return true;
            } else if (this.major < other.major) {
                return false;
            } else if (this.anyMinor || other.anyMinor) {
                return true;
            } else {
                return this.minor >= other.minor;
            }
        }

        /**
         * Gets whether this pack version is equal to or less than the
         * version specified. If either version allows any minor version, then
         * will return true if the major versions match. If not, the minor versions
         * must also be less or equal to one another.
         *
         * @param other Other PackVersion to compare against
         * @return True if this &lt;= other
         */
        public boolean isAtMost(PackVersion other) {
            if (this.major < other.major) {
                return true;
            } else if (this.major > other.major) {
                return false;
            } else if (this.anyMinor || other.anyMinor) {
                return true;
            } else {
                return this.minor <= other.minor;
            }
        }

        @Override
        public int hashCode() {
            return 31 * major + minor;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof PackVersion) {
                PackVersion other = (PackVersion) o;
                return major == other.major && minor == other.minor && anyMinor == other.anyMinor;
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return this.major + "." + (anyMinor ? "ANY" : this.minor);
        }

        /**
         * Gets a resource pack <b>PackVersion</b> value by the Minecraft client game version since which it was
         * introduced. See: <a href="https://minecraft.wiki/w/Pack_format">Minecraft wiki: pack_format</a>
         *
         * @param minecraftVersion Minecraft game version
         * @return Resource pack format used on that game version
         */
        public static PackVersion byGameVersion(String minecraftVersion) {
            TextValueSequence minecraftSeq = TextValueSequence.parse(minecraftVersion);

            // Sort older values and select the highest version
            {
                Iterator<PackVersion> iter = BY_VERSION.headMap(minecraftSeq, true).descendingMap().values().iterator();
                if (iter.hasNext()) {
                    return iter.next();
                }
            }

            return MINIMUM;
        }
    }

    /**
     * A range of {@link PackVersion} with a minimum (inclusive) and maximum (inclusive)
     * pack version.
     */
    public static final class PackVersionRange {
        /**
         * When the version is within this range, then it will interpret <code>min_format</code> and
         * <code>max_format</code> metadata fields in pack.mcmeta.
         */
        public static final PackVersionRange USES_MIN_MAX_FORMAT = of(PackVersion.of(65), PackVersion.MAXIMUM);

        /**
         * When the version is within this range, then it will parse item predicates using the legacy
         * syntax. The new item model system is not used.
         */
        public static final PackVersionRange USES_ITEM_PREDICATE_OVERRIDES = of(PackVersion.of(2), PackVersion.of(45));

        /**
         * When the version is within this range, then it will parse item models using the item model
         * syntax. The old predicates system is no longer used.
         */
        public static final PackVersionRange USES_ITEM_MODELS = of(PackVersion.of(46), PackVersion.MAXIMUM);

        private final PackVersion min_inclusive, max_inclusive;

        public static PackVersionRange of(int packFormatMajorVersion) {
            PackVersion asPackVersion = PackVersion.of(packFormatMajorVersion);
            return new PackVersionRange(asPackVersion, asPackVersion);
        }

        public static PackVersionRange of(int min_inclusive_major, int max_inclusive_major) {
            return new PackVersionRange(PackVersion.of(min_inclusive_major), PackVersion.of(max_inclusive_major));
        }

        public static PackVersionRange of(PackVersion min_inclusive, PackVersion max_inclusive) {
            return new PackVersionRange(min_inclusive, max_inclusive);
        }

        private PackVersionRange(PackVersion min_inclusive, PackVersion max_inclusive) {
            this.min_inclusive = min_inclusive;
            this.max_inclusive = max_inclusive;
        }

        /**
         * Checks whether a this range supports one of the pack versions in the range specified.
         * This tests for a particular resource pack feature that is supported between the
         * min and max pack version specified.
         *
         * @param usedFormat Pack format version that is used to interpret the resource pack
         * @return True if the range is supported
         */
        public boolean isSupported(PackVersion usedFormat) {
            return usedFormat.isAtMost(this.max_inclusive) && usedFormat.isAtLeast(this.min_inclusive);
        }

        public PackVersion min_inclusive() {
            return min_inclusive;
        }

        public PackVersion max_inclusive() {
            return max_inclusive;
        }
    }

    /**
     * Contents of <b>pack.mcmeta</b>
     */
    public static class Metadata {
        private int pack_format;
        private PackVersion min_format;
        private PackVersion max_format;
        private ResourcePackDescription description;
        private List<PackVersionRange> supported_formats = Collections.emptyList();
        private final transient DeferredSupplier<Boolean> hasItemModels = DeferredSupplier.of(() -> PackVersionRange.USES_ITEM_MODELS.isSupported(getUsedPackVersion()));
        private final transient DeferredSupplier<Boolean> hasItemPredicateOverrides = DeferredSupplier.of(() -> PackVersionRange.USES_ITEM_PREDICATE_OVERRIDES.isSupported(getUsedPackVersion()));
        private transient PackVersion preferredPackVersion = PackVersion.SERVER;
        private transient PackVersion usedPackVersion = null;

        /**
         * Gets the pack_format value
         *
         * @return Pack format
         */
        public int getPackFormat() {
            return pack_format;
        }

        /**
         * Gets the description of this resource pack
         *
         * @return Description
         */
        public String getDescription() {
            return hasDescription() ? description.plainContent : "No Description";
        }

        public boolean hasDescription() {
            return description != null && !description.plainContent.isEmpty();
        }

        /**
         * Gets whether this resource pack version makes use of {@link ResourceType#ITEMS item models}.
         *
         * @return True if this resource pack makes use of item models. False if it uses the old
         *         item predicate system.
         */
        public boolean hasItemModels() {
            return hasItemModels.get();
        }

        /**
         * Gets whether this resource pack version defines item overrides using the (legacy) item predicates system.
         * Is <i>false</i> when {@link #hasItemModels()} is <i>true</i> or on very old pack versions.
         *
         * @return True if the item predicate system is used for this pack
         */
        public boolean hasItemPredicateOverrides() {
            return hasItemPredicateOverrides.get();
        }

        /**
         * Gets the resource pack format version that is used by BKCommonLib when interpreting this resource pack.
         * This version is automatically chosen by comparing
         * {@link Builder#preferredPackVersion(PackVersion) preferredPackVersion(PackVersion)} against the
         * formats supported by the resource pack.<br>
         * <br>
         * If no preferred pack version was configured, it tries to use {@link PackVersion#SERVER}.
         *
         * @return PackVersion chosen to interpret this resource pack
         */
        public PackVersion getUsedPackVersion() {
            PackVersion cached = this.usedPackVersion;
            if (cached == null) {
                this.usedPackVersion = cached = calculateUsedPackVersion(this.preferredPackVersion);
            }
            return cached;
        }

        private PackVersion calculateUsedPackVersion(PackVersion preferredVersion) {
            // If min_format and max_format are to be used, and they are available in this resource pack,
            // check that the server pack version is within range. If not, clamp.
            // If the server is too old, it allows us to still interpret newer resource packs.
            // If the server is too new, we can pick the most recent format we do support.
            if (min_format != null && max_format != null && PackVersionRange.USES_MIN_MAX_FORMAT.isSupported(preferredVersion)) {
                // This is a 1.21.9+ resource pack on a 1.21.9+ interpreter
                // We don't touch the old deprecated format options here, and just clamp on min/max
                if (!preferredVersion.isAtMost(max_format)) {
                    return max_format;
                } else if (!preferredVersion.isAtLeast(min_format)) {
                    return min_format;
                } else {
                    return preferredVersion;
                }
            }

            // Interpret legacy format options when the version is old enough
            if (supported_formats != null) {
                // See if the exact server format is supported
                for (PackVersionRange range : supported_formats) {
                    if (range.isSupported(preferredVersion)) {
                        return preferredVersion;
                    }
                }

                // Downgrade: look for a pack version that is older than the preferred version
                // If multiple are older, pick the newest one
                PackVersion bestVersion = null;
                for (PackVersionRange range : supported_formats) {
                    // Range max must be below preferred version
                    if (range.max_inclusive().isAtLeast(preferredVersion)) {
                        continue;
                    }

                    if (bestVersion == null || range.max_inclusive().isAtLeast(bestVersion)) {
                        bestVersion = range.max_inclusive();
                    }
                }
                if (bestVersion != null) {
                    return bestVersion;
                }

                // Upgrade: look for a pack version that is newer than the preferred version
                // If multiple are newer, pick the oldest one
                for (PackVersionRange range : supported_formats) {
                    // Range max must be below preferred version
                    if (range.min_inclusive().isAtMost(preferredVersion)) {
                        continue;
                    }

                    if (bestVersion == null || range.min_inclusive().isAtMost(bestVersion)) {
                        bestVersion = range.max_inclusive();
                    }
                }
                if (bestVersion != null) {
                    return bestVersion;
                }
            }

            // Rely exclusively on the pack_format
            return PackVersion.of(pack_format);
        }

        /**
         * Creates fallback metadata
         *
         * @param errorReason Reason metadata could not be loaded
         * @return Metadata
         */
        public static Metadata fallback(String errorReason) {
            Metadata metadata = new Metadata();
            metadata.pack_format = PackVersion.HIGHEST_KNOWN.major();
            metadata.min_format = PackVersion.HIGHEST_KNOWN;
            metadata.max_format = PackVersion.HIGHEST_KNOWN;
            metadata.description = new ResourcePackDescription("Unknown Resource pack - " + errorReason);
            return metadata;
        }

        /**
         * Creates metadata for the Vanilla Minecraft client jar assets
         *
         * @param mcVersion Minecraft version of the client jar
         * @return Metadata
         */
        public static Metadata vanilla(String mcVersion) {
            Metadata metadata = new Metadata();
            metadata.min_format = PackVersion.byGameVersion(mcVersion);
            metadata.max_format = metadata.min_format;
            metadata.pack_format = metadata.min_format.major();
            metadata.description = new ResourcePackDescription("Vanilla Minecraft " + mcVersion);
            return metadata;
        }

        public static class Overlay {
            public String directory = "";
            public List<PackVersionRange> formats = Collections.emptyList();
        }

        public static class Overlays {
            public List<Overlay> entries = Collections.emptyList();
        }

        public static class PackWrapper {
            public Metadata pack; // JSON file has a 'pack' field
            public Overlays overlays = new Overlays(); // Version-specific directories
        }
    }

    /**
     * Options for searching for resource files in the resource pack
     */
    public static class SearchOptions implements Cloneable {
        private ResourceType resourceType = null;
        private String namespace = "minecraft";
        private String folder = "/";
        private boolean includingParentPacks = false;
        private boolean deep = false;
        private boolean prependNamespace = false;

        /**
         * Creates new SearchOptions with default settings.
         * Must at least set the resource type to search for.
         *
         * @return New SearchOptions
         */
        public static SearchOptions create() {
            return new SearchOptions();
        }

        public SearchOptions() {
        }

        public ResourceType getResourceType() {
            return resourceType;
        }

        public SearchOptions setResourceType(ResourceType type) {
            this.resourceType = type;
            return this;
        }

        public String getNamespace() {
            return namespace;
        }

        public SearchOptions setNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public String getFolder() {
            return folder;
        }

        public SearchOptions setFolder(String folder) {
            if (!folder.endsWith("/")) {
                folder += "/";
            }
            this.folder = folder;
            return this;
        }

        /**
         * Gets whether to include resources in the results that are in resource packs that are a
         * base of the one being searched.
         *
         * @return True if recursing into parent resource packs
         */
        public boolean isIncludingParentPacks() {
            return includingParentPacks;
        }

        /**
         * Sets whether to include resources in the results that are in resource packs that are a
         * base of the one being searched.
         *
         * @param includingParentPacks True to recurse into parent resource packs
         * @return this
         */
        public SearchOptions setIncludingParentPacks(boolean includingParentPacks) {
            this.includingParentPacks = includingParentPacks;
            return this;
        }

        /**
         * Gets whether to deep-search and include files/folders that are inside subdirectories
         * of the search folder.
         *
         * @return True if deep-searching
         */
        public boolean isDeep() {
            return deep;
        }

        /**
         * Sets whether to deep-search and include files/folders that are inside subdirectories
         * of the search folder.
         *
         * @param deep Whether to do a deep-search
         * @return this
         */
        public SearchOptions setDeep(boolean deep) {
            this.deep = deep;
            return this;
        }

        public boolean isPrependNamespace() {
            return prependNamespace;
        }

        public SearchOptions setPrependNamespace(boolean prepend) {
            this.prependNamespace = prepend;
            return this;
        }

        public boolean isRootPath() {
            return folder.equals("/");
        }

        public String getFullArchivePath() {
            if (isRootPath()) {
                return resourceType.getRoot(namespace);
            } else {
                return resourceType.getRoot(namespace) + folder;
            }
        }

        /**
         * Depending on these search options, populates information around the path
         * such as namespace and the folder.
         *
         * @param path Relative path result
         * @return Populated path
         */
        public String populatePathPrefix(String path) {
            if (isRootPath()) {
                return isPrependNamespace() ? namespace + ":" + path : path;
            } else {
                return isPrependNamespace() ? namespace + ":" + folder + path : folder + path;
            }
        }

        @Override
        public SearchOptions clone() {
            return create()
                    .setResourceType(this.getResourceType())
                    .setNamespace(this.getNamespace())
                    .setFolder(this.getFolder())
                    .setIncludingParentPacks(this.isIncludingParentPacks())
                    .setPrependNamespace(this.isPrependNamespace())
                    .setDeep(this.isDeep());
        }
    }

    /**
     * Configures a new resource pack. Sets up the settings for this resource pack,
     * but does not load the file right away after creating. For that, call
     * {@link MapResourcePack#load()}.
     */
    public static final class Builder {
        private MapResourcePack baseResourcePack;
        private String resourcePackPath;
        private String resourcePackHash;
        private PackVersion preferredPackVersion;

        public Builder(MapResourcePack baseResourcePack) {
            this.baseResourcePack = baseResourcePack;
            this.resourcePackPath = "";
            this.resourcePackHash = "";
            this.preferredPackVersion = PackVersion.SERVER;
        }

        /**
         * Changes the base resource pack that is extended. Is normally
         * {@link #VANILLA}.
         *
         * @param baseResourcePack New resource pack to extend. Can be <i>null</i> to extend none.
         * @return this Builder
         */
        public Builder baseResourcePack(MapResourcePack baseResourcePack) {
            this.baseResourcePack = baseResourcePack;
            return this;
        }

        /**
         * Changes the path or URL to the resource pack to load
         *
         * @param resourcePackPath Path or URL to the resource pack
         * @return this Builder
         */
        public Builder resourcePackPath(String resourcePackPath) {
            this.resourcePackPath = resourcePackPath;
            return this;
        }

        /**
         * Changes the SHA-1 hash to verify the resource pack contents with.
         * Primarily useful for downloaded resource packs (URL) to redownload them
         * when the hash changes.
         *
         * @param resourcePackHash SHA-1 hash of the resource pack
         * @return this Builder
         */
        public Builder resourcePackHash(String resourcePackHash) {
            this.resourcePackHash = resourcePackHash;
            return this;
        }

        /**
         * Changes the preferred Pack Version to use when loading this resource pack.
         * Since BKCommonLib supports all resource pack formats, and packs can be multi-format
         * these days, this controls which one to load. It controls the overlay layers to use
         * and whether to use item models or legacy item predicates.<br>
         * <br>
         * Uses {@link PackVersion#SERVER} by default.
         *
         * @param packVersion PackVersion to prefer when loading
         * @return this Builder
         */
        public Builder preferredPackVersion(PackVersion packVersion) {
            this.preferredPackVersion = packVersion;
            return this;
        }

        /**
         * Takes the configuration of this Builder and constructs a new MapResourcePack.
         * Call {@link MapResourcePack#load()} to actually load it to avoid lag on first use.
         *
         * @return new MapResourcePack
         */
        public MapResourcePack build() {
            return new MapResourcePack(this);
        }
    }
}
