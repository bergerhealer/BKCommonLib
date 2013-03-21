package com.bergerkiller.bukkit.common.conversion.type;

import net.minecraft.server.v1_5_R2.Chunk;
import net.minecraft.server.v1_5_R2.ChunkCoordIntPair;
import net.minecraft.server.v1_5_R2.ChunkCoordinates;
import net.minecraft.server.v1_5_R2.ChunkPosition;
import net.minecraft.server.v1_5_R2.ContainerAnvilInventory;
import net.minecraft.server.v1_5_R2.ContainerEnchantTableInventory;
import net.minecraft.server.v1_5_R2.Entity;
import net.minecraft.server.v1_5_R2.IInventory;
import net.minecraft.server.v1_5_R2.InventoryCrafting;
import net.minecraft.server.v1_5_R2.InventoryMerchant;
import net.minecraft.server.v1_5_R2.ItemStack;
import net.minecraft.server.v1_5_R2.PlayerInventory;
import net.minecraft.server.v1_5_R2.TileEntity;
import net.minecraft.server.v1_5_R2.TileEntityBeacon;
import net.minecraft.server.v1_5_R2.TileEntityBrewingStand;
import net.minecraft.server.v1_5_R2.TileEntityFurnace;
import net.minecraft.server.v1_5_R2.Vec3D;
import net.minecraft.server.v1_5_R2.World;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.WorldType;
import org.bukkit.block.BlockState;

import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftInventoryAnvil;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftInventoryBeacon;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftInventoryBrewer;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftInventoryEnchanting;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftInventoryFurnace;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftInventoryMerchant;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftInventoryPlayer;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftItemStack;

import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.BasicConverter;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.reflection.classes.DataWatcherRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerRef;
import com.bergerkiller.bukkit.common.reflection.classes.EnumGamemodeRef;
import com.bergerkiller.bukkit.common.reflection.classes.IntHashMapRef;
import com.bergerkiller.bukkit.common.reflection.classes.LongHashMapRef;
import com.bergerkiller.bukkit.common.reflection.classes.LongHashSetRef;
import com.bergerkiller.bukkit.common.reflection.classes.NBTRef;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerAbilitiesRef;
import com.bergerkiller.bukkit.common.reflection.classes.TileEntityRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldTypeRef;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;

/**
 * Converter for converting to wrapper classes (from handles and other types)
 * 
 * <T> - type of wrapper
 */
public abstract class WrapperConverter<T> extends BasicConverter<T> {
	public static final WrapperConverter<org.bukkit.entity.Entity> toEntity = new WrapperConverter<org.bukkit.entity.Entity>(org.bukkit.entity.Entity.class) {
		@Override
		public org.bukkit.entity.Entity convertSpecial(Object value, Class<?> valueType, org.bukkit.entity.Entity def) {
			if (value instanceof Entity) {
				final Entity handle = (Entity) value;
				if (handle.world == null) {
					// We need this to avoid NPE's for non-spawned entities!
					org.bukkit.entity.Entity entity = EntityRef.bukkitEntity.get(handle);
					if (entity == null) {
						entity = CraftEntity.getEntity((CraftServer) Bukkit.getServer(), handle);
						EntityRef.bukkitEntity.set(handle, entity);
					}
					return entity;
				} else {
					return handle.getBukkitEntity();
				}
			} else {
				return def;
			}
		}

		@Override
		public boolean isCastingSupported() {
			return true;
		}
	};
	public static final WrapperConverter<org.bukkit.World> toWorld = new WrapperConverter<org.bukkit.World>(org.bukkit.World.class) {
		@Override
		public org.bukkit.World convertSpecial(Object value, Class<?> valueType, org.bukkit.World def) {
			if (value instanceof World) {
				return ((World) value).getWorld();
			} else if (value instanceof Location) {
				return ((Location) value).getWorld();
			} else if (value instanceof org.bukkit.block.Block) {
				return ((org.bukkit.block.Block) value).getWorld();
			} else if (value instanceof BlockState) {
				return ((BlockState) value).getWorld();
			} else if (value instanceof Entity) {
				return ((Entity) value).world.getWorld();
			} else if (value instanceof TileEntity) {
				TileEntity tile = (TileEntity) value;
				return TileEntityRef.world.get(tile);
			} else if (value instanceof org.bukkit.entity.Entity) {
				return ((org.bukkit.entity.Entity) value).getWorld();
			} else if (value instanceof BlockState) {
				return ((BlockState) value).getWorld();
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<org.bukkit.Chunk> toChunk = new WrapperConverter<org.bukkit.Chunk>(org.bukkit.Chunk.class) {
		@Override
		public org.bukkit.Chunk convertSpecial(Object value, Class<?> valueType, org.bukkit.Chunk def) {
			if (value instanceof Chunk) {
				return ((Chunk) value).bukkitChunk;
			} else if (value instanceof org.bukkit.block.Block) {
				return ((org.bukkit.block.Block) value).getChunk();
			} else if (value instanceof BlockState) {
				return ((BlockState) value).getChunk();
			} else if (value instanceof Location) {
				return ((Location) value).getChunk();
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<org.bukkit.block.Block> toBlock = new WrapperConverter<org.bukkit.block.Block>(org.bukkit.block.Block.class) {
		@Override
		public org.bukkit.block.Block convertSpecial(Object value, Class<?> valueType, org.bukkit.block.Block def) {
			if (value instanceof Location) {
				return ((Location) value).getBlock();
			} else if (value instanceof BlockState) {
				return ((BlockState) value).getBlock();
			} else if (value instanceof TileEntity) {
				TileEntity tile = (TileEntity) value;
				return TileEntityRef.world.get(tile).getBlockAt(tile.x, tile.y, tile.z);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<BlockState> toBlockState = new WrapperConverter<BlockState>(BlockState.class) {
		@Override
		public BlockState convertSpecial(Object value, Class<?> valueType, BlockState def) {
			org.bukkit.block.Block block = toBlock.convert(value);
			if (block != null) {
				return block.getState();
			} else {
				return def;
			}
		}

		@Override
		public boolean isCastingSupported() {
			return true;
		}
	};
	public static final WrapperConverter<CommonTag> toCommonTag = new WrapperConverter<CommonTag>(CommonTag.class) {
		@Override
		public CommonTag convertSpecial(Object value, Class<?> valueType, CommonTag def) {
			if (NBTRef.NBTBase.isInstance(value)) {
				return CommonTag.create(value);
			} else {
				try {
					return CommonTag.create(null, value);
				} catch (Exception ex) {
				}
			}
			return def;
		}

		@Override
		public boolean isCastingSupported() {
			return true;
		}
	};
	public static final WrapperConverter<DataWatcher> toDataWatcher = new WrapperConverter<DataWatcher>(DataWatcher.class) {
		@Override
		public DataWatcher convertSpecial(Object value, Class<?> valueType, DataWatcher def) {
			if (DataWatcherRef.TEMPLATE.isInstance(value)) {
				return new DataWatcher(value);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<org.bukkit.inventory.ItemStack> toItemStack = new WrapperConverter<org.bukkit.inventory.ItemStack>(org.bukkit.inventory.ItemStack.class) {
		@Override
		public org.bukkit.inventory.ItemStack convertSpecial(Object value, Class<?> valueType, org.bukkit.inventory.ItemStack def) {
			if (value instanceof ItemStack) {
				return CraftItemStack.asCraftMirror((ItemStack) value);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<Inventory> toInventory = new WrapperConverter<Inventory>(Inventory.class) {
		@Override
		protected Inventory convertSpecial(Object value, Class<?> valueType, Inventory def) {
			if (value instanceof InventoryCrafting) {
				return new CraftInventoryCrafting((InventoryCrafting) value, null);
			} else if (value instanceof PlayerInventory) {
				return new CraftInventoryPlayer((PlayerInventory) value);
			} else if (value instanceof TileEntityFurnace) {
				return new CraftInventoryFurnace((TileEntityFurnace) value);
			} else if (value instanceof ContainerEnchantTableInventory) {
				return new CraftInventoryEnchanting((ContainerEnchantTableInventory) value);
			} else if (value instanceof TileEntityBrewingStand) {
				return new CraftInventoryBrewer((TileEntityBrewingStand) value);
			} else if (value instanceof InventoryMerchant) {
				return new CraftInventoryMerchant((InventoryMerchant) value);
			} else if (value instanceof TileEntityBeacon) {
				return new CraftInventoryBeacon((TileEntityBeacon) value);
			} else if (value instanceof ContainerAnvilInventory) {
				return new CraftInventoryAnvil((ContainerAnvilInventory) value);
			} else if (value instanceof IInventory) {
				return new CraftInventory((IInventory) value);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<Difficulty> toDifficulty = new WrapperConverter<Difficulty>(Difficulty.class) {
		@Override
		public Difficulty convertSpecial(Object value, Class<?> valueType, Difficulty def) {
			if (value instanceof Number) {
				return LogicUtil.fixNull(Difficulty.getByValue(((Number) value).intValue()), def);
			} else {
				return ParseUtil.parseEnum(Difficulty.class, value.toString(), def);
			}
		}
	};
	public static final WrapperConverter<org.bukkit.WorldType> toWorldType = new WrapperConverter<org.bukkit.WorldType>(org.bukkit.WorldType.class) {
		@Override
		public org.bukkit.WorldType convertSpecial(Object value, Class<?> valueType, org.bukkit.WorldType def) {
			if (WorldTypeRef.TEMPLATE.isInstance(value)) {
				return WorldType.getByName(WorldTypeRef.name.get(value));
			} else if (value != null) {
				return ParseUtil.parseEnum(org.bukkit.WorldType.class, value.toString(), def);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<org.bukkit.GameMode> toGameMode = new WrapperConverter<org.bukkit.GameMode>(org.bukkit.GameMode.class) {
		@Override
		public org.bukkit.GameMode convertSpecial(Object value, Class<?> valueType, org.bukkit.GameMode def) {
			if (EnumGamemodeRef.TEMPLATE.isInstance(value)) {
				return org.bukkit.GameMode.getByValue(EnumGamemodeRef.egmId.get(value));
			} else if (value instanceof Number) {
				return org.bukkit.GameMode.getByValue(((Number) value).intValue());
			} else {
				return ParseUtil.parseEnum(org.bukkit.GameMode.class, value.toString(), def);
			}
		}
	};
	public static final WrapperConverter<CommonPacket> toCommonPacket = new WrapperConverter<CommonPacket>(CommonPacket.class) {
		@Override
		public CommonPacket convertSpecial(Object value, Class<?> valueType, CommonPacket def) {
			if (PacketFields.DEFAULT.isInstance(value)) {
				return new CommonPacket(value);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<IntVector2> toIntVector2 = new WrapperConverter<IntVector2>(IntVector2.class) {
		@Override
		public IntVector2 convertSpecial(Object value, Class<?> valueType, IntVector2 def) {
			if (value instanceof ChunkCoordIntPair) {
				ChunkCoordIntPair pair = (ChunkCoordIntPair) value;
				return new IntVector2(pair.x, pair.z);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<IntVector3> toIntVector3 = new WrapperConverter<IntVector3>(IntVector3.class) {
		@Override
		public IntVector3 convertSpecial(Object value, Class<?> valueType, IntVector3 def) {
			if (value instanceof ChunkPosition) {
				ChunkPosition pos = (ChunkPosition) value;
				return new IntVector3(pos.x, pos.y, pos.z);
			} else if (value instanceof ChunkCoordinates) {
				ChunkCoordinates coord = (ChunkCoordinates) value;
				return new IntVector3(coord.x, coord.y, coord.z);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<Vector> toVector = new WrapperConverter<Vector>(Vector.class) {
		@Override
		public Vector convertSpecial(Object value, Class<?> valueType, Vector def) {
			if (value instanceof Location) {
				Location loc = (Location) value;
				return new Vector(loc.getX(), loc.getY(), loc.getZ());
			} else if (value instanceof Vec3D) {
				Vec3D vec = (Vec3D) value;
				return new Vector(vec.c, vec.d, vec.e);
			} else {
				IntVector3 v3 = toIntVector3.convert(value);
				if (value != null) {
					return new Vector(v3.x, v3.y, v3.z);
				} else {
					return def;
				}
			}
		}
	};
	public static final WrapperConverter<PlayerAbilities> toPlayerAbilities = new WrapperConverter<PlayerAbilities>(PlayerAbilities.class) {
		@Override
		protected PlayerAbilities convertSpecial(Object value, Class<?> valueType, PlayerAbilities def) {
			if (PlayerAbilitiesRef.TEMPLATE.isInstance(value)) {
				return new PlayerAbilities(value);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<EntityTracker> toEntityTracker = new WrapperConverter<EntityTracker>(EntityTracker.class) {
		@Override
		protected EntityTracker convertSpecial(Object value, Class<?> valueType, EntityTracker def) {
			if (EntityTrackerRef.TEMPLATE.isInstance(value)) {
				return new EntityTracker(value);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<LongHashMap<Object>> toLongHashMap = new WrapperConverter<LongHashMap<Object>>(LongHashMap.class) {
		@Override
		protected LongHashMap<Object> convertSpecial(Object value, Class<?> valueType, LongHashMap<Object> def) {
			if (LongHashMapRef.TEMPLATE.isInstance(value)) {
				return new LongHashMap<Object>(value);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<LongHashSet> toLongHashSet = new WrapperConverter<LongHashSet>(LongHashSet.class) {
		@Override
		protected LongHashSet convertSpecial(Object value, Class<?> valueType, LongHashSet def) {
			if (LongHashSetRef.TEMPLATE.isInstance(value)) {
				return new LongHashSet(value);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<IntHashMap<Object>> toIntHashMap = new WrapperConverter<IntHashMap<Object>>(IntHashMap.class) {
		@Override
		protected IntHashMap<Object> convertSpecial(Object value, Class<?> valueType, IntHashMap<Object> def) {
			if (IntHashMapRef.TEMPLATE.isInstance(value)) {
				return new IntHashMap<Object>(value);
			} else {
				return def;
			}
		}
	};

	@SuppressWarnings("unchecked")
	public WrapperConverter(Class<?> outputType) {
		super((Class<T>) outputType);
	}
}
