package com.bergerkiller.bukkit.common.conversion.type;

import java.util.Map.Entry;

import com.bergerkiller.bukkit.common.conversion.CastingConverter;
import com.bergerkiller.bukkit.common.conversion.Converter;
import com.bergerkiller.bukkit.common.conversion.ConverterPair;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingEntry;

/**
 * Converter that uses a separate key and value converter to convert incoming entries
 * 
 * @param <K> - entry converter key type
 * @param <V> - entry converter value type
 */
public class EntryConverter<K, V> implements Converter<Entry<K, V>> {
	private final ConverterPair<Object, K> keyConverter;
	private final ConverterPair<Object, V> valueConverter;

	@SuppressWarnings("unchecked")
	public EntryConverter(ConverterPair<?, K> keyConverter, ConverterPair<?, V> valueConverter) {
		this.keyConverter = (ConverterPair<Object, K>) keyConverter;
		this.valueConverter = (ConverterPair<Object, V>) valueConverter;
	}

	@Override
	public Entry<K, V> convert(Object value, Entry<K, V> def) {
		if (value instanceof Entry) {
			return new ConvertingEntry<K, V>((Entry<?, ?>) value, keyConverter, valueConverter);
		} else {
			return def;
		}
	}

	@Override
	public Entry<K, V> convert(Object value) {
		return convert(value, null);
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Class<Entry<K, V>> getOutputType() {
		return (Class) Entry.class;
	}

	@Override
	public <B> ConverterPair<Entry<K, V>, B> formPair(Converter<B> converterB) {
		return new ConverterPair<Entry<K, V>, B>(this, converterB);
	}

	@Override
	public <C> Converter<C> cast(Class<C> type) {
		return new CastingConverter<C>(type, this);
	}

	@Override
	public boolean isCastingSupported() {
		return false;
	}

	@Override
	public boolean isRegisterSupported() {
		return false;
	}
}
