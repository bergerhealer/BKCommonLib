package com.bergerkiller.bukkit.common.wrappers;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.AxisAlignedBBHandle;
import com.bergerkiller.generated.net.minecraft.server.BlockHandle;
import com.bergerkiller.generated.net.minecraft.server.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.ExplosionHandle;
import com.bergerkiller.generated.net.minecraft.server.IBlockDataHandle;
import com.bergerkiller.generated.net.minecraft.server.IBlockStateHandle;
import com.bergerkiller.generated.net.minecraft.server.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.server.RegistryBlockIDHandle;
import com.bergerkiller.generated.net.minecraft.server.RegistryIDHandle;
import com.bergerkiller.generated.net.minecraft.server.RegistryMaterialsHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;

@SuppressWarnings("deprecation")
public class BlockDataImpl extends BlockData {
    private BlockHandle block;
    private IBlockDataHandle data;
    private MaterialData materialData;
    private Material type;
    private int rawData;
    private boolean hasRenderOptions;

    public static final int ID_BITS = 8;
    public static final int DATA_BITS = 4;

    public static final int REGISTRY_SIZE = (1 << 16); // 65536
    public static final int REGISTRY_MASK = (REGISTRY_SIZE - 1);

    public static final BlockDataConstant AIR;
    public static final EnumMap<Material, BlockDataConstant> BY_MATERIAL = new EnumMap<Material, BlockDataConstant>(Material.class);
    public static final BlockDataConstant[] BY_ID_AND_DATA = new BlockDataConstant[REGISTRY_SIZE];
    public static final Map<Object, BlockDataConstant> BY_BLOCK = new IdentityHashMap<Object, BlockDataConstant>();
    public static final IdentityHashMap<Object, BlockDataConstant> BY_BLOCK_DATA = new IdentityHashMap<Object, BlockDataConstant>();

    // Legacy: array of all possible Material values with all possible legacy data values
    // Index into it by taking data x 1024 | mat.ordinal()
    public static final int BY_LEGACY_MAT_DATA_SHIFT = 10; // (1<<10 == 1024)
    public static final BlockDataConstant[] BY_LEGACY_MAT_DATA = new BlockDataConstant[16 << BY_LEGACY_MAT_DATA_SHIFT];

    static {
        // Fill BY_MATERIAL and BY_BLOCK mapping with all existing Block types
        AIR = new BlockDataConstant(BlockHandle.createHandle(CraftMagicNumbersHandle.getBlockFromMaterial(Material.AIR)));
        for (Object rawBlock : BlockHandle.REGISTRY) {
            BlockHandle block = BlockHandle.createHandle(rawBlock);
            Material material = CraftMagicNumbersHandle.getMaterialFromBlock(rawBlock);
            BlockDataConstant blockConst = (material == Material.AIR) ? AIR : new BlockDataConstant(block);
            BY_BLOCK.put(rawBlock, blockConst);
            BY_MATERIAL.put(material, blockConst);
        }

        // Cache a mapping of all possible IBlockData instances
        Arrays.fill(BY_ID_AND_DATA, AIR);
        for (Object rawIBlockData : BlockHandle.REGISTRY_ID) {
            IBlockDataHandle blockData = IBlockDataHandle.createHandle(rawIBlockData);
            BlockDataConstant block_const = BY_BLOCK.get(blockData.getBlock().getRaw());
            if (block_const.getData() != rawIBlockData) {
                block_const = new BlockDataConstant(blockData);
            }
            BY_BLOCK_DATA.put(rawIBlockData, block_const);
            BY_ID_AND_DATA[block_const.getCombinedId_1_8_8()] = block_const;
        }
        BY_BLOCK_DATA.put(null, AIR);

        // Check for any missing Material enum values - store those also in the BY_MATERIAL mapping
        // This mainly applies to the legacy 1.13 enum values
        // Also store all possible values of BY_LEGACY_MAT_DATA
        Arrays.fill(BY_LEGACY_MAT_DATA, AIR);
        for (Material mat : Material.values()) {
            if (!mat.isBlock()) {
                BY_MATERIAL.put(mat, AIR);
                continue;
            }

            BlockDataConstant blockConst = BY_MATERIAL.get(mat);
            if (blockConst == null) {
                Object rawBlock = CraftMagicNumbersHandle.getBlockFromMaterial(mat);
                blockConst = BY_BLOCK.get(rawBlock);
                if (blockConst == null) {
                    blockConst = new BlockDataConstant(BlockHandle.createHandle(rawBlock));
                    BY_BLOCK.put(rawBlock, blockConst);
                }
                BY_MATERIAL.put(mat, blockConst);
            }

            for (byte data = 0; data < 16; data++) {
                // Find IBlockData from Material + Data and cache it if needed
                Object rawBlockData = CraftMagicNumbersHandle.fromLegacyData(mat, data);
                BlockDataConstant dataBlockConst = blockConst;
                if (rawBlockData != blockConst.getData()) {
                    dataBlockConst = BY_BLOCK_DATA.get(rawBlockData);
                    if (dataBlockConst == null) {
                        dataBlockConst = new BlockDataConstant(IBlockDataHandle.createHandle(rawBlockData));
                        BY_BLOCK_DATA.put(rawBlockData, dataBlockConst);
                    }
                }

                // Store in lookup table
                BY_LEGACY_MAT_DATA[mat.ordinal() | ((int) data << BY_LEGACY_MAT_DATA_SHIFT)] = dataBlockConst;
            }
        }
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
        this(AIR.getBlock());
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
        this.refreshBlock();
    }

    @Override
    public void loadBlock(Object block) {
        this.block = BlockHandle.createHandle(block);
        this.data = this.block.getBlockData();
        this.refreshBlock();
    }

    @Override
    public void loadBlockData(Object iBlockData) {
        this.data = IBlockDataHandle.createHandle(iBlockData);
        this.block = this.data.getBlock();
        this.refreshBlock();
    }

    @Override
    public void loadMaterialData(Material material, int data) {
        this.block = BlockHandle.createHandle(CraftMagicNumbersHandle.getBlockFromMaterial(material));
        this.data = IBlockDataHandle.createHandle(CraftMagicNumbersHandle.fromLegacyData(material, (byte) data));
        this.refreshBlock();
    }

    private final void refreshBlock() {
        this.materialData = null;
        this.hasRenderOptions = true;
        this.type = CraftMagicNumbersHandle.getMaterialFromBlock(this.block.getRaw());
        this.rawData = CraftMagicNumbersHandle.toLegacyData(this.data.getRaw());
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
        return this.type.getId();
    }

    @Override
    public final int getRawData() {
        return this.rawData;
    }

    @Override
    public final org.bukkit.Material getType() {
        return this.type;
    }

    @Override
    public final MaterialData getMaterialData() {
        if (this.materialData == null) {
            Material type = this.getType();

            // Null: return AIR
            if (type == null) {
                return new MaterialData(Material.AIR, (byte) 0);
            }

            // Create new MaterialData + some fixes.
            if (LogicUtil.contains(type.name(), "GOLD_PLATE", "IRON_PLATE", "HEAVY_WEIGHTED_PRESSURE_PLATE", "LIGHT_WEIGHTED_PRESSURE_PLATE")) {
                // Bukkit bugfix.
                this.materialData = new org.bukkit.material.PressurePlate(type, (byte) this.getRawData());
            } else if (
                    type == Material.JUNGLE_DOOR || type == Material.ACACIA_DOOR ||
                    type == Material.DARK_OAK_DOOR || type == Material.SPRUCE_DOOR ||
                    type == Material.BIRCH_DOOR) {
                // Bukkit bugfix. (<= 1.8.3)
                this.materialData = new org.bukkit.material.Door(type, (byte) this.getRawData());
            } else {
                this.materialData = type.getNewData((byte) this.getRawData());
            }

            // Fix attachable face returning NULL sometimes
            if (this.materialData instanceof Attachable) {
                Attachable att = (Attachable) this.materialData;
                if (att.getAttachedFace() == null) {
                    att.setFacingDirection(BlockFace.NORTH);
                }
            }
        }
        return this.materialData;
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

    @Override
    public BlockRenderOptions getRenderOptions(World world, int x, int y, int z) {
        if (!this.hasRenderOptions) {
            return new BlockRenderOptions(this, "");
        }

        Object stateData;
        if (world == null) {
            //TODO: We should call updateState() with an IBlockAccess that returns all Air.
            // Right now, it will return the options of the last-modified block
            stateData = this.data.getRaw();
        } else {
            // This refreshes the state (cached) to reflect a particular Block
            stateData = BlockHandle.T.updateState.raw.invoke(
                    this.block.getRaw(),
                    this.data.getRaw(),
                    HandleConversion.toWorldHandle(world),
                    BlockPositionHandle.T.constr_x_y_z.raw.newInstance(x, y, z)
            );
        }

        // Not sure if this can happen; but we handle it!
        if (stateData == null) {
            return new BlockRenderOptions(this, new HashMap<String, String>(0));
        }

        // Serialize all tokens into String key-value pairs
        Map<Object, Object> states = IBlockDataHandle.T.getStates.invoke(stateData);
        Map<String, String> statesStr = new HashMap<String, String>(states.size());
        for (Map.Entry<Object, Object> state : states.entrySet()) {
            String key = IBlockStateHandle.T.getKeyToken.invoke(state.getKey());
            String value = IBlockStateHandle.T.getValueToken.invoke(state.getKey(), state.getValue());
            statesStr.put(key, value);
        }
        BlockRenderOptions options = new BlockRenderOptions(this, statesStr);

        // Add additional options not provided by the server
        // This handles the display parameters for blocks like Water and Lava
        BlockRenderProvider renderProvider = BlockRenderProvider.get(this);
        if (renderProvider != null) {
            renderProvider.addOptions(options, world, x, y, z);
        }

        // When no options are being used, do not check for them again in the future
        // This offers performance benefits
        if (options.isEmpty()) {
            this.hasRenderOptions = false;
        }

        return options;
    }

    /* ====================================================================== */
    /* ========================= Block Properties =========================== */
    /* ====================================================================== */

    @Override
    public final ResourceKey getStepSound() {
        return ResourceKey.fromMinecraftKey(block.getSoundType().getStepSound().getName());
    }

    @Override
    public final ResourceKey getPlaceSound() {
        return ResourceKey.fromMinecraftKey(block.getSoundType().getPlaceSound().getName());
    }

    @Override
    public final ResourceKey getBreakSound() {
        return ResourceKey.fromMinecraftKey(block.getSoundType().getBreakSound().getName());
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
    public final AxisAlignedBBHandle getBoundingBox(Block block) {
        return this.block.getBoundingBox(this.data, WorldHandle.fromBukkit(block.getWorld()), new IntVector3(block));
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
