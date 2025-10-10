package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.server.dedicated.DedicatedPlayerListHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityLivingHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.item.EntityItemHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.EntityHumanHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.ChunkHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftServerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Contains utility functions to get to the net.minecraft.server core in the
 * CraftBukkit library.<br>
 * This Class should only be used internally by BKCommonLib, as it exposes NMS
 * and CraftBukkit types.<br>
 * Where possible, methods in this Class will delegate to Conversion
 * constants.<br>
 * Do NOT use these methods in your converters, it might fail with stack
 * overflow exceptions.
 */
public class CommonNMS {
    // Optional class that is used by the DataWatcherRegistry. Differs between 1.13 vs older.
    private static Class<?> JAVA_UTIL_OPTIONAL_TYPE = CommonUtil.getClass("java.util.Optional");
    private static Class<?> GOOGLE_OPTIONAL_TYPE = CommonUtil.getClass("com.google.common.base.Optional");
    public static Class<?> DWR_OPTIONAL_TYPE = Common.evaluateMCVersion(">=", "1.13") ? JAVA_UTIL_OPTIONAL_TYPE : GOOGLE_OPTIONAL_TYPE;

    public static boolean isDWROptionalType(Class<?> type) {
        return type != null && type.equals(DWR_OPTIONAL_TYPE);
    }

    @SuppressWarnings("unchecked")
    public static Object unwrapDWROptional(Object value) {
        if (DWR_OPTIONAL_TYPE != null) {
            if (DWR_OPTIONAL_TYPE == GOOGLE_OPTIONAL_TYPE) {
                if (value instanceof com.google.common.base.Optional) {
                    com.google.common.base.Optional<Object> opt = (com.google.common.base.Optional<Object>) value;
                    return opt.isPresent() ? opt.get() : null;
                }
            } else if (DWR_OPTIONAL_TYPE == JAVA_UTIL_OPTIONAL_TYPE) {
                if (value instanceof java.util.Optional) {
                    java.util.Optional<Object> opt = (java.util.Optional<Object>) value;
                    return opt.isPresent() ? opt.get() : null;
                }
            }
        }
        return value;
    }

    public static Object wrapDWROptional(Object value) {
        if (DWR_OPTIONAL_TYPE != null && (value == null || !DWR_OPTIONAL_TYPE.isAssignableFrom(value.getClass()))) {
            if (DWR_OPTIONAL_TYPE.equals(JAVA_UTIL_OPTIONAL_TYPE)) {
                value = java.util.Optional.ofNullable(value);
            } else {
                value = com.google.common.base.Optional.fromNullable(value);
            }
        }
        return value;
    }

    public static boolean isItemEmpty(Object rawItemStackHandle) {
        if (rawItemStackHandle == null) {
            return true;
        }
        if (ItemStackHandle.T.isEmpty.isAvailable()) {
            return ItemStackHandle.T.isEmpty.invoke(rawItemStackHandle);
        } else {
            return false;
        }
    }

    public static ChunkHandle getHandle(org.bukkit.Chunk chunk) {
        return ChunkHandle.createHandle(HandleConversion.toChunkHandle(chunk));
    }

    public static ItemStackHandle getHandle(org.bukkit.inventory.ItemStack stack) {
        return ItemStackHandle.createHandle(Conversion.toItemStackHandle.convert(stack));
    }

    public static EntityHandle getHandle(org.bukkit.entity.Entity entity) {
        return getHandle(entity, EntityHandle.T);
    }

    public static EntityItemHandle getHandle(org.bukkit.entity.Item item) {
        return getHandle(item, EntityItemHandle.T);
    }

    public static EntityLivingHandle getHandle(LivingEntity l) {
        return getHandle(l, EntityLivingHandle.T);
    }

    public static EntityHumanHandle getHandle(HumanEntity h) {
        return getHandle(h, EntityHumanHandle.T);
    }

    public static EntityPlayerHandle getHandle(Player p) {
        return getHandle(p, EntityPlayerHandle.T);
    }

    public static Object getRawHandle(org.bukkit.entity.Entity e, Template.Class<?> type) {
        return LogicUtil.tryCast(HandleConversion.toEntityHandle(e), type.getType());
    }

    public static <T extends Template.Handle> T getHandle(org.bukkit.entity.Entity e, Template.Class<T> type) {
        Object rawInstance = HandleConversion.toEntityHandle(e);
        if (type.isAssignableFrom(rawInstance)) {
            return type.createHandle(rawInstance);
        } else {
            return null;
        }
    }

    public static WorldServerHandle getHandle(org.bukkit.World world) {
        return WorldServerHandle.createHandle(HandleConversion.toWorldHandle(world));
    }

    public static ItemHandle getItem(org.bukkit.Material material) {
        return material == null ? null : ItemHandle.createHandle(HandleConversion.toItemHandle(material));
    }

    /**
     * Gets the native Minecraft Server Player List, which keeps track of
     * player-related information
     *
     * @return Minecraft Server Player List
     */
    public static DedicatedPlayerListHandle getPlayerList() {
        return CraftServerHandle.instance().getPlayerList();
    }
}
