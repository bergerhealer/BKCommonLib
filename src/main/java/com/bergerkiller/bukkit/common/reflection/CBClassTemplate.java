package com.bergerkiller.bukkit.common.reflection;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * A Class Template meant for pointing to a Class in the org.bukkit.craftbukkit Package<br>
 * Automatically deals with package versioning<br>
 * The empty constructor can be used to initialize this Class using the current Class name
 */
public class CBClassTemplate extends ClassTemplate<Object> {
	/**
	 * Initializes this CB Class Template using the CB Class name the same as this Class<br>
	 * If this Class is called 'CraftServer', it will point to Class 'org.bukkit.craftbukkit.[version].CraftServer'<br>
	 * A leading 'CB' or trailing 'Ref' is omitted from the Class name, avoiding the Class showing up in the imports<br>
	 * 
	 * This constructor should and can only be called by an extension of this Class
	 */
	protected CBClassTemplate() {
		setCBClass(getClass().getSimpleName());
	}

	/**
	 * Initializes this CB Class Template pointing to the class name specified
	 * 
	 * @param className in the CB package
	 */
	public CBClassTemplate(String className) {
		setCBClass(className);
	}

	/**
	 * Initializes this org.bukkit.craftbukkit Class Template to represent the CB Class name specified
	 * 
	 * @param className to represent
	 */
	@SuppressWarnings("unchecked")
	protected void setCBClass(String className) {
		// Get rid of nested-class
		int nestedIdx = className.lastIndexOf('$');
		if (nestedIdx != -1) {
			className = className.substring(nestedIdx + 1);
		}
		// Remove name appendices
		if (className.endsWith("Ref")) {
			className = className.substring(0, className.length() - 3);
		}
		if (className.startsWith("CB")) {
			className = className.substring(2);
		}
		setClass((Class<Object>) CommonUtil.getCBClass(className));
		if (getType() == null) {
			new RuntimeException("Failed to set a valid CB Class: name = " + className).printStackTrace();
		}
	}

	/**
	 * Creates a new CB Class Template for the org.bukkit.craftbukkit Class name specified
	 * 
	 * @param name of the class in the CB package
	 * @return new Class Template
	 */
	public static CBClassTemplate create(String name) {
		return new CBClassTemplate(name);
	}
}
