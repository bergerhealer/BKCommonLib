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
import net.minecraft.server.MathHelper;

import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;

import com.bergerkiller.bukkit.common.CraftRecipe;
import com.bergerkiller.bukkit.common.ItemParser;

public class RecipeUtil {
	private static final Map<Integer, Integer> fuelTimes = new HashMap<Integer, Integer>();
	static {
		fuelTimes.put(Material.WOOD.getId(), 300);
		fuelTimes.put(Material.STICK.getId(), 100);
		fuelTimes.put(Material.COAL.getId(), 1600);
		fuelTimes.put(Material.LAVA_BUCKET.getId(), 20000);
		fuelTimes.put(Material.SAPLING.getId(), 100);
		fuelTimes.put(Material.BLAZE_ROD.getId(), 2400);
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
		return FurnaceRecipes.getInstance().a(itemid);
	}
	@SuppressWarnings("unchecked")
	public static Map<Integer, ItemStack> getFurnaceResults() {
		return (Map<Integer, ItemStack>) FurnaceRecipes.getInstance().b();
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
		int count = 1;
		IInventory nativesource = ItemUtil.getNative(source);
		for (CraftRecipe rec : getCraftingRequirements(itemid, data)) {			
			//try to craft until no longer possible
			ItemStack[] from = new ItemStack[rec.getInput().length];
			while (from != null) {
				count = (int) Math.floor((double) limit / (double) rec.getOutput().count);
				for (int i = 0; i < from.length; i++) {
					ItemStack inp = rec.getInput(i);
					data = inp.getData() == -1 ? null : inp.getData();
				    from[i] = ItemUtil.findItem(nativesource, inp.id, data);
					if (from[i] == null) {
						from = null;
						break;
					} else {
						count = Math.min(count, MathHelper.floor((double) from[i].count / (double) inp.count));
						if (count == 0) {
							from = null;
							break;
						}
					}
				}
				if (count == 0) {
					from = null;
				} else if (from != null) {
					CraftItemStack out = new CraftItemStack(rec.getOutput().cloneItemStack());
					for (int i = 0; i < from.length; i++) {
						from[i].count = rec.getInput(i).count;
					}
					for (int i = 0; i < count; i++) {
						//try to add a new item - possible?
						if (ItemUtil.testTransfer(out, source) == out.getAmount()) {
							//transfer items
							for (ItemStack item : from) {
								ItemUtil.removeItem(nativesource, item);
							}
							limit -= ItemUtil.transfer(out.clone(), source, Integer.MAX_VALUE);
						} else {
							from = null;
							break;
						}
					}
				}
			}
		}
	}
		
	@SuppressWarnings("unchecked")
	public static List<CraftingRecipe> getCraftRecipes() {
		return (List<CraftingRecipe>) CraftingManager.getInstance().b();
	}
}
