package com.bergerkiller.bukkit.common.conversion.blockstate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.core.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.TileEntityHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftChunkHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftWorldHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockStateHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.util.fast.ConstantReturningInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import com.bergerkiller.mountiplex.reflection.util.fast.NullInvoker;

/**
 * BlockState conversion used on MC 1.12.2 and before
 */
public class BlockStateConversion_1_12_2 extends BlockStateConversion {
    private TileState input_state;
    private final Object proxy_nms_world;
    private final World proxy_world;
    private final Chunk proxy_chunk;
    private final Block proxy_block;
    private static final Invoker<Object> non_instrumented_invokable = (instance, args) -> {
        String name = instance.getClass().getSuperclass().getSimpleName();
        throw new UnsupportedOperationException("Method not instrumented by the " + name + " proxy");
    };

    public BlockStateConversion_1_12_2() throws Throwable {
        // Find CraftBlock class
        final Class<?> craftBlock_type = CommonUtil.getClass("org.bukkit.craftbukkit.block.CraftBlock");
        final java.lang.reflect.Field chunkField = craftBlock_type.getDeclaredField("chunk");
        chunkField.setAccessible(true);

        // Create a CraftChunk proxy that only supports the following calls:
        // - getCraftWorld() -> returns the proxy world (final fallback in getState() requires this)
        proxy_chunk = (Chunk) new ClassInterceptor() {
            @Override
            protected Invoker<?> getCallback(Method method) {
                // Gets the proxy world
                if (method.getName().equals("getCraftWorld")) {
                    return (instance, args) -> proxy_world;
                }

                // All other method calls fail
                return non_instrumented_invokable;
            }
        }.createInstance(CraftChunkHandle.T.getType());

        // Only appears to be used on forge servers, standard Spigot never calls getHandle() in CraftWorld
        proxy_nms_world = new NMSWorldHook().createInstance(WorldServerHandle.T.getType());

        // Create a CraftWorld proxy that only supports the following calls:
        // - getTileEntityAt(x, y, z) to return our requested entity
        // All other methods will fail.
        proxy_world = (World) new ClassInterceptor() {
            final Class<?> tileEntityType = CommonUtil.getClass("net.minecraft.world.level.block.entity.TileEntity");

            @Override
            protected Invoker<?> getCallback(Method method) {
                // Gets our requested tile entity
                if (method.getReturnType().equals(tileEntityType)) {
                    return (instance, args) -> input_state.tileEntity;
                }

                // Gets the Handle (World/WorldServer)
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
            @SuppressWarnings("deprecation")
            @Override
            protected Invoker<?> getCallback(Method method) {
                String name = method.getName();
                if (name.equals("getWorld")) {
                    return new NullInvoker<World>(proxy_world);
                } else if (name.equals("getChunk")) {
                    return (instance, args) -> input_state.block.getChunk();
                } else if (name.equals("getType")) {
                    return (instance, args) -> input_state.blockData.getType();
                } else if (name.equals("getTypeId")) {
                    return (instance, args) -> CommonLegacyMaterials.getIdFromMaterial(input_state.blockData.getType());
                } else if (name.equals("getData")) {
                    return (instance, args) -> (byte) input_state.blockData.getRawData();
                } else if (name.equals("getLightLevel")) {
                    return (instance, args) -> input_state.block.getLightLevel();
                } else if (name.equals("getX")) {
                    return (instance, args) -> input_state.block.getX();
                } else if (name.equals("getY")) {
                    return (instance, args) -> input_state.block.getY();
                } else if (name.equals("getZ")) {
                    return (instance, args) -> input_state.block.getZ();
                } else if (name.equals("getState")) {
                    return null; // allow the default implementation to be called
                } else if (name.equals("getState0")) {
                    return null; // Paperspigot: uses this method to abstract out state snapshotting
                }

                // All other method calls fail
                return non_instrumented_invokable;
            }
        }.createInstance(craftBlock_type);
        chunkField.set(proxy_block, proxy_chunk);
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
        return block.getState();

        // I'm not sure why we ever want to do this, because it introduces a nasty overhead for no reason!
        // Restore this part if there really was a reason to have this
        /*
        Object tileEntity = getTileEntityFromWorld(block);
        if (tileEntity != null) {
            return tileEntityToBlockState(block, tileEntity);
        } else {
            return CraftBlockStateHandle.createNew(block);
        }
        */
    }

    @Override
    public synchronized BlockState tileEntityToBlockState(org.bukkit.Chunk chunk, Object nmsTileEntity) {
        if (nmsTileEntity == null) {
            throw new IllegalArgumentException("Tile Entity is null");
        }

        BlockPositionHandle pos = TileEntityHandle.T.getPosition.invoke(nmsTileEntity);

        Block block;
        if (chunk == null) {
            Object world = TileEntityHandle.T.getWorld.raw.invoke(nmsTileEntity);
            if (world == null) {
                throw new IllegalArgumentException("Tile Entity has no world set");
            }
            block = WrapperConversion.toWorld(world).getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        } else {
            block = chunk.getBlock(pos.getX(), pos.getY(), pos.getZ());
        }
        return tileEntityToBlockState(block, nmsTileEntity);
    }

    public synchronized BlockState tileEntityToBlockState(Block block, Object nmsTileEntity) {
        // Store and restore old state in case of recursive calls to this function
        // This could happen if inside BlockState construction a chunk is loaded anyway
        // Would be bad, but its best to assume the worst
        TileState old_state = input_state;
        try {
            input_state = new TileState(block, nmsTileEntity);
            BlockState result = proxy_block.getState();

            // Internal BlockState needs to have all proxy field instances replaced with what it should be
            BlockStateCache cache = BlockStateCache.get(result.getClass());
            for (FastField<World> worldField : cache.worldFields) {
                worldField.set(result, input_state.block.getWorld());
            }
            for (FastField<Chunk> chunkField : cache.chunkFields) {
                chunkField.set(result, input_state.block.getChunk());
            }

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

    @Override
    public Object getTileEntityFromWorld(Block block) {
        return getTileEntityFromWorld(block.getWorld(), HandleConversion.toBlockPositionHandle(block));
    }

    public Object getTileEntityFromWorld(World world, Object blockPosition) {
        return WorldHandle.T.getTileEntity.raw.invoke(HandleConversion.toWorldHandle(world), blockPosition);
    }

    private static final class TileState {
        public final Object tileEntity;
        public final BlockData blockData;
        public final Block block;

        public TileState(Block block, Object nmsTileEntity) {
            this.block = block;
            this.tileEntity = nmsTileEntity;
            this.blockData = TileEntityHandle.T.getBlockData.invoke(nmsTileEntity);
        }
    }

    @ClassHook.HookPackage("net.minecraft.server")
    public class NMSWorldHook extends ClassHook<NMSWorldHook> {
        @Override
        protected Invoker<?> getCallback(Class<?> hookedType, Method method) {
            Invoker<?> callback = super.getCallback(hookedType, method);
            return (callback != null) ? callback : non_instrumented_invokable;
        }

        @HookMethod("public TileEntity getTileEntity(BlockPosition blockposition)")
        public Object getTileEntity(Object blockPosition) {
            return input_state.tileEntity;
        }

        @HookMethod("public MinecraftServer getMinecraftServer()")
        public Object getMinecraftServer() {
            return MinecraftServerHandle.instance().getRaw();
        }

        @HookMethod("public IBlockData getType(BlockPosition blockposition)")
        public Object getType(Object blockPosition) {
            return input_state.blockData.getData();
        }

        @HookMethod("public boolean setTypeAndData(BlockPosition blockposition, IBlockData iblockdata, int i)")
        public boolean setTypeAndData(Object blockPosition, Object iblockdata, int i) {
            return true;
        }
    }

    // caches the CraftWorld/CraftChunk fields stored inside the BlockState for later re-swapping
    private static final class BlockStateCache {
        private static final HashMap<Class<?>, BlockStateCache> cache = new HashMap<Class<?>, BlockStateCache>();
        public final FastField<Chunk> chunkFields[];
        public final FastField<World> worldFields[];
        public final FastField<Object> tileEntityField;

        @SuppressWarnings("unchecked")
        private BlockStateCache(Class<?> type) {
            List<Field> allFields = ReflectionUtil.getAllNonStaticFields(type)
                    .collect(Collectors.toList());

            chunkFields = allFields.stream()
                    .filter(f -> Chunk.class.isAssignableFrom(f.getType()))
                    .map(FastField::new)
                    .toArray(FastField[]::new);

            worldFields = allFields.stream()
                    .filter(f -> World.class.isAssignableFrom(f.getType()))
                    .map(FastField::new)
                    .toArray(FastField[]::new);

            tileEntityField = allFields.stream()
                    .filter(f -> TileEntityHandle.T.isAssignableFrom(f.getType()))
                    .reduce((first, second) -> second) // Select last
                    .map(FastField::new)
                    .orElse(null);
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
}
