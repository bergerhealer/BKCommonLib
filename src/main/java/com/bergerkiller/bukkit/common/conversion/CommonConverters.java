package com.bergerkiller.bukkit.common.conversion;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.NBTConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.mountiplex.conversion.Conversion;

public class CommonConverters extends Conversion {

    static {
        registerConverters(WrapperConversion.class);
        registerConverters(HandleConversion.class);
        registerConverters(NBTConversion.class);
    }
}
