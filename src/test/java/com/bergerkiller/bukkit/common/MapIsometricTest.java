package com.bergerkiller.bukkit.common;

import org.bukkit.Material;
import org.junit.Ignore;
import org.junit.Test;

import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.util.MapDebugWindow;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;

/**
 * Tests isometric rendering of blocks
 */
public class MapIsometricTest {

    @Ignore
    @Test
    public void testIsometric() {
        // Load the source textures
        MapResourcePack texturePack = MapResourcePack.VANILLA;

        MapTexture sprite1 = renderSprite(texturePack.getBlockModel(Material.DIRT));
        MapTexture sprite2 = renderSprite(texturePack.getBlockModel(MaterialUtil.getMaterial("LEGACY_GRASS")));
        MapTexture sprite3 = renderSprite(texturePack.getBlockModel(MaterialUtil.getMaterial("LEGACY_SIGN_POST")));

        MapTexture tile = MapTexture.createEmpty(128, 128);
        //tile.fill(MapColorPalette.COLOR_RED);
        renderTile(tile, sprite1, 0, 18);
        renderTile(tile, sprite2, 0, 0);
        renderTile(tile, sprite3, 0, -18);

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
        map = MapTexture.createEmpty(32, 43);
        //map.setBrushMask(mask);
        //map.fill(MapColorPalette.COLOR_RED);
        //map.setBrushMask(null);
        
        Matrix4x4 transform = new Matrix4x4();

        //transform.rotateOrigin(new Vector3f(8,8,8), new Vector3f(180, 0, 0));

        transform.translate(map.getWidth(), 0.0f, map.getWidth() - 1);
        transform.scale(1.45f, 1.0f, 1.71f);

        transform.rotateX(-45.0f); // pitch
        transform.rotateY(225.0f); // yaw
        
        //transform.rotateOrigin(new Vector3f(8,8,8), new Vector3f(0, 0, 0));

        //map.fill(MapColorPalette.COLOR_RED);
        map.setLightOptions(0.2f, 0.8f, new Vector3(-1.0, 1.0, -1.0));
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
