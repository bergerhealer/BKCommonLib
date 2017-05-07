package com.bergerkiller.bukkit.common.conversion2;

import com.bergerkiller.bukkit.common.conversion2.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion2.type.WrapperConversion;
import com.bergerkiller.bukkit.common.conversion2.type.NBTConversion;
import com.bergerkiller.mountiplex.conversion2.Conversion;

public class CommonConverters extends Conversion {

    static {
        registerConverters(WrapperConversion.class);
        registerConverters(HandleConversion.class);
        registerConverters(NBTConversion.class);
    }
}
