package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.world.entity.EntityInsentientHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

/**
 * Deprecated: use EntityInsentientHandle instead
 */
@Deprecated
public class NMSEntityInsentient extends NMSEntityLiving {
    public static final ClassTemplate<?> T = ClassTemplate.create(EntityInsentientHandle.T.getType());

    public static final MethodAccessor<Object> getNavigation = EntityInsentientHandle.T.getNavigation.toMethodAccessor();
}
