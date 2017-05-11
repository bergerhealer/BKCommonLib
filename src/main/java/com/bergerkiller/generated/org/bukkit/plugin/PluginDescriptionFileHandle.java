package com.bergerkiller.generated.org.bukkit.plugin;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;

public class PluginDescriptionFileHandle extends Template.Handle {
    public static final PluginDescriptionFileClass T = new PluginDescriptionFileClass();


    public Map<String, Map<String, Object>> getCommands() {
        return T.commands.get(instance);
    }

    public void setCommands(Map<String, Map<String, Object>> value) {
        T.commands.set(instance, value);
    }

    public static class PluginDescriptionFileClass extends Template.Class {

        protected PluginDescriptionFileClass() {
            init(PluginDescriptionFileClass.class, "org.bukkit.plugin.PluginDescriptionFile");
        }

        public final Template.Field<Map<String, Map<String, Object>>> commands = new Template.Field<Map<String, Map<String, Object>>>();

    }
}
