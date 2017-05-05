package com.bergerkiller.bukkit.common.conversion2;

import com.bergerkiller.bukkit.common.conversion2.type.WorldConverter;
import com.bergerkiller.mountiplex.conversion2.Conversion;

public class CommonConverters extends Conversion {

    static {
        registerConverters(WorldConverter.class);
    }
}
