package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.HashSet;

import com.bergerkiller.mountiplex.reflection.ClassHook;

/**
 * This hook is used to spawn entities without creating an entity tracker entry.
 * It is only temporarily hooked for the duration of a single Entity spawn.
 */
public class EntityTrackerHook extends ClassHook<EntityTrackerHook> {
    public final HashSet<Object> ignoredEntities = new HashSet<Object>();
    public final Object original;

    public EntityTrackerHook(Object original) {
        this.original = original;
    }

    @HookMethod("public void track(Entity entity)")
    public void track(Object nmsEntityHandle) {
        if (!ignoredEntities.contains(nmsEntityHandle)) {
            base.track(nmsEntityHandle);
        }
    }

}
