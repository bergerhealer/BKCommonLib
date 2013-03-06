package com.bergerkiller.bukkit.common;

import java.util.HashSet;
import java.util.Locale;

import org.bukkit.Material;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Can compare a material with an internal whitelist of materials
 */
public class MaterialTypeProperty extends MaterialBooleanProperty {
	private int[] allowedTypes;

	/**
	 * Initializes a new material type property containing all types from
	 * the other type properties specified
	 * 
	 * @param properties to set the types of
	 */
	public MaterialTypeProperty(MaterialTypeProperty... properties) {
		HashSet<Integer> elems = new HashSet<Integer>();
		for (MaterialTypeProperty prop : properties) {
			for (int id : prop.allowedTypes) {
				elems.add(id);
			}
		}
		this.allowedTypes = Conversion.toIntArr.convert(elems);
	}

	/**
	 * Initializes a new material type property
	 * 
	 * @param allowedMaterials to set
	 */
	public MaterialTypeProperty(Material... allowedMaterials) {
		this.allowedTypes = new int[allowedMaterials.length];
		for (int i = 0; i < allowedMaterials.length; i++) {
			this.allowedTypes[i] = allowedMaterials[i].getId();
		}
	}

	/**
	 * Initializes a new material type property
	 * 
	 * @param allowedTypes to set
	 */
	public MaterialTypeProperty(int... allowedTypes) {
		this.allowedTypes = new int[allowedTypes.length];
		for (int i = 0; i < allowedTypes.length; i++) {
			this.allowedTypes[i] = allowedTypes[i];
		}
	}

	@Override
	public Boolean get(int typeId) {
		return LogicUtil.containsInt(typeId, this.allowedTypes);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int typeId : this.allowedTypes) {
			final Material mat = Material.getMaterial(typeId);
			if (mat != null) {
				if (builder.length() > 0) {
					builder.append(';');
				}
				builder.append(mat.toString().toLowerCase(Locale.ENGLISH));
			}
		}
		return builder.toString();
	}
}
