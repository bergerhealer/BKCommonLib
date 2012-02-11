package com.bergerkiller.bukkit.common.utils;

import java.util.logging.Level;

import me.snowleo.bleedingmobs.BleedingMobs;
import net.minecraft.server.IInventory;
import net.minecraft.server.TileEntityChest;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
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
import com.bergerkiller.bukkit.common.MergedInventory;
import com.miykeal.showCaseStandalone.ShowCaseStandalone;
import com.narrowtux.showcase.Showcase;

public class ItemUtil {

	public static boolean isIgnored(Entity itementity) {
		if (!(itementity instanceof Item)) return true; 
		Item item = (Item) itementity;
		if (Common.isShowcaseEnabled) {
			try {
				// Placeholder for a possible new function
				// if (!Common.showCaseUseOldMode) {
				// 	try {
				// 		//TODO: USE NEWER VERSION
				// 	} catch (Throwable t) {
				// 		Common.showCaseUseOldMode = true;
				// 	}
				// }
				if (Common.showCaseUseOldMode) { 
					if (Showcase.instance.getItemByDrop(item) != null) return true;
				}
				
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
	
	public static net.minecraft.server.ItemStack getNative(ItemStack stack) {
		net.minecraft.server.ItemStack rval = ((CraftItemStack) stack).getHandle();
		if (rval == null) {
			stack.setTypeId(1); //force the creation of a new native itemstack
			rval = ((CraftItemStack) stack).getHandle();
			if (rval == null) {
				throw new RuntimeException("Native item is null when setting a valid type!");
			}
			rval.id = 0;
			rval.count = 0;
		}
		return rval;
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
	
	public static void setItem(Inventory inv, int index, ItemStack item) {
		if (item != null && (item.getAmount() == 0 || item.getTypeId() == 0)) {
			item = null;
		}
		inv.setItem(index, item);
	}
	
	/**
	 * Tests if the given ItemStack can be transferred to the Inventory
	 * @return The amount that could be transferred
	 */
	public static int testTransfer(ItemStack from, Inventory to) {
		return testTransfer(((CraftItemStack) from).getHandle(), 
				((CraftInventory) to).getInventory());
	}
	
	/**
	 * Tests if the given ItemStack can be transferred to the IInventory
	 * @return The amount that could be transferred
	 */
	public static int testTransfer(net.minecraft.server.ItemStack from, IInventory to) {
		if (from == null || from.count == 0 || from.id == 0) return 0;
		int olditemcount = from.count;
		int trans = 0;
		int tmptrans;
		for (net.minecraft.server.ItemStack item : to.getContents()) {
			tmptrans = testTransfer(from, item);
			from.count -= tmptrans;
			trans += tmptrans;
			if (from.count == 0) break;
		}
		from.count = olditemcount;
		return trans;
	}

	/**
	 * Tests if the two items can be merged
	 * @return The amount that could be transferred
	 */
	public static int testTransfer(ItemStack from, ItemStack to) {
		return testTransfer(((CraftItemStack) from).getHandle(), ((CraftItemStack) to).getHandle());
	}
	
	/**
	 * Tests if the two items can be merged
	 * @return The amount that could be transferred
	 */
	public static int testTransfer(net.minecraft.server.ItemStack from, net.minecraft.server.ItemStack to) {
		final int trans;
		if (from == null) {
			trans = 0;
		} else if (to == null) {
			trans = Math.min(from.count, from.getMaxStackSize());
		} else if (to.id == from.id && to.getData() == from.getData()) {
			trans = Math.min(from.count, from.getMaxStackSize() - to.count);
		} else {
			trans = 0;
		}
		return trans;
	}
		
	/**
	 * Transfers all ItemStacks from one Inventory to another
	 * @param from: The Inventory to take ItemStacks from
	 * @param to: The Inventory to transfer to
	 * @param maxAmount: The maximum amount of items to transfer
	 * @param parser: The item parser used to set what items to transfer. Can be null.
	 * @return The amount of items that got transferred
	 */
	public static int transfer(Inventory from, Inventory to, ItemParser parser, int maxAmount) {
		if (maxAmount == 0) return 0;
		ItemStack item;
		int transferred = 0;
		for (int i = 0; i < from.getSize(); i++) {
			item = from.getItem(i);
			if (item == null) continue;
			if (parser != null && !parser.match(item)) continue;
			transferred += transfer(item, to, maxAmount - transferred);
			setItem(from, i, item);
		}
		return transferred;
	}
	
	/**
	 * Transfers the given ItemStack to multiple slots in the Inventory
	 * @param from: The ItemStack to transfer
	 * @param to: The Inventory to transfer to
	 * @param maxAmount: The maximum amount of the item to transfer
	 * @return The amount of the item that got transferred
	 */
	public static int transfer(ItemStack from, Inventory to, int maxAmount) {
		if (maxAmount == 0) return 0;
		int transferred = 0;
		int tmptrans;
		if (from == null) return transferred;
		if (from.getTypeId() == 0) return transferred;
		if (from.getAmount() < 1) return transferred;

		//try to add to already existing items
		for (ItemStack toitem : to.getContents()) {
			if (toitem == null) continue;
			if (toitem.getTypeId() == from.getTypeId()) {
				if (toitem.getDurability() == from.getDurability()) {
					tmptrans = transfer(from, toitem, maxAmount);
					maxAmount -= tmptrans;
					transferred += tmptrans;
					//everything done?
					if (maxAmount == 0 || from.getAmount() == 0) break;
				}
			}
		}
		
		//try to add it to empty slots
		if (maxAmount > 0 && from.getAmount() > 0) {
			ItemStack toitem;
			for (int i = 0; i < to.getSize(); i++) {
				toitem = to.getItem(i);
				if (toitem.getTypeId() == 0) {
					tmptrans = transfer(from, toitem, maxAmount);
					maxAmount -= tmptrans;
					transferred += tmptrans;
					setItem(to, i, toitem);
					//everything done?
					if (maxAmount == 0 || from.getAmount() == 0) break;
				}
			}
		}
		return transferred;
	}
		
	/**
	 * Merges two ItemStacks
	 * @param from: The ItemStack to merge
	 * @param to: The receiving ItemStack
	 * @param maxAmount: The maximum amount of the item to transfer
	 * @return The amount of the item that got transferred
	 */
	public static int transfer(ItemStack from, ItemStack to, int maxAmount) {
		net.minecraft.server.ItemStack nmsFrom = getNative(from);
		net.minecraft.server.ItemStack nmsTo = getNative(to);
		int trans = transfer(nmsFrom, nmsTo, maxAmount);
		//make sure the native items are null if they are empty
		if (nmsFrom.count == 0) from.setTypeId(0);
		if (nmsTo.count == 0) to.setTypeId(0);
		return trans;
	}
	/**
	 * Merges two native ItemStacks
	 * @param from: The ItemStack to merge
	 * @param to: The receiving ItemStack
	 * @param maxAmount: The maximum amount of the item to transfer
	 * @return The amount of the item that got transferred
	 */
	public static int transfer(net.minecraft.server.ItemStack from, net.minecraft.server.ItemStack to, int maxCount) {
		maxCount = Math.min(maxCount, from.getMaxStackSize());
		if (maxCount == 0) {
			//nothing to stack
			return 0;
		} else if (to.id== 0 || to.count == 0) {
			//fully copy data over
			to.id = from.id;
			to.setData(from.getData());
			final int transferred = Math.min(maxCount, from.count);
			to.count = transferred;
			from.count -= transferred;
			return transferred;
		} else if (maxCount == 1) {
			//can't stack any further
			return 0;
		} else if (to.id != from.id || to.getData() != from.getData()) {
			//different items - can't stack
			return 0;
		} else {
			//stack the items
			final int transferred = Math.min(maxCount - to.count, from.count);
			to.count += transferred;
			from.count -= transferred;
			return transferred;
		}
	}

	/**
	 * Tries to transfer a single item from the Inventory to the ItemStack
	 * @param from: The Inventory to take an ItemStack from
	 * @param to: The ItemStack to merge the item taken
	 * @param maxAmount: The maximum amount of items to transfer
	 * @param parser: The item parser used to set what item to transfer if the receiving item is empty. Can be null.
	 * @return The amount of the item that got transferred
	 */
	public static int transfer(Inventory from, ItemStack to, ItemParser parser, int maxAmount) {
		int trans = 0;
		boolean hasdata = true;
		boolean all = false;
		for (int i = 0; i < from.getSize(); i++) {
			ItemStack item = from.getItem(i);
			if (item == null || item.getTypeId() == 0 || item.getAmount() < 1) continue;
			if (to.getTypeId() == 0) {
				//take over entire item - or use parser
				if (parser != null && parser.hasType())  {
					to.setTypeId(parser.getTypeId());
					if (parser.hasData()) {
						to.setDurability(parser.getData());
					} else {
						hasdata = false;
					}
				} else {
					to.setTypeId(item.getTypeId());
					to.setDurability(item.getDurability());
				}
				all = true;
			}
			if (item.getTypeId() == to.getTypeId()) {
				if (!hasdata) {
					to.setDurability(item.getDurability());
					hasdata = true;
				}
				if (item.getDurability() == to.getDurability()) {
					if (all) {
						//we need to fake zero count
						net.minecraft.server.ItemStack nmsTo = getNative(to);
						nmsTo.count = 0;
						trans += transfer(getNative(item), nmsTo, maxAmount - trans);
						setItem(from, i, item);
						if (nmsTo.count > 0) {
							all = false;
						}
					} else {
						trans += transfer(item, to, maxAmount - trans);
						setItem(from, i, item);
						if (maxAmount == trans) break;
					}
				}
			}
		}
		return trans;
	}

	public static net.minecraft.server.ItemStack transferItem(IInventory inventory, ItemParser parser, int limit) {
		net.minecraft.server.ItemStack rval = null;
		for (int i = 0; i < inventory.getSize(); i++) {
			net.minecraft.server.ItemStack item = inventory.getItem(i);
			if (item == null) continue;
			if (parser.match(item)) {
				if (item.count <= limit) {
					limit -= item.count;
					if (rval == null) {
						rval = item;
					} else {
						rval.count += item.count;
					}
				}
			}
		}
		return rval;
	}

	public static int getMaxSize(ItemStack stack) {
		if (stack == null) return 0;
		int max = stack.getMaxStackSize();
		return max == -1 ? 64 : max;
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

	public static Inventory getChestInventory(TileEntityChest[] chests) {
		if (chests == null || chests.length == 0) return null;
		return MergedInventory.convert(chests);
	}

	public static Inventory getChestInventory(Block middle, int radius) {
		return getChestInventory(BlockUtil.getChestTiles(middle, radius));
	}
	public static Inventory getChestInventory(Block chest) {
		return getChestInventory(BlockUtil.getChestTiles(chest));
	}

}
