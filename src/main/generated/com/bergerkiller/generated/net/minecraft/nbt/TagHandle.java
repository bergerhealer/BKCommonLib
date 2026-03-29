package com.bergerkiller.generated.net.minecraft.nbt;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.nbt.Tag</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.nbt.Tag")
public abstract class TagHandle extends Template.Handle {
    /** @see TagClass */
    public static final TagClass T = Template.Class.create(TagClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static TagHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract byte getTypeId();
    public abstract Object raw_clone();
    public com.bergerkiller.bukkit.common.nbt.CommonTag toCommonTag() {
        return new com.bergerkiller.bukkit.common.nbt.CommonTag(this);
    }
    public abstract TagHandle clone();
    public abstract Object getData();

    public final String toPrettyString() {
        StringBuilder str = new StringBuilder(100);
        toPrettyString(str, 0);
        return str.toString();
    }

    public void toPrettyString(StringBuilder str, int indent) {
        while (indent-- > 0) {
            str.append("  ");
        }
        Object data = getData();
        if (data == null) {
            str.append("UNKNOWN[").append(getTypeId()).append("]");
        } else {
            Class<?> unboxedType = com.bergerkiller.mountiplex.reflection.util.BoxedType.getUnboxedType(data.getClass());
            if (unboxedType != null) {
                str.append(unboxedType.getSimpleName());
            } else {
                str.append(data.getClass().getSimpleName());
            }
            str.append(": ");

            if (data instanceof byte[]) {
                byte[] values = (byte[]) data;
                str.append("[");
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) str.append(", ");
                    str.append(values[i]);
                }
                str.append("]");
            } else if (data instanceof int[]) {
                int[] values = (int[]) data;
                str.append("[");
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) str.append(", ");
                    str.append(values[i]);
                }
                str.append("]");
            } else if (data instanceof long[]) {
                long[] values = (long[]) data;
                str.append("[");
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) str.append(", ");
                    str.append(values[i]);
                }
                str.append("]");
            } else {
                str.append(data);
            }
        }
    }

    private static final class TypeInfo {
        public final Class<?> dataType;
        public final Template.Class<? extends TagHandle> handleClass;
        public final java.util.function.Function<Object, Object> constructor;
        public final java.util.function.Function<Object, Object> get_data;

        public TypeInfo(Class<?> dataType,
                        Template.Class<? extends TagHandle> handleClass,
                        java.util.function.Function<Object, Object> constructor,
                        java.util.function.Function<Object, Object> get_data)
        {
            this.dataType = dataType;
            this.handleClass = handleClass;
            this.constructor = constructor;
            this.get_data = get_data;
        }
    }

    private static class TypeInfoLookup {
        public final com.bergerkiller.bukkit.common.collections.ClassMap<TypeInfo> byType = new com.bergerkiller.bukkit.common.collections.ClassMap<TypeInfo>();
        public final TypeInfo toStringFallback;

        public TypeInfoLookup() {
            toStringFallback = new TypeInfo(
                    String.class, StringTagHandle.T,
                    data -> StringTagHandle.T.create.raw.invoke(com.bergerkiller.bukkit.common.conversion.Conversion.toString.convert(data, "")),
                    java.util.function.Function.identity()
            );

            registerTypeInfo(String.class, StringTagHandle.T, StringTagHandle.T.create.raw::invoke, StringTagHandle.T.getData::invoke);
            registerTypeInfo(byte.class, ByteTagHandle.T, ByteTagHandle.T.create.raw::invoke, ByteTagHandle.T.getByteData::invoke);
            registerTypeInfo(short.class, ShortTagHandle.T, ShortTagHandle.T.create.raw::invoke, ShortTagHandle.T.getShortData::invoke);
            registerTypeInfo(int.class, IntTagHandle.T, IntTagHandle.T.create.raw::invoke, IntTagHandle.T.getIntegerData::invoke);
            registerTypeInfo(long.class, LongTagHandle.T, LongTagHandle.T.create.raw::invoke, LongTagHandle.T.getLongData::invoke);
            registerTypeInfo(float.class, FloatTagHandle.T, FloatTagHandle.T.create.raw::invoke, FloatTagHandle.T.getFloatData::invoke);
            registerTypeInfo(double.class, DoubleTagHandle.T, DoubleTagHandle.T.create.raw::invoke, DoubleTagHandle.T.getDoubleData::invoke);
            registerTypeInfo(byte[].class, ByteArrayTagHandle.T, ByteArrayTagHandle.T.create.raw::invoke, ByteArrayTagHandle.T.getData::invoke);
            registerTypeInfo(int[].class, IntArrayTagHandle.T, IntArrayTagHandle.T.create.raw::invoke, IntArrayTagHandle.T.getData::invoke);

            if (LongArrayTagHandle.T.isAvailable()) {
                registerTypeInfo(long[].class, LongArrayTagHandle.T, LongArrayTagHandle.T.create.raw::invoke, LongArrayTagHandle.T.getData::invoke);
            }

            registerTypeInfo(java.util.Collection.class, ListTagHandle.T, ListTagHandle.T.create.raw::invoke, ListTagHandle.T.data.raw::get);
            registerTypeInfo(java.util.Map.class, CompoundTagHandle.T, CompoundTagHandle.T.create.raw::invoke, CompoundTagHandle.T.data.raw::get);
        }

        private void registerTypeInfo(
                Class<?> dataType,
                Template.Class<? extends TagHandle> handleClass,
                java.util.function.Function<Object, Object> constructor,
                java.util.function.Function<Object, Object> get_data)
        {
            TypeInfo data_typeInfo = new TypeInfo(dataType, handleClass, constructor, java.util.function.Function.identity());
            byType.put(dataType, data_typeInfo);
            Class<?> boxedDataType = com.bergerkiller.mountiplex.reflection.util.BoxedType.getBoxedType(dataType);
            if (boxedDataType != null) {
                byType.put(boxedDataType, data_typeInfo);
            }

            byType.put(handleClass.getType(), new TypeInfo(dataType, handleClass,
                    java.util.function.Function.identity(), get_data));

            byType.put(handleClass.getHandleType(), new TypeInfo(dataType, handleClass,
                    handle -> ((Template.Handle) handle).getRaw(),
                    handle -> get_data.apply(((Template.Handle) handle).getRaw())));

            handleClass.createHandle(null, true);
        }
    }

    private static TypeInfoLookup lookup = null;

    private static TypeInfoLookup lookup() {
        TypeInfoLookup lookup;
        if ((lookup = TagHandle.lookup) != null) {
            return lookup;
        }

        synchronized (TagHandle.class) {
            if ((lookup = TagHandle.lookup) != null) {
                return lookup;
            }

            lookup = new TypeInfoLookup();
            TagHandle.lookup = lookup;
            return lookup;
        }
    }

    private static TypeInfo findTypeInfo(Object data) {
        if (data == null) {
            throw new IllegalArgumentException("Can not find tag type information for null data");
        }

        TypeInfoLookup lookup = lookup();
        TypeInfo info = lookup.byType.get(data.getClass());
        if (info != null) {
            return info;
        }
        if (data instanceof com.bergerkiller.bukkit.common.nbt.CommonTag) {
            final TypeInfo handle_info = findTypeInfo(((com.bergerkiller.bukkit.common.nbt.CommonTag) data).getRawHandle());
            return new TypeInfo(
                handle_info.dataType, handle_info.handleClass,
                tag -> ((com.bergerkiller.bukkit.common.nbt.CommonTag) data).getRawHandle(),
                tag -> handle_info.get_data.apply(((com.bergerkiller.bukkit.common.nbt.CommonTag) data).getRawHandle())
            );
        }
        return lookup.toStringFallback;
    }

    public static boolean isDataSupportedNatively(Object data) {
        TypeInfoLookup lookup = lookup();
        return lookup.byType.get(data) != null || data instanceof com.bergerkiller.bukkit.common.nbt.CommonTag;
    }

    public static Object getDataForHandle(Object handle) {
        return findTypeInfo(handle).get_data.apply(handle);
    }

    public static Object createRawHandleForData(Object data) {
        return findTypeInfo(data).constructor.apply(data);
    }

    public static TagHandle createHandleForData(Object data) {
        TypeInfo info = findTypeInfo(data);
        return info.handleClass.createHandle(info.constructor.apply(data));
    }

    public static java.util.function.Consumer<String> createPartialErrorLogger(Object nbtBase) {
        return (s) -> {
            String nbtToStr = (nbtBase == null) ? "[null]" : nbtBase.toString();
            com.bergerkiller.bukkit.common.Logging.LOGGER.severe(
                    "Failed to read (" + nbtToStr + "): " + s);
        };
    }
    /**
     * Stores class members for <b>net.minecraft.nbt.Tag</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class TagClass extends Template.Class<TagHandle> {
        public final Template.StaticMethod<TagHandle> createHandle = new Template.StaticMethod<TagHandle>();

        public final Template.Method<Byte> getTypeId = new Template.Method<Byte>();
        public final Template.Method<Object> raw_clone = new Template.Method<Object>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.StringTag</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.StringTag")
    public abstract static class StringTagHandle extends TagHandle {
        /** @see StringTagClass */
        public static final StringTagClass T = Template.Class.create(StringTagClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static StringTagHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static StringTagHandle create(String data) {
            return T.create.invoke(data);
        }

        public abstract String getData();
        public TagHandle.StringTagHandle clone() {
            return com.bergerkiller.bukkit.common.internal.CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : createHandle(raw_clone());
        }
        /**
         * Stores class members for <b>net.minecraft.nbt.StringTag</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class StringTagClass extends Template.Class<StringTagHandle> {
            public final Template.StaticMethod.Converted<StringTagHandle> create = new Template.StaticMethod.Converted<StringTagHandle>();

            public final Template.Method<String> getData = new Template.Method<String>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.ByteTag</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.ByteTag")
    public abstract static class ByteTagHandle extends TagHandle {
        /** @see ByteTagClass */
        public static final ByteTagClass T = Template.Class.create(ByteTagClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static ByteTagHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static ByteTagHandle create(byte data) {
            return T.create.invoke(data);
        }

        public abstract byte getByteData();
        public TagHandle.ByteTagHandle clone() {
            return com.bergerkiller.bukkit.common.internal.CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : createHandle(raw_clone());
        }
        public Byte getData() { return Byte.valueOf(getByteData()); }
        /**
         * Stores class members for <b>net.minecraft.nbt.ByteTag</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class ByteTagClass extends Template.Class<ByteTagHandle> {
            public final Template.StaticMethod.Converted<ByteTagHandle> create = new Template.StaticMethod.Converted<ByteTagHandle>();

            public final Template.Method<Byte> getByteData = new Template.Method<Byte>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.ShortTag</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.ShortTag")
    public abstract static class ShortTagHandle extends TagHandle {
        /** @see ShortTagClass */
        public static final ShortTagClass T = Template.Class.create(ShortTagClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static ShortTagHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static ShortTagHandle create(short data) {
            return T.create.invoke(data);
        }

        public abstract short getShortData();
        public static Object createRaw(Object data) { return T.create.raw.invoke(data); }
        public TagHandle.ShortTagHandle clone() {
            return com.bergerkiller.bukkit.common.internal.CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : createHandle(raw_clone());
        }
        public Short getData() { return Short.valueOf(getShortData()); }
        /**
         * Stores class members for <b>net.minecraft.nbt.ShortTag</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class ShortTagClass extends Template.Class<ShortTagHandle> {
            public final Template.StaticMethod.Converted<ShortTagHandle> create = new Template.StaticMethod.Converted<ShortTagHandle>();

            public final Template.Method<Short> getShortData = new Template.Method<Short>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.IntTag</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.IntTag")
    public abstract static class IntTagHandle extends TagHandle {
        /** @see IntTagClass */
        public static final IntTagClass T = Template.Class.create(IntTagClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static IntTagHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static IntTagHandle create(int data) {
            return T.create.invoke(data);
        }

        public abstract int getIntegerData();
        public TagHandle.IntTagHandle clone() {
            return com.bergerkiller.bukkit.common.internal.CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : createHandle(raw_clone());
        }
        public Integer getData() { return Integer.valueOf(getIntegerData()); }
        /**
         * Stores class members for <b>net.minecraft.nbt.IntTag</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class IntTagClass extends Template.Class<IntTagHandle> {
            public final Template.StaticMethod.Converted<IntTagHandle> create = new Template.StaticMethod.Converted<IntTagHandle>();

            public final Template.Method<Integer> getIntegerData = new Template.Method<Integer>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.LongTag</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.LongTag")
    public abstract static class LongTagHandle extends TagHandle {
        /** @see LongTagClass */
        public static final LongTagClass T = Template.Class.create(LongTagClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static LongTagHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static LongTagHandle create(long data) {
            return T.create.invoke(data);
        }

        public abstract long getLongData();
        public TagHandle.LongTagHandle clone() {
            return com.bergerkiller.bukkit.common.internal.CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : createHandle(raw_clone());
        }
        public Long getData() { return Long.valueOf(getLongData()); }
        /**
         * Stores class members for <b>net.minecraft.nbt.LongTag</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class LongTagClass extends Template.Class<LongTagHandle> {
            public final Template.StaticMethod.Converted<LongTagHandle> create = new Template.StaticMethod.Converted<LongTagHandle>();

            public final Template.Method<Long> getLongData = new Template.Method<Long>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.FloatTag</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.FloatTag")
    public abstract static class FloatTagHandle extends TagHandle {
        /** @see FloatTagClass */
        public static final FloatTagClass T = Template.Class.create(FloatTagClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static FloatTagHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static FloatTagHandle create(float data) {
            return T.create.invoke(data);
        }

        public abstract float getFloatData();
        public TagHandle.FloatTagHandle clone() {
            return com.bergerkiller.bukkit.common.internal.CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : createHandle(raw_clone());
        }
        public Float getData() { return Float.valueOf(getFloatData()); }
        /**
         * Stores class members for <b>net.minecraft.nbt.FloatTag</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class FloatTagClass extends Template.Class<FloatTagHandle> {
            public final Template.StaticMethod.Converted<FloatTagHandle> create = new Template.StaticMethod.Converted<FloatTagHandle>();

            public final Template.Method<Float> getFloatData = new Template.Method<Float>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.DoubleTag</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.DoubleTag")
    public abstract static class DoubleTagHandle extends TagHandle {
        /** @see DoubleTagClass */
        public static final DoubleTagClass T = Template.Class.create(DoubleTagClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static DoubleTagHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static DoubleTagHandle create(double data) {
            return T.create.invoke(data);
        }

        public abstract double getDoubleData();
        public TagHandle.DoubleTagHandle clone() {
            return com.bergerkiller.bukkit.common.internal.CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : createHandle(raw_clone());
        }
        public Double getData() { return Double.valueOf(getDoubleData()); }
        /**
         * Stores class members for <b>net.minecraft.nbt.DoubleTag</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class DoubleTagClass extends Template.Class<DoubleTagHandle> {
            public final Template.StaticMethod.Converted<DoubleTagHandle> create = new Template.StaticMethod.Converted<DoubleTagHandle>();

            public final Template.Method<Double> getDoubleData = new Template.Method<Double>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.ByteArrayTag</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.ByteArrayTag")
    public abstract static class ByteArrayTagHandle extends TagHandle {
        /** @see ByteArrayTagClass */
        public static final ByteArrayTagClass T = Template.Class.create(ByteArrayTagClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static ByteArrayTagHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static ByteArrayTagHandle create(byte[] data) {
            return T.create.invoke(data);
        }

        public abstract byte[] getData();
        /**
         * Stores class members for <b>net.minecraft.nbt.ByteArrayTag</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class ByteArrayTagClass extends Template.Class<ByteArrayTagHandle> {
            public final Template.StaticMethod.Converted<ByteArrayTagHandle> create = new Template.StaticMethod.Converted<ByteArrayTagHandle>();

            public final Template.Method<byte[]> getData = new Template.Method<byte[]>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.IntArrayTag</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.IntArrayTag")
    public abstract static class IntArrayTagHandle extends TagHandle {
        /** @see IntArrayTagClass */
        public static final IntArrayTagClass T = Template.Class.create(IntArrayTagClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static IntArrayTagHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static IntArrayTagHandle create(int[] data) {
            return T.create.invoke(data);
        }

        public abstract int[] getData();
        /**
         * Stores class members for <b>net.minecraft.nbt.IntArrayTag</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class IntArrayTagClass extends Template.Class<IntArrayTagHandle> {
            public final Template.StaticMethod.Converted<IntArrayTagHandle> create = new Template.StaticMethod.Converted<IntArrayTagHandle>();

            public final Template.Method<int[]> getData = new Template.Method<int[]>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.LongArrayTag</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.Optional
    @Template.InstanceType("net.minecraft.nbt.LongArrayTag")
    public abstract static class LongArrayTagHandle extends TagHandle {
        /** @see LongArrayTagClass */
        public static final LongArrayTagClass T = Template.Class.create(LongArrayTagClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static LongArrayTagHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static LongArrayTagHandle create(long[] data) {
            return T.create.invoke(data);
        }

        public abstract long[] getData();
        /**
         * Stores class members for <b>net.minecraft.nbt.LongArrayTag</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class LongArrayTagClass extends Template.Class<LongArrayTagHandle> {
            public final Template.StaticMethod.Converted<LongArrayTagHandle> create = new Template.StaticMethod.Converted<LongArrayTagHandle>();

            public final Template.Method<long[]> getData = new Template.Method<long[]>();

        }

    }

}

