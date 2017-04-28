package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

public class NMSEntityInsentient extends NMSEntityLiving {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityInsentient");
    
    public static final MethodAccessor<Object> getNavigation = T.selectMethod("public NavigationAbstract getNavigation()");
}
