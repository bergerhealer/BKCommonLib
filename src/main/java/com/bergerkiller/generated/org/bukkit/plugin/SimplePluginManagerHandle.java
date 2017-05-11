package com.bergerkiller.generated.org.bukkit.plugin;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.plugin.Plugin;
import java.util.List;

public class SimplePluginManagerHandle extends Template.Handle {
    public static final SimplePluginManagerClass T = new SimplePluginManagerClass();


    public List<Plugin> getPlugins() {
        return T.plugins.get(instance);
    }

    public void setPlugins(List<Plugin> value) {
        T.plugins.set(instance, value);
    }

    public static class SimplePluginManagerClass extends Template.Class {

        protected SimplePluginManagerClass() {
            init(SimplePluginManagerClass.class, "org.bukkit.plugin.SimplePluginManager");
        }

        public final Template.Field<List<Plugin>> plugins = new Template.Field<List<Plugin>>();

    }
}
