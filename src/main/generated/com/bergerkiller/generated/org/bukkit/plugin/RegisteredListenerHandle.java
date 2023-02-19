package com.bergerkiller.generated.org.bukkit.plugin;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.plugin.EventExecutor;

/**
 * Instance wrapper handle for type <b>org.bukkit.plugin.RegisteredListener</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.plugin.RegisteredListener")
public abstract class RegisteredListenerHandle extends Template.Handle {
    /** @See {@link RegisteredListenerClass} */
    public static final RegisteredListenerClass T = Template.Class.create(RegisteredListenerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static RegisteredListenerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract EventExecutor getExecutor();
    public abstract void setExecutor(EventExecutor value);
    /**
     * Stores class members for <b>org.bukkit.plugin.RegisteredListener</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RegisteredListenerClass extends Template.Class<RegisteredListenerHandle> {
        public final Template.Field<EventExecutor> executor = new Template.Field<EventExecutor>();

    }

}

