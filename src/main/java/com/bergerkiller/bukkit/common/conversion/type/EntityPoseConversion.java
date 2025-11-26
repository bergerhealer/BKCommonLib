package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityPose;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Converts NMS EntityPose into our Wrapper {@link EntityPose} and back.
 * Only used on Minecraft 1.14+.
 */
public class EntityPoseConversion {
    public static final Class<?> NMS_ENTITY_POSE_TYPE = CommonUtil.getClass("net.minecraft.world.entity.EntityPose");
    private static final Map<Object, EntityPose> toWrapper;
    private static final Map<EntityPose, Object> toHandle;
    static {
        if (CommonBootstrap.evaluateMCVersion("<", "1.14")) {
            toWrapper = Collections.emptyMap();
            toHandle = Collections.emptyMap();
        } else if (NMS_ENTITY_POSE_TYPE == null || !Enum.class.isAssignableFrom(NMS_ENTITY_POSE_TYPE)) {
            toWrapper = Collections.emptyMap();
            toHandle = Collections.emptyMap();
            Logging.LOGGER_REFLECTION.severe("Failed to find EntityPose enum class, entity pose API is broken");
        } else {
            toWrapper = LogicUtil.createRawEnumMap(NMS_ENTITY_POSE_TYPE);
            toHandle = new EnumMap<>(EntityPose.class);
            for (Object enumValue : NMS_ENTITY_POSE_TYPE.getEnumConstants()) {
                String name = ((Enum<?>) enumValue).name();
                EntityPose wrapperValue = EntityPose.fromName(name);
                toWrapper.put(enumValue, wrapperValue);
                toHandle.put(wrapperValue, enumValue);
            }

            // For all unknown constants (including UNKNOWN), default to STANDING
            Object nmsStanding = toHandle.get(EntityPose.STANDING);
            for (EntityPose pose : EntityPose.values()) {
                toHandle.putIfAbsent(pose, nmsStanding);
            }
        }
    }

    @ConverterMethod(output="net.minecraft.world.entity.EntityPose")
    public static Object getEnumEntityPose(EntityPose pose) {
        return toHandle.get(pose);
    }

    @ConverterMethod(input="net.minecraft.world.entity.EntityPose")
    public static EntityPose getEntityPose(Object enumEntityPose) {
        return toWrapper.get(enumEntityPose);
    }
}
