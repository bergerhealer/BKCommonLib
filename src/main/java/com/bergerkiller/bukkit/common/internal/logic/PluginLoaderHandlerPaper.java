package com.bergerkiller.bukkit.common.internal.logic;

import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Used on modern paper servers when the plugin contains a paper-plugin.yml.
 * Doesn't really do much.
 */
class PluginLoaderHandlerPaper extends PluginLoaderHandler {

    public PluginLoaderHandlerPaper(Plugin plugin, String pluginConfigText) {
        super(plugin, pluginConfigText);
    }

    @Override
    public void bootstrap() {
    }

    @Override
    public void onPluginLoaded(Plugin plugin) {
    }

    @Override
    public void addAccessToClassloader(Plugin plugin) {
    }

    @Override
    public void addAccessToClassloaders(List<String> pluginNames) {
    }
}
