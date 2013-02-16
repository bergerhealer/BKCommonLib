package com.bergerkiller.bukkit.common.reflection.accessors;

import net.minecraft.server.v1_4_R1.Entity;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;
import com.bergerkiller.bukkit.common.utils.NativeUtil;

public class EntityFieldAccessor extends TranslatorFieldAccessor<org.bukkit.entity.Entity> {

	public EntityFieldAccessor(FieldAccessor<?> base) {
		super(base);
	}

	@Override
	public org.bukkit.entity.Entity convert(Object value) {
		return NativeUtil.getEntity((Entity) value);
	}

	@Override
	public Object revert(org.bukkit.entity.Entity value) {
		return NativeUtil.getNative(value);
	}
}
