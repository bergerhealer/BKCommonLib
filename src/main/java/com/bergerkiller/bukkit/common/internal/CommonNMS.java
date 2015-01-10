package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.server.v1_8_R1.*;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.inventory.*;
import org.bukkit.craftbukkit.v1_8_R1.util.CraftMagicNumbers;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingCollection;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingList;
import com.bergerkiller.bukkit.common.reflection.classes.BlockStateRef;
import com.bergerkiller.bukkit.common.reflection.classes.CraftServerRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityLivingRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

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
@SuppressWarnings("rawtypes")
public class CommonNMS {

    public static double getMiddleX(AxisAlignedBB aabb) {
        return 0.5 * (aabb.a + aabb.d);
    }

    public static double getMiddleY(AxisAlignedBB aabb) {
        return 0.5 * (aabb.b + aabb.e);
    }

    public static double getMiddleZ(AxisAlignedBB aabb) {
        return 0.5 * (aabb.c + aabb.f);
    }

    public static Vec3D newVec3D(double x, double y, double z) {
        Vec3D vec3d = new Vec3D(x, y, z);
        return vec3d.a(x, y, z);
    }

    /**
     * Obtains the internal list of native Minecraft server worlds<br>
     * Gets the MinecraftServer.worlds value
     *
     * @return A list of WorldServer instances
     */
    public static List<WorldServer> getWorlds() {
        try {
            List<WorldServer> worlds = getMCServer().worlds;
            if (worlds != null) {
                return worlds;
            }
        } catch (NullPointerException ex) {
        }
        return new ArrayList<WorldServer>();
    }

    /**
     * Obtains the internal list of native Entity instances in a world
     *
     * @param world to get from
     * @return list of native entity instances
     */
    @SuppressWarnings("unchecked")
    public static List<Entity> getEntities(org.bukkit.World world) {
        return getNative(world).entityList;
    }

    public static ItemStack getNative(org.bukkit.inventory.ItemStack stack) {
        return (ItemStack) Conversion.toItemStackHandle.convert(stack);
    }

    public static IInventory getNative(Inventory inv) {
        return inv instanceof CraftInventory ? ((CraftInventory) inv).getInventory() : null;
    }

    public static EntityItem getNative(org.bukkit.entity.Item item) {
        return getNative(item, EntityItem.class);
    }

    public static EntityMinecartAbstract getNative(Minecart m) {
        return getNative(m, EntityMinecartAbstract.class);
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

    public static TileEntity getNative(org.bukkit.block.BlockState blockState) {
        return (TileEntity) BlockStateRef.toTileEntity(blockState);
    }

    public static Inventory getInventory(IInventory inventory) {
        return Conversion.toInventory.convert(inventory);
    }

    public static <T extends Inventory> T getInventory(IInventory inventory, Class<T> type) {
        return CommonUtil.tryCast(getInventory(inventory), type);
    }

    public static HumanEntity getHuman(EntityHuman entity) {
        return getEntity(entity, HumanEntity.class);
    }

    public static Player getPlayer(EntityPlayer entity) {
        return getEntity(entity, Player.class);
    }

    public static org.bukkit.entity.Item getItem(EntityItem entity) {
        return getEntity(entity, org.bukkit.entity.Item.class);
    }

    public static <T extends org.bukkit.entity.Entity> T getEntity(Entity entity, Class<T> type) {
        return CommonUtil.tryCast(getEntity(entity), type);
    }

    public static org.bukkit.entity.Entity getEntity(Entity entity) {
        return Conversion.toEntity.convert(entity);
    }

    public static org.bukkit.Chunk getChunk(Chunk chunk) {
        return chunk == null ? null : chunk.bukkitChunk;
    }

    public static org.bukkit.World getWorld(World world) {
        return world == null ? null : world.getWorld();
    }

    public static Collection<org.bukkit.Chunk> getChunks(Collection<?> chunks) {
        return new ConvertingCollection<org.bukkit.Chunk>(chunks, ConversionPairs.chunk);
    }

    public static Collection<Player> getPlayers(Collection players) {
        return getEntities(players, Player.class);
    }

    public static Collection<org.bukkit.entity.Entity> getEntities(Collection entities) {
        return getEntities(entities, org.bukkit.entity.Entity.class);
    }

    public static <T extends org.bukkit.entity.Entity> Collection<T> getEntities(Collection entities, Class<T> type) {
        return new ConvertingCollection<T>(entities, Conversion.toEntityHandle, Conversion.getConverter(type));
    }

    public static org.bukkit.inventory.ItemStack getItemStack(ItemStack itemstack) {
        return CraftItemStack.asCraftMirror(itemstack);
    }

    public static org.bukkit.inventory.ItemStack[] getItemStacks(ItemStack[] itemstacks) {
        org.bukkit.inventory.ItemStack[] stacks = new org.bukkit.inventory.ItemStack[itemstacks.length];
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = getItemStack(itemstacks[i]);
        }
        return stacks;
    }

    public static boolean isValidBlockId(int blockId) {
        return Item.getById(blockId) != null;
    }

    public static List<Entity> getEntities(World world, Entity ignore,
            double xmin, double ymin, double zmin, double xmax, double ymax, double zmax) {
        return getEntitiesIn(world, ignore, AxisAlignedBB.a(xmin, ymin, zmin, xmax, ymax, zmax));
    }

    @SuppressWarnings("unchecked")
    public static List<Entity> getEntitiesIn(World world, Entity ignore, AxisAlignedBB bounds) {
        return (List<Entity>) world.getEntities(ignore, bounds.grow(0.25, 0.25, 0.25));
    }

    public static List<org.bukkit.entity.Entity> getEntities(org.bukkit.World world, org.bukkit.entity.Entity ignore, AxisAlignedBB area) {
        List<?> list = CommonNMS.getEntitiesIn(CommonNMS.getNative(world), CommonNMS.getNative(ignore), area);
        if (LogicUtil.nullOrEmpty(list)) {
            return Collections.emptyList();
        }
        return new ConvertingList<org.bukkit.entity.Entity>(list, ConversionPairs.entity);
    }

    @SuppressWarnings("deprecation")
    public static Block getBlock(int id) {
        return getBlock(org.bukkit.Material.getMaterial(id));
    }

    @SuppressWarnings("deprecation")
    public static Item getItem(int id) {
        return CraftMagicNumbers.getItem(id);
    }

    public static Block getBlock(org.bukkit.Material material) {
        return material == null ? null : CraftMagicNumbers.getBlock(material);
    }

    public static Item getItem(org.bukkit.Material material) {
        return material == null ? null : CraftMagicNumbers.getItem(material);
    }

    public static org.bukkit.Material getMaterial(Block block) {
        return CraftMagicNumbers.getMaterial(block);
    }

    public static org.bukkit.Material getMaterial(Item item) {
        return CraftMagicNumbers.getMaterial(item);
    }

    /**
     * Gets the native Minecraft Server which contains the main logic
     *
     * @return Minecraft Server
     */
    public static MinecraftServer getMCServer() {
        return (MinecraftServer) CraftServerRef.getServer.invoke(Bukkit.getServer());
    }

    /**
     * Gets the native Minecraft Server Player List, which keeps track of
     * player-related information
     *
     * @return Minecraft Server Player List
     */
    public static DedicatedPlayerList getPlayerList() {
        return (DedicatedPlayerList) CraftServerRef.getPlayerList.invoke(Bukkit.getServer());
    }

    public static AttributeMapServer getEntityAttributes(org.bukkit.entity.LivingEntity entity) {
        return (AttributeMapServer) EntityLivingRef.getAttributesMap.invoke(Conversion.toEntityHandle.convert(entity));
    }
}
