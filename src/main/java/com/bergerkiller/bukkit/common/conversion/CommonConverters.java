package com.bergerkiller.bukkit.common.conversion;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.MC1_8_8_Conversion;
import com.bergerkiller.bukkit.common.conversion.type.NBTConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.mountiplex.conversion.Conversion;

public class CommonConverters extends Conversion {

    static {
        registerConverters(WrapperConversion.class);
        registerConverters(HandleConversion.class);
        registerConverters(NBTConversion.class);
        if (Common.evaluateMCVersion("<=", "1.8.8")) {
            registerConverters(MC1_8_8_Conversion.class);
        }
    }
}
