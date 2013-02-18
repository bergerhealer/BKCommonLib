package com.bergerkiller.bukkit.common.conversion;

import org.bukkit.Material;

import com.bergerkiller.bukkit.common.utils.ParseUtil;

import net.minecraft.server.v1_4_R1.*;

/**
 * Converter to convert to a certain property obtained from various kinds of objects<br>
 * These converters are not registered, as they can potentially overwrite data conversions
 * 
 * @param <T> - type of output
 */
public abstract class PropertyConverter<T> extends BasicConverter<T> {
	public static final PropertyConverter<Integer> toItemId = new PropertyConverter<Integer>(Integer.class) {
		@Override
		public Integer convert(Object value, Integer def) {
			if (value == null) {
				return def;
			} else if (value instanceof Material) {
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
		public Material convert(Object value, Material def) {
			if (value instanceof Material) {
				return (Material) value;
			} else {
				Integer id = toItemId.convert(value);
				if (id != null) {
					Material mat = Material.getMaterial(id.intValue());
					if (mat != null) {
						return mat;
					}
				}
			}
			return def;
		}
	};

	public PropertyConverter(Class<T> outputType) {
		super(outputType);
	}
}
