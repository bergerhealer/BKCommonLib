package com.bergerkiller.bukkit.common.config.yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.error.YAMLException;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

/**
 * A node in the YAML tree
 */
public class YamlNode {
    protected YamlRoot _root;
    protected YamlEntry _entry;
    protected List<YamlEntry> _children;

    /**
     * Creates a new empty Yaml Root Node
     */
    public YamlNode() {
        this(null);
    }

    /**
     * Creates a new child node for an entry, or a new root node
     * if the entry is null.
     * 
     * @param entry
     */
    protected YamlNode(YamlEntry entry) {
        this._children = new ArrayList<YamlEntry>(10);
        if (entry == null) {
            this._root = new YamlRoot();
            this._entry = new YamlEntry(this);
            this._root.putEntry(this._entry);
        } else {
            this._root = entry.getParentNode()._root;
            this._entry = entry;
        }
    }

    /**
     * Creates a new YamlNode instance used by an entry.
     * Can be overridden to create custom YamlNode implementations.
     * 
     * @param entry to which the node is bound, null to create a new root node
     * @return new node
     */
    protected YamlNode createNode(YamlEntry entry) {
        return new YamlNode(entry);
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
     * Gets the {@link YamlPath} to this node as a String
     * 
     * @return path
     */
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
     * Sets all the values mapped to the keys that are a descendant
     * of this node. Values in the map that are maps or collections are turned
     * into deeper nested nodes.
     * 
     * @param values to set
     */
    public void setValues(Map<?, ?> values) {
        this.clear();
        for (Map.Entry<?, ?> entry : values.entrySet()) {
            this.set(entry.getKey().toString(), entry.getValue());
        }
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
        for (int i = this._children.size()-1; i >= 0; --i) {
            this.removeChildEntryAt(i);
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
     * Gets a list of values at the path specified, creates one if not present.
     * If originally a normal node is stored, then that node is turned into a list
     * storing the values sorted by key.
     * 
     * @param path to get a list
     * @return the list
     */
    public List<Object> getList(String path) {
        return this.getEntry(path).createListNodeValue();
    }

    /**
     * Gets the raw value at the path specified
     *
     * @param path The path to the value to get
     * @return the raw value
     */
    public Object get(String path) {
        YamlEntry entry = this.getEntryIfExists(path);
        return (entry == null) ? null : entry.getValue();
    }

    /**
     * Gets the raw value at the path as the type specified
     *
     * @param path The path to the value to get
     * @param type of value to get
     * @return the converted value, or null if not found or of the wrong type
     */
    public <T> T get(String path, Class<T> type) {
        return ParseUtil.convert(this.get(path), type, null);
    }

    /**
     * Gets the value stored at the path as the type specified.
     * If the value is not stored or could not be converted to the type,
     * then the default value is set and returned instead.<br>
     * <br>
     * <b>The def value is used to get the type, it can not be null!</b>
     *
     * @param path   The path to the value to get
     * @param def    The value to return and store on failure, defines the type of value to return
     * @return The converted value, or the default value if not found or of the wrong type
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String path, T def) {
        return this.get(path, (Class<T>) def.getClass(), def);
    }

    /**
     * Gets the raw value at the path as the type specified.
     * If the value is not stored or could not be converted to the type,
     * then the default value is set and returned instead.
     *
     * @param path    The path to the value to get
     * @param type    The type of value to convert the stored value to
     * @param def     The value to return and store on failure
     * @return The converted value, or the default value if not found or of the wrong type
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String path, Class<T> type, T def) {
        YamlEntry entry = this.getEntry(path);
        Object value = entry.getValue();
        if (type == String.class && value instanceof String[]) {
            // Special conversion to line-by-line String
            // This is needed, as it saves line-split Strings as such
            return (T) StringUtil.join("\n", (String[]) value);
        }
        T rval = ParseUtil.convert(value, type, null);
        if (rval == null) {
            rval = def;
            entry.setValue(rval);
        }
        return rval;
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

    /**
     * Removes the value at the path specified
     *
     * @param path to remove at
     */
    public void remove(String path) {
        YamlEntry entry = this.getEntryIfExists(path);
        if (entry != null && entry.getParentNode() != null) {
            entry.getParentNode().removeChildEntry(entry);
        }
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
     * Shares a single value with a target node:<br>
     * - Writes the value from this node to the target if possible<br>
     * - Writes the value from the target to this node alternatively<br>
     * - If no value was found at all, both the target and this node get the
     * default value
     *
     * @param target  The YamlNode to share the value with
     * @param path    The relative path to the value to share
     * @param def     The default value to store if no value was found
     */
    public void shareWith(YamlNode target, String path, Object def) {
        YamlEntry entry_a = this.getEntry(path);
        YamlEntry entry_b = target.getEntry(path);
        if (entry_a.getValue() != null) {
            entry_b.setValue(entry_a.getValue());
        } else if (entry_b.getValue() != null) {
            entry_a.setValue(entry_b.getValue());
        } else {
            entry_a.setValue(def);
            entry_b.setValue(def);
        }
    }

    /**
     * Shares a single value with a target key-value map:<br>
     * - Writes the value from this node to the target map if possible<br>
     * - Writes the value from the target map to this node alternatively<br>
     * - If no value was found at all, both the target map and this node get the
     * default value
     *
     * @param target  The map of key-value pairs to share the value with
     * @param path    The relative path to the value to share
     * @param def     The default value to store if no value was found
     */
    public void shareWithMap(Map<String, Object> target, String path, Object def) {
        YamlEntry entry_a = this.getEntry(path);
        if (entry_a.getValue() != null) {
            target.put(path, entry_a.getValue());
        } else {
            Object value = target.get(path);
            if (value == null) {
                value = def;
                target.put(path, def);
            }
            entry_a.setValue(value);
        }
    }

    /**
     * Loads the YAML-encoded text from an input stream and fills this node with this information.
     * All original node information will be lost.
     * Multiple indentation formats are supported and automatically detected.
     * The platform's default character encoding is used to decode the stream.
     * The stream is automatically closed, also when errors occur.
     * 
     * @param stream to read from
     * @throws YAMLException When the YAML-encoded text is malformed or an IO Exception occurs
     */
    public void loadFromStream(InputStream stream) throws YAMLException {
        loadFromReader(new InputStreamReader(stream));
    }

    /**
     * Loads the YAML-encoded text from a reader and fills this node with this information.
     * All original node information will be lost.
     * Multiple indentation formats are supported and automatically detected.
     * The stream is automatically closed, also when errors occur.
     * 
     * @param reader to load from
     * @throws YAMLException When the YAML-encoded text is malformed or an IO Exception occurs
     */
    public void loadFromReader(Reader reader) throws YAMLException {
        loadDeserializerOutput(YamlDeserializer.INSTANCE.deserialize(reader));
    }

    /**
     * Loads the YAML-encoded text from a String and fills this node with this information.
     * All original node information will be lost.
     * Multiple indentation formats are supported and automatically detected.
     * 
     * @param yamlString YAML-encoded text to decode and load
     * @throws YAMLException When the YAML-encoded text is malformed
     */
    public void loadFromString(String yamlString) throws YAMLException {
        loadDeserializerOutput(YamlDeserializer.INSTANCE.deserialize(yamlString));
    }

    /**
     * Loads the output of the {@link YamlDeserializer} into this node
     * 
     * @param output
     */
    public void loadDeserializerOutput(YamlDeserializer.Output output) {
        this.setValues(output.root);
        for (Map.Entry<YamlPath, String> header : output.headers.entrySet()) {
            YamlEntry entry = this._root.getEntryIfExists(this.getYamlPath().child(header.getKey()));
            if (entry != null) {
                entry.setHeader(header.getValue());
            }
        }
    }

    /**
     * Encodes this YamlNode to YAML-formatted text and writes it to an
     * output stream.
     * The platform's default character encoding is used to encode the text.
     * The stream is automatically closed, also when errors occur.
     * 
     * @param stream to write to
     * @throws IOException When the stream throws an error while writing
     */
    public void saveToStream(OutputStream stream) throws IOException {
        saveToWriter(new OutputStreamWriter(stream));
    }

    /**
     * Encodes this YamlNode to YAML-formatted text and writes it to a writer.
     * The stream is automatically closed, also when errors occur.
     * 
     * @param writer to write to
     * @throws IOException When the writer throws an error while writing
     */
    public void saveToWriter(Writer writer) throws IOException {
        try {
            writer.write(this.toString());
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {}
        }
    }

    /**
     * Creates an exact clone of this node, where this node is the root of the YAML tree.
     * The clone will have the same data type as this node.
     */
    @Override
    public YamlNode clone() {
        YamlNode clone = this.createNode(null);
        clone._entry.assignProperties(this._entry);
        this.cloneChildrenTo(clone);
        return clone;
    }

    private void cloneChildrenTo(YamlNode clone) {
        for (YamlEntry child : this._children) {
            YamlPath childPath = clone.getYamlPath().child(child.getPath().name());
            YamlEntry childClone = clone.createChildEntry(clone._children.size(), childPath);
            childClone.assignProperties(child);

            if (child.isNodeValue()) {
                YamlNode originalChildNode = child.getNodeValue();
                YamlNode childCloneNode;
                if (originalChildNode instanceof YamlListNode) {
                    childCloneNode = childClone.createListNodeValue();
                } else {
                    childCloneNode = childClone.createNodeValue();
                }
                originalChildNode.cloneChildrenTo(childCloneNode);
            } else {
                childClone.value = child.value;
            }
        }
    }

    /**
     * Gets the YAML of this YamlNode and its descendants. If this is the root node, it contains
     * the full YAML document as a String. If it is not, then it contains the YAML of the node's
     * descendants. This node's key and header are omitted.
     * 
     * @return YAML String
     */
    @Override
    public String toString() {
        if (this.getYamlPath().depth() == 0) {
            // Root node: we can include the entire yaml String
            return this._entry.getYaml().toString();
        } else {
            // Descendant of root: only include the yaml of the children
            // Omit indentation at this depth level
            StringBuilder yaml = new StringBuilder();
            int indent = 2 * this.getYamlPath().depth();
            for (YamlEntry child : this._children) {
                String childYaml = child.getYaml().toString();
                int lineStart = 0;
                for (int i = 0; i < childYaml.length(); i++) {
                    char c = childYaml.charAt(i);
                    if (c == '\n') {
                        yaml.append(c);
                        lineStart = i + 1;
                    } else if ((i-lineStart) >= indent) {
                        yaml.append(c);
                    }
                }
            }
            return yaml.toString();
        }
    }

    protected YamlEntry createChildEntry(int index, YamlPath path) {
        // We can only insert children when the path is relative to this node
        // Create the parent entries first when path has multiple depths
        if (!path.parent().equals(this._entry.getPath())) {
            throw new IllegalArgumentException("Path " + path + " is not a child of " + this._entry.getPath());
        }

        // When children were empty, then YAML writes a {} or []
        // Make sure YAML is regenerated so that this is removed again
        if (this._children.isEmpty()) {
            this._entry.markYamlChanged();
        }

        // Create a new child entry and add it to this node
        // TODO: What about lists?
        YamlEntry entry = new YamlEntry(this, path, this._entry.yaml.insert(index));
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
        int index = this._children.indexOf(entry);
        if (index != -1) {
            removeChildEntryAt(index);
        } else {
            throw new IllegalArgumentException("The entry is not a child of this node");
        }
    }

    protected YamlEntry removeChildEntryAt(int index) {
        if (index < 0 || index >= this._children.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds");
        }

        // Detach as a child
        YamlEntry entry = this._children.remove(index);
        this._root.detach(entry);

        // When children are empty, it now writes [] or {} as YAML
        // Make sure YAML is regenerated so this is done
        if (this._children.isEmpty()) {
            this._entry.markYamlChanged();
        }

        // May be useful?
        return entry;
    }

}
