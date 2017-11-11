package com.bergerkiller.bukkit.common.map;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.internal.resources.builtin.GeneratedModel;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackArchive;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackAutoArchive;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackClientArchive;
import com.bergerkiller.bukkit.common.map.gson.BlockFaceDeserializer;
import com.bergerkiller.bukkit.common.map.gson.ConditionalDeserializer;
import com.bergerkiller.bukkit.common.map.gson.VariantListDeserializer;
import com.bergerkiller.bukkit.common.map.gson.Vector3Deserializer;
import com.bergerkiller.bukkit.common.map.util.ModelInfoLookup;
import com.bergerkiller.bukkit.common.map.util.BlockModelState;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.map.util.VanillaResourcePack;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.utils.DebugUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
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
     * your plugin accesses this resource pack.
     */
    public static final MapResourcePack SERVER = new MapResourcePack("server");

    private final MapResourcePack baseResourcePack;
    protected MapResourcePackArchive archive;
    private final Map<String, MapTexture> textureCache = new HashMap<String, MapTexture>();
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
            MinecraftServerHandle mcs = MinecraftServerHandle.instance();
            resourcePackPath = mcs.getResourcePack();
            resourcePackHash = mcs.getResourcePackHash();
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
                model = this.createPlaceholderModel(blockRenderOptions);
            }
            blockModelCache.put(blockRenderOptions, model);
        }
        return model;
    }

    /**
     * Loads a JSON-syntax model from this resource pack.
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
            model = this.createPlaceholderModel(BlockData.AIR.getDefaultRenderOptions()); // failed to load or find
            model.name = path;
        }
        modelCache.put(path, model);
        return model;
    }

    /**
     * Loads the model to be displayed for items displayed in the world, held in a player's hand
     * or shown in a GUI item slot.
     * 
     * @param item to get the model for
     * @return item model for the item
     */
    public Model getItemModel(ItemStack item) {
        ItemRenderOptions options = ModelInfoLookup.lookupItemRenderOptions(item);
        String itemModelName = options.lookupModelName();
        Model m = this.loadModel("item/" + itemModelName, options);
        if (m != null) {
            m.buildBlock(options);
            m.buildQuads();
        }
        if (m == null) {
            m = this.createPlaceholderModel(options);
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
        Model model = this.getItemModel(item);
        if (model == null || model.placeholder) {
            return createPlaceholderTexture(width, height);
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
     * Loads a texture from this resource pack.
     * 
     * @param path to the texture, e.g. "blocks/stone"
     * @return the texture, a placeholder texture if it could not be found
     */
    public MapTexture getTexture(String path) {
        MapTexture result = textureCache.get(path);
        if (result == null) {
            InputStream inputStream = openFileStream(ResourceType.TEXTURES, path);
            if (inputStream != null) {
                result = MapTexture.fromStream(inputStream);
            }
            if (result == null) {
                if (!path.startsWith("#")) {
                    Logging.LOGGER_MAPDISPLAY.once(Level.WARNING, "Failed to load texture: " + path);
                }
                result = this.createPlaceholderTexture();
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
        if (blockRenderOptions.getBlockData().getType() == Material.AIR) {
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
                variant.modelName = blockName;
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
        Model model = this.loadModel("block/" + variant.modelName, blockRenderOptions);
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
        if (model.overrides != null && !model.overrides.isEmpty()) {
            for (Model.ModelOverride override : model.overrides) {
                if (override.matches(options)) {
                    //System.out.println("MATCH " + override.model + "  " + options);
                    return this.loadModel(override.model, options);
                }
            }
        }

        // Insert the parent model as required
        if (model.getParentName() != null) {
            Model parentModel = this.loadModel(model.getParentName(), options);
            if (parentModel == null || parentModel.placeholder) {
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
     * Creates a placeholder model. Used when models can not be loaded.
     * 
     * @return placeholder model
     */
    protected final Model createPlaceholderModel(RenderOptions renderOptions) {
        Model model = new Model();
        Model.Element element = new Model.Element();
        for (BlockFace face : FaceUtil.BLOCK_SIDES) {
            element.faces.put(face, createPlaceholderFace());
        }
        element.buildQuads();
        model.placeholder = true;
        model.elements.add(element);
        model.name = renderOptions.lookupModelName();
        return model;
    }

    private final Model.Element.Face createPlaceholderFace() {
        Model.Element.Face face = new Model.Element.Face();
        face.texture = this.createPlaceholderTexture();
        return face;
    }

    /**
     * Creates a placeholder 16x16 texture. Used when textures can not be loaded.
     * 
     * @return placeholder texture
     */
    protected final MapTexture createPlaceholderTexture() {
        return createPlaceholderTexture(16, 16);
    }

    /**
     * Creates a placeholder 16x16 texture. Used when textures can not be loaded.
     * 
     * @param width
     * @param height
     * @return placeholder texture
     */
    protected final MapTexture createPlaceholderTexture(int width, int height) {
        int wd2 = width >> 2;
        int hd2 = height >> 2;
        MapTexture result = MapTexture.createEmpty(width, height);
        result.fill(MapColorPalette.COLOR_PURPLE);
        result.fillRectangle(0, 0, wd2, hd2, MapColorPalette.COLOR_BLUE);
        result.fillRectangle(wd2, hd2, width - wd2, height - hd2, MapColorPalette.COLOR_BLUE);
        return result;
    }

    /**
     * Attempts to find a file in the resource pack and open it for reading
     * 
     * @param type of resource to find
     * @param path of the resource (relative)
     * @return InputStream to read the file from, null if not found
     */
    protected InputStream openFileStream(ResourceType type, String path) {
        // =null: failed to load resource pack file
        this.handleLoad(true, false);
        if (this.archive != null) {
            try {
                InputStream stream = this.archive.openFileStream(type.makePath(path));
                if (stream != null) {
                    return stream;
                }
            } catch (IOException ex) {
            }
        }

        // Fallback: try the underlying resource pack (usually Vanilla)
        if (this.baseResourcePack != null) {
            InputStream stream =  this.baseResourcePack.openFileStream(type, path);
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
     * @param objectType to deserialize the JSON as
     * @param type of resource
     * @param path of the resource
     * @return loaded Gson object, or <i>null</i> if not found or loadable
     */
    protected final <T> T openGsonObject(Class<T> objectType, ResourceType type, String path) {
        InputStream inputStream = this.openFileStream(type, path);
        if (inputStream == null) {
            return null;
        }
        try {
            try {
                Reader reader = new InputStreamReader(inputStream, "UTF-8");
                if (this.gson == null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(Vector3.class, new Vector3Deserializer());
                    gsonBuilder.registerTypeAdapter(BlockFace.class, new BlockFaceDeserializer());
                    gsonBuilder.registerTypeAdapter(BlockModelState.VariantList.class, new VariantListDeserializer());
                    gsonBuilder.registerTypeAdapter(BlockModelState.Condition.class, new ConditionalDeserializer());
                    this.gson = gsonBuilder.create();
                }
                T result = this.gson.fromJson(reader, objectType);
                if (result == null) {
                    throw new IOException("Failed to parse JSON for " + objectType.getSimpleName() + " at " + path);
                }
                return result;
            } finally {
                inputStream.close();
            }
        } catch (JsonSyntaxException ex) {
            System.out.println("Failed to parse GSON for " + objectType.getSimpleName() + " at " + path + ": " + ex.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * A type of resource that can be read from a Resource Pack
     */
    public static enum ResourceType {
        MODELS("models/", ".json"),
        BLOCKSTATES("blockstates/", ".json"),
        TEXTURES("textures/", ".png");

        private final String root;
        private final String ext;

        private ResourceType(String root, String ext) {
            this.root = root;
            this.ext = ext;
        }

        public String getRoot() {
            return this.root;
        }

        public String getExtension() {
            return this.ext;
        }

        public String makePath(String path) {
            return "assets/minecraft/" + this.root + path + this.ext;
        }

        public String makeBKCPath(String path) {
            return "/com/bergerkiller/bukkit/common/internal/resources/" + this.root + path + this.ext;
        }
    }

}
