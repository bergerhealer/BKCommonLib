package com.bergerkiller.bukkit.common.config.yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bergerkiller.bukkit.common.utils.ParseUtil;

public class YamlNode {
    protected YamlRoot _root;
    protected YamlEntry _entry;
    protected List<YamlEntry> _children;
    protected boolean _isListNode;

    public YamlNode() {
        this._root = new YamlRoot();
        this._entry = new YamlEntry(this);
        this._children = new ArrayList<YamlEntry>(10);
        this._isListNode = false;
        this._root.putEntry(this._entry);
    }

    // Creates a new child node from an entry
    protected YamlNode(YamlEntry entry, boolean isListNode) {
        this._root = entry.getParentNode()._root;
        this._entry = entry;
        this._children = new ArrayList<YamlEntry>(10);
        this._isListNode = isListNode;
        this._entry.setValue(this);
    }

    /**
     * Checks if this node has a parent, or if it a root node
     *
     * @return True if it has a parent, False if not
     */
    public boolean hasParent() {
        return this.getParent() != null;
    }

    /**
     * Checks if this node has sub-nodes or values
     *
     * @return True if this node is empty, False if not
     */
    public boolean isEmpty() {
        return this._children.isEmpty();
    }

    /**
     * Gets the parent configuration node of this Node
     *
     * @return parent node
     */
    public YamlNode getParent() {
        return this._entry.getParentNode();
    }

    /**
     * Gets the YamlPath that refers to this node
     * 
     * @return path
     */
    public YamlPath getYamlPath() {
        return this._entry.getPath();
    }

    /**
     * Gets the name of this Configuration Node
     *
     * @return node name
     */
    public String getName() {
        return this.getYamlPath().name();
    }

    /**
     * Gets the {@link YamlPath} to this node as a String.<br>
     * <b>Deprecated: use {@link #getYamlPath()}.toString() instead</b>
     * 
     * @return path
     */
    @Deprecated
    public String getPath() {
        return this.getYamlPath().toString();
    }

    /**
     * Gets the full path of a value or node relative to this Configuration Node.<br>
     * <b>Deprecated: you can construct these paths yourself using {@link YamlPath}</b>
     *
     * @param append path, the path to the value or node relative to this Node
     * @return The full path
     */
    @Deprecated
    public String getPath(String append) {
        return this.getYamlPath().child(append).toString();
    }

    /**
     * Gets the header of this Node
     *
     * @return The header
     */
    public String getHeader() {
        return this._entry.getHeader();
    }

    /**
     * Sets the header displayed above this configuration node
     *
     * @param header to set to
     */
    public void setHeader(String header) {
        this._entry.setHeader(header);
    }

    /**
     * Adds a new line to the header displayed above this configuration node
     *
     * @param header line to add
     */
    public void addHeader(String header) {
        this._entry.addHeader(header);
    }

    /**
     * Removes the header for this Node
     */
    public void removeHeader() {
        this.setHeader("");
    }

    /**
     * Gets the header of a value or node
     *
     * @param path to the value or node
     * @return Header at the path specified
     */
    public String getHeader(String path) {
        return getEntry(path).getHeader();
    }

    /**
     * Sets the header displayed above a given configuration node or value
     *
     * @param path to the node or value the header is for
     * @param header to set to
     */
    public void setHeader(String path, String header) {
        getEntry(path).setHeader(header);
    }

    /**
     * Adds a new line to the header displayed above a given configuration node
     * or value
     *
     * @param path to the node or value the header is for
     * @param header line to add
     */
    public void addHeader(String path, String header) {
        getEntry(path).addHeader(header);
    }

    /**
     * Removes the header for a certain value or node
     *
     * @param path to the value or node
     */
    public void removeHeader(String path) {
        this.setHeader(path, "");
    }

    /**
     * Clears all the headers displayed for this node and the values and
     * sub-nodes in this node
     */
    public void clearHeaders() {
        this._entry.setHeader("");
        for (YamlNode childNode : this.getNodes()) {
            childNode.clearHeaders();
        }
    }

    /**
     * Checks if a value is contained at the path specified
     *
     * @param path to check at
     * @return True if a value is contained, False if not
     */
    public boolean contains(String path) {
        return this.getEntryIfExists(path) != null;
    }

    /**
     * Gets all the keys of the values sorted in the order they exist in the YAML file
     *
     * @return key set
     */
    public Set<String> getKeys() {
        return this.getValues().keySet();
    }

    /**
     * Gets all the values mapped to the keys that are a descendant
     * of this node.
     *
     * @return map of the values
     */
    public Map<String, Object> getValues() {
        //TODO: Efficiency!
        HashMap<String, Object> values = new HashMap<String, Object>();
        for (YamlEntry entry : this._children) {
            values.put(entry.getPath().name(), entry.getValue());
        }
        return values;
    }

    /**
     * Gets all the values mapped to the keys of the type specified
     * that are a descendant of this node.
     *
     * @param type to convert to
     * @return map of the converted values
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getValues(Class<T> type) {
        //TODO: Use a functional map filter/transformer instead!
        Map<String, Object> values = this.getValues();
        Iterator<Map.Entry<String, Object>> iter = values.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            T newvalue = ParseUtil.convert(entry.getValue(), type);
            if (newvalue == null) {
                iter.remove();
            } else {
                entry.setValue(newvalue);
            }
        }
        return (Map<String, T>) values;
    }

    /**
     * Clears all values stored that are descendant of this node.
     * Nodes that were removed turn into a detached tree of their own.
     */
    public void clear() {
        Iterator<YamlEntry> iter = this._children.iterator();
        while (iter.hasNext()) {
            YamlEntry entry = iter.next();
            iter.remove();
            this.removeChildEntry(entry);
        }
    }

    /**
     * Gets all configuration nodes sorted by insertion order
     *
     * @return Set of configuration nodes
     */
    public Set<YamlNode> getNodes() {
        //TODO: Efficiency!
        LinkedHashSet<YamlNode> result = new LinkedHashSet<YamlNode>();
        for (YamlEntry child : this._children) {
            if (child.isNodeValue()) {
                result.add((YamlNode) child.getValue());
            }
        }
        return result;
    }

    /**
     * Checks if a node is contained at the path specified
     *
     * @param path to check at
     * @return True if it is a node, False if not
     */
    public boolean isNode(String path) {
        YamlEntry entryAtPath = this.getEntryIfExists(path);
        return entryAtPath != null && entryAtPath.isNodeValue();
    }

    /**
     * Gets the node at the path specified, creates one if not present
     *
     * @param path to get a node
     * @return the node
     */
    public YamlNode getNode(String path) {
        return this.getEntry(path).createNodeValue();
    }

    /**
     * Removes this node from the parent. If this node is not added
     * to any parent and is a root node, this method does nothing.
     * This node becomes a root node after removing and can be added
     * to other yaml trees or positions.
     */
    public void remove() {
        YamlNode parent = this._entry.getParentNode();
        if (parent != null) {
            parent.removeChildEntry(this._entry);
        }
    }

    /**
     * Sets a value at a certain path
     *
     * @param path to set
     * @param value to set to
     */
    public void set(String path, Object value) {
        this.getEntry(path).setValue(value);
    }

    @Override
    public String toString() {
        return this._entry.getYaml().toString();
    }

    protected YamlNode createChildNode(int index, boolean isListNode, YamlPath path) {
        return new YamlNode(createChildEntry(index, path), isListNode);
    }

    protected YamlEntry createChildEntry(int index, YamlPath path) {
        // If the parent path of the path is not equal to the path to this node,
        // we need to insert additional nodes in-between to get there
        // This works recursively. If we hit the root node, then the path is invalid.
        if (!path.parent().equals(this._entry.getPath())) {
            if (path.isRoot()) {
                throw new IllegalArgumentException("Path " + path + " is not a child of " + this._entry.getPath());
            } else {
                return createChildNode(index, path.isList(), path.parent()).createChildEntry(0, path);
            }
        }

        // Create a new child entry and add it to this node
        // TODO: What about lists?
        YamlEntry entry = new YamlEntry(this, path, this._entry.getYaml().insert(index));
        this._children.add(index, entry);
        this._root.putEntry(entry);
        return entry;
    }

    protected YamlEntry getEntryIfExists(String path) {
        return this._root.getEntryIfExists(this._entry.getPath(), path);
    }

    protected YamlEntry getEntry(String path) {
        return this._root.getEntry(this._entry.getPath(), path);
    }

    protected void removeChildEntry(YamlEntry entry) {
        if (entry.getParentNode() != this) {
            throw new IllegalArgumentException("The entry is not a child of this node");
        }

        // Detach as a child
        this._children.remove(entry);
        this._root.detach(entry);
    }
}
