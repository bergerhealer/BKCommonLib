package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.SafeDirectMethod;
import com.bergerkiller.mountiplex.reflection.SafeField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

public class NMSNBT {
    
    /**
     * Creates an NBT Tag handle to store the data specified in<br>
     * All primitive types, including byte[] and int[], and list/maps are
     * supported
     *
     * @param data to store in this handle initially
     * @return new handle
     */
    public static Object createHandle(Object data) {
        return Type.find(data).createHandle(data);
    }

    /**
     * Obtains the raw data from an NBT Tag handle. NBTTagList and
     * NBTTagCompound return a List and Map of NBT Tags respectively. If a null
     * handle is specified, null is returned to indicate no data.
     *
     * @param nbtTagHandle to get the value of
     * @return the NBTTag data
     */
    public static Object getData(Object nbtTagHandle) {
        if (nbtTagHandle == null) {
            return null;
        }
        return Type.find(nbtTagHandle).getData(nbtTagHandle);
    }

    /**
     * Gets the type Id of the tag used to identify it
     *
     * @param nbtTagHandle to read from
     * @return tag type id
     */
    public static byte getTypeId(Object nbtTagHandle) {
        if (nbtTagHandle == null) {
            return (byte) 0;
        }
        return NMSNBT.Base.getTypeId.invoke(nbtTagHandle).byteValue();
    }

    public static class Type {
        private static final ClassMap<Type> dataTags = new ClassMap<Type>();
        private static final Map<Class<?>, Type> nbtTags = new HashMap<Class<?>, Type>();
        public final Class<?> nbtType;
        public final Constructor<?> constructor;
        public final Field dataField;
        public final Class<?> dataType;
        public final String dataName;
        
        private Type(Class<?> nbtClass) throws Throwable {
            this.nbtType = nbtClass;
            if (NMSNBT.List.T.isType(nbtClass)) {
                this.dataField = nbtClass.getDeclaredField(Common.SERVER.getFieldName(nbtClass, "list"));
                this.dataType = java.util.List.class;
                this.constructor = nbtClass.getDeclaredConstructor();
                this.dataName = "TagList";
            } else if (NMSNBT.Compound.T.isType(nbtClass)) {
                this.dataField = nbtClass.getDeclaredField(Common.SERVER.getFieldName(nbtClass, "map"));
                this.dataType = java.util.Map.class;
                this.constructor = nbtClass.getDeclaredConstructor();
                this.dataName = "TagCompound";
            } else {
                this.dataField = nbtClass.getDeclaredField(Common.SERVER.getFieldName(nbtClass, "data"));
                final Class<?> dataType = this.dataField.getType();
                this.constructor = nbtClass.getDeclaredConstructor(dataType);
                // Box it
                final Class<?> boxed = LogicUtil.getBoxedType(dataType);
                if (boxed == null) {
                    this.dataType = dataType;
                } else {
                    this.dataType = boxed;
                }
                this.dataName = this.dataField.getType().getSimpleName();
            }
            this.dataField.setAccessible(true);
        }
        
        static {
            java.util.List<String> names = Arrays.asList("NBTTagByte", "NBTTagShort", "NBTTagInt",
                    "NBTTagLong", "NBTTagFloat", "NBTTagDouble", "NBTTagString", "NBTTagByteArray",
                    "NBTTagIntArray", "NBTTagList", "NBTTagCompound");
            
            for (String name : names) {
                try {
                    Class<?> nbtType = CommonUtil.getNMSClass(name);
                    Type dataTag = new Type(nbtType);
                    Class<?> unboxed = LogicUtil.getUnboxedType(dataTag.dataType);
                    if (unboxed != null) {
                        dataTags.put(unboxed, dataTag);
                    }
                    Class<?> boxed = LogicUtil.getBoxedType(dataTag.dataType);
                    if (boxed != null) {
                        dataTags.put(boxed, dataTag);
                    }
                    dataTags.put(dataTag.dataType, dataTag);
                    nbtTags.put(dataTag.nbtType, dataTag);
                } catch (Throwable e) {
                    Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to load NBT Tag " + name + ":", e);
                }
            }
        }

        /**
         * Finds the NBT Type used to store data
         * 
         * @param data to store
         * @return NBT Type fit for storing the data
         */
        public static Type find(Object data) {
            if (data == null) {
                throw new RuntimeException("Can not find tag information of null data");
            } else if (data instanceof CommonTag) {
                return ((CommonTag) data).getType();
            } else if (NMSNBT.Base.T.isInstance(data)) {
                // Get from handle
                Type info = nbtTags.get(data.getClass());
                if (info == null) {
                    throw new RuntimeException("Unsupported NBTTag Handle: " + data.getClass().getName());
                }
                return info;
            } else {
                // Get from data
                Type info = dataTags.get(data);
                if (info == null) {
                    throw new RuntimeException("Unknown tag data type: " + data.getClass().getName());
                }
                return info;
            }
        }

        private void validateHandle(Object handle) {
            if (handle == null) {
                throw new IllegalArgumentException("Handle is null");
            } else if (!nbtType.isAssignableFrom(handle.getClass())) {
                throw new IllegalArgumentException("Handle - NBT Type mismatch");
            }
        }

        public void setData(Object handle, Object data) {
            validateHandle(handle);
            if (data == null) {
                throw new IllegalArgumentException("Can not set a handle data to null");
            } else if (!dataType.isAssignableFrom(data.getClass())) {
                throw new IllegalArgumentException("NBTInfo not suitable for specified data");
            }
            try {
                dataField.set(handle, data);
            } catch (Throwable t) {
                throw new RuntimeException("Unable to write data to handle", t);
            }
        }

        public Object getData(Object handle) {
            validateHandle(handle);
            try {
                return dataField.get(handle);
            } catch (Throwable t) {
                throw new RuntimeException("Unable to read data from handle", t);
            }
        }

        @SuppressWarnings("unchecked")
        public String toString(Object handle, int indent) {
            final String indentTxt = StringUtil.getFilledString("  ", indent);
            if (!NMSNBT.Base.T.isInstance(handle)) {
                return indentTxt + "UNKNOWN(\"\"): null";
            }
            StringBuilder text = new StringBuilder(100);

            // Data type and name header
            text.append(dataName).append(": ");

            // Tag data information
            if (NMSNBT.List.T.isInstance(handle)) {
                java.util.List<Object> elements = (java.util.List<Object>) getData(handle);

                // In case of list: show all values on several lines
                text.append(elements.size()).append(" entries [");
                for (Object handleElem : elements) {
                    text.append('\n').append(indentTxt).append("  ").append(find(handleElem).toString(handleElem, indent + 1));
                }
                return text.append('\n').append(indentTxt).append("]").toString();
            } else if (NMSNBT.Compound.T.isInstance(handle)) {
                // In case of compound: show all key: value pairs on several lines
                // Make sure to sort the map by key name first for easier display!
                TreeMap<String, Object> elements = new TreeMap<String, Object>((Map<String, Object>) getData(handle));
                text.append(elements.size()).append(" entries {");
                for (Entry<String, Object> handleElem : elements.entrySet()) {
                    text.append('\n').append(indentTxt).append("  ");
                    text.append(handleElem.getKey()).append(" = ");

                    Object value = handleElem.getValue();
                    text.append(find(value).toString(value, indent + 1));
                }
                return text.append('\n').append(indentTxt).append("}").toString();
            } else {
                // Simple values
                return text.append(getData(handle)).toString();
            }
        }

        @SuppressWarnings("unchecked")
        public Object createHandle(Object data) {
            if (data == null) {
                throw new RuntimeException("Can not create a tag for null data");
            }
            if (nbtType.isAssignableFrom(data.getClass())) {
                return data;
            }
            if (!dataType.isAssignableFrom(data.getClass())) {
                throw new RuntimeException("Can not store " + data.getClass().getName() + " in tag " + dataType.getSimpleName());
            }
            // Create a new handle from data
            try {
                final Object handle;
                if (NMSNBT.List.T.isType(nbtType)) {
                    // Create a new list of valid NBT handles
                    java.util.List<Object> oldData = (java.util.List<Object>) data;
                    ArrayList<Object> newData = new ArrayList<Object>(oldData.size());
                    byte type = 0;
                    for (Object element : oldData) {
                        Object base;
                        if (NMSNBT.Base.T.isInstance(element)) {
                            base = element;
                        } else if (element instanceof CommonTag) {
                            base = ((CommonTag) element).getHandle();
                        } else {
                            base = createHandle(element);
                        }
                        type = NMSNBT.getTypeId(base);
                        newData.add(base);
                    }
                    // Assign this data to a new valid NBT Tag List
                    handle = constructor.newInstance();
                    NMSNBT.List.type.set(handle, type);
                    dataField.set(handle, newData);
                } else if (NMSNBT.Compound.T.isType(nbtType)) {
                    // Fix up the map data
                    Map<Object, Object> oldData = (Map<Object, Object>) data;
                    Map<String, Object> newData = new HashMap<String, Object>(oldData.size());
                    for (Entry<Object, Object> entry : oldData.entrySet()) {
                        Object base;
                        final String key = entry.getKey().toString();
                        if (NMSNBT.Base.T.isInstance(entry.getValue())) {
                            base = entry.getValue();
                        } else if (entry.getValue() instanceof CommonTag) {
                            base = ((CommonTag) entry.getValue()).getHandle();
                        } else {
                            base = createHandle(entry.getValue());
                        }
                        newData.put(key, base);
                    }
                    // Assign this data to a new valid NBT Tag Compound
                    handle = constructor.newInstance();
                    dataField.set(handle, newData);
                } else {
                    handle = constructor.newInstance(data);
                }
                return handle;
            } catch (Throwable t) {
                throw new RuntimeException("Failed to create a new handle", t);
            }
        }
    }

    public static class Base {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("NBTBase");
        public static final MethodAccessor<Byte> getTypeId = T.selectMethod("public abstract byte getTypeId()");
        public static final MethodAccessor<Object> clone = T.selectMethod("public abstract NBTBase clone()");
    }

    public static class List extends Base {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("NBTTagList");
        public static final FieldAccessor<java.util.List<Object>> list = T.selectField("private List<NBTBase> list");
        public static final FieldAccessor<Byte> type = T.selectField("private byte type");
        public static final MethodAccessor<Void> add = T.selectMethod("public void add(NBTBase value)");
        public static final MethodAccessor<Integer> size = T.selectMethod("public int size()");
        public static final MethodAccessor<Object> get = T.selectMethod("public NBTBase h(int paramInt)");
    }

    public static class Compound extends Base {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("NBTTagCompound");
        public static final FieldAccessor<Map<String, ?>> map = T.selectField("private final Map<String, NBTBase> map");
        public static final MethodAccessor<Integer> size = T.selectMethod("public int d()");
        public static final MethodAccessor<Set<String>> getKeys = T.selectMethod("public Set<String> c()");
        public static final MethodAccessor<Collection<?>> getValues = new SafeDirectMethod<Collection<?>>() {
            @Override
            public Collection<?> invoke(Object instance, Object... args) {
                return map.get(instance).values();
            }
        };
        public static final MethodAccessor<Void> remove = T.selectMethod("public void remove(String key)");
        public static final MethodAccessor<Void> set = T.selectMethod("public void set(String key, NBTBase value)");
        public static final MethodAccessor<Object> get = T.selectMethod("public NBTBase get(String key)");
        public static final MethodAccessor<Boolean> contains = T.selectMethod("public boolean hasKey(String key)");
        public static final MethodAccessor<Boolean> isEmpty = T.selectMethod("public boolean isEmpty()");
    }

    public static class StreamTools {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("NBTCompressedStreamTools")
                .addImport("java.io.*");

        public static class Uncompressed {
            public static final MethodAccessor<Object> readTag = T.selectMethod("private static NBTBase a(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter)");
            public static final MethodAccessor<Void> writeTag = T.selectMethod("private static void a(NBTBase nbtbase, DataOutput dataoutput)");
            public static final MethodAccessor<Object> readTagCompound = T.selectMethod("public static NBTTagCompound a(DataInputStream datainputstream)");
            public static final MethodAccessor<Void> writeTagCompound = T.selectMethod("public static void a(NBTTagCompound nbttagcompound, DataOutput dataoutput)");

            private static final Class<?> readLimiter_t = CommonUtil.getNMSClass("NBTReadLimiter");
            private static final SafeField<?> inf_lim = new SafeField<Object>(readLimiter_t, "a", readLimiter_t);
            public static Object getNoReadLimiter() {
                return inf_lim.get(null);
            }
        }

        public static class Compressed {
            public static final MethodAccessor<Object> readTagCompound = T.selectMethod("public static NBTTagCompound a(InputStream inputstream)");
            public static final MethodAccessor<Void> writeTagCompound = T.selectMethod("public static void a(NBTTagCompound nbttagcompound, OutputStream outputstream)");
        }
    }
}
