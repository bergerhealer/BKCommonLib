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

import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.map.gson.BlockFaceDeserializer;
import com.bergerkiller.bukkit.common.map.gson.Vector3fDeserializer;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.map.util.Vector3f;
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

    public MapTexture getTexture(String path) {
        MapTexture result = textureCache.get(path);
        if (result == null) {
            InputStream inputStream = openFileStream(ResourceType.TEXTURES, path);
            if (inputStream != null) {
                result = MapTexture.fromStream(inputStream);
            }
            if (result == null) {
                //System.out.println("Failed to load texture: " + path);
                result = MapTexture.createEmpty(16, 16);
                result.fill(MapColorPalette.COLOR_PURPLE);
                result.fillRectangle(0, 0, 8, 8, MapColorPalette.COLOR_BLUE);
                result.fillRectangle(8, 8, 8, 8, MapColorPalette.COLOR_BLUE);
            }
            textureCache.put(path, result);
        }
        return result;
    }

    public Model getModel(String path) {
        Model model = modelCache.get(path);
        if (model != null) {
            return model;
        }

        InputStream inputStream = openFileStream(ResourceType.MODELS, path);
        if (inputStream == null) {
            return null;
        }

        try {
            try {
                Reader reader = new InputStreamReader(inputStream, "UTF-8");
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Vector3f.class, new Vector3fDeserializer());
                gsonBuilder.registerTypeAdapter(BlockFace.class, new BlockFaceDeserializer());
                Gson gson = gsonBuilder.create();
                model = gson.fromJson(reader, Model.class);
                if (model == null) {
                    return null;
                }

                // Insert the parent model as required
                if (model.getParentName() != null) {
                    Model parentModel = getModel(model.getParentName());
                    if (parentModel == null) {
                        return null;
                    }
                    model.loadParent(parentModel);
                }

                // Make all texture paths absolute
                model.build(this);

                // Store for later (re-)use
                modelCache.put(path, model);

                return model;
            } finally {
                inputStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
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
