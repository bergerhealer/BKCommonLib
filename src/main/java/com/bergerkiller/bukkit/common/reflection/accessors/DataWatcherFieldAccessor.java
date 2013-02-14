package com.bergerkiller.bukkit.common.reflection.accessors;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;

//TODO: Implement a wrapper DataWatcher type to expose!
public class DataWatcherFieldAccessor extends TranslatorFieldAccessor<Object> {

	public DataWatcherFieldAccessor(FieldAccessor<?> base) {
		super(base);
	}

	@Override
	public Object convert(Object value) {
		return value;
	}

	@Override
	public Object revert(Object value) {
		return value;
	}
}
