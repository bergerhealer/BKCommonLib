package com.bergerkiller.generated.net.minecraft.nbt;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.nbt.NBTBase</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.nbt.NBTBase")
public abstract class NBTBaseHandle extends Template.Handle {
    /** @See {@link NBTBaseClass} */
    public static final NBTBaseClass T = Template.Class.create(NBTBaseClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static NBTBaseHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract byte getTypeId();
    public abstract Object raw_clone();

    public com.bergerkiller.bukkit.common.nbt.CommonTag toCommonTag() {
        return new com.bergerkiller.bukkit.common.nbt.CommonTag(this);
    }
    public abstract NBTBaseHandle clone();
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
            str.append(": ").append(data);
        }
    }

    private static final class TypeInfo {
        public final Class<?> dataType;
        public final Template.Class<? extends NBTBaseHandle> handleClass;
        public final java.util.function.Function<Object, Object> constructor;
        public final java.util.function.Function<Object, Object> get_data;

        public TypeInfo(Class<?> dataType,
                        Template.Class<? extends NBTBaseHandle> handleClass,
                        java.util.function.Function<Object, Object> constructor,
                        java.util.function.Function<Object, Object> get_data)
        {
            this.dataType = dataType;
            this.handleClass = handleClass;
            this.constructor = constructor;
            this.get_data = get_data;
        }
    }

    private static final com.bergerkiller.bukkit.common.collections.ClassMap<TypeInfo> typeInfoLookup = new com.bergerkiller.bukkit.common.collections.ClassMap<TypeInfo>();
    private static final TypeInfo toStringFallbackTypeInfo = new TypeInfo(
        String.class, NBTTagStringHandle.T,
        data -> NBTTagStringHandle.T.create.raw.invoke(com.bergerkiller.bukkit.common.conversion.Conversion.toString.convert(data, "")),
        java.util.function.Function.identity()
    );

    protected static void registerTypeInfo(Class<?> dataType,
                                         Template.Class<? extends NBTBaseHandle> handleClass,
                                         java.util.function.Function<Object, Object> constructor,
                                         java.util.function.Function<Object, Object> get_data)
    {
        TypeInfo data_typeInfo = new TypeInfo(dataType, handleClass, constructor, java.util.function.Function.identity());
        typeInfoLookup.put(dataType, data_typeInfo);
        Class<?> boxedDataType = com.bergerkiller.mountiplex.reflection.util.BoxedType.getBoxedType(dataType);
        if (boxedDataType != null) {
            typeInfoLookup.put(boxedDataType, data_typeInfo);
        }

        typeInfoLookup.put(handleClass.getType(), new TypeInfo(dataType, handleClass,
            java.util.function.Function.identity(), get_data));

        typeInfoLookup.put(handleClass.getHandleType(), new TypeInfo(dataType, handleClass,
            handle -> ((Template.Handle) handle).getRaw(),
            handle -> get_data.apply(((Template.Handle) handle).getRaw())));

        handleClass.createHandle(null, true);
    }

    private static TypeInfo findTypeInfo(Object data) {
        if (data == null) {
            throw new IllegalArgumentException("Can not find tag type information for null data");
        }

        TypeInfo info = typeInfoLookup.get(data.getClass());
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
        return toStringFallbackTypeInfo;
    }

    public static boolean isDataSupportedNatively(Object data) {
        return typeInfoLookup.get(data) != null || data instanceof com.bergerkiller.bukkit.common.nbt.CommonTag;
    }

    public static Object getDataForHandle(Object handle) {
        return findTypeInfo(handle).get_data.apply(handle);
    }

    public static Object createRawHandleForData(Object data) {
        return findTypeInfo(data).constructor.apply(data);
    }

    public static NBTBaseHandle createHandleForData(Object data) {
        TypeInfo info = findTypeInfo(data);
        return info.handleClass.createHandle(info.constructor.apply(data));
    }

    static {
        try {
            Class.forName(NBTTagStringHandle.class.getName());
            Class.forName(NBTTagByteHandle.class.getName());
            Class.forName(NBTTagShortHandle.class.getName());
            Class.forName(NBTTagIntHandle.class.getName());
            Class.forName(NBTTagLongHandle.class.getName());
            Class.forName(NBTTagFloatHandle.class.getName());
            Class.forName(NBTTagDoubleHandle.class.getName());
            Class.forName(NBTTagByteArrayHandle.class.getName());
            Class.forName(NBTTagIntArrayHandle.class.getName());
            Class.forName(NBTTagLongArrayHandle.class.getName());
            Class.forName(NBTTagListHandle.class.getName());
            Class.forName(NBTTagCompoundHandle.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * Stores class members for <b>net.minecraft.nbt.NBTBase</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class NBTBaseClass extends Template.Class<NBTBaseHandle> {
        public final Template.StaticMethod<NBTBaseHandle> createHandle = new Template.StaticMethod<NBTBaseHandle>();

        public final Template.Method<Byte> getTypeId = new Template.Method<Byte>();
        public final Template.Method<Object> raw_clone = new Template.Method<Object>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.NBTTagString</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.NBTTagString")
    public abstract static class NBTTagStringHandle extends NBTBaseHandle {
        /** @See {@link NBTTagStringClass} */
        public static final NBTTagStringClass T = Template.Class.create(NBTTagStringClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static NBTTagStringHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static NBTTagStringHandle create(String data) {
            return T.create.invoke(data);
        }

        public abstract String getData();

        static {
            registerTypeInfo(String.class, T, T.create.raw::invoke, T.getData::invoke);
        }

        public NBTBaseHandle.NBTTagStringHandle clone() {
            return com.bergerkiller.bukkit.common.internal.CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : createHandle(raw_clone());
        }
        /**
         * Stores class members for <b>net.minecraft.nbt.NBTTagString</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class NBTTagStringClass extends Template.Class<NBTTagStringHandle> {
            public final Template.StaticMethod.Converted<NBTTagStringHandle> create = new Template.StaticMethod.Converted<NBTTagStringHandle>();

            public final Template.Method<String> getData = new Template.Method<String>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.NBTTagByte</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.NBTTagByte")
    public abstract static class NBTTagByteHandle extends NBTBaseHandle {
        /** @See {@link NBTTagByteClass} */
        public static final NBTTagByteClass T = Template.Class.create(NBTTagByteClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static NBTTagByteHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static NBTTagByteHandle create(byte data) {
            return T.create.invoke(data);
        }

        public abstract byte getByteData();

        static {
            registerTypeInfo(byte.class, T, T.create.raw::invoke, T.getByteData::invoke);
        }

        public NBTBaseHandle.NBTTagByteHandle clone() {
            return com.bergerkiller.bukkit.common.internal.CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : createHandle(raw_clone());
        }
        public Byte getData() { return Byte.valueOf(getByteData()); }
        /**
         * Stores class members for <b>net.minecraft.nbt.NBTTagByte</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class NBTTagByteClass extends Template.Class<NBTTagByteHandle> {
            public final Template.StaticMethod.Converted<NBTTagByteHandle> create = new Template.StaticMethod.Converted<NBTTagByteHandle>();

            public final Template.Method<Byte> getByteData = new Template.Method<Byte>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.NBTTagShort</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.NBTTagShort")
    public abstract static class NBTTagShortHandle extends NBTBaseHandle {
        /** @See {@link NBTTagShortClass} */
        public static final NBTTagShortClass T = Template.Class.create(NBTTagShortClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static NBTTagShortHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static NBTTagShortHandle create(short data) {
            return T.create.invoke(data);
        }

        public abstract short getShortData();

        static {
            registerTypeInfo(short.class, T, T.create.raw::invoke, T.getShortData::invoke);
        }

        public static Object createRaw(Object data) { return T.create.raw.invoke(data); }
        public NBTBaseHandle.NBTTagShortHandle clone() {
            return com.bergerkiller.bukkit.common.internal.CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : createHandle(raw_clone());
        }
        public Short getData() { return Short.valueOf(getShortData()); }
        /**
         * Stores class members for <b>net.minecraft.nbt.NBTTagShort</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class NBTTagShortClass extends Template.Class<NBTTagShortHandle> {
            public final Template.StaticMethod.Converted<NBTTagShortHandle> create = new Template.StaticMethod.Converted<NBTTagShortHandle>();

            public final Template.Method<Short> getShortData = new Template.Method<Short>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.NBTTagInt</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.NBTTagInt")
    public abstract static class NBTTagIntHandle extends NBTBaseHandle {
        /** @See {@link NBTTagIntClass} */
        public static final NBTTagIntClass T = Template.Class.create(NBTTagIntClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static NBTTagIntHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static NBTTagIntHandle create(int data) {
            return T.create.invoke(data);
        }

        public abstract int getIntegerData();

        static {
            registerTypeInfo(int.class, T, T.create.raw::invoke, T.getIntegerData::invoke);
        }

        public NBTBaseHandle.NBTTagIntHandle clone() {
            return com.bergerkiller.bukkit.common.internal.CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : createHandle(raw_clone());
        }
        public Integer getData() { return Integer.valueOf(getIntegerData()); }
        /**
         * Stores class members for <b>net.minecraft.nbt.NBTTagInt</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class NBTTagIntClass extends Template.Class<NBTTagIntHandle> {
            public final Template.StaticMethod.Converted<NBTTagIntHandle> create = new Template.StaticMethod.Converted<NBTTagIntHandle>();

            public final Template.Method<Integer> getIntegerData = new Template.Method<Integer>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.NBTTagLong</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.NBTTagLong")
    public abstract static class NBTTagLongHandle extends NBTBaseHandle {
        /** @See {@link NBTTagLongClass} */
        public static final NBTTagLongClass T = Template.Class.create(NBTTagLongClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static NBTTagLongHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static NBTTagLongHandle create(long data) {
            return T.create.invoke(data);
        }

        public abstract long getLongData();

        static {
            registerTypeInfo(long.class, T, T.create.raw::invoke, T.getLongData::invoke);
        }

        public NBTBaseHandle.NBTTagLongHandle clone() {
            return com.bergerkiller.bukkit.common.internal.CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : createHandle(raw_clone());
        }
        public Long getData() { return Long.valueOf(getLongData()); }
        /**
         * Stores class members for <b>net.minecraft.nbt.NBTTagLong</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class NBTTagLongClass extends Template.Class<NBTTagLongHandle> {
            public final Template.StaticMethod.Converted<NBTTagLongHandle> create = new Template.StaticMethod.Converted<NBTTagLongHandle>();

            public final Template.Method<Long> getLongData = new Template.Method<Long>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.NBTTagFloat</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.NBTTagFloat")
    public abstract static class NBTTagFloatHandle extends NBTBaseHandle {
        /** @See {@link NBTTagFloatClass} */
        public static final NBTTagFloatClass T = Template.Class.create(NBTTagFloatClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static NBTTagFloatHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static NBTTagFloatHandle create(float data) {
            return T.create.invoke(data);
        }

        public abstract float getFloatData();

        static {
            registerTypeInfo(float.class, T, T.create.raw::invoke, T.getFloatData::invoke);
        }

        public NBTBaseHandle.NBTTagFloatHandle clone() {
            return com.bergerkiller.bukkit.common.internal.CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : createHandle(raw_clone());
        }
        public Float getData() { return Float.valueOf(getFloatData()); }
        /**
         * Stores class members for <b>net.minecraft.nbt.NBTTagFloat</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class NBTTagFloatClass extends Template.Class<NBTTagFloatHandle> {
            public final Template.StaticMethod.Converted<NBTTagFloatHandle> create = new Template.StaticMethod.Converted<NBTTagFloatHandle>();

            public final Template.Method<Float> getFloatData = new Template.Method<Float>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.NBTTagDouble</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.NBTTagDouble")
    public abstract static class NBTTagDoubleHandle extends NBTBaseHandle {
        /** @See {@link NBTTagDoubleClass} */
        public static final NBTTagDoubleClass T = Template.Class.create(NBTTagDoubleClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static NBTTagDoubleHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static NBTTagDoubleHandle create(double data) {
            return T.create.invoke(data);
        }

        public abstract double getDoubleData();

        static {
            registerTypeInfo(double.class, T, T.create.raw::invoke, T.getDoubleData::invoke);
        }

        public NBTBaseHandle.NBTTagDoubleHandle clone() {
            return com.bergerkiller.bukkit.common.internal.CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : createHandle(raw_clone());
        }
        public Double getData() { return Double.valueOf(getDoubleData()); }
        /**
         * Stores class members for <b>net.minecraft.nbt.NBTTagDouble</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class NBTTagDoubleClass extends Template.Class<NBTTagDoubleHandle> {
            public final Template.StaticMethod.Converted<NBTTagDoubleHandle> create = new Template.StaticMethod.Converted<NBTTagDoubleHandle>();

            public final Template.Method<Double> getDoubleData = new Template.Method<Double>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.NBTTagByteArray</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.NBTTagByteArray")
    public abstract static class NBTTagByteArrayHandle extends NBTBaseHandle {
        /** @See {@link NBTTagByteArrayClass} */
        public static final NBTTagByteArrayClass T = Template.Class.create(NBTTagByteArrayClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static NBTTagByteArrayHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static NBTTagByteArrayHandle create(byte[] data) {
            return T.create.invoke(data);
        }

        public abstract byte[] getData();

        static {
            registerTypeInfo(byte[].class, T, T.create.raw::invoke, T.getData::invoke);
        }
        /**
         * Stores class members for <b>net.minecraft.nbt.NBTTagByteArray</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class NBTTagByteArrayClass extends Template.Class<NBTTagByteArrayHandle> {
            public final Template.StaticMethod.Converted<NBTTagByteArrayHandle> create = new Template.StaticMethod.Converted<NBTTagByteArrayHandle>();

            public final Template.Method<byte[]> getData = new Template.Method<byte[]>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.NBTTagIntArray</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.nbt.NBTTagIntArray")
    public abstract static class NBTTagIntArrayHandle extends NBTBaseHandle {
        /** @See {@link NBTTagIntArrayClass} */
        public static final NBTTagIntArrayClass T = Template.Class.create(NBTTagIntArrayClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static NBTTagIntArrayHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static NBTTagIntArrayHandle create(int[] data) {
            return T.create.invoke(data);
        }

        public abstract int[] getData();

        static {
            registerTypeInfo(int[].class, T, T.create.raw::invoke, T.getData::invoke);
        }
        /**
         * Stores class members for <b>net.minecraft.nbt.NBTTagIntArray</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class NBTTagIntArrayClass extends Template.Class<NBTTagIntArrayHandle> {
            public final Template.StaticMethod.Converted<NBTTagIntArrayHandle> create = new Template.StaticMethod.Converted<NBTTagIntArrayHandle>();

            public final Template.Method<int[]> getData = new Template.Method<int[]>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.nbt.NBTTagLongArray</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.Optional
    @Template.InstanceType("net.minecraft.nbt.NBTTagLongArray")
    public abstract static class NBTTagLongArrayHandle extends NBTBaseHandle {
        /** @See {@link NBTTagLongArrayClass} */
        public static final NBTTagLongArrayClass T = Template.Class.create(NBTTagLongArrayClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static NBTTagLongArrayHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static NBTTagLongArrayHandle create(long[] data) {
            return T.create.invoke(data);
        }

        public abstract long[] getData();

        static {
            if (T.isAvailable()) {
                registerTypeInfo(long[].class, T, T.create.raw::invoke, T.getData::invoke);
            }
        }
        /**
         * Stores class members for <b>net.minecraft.nbt.NBTTagLongArray</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class NBTTagLongArrayClass extends Template.Class<NBTTagLongArrayHandle> {
            public final Template.StaticMethod.Converted<NBTTagLongArrayHandle> create = new Template.StaticMethod.Converted<NBTTagLongArrayHandle>();

            public final Template.Method<long[]> getData = new Template.Method<long[]>();

        }

    }

}

