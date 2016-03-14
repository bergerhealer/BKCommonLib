package com.bergerkiller.bukkit.common.utils;

import net.minecraft.server.v1_9_R1.EnumHand;
import org.bukkit.inventory.MainHand;

/**
 * Created by Develop on 14-3-2016.
 */
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
}
