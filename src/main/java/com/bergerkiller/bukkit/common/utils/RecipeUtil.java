package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.inventory.CraftRecipe;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.generated.net.minecraft.server.CraftingManagerHandle;
import com.bergerkiller.generated.net.minecraft.server.IRecipeHandle;
import com.bergerkiller.generated.net.minecraft.server.RecipesFurnaceHandle;
import com.bergerkiller.generated.net.minecraft.server.TileEntityFurnaceHandle;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingSet;
import com.bergerkiller.reflection.net.minecraft.server.NMSItemStack;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RecipeUtil {

    private static final EnumMap<Material, Integer> fuelTimes = new EnumMap<Material, Integer>(Material.class);

    static {
        for (Material material : Material.values()) {
            Object item = NMSItemStack.newInstance(material, 0, 1);
            int fuel = ((Integer) TileEntityFurnaceHandle.T.fuelTime.raw.invoke(item)).intValue();
            if (fuel > 0) {
                fuelTimes.put(material, fuel);
            }
        }
    }

    public static Set<Material> getFuelItems() {
        return fuelTimes.keySet();
    }

    public static Map<Material, Integer> getFuelTimes() {
        return fuelTimes;
    }

    public static int getFuelTime(Material material) {
        return getFuelTime(new ItemStack(material, 1));
    }

    public static int getFuelTime(org.bukkit.inventory.ItemStack item) {
        if (item == null) {
            return 0;
        } else {
            return item.getAmount() * TileEntityFurnaceHandle.fuelTime(CommonNMS.getHandle(item));
        }
    }

    public static boolean isFuelItem(Material material) {
        return fuelTimes.containsKey(material);
    }

    public static boolean isFuelItem(org.bukkit.inventory.ItemStack item) {
        return item != null && isFuelItem(item.getType());
    }

    public static boolean isHeatableItem(Material material) {
        return !ItemUtil.isEmpty(getFurnaceResult(material));
    }

    public static boolean isHeatableItem(org.bukkit.inventory.ItemStack item) {
        return !ItemUtil.isEmpty(getFurnaceResult(item));
    }

    public static org.bukkit.inventory.ItemStack getFurnaceResult(org.bukkit.Material cookedType) {
        return getFurnaceResult(new ItemStack(cookedType, 1));
    }

    public static org.bukkit.inventory.ItemStack getFurnaceResult(org.bukkit.inventory.ItemStack cooked) {
        return Conversion.toItemStack.convert(RecipesFurnaceHandle.getInstance().getResult(CommonNMS.getHandle(cooked)));
    }

    public static Collection<ItemStack> getHeatableItemStacks() {
        return RecipesFurnaceHandle.getInstance().getRecipes().keySet();
    }

    @Deprecated
    public static Set<Integer> getHeatableItems() {
        DuplexConverter<?, Integer> conv = DuplexConverter.pair(Conversion.toItemId, Conversion.toItemStackHandle);
        Map<?, ?> recipes = (Map<?, ?>) RecipesFurnaceHandle.T.recipes.raw.get(RecipesFurnaceHandle.getInstance().getRaw());
        return new ConvertingSet<Integer>(recipes.keySet(), conv);
    }

    /**
     * Gets all Crafting Recipes able to produce the ItemStack specified
     *
     * @param type of the item to craft (NULL for any type)
     * @param data of the item to craft (-1 for any data)
     * @return the Crafting Recipes that can craft the item specified
     */
    public static CraftRecipe[] getCraftingRequirements(Material type, int data) {
        List<CraftRecipe> poss = new ArrayList<CraftRecipe>(2);
        for (IRecipeHandle rec : getCraftRecipes()) {
            ItemStack item = rec.getOutput();
            if (item != null && (type == null || item.getType() == type) && (data == -1 || MaterialUtil.getRawData(item) == data)) {
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
            craftItems(parser.getType(), parser.getData(), source, limit);
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
    public static void craftItems(Material type, int data, Inventory source, int limit) {
        for (CraftRecipe rec : getCraftingRequirements(type, data)) {
            limit -= rec.craftItems(source, limit);
        }
    }

    private static Iterable<IRecipeHandle> getCraftRecipes() {
        return CraftingManagerHandle.getRecipes();
    }
}
