package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.block.SignSide;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;

/**
 * Since Minecraft 26.3 there is a SignTextSlot enum. This converts from/to that enum so we can
 * use our {@link com.bergerkiller.bukkit.common.block.SignSide} API type instead.
 */
public class SignTextSlotConversion {
    private static final Object BACK_SLOT, FRONT_SLOT;
    static {
        Object backSlot = null, frontSlot = null;

        if (CommonBootstrap.evaluateMCVersion(">=", "26.3")) {
            CommonBootstrap.initCommonServer();
            Class<?> enumType = CommonUtil.getClass("net.minecraft.world.level.block.entity.SignTextSlot");
            if (enumType != null) {
                Object[] values = enumType.getEnumConstants();
                if (values.length == 2) {
                    backSlot = values[0];
                    frontSlot = values[1];
                }
            }
            if (backSlot == null) {
                Logging.LOGGER_REFLECTION.severe("Failed to find the SignTextSlot enumeration constants");
            }
        }

        BACK_SLOT = backSlot;
        FRONT_SLOT = frontSlot;
    }

    @ConverterMethod(input="net.minecraft.world.level.block.entity.SignTextSlot")
    public static SignSide fromSignTextSlot(Object signTextSlot) {
        return signTextSlot == BACK_SLOT ? SignSide.BACK : SignSide.FRONT;
    }

    @ConverterMethod(output="net.minecraft.world.level.block.entity.SignTextSlot")
    public static Object toSignTextSlot(SignSide side) {
        return side == SignSide.BACK ? BACK_SLOT : FRONT_SLOT;
    }
}
