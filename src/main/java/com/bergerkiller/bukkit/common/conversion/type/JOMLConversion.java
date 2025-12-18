package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.util.Vector;

/**
 * Conversion from org.joml types to BKCommonLib math primitives.
 * Only used on version 1.17 and later when JOML was introduced.
 */
public class JOMLConversion {
    /** Class type for a mutable Vector3f */
    public static final Class<?> JOML_VECTOR3F_TYPE = LogicUtil.tryMake(() -> Class.forName("org.joml.Vector3f"), null);
    /** Interface for a read-only (constant) Vector3f. Used after 1.21.11 to store vectors in data watchers. */
    public static final Class<?> JOML_VECTOR3F_CONSTANT_TYPE = LogicUtil.tryMake(() -> Class.forName("org.joml.Vector3fc"), null);

    /** Class type for a mutable Quaternion3f */
    public static final Class<?> JOML_QUATERNIONF_TYPE = LogicUtil.tryMake(() -> Class.forName("org.joml.Quaternionf"), null);
    /** Class type for a read-only (constant) Quaternion3f. Used after 1.21.11 to store quaternions in data watchers. */
    public static final Class<?> JOML_QUATERNIONF_CONSTANT_TYPE = LogicUtil.tryMake(() -> Class.forName("org.joml.Quaternionfc"), null);

    private static final ConversionLogic LOGIC = (JOML_VECTOR3F_TYPE != null && JOML_QUATERNIONF_TYPE != null)
            ? Template.Class.create(ConversionLogic.class) : null;

    public static boolean available() {
        return LOGIC != null;
    }

    public static void init() {
        if (LOGIC != null) {
            LOGIC.forceInitialization();
        }
    }

    @ConverterMethod(input="org.joml.Vector3fc")
    public static Vector toVector(Object vector3f) {
        return LOGIC.decodeVector3f(vector3f);
    }

    @ConverterMethod(output="org.joml.Vector3f")
    public static Object fromVector(Vector vector) {
        return LOGIC.encodeVector3f(vector);
    }

    @ConverterMethod(input="org.joml.Quaternionfc")
    public static Quaternion toQuaternion(Object quaternionf) {
        return LOGIC.decodeQuaternionf(quaternionf);
    }

    @ConverterMethod(output="org.joml.Quaternionf")
    public static Object fromQuaternion(Quaternion quaternion) {
        return LOGIC.encodeQuaternionf(quaternion);
    }

    @Template.Import("org.joml.Vector3f")
    @Template.Import("org.joml.Vector3fc")
    @Template.Import("org.joml.Quaternionf")
    @Template.Import("org.joml.Quaternionfc")
    @Template.Import("org.bukkit.util.Vector")
    @Template.Import("com.bergerkiller.bukkit.common.math.Quaternion")
    public static abstract class ConversionLogic extends Template.Class<Template.Handle> {
        /*
         * <ENCODE_VECTOR3F_XYZ>
         * public static Vector3f encode(float x, float y, float z) {
         *     return new Vector3f(x, y, z);
         * }
         */
        @Template.Generated("%ENCODE_VECTOR3F_XYZ%")
        public abstract Object encodeVector3f(float x, float y, float z);

        /*
         * <ENCODE_VECTOR3F_V>
         * public static Vector3f encode(Vector v) {
         *     return new Vector3f((float) v.getX(), (float) v.getY(), (float) v.getZ());
         * }
         */
        @Template.Generated("%ENCODE_VECTOR3F_V%")
        public abstract Object encodeVector3f(Vector vector);

        /*
         * <DECODE_VECTOR3F>
         * public static Vector decode(Vector3fc v) {
         *     return new Vector((double) v.x(), (double) v.y(), (double) v.z());
         * }
         */
        @Template.Generated("%DECODE_VECTOR3F%")
        public abstract Vector decodeVector3f(Object vector);

        /*
         * <ENCODE_QUATERNIONF>
         * public static Quaternionf encode(Quaternion q) {
         *     return new Quaternionf(q.getX(), q.getY(), q.getZ(), q.getW());
         * }
         */
        @Template.Generated("%ENCODE_QUATERNIONF%")
        public abstract Object encodeQuaternionf(Quaternion quaternion);

        /*
         * <DECODE_QUATERNIONF>
         * public static Quaternion decode(Quaternionfc q) {
         *     return new Quaternion((double) q.x(), (double) q.y(), (double) q.z(), (double) q.w());
         * }
         */
        @Template.Generated("%DECODE_QUATERNIONF%")
        public abstract Quaternion decodeQuaternionf(Object quaternion);
    }
}
