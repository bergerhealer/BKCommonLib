package com.bergerkiller.bukkit.common.conversion.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.bergerkiller.bukkit.common.conversion.ConverterPair;

/**
 * Wraps around another Map of unknown contents and performs key/value conversions automatically.
 * This can be used to interact with maps that require additional element conversion.
 * 
 * @param <K> - key type
 * @param <V> - value type
 */
public class ConvertingMap<K, V> implements Map<K, V> {
	private final Map<Object, Object> base;
	protected final ConverterPair<Object, K> keyConverter;
	protected final ConverterPair<Object, V> valueConverter;

	@SuppressWarnings("unchecked")
	public ConvertingMap(Map<?, ?> map, ConverterPair<?, K> keyConverter, ConverterPair<?, V> valueConverter) {
		this.base = (Map<Object, Object>) map;
		this.keyConverter = (ConverterPair<Object, K>) keyConverter;
		this.valueConverter = (ConverterPair<Object, V>) valueConverter;
	}

	@Override
	public int size() {
		return base.size();
	}

	@Override
	public boolean isEmpty() {
		return base.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return base.containsKey(keyConverter.convertA(key));
	}

	@Override
	public boolean containsValue(Object value) {
		return base.containsValue(valueConverter.convertA(value));
	}

	@Override
	public V get(Object key) {
		return valueConverter.convertB(base.get(keyConverter.convertA(key)));
	}

	@Override
	public V put(K key, V value) {
		return valueConverter.convertB(base.put(keyConverter.convertA(key), valueConverter.convertB(value)));
	}

	@Override
	public V remove(Object key) {
		return valueConverter.convertB(base.remove(keyConverter.convertA(key)));
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		base.clear();
	}

	@Override
	public Set<K> keySet() {
		return new ConvertingSet<K>(base.keySet(), keyConverter);
	}

	@Override
	public Collection<V> values() {
		return new ConvertingCollection<V>(base.values(), valueConverter);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return new ConvertingEntrySet<K, V>(base.entrySet(), keyConverter, valueConverter);
	}
}
