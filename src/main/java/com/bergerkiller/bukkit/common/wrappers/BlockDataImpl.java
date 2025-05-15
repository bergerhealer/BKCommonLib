package com.bergerkiller.bukkit.common.wrappers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.BlockFaceSet;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.CommonListener;
import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.internal.legacy.IBlockDataToMaterialData;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialDataToIBlockData;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataSerializer;
import com.bergerkiller.bukkit.common.resources.ResourceCategory;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.core.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.core.RegistryBlockIDHandle;
import com.bergerkiller.generated.net.minecraft.core.RegistryMaterialsHandle;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.util.RegistryIDHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.IBlockDataHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.properties.IBlockStateHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AxisAlignedBBHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingMap;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;

@SuppressWarnings("deprecation")
class BlockDataImpl extends BlockData {
    private BlockHandle block;
    private IBlockDataHandle data;
    private MaterialData materialData;
    private Material type;
    private Material legacyType;
    private boolean hasRenderOptions;
    private int combinedId;

    // Cached properties
    private BlockFaceSet cachedOpaqueFaces; // null if unavailable
    private int cachedOpacity; // -1 if unavailable

    public static final int ID_BITS = 8;
    public static final int DATA_BITS = 4;

    public static final int REGISTRY_SIZE;
    public static final int REGISTRY_MASK;
    public static final BlockDataConstant[] BY_ID_AND_DATA;

    public static final Material LEGACY_AIR_TYPE;
    public static final BlockDataConstant AIR;
    public static final EnumMap<Material, BlockDataConstant> BY_MATERIAL = new EnumMap<Material, BlockDataConstant>(Material.class);
    public static final Map<Object, BlockDataConstant> BY_BLOCK = new IdentityHashMap<Object, BlockDataConstant>();
    public static final Cache BY_BLOCK_DATA;

    // Legacy: array of all possible Material values with all possible legacy data values
    // Index into it by taking data x 1024 | mat.ordinal()
    public static final int BY_LEGACY_MAT_DATA_SHIFT;
    public static final BlockDataConstant[] BY_LEGACY_MAT_DATA;

    // These Material types do not support Block.updateState, as they cause issues with block physics events being fired
    private static final EnumSet<Material> BLOCK_UPDATE_STATE_BLACKLIST = EnumSet.noneOf(Material.class);

    static {
        // Block Data requires an initialized server to work properly, so initialize that now
        // Doing this here makes it so that server identification and test server setup is done
        // before templates are initialized. Easier to debug.
        CommonBootstrap.initServer();

        // Retrieve
        Iterable<?> REGISTRY = BlockHandle.getRegistry();

        // Fill BY_MATERIAL and BY_BLOCK mapping with all existing Block types
        LEGACY_AIR_TYPE = MaterialsByName.getMaterial("LEGACY_AIR");
        AIR = new BlockDataConstant(BlockHandle.createHandle(CraftMagicNumbersHandle.getBlockFromMaterial(Material.AIR)));
        for (Object rawBlock : REGISTRY) {
            BlockHandle block = BlockHandle.createHandle(rawBlock);
            Material material = CraftMagicNumbersHandle.getMaterialFromBlock(rawBlock);
            BlockDataConstant blockConst = (material == Material.AIR) ? AIR : new BlockDataConstant(block);
            BY_BLOCK.put(rawBlock, blockConst);
            if (material != null) {
                BY_MATERIAL.put(material, blockConst);
            }
        }

        // Create cache
        BY_BLOCK_DATA = new Cache(AIR);

        // Cache a mapping of all possible IBlockData instances
        {
            BlockDataConstant[] tmp = new BlockDataConstant[1 << 14];
            Arrays.fill(tmp, AIR);

            for (Object rawIBlockData : BlockHandle.REGISTRY_ID) {
                IBlockDataHandle blockData = IBlockDataHandle.createHandle(rawIBlockData);
                BlockDataConstant block_const = BY_BLOCK.get(blockData.getBlock().getRaw());
                if (block_const.getData() != rawIBlockData) {
                    block_const = new BlockDataConstant(blockData);
                }
                BY_BLOCK_DATA.cacheWritable.put(rawIBlockData, block_const);

                int combined_id = block_const.getCombinedId_1_8_8();
                while (combined_id >= tmp.length) {
                    int oldLength = tmp.length;
                    tmp = Arrays.copyOf(tmp, oldLength << 1);
                    Arrays.fill(tmp, oldLength, tmp.length, AIR);
                }
                tmp[combined_id] = block_const;
            }

            // Note: array must be power of 2 in length
            BY_ID_AND_DATA = tmp;
            REGISTRY_SIZE = tmp.length;
            REGISTRY_MASK = REGISTRY_SIZE - 1;
        }

        // Cache
        BY_BLOCK_DATA.updateVisible();

        // Compute how large to make the legacy material data array to fit in all materials
        {
            int shift = 1;
            while (CommonLegacyMaterials.getAllMaterials().length >= (1<<shift))
                shift++;
            BY_LEGACY_MAT_DATA_SHIFT = shift;
            BY_LEGACY_MAT_DATA = new BlockDataConstant[16 << BY_LEGACY_MAT_DATA_SHIFT];
        }

        // Check for any missing Material enum values - store those also in the BY_MATERIAL mapping
        // This mainly applies to the legacy 1.13 enum values
        // Also store all possible values of BY_LEGACY_MAT_DATA
        Arrays.fill(BY_LEGACY_MAT_DATA, AIR);
        for (Material mat : CommonLegacyMaterials.getAllMaterials()) {
            if (!MaterialsByName.isBlock(mat)) {
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
            if (materialdata.getItemType() == null) {
                // On forge these materials don't have valid registration in the server
                // Just fill with the base IBlockData
                int index = CommonLegacyMaterials.getOrdinal(mat);
                for (int data = 0; data < 16; data++) {
                    BY_LEGACY_MAT_DATA[index | (data << BY_LEGACY_MAT_DATA_SHIFT)] = blockConst;
                }
                continue;
            }

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
                    "net.minecraft.world.level.block.BlockPlant",
                    "net.minecraft.world.level.block.BlockObserver",
                    "net.minecraft.world.level.block.BlockBubbleColumn",
                    "net.minecraft.world.level.block.BlockConcretePowder",
                    "net.minecraft.world.level.block.BlockLeaves",
                    "net.minecraft.world.level.block.BlockDirtSnow"
            };
            for (String nmsBlockTypeName : blocked_types) {
                Class<?> nmsBlockType = CommonUtil.getClass(nmsBlockTypeName);
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
            Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Error initializing BlockData updateState() blacklist", t);
        }
    }

    private static BlockDataConstant findConstant(IBlockDataHandle iblockdata) {
        BlockDataConstant dataBlockConst = BY_BLOCK_DATA.get(iblockdata.getRaw());
        if (dataBlockConst == null) {
            return BY_BLOCK_DATA.create(iblockdata);
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
        this.type = LogicUtil.fixNull(CraftMagicNumbersHandle.getMaterialFromBlock(this.block.getRaw()), Material.AIR);
        this.materialData = IBlockDataToMaterialData.getMaterialData(this.data);
        this.legacyType = CommonLegacyMaterials.toLegacy(this.materialData.getItemType());
        this.combinedId = BlockHandle.getCombinedId(this.data);
        this.cachedOpaqueFaces = this.data.getCachedOpaqueFaces();
        this.cachedOpacity = this.data.getCachedOpacity();
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
        return this.legacyType;
    }

    @Override
    public final boolean hasLegacyType() {
        // If the legacy type is air, and this block data isn't air, then this BlockData has
        // no legacy type.
        return this.legacyType != LEGACY_AIR_TYPE || this == AIR;
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
        return MinecraftKeyHandle.T.getName.invoke(minecraftKey);
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
        return ResourceCategory.sound_effect.createKey(data.getSoundType().getStepSound().getName());
    }

    @Override
    public final ResourceKey<SoundEffect> getPlaceSound() {
        return ResourceCategory.sound_effect.createKey(data.getSoundType().getPlaceSound().getName());
    }

    @Override
    public final ResourceKey<SoundEffect> getBreakSound() {
        return ResourceCategory.sound_effect.createKey(data.getSoundType().getBreakSound().getName());
    }

    @Override
    public final int getOpacity(World world, int x, int y, int z) {
        if (this.cachedOpacity == -1) {
            try {
                return this.block.getOpacity(this.data, world, x, y, z);
            } catch (RuntimeException ex) {
                if (world == null) {
                    throw new IllegalArgumentException("For BlockData " + this + " World Access is required to read opacity");
                } else {
                    throw ex; // weird?
                }
            }
        } else {
            return this.cachedOpacity;
        }
    }

    @Override
    public final int getOpacity(Block block) {
        if (this.cachedOpacity == -1) {
            try {
                return this.block.getOpacity(this.data, block.getWorld(), block.getX(), block.getY(), block.getZ());
            } catch (NullPointerException ex) {
                if (block == null) {
                    throw new IllegalArgumentException("For BlockData " + this + " World Access is required to read opacity");
                } else {
                    throw ex; // weird?
                }
            }
        } else {
            return this.cachedOpacity;
        }
    }

    @Override
    public final BlockFaceSet getOpaqueFaces(World world, int x, int y, int z) {
        if (this.cachedOpaqueFaces != null) {
            return this.cachedOpaqueFaces;
        } else if (world == null) {
            throw new IllegalArgumentException("For BlockData " + this + " World Access is required to read opaque faces");
        } else {
            int mask = 0;
            if (this.block.isFaceOpaque(this.data, world, x, y, z, BlockFace.NORTH)) {
                mask |= BlockFaceSet.MASK_NORTH;
            }
            if (this.block.isFaceOpaque(this.data, world, x, y, z, BlockFace.EAST)) {
                mask |= BlockFaceSet.MASK_EAST;
            }
            if (this.block.isFaceOpaque(this.data, world, x, y, z, BlockFace.SOUTH)) {
                mask |= BlockFaceSet.MASK_SOUTH;
            }
            if (this.block.isFaceOpaque(this.data, world, x, y, z, BlockFace.WEST)) {
                mask |= BlockFaceSet.MASK_WEST;
            }
            if (this.block.isFaceOpaque(this.data, world, x, y, z, BlockFace.UP)) {
                mask |= BlockFaceSet.MASK_UP;
            }
            if (this.block.isFaceOpaque(this.data, world, x, y, z, BlockFace.DOWN)) {
                mask |= BlockFaceSet.MASK_DOWN;
            }
            return BlockFaceSet.byMask(mask);
        }
    }

    @Override
    public final BlockFaceSet getOpaqueFaces(Block block) {
        if (this.cachedOpaqueFaces != null) {
            return this.cachedOpaqueFaces;
        } else if (block == null) {
            throw new IllegalArgumentException("For BlockData " + this + " World Access is required to read opaque faces");
        } else {
            return getOpaqueFaces(block.getWorld(), block.getX(), block.getY(), block.getZ());
        }
    }

    @Override
    @Deprecated
    public final int getEmission() {
        return this.block.getEmission(this.data, null, 0, 0, 0);
    }

    @Override
    public int getEmission(World world, int x, int y, int z) {
        return this.block.getEmission(this.data, world, x, y, z);
    }

    @Override
    public int getEmission(Block block) {
        return this.block.getEmission(this.data, block.getWorld(), block.getX(), block.getY(), block.getZ());
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
        return this.data.isPowerSource();
    }

    @Override
    public boolean isSolid() {
        return this.data.isSolid();
    }

    @Override
    public final AxisAlignedBBHandle getBoundingBox(Block block) {
        return this.data.getBoundingBox(WorldHandle.fromBukkit(block.getWorld()), new IntVector3(block));
    }

    @Override
    public final float getDamageResilience() {
        return this.block.getDamageResillience();
    }

    @Override
    public final boolean canSupportTop(Block block) {
        return this.block.canSupportOnFace(this.data, block, BlockFace.UP);
    }

    /**
     * Gets whether the given block face of a Block is capable of supporting other blocks, like torches and minecart track.
     * 
     * @param block position where this BlockData exists
     * @param face BlockFace to check
     * @return True if supporting blocks on this face
     */
    public final boolean canSupportOnFace(Block block, BlockFace face) {
        return this.block.canSupportOnFace(this.data, block, face);
    }

    @Override
    public final void dropNaturally(org.bukkit.World world, int x, int y, int z, float yield, int chance) {
        this.block.dropNaturally(this.data, world, new IntVector3(x, y, z), yield, chance);
    }

    @Override
    public void destroy(org.bukkit.World world, int x, int y, int z, float yield) {
        dropNaturally(world, x, y, z, yield);
        WorldUtil.setBlockData(world, x, y, z, AIR);
    }

    @Override
    public void stepOn(org.bukkit.World world, IntVector3 blockPosition, org.bukkit.entity.Entity entity) {
        this.block.stepOn(world, blockPosition, this.data, entity);
    }

    @Override
    public BlockData setState(String key, Object value) {
        IBlockDataHandle updated_data = this.data.set(key, value);
        return BlockDataRegistry.fromBlockData(updated_data.getRaw());
    }

    @Override
    public BlockData setState(BlockDataStateKey<?> stateKey, Object value) {
        IBlockDataHandle updated_data = this.data.set(stateKey.getBackingHandle(), value);
        return BlockDataRegistry.fromBlockData(updated_data.getRaw());
    }

    @Override
    @Deprecated
    public BlockData setState(BlockState<?> stateKey, Object value) {
        IBlockDataHandle updated_data = this.data.set(stateKey.getBackingHandle(), value);
        return BlockDataRegistry.fromBlockData(updated_data.getRaw());
    }

    @Override
    public <T> T getState(String key, Class<T> type) {
        return this.data.get(key, type);
    }

    @Override
    public <T extends Comparable<?>> T getState(BlockDataStateKey<T> stateKey) {
        return CommonUtil.unsafeCast(this.data.get(stateKey.getBackingHandle()));
    }

    @Override
    public Map<BlockDataStateKey<?>, Comparable<?>> getStates() {
        DuplexConverter<IBlockStateHandle, BlockDataStateKey<?>> keyConverter = new DuplexConverter<IBlockStateHandle, BlockDataStateKey<?>>(IBlockStateHandle.class, BlockDataStateKey.class) {
            @Override
            public BlockDataStateKey<?> convertInput(IBlockStateHandle value) {
                return new BlockDataStateKey<Comparable<?>>(value);
            }

            @Override
            public IBlockStateHandle convertOutput(BlockDataStateKey<?> value) {
                return value.getBackingHandle();
            }
        };
        DuplexConverter<Comparable<?>, Comparable<?>> valueConverter = DuplexConverter.createNull(TypeDeclaration.fromClass(Comparable.class));
        return new ConvertingMap<BlockDataStateKey<?>, Comparable<?>>(this.data.getStates(), keyConverter, valueConverter);
    }

    @Override
    public BlockDataStateKey<?> getStateKey(String key) {
        IBlockStateHandle handle = this.data.findState(key);
        return (handle == null) ? null : new BlockDataStateKey<>(handle);
    }

    @Override
    public org.bukkit.inventory.ItemStack createItem(int amount) {
        return ItemStackHandle.fromBlockData(this.data, amount).toBukkit();
    }

    public static final class Cache {
        private final IdentityHashMap<Object, BlockDataConstant> cacheWritable;
        private IdentityHashMap<Object, BlockDataConstant> cache;
        private BlockDataConstant last;

        private Cache(BlockDataConstant air) {
            this.cacheWritable = new IdentityHashMap<>();
            this.cacheWritable.put(air.getBlockRaw(), air);
            this.last = air;
            updateVisible();
        }

        public Collection<BlockData> getAll() {
            return Collections.unmodifiableCollection(cache.values());
        }

        public BlockDataConstant get(Object rawIBlockData) {
            if (last.getBlockRaw() == rawIBlockData) {
                return last;
            } else {
                return cache.get(rawIBlockData);
            }
        }

        public BlockDataConstant getOrCreate(Object rawIBlockData) {
            BlockDataConstant last = this.last;
            if (last.getBlockRaw() == rawIBlockData) {
                return last;
            }

            return this.last = LogicUtil.synchronizeCopyOnWrite(this, l -> cache, rawIBlockData, IdentityHashMap::get,
                    (map, key) -> createUnsynchronized(IBlockDataHandle.createHandle(key)));
        }

        public synchronized BlockDataConstant create(IBlockDataHandle iblockdataHandle) {
            return createUnsynchronized(iblockdataHandle);
        }

        private BlockDataConstant createUnsynchronized(IBlockDataHandle iblockdataHandle) {
            BlockDataConstant c = new BlockDataImpl.BlockDataConstant(iblockdataHandle);
            cacheWritable.put(iblockdataHandle.getRaw(), c);
            updateVisible();
            return c;
        }

        @SuppressWarnings("unchecked")
        private void updateVisible() {
            cache = (IdentityHashMap<Object, BlockDataConstant>) cacheWritable.clone();
        }
    }
}
