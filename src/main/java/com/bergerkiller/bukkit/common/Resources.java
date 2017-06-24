package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.wrappers.ResourceKey;
import com.bergerkiller.generated.net.minecraft.server.SoundEffectsHandle;

/**
 * Stores some resource constants for resources used in Minecraft
 */
public class Resources {
    public static final ResourceKey SOUND_FIRE_EXTINGUISH;
    
    static {
        if (SoundEffectsHandle.T.isAvailable()) {
            SOUND_FIRE_EXTINGUISH = SoundEffectsHandle.EXTINGUISH_FIRE;
        } else {
            SOUND_FIRE_EXTINGUISH = ResourceKey.fromPath("random.fizz");
        }
    }
}
