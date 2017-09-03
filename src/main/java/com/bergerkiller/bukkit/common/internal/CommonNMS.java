package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.AttributeMapServerHandle;
import com.bergerkiller.generated.net.minecraft.server.ChunkHandle;
import com.bergerkiller.generated.net.minecraft.server.DedicatedPlayerListHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHumanHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityItemHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityLivingHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.ItemHandle;
import com.bergerkiller.generated.net.minecraft.server.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;
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
        return CommonUtil.tryCast(Conversion.toEntityHandle.convert(e), type.getType());
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
     * Gets the native Minecraft Server which contains the main logic
     *
     * @return Minecraft Server
     */
    public static MinecraftServerHandle getMCServer() {
        return MinecraftServerHandle.instance();
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

    public static AttributeMapServerHandle getEntityAttributes(org.bukkit.entity.LivingEntity entity) {
        return EntityLivingHandle.T.getAttributeMap.invoke(HandleConversion.toEntityHandle(entity));
    }

}
