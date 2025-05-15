package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeDirectField;

/**
 * <b>Deprecated: </b>Use {@link MinecraftKeyHandle} instead.
 */
@Deprecated
public class NMSMinecraftKey {
	public static final ClassTemplate<?> T = ClassTemplate.create(MinecraftKeyHandle.T.getType());

	public static final FieldAccessor<String> namespace = new SafeDirectField<String>() {
		@Override
		public String get(Object instance) {
			return MinecraftKeyHandle.T.getNamespace.invoke(instance);
		}

		@Override
		public boolean set(Object instance, String value) {
			return false;
		}
	};
	public static final FieldAccessor<String> name = new SafeDirectField<String>() {
		@Override
		public String get(Object instance) {
			return MinecraftKeyHandle.T.getName.invoke(instance);
		}

		@Override
		public boolean set(Object instance, String value) {
			return false;
		}
	};

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
