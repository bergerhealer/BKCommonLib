package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Wrapper type representing the 26.3+ ClientboundLevelParticlesPacket randomization type.
 * Before 26.3 only DEFAULT is used.
 */
public enum ParticleRandomization {
    DEFAULT,
    ALTERNATIVE,
    ALTERNATIVE_WITH_SPEED;

    private final Object handle;
    private static final Map<Object, ParticleRandomization> byHandle = createByHandleMap();

    static {
        for (ParticleRandomization value : values()) {
            if (value.handle != null) {
                byHandle.put(value.handle, value);
            }
        }
    }

    ParticleRandomization() {
        CommonBootstrap.initCommonServer();

        Object handle = null;
        if (CommonBootstrap.evaluateMCVersion(">=", "26.3")) {
            Class<?> enumType = CommonUtil.getClass("net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket$RandomizationType");
            if (enumType != null) {
                for (Object type : enumType.getEnumConstants()) {
                    if (type instanceof Enum && ((Enum<?>) type).name().equals(this.name())) {
                        handle = type;
                        break;
                    }
                }
            }
            if (handle == null) {
                Logging.LOGGER_CONVERSION.severe("Failed to find Particle Randomization enum for " + name());
            }
        }
        this.handle = handle;
    }

    @ConverterMethod(input="net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket$RandomizationType")
    public static ParticleRandomization fromNMSRandomizationType(Object type) {
        return byHandle.get(type);
    }

    @ConverterMethod(output="net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket$RandomizationType")
    public static Object getNMSRandomizationType(ParticleRandomization particleRandomization) {
        return particleRandomization.handle;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Map<Object, ParticleRandomization> createByHandleMap() {
        if (CommonBootstrap.evaluateMCVersion(">=", "26.3")) {
            Class<?> enumType = CommonUtil.getClass("net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket$RandomizationType");
            if (enumType != null) {
                return new EnumMap(enumType);
            }
        }

        return Collections.emptyMap();
    }
}
