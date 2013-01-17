package com.bergerkiller.bukkit.common.inventory;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.NativeUtil;
import net.minecraft.server.v1_4_R1.IRecipe;
import net.minecraft.server.v1_4_R1.ItemStack;
import net.minecraft.server.v1_4_R1.MathHelper;
import net.minecraft.server.v1_4_R1.ShapedRecipes;
import net.minecraft.server.v1_4_R1.ShapelessRecipes;

public class CraftRecipe {
	private static final SafeField<ItemStack[]> srItems = new SafeField<ItemStack[]>(ShapedRecipes.class, "items");
	private static final SafeField<List<ItemStack>> slIngredients = new SafeField<List<ItemStack>>(ShapelessRecipes.class, "ingredients");

	private CraftRecipe(ItemStack[] input, ItemStack output) {
		List<ItemStack> newinput = new ArrayList<ItemStack>(input.length);
		boolean create;
		for (ItemStack item : input) {
			if (item == null)
				continue;
			create = true;
			for (ItemStack newitem : newinput) {
				if (newitem.id == item.id && newitem.getData() == item.getData()) {
					newitem.count++;
					create = false;
					break;
				}
			}
			if (create) {
				item = item.cloneItemStack();
				item.count = 1;
				newinput.add(item);
			}
		}
		this.input = NativeUtil.getItemStacks(newinput.toArray(new ItemStack[0]));
		List<ItemStack> newoutput = new ArrayList<ItemStack>(1);
		newoutput.add(output.cloneItemStack());
		for (ItemStack stack : newinput) {
			if (BlockUtil.isType(stack.id, Material.LAVA_BUCKET, Material.WATER_BUCKET, Material.MILK_BUCKET)) {
				ItemStack s = stack.cloneItemStack();
				s.id = Material.BUCKET.getId();
				newoutput.add(s);
			}
		}
		this.output = NativeUtil.getItemStacks(newoutput.toArray(new ItemStack[0]));
	}

	private final org.bukkit.inventory.ItemStack[] input;
	private final org.bukkit.inventory.ItemStack[] output;

	/**
	 * Gets the input item at the index specified
	 * 
	 * @param index of the item
	 * @return input Item
	 */
	public org.bukkit.inventory.ItemStack getInput(int index) {
		return this.input[index];
	}

	/**
	 * Gets all the input items
	 * 
	 * @return input Items
	 */
	public org.bukkit.inventory.ItemStack[] getInput() {
		return this.input;
	}

	/**
	 * Gets all the output items
	 * 
	 * @return output Items
	 */
	public org.bukkit.inventory.ItemStack[] getOutput() {
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
		for (org.bukkit.inventory.ItemStack item : this.input) {
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
		for (org.bukkit.inventory.ItemStack item : this.output) {
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
		int lim = MathHelper.floor((double) itemlimit / (double) this.output[0].getAmount());
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
		for (org.bukkit.inventory.ItemStack item : this.input) {
			if (ItemUtil.getItemCount(inventory, item.getTypeId(), item.getDurability()) < item.getAmount()) {
				return false;
			}
		}
		// contains enough room to put in the results?
		if (!ItemUtil.canTransferAll(this.output, inventory)) {
			return false;
		}
		// actually transfer everything...
		for (org.bukkit.inventory.ItemStack item : this.input) {
			ItemUtil.removeItems(inventory, item);
		}
		for (org.bukkit.inventory.ItemStack item : this.output) {
			ItemUtil.transfer(ItemUtil.cloneItem(item), inventory, Integer.MAX_VALUE);
		}
		return true;
	}

	public static CraftRecipe create(IRecipe recipe) {
		if (recipe instanceof ShapedRecipes) {
			return create(srItems.get(recipe), recipe.b());
		} else if (recipe instanceof ShapelessRecipes) {
			return create(slIngredients.get(recipe), recipe.b());
		} else {
			return null;
		}
	}

	public static CraftRecipe create(List<ItemStack> input, ItemStack output) {
		if (input == null) {
			return null;
		} else {
			return create(input.toArray(new ItemStack[0]), output);
		}
	}

	public static CraftRecipe create(ItemStack[] input, ItemStack output) {
		if (input == null || output == null || input.length == 0) {
			return null;
		} else {
			return new CraftRecipe(input, output);
		}
	}
}
