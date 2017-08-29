package com.bergerkiller.bukkit.common.internal.blocks.type;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;

public class WaterRenderingProvider extends BlockRenderProvider {

    private final HashSet<Material> _waterTypes;

    public WaterRenderingProvider() {
        _waterTypes = new HashSet<Material>();
        _waterTypes.addAll(Arrays.asList(Material.WATER, Material.STATIONARY_WATER));
    }

    @Override
    public void addOptions(BlockRenderOptions options, World world, int x, int y, int z) {
        //BlockFace facing = blockData.getFacingDirection();
        //System.out.println("[" + x + ", " + y + ", " + z + "] = " + facing);
        //System.out.println("OPT: " + options);
    }

    @Override
    public Collection<Material> getTypes() {
        return _waterTypes;
    }

}
