package com.bergerkiller.generated.org.bukkit.plugin;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.plugin.EventExecutor;

/**
 * Instance wrapper handle for type <b>org.bukkit.plugin.RegisteredListener</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class RegisteredListenerHandle extends Template.Handle {
    /** @See {@link RegisteredListenerClass} */
    public static final RegisteredListenerClass T = new RegisteredListenerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(RegisteredListenerHandle.class, "org.bukkit.plugin.RegisteredListener");

    /* ============================================================================== */

    public static RegisteredListenerHandle createHandle(Object handleInstance) {
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

    /**
     * Stores class members for <b>org.bukkit.plugin.RegisteredListener</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RegisteredListenerClass extends Template.Class<RegisteredListenerHandle> {
        public final Template.Field<EventExecutor> executor = new Template.Field<EventExecutor>();

    }

}

