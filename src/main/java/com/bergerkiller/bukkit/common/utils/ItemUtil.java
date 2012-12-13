package com.bergerkiller.bukkit.common.utils;

import net.minecraft.server.v1_4_5.EntityItem;
import net.minecraft.server.v1_4_5.IInventory;
import net.minecraft.server.v1_4_5.Item;
import net.minecraft.server.v1_4_5.ItemStack;
import net.minecraft.server.v1_4_5.NBTTagCompound;
import net.minecraft.server.v1_4_5.NBTTagList;

import org.bukkit.craftbukkit.v1_4_5.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_4_5.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;

import com.bergerkiller.bukkit.common.items.ItemParser;
import com.bergerkiller.bukkit.common.natives.IInventoryBaseImpl;

public class ItemUtil {

	public static void transfer(IInventory from, IInventory to) {
		ItemStack[] items = from.getContents();
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				to.setItem(i, items[i].cloneItemStack());
			}
		}
		for (int i = 0; i < items.length; i++) {
			from.setItem(i, null);
		}
	}

	public static void setItem(Inventory inv, int index, org.bukkit.inventory.ItemStack item) {
		org.bukkit.inventory.ItemStack itemToSet = item;
		if (LogicUtil.nullOrEmpty(itemToSet)) {
			itemToSet = null;
		}
		inv.setItem(index, item);
	}

	/**
	 * Kills the old item and spawns a new item in it's place
	 * 
	 * @param item to respawn
	 * @return Respawned item
	 */
	public static org.bukkit.entity.Item respawnItem(org.bukkit.entity.Item bitem) {
		bitem.remove();
		EntityItem item = NativeUtil.getNative(bitem);
		EntityItem newItem = new EntityItem(item.world, item.locX, item.locY, item.locZ, item.itemStack);
		newItem.fallDistance = item.fallDistance;
		newItem.fireTicks = item.fireTicks;
		newItem.pickupDelay = item.pickupDelay;
		newItem.motX = item.motX;
		newItem.motY = item.motY;
		newItem.motZ = item.motZ;
		newItem.age = item.age;
		newItem.world.addEntity(newItem);
		return NativeUtil.getItem(newItem);
	}

	/**
	 * Gets the total item count of a given type and data
	 * 
	 * @param inventory to look in
	 * @param typeid of the items to look for, null for any item
	 * @param data of the items to look for, null for any data
	 * @return Amount of items in the inventory
	 */
	public static org.bukkit.inventory.ItemStack findItem(Inventory inventory, int typeid, Integer data) {
		ItemStack item = findItem(NativeUtil.getNative(inventory), typeid, data);
		return item == null ? null : NativeUtil.getItemStack(item);
	}

	/**
	 * Gets the total item count of a given type and data
	 * 
	 * @param inventory to look in
	 * @param typeid of the items to look for, null for any item
	 * @param data of the items to look for, null for any data
	 * @return Amount of items in the inventory
	 */
	public static ItemStack findItem(IInventory inventory, int typeid, Integer data) {
		ItemStack rval = null;
		Integer itemData = data;
		for (ItemStack item : inventory.getContents()) {
			if (item == null || item.id != typeid) {
				continue;
			}
			if (itemData == null) {
				itemData = item.getData();
			} else if (itemData != item.getData()) {
				continue;
			}
			// addition
			if (rval == null) {
				rval = item.cloneItemStack();
			} else {
				rval.count += item.count;
			}
		}
		return rval;
	}

	/**
	 * Gets the total item count of a given type and data
	 * 
	 * @param inventory to look in
	 * @param typeid of the items to look for, null for any item
	 * @param data of the items to look for, null for any data
	 * @return Amount of items in the inventory
	 */
	public static int getItemCount(Inventory inventory, Integer typeid, Integer data) {
		if (typeid == null) {
			int count = 0;
			for (org.bukkit.inventory.ItemStack item : inventory.getContents()) {
				if (!LogicUtil.nullOrEmpty(item)) {
					count += item.getAmount();
				}
			}
			return count;
		} else {
			org.bukkit.inventory.ItemStack rval = findItem(inventory, typeid, data);
			return rval == null ? 0 : rval.getAmount();
		}
	}

	/**
	 * Gets the total item count of a given type and data
	 * 
	 * @param inventory to look in
	 * @param typeid of the items to look for, null for any item
	 * @param data of the items to look for, null for any data
	 * @return Amount of items in the inventory
	 */
	public static int getItemCount(IInventory inventory, Integer typeid, Integer data) {
		if (typeid == null) {
			int count = 0;
			for (ItemStack item : inventory.getContents()) {
				if (!LogicUtil.nullOrEmpty(item)) {
					count += item.count;
				}
			}
			return count;
		} else {
			ItemStack rval = findItem(inventory, typeid, data);
			return rval == null ? 0 : rval.count;
		}
	}

	public static void removeItem(Inventory inventory, org.bukkit.inventory.ItemStack item) {
		removeItem(inventory, item.getTypeId(), (int) item.getDurability(), item.getAmount());
	}

	public static void removeItem(Inventory inventory, int itemid, Integer data, int count) {
		removeItem(NativeUtil.getNative(inventory), itemid, data, count);
	}

	public static void removeItem(IInventory inventory, ItemStack item) {
		removeItem(inventory, item.id, item.getData() == -1 ? null : item.getData(), item.count);
	}

	public static void removeItem(IInventory inventory, int itemid, Integer data, int count) {
		Integer itemData = data;
		int countToRemove = count;
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if (item == null || item.id != itemid || (itemData != null && item.getData() != itemData)) {
				continue;
			}
			if (item.count < countToRemove) {
				countToRemove -= item.count;
				inventory.setItem(i, null);
			} else {
				item.count -= countToRemove;
				countToRemove = 0;
				inventory.setItem(i, item.count == 0 ? null : item);
				break;
			}
		}
	}

	/**
	 * Creates a new itemstack array containing items not referencing the input item stacks
	 * 
	 * @param input array to process
	 * @return Cloned item stack array
	 */
	public static org.bukkit.inventory.ItemStack[] cloneItems(org.bukkit.inventory.ItemStack[] input) {
		org.bukkit.inventory.ItemStack[] cloned = new org.bukkit.inventory.ItemStack[input.length];
		for (int i = 0; i < cloned.length; i++) {
			cloned[i] = input[i] == null ? null : input[i].clone();
		}
		return cloned;
	}

	/**
	 * Creates a new itemstack array containing items not referencing the input item stacks
	 * 
	 * @param input array to process
	 * @return Cloned item stack array
	 */
	public static ItemStack[] cloneItems(ItemStack[] input) {
		ItemStack[] cloned = new ItemStack[input.length];
		for (int i = 0; i < cloned.length; i++) {
			cloned[i] = input[i] == null ? null : input[i].cloneItemStack();
		}
		return cloned;
	}

	/**
	 * Tests if the given ItemStacks can be transferred to the Inventory
	 * 
	 * @return Whether it was possible
	 */
	public static boolean testTransfer(ItemStack[] from, IInventory to) {
		return testTransfer(from, to.getContents());
	}

	/**
	 * Tests if the given ItemStacks can be transferred to the Inventory
	 * 
	 * @return Whether it was possible
	 */
	public static boolean testTransfer(ItemStack[] from, ItemStack[] to) {
		Inventory invto = new IInventoryBaseImpl(cloneItems(to)).getInventory();
		for (ItemStack nitem : cloneItems(from)) {
			org.bukkit.inventory.ItemStack item = NativeUtil.getItemStack(nitem);
			transfer(item, invto, Integer.MAX_VALUE);
			if (item.getAmount() > 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests if the given ItemStack can be transferred to the Inventory
	 * 
	 * @return The amount that could be transferred
	 */
	public static int testTransfer(org.bukkit.inventory.ItemStack from, Inventory to) {
		return testTransfer(NativeUtil.getNative(from), ((CraftInventory) to).getInventory());
	}

	/**
	 * Tests if the given ItemStack can be transferred to the IInventory
	 * 
	 * @return The amount that could be transferred
	 */
	public static int testTransfer(ItemStack from, IInventory to) {
		if (LogicUtil.nullOrEmpty(from)) {
			return 0;
		}
		int olditemcount = from.count;
		int trans = 0;
		int tmptrans;
		for (ItemStack item : to.getContents()) {
			tmptrans = testTransfer(from, item);
			from.count -= tmptrans;
			trans += tmptrans;
			if (LogicUtil.nullOrEmpty(from)) {
				break;
			}
		}
		from.count = olditemcount;
		return trans;
	}

	/**
	 * Tests if the two items can be merged
	 * 
	 * @return The amount that could be transferred
	 */
	public static int testTransfer(org.bukkit.inventory.ItemStack from, org.bukkit.inventory.ItemStack to) {
		return testTransfer(NativeUtil.getNative(from), NativeUtil.getNative(to));
	}

	/**
	 * Tests if the two items can be merged
	 * 
	 * @return The amount that could be transferred
	 */
	public static int testTransfer(ItemStack from, ItemStack to) {
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
	 * 
	 * @param from The Inventory to take ItemStacks from
	 * @param to The Inventory to transfer to
	 * @param maxAmount The maximum amount of items to transfer
	 * @param parser The item parser used to set what items to transfer. Can be null.
	 * @return The amount of items that got transferred
	 */
	public static int transfer(Inventory from, Inventory to, ItemParser parser, int maxAmount) {
		int transferred = 0;
		if (maxAmount > 0) {
			for (int i = 0; i < from.getSize(); i++) {
				org.bukkit.inventory.ItemStack item = from.getItem(i);
				if (item == null || (parser != null && !parser.match(item))) {
					continue;
				}
				transferred += transfer(item, to, maxAmount - transferred);
				setItem(from, i, item);
			}
		}
		return transferred;
	}

	/**
	 * Transfers the given ItemStack to multiple slots in the Inventory
	 * 
	 * @param from The ItemStack to transfer
	 * @param to The Inventory to transfer to
	 * @param maxAmount The maximum amount of the item to transfer
	 * @return The amount of the item that got transferred
	 */
	public static int transfer(org.bukkit.inventory.ItemStack from, Inventory to, int maxAmount) {
		if (maxAmount <= 0 || from == null || from.getTypeId() == 0 || from.getAmount() < 1) {
			return 0;
		}

		int transferred = 0;
		int tmptrans;
		int amountToTransfer = maxAmount;

		// try to add to already existing items
		for (int i = 0; i < to.getSize(); i++) {
			org.bukkit.inventory.ItemStack toitem = to.getItem(i);
			if (toitem == null)
				continue;
			if (toitem.getTypeId() == from.getTypeId() && toitem.getDurability() == from.getDurability()) {
				tmptrans = transfer(from, toitem, amountToTransfer);
				amountToTransfer -= tmptrans;
				transferred += tmptrans;
				setItem(to, i, toitem);
				// everything done?
				if (amountToTransfer <= 0 || from.getAmount() == 0){
					break;
				}
			}
		}

		// try to add it to empty slots
		if (amountToTransfer > 0 && from.getAmount() > 0) {
			org.bukkit.inventory.ItemStack toitem;
			for (int i = 0; i < to.getSize(); i++) {
				toitem = to.getItem(i);
				if (toitem == null) {
					toitem = CraftItemStack.asCraftCopy(new org.bukkit.inventory.ItemStack(0));
				}
				if (toitem.getTypeId() == 0) {
					tmptrans = transfer(from, toitem, amountToTransfer);
					amountToTransfer -= tmptrans;
					transferred += tmptrans;
					setItem(to, i, toitem);
					// everything done?
					if (amountToTransfer <= 0 || from.getAmount() == 0) {
						break;
					}
				}
			}
		}
		return transferred;
	}

	/**
	 * Merges two ItemStacks
	 * 
	 * @param from The ItemStack to merge
	 * @param to The receiving ItemStack
	 * @param maxAmount The maximum amount of the item to transfer
	 * @return The amount of the item that got transferred
	 */
	public static int transfer(org.bukkit.inventory.ItemStack from, org.bukkit.inventory.ItemStack to, int maxAmount) {
		ItemStack nmsFrom = NativeUtil.getNative(from);
		ItemStack nmsTo = NativeUtil.getNative(to);
		int trans = transfer(nmsFrom, nmsTo, maxAmount);
		// make sure the native items are null if they are empty
		if (nmsFrom.count == 0) {
			from.setTypeId(0);
		}
		if (nmsTo.count == 0) {
			to.setTypeId(0);
		}
		return trans;
	}

	/**
	 * Merges two native ItemStacks
	 * 
	 * @param from The ItemStack to merge
	 * @param to The receiving ItemStack
	 * @param maxAmount The maximum amount of the item to transfer
	 * @return The amount of the item that got transferred
	 */
	public static int transfer(ItemStack from, ItemStack to, int maxCount) {
		int amountToTransfer = Math.min(maxCount, from.count);
		if (!LogicUtil.nullOrEmpty(to)) {
			amountToTransfer = Math.min(amountToTransfer, to.getMaxStackSize() - to.count);
		}
		if (amountToTransfer <= 0) {
			// nothing to stack
			return 0;
		} else if (to.id == 0 || to.count == 0) {
			// fully copy data over
			to.id = from.id;
			to.setData(from.getData());
			// enchantments
			if (from.hasEnchantments()) {
				if (to.tag == null) {
					to.tag = new NBTTagCompound();
				}
				to.tag.set("ench", from.getEnchantments().clone());
			}
			to.count = amountToTransfer;
			from.count -= amountToTransfer;
			return amountToTransfer;
		} else if (to.id != from.id || to.getData() != from.getData()) {
			// different items - can't stack
			return 0;
		} else {
			// can we stack (enchantments)
			NBTTagList efrom = from.getEnchantments();
			NBTTagList eto = to.getEnchantments();
			if (efrom != null && eto != null && efrom.size() == eto.size()) {
				// same enchantments?
				if (!hasEnchantments(efrom, eto) || !hasEnchantments(eto, efrom)) {
					return 0;
				}
			} else if (efrom != null || eto != null) {
				return 0;
			}
			// stack the items
			to.count += amountToTransfer;
			from.count -= amountToTransfer;
			return amountToTransfer;
		}
	}

	/**
	 * Checks if a given list of enchantments contains all of the enchantments specified
	 * 
	 * @param enchantments to look int
	 * @param enchantmentsToCheck to evaluate against
	 * @return True if all enchantments are contained, False if not
	 */
	public static boolean hasEnchantments(NBTTagList enchantments, NBTTagList enchantmentsToCheck) {
		try {
			for (int i = 0; i < enchantmentsToCheck.size(); i++) {
				if (!hasEnchantment(enchantments, (NBTTagCompound) enchantmentsToCheck.get(i))) {
					return false;
				}
			}
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Checks whether a NBT list has a given enchantment
	 * 
	 * @param enchantments to look in
	 * @param enchantment data to look for
	 * @return True if such an enchantment is contained, False if not
	 */
	public static boolean hasEnchantment(NBTTagList enchantments, NBTTagCompound enchantment) {
		return hasEnchantment(enchantments, enchantment.getShort("id"), enchantment.getShort("lvl"));
	}

	/**
	 * Checks whether a NBT list has a given enchantment
	 * 
	 * @param enchantments to look in
	 * @param id of the enchantment
	 * @param level of the enchantment
	 * @return True if such an enchantment is contained, False if not
	 */
	public static boolean hasEnchantment(NBTTagList enchantments, short id, short level) {
		NBTTagCompound comp;
		for (int i = 0; i < enchantments.size(); i++) {
			try {
				comp = (NBTTagCompound) enchantments.get(i);
				if (comp.getShort("id") == id && comp.getShort("lvl") == level) {
					return true;
				}
			} catch (Exception ex) {
			}
		}
		return false;
	}

	/**
	 * Tries to transfer a single item from the Inventory to the ItemStack
	 * 
	 * @param from The Inventory to take an ItemStack from
	 * @param to The ItemStack to merge the item taken
	 * @param maxAmount The maximum amount of items to transfer
	 * @param parser The item parser used to set what item to transfer if the receiving item is empty. Can be null.
	 * @return The amount of the item that got transferred
	 */
	public static int transfer(Inventory from, org.bukkit.inventory.ItemStack to, ItemParser parser, int maxAmount) {
		int trans = 0;
		boolean hasdata = true;
		boolean all = false;
		for (int i = 0; i < from.getSize(); i++) {
			org.bukkit.inventory.ItemStack item = from.getItem(i);
			if (item == null || item.getTypeId() == 0 || item.getAmount() < 1) {
				continue;
			}
			if (to.getTypeId() == 0) {
				// take over entire item - or use parser
				if (parser != null && parser.hasType()) {
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
				NativeUtil.getNative(to).count = 0;
				all = true;
			}
			if (item.getTypeId() == to.getTypeId()) {
				if (!hasdata) {
					to.setDurability(item.getDurability());
					hasdata = true;
				}
				if (item.getDurability() == to.getDurability()) {
					if (all) {
						// we need to fake zero count
						trans += transfer(item, to, maxAmount - trans);
						if (to.getAmount() > 0) {
							setItem(from, i, item);
							all = false;
						}
					} else {
						trans += transfer(item, to, maxAmount - trans);
						setItem(from, i, item);
						if (maxAmount == trans) {
							break;
						}
					}
				}
			}
		}
		return trans;
	}

	/**
	 * Gets the max stacking size for a given item
	 * 
	 * @param stack to get the max stacked size
	 * @return max stacking size
	 */
	public static int getMaxSize(ItemStack stack) {
		if (stack == null) {
			return 0;
		}
		Item item = stack.getItem();
		if (item == null) {
			return 0;
		} else {
			return item.getMaxStackSize();
		}
	}

	/**
	 * Gets the max stacking size for a given item
	 * 
	 * @param stack to get the max stacked size
	 * @return max stacking size
	 */
	public static int getMaxSize(org.bukkit.inventory.ItemStack stack) {
		if (stack == null) {
			return 0;
		} else {
			return getMaxSize(NativeUtil.getNative(stack));
		}
	}
}
