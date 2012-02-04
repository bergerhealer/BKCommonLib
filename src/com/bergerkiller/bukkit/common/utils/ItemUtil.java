package com.bergerkiller.bukkit.common.utils;

import java.util.ArrayList;
import java.util.logging.Level;

import me.snowleo.bleedingmobs.BleedingMobs;
import net.minecraft.server.IInventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TexturedMaterial;
import org.bukkit.material.Tree;
import org.bukkit.material.Wool;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.ItemParser;
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.narrowtux.showcase.Showcase;

public class ItemUtil {
	
	public static boolean isIgnored(Entity itementity) {
		if (!(itementity instanceof Item)) return true; 
		Item item = (Item) itementity;
		if (Common.isShowcaseEnabled) {
			try {
		        if (Showcase.instance.getItemByDrop(item) != null) return true;
			} catch (Throwable t) {
				Bukkit.getLogger().log(Level.SEVERE, "Showcase item verification failed (update needed?), contact the authors!");
				t.printStackTrace();
				Common.isShowcaseEnabled = false;
			}
		}
		if (Common.isSCSEnabled) { 
			try {
				if (ShowCaseStandalone.get().isShowCaseItem(item)) return true;
			} catch (Throwable t) {
			    Bukkit.getLogger().log(Level.SEVERE, "ShowcaseStandalone item verification failed (update needed?), contact the authors!");
		        t.printStackTrace();
		        Common.isSCSEnabled = false;
			}
		}
		if (Common.bleedingMobsInstance != null) {
			try {
				BleedingMobs bm = (BleedingMobs) Common.bleedingMobsInstance;
				if (bm.isSpawning()) return true;
				if (bm.isWorldEnabled(item.getWorld())) {
					if (bm.isParticleItem(((CraftItem) item).getUniqueId())) {
						return true;
					}
				}
			} catch (Throwable t) {
				Bukkit.getLogger().log(Level.SEVERE, "Bleeding Mobs item verification failed (update needed?), contact the authors!");
		        t.printStackTrace();
		        Common.bleedingMobsInstance = null;
			}
		}
		return false;
	}
	
	public static void transfer(IInventory from, IInventory to) {
		net.minecraft.server.ItemStack[] items = from.getContents();
		for (int i = 0;i < items.length;i++) {
			if (items[i] != null) {
				to.setItem(i, new net.minecraft.server.ItemStack(items[i].id, items[i].count, items[i].b));
			}
		}
		for (int i = 0;i < items.length;i++) from.setItem(i, null);
	}
	
	public static int transfer(Inventory from, Inventory to, ItemParser parser, int limit) {
		return transfer(from, new Inventory[] {to}, parser, limit);
	}
	public static int transfer(Inventory[] from, Inventory to, ItemParser parser, int limit) {
		return transfer(from, new Inventory[] {to}, parser, limit);
	}
	public static int transfer(Inventory[] from, Inventory[] to, ItemParser parser, int limit) {
		if (limit == 0) return 0;
		int transferred = 0;
		for (Inventory ifrom : from) {
			transferred += transfer(ifrom, to, parser, limit - transferred);
		}
		return transferred;
	}
	public static int transfer(Inventory from, Inventory[] to, ItemParser parser, int limit) {
		if (limit == 0) return 0;
		ItemStack item;
		int transferred = 0;
		for (int i = 0; i < from.getSize(); i++) {
			item = from.getItem(i);
			if (item == null) continue;
			if (parser != null && !parser.match(item)) continue;
			transferred += transfer(item, to, limit - transferred);
			if (item.getAmount() == 0) from.setItem(i, null);
		}
		return transferred;
	}
	
	public static int transfer(ItemStack item, Inventory[] inventories, int limit) {
		if (limit == 0) return 0;
		int transferred = 0;
		for (Inventory inv : inventories) {
			transferred += transfer(item, inv, limit - transferred);
			if (item.getAmount() <= 0) break;
		}
		return transferred;
	}
	public static int transfer(ItemStack item, Inventory inventory, int limit) {
		if (limit == 0) return 0;
		int transferred = 0;
		if (item == null) return transferred;
		if (item.getTypeId() == 0) return transferred;
		if (item.getAmount() <= 0) return transferred;
		
		//try to add to already existing items
		for (ItemStack iitem : inventory.getContents()) {
			transferred += transfer(item, iitem, limit - transferred);
		}
		limit -= transferred;
		//try to add it to empty slots
		if (limit > 0 && item.getAmount() > 0) {
			for (int i = 0; i < inventory.getSize(); i++) {
				if (inventory.getItem(i) != null) {
					if (inventory.getItem(i).getTypeId() != 0) continue;
				}
				if (item.getAmount() <= limit) {
					transferred += item.getAmount();
					inventory.setItem(i, item.clone());
					item.setAmount(0);
				} else {
					ItemStack newitem = item.clone();
					newitem.setAmount(limit);
					transferred += limit;
					inventory.setItem(i, newitem);
					item.setAmount(item.getAmount() - limit);
				}
				break;
			}
		}
		return transferred;
	}
	public static int transfer(ItemStack from, ItemStack to, int limit) {
		if (limit == 0) return 0;
		final int max = getMaxSize(from);
		if (!canTransfer(from, to, max)) return 0;
		final int newamount, remainder, trans;
		if (from.getAmount() <= limit) {
			newamount = from.getAmount() + to.getAmount();
			remainder = 0;
		} else {
			newamount = limit + to.getAmount();
			remainder = from.getAmount() - limit;
		}
		if (newamount >= max) {
			trans = max - to.getAmount();
			to.setAmount(max);
			from.setAmount(newamount - max + remainder);
		} else {
			trans = newamount - to.getAmount();
			to.setAmount(newamount);
			from.setAmount(remainder);
		}
		return trans;
	}
	
	public static int getMaxSize(ItemStack stack) {
		if (stack == null) return 0;
		int max = stack.getMaxStackSize();
		if (max == -1) max = 64;
		return max;
	}
	
	public static boolean canTransfer(ItemStack from, Inventory to) {
		final int max = getMaxSize(from);
		for (ItemStack item : to.getContents()) {
			if (item == null) return true;
			if (item.getTypeId() == 0) return true;
			if (canTransfer(from, item, max)) return true;
		}
		return false;
	}
	public static boolean canTransfer(ItemStack from, ItemStack to) {
		return canTransfer(from, to, getMaxSize(from));
	}
	public static boolean canTransfer(ItemStack from, ItemStack to, final int maxstacksize) {
		if (from == null || to == null) return false;
		if (to.getTypeId() != from.getTypeId()) return false;
		if (to.getDurability() != from.getDurability()) return false;
		if (from.getAmount() <= 0) return false;
		if (from.getAmount() >= maxstacksize) return false;
		if (to.getAmount() >= maxstacksize) return false;
		return true;
	}
	
	public static boolean isMinecartItem(Item item) {
		return isMinecartItem(item.getItemStack().getType());
	}
	public static boolean isMinecartItem(Material type) {
		switch (type) {
		case MINECART :
		case POWERED_MINECART :
		case STORAGE_MINECART : return true;
		default : return false;
		}
	}
		
	public static Byte getData(Material type, String name) {
		try {
			return Byte.parseByte(name);
		} catch (NumberFormatException ex) {
			MaterialData dat = type.getNewData((byte) 0);
			if (dat instanceof TexturedMaterial) {
				TexturedMaterial tdat = (TexturedMaterial) dat;
				tdat.setMaterial(EnumUtil.parseMaterial(name, null));
			} else if (dat instanceof Wool) {
				Wool wdat = (Wool) dat;
				wdat.setColor(EnumUtil.parseDyeColor(name, null));
			} else if (dat instanceof Tree) {
				Tree tdat = (Tree) dat;
				tdat.setSpecies(EnumUtil.parseTreeSpecies(name, null));
			} else {
				return null;
			}
			return dat.getData();
		}
	}

	public static Inventory[] getChests(Block attached) {
		ArrayList<Inventory> invs = new ArrayList<Inventory>();
		Block c1, c2;
		for (BlockFace face : FaceUtil.axis) {
			c1 = attached.getRelative(face);
			if (c1.getType() != Material.CHEST) continue;
			invs.add(((Chest) c1.getState()).getInventory());
			for (BlockFace sface : FaceUtil.axis) { 
				c2 = c1.getRelative(sface);
				if (c2.getType() != Material.CHEST) continue;
				invs.add(((Chest) c2.getState()).getInventory());
			}
		}	
		return invs.toArray(new Inventory[0]);
	}

}
