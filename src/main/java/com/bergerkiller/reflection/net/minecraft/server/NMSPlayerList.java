package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;

public class NMSPlayerList {
	public static final ClassTemplate<?> T = ClassTemplate.createNMS("PlayerList");
	public static final FieldAccessor<Integer> maxPlayers = T.selectField("protected int maxPlayers");
}
