package com.bergerkiller.bukkit.common;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;

import net.minecraft.server.CraftingRecipe;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MathHelper;
import net.minecraft.server.ShapedRecipes;
import net.minecraft.server.ShapelessRecipes;

public class CraftRecipe {
	
	private static SafeField<ItemStack[]> sf1 = new SafeField<ItemStack[]>(ShapedRecipes.class, "items");
	private static SafeField<List<ItemStack>> sf2 = new SafeField<List<ItemStack>>(ShapelessRecipes.class, "ingredients");
	
	private CraftRecipe(ItemStack[] input, ItemStack output) {
		List<ItemStack> newinput = new ArrayList<ItemStack>(input.length);
		boolean create;
		for (ItemStack item : input) {
			if (item == null) continue;
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
		this.input = newinput.toArray(new ItemStack[0]);
		List<ItemStack> newoutput = new ArrayList<ItemStack>(1);
		newoutput.add(output.cloneItemStack());
		for (ItemStack stack : this.input) {
			if (BlockUtil.isType(stack.id, Material.LAVA_BUCKET, Material.WATER_BUCKET, Material.MILK_BUCKET)) {
				ItemStack s = stack.cloneItemStack();
				s.id = Material.BUCKET.getId();
				newoutput.add(s);
			}
		}
		this.output = newoutput.toArray(new ItemStack[0]);
	}
	
	private final ItemStack[] input;
	private final ItemStack[] output;
	public ItemStack getInput(int index) {
		return this.input[index];
	}
	public ItemStack[] getInput() {
		return this.input;
	}
	public ItemStack[] getOutput() {
		return this.output;
	}
	
	public int getInputSize() {
		int count = 0;
		for (ItemStack item : this.input) {
			count += item.count;
		}
		return count;
	}
	public int getOutputSize() {
		int count = 0;
		for (ItemStack item : this.output) {
			count += item.count;
		}
		return count;
	}

	/**
	 * Performs this recipe multiple times in the inventory specified
	 * @param inventory to craft in
	 * @param itemlimit the max amount of resulting items
	 * @return the amount of resulting items that were crafted
	 */
	public int craftItems(IInventory inventory, int itemlimit) {
		int lim = MathHelper.floor((double) itemlimit / (double) this.output[0].count);
		return this.craft(inventory, lim) * this.output[0].count;
	}
	
	/**
	 * Performs this recipe multiple times in the inventory specified
	 * @param inventory to craft in
	 * @param limit the amount of times it can craft
	 * @return the amount of times it crafted
	 */
	public int craft(IInventory inventory, int limit) {
		int amount = 0;
		while (amount < limit && craft(inventory)) amount++;
		return amount;
	}
	
	/**
	 * Performs this recipe once in the inventory specified
	 * @param inventory to craft in
	 * @return whether the crafting was successful
	 */
	public boolean craft(IInventory inventory) {
		//contains the required items?
	    for (ItemStack item : this.input) {
	    	Integer data = item.getData();
	    	if (data == -1) data = null;
	    	if (ItemUtil.getItemCount(inventory, item.id, data) < item.count) {
	    		return false;
	    	}
	    }
	    //contains enough room to put in the results?
	    if (ItemUtil.testTransfer(this.output, inventory)) {
	    	//actually transfer everything...
	    	for (ItemStack item : this.input) {
	    		ItemUtil.removeItem(inventory, item);
	    	}
	    	CraftInventory ci = new CraftInventory(inventory);
	    	for (ItemStack item : this.output) {
	    		ItemUtil.transfer(new CraftItemStack(item.cloneItemStack()), ci, Integer.MAX_VALUE);
	    	}
	    	return true;
	    }
	    return false;
	}

	public static CraftRecipe create(CraftingRecipe recipe) {
		if (recipe instanceof ShapedRecipes) {
			return create(sf1.get(recipe), recipe.b());
		} else if (recipe instanceof ShapelessRecipes) {
			return create(sf2.get(recipe), recipe.b());
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
