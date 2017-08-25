package com.bergerkiller.bukkit.common.wrappers;

import java.util.Arrays;
import java.util.IdentityHashMap;

import org.bukkit.Material;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.BlockHandle;
import com.bergerkiller.generated.net.minecraft.server.ExplosionHandle;
import com.bergerkiller.generated.net.minecraft.server.IBlockDataHandle;
import com.bergerkiller.generated.net.minecraft.server.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.server.RegistryBlockIDHandle;
import com.bergerkiller.generated.net.minecraft.server.RegistryIDHandle;
import com.bergerkiller.generated.net.minecraft.server.RegistryMaterialsHandle;

public class BlockDataImpl extends BlockData {
    private BlockHandle block;
    private IBlockDataHandle data;

    public static final int ID_BITS = 8;
    public static final int DATA_BITS = 4;

    public static final int ID_SIZE = (1 << ID_BITS);
    public static final int ID_MASK = (ID_SIZE - 1);
    public static final int DATA_SIZE = (1 << DATA_BITS);
    public static final int DATA_MASK = (DATA_SIZE - 1);

    public static final int REGISTRY_SIZE = (1 << (ID_BITS + DATA_BITS));
    public static final int REGISTRY_MASK = (REGISTRY_SIZE - 1);

    public static final BlockDataConstant AIR = new BlockDataConstant(BlockHandle.getById(0));
    public static final BlockDataConstant[] BY_ID = new BlockDataConstant[ID_SIZE];
    public static final BlockDataConstant[] BY_ID_AND_DATA = new BlockDataConstant[REGISTRY_SIZE];
    public static final IdentityHashMap<Object, BlockDataConstant> BY_BLOCK_DATA = new IdentityHashMap<Object, BlockDataConstant>();

    static {
        // Cache all possible Block Ids (0 - 255) and combined Id+Data (0 - 4096)
        Arrays.fill(BY_ID, AIR);
        Arrays.fill(BY_ID_AND_DATA, AIR);
        for (Object rawBlock : BlockHandle.REGISTRY) {
            BlockHandle block = BlockHandle.createHandle(rawBlock);
            int id = BlockHandle.getId(block);
            BlockDataConstant blockConst = (id == 0) ? AIR : new BlockDataConstant(block);
            BY_ID[id] = blockConst;
            Arrays.fill(BY_ID_AND_DATA, id << DATA_BITS, (id + 1) << DATA_BITS, blockConst);
        }

        // Cache a mapping of all possible IBlockData instances
        for (Object rawIBlockData : BlockHandle.REGISTRY_ID) {
            IBlockDataHandle blockData = IBlockDataHandle.createHandle(rawIBlockData);
            BlockDataConstant block_const = BY_ID[BlockHandle.getId(blockData.getBlock())];
            if (block_const.getData() != rawIBlockData) {
                block_const = new BlockDataConstant(blockData);
            }
            BY_BLOCK_DATA.put(rawIBlockData, block_const);
            BY_ID_AND_DATA[block_const.getCombinedId_1_8_8()] = block_const;
        }

        BY_BLOCK_DATA.put(null, AIR);
    }

    /**
     * Used to convert a BlockData value into an immutable version.
     * This protects statically registered block objects from being mutated by plugins.
     */
    public static class BlockDataConstant extends BlockDataImpl {

        public BlockDataConstant(BlockHandle block) {
            super(block);
        }

        public BlockDataConstant(IBlockDataHandle blockData) {
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
        this(BlockHandle.getById(0));
    }

    public BlockDataImpl(IBlockDataHandle data) {
        this(data.getBlock(), data);
    }

    public BlockDataImpl(BlockHandle block) {
        this(block, block.getBlockData());
    }

    public BlockDataImpl(BlockHandle block, IBlockDataHandle data) {
        this.block = block;
        this.data = data;
    }

    @Override
    public void loadBlock(Object block) {
        this.block = BlockHandle.createHandle(block);
        this.data = this.block.getBlockData();
    }

    @Override
    public void loadBlockData(Object iBlockData) {
        this.data = IBlockDataHandle.createHandle(iBlockData);
        this.block = this.data.getBlock();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void loadMaterialData(Material material, int data) {
        this.block = BlockHandle.getById(material.getId());
        this.data = this.block.fromLegacyData(data);
    }

    @Override
    public final BlockHandle getBlock() {
        return this.block;
    }

    @Override
    public final Object getData() {
        return this.data.getRaw();
    }

    @Override
    public final int getTypeId() {
        return BlockHandle.getId(this.block);
    }

    @Override
    public final int getRawData() {
        return this.block.toLegacyData(this.data);
    }

    @Override
    public final int getCombinedId() {
        return BlockHandle.getCombinedId(this.data);
    }

    @Override
    public final int getCombinedId_1_8_8() {
        if (RegistryBlockIDHandle.T.isAssignableFrom(BlockHandle.REGISTRY_ID)) {
            // >= MC 1.10.2
            return RegistryBlockIDHandle.T.getId.invoke(BlockHandle.REGISTRY_ID, this.data.getRaw());
        } else {
            // <= MC 1.8.8
            return RegistryIDHandle.T.getId.invoke(BlockHandle.REGISTRY_ID, this.data.getRaw());
        }
    }

    @Override
    public String getBlockName() {
        Object minecraftKey = RegistryMaterialsHandle.T.getKey.invoke(BlockHandle.REGISTRY, this.getBlockRaw());
        return MinecraftKeyHandle.T.name.get(minecraftKey);
    }

    /* ====================================================================== */
    /* ========================= Block Properties =========================== */
    /* ====================================================================== */

    @Override
    public final String getStepSound() {
        return block.getStepSound().getDefault().toString();
    }

    @Override
    public final int getOpacity() {
        return this.block.getOpacity(this.data);
    }

    @Override
    public final int getEmission() {
        return this.block.getEmission(this.data);
    }

    @Override
    public final boolean isOccluding() {
        return this.block.isOccluding(this.data);
    }

    @Override
    public final boolean isSuffocating() {
        return this.block.isOccluding(this.data);
    }

    @Override
    public final boolean isPowerSource() {
        return this.block.isPowerSource(this.data);
    }

    @Override
    public final float getDamageResilience(org.bukkit.entity.Entity source) {
        return this.block.getDamageResillience(source);
    }

    @Override
    public final void dropNaturally(org.bukkit.World world, int x, int y, int z, float yield, int chance) {
        this.block.dropNaturally(world, new IntVector3(x, y, z), this.data, yield, chance);
    }

    @Override
    public final void ignite(org.bukkit.World world, int x, int y, int z) {
        ExplosionHandle ex = ExplosionHandle.createNew(world, null, x, y, z, 4.0f, true, true);
        this.block.ignite(world, new IntVector3(x, y, z), ex);
    }

    @Override
    public void destroy(org.bukkit.World world, int x, int y, int z, float yield) {
        dropNaturally(world, x, y, z, yield);
        WorldUtil.setBlockData(world, x, y, z, AIR);
    }

    @Override
    public void stepOn(org.bukkit.World world, IntVector3 blockPosition, org.bukkit.entity.Entity entity) {
        this.block.stepOn(world, blockPosition, entity);
    }
}
