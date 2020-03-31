package com.bergerkiller.bukkit.common.internal.resources;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Tracks overrided resources
 */
public class ResourceOverrides {
    private static final Set<String> _overrided = new HashSet<String>();

    static {
        // Shulker boxes
        Stream.of("black", "blue", "brown", "cyan",
                  "gray", "green", "light_blue", "lime",
                  "magenta", "orange", "pink", "purple",
                  "red", "silver", "white", "yellow",
                  "light_gray" /* 1.13 */)
        .flatMap(color -> Stream.of(
                "assets/minecraft/models/item/" + color + "_shulker_box.json",
                "assets/minecraft/models/block/" + color + "_shulker_box.json"))
        .forEach(_overrided::add);

        // >1.14 wall sign types (+legacy)
        Stream.of("acacia", "birch", "dark_oak",
                  "jungle", "legacy", "oak", "spruce")
        .flatMap(type -> Stream.of(type + "_wall_sign", type + "_sign"))
        .flatMap(type -> Stream.of(
                "assets/minecraft/models/block/" + type + ".json",
                "assets/minecraft/blockstates/" + type + ".json"))
        .forEach(_overrided::add);

        // Chests
        Stream.of("chest", "ender_chest", "trapped_chest",
                  "christmas_chest", "shulker_box")
        .flatMap(name -> Stream.of(
                "assets/minecraft/models/block/" + name + ".json",
                "assets/minecraft/models/item/" + name + ".json",
                "assets/minecraft/blockstates/" + name + ".json"))
        .forEach(_overrided::add);

        // Skulls and heads. All have a wall variant too.
        Stream.of("player_head", "skeleton_skull", "wither_skeleton_skull",
                  "creeper_head", "zombie_head")
        .flatMap(name -> {
            int idx = name.lastIndexOf('_');
            return Stream.of(name, name.substring(0, idx)+"_wall"+name.substring(idx));
        }).flatMap(name -> Stream.of(
                "assets/minecraft/models/block/" + name + ".json",
                "assets/minecraft/models/item/" + name + ".json",
                "assets/minecraft/blockstates/" + name + ".json"))
        .forEach(_overrided::add);
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
