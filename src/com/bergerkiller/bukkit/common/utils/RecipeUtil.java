package com.bergerkiller.bukkit.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.server.CraftingManager;
import net.minecraft.server.IInventory;
import net.minecraft.server.IRecipe;
import net.minecraft.server.ItemStack;
import net.minecraft.server.RecipesFurnace;
import net.minecraft.server.TileEntityFurnace;

import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;

import com.bergerkiller.bukkit.common.items.CraftRecipe;
import com.bergerkiller.bukkit.common.items.ItemParser;

public class RecipeUtil {
	private static final Map<Integer, Integer> fuelTimes = new HashMap<Integer, Integer>();
	static {
		ItemStack item;
		for (Material material : Material.values()) {
			item = new ItemStack(material.getId(), 1, 0);
			if (item.getItem() == null) {
				continue;
			}
			int fuel = TileEntityFurnace.fuelTime(item);
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
		if (item == null)
			return 0;
		return getFuelTime(item.getTypeId()) * item.getAmount();
	}

	public static int getFuelTime(ItemStack item) {
		if (item == null)
			return 0;
		return getFuelTime(item.id) * item.count;
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

	public static org.bukkit.inventory.ItemStack getFurnaceResult(org.bukkit.inventory.ItemStack cooked) {
		return new CraftItemStack(getFurnaceResult(cooked.getTypeId()));
	}

	public static ItemStack getFurnaceResult(ItemStack cooked) {
		return getFurnaceResult(cooked.id);
	}

	public static ItemStack getFurnaceResult(int itemid) {
		return RecipesFurnace.getInstance().getResult(itemid);
	}

	@SuppressWarnings("unchecked")
	public static Map<Integer, ItemStack> getFurnaceResults() {
		return (Map<Integer, ItemStack>) RecipesFurnace.getInstance().getRecipes();
	}

	public static Set<Integer> getHeatableItems() {
		return getFurnaceResults().keySet();
	}

	public static CraftRecipe[] getCraftingRequirements(int itemid, Integer data) {
		List<CraftRecipe> poss = new ArrayList<CraftRecipe>();
		for (IRecipe rec : getCraftRecipes()) {
			net.minecraft.server.ItemStack item = rec.b();
			if (item != null && item.id == itemid && (data == null || data == item.getData())) {
				CraftRecipe crec = CraftRecipe.create(rec);
				if (crec != null)
					poss.add(crec);
			}
		}
		return poss.toArray(new CraftRecipe[0]);
	}

	public static void craftItems(ItemParser parser, Inventory source) {
		if (parser.hasType()) {
			final int limit;
			if (parser.hasAmount()) {
				limit = parser.getAmount();
			} else {
				limit = Integer.MAX_VALUE;
			}
			Integer data = parser.hasData() ? (int) parser.getData() : null;
			craftItems(parser.getTypeId(), data, source, limit);
		}
	}

	public static void craftItems(int itemid, Integer data, Inventory source, int limit) {
		craftItems(itemid, data, ItemUtil.getNative(source), limit);
	}

	public static void craftItems(int itemid, Integer data, IInventory source, int limit) {
		for (CraftRecipe rec : getCraftingRequirements(itemid, data)) {
			limit -= rec.craftItems(source, limit);
		}
	}

	@SuppressWarnings("unchecked")
	public static List<IRecipe> getCraftRecipes() {
		return (List<IRecipe>) CraftingManager.getInstance().getRecipes();
	}
}
