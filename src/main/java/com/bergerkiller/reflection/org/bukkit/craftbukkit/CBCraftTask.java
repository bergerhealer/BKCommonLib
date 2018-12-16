package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.generated.org.bukkit.craftbukkit.scheduler.CraftTaskHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

import org.bukkit.plugin.Plugin;

/**
 * <b>Deprecated: use {@link CraftTaskHandle} instead</b>
 */
@Deprecated
public class CBCraftTask {
    public static final ClassTemplate<?> T = ClassTemplate.create(CraftTaskHandle.T.getType());
    public static final FieldAccessor<Runnable> task = CraftTaskHandle.T.task.toFieldAccessor();
    public static final FieldAccessor<Plugin> plugin = CraftTaskHandle.T.plugin.toFieldAccessor();
}
