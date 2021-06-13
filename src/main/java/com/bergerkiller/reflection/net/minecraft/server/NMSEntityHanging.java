package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.EntityHangingHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

/**
 * Deprecated: use {@link EntityHangingHandle} instead
 */
@Deprecated
public class NMSEntityHanging extends NMSEntity {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityHanging");
    public static final TranslatorFieldAccessor<IntVector3> blockPosition = EntityHangingHandle.T.blockPosition.toFieldAccessor();
}
