package com.bergerkiller.bukkit.common.map.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.Logging;
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
    public transient String name = "unknown";
    private String parent = null;
    private int totalQuadCount = 0;
    public transient boolean placeholder = false;
    protected transient BuiltinType builtinType = BuiltinType.DEFAULT;
    public boolean ambientocclusion = true;
    public Map<String, Display> display = new HashMap<String, Display>();
    public Map<String, String> textures = new HashMap<String, String>();
    public List<Element> elements = new ArrayList<Element>();
    public List<ModelOverride> overrides = new ArrayList<ModelOverride>();

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
        // Mostly for debug, but can be useful elsewhere perhaps?
        this.name = options.lookupModelName();

        // Build all textures, turning paths into absolute paths
        boolean hasChanges;
        int loop_limit = 100;
        String loop_last_changed = null;
        do {
            hasChanges = false;
            for (Map.Entry<String, String> textureEntry : this.textures.entrySet()) {
                String oldTextureValue = textureEntry.getValue();
                if (oldTextureValue.startsWith("#")) {
                    String texture = this.textures.get(oldTextureValue.substring(1));
                    if (texture != null && !texture.equals(oldTextureValue)) {
                        textureEntry.setValue(texture);
                        loop_last_changed = texture;
                        hasChanges = true;
                    }
                }
            }

            // When a cyclical texture dependency exists, this could loop forever
            // Allow for a maximum of 100 loops, then log a cyclical texture loop error
            if (--loop_limit <= 0) {
                if (loop_last_changed != null) {
                    Logging.LOGGER_MAPDISPLAY.warning("Texture loop error for model " +
                        this.name + " texture " + loop_last_changed);
                }
                break;
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
                // Rescale the texture to 16x16 before continueing
                // We really cannot handle models like 600x600 - bad things really happen...
                if (result.getWidth() > 16 || result.getHeight() > 16) {
                    MapTexture newTexture = MapTexture.createEmpty(16, 16);
                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < 16; y++) {
                            int px = (x * result.getWidth()) / 16;
                            int py = (y * result.getHeight()) / 16;
                            newTexture.writePixel(x, y, result.readPixel(px, py));
                        }
                    }
                    result = newTexture;
                }

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
                                int x2 = x + bface.getModX();
                                int y2 = y + bface.getModZ();
                                if (result.readPixel(x2, y2) != MapColorPalette.COLOR_TRANSPARENT) {
                                    continue;
                                }
                            }

                            Face face = new Face();

                            face.texture = SinglePixelTexture.get(color);
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
        clone.name = this.name;
        clone.ambientocclusion = this.ambientocclusion;
        clone.textures.putAll(this.textures);
        for (Map.Entry<String, Display> displayEntry : this.display.entrySet()) {
            clone.display.put(displayEntry.getKey(), displayEntry.getValue().clone());
        }
        for (Element element : this.elements) {
            clone.elements.add(element.clone());
        }
        if (this.overrides != null && !this.overrides.isEmpty()) {
            for (ModelOverride override : this.overrides) {
                clone.overrides.add(override);
            }
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
                    int x1 = (int) ((double) uv[0] * (double) this.texture.getWidth() / 16.0);
                    int x2 = (int) ((double) uv[2] * (double) this.texture.getWidth() / 16.0);
                    int y1 = (int) ((double) uv[1] * (double) this.texture.getHeight() / 16.0);
                    int y2 = (int) ((double) uv[3] * (double) this.texture.getHeight() / 16.0);

                    // Correct X
                    if (x2 > x1)
                        --x2;
                    else if (x1 > x2)
                        --x1;

                    // Correct Y
                    if (y2 > y1)
                        --y2;
                    else if (y1 > y2)
                        --y1;

                    if (x1 == x2 && y1 == y2) {
                        // Optimization for single-pixel color textures (voxels)
                        this.texture = SinglePixelTexture.get(this.texture.readPixel(x1, y1));
                    } else {
                        // Cut UV area from texture
                        int dx = (x2 - x1);
                        int dy = (y2 - y1);

                        MapTexture texture_uv = MapTexture.createEmpty(Math.abs(dx)+1, Math.abs(dy)+1);
                        byte[] buffer = texture_uv.getBuffer();

                        int sx = (dx >= 1) ? 1 : -1;
                        int sy = (dy >= 1) ? 1 : -1;

                        int i = 0;
                        int y = y1 - sy;
                        do {
                            y += sy;

                            int x = x1 - sx;
                            do {
                                x += sx;
                                buffer[i++] = this.texture.readPixel(x, y);
                            } while (x != x2);
                        } while (y != y2);

                        this.texture = texture_uv;
                    }
                }

                // By default a rotation of 180 is used. This rotation can be altered by specifying it.
                if (this.rotation != 180 && (this.texture.getWidth() > 1 || this.texture.getHeight() > 1)) {
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
     * Model override based on a predicate.
     * When the predicate matches, the linked model is loaded instead of this one.
     */
    public static class ModelOverride {
        public Map<String, String> predicate;
        public String model;

        @Override
        public ModelOverride clone() {
            ModelOverride clone = new ModelOverride();
            clone.model = this.model;
            clone.predicate = (this.predicate == null) ? null : new HashMap<String, String>(this.predicate);
            return clone;
        }

        @Override
        public String toString() {
            return this.model + "[" + this.predicate + "]";
        }

        /**
         * Checks whether the predicate of this override matches the render options
         * 
         * @param options to check
         * @return True if it matches the predicate
         */
        public boolean matches(RenderOptions options) {
            if (this.predicate != null && !this.predicate.isEmpty()) {
                for (Map.Entry<String, String> pred : this.predicate.entrySet()) {
                    String opt = options.get(pred.getKey());
                    if (opt == null) {
                        return false;
                    }
                    if (opt.equals(pred.getValue())) {
                        continue;
                    }
                    try {
                        final double RANGE = 0.0000001;
                        double diff = Double.parseDouble(opt) - Double.parseDouble(pred.getValue());
                        return diff >= -RANGE && diff <= RANGE;
                    } catch (NumberFormatException ex) {}

                    return false; // No rounding for non-numeric values
                }
            }
            return true;
        }
    }

    /**
     * The builtin internal type that manages the further loading of this model
     */
    public static enum BuiltinType {
        DEFAULT, GENERATED
    }
}
