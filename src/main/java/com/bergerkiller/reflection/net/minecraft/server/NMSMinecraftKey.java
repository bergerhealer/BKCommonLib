package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

/**
 * <b>Deprecated: </b>Use {@link MinecraftKeyHandle} instead.
 */
@Deprecated
public class NMSMinecraftKey {
	public static final ClassTemplate<?> T = ClassTemplate.createNMS("MinecraftKey");

	public static final FieldAccessor<String> namespace = MinecraftKeyHandle.T.namespace.toFieldAccessor();
	public static final FieldAccessor<String> name = MinecraftKeyHandle.T.name.toFieldAccessor();

	public static Object newInstance(String namespace, String name) {
        return MinecraftKeyHandle.T.createNew2.raw.invoke(namespace, name);
	}

	public static Object newInstance(String name) {
	    return MinecraftKeyHandle.T.createNew.raw.invoke(name);
	}

	public static String getCombinedName(Object minecraftKey) {
		return minecraftKey.toString();
	}
}
