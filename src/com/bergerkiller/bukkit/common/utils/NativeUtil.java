package com.bergerkiller.bukkit.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.server.Chunk;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EntityMinecart;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.TileEntityChest;
import net.minecraft.server.TileEntityDispenser;
import net.minecraft.server.TileEntityFurnace;
import net.minecraft.server.TileEntitySign;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftChest;
import org.bukkit.craftbukkit.block.CraftDispenser;
import org.bukkit.craftbukkit.block.CraftFurnace;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.bergerkiller.bukkit.common.natives.NativeChunkWrapper;
import com.bergerkiller.bukkit.common.natives.NativeEntityWrapper;
import com.bergerkiller.bukkit.common.reflection.classes.BlockStateRef;

/**
 * Contains utility functions to get to the net.minecraft.server core in the CraftBukkit library<br>
 * Try to avoid using this class as much as possible!
 */
public class NativeUtil {

	/**
	 * Obtains the internal list of native Minecraft server worlds
	 * 
	 * @return A list of WorldServer instances
	 */
	public static List<WorldServer> getWorlds() {
		try {
			List<WorldServer> worlds = CommonUtil.getMCServer().worlds;
			if (worlds != null)
				return worlds;
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
		ItemStack rval = CraftItemStack.createNMSItemStack(stack);
		if (rval == null) {
			rval = new ItemStack(0, 0, 0);
		}
		return rval;
	}

	public static IInventory getNative(Inventory inv) {
		return inv instanceof CraftInventory ? ((CraftInventory) inv).getInventory() : null;
	}

	public static EntityItem getNative(Item item) {
		return getNative(item, EntityItem.class);
	}

	public static EntityMinecart getNative(Minecart m) {
		return getNative(m, EntityMinecart.class);
	}

	public static EntityPlayer getNative(Player p) {
		return getNative(p, EntityPlayer.class);
	}

	public static <T extends Entity> T getNative(org.bukkit.entity.Entity e, Class<T> type) {
		return CommonUtil.tryCast(getNative(e), type);
	}

	public static Entity getNative(org.bukkit.entity.Entity entity) {
		return entity instanceof CraftEntity ? ((CraftEntity) entity).getHandle() : null;
	}

	public static WorldServer getNative(org.bukkit.World world) {
		return world instanceof CraftWorld ? ((CraftWorld) world).getHandle() : null;
	}

	public static Chunk getNative(org.bukkit.Chunk chunk) {
		return chunk instanceof CraftChunk ? ((CraftChunk) chunk).getHandle() : null;
	}

	public static TileEntitySign getNative(Sign sign) {
		return sign instanceof CraftSign ? BlockStateRef.SIGN.get(sign) : null;
	}

	public static TileEntityFurnace getNative(Furnace furnace) {
		return furnace instanceof CraftFurnace ? BlockStateRef.FURNACE.get(furnace) : null;
	}

	public static TileEntityDispenser getNative(Dispenser dispenser) {
		return dispenser instanceof CraftDispenser ? BlockStateRef.DISPENSER.get(dispenser) : null;
	}

	public static TileEntityChest getNative(Chest chest) {
		return chest instanceof CraftChest ? BlockStateRef.CHEST.get(chest) : null;
	}

	public static <T extends org.bukkit.entity.Entity> T getEntity(Entity entity, Class<T> type) {
		return CommonUtil.tryCast(getEntity(entity), type);
	}

	public static org.bukkit.entity.Entity getEntity(Entity entity) {
		return entity == null ? null : entity.getBukkitEntity();
	}

	public static org.bukkit.Chunk getChunk(Chunk chunk) {
		return chunk == null ? null : chunk.bukkitChunk;
	}

	public static org.bukkit.World getWorld(World world) {
		return world == null ? null : world.getWorld();
	}

	public static Collection<org.bukkit.Chunk> getChunks(Collection<Chunk> chunks) {
		return new NativeChunkWrapper(chunks);
	}

	public static Collection<org.bukkit.entity.Entity> getEntities(Collection<Entity> entities) {
		return new NativeEntityWrapper(entities);
	}
}
