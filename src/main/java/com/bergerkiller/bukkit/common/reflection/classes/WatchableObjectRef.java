package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class WatchableObjectRef {

    public static final ClassTemplate<Object> TEMPLATE = new NMSClassTemplate("WatchableObject");
    public static final MethodAccessor<Object> getHandle = TEMPLATE.getMethod("b");
}
