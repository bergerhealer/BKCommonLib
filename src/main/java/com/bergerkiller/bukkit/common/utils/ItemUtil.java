package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.inventory.InventoryBaseImpl;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.generated.net.minecraft.server.EntityItemHandle;
import com.bergerkiller.generated.net.minecraft.server.ItemHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import com.bergerkiller.reflection.net.minecraft.server.NMSItemStack;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftItemStack;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Contains item stack, item and inventory utilities
 */
public class ItemUtil {

    /**
     * Tests if the given ItemStacks can be fully transferred to another array
     * of ItemStacks
     *
     * @param from ItemStack source array
     * @param to destination Inventory
     * @return True if full transfer was possible, False if not
     */
    public static boolean canTransferAll(org.bukkit.inventory.ItemStack[] from, Inventory to) {
        return canTransferAll(from, to.getContents());
    }

    /**
     * Tests if the given ItemStacks can be fully transferred to another array
     * of ItemStacks
     *
     * @param from ItemStack source array
     * @param to ItemStack destination array
     * @return True if full transfer was possible, False if not
     */
    public static boolean canTransferAll(org.bukkit.inventory.ItemStack[] from, org.bukkit.inventory.ItemStack[] to) {
        Inventory invto = new InventoryBaseImpl(to, true);
        for (org.bukkit.inventory.ItemStack item : cloneItems(from)) {
            transfer(item, invto, Integer.MAX_VALUE);
            if (!LogicUtil.nullOrEmpty(item)) {
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
        if (LogicUtil.nullOrEmpty(from)) {
            return 0;
        }
        int startAmount = from.getAmount();
        int fromAmount = startAmount;
        for (org.bukkit.inventory.ItemStack item : to.getContents()) {
            if (LogicUtil.nullOrEmpty(item)) {
                // Full transfer is possible (empty slot)
                fromAmount -= getMaxSize(from);
            } else if (equalsIgnoreAmount(from, item)) {
                // Stack transfer is possible (same item slot)
                fromAmount -= getMaxSize(item) - item.getAmount();
            }
            if (fromAmount <= 0) {
                // All items could be transferred!
                return startAmount;
            }
        }
        return startAmount - fromAmount;
    }

    /**
     * Tests if the two items can be merged
     *
     * @return The amount that could be transferred
     */
    public static int testTransfer(org.bukkit.inventory.ItemStack from, org.bukkit.inventory.ItemStack to) {
        if (LogicUtil.nullOrEmpty(from)) {
            return 0;
        }
        if (LogicUtil.nullOrEmpty(to)) {
            return Math.min(from.getAmount(), getMaxSize(from));
        }
        if (equalsIgnoreAmount(from, to)) {
            return Math.min(from.getAmount(), getMaxSize(to) - to.getAmount());
        }
        return 0;
    }

    /**
     * Transfers all ItemStacks from one Inventory to another
     *
     * @param from The Inventory to take ItemStacks from
     * @param to The Inventory to transfer to
     * @param maxAmount The maximum amount of items to transfer, -1 for infinite
     * @param parser The item parser used to set what items to transfer. Can be
     * null.
     * @return The amount of items that got transferred
     */
    public static int transfer(Inventory from, Inventory to, ItemParser parser, int maxAmount) {
        int startAmount = maxAmount < 0 ? Integer.MAX_VALUE : maxAmount;
        int amountToTransfer = startAmount;
        int tmptrans;
        for (int i = 0; i < from.getSize() && amountToTransfer > 0; i++) {
            org.bukkit.inventory.ItemStack item = from.getItem(i);
            if (LogicUtil.nullOrEmpty(item) || (parser != null && !parser.match(item))) {
                continue;
            }
            tmptrans = transfer(item, to, amountToTransfer);
            if (tmptrans > 0) {
                amountToTransfer -= tmptrans;
                from.setItem(i, item);
            }
        }
        return startAmount - amountToTransfer;
    }

    /**
     * Transfers the given ItemStack to multiple slots in the Inventory
     *
     * @param from The ItemStack to transfer
     * @param to The Inventory to transfer to
     * @param maxAmount The maximum amount of the item to transfer, -1 for
     * infinite
     * @return The amount of the item that got transferred
     */
    public static int transfer(org.bukkit.inventory.ItemStack from, Inventory to, int maxAmount) {
        int startAmount = maxAmount < 0 ? Integer.MAX_VALUE : maxAmount;
        if (startAmount == 0 || LogicUtil.nullOrEmpty(from)) {
            return 0;
        }
        int tmptrans;
        int amountToTransfer = startAmount;

        // try to stack to already existing items
        org.bukkit.inventory.ItemStack toitem;
        for (int i = 0; i < to.getSize(); i++) {
            toitem = to.getItem(i);
            if (!LogicUtil.nullOrEmpty(toitem)) {
                tmptrans = transfer(from, toitem, amountToTransfer);
                if (tmptrans > 0) {
                    amountToTransfer -= tmptrans;
                    to.setItem(i, toitem);
                    // everything done?
                    if (amountToTransfer <= 0 || LogicUtil.nullOrEmpty(from)) {
                        break;
                    }
                }
            }
        }

        // try to add it to empty slots
        if (amountToTransfer > 0 && from.getAmount() > 0) {
            for (int i = 0; i < to.getSize(); i++) {
                toitem = to.getItem(i);
                if (LogicUtil.nullOrEmpty(toitem)) {
                    toitem = emptyItem();
                    // Transfer
                    tmptrans = transfer(from, toitem, amountToTransfer);
                    if (tmptrans > 0) {
                        amountToTransfer -= tmptrans;
                        to.setItem(i, toitem);
                        // everything done?
                        if (amountToTransfer <= 0 || LogicUtil.nullOrEmpty(from)) {
                            break;
                        }
                    }
                }
            }
        }
        return startAmount - amountToTransfer;
    }

    /**
     * Tries to transfer items from the Inventory to the ItemStack
     *
     * @param from The Inventory to take an ItemStack from
     * @param to The ItemStack to merge the item taken
     * @param parser The item parser used to set what item to transfer if the
     * receiving item is empty. Can be null.
     * @param maxAmount The maximum amount of the item to transfer, -1 for
     * infinite
     * @return The amount of the item that got transferred
     */
    public static int transfer(Inventory from, org.bukkit.inventory.ItemStack to, ItemParser parser, int maxAmount) {
        int startAmount = maxAmount < 0 ? Integer.MAX_VALUE : maxAmount;
        int amountToTransfer = startAmount;
        for (int i = 0; i < from.getSize() && amountToTransfer > 0; i++) {
            org.bukkit.inventory.ItemStack item = from.getItem(i);
            if (LogicUtil.nullOrEmpty(item)) {
                continue;
            }
            if (LogicUtil.nullOrEmpty(to)) {
                // Parser matching
                if (parser != null && !parser.match(item)) {
                    continue;
                }
                // Set item info to this item
                transferInfo(item, to);
            }
            amountToTransfer -= transfer(item, to, amountToTransfer);
            from.setItem(i, item);
        }
        return startAmount - amountToTransfer;
    }

    /**
     * Merges two ItemStacks together<br>
     * - If from is empty or null, no transfer happens<br>
     * - If to is null, no transfer happens<br>
     * - If to is empty, full transfer occurs
     *
     * @param from The ItemStack to merge
     * @param to The receiving ItemStack
     * @param maxAmount The maximum amount of the item to transfer, -1 for
     * infinite
     * @return The amount of the item that got transferred
     */
    public static int transfer(org.bukkit.inventory.ItemStack from, org.bukkit.inventory.ItemStack to, int maxAmount) {
        if (LogicUtil.nullOrEmpty(from) || to == null) {
            return 0;
        }
        int amountToTransfer = Math.min(maxAmount < 0 ? Integer.MAX_VALUE : maxAmount, from.getAmount());

        // Transfering to an empty item, don't bother doing any stacking logic
        if (LogicUtil.nullOrEmpty(to)) {
            // Limit amount by maximum of the from item
            amountToTransfer = Math.min(amountToTransfer, getMaxSize(from));
            if (amountToTransfer <= 0) {
                return 0;
            }
            // Transfer item information
            transferInfo(from, to);
            // Transfer the amount
            to.setAmount(amountToTransfer);
            subtractAmount(from, amountToTransfer);
            return amountToTransfer;
        }

        // Can we stack?
        amountToTransfer = Math.min(amountToTransfer, getMaxSize(to) - to.getAmount());
        if (amountToTransfer <= 0 || !equalsIgnoreAmount(from, to)) {
            return 0;
        }

        // From and to are equal, we can now go ahead and stack them
        addAmount(to, amountToTransfer);
        subtractAmount(from, amountToTransfer);
        return amountToTransfer;
    }

    /**
     * Transfers the item type, data and enchantments from one item stack to the
     * other
     *
     * @param from which Item Stack to read the info
     * @param to which Item Stack to transfer the info to
     */
    @SuppressWarnings("deprecation")
    public static void transferInfo(org.bukkit.inventory.ItemStack from, org.bukkit.inventory.ItemStack to) {
        // Transfer type, durability and any other remaining metadata information
        to.setTypeId(from.getTypeId());
        to.setDurability(from.getDurability());
        setMetaTag(to, LogicUtil.clone(getMetaTag(from)));
    }

    /**
     * Checks whether two item stacks equal, while ignoring the item amounts
     *
     * @param item1 to check
     * @param item2 to check
     * @return True if the items have the same type, data and enchantments,
     * False if not
     */
    public static boolean equalsIgnoreAmount(org.bukkit.inventory.ItemStack item1, org.bukkit.inventory.ItemStack item2) {
        if (item1 == null || item2 == null) {
            return false;
        }
        if (item1.getType() != item2.getType() || MaterialUtil.getRawData(item1) != MaterialUtil.getRawData(item2)) {
            return false;
        }
        // Metadata checks
        boolean hasMeta = hasMetaTag(item1);
        if (hasMeta != hasMetaTag(item2)) {
            return false;
        } else if (!hasMeta) {
            // No further data to test
            return true;
        }
        if (!item1.getItemMeta().equals(item2.getItemMeta())) {
            return false;
        }

        // Not included in metadata checks: Item attributes (Bukkit needs to update)
        CommonTagList item1Attr = getMetaTag(item1).get("AttributeModifiers", CommonTagList.class);
        CommonTagList item2Attr = getMetaTag(item2).get("AttributeModifiers", CommonTagList.class);
        return LogicUtil.bothNullOrEqual(item1Attr, item2Attr);
    }

    /**
     * Removes certain kinds of items from an inventory
     *
     * @param inventory to remove items from
     * @param item signature of the items to remove
     */
    public static void removeItems(Inventory inventory, org.bukkit.inventory.ItemStack item) {
        removeItems(inventory, item.getType(), MaterialUtil.getRawData(item), item.getAmount());
    }

    /**
     * Removes certain kinds of items from an inventory
     *
     * @param inventory to remove items from
     * @param itemid of the items to remove
     * @param data of the items to remove, -1 for any data
     * @param amount of items to remove, -1 for infinite amount
     */
    public static void removeItems(Inventory inventory, Material type, int data, int amount) {
        int countToRemove = amount < 0 ? Integer.MAX_VALUE : amount;
        for (int i = 0; i < inventory.getSize(); i++) {
            org.bukkit.inventory.ItemStack item = inventory.getItem(i);
            if (LogicUtil.nullOrEmpty(item) || (item.getType() != type) || (data != -1 && MaterialUtil.getRawData(item) != data)) {
                continue;
            }
            if (item.getAmount() <= countToRemove) {
                countToRemove -= item.getAmount();
                inventory.setItem(i, null);
            } else {
                subtractAmount(item, countToRemove);
                countToRemove = 0;
                inventory.setItem(i, item);
                break;
            }
        }
    }

    /**
     * Obtains an empty item stack that allows mutual changes<br>
     * This is a CraftItemStack with a NMS ItemStack as buffer
     *
     * @return Empty item stack
     */
    public static org.bukkit.inventory.ItemStack emptyItem() {
        return createItem(Material.AIR, 0, 0);
    }

    /**
     * Creates a new ItemStack that is guaranteed to be a CraftItemStack with a valid NMS ItemStack handle.
     * If the original item is a CraftItemStack, the handle is re-used.
     * 
     * @param item to use as input
     */
    public static org.bukkit.inventory.ItemStack createItem(ItemStack item) {
        return CraftItemStackHandle.asCraftCopy(item);
    }

    /**
     * Creates a new ItemStack that is guaranteed to be a CraftItemStack with a valid NMS ItemStack handle
     * 
     * @param type of item
     * @param amount of the item
     * @return ItemStack
     */
    public static org.bukkit.inventory.ItemStack createItem(Material type, int amount) {
        return createItem(type, 0, amount);
    }

    /**
     * Creates a new ItemStack that is guaranteed to be a CraftItemStack with a valid NMS ItemStack handle
     * 
     * @param type of item
     * @param data of the item
     * @param amount of the item
     * @return ItemStack
     */
    public static org.bukkit.inventory.ItemStack createItem(Material type, int data, int amount) {
        return CBCraftItemStack.newInstanceFromHandle(NMSItemStack.newInstance(type, data, amount));
    }

    /**
     * Checks whether a given ItemStack is that of an empty (slot) item.
     * This includes NULL items, AIR-type items and items with 0 amount.
     * 
     * @param item to check
     * @return True if empty, False if not
     */
    public static boolean isEmpty(org.bukkit.inventory.ItemStack item) {
        return item == null || item.getAmount() == 0 || item.getType() == Material.AIR;
    }

    /**
     * Kills the old item and spawns a new item in it's place
     *
     * @param item to respawn
     * @return Respawned item
     */
    public static org.bukkit.entity.Item respawnItem(org.bukkit.entity.Item item) {
        item.remove();
        EntityItemHandle oldItemHandle = CommonNMS.getHandle(item);
        EntityItemHandle newItemHandle = EntityItemHandle.createNew(oldItemHandle.getWorld(), oldItemHandle.getLocX(), oldItemHandle.getLocY(), oldItemHandle.getLocZ(), oldItemHandle.getItemStack());

        newItemHandle.setFallDistance(oldItemHandle.getFallDistance());
        newItemHandle.setFireTicks(oldItemHandle.getFireTicks());
        newItemHandle.setPickupDelay(oldItemHandle.getPickupDelay());
        newItemHandle.setMotX(oldItemHandle.getMotX());
        newItemHandle.setMotY(oldItemHandle.getMotY());
        newItemHandle.setMotZ(oldItemHandle.getMotZ());
        newItemHandle.setAge(oldItemHandle.getAge());
        newItemHandle.getWorld().addEntity(newItemHandle);
        return Conversion.toItem.convert(newItemHandle.getRaw());
    }

    /**
     * Gets the contents of an inventory, cloning all the items
     *
     * @param inventory to get the cloned contents of
     * @return Cloned inventory contents array
     */
    public static org.bukkit.inventory.ItemStack[] getClonedContents(Inventory inventory) {
        org.bukkit.inventory.ItemStack[] rval = new org.bukkit.inventory.ItemStack[inventory.getSize()];
        for (int i = 0; i < rval.length; i++) {
            rval[i] = cloneItem(inventory.getItem(i));
        }
        return rval;
    }

    /**
     * Clones a single item
     *
     * @param stack to be cloned, can be null
     * @return Cloned item stack
     */
    public static org.bukkit.inventory.ItemStack cloneItem(org.bukkit.inventory.ItemStack stack) {
        return LogicUtil.clone(stack);
    }

    /**
     * Creates a new itemstack array containing items not referencing the input
     * item stacks
     *
     * @param input array to process
     * @return Cloned item stack array
     */
    public static org.bukkit.inventory.ItemStack[] cloneItems(org.bukkit.inventory.ItemStack[] input) {
        return LogicUtil.cloneAll(input);
    }

    /**
     * Subtracts a certain amount from an item, without limiting to the max
     * stack size
     *
     * @param item
     * @param amount to subtract
     */
    public static void subtractAmount(org.bukkit.inventory.ItemStack item, int amount) {
        addAmount(item, -amount);
    }

    /**
     * Adds a certain amount to an item, without limiting to the max stack size
     *
     * @param item
     * @param amount to add
     */
    public static void addAmount(org.bukkit.inventory.ItemStack item, int amount) {
        item.setAmount(Math.max(item.getAmount() + amount, 0));
    }

    /**
     * Obtains an item of the given type and data in the inventory specified<br>
     * If multiple items with the same type and data exist, their amounts are
     * added together
     *
     * @param inventory to look in
     * @param type of the items to look for, null for any item type
     * @param data of the items to look for, -1 for any data
     * @return Amount of items in the inventory
     */
    public static org.bukkit.inventory.ItemStack findItem(Inventory inventory, Material type, int data) {
        org.bukkit.inventory.ItemStack rval = null;
        int itemData = data;
        Material itemType = type;
        for (org.bukkit.inventory.ItemStack item : inventory.getContents()) {
            if (LogicUtil.nullOrEmpty(item)) {
                continue;
            }
            // Compare type Id
            if (itemType == null) {
                itemType = item.getType();
            } else if (itemType != item.getType()) {
                continue;
            }
            // Compare data
            if (itemData == -1) {
                itemData = MaterialUtil.getRawData(item);
            } else if (MaterialUtil.getRawData(item) != itemData) {
                continue;
            }
            // addition
            if (rval == null) {
                rval = item.clone();
            } else {
                addAmount(rval, item.getAmount());
            }
        }
        return rval;
    }

    /**
     * Gets the total item count of a given type and data
     *
     * @param inventory to look in
     * @param typeid of the items to look for, null for any item
     * @param data of the items to look for, -1 for any data
     * @return Amount of items in the inventory
     */
    public static int getItemCount(Inventory inventory, Material type, int data) {
        if (type == null) {
            int count = 0;
            for (org.bukkit.inventory.ItemStack item : inventory.getContents()) {
                if (!LogicUtil.nullOrEmpty(item)) {
                    count += item.getAmount();
                }
            }
            return count;
        } else {
            org.bukkit.inventory.ItemStack rval = findItem(inventory, type, data);
            return rval == null ? 0 : rval.getAmount();
        }
    }

    /**
     * Gets the max stacking size for a given item
     *
     * @param itemType of the item
     * @param def to return for invalid items
     * @return max stacking size
     */
    public static int getMaxSize(Material itemType, int def) {
        ItemHandle item = CommonNMS.getItem(itemType);
        return item == null ? def : item.getMaxStackSize();
    }

    /**
     * Gets the max stacking size for a given item
     *
     * @param stack to get the max stacked size
     * @return max stacking size
     */
    public static int getMaxSize(org.bukkit.inventory.ItemStack stack) {
        if (LogicUtil.nullOrEmpty(stack)) {
            return 0;
        } else {
            return getMaxSize(stack.getType(), 0);
        }
    }

    /**
     * Checks whether an Item stores a metadata tag
     *
     * @param stack to check
     * @return True if a metadata tag is stored, False if not
     */
    public static boolean hasMetaTag(org.bukkit.inventory.ItemStack stack) {
        return CommonNMS.getHandle(stack).hasTag();
    }

    /**
     * Sets the Metadata tag stored in an item. If tag is null, all metadata is
     * cleared.
     *
     * @param stack to set the metadata tag of
     * @param tag to set to
     */
    public static void setMetaTag(org.bukkit.inventory.ItemStack stack, CommonTagCompound tag) {
        if (CBCraftItemStack.T.isInstance(stack)) {
            NMSItemStack.tag.set(Conversion.toItemStackHandle.convert(stack), tag);
        } else {
            throw new RuntimeException("This item is not a CraftItemStack! Please create one using createCraftItem()");
        }
    }

    /**
     * Obtains the CommonTagCompound storing metadata for an item. If the item has
     * no metadata tag yet, null is returned instead.
     * 
     * @param stack to get the tag compound for
     * @return Tag Compound, or null if none exist.
     */
    public static CommonTagCompound getMetaTag(org.bukkit.inventory.ItemStack stack) {
        return getMetaTag(stack, false);
    }

    /**
     * Obtains the CommonTagCompound storing metadata for an item
     * 
     * @param stack to get the tag compound for
     * @param create whether to create a new tag if one does not exist
     * @return Tag Compound, or null if none exist and create is false
     */
    public static CommonTagCompound getMetaTag(org.bukkit.inventory.ItemStack stack, boolean create) {
        if (CBCraftItemStack.T.isInstance(stack)) {
            Object handle = Conversion.toItemStackHandle.convert(stack);
            CommonTagCompound tag = NMSItemStack.tag.get(handle);
            if (tag == null && create) {
                tag = new CommonTagCompound();
                NMSItemStack.tag.set(handle, tag);
            }
            return tag;
        } else {
            throw new RuntimeException("This item is not a CraftItemStack! Please create one using createCraftItem()");
        }
    }

    /**
     * Sets the cost of repairing this item
     *
     * @param stack to set the repair cost of
     * @param repairCost to set to
     */
    public static void setRepairCost(org.bukkit.inventory.ItemStack stack, int repairCost) {
        CommonNMS.getHandle(stack).setRepairCost(repairCost);
    }

    /**
     * Gets the cost of repairing this item
     *
     * @param stack to get the repair cost of
     * @return repair cost
     */
    public static int getRepairCost(org.bukkit.inventory.ItemStack stack) {
        return CommonNMS.getHandle(stack).getRepairCost();
    }

    /**
     * Checks whether an item has a custom display name set
     *
     * @param stack to check
     * @return True if a custom name is set, False if not
     */
    public static boolean hasDisplayName(org.bukkit.inventory.ItemStack stack) {
        return CommonNMS.getHandle(stack).hasName();
    }

    /**
     * Gets the current display name of an Item. If no name is set, the default
     * Material name is returned instead.
     *
     * @param stack to get the display name of
     * @return display name
     */
    public static String getDisplayName(org.bukkit.inventory.ItemStack stack) {
        return CommonNMS.getHandle(stack).getName();
    }

    /**
     * Sets the current display name of an Item
     *
     * @param stack to set the display name of
     * @param displayName to set to, null to reset to the default
     */
    public static void setDisplayName(org.bukkit.inventory.ItemStack stack, String displayName) {
        if (displayName != null) {
            if (CBCraftItemStack.T.isInstance(stack)) {
                CommonNMS.getHandle(stack).setName(displayName);
            } else {
                throw new RuntimeException("This item is not a CraftItemStack! Please create one using createCraftItem()");
            }
        } else if (hasDisplayName(stack)) {
            CommonNMS.getHandle(stack).getTag().remove("display");
        }
    }
}
