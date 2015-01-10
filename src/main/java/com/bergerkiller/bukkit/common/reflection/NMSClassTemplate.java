package com.bergerkiller.bukkit.common.reflection;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * A Class Template meant for pointing to a Class in the net.minecraft.server Package<br>
 * Automatically deals with package versioning<br>
 * The empty constructor can be used to initialize this Class using the current Class name
 */
public class NMSClassTemplate extends ClassTemplate<Object> {

	/**
	 * Initializes this NMS Class Template using the NMS Class name the same as this Class<br>
	 * If this Class is called 'Packet', it will point to Class 'net.minecraft.server.[version].Packet'<br>
	 * A leading 'NMS' or trailing 'Ref' is omitted from the Class name, avoiding the Class showing up in the imports<br>
	 * 
	 * This constructor should and can only be called by an extension of this Class
	 */
	protected NMSClassTemplate() {
		setNMSClass(getClass().getSimpleName());
	}

	/**
	 * Initializes this NMS Class Template pointing to the class name specified
	 * 
	 * @param className in the NMS package
	 */
	public NMSClassTemplate(String className) {
		setNMSClass(className);
	}

	/**
	 * Initializes this net.minecraft.server Class Template to represent the NMS Class name specified
	 * 
	 * @param className to represent
	 */
	@SuppressWarnings("unchecked")
	protected void setNMSClass(String className) {
		// Get rid of nested-class
		int nestedIdx = className.lastIndexOf('$');
		if (nestedIdx != -1) {
			className = className.substring(nestedIdx + 1);
		}
		// Remove name appendices
		if (className.endsWith("Ref")) {
			className = className.substring(0, className.length() - 3);
		}
		if (className.startsWith("NMS")) {
			className = className.substring(3);
		}
		setClass((Class<Object>) CommonUtil.getNMSClass(className));
		if (getType() == null) {
			new RuntimeException("Failed to set a valid NMS Class: name = " + className).printStackTrace();
		}
	}

	/**
	 * Creates a new NMS Class Template for the net.minecraft.server Class name specified
	 * 
	 * @param name of the class in the NMS package
	 * @return new Class Template
	 */
	public static NMSClassTemplate create(String name) {
		return new NMSClassTemplate(name);
	}
}
