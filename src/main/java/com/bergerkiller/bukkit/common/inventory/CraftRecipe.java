package com.bergerkiller.bukkit.common.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.reflection.classes.RecipeRef;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;

public class CraftRecipe {
	private final ItemStack[] input;
	private final ItemStack[] output;

	private CraftRecipe(Collection<ItemStack> input, ItemStack output) {
		// Convert the input
		List<ItemStack> newinput = new ArrayList<ItemStack>(input.size());
		boolean create;
		for (ItemStack item : input) {
			if (LogicUtil.nullOrEmpty(item)) {
				continue;
			}

			create = true;
			for (ItemStack newitem : newinput) {
				if (newitem.getTypeId() == item.getTypeId() && newitem.getDurability() == item.getDurability()) {
					ItemUtil.addAmount(newitem, 1);
					create = false;
					break;
				}
			}
			if (create) {
				item = item.clone();
				item.setAmount(1);
				newinput.add(item);
			}
		}
		this.input = newinput.toArray(new ItemStack[0]);

		// Convert the output
		List<ItemStack> newoutput = new ArrayList<ItemStack>(1);
		newoutput.add(output.clone());
		// Deal with special cases that demand an additional item (added elsewhere)
		for (ItemStack stack : newinput) {
			if (BlockUtil.isType(stack.getTypeId(), Material.LAVA_BUCKET, Material.WATER_BUCKET, Material.MILK_BUCKET)) {
				newoutput.add(new ItemStack(Material.BUCKET, stack.getAmount()));
			}
		}
		this.output = newoutput.toArray(new ItemStack[0]);
	}

	/**
	 * Gets the input item at the index specified
	 * 
	 * @param index of the item
	 * @return input Item
	 */
	public ItemStack getInput(int index) {
		return this.input[index];
	}

	/**
	 * Gets all the input items
	 * 
	 * @return input Items
	 */
	public ItemStack[] getInput() {
		return this.input;
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
	 * Gets the total amount of items, this adds all the amounts of all the items together<br>
	 * <b>This is not the length of the Input item array!</b>
	 * 
	 * @return Input item amount
	 */
	public int getInputSize() {
		int count = 0;
		for (ItemStack item : this.input) {
			count += item.getAmount();
		}
		return count;
	}

	/**
	 * Gets the total amount of items, this adds all the amounts of all the items together<br>
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
	 * Performs this recipe multiple times in the inventory specified
	 * 
	 * @param inventory to craft in
	 * @param limit the amount of times it can craft
	 * @return the amount of times it crafted
	 */
	public int craft(Inventory inventory, int limit) {
		int amount;
		for (amount = 0; amount < limit && craft(inventory); amount++);
		return amount;
	}

	/**
	 * Performs this recipe once in the inventory specified
	 * 
	 * @param inventory to craft in
	 * @return True if crafting occurred, False if not
	 */
	public boolean craft(Inventory inventory) {
		// contains the required items?
		for (ItemStack item : this.input) {
			if (ItemUtil.getItemCount(inventory, item.getTypeId(), item.getDurability()) < item.getAmount()) {
				return false;
			}
		}
		// contains enough room to put in the results?
		if (!ItemUtil.canTransferAll(this.output, inventory)) {
			return false;
		}
		// actually transfer everything...
		for (ItemStack item : this.input) {
			ItemUtil.removeItems(inventory, item);
		}
		for (ItemStack item : this.output) {
			ItemUtil.transfer(ItemUtil.cloneItem(item), inventory, Integer.MAX_VALUE);
		}
		return true;
	}

	/**
	 * Creates a new Craft Recipe from an IRecipe instance.
	 * This method is not recommended to be used.
	 * 
	 * @param recipe to use
	 * @return the CraftRecipe, or null on failure
	 */
	public static CraftRecipe create(Object recipe) {
		final ItemStack output = RecipeRef.getOutput(recipe);
		if (RecipeRef.SHAPED_TEMPLATE.isInstance(recipe)) {
			return create(RecipeRef.shapedInput.get(recipe), output);
		} else if (RecipeRef.SHAPELESS_TEMPLATE.isInstance(recipe)) {
			return create(RecipeRef.shapelessInput.get(recipe), output);
		} else {
			return null;
		}
	}

	public static CraftRecipe create(Collection<ItemStack> input, ItemStack output) {
		if (LogicUtil.nullOrEmpty(input) || LogicUtil.nullOrEmpty(output)) {
			return null;
		} else {
			CraftRecipe rval = new CraftRecipe(input, output);
			// Check that input and output are not causing a loop
			// For example Sandstone has an infinite crafting loop going on
			// (You can craft 4 Sandstone using 4 Sandstone...yeah)
			if (rval.input.length == 1 && rval.output.length == 1 && rval.input[0].getTypeId() == rval.output[0].getTypeId()) {
				return null;
			}
			return rval;
		}
	}
}
