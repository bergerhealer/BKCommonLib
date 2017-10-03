package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

@Deprecated
public class NMSEntityItem {
	public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityItem");
	public static final FieldAccessor<Integer> age = T.nextField("private int age");
}
