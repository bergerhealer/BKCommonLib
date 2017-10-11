package com.bergerkiller.bukkit.common.resources;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.wrappers.ResourceKey;

/**
 * Declares some commonly used sound resources by keys
 */
public class CommonSounds {
    public static final ResourceKey EXTINGUISH;
    public static final ResourceKey WALK_CLOTH;

    static {
        if (CommonCapabilities.KEYED_EFFECTS) {
            EXTINGUISH = ResourceKey.fromPath("block.fire.extinguish");
            WALK_CLOTH = ResourceKey.fromPath("block.cloth.fall");
        } else {
            EXTINGUISH = ResourceKey.fromPath("random.fizz");
            WALK_CLOTH = ResourceKey.fromPath("step.cloth");
        }
    }
}
