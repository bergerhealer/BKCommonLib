package com.bergerkiller.bukkit.common.reflection;

import com.bergerkiller.bukkit.common.conversion.ConverterPair;

/**
 * A field implementation that allows direct getting and setting
 */
public abstract class SafeDirectField<T> implements FieldAccessor<T> {

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public T transfer(Object from, Object to) {
		T old = get(to);
		set(to, get(from));
		return old;
	}

	@Override
	public <K> TranslatorFieldAccessor<K> translate(ConverterPair<?, K> converterPair) {
		return new TranslatorFieldAccessor<K>(this, converterPair);
	}
}
