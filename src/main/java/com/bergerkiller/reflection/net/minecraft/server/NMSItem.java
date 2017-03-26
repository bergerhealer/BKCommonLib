package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;

public class NMSItem {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("Item");
    public static final FieldAccessor<Integer> maxStackSize = T.nextField("protected int maxStackSize");
}
