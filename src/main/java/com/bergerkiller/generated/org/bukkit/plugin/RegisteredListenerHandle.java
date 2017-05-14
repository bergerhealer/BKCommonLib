package com.bergerkiller.generated.org.bukkit.plugin;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.plugin.EventExecutor;

public class RegisteredListenerHandle extends Template.Handle {
    public static final RegisteredListenerClass T = new RegisteredListenerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(RegisteredListenerHandle.class, "org.bukkit.plugin.RegisteredListener");


    /* ============================================================================== */

    public static final RegisteredListenerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        RegisteredListenerHandle handle = new RegisteredListenerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public EventExecutor getExecutor() {
        return T.executor.get(instance);
    }

    public void setExecutor(EventExecutor value) {
        T.executor.set(instance, value);
    }

    public static final class RegisteredListenerClass extends Template.Class {
        public final Template.Field<EventExecutor> executor = new Template.Field<EventExecutor>();

    }
}
