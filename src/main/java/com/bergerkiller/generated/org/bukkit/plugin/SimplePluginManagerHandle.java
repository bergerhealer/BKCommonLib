package com.bergerkiller.generated.org.bukkit.plugin;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.plugin.Plugin;
import java.util.List;

public class SimplePluginManagerHandle extends Template.Handle {
    public static final SimplePluginManagerClass T = new SimplePluginManagerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(SimplePluginManagerHandle.class, "org.bukkit.plugin.SimplePluginManager");


    /* ============================================================================== */

    public static final SimplePluginManagerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        SimplePluginManagerHandle handle = new SimplePluginManagerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public List<Plugin> getPlugins() {
        return T.plugins.get(instance);
    }

    public void setPlugins(List<Plugin> value) {
        T.plugins.set(instance, value);
    }

    public static final class SimplePluginManagerClass extends Template.Class {
        public final Template.Field<List<Plugin>> plugins = new Template.Field<List<Plugin>>();

    }
}
