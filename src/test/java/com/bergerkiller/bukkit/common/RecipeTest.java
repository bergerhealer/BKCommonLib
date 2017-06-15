package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import com.bergerkiller.bukkit.common.inventory.CraftRecipe;
import com.bergerkiller.bukkit.common.inventory.InventoryBaseImpl;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.RecipeUtil;

public class RecipeTest {

    static {
        CommonUtil.bootstrap();
    }

    @Test
    public void testFurnaceRecipes() {
        assertEquals(16000, RecipeUtil.getFuelTime(Material.COAL_BLOCK));
        assertEquals(1600, RecipeUtil.getFuelTime(Material.COAL));
        assertEquals(0, RecipeUtil.getFuelTime(Material.STONE));
        assertEquals(300, RecipeUtil.getFuelTime(Material.WOOD));
        assertTrue(RecipeUtil.isFuelItem(Material.COAL));
        assertTrue(RecipeUtil.isFuelItem(Material.WOOD));
        assertTrue(RecipeUtil.isFuelItem(Material.LOG));
        assertFalse(RecipeUtil.isFuelItem(Material.GLASS));
        assertTrue(RecipeUtil.isHeatableItem(Material.PORK));
        assertFalse(RecipeUtil.isHeatableItem(Material.LEATHER));
        assertTrue(RecipeUtil.isHeatableItem(Material.LOG));
        assertEquals(Material.GLASS, RecipeUtil.getFurnaceResult(Material.SAND).getType());
    }

    @Test
    public void testCraftRecipes() {
        assertRequirements(Material.IRON_DOOR, new ItemStack(Material.IRON_INGOT, 6));
        assertRequirements(Material.DIODE,
                new ItemStack(Material.REDSTONE_TORCH_ON, 2),
                new ItemStack(Material.REDSTONE, 1),
                new ItemStack(Material.STONE, 3));
        assertRequirements(Material.WOOD, new ItemStack(Material.LOG, 1));
        assertRequirements(Material.GLOWSTONE, new ItemStack(Material.GLOWSTONE_DUST, 4));
    }

    @Test
    public void testCrafting() {
        Inventory testInventory = new InventoryBaseImpl(64);
        CraftRecipe recipe = RecipeUtil.getCraftingRequirements(Material.DIODE, 0)[0];

        // Add enough resources to craft 2 diodes, and a little more but not enough for a third
        // Also add some garbage items
        testInventory.addItem(new ItemStack(Material.REDSTONE, 64));
        testInventory.addItem(new ItemStack(Material.COBBLESTONE, 64));
        testInventory.addItem(new ItemStack(Material.STONE, 8));
        testInventory.addItem(new ItemStack(Material.REDSTONE_TORCH_ON, 5));
        testInventory.addItem(new ItemStack(Material.WOOD, 12));
        assertTrue(recipe.craft(testInventory));
        assertTrue(recipe.craft(testInventory));
        assertFalse(recipe.craft(testInventory));

        // Verify the items in the inventory
        assertEquals(testInventory.getItem(0), new ItemStack(Material.REDSTONE, 62));
        assertEquals(testInventory.getItem(1), new ItemStack(Material.COBBLESTONE, 64));
        assertEquals(testInventory.getItem(2), new ItemStack(Material.STONE, 2));
        assertEquals(testInventory.getItem(3), new ItemStack(Material.REDSTONE_TORCH_ON, 1));
        assertEquals(testInventory.getItem(4), new ItemStack(Material.WOOD, 12));
        assertEquals(testInventory.getItem(5), new ItemStack(Material.DIODE, 2));
    }
    
    public void assertRequirements(Material outputType, ItemStack... inputs) {
        CraftRecipe[] recipes = RecipeUtil.getCraftingRequirements(outputType, 0);
        assertEquals(1, recipes.length);
        assertEquals(inputs.length, recipes[0].getInputSlots().length);
        for (int i = 0; i < inputs.length; i++) {
            assertEquals(inputs[i], recipes[0].getInputSlots()[i].getDefaultChoice());
        }
    }
}
