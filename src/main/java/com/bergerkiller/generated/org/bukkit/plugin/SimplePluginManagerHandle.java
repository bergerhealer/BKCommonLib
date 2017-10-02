package com.bergerkiller.generated.org.bukkit.plugin;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.plugin.Plugin;
import java.util.List;

/**
 * Instance wrapper handle for type <b>org.bukkit.plugin.SimplePluginManager</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class SimplePluginManagerHandle extends Template.Handle {
    /** @See {@link SimplePluginManagerClass} */
    public static final SimplePluginManagerClass T = new SimplePluginManagerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(SimplePluginManagerHandle.class, "org.bukkit.plugin.SimplePluginManager");

    /* ============================================================================== */

    public static SimplePluginManagerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public List<Plugin> getPlugins() {
        return T.plugins.get(getRaw());
    }

    public void setPlugins(List<Plugin> value) {
        T.plugins.set(getRaw(), value);
    }

    /**
     * Stores class members for <b>org.bukkit.plugin.SimplePluginManager</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SimplePluginManagerClass extends Template.Class<SimplePluginManagerHandle> {
        public final Template.Field<List<Plugin>> plugins = new Template.Field<List<Plugin>>();

    }

}

