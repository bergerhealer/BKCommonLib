package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.EnumGamemodeHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

/**
 * Deprecated: use EnumGamemodeHandle instead
 */
@Deprecated
public class NMSEnumGamemode {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EnumGamemode");
    public static final FieldAccessor<Integer> egmId = EnumGamemodeHandle.T.id.toFieldAccessor();
    public static final MethodAccessor<Object> getFromId = EnumGamemodeHandle.T.getById.raw.toMethodAccessor();
}
