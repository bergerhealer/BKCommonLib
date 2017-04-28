package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

public class NMSItem {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("Item");
    public static final FieldAccessor<Integer> maxStackSize = T.nextField("protected int maxStackSize");
}
