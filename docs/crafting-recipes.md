# Crafting Recipes

BKCommonLib's `RecipeUtil` can look up crafting recipes by their output item and craft those results directly from a Bukkit inventory. It adapts the server's version-specific recipe registry into `CraftRecipe` and `CraftInputSlot` objects, so callers do not need to read NMS recipe classes.

This is inventory-based crafting logic; it does not open a crafting UI or simulate a crafting grid. The API described here covers crafting recipes, not furnace/smelting recipes or fuel. `RecipeUtil` has separate methods for furnace results and fuel queries.

## Key files

- [`RecipeUtil`](../src/main/java/com/bergerkiller/bukkit/common/utils/RecipeUtil.java): looks up recipes by output and crafts them from an inventory.
- [`CraftRecipe`](../src/main/java/com/bergerkiller/bukkit/common/inventory/CraftRecipe.java): normalized recipe inputs and outputs, inventory checks, and crafting operations.
- [`CraftInputSlot`](../src/main/java/com/bergerkiller/bukkit/common/inventory/CraftInputSlot.java): one ingredient requirement, including its set of alternative item choices.
- [`item_crafting_recipes.txt`](../src/main/templates/com/bergerkiller/templates/net/minecraft/world/item_crafting_recipes.txt): cross-version adapters for recipe outputs, ingredients, and recipe-manager access.

## Find recipes by result

Call `RecipeUtil.getCraftingRequirements(type, data)` to get all supported crafting recipes producing a material and legacy data value. Pass `null` for any material and `-1` for any data:

```java
CraftRecipe[] recipes = RecipeUtil.getCraftingRequirements(Material.BREAD, -1);
for (CraftRecipe recipe : recipes) {
    // Each entry is one way to craft the requested result
}
```

The returned array can contain multiple recipes for the same result. Each `CraftRecipe` exposes its input requirements through `getInputSlots()` and the resulting items through `getOutput()`. An input slot can have multiple acceptable choices, such as alternative items represented by a Minecraft ingredient. `getInput()` is deprecated because it reduces each slot to a single default choice and cannot represent those alternatives.

BKCommonLib obtains recipes from the server's recipe manager, unwraps version-specific recipe holders, and keeps crafting recipes (shaped, shapeless, or supported complex recipes). Other recipe categories, such as furnace and stonecutting recipes, are filtered out. Recipes whose inputs or outputs cannot be represented by this API are omitted. Some complex recipes, such as fireworks, receive a specific simplified representation.

## Craft from an inventory

`RecipeUtil.craftItems()` finds recipes for the requested result and tries them against the supplied inventory up to the item limit:

```java
RecipeUtil.craftItems(Material.BREAD, -1, inventory, 16);
```

The limit is the maximum number of output items, not the number of recipe executions. If the result stack contains multiple items, fewer executions may fit within the requested limit. The overload accepting `ItemParser` uses its material, data, and optional amount as the target and output limit.

`CraftRecipe.craft(inventory)` attempts one execution; `craft(inventory, limit)` attempts a number of executions and returns that count. `craftItems(inventory, itemLimit)` limits the number of primary output items and returns the number produced. These methods consume ingredients and add outputs to the inventory. An attempt is only committed when the ingredients can be taken and the outputs (including any container return items) fit. If the inventory has no room for a result, that attempt does not consume ingredients.

`RecipeUtil.craftItems()` processes matching recipes in the order supplied by the server recipe manager, reducing the remaining output limit as each recipe crafts. It can therefore use multiple alternative recipes for the same result, depending on which ingredients are available in the inventory.

## Ingredient matching and limitations

`CraftInputSlot` models a single required ingredient as one or more acceptable item stacks. During inventory crafting, it searches slots for those choices and consumes the required amount; recipe slots with one required choice are checked before slots with alternatives. Repeated identical single-choice ingredients are merged into one amount requirement.

This is a convenience approximation of crafting, not a call to Minecraft's live recipe matcher. The inventory path matches choices by material and legacy durability/data (including a wildcard durability choice); it does not reproduce every modern component, tag, or grid-specific recipe rule. Use it where this simplified inventory-level behavior is appropriate, and validate inputs separately if a mechanic depends on exact vanilla crafting semantics.

The normalized recipe output also accounts for the empty bucket returned by recipes consuming lava, water, or milk buckets. Recipe-specific behavior beyond what can be represented as inventory inputs and outputs may not be reproduced.

## Change entry points

- To change which server recipes are exposed, update the version-conditional `RecipeManager` and `Recipe` adapters in `item_crafting_recipes.txt`.
- To change output filtering or automatic crafting, update `RecipeUtil`.
- To change ingredient normalization, matching, or inventory consumption, update `CraftRecipe` and `CraftInputSlot` together.
