package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import java.util.Map;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;

public class CBAsynchronousExecutor {
    public static final ClassTemplate<?> T = ClassTemplate.createCB("util.AsynchronousExecutor");
    public static final FieldAccessor<Map<?, ?>> tasks = T.selectField("final Map<P, Task> tasks");
}
