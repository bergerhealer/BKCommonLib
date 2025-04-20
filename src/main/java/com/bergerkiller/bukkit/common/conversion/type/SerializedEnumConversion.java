package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

/**
 * Enum classes (Minecraft) that feature enums that are convertible from and to
 * String, using a key function name. (Typically INamable getSerializedName())
 */
public class SerializedEnumConversion {

    /**
     * Registers internal Minecraft enums that serialize from and to String
     */
    public static void registerMinecraftEnumConversion() {
        if (CommonBootstrap.evaluateMCVersion(">=", "1.21.5")) {
            registerEnum("net.minecraft.world.scores.ScoreboardTeamBase$EnumNameTagVisibility", "getSerializedName");
            registerEnum("net.minecraft.world.scores.ScoreboardTeamBase$EnumTeamPush", "getSerializedName");
        }
    }

    private static void registerEnum(String nmsEnumClassName, String nameGetterFuncName) {
        Class<?> nmsEnumClass = Resolver.loadClass(nmsEnumClassName, true);
        if (nmsEnumClass == null) {
            Logging.LOGGER_REFLECTION.severe("Failed to find enum " + nmsEnumClassName + " to register it in conversion");
        }

        try {
            Conversion.registerConverter(new EnumDuplexConverter(nmsEnumClass, nameGetterFuncName));
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to register enum " + nmsEnumClassName + " for conversion", t);
        }
    }

    public static class EnumDuplexConverter extends DuplexConverter<String, Object> {
        private final FastMethod<String> nameGetter;
        private final Map<String, Object> byName;

        public EnumDuplexConverter(Class<?> enumType, String nameGetterFuncName) {
            super(String.class, enumType);

            if (!enumType.isEnum())
                throw new IllegalStateException("Input type " + enumType + " is not an enum");

            try {
                java.lang.reflect.Method getter = Resolver.resolveAndGetDeclaredMethod(enumType, nameGetterFuncName);

                if (Modifier.isStatic(getter.getModifiers()))
                    throw new IllegalStateException("Method " + nameGetterFuncName +
                            " of " + enumType.getName() + " is static");

                if (getter.getReturnType() != String.class)
                    throw new IllegalStateException("Method " + nameGetterFuncName +
                            " of " + enumType.getName() +
                            " does not return String (returns " +
                            getter.getReturnType().getName() + " instead)");

                nameGetter = new FastMethod<>(getter);
                nameGetter.forceInitialization();

                Object[] constants = enumType.getEnumConstants();
                byName = new HashMap<>(constants.length * 3);
                for (Object value : constants) {
                    String key = nameGetter.invoke(value);
                    byName.put(key, value);
                    byName.put(key.toLowerCase(Locale.ENGLISH), value);
                    byName.put(key.toUpperCase(Locale.ENGLISH), value);
                }
            } catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow(t);
            }
        }

        @Override
        public Object convertInput(String value) {
            Object item = byName.get(value);
            if (item == null)
                throw new IllegalArgumentException("Not a valid value for " + this.output + ": " + value);

            return item;
        }

        @Override
        public String convertOutput(Object value) {
            return nameGetter.invoke(value);
        }
    }
}
