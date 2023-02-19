package com.bergerkiller.bukkit.common;

import org.bukkit.plugin.Plugin;

/**
 * Hides away some internal-use PluginBase APIs. Don't use please :)
 */
public class PluginBaseInternalHelper {

    /**
     * Internal use only! Calls the {@link PluginBase#updateDependency(Plugin, String, boolean)} method, while doing
     * some background book-keeping as well.
     *
     * @param pluginBase PluginBase on which to call updateDependency
     * @param plugin Plugin that enabled or disabled
     * @param pluginName Name of the plugin
     * @param enabled Whether the Plugin Enabled (true) or Disabled (false)
     */
    public static void handleUpdateDependency(PluginBase pluginBase, Plugin plugin, String pluginName, boolean enabled) {
        if (enabled) {
            pluginBase.handleClassDependencyLoaded(plugin);
        }
        pluginBase.updateDependency(plugin, pluginName, enabled);
    }
}
