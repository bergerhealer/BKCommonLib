package com.bergerkiller.bukkit.common.internal.proxy;

import java.util.Map;

/**
 * PlayerProfile Bukkit class did not exist before Minecraft 1.18.1.
 * This proxy class stores the yaml-deserialized information so later migration steps
 * can properly process it instead of throwing exceptions.
 */
public class PlayerProfile_1_8_to_1_18 {
    public final Map<String, Object> meta;

    public PlayerProfile_1_8_to_1_18(Map<String, Object> meta) {
        this.meta = meta;
    }
}
