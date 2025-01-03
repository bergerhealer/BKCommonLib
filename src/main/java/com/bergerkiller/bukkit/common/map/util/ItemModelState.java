package com.bergerkiller.bukkit.common.map.util;

/**
 * Used since Minecraft 1.21.4 for assets/minecraft/items/*.json files.
 * Contains information where the model for an item can be found.
 */
public class ItemModelState {
    public ModelInfo model;

    public static class ModelInfo {
        public String type;
        public String model;
    }
}
