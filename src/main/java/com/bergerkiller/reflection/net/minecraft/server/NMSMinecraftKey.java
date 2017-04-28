package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeConstructor;

public class NMSMinecraftKey {
	public static final ClassTemplate<?> T = ClassTemplate.createNMS("MinecraftKey");
	
	public static final FieldAccessor<String> namespace = T.nextField("protected final String a");
	public static final FieldAccessor<String> name = T.nextField("protected final String b");

	private static final SafeConstructor<?> constr1 = T.getConstructor(String.class, String.class);
	private static final SafeConstructor<?> constr2 = T.getConstructor(String.class);
	
	public static Object newInstance(String namespace, String name) {
		return constr1.newInstance(namespace, name);
	}
	
	public static Object newInstance(String name) {
		return constr2.newInstance(name);
	}
	
	public static String getCombinedName(Object minecraftKey) {
		return minecraftKey.toString();
	}
}
