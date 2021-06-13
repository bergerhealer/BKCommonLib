package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.world.entity.item.EntityItemHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

@Deprecated
public class NMSEntityItem {
	public static final ClassTemplate<?> T = ClassTemplate.create(EntityItemHandle.T.getType());
	public static final FieldAccessor<Integer> age = EntityItemHandle.T.age.toFieldAccessor();
}
