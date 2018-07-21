package com.bergerkiller.bukkit.common.internal.blocks.type;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;

public class GrassRenderingProvider extends BlockRenderProvider {
    private final ArrayList<Material> materials = new ArrayList<Material>();

    public GrassRenderingProvider() {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            this.materials.add(Material.getMaterial("GRASS"));
            for (Material m : Material.values()) {
                if (m.name().endsWith("_LEAVES")) {
                    this.materials.add(m);
                }
            }
        } else {
            this.materials.add(Material.getMaterial("GRASS"));
            this.materials.add(Material.getMaterial("LEAVES"));
            this.materials.add(Material.getMaterial("LEAVES_2"));
        }
    }

    @Override
    public void addOptions(BlockRenderOptions options, World world, int x, int y, int z) {
        //TODO: Handle biomes and add the correct color for the biome to the options here
        // For now we always add the same color for grass everywhere
        Material type = options.getBlockData().getType();
        if (type == Material.GRASS) {
            //options.put("tint", "#8fba58");
            options.put("tint", "#9ac460");
        } else if (materials.contains(type)) {
            options.put("tint", "#7fa554");
        }
    }

    @Override
    public Collection<Material> getTypes() {
        return materials;
    }
}
