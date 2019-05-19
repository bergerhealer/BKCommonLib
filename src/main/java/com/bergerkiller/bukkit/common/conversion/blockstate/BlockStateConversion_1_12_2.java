package com.bergerkiller.bukkit.common.conversion.blockstate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.server.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.TileEntityHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftChunkHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftWorldHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockStateHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.Invokable;
import com.bergerkiller.mountiplex.reflection.SafeField;

/**
 * BlockState conversion used on MC 1.12.2 and before
 */
public class BlockStateConversion_1_12_2 extends BlockStateConversion {
    private TileState input_state;
    private final World proxy_world;
    private final Chunk proxy_chunk;
    private final Block proxy_block;
    private final Invokable non_instrumented_invokable = new Invokable() {
        @Override
        public Object invoke(Object instance, Object... args) {
            String name = instance.getClass().getSuperclass().getSimpleName();
            throw new UnsupportedOperationException("Method not instrumented by the " + name + " proxy");
        }
    };

    public BlockStateConversion_1_12_2() throws Throwable {
        // Find CraftBlock class
        final Class<?> craftBlock_type = CommonUtil.getCBClass("block.CraftBlock");
        final java.lang.reflect.Field chunkField = craftBlock_type.getDeclaredField("chunk");
        chunkField.setAccessible(true);

        // Create a CraftChunk proxy that only supports the following calls:
        // - getCraftWorld() -> returns the proxy world (final fallback in getState() requires this)
        proxy_chunk = (Chunk) new ClassInterceptor() {
            @Override
            protected Invokable getCallback(Method method) {
                // Gets the proxy world
                if (method.getName().equals("getCraftWorld")) {
                    return new Invokable() {
                        @Override
                        public Object invoke(Object instance, Object... args) {
                            return proxy_world;
                        }
                    };
                }

                // All other method calls fail
                return non_instrumented_invokable;
            }
        }.createInstance(CraftChunkHandle.T.getType());

        // Create a CraftWorld proxy that only supports the following calls:
        // - getTileEntityAt(x, y, z) to return our requested entity
        // All other methods will fail.
        proxy_world = (World) new ClassInterceptor() {
            @Override
            protected Invokable getCallback(Method method) {
                // Gets our requested tile entity
                if (method.getName().equals("getTileEntityAt")) {
                    return new Invokable() {
                        @Override
                        public Object invoke(Object instance, Object... args) {
                            return input_state.tileEntity;
                        }
                    };
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
            protected Invokable getCallback(Method method) {
                String name = method.getName();
                if (name.equals("getWorld")) {
                    return new NullInvokable(proxy_world);
                } else if (name.equals("getChunk")) {
                    return new Invokable() {
                        @Override
                        public Object invoke(Object instance, Object... args) {
                            return input_state.block.getChunk();
                        }
                    };
                } else if (name.equals("getType")) {
                    return new Invokable() {
                        @Override
                        public Object invoke(Object instance, Object... args) {
                            return input_state.type;
                        }
                    };
                } else if (name.equals("getTypeId")) {
                    return new Invokable() {
                        @Override
                        @SuppressWarnings("deprecation")
                        public Object invoke(Object instance, Object... args) {
                            return CommonLegacyMaterials.getIdFromMaterial(input_state.type);
                        }
                    };
                } else if (name.equals("getData")) {
                    return new Invokable() {
                        @Override
                        public Object invoke(Object instance, Object... args) {
                            return input_state.rawData;
                        }
                    };
                } else if (name.equals("getLightLevel")) {
                    return new Invokable() {
                        @Override
                        public Object invoke(Object instance, Object... args) {
                            return input_state.block.getLightLevel();
                        }
                    };
                } else if (name.equals("getX")) {
                    return new Invokable() {
                        @Override
                        public Object invoke(Object instance, Object... args) {
                            return input_state.block.getX();
                        }
                    };
                } else if (name.equals("getY")) {
                    return new Invokable() {
                        @Override
                        public Object invoke(Object instance, Object... args) {
                            return input_state.block.getY();
                        }
                    };
                } else if (name.equals("getZ")) {
                    return new Invokable() {
                        @Override
                        public Object invoke(Object instance, Object... args) {
                            return input_state.block.getZ();
                        }
                    };
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
    public BlockState tileEntityToBlockState(org.bukkit.Chunk chunk, Object nmsTileEntity) {
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

    public BlockState tileEntityToBlockState(Block block, Object nmsTileEntity) {
        // Store and restore old state in case of recursive calls to this function
        // This could happen if inside BlockState construction a chunk is loaded anyway
        // Would be bad, but its best to assume the worst
        TileState old_state = input_state;
        try {
            input_state = new TileState(block, nmsTileEntity);
            BlockState result = proxy_block.getState();

            // Internal BlockState needs to have all proxy field instances replaced with what it should be
            BlockStateCache cache = BlockStateCache.get(result.getClass());
            for (SafeField<World> worldField : cache.worldFields) {
                worldField.set(result, input_state.block.getWorld());
            }
            for (SafeField<Chunk> chunkField : cache.chunkFields) {
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

    public Object getTileEntityFromWorld(Block block) {
        return getTileEntityFromWorld(block.getWorld(), HandleConversion.toBlockPositionHandle(block));
    }

    public Object getTileEntityFromWorld(World world, Object blockPosition) {
        return WorldHandle.T.getTileEntity.raw.invoke(HandleConversion.toWorldHandle(world), blockPosition);
    }

    private static final class TileState {
        public final Object tileEntity;
        public final byte rawData;
        public final Material type;
        public final Block block;

        public TileState(Block block, Object nmsTileEntity) {
            this.block = block;
            this.tileEntity = nmsTileEntity;
            this.type = TileEntityHandle.T.getType.invoke(nmsTileEntity);
            this.rawData = (byte) (TileEntityHandle.T.getLegacyData.invoke(nmsTileEntity) & 0xF);
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
}
