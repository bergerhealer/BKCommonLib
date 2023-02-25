package com.bergerkiller.generated.org.bukkit.plugin;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>org.bukkit.plugin.PluginDescriptionFile</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.plugin.PluginDescriptionFile")
public abstract class PluginDescriptionFileHandle extends Template.Handle {
    /** @see PluginDescriptionFileClass */
    public static final PluginDescriptionFileClass T = Template.Class.create(PluginDescriptionFileClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PluginDescriptionFileHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Map<String, Map<String, Object>> getCommands();
    public abstract void setCommands(Map<String, Map<String, Object>> value);
    /**
     * Stores class members for <b>org.bukkit.plugin.PluginDescriptionFile</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PluginDescriptionFileClass extends Template.Class<PluginDescriptionFileHandle> {
        public final Template.Field<Map<String, Map<String, Object>>> commands = new Template.Field<Map<String, Map<String, Object>>>();

    }

}

