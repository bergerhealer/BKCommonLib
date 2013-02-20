package com.bergerkiller.bukkit.common.conversion.type;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.WorldType;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_4_R1.inventory.CraftItemStack;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.BasicConverter;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.reflection.classes.DataWatcherRef;
import com.bergerkiller.bukkit.common.reflection.classes.EnumGamemodeRef;
import com.bergerkiller.bukkit.common.reflection.classes.NBTRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldTypeRef;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;

import net.minecraft.server.v1_4_R1.*;

/**
 * Converter for converting to wrapper classes (from handles and other types)
 * 
 * <T> - type of wrapper
 */
public abstract class WrapperConverter<T> extends BasicConverter<T> {
	public static final WrapperConverter<org.bukkit.entity.Entity> toEntity = new WrapperConverter<org.bukkit.entity.Entity>(org.bukkit.entity.Entity.class) {
		@Override
		public org.bukkit.entity.Entity convert(Object value, org.bukkit.entity.Entity def) {
			if (value instanceof org.bukkit.entity.Entity) {
				return (org.bukkit.entity.Entity) value;
			} else if (value instanceof Entity) {
				return ((Entity) value).getBukkitEntity();
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
		public org.bukkit.World convert(Object value, org.bukkit.World def) {
			if (value instanceof org.bukkit.World) {
				return (org.bukkit.World) value;
			} else if (value instanceof World) {
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
				return ((TileEntity) value).world.getWorld();
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
		public org.bukkit.Chunk convert(Object value, org.bukkit.Chunk def) {
			if (value instanceof org.bukkit.Chunk) {
				return (org.bukkit.Chunk) value;
			} else if (value instanceof Chunk) {
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
		public org.bukkit.block.Block convert(Object value, org.bukkit.block.Block def) {
			if (value instanceof org.bukkit.block.Block) {
				return (org.bukkit.block.Block) value;
			} else if (value instanceof Location) {
				return ((Location) value).getBlock();
			} else if (value instanceof BlockState) {
				return ((BlockState) value).getBlock();
			} else if (value instanceof TileEntity) {
				TileEntity tile = (TileEntity) value;
				return tile.world.getWorld().getBlockAt(tile.x, tile.y, tile.z);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<BlockState> toBlockState = new WrapperConverter<BlockState>(BlockState.class) {
		@Override
		public BlockState convert(Object value, BlockState def) {
			if (value instanceof BlockState) {
				return (BlockState) value;
			} else {
				org.bukkit.block.Block block = toBlock.convert(value);
				if (block != null) {
					return block.getState();
				} else {
					return def;
				}
			}
		}

		@Override
		public boolean isCastingSupported() {
			return true;
		}
	};
	public static final WrapperConverter<CommonTag> toCommonTag = new WrapperConverter<CommonTag>(CommonTag.class) {
		@Override
		public CommonTag convert(Object value, CommonTag def) {
			if (value instanceof CommonTag) {
				return (CommonTag) value;
			} else if (NBTRef.NBTBase.isInstance(value)) {
				return CommonTag.create(value);
			} else if (value != null) {
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
		public DataWatcher convert(Object value, DataWatcher def) {
			if (value instanceof DataWatcher) {
				return (DataWatcher) value;
			} else if (DataWatcherRef.TEMPLATE.isInstance(value)) {
				return null; //TODO new DataWatcher(value);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<org.bukkit.inventory.ItemStack> toItemStack = new WrapperConverter<org.bukkit.inventory.ItemStack>(org.bukkit.inventory.ItemStack.class) {
		@Override
		public org.bukkit.inventory.ItemStack convert(Object value, org.bukkit.inventory.ItemStack def) {
			if (value instanceof org.bukkit.inventory.ItemStack) {
				return (org.bukkit.inventory.ItemStack) value;
			} else if (value instanceof ItemStack) {
				return CraftItemStack.asCraftMirror((ItemStack) value);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<Difficulty> toDifficulty = new WrapperConverter<Difficulty>(Difficulty.class) {
		@Override
		public Difficulty convert(Object value, Difficulty def) {
			if (value instanceof Difficulty) {
				return (org.bukkit.Difficulty) value;
			} else if (value instanceof Number) {
				return LogicUtil.fixNull(Difficulty.getByValue(((Number) value).intValue()), def);
			} else if (value != null) {
				return ParseUtil.parseEnum(Difficulty.class, value.toString(), def);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<org.bukkit.WorldType> toWorldType = new WrapperConverter<org.bukkit.WorldType>(org.bukkit.WorldType.class) {
		@Override
		public org.bukkit.WorldType convert(Object value, org.bukkit.WorldType def) {
			if (value instanceof org.bukkit.WorldType) {
				return (org.bukkit.WorldType) value;
			} else if (WorldTypeRef.TEMPLATE.isInstance(value)) {
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
		public org.bukkit.GameMode convert(Object value, org.bukkit.GameMode def) {
			if (value instanceof org.bukkit.GameMode) {
				return (org.bukkit.GameMode) value;
			} else if (EnumGamemodeRef.TEMPLATE.isInstance(value)) {
				return org.bukkit.GameMode.getByValue(EnumGamemodeRef.egmId.get(value));
			} else if (value instanceof Number) {
				return org.bukkit.GameMode.getByValue(((Number) value).intValue());
			} else if (value != null) {
				return ParseUtil.parseEnum(org.bukkit.GameMode.class, value.toString(), def);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<CommonPacket> toCommonPacket = new WrapperConverter<CommonPacket>(CommonPacket.class) {
		@Override
		public CommonPacket convert(Object value, CommonPacket def) {
			if (value instanceof CommonPacket) {
				return (CommonPacket) value;
			} else if (PacketFields.DEFAULT.isInstance(value)) {
				return new CommonPacket(value);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<IntVector2> toIntVector2 = new WrapperConverter<IntVector2>(IntVector2.class) {
		@Override
		public IntVector2 convert(Object value, IntVector2 def) {
			if (value instanceof IntVector2) {
				return (IntVector2) value;
			} else if (value instanceof ChunkCoordIntPair) {
				ChunkCoordIntPair pair = (ChunkCoordIntPair) value;
				return new IntVector2(pair.x, pair.z);
			} else {
				return def;
			}
		}
	};
	public static final WrapperConverter<IntVector3> toIntVector3 = new WrapperConverter<IntVector3>(IntVector3.class) {
		@Override
		public IntVector3 convert(Object value, IntVector3 def) {
			if (value instanceof IntVector3) {
				return (IntVector3) value;
			} else if (value instanceof ChunkPosition) {
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

	public WrapperConverter(Class<T> outputType) {
		super(outputType);
	}
}
