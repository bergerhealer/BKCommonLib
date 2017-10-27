package com.bergerkiller.bukkit.common.map.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.map.MapBlendMode;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.util.Model.Element.Face;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.wrappers.RenderOptions;
import com.google.gson.annotations.SerializedName;

public class Model {
    private String parent = null;
    private int totalQuadCount = 0;
    public transient boolean placeholder = false;
    protected transient BuiltinType builtinType = BuiltinType.DEFAULT;
    public boolean ambientocclusion = true;
    public Map<String, Display> display = new HashMap<String, Display>();
    public Map<String, String> textures = new HashMap<String, String>();
    public List<Element> elements = new ArrayList<Element>();

    public final String getParentName() {
        return this.parent;
    }

    public void loadParent(Model parentModel) {
        for (Map.Entry<String, String> textureEntry : parentModel.textures.entrySet()) {
            if (!this.textures.containsKey(textureEntry.getKey())) {
                this.textures.put(textureEntry.getKey(), textureEntry.getValue());
            }
        }
        for (Map.Entry<String, Display> displayEntry : parentModel.display.entrySet()) {
            if (!this.display.containsKey(displayEntry.getKey())) {
                this.display.put(displayEntry.getKey(), displayEntry.getValue().clone());
            }
        }

        int elementIdx = 0;
        for (Element element : parentModel.elements) {
            this.elements.add(elementIdx++, element.clone());
        }

        this.builtinType = parentModel.builtinType;
    }

    public void build(MapResourcePack resourcePack, RenderOptions options) {
        // Build all textures, turning paths into absolute paths
        boolean hasChanges;
        do {
            hasChanges = false;
            for (Map.Entry<String, String> textureEntry : this.textures.entrySet()) {
                if (textureEntry.getValue().startsWith("#")) {
                    String texture = this.textures.get(textureEntry.getValue().substring(1));
                    if (texture != null) {
                        textureEntry.setValue(texture);
                        hasChanges = true;
                    }
                }
            }
        } while (hasChanges);

        // For generated models, we must now generate the model elements up-front
        // This basically creates a small cube for every non-transparent pixel in the texture
        if (this.builtinType == BuiltinType.GENERATED) {
            this.elements.clear();

            MapTexture result = null;
            for (int i = 0;;i++) {
                String layerKey = "layer" + i;
                String layerTexturePath = this.textures.get(layerKey);
                if (layerTexturePath == null) {
                    break;
                }
                MapTexture texture = resourcePack.getTexture(layerTexturePath);

                // Item-specific layer render colors
                texture = applyTint(texture, options.get(layerKey + "tint"));

                if (result == null) {
                    result = texture.clone();
                } else {
                    result.draw(texture, 0, 0);
                }
            }

            if (result != null) {
                for (int y = 0; y < result.getHeight(); y++) {
                    for (int x = 0; x < result.getWidth(); x++) {
                        byte color = result.readPixel(x, y);
                        if (color == MapColorPalette.COLOR_TRANSPARENT) {
                            continue;
                        }
                        
                        Element element = new Element();
                        element.from = new Vector3(x, 0, y);
                        element.to = new Vector3(element.from.x + 1, element.from.y + 1, element.from.z + 1);

                        for (BlockFace bface : FaceUtil.BLOCK_SIDES) {
                            // If pixel on this face is not transparent, do not add one there
                            if (!FaceUtil.isVertical(bface)) {
                                int x2 = x - bface.getModX();
                                int y2 = y - bface.getModZ();
                                if (result.readPixel(x2, y2) == MapColorPalette.COLOR_TRANSPARENT) {
                                    continue;
                                }
                            }
                            
                            
                            Face face = new Face();
                            
                            MapTexture tex = MapTexture.createEmpty(1, 1);
                            tex.writePixel(0, 0, color);
                            
                            face.texture = tex;
                            element.faces.put(bface, face);
                        }
                        this.elements.add(element);
                    }
                }
            }
        }

        // Apply all textures to the model faces
        for (Element element : this.elements) {
            element.build(resourcePack, this.textures);
        }
    }

    /**
     * Called after build to initialize block-specific render options
     * 
     * @param options
     */
    public void buildBlock(RenderOptions options) {
        for (Element element : this.elements) {
            for (Element.Face face : element.faces.values()) {
                face.buildBlock(options);
            }
        }
    }

    /**
     * Builds the quads for all elements of this Model. See also: {@link Element#buildQuads()}
     */
    public void buildQuads() {
        for (Element element : this.elements) {
            element.buildQuads();
        }
    }

    public List<Quad> getQuads() {
        ArrayList<Quad> result = new ArrayList<Quad>(this.totalQuadCount);
        for (Element element : this.elements) {
            for (Face face : element.faces.values()) {
                result.add(face.quad.clone());
            }
        }
        this.totalQuadCount = result.size();
        return result;
    }

    @Override
    public Model clone() {
        Model clone = new Model();
        clone.ambientocclusion = this.ambientocclusion;
        clone.textures.putAll(this.textures);
        for (Map.Entry<String, Display> displayEntry : this.display.entrySet()) {
            clone.display.put(displayEntry.getKey(), displayEntry.getValue().clone());
        }
        for (Element element : this.elements) {
            clone.elements.add(element.clone());
        }
        return clone;
    }

    private static MapTexture applyTint(MapTexture input, String tint) {
        if (tint == null || input == null) {
            return input;
        }
        try {
            byte color = MapColorPalette.getColor(Color.decode(tint));
            MapTexture result = input.clone();
            byte[] buffer = result.getBuffer();
            MapBlendMode.MULTIPLY.process(color, buffer);
            return result;
        } catch (NumberFormatException ex) {
        }
        return input;
    }

    /**
     * A square element in the model
     */
    public static class Element {
        public Vector3 from = new Vector3(0.0f, 0.0f, 0.0f);
        public Vector3 to = new Vector3(16.0f, 16.0f, 16.0f);
        public Rotation rotation = null;
        public Map<BlockFace, Face> faces = new EnumMap<BlockFace, Face>(BlockFace.class);
        public transient Matrix4x4 transform = null;

        public void build(MapResourcePack resourcePack, Map<String, String> textures) {
            for (Face face : faces.values()) {
                face.build(resourcePack, textures);
            }
            this.buildQuads();
        }

        /**
         * Uses this Model's size (from/to), rotation and transform to build the {@link Face#quad}
         * field for all faces of this Element. After changing properties of this Model,
         * this function should be called again.
         */
        public void buildQuads() {
            for (Map.Entry<BlockFace, Face> faceEntry : faces.entrySet()) {
                Face face = faceEntry.getValue();
                face.quad = new Quad(faceEntry.getKey(), from.clone(), to.clone(), face.texture);
            }

            if (rotation != null) {
                Matrix4x4 transform = new Matrix4x4();
                transform.translate(rotation.origin);
                if (rotation.axis.equals("x")) {
                    transform.rotateX(rotation.angle);
                    if (rotation.rescale) {
                        transform.scale(1.0f, 1.0f / transform.m21, 1.0f / transform.m22);
                    }
                } else if (rotation.axis.equals("y")) {
                    transform.rotateY(rotation.angle);
                    if (rotation.rescale) {
                        transform.scale(1.0f / transform.m00, 1.0f, 1.0f / transform.m02);
                    }
                } else if (rotation.axis.equals("z")) {
                    transform.rotateZ(rotation.angle);
                    if (rotation.rescale) {
                        transform.scale(1.0f / transform.m10, 1.0f / transform.m11, 1.0f);
                    }
                }
                transform.translate(rotation.origin.negate());

                for (Face face : faces.values()) {
                    transform.transformQuad(face.quad);
                }
            }

            // If set, this transforms the entire model is desired
            // Used by variants, but could be used for other things, too
            if (this.transform != null) {
                for (Face face : faces.values()) {
                    this.transform.transformQuad(face.quad);
                }
            }

            // Merge quad vector instances when they have the same values
            // This makes manipulation much easier
            for (Face face : faces.values()) {
                for (Face otherFace : faces.values()) {
                    if (face != otherFace) {
                        face.quad.mergePoints(otherFace.quad);
                    }
                }
            }
        }

        @Override
        public Element clone() {
            Element clone = new Element();
            clone.from = this.from.clone();
            clone.to = this.to.clone();
            clone.transform = (this.transform == null) ? null : this.transform.clone();
            clone.rotation = (this.rotation == null) ? null : this.rotation.clone();
            for (Map.Entry<BlockFace, Face> face : this.faces.entrySet()) {
                clone.faces.put(face.getKey(), face.getValue().clone());
            }
            return clone;
        }

        /**
         * A single textured quad
         */
        public static class Face {
            @SerializedName("texture")
            private String textureName = "";
            private float[] uv = null;
            public transient MapTexture texture = null;
            public int tintindex = -1;
            public int rotation = 180;
            public BlockFace cullface;
            public transient Quad quad = null;

            public void build(MapResourcePack resourcePack, Map<String, String> textures) {
                if (this.textureName.startsWith("#")) {
                    String texture = textures.get(this.textureName.substring(1));
                    if (texture != null) {
                        this.textureName = texture;
                    }
                }
                if (!this.textureName.isEmpty()) {
                    this.texture = resourcePack.getTexture(this.textureName);
                }
                if (uv != null) {
                    int[] i_uv = new int[uv.length];
                    for (int i = 0; i < uv.length; i++) {
                        i_uv[i] = (int) Math.ceil(uv[i]);
                    }

                    int dx = i_uv[2] - i_uv[0];
                    int dy = i_uv[3] - i_uv[1];
                    int sx = (dx >= 1) ? 1 : -1;
                    int sy = (dy >= 1) ? 1 : -1;
                    int ox = (dx >= 1) ? 0 : 1;
                    int oy = (dy >= 1) ? 0 : 1;
                    MapTexture texture_uv = MapTexture.createEmpty(Math.abs(dx), Math.abs(dy));
                    byte[] buffer = texture_uv.getBuffer();
                    int index = 0;
                    for (int y = i_uv[1]; y != i_uv[3]; y += sy) {
                        for (int x = i_uv[0]; x != i_uv[2]; x += sx) {
                            int px = x - ox;
                            int py = y - oy;
                            if (px < 0 || py < 0 || px >= this.texture.getWidth() || py >= this.texture.getHeight()) {
                                buffer[index] = MapColorPalette.COLOR_GREEN;
                            } else {
                                int a = x - ox;
                                int b = y - oy;
                                if (sx == -1) {
                                    a = texture.getWidth() - a - 1;
                                }
                                if (sy == -1) {
                                    b = texture.getHeight() - b - 1;
                                }
                                
                                buffer[index] = this.texture.readPixel(a, b);
                            }
                            index++;
                        }
                    }
                    this.texture = texture_uv;
                }

                // By default a rotation of 180 is used. This rotation can be altered by specifying it.
                if (this.rotation != 180) {
                    this.texture = MapTexture.rotate(this.texture, this.rotation + 180);
                }
            }

            /**
             * Called after build to initialize block-specific render options
             * 
             * @param options
             */
            public void buildBlock(RenderOptions options) {
                if (this.tintindex != -1) {
                    this.texture = applyTint(this.texture, options.get("tint"));
                }
            }

            @Override
            public Face clone() {
                Face clone = new Face();
                clone.uv = (this.uv == null) ? null : this.uv.clone();
                clone.rotation = this.rotation;
                clone.textureName = this.textureName;
                clone.texture = this.texture.clone();
                clone.cullface = this.cullface;
                clone.tintindex = this.tintindex;
                clone.quad = this.quad.clone();
                return clone;
            }
        }

        /**
         * The rotation of a model element
         */
        public static class Rotation implements Cloneable {
            public Vector3 origin = new Vector3();
            public String axis = "y";
            public float angle = 0.0f;
            public boolean rescale = false;

            @Override
            public Rotation clone() {
                Rotation rotation = new Rotation();
                rotation.origin = this.origin.clone();
                rotation.axis = this.axis;
                rotation.angle = this.angle;
                rotation.rescale = this.rescale;
                return rotation;
            }
        }
    }

    /**
     * Display rendering options
     */
    public static class Display {
        public Vector3 rotation = new Vector3();
        public Vector3 translation = new Vector3();
        public Vector3 scale = new Vector3(1, 1, 1);

        public void apply(Matrix4x4 transform) {
            transform.translate(8, 8, 8);
            transform.rotateZ(this.rotation.z);
            transform.rotateX(this.rotation.x - 90.0);
            transform.rotateY(this.rotation.y);
            transform.scale(this.scale);
            transform.translate(-8, -8, -8);
            transform.translate(this.translation);
        }

        @Override
        public Display clone() {
            Display clone = new Display();
            clone.rotation = this.rotation.clone();
            clone.translation = this.translation.clone();
            clone.scale = this.scale.clone();
            return clone;
        }
    }

    /**
     * The builtin internal type that manages the further loading of this model
     */
    public static enum BuiltinType {
        DEFAULT, GENERATED
    }
}
