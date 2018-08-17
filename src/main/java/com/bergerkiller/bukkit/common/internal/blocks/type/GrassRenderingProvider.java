package com.bergerkiller.bukkit.common.internal.blocks.type;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;

public class GrassRenderingProvider extends BlockRenderProvider {
    private final ArrayList<Material> grass_materials = new ArrayList<Material>();
    private final ArrayList<Material> materials = new ArrayList<Material>();

    public GrassRenderingProvider() {
        // Grass types
        this.grass_materials.add(CommonLegacyMaterials.getLegacyMaterial("GRASS"));
        this.grass_materials.add(CommonLegacyMaterials.getMaterial("GRASS_BLOCK"));
        this.materials.addAll(this.grass_materials);

        // Grass leaves types (slightly different tint)
        this.materials.add(CommonLegacyMaterials.getLegacyMaterial("LEAVES"));
        this.materials.add(CommonLegacyMaterials.getLegacyMaterial("LEAVES_2"));
        this.materials.add(CommonLegacyMaterials.getLegacyMaterial("LONG_GRASS"));
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {            
            for (Material m : CommonLegacyMaterials.getAllMaterials()) {
                if (CommonLegacyMaterials.getMaterialName(m).endsWith("_LEAVES")) {
                    if (!this.materials.contains(m)) {
                        this.materials.add(m);
                    }
                }
            }
            this.materials.add(CommonLegacyMaterials.getMaterial("TALL_GRASS"));
            this.materials.add(CommonLegacyMaterials.getMaterial("GRASS"));
        }

        // Remove null materials (Materials not found?)
        for (int i = this.materials.size()-1; i >= 0; --i) {
            if (this.materials.get(i) == null) {
                this.materials.remove(i);
            }
        }
    }

    @Override
    public void addOptions(BlockRenderOptions options, World world, int x, int y, int z) {
        //TODO: Handle biomes and add the correct color for the biome to the options here
        // For now we always add the same color for grass everywhere
        Material type = options.getBlockData().getType();
        if (this.grass_materials.contains(type)) {
            //options.put("tint", "#8fba58");
            options.put("tint", "#9ac460");
        } else if (this.materials.contains(type)) {
            options.put("tint", "#7fa554");
        }
    }

    @Override
    public Collection<Material> getTypes() {
        return materials;
    }
}
