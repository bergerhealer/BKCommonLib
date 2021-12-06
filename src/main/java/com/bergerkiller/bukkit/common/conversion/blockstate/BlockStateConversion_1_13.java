package com.bergerkiller.bukkit.common.conversion.blockstate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.utils.ChunkUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.TileEntityHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftWorldHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockStateHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.NullInstantiator;
import com.bergerkiller.mountiplex.reflection.util.fast.ConstantReturningInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;

/**
 * BlockState conversion used on MC 1.13 and after
 */
public class BlockStateConversion_1_13 extends BlockStateConversion {
    private TileState input_state;
    private final World proxy_world;
    private final Object proxy_nms_world;
    private final Object proxy_nms_world_ticklist;
    private final Block proxy_block;
    private final Map<Material, NullInstantiator<BlockState>> blockStateInstantiators;
    private final Class<?> craftBlockEntityState_type;
    private final SafeField<?> craftBlockEntityState_snapshot_field;
    private final Invoker<Object> non_instrumented_invokable = (instance, args) -> {
        String name = instance.getClass().getSuperclass().getSimpleName();
        throw new UnsupportedOperationException("Method not instrumented by the " + name + " proxy");
    };

    public BlockStateConversion_1_13() throws Throwable {
        // Find CraftBlock class
        final Class<?> craftBlock_type = CommonUtil.getClass("org.bukkit.craftbukkit.block.CraftBlock");
        final java.lang.reflect.Field worldField = craftBlock_type.getDeclaredField("world");
        worldField.setAccessible(true);

        // Stores a mapping of CraftBlockState types we have already created, by Block Material
        this.blockStateInstantiators = new EnumMap<Material, NullInstantiator<BlockState>>(Material.class);

        // Find CraftBlockEntityState class; we need to fix up the 'world' property of the snapshot tile
        this.craftBlockEntityState_type = CommonUtil.getClass("org.bukkit.craftbukkit.block.CraftBlockEntityState");
        this.craftBlockEntityState_snapshot_field = SafeField.create(this.craftBlockEntityState_type,
                "snapshot", TileEntityHandle.T.getType());

        // NMS World Proxy has a 'TickListServer' object that is requested by TileEntityCommand
        proxy_nms_world_ticklist = new ClassInterceptor() {
            @Override
            protected Invoker<?> getCallback(Method method) {
                if (method.getReturnType().equals(boolean.class)) {
                    // Boolean return value expected, so return false always
                    return (instance, args) -> Boolean.FALSE;
                } else if (method.getReturnType().equals(void.class)) {
                    // Void method, do nothing
                    return (instance, args) -> null;
                } else {
                    // All other method calls fail
                    return non_instrumented_invokable;
                }
            }
        }.createInstance(CommonUtil.getClass("net.minecraft.world.ticks.TickListServer"));

        // Create a NMS World proxy for handling various calls done from tile entities / block states
        proxy_nms_world = new WorldServerHook(this).createInstance(WorldServerHandle.T.getType());

        // Create a CraftWorld proxy that only supports the following calls:
        // - getTileEntityAt(x, y, z) to return our requested entity
        // All other methods will fail.
        proxy_world = (World) new ClassInterceptor() {
            @Override
            protected Invoker<?> getCallback(Method method) {
                // Gets our requested tile entity
                if (method.getName().equals("getTileEntityAt")) {
                    return (instance, args) -> input_state.tileEntity;
                }

                // Get the NMS World proxy
                if (method.getName().equals("getHandle")) {
                    return ConstantReturningInvoker.of(proxy_nms_world);
                }

                // All other method calls fail
                return non_instrumented_invokable;
            }
        }.createInstance(CraftWorldHandle.T.getType());

        // Create a block proxy that that only supports the following calls:
        // - CraftBlock:getTypeId()/getType()/getX()/getY()/getZ() - used in constructor
        // - CraftBlock:getWorld() - to instrument getTileEntityAt
        // All other methods will fail.
        proxy_block = (Block) new ClassInterceptor() {
            @Override
            protected Invoker<?> getCallback(Method method) {
                String name = method.getName();
                if (name.equals("getWorld")) {
                    return ConstantReturningInvoker.of(proxy_world);
                } else if (name.equals("getChunk")) {
                    return (instance, args) -> input_state.chunk;
                } else if (name.equals("getType")) {
                    return (instance, args) -> input_state.blockData.getType();
                } else if (name.equals("getData")) {
                    return (instance, args) -> input_state.blockData.getRawData();
                } else if (name.equals("getLightLevel")) {
                    return (instance, args) -> input_state.block.getLightLevel();
                } else if (name.equals("getX")) {
                    return (instance, args) -> input_state.block.getX();
                } else if (name.equals("getY")) {
                    return (instance, args) -> input_state.block.getY();
                } else if (name.equals("getZ")) {
                    return (instance, args) -> input_state.block.getZ();
                } else if (name.equals("getPosition")) {
                    return (instance, args) -> CraftBlockHandle.getBlockPosition(input_state.block);
                } else if (name.equals("getNMS")) {
                    return (instance, args) -> input_state.blockData.getData();
                } else if (name.equals("getNMSBlock")) {
                    return (instance, args) -> input_state.blockData.getBlockRaw();
                } else if (name.equals("getState")) {
                    return null; // allow the default implementation to be called
                } else if (name.equals("getHandle")) {
                    return (instance, args) -> proxy_nms_world;
                } else if (name.equals("toString")) {
                    // This does a bunch of internal lookups we really do not want to see happen.
                    return (instance, args) -> {
                        // return "CraftBlock{pos=" + position + ",type=" + getType() + ",data=" + getNMS() + ",fluid=" + world.getFluid(position) + '}';
                        StringBuilder str = new StringBuilder();
                        str.append("CraftBlock{pos=");
                        str.append("BlockPosition{x=").append(input_state.block.getX());
                        str.append(",y=").append(input_state.block.getY());
                        str.append(",z=").append(input_state.block.getY()).append('}');
                        str.append(",type=").append(input_state.blockData.getType());
                        str.append(",data=").append(input_state.blockData.toString());
                        str.append('}');
                        return str.toString();
                    };
                } else if (name.equals("getState0")) {
                    return null; // Paperspigot: uses this method to abstract out state snapshotting
                }

                // All other method calls fail
                return non_instrumented_invokable;
            }
        }.createInstance(craftBlock_type);
        worldField.set(proxy_block, proxy_nms_world);
    }

    private static String remapWorldServerMethodName(String methodName, String... parameterTypeNames) {
        Class<?>[] parameterTypes = new Class<?>[parameterTypeNames.length];
        for (int i = 0 ; i < parameterTypes.length; i++) {
            parameterTypes[i] = Resolver.loadClass(parameterTypeNames[i], false);
            if (parameterTypes[i] == null) {
                throw new IllegalStateException("Failed to find parameter[" + i + "] " +
                        parameterTypeNames[i] + " of method " + methodName);
            }
        }
        return Resolver.resolveMethodName(WorldServerHandle.T.getType(), methodName, parameterTypes);
    }

    @Override
    public Object blockStateToTileEntity(BlockState state) {
        BlockStateCache cache = BlockStateCache.get(state.getClass());
        Object nmsTileEntity = null;
        if (cache.tileEntityField != null) {
            nmsTileEntity = cache.tileEntityField.get(state);
        }
        if (nmsTileEntity == null) {
            nmsTileEntity = getTileEntityFromWorld(state.getBlock());
        }
        return nmsTileEntity;
    }

    @Override
    public BlockState blockToBlockState(Block block) {
        if (!CommonUtil.isMainThread()) {
            throw new IllegalStateException("Asynchronous access is not permitted");
        }

        // Most reliable
        // return block.getState();

        // Eliminates the 'snapshotting' overhead of BlockState information
        Object tileEntity = getTileEntityFromWorld(block);
        if (tileEntity != null) {
            return tileEntityToBlockState(null, block, tileEntity);
        } else {
            return block.getState();
        }
    }

    @Override
    public BlockState tileEntityToBlockState(org.bukkit.Chunk chunk, Object nmsTileEntity) {
        if (nmsTileEntity == null) {
            throw new IllegalArgumentException("Tile Entity is null");
        }
        return tileEntityToBlockState(chunk, CraftBlockHandle.createBlockAtTileEntity(nmsTileEntity), nmsTileEntity);
    }

    public synchronized BlockState tileEntityToBlockState(org.bukkit.Chunk chunk, Block block, Object nmsTileEntity) {
        // If chunk is null, retrieve it!
        if (chunk == null) {
            chunk = block.getChunk();
        }

        // Obtain BlockData from Tile Entity if cached, otherwise from the chunk
        BlockData blockData = TileEntityHandle.T.getBlockDataIfCached.invoke(nmsTileEntity);
        if (blockData == null) {
            blockData = ChunkUtil.getBlockData(chunk, block.getX(), block.getY(), block.getZ());
        }

        // If cached, create the BlockState by null-instantiating it and calling load() on it ourselves
        // This prevents creating a snapshot copy of the Tile Entity state
        NullInstantiator<BlockState> state_instantiator = this.blockStateInstantiators.get(blockData.getType());
        if (state_instantiator != null) {
            BlockState result = state_instantiator.create();

            // Initialize the fields in BlockState
            // public void init(org.bukkit.block.Block block, org.bukkit.Chunk chunk, IBlockData blockData, TileEntity tileEntity) 
            CraftBlockStateHandle.T.init.invoke(result, block, chunk, blockData.getData(), nmsTileEntity);

            return result;
        }

        // Store and restore old state in case of recursive calls to this function
        // This could happen if inside BlockState construction a chunk is loaded anyway
        // Would be bad, but its best to assume the worst
        TileState old_state = input_state;
        try {
            input_state = new TileState(chunk, block, nmsTileEntity, blockData);
            World world = block.getWorld();
            BlockState result = proxy_block.getState();

            // Internal BlockState needs to have all proxy field instances replaced with what it should be
            BlockStateCache cache = BlockStateCache.get(result.getClass());
            for (SafeField<World> worldField : cache.worldFields) {
                worldField.set(result, world);
            }
            for (SafeField<Chunk> chunkField : cache.chunkFields) {
                chunkField.set(result, chunk);
            }

            // Correct the snapshot field for BlockEntityState Block States
            if (this.craftBlockEntityState_type.isAssignableFrom(result.getClass())) {
                Object snapshotTile = this.craftBlockEntityState_snapshot_field.get(result);
                if (snapshotTile != null) {
                    TileEntityHandle.T.world_field.set(snapshotTile, world);
                }
            }

            // Cache type instantiator for next time
            this.blockStateInstantiators.put(blockData.getType(), NullInstantiator.of(result.getClass()));

            // All done!
            return result;
        } catch (Throwable t) {
            Logging.LOGGER_CONVERSION.once(Level.SEVERE, "Failed to convert " +
                    nmsTileEntity.getClass().getName() + " to CraftBlockState", t);
            return CraftBlockStateHandle.createNew(input_state.block);
        } finally {
            input_state = old_state;
        }
    }

    public Object getTileEntityFromWorld(Block block) {
        return getTileEntityFromWorld(block.getWorld(), HandleConversion.toBlockPositionHandle(block));
    }

    public Object getTileEntityFromWorld(World world, Object blockPosition) {
        return WorldHandle.T.getTileEntity.raw.invoke(HandleConversion.toWorldHandle(world), blockPosition);
    }

    private static final class TileState {
        public final Block block;
        public final Chunk chunk;
        public final Object tileEntity;
        public final BlockData blockData;

        public TileState(Chunk chunk, Block block, Object nmsTileEntity, BlockData blockData) {
            this.block = block;
            this.chunk = (chunk == null) ? block.getChunk() : chunk;
            this.tileEntity = nmsTileEntity;
            this.blockData = blockData;
        }
    }

    // caches the CraftWorld/CraftChunk fields stored inside the BlockState for later re-swapping
    private static final class BlockStateCache {
        private static final HashMap<Class<?>, BlockStateCache> cache = new HashMap<Class<?>, BlockStateCache>();
        public final SafeField<Chunk> chunkFields[];
        public final SafeField<World> worldFields[];
        public final SafeField<Object> tileEntityField;

        @SuppressWarnings("unchecked")
        private BlockStateCache(Class<?> type) {
            ClassTemplate<?> template = ClassTemplate.create(type);
            ArrayList<SafeField<?>> tmpChunkFields = new ArrayList<SafeField<?>>();
            ArrayList<SafeField<?>> tmpWorldFields = new ArrayList<SafeField<?>>();
            SafeField<?> tmpTileEntityField = null;
            for (SafeField<?> f : template.getFields()) {
                if (Chunk.class.isAssignableFrom(f.getType())) {
                    tmpChunkFields.add(f);
                } else if (World.class.isAssignableFrom(f.getType())) {
                    tmpWorldFields.add(f);
                } else if (TileEntityHandle.T.isAssignableFrom(f.getType())) {
                    tmpTileEntityField = f;
                }
            }
            chunkFields = LogicUtil.toArray(tmpChunkFields, SafeField.class);
            worldFields = LogicUtil.toArray(tmpWorldFields, SafeField.class);
            tileEntityField = (SafeField<Object>) tmpTileEntityField;
        }

        public static BlockStateCache get(Class<?> type) {
            BlockStateCache result = cache.get(type);
            if (result == null) {
                result = new BlockStateCache(type);
                cache.put(type, result);
            }
            return result;
        }
    }

    @ClassHook.HookPackage("net.minecraft.world.level")
    @ClassHook.HookImport("net.minecraft.core.BlockPosition")
    @ClassHook.HookImport("net.minecraft.server.MinecraftServer")
    @ClassHook.HookImport("net.minecraft.world.level.block.Block")
    @ClassHook.HookImport("net.minecraft.world.level.block.entity.TileEntity")
    @ClassHook.HookImport("net.minecraft.world.level.block.state.IBlockData")
    @ClassHook.HookImport("net.minecraft.world.level.chunk.Chunk")
    @ClassHook.HookLoadVariables("com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
    public static class WorldServerHook extends ClassHook<WorldServerHook> {
        private final BlockStateConversion_1_13 conversion;

        public WorldServerHook(BlockStateConversion_1_13 conversion) {
            this.conversion = conversion;
        }

        @Override
        protected Invoker<?> getCallback(Class<?> declaringClass, Method method) {
            Invoker<?> callback = super.getCallback(declaringClass, method);
            if (callback != null) {
                return callback;
            }

            // Fluid and Block Tick list
            Class<?>[] params = method.getParameterTypes();
            if (params.length == 0 && CommonUtil.getClass("net.minecraft.world.ticks.TickList").isAssignableFrom(method.getReturnType())) {
                return ConstantReturningInvoker.of(this.conversion.proxy_nms_world_ticklist);
            }

            // All other method calls fail
            return this.conversion.non_instrumented_invokable;
        }

        @HookMethod("public TileEntity getTileEntity:???(BlockPosition blockPosition)")
        public Object getTileEntity(Object blockPosition) {
            return this.conversion.input_state.tileEntity;
        }

        @HookMethod("public IBlockData getBlockData:???(BlockPosition blockposition)")
        public Object getBlockData(Object blockPosition) {
            return this.conversion.input_state.blockData.getData();
        }

        @HookMethod("public MinecraftServer getMinecraftServer:???()")
        public Object getMinecraftServer() {
            return MinecraftServerHandle.instance().getRaw();
        }

        // Note there is also a setBlockData variant with two ints (default of second int is 512)
        // Since a later version of Minecraft, this method calls that one. For this reason on Paper,
        // this method is final. As such we've made the method optional so it won't warn.
        @HookMethod(value="public boolean setBlockData:???(BlockPosition blockposition, IBlockData iblockdata, int updateFlags)", optional=true)
        public boolean setBlockData(Object blockPosition, Object iblockdata, int updateFlags) {
            return true;
        }

        // This method was added in Minecraft 1.16. On Paper the normal setBlockData method
        // is made final and calls this method. To override properly, we must override this method
        // as well.
        @HookMethodCondition("version >= 1.16 && version <= 1.17.1")
        @HookMethod("public boolean a(BlockPosition blockposition, IBlockData iblockdata, int i, int j)")
        public boolean setBlockData(Object blockPosition, Object iblockdata, int updateFlags, int otherFlags) {
            return true;
        }

        @HookMethodCondition("version >= 1.18")
        @HookMethod("public abstract void sendBlockUpdated(BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, int i)")
        public void sendBlockUpdated(Object blockposition, Object iblockdata, Object iblockdata1, int i) {
            // No-op
        }

        @HookMethodCondition("version >= 1.18")
        @HookMethod("public void notifyAndUpdatePhysics(BlockPosition blockposition, Chunk chunk, IBlockData oldBlock, IBlockData newBlock, IBlockData actualBlock, int i, int j)")
        public void notifyAndUpdatePhysics(Object blockposition, Object chunk, Object oldBlock, Object newBlock, Object actualBlock, int i, int j) {
            // No-op
        }

        @HookMethodCondition("version >= 1.18")
        @HookMethod("public void updateNeighbourForOutputSignal(BlockPosition blockposition, Block block)")
        public void updateNeighbourForOutputSignal(Object blockposition, Object block) {
            // No-op
        }

        @HookMethodCondition("version <= 1.17.1")
        @HookMethod("public void updateAdjacentComparators(BlockPosition blockposition, Block block)")
        public void updateAdjacentComparators(Object blockposition, Object block) {
            // No-op
        }

        @HookMethodCondition("version <= 1.17.1")
        @HookMethod("public void notify(BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, int i)")
        public void notify(Object blockposition, Object iblockdata, Object iblockdata1, int i) {
            // No-op
        }

        @HookMethodCondition("version >= 1.18")
        @HookMethod("public void onBlockStateChange(BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1)")
        public void onBlockStateChange(Object blockposition, Object iblockdata, Object iblockdata1) {
            // No-op
        }

        // Same as onBlockStateChange, but obfuscated method name
        @HookMethodCondition("version <= 1.17.1")
        @HookMethod("public void b(BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1)")
        public void onBlockStateChangeObf(Object blockposition, Object iblockdata, Object iblockdata1) {
            // No-op
        }
    }
}
