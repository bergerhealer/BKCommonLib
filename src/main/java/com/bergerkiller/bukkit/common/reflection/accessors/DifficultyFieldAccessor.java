package com.bergerkiller.bukkit.common.reflection.accessors;

import org.bukkit.Difficulty;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;

/**
 * Accesses a difficulty byte or int field
 */
public class DifficultyFieldAccessor extends TranslatorFieldAccessor<Difficulty> {

	public DifficultyFieldAccessor(FieldAccessor<?> base) {
		super(base);
	}

	@Override
	public Difficulty convert(Object value) {
		if (value instanceof Number) {
			return Difficulty.getByValue(((Number) value).intValue());
		} else {
			return Difficulty.NORMAL;
		}
	}

	@Override
	public Object revert(Difficulty value) {
		return Byte.valueOf((byte) value.getValue());
	}
}
