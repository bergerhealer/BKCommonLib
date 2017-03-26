package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;

public class NMSEntityItem {
	public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityItem");
	public static final FieldAccessor<Integer> age = T.nextField("private int age");
}
