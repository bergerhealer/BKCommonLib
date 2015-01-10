package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;

public class RecipeRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("IRecipe");
	public static final ClassTemplate<?> SHAPED_TEMPLATE = NMSClassTemplate.create("ShapedRecipes");
	public static final ClassTemplate<?> SHAPELESS_TEMPLATE = NMSClassTemplate.create("ShapelessRecipes");
	public static final TranslatorFieldAccessor<List<ItemStack>> shapedInput = SHAPED_TEMPLATE.getField("items").translate(ConversionPairs.itemStackList);
	public static final TranslatorFieldAccessor<List<ItemStack>> shapelessInput = SHAPELESS_TEMPLATE.getField("ingredients").translate(ConversionPairs.itemStackList);
	private static final MethodAccessor<Object> getOutput = TEMPLATE.getMethod("b");

	public static ItemStack getOutput(Object iRecipe) {
		return Conversion.toItemStack.convert(getOutput.invoke(iRecipe));
	}
}
