package com.bergerkiller.bukkit.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.CraftingManager;
import net.minecraft.server.RecipesFurnace;
import net.minecraft.server.TileEntityFurnace;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.inventory.CraftRecipe;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.reflection.classes.RecipeRef;

public class RecipeUtil {
	private static final Map<Integer, Integer> fuelTimes = new HashMap<Integer, Integer>();
	static {
		ItemStack item;
		for (Material material : Material.values()) {
			item = new ItemStack(material, 1);
			if (CommonNMS.getNative(item).getItem() == null) {
				continue;
			}
			int fuel = TileEntityFurnace.fuelTime(CommonNMS.getNative(item));
			if (fuel > 0) {
				fuelTimes.put(material.getId(), fuel);
			}
		}
	}

	public static Set<Integer> getFuelItems() {
		return fuelTimes.keySet();
	}

	public static Map<Integer, Integer> getFuelTimes() {
		return fuelTimes;
	}

	public static int getFuelTime(int itemid) {
		Integer rval = fuelTimes.get(itemid);
		return rval == null ? 0 : rval;
	}

	public static int getFuelTime(org.bukkit.inventory.ItemStack item) {
		if (item == null) {
			return 0;
		} else {
			return getFuelTime(item.getTypeId()) * item.getAmount();
		}
	}

	public static int getFuelTime(Material material) {
		return getFuelTime(material.getId());
	}

	public static boolean isFuelItem(int itemid) {
		return fuelTimes.containsKey(itemid);
	}

	public static boolean isFuelItem(Material material) {
		return isFuelItem(material.getId());
	}

	public static boolean isFuelItem(org.bukkit.inventory.ItemStack item) {
		return isFuelItem(item.getTypeId());
	}

	public static boolean isHeatableItem(int itemid) {
		return RecipesFurnace.getInstance().recipes.containsKey(itemid);
	}

	public static boolean isHeatableItem(Material material) {
		return isFuelItem(material.getId());
	}

	public static boolean isHeatableItem(org.bukkit.inventory.ItemStack item) {
		return isHeatableItem(item.getTypeId());
	}

	public static org.bukkit.inventory.ItemStack getFurnaceResult(int itemid) {
		return Conversion.toItemStack.convert(RecipesFurnace.getInstance().getResult(itemid));
	}

	public static org.bukkit.inventory.ItemStack getFurnaceResult(org.bukkit.inventory.ItemStack cooked) {
		return getFurnaceResult(cooked.getTypeId());
	}

	@SuppressWarnings("unchecked")
	public static Set<Integer> getHeatableItems() {
		return RecipesFurnace.getInstance().recipes.keySet();
	}

	/**
	 * Gets all Crafting Recipes able to produce the ItemStack specified
	 * 
	 * @param itemid of the item to craft
	 * @param data of the item to craft (-1 for any data)
	 * @return the Crafting Recipes that can craft the item specified
	 */
	public static CraftRecipe[] getCraftingRequirements(int itemid, int data) {
		List<CraftRecipe> poss = new ArrayList<CraftRecipe>(2);
		for (Object rec : getCraftRecipes()) {
			ItemStack item = RecipeRef.getOutput(rec);
			if (item != null && item.getTypeId() == itemid && (data == -1 || data == item.getDurability())) {
				CraftRecipe crec = CraftRecipe.create(rec);
				if (crec != null) {
					poss.add(crec);
				}
			}
		}
		return poss.toArray(new CraftRecipe[0]);
	}

	/**
	 * Crafts items specified in an Inventory
	 * 
	 * @param parser that specified the item type, data and amount to craft
	 * @param source inventory to craft in
	 */
	public static void craftItems(ItemParser parser, Inventory source) {
		if (parser.hasType()) {
			final int limit;
			if (parser.hasAmount()) {
				limit = parser.getAmount();
			} else {
				limit = Integer.MAX_VALUE;
			}
			craftItems(parser.getTypeId(), parser.getData(), source, limit);
		}
	}

	/**
	 * Crafts items specified in an Inventory
	 * 
	 * @param itemid of the item to craft
	 * @param data of the item to craft (-1 for any data)
	 * @param source inventory to craft in
	 * @param limit amount of items to craft
	 */
	public static void craftItems(int itemid, int data, Inventory source, int limit) {
		for (CraftRecipe rec : getCraftingRequirements(itemid, data)) {
			limit -= rec.craftItems(source, limit);
		}
	}

	@SuppressWarnings("unchecked")
	private static List<Object> getCraftRecipes() {
		return (List<Object>) CraftingManager.getInstance().getRecipes();
	}
}
