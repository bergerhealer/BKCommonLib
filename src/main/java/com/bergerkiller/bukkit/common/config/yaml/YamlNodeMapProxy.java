package com.bergerkiller.bukkit.common.config.yaml;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Accesses a YamlNode's children using a Map interface.
 * Used by {@link YamlNode#getValues()}.
 */
public class YamlNodeMapProxy implements Map<String, Object> {
    private final YamlNode _node;

    public YamlNodeMapProxy(YamlNode node) {
       _node = node;
    }

    @Override
    public void clear() {
        _node.clear();
    }

    @Override
    public int size() {
        return _node._children.size();
    }

    @Override
    public boolean isEmpty() {
        return _node._children.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return _node.indexOfKey(key) != -1;
    }

    @Override
    public boolean containsValue(Object value) {
        return _node.indexOfValue(value) != -1;
    }

    @Override
    public Object get(Object key) {
        int index = _node.indexOfKey(key);
        return (index == -1) ? null : _node._children.get(index).getValue();
    }

    @Override
    public Object put(String key, Object value) {
        int index = _node.indexOfKey(key);
        if (index == -1) {
            YamlEntry entry = _node.createChildEntry(_node._children.size(), _node.getYamlPath().child(key));
            entry.setValue(value);
            return null;
        } else {
            YamlEntry entry = _node._children.get(index);
            Object previousValue = entry.getValue();
            entry.setValue(value);
            return previousValue;
        }
    }

    @Override
    public Object remove(Object key) {
        int index = _node.indexOfKey(key);
        if (index == -1) {
            return null;
        } else {
            return _node.removeChildEntryAt(index).getValue();
        }
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        for (Map.Entry<? extends String, ? extends Object> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<String> keySet() {
        return new YamlNodeKeySetProxy(_node);
    }

    @Override
    public Collection<Object> values() {
        if (_node instanceof YamlListNode) {
            return (YamlListNode) _node;
        } else {
            return new YamlNodeValueCollectionProxy(_node);
        }
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return new YamlNodeEntrySetProxy(_node);
    }
}
