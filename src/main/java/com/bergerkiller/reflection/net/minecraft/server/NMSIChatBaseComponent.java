package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.MethodAccessor;

public class NMSIChatBaseComponent {
	public static final ClassTemplate<?> T = ClassTemplate.createNMS("IChatBaseComponent");
	public static final MethodAccessor<String> getText = T.selectMethod("public abstract String getText()");
}
