package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.MethodAccessor;

public class NMSCommandBlockListenerAbstract {
	public static final ClassTemplate<?> T = ClassTemplate.createNMS("CommandBlockListenerAbstract");
	public static final MethodAccessor<String> getCommand = T.selectMethod("public String getCommand()");
	public static final MethodAccessor<String> getName = T.selectMethod("public String getName()");
}
