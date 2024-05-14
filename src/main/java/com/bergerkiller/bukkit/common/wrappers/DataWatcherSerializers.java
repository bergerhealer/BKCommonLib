package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.core.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.core.EnumDirectionHandle;
import com.bergerkiller.generated.net.minecraft.core.Vector3fHandle;
import com.bergerkiller.generated.net.minecraft.network.chat.IChatBaseComponentHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.IBlockDataHandle;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.OptionalInt;
import java.util.logging.Level;

/**
 * Keeps track of the internal DataWatcher serializers that can be used to store data at keys.
 * Also tracks the API external types for internal types. For example, that BlockPosition is
 * represented as IntVector3.
 */
class DataWatcherSerializers {
    // Converters used as part of API conversion
    public static final DuplexConverter<Object, OptionalInt> ENTITY_ID_TYPE_CONVERTER = new EntityIdTypeConverter();
    public static final DuplexConverter<Object, BoatWoodType> BOAT_WOOD_TYPE_CONVERTER = new BoatWoodTypeIdConverter();
    public static final DuplexConverter<Object, Integer> SLIME_SIZE_CONVERTER = new SlimeSizeByteConverter();

    // Cached registry information
    private static final HashMap<Object, InternalType> tokenRegistryRev = new HashMap<>();
    private static final HashMap<Class<?>, Object> tokenRegistry = new HashMap<>();
    private static final HashMap<Class<?>, Object> tokenRegistry_optional = new HashMap<>();
    private static final HashMap<Class<?>, Class<?>> typeMapping = new HashMap<>();

    static {
        // This MUST be done first, or we get bootstrap errors!
        CommonBootstrap.initServer();

        Class<?> registryClass = CommonUtil.getClass("net.minecraft.network.syncher.DataWatcherRegistry");
        Class<?> serializerClass = CommonUtil.getClass("net.minecraft.network.syncher.DataWatcherSerializer");
        if (registryClass != null && serializerClass != null) {
            // Since MC 1.9
            for (Field f : registryClass.getDeclaredFields()) {
                if (f.getType().equals(serializerClass) && Modifier.isStatic(f.getModifiers())) {
                    try {
                        if (!(Modifier.isPublic(f.getModifiers()))) {
                            f.setAccessible(true);
                        }
                        TypeDeclaration typeDec = TypeDeclaration.fromType(f.getGenericType());
                        if (typeDec.genericTypes.length == 1) {
                            TypeDeclaration dataType = typeDec.genericTypes[0];

                            // Sometimes google Optional is used to wrap null values. We aren't interested in that ourselves.
                            boolean isOptional = CommonNMS.isDWROptionalType(dataType.type) && (dataType.genericTypes.length == 1);
                            if (isOptional) {
                                dataType = dataType.genericTypes[0];
                            }

                            // Store in map for future use, mapped to the serializer instance
                            register(dataType.type, f.get(null), isOptional);
                        }
                    } catch (Throwable t) {
                        Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Error registering Datawatcher serializer " + f, t);
                    }
                }
            }

            // ChatText -> IChatbaseComponent
            typeMapping.put(ChatText.class, IChatBaseComponentHandle.T.getType());

            // Bukkit BlockFace -> nms EnumDirection
            typeMapping.put(BlockFace.class, EnumDirectionHandle.T.getType());
        } else {
            // Use our own kind of tokens on MC 1.8.8 and before
            register(Byte.class, 0);
            register(Short.class, 1);
            register(Integer.class, 2);
            register(Float.class, 3);
            register(String.class, 4);
            register(ItemStackHandle.T.getType(), 5);
            register(BlockPositionHandle.T.getType(), 6);
            register(Vector3fHandle.T.getType(), 7);

            // Booleans are stored as Byte
            typeMapping.put(Boolean.class, Byte.class);

            // IChatBaseComponent -> String
            typeMapping.put(IChatBaseComponentHandle.T.getType(), String.class);

            // ChatText -> String
            typeMapping.put(ChatText.class, String.class);

            // Bukkit BlockFace -> int (not really used anywhere)
            typeMapping.put(BlockFace.class, Integer.class);
        }

        // Add all type mappings to self
        for (Class<?> type : tokenRegistry.keySet()) {
            typeMapping.put(type, type);
        }
        for (Class<?> type : tokenRegistry_optional.keySet()) {
            typeMapping.put(type, type);
        }

        // Vector -> Vector3f
        typeMapping.put(Vector.class, Vector3fHandle.T.getType());
        // IntVector3 -> BlockPosition
        typeMapping.put(IntVector3.class, BlockPositionHandle.T.getType());
        // Bukkit ItemStack -> nms ItemStack
        typeMapping.put(ItemStack.class, ItemStackHandle.T.getType());
        // BlockData -> nms IBlockData
        typeMapping.put(BlockData.class, IBlockDataHandle.T.getType());
    }

    private static void register(Class<?> type, Object token) {
        register(type, token, false);
    }

    private static void register(Class<?> type, Object token, boolean optional) {
        register(new InternalType(token, type, optional));
    }

    private static void register(InternalType type) {
        if (type.optional) {
            tokenRegistry_optional.put(type.type, type.token);
        } else {
            tokenRegistry.put(type.type, type.token);
        }
        tokenRegistryRev.put(type.token, type);
    }

    public static Class<?> getInternalType(Class<?> exposedType) {
        return typeMapping.get(exposedType);
    }

    private static Object getSerializerToken(Class<?> type, boolean optional) {
        return (optional ? tokenRegistry_optional : tokenRegistry).get(type);
    }

    public static InternalType getInternalTypeFromToken(Object token) {
        return tokenRegistryRev.get(token);
    }

    @SuppressWarnings("unchecked")
    public static <T> ConvertedToken<T> getConvertedSerializerToken(Class<?> internalType, Class<T> externalType) {
        boolean optional = false;
        Object token = getSerializerToken(internalType, optional);
        if (token == null) {
            // Try optional
            optional = true;
            token = getSerializerToken(internalType, optional);
        }
        if (token == null) {
            throw new RuntimeException("No token found for internal type " + internalType.getName());
        }
        if (!CommonCapabilities.DATAWATCHER_OBJECTS && !(token instanceof Integer)) {
            throw new RuntimeException("Legacy type serializer tokens must be Integers!");
        }

        DuplexConverter<Object, T> converter = Conversion.findDuplex((Class<Object>) internalType, externalType);
        if (converter == null) {
            throw new RuntimeException("Failed to find converter from internal type " +
                    internalType.getName() + " to " + externalType.getName());
        }

        if (optional) {
            converter = new OptionalDuplexConverter<T>(converter);
        }

        return new ConvertedToken<>(token, converter);
    }

    public static final class ConvertedToken<T> {
        public final Object token;
        public final DuplexConverter<Object, T> converter;

        public ConvertedToken(Object token, DuplexConverter<Object, T> converter) {
            this.token = token;
            this.converter = converter;
        }
    }

    public static final class InternalType {
        public final Object token;
        public final Class<?> type;
        public final boolean optional;

        private InternalType(Object token, Class<?> type, boolean optional) {
            this.token = token;
            this.type = type;
            this.optional = optional;
        }

        @Override
        public String toString() {
            String s = this.type.getName();
            if (this.optional) {
                s = "Optional<" + s + ">";
            }
            return s + ":" + this.token;
        }
    }

    /**
     * Used to convert between Integer and Optional<Integer>.
     * Internally it stores the int value incremented or decremented by one, to allow for 0 as 'not set'
     */
    private static final class EntityIdTypeConverter extends DuplexConverter<Object, java.util.OptionalInt> {

        public EntityIdTypeConverter() {
            super(Integer.class, java.util.OptionalInt.class);
        }

        @Override
        public java.util.OptionalInt convertInput(Object value) {
            if (value instanceof Integer) {
                int intValue = ((Integer) value).intValue();
                if (intValue > 0) {
                    return java.util.OptionalInt.of(intValue - 1);
                }
            }
            return java.util.OptionalInt.empty();
        }

        @Override
        public Object convertOutput(java.util.OptionalInt value) {
            if (value != null && value.isPresent()) {
                return Integer.valueOf(value.getAsInt() + 1);
            } else {
                return Integer.valueOf(0);
            }
        }

        @Override
        public boolean acceptsNullInput() {
            return true;
        }

        @Override
        public boolean acceptsNullOutput() {
            return true;
        }
    }

    private static final class BoatWoodTypeIdConverter extends DuplexConverter<Object, BoatWoodType> {

        public BoatWoodTypeIdConverter() {
            super(Integer.class, BoatWoodType.class);
        }

        @Override
        public BoatWoodType convertInput(Object value) {
            if (value instanceof Integer) {
                return BoatWoodType.byId(((Integer) value).intValue());
            } else {
                return BoatWoodType.OAK;
            }
        }

        @Override
        public Object convertOutput(BoatWoodType value) {
            return Integer.valueOf((value == null) ? 0 : value.getId());
        }

        @Override
        public boolean acceptsNullInput() {
            return true;
        }

        @Override
        public boolean acceptsNullOutput() {
            return true;
        }
    }

    private static final class SlimeSizeByteConverter extends DuplexConverter<Object, Integer> {

        public SlimeSizeByteConverter() {
            super(byte.class, int.class);
        }

        @Override
        public Integer convertInput(Object value) {
            return Integer.valueOf(((Number) value).byteValue() & 0xFF);
        }

        @Override
        public Object convertOutput(Integer integer) {
            return Byte.valueOf(integer.byteValue());
        }
    }

    public static final class OptionalDuplexConverter<T> extends DuplexConverter<Object, T> {
        private final DuplexConverter<Object, T> _baseConverter;

        public OptionalDuplexConverter(DuplexConverter<Object, T> baseConverter) {
            super(makeOptional(baseConverter.input), baseConverter.output);
            this._baseConverter = baseConverter;
        }

        public DuplexConverter<Object, T> getBase() {
            return _baseConverter;
        }

        @Override
        public T convertInput(Object value) {
            value = CommonNMS.unwrapDWROptional(value);
            if (value == null && !this._baseConverter.acceptsNullInput()) {
                return null;
            } else {
                return this._baseConverter.convertInput(value);
            }
        }

        @Override
        public Object convertOutput(T value) {
            Object result;
            if (value != null || this._baseConverter.acceptsNullOutput()) {
                result = this._baseConverter.convertOutput(value);
            } else {
                result = null;
            }
            result = CommonNMS.wrapDWROptional(result);
            return result;
        }

        @Override
        public boolean acceptsNullInput() {
            return true;
        }

        @Override
        public boolean acceptsNullOutput() {
            return true;
        }

        private static TypeDeclaration makeOptional(TypeDeclaration type) {
            return TypeDeclaration.createGeneric(CommonNMS.DWR_OPTIONAL_TYPE, type);
        }
    }
}
