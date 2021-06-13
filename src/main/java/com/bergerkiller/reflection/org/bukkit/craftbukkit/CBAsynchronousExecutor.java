package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import java.util.Map;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

public class CBAsynchronousExecutor {
    public static final ClassTemplate<?> T = ClassTemplate.create("org.bukkit.craftbukkit.util.AsynchronousExecutor");
    public static final FieldAccessor<Map<?, ?>> tasks = T.selectField("final Map<P, Task> tasks");
}
