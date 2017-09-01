package com.bergerkiller.bukkit.common.map;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.map.gson.BlockFaceDeserializer;
import com.bergerkiller.bukkit.common.map.gson.ConditionalDeserializer;
import com.bergerkiller.bukkit.common.map.gson.VariantListDeserializer;
import com.bergerkiller.bukkit.common.map.gson.Vector3fDeserializer;
import com.bergerkiller.bukkit.common.map.util.BlockModelState;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.map.util.VanillaResourcePack;
import com.bergerkiller.bukkit.common.map.util.Vector3f;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;
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
     * and install this Minecraft client jar. This downloading is only performed once.</b><br>
     * <br>
     * The Minecraft client resources are owned by Mojang. These assets are installed separately from BKCommonLib.
     * BKCommonLib, nor its developers, distribute (illegal) copies of the Minecraft client.
     * We assume you have already accepted the Minecraft EULA to run the server. If you have not,
     * please read <a href="https://account.mojang.com/documents/minecraft_eula">https://account.mojang.com/documents/minecraft_eula.</a>
     */
    public static final VanillaResourcePack VANILLA = new VanillaResourcePack();

    private final MapResourcePack baseResourcePack;
    protected ZipFile archive;
    private final Map<String, MapTexture> textureCache = new HashMap<String, MapTexture>();
    private final Map<String, Model> modelCache = new HashMap<String, Model>();
    private final Map<BlockRenderOptions, Model> blockModelCache = new HashMap<BlockRenderOptions, Model>();
    private BlockRenderProvider currProvider = null;

    /**
     * Loads a new resource pack, extending the default {@link #VANILLA} resource pack
     * 
     * @param resourcePackFilePath of the resource pack to load
     */
    public MapResourcePack(String resourcePackFilePath) {
        this(VANILLA, resourcePackFilePath);
    }

    /**
     * Loads a new resource pack, extending another one
     * 
     * @param baseResourcePack to extend
     * @param resourcePackFilePath of the resource pack to load
     */
    public MapResourcePack(MapResourcePack baseResourcePack, String resourcePackFilePath) {
        this.baseResourcePack = baseResourcePack;
        try {
            this.archive = new JarFile(resourcePackFilePath);
        } catch (IOException ex) {
            this.archive = null;
            Logging.LOGGER.log(Level.SEVERE, "Failed to load resource pack", ex);
        }
    }

    // constructor only used by the Vanilla texture pack
    protected MapResourcePack() {
        this.baseResourcePack = null;
        this.archive = null;
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
                model = this.createPlaceholderModel();
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
            model = this.createPlaceholderModel(); // failed to load or find
        }
        modelCache.put(path, model);
        return model;
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
                //System.out.println("Failed to load texture: " + path);
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
                return this.loadBlockVariant(variants.get(0));
            }

            // Add all variant elements to the model
            Model result = new Model();
            boolean succ = true;
            for (BlockModelState.Variant variant : variants) {
                Model subModel = this.loadBlockVariant(variant);
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

    private Model loadBlockVariant(BlockModelState.Variant variant) {
        Model model = this.loadModel("block/" + variant.modelName);
        if (model == null) {
            return null;
        }
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
        Model model = openGsonObject(Model.class, ResourceType.MODELS, path);
        if (model == null) {
            Logging.LOGGER_MAPDISPLAY.warning("Failed to load model " + path);
            return null;
        }

        // Insert the parent model as required
        if (model.getParentName() != null) {
            Model parentModel = getModel(model.getParentName());
            if (parentModel == null || parentModel.placeholder) {
                Logging.LOGGER_MAPDISPLAY.warning("Parent of model " + path + " not found: " + model.getParentName());
                return null;
            }
            model.loadParent(parentModel);
        }

        // Make all texture paths absolute
        model.build(this);
        return model;
    }

    /**
     * Creates a placeholder model. Used when models can not be loaded.
     * 
     * @return placeholder model
     */
    protected final Model createPlaceholderModel() {
        Model model = new Model();
        Model.Element element = new Model.Element();
        for (BlockFace face : FaceUtil.BLOCK_SIDES) {
            element.faces.put(face, createPlaceholderFace());
        }
        element.buildQuads();
        model.placeholder = true;
        model.elements.add(element);
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
        MapTexture result = MapTexture.createEmpty(16, 16);
        result.fill(MapColorPalette.COLOR_PURPLE);
        result.fillRectangle(0, 0, 8, 8, MapColorPalette.COLOR_BLUE);
        result.fillRectangle(8, 8, 8, 8, MapColorPalette.COLOR_BLUE);
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
        if (this.archive != null) {
            try {
                ZipEntry entry = this.archive.getEntry(type.makePath(path));
                if (entry != null) {
                    InputStream stream = this.archive.getInputStream(entry);
                    if (stream != null) {
                        return stream;
                    }
                }
            } catch (IOException ex) {
            }
            if (this.baseResourcePack != null) {
                InputStream stream =  this.baseResourcePack.openFileStream(type, path);
                if (stream != null) {
                    return stream;
                }
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
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Vector3f.class, new Vector3fDeserializer());
                gsonBuilder.registerTypeAdapter(BlockFace.class, new BlockFaceDeserializer());
                gsonBuilder.registerTypeAdapter(BlockModelState.VariantList.class, new VariantListDeserializer());
                gsonBuilder.registerTypeAdapter(BlockModelState.Condition.class, new ConditionalDeserializer());
                Gson gson = gsonBuilder.create();
                T result = gson.fromJson(reader, objectType);
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
