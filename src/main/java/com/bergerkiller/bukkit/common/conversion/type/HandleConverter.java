package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.conversion.type.ProxyConverter;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;

/**
 * Converter for converting to internal handles (from wrapper classes)<br>
 * <b>Do not reference external state-classes while constructing (e.g.
 * reflection classes)</b>
 */
@Deprecated
public class HandleConverter extends ProxyConverter<Object> {
    private static final ClassResolver NMS_RESOLVER = new ClassResolver(Common.NMS_ROOT);

    private HandleConverter(String outputTypeName, boolean isCastingSupported) {
        super(NMS_RESOLVER, outputTypeName, isCastingSupported);
    }

    public static HandleConverter create(String outputTypeName, boolean isCastingSupported) {
        return new HandleConverter(outputTypeName, isCastingSupported);
    }

    public static HandleConverter create(String outputTypeName) {
        return new HandleConverter(outputTypeName, false);
    }
}
