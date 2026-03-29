package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.world.entity.MobHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

/**
 * Deprecated: use MobHandle instead
 */
@Deprecated
public class NMSEntityInsentient extends NMSEntityLiving {
    public static final ClassTemplate<?> T = ClassTemplate.create(MobHandle.T.getType());

    public static final MethodAccessor<Object> getNavigation = MobHandle.T.getNavigation.toMethodAccessor();
}
