package com.bergerkiller.bukkit.common.config.yaml;

/**
 * A type of value that refers to data elsewhere.
 * Adds a method that migrates that data to a target destination.
 */
public interface YamlNodeLinkedValue {

    /**
     * Assigns the contents of this value to an entry
     * 
     * @param entry
     */
    void assignTo(YamlEntry entry);
}
