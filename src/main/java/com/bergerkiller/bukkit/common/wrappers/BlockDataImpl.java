package com.bergerkiller.bukkit.common.wrappers;

import java.util.Arrays;
import java.util.IdentityHashMap;

import org.bukkit.Material;

import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.reflection.net.minecraft.server.NMSMinecraftKey;
import com.bergerkiller.reflection.net.minecraft.server.NMSSoundEffect;

import net.minecraft.server.v1_11_R1.Block;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.Explosion;
import net.minecraft.server.v1_11_R1.IBlockData;
import net.minecraft.server.v1_11_R1.World;

public class BlockDataImpl extends BlockData {
    private Block block;
    private IBlockData data;

    public static final int ID_BITS = 8;
    public static final int DATA_BITS = 4;

    public static final int ID_SIZE = (1 << ID_BITS);
    public static final int DATA_SIZE = (1 << DATA_BITS);
    public static final int DATA_MASK = (DATA_SIZE - 1);

    public static final int REGISTRY_SIZE = (1 << (ID_BITS + DATA_BITS));
    public static final int REGISTRY_MASK = (REGISTRY_SIZE - 1);

    public static final BlockDataConstant AIR = new BlockDataConstant(Block.getById(0));
    public static final BlockDataConstant[] BY_ID = new BlockDataConstant[ID_SIZE];
    public static final BlockDataConstant[] BY_ID_AND_DATA = new BlockDataConstant[REGISTRY_SIZE];
    public static final IdentityHashMap<IBlockData, BlockDataConstant> BY_BLOCK_DATA = new IdentityHashMap<IBlockData, BlockDataConstant>();

    static {
        // Cache all possible Block Ids (0 - 255) and combined Id+Data (0 - 4096)
        Arrays.fill(BY_ID, AIR);
        Arrays.fill(BY_ID_AND_DATA, AIR);
        for (Object oblock : Block.REGISTRY) {
            Block b = (Block) oblock;
            int id = Block.getId(b);
            BlockDataConstant blockConst = (id == 0) ? AIR : new BlockDataConstant(b);
            BY_ID[id] = blockConst;
            Arrays.fill(BY_ID_AND_DATA, id << DATA_BITS, (id + 1) << DATA_BITS, blockConst);
        }

        // Cache a mapping of all possible IBlockData instances
        for (Object oblockdata : Block.REGISTRY_ID) {
            IBlockData blockData = (IBlockData) oblockdata;
            BlockDataConstant block_const = BY_ID[Block.getId(blockData.getBlock())];
            if (block_const.getData() != blockData) {
                block_const = new BlockDataConstant(blockData);
            }
            BY_BLOCK_DATA.put(blockData, block_const);
            BY_ID_AND_DATA[Block.REGISTRY_ID.getId(blockData)] = block_const;
        }

        BY_BLOCK_DATA.put(null, AIR);
    }

    /**
     * Used to convert a BlockData value into an immutable version.
     * This protects statically registered block objects from being mutated by plugins.
     */
    public static class BlockDataConstant extends BlockDataImpl {

        public BlockDataConstant(Block block) {
            super(block);
        }

        public BlockDataConstant(IBlockData blockData) {
            super(blockData);
        }

        @Override
        public void loadBlock(Object block) {
            throw new UnsupportedOperationException("Immutable Block Data objects can not be changed");
        }

        @Override
        public void loadBlockData(Object iBlockData) {
            throw new UnsupportedOperationException("Immutable Block Data objects can not be changed");
        }

        @Override
        public void loadMaterialData(Material material, int data) {
            throw new UnsupportedOperationException("Immutable Block Data objects can not be changed");
        }
    }

    public BlockDataImpl() {
        this(Block.getById(0));
    }

    public BlockDataImpl(IBlockData data) {
        this(data.getBlock(), data);
    }

    public BlockDataImpl(Block block) {
        this(block, block.getBlockData());
    }

    public BlockDataImpl(Block block, IBlockData data) {
        this.block = block;
        this.data = data;
    }

    @Override
    public void loadBlock(Object block) {
        this.block = (Block) block;
        this.data = this.block.getBlockData();
    }

    @Override
    public void loadBlockData(Object iBlockData) {
        this.data = (IBlockData) iBlockData;
        this.block = this.data.getBlock();
    }

    @Override
    public void loadMaterialData(Material material, int data) {
        this.block = Block.getById(material.getId());
        this.data = this.block.fromLegacyData(data);
    }

    @Override
    public final Object getBlock() {
        return this.block;
    }

    @Override
    public final Object getData() {
        return this.data;
    }

    @Override
    public final int getTypeId() {
        return Block.getId(this.block);
    }

    @Override
    public final int getRawData() {
        return this.block.toLegacyData(this.data);
    }

    /* ====================================================================== */
    /* ========================= Block Properties =========================== */
    /* ====================================================================== */

    @Override
    public final String getStepSound() {
        return NMSMinecraftKey.getCombinedName(NMSSoundEffect.key.get(block.getStepSound().e()));
    }

    @Override
    public final int getOpacity() {
        return block.m(data);
    }

    @Override
    public final int getEmission() {
        return block.o(data);
    }

    @Override
    public final boolean isOccluding() {
        return block.isOccluding(data);
    }

    @Override
    public final boolean isSuffocating() {
        return block.isOccluding(data);
    }

    @Override
    public final boolean isPowerSource() {
        return block.isPowerSource(data);
    }

    @Override
    public final float getDamageResilience(org.bukkit.entity.Entity source) {
        return block.a(CommonNMS.getNative(source));
    }

    @Override
    public final void dropNaturally(org.bukkit.World world, int x, int y, int z, float yield, int chance) {
        block.dropNaturally(CommonNMS.getNative(world), new BlockPosition(x, y, z), data, yield, chance);
    }

    @Override
    public final void ignite(org.bukkit.World world, int x, int y, int z) {
        World worldhandle = CommonNMS.getNative(world);
        Explosion ex = new Explosion(worldhandle, null, x, y, z, (float) 4.0, true, true);
        block.wasExploded(worldhandle, new BlockPosition(x, y, z), ex);
    }

    @Override
    public void destroy(org.bukkit.World world, int x, int y, int z, float yield) {
        dropNaturally(world, x, y, z, yield);
        WorldUtil.setBlockData(world, x, y, z, AIR);
    }
}
