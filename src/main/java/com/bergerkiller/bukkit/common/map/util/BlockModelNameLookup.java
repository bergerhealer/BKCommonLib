package com.bergerkiller.bukkit.common.map.util;

import java.util.Map;

import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * This is needed because Minecraft is really stupid sometimes
 */
public class BlockModelNameLookup {

    public static String lookup(BlockData blockData, Map<String, String> options) {
        String name = blockData.getBlockName();
        String variant = options.get("variant");

        // Not all slabs are equal
        if (name.equals("purpur_slab") || name.equals("purpur_double_slab")) {
            variant = null;
        } else if (name.contains("_slab")) {
            if (variant == null) {
                variant = "stone";
            }
            name = "slab";
        }

        // Taxonomy is pretty important
        if (name.equals("sapling")) {
            String type = options.get("type");
            if (type == null) {
                type = "oak";
            }
            name = type + "_" + name;
        }

        // Color me surprised
        if (
                name.equals("wool") || 
                name.equals("concrete") || 
                name.equals("concrete_powder") ||
                name.equals("stained_hardened_clay") ||
                name.equals("stained_glass_pane") ||
                name.equals("stained_glass")
        ) {
            String color = options.get("color");
            if (color == null) {
                color = "white";
            }
            name = color + "_" + name;
        }

        // Rolls down stairs, alone or in pairs, rolls over your neighbor's dog!
        if (name.equals("log2")) {
            if (variant == null) {
                variant = "oak";
            }
            name = "log";
        }

        // Uweh!
        if (name.equals("leaves2")) {
            name = "leaves";
        }

        // Half a plant is no plant at all
        if (name.equals("double_plant")) {
            name = ""; // only variant is used
            if (variant == null) {
                variant = "double_grass";
            }
        }

        // FAIL
        if (name.equals("tallgrass")) {
            name = "tall_grass";
        }
        if (name.equals("deadbush")) {
            name = "dead_bush";
        }

        // Roses are red, violets are blue
        // These naming conventions need a review
        if (name.equals("red_flower") || name.equals("yellow_flower")) {
            String type = options.get("type");
            if (type == null) {
                type = "red_tulip";
            }
            name = type;
        }

        // Special needs blocks
        if (
                name.equals("brown_mushroom_block") ||
                name.equals("red_mushroom_block") ||
                name.equals("stonebrick") ||
                name.equals("cobblestone_wall") ||
                name.equals("stone") ||
                name.equals("prismarine") ||
                name.equals("purpur_slab") ||
                name.equals("quartz_block") ||
                name.equals("dirt") ||
                name.equals("sand")
        ) {
            variant = null; //suppress
        }

        if (name.equals("end_gateway")) {
            name = "end_portal_frame";
        }
        
        // Now add the variant!
        if (variant != null) {
            if (name.length() > 0) {
                name = variant + "_" + name;
            } else {
                name = variant;
            }
        }

        return name;
    }
}
