package com.bergerkiller.bukkit.common.resources;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.wrappers.ResourceKey;

/**
 * Declares some commonly used sound resources by keys.
 * These keys are functional on all supported versions of Minecraft.
 */
public class CommonSounds {
    public static final ResourceKey EXTINGUISH;
    public static final ResourceKey WALK_CLOTH;
    public static final ResourceKey CLICK;
    public static final ResourceKey CLICK_WOOD;
    public static final ResourceKey PISTON_CONTRACT;
    public static final ResourceKey PISTON_EXTEND;
    public static final ResourceKey ITEM_BREAK;

    static {
        if (CommonCapabilities.KEYED_EFFECTS) {
            EXTINGUISH = ResourceKey.fromPath("block.fire.extinguish");
            WALK_CLOTH = ResourceKey.fromPath("block.cloth.fall");
            CLICK = ResourceKey.fromPath("ui.button.click");
            CLICK_WOOD = ResourceKey.fromPath("block.wood_button.click_on");
            PISTON_CONTRACT = ResourceKey.fromPath("block.piston.contract");
            PISTON_EXTEND = ResourceKey.fromPath("block.piston.extend");
            ITEM_BREAK = ResourceKey.fromPath("entity.item.break");
        } else {
            EXTINGUISH = ResourceKey.fromPath("random.fizz");
            WALK_CLOTH = ResourceKey.fromPath("step.cloth");
            CLICK = ResourceKey.fromPath("random.click");
            CLICK_WOOD = ResourceKey.fromPath("random.wood_click");
            PISTON_CONTRACT = ResourceKey.fromPath("tile.piston.in");
            PISTON_EXTEND = ResourceKey.fromPath("tile.piston.out");
            ITEM_BREAK = ResourceKey.fromPath("random.break");
        }
    }
}
