package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.wrappers.ResourceKey;

/**
 * Stores some resource constants for resources used in Minecraft
 */
public class Resources {
    public static final ResourceKey SOUND_FIRE_EXTINGUISH;
    
    static {
        if (Common.evaluateMCVersion(">=", "1.9")) {
            SOUND_FIRE_EXTINGUISH = ResourceKey.fromPath("block.fire.extinguish");
        } else {
            SOUND_FIRE_EXTINGUISH = ResourceKey.fromPath("random.fizz");
        }
    }
}
