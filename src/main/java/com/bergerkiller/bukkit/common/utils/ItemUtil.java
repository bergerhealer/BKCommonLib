package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.logic.ItemVariantListHandler;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.inventory.InventoryBaseImpl;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.item.EntityItemHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.bergerkiller.generated.org.bukkit.inventory.InventoryHandle;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * Contains item stack, item and inventory utilities
 */
public class ItemUtil {
    private static final Function<InventoryClickEvent, Inventory> getClickedInventoryFunc = findGetClickedInventoryFunc();

    /**
     * Gets the clicked inventory from an InventoryClickEvent. Here for backwards-compatibility
     * reasons, as this method was added in Bukkit for MC 1.13.2. Defaults back to Bukkit's
     * own method if it is available.
     *
     * @param event Click Event
     * @return clicked inventory
     */
    public static Inventory getClickedInventory(InventoryClickEvent event) {
        return getClickedInventoryFunc.apply(event);
    }

    /**
     * Gets the Player of an inventory view. Fixes an issue with compatibility between versions
     * 1.20.6 and 1.21 where Bukkit changed it from an interface to a class.
     *
     * @param view View
     * @return InventoryView getPlayer()
     */
    public static Player getViewPlayer(InventoryView view) {
        return InventoryHandle.getViewPlayer(view);
    }

    private static Function<InventoryClickEvent, Inventory> findGetClickedInventoryFunc() {
        try {
            InventoryClickEvent.class.getDeclaredMethod("getClickedInventory");
            return InventoryClickEvent::getClickedInventory;
        } catch (Throwable t) {}

        // Fallback for mc 1.8 - 1.13.1/2
        return event -> {
            if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
                return event.getView().getTopInventory();
            } else {
                return event.getView().getBottomInventory();
            }
        };
    }

    /**
     * Creates a new player head item with the game profile information specified.
     * This creates a player head with this profile's skin texture information.
     *
     * @param gameProfile
     * @return Skull item
     * @deprecated Use {@link CommonItemStack#createPlayerSkull(GameProfileHandle)} instead
     */
    @Deprecated
    public static ItemStack createPlayerHeadItem(GameProfileHandle gameProfile) {
        return CommonItemStack.createPlayerSkull(gameProfile).toBukkit();
    }

    /**
     * Tests if the given ItemStacks can be fully transferred to another array
     * of ItemStacks. <b>Note: slow</b>
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
     * of ItemStacks. <b>Note: slow</b>
     *
     * @param from ItemStack source array
     * @param to ItemStack destination array
     * @return True if full transfer was possible, False if not
     */
    public static boolean canTransferAll(org.bukkit.inventory.ItemStack[] from, org.bukkit.inventory.ItemStack[] to) {
        Inventory invto = new InventoryBaseImpl(to, true);
        for (org.bukkit.inventory.ItemStack item : from) {
            CommonItemStack fromItem = CommonItemStack.copyOf(item);
            fromItem.transferTo(invto, -1);
            if (!fromItem.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests if the given ItemStack can be transferred to the Inventory
     *
     * @return The amount that could be transferred
     * @deprecated Use {@link CommonItemStack#testTransferTo(Inventory)} instead
     */
    @Deprecated
    public static int testTransfer(org.bukkit.inventory.ItemStack from, Inventory to) {
        return CommonItemStack.of(from).testTransferTo(to);
    }

    /**
     * Tests if the two items can be merged
     *
     * @return The amount that could be transferred
     * @deprecated Moved to {@link CommonItemStack#testTransferTo(CommonItemStack)}
     */
    @Deprecated
    public static int testTransfer(org.bukkit.inventory.ItemStack from, org.bukkit.inventory.ItemStack to) {
        return CommonItemStack.of(from).testTransferTo(CommonItemStack.of(to));
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
     * @deprecated Use {@link CommonItemStack#transfer(Inventory, Inventory, Predicate, int)} instead
     */
    @Deprecated
    public static int transfer(Inventory from, Inventory to, ItemParser parser, int maxAmount) {
        return CommonItemStack.transfer(from, to, parser, maxAmount);
    }

    /**
     * Creates a copy of an existing inventory, with all fields deeply copied.
     * Changes to this returned inventory will not affect the original inventory.
     * The returned inventory is not of the same class type!
     * 
     * @param inventory to clone
     * @return cloned inventory
     */
    public static Inventory cloneInventory(Inventory inventory) {
        return new InventoryBaseImpl(inventory.getContents(), true);
    }

    /**
     * Checks whether two item stacks equal, while ignoring the item amounts
     *
     * @param item1 to check
     * @param item2 to check
     * @return True if the items have the same type, data and enchantments,
     *         False if not
     */
    public static boolean equalsIgnoreAmount(org.bukkit.inventory.ItemStack item1, org.bukkit.inventory.ItemStack item2) {
        return (item1 == null) ? (item2 == null) : item1.isSimilar(item2);
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
     * @param inventory Inventory to remove items from
     * @param type Type of the items to remove
     * @param data Legacy data value of the items to remove, -1 for any data
     * @param amount Amount of items to remove, -1 for infinite amount
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
        return createItem(Material.AIR, 0);
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
        ItemStackHandle stack = ItemStackHandle.newInstance(type);
        stack.setAmountField(amount);
        return stack.toBukkit();
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
        newItemHandle.getWorldServer().addEntity(newItemHandle);
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
        return stack == null ? null : stack.clone();
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
     * @deprecated This method is bad, because it doesn't adhere to the stack limit. Use
     *             {@link CommonItemStack#streamOfContents(Inventory)} instead to find all items
     *             that match in the inventory, and operate on that instead.
     */
    @Deprecated
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
     * @param inventory Inventory to look in
     * @param type Type of the items to look for, null for any item
     * @param data Legacy data of the items to look for, -1 for any data
     * @return Amount of items in the inventory
     */
    public static int getItemCount(Inventory inventory, Material type, int data) {
        Stream<CommonItemStack> stream = CommonItemStack.streamOfContents(inventory);
        if (type != null) {
            stream = stream.filter(item -> item.isType(type));
            if (data != -1) {
                stream = stream.filter(item -> MaterialUtil.getRawData(item.toBukkit()) == data);
            }
        }
        return stream.mapToInt(CommonItemStack::getAmount).sum();
    }

    /**
     * Gets the max durability value for a given item.
     * When the durability values becomes larger than this value, the item breaks.
     * Items that do not use durability return def here.
     * 
     * @param itemType of the item
     * @param def to return for invalid items (that do not have durability)
     * @return max durability
     */
    public static int getMaxDurability(Material itemType, int def) {
        ItemHandle item = CommonNMS.getItem(itemType);
        return (item == null || !item.usesDurability()) ? def : item.getMaxDurability();
    }

    /**
     * Gets the max durability value for a given item.
     * When the durability values becomes larger than this value, the item breaks.
     * Items that do not use durability return 0 here.
     *
     * @param stack to get the max durability
     * @return max durability
     */
    public static int getMaxDurability(ItemStack stack) {
        if (LogicUtil.nullOrEmpty(stack)) {
            return 0;
        } else {
            return getMaxDurability(stack.getType(), 0);
        }
    }

    /**
     * Checks whether a particular item material type uses durability
     * for automatically breaking the item after a number of uses.
     * 
     * @param itemType to check
     * @return True if durability is used
     */
    public static boolean hasDurability(Material itemType) {
        ItemHandle item = CommonNMS.getItem(itemType);
        return item != null && item.usesDurability();
    }

    /**
     * Checks whether a particular item material type uses durability
     * for automatically breaking the item after a number of uses.
     * 
     * @param stack to check
     * @return True if durability is used
     */
    public static boolean hasDurability(ItemStack stack) {
        return !LogicUtil.nullOrEmpty(stack) && hasDurability(stack.getType());
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
     * @deprecated Use {@link CommonItemStack#hasCustomName()} instead
     */
    @Deprecated
    public static boolean hasDisplayName(org.bukkit.inventory.ItemStack stack) {
        return CommonItemStack.of(stack).hasCustomName();
    }

    /**
     * Gets the current display name of an Item. If no name is set, the default
     * Material name is returned instead. If the item is null or empty, an illegal argument exception
     * is thrown.
     *
     * @param stack to get the display name of
     * @return display name
     * @deprecated Use {@link CommonItemStack#getDisplayNameMessage()} instead
     */
    @Deprecated
    public static String getDisplayName(org.bukkit.inventory.ItemStack stack) {
        return CommonItemStack.of(stack).getDisplayNameMessage();
    }

    /**
     * Gets the current display name of an Item as a {@link ChatText} object. If no name is set, the default
     * Material name is returned instead. If the item is null or empty, an illegal argument exception
     * is thrown.
     *
     * @param stack to get the display name of
     * @return display name ChatText
     * @deprecated Use {@link CommonItemStack#getDisplayName()} instead
     */
    @Deprecated
    public static ChatText getDisplayChatText(org.bukkit.inventory.ItemStack stack) {
        return CommonItemStack.of(stack).getDisplayName();
    }

    /**
     * Sets the current display name of an Item
     *
     * @param stack to set the display name of
     * @param displayName to set to, null to reset to the default
     * @deprecated Use {@link CommonItemStack#setCustomName(ChatText)} instead
     */
    @Deprecated
    public static void setDisplayName(org.bukkit.inventory.ItemStack stack, String displayName) {
        setDisplayChatText(stack, ChatText.fromMessage(displayName));
    }

    /**
     * Sets the current display name of an Item as a {@link ChatText} object.
     *
     * @param stack to set the display name of
     * @param displayName to set to, null to reset to the default
     * @deprecated Use {@link CommonItemStack#setCustomName(ChatText)} instead
     */
    @Deprecated
    public static void setDisplayChatText(org.bukkit.inventory.ItemStack stack, ChatText displayName) {
        if (displayName != null) {
            if (CraftItemStackHandle.T.isAssignableFrom(stack)) {
                CommonNMS.getHandle(stack).setCustomName(displayName);
            } else {
                throw new RuntimeException("This item is not a CraftItemStack! Please create one using createItem(ItemStack)");
            }
        } else if (hasDisplayName(stack)) {
            if (CraftItemStackHandle.T.isAssignableFrom(stack)) {
                CommonNMS.getHandle(stack).setCustomName(null);
            } else {
                throw new RuntimeException("This item is not a CraftItemStack! Please create one using createItem(ItemStack)");
            }
        }
    }

    /**
     * Removes all lores from an item that are set, if they are set
     * 
     * @param itemStack
     * @deprecated Use {@link CommonItemStack#clearLores()} instead
     */
    @Deprecated
    public static void clearLoreNames(org.bukkit.inventory.ItemStack itemStack) {
        CommonItemStack.of(itemStack).clearLores();
    }

    /**
     * Adds a lore name to an item
     * 
     * @param itemStack
     * @param name
     * @deprecated Use {@link CommonItemStack#addLoreMessage(String)} instead
     */
    @Deprecated
    public static void addLoreName(org.bukkit.inventory.ItemStack itemStack, String name) {
        CommonItemStack.of(itemStack).addLoreMessage(name);
    }

    /**
     * Adds a lore name to an item, using a ChatText for more expressive formatting
     * 
     * @param itemStack
     * @param lore
     * @deprecated Use {@link CommonItemStack#addLore(ChatText)} instead
     */
    @Deprecated
    public static void addLoreChatText(org.bukkit.inventory.ItemStack itemStack, ChatText lore) {
        CommonItemStack.of(itemStack).addLore(lore);
    }

    /**
     * Gets a list of valid items of a particular item type.
     * A list with only a single variant will be returned if the item has no other variants.
     * 
     * @param itemType
     * @return item variants
     */
    public static List<ItemStack> getItemVariants(Material itemType) {
        Object itemHandle = HandleConversion.toItemHandle(itemType);
        if (itemHandle == null) {
            return new ArrayList<ItemStack>(0);
        } else {
            return ItemVariantListHandler.INSTANCE.getVariants(itemHandle);
        }
    }

    /**
     * Gets a list of valid materials that can be items stored in a player's inventory.
     * Blocks that can not be used as items are excluded.
     * 
     * @return list of valid inventory item types
     */
    public static List<Material> getItemTypes() {
        List<Material> result = new ArrayList<Material>(500);
        for (Object itemRawHandle : ItemHandle.getRegistry()) {
            Material type = WrapperConversion.toMaterialFromItemHandle(itemRawHandle);
            if (type == Material.AIR) {
                continue;
            }
            result.add(type);
        }
        return result;
    }
}
