package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.MethodAccessor;

public class NMSWorldType {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("WorldType");
    public static final FieldAccessor<String> name = T.selectField("private final String name");
    public static final MethodAccessor<Object> getType = T.selectMethod("public static WorldType getType(String paramString)");
}
