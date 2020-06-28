package com.bergerkiller.bukkit.common.resources;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;

/**
 * Marker class for resource keys
 */
public final class SoundEffect {
    public static final ResourceKey<SoundEffect> EXTINGUISH;
    public static final ResourceKey<SoundEffect> WALK_CLOTH;
    public static final ResourceKey<SoundEffect> CLICK;
    public static final ResourceKey<SoundEffect> CLICK_WOOD;
    public static final ResourceKey<SoundEffect> PISTON_CONTRACT;
    public static final ResourceKey<SoundEffect> PISTON_EXTEND;
    public static final ResourceKey<SoundEffect> ITEM_BREAK;

    static {
        if (CommonCapabilities.KEYED_EFFECTS) {
            if (Common.evaluateMCVersion(">=", "1.13")) {
                CLICK_WOOD = fromName("block.wooden_button.click_on");
                WALK_CLOTH = fromName("block.wool.fall");
            } else {
                CLICK_WOOD = fromName("block.wood_button.click_on");
                WALK_CLOTH = fromName("block.cloth.fall");
            }
            EXTINGUISH = fromName("block.fire.extinguish");
            CLICK = fromName("ui.button.click");
            PISTON_CONTRACT = fromName("block.piston.contract");
            PISTON_EXTEND = fromName("block.piston.extend");
            ITEM_BREAK = fromName("entity.item.break");
        } else {
            EXTINGUISH = fromName("random.fizz");
            WALK_CLOTH = fromName("step.cloth");
            CLICK = fromName("random.click");
            CLICK_WOOD = fromName("random.wood_click");
            PISTON_CONTRACT = fromName("tile.piston.in");
            PISTON_EXTEND = fromName("tile.piston.out");
            ITEM_BREAK = fromName("random.break");
        }
    }

    /**
     * Creates a sound effect resource key from a given name
     * 
     * @param name
     * @return resource key for the sound effect with this name
     */
    public static ResourceKey<SoundEffect> fromName(String name) {
        return ResourceCategory.sound_effect.createKey(name);
    }
}
