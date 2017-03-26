package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.MethodAccessor;

public class NMSDataWatcherObject {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("DataWatcherObject");
    public static final MethodAccessor<Object> getHandle = T.selectMethod("public DataWatcherSerializer<T> b()");
}
