package com.bergerkiller.bukkit.common.config.yaml;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * List implementation for a node that doesn't exist yet.
 * When an element is added for the first time, the list is created.
 * From then on this value list acts as a proxy for the created list.
 */
public class YamlNodeLazyCreateValueList extends AbstractList<Object> implements YamlNodeLinkedValue {
    private final boolean _createIndexedValueList;
    private YamlNodeAbstract<?> _parent;
    private String _path;
    private List<Object> _list;

    /**
     * Creates a new YamlNodeLazyCreateValueList. The createIndexedValueList parameter
     * specifies whether, when first adding a value to this list, an indexed node list is created.
     * When this is a false, a standard YAML list is created.
     * 
     * @param parent The parent relative to which the path is
     * @param path The path to where the list can be found or should be created
     * @param createIndexedValueList Whether to create an indexed value list
     */
    public YamlNodeLazyCreateValueList(YamlNodeAbstract<?> parent, String path, boolean createIndexedValueList) {
        _createIndexedValueList = createIndexedValueList;
        _parent = parent;
        _path = path;
        _list = null;
    }

    @Override
    public void clear() {
        getList().clear();
    }

    @Override
    public int size() {
        return getList().size();
    }

    @Override
    public boolean isEmpty() {
        return getList().isEmpty();
    }

    @Override
    public Iterator<Object> iterator() {
        return getList().iterator();
    }

    @Override
    public boolean contains(Object value) {
        return getList().contains(value);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getList().containsAll(c);
    }

    @Override
    public int indexOf(Object value) {
        return getList().indexOf(value);
    }

    @Override
    public int lastIndexOf(Object value) {
        return getList().lastIndexOf(value);
    }

    @Override
    public Object get(int index) {
        return getList().get(index);
    }

    @Override
    public Object set(int index, Object value) {
        return getList().set(index, value);
    }

    @Override
    public Object remove(int index) {
        return getList().remove(index);
    }

    @Override
    public boolean remove(Object value) {
        return getList().remove(value);
    }

    @Override
    public boolean removeAll(Collection<? extends Object> c) {
        return createList().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<? extends Object> c) {
        return createList().retainAll(c);
    }

    @Override
    public boolean add(Object value) {
        return createList().add(value);
    }

    @Override
    public void add(int index, Object value) {
        createList().add(index, value);
    }

    @Override
    public boolean addAll(Collection<? extends Object> c) {
        return createList().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Object> c) {
        return createList().addAll(index, c);
    }

    // Check whether the list now exists
    private List<Object> getList() {
        if (_list != null) {
            return _list;
        }

        // Check entry exists
        YamlEntry entry = _parent.getEntryIfExists(_path);
        if (entry == null) {
            return Collections.emptyList();
        }

        // Initialize the list
        Object value = entry.getValue();
        if (value instanceof YamlListNode) {
            _list = (YamlListNode) value;
        } else if (value instanceof YamlNodeAbstract) {
            _list = YamlNodeIndexedValueList.sortAndCreate((YamlNodeAbstract<?>) value);
        } else if (_createIndexedValueList) {
            _list = YamlNodeIndexedValueList.sortAndCreate(entry.createNodeValue());
        } else {
            _list = entry.createListNodeValue();
        }
        return _list;
    }

    // Create a new list if one didn't already exist
    private List<Object> createList() {
        if (_list == null) {
            YamlEntry entry = _parent.getEntry(_path);
            if (_createIndexedValueList) {
                _list = YamlNodeIndexedValueList.sortAndCreate(entry.createNodeValue());
            } else {
                _list = entry.createListNodeValue();
            }
        }
        return _list;
    }

    @Override
    public void assignTo(YamlEntry entry) {
        _parent = entry.getParentNode();
        _path = entry.getKey();
        if (_list != null) {
            entry.setValue(_list);
        } else if (_createIndexedValueList) {
            _list = YamlNodeIndexedValueList.sortAndCreate(entry.createNodeValue());
        } else {
            _list = entry.createListNodeValue();
        }
    }
}
