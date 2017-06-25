package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.MinecraftKeyHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

/**
 * <b>Deprecated: </b>Use {@link MinecraftKeyHandle} instead.
 */
@Deprecated
public class NMSMinecraftKey {
	public static final ClassTemplate<?> T = ClassTemplate.createNMS("MinecraftKey");
	
	public static final FieldAccessor<String> namespace = T.nextField("protected final String a");
	public static final FieldAccessor<String> name = T.nextField("protected final String b");

	public static Object newInstance(String namespace, String name) {
	    return MinecraftKeyHandle.T.constr_code_parts.raw.newInstance(0, new String[] { namespace, name});
	}

	public static Object newInstance(String name) {
	    return MinecraftKeyHandle.T.constr_keyToken.raw.newInstance(name);
	}

	public static String getCombinedName(Object minecraftKey) {
		return minecraftKey.toString();
	}
}
