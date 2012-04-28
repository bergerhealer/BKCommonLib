package com.bergerkiller.bukkit.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.server.CraftingRecipe;
import net.minecraft.server.FurnaceRecipes;
import net.minecraft.server.CraftingManager;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;

import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;

import com.bergerkiller.bukkit.common.CraftRecipe;
import com.bergerkiller.bukkit.common.ItemParser;

public class RecipeUtil {
	private static final Map<Integer, Integer> fuelTimes = new HashMap<Integer, Integer>();
	static {
		fuelTimes.put(Material.STICK.getId(), 100);
		fuelTimes.put(Material.SAPLING.getId(), 100);
		fuelTimes.put(Material.LOG.getId(), 300);
		fuelTimes.put(Material.WOOD.getId(), 300);
		fuelTimes.put(Material.FENCE.getId(), 300);
		fuelTimes.put(Material.WOOD_STAIRS.getId(), 300);
		fuelTimes.put(Material.TRAP_DOOR.getId(), 300);
		fuelTimes.put(Material.WORKBENCH.getId(), 300);
		fuelTimes.put(Material.BOOKSHELF.getId(), 300);
		fuelTimes.put(Material.CHEST.getId(), 300);
		fuelTimes.put(Material.JUKEBOX.getId(), 300);
		fuelTimes.put(Material.NOTE_BLOCK.getId(), 300);
		fuelTimes.put(Material.HUGE_MUSHROOM_1.getId(), 300);
		fuelTimes.put(Material.HUGE_MUSHROOM_2.getId(), 300);
		fuelTimes.put(Material.COAL.getId(), 1600);
		fuelTimes.put(Material.BLAZE_ROD.getId(), 2400);
		fuelTimes.put(Material.LAVA_BUCKET.getId(), 20000);
	}
	public static Map<Integer, Integer> getFuelTimes() {
		return fuelTimes;
	}
	public static int getFuelTime(int itemid) {
		Integer rval = fuelTimes.get(itemid);
		return rval == null ? 0 : rval;
	}
	public static int getFuelTime(org.bukkit.inventory.ItemStack item) {
		if (item == null) return 0;
		return getFuelTime(item.getTypeId()) * item.getAmount();
	}
	public static int getFuelTime(ItemStack item) {
		if (item == null) return 0;
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
		return FurnaceRecipes.getInstance().getResult(itemid);
	}
	@SuppressWarnings("unchecked")
	public static Map<Integer, ItemStack> getFurnaceResults() {
		return (Map<Integer, ItemStack>) FurnaceRecipes.getInstance().getRecipies();
	}
	public static Set<Integer> getHeatableItems() {
		return getFurnaceResults().keySet();
	}
	
	public static CraftRecipe[] getCraftingRequirements(int itemid, Integer data) {
		List<CraftRecipe> poss = new ArrayList<CraftRecipe>();
		for (CraftingRecipe rec : getCraftRecipes()) {
			net.minecraft.server.ItemStack item = rec.b();
			if (item.id == itemid && (data == null || data == item.getData())) {
				CraftRecipe crec = CraftRecipe.create(rec);
				if (crec != null) poss.add(crec);
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
			Integer data = parser.hasData() ? null : (int) parser.getData();
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
	public static List<CraftingRecipe> getCraftRecipes() {
		return (List<CraftingRecipe>) CraftingManager.getInstance().getRecipies();
	}
}
