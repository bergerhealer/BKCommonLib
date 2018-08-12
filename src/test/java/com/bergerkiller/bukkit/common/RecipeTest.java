package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;
import static com.bergerkiller.bukkit.common.utils.MaterialUtil.getFirst;

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
        assertEquals(300, RecipeUtil.getFuelTime(getFirst("OAK_PLANKS", "LEGACY_WOOD")));
        assertTrue(RecipeUtil.isFuelItem(Material.COAL));
        assertTrue(RecipeUtil.isFuelItem(getFirst("OAK_PLANKS", "LEGACY_WOOD")));
        assertTrue(RecipeUtil.isFuelItem(getFirst("OAK_LOG", "LEGACY_LOG")));
        assertFalse(RecipeUtil.isFuelItem(Material.GLASS));
        assertTrue(RecipeUtil.isHeatableItem(getFirst("PORKCHOP", "LEGACY_PORK")));
        assertFalse(RecipeUtil.isHeatableItem(Material.LEATHER));
        assertTrue(RecipeUtil.isHeatableItem(getFirst("OAK_LOG", "LEGACY_LOG")));
        assertEquals(Material.GLASS, RecipeUtil.getFurnaceResult(Material.SAND).getType());
    }

    @Test
    public void testCraftRecipes() {
        assertRequirements(Material.IRON_DOOR, new ItemStack(Material.IRON_INGOT, 6));
        assertRequirements(getFirst("REPEATER", "LEGACY_DIODE"),
                new ItemStack(getFirst("REDSTONE_TORCH", "LEGACY_REDSTONE_TORCH_ON"), 2),
                new ItemStack(Material.REDSTONE, 1),
                new ItemStack(Material.STONE, 3));
        assertRequirements(getFirst("OAK_PLANKS", "LEGACY_WOOD"), new ItemStack(getFirst("OAK_LOG", "LEGACY_LOG"), 1));
        assertRequirements(Material.GLOWSTONE, new ItemStack(Material.GLOWSTONE_DUST, 4));
        assertRequirements(Material.IRON_BLOCK, new ItemStack(Material.IRON_INGOT, 9));
    }

    @Test
    public void testCrafting() {
        Inventory testInventory = new InventoryBaseImpl(64);
        CraftRecipe recipe = RecipeUtil.getCraftingRequirements(getFirst("REPEATER", "LEGACY_DIODE"), 0)[0];

        // Add enough resources to craft 2 diodes, and a little more but not enough for a third
        // Also add some garbage items
        testInventory.addItem(new ItemStack(Material.REDSTONE, 64));
        testInventory.addItem(new ItemStack(Material.COBBLESTONE, 64));
        testInventory.addItem(new ItemStack(Material.STONE, 8));
        testInventory.addItem(new ItemStack(getFirst("REDSTONE_TORCH", "LEGACY_REDSTONE_TORCH_ON"), 5));
        testInventory.addItem(new ItemStack(getFirst("OAK_PLANKS", "LEGACY_WOOD"), 12));
        assertTrue(recipe.craft(testInventory));
        assertTrue(recipe.craft(testInventory));
        assertFalse(recipe.craft(testInventory));

        // Verify the items in the inventory
        assertEquals(testInventory.getItem(0), new ItemStack(Material.REDSTONE, 62));
        assertEquals(testInventory.getItem(1), new ItemStack(Material.COBBLESTONE, 64));
        assertEquals(testInventory.getItem(2), new ItemStack(Material.STONE, 2));
        assertEquals(testInventory.getItem(3), new ItemStack(getFirst("REDSTONE_TORCH", "LEGACY_REDSTONE_TORCH_ON"), 1));
        assertEquals(testInventory.getItem(4), new ItemStack(getFirst("OAK_PLANKS", "LEGACY_WOOD"), 12));
        assertEquals(testInventory.getItem(5), new ItemStack(getFirst("REPEATER", "LEGACY_DIODE"), 2));
    }

    public void assertRequirements(Material outputType, ItemStack... inputs) {
        CraftRecipe[] recipes = RecipeUtil.getCraftingRequirements(outputType, 0);
        assertTrue(recipes.length > 0);
        for (CraftRecipe recipe : recipes) {
            if (inputs.length != recipe.getInputSlots().length) {
                continue;
            }

            boolean allMatches = true;
            for (int i = 0; i < inputs.length; i++) {
                ItemStack item2 = recipe.getInputSlots()[i].match(inputs[i]);
                if (item2 == null) {
                    item2 = recipe.getInputSlots()[i].getDefaultChoice();
                }
                if (item2.getType() != inputs[i].getType() || item2.getAmount() != inputs[i].getAmount()) {
                    // fail("Item at [" +  i + "] expected " + inputs[i] + ", but was " + item2);
                    allMatches = false;
                }
            }
            if (allMatches) {
                return;
            }
        }
        assertEquals(inputs.length, recipes[0].getInputSlots().length);
        for (int i = 0; i < inputs.length; i++) {
            ItemStack item2 = recipes[0].getInputSlots()[i].match(inputs[i]);
            if (item2 == null) {
                item2 = recipes[0].getInputSlots()[i].getDefaultChoice();
            }
            if (item2.getType() != inputs[i].getType() || item2.getAmount() != inputs[i].getAmount()) {
                fail("Item at [" +  i + "] expected " + inputs[i] + ", but was " + item2);
            }
        }
        fail("Could not find recipe for some reason");
    }

}
