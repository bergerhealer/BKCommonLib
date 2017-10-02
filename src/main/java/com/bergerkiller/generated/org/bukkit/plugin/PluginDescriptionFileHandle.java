package com.bergerkiller.generated.org.bukkit.plugin;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>org.bukkit.plugin.PluginDescriptionFile</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PluginDescriptionFileHandle extends Template.Handle {
    /** @See {@link PluginDescriptionFileClass} */
    public static final PluginDescriptionFileClass T = new PluginDescriptionFileClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PluginDescriptionFileHandle.class, "org.bukkit.plugin.PluginDescriptionFile");

    /* ============================================================================== */

    public static PluginDescriptionFileHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public Map<String, Map<String, Object>> getCommands() {
        return T.commands.get(getRaw());
    }

    public void setCommands(Map<String, Map<String, Object>> value) {
        T.commands.set(getRaw(), value);
    }

    /**
     * Stores class members for <b>org.bukkit.plugin.PluginDescriptionFile</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PluginDescriptionFileClass extends Template.Class<PluginDescriptionFileHandle> {
        public final Template.Field<Map<String, Map<String, Object>>> commands = new Template.Field<Map<String, Map<String, Object>>>();

    }

}

