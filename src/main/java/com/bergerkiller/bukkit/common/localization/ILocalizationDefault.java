package com.bergerkiller.bukkit.common.localization;

import java.util.Locale;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;

public interface ILocalizationDefault {

    /**
     * Gets the full name of this Localization node
     *
     * @return Localization name
     */
    String getName();

    /**
     * Gets the default value set for this Localization node
     *
     * @return Localization default
     */
    String getDefault();

    /**
     * Writes the default localization configuration using
     * {@link #writeDefaults(ConfigurationNode, String)} to the specified
     * Localization configuration if a node or value is not yet present.
     *
     * @param config Configuration to initialize the defaults in
     */
    default void initDefaults(ConfigurationNode config) {
        String path = this.getName().toLowerCase(Locale.ENGLISH);
        if (!config.contains(path)) {
            writeDefaults(config, path);
        }
    }

    /**
     * Writes the default localization configuration to the full Localization
     * configuration object.
     *
     * @param config Configuration to write to
     * @param path Path of the Localization, lowercase of {@link #getName()}
     */
    default void writeDefaults(ConfigurationNode config, String path) {
        config.set(path, this.getDefault());
    }
}
