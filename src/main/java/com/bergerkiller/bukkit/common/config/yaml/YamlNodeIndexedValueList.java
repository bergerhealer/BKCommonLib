package com.bergerkiller.bukkit.common.config.yaml;

import java.util.AbstractList;
import java.util.Collections;
import java.util.Comparator;

import com.bergerkiller.bukkit.common.utils.ParseUtil;

/**
 * Represents a YamlNode as a list of values. The values are sorted by key name
 * and accessed in that order by index. When adding a value to the list using
 * this instance, all original keys are removed and replaced with a number
 * incremented from 0.
 */
public class YamlNodeIndexedValueList extends AbstractList<Object> implements YamlNodeLinkedValue {
    private final YamlNodeAbstract<?> _node;
    private boolean _namedByIndex;

    private YamlNodeIndexedValueList(YamlNodeAbstract<?> node) {
        _node = node;
        _namedByIndex = false;
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
    public boolean contains(Object value) {
        return _node.indexOfValue(value) != -1;
    }

    @Override
    public int indexOf(Object value) {
        return _node.indexOfValue(value);
    }

    @Override
    public int lastIndexOf(Object o) {
        return _node.lastIndexOfValue(o);
    }

    @Override
    public Object get(int index) {
        return _node._children.get(index).getValue();
    }

    @Override
    public Object set(int index, Object value) {
        return _node._children.get(index).setValue(value);
    }

    @Override
    public Object remove(int index) {
        Object result = _node.removeChildEntryAt(index);
        if (_namedByIndex) {
            this.updateIndexFrom(index);
        }
        return result;
    }

    @Override
    public boolean remove(Object value) {
        int index = _node.indexOfValue(value);
        if (index == -1) {
            return false;
        } else {
            this.remove(index);
            return true;
        }
    }

    @Override
    public boolean add(Object value) {
        add(_node._children.size(), value);
        return true;
    }

    @Override
    public void add(int index, Object value) {
        if (!_namedByIndex) {
            _namedByIndex = true;
            updateIndexFrom(0);
        }
        _node.createChildEntry(index, getIndexedPath(index)).setValue(value);
        updateIndexFrom(index+1);
    }

    @Override
    public void assignTo(YamlEntry entry) {
        entry.setValue(_node);
    }

    private void updateIndexFrom(int index) {
        while (index < _node._children.size()) {
            _node._children.get(index).setPath(getIndexedPath(index));
            index++;
        }
    }

    private YamlPath getIndexedPath(int index) {
        return _node.getYamlPath().child(Integer.toString(index));
    }

    /**
     * Sorts the children of a node by key and then creates an indexed value list
     * that can be used to address them.
     * 
     * @param node
     * @return value list
     */
    public static YamlNodeIndexedValueList sortAndCreate(YamlNodeAbstract<?> node) {
        Collections.sort(node._children, new Comparator<YamlEntry>() {
            @Override
            public int compare(YamlEntry e1, YamlEntry e2) {
                String n1 = e1.getKey();
                String n2 = e2.getKey();
                if (ParseUtil.isNumeric(n1) && ParseUtil.isNumeric(n2)) {
                    try {
                        int num1 = Integer.parseInt(n1);
                        int num2 = Integer.parseInt(n2);
                        return Integer.compare(num1, num2);
                    } catch (NumberFormatException ex) {}
                }
                return n1.compareTo(n2);
            }
        });
        for (int i = 0; i < node._children.size(); i++) {
            node._children.get(i).yaml.setIndex(i);
        }
        return new YamlNodeIndexedValueList(node);
    }
}
