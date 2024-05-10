package com.bergerkiller.bukkit.common.inventory;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.map.util.ModelInfoLookup;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.ItemRenderOptions;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Provides an additional API for Bukkit ItemStacks. In addition to Bukkit's methods, you
 * can use it to get and set custom NBT data. The CommonItemStack is never null. When creating
 * one from a <i>null</i> Bukkit ItemStack, it is treated as an {@link #isEmpty() Empty}
 * CommonItemStack instead. This makes handling unknown items less error-prone.<br>
 * <br>
 * Null items are automatically initialized to empty Bukkit ItemStack instances when calling methods
 * that modify items. As such, you can create an empty item, and then use setAmount and setType
 * to initialize to to a valid non-empty state.
 * <br>
 * <h2>Important note</h2>
 * For some APIs it internally operates on the net.minecraft ItemStack, which requires the Bukkit ItemStack
 * to be a CraftItemStack. When creating a CommonItemStack from a Bukkit item, it might for that reason
 * not reference the same item internally. Similarly, when creating it from a null item,
 * a new ItemStack is created implicitly when changing item properties afterward.
 * You should re-set the item in the player's inventory, or elsewhere, if this is the case.
 * Use {@link #isCraftItemStack(ItemStack)} to check beforehand if this is important.
 */
public final class CommonItemStack implements Cloneable {
    private ItemStack bukkitItemStack;

    // Cached and computed on demand
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
    private Optional<ItemStackHandle> itemStackHandle = null;

    /**
     * Checks whether a Bukkit ItemStack is a CraftItemStack, meaning it is an ItemStack backed
     * by a net.minecraft ItemStack handle. Some of the CommonItemStack API only operates on Minecraft's
     * ItemStack instances. Attempts to make changes such as custom data will result in a
     * new backing CraftItemStack being created, if the original item is a stock Bukkit ItemStack.
     *
     * @param itemStack ItemStack to check
     * @return True if the ItemStack is a CraftItemStack, backed by a Minecraft ItemStack
     */
    public static boolean isCraftItemStack(ItemStack itemStack) {
        return CraftItemStackHandle.T.isAssignableFrom(itemStack);
    }

    /**
     * Creates a new CommonItemStack wrapping the Bukkit ItemStack specified. If the input
     * item is <i>null</i>, then a <i>non-null</i> CommonItemStack is returned representing
     * an empty item.
     *
     * @param itemStack Bukkit ItemStack
     * @return CommonItemStack
     */
    public static CommonItemStack of(ItemStack itemStack) {
        return new CommonItemStack(itemStack);
    }

    /**
     * Creates a new CommonItemStack wrapping a copy of the Bukkit ItemStack specified.
     * If the input item is <i>null</i>, then a <i>non-null</i> CommonItemStack is returned representing
     * an empty item.
     *
     * @param itemStack Bukkit ItemStack
     * @return CommonItemStack
     * @see #clone()
     */
    public static CommonItemStack copyOf(ItemStack itemStack) {
        return of(ItemUtil.cloneItem(itemStack));
    }

    /**
     * Creates a new CommonItemStack representing an empty item. A type and amount can be set
     * on this empty item (in this order) to initialize it to a non-empty item.
     *
     * @return Empty CommonItemStack
     */
    public static CommonItemStack empty() {
        return new CommonItemStack(null);
    }

    /**
     * Creates a new CommonItemStack representing a new item by a given type and amount
     *
     * @param type Material Type for the new item
     * @param amount Amount of the new item
     * @return CommonItemStack
     */
    public static CommonItemStack create(Material type, int amount) {
        return of(ItemUtil.createItem(type, amount));
    }

    /**
     * Creates a new skull item with a certain player game profile as a skin for the skull
     *
     * @param profile Game Profile for the skull item
     * @return CommonItemStack
     */
    public static CommonItemStack createPlayerSkull(GameProfileHandle profile) {
        return create(CommonItemMaterials.SKULL, 1).setSkullProfile(profile);
    }

    private CommonItemStack(ItemStack bukkitItemStack) {
        this.bukkitItemStack = bukkitItemStack;
    }

    @SuppressWarnings("OptionalAssignedToNull")
    private void invalidateItemStackHandle() {
        this.itemStackHandle = null;
    }

    /**
     * Clears the contents of this item. Equivalent to setting to <i>null</i>.
     */
    public void clear() {
        this.bukkitItemStack = null;
        this.invalidateItemStackHandle();
    }

    /**
     * Sets the underlying Bukkit ItemStack this CommonItemStack represents. Further changes
     * to this item may reflect changes to the input item. If you do not want to modify
     * the input item, use {@link #setToCopyOf(CommonItemStack)} instead.
     *
     * @param commonItemStack CommonItemStack to set to
     * @return this CommonItemStack
     */
    public CommonItemStack setTo(CommonItemStack commonItemStack) {
        this.bukkitItemStack = commonItemStack.bukkitItemStack;
        this.itemStackHandle = commonItemStack.itemStackHandle;
        return this;
    }

    /**
     * Sets the underlying Bukkit ItemStack this CommonItemStack represents. Further changes
     * to this item may reflect changes to the input item. If you do not want to modify
     * the input item, use {@link #setToCopyOf(ItemStack)} instead.
     *
     * @param itemStack ItemStack to set to
     * @return this CommonItemStack
     */
    public CommonItemStack setTo(ItemStack itemStack) {
        this.bukkitItemStack = itemStack;
        this.invalidateItemStackHandle();
        return this;
    }

    /**
     * Sets the underlying Bukkit ItemStack this CommonItemStack represents. Further changes
     * to this item will <b>not</b> reflect changes to the input item.
     *
     * @param commonItemStack CommonItemStack to copy and set this item to
     * @return this CommonItemStack
     */
    public CommonItemStack setToCopyOf(CommonItemStack commonItemStack) {
        if (commonItemStack.isEmpty()) {
            this.bukkitItemStack = null;
        } else {
            this.bukkitItemStack = commonItemStack.bukkitItemStack.clone();
        }
        this.invalidateItemStackHandle();
        return this;
    }

    /**
     * Sets the underlying Bukkit ItemStack this CommonItemStack represents. Further changes
     * to this item will <b>not</b> reflect changes to the input item.
     *
     * @param itemStack ItemStack to copy and set this item to
     * @return this CommonItemStack
     */
    public CommonItemStack setToCopyOf(ItemStack itemStack) {
        this.bukkitItemStack = ItemUtil.cloneItem(itemStack);
        this.invalidateItemStackHandle();
        return this;
    }

    /**
     * Checks whether this CommonItemStack is backed by a non-null CraftItemStack, meaning
     * it is an ItemStack backed  by a net.minecraft ItemStack handle. Some of the CommonItemStack
     * API only operates on Minecraft's ItemStack instances. Attempts to make changes such as
     * custom data will result in a new backing CraftItemStack being created, if the original item
     * is a stock Bukkit ItemStack.
     *
     * @return True if this CommonItemStack is backed by a CraftItemStack, backed by a Minecraft ItemStack
     */
    public boolean isCraftItemStack() {
        return isCraftItemStack(bukkitItemStack);
    }

    /**
     * Gets the internal net.minecraft ItemStack handle. If the underlying item is not a
     * CraftItemStack (see: {@link #isCraftItemStack()}), then a new CraftItemStack copy
     * is created of the underlying item, whose handle is then returned. The instance returned
     * by {@link #toBukkit()} can change as a result.<br>
     * <br>
     * If the underlying item is {@link #isEmpty() empty}, this method returns an empty
     * Optional to indicate no internal handle is available.
     *
     * @return Optional with the NMS ItemStack handle, or empty if empty.
     */
    public Optional<ItemStackHandle> getHandle() {
        return getHandle(false);
    }

    // With detach=true we can create a copy of a handle to read properties
    // This makes use of Bukkits handy conversion to CraftItemStack without altering this one
    private Optional<ItemStackHandle> getHandle(boolean detach) {
        Optional<ItemStackHandle> cached = this.itemStackHandle;
        //noinspection OptionalAssignedToNull
        if (cached != null) {
            return cached;
        }

        ItemStack bukkitItemStack = this.bukkitItemStack;

        // Promote to a CraftItemStack first
        if (!isCraftItemStack(bukkitItemStack)) {
            if (ItemUtil.isEmpty(bukkitItemStack)) {
                this.itemStackHandle = cached = Optional.empty();
                return cached;
            } else if (detach) {
                // Do all of this without assigning it to this CommonItemStack
                // This plugs some holes when reading certain properties from Bukkit ItemStacks.
                bukkitItemStack = CraftItemStackHandle.asCraftCopy(bukkitItemStack);
                Object nmsHandle = CraftItemStackHandle.T.handle.get(bukkitItemStack);
                if (nmsHandle != null) {
                    return Optional.of(ItemStackHandle.createHandle(nmsHandle));
                } else {
                    return Optional.empty();
                }
            } else {
                this.bukkitItemStack = bukkitItemStack = CraftItemStackHandle.asCraftCopy(bukkitItemStack);
            }
        }

        // Try to read the handle. If this is null, treat it as an empty item
        Object nmsHandle = CraftItemStackHandle.T.handle.get(bukkitItemStack);
        if (nmsHandle == null) {
            this.itemStackHandle = cached = Optional.empty();
            return cached;
        }

        // Cache the resulting handle for future calls
        this.itemStackHandle = cached = Optional.of(ItemStackHandle.createHandle(nmsHandle));
        return cached;
    }

    /**
     * Same as {@link #getHandle()}, but if the underlying item is empty or
     * {@link #isCraftItemStack() not a CraftItemStack}, returns an empty Optional instead.
     * This method will not modify the result of {@link #toBukkit()}.
     *
     * @return Handle, or Empty if this is an empty item or not backed by a CraftItemStack
     */
    public Optional<ItemStackHandle> getHandleIfCraftItemStack() {
        Optional<ItemStackHandle> cached = this.itemStackHandle;
        //noinspection OptionalAssignedToNull
        if (cached != null) {
            return cached;
        }

        ItemStack bukkitItemStack = this.bukkitItemStack;
        if (isCraftItemStack(bukkitItemStack)) {
            Object nmsHandle = CraftItemStackHandle.T.handle.get(bukkitItemStack);
            if (nmsHandle != null) {
                this.itemStackHandle = cached = Optional.of(ItemStackHandle.createHandle(nmsHandle));
                return cached;
            }
        } else if (ItemUtil.isEmpty(bukkitItemStack)) {
            this.itemStackHandle = cached = Optional.empty();
            return cached;
        }

        return Optional.empty();
    }

    /**
     * Gets whether this item is empty. The item can be empty because the underlying
     * item is null, has zero amount or is air.
     *
     * @return True if this item is empty
     */
    public boolean isEmpty() {
        return ItemUtil.isEmpty(this.bukkitItemStack);
    }

    /**
     * Gets the Material type of this item
     *
     * @return Type, Material.AIR if the underlying Bukkit Item was <i>null</i>
     */
    public Material getType() {
        return (bukkitItemStack == null) ? Material.AIR : bukkitItemStack.getType();
    }

    /**
     * Gets whether the Material type of this item matches the Material specified
     *
     * @param type Type
     * @return True if it is this type
     */
    public boolean isType(Material type) {
        return getType() == type;
    }

    /**
     * Efficiently checks whether this item is a filled map. This is useful to see if this
     * item could be a Map Display.
     *
     * @return True if this item is a Filled Map
     */
    public boolean isFilledMap() {
        return getHandleIfCraftItemStack()
                .map(ItemStackHandle::isMapItem)
                .orElseGet(() -> isType(CommonItemMaterials.FILLED_MAP));
    }

    /**
     * Gets whether this item is a filled map item that also includes Map Display metadata.
     * If this is the case, the map contents are controlled by BKCommonLib.
     *
     * @return True if this item is that of a BKCommonLib Map Display
     */
    public boolean isMapDisplay() {
        // Note: non-CraftItemStacks are presumed to not contain custom data, so no displays
        return getHandleIfCraftItemStack()
                .map(h -> h.isMapItem() && h.getCustomData().getUUID("mapDisplay") != null)
                .orElse(Boolean.FALSE);
    }

    /**
     * Sets a new Material type for this item
     *
     * @param type New Type to set to
     * @return this CommonItemStack
     */
    public CommonItemStack setType(Material type) {
        toBukkit(true).setType(type);
        this.invalidateItemStackHandle();
        return this;
    }

    /**
     * Gets the stacked amount of this item. If this item is {@link #isEmpty() empty}, returns 0.
     *
     * @return Item Amount
     */
    public int getAmount() {
        return (bukkitItemStack == null) ? 0 : bukkitItemStack.getAmount();
    }

    /**
     * Sets a new amount on this item. Before an amount can be set, a {@link #setType(Material) type}
     * must be set. This method fails with an error otherwise.
     *
     * @param amount New amount to set
     * @return this CommonItemStack
     */
    public CommonItemStack setAmount(int amount) {
        ItemStack item = toBukkit();
        if (item == null || item.getType() == Material.AIR) {
            throw new IllegalStateException("Cannot change the amount of an item without type");
        }
        item.setAmount(amount);
        if (amount == 0) {
            this.invalidateItemStackHandle();
        }
        return this;
    }

    /**
     * Subtracts a certain amount from an item, without limiting to the max
     * stack size.
     * Before an amount can be set, a {@link #setType(Material) type}
     * must be set. This method fails with an error otherwise.
     *
     * @param amountToSubtract Amount to subtract
     * @return this CommonItemStack
     */
    public CommonItemStack subtractAmount(int amountToSubtract) {
        return addAmount(-amountToSubtract);
    }

    /**
     * Adds a certain amount to an item, without limiting to the max stack size.
     * Before an amount can be set, a {@link #setType(Material) type}
     * must be set. This method fails with an error otherwise.
     *
     * @param amountToAdd Amount to add
     * @return this CommonItemStack
     */
    public CommonItemStack addAmount(int amountToAdd) {
        return setAmount(Math.max(getAmount() + amountToAdd, 0));
    }

    /**
     * If this item {@link #isDamageSupported() supports a damage value}, returns the current
     * damage value of this item.
     *
     * @return Damage value
     */
    @SuppressWarnings("deprecation")
    public int getDamage() {
        return bukkitItemStack == null ? 0 : (int) bukkitItemStack.getDurability();
    }

    /**
     * Sets a new damage value for this item. If this item
     * {@link #isDamageSupported()} does not support} damage,
     * throws an IllegalStateException.
     *
     * @param damage New damage value to set
     * @return this CommonItemStack
     * @throws IllegalStateException If this item does not use support damage
     */
    public CommonItemStack setDamage(int damage) {
        if (isDamageSupported()) {
            getHandle().ifPresent(h -> h.setDamageValue(damage));
            return this;
        } else {
            throw new IllegalStateException("This item does not support durability");
        }
    }

    /**
     * Gets the max stacking size of this item. Returns <i>0</i> if this is
     * an item of an unknown type.
     *
     * @return Max stacking size, 0 if type is invalid or AIR
     */
    public int getMaxStackSize() {
        return (bukkitItemStack == null) ? 0 : bukkitItemStack.getMaxStackSize();
    }

    /**
     * Gets whether this item supports damage over time, like for example armor and swords.
     * This also returns true for such items when 'damageable' is set to 0 (unbreakable),
     * where the damage value is only a visual aspect.
     *
     * @return True if this item uses durability, False if not
     */
    public boolean isDamageSupported() {
        ItemStack bukkitItemStack = this.bukkitItemStack;
        if (bukkitItemStack == null) {
            return false;
        }
        ItemHandle item = CommonNMS.getItem(bukkitItemStack.getType());
        return item != null && item.usesDurability();
    }

    /**
     * Gets the current repair cost (anvil) of this Item.
     *
     * @return Repair cost, 0 is no cost
     */
    public int getRepairCost() {
        return getHandle(true)
                .map(ItemStackHandle::getRepairCost)
                .orElse(0);
    }

    /**
     * Sets the repair cost (anvil) of this Item. A repair cost of 0 results in no
     * cost being displayed in the anvil menu.
     *
     * @param cost Repair Cost
     * @return this CommonItemStack
     * @throws IllegalStateException If this item is empty
     */
    public CommonItemStack setRepairCost(int cost) {
        getHandle()
                .orElseThrow(() -> new IllegalStateException("Can not set repair cost on an empty item"))
                .setRepairCost(cost);
        return this;
    }

    /**
     * Gets whether this item is set as unbreakable. If this item
     * {@link #isDamageSupported() supports being damaged} then the item will
     * no longer sustain damage. It also may change the visual display of the item.
     *
     * @return True if this item is unbreakable
     */
    public boolean isUnbreakable() {
        return getHandle(true)
                .map(ItemStackHandle::isUnbreakable)
                .orElse(Boolean.FALSE);
    }

    /**
     * Sets whether this item is set as unbreakable. If this item
     * {@link #isDamageSupported() supports being damaged} then the item will
     * no longer sustain damage. It also may change the visual display of the item.
     *
     * @param unbreakable New unbreakable state
     * @return this CommonItemStack
     */
    public CommonItemStack setUnbreakable(boolean unbreakable) {
        getHandle()
                .orElseThrow(() -> new IllegalStateException("Can not set unbreakable on an empty item"))
                .setUnbreakable(unbreakable);
        return this;
    }

    /**
     * Gets the maximum damage this item supports sustaining.
     * When the durability values becomes larger than this value, the item breaks.
     * Items that do not use damage values return 0 here.
     *
     * @return max damage value
     * @see #getDamage()
     */
    public int getMaxDamage() {
        return ItemUtil.getMaxDurability(bukkitItemStack);
    }

    /**
     * Gets whether a custom display name is set on this item.
     *
     * @return True if a display name is set
     */
    public boolean hasCustomName() {
        return getHandle(true)
                .map(ItemStackHandle::hasCustomName)
                .orElse(Boolean.FALSE);
    }

    /**
     * Gets the custom display name of this item, if one is set. Returns <i>null</i>
     * if no special display name is set.
     *
     * @return Display Name as formatted ChatText component, or null if not set
     */
    public ChatText getCustomName() {
        return getHandle(true)
                .map(ItemStackHandle::getCustomName)
                .orElse(null);
    }

    /**
     * Gets the custom display name of this item as a legacy formatted message string,
     * if one is set. Returns <i>null</i> if no special display name is set.
     *
     * @return Display Name as legacy message String, or null if not set
     */
    public String getCustomNameMessage() {
        ChatText text = getCustomName();
        return text == null ? null : text.getMessage();
    }

    /**
     * Sets a custom display name for this item. Throws an exception if this item is empty.
     *
     * @param displayName Display Name ChatText to set, or <i>null</i> to reset/clear it
     * @return this CommonItemStack
     * @throws IllegalStateException If this item is {@link #isEmpty() empty}.
     */
    public CommonItemStack setCustomName(ChatText displayName) {
        getHandle()
                .orElseThrow(() -> new IllegalStateException("Can not set display name on an empty item"))
                .setCustomName(displayName);
        return this;
    }

    /**
     * Sets a custom display name for this item based on a legacy formatted message.
     * Throws an exception if this item is empty.
     *
     * @param displayName Display Name String Message to set, or <i>null</i> to reset/clear it
     * @return this CommonItemStack
     * @throws IllegalStateException If this item is {@link #isEmpty() empty}.
     */
    public CommonItemStack setCustomNameMessage(String displayName) {
        return setCustomName(displayName == null ? null : ChatText.fromMessage(displayName));
    }

    /**
     * Sets an empty custom name on this item, so that the original name of the item is not
     * displayed. An empty String might not as reliably work, so use this method instead
     * of hiding the original name is important.
     *
     * @return this CommonItemStack
     * @throws IllegalStateException If this item is {@link #isEmpty() empty}.
     */
    public CommonItemStack setEmptyCustomName() {
        if (CommonCapabilities.EMPTY_ITEM_NAME) {
            return setCustomNameMessage(ChatColor.RESET.toString());
        } else {
            return setCustomNameMessage(ChatColor.RESET + "\0");
        }
    }

    /**
     * Gets a list of Lore lines that are added to this item. Returns an empty list if no lores
     * have been assigned.
     *
     * @return Lore lines of this item
     */
    public List<ChatText> getLores() {
        return getHandle(true)
                .map(ItemStackHandle::getLores)
                .orElse(Collections.emptyList());
    }

    /**
     * Adds a new Lore line to this item, from a Bukkit-formatted legacy
     * message String.
     *
     * @param loreMessage Legacy Message format String
     * @return this CommonItemStack
     * @throws IllegalStateException If this item is empty
     */
    public CommonItemStack addLoreMessage(String loreMessage) {
        return addLore(ChatText.fromMessage(loreMessage));
    }

    /**
     * Adds a new Lore line to this item that is empty, acting as a line spacer
     * between multiple added lore messages.
     *
     * @return this CommonItemStack
     * @throws IllegalStateException If this item is empty
     */
    public CommonItemStack addLoreLine() {
        return addLore(ChatText.empty());
    }

    /**
     * Adds a new Lore line to this item
     *
     * @param loreLine Formatted Lore Line ChatText to add as a Lore
     * @return this CommonItemStack
     * @throws IllegalStateException If this item is empty
     */
    public CommonItemStack addLore(ChatText loreLine) {
        getHandle()
                .orElseThrow(() -> new IllegalStateException("Can not set lores on an empty item"))
                .addLore(loreLine);
        return this;
    }

    /**
     * Clears all Lore lines currently set to this item. New lore lines can then be added
     * using {@link #addLore(ChatText)}
     *
     * @return this CommonItemStack
     */
    public CommonItemStack clearLores() {
        getHandle().ifPresent(ItemStackHandle::clearLores);
        return this;
    }

    /**
     * Gets the player profile information for a skull item. If the item lacks such skull information,
     * returns <i>null</i>.
     *
     * @return Skull player GameProfile Handle
     */
    public GameProfileHandle getSkullProfile() {
        return getHandle(true).map(ItemStackHandle::getSkullProfile).orElse(null);
    }

    /**
     * Sets the player profile information for a skull item, changing the appearance of skull items
     *
     * @param profile Game Profile of the skull. <i>Null</i> to clear skull profile information.
     * @return this CommonItemStack
     */
    public CommonItemStack setSkullProfile(GameProfileHandle profile) {
        getHandle()
                .orElseThrow(() -> new IllegalStateException("Can not set skull on an empty item"))
                .setSkullProfile(profile);
        return this;
    }

    /**
     * Gets whether this item stores a custom model data integer value, which controls
     * client-side display of models. Since Minecraft 1.14.
     *
     * @return True if this item includes custom model data
     */
    public boolean hasCustomModelData() {
        return getHandle(true)
                .map(ItemStackHandle::hasCustomModelData)
                .orElse(Boolean.FALSE);
    }

    /**
     * Gets the custom model data integer value, if
     * {@link #hasCustomModelData() this item includes custom model data}.
     * Otherwise, returns -1.
     *
     * @return Custom Model Data, or -1 if none is stored
     */
    public int getCustomModelData() {
        return getHandle(true)
                .map(ItemStackHandle::getCustomModelData)
                .orElse(-1);
    }

    /**
     * Sets a new custom model data integer value. Note that -1 does not
     * clear it, use {@link #clearCustomModelData()} instead for that.
     *
     * @param value New custom model data integer value to set
     * @return this CommonItemStack
     */
    public CommonItemStack setCustomModelData(int value) {
        getHandle()
                .orElseThrow(() -> new IllegalStateException("Can not set custom model data on an empty item"))
                .setCustomModelData(value);
        return this;
    }

    /**
     * Clears any custom model data integer value set.
     *
     * @return this CommonItemStack
     * @see #hasCustomModelData()
     */
    public CommonItemStack clearCustomModelData() {
        getHandle().ifPresent(ItemStackHandle::clearCustomModelData);
        return this;
    }

    /**
     * Gets whether this ItemStack includes custom (plugin) data. If this returns true, then
     * {@link #getCustomData()} and {@link #getCustomDataCopy()} will
     * return a non-empty NBT Tag containing metadata. If false, then an empty tag
     * is returned there.
     *
     * @return True if this item contains custom data
     */
    public boolean hasCustomData() {
        return getHandleIfCraftItemStack()
                .map(ItemStackHandle::hasCustomData)
                .orElse(Boolean.FALSE);
    }

    /**
     * Looks up the item render options using the {@link ModelInfoLookup}. This is used to
     * display the icons of items on map displays.
     *
     * @return Render options for displaying this item
     */
    public ItemRenderOptions lookupRenderOptions() {
        return ModelInfoLookup.lookupItemRenderOptions(this);
    }

    /**
     * Gets the ID value of a filled map item, if this item is a filled map
     *
     * @return Filled map id, or -1 if this item doesn't store one
     */
    public int getFilledMapId() {
        return getHandle(true)
                .map(ItemStackHandle::getMapId)
                .orElse(-1);
    }

    /**
     * Sets the ID value of a filled map item. Use -1 to remove the map id metadata.
     *
     * @param mapId New Filled Map ID to set
     * @return this CommonItemStack
     */
    public CommonItemStack setFilledMapId(int mapId) {
        getHandle()
                .orElseThrow(() -> new IllegalStateException("Item is empty and cannot store a map id"))
                .setMapId(mapId);
        return this;
    }

    /**
     * Gets a custom filled map color, if any was set. If none was set, returns -1.
     * The color is a RGB value (without alpha).
     *
     * @return Filled map color in RGB, or -1 if none was set
     */
    public int getFilledMapColor() {
        return getHandle(true)
                .map(ItemStackHandle::getMapColor)
                .orElse(-1);
    }

    /**
     * Sets a new custom filled map color
     *
     * @param rgb RGB Map color value to set, or -1 to clear it
     * @return this CommonItemStack
     */
    public CommonItemStack setFilledMapColor(int rgb) {
        getHandle()
                .orElseThrow(() -> new IllegalStateException("Item is empty and cannot store a map color"))
                .setMapColor(rgb);
        return this;
    }

    /**
     * Adds a Bukkit Enchantment of a certain level to this item, in an unsafe way.
     * Works when addEnchantment doesn't.
     *
     * @param enchantment Enchantment
     * @param level Level of the enchantment
     * @return this CommonItemStack
     */
    public CommonItemStack addUnsafeEnchantment(Enchantment enchantment, int level) {
        ItemStack bukkitItem = toBukkit();
        if (bukkitItem == null) {
            throw new IllegalStateException("Cannot add enchantments to an empty item");
        }
        bukkitItem.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    /**
     * Adds a Bukkit Enchantment of a certain level to this item
     *
     * @param enchantment Enchantment
     * @param level Level of the enchantment
     * @return this CommonItemStack
     */
    public CommonItemStack addEnchantment(Enchantment enchantment, int level) {
        ItemStack bukkitItem = toBukkit();
        if (bukkitItem == null) {
            throw new IllegalStateException("Cannot add enchantments to an empty item");
        }
        bukkitItem.addEnchantment(enchantment, level);
        return this;
    }

    /**
     * Hides all attributes of this item, such as 'unbreakable' and potion effects,
     * from the tooltip of this item. Equivalent to calling {@link #addItemFlags(ItemFlag...)}
     * with all the HIDE_ flags.
     *
     * @return this CommonItemStack
     */
    public CommonItemStack hideAllAttributes() {
        return addItemFlags(ItemFlag.values());
    }

    /**
     * Gets all currently set item flags of this item. This returned set is unmodifiable.
     *
     * @return Set item flags, an Empty set if none are set or this item is empty
     */
    public Set<ItemFlag> getItemFlags() {
        ItemStack bukkitItem = toBukkit();
        if (bukkitItem == null) {
            return Collections.emptySet();
        }
        ItemMeta meta = bukkitItem.getItemMeta();
        if (meta == null) {
            return Collections.emptySet();
        }

        return meta.getItemFlags();
    }

    /**
     * Adds all Bukkit ItemFlags specified to this item
     *
     * @param itemFlags ItemFlags to add/set
     * @return this CommonItemStack
     */
    public CommonItemStack addItemFlags(ItemFlag... itemFlags) {
        ItemStack bukkitItem = toBukkit();
        if (bukkitItem == null) {
            throw new IllegalStateException("Cannot set item flags on an empty item");
        }
        ItemMeta meta = bukkitItem.getItemMeta();
        if (meta == null) {
            throw new IllegalStateException("Item of type " + bukkitItem.getType() + " cannot store item flags");
        }
        meta.addItemFlags(itemFlags);
        bukkitItem.setItemMeta(meta);
        return this;
    }

    /**
     * Resets this item so that all item flags are the defaults. This clears all set
     * item flags.
     *
     * @return this CommonItemStack
     */
    public CommonItemStack resetItemFlags() {
        return removeItemFlags(ItemFlag.values());
    }

    /**
     * Removes item flags from this item
     *
     * @param itemFlags Item Flags to remove
     * @return this CommonItemStack
     */
    public CommonItemStack removeItemFlags(ItemFlag... itemFlags) {
        ItemStack bukkitItem = toBukkit();
        if (bukkitItem == null) {
            throw new IllegalStateException("Cannot set item flags on an empty item");
        }
        ItemMeta meta = bukkitItem.getItemMeta();
        if (meta == null) {
            throw new IllegalStateException("Item of type " + bukkitItem.getType() + " cannot store item flags");
        }
        meta.removeItemFlags(itemFlags);
        bukkitItem.setItemMeta(meta);
        return this;
    }

    /**
     * Returns the <b>Read-Only</b> custom data of this ItemStack, if present. If no such
     * custom data is present, returns {@link CommonTagCompound#EMPTY}. You can check for
     * this with {@link #hasCustomData()} or by checking {@link CommonTagCompound#isEmpty()}.<br>
     * <br>
     * The returned data is read-only, unmodifiable. It can only be used to read attributes of
     * the item, not set new ones. To set metadata, clone the tag, or use
     * {@link #getCustomDataCopy()} instead.<br>
     * <br>
     * Note that on Minecraft 1.20.4 and before, the returned tag may also include Vanilla Minecraft
     * item information under Minecraft-specific keys.
     *
     * @return Read-Only Custom Data tag
     */
    public CommonTagCompound getCustomData() {
        return getHandleIfCraftItemStack()
                .map(ItemStackHandle::getCustomData)
                .orElse(CommonTagCompound.EMPTY);
    }

    /**
     * Returns a writable copy of the custom data of this ItemStack, if present. If no such
     * custom data is present, returns a new empty tag. You can check for this with
     * {@link #hasCustomData()} or by checking {@link CommonTagCompound#isEmpty()}.<br>
     * <br>
     * If you only need to read custom data from the item, and not alter it, you can use
     * {@link #getCustomData()} instead.<br>
     * <br>
     * Note that on Minecraft 1.20.4 and before, the returned tag may also include Vanilla Minecraft
     * item information under Minecraft-specific keys.
     *
     * @return Writable Copy of the Custom Data tag
     */
    public CommonTagCompound getCustomDataCopy() {
        return getHandleIfCraftItemStack()
                .map(ItemStackHandle::getCustomDataCopy)
                .orElseGet(CommonTagCompound::new);
    }

    /**
     * Updates the custom data set for this ItemStack. If the input tag is null or empty, removes the
     * custom data. The underlying item must not be empty.<br>
     * <br>
     * <strong>Note:</strong>
     * If the underlying item is not a {@link #isCraftItemStack() CraftItemStack},
     * then the item is cloned and converted into one. As such, this method can change the returned
     * instance of {@link #toBukkit()}.
     *
     * @param customDataTag New Custom Data tag to set, null or empty to remove it
     * @return this CommonItemStack
     * @see #updateCustomData(Consumer)
     */
    public CommonItemStack setCustomData(CommonTagCompound customDataTag) {
        getHandle()
                .orElseThrow(() -> new IllegalStateException("Item is empty and cannot store custom data"))
                .setCustomData(customDataTag);
        return this;
    }

    /**
     * Updates the custom data set for this ItemStack with a callback modifying the original custom data, if any.
     * If there was no previous custom data stored, the consumer receives a fresh new empty tag.
     * The underlying item must not be empty.<br>
     * <br>
     * <strong>Note:</strong>
     * If the underlying item is not a {@link #isCraftItemStack() CraftItemStack},
     * then the item is cloned and converted into one. As such, this method can change the returned
     * instance of {@link #toBukkit()}.
     *
     * @param consumer Callback called with a copy of the current Custom Data tag. The consumer can modify it,
     *                 after which this is set as the new value.
     * @return this CommonItemStack
     */
    public CommonItemStack updateCustomData(Consumer<CommonTagCompound> consumer) {
        getHandle()
                .orElseThrow(() -> new IllegalStateException("Item is empty and cannot store custom data"))
                .updateCustomData(consumer);
        return this;
    }

    /**
     * Gets the Bukkit ItemStack representation of this item. This can be used with
     * Bukkit APIs. Do note that if this CommonItemStack is empty, this method returns
     * null. If you want to be able to modify the empty item, use
     * {@link #toBukkit(boolean) toBukkit(true)} instead.
     *
     * @return Bukkit ItemStack, or <i>null</i> if this CommonItemStack is empty
     */
    public ItemStack toBukkit() {
        return toBukkit(false);
    }

    /**
     * Gets the Bukkit ItemStack representation of this item. This can be used with
     * Bukkit APIs. If <i>returnEmptyItem</i> is <i>true</i>, then if the underlying item
     * is empty, this empty item is returned, usually with type air and amount 0.
     *
     * @param returnEmptyItem True to return an empty Bukkit Item
     * @return Bukkit ItemStack, or <i>null</i> if <i>returnEmptyItem</i> is false
     *         and this item is empty.
     */
    public ItemStack toBukkit(boolean returnEmptyItem) {
        ItemStack bukkitItemStack = this.bukkitItemStack;
        if (bukkitItemStack == null) {
            if (!returnEmptyItem) {
                // Cache empty writable item
                this.bukkitItemStack = bukkitItemStack = ItemUtil.emptyItem();
            }
        } else if (!returnEmptyItem && ItemUtil.isEmpty(bukkitItemStack)) {
            bukkitItemStack = null;
        }

        return bukkitItemStack;
    }

    /**
     * Checks whether this item equals a Bukkit ItemStack. If the input item is null,
     * check that this CommonItemStack is empty, as is Bukkit's behavior.
     *
     * @param itemStack ItemStack to check, null to check for an empty item
     * @return True if this CommonItemStack equals the Bukkit itemStack
     */
    public boolean equalsBukkitItem(ItemStack itemStack) {
        if (this.bukkitItemStack == null) {
            return ItemUtil.isEmpty(itemStack);
        } else {
            return this.bukkitItemStack.equals(itemStack);
        }
    }

    /**
     * Checks whether this item equals another item, while ignoring the amount of
     * the item. This is equivalent to Bukkit's {@link ItemStack#isSimilar(ItemStack)}
     * API.
     *
     * @param itemStack CommonItemStack to compare
     * @return True if this item equals the input item, while ignoring the amount.
     *         Also returns true if both items are empty.
     */
    public boolean equalsIgnoreAmount(CommonItemStack itemStack) {
        if (itemStack.bukkitItemStack == null) {
            return this.isEmpty();
        } else {
            return equalsIgnoreAmount(itemStack.bukkitItemStack);
        }
    }

    /**
     * Checks whether this item equals another item, while ignoring the amount of
     * the item. This is equivalent to Bukkit's {@link ItemStack#isSimilar(ItemStack)}
     * API.
     *
     * @param itemStack Bukkit ItemStack to compare
     * @return True if this item equals the input item, while ignoring the amount.
     *         Also returns true if both items are empty.
     */
    public boolean equalsIgnoreAmount(ItemStack itemStack) {
        if (this.bukkitItemStack == null) {
            return ItemUtil.isEmpty(itemStack);
        } else {
            return this.bukkitItemStack.isSimilar(itemStack);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof CommonItemStack) {
            CommonItemStack other = (CommonItemStack) o;
            if (this.bukkitItemStack == null) {
                return other.isEmpty();
            } else {
                return this.bukkitItemStack.equals(other.bukkitItemStack);
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.isEmpty() ? 0 : this.bukkitItemStack.hashCode();
    }

    @Override
    public String toString() {
        return this.isEmpty() ? "Empty" : this.bukkitItemStack.toString();
    }

    /**
     * Returns a new CommonItemStack instance with a copy of the underlying item
     *
     * @return CommonItemStack clone
     */
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CommonItemStack clone() {
        ItemStack bukkitItemStack = this.bukkitItemStack;
        return of(bukkitItemStack != null ? bukkitItemStack.clone() : null);
    }

    /**
     * Tests how much of this item can be transferred to another item. This can be called
     * before calling {@link #transferTo(CommonItemStack, int)} to see if a full transfer
     * is possible at all.
     *
     * @param to ItemStack to test adding amount to
     * @return Amount that can be taken from this ItemStack and added to the
     *         <i>to</i> ItemStack without violating a max stack size.
     */
    public int testTransferTo(CommonItemStack to) {
        if (this.isEmpty()) {
            return 0;
        }
        if (to.bukkitItemStack == null) {
            return this.getAmount();
        }
        if (!this.equalsIgnoreAmount(to)) {
            return 0;
        }
        return Math.min(this.getAmount(), to.getMaxStackSize() - to.getAmount());
    }

    /**
     * Attempts to transfer this item into another one. I If the destination item stack
     * reached the maximum stack size, or is incompatible with this item, then
     * no more items are transferred. Equivalent to {@link #transferTo(CommonItemStack, int)}
     * with a max amount of -1.<br>
     * <br>
     * <strong>Note:</strong>
     * Note that if the target item is an empty item with a defined type, then it will
     * not transfer items of a different type. It only does this if this item
     * was initialized without type (AIR).
     *
     * @param to Item to transfer to. The amount is added to this item.
     * @return Number of items that were transferred. If greater than 0, then this item
     *         and the input item have been modified.
     */
    public int transferAllTo(CommonItemStack to) {
        return this.transferTo(to, -1);
    }

    /**
     * Attempts to transfer this item into another one. If the destination item stack
     * reached the maximum stack size, or is incompatible with this item, then
     * no more items are transferred.<br>
     * <br>
     * <strong>Note:</strong>
     * Note that if the target item is an empty item with a defined type, then it will
     * not transfer items of a different type. It only does this if this item
     * was initialized without type (AIR).
     *
     * @param to Item to transfer to. The amount is added to this item.
     * @param maxAmount Maximum number of items to transfer over. Use -1 to not impose
     *                  a limit. The max stack size limit is always active.
     * @return Number of items that were transferred. If greater than 0, then this item
     *         and the input item have been modified.
     */
    public int transferTo(CommonItemStack to, int maxAmount) {
        // Ignore if this item is empty
        if (this.isEmpty() || maxAmount == 0) {
            return 0;
        }

        // If this item is null-empty, see if we can assign the full 'from' item
        // If not, take only a part of it and assign that.
        // There is no need to check the max stack size, the from-item is presumed
        // to already adhere to this limit.
        ItemStack fromItemStack = this.bukkitItemStack;
        ItemStack toItemStack = to.bukkitItemStack;
        if (toItemStack == null) {
            int excess;
            if (maxAmount < 0 || (excess = (fromItemStack.getAmount() - maxAmount)) <= 0) {
                // Assign fully
                to.setTo(this);
                this.clear();
                return to.getAmount();
            } else {
                // Split the stack
                to.setToCopyOf(this);
                to.setAmount(maxAmount);
                this.setAmount(excess);
                return maxAmount;
            }
        }

        // Check types are compatible
        if (!equalsIgnoreAmount(to)) {
            return 0;
        }

        // Calculate the amount to transfer
        int amountToTransfer = fromItemStack.getAmount();
        if (maxAmount >= 0) {
            amountToTransfer = Math.min(amountToTransfer, maxAmount);
        }
        amountToTransfer = Math.min(amountToTransfer, to.getMaxStackSize() - toItemStack.getAmount());
        if (amountToTransfer <= 0) {
            return 0;
        }

        to.addAmount(amountToTransfer);

        if (amountToTransfer == fromItemStack.getAmount()) {
            this.clear();
        } else {
            this.subtractAmount(amountToTransfer);
        }

        return amountToTransfer;
    }

    /**
     * Tests how much of this item can be transferred to another inventory. If all inventory
     * slots of the destination inventory are full and incompatible with this item, then
     * this limits the amount. If there is room, returns up to the amount of this item.
     *
     * @param to Inventory to test adding this item to
     * @return Amount that can be taken from this ItemStack and added to the
     *         <i>to</i> Inventory.
     */
    public int testTransferTo(Inventory to) {
        if (this.isEmpty()) {
            return 0;
        }

        int maxCanTransfer = this.getAmount();
        int totalCanTransfer = 0;
        int toSlotCount = to.getSize();
        for (int toSlot = 0; toSlot < toSlotCount; toSlot++) {
            CommonItemStack toItem = of(to.getItem(toSlot));
            totalCanTransfer += this.testTransferTo(toItem);
            if (totalCanTransfer >= maxCanTransfer) {
                totalCanTransfer = maxCanTransfer;
                break;
            }
        }
        return totalCanTransfer;
    }

    /**
     * Attempts to transfer this entire item into an inventory. First all pre-existing
     * items in the inventory that match this item are filled up to the stack size,
     * then empty inventory slots are filled up.<br>
     * <br>
     * If this entire item is transferred, then this item becomes {@link #isEmpty() empty}.
     * If more than 0 is returned, this item was changed.
     *
     * @param to Inventory to transfer this item to
     * @return Amount of this item transferred to the inventory
     */
    public int transferAllTo(Inventory to) {
        return transferTo(to, -1);
    }

    /**
     * Attempts to transfer this item into an inventory. First all pre-existing
     * items in the inventory that match this item are filled up to the stack size,
     * then empty inventory slots are filled up.<br>
     * <br>
     * If this entire item is transferred, then this item becomes {@link #isEmpty() empty}.
     * If more than 0 is returned, this item was changed.
     *
     * @param to Inventory to transfer this item to
     * @param maxAmount Maximum amount of the item to transfer, -1 for unlimited
     * @return Amount of this item transferred to the inventory
     */
    public int transferTo(Inventory to, int maxAmount) {
        int remainingAmount = getAmount(); // 0 if empty
        if (maxAmount >= 0) {
            remainingAmount = Math.min(remainingAmount, maxAmount);
        }

        // First stack in existing slots, then fill empty slots
        int totalTransferred = transferToInvInternal(to, remainingAmount, false);
        remainingAmount -= totalTransferred;
        totalTransferred += transferToInvInternal(to, remainingAmount, true);
        return totalTransferred;
    }

    private int transferToInvInternal(Inventory to, int remainingAmount, boolean emptySlots) {
        if (remainingAmount <= 0) {
            return 0;
        }

        int totalTransferred = 0;
        int inventorySize = to.getSize();
        for (int i = 0; i < inventorySize; i++) {
            CommonItemStack item = of(to.getItem(i));
            if (emptySlots != item.isEmpty()) {
                continue;
            }

            // Note: if item is empty, transfers the full item possible
            int transferred = this.transferTo(item, remainingAmount);
            if (transferred > 0) {
                remainingAmount -= transferred;
                totalTransferred += transferred;
                to.setItem(i, item.toBukkit());
                if (remainingAmount <= 0) {
                    break;
                }
            }
        }
        return totalTransferred;
    }

    /**
     * Attempts to {@link #transferTo(CommonItemStack, int) transfer} items from an inventory
     * into this item if they match this item, up to the max stacking size of
     * this item. If this item is {@link #isEmpty() empty}, does not take anything.
     *
     * @param inventory Inventory to take from
     * @return Number of items taken and added to the amount of this item
     * @see #takeFrom(Inventory, int)
     * @see #take(Inventory, Predicate)
     */
    public int takeAllFrom(Inventory inventory) {
        return takeFrom(inventory, -1);
    }

    /**
     * Attempts to {@link #transferTo(CommonItemStack, int) transfer} items from an inventory
     * into this item if they match this item, up to maxAmount or the max stacking size of
     * this item. If this item is {@link #isEmpty() empty}, does not take anything.
     *
     * @param inventory Inventory to take from
     * @param maxAmount Maximum amount to take, -1 for unlimited (up to stack size)
     * @return Number of items taken and added to the amount of this item
     * @see #take(Inventory, Predicate, int)
     */
    public int takeFrom(Inventory inventory, int maxAmount) {
        int remainingAmount = getMaxStackSize() - getAmount();
        if (maxAmount >= 0) {
            remainingAmount = Math.min(remainingAmount, maxAmount);
        }
        if (remainingAmount <= 0) {
            return 0;
        }

        int amountTaken = 0;
        int inventorySize = inventory.getSize();
        for (int i = 0; i < inventorySize; i++) {
            CommonItemStack item = of(inventory.getItem(i));
            int transferred = item.transferTo(this, remainingAmount);
            if (transferred > 0) {
                remainingAmount -= transferred;
                amountTaken += transferred;
                inventory.setItem(i, item.toBukkit());
                if (remainingAmount <= 0) {
                    break;
                }
            }
        }

        return amountTaken;
    }

    /**
     * Attempts to take a certain item from an inventory. The filter predicate is used to find the
     * eligible items. Once the condition is met, it takes the maximum amount of items possible. The
     * max stacking size limit of the item is active.
     *
     * @param inventory Inventory to take items from
     * @param filter Filter to identify the item to take. Once an item is taken, it is not checked again
     *               for further items that are identical. Ignored if null.
     * @return Taken Item. Can be an {@link #isEmpty() Empty} item stack if no items were taken.
     */
    public static CommonItemStack take(Inventory inventory, Predicate<CommonItemStack> filter) {
        return take(inventory, filter, -1);
    }

    /**
     * Attempts to take a certain item from an inventory. The filter predicate is used to find the
     * eligible items. Once the condition is met, it takes the maximum amount of items possible. The
     * maxAmount controls a limit. In addition, the max stacking size limit of the item is active.
     *
     * @param inventory Inventory to take items from
     * @param filter Filter to identify the item to take. Once an item is taken, it is not checked again
     *               for further items that are identical. Ignored if null.
     * @param maxAmount Maximum number of items to take. If -1, takes as many of the item as possible,
     *                  up to the max stacking size of the item.
     * @return Taken Item. Can be an {@link #isEmpty() Empty} item stack if no items were taken.
     */
    public static CommonItemStack take(Inventory inventory, Predicate<CommonItemStack> filter, int maxAmount) {
        if (maxAmount == 0) {
            return empty();
        }

        if (filter == null) {
            filter = LogicUtil.alwaysTruePredicate();
        }

        CommonItemStack result = empty();
        boolean foundItem = false;
        int inventorySize = inventory.getSize();
        int remainingAmount = 0; // Set to max stack size / maxAmount later
        for (int i = 0; i < inventorySize; i++) {
            CommonItemStack item = of(inventory.getItem(i));
            if (!foundItem) {
                // Identify the first item that matches that we can take
                if (item.isEmpty() || !filter.test(item)) {
                    continue;
                }

                // Compute remaining amount based on the max stack size and maxAmount parameters
                remainingAmount = result.getMaxStackSize();
                if (maxAmount >= 0 && maxAmount < remainingAmount) {
                    remainingAmount = maxAmount;
                }
            }

            // Transfer the item into the result. The first time, transfers into an empty item.
            int transferred = item.transferTo(result, remainingAmount);
            if (transferred > 0) {
                foundItem = true;
                remainingAmount -= transferred;
                inventory.setItem(i, item.toBukkit());
                if (remainingAmount <= 0) {
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Transfers items from an inventory into an item if they match. The filter controls what items are
     * selected for transferring. Unlimited amount of items are transferred. If the output
     * item is not empty, and does not match the filter, then no items are taken from the inventory.
     *
     * @param from Inventory to take items from
     * @param to CommonItemStack item to add items taken from the inventory to. Can be empty.
     * @param filter Filter predicate to select the items that can be transferred. Ignored if null.
     * @return Total amount (their summed amounts) of items transferred.
     */
    public static int transferAll(Inventory from, CommonItemStack to, Predicate<CommonItemStack> filter) {
        return transfer(from, to, filter, -1);
    }

    /**
     * Transfers items from an inventory into an item if they match. The filter controls what items are
     * selected for transferring. Up to the maximum amount of items are transferred. If the output
     * item is not empty, and does not match the filter, then no items are taken from the inventory.
     *
     * @param from Inventory to take items from
     * @param to CommonItemStack item to add items taken from the inventory to. Can be empty.
     * @param filter Filter predicate to select the items that can be transferred. Ignored if null.
     * @param maxAmount Maximum total amount of items to transfer. -1 for unlimited.
     * @return Total amount (their summed amounts) of items transferred.
     */
    public static int transfer(Inventory from, CommonItemStack to, Predicate<CommonItemStack> filter, int maxAmount) {
        if (to.isEmpty()) {
            to.setTo(take(from, filter, maxAmount));
            return to.getAmount();
        } else if (filter == null || filter.test(to)) {
            return to.takeFrom(from, maxAmount);
        } else {
            return 0;
        }
    }

    /**
     * Transfers all items from one inventory to another. The filter controls what items are selected for transferring.
     * Unlimited amount of items are transferred.
     *
     * @param from Inventory to take items from
     * @param to Inventory to transfer items to
     * @param filter Filter predicate to select the items that can be transferred. Ignored if null.
     * @return Total amount (their summed amounts) of items transferred.
     */
    public static int transferAll(Inventory from, Inventory to, Predicate<CommonItemStack> filter) {
        return transfer(from, to, filter, -1);
    }

    /**
     * Transfers items from one inventory to another. The filter controls what items are selected for transferring.
     * Up to the maximum amount of items are transferred.
     *
     * @param from Inventory to take items from
     * @param to Inventory to transfer items to
     * @param filter Filter predicate to select the items that can be transferred. Ignored if null.
     * @param maxAmount Maximum total amount of items to transfer. -1 for unlimited.
     * @return Total amount (their summed amounts) of items transferred.
     */
    public static int transfer(Inventory from, Inventory to, Predicate<CommonItemStack> filter, int maxAmount) {
        if (maxAmount == 0) {
            return 0;
        }

        if (filter == null) {
            filter = LogicUtil.alwaysTruePredicate();
        }

        int fromSlotCount = from.getSize();
        int remainingAmount = maxAmount; // -1 for unlimited
        int totalTransferred = 0;
        for (int fromSlot = 0; fromSlot < fromSlotCount; fromSlot++) {
            CommonItemStack fromItem = of(from.getItem(fromSlot));
            if (fromItem.isEmpty() || !filter.test(fromItem)) {
                continue;
            }

            int transferred = fromItem.transferTo(to, remainingAmount);
            if (transferred > 0) {
                totalTransferred += transferred;
                if (remainingAmount >= 0) {
                    remainingAmount -= transferred;
                }
                from.setItem(fromSlot, fromItem.toBukkit());
            }
        }

        return totalTransferred;
    }

    /**
     * Creates a Stream of all non-empty ItemStack contents in an inventory.
     *
     * @param inventory Inventory
     * @return Stream of all non-empty ItemStacks in this inventory
     */
    public static Stream<CommonItemStack> streamOfContents(final Inventory inventory) {
        final int slotCount = inventory.getSize();
        return IntStream.range(0, slotCount)
                .mapToObj(inventory::getItem)
                .filter(Objects::nonNull)
                .map(CommonItemStack::of);
    }
}
