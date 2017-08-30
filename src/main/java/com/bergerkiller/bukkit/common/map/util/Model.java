package com.bergerkiller.bukkit.common.map.util;

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
import com.google.gson.annotations.SerializedName;

public class Model {
    private String parent = null;
    private int totalQuadCount = 0;
    public transient boolean placeholder = false;
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
    }

    public void build(MapResourcePack resourcePack) {
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

        // Apply all textures to the model faces
        for (Element element : this.elements) {
            element.build(resourcePack, this.textures);
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
            if (!this.display.containsKey(displayEntry.getKey())) {
                clone.display.put(displayEntry.getKey(), displayEntry.getValue().clone());
            }
        }
        for (Element element : this.elements) {
            clone.elements.add(element.clone());
        }
        return clone;
    }

    /**
     * A square element in the model
     */
    public static class Element {
        public Vector3f from = new Vector3f(0.0f, 0.0f, 0.0f);
        public Vector3f to = new Vector3f(16.0f, 16.0f, 16.0f);
        public Rotation rotation = null;
        public Map<BlockFace, Face> faces = new EnumMap<BlockFace, Face>(BlockFace.class);
        public transient Matrix4f transform = null;

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
                Matrix4f transform = new Matrix4f();
                transform.translate(rotation.origin);
                if (rotation.axis.equals("x")) {
                    transform.rotateX(rotation.angle);
                } else if (rotation.axis.equals("y")) {
                    transform.rotateY(rotation.angle);
                } else if (rotation.axis.equals("z")) {
                    transform.rotateZ(rotation.angle);
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
            public BlockFace cullface;
            public transient Quad quad = null;

            public void build(MapResourcePack resourcePack, Map<String, String> textures) {
                if (this.textureName.startsWith("#")) {
                    String texture = textures.get(this.textureName.substring(1));
                    if (texture != null) {
                        this.textureName = texture;
                    }
                }
                this.texture = resourcePack.getTexture(this.textureName);
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

                // This is used for biome colors and such
                // For now I hardcode it by overlaying a green factor color
                if (this.tintindex != -1) {
                    this.texture = this.texture.clone();
                    byte[] buffer = this.texture.getBuffer();
                    MapBlendMode.MULTIPLY.process((byte) 79, buffer);
                }
            }

            @Override
            public Face clone() {
                Face clone = new Face();
                clone.uv = (this.uv == null) ? null : this.uv.clone();
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
            public Vector3f origin = new Vector3f();
            public String axis = "y";
            public float angle = 0.0f;

            @Override
            public Rotation clone() {
                Rotation rotation = new Rotation();
                rotation.origin = this.origin.clone();
                rotation.axis = this.axis;
                rotation.angle = this.angle;
                return rotation;
            }
        }
    }

    /**
     * Display rendering options
     */
    public static class Display {
        public Vector3f rotation = new Vector3f();
        public Vector3f translation = new Vector3f();
        public Vector3f scale = new Vector3f();

        @Override
        public Display clone() {
            Display clone = new Display();
            clone.rotation = this.rotation.clone();
            clone.translation = this.translation.clone();
            clone.scale = this.scale.clone();
            return clone;
        }
    }
}
