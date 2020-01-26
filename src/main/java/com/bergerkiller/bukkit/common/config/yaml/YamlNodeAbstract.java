package com.bergerkiller.bukkit.common.config.yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.yaml.snakeyaml.error.YAMLException;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.io.AsyncTextWriter;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

/**
 * Abstract implementation for a Yaml node in a YAML tree.
 * Can be further implemented to create a custom yaml node type.
 * 
 * @param <N> - Type of YamlNodeAbstract implementation
 */
public abstract class YamlNodeAbstract<N extends YamlNodeAbstract<?>> implements Cloneable {
    protected YamlRoot _root;
    protected YamlEntry _entry;
    protected List<YamlEntry> _children;

    /**
     * Creates a new empty Yaml Root Node
     */
    public YamlNodeAbstract() {
        this(null);
    }

    /**
     * Creates a new child node for an entry, or a new root node
     * if the entry is null.
     * 
     * @param entry
     */
    protected YamlNodeAbstract(YamlEntry entry) {
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
    protected abstract N createNode(YamlEntry entry);

    /**
     * Checks if this node has a parent, or if it a root node
     *
     * @return True if it has a parent, False if not
     */
    public boolean hasParent() {
        return this.getYamlParent() != null;
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
    @SuppressWarnings("unchecked")
    public N getParent() {
        return (N) this._entry.getParentNode();
    }

    /**
     * Gets the parent YAML node of this Node.
     * This may be of a type different than {@link #getParent()} supports.
     * 
     * @return parent YAML node
     */
    public final YamlNodeAbstract<?> getYamlParent() {
        return this._entry.getParentNode();
    }

    /**
     * Gets the YamlPath that refers to this node
     * 
     * @return path
     */
    public YamlPath getYamlPath() {
        return this._entry.getYamlPath();
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
        for (YamlNodeAbstract<?> childNode : this.getNodes()) {
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
        return new YamlNodeKeySetProxy(this);
    }

    /**
     * Gets all the values mapped to the keys that are a descendant
     * of this node.
     *
     * @return map of the values
     */
    public Map<String, Object> getValues() {
        return new YamlNodeMapProxy(this);
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
     * Calls {@link #getValues()} and converts all values to the value type
     * specified. Values that cannot be converted are removed from the map.
     * Strings can be converted to numbers, among other things.
     *
     * @param valueType The value type to convert the original values to
     * @return map of the converted values
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getValues(Class<T> valueType) {
        Map<String, Object> values = this.getValues();
        YamlNodeValueCollectionProxy.ValueIterator iter = new YamlNodeValueCollectionProxy.ValueIterator(this);
        while (iter.hasNext()) {
            Object oldValue = iter.next();
            if (oldValue != null) {
                T convertedValue = Conversion.convert(oldValue, valueType, null);
                if (convertedValue == null) {
                    iter.remove();
                } else if (convertedValue != oldValue) {
                    iter.set(convertedValue);
                }
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
     * Gets all children of this node that are nodes themselves,
     * sorted by insertion order. Changes to the returned set do not
     * cause modifications to this node.
     *
     * @return Set of child nodes
     */
    @SuppressWarnings("unchecked")
    public Set<N> getNodes() {
        LinkedHashSet<N> result = new LinkedHashSet<N>(this._children.size());
        for (YamlEntry child : this._children) {
            if (child.isNode()) {
                result.add((N) child.getValue());
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
        if (entryAtPath != null) {
            Object value = entryAtPath.getValue();
            if (value instanceof YamlNodeAbstract && !(value instanceof YamlListNode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the node at the path specified, creates one if not present
     *
     * @param path to get a node
     * @return the node
     */
    @SuppressWarnings("unchecked")
    public N getNode(String path) {
        return (N) this.getEntry(path).createNodeValue();
    }

    /**
     * Stores a list of nodes at the path specified, creates one if not present.<br>
     * <br>
     * If at the path a node is stored rather than a list, then the nodes are set
     * as children of this node numbered 0 incrementing.<br>
     * <br>
     * <b>Legacy: this will always produce a node with index children rather than a
     * list of nodes. This will change in the near future.</b>
     * 
     * @param path
     * @param nodes List of nodes to store
     */
    public void setNodeList(String path, List<N> nodes) {
        // Make sure that if the value did not exist, a node is created (not a list node)
        // In newer versions we may allow the set() logic to create a normal list node instead
        // See the above legacy bolded warning
        YamlEntry entry = this.getEntry(path);
        if (!entry.isAbstractNode()) {
            entry.createNodeValue();
        }
        entry.setValue(nodes);
    }

    /**
     * Gets a list of nodes at the path specified, creates one if not present.<br>
     * <br>
     * If at the path a node is stored rather than a list, then the children of
     * that node are represented as a List instead, sorted by key names.
     * No changes will occur to the node structure of the value at the path
     * while reading these values. When a new value is added to this list,
     * then the original key names will be replaced with an index incrementing
     * starting at 1.<br>
     * <br>
     * If nothing is yet stored at this path, then a new list is created the first time
     * a node is added to the list or when {@link #setNodeList(String, List)} is called.
     * The returned list will be empty in that case.<br>
     * <br>
     * <b>Legacy: this will always produce a node with index children rather than a
     * list of nodes. This will change in the near future.</b>
     * 
     * @param path
     * @return list of configuration nodes
     */
    @SuppressWarnings("unchecked")
    public List<N> getNodeList(String path) {
        // Obtain the list, filter non-node values from it
        List<?> list = this.getList(path, true);
        for (int i = 0; i < list.size(); i++) {
            Object value = list.get(i);
            if (!(value instanceof YamlNodeAbstract) || value instanceof YamlListNode) {
                list.remove(i--);
            }
        }
        return (List<N>) list;
    }

    /**
     * Gets a list of values at the path specified. If nothing is stored at the path,
     * an empty list is returned and no list is created. When an element is added to this
     * empty list for the first time, then the list is created and filled with that element.<br>
     * <br>
     * If at the path a node is stored rather than a list, then the children of
     * that node are represented as a List instead, sorted by key names.
     * No changes will occur to the node structure of the value at the path
     * while reading these values. When a new value is added to this list,
     * then the original key names will be replaced with an index incrementing
     * starting at 0.
     * 
     * @param path The path to get a list
     * @return The modifiable list at the path. Changes to the list result in updates to this tree.
     */
    public List<Object> getList(String path) {
        return getList(path, false);
    }

    // Helper function adding createIndexed option
    private List<Object> getList(String path, boolean createIndexed) {
        YamlEntry entry = this.getEntryIfExists(path);
        if (entry != null) {
            return entry.createList();
        } else {
            return new YamlNodeLazyCreateValueList(this, path, createIndexed);
        }
    }

    /**
     * Gets the value at the path as a List of a given type, creates one if not present.
     * Values in the list that cannot be converted to the type are removed.<br>
     * <br>
     * If at the path a node is stored rather than a list, then the children of
     * that node are represented as a List instead, sorted by key names.
     * No changes will occur to the node structure of the value at the path
     * while reading these values. When a new value is added to this list,
     * then the original key names will be replaced with an index incrementing
     * starting at 1.
     *
     * @param path       The path to get a list
     * @param valueType  The type of values the list should contain
     * @return the list storing values of valueType, is modifiable
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String path, Class<T> valueType) {
        List<Object> list = this.getList(path);
        for (int i = 0; i < list.size(); i++) {
            Object oldValue = list.get(i);
            if (oldValue != null) {
                T convertedValue = Conversion.convert(oldValue, valueType, null);
                if (convertedValue == null) {
                    list.remove(i--);
                } else if (convertedValue != oldValue) {
                    list.set(i, convertedValue);
                }
            }
        }
        return (List<T>) list;
    }

    /**
     * Gets the value at the path as a List of a given type, creates one if not present
     * using the initial values specified in def. Values in the list that cannot be
     * converted to the type are removed.<br>
     * <br>
     * If at the path a node is stored rather than a list, then the children of
     * that node are represented as a List instead, sorted by key names.
     * No changes will occur to the node structure of the value at the path
     * while reading these values. When a new value is added to this list,
     * then the original key names will be replaced with an index incrementing
     * starting at 1.
     *
     * @param path       The path to get a list
     * @param valueType  The type of values the list should contain
     * @param def        The default values to store if the list does not exist
     * @return the list storing values of valueType, is modifiable
     */
    public <T> List<T> getList(String path, Class<T> type, List<T> def) {
        if (!this.contains(path)) {
            this.set(path, def);
        }
        return this.getList(path, type);
    }

    /**
     * Gets the raw value at the path specified.
     * Returns null if nothing is stored at the path.
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
        return this.get(path, type, null);
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
        Object value = this.get(path);
        if (type == String.class && value instanceof String[]) {
            // Special conversion to line-by-line String
            // This is needed, as it saves line-split Strings as such
            return (T) StringUtil.join("\n", (String[]) value);
        }
        T rval = ParseUtil.convert(value, type, null);
        if (rval == null) {
            rval = def;
            this.set(path, rval);
        }
        return rval;
    }

    /**
     * Sets a value at a certain path<br>
     * <br>
     * If the value is a node, it will be parented to this node's tree. Changes to the node
     * then impact this tree. If the node is already parented, it is cloned and later
     * changes to the node will not impact this tree.
     *
     * @param path to set
     * @param value to set to
     */
    public void set(String path, Object value) {
        this.getEntry(path).setValue(value);
    }

    /**
     * Sets a value at a certain path, provided no previous value exists<br>
     * <br>
     * If the value is a node, it will be parented to this node's tree. Changes to the node
     * then impact this tree. If the node is already parented, it is cloned and later
     * changes to the node will not impact this tree.
     * 
     * @param path to set
     * @param value to set to
     * @return True if the value was stored, False if a previous value existed
     */
    public boolean setIfAbsent(String path, Object value) {
        YamlEntry entry = this.createEntryIfAbsent(path);
        if (entry != null) {
            entry.setValue(value);
            return true;
        } else {
            return false;
        }
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
        YamlNodeAbstract<?> parent = this._entry.getParentNode();
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
     * @param target  The node to share the value with
     * @param path    The relative path to the value to share
     * @param def     The default value to store if no value was found
     */
    public void shareWith(N target, String path, Object def) {
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
        loadFromReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
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
        saveToWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
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
     * Encodes this YamlNode to YAML-formatted text and writes it to a file asynchronously.
     * The returned completable future is completed when the operation finishes, or when
     * an error occurs. If writing fails and a previous file existed at this path, it will
     * be unchanged. To wait for saving to complete, use {@link CompletableFuture#get()}.
     * 
     * @param file  The file to write to
     * @return completable future for the asynchronous operation
     */
    public CompletableFuture<Void> saveToFileAsync(File file) {
        return AsyncTextWriter.writeSafe(file, this.toCharBuffer());
    }

    /**
     * Creates an exact clone of this node, where this node is the root of the YAML tree.
     * The clone will have the same data type as this node.
     */
    @Override
    public N clone() {
        N clone = this.createNode(null);
        clone._entry.assignProperties(this._entry);
        this.cloneChildrenTo(clone);
        return clone;
    }

    /**
     * Clones all the child entries of this node and assigns them to the node specified.
     * This operation is recursive.
     * 
     * @param clone The clone to assign the clones entries to
     */
    private void cloneChildrenTo(YamlNodeAbstract<?> clone) {
        for (YamlEntry child : this._children) {
            YamlPath childPath = clone.getYamlPath().child(child.getYamlPath().name());
            YamlEntry childClone = clone.createChildEntry(clone._children.size(), childPath);
            childClone.assignProperties(child);

            if (child.isAbstractNode()) {
                YamlNodeAbstract<?> originalChildNode = child.getAbstractNode();
                YamlNodeAbstract<?> childCloneNode;
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
     * Replaces a child entry of this node with a clone, reusing the original entry's YAML
     * buffer and properties. For entries that are nodes, they are given a new node instance.
     * The original entries should not be used anymore. This operation is recursive.
     * 
     * @param index of the child to clone
     * @return cloned child entry
     */
    protected YamlEntry cloneChildEntry(int index) {
        YamlEntry oldEntry = this._children.get(index);
        YamlEntry newEntry = new YamlEntry(this, oldEntry.getYamlPath(), oldEntry.yaml);
        newEntry.assignProperties(oldEntry);
        newEntry.yaml_check_children = oldEntry.yaml_check_children;
        newEntry.yaml_needs_generating = oldEntry.yaml_needs_generating;
        this._children.set(index, newEntry);
        this._root.putEntry(newEntry);
        if (oldEntry.isAbstractNode()) {
            YamlNodeAbstract<?> oldNode = oldEntry.getAbstractNode();
            YamlNodeAbstract<?> newNode = oldNode.createNode(newEntry);
            newNode._children.addAll(oldNode._children);
            for (int i = 0; i < newNode._children.size(); i++) {
                newNode.cloneChildEntry(i);
            }
            newEntry.value = newNode;
        } else {
            newEntry.value = oldEntry.value;
        }
        return newEntry;
    }

    /**
     * Gets the YAML of this YamlNode and its descendants in a CharBuffer. If this is the root node, it contains
     * the full YAML document. If it is not, then it contains the YAML of the node's
     * descendants.
     * 
     * @return YAML stored in CharBuffer
     */
    public CharBuffer toCharBuffer() {
        if (this.getYamlPath().depth() == 0) {
            // Root node: we can include the entire yaml String
            return this._entry.getYaml().toCharBuffer();
        } else {
            // Fallback requires a StringBuilder, so just call toString()
            return CharBuffer.wrap(this.toString());
        }
    }

    /**
     * Gets the YAML of this YamlNode and its descendants in a String. If this is the root node, it contains
     * the full YAML document. If it is not, then it contains the YAML of the node's
     * descendants.
     * 
     * @return YAML stored in String
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
        if (!path.parent().equals(this._entry.getYamlPath())) {
            throw new IllegalArgumentException("Path " + path + " is not a child of " + this._entry.getYamlPath());
        }

        // When children were empty, then YAML writes a {} or []
        // Make sure YAML is regenerated so that this is removed again
        if (this._children.isEmpty()) {
            this._entry.markYamlChanged();
        }

        // Create a new child entry and add it to this node
        YamlEntry entry = new YamlEntry(this, path, this._entry.yaml.insert(index));
        this._children.add(index, entry);
        this._root.putEntry(entry);
        return entry;
    }

    protected int indexOfKey(Object key) {
        if (key != null && !this._children.isEmpty()) {
            String keyStr = key.toString();
            for (int i = 0; i < this._children.size(); i++) {
                if (this._children.get(i).getKey().equals(keyStr)) {
                    return i;
                }
            }
        }
        return -1;
    }

    protected int indexOfValue(Object value) {
        if (value == null) {
            for (int i = 0; i < this._children.size(); i++) {
                if (this._children.get(i).getValue() == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < this._children.size(); i++) {
                if (value.equals(this._children.get(i).getValue())) {
                    return i;
                }
            }
        }
        return -1;
    }

    protected int lastIndexOfValue(Object value) {
        if (value == null) {
            for (int i = this._children.size()-1; i >= 0; i--) {
                if (this._children.get(i).getValue() == null) {
                    return i;
                }
            }
        } else {
            for (int i = this._children.size()-1; i >= 0; i--) {
                if (value.equals(this._children.get(i).getValue())) {
                    return i;
                }
            }
        }
        return -1;
    }

    protected YamlEntry createEntryIfAbsent(String path) {
        return this._root.createEntryIfAbsent(this._entry.getYamlPath(), path);
    }

    protected YamlEntry getEntryIfExists(String path) {
        return this._root.getEntryIfExists(this._entry.getYamlPath(), path);
    }

    protected YamlEntry getEntry(String path) {
        return this._root.getEntry(this._entry.getYamlPath(), path);
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
