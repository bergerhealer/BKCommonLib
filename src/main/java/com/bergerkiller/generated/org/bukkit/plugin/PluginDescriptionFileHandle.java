package com.bergerkiller.generated.org.bukkit.plugin;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class PluginDescriptionFileHandle extends Template.Handle {
    public static final PluginDescriptionFileClass T = new PluginDescriptionFileClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PluginDescriptionFileHandle.class, "org.bukkit.plugin.PluginDescriptionFile");


    /* ============================================================================== */

    public static final PluginDescriptionFileHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PluginDescriptionFileHandle handle = new PluginDescriptionFileHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public Map<String, Map<String, Object>> getCommands() {
        return T.commands.get(instance);
    }

    public void setCommands(Map<String, Map<String, Object>> value) {
        T.commands.set(instance, value);
    }

    public static final class PluginDescriptionFileClass extends Template.Class {
        public final Template.Field<Map<String, Map<String, Object>>> commands = new Template.Field<Map<String, Map<String, Object>>>();

    }
}
