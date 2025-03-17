package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.util.VanillaResourcePackFormat;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Ignore;
import org.junit.Test;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.util.MapDebugWindow;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * These test the various texture/model loading routines of the Map Resource Pack
 */
public class MapResourcePackTest {

    @Test
    public void testPackFormat() {
        assertEquals(8, VanillaResourcePackFormat.getPackFormat("1.18.1"));
        assertEquals(VanillaResourcePackFormat.getLatestPackFormat(),
                VanillaResourcePackFormat.getPackFormat(CommonBootstrap.initCommonServer().getMinecraftVersion()));
    }

    @Test
    public void testPackItemModelsLegacy() {
        MapResourcePack pack = new MapResourcePack("./misc/resource_packs/TestPack_TrainCarts_Demo_TP_v4_1_19_3.zip");
        pack.load();

        // Sanity check
        assertEquals(12, pack.getMetadata().getPackFormat());
        assertFalse(pack.getMetadata().hasItemOverrides());

        // Verify it can detect the golden_pickaxe override and doesn't list anything else
        assertEquals(Collections.singleton("golden_pickaxe"), pack.listOverriddenItemModelNames());

        //System.out.println(pack.getItemModel("golden_pickaxe"));
    }

    @Test
    public void testPackItemModelsModern() {
        MapResourcePack pack = new MapResourcePack("./misc/resource_packs/TestPack_TrainCarts_Demo_TP_v4_1_21_4.zip");
        pack.load();

        // Sanity check
        assertEquals(46, pack.getMetadata().getPackFormat());
        assertTrue(pack.getMetadata().hasItemOverrides());

        // Verify it can detect the golden tool overrides and doesn't list anything else
        // golden_pickaxe: Uses unbreakable + damage values (condition + range_dispatch)
        // golden_axe: Uses custom model data flags / int (range_dispatch)
        // golden_sword: Uses custom model data flags + strings (condition + select)
        assertEquals(new HashSet<>(Arrays.asList("golden_pickaxe", "golden_axe", "golden_sword")),
                pack.listOverriddenItemModelNames());

        //System.out.println(pack.getItemModel("golden_sword"));
    }

    @Ignore
    @Test
    public void testItemSlotTexture() {
        Random rand = new Random();
        MapTexture map = MapTexture.createEmpty(128, 128);
        map.fill(MapColorPalette.getColor(128, 128, 128));

        MapResourcePack pack = MapResourcePack.SERVER; //new MapResourcePack("https://www.dropbox.com/s/s77nz3zaclrdqog/TestPack.zip?dl=1");
        pack.load();

        CommonItemStack item = CommonItemStack.create(Material.DIAMOND_SWORD, 2)
                .setDamage(1)
                .setUnbreakable(true);

        //map.draw(pack.getItemTexture(item, 128, 128), 0, 0);

        for (int x = 0; x < 128-16; x += 18) {
            for (int y = 0; y < 128-16; y += 18) {
                testDrawModel(map, pack, x, y, rand.nextInt(70));
            }
        }

        /*
        for (int x = 0; x < 128-16; x += 18) {
            for (int y = 0; y < 128-16; y += 18) {
                testDraw(map, pack, x, y, CommonMethods.getAllMaterials()[rand.nextInt(Material.values().length)]);
            }
        }
        */

        MapDebugWindow.showMapForeverAutoScale(map);
    }

    protected void testDraw(MapTexture canvas, MapResourcePack pack, int x, int y, Material material) {
        testDraw(canvas, pack, x, y, new ItemStack(material));
    }

    protected void testDrawModel(MapTexture canvas, MapResourcePack pack, int x, int y, int damage) {
        CommonItemStack item = CommonItemStack.create(Material.DIAMOND_SWORD, 1)
                .setDamage(damage)
                .setUnbreakable(true);
        testDraw(canvas, pack, x, y, item);
    }

    protected void testDraw(MapTexture canvas, MapResourcePack pack, int x, int y, CommonItemStack item) {
        canvas.drawRectangle(x, y, 16, 16, MapColorPalette.COLOR_RED);
        canvas.draw(pack.getItemTexture(item, 16, 16), x, y);
    }

    protected void testDraw(MapTexture canvas, MapResourcePack pack, int x, int y, ItemStack item) {
        canvas.drawRectangle(x, y, 16, 16, MapColorPalette.COLOR_RED);
        canvas.draw(pack.getItemTexture(item, 16, 16), x, y);
    }

    @Ignore
    @Test
    public void testFontDrawing() {
        MapTexture texture = MapTexture.createEmpty(128, 128);
        texture.setSpacing(1);
        String text = "Hello, World\nHave a nice day\nPlease come again";
        Dimension size = texture.calcFontSize(MapFont.MINECRAFT, text);
        texture.drawRectangle(5, 5, size.width, size.height, MapColorPalette.COLOR_GREEN);
        texture.draw(MapFont.MINECRAFT, 5, 5, MapColorPalette.COLOR_RED, text);
        MapDebugWindow.showMapForeverAutoScale(texture);
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
    public void testItemModels() {
        MapResourcePack resourcePack = MapResourcePack.VANILLA;

        // All of these have animations or a complicated model
        // We need to define a simplified placeholder model to make it work
        // This acts as a TODO list for models to fix
        HashSet<String> ignore = new HashSet<String>();
        ignore.add("bed");
        ignore.add("chest");
        ignore.add("trapped_chest");
        ignore.add("ender_chest");
        ignore.add("anvil");
        ignore.add("skull");
        ignore.add("banner");
        ignore.add("shield");

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
        for (Material type : ItemUtil.getItemTypes()) {
            for (ItemStack variant : ItemUtil.getItemVariants(type)) {
                Model model = resourcePack.getItemModel(variant);
                if (model.isPlaceholder() && !ignore.contains(model.getName())) {
                    hasLoadErrors = true;
                    System.err.println("Failed to load model of item " + 
                            variant.getType() + ":" + variant.getDurability() +  " " +
                            model.getName());
                }
            }
        }
        if (hasLoadErrors) {
            fail("Some block models could not be loaded!");
        }
    }

    @Ignore
    @Test
    public void testBlockModels() {
        MapResourcePack resourcePack = MapResourcePack.VANILLA;

        // All of these have animations or a complicated model
        // We need to define a simplified placeholder model to make it work
        // This acts as a TODO list for models to fix
        HashSet<String> ignore = new HashSet<String>();
        ignore.add("bed");
        ignore.add("skull");
        ignore.add("barrier");
        ignore.add("standing_banner");
        ignore.add("wall_banner");
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
            if (model.isPlaceholder() && !ignore.contains(name)) {
                hasLoadErrors = true;
                System.err.println("Failed to load model of block " + block.toString());
            }
        }
        if (hasLoadErrors) {
            fail("Some block models could not be loaded!");
        }
    }
}
