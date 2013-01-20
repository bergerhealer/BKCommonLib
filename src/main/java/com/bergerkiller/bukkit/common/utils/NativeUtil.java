package com.bergerkiller.bukkit.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.server.v1_4_R1.*;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_4_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_4_R1.block.*;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_4_R1.inventory.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import com.bergerkiller.bukkit.common.natives.NativeChunkWrapper;
import com.bergerkiller.bukkit.common.natives.NativeEntityWrapper;
import com.bergerkiller.bukkit.common.reflection.classes.BlockStateRef;
import com.bergerkiller.bukkit.common.reflection.classes.CraftItemStackRef;

/**
 * Contains utility functions to get to the net.minecraft.server core in the CraftBukkit library<br>
 * Try to avoid using this class as much as possible!
 */
@SuppressWarnings("rawtypes")
public class NativeUtil {

	/**
	 * Obtains the internal list of native Minecraft server worlds<br>
	 * Gets the MinecraftServer.worlds value
	 * 
	 * @return A list of WorldServer instances
	 */
	public static List<WorldServer> getWorlds() {
		try {
			List<WorldServer> worlds = CommonUtil.getMCServer().worlds;
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
		if (stack instanceof CraftItemStack) {
			return CraftItemStackRef.handle.get(stack);
		}
		return CraftItemStack.asNMSCopy(stack);
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

	public static Inventory getInventory(IInventory inventory) {
		if (inventory == null) {
			return null;
		}
		if (inventory instanceof InventoryCrafting) {
			return new CraftInventoryCrafting((InventoryCrafting) inventory, null);
		} else if (inventory instanceof PlayerInventory) {
			return new CraftInventoryPlayer((PlayerInventory) inventory);
		} else if (inventory instanceof TileEntityFurnace) {
			return new CraftInventoryFurnace((TileEntityFurnace) inventory);
		} else if (inventory instanceof ContainerEnchantTableInventory) {
			return new CraftInventoryEnchanting((ContainerEnchantTableInventory) inventory);
		} else if (inventory instanceof TileEntityBrewingStand) {
			return new CraftInventoryBrewer((TileEntityBrewingStand) inventory);
		} else if (inventory instanceof InventoryMerchant) {
			return new CraftInventoryMerchant((InventoryMerchant) inventory);
		} else if (inventory instanceof TileEntityBeacon) {
			return new CraftInventoryBeacon((TileEntityBeacon) inventory);
		} else if (inventory instanceof ContainerAnvilInventory) {
			return new CraftInventoryAnvil((ContainerAnvilInventory) inventory);
		} else {
			return new CraftInventory(inventory);
		}
	}

	public static <T extends Inventory> T getInventory(IInventory inventory, Class<T> type) {
		return CommonUtil.tryCast(getInventory(inventory), type);
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
		return entity == null ? null : (org.bukkit.entity.Entity)entity.getBukkitEntity();
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

	@SuppressWarnings("unchecked")
	public static Collection<EntityPlayer> getOnlinePlayers() {
		return CommonUtil.getServerConfig().players;
	}

	public static Collection<Player> getPlayers(Collection players) {
		return getEntities(players, Player.class);
	}

	public static Collection<org.bukkit.entity.Entity> getEntities(Collection entities) {
		return getEntities(entities, org.bukkit.entity.Entity.class);
	}

	public static <T extends org.bukkit.entity.Entity> Collection<T> getEntities(Collection entities, Class<T> type) {
		return new NativeEntityWrapper<T>(entities, type);
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
}
