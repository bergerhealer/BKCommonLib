package com.bergerkiller.bukkit.common;

import java.util.HashSet;

import org.junit.Test;

import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * These test the various texture/model loading routines of the Map Resource Pack
 */
public class MapResourcePackTest {

    static {
        CommonUtil.bootstrap();
    }

    //@Test
    public void testBlockModels() {
        MapResourcePack resourcePack = MapResourcePack.VANILLA;

        // All of these have animations or a complicated model
        // We need to define a simplified placeholder model to make it work
        HashSet<String> ignore = new HashSet<String>();
        ignore.add("bed");
        ignore.add("skull");
        ignore.add("chest");
        ignore.add("trapped_chest");
        ignore.add("ender_chest");
        ignore.add("barrier");
        ignore.add("lava");
        ignore.add("water");
        ignore.add("flowing_lava");
        ignore.add("flowing_water");
        ignore.add("standing_lava");
        ignore.add("standing_water");
        ignore.add("standing_banner");
        ignore.add("standing_sign");
        ignore.add("wall_banner");
        ignore.add("wall_sign");
        ignore.add("piston_extension");

        ignore.add("end_portal"); // this is the black void-looking surface
        ignore.add("structure_void"); // I could not find what this was

        // Shulker boxes come in many colors. How will we deal?
        ignore.add("brown_shulker_box");
        ignore.add("white_shulker_box");
        ignore.add("red_shulker_box");
        ignore.add("magenta_shulker_box");
        ignore.add("pink_shulker_box");
        ignore.add("yellow_shulker_box");
        ignore.add("black_shulker_box");
        ignore.add("cyan_shulker_box");
        ignore.add("orange_shulker_box");
        ignore.add("lime_shulker_box");
        ignore.add("light_blue_shulker_box");
        ignore.add("green_shulker_box");
        ignore.add("purple_shulker_box");
        ignore.add("gray_shulker_box");
        ignore.add("blue_shulker_box");
        ignore.add("silver_shulker_box");

        boolean hasLoadErrors = false;
        for (BlockData block : BlockData.values()) {
            Model model = resourcePack.getBlockModel(block);
            String name = block.getBlockName();
            if (model.placeholder && !ignore.contains(name)) {
                hasLoadErrors = true;
                System.err.println("Failed to load model of block " + block.toString());
            }
        }
        if (hasLoadErrors) {
            throw new RuntimeException("Some block models could not be loaded!");
        }
    }
}
