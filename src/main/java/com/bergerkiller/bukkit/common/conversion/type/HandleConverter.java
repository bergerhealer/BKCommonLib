package com.bergerkiller.bukkit.common.conversion.type;

import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.EnumDifficulty;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R1.util.CraftMagicNumbers;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.BasicConverter;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.proxies.EntityProxy;
import com.bergerkiller.bukkit.common.proxies.InventoryProxy;
import com.bergerkiller.bukkit.common.reflection.classes.BlockStateRef;
import com.bergerkiller.bukkit.common.reflection.classes.CraftItemStackRef;
import com.bergerkiller.bukkit.common.reflection.classes.EnumGamemodeRef;
import com.bergerkiller.bukkit.common.reflection.classes.ItemStackRef;
import com.bergerkiller.bukkit.common.reflection.classes.TileEntityRef;
import com.bergerkiller.bukkit.common.reflection.classes.VectorRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldTypeRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.NBTUtil;
import com.bergerkiller.bukkit.common.wrappers.ScoreboardAction;
import com.bergerkiller.bukkit.common.wrappers.UseAction;

/**
 * Converter for converting to internal handles (from wrapper classes)<br>
 * <b>Do not reference external state-classes while constructing (e.g.
 * reflection classes)</b>
 */
public abstract class HandleConverter extends BasicConverter<Object> {

    public static final HandleConverter toEntityHandle = new HandleConverter("Entity") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            if (value instanceof EntityProxy) {
                value = ((EntityProxy<?>) value).getProxyBase();
            }
            if (value instanceof CraftEntity) {
                return ((CraftEntity) value).getHandle();
            } else {
                return def;
            }
        }

        @Override
        public boolean isCastingSupported() {
            return true;
        }
    };
    public static final HandleConverter toWorldHandle = new HandleConverter("World") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            org.bukkit.World world = WrapperConverter.toWorld.convert(value);
            if (world instanceof CraftWorld) {
                return ((CraftWorld) world).getHandle();
            } else {
                return def;
            }
        }

        @Override
        public boolean isCastingSupported() {
            return true;
        }
    };
    public static final HandleConverter toChunkHandle = new HandleConverter("Chunk") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            org.bukkit.Chunk chunk = WrapperConverter.toChunk.convert(value);
            if (chunk instanceof CraftChunk) {
                return ((CraftChunk) value).getHandle();
            } else {
                return def;
            }
        }
    };
    public static final HandleConverter toItemStackHandle = new HandleConverter("ItemStack") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            if (value instanceof CraftItemStack) {
                return CraftItemStackRef.handle.get(value);
            } else if (value instanceof org.bukkit.inventory.ItemStack) {
                org.bukkit.inventory.ItemStack stack = (org.bukkit.inventory.ItemStack) value;
                Object rval = CraftItemStack.asNMSCopy(stack);
                if (rval == null) {
                    rval = ItemStackRef.newInstance(stack.getType(), MaterialUtil.getRawData(stack), stack.getAmount());
                }
                return rval;
            } else {
                return def;
            }
        }
    };
    public static final HandleConverter toTileEntityHandle = new HandleConverter("TileEntity") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            if (value instanceof BlockState) {
                return BlockStateRef.toTileEntity((BlockState) value);
            }
            org.bukkit.block.Block block = Conversion.toBlock.convert(value);
            if (block != null) {
                Object tile = TileEntityRef.getFromWorld(block);
                if (tile != null) {
                    return tile;
                }
            }
            return def;
        }

        @Override
        public boolean isCastingSupported() {
            return true;
        }
    };
    public static final HandleConverter toInventoryHandle = new HandleConverter("IInventory") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            if (value instanceof InventoryProxy) {
                value = ((InventoryProxy) value).getProxyBase();
            }
            if (value instanceof CraftInventory) {
                return LogicUtil.fixNull(((CraftInventory) value).getInventory(), def);
            }
            return def;
        }
    };
    public static final HandleConverter toDataWatcherHandle = new HandleConverter("DataWatcher") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            if (value instanceof com.bergerkiller.bukkit.common.wrappers.DataWatcher) {
                return ((com.bergerkiller.bukkit.common.wrappers.DataWatcher) value).getHandle();
            } else if (value instanceof Entity) {
                return ((Entity) value).getDataWatcher();
            } else {
                return def;
            }
        }
    };
    public static final HandleConverter toNBTTagHandle = new HandleConverter("NBTBase") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            if (value instanceof CommonTag) {
                return ((CommonTag) value).getHandle();
            } else {
                try {
                    return NBTUtil.createHandle(value);
                } catch (Exception ex) {
                    return def;
                }
            }
        }

        @Override
        public boolean isCastingSupported() {
            return true;
        }
    };
    public static final HandleConverter toItemHandle = new HandleConverter("Item") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            org.bukkit.Material material = ConversionTypes.toMaterial.convert(value);
            if (material != null) {
                return CraftMagicNumbers.getItem(material);
            }
            return def;
        }

        @Override
        public boolean isCastingSupported() {
            return true;
        }
    };
    public static final HandleConverter toBlockHandle = new HandleConverter("Block") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            org.bukkit.Material material = ConversionTypes.toMaterial.convert(value);
            if (material != null) {
                return CraftMagicNumbers.getBlock(material);
            }
            return def;
        }

        @Override
        public boolean isCastingSupported() {
            return true;
        }
    };
    @SuppressWarnings("deprecation")
    public static final HandleConverter toGameModeHandle = new HandleConverter("EnumGamemode") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            GameMode gameMode = Conversion.toGameMode.convert(value);
            if (gameMode != null) {
                return EnumGamemodeRef.getFromId.invoke(null, gameMode.getValue());
            }
            return def;
        }
    };
    public static final HandleConverter toWorldTypeHandle = new HandleConverter("WorldType") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            org.bukkit.WorldType type = Conversion.toWorldType.convert(value);
            if (type != null) {
                return WorldTypeRef.getType.invoke(null, type.getName());
            } else {
                return def;
            }
        }
    };
    public static final HandleConverter toPacketHandle = new WrapperHandleConverter("Packet");
    public static final HandleConverter toChunkCoordIntPairHandle = new HandleConverter("ChunkCoordIntPair") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            if (value instanceof IntVector2) {
                IntVector2 iv2 = (IntVector2) value;
                return VectorRef.newPair(iv2.x, iv2.z);
            } else {
                return def;
            }
        }
    };
    public static final HandleConverter toChunkCoordinatesHandle = new HandleConverter("ChunkCoordinates") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            if (VectorRef.isPair(value)) {
                final int x = VectorRef.getPairX(value);
                final int z = VectorRef.getPairZ(value);
                return VectorRef.newPair(x, z);
            } else if (value instanceof IntVector3) {
                IntVector3 iv3 = (IntVector3) value;
                return VectorRef.newPair(iv3.x, iv3.z);
            } else {
                return def;
            }
        }
    };
    public static final HandleConverter toChunkPositionHandle = new HandleConverter("ChunkPosition") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            if (VectorRef.isPair(value)) {
                final int x = VectorRef.getPairX(value);
                final int z = VectorRef.getPairZ(value);
                return VectorRef.newPair(x, z);
            } else if (value instanceof IntVector3) {
                IntVector3 iv3 = (IntVector3) value;
                return new IntVector3(iv3.x, iv3.y, iv3.z);
            } else {
                return def;
            }
        }
    };
    public static final HandleConverter toVec3DHandle = new HandleConverter("Vec3D") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            Vector vec = WrapperConverter.toVector.convert(value);
            if (vec != null) {
                return VectorRef.newVec(vec.getX(), vec.getY(), vec.getZ());
            } else {
                return def;
            }
        }
    };
    public static final HandleConverter toPlayerAbilitiesHandle = new WrapperHandleConverter("PlayerAbilities");
    public static final HandleConverter toEntityTrackerHandle = new WrapperHandleConverter("EntityTracker");
    public static final HandleConverter toLongHashMapHandle = new WrapperHandleConverter("LongHashMap");
    public static final HandleConverter toLongHashSetHandle = new WrapperHandleConverter(CommonUtil.getCBClass("util.LongHashSet"));
    public static final HandleConverter toIntHashMapHandle = new WrapperHandleConverter("IntHashMap");
    public static final HandleConverter toUseActionHandle = new HandleConverter("EnumEntityUseAction") {
        @Override
        protected Object convertSpecial(Object value, Class<?> valueType, Object def) {
            if (value instanceof UseAction) {
                return ((UseAction) value).getHandle();
            } else {
                return def;
            }
        }
    };
    public static final HandleConverter toScoreboardActionHandle = new HandleConverter("EnumScoreboardAction") {
        @Override
        protected Object convertSpecial(Object value, Class<?> valueType, Object def) {
            if (value instanceof ScoreboardAction) {
                return ((UseAction) value).getHandle();
            } else {
                return def;
            }
        }
    };
    public static final HandleConverter toDifficultyHandle = new HandleConverter("EnumDifficulty") {
        @Override
        @SuppressWarnings("deprecation")
        protected Object convertSpecial(Object value, Class<?> valueType, Object def) {
            int id;
            if (value instanceof Number) {
                id = ((Number) value).intValue();
            } else if (value instanceof Difficulty) {
                id = ((Difficulty) value).getValue();
            } else {
                return def;
            }
            return EnumDifficulty.getById(id);
        }
    };

    public HandleConverter(String outputTypeName) {
        this(CommonUtil.getNMSClass(outputTypeName));
    }

    @SuppressWarnings("unchecked")
    public HandleConverter(Class<?> outputType) {
        super((Class<Object>) outputType);
    }
}
