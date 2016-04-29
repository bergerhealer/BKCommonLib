package com.bergerkiller.bukkit.common.utils;

import net.minecraft.server.v1_9_R1.EnumHand;
import org.bukkit.inventory.MainHand;

public class HandUtil {

    public static EnumHand toEnumHand(MainHand hand) {
        switch(hand) {
            case LEFT:
                return EnumHand.OFF_HAND;
            case RIGHT:
                return EnumHand.MAIN_HAND;
            default:
                return null;
        }
    }

    public static MainHand toMainHand(EnumHand hand) {
        switch(hand) {
            case OFF_HAND:
                return MainHand.LEFT;
            case MAIN_HAND:
                return MainHand.RIGHT;
            default:
                return null;
        }
    }
}
