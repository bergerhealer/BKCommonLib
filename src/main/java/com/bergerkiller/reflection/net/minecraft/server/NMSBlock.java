package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

@Deprecated
public class NMSBlock {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("Block");

    public static final FieldAccessor<String> name = T.selectField("private String name");
    public static final FieldAccessor<Object> material = T.selectField("protected final Material material");
}
