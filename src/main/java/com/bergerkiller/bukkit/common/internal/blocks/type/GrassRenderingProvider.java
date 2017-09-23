package com.bergerkiller.bukkit.common.internal.blocks.type;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;

public class GrassRenderingProvider extends BlockRenderProvider{

    @Override
    public void addOptions(BlockRenderOptions options, World world, int x, int y, int z) {
        //TODO: Handle biomes and add the correct color for the biome to the options here
        // For now we always add the same color for grass everywhere
        Material type = options.getBlockData().getType();
        if (type == Material.GRASS) {
            //options.put("tint", "#8fba58");
            options.put("tint", "#9ac460");
        } else if (type == Material.LEAVES || type == Material.LEAVES_2) {
            options.put("tint", "#7fa554");
        }
    }

    @Override
    public Collection<Material> getTypes() {
        return Arrays.asList(Material.GRASS, Material.LEAVES, Material.LEAVES_2);
    }
}
