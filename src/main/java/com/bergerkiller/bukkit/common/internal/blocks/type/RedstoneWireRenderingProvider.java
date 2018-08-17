package com.bergerkiller.bukkit.common.internal.blocks.type;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;

public class RedstoneWireRenderingProvider extends BlockRenderProvider {

    private static final String[] WIRE_COLORS = {
            "#4A0000",
            "#6D0000",
            "#770000",
            "#810000",
            "#8B0000",
            "#950000",
            "#9F0000",
            "#A90000",
            "#B40000",
            "#BE0000",
            "#C90000",
            "#D30000",
            "#DC0000",
            "#E60600",
            "#F01B00",
            "#FB3100"
    };

    @Override
    public void addOptions(BlockRenderOptions options, World world, int x, int y, int z) {
        // Add the right red color for the redstone wire based on the power level
        int power = ParseUtil.parseInt(options.get("power"), 0);
        if (power < 0) {
            power = 0;
        } else if (power >= WIRE_COLORS.length) {
            power = WIRE_COLORS.length - 1;
        }
        options.put("tint", WIRE_COLORS[power]);
    }

    @Override
    public Collection<Material> getTypes() {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            return Arrays.asList(Material.REDSTONE_WIRE, MaterialUtil.getMaterial("LEGACY_REDSTONE_WIRE"));
        } else {
            return Arrays.asList(Material.REDSTONE_WIRE);
        }
    }

}
