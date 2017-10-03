package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.WorldTypeHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

@Deprecated
public class NMSWorldType {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("WorldType");
    public static final FieldAccessor<String> name = WorldTypeHandle.T.name.toFieldAccessor();
    public static final MethodAccessor<Object> getType = WorldTypeHandle.T.getType.raw.toMethodAccessor();
}
