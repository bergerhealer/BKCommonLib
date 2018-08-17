package com.bergerkiller.bukkit.common.internal.resources;

import java.util.HashSet;
import java.util.Set;

/**
 * Tracks overrided resources
 */
public class ResourceOverrides {
    private static final Set<String> _overrided = new HashSet<String>();

    static {
        String[] shulker_colors = new String[] {
                "black", "blue", "brown", "cyan",
                "gray", "green", "light_blue", "lime",
                "magenta", "orange", "pink", "purple",
                "red", "silver", "white", "yellow",
                "light_gray" /* 1.13 */
        };
        for (String color : shulker_colors) {
            _overrided.add("assets/minecraft/models/item/" + color + "_shulker_box.json");
            _overrided.add("assets/minecraft/models/block/" + color + "_shulker_box.json");
        }
        String[] forced_names = new String[] {
                "chest", "ender_chest", "trapped_chest",
                "christmas_chest", "wall_sign", "standing_sign",
                "shulker_box"
        };
        for (String name : forced_names) {
            _overrided.add("assets/minecraft/models/block/" + name + ".json");
            _overrided.add("assets/minecraft/models/item/" + name + ".json");
            _overrided.add("assets/minecraft/blockstates/" + name + ".json");
        }
    }

    /**
     * Some resources stored internally in BKCommonLib must be used in place of those found
     * in resource packs. For resources where this applies, this method returns true.
     * 
     * @param path of the resource
     * @return True if the resource path is overrided by BKCommonLib
     */
    public static boolean isResourceOverrided(String path) {
        return _overrided.contains(path);
    }
}
