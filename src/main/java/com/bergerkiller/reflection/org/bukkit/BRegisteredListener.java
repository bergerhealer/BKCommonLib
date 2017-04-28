package com.bergerkiller.reflection.org.bukkit;

import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

public class BRegisteredListener {
    public static final ClassTemplate<RegisteredListener> T = ClassTemplate.create(RegisteredListener.class);
    public static final FieldAccessor<EventExecutor> executor = T.selectField("private final EventExecutor executor");
}
