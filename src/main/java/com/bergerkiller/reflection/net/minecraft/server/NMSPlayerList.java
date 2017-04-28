package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

public class NMSPlayerList {
	public static final ClassTemplate<?> T = ClassTemplate.createNMS("PlayerList");
	public static final FieldAccessor<Integer> maxPlayers = T.selectField("protected int maxPlayers");
}
