package com.bergerkiller.bukkit.common.conversion.util;

import com.bergerkiller.bukkit.common.conversion.ConverterPair;
import com.bergerkiller.bukkit.common.conversion.type.EntryConverter;

import java.util.Map.Entry;
import java.util.Set;

public class ConvertingEntrySet<K, V> extends ConvertingSet<Entry<K, V>> {

    @SuppressWarnings("unchecked")
    public ConvertingEntrySet(Set<?> entrySet, ConverterPair<?, K> keyConverter, ConverterPair<?, V> valueConverter) {
        super(entrySet, new EntryConverter<Object, Object>((ConverterPair<K, Object>) keyConverter.reverse(),
                (ConverterPair<V, Object>) valueConverter.reverse()), new EntryConverter<K, V>(keyConverter, valueConverter));
        // This line is awful D:
    }
}
