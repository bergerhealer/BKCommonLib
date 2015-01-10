package com.bergerkiller.bukkit.common.conversion.util;

import java.util.Map.Entry;

import com.bergerkiller.bukkit.common.conversion.ConverterPair;

/**
 * Wraps around another Entry of unknown contents and performs conversions automatically.
 * This can be used to interact with entries that require additional element conversion.
 *
 * @param <K> - entry key type
 * @param <V> - entry value type
 */
public class ConvertingEntry<K, V> implements Entry<K, V> {
	private final Entry<Object, Object> base;
	private final ConverterPair<Object, K> keyConverter;
	private final ConverterPair<Object, V> valueConverter;

	@SuppressWarnings("unchecked")
	public ConvertingEntry(Entry<?, ?> entry, ConverterPair<?, K> keyConverter, ConverterPair<?, V> valueConverter) {
		this.base = (Entry<Object, Object>) entry;
		this.keyConverter = (ConverterPair<Object, K>) keyConverter;
		this.valueConverter = (ConverterPair<Object, V>) valueConverter;
	}

	@Override
	public K getKey() {
		return keyConverter.convertB(base.getKey());
	}

	@Override
	public V getValue() {
		return valueConverter.convertB(base.getValue());
	}

	@Override
	public V setValue(V value) {
		return valueConverter.convertB(base.setValue(valueConverter.convertA(value)));
	}
}
