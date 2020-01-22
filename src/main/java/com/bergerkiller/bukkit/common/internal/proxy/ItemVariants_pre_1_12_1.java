package com.bergerkiller.bukkit.common.internal.proxy;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;

/**
 * Provides item variants (for example, different colors of wool) for MC versions before 1.12.1.
 * The API for this was not available in the server prior.
 */
public class ItemVariants_pre_1_12_1 {
    private static HashMap<Material, VariantProducer> variants = new HashMap<Material, VariantProducer>();

    static {
        // Empty variants that don't show up at all
        {
            VariantProducer emptyProducer = (type, result) -> {
            };
            for (String name : new String[] {
                    "AIR", "MOB_SPAWNER", "SOIL", "HUGE_MUSHROOM_1", "HUGE_MUSHROOM_2",
                    "DRAGON_EGG", "COMMAND", "BARRIER", "GRASS_PATH", "COMMAND_REPEATING",
                    "COMMAND_CHAIN", "STRUCTURE_VOID", "STRUCTURE_BLOCK", "MAP", "WRITTEN_BOOK",
                    "FIREWORK", "COMMAND_MINECART", "KNOWLEDGE_BOOK"
            }) {
                register(name, emptyProducer);
            }
        }

        // Simple ranges
        registerRange("STONE", 0, 6);
        registerRange("DIRT", 0, 2);
        registerRange("WOOD", 0, 5);
        registerRange("SAPLING", 0, 5);
        registerRange("SAND", 0, 1);
        registerRange("LOG", 0, 3);
        registerRange("LEAVES", 0, 3);
        registerRange("SPONGE", 0, 1);
        registerRange("SANDSTONE", 0, 2);
        registerRange("LONG_GRASS", 1, 2);
        registerRange("WOOL", 0, 15);
        registerRange("RED_ROSE", 0, 8);
        registerRange("STAINED_GLASS", 0, 15);
        registerRange("MONSTER_EGGS", 0, 5);
        registerRange("SMOOTH_BRICK", 0, 3);
        registerRange("WOOD_STEP", 0, 5);
        registerRange("COBBLE_WALL", 0, 1);
        registerRange("ANVIL", 0, 2);
        registerRange("QUARTZ_BLOCK", 0, 2);
        registerRange("STAINED_CLAY", 0, 15);
        registerRange("STAINED_GLASS_PANE", 0, 15);
        registerRange("LEAVES_2", 0, 1);
        registerRange("LOG_2", 0, 1);
        registerRange("PRISMARINE", 0, 2);
        registerRange("CARPET", 0, 15);
        registerRange("DOUBLE_PLANT", 0, 5);
        registerRange("RED_SANDSTONE", 0, 2);
        registerRange("CONCRETE", 0, 15);
        registerRange("CONCRETE_POWDER", 0, 15);
        registerRange("COAL", 0, 1);
        registerRange("GOLDEN_APPLE", 0, 1);
        registerRange("RAW_FISH", 0, 3);
        registerRange("COOKED_FISH", 0, 1);
        registerRange("INK_SACK", 0, 15);
        registerRange("BED", 0, 15);
        registerRange("SKULL_ITEM", 0, 5);
        registerRange("BANNER", 15, 0);

        // Complicated generators
        register("STEP", (type, result) -> {
            result.add(new ItemStack(type, 1, (short) 0));
            result.add(new ItemStack(type, 1, (short) 1));
            result.add(new ItemStack(type, 1, (short) 3));
            result.add(new ItemStack(type, 1, (short) 4));
            result.add(new ItemStack(type, 1, (short) 5));
            result.add(new ItemStack(type, 1, (short) 6));
            result.add(new ItemStack(type, 1, (short) 7));
        });

        //TODO: 
        // POTION
        // SPLASH_POTION
        // LINGERING_POTION
        // MONSTER_EGG
        // TIPPED_ARROW
        // ENCHANTED_BOOK
    }

    public static void addVariants(Material type, List<ItemStack> result) {
        VariantProducer producer = variants.get(type);
        if (producer == null) {
            result.add(new org.bukkit.inventory.ItemStack(type));
        } else {
            producer.addVariants(type, result);
        }
    }

    private static void registerRange(String name, int start, int end) {
        register(name, new StandardVariantRange(start, end));
    }

    private static void register(String name, VariantProducer producer) {
        for (Material m : MaterialsByName.getAllMaterials()) {
            if (m.name().equals(name)) {
                variants.put(m, producer);
                return;
            }
        }
    }

    private static interface VariantProducer {
        public void addVariants(Material type, List<ItemStack> result);
    }

    private static class StandardVariantRange implements VariantProducer {
        public final int start;
        public final int end;

        public StandardVariantRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public void addVariants(Material type, List<ItemStack> result) {
            for (int durability = this.start; durability <= this.end; durability++) {
                result.add(new ItemStack(type, 1, (short) durability));
            }
        }
    }

}
