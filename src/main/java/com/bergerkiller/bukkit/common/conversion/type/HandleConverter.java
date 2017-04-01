package com.bergerkiller.bukkit.common.conversion.type;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

import com.bergerkiller.bukkit.common._unused.EntityProxy_unused;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.BasicConverter;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.proxies.InventoryProxy;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.ScoreboardAction;
import com.bergerkiller.bukkit.common.wrappers.UseAction;
import com.bergerkiller.reflection.net.minecraft.server.NMSEnumGamemode;
import com.bergerkiller.reflection.net.minecraft.server.NMSItemStack;
import com.bergerkiller.reflection.net.minecraft.server.NMSNBT;
import com.bergerkiller.reflection.net.minecraft.server.NMSTileEntity;
import com.bergerkiller.reflection.net.minecraft.server.NMSVector;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorldType;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftBlockState;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftItemStack;

import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EnumDifficulty;
import net.minecraft.server.v1_11_R1.EnumHand;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_11_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_11_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.MainHand;
import org.bukkit.util.Vector;

/**
 * Converter for converting to internal handles (from wrapper classes)<br>
 * <b>Do not reference external state-classes while constructing (e.g.
 * reflection classes)</b>
 */
public abstract class HandleConverter extends BasicConverter<Object> {

    public static final HandleConverter toEntityHandle = new HandleConverter("Entity") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            if (value instanceof EntityProxy_unused) {
                value = ((EntityProxy_unused<?>) value).getProxyBase();
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
                return CBCraftItemStack.handle.get(value);
            } else if (value instanceof org.bukkit.inventory.ItemStack) {
                org.bukkit.inventory.ItemStack stack = (org.bukkit.inventory.ItemStack) value;
                Object rval = Bukkit.getServer() != null ? CraftItemStack.asNMSCopy(stack) : null;
                if (rval == null) {
                    rval = NMSItemStack.newInstance(stack.getType(), MaterialUtil.getRawData(stack), stack.getAmount());
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
                return CBCraftBlockState.toTileEntity((BlockState) value);
            }
            org.bukkit.block.Block block = Conversion.toBlock.convert(value);
            if (block != null) {
                Object tile = NMSTileEntity.getFromWorld(block);
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
                    return NMSNBT.createHandle(value);
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
                return NMSEnumGamemode.getFromId.invoke(null, gameMode.getValue());
            }
            return def;
        }
    };
    public static final HandleConverter toMainHandHandle = new HandleConverter("EnumHand") {
		@Override
		protected Object convertSpecial(Object value, Class<?> valueType, Object def) {
			if (value instanceof MainHand) {
		        switch((MainHand) value) {
	            case LEFT:
	                return EnumHand.OFF_HAND;
	            case RIGHT:
	                return EnumHand.MAIN_HAND;
		        }
			}
			return def;
		}
    };
    public static final HandleConverter toWorldTypeHandle = new HandleConverter("WorldType") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            org.bukkit.WorldType type = Conversion.toWorldType.convert(value);
            if (type != null) {
                return NMSWorldType.getType.invoke(null, type.getName());
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
                return NMSVector.newPair(iv2.x, iv2.z);
            } else {
                return def;
            }
        }
    };
    public static final HandleConverter toBlockPositionHandle = new HandleConverter("BlockPosition") {
        @Override
        public Object convertSpecial(Object value, Class<?> valueType, Object def) {
            if (value instanceof IntVector3) {
                IntVector3 iv3 = (IntVector3) value;
                return NMSVector.newPosition(iv3.x, iv3.y, iv3.z);
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
                return NMSVector.newVec(vec.getX(), vec.getY(), vec.getZ());
            } else {
                return def;
            }
        }
    };
    public static final HandleConverter toPlayerAbilitiesHandle = new WrapperHandleConverter("PlayerAbilities");
    public static final HandleConverter toEntityTrackerHandle = new WrapperHandleConverter("EntityTracker");
    public static final HandleConverter toLongHashMapHandle = new WrapperHandleConverter(Long2ObjectMap.class);
    public static final HandleConverter toLongHashSetHandle = new WrapperHandleConverter(CommonUtil.getCBClass("util.LongHashSet"));
    public static final HandleConverter toIntHashMapHandle = new WrapperHandleConverter("IntHashMap");
    public static final HandleConverter toUseActionHandle = new WrapperHandleConverter("EnumEntityUseAction");
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

    public HandleConverter(String outputTypeName) {
        this(CommonUtil.getNMSClass(outputTypeName));
    }

    @SuppressWarnings("unchecked")
    public HandleConverter(Class<?> outputType) {
        super((Class<Object>) outputType);
    }
}
