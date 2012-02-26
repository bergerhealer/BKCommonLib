package com.bergerkiller.bukkit.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.CraftingRecipe;
import net.minecraft.server.ItemStack;
import net.minecraft.server.ShapedRecipes;
import net.minecraft.server.ShapelessRecipes;

public class CraftRecipe {
	
	private static SafeField<ItemStack[]> sf1 = new SafeField<ItemStack[]>(ShapedRecipes.class, "d");
	private static SafeField<List<ItemStack>> sf2 = new SafeField<List<ItemStack>>(ShapelessRecipes.class, "b");
	
	private CraftRecipe(ItemStack[] input, ItemStack output) {
		List<ItemStack> newinput = new ArrayList<ItemStack>(input.length);
		boolean create;
		for (ItemStack item : input) {
			create = true;
			for (ItemStack newitem : newinput) {
				if (newitem.id == item.id && newitem.getData() == item.getData()) {
					newitem.count += item.count;
					create = false;
					break;
				}
			}
			if (create) {
				newinput.add(item.cloneItemStack());
			}
		}
		this.input = newinput.toArray(new ItemStack[0]);
		this.output = output.cloneItemStack();
	}
	
	private final ItemStack[] input;
	private final ItemStack output;
	public ItemStack getInput(int index) {
		return this.input[index];
	}
	public ItemStack[] getInput() {
		return this.input;
	}
	public ItemStack getOutput() {
		return this.output;
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
