package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityLiving;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftServer;

import net.minecraft.server.v1_11_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.util.CraftMagicNumbers;
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
 
    public static ItemStack getNative(org.bukkit.inventory.ItemStack stack) {
        return (ItemStack) Conversion.toItemStackHandle.convert(stack);
    }

    public static EntityItem getNative(org.bukkit.entity.Item item) {
        return getNative(item, EntityItem.class);
    }

    public static EntityLiving getNative(LivingEntity l) {
        return getNative(l, EntityLiving.class);
    }

    public static EntityHuman getNative(HumanEntity h) {
        return getNative(h, EntityHuman.class);
    }

    public static EntityPlayer getNative(Player p) {
        return getNative(p, EntityPlayer.class);
    }

    public static <T extends Entity> T getNative(org.bukkit.entity.Entity e, Class<T> type) {
        return CommonUtil.tryCast(getNative(e), type);
    }

    public static Entity getNative(org.bukkit.entity.Entity entity) {
        return (Entity) Conversion.toEntityHandle.convert(entity);
    }

    public static WorldServer getNative(org.bukkit.World world) {
        return world instanceof CraftWorld ? ((CraftWorld) world).getHandle() : null;
    }

    public static Chunk getNative(org.bukkit.Chunk chunk) {
        return (Chunk) Conversion.toChunkHandle.convert(chunk);
    }

    @SuppressWarnings("deprecation")
    public static Block getBlock(int id) {
        return getBlock(org.bukkit.Material.getMaterial(id));
    }

    public static Item getItem(int id) {
        return Item.getById(id);
    }

    public static Block getBlock(org.bukkit.Material material) {
        return material == null ? null : CraftMagicNumbers.getBlock(material);
    }

    public static Item getItem(org.bukkit.Material material) {
        return material == null ? null : CraftMagicNumbers.getItem(material);
    }

    /**
     * Gets the native Minecraft Server which contains the main logic
     *
     * @return Minecraft Server
     */
    public static MinecraftServer getMCServer() {
        return (MinecraftServer) CBCraftServer.getServer.invoke(Bukkit.getServer());
    }

    /**
     * Gets the native Minecraft Server Player List, which keeps track of
     * player-related information
     *
     * @return Minecraft Server Player List
     */
    public static DedicatedPlayerList getPlayerList() {
        return (DedicatedPlayerList) CBCraftServer.getPlayerList.invoke(Bukkit.getServer());
    }

    public static AttributeMapServer getEntityAttributes(org.bukkit.entity.LivingEntity entity) {
        return (AttributeMapServer) NMSEntityLiving.getAttributesMap.invoke(Conversion.toEntityHandle.convert(entity));
    }

}
