package com.bergerkiller.bukkit.common.reflection.accessors;

import org.bukkit.WorldType;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;

public class WorldTypeFieldAccessor extends TranslatorFieldAccessor<WorldType> {
	private static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("WorldType");
	private static final FieldAccessor<String> name = TEMPLATE.getField("name");
	private static final MethodAccessor<Object> getType = TEMPLATE.getMethod("getType", String.class);

	public WorldTypeFieldAccessor(FieldAccessor<?> base) {
		super(base);
	}

	@Override
	public WorldType convert(Object value) {
		if (TEMPLATE.isInstance(value)) {
			return WorldType.getByName(name.get(value));
		} else {
			return WorldType.NORMAL;
		}
	}

	@Override
	public Object revert(WorldType value) {
		if (value == null) {
			return null;
		} else {
			return getType.invoke(null, value.getName());
		}
	}
}
