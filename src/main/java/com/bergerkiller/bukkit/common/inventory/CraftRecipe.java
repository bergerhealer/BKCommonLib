package com.bergerkiller.bukkit.common.inventory;

import com.bergerkiller.bukkit.common.utils.*;
import com.bergerkiller.generated.net.minecraft.world.item.crafting.IRecipeHandle;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CraftRecipe {

    private final CraftInputSlot[] inputSlots;
    private final ItemStack[] input;
    private final ItemStack[] output;

    private CraftRecipe(Collection<CraftInputSlot> unmodifiedIngredients, ItemStack output) {
        // Merge the input slots when possible
        ArrayList<CraftInputSlot> inputSlotsList = new ArrayList<CraftInputSlot>(unmodifiedIngredients.size());
        for (CraftInputSlot unmodInput : unmodifiedIngredients) {
            boolean merged = false;
            for (int i = 0; i < inputSlotsList.size(); i++) {
                CraftInputSlot mergedSlot = inputSlotsList.get(i).tryMergeWith(unmodInput);
                if (merged = (mergedSlot != null)) {
                    inputSlotsList.set(i, mergedSlot);
                    break;
                }
            }
            if (!merged) {
                inputSlotsList.add(unmodInput);
            }
        }
        this.inputSlots = inputSlotsList.toArray(new CraftInputSlot[inputSlotsList.size()]);

        // Take the default item for all inputs and merge those, too
        List<ItemStack> inputItemsList = new ArrayList<ItemStack>(inputSlotsList.size());
        boolean create;
        for (CraftInputSlot itemSlot : inputSlotsList) {
            ItemStack item = itemSlot.getDefaultChoice();

            if (LogicUtil.nullOrEmpty(item)) {
                continue;
            }

            item = item.clone();
            create = true;
            for (ItemStack newitem : inputItemsList) {
                if (ItemUtil.equalsIgnoreAmount(item, newitem)) {
                    ItemUtil.addAmount(newitem, 1);
                    create = false;
                    break;
                }
            }
            if (create) {
                item.setAmount(1);
                inputItemsList.add(item);
            }
        }
        this.input = inputItemsList.toArray(new ItemStack[inputItemsList.size()]);

        // Convert the output
        List<ItemStack> newoutput = new ArrayList<ItemStack>(1);
        newoutput.add(output.clone());
        // Deal with special cases that demand an additional item (added elsewhere)
        for (ItemStack stack : inputItemsList) {
            if (BlockUtil.isType(stack, Material.LAVA_BUCKET, Material.WATER_BUCKET, Material.MILK_BUCKET)) {
                newoutput.add(new ItemStack(Material.BUCKET, stack.getAmount()));
            }
        }
        this.output = newoutput.toArray(new ItemStack[0]);
    }

    /**
     * Gets the input item at the index specified<br>
     * <br>
     * <b>Deprecated:</b> use of {@link #getInputSlots()} is preferred to allow multiple-choice inputs
     *
     * @param index of the item
     * @return input Item
     */
    @Deprecated
    public ItemStack getInput(int index) {
        return this.getInput()[index];
    }

    /**
     * Gets all the input items.<br>
     * <br>
     * <b>Deprecated:</b> use of {@link #getInputSlots()} is preferred to allow multiple-choice inputs
     *
     * @return input Items
     */
    @Deprecated
    public ItemStack[] getInput() {
        return this.input;
    }

    /**
     * Gets a list of all non-empty input slots that need to be filled for this recipe.
     * Each input slot is multiple-choice.
     * 
     * @return input slots
     */
    public CraftInputSlot[] getInputSlots() {
        return this.inputSlots;
    }

    /**
     * Gets all the output items
     *
     * @return output Items
     */
    public ItemStack[] getOutput() {
        return this.output;
    }

    /**
     * Gets the total amount of items, this adds all the amounts of all the
     * items together<br>
     * <b>This is not the length of the Input item array!</b><br>
     * <br>
     * <b>Deprecated:</b> use of {@link #getInputSlots()} is preferred to allow multiple-choice inputs
     *
     * @return Input item amount
     */
    @Deprecated
    public int getInputSize() {
        int count = 0;
        for (ItemStack item : this.getInput()) {
            count += item.getAmount();
        }
        return count;
    }

    /**
     * Gets the total amount of items, this adds all the amounts of all the
     * items together<br>
     * <b>This is not the length of the Output item array!</b>
     *
     * @return Output item amount
     */
    public int getOutputSize() {
        int count = 0;
        for (ItemStack item : this.output) {
            count += item.getAmount();
        }
        return count;
    }

    /**
     * Checks whether the input items of this recipe are contained within an
     * Inventory
     *
     * @param inventory to check
     * @return True if the items are available, False if not
     */
    public boolean containsInput(Inventory inventory) {
        return this.testCraftOnce(ItemUtil.cloneInventory(inventory), false);
    }

    /**
     * Performs this recipe multiple times in the inventory specified
     *
     * @param inventory to craft in
     * @param itemlimit the max amount of resulting items
     * @return the amount of resulting items that were crafted
     */
    public int craftItems(Inventory inventory, int itemlimit) {
        int lim = MathUtil.floor((double) itemlimit / (double) this.output[0].getAmount());
        return this.craft(inventory, lim) * this.output[0].getAmount();
    }

    /**
     * Performs this recipe once in the inventory specified
     *
     * @param inventory to craft in
     * @return True if crafting occurred, False if not
     */
    public boolean craft(Inventory inventory) {
        return craft(inventory, 1) == 1;
    }

    /**
     * Performs this recipe multiple times in the inventory specified
     *
     * @param inventory to craft in
     * @param limit the amount of times it can craft
     * @return the amount of times it crafted
     */
    public int craft(Inventory inventory, int limit) {
        // Before cloning everything, check whether we can craft at all
        if (!this.containsInput(inventory)) {
            return 0;
        }

        // Create a (temporary) clone of the inventory to work with
        final ItemStack[] items = inventory.getContents();
        final int size = items.length;
        final Inventory inventoryClone = new InventoryBaseImpl(items, true);
        int amount, i;

        // Craft items until the limit is reached, or crafting is impossible
        // Below is the craftloop label, which is used to break out of crafting
        for (amount = 0; amount < limit; amount++) {
            // Attempt to craft on the cloned inventory
            if (!this.testCraftOnce(inventoryClone, true)) {
                break;
            }

            // Crafting was successful, transfer items over
            // Be sure NOT to produce new ItemStack instances!
            for (i = 0; i < size; i++) {
                ItemStack newItem = inventoryClone.getItem(i);
                if (LogicUtil.nullOrEmpty(newItem)) {
                    items[i] = null;
                } else if (items[i] == null) {
                    items[i] = newItem.clone();
                } else {
                    // Transfer info and amount
                    ItemUtil.transferInfo(newItem, items[i]);
                    items[i].setAmount(newItem.getAmount());
                }
            }
        }

        // Update input inventory with the new items
        inventory.setContents(items);
        return amount;
    }

    // attempts to craft this recipe using the items in an inventory
    // if this fails, false is returned. Inventory will always lose items.
    private boolean testCraftOnce(Inventory inventory, boolean addOutputItems) {
        // First do all input slots with only one choice (the MUST)
        for (CraftInputSlot input : this.inputSlots) {
            if (input.getChoices().length == 1 && !input.takeFrom(inventory)) {
                return false;
            }
        }

        // Then do all the input slots with more than one choice
        for (CraftInputSlot input : this.inputSlots) {
            if (input.getChoices().length > 1 && !input.takeFrom(inventory)) {
                return false;
            }
        }

        // Attempt to add the result from crafting as well
        if (addOutputItems) {

            // add resulting items to inventory
            for (ItemStack item : this.output) {
                ItemStack cloned = ItemUtil.cloneItem(item);
                ItemUtil.transfer(cloned, inventory, Integer.MAX_VALUE);
                if (!LogicUtil.nullOrEmpty(cloned)) {
                    // Could not add result (inventory is full), unsuccessful
                    return false;
                }
            }

        }

        return true;
    }

    /**
     * Creates a new Craft Recipe from an IRecipe instance. This method is not
     * recommended to be used.
     *
     * @param recipe to use
     * @return the CraftRecipe, or null on failure
     */
    @Deprecated
    public static CraftRecipe create(Object recipe) {
        return create(IRecipeHandle.createHandle(recipe));
    }

    public static CraftRecipe create(IRecipeHandle recipe) {
        final ItemStack output = recipe.getOutput();
        final List<CraftInputSlot> ingredients = recipe.getIngredients();
        if (ingredients != null) {
            return createSlots(ingredients, output);
        } else {
            return null;
        }
    }

    public static CraftRecipe createSlots(Collection<CraftInputSlot> ingredients, ItemStack output) {
        if (LogicUtil.nullOrEmpty(ingredients) || LogicUtil.nullOrEmpty(output)) {
            return null;
        } else {
            CraftRecipe rval = new CraftRecipe(ingredients, output);
            // Check that input and output are not causing a loop
            // For example Sandstone has an infinite crafting loop going on
            // (You can craft 4 Sandstone using 4 Sandstone...yeah)
            if (rval.input.length == 1 && rval.output.length == 1 && rval.input[0].getType() == rval.output[0].getType()) {
                return null;
            }
            return rval;
        }
    }

    /**
     * Deprecated: use {@link #createSlots(inputs, output)} instead to enable multiple-choice inputs
     */
    @Deprecated
    public static CraftRecipe create(Collection<ItemStack> inputs, ItemStack output) {
        if (LogicUtil.nullOrEmpty(inputs) || LogicUtil.nullOrEmpty(output)) {
            return null;
        } else {
            ArrayList<CraftInputSlot> slots = new ArrayList<CraftInputSlot>();
            for (ItemStack input : inputs) {
                slots.add(new CraftInputSlot(new ItemStack[] {input}));
            }
            return createSlots(slots, output);
        }
    }
}
