package com.bergerkiller.bukkit.common.reflection.accessors;

import org.bukkit.GameMode;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;

public class GameModeFieldAccessor extends TranslatorFieldAccessor<GameMode> {
	private static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("EnumGamemode");
	private static final FieldAccessor<Integer> egmId = TEMPLATE.getField("e");
	private static final MethodAccessor<Object> getFromId = TEMPLATE.getMethod("a", int.class);

	public GameModeFieldAccessor(FieldAccessor<?> base) {
		super(base);
	}

	@Override
	public GameMode convert(Object value) {
		return GameMode.getByValue(egmId.get(value));
	}

	@Override
	public Object revert(GameMode value) {
		return getFromId.invoke(null, value.getValue());
	}
}
