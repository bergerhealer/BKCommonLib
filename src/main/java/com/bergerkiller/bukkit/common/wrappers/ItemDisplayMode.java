package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;

/**
 * A mode of displaying an ItemStack inside a
 * {@link com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle.ItemDisplayHandle Item Display Entity}
 */
public enum ItemDisplayMode {
    NONE(0, "none"),
    THIRD_PERSON_LEFT_HAND(1, "thirdperson_lefthand"),
    THIRD_PERSON_RIGHT_HAND(2, "thirdperson_righthand"),
    FIRST_PERSON_LEFT_HAND(3, "firstperson_lefthand"),
    FIRST_PERSON_RIGHT_HAND(4, "firstperson_righthand"),
    HEAD(5, "head"),
    GUI(6, "gui"),
    GROUND(7, "ground"),
    FIXED(8, "fixed");

    private final byte bId;
    private final int id;
    private final String description;
    static final ItemDisplayMode[] VALUES_BY_ID = LogicUtil.make(() -> {
        int max = 0;
        for (ItemDisplayMode mode : values()) {
            max = Math.max(max, mode.id);
        }
        ItemDisplayMode[] result = new ItemDisplayMode[max + 1];
        for (ItemDisplayMode mode : values()) {
            result[mode.id] = mode;
        }
        return result;
    });

    ItemDisplayMode(int id, String description) {
        this.id = id;
        this.bId = (byte) id;
        this.description = description;
    }

    /**
     * Gets a description of this display mode
     *
     * @return description
     */
    public String description() {
        return description;
    }

    /**
     * Gets the ItemDisplayMode for a given {@link #getId(ItemDisplayMode)}
     *
     * @param id Mode ID
     * @return Mode. {@link #NONE} if out of bounds.
     */
    @ConverterMethod
    public static ItemDisplayMode byId(byte id) {
        int int_id = (id & 0xFF);
        ItemDisplayMode[] values = VALUES_BY_ID;
        return (int_id >= values.length) ? NONE : values[int_id];
    }

    /**
     * Gets the unique id of a certain item display mode
     *
     * @param mode Item Display Mode
     * @return ID of this mode
     */
    @ConverterMethod
    public static byte getId(ItemDisplayMode mode) {
        return mode.bId;
    }
}
