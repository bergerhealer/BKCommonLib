package com.bergerkiller.bukkit.common.map;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import com.bergerkiller.bukkit.common.map.gson.BlockFaceDeserializer;
import com.bergerkiller.bukkit.common.map.gson.ConditionalDeserializer;
import com.bergerkiller.bukkit.common.map.gson.NonNullListDeserializer;
import com.bergerkiller.bukkit.common.map.gson.VariantListDeserializer;
import com.bergerkiller.bukkit.common.map.gson.Vector3Deserializer;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

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
    public static final MapResourcePack SERVER = new MapResourcePack("server");

    private final MapResourcePack baseResourcePack;
    protected MapResourcePackArchive archive;
    private final Map<String, MapTexture> textureCache = new HashMap<String, MapTexture>();
    private final Map<String, ConfigurationNode> yamlCache = new HashMap<String, ConfigurationNode>();
    private final Map<String, Model> modelCache = new HashMap<String, Model>();
    private final Map<BlockRenderOptions, Model> blockModelCache = new HashMap<BlockRenderOptions, Model>();
    private BlockRenderProvider currProvider = null;
    private Gson gson = null;
    private boolean loaded = false;

    /**
     * Loads a new resource pack, extending the default {@link #VANILLA} resource pack
     * 
     * @param resourcePackPath of the resource pack to load. File or URL.
     */
    public MapResourcePack(String resourcePackPath) {
        this(VANILLA, resourcePackPath, "");
    }

    /**
     * Loads a new resource pack, extending the default {@link #VANILLA} resource pack
     * 
     * @param resourcePackPath of the resource pack to load. File or URL.
     * @param resourcePackHash SHA1 hash of the resource pack to detect changes (when URL)
     */
    public MapResourcePack(String resourcePackPath, String resourcePackHash) {
        this(VANILLA, resourcePackPath, resourcePackHash);
    }

    /**
     * Loads a new resource pack, extending another one
     * 
     * @param baseResourcePack to extend
     * @param resourcePackPath of the resource pack to load. File path or URL.
     */
    public MapResourcePack(MapResourcePack baseResourcePack, String resourcePackPath) {
        this(baseResourcePack, resourcePackPath, "");
    }

    /**
     * Loads a new resource pack, extending another one
     * 
     * @param baseResourcePack to extend
     * @param resourcePackPath of the resource pack to load. File path or URL.
     * @param resourcePackHash SHA1 hash of the resource pack to detect changes (when URL)
     */
    public MapResourcePack(MapResourcePack baseResourcePack, String resourcePackPath, String resourcePackHash) {
        this.baseResourcePack = baseResourcePack;
        this.archive = null;

        // Detect the appropriate archive to use

        // Server-defined. Take over the options from the server.properties
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

    // constructor only used by the Vanilla texture pack
    protected MapResourcePack() {
        this.baseResourcePack = null;
        this.archive = null;
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
        if (lazy && !recurse) {
            Logging.LOGGER_MAPDISPLAY.warning("[Developer] You must call MapResourcePack.load() when enabling your plugin!");
            Logging.LOGGER_MAPDISPLAY.warning("[Developer] This avoids stalling the server while downloading large resource packs/Minecraft client.");
            Logging.LOGGER_MAPDISPLAY.warning("[Developer] Potential plugins that caused this: " + DebugUtil.getPluginCauses());
        }
        if (this.archive != null) {
            this.archive.load(lazy);
        }
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
        ItemRenderOptions options = ModelInfoLookup.lookupItemRenderOptions(item);
        String itemModelName = options.lookupModelName();
        Model m = this.loadModel("item/" + itemModelName, options);
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
     * @param recurse Whether to include resources found in parent resource packs in the results
     * @return Set of files matching this resource type found in the folder. Without extension.
     *         These paths can be directly used with methods like {@link #getTexture(String)}
     *         and {@link #getConfig(String)}.
     */
    public Set<String> listResources(ResourceType type, String folder, boolean recurse) {
        return listResources(type, "minecraft", folder, recurse);
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
     * @param recurse Whether to also list resources found in parent resource packs
     * @return Set of files matching this resource type found in the folder. Without extension.
     *         These paths can be directly used with methods like {@link #getTexture(String)}
     *         and {@link #getConfig(String)}.
     */
    public Set<String> listResources(ResourceType type, String namespace, String folder, boolean recurse) {
        // Must end with / to be a valid zip directory 'file'
        if (!folder.endsWith("/")) {
            folder += "/";
        }

        String zipFolderPath;
        if (folder.equals("/")) {
            zipFolderPath = type.getRoot(namespace);
        } else {
            zipFolderPath = type.getRoot(namespace) + folder;
        }

        Set<String> result = new HashSet<>();
        this.listResources(type, folder, zipFolderPath, result, false, recurse);
        return result;
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
        this.listResources(null, "", "assets/", result, true, recurse);
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
     * @param recurse Whether to include directories found in parent resource packs
     * @return Set of directory paths that are child of the folder path specified
     */
    public Set<String> listDirectories(ResourceType type, String folder, boolean recurse) {
        return listDirectories(type, "minecraft", folder, recurse);
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
     * @param recurse Whether to include directories found in parent resource packs
     * @return Set of directory paths that are child of the folder path specified
     */
    public Set<String> listDirectories(ResourceType type, String namespace, String folder, boolean recurse) {
        // Must end with / to be a valid zip directory 'file'
        if (!folder.endsWith("/")) {
            folder += "/";
        }

        String zipFolderPath;
        if (folder.equals("/")) {
            zipFolderPath = type.getRoot(namespace);
            folder = ""; // Don't return results starting with /
        } else {
            zipFolderPath = type.getRoot(namespace) + folder;
        }

        Set<String> result = new HashSet<>();
        this.listResources(type, folder, zipFolderPath, result, true, recurse);
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

        Model model = openGsonObject(Model.class, ResourceType.MODELS, path);
        if (model == null) {
            Logging.LOGGER_MAPDISPLAY.once(Level.WARNING, "Failed to load model " + path);
            return null;
        }

        // Handle overrides first
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
     * @param type
     * @param folder
     * @param rootRelFolder
     * @param result
     * @param directories Whether to list directories instead of files
     * @param recurse Whether to also look at base resource packs of this resource pack
     */
    protected void listResources(ResourceType type, String folder, String rootRelFolder, Set<String> result, boolean directories, boolean recurse) {
        // =null: failed to load resource pack file
        this.handleLoad(true, false);
        if (this.archive != null) {
            try {
                if (directories) {
                    for (String file : this.archive.listFiles(rootRelFolder)) {
                        if (file.endsWith("/")) {
                            result.add(folder + file.substring(0, file.length() - 1));
                        }
                    }
                } else {
                    for (String file : this.archive.listFiles(rootRelFolder)) {
                        if (type.isExtension(file)) {
                            result.add(folder + file.substring(0, file.length() - type.getExtension().length()));
                        }
                    }
                }
            } catch (IOException ex) {
            }
        }

        // Ask base pack as well!
        if (recurse && this.baseResourcePack != null) {
            this.baseResourcePack.listResources(type, folder, rootRelFolder, result, directories, recurse);
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

    private final <T> T readGsonObject(Class<T> objectType, InputStream inputStream, String optPath) {
        if (inputStream == null) {
            return null;
        }
        try {
            try {
                Reader reader = new InputStreamReader(inputStream, "UTF-8");
                if (this.gson == null) {
                    this.gson = createGson();
                }
                T result = this.gson.fromJson(reader, objectType);
                if (result == null) {
                    String s = (optPath == null) ? "" : (" at " + optPath);
                    throw new IOException("Failed to parse JSON for " + objectType.getSimpleName() + s);
                }
                return result;
            } finally {
                inputStream.close();
            }
        } catch (JsonSyntaxException ex) {
            String s = (optPath == null) ? "" : (" at " + optPath);
            String msg = ex.getMessage();
            msg = StringUtil.trimStart(msg, "com.bergerkiller.bukkit.common.dep.gson.stream.MalformedJsonException: ");
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Failed to parse GSON for " + objectType.getSimpleName() +
                    s + ": " + msg);
        } catch (IOException ex) {
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Unhandled IO Exception", ex);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Vector3.class, new Vector3Deserializer());
        gsonBuilder.registerTypeAdapter(BlockFace.class, new BlockFaceDeserializer());
        gsonBuilder.registerTypeAdapter(BlockModelState.VariantList.class, new VariantListDeserializer());
        gsonBuilder.registerTypeAdapter(BlockModelState.Condition.class, new ConditionalDeserializer());
        gsonBuilder.registerTypeAdapter(List.class, new NonNullListDeserializer());
        return gsonBuilder.create();
    }

    /**
     * A type of resource that can be read from a Resource Pack
     */
    public static enum ResourceType {
        /** Models found in <b>assets/minecraft/models/</b> */
        MODELS("/models/", ".json"),
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

}
