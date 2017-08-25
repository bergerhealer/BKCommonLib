package com.bergerkiller.bukkit.common.map;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.map.gson.BlockFaceDeserializer;
import com.bergerkiller.bukkit.common.map.gson.Vector3fDeserializer;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.map.util.Vector3f;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Retrieves Block model textures from a Minecraft jar or zip texture pack archive
 */
public class MapResourcePack {
    private final MapResourcePack baseTexturePack;
    private final ZipFile archive;
    private final Map<String, MapTexture> textureCache = new HashMap<String, MapTexture>();
    private final Map<String, Model> modelCache = new HashMap<String, Model>();
    private final Map<BlockData, Model> blockModelCache = new HashMap<BlockData, Model>();

    public MapResourcePack(String texturePackFilePath) {
        this(null, texturePackFilePath);
    }

    public MapResourcePack(MapResourcePack baseTexturePack, String texturePackFilePath) {
        this.baseTexturePack = baseTexturePack;
        
        try {
            JarFile jarFile = new JarFile(texturePackFilePath);
            
            archive = jarFile;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Loads the model for a particular Block, specific to that Block's data value.
     * 
     * @param blockMaterial of the block
     * @return block model
     */
    public Model getBlockModel(Material blockMaterial) {
        return getBlockModel(BlockData.fromMaterial(blockMaterial));
    }

    /**
     * Loads the model for a particular Block, specific to that Block's data value.
     * 
     * @param blockMaterial of the block
     * @param data of the block
     * @return block model
     */
    @SuppressWarnings("deprecation")
    public Model getBlockModel(Material blockMaterial, int data) {
        return getBlockModel(BlockData.fromMaterialData(blockMaterial, data));
    }

    /**
     * Loads the model for a particular Block, specific to that Block's data value.
     * 
     * @param blockData of the block
     * @return block model
     */
    public Model getBlockModel(BlockData blockData) {
        Model model = blockModelCache.get(blockData);
        if (model == null) {
            if (blockData != null) {
                model = this.loadBlockModel(blockData);
            }
            if (model == null) {
                model = this.createPlaceholderModel();
            }
            blockModelCache.put(blockData, model);
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
     * @param blockData of the block
     * @return the model, or <i>null</i> if not found
     */
    protected final Model loadBlockModel(BlockData blockData) {
        Model blockModel = this.loadModel("block/" + blockData.getBlockName());
        if (blockModel == null) {
            return null;
        }

        //TODO: block variants!
        return blockModel;
    }

    /**
     * Loads a model, always fetching it from the resource pack instead of the cache
     * 
     * @param path to find the model at
     * @return the model, or <i>null</i> if not found
     */
    protected final Model loadModel(String path) {
        InputStream inputStream = openFileStream(ResourceType.MODELS, path);
        if (inputStream == null) {
            return null; // not found
        }

        try {
            try {
                Reader reader = new InputStreamReader(inputStream, "UTF-8");

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Vector3f.class, new Vector3fDeserializer());
                gsonBuilder.registerTypeAdapter(BlockFace.class, new BlockFaceDeserializer());
                Gson gson = gsonBuilder.create();
                Model model = gson.fromJson(reader, Model.class);
                if (model == null) {
                    throw new IOException("Model " + path + " could not be parsed from JSON");
                }

                // Insert the parent model as required
                if (model.getParentName() != null) {
                    Model parentModel = getModel(model.getParentName());
                    if (parentModel == null) {
                        throw new IOException("Parent of model " + path + 
                                " not found: " + model.getParentName());
                    }
                    model.loadParent(parentModel);
                }

                // Make all texture paths absolute
                model.build(this);
                return model;
            } finally {
                inputStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null; // error
    }

    /**
     * Creates a placeholder model. Used when models can not be loaded.
     * 
     * @return placeholder model
     */
    protected final Model createPlaceholderModel() {
        Model model = new Model();
        Model.Element element = new Model.Element();
        element.from = new Vector3f(0, 0, 0);
        element.to = new Vector3f(16, 16, 16);
        for (BlockFace face : FaceUtil.BLOCK_SIDES) {
            element.faces.put(face, createPlaceholderFace());
        }
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
    protected final InputStream openFileStream(ResourceType type, String path) {
        try {
            ZipEntry entry = this.archive.getEntry(type.getRoot() + path + type.getExtension());
            if (entry != null) {
                return this.archive.getInputStream(entry);
            }
        } catch (IOException ex) {
        }
        if (this.baseTexturePack != null) {
            return this.baseTexturePack.openFileStream(type, path);
        } else {
            return null;
        }
    }

    /**
     * A type of resource that can be read from a Resource Pack
     */
    public static enum ResourceType {
        MODELS("assets/minecraft/models/", ".json"),
        TEXTURES("assets/minecraft/textures/", ".png");

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
    }
}
