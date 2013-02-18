package com.bergerkiller.bukkit.common.conversion;

import org.bukkit.Location;
import org.bukkit.block.BlockState;

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
	};

	public WrapperConverter(Class<T> outputType) {
		super(outputType);
	}
}
