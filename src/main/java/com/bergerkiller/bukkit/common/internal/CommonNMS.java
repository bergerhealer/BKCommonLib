package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.server.v1_5_R2.*;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.craftbukkit.v1_5_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_5_R2.inventory.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingCollection;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingList;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Contains utility functions to get to the net.minecraft.server core in the CraftBukkit library.<br>
 * This Class should only be used internally by BKCommonLib, as it exposes NMS and CraftBukkit types.<br>
 * Where possible, methods in this Class will delegate to Conversion constants.<br>
 * Do NOT use these methods in your converters, it might fail with stack overflow exceptions.
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

	public static EntityItem getNative(Item item) {
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

	public static Item getItem(EntityItem entity) {
		return getEntity(entity, Item.class);
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
		return LogicUtil.isInBounds(Block.byId, blockId) && Block.byId[blockId] != null;
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

	/**
	 * Gets the native Minecraft Server which contains the main logic
	 * 
	 * @return Minecraft Server
	 */
	public static MinecraftServer getMCServer() {
		return getCraftServer().getServer();
	}

	/**
	 * Gets the Craft server
	 * 
	 * @return Craft server
	 */
	public static CraftServer getCraftServer() {
		return (CraftServer) Bukkit.getServer();
	}
}
