package com.bergerkiller.bukkit.common;

import java.util.Locale;

import org.bukkit.Material;

/**
 * Base class for material properties that return a Boolean when getting.
 * Provides a basic implementation for toString to list all enabled (True) elements
 */
public abstract class MaterialBooleanProperty extends MaterialProperty<Boolean> {

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Material material : Material.values()) {
			if (Boolean.TRUE.equals(get(material))) {
				if (builder.length() > 0) {
					builder.append(';');
				}
				builder.append(material.toString().toLowerCase(Locale.ENGLISH));
			}
		}
		return builder.toString();
	}
}
