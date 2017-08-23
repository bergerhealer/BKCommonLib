package com.bergerkiller.bukkit.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.util.MapDebugWindow;
import com.bergerkiller.bukkit.common.map.util.Matrix4f;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.map.util.Vector3f;

/**
 * Tests isometric rendering of blocks
 */
public class MapIsometricTest {

    //@Test
    public void testIsometric() {
        // Load the source textures
        MapResourcePack texturePack = new MapResourcePack("C:\\Users\\QT\\Desktop\\TexturePack\\1.12.1.jar");

        MapTexture dispenser = renderSprite(texturePack.getModel("block/sand"));
        MapTexture cactus = renderSprite(texturePack.getModel("block/cactus"));

        MapTexture tile = MapTexture.createEmpty(128, 128);
        tile.fill(MapColorPalette.COLOR_RED);
        renderTile(tile, dispenser, 0, 18);
        renderTile(tile, cactus, 0, 0);
        renderTile(tile, cactus, 0, -18);
        
        // Show a 2x2 map display
        MapTexture tile2x2 = MapTexture.createEmpty(256, 256);
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                tile2x2.draw(tile, x * 128, y * 128);
            }
        }
        MapDebugWindow.showMapForever(tile2x2, 4);
    }

    private MapTexture renderSprite(Model model) {
        MapTexture map = null;
        try {
            MapTexture mask = MapTexture.fromStream(new FileInputStream("C:\\Users\\QT\\Desktop\\TexturePack\\mask2.png"));
            map = MapTexture.createEmpty(32, 43);
            //map.setBrushMask(mask);
            //map.fill(MapColorPalette.COLOR_RED);
            //map.setBrushMask(null);
       } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
       }
        
        Matrix4f translation = new Matrix4f();
        translation.translate(map.getWidth(), 0.0f, map.getWidth() - 1);
        translation.scale(1.45f, 1.0f, 1.71f);
        
        Matrix4f rotationPitch = new Matrix4f();
        rotationPitch.rotateX(-45.0f);

        Matrix4f rotationYaw = new Matrix4f();
        rotationYaw.rotateY(225.0f);

        Matrix4f transform = new Matrix4f();
        transform.setIdentity();
        transform.multiply(translation);
        transform.multiply(rotationPitch);
        transform.multiply(rotationYaw);
        
        //map.fill(MapColorPalette.COLOR_RED);
        map.drawModel(model, transform);

        return map;
    }

    private void renderTile(MapTexture tile, MapTexture sprite, int dx, int dy) {
        int stepY = 32;
        /*
        for (int i = 0; i < 5; i++) {
            tile.draw(sprite, dx + i * sprite.getWidth() - sprite.getWidth() / 2, dy + -stepY);
        }
        */
        
        ///*
        for (int i = 0; i < 4; i++) {
            tile.draw(sprite, dx + i * sprite.getWidth(), dy);
        }
        //*/

        /*
        for (int i = 0; i < 5; i++) {
            tile.draw(sprite, dx + i * sprite.getWidth() - sprite.getWidth() / 2, dy + stepY);
        }
        */

        for (int i = 0; i < 4; i++) {
            tile.draw(sprite, dx + i * sprite.getWidth(), dy + 2 * stepY);
        }
        /*
        for (int i = 0; i < 5; i++) {
            tile.draw(sprite, dx + i * sprite.getWidth() - sprite.getWidth() / 2, dy + 3 * stepY);
        }
        */
        
        for (int i = 0; i < 4; i++) {
            tile.draw(sprite, dx + i * sprite.getWidth(), dy + 4 * stepY);
        }
    }
}
