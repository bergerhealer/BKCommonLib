# CommonItemStack

`CommonItemStack` wraps a Bukkit item stack and provides a cross-version API for reading and changing item properties. It also exposes custom item NBT through BKCommonLib's [`CommonTagCompound`](commontag.md) API, which plugins can use to store their own item identifiers and metadata.

## Key files

- [`CommonItemStack`](../src/main/java/com/bergerkiller/bukkit/common/inventory/CommonItemStack.java): Bukkit/NMS item wrapper, item property accessors, custom data, and inventory transfer helpers.
- [`CommonTagCompound`](../src/main/java/com/bergerkiller/bukkit/common/nbt/CommonTagCompound.java): typed NBT compound API used for custom item data.
- [`ItemUtil`](../src/main/java/com/bergerkiller/bukkit/common/utils/ItemUtil.java): item creation, cloning, and related utilities.

## Wrapping and ownership

Use `CommonItemStack.of(item)` to wrap an existing Bukkit `ItemStack`, or `CommonItemStack.create(material, amount)` to create a new item. `CommonItemStack.empty()` creates a non-null empty wrapper; a type can be assigned before setting its amount. A `CommonItemStack` itself is never `null`, and a `null` Bukkit item is treated as empty.

`of()` initially wraps the supplied Bukkit stack, so changes can affect that item. Use `copyOf(item)` or `clone()` when you need an independent copy. The wrapper is mutable and most setters return `this`, allowing chained calls:

```java
CommonItemStack item = CommonItemStack.create(Material.PAPER, 1)
        .setCustomNameMessage("Travel Pass")
        .addLoreMessage("One use");
```

`toBukkit()` returns the Bukkit representation, or `null` if the item is empty. Pass `true` to `toBukkit(true)` when a non-null empty Bukkit stack is needed.

## Cross-version item properties

The wrapper provides consistent access to common properties such as material, amount, damage, maximum stack size and durability, repair cost, unbreakable state, display name and lore, enchantments and item flags, skull profile, custom model data, item model, and filled-map information. Prefer these methods over direct NMS access when working with item properties that vary between server versions.

For item movement between stacks or inventories, use the `transferTo`, `transferAllTo`, and related methods. These enforce item compatibility and stack-size limits and return the amount transferred.

### Custom name and lore

Use `setCustomNameMessage()` for a legacy-formatted string, or `setCustomName(ChatText)` for formatted text. Pass `null` to clear the custom name. `addLoreMessage()` appends a lore line from a legacy-formatted string, `addLore(ChatText)` appends formatted text, and `clearLores()` removes all lore:

```java
CommonItemStack item = CommonItemStack.create(Material.PAPER, 1)
        .setCustomNameMessage("§aTravel Pass")
        .addLoreMessage("§7One use");
```

These are item display properties, not plugin metadata. Use custom NBT for identifying an item or storing data that your plugin needs to read back.

### Mimic another item type

`mimicAsType(material)` changes the item's behavior type while preserving its current visual model, where the server supports item model components (Minecraft 1.21.2+). This is useful when an item should act like one material but appear like another—for example, using a non-block behavior to avoid placing a block when the player uses a custom tool:

```java
CommonItemStack tool = CommonItemStack.create(Material.STONE, 1)
        .mimicAsType(Material.PAPER);
```

Here the item uses the original stone model but has paper behavior. On older servers without item model component support, `mimicAsType()` does nothing; check `CommonItemStack.canSetItemModel()` when the behavior must be guaranteed.

### Hide the tooltip

`hideTooltip()` hides the entire item tooltip when the player hovers over the item, including its name and lore. This differs from `hideAllAttributes()`, which hides item attributes such as enchantments or unbreakable status but leaves the tooltip visible.

## Store plugin metadata

Use `updateCustomData()` to modify a writable copy of the item's custom tag. Give plugin-owned keys a unique prefix so they do not conflict with other plugins' data:

```java
CommonItemStack item = CommonItemStack.of(stack);
item.updateCustomData(tag -> tag.putValue("exampleplugin:item_id", "travel_pass"));

String itemId = item.getCustomData().getValue("exampleplugin:item_id", "");
if (itemId.equals("travel_pass")) {
    // Handle this plugin's item
}
```

`getCustomData()` is a read-only view and returns `CommonTagCompound.EMPTY` when there is no data. Do not mutate it; use `updateCustomData()` for edits, or `getCustomDataCopy()` followed by `setCustomData()` when a separate editing step is needed. Updating only your own key preserves other custom data. `setCustomData(null)` or an empty compound removes the item's custom data.

Custom data can identify a special item and hold plugin-specific values, but it does not by itself prevent an item from being copied or forged. Validate the item's material and metadata as appropriate for the action being performed.

## Bukkit stack replacement caveat

Some operations need Minecraft's internal `ItemStack`. If the wrapped item is not already backed by a CraftBukkit `CraftItemStack`, BKCommonLib may create a Craft copy and make that the wrapper's new Bukkit representation. Custom-data edits can therefore change the object returned by `toBukkit()`.

When modifying an item from an inventory, write the resulting Bukkit item back to the slot after operations that may convert its backing stack:

```java
CommonItemStack item = CommonItemStack.of(inventory.getItem(slot));
item.updateCustomData(tag -> tag.putValue("exampleplugin:item_id", "travel_pass"));
inventory.setItem(slot, item.toBukkit());
```

Use `isCraftItemStack()` when knowing whether the current Bukkit item is already NMS-backed is important. For general inventory code, explicitly writing the result back is the safe approach.

For the NBT value/tag distinction and additional custom-data examples, see the [CommonTag guide](commontag.md) and its [examples](examples/commontag.md).
