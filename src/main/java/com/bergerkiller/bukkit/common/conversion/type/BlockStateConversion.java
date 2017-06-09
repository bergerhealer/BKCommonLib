package com.bergerkiller.bukkit.common.conversion.type;

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
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.server.TileEntityHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftWorldHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockStateHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.Invokable;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.reflection.net.minecraft.server.NMSTileEntity;

/**
 * Specialized utility class that deals with conversion from TileEntity to its respective
 * BlockState. This is done in such a way that no calls to the server state are made, guaranteeing
 * no internal modifications occur. This is required when querying TileEntities not yet added to a world.<br>
 * <br>
 * All of this is done by creating a virtual proxy environment in which to perform CraftBlockState construction.
 */
public class BlockStateConversion {
    private static TileState input_state;
    private static final World proxy_world;
    private static final Block proxy_block;
    private static final Invokable non_instrumented_invokable = new Invokable() {
        @Override
        public Object invoke(Object instance, Object... args) {
            throw new UnsupportedOperationException("Method not instrumented by the CraftWorld proxy");
        }
    };

    static {
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
                            return input_state.type.getId();
                        }
                    };
                } else if (name.equals("getData")) {
                    return new Invokable() {
                        @Override
                        public Object invoke(Object instance, Object... args) {
                            return input_state.rawData;
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
                }

                // All other method calls fail
                return non_instrumented_invokable;
            }
        }.createInstance(CraftBlockHandle.T.getType());
    }

    public static Object blockStateToTileEntity(BlockState state) {
        BlockStateCache cache = BlockStateCache.get(state.getClass());
        Object nmsTileEntity = null;
        if (cache.tileEntityField != null) {
            nmsTileEntity = cache.tileEntityField.get(state);
        }
        if (nmsTileEntity == null) {
            nmsTileEntity = NMSTileEntity.getFromWorld(state.getBlock());
        }
        return nmsTileEntity;
    }

    public static BlockState blockToBlockState(Block block) {
        Object tileEntity = NMSTileEntity.getFromWorld(block);
        if (tileEntity != null) {
            return tileEntityToBlockState(block, tileEntity);
        } else {
            return CraftBlockStateHandle.createNew(block);
        }
    }

    public static BlockState tileEntityToBlockState(Object nmsTileEntity) {
        if (nmsTileEntity == null || !NMSTileEntity.hasWorld(nmsTileEntity)) {
            throw new IllegalArgumentException("Tile Entity is null or has no world set");
        }
        return tileEntityToBlockState(NMSTileEntity.getBlock(nmsTileEntity), nmsTileEntity);
    }

    public static BlockState tileEntityToBlockState(Block block, Object nmsTileEntity) {
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

    private static final class TileState {
        public final Object tileEntity;
        public final byte rawData;
        public final Material type;
        public final Block block;

        public TileState(Block block, Object nmsTileEntity) {
            this.block = block;
            this.tileEntity = nmsTileEntity;
            this.type = TileEntityHandle.T.getType.invoke(nmsTileEntity);
            this.rawData = (byte) (TileEntityHandle.T.getRawData.invoke(nmsTileEntity) & 0xF);
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
