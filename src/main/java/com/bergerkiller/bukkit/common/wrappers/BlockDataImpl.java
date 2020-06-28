package com.bergerkiller.bukkit.common.wrappers;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.CommonListener;
import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.internal.legacy.IBlockDataToMaterialData;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialDataToIBlockData;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataSerializer;
import com.bergerkiller.bukkit.common.resources.ResourceCategory;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.AxisAlignedBBHandle;
import com.bergerkiller.generated.net.minecraft.server.BlockHandle;
import com.bergerkiller.generated.net.minecraft.server.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.ExplosionHandle;
import com.bergerkiller.generated.net.minecraft.server.IBlockDataHandle;
import com.bergerkiller.generated.net.minecraft.server.IBlockStateHandle;
import com.bergerkiller.generated.net.minecraft.server.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.server.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.server.RegistryBlockIDHandle;
import com.bergerkiller.generated.net.minecraft.server.RegistryIDHandle;
import com.bergerkiller.generated.net.minecraft.server.RegistryMaterialsHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingMap;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;

@SuppressWarnings("deprecation")
public class BlockDataImpl extends BlockData {
    private BlockHandle block;
    private IBlockDataHandle data;
    private MaterialData materialData;
    private Material type;
    private boolean hasRenderOptions;
    private int combinedId;

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
    public static final int BY_LEGACY_MAT_DATA_SHIFT = 11; // (1<<11 == 2048)
    public static final BlockDataConstant[] BY_LEGACY_MAT_DATA = new BlockDataConstant[16 << BY_LEGACY_MAT_DATA_SHIFT];

    // These Material types do not support Block.updateState, as they cause issues with block physics events being fired
    private static final EnumSet<Material> BLOCK_UPDATE_STATE_BLACKLIST = EnumSet.noneOf(Material.class);

    static {
        // Make sure templates are initialized first
        Common.bootstrap();

        // Retrieve
        Iterable<?> REGISTRY = BlockHandle.getRegistry();

        // Fill BY_MATERIAL and BY_BLOCK mapping with all existing Block types
        AIR = new BlockDataConstant(BlockHandle.createHandle(CraftMagicNumbersHandle.getBlockFromMaterial(Material.AIR)));
        for (Object rawBlock : REGISTRY) {
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

        // Sanity check
        if (CommonLegacyMaterials.getAllMaterials().length >= (1<<BY_LEGACY_MAT_DATA_SHIFT)) {
            throw new IllegalStateException("BY_LEGACY_MAT_DATA_SHIFT is too low, can't store " +
                    CommonLegacyMaterials.getAllMaterials().length + " materials");
        }

        // Check for any missing Material enum values - store those also in the BY_MATERIAL mapping
        // This mainly applies to the legacy 1.13 enum values
        // Also store all possible values of BY_LEGACY_MAT_DATA
        Arrays.fill(BY_LEGACY_MAT_DATA, AIR);
        for (Material mat : CommonLegacyMaterials.getAllMaterials()) {
            if (!mat.isBlock()) {
                BY_MATERIAL.put(mat, AIR);
                continue;
            }

            BlockDataConstant blockConst = BY_MATERIAL.get(mat);
            if (blockConst == null) {
                if (CommonCapabilities.MATERIAL_ENUM_CHANGES && CommonLegacyMaterials.isLegacy(mat)) {
                    // Legacy Material -> IBlockData logic
                    MaterialData materialData = IBlockDataToMaterialData.createMaterialData(mat);
                    blockConst = findConstant(MaterialDataToIBlockData.getIBlockData(materialData));
                } else {
                    // Normal Material -> Block -> IBlockData logic
                    Object rawBlock = CraftMagicNumbersHandle.getBlockFromMaterial(mat);
                    blockConst = BY_BLOCK.get(rawBlock);
                    if (blockConst == null) {
                        blockConst = new BlockDataConstant(BlockHandle.createHandle(rawBlock));
                        BY_BLOCK.put(rawBlock, blockConst);
                    }
                }
                BY_MATERIAL.put(mat, blockConst);
            }

            MaterialData materialdata = new MaterialData(mat);
            for (int data = 0; data < 16; data++) {
                // Find IBlockData from Material + Data and cache it if needed
                materialdata.setData((byte) data);
                IBlockDataHandle blockData = MaterialDataToIBlockData.getIBlockData(materialdata);
                BlockDataConstant dataBlockConst = blockConst;
                if (blockData == null) {
                    Logging.LOGGER_REGISTRY.warning("Obtaining BlockData of MaterialData " + materialdata + " yielded null result!");
                } else if (blockData.getRaw() != blockConst.getData()) {
                    dataBlockConst = findConstant(blockData);
                }

                // Store in lookup table
                int index = CommonLegacyMaterials.getOrdinal(mat);
                index |= (data << BY_LEGACY_MAT_DATA_SHIFT);
                BY_LEGACY_MAT_DATA[index] = dataBlockConst;
            }
        }

        // Do not allow updateState() on these block types
        // Some materials do not exist on all MC versions, hence the hack with the enum names
        try {
            String[] blocked_types =new String[] {
                    "BlockPlant",
                    "BlockObserver",
                    "BlockBubbleColumn",
                    "BlockConcretePowder",
                    "BlockLeaves",
                    "BlockDirtSnow"
            };
            for (String nmsBlockTypeName : blocked_types) {
                Class<?> nmsBlockType = CommonUtil.getNMSClass(nmsBlockTypeName);
                if (nmsBlockType != null) {
                    for (Material mat : CommonLegacyMaterials.getAllMaterials()) {
                        BlockDataConstant blockData = BY_MATERIAL.get(mat);
                        if (blockData != null) {
                            Object raw_block = blockData.getBlockRaw();
                            if (raw_block != null && nmsBlockType.isAssignableFrom(raw_block.getClass()))
                                BLOCK_UPDATE_STATE_BLACKLIST.add(mat);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            // This happens sometimes?
            t.printStackTrace();
        }
    }

    private static BlockDataConstant findConstant(IBlockDataHandle iblockdata) {
        BlockDataConstant dataBlockConst = BY_BLOCK_DATA.get(iblockdata.getRaw());
        if (dataBlockConst == null) {
            dataBlockConst = new BlockDataConstant(iblockdata);
            BY_BLOCK_DATA.put(iblockdata.getRaw(), dataBlockConst);
        }
        return dataBlockConst;
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
        public void loadMaterialData(MaterialData materialdata) {
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
    public void loadMaterialData(MaterialData materialdata) {
        this.data = MaterialDataToIBlockData.getIBlockData(materialdata);
        this.block = this.data.getBlock();
        this.refreshBlock();
    }

    private final void refreshBlock() {
        this.hasRenderOptions = true;
        this.type = CraftMagicNumbersHandle.getMaterialFromBlock(this.block.getRaw());
        this.materialData = IBlockDataToMaterialData.getMaterialData(this.data);
        this.combinedId = BlockHandle.getCombinedId(this.data);
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
    public final int getRawData() {
        return this.materialData.getData();
    }

    @Override
    public final org.bukkit.Material getType() {
        return this.type;
    }

    @Override
    public final org.bukkit.Material getLegacyType() {
        return CommonLegacyMaterials.toLegacy(this.materialData.getItemType());
    }

    @Override
    public final MaterialData getMaterialData() {
        return this.materialData;
    }

    @Override
    public final int getCombinedId() {
        return this.combinedId;
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
        Object minecraftKey = RegistryMaterialsHandle.T.getKey.invoke(BlockHandle.getRegistry(), this.getBlockRaw());
        return MinecraftKeyHandle.T.name.get(minecraftKey);
    }

    @Override
    public String serializeToString() {
        return BlockDataSerializer.INSTANCE.serialize(this);
    }

    @Override
    public BlockRenderOptions getRenderOptions(World world, int x, int y, int z) {
        if (!this.hasRenderOptions) {
            return new BlockRenderOptions(this, "");
        }

        CommonListener.BLOCK_PHYSICS_FIRED = false;

        Object stateData;
        if (world == null || BLOCK_UPDATE_STATE_BLACKLIST.contains(this.getType())) {
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

        BlockRenderOptions options;

        if (stateData == null) {
            // Not sure if this can happen; but we handle it!
            options = new BlockRenderOptions(this, new HashMap<String, String>(0));
        } else {
            // Serialize all tokens into String key-value pairs
            Map<IBlockStateHandle, Comparable<?>> states = IBlockDataHandle.T.getStates.invoke(stateData);
            Map<String, String> statesStr = new HashMap<String, String>(states.size());
            for (Map.Entry<IBlockStateHandle, Comparable<?>> state : states.entrySet()) {
                String key = state.getKey().getKeyToken();
                String value = state.getKey().getValueToken(state.getValue());
                statesStr.put(key, value);
            }
            options = new BlockRenderOptions(this, statesStr);
        }

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

        // Block physics events ruin things, if they occur, disable the type and log it
        if (CommonListener.BLOCK_PHYSICS_FIRED) {
            CommonListener.BLOCK_PHYSICS_FIRED = false;
            BLOCK_UPDATE_STATE_BLACKLIST.add(this.getType());

            Logging.LOGGER.warning("[BlockData] Block physics are occurring when reading state of " + 
                    CommonLegacyMaterials.getMaterialName(this.getType()) +
                    " data=" + this.toString() + " options=" + options);
        }

        return options;
    }

    /* ====================================================================== */
    /* ========================= Block Properties =========================== */
    /* ====================================================================== */

    @Override
    public final ResourceKey<SoundEffect> getStepSound() {
        return ResourceCategory.sound_effect.createKey(block.getSoundType(data).getStepSound().getName());
    }

    @Override
    public final ResourceKey<SoundEffect> getPlaceSound() {
        return ResourceCategory.sound_effect.createKey(block.getSoundType(data).getPlaceSound().getName());
    }

    @Override
    public final ResourceKey<SoundEffect> getBreakSound() {
        return ResourceCategory.sound_effect.createKey(block.getSoundType(data).getBreakSound().getName());
    }

    @Override
    public final int getOpacity(World world, int x, int y, int z) {
        return this.block.getOpacity(this.data, world, x, y, z);
    }

    @Override
    public final int getEmission() {
        return this.block.getEmission(this.data);
    }

    @Override
    public final boolean isOccluding(Block block) {
        return this.block.isOccluding(this.data, block);
    }

    @Override
    public boolean isOccluding(World world, int x, int y, int z) {
        return this.block.isOccluding_at(this.data, world, x, y, z);
    }

    @Override
    public final boolean isSuffocating(Block block) {
        return this.block.isOccluding(this.data, block);
    }

    @Override
    public final boolean isPowerSource() {
        return this.block.isPowerSource(this.data);
    }

    @Override
    public boolean isBuildable() {
        return this.data.isBuildable();
    }

    @Override
    public final AxisAlignedBBHandle getBoundingBox(Block block) {
        return this.block.getBoundingBox(this.data, WorldHandle.fromBukkit(block.getWorld()), new IntVector3(block));
    }

    @Override
    public final float getDamageResilience() {
        return this.block.getDamageResillience();
    }

    @Override
    public final boolean canSupportTop(Block block) {
        return this.block.canSupportTop(this.data, block);
    }

    @Override
    public final void dropNaturally(org.bukkit.World world, int x, int y, int z, float yield, int chance) {
        this.block.dropNaturally(this.data, world, new IntVector3(x, y, z), yield, chance);
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

    @Override
    public BlockData setState(String key, Object value) {
        IBlockDataHandle updated_data = this.data.set(key, value);
        BlockData data = BlockDataImpl.BY_BLOCK_DATA.get(updated_data.getRaw());
        if (data != null) {
            return data;
        }

        // Store in map (should only rarely happen)
        BlockDataConstant c = new BlockDataImpl.BlockDataConstant(updated_data);
        BY_BLOCK_DATA.put(updated_data.getRaw(), c);
        return c;
    }

    @Override
    public BlockData setState(BlockState<?> state, Object value) {
        IBlockDataHandle updated_data = this.data.set(state.getBackingHandle(), value);
        BlockData data = BlockDataImpl.BY_BLOCK_DATA.get(updated_data.getRaw());
        if (data != null) {
            return data;
        }

        // Store in map (should only rarely happen)
        BlockDataConstant c = new BlockDataImpl.BlockDataConstant(updated_data);
        BY_BLOCK_DATA.put(updated_data.getRaw(), c);
        return c;
    }

    @Override
    public <T> T getState(String key, Class<T> type) {
        return this.data.get(key, type);
    }

    @Override
    public <T extends Comparable<?>> T getState(BlockState<T> state) {
        return CommonUtil.unsafeCast(this.data.get(state.getBackingHandle()));
    }

    @Override
    public Map<BlockState<?>, Comparable<?>> getStates() {
        DuplexConverter<IBlockStateHandle, BlockState<?>> keyConverter = new DuplexConverter<IBlockStateHandle, BlockState<?>>(IBlockStateHandle.class, BlockState.class) {
            @Override
            public BlockState<?> convertInput(IBlockStateHandle value) {
                return new BlockState<Comparable<?>>(value);
            }

            @Override
            public IBlockStateHandle convertOutput(BlockState<?> value) {
                return value.getBackingHandle();
            }
        };
        DuplexConverter<Comparable<?>, Comparable<?>> valueConverter = DuplexConverter.createNull(TypeDeclaration.fromClass(Comparable.class));
        return new ConvertingMap<BlockState<?>, Comparable<?>>(this.data.getStates(), keyConverter, valueConverter);
    }

    @Override
    public org.bukkit.inventory.ItemStack createItem(int amount) {
        return ItemStackHandle.fromBlockData(this.data, amount).toBukkit();
    }
}
