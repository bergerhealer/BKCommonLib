package com.bergerkiller.bukkit.common.internal.blocks.type;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.map.util.Model.Element.Face;
import com.bergerkiller.bukkit.common.map.util.Vector3f;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;

public class WaterRenderingProvider extends BlockRenderProvider {

    private final HashSet<Material> _waterTypes;

    public WaterRenderingProvider() {
        _waterTypes = new HashSet<Material>();
        _waterTypes.addAll(Arrays.asList(Material.WATER, Material.STATIONARY_WATER));
    }

    @Override
    public void addOptions(BlockRenderOptions options, World world, int x, int y, int z) {
        // Store all 8 neighboring block water state information
        storeWaterBlock(options, "neigh_nn", world, x, y, z, BlockFace.NORTH);
        storeWaterBlock(options, "neigh_ne", world, x, y, z, BlockFace.NORTH_EAST);
        storeWaterBlock(options, "neigh_ee", world, x, y, z, BlockFace.EAST);
        storeWaterBlock(options, "neigh_se", world, x, y, z, BlockFace.SOUTH_EAST);
        storeWaterBlock(options, "neigh_ss", world, x, y, z, BlockFace.SOUTH);
        storeWaterBlock(options, "neigh_sw", world, x, y, z, BlockFace.SOUTH_WEST);
        storeWaterBlock(options, "neigh_ww", world, x, y, z, BlockFace.WEST);
        storeWaterBlock(options, "neigh_nw", world, x, y, z, BlockFace.NORTH_WEST);
    }

    @Override
    public Collection<Material> getTypes() {
        return _waterTypes;
    }

    @Override
    public Model createModel(MapResourcePack resources, BlockRenderOptions options) {
        // Read all water blocks from options
        WaterBlock self = getWaterBlock(options.getBlockData());
        WaterBlock neigh_nn = readWaterBlock(options, "neigh_nn");
        WaterBlock neigh_ne = readWaterBlock(options, "neigh_ne");
        WaterBlock neigh_ee = readWaterBlock(options, "neigh_ee");
        WaterBlock neigh_se = readWaterBlock(options, "neigh_se");
        WaterBlock neigh_ss = readWaterBlock(options, "neigh_ss");
        WaterBlock neigh_sw = readWaterBlock(options, "neigh_sw");
        WaterBlock neigh_ww = readWaterBlock(options, "neigh_ww");
        WaterBlock neigh_nw = readWaterBlock(options, "neigh_nw");

        Model model = new Model();
        Model.Element water = new Model.Element();

        // This is the 'side' of the water where no water animations show
        MapTexture waterSide = resources.getTexture("blocks/water_overlay");

        // Cut out only the first animation block from the texture
        // For now, we don't do animations in this renderer.
        MapTexture waterTexture = resources.getTexture("blocks/water_still");
        waterTexture = waterTexture.getView(0, 0, waterTexture.getWidth(), waterTexture.getWidth()).clone();

        for (BlockFace blockFace : FaceUtil.BLOCK_SIDES) {
            Model.Element.Face face = new Model.Element.Face();

            // If blocked by some solid block, show the non-animated 'overlay' texture
            // If flowing or top, show the flowing texture
            // On the top, we always show the flowing texture
            //TODO!
            face.texture = FaceUtil.isVertical(blockFace) ? waterTexture : waterSide;
            water.faces.put(blockFace, face);
        }

        water.buildQuads();

        // Calculate the water levels of the 4 corners
        // Only do this when not flowing down
        if ((options.getBlockData().getRawData() & 0x8) != 0x8) {
            Face topFace = water.faces.get(BlockFace.UP);
            topFace.quad.p0.y = calcLevel(self, neigh_ww, neigh_nw, neigh_nn);
            topFace.quad.p1.y = calcLevel(self, neigh_ss, neigh_sw, neigh_ww);
            topFace.quad.p2.y = calcLevel(self, neigh_ee, neigh_se, neigh_ss);
            topFace.quad.p3.y = calcLevel(self, neigh_nn, neigh_ne, neigh_ee);
        }

        model.elements.add(water);
        return model;
    }

    private final float dist(float aa, float ab) {
        float a = (aa - ab);
        return (a * a);
    }

    private final float calcLevel(WaterBlock... blocks) {
        float weight = 0.0f;
        float level = 0.0f;
        for (WaterBlock block : blocks) {
            level += block.level;
            weight += block.weight;
        }
        return level / weight;
    }

    private static void storeWaterBlock(BlockRenderOptions options, String name, World world, int x, int y, int z, BlockFace face) {
        options.put(name, getWaterBlock(WorldUtil.getBlockData(world, x + face.getModX(), y, z + face.getModZ())).name());
    }

    private static WaterBlock readWaterBlock(BlockRenderOptions options, String name) {
        String valueStr = options.get(name);
        if (valueStr != null) {
            for (WaterBlock block : WaterBlock.values()) {
                if (valueStr.equals(block.name())) {
                    return block;
                }
            }
        }
        return WaterBlock.AIR;
    }

    private static WaterBlock getWaterBlock(BlockData blockData) {
        Material mat = blockData.getType();
        if (mat == Material.WATER || mat == Material.STATIONARY_WATER) {
            return WaterBlock.L_VALUES[blockData.getRawData() & 0x7];
        } else if (mat.isSolid()) {
            return WaterBlock.SOLID;
        } else {
            return WaterBlock.AIR;
        }
    }

    private static enum WaterBlock {
        L0(14.0f), L1(11.5f), L2(10.8f), L3(9.0f), L4(7f), L5(5.7f), L6(3.8f), L7(2.0f),
        SOLID(0.0f, 0.0f), AIR(0.0f, 1.0f);

        public static final WaterBlock[] L_VALUES = {L0,L1,L2,L3,L4,L5,L6,L7};

        public final float level;
        public final float weight;

        private WaterBlock(float level) {
            this(level, 1.0f);
        }

        private WaterBlock(float level, float weight) {
            this.level = level;
            this.weight = weight;
        }
    }
}
