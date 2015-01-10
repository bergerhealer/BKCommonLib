package com.bergerkiller.bukkit.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.bukkit.Material;

import com.bergerkiller.bukkit.common.utils.MaterialUtil;

/**
 * Can compare a material with an internal whitelist of materials
 */
public class MaterialTypeProperty extends MaterialBooleanProperty {
	private Material[] allowedTypes;

	/**
	 * Initializes a new material type property containing all types from
	 * the other type properties specified
	 * 
	 * @param properties to set the types of
	 */
	public MaterialTypeProperty(MaterialTypeProperty... properties) {
		HashSet<Material> elems = new HashSet<Material>();
		for (MaterialTypeProperty prop : properties) {
			for (Material mat : prop.allowedTypes) {
				elems.add(mat);
			}
		}
		this.allowedTypes = elems.toArray(new Material[0]);
	}

	/**
	 * Initializes a new material type property
	 * 
	 * @param allowedTypes to set
	 */
	@Deprecated
	public MaterialTypeProperty(int... allowedTypes) {
		this.allowedTypes = new Material[allowedTypes.length];
		for (int i = 0; i < allowedTypes.length; i++) {
			this.allowedTypes[i] = MaterialUtil.getType(allowedTypes[i]);
		}
	}

	/**
	 * Initializes a new material type property
	 * 
	 * @param allowedMaterials to set
	 */
	public MaterialTypeProperty(Material... allowedMaterials) {
		this.allowedTypes = new Material[allowedMaterials.length];
		System.arraycopy(allowedMaterials, 0, this.allowedTypes, 0, allowedMaterials.length);
	}

	@Override
	public Boolean get(Material type) {
		return MaterialUtil.isType(type, this.allowedTypes);
	}

	@Override
	@Deprecated
	public Boolean get(int typeId) {
		return super.get(typeId);
	}
	
	@Override
	public Collection<Material> getMaterials() {
		return Collections.unmodifiableList(Arrays.asList(this.allowedTypes));
	}
}
