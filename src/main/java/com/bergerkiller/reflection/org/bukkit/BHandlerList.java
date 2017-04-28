package com.bergerkiller.reflection.org.bukkit;

import java.util.ArrayList;
import java.util.EnumMap;

import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

public class BHandlerList {
	public static final ClassTemplate<HandlerList> T = ClassTemplate.create(HandlerList.class);
	public static final FieldAccessor<EnumMap<EventPriority, ArrayList<RegisteredListener>>> handlerslots = T.getField("handlerslots", EnumMap.class);
}
