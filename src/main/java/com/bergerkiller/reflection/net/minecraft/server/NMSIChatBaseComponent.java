package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.network.chat.ComponentHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

public class NMSIChatBaseComponent {
	public static final ClassTemplate<?> T = ClassTemplate.create(ComponentHandle.T.getType());
	public static final MethodAccessor<String> getText = ComponentHandle.T.getText.toMethodAccessor();
}
