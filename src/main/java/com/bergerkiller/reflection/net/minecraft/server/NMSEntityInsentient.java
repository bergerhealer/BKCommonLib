package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.MethodAccessor;

public class NMSEntityInsentient extends NMSEntityLiving {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityInsentient");
    
    public static final MethodAccessor<Object> getNavigation = T.selectMethod("public NavigationAbstract getNavigation()");
}
