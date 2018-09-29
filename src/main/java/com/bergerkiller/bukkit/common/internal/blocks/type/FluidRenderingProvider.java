package com.bergerkiller.bukkit.common.internal.blocks.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.map.util.Model.Element.Face;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;

public class FluidRenderingProvider extends BlockRenderProvider {
    private final List<Material> fluidMaterials;
    private final String fluidTexture1, fluidTexture2;
    private final String tint;

    public FluidRenderingProvider(String texture1, String texture2, String tint, Collection<Material> fluidMaterials) {
        this.fluidTexture1 = texture1;
        this.fluidTexture2 = texture2;
        this.fluidMaterials = new ArrayList<Material>(fluidMaterials);
        this.tint = tint;
    }

    @Override
    public void addOptions(BlockRenderOptions options, World world, int x, int y, int z) {
        if (world == null) {
            return;
        }

        // Store all 8 neighboring block water state information
        storeWaterBlock(options, "neigh_nn", world, x, y, z, BlockFace.NORTH);
        storeWaterBlock(options, "neigh_ne", world, x, y, z, BlockFace.NORTH_EAST);
        storeWaterBlock(options, "neigh_ee", world, x, y, z, BlockFace.EAST);
        storeWaterBlock(options, "neigh_se", world, x, y, z, BlockFace.SOUTH_EAST);
        storeWaterBlock(options, "neigh_ss", world, x, y, z, BlockFace.SOUTH);
        storeWaterBlock(options, "neigh_sw", world, x, y, z, BlockFace.SOUTH_WEST);
        storeWaterBlock(options, "neigh_ww", world, x, y, z, BlockFace.WEST);
        storeWaterBlock(options, "neigh_nw", world, x, y, z, BlockFace.NORTH_WEST);

        // Tint color for water
        if (this.tint != null) {
            options.put("tint", this.tint);
        }
    }

    @Override
    public Collection<Material> getTypes() {
        return this.fluidMaterials;
    }

    @Override
    public Model createModel(MapResourcePack resources, BlockRenderOptions options) {
        // Read all water blocks from options
        FluidBlock self = getFluidBlock(options.getBlockData());
        FluidBlock neigh_nn = readFluidBlock(options, "neigh_nn");
        FluidBlock neigh_ne = readFluidBlock(options, "neigh_ne");
        FluidBlock neigh_ee = readFluidBlock(options, "neigh_ee");
        FluidBlock neigh_se = readFluidBlock(options, "neigh_se");
        FluidBlock neigh_ss = readFluidBlock(options, "neigh_ss");
        FluidBlock neigh_sw = readFluidBlock(options, "neigh_sw");
        FluidBlock neigh_ww = readFluidBlock(options, "neigh_ww");
        FluidBlock neigh_nw = readFluidBlock(options, "neigh_nw");

        Model model = new Model();
        Model.Element water = new Model.Element();

        // Cut out only the first animation block from the texture
        // This is the 'side' of the water where no water animations show
        MapTexture waterSide = resources.getTexture(this.fluidTexture1);
        waterSide = waterSide.getView(0, 0, waterSide.getWidth(), waterSide.getWidth()).clone();

        // Cut out only the first animation block from the texture
        // For now, we don't do animations in this renderer.
        MapTexture waterTexture = resources.getTexture(this.fluidTexture2);
        waterTexture = waterTexture.getView(0, 0, waterTexture.getWidth(), waterTexture.getWidth()).clone();

        for (BlockFace blockFace : FaceUtil.BLOCK_SIDES) {
            Model.Element.Face face = new Model.Element.Face();

            // If blocked by some solid block, show the non-animated 'overlay' texture
            // If flowing or top, show the flowing texture
            // On the top, we always show the flowing texture
            //TODO!
            face.texture = FaceUtil.isVertical(blockFace) ? waterTexture : waterSide;
            if (this.tint != null) {
                face.tintindex = 0;
            }
            face.buildBlock(options);
            water.faces.put(blockFace, face);
        }

        water.buildQuads();

        // Calculate the water levels of the 4 corners
        // Only do this when not flowing down
        if (!isFlowingDown(options.getBlockData())) {
            Face topFace = water.faces.get(BlockFace.UP);
            topFace.quad.p0.y = calcLevel(self, neigh_ww, neigh_nw, neigh_nn);
            topFace.quad.p1.y = calcLevel(self, neigh_ss, neigh_sw, neigh_ww);
            topFace.quad.p2.y = calcLevel(self, neigh_ee, neigh_se, neigh_ss);
            topFace.quad.p3.y = calcLevel(self, neigh_nn, neigh_ne, neigh_ee);
            if (this.tint != null) {
                topFace.tintindex = 0;
            }
            topFace.buildBlock(options);
        }

        model.elements.add(water);
        return model;
    }

    private final float calcLevel(FluidBlock... blocks) {
        float weight = 0.0f;
        float level = 0.0f;
        for (FluidBlock block : blocks) {
            level += block.level;
            weight += block.weight;
        }
        return level / weight;
    }

    private void storeWaterBlock(BlockRenderOptions options, String name, World world, int x, int y, int z, BlockFace face) {
        options.put(name, getFluidBlock(WorldUtil.getBlockData(world, x + face.getModX(), y, z + face.getModZ())).name());
    }

    private static FluidBlock readFluidBlock(BlockRenderOptions options, String name) {
        String valueStr = options.get(name);
        if (valueStr != null) {
            for (FluidBlock block : FluidBlock.values()) {
                if (valueStr.equals(block.name())) {
                    return block;
                }
            }
        }
        return FluidBlock.AIR;
    }

    @SuppressWarnings("deprecation")
    private FluidBlock getFluidBlock(BlockData blockData) {
        Material mat = blockData.getType();
        for (Material f : this.fluidMaterials) {
            if (mat == f) {
                return FluidBlock.L_VALUES[blockData.getRawData() & 0x7];
            }
        }

        if (mat.isSolid()) {
            return FluidBlock.SOLID;
        } else {
            return FluidBlock.AIR;
        }
    }

    @SuppressWarnings("deprecation")
    private boolean isFlowingDown(BlockData blockData) {
        return (blockData.getRawData() & 0x8) == 0x8;
    }

    private static enum FluidBlock {
        L0(14.0f), L1(11.5f), L2(10.8f), L3(9.0f), L4(7f), L5(5.7f), L6(3.8f), L7(2.0f),
        SOLID(0.0f, 0.0f), AIR(0.0f, 0.5f);

        public static final FluidBlock[] L_VALUES = {L0,L1,L2,L3,L4,L5,L6,L7};

        public final float level;
        public final float weight;

        private FluidBlock(float level) {
            this(level, 1.0f);
        }

        private FluidBlock(float level, float weight) {
            this.level = level;
            this.weight = weight;
        }
    }

}
