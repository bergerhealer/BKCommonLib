package com.bergerkiller.bukkit.common.conversion.type;

import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import com.bergerkiller.bukkit.common.conversion.BasicConverter;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;

import net.minecraft.server.v1_4_R1.*;

/**
 * Converter to convert to a certain property obtained from various kinds of objects<br>
 * These converters are not registered, as they can potentially overwrite data conversions
 * 
 * @param <T> - type of output
 */
public abstract class PropertyConverter<T> extends BasicConverter<T> {
	private static final BlockFace[] paintingFaces = {BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST};

	public static final PropertyConverter<Integer> toItemId = new PropertyConverter<Integer>(Integer.class) {
		@Override
		public Integer convertSpecial(Object value, Class<?> valueType, Integer def) {
			if (value instanceof Material) {
				return ((Material) value).getId();
			} else if (value instanceof Block) {
				return ((Block) value).id;
			} else if (value instanceof org.bukkit.block.Block) {
				return ((org.bukkit.block.Block) value).getTypeId();
			} else if (value instanceof Item) {
				return ((Item) value).id;
			} else if (value instanceof ItemStack) {
				return ((ItemStack) value).id;
			} else if (value instanceof EntityItem) {
				return ((EntityItem) value).getItemStack().id;
			} else if (value instanceof org.bukkit.entity.Item) {
				return ((org.bukkit.entity.Item) value).getItemStack().getTypeId();
			} else if (value instanceof org.bukkit.inventory.ItemStack) {
				return ((org.bukkit.inventory.ItemStack) value).getTypeId();
			} else if (value instanceof Number) {
				return NumberConverter.toInt.convert((Number) value);
			} else {
				// Get id by name
				Material mat = ParseUtil.parseMaterial(value.toString(), null);
				if (mat != null) {
					return mat.getId();
				} else {
					return def;
				}
			}
		}
	};
	public static final PropertyConverter<Material> toItemMaterial = new PropertyConverter<Material>(Material.class) {
		@Override
		public Material convertSpecial(Object value, Class<?> valueType, Material def) {
			Integer id = toItemId.convert(value);
			if (id != null) {
				Material mat = Material.getMaterial(id.intValue());
				if (mat != null) {
					return mat;
				}
			}
			return def;
		}
	};
	public static final PropertyConverter<Byte> toDifficultyId = new PropertyConverter<Byte>(Byte.class) {
		@Override
		public Byte convertSpecial(Object value, Class<?> valueType, Byte def) {
			value = WrapperConverter.toDifficulty.convert(value);
			if (value instanceof Difficulty) {
				return Byte.valueOf((byte) ((Difficulty) value).getValue());
			} else {
				return def;
			}
		}
	};
	public static final PropertyConverter<Integer> toPaintingFacingId = new PropertyConverter<Integer>(Integer.class) {
		@Override
		public Integer convertSpecial(Object value, Class<?> valueType, Integer def) {
			if (value instanceof Number) {
				return ((Number) value).intValue();
			} else {
				BlockFace face = Conversion.convert(value, BlockFace.class);
				if (face != null) {
					for (int i = 0; i < paintingFaces.length; i++) {
						if (paintingFaces[i] == face) {
							return i;
						}
					}
				}
				return def;
			}
		}
	};
	public static final PropertyConverter<BlockFace> toPaintingFacing = new PropertyConverter<BlockFace>(BlockFace.class) {
		@Override
		public BlockFace convertSpecial(Object value, Class<?> valueType, BlockFace def) {
			Integer id = toPaintingFacingId.convert(value);
			if (id != null) {
				final int idInt = id.intValue();
				if (LogicUtil.isInBounds(paintingFaces, idInt)) {
					return paintingFaces[idInt];
				}
			}
			return def;
		}
	};

	public PropertyConverter(Class<T> outputType) {
		super(outputType);
	}

	@Override
	public boolean isRegisterSupported() {
		return false;
	}
}
