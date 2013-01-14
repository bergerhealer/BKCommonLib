package com.bergerkiller.bukkit.common;

import org.bukkit.Material;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Can compare a material with an internal whitelist of materials
 */
public class MaterialTypeProperty extends MaterialProperty<Boolean> {
	private int[] allowedTypes;

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
}
