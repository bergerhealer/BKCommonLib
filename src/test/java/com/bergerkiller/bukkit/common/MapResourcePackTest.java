package com.bergerkiller.bukkit.common;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Ignore;
import org.junit.Test;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.util.MapDebugWindow;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * These test the various texture/model loading routines of the Map Resource Pack
 */
public class MapResourcePackTest {

    static {
        CommonUtil.bootstrap();
    }

    @Ignore
    @Test
    public void testItemSlotTexture() {
        Random rand = new Random();
        MapTexture map = MapTexture.createEmpty(128, 128);
        map.fill(MapColorPalette.getColor(128, 128, 128));

        MapResourcePack pack = MapResourcePack.SERVER; //new MapResourcePack("https://www.dropbox.com/s/s77nz3zaclrdqog/TestPack.zip?dl=1");
        pack.load();

        ItemStack item = ItemUtil.createItem(Material.DIAMOND_SWORD, 2, 1);
        ItemUtil.getMetaTag(item, true).putValue("Unbreakable", true);

        //map.draw(pack.getItemTexture(item, 128, 128), 0, 0);

        for (int x = 0; x < 128-16; x += 18) {
            for (int y = 0; y < 128-16; y += 18) {
                testDrawModel(map, pack, x, y, rand.nextInt(70));
            }
        }

        /*
        for (int x = 0; x < 128-16; x += 18) {
            for (int y = 0; y < 128-16; y += 18) {
                testDraw(map, pack, x, y, Material.values()[rand.nextInt(Material.values().length)]);
            }
        }
        */

        MapDebugWindow.showMapForeverAutoScale(map);
    }

    protected void testDraw(MapTexture canvas, MapResourcePack pack, int x, int y, Material material) {
        testDraw(canvas, pack, x, y, new ItemStack(material));
    }

    protected void testDrawModel(MapTexture canvas, MapResourcePack pack, int x, int y, int damage) {
        ItemStack item = ItemUtil.createItem(Material.DIAMOND_SWORD, damage, 1);
        ItemUtil.getMetaTag(item, true).putValue("Unbreakable", true);
        testDraw(canvas, pack, x, y, item);
    }

    protected void testDraw(MapTexture canvas, MapResourcePack pack, int x, int y, ItemStack item) {
        canvas.drawRectangle(x, y, 16, 16, MapColorPalette.COLOR_RED);
        canvas.draw(pack.getItemTexture(item, 16, 16), x, y);
    }

    @Ignore
    @Test
    public void testLineDrawing() {
        MapTexture texture = MapTexture.createEmpty(32, 32);
        texture.drawLine(1, 1, 30, 1, MapColorPalette.COLOR_RED);
        texture.drawLine(30, 3, 1, 3, MapColorPalette.COLOR_RED);
        texture.drawLine(1, 5, 1, 30, MapColorPalette.COLOR_RED);
        texture.drawLine(3, 30, 3, 5, MapColorPalette.COLOR_RED);
        texture.drawLine(5, 7, 28, 30, MapColorPalette.COLOR_RED);
        texture.drawLine(30, 28, 7, 5, MapColorPalette.COLOR_RED);
        texture.drawLine(5, 9, 10, 30, MapColorPalette.COLOR_RED);
        texture.drawLine(30, 10, 9, 5, MapColorPalette.COLOR_RED);
        MapDebugWindow.showMapForeverAutoScale(texture);
    }

    @Ignore
    @Test
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
