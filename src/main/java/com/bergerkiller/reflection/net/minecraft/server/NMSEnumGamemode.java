package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.MethodAccessor;

public class NMSEnumGamemode {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EnumGamemode");
    public static final FieldAccessor<Integer> egmId = T.selectField("int f");
    public static final MethodAccessor<Object> getFromId = T.selectMethod("public static EnumGamemode getById(int paramInt)");
}
