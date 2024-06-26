package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.mountiplex.conversion.builtin.EntryConverter;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A high-performance HashMap implementation that ignores the case of keys. The
 * keys are stored in the original case. This map violates it general contract
 * for that reason, as keys no longer have to equal one another.<br>
 * <br>
 * Not multithread-safe!
 *
 * @param <V> - Value type to map to String keys
 */
public class StringMapCaseInsensitive<V> implements Map<String, V> {
    private static final DuplexConverter<StringWrap, String> pair = new DuplexConverter<StringWrap, String>(StringWrap.class, String.class) {
        @Override
        public String convertInput(StringWrap value) {
            return value.key;
        }

        @Override
        public StringWrap convertOutput(String value) {
            return new StringWrap(value);
        }
    };

    private final HashMap<StringWrap, V> base = new HashMap<StringWrap, V>();
    private final StringWrap tmpWrap = new StringWrap("");

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
        if (key instanceof String) {
            return base.containsKey(tmpWrap.fill((String) key));
        } else if (key == null) {
            return base.containsKey(key);
        } else {
            return false;
        }
    }

    @Override
    public boolean containsValue(Object value) {
        return base.containsValue(value);
    }

    @Override
    public V get(Object key) {
        if (key instanceof String) {
            return base.get(tmpWrap.fill((String) key));
        } else if (key == null) {
            return base.get(null);
        } else {
            return null;
        }
    }

    @Override
    public V put(String key, V value) {
        return base.put(key == null ? null : new StringWrap(key), value);
    }

    @Override
    public V remove(Object key) {
        if (key == null) {
            return base.remove(key);
        } else if (key instanceof String) {
            return base.remove(tmpWrap.fill((String) key));
        } else {
            return null;
        }
    }

    @Override
    public void clear() {
        base.clear();
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        for (Entry<? extends String, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        if (key == null) {
            return base.getOrDefault(null, defaultValue);
        } else if (key instanceof String) {
            return base.getOrDefault(tmpWrap.fill((String) key), defaultValue);
        } else {
            return defaultValue;
        }
    }

    @Override
    public Set<String> keySet() {
        return new ConvertingSet<String>(base.keySet(), pair);
    }

    @Override
    public Collection<V> values() {
        return base.values();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Entry<String, V>> entrySet() {
        EntryConverter<String, V> converter = EntryConverter.create(pair, DuplexConversion.NONE);
        return new ConvertingSet<Entry<String, V>>(base.entrySet(), converter);
    }

    private static class StringWrap {

        private String key;
        private int hashCode;

        public StringWrap(String key) {
            fill(key);
        }

        public StringWrap fill(String key) {
            this.key = key;
            this.hashCode = 0;
            for (int i = 0; i < key.length(); i++) {
                this.hashCode = 31 * this.hashCode + Character.toLowerCase(key.charAt(i));
            }
            return this;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof StringWrap && this.key.equalsIgnoreCase(((StringWrap) o).key);
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }
    }
}
