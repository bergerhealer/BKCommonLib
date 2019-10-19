package com.bergerkiller.bukkit.common.config.yaml;

import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Map;

import com.bergerkiller.bukkit.common.collections.StringTreeNode;

/**
 * A single entry inside the YAML document.
 * Stores the name, value, header and cached serialized YAML String.
 * Automatically regenerates the yaml when required.
 */
public class YamlEntry implements Map.Entry<String, Object> {
    private final YamlNodeAbstract<?> parent;
    private YamlPath path;
    protected final StringTreeNode yaml;
    protected boolean yaml_needs_generating;
    protected boolean yaml_check_children;
    private String header;
    protected Object value;

    // Root node only
    protected YamlEntry(YamlNodeAbstract<?> rootNode) {
        this.parent = null;
        this.path = YamlPath.ROOT;
        this.header = "";
        this.value = rootNode;
        this.yaml = new StringTreeNode();
        this.yaml_needs_generating = true;
        this.yaml_check_children = false;
    }

    // Constructor used inside YamlNode to create new entries
    protected YamlEntry(YamlNodeAbstract<?> parent, YamlPath path, StringTreeNode yaml) {
        this.parent = parent;
        this.path = path;
        this.header = "";
        this.value = null;
        this.yaml = yaml;
        this.yaml_needs_generating = false;
        this.yaml_check_children = false;
        this.markYamlChanged(); // Updates check_children of parent
    }

    /**
     * Gets the path to which this entry's value is bound
     * 
     * @return path
     */
    public YamlPath getYamlPath() {
        return this.path;
    }

    /**
     * Sets the path to which this entry's value is bound. Updates it
     * for this entry, and if this is a node, all its child entries.
     * 
     * @param path
     */
    public void setPath(YamlPath path) {
        if (this.parent != null) {
            setPath(this.parent._root, path);
        } else if (this.isAbstractNode()) {
            setPath(this.getAbstractNode()._root, path);
        } else {
            // Not bound to any tree, just update the path variable
            this.path = path;
        }
    }

    private void setPath(YamlRoot root, YamlPath path) {
        root.updateEntryPath(this, path);
        this.path = path;
        if (this.isAbstractNode()) {
            for (YamlEntry childEntry : this.getAbstractNode()._children) {
                childEntry.setPath(root, path.child(childEntry.path.name()));
            }
        }
    }

    /**
     * Gets the parent yaml node of which this entry is a child
     * 
     * @return parent node
     */
    public YamlNodeAbstract<?> getParentNode() {
        return this.parent;
    }

    /**
     * Gets the YAML of this entry and its children
     * 
     * @return yaml
     */
    public StringTreeNode getYaml() {
        this.generateYaml();
        return this.yaml;
    }

    /**
     * Returns this value as a YamlNode. If the value is not a
     * node, the original value is discarded and a new node is created.
     * If the original node is a list node and then the node type is changed.
     * 
     * @return YamlNode
     */
    public YamlNodeAbstract<?> createNodeValue() {
        // Check if original value is already a node
        if (this.value instanceof YamlNodeAbstract<?> && !(this.value instanceof YamlListNode)) {
            return (YamlNodeAbstract<?>) this.value;
        }

        // Find a YamlNode parent that is not a YamlNodeList
        // We ask this parent to create a new node value
        // This cannot be a 'list' type because that one cannot be extended
        YamlNodeAbstract<?> closeParent = this.parent;
        while (closeParent instanceof YamlListNode) {
            closeParent = closeParent.getParent();
        }

        // Create a new node, ask YamlNode parent or previous value to create it
        // This makes sure custom YamlNodeAbstract implementations can work
        YamlNodeAbstract<?> newNode;
        if (closeParent != null) {
            newNode = closeParent.createNode(this);
        } else {
            newNode = new YamlNode(this);
        }

        // Discards the old value and assigns a new node
        this.setValue(newNode);
        return newNode;
    }

    /**
     * Returns this value as a YamlNode. If the value is not a
     * list node, the original value is discarded and a new node is created.
     * If the original node is a normal node then the original keys are sorted
     * and values added to a list in that order.
     * 
     * @return YamlNode
     */
    public YamlListNode createListNodeValue() {
        // Check if original value is already a node
        if (this.value instanceof YamlListNode) {
            return (YamlListNode) this.value;
        }

        // Discards the old value and creates a new node
        YamlListNode newNode = new YamlListNode(this);
        this.setValue(newNode);
        return newNode;
    }

    /**
     * Gets whether this entry stores a YamlNodeAbstract or YamlListNode value.
     * This only indicates the value is an abstract node, it does not mean it
     * can be cast to the custom implementation type of the node.
     * 
     * @return True if the value is a YamlNode
     */
    public boolean isAbstractNode() {
        return this.value instanceof YamlNodeAbstract;
    }

    /**
     * Gets whether this entry stores a node. Lists are excluded.
     * 
     * @return True if this is a node
     */
    public boolean isNode() {
        return this.value instanceof YamlNodeAbstract && !(this.value instanceof YamlListNode);
    }

    /**
     * Gets this entry's value as a YamlNode, which can also be
     * a YamlListNode.
     * 
     * @return node value
     */
    public YamlNodeAbstract<?> getAbstractNode() {
        return (YamlNodeAbstract<?>) this.value;
    }

    /**
     * Gets the key of this entry relative to the parent, which will
     * be the name of the path.
     */
    @Override
    public String getKey() {
        return this.path.name();
    }

    /**
     * Gets the current value of this entry
     * 
     * @return value
     */
    @Override
    public Object getValue() {
        return this.value;
    }

    /**
     * Sets a new value for this entry
     * 
     * @param value to set to
     */
    @Override
    public Object setValue(Object value) {
        Object oldValue = this.value;
        if (oldValue == value) {
            return oldValue;
        }
        if (oldValue != null && oldValue.equals(value)) {
            this.value = value;
            return oldValue;
        }

        // Turn a Collection (or List) value into a YamlListNode
        if (value instanceof Collection && !(value instanceof YamlNodeAbstract<?>)) {
            YamlListNode listNode = this.createListNodeValue();
            listNode.clear();
            listNode.addAll((Collection<?>) value);
            return oldValue;
        }

        // Turn a Map into a YamlNode
        if (value instanceof Map && !(value instanceof YamlNodeAbstract<?>)) {
            YamlNodeAbstract<?> node = this.createNodeValue();
            node.clear();
            node.setValues((Map<?, ?>) value);
            return oldValue;
        }

        YamlNodeAbstract<?> newNode;
        if (value instanceof YamlNodeAbstract<?> && ((newNode = (YamlNodeAbstract<?>) value)._entry != this)) {
            // Assigning to the root node doesn't work very well
            if (this.parent == null) {
                throw new IllegalArgumentException("Cannot assign a new node to a root node");
            }

            // If the node is already assigned to another parent, then replace that node in the parent
            // with a new instance. The node becomes parented to this one's parent, with the original
            // parent getting a clone of the original node assigned.
            if (newNode.hasParent()) {
                int index = newNode.getParent()._children.indexOf(newNode._entry);
                if (index == -1) {
                    // Fallback
                    newNode = newNode.clone();
                    value = newNode;
                } else {
                    // Replace original entry with a clone for the original parent
                    newNode.getParent().cloneChildEntry(index);
                }
            }

            // Clean up old value
            removeNodeValue();

            // Take over certain properties of the new node
            this.assignProperties(newNode._entry);

            // Re-assign the node's entry and root to refer to ourself
            newNode._entry = this;
            newNode._root = this.parent._root;

            // Move all children from this node to the new root as a descendant of the parent
            if (!newNode._children.isEmpty()) {
                ListIterator<YamlEntry> iter = newNode._children.listIterator();
                while (iter.hasNext()) {
                    YamlEntry childEntry = iter.next();
                    YamlPath newChildPath = this.path.child(childEntry.path.name());
                    StringTreeNode newChildYaml = this.yaml.add();
                    iter.set(childEntry.copyToParent(newNode, newNode._root, newChildPath, newChildYaml));
                }
            }
        } else {
            // Clean up old value
            removeNodeValue();
        }

        // Assign the new value and schedule YAML for rebuilding
        this.value = value;
        this.markYamlChanged();
        return oldValue;
    }

    // If this entry stores a node as value, detach that node from the tree
    // It will become a detached root node with a new YamlEntry.
    // This entry is not removed or detached and remains functional to store other values
    private void removeNodeValue() {
        if (this.isAbstractNode()) {
            YamlNodeAbstract<?> node = this.getAbstractNode();
            node._root.removeChildEntries(node);
            this.copyToParent(null, new YamlRoot(), YamlPath.ROOT, new StringTreeNode());
        }
    }

    /**
     * Creates a copy of this entry and all child entries and assigns it to a new parent.
     * The original YamlNodeAbstract instances are preserved.
     * All nodes and values get a new entry with the updated path and yaml.<br>
     * <br>
     * <b>Note: </b>this entry is not removed from the original root and parent, so the entry can be repurposed
     * to store different data. Call {@link YamlRoot#removeEntry(entry)} to remove this entry before
     * calling this function if this is desired, or use {@link YamlRoot#detach(entry)}.
     * 
     * @param newParent  The new parent node for the entry, null if it is a root node
     * @param newRoot    The new root to store the entry in
     * @param newPath    The new (root) path of the entry
     * @param newYaml    The new yaml String tree node for the entry
     * @return The new entry. It is up to the caller to assign the entry to the new parent node's children.
     */
    protected YamlEntry copyToParent(YamlNodeAbstract<?> newParent, YamlRoot newRoot, YamlPath newPath, StringTreeNode newYaml) {
        // Create the replacement entry, which is at a new path
        // Preserve certain properties from the original entry, such as the header
        YamlEntry newEntry = new YamlEntry(newParent, newPath, newYaml);
        newEntry.assignProperties(this);
        newEntry.value = this.value;
        newRoot.putEntry(newEntry);

        // Special handling for nodes
        if (this.isAbstractNode()) {
            // Store the new entry in the node
            YamlNodeAbstract<?> node = this.getAbstractNode();
            node._root = newRoot;
            node._entry = newEntry;

            // Recursively operate on the children of the node
            ListIterator<YamlEntry> iter = node._children.listIterator();
            while (iter.hasNext()) {
                YamlEntry childEntry = iter.next();
                YamlPath newChildPath = newPath.child(childEntry.getKey());
                StringTreeNode newChildYaml = newYaml.add();
                iter.set(childEntry.copyToParent(node, newRoot, newChildPath, newChildYaml));
            }
        }

        return newEntry;
    }

    /**
     * Assigns properties such as header from another entry.
     * The value is not copied as it requires special handling of node values.
     * 
     * @param entry to assign the properties of to this entry
     */
    protected void assignProperties(YamlEntry entry) {
        this.header = entry.header;
    }

    /**
     * Gets the multi-line header put in front of the YAML entry.
     * If this is an empty String, then there is no header.
     * 
     * @return header
     */
    public String getHeader() {
        return this.header;
    }

    /**
     * Sets a new header to be put in front of the YAML entry.
     * An empty String will omit the header.
     * 
     * @param header to set to, an empty String to omit it
     */
    public void setHeader(String header) {
        this.header = header;
        this.markYamlChanged();
    }

    /**
     * Appends a new line to the header of this YAML entry.
     * If the current header is empty, the header is appended
     * without a newline.
     * 
     * @param header to append
     */
    public void addHeader(String header) {
        if (this.header.isEmpty()) {
            this.header = header;
        } else {
            this.header += "\n" + header;
        }
        this.markYamlChanged();
    }

    /**
     * Requests the YAML for this entry to be regenerated the next time
     * {@link #getYaml()} is called.
     */
    public void markYamlChanged() {
        if (!this.yaml_needs_generating) {
            this.yaml_needs_generating = true;

            YamlNodeAbstract<?> node = this.parent;
            while (node != null && !node._entry.yaml_check_children) {
                node._entry.yaml_check_children = true;
                node = node.getParent();
            }
        }
    }

    /**
     * Serializes a value to a YAML-formatted String using all the YAML formatting rules for values.
     * Also checks whether an additional - needs to be prefixed because it is the first
     * item of a list. This is done here because then these characters will align on the same line.
     * 
     * @param value
     * @return YAML formatted String
     */
    private String serializeYamlValue(Object value) {
        // When value is the first item of a parent list, we want to prefix it with an (additional) -
        // This is done by wrapping the value into a list and adjusting indent
        int indent = this.path.depth();
        YamlEntry entry = this;
        while (entry != null
               && entry.parent._children.get(0) == entry
               && entry.parent.getParent() instanceof YamlListNode)
        {
            value = Collections.singletonList(value);
            indent--;
            entry = entry.parent._entry;
        }

        // Serialize the value to a String
        return YamlSerializer.INSTANCE.serialize(value, this.header, indent);
    }

    private void generateYaml() {
        // Call generateYaml() on the children
        if (this.yaml_check_children) {
            this.yaml_check_children = false;
            if (this.isAbstractNode()) {
                for (YamlEntry entry : this.getAbstractNode()._children) {
                    entry.generateYaml();
                }
            }
        }

        // Regenerate our own YAML if needed
        if (this.yaml_needs_generating) {
            this.yaml_needs_generating = false;

            YamlNodeAbstract<?> node = this.isAbstractNode() ? this.getAbstractNode() : null;

            if (this.parent == null) {
                if (this.header.isEmpty()) {
                    this.yaml.setValue("");
                } else {
                    // Generate the root YAML, which only contains a header
                    // Special about this header is that it uses #> instead of #
                    // This allows us to differentiate it from the header at a key
                    StringBuilder builder = new StringBuilder();
                    YamlSerializer.INSTANCE.appendHeader(builder, header, 0);
                    this.yaml.setValue(builder.toString());
                }
            } else if (node != null && !node._children.isEmpty()) {
                if (this.parent instanceof YamlListNode) {
                    // We store nothing for lists, this is the responsibility of the elements
                    // This is because lists don't have a clear header
                    this.yaml.setValue("");
                } else {
                    // # Header line
                    // key:\n
                    StringBuilder builder = new StringBuilder();
                    builder.append(this.serializeYamlValue(this.path.name()));
                    builder.insert(builder.length()-1, ':');
                    this.yaml.setValue(builder.toString());
                }
            } else {
                Object value = this.value;
                if (node instanceof YamlListNode) {
                    // Generate YAML that looks like this: []\n
                    value = Collections.emptyList();
                } else if (node instanceof YamlNodeAbstract<?>) {
                    // Generate YAML that looks like this: {}\n
                    value = Collections.emptyMap();
                } else if (value != null && value.getClass().isEnum()) {
                    // Special handling for enums: write the text version of it instead
                    // For enum values true/false (Bukkit PermissionDefault) write boolean true/false
                    // TODO: Is toString() really the right way to go? name() might be better.
                    String text = value.toString();
                    if (text.equalsIgnoreCase("true")) {
                        value = Boolean.TRUE;
                    } else if (text.equalsIgnoreCase("false")) {
                        value = Boolean.FALSE;
                    } else {
                        value = text;
                    }
                }

                // When value is a key: value pair, use a singleton map to emulate that
                // If it is a list value, then use a singleton list to prefix a -
                if (this.parent instanceof YamlListNode) {
                    // Generate YAML that looks like this: - value\n
                    value = Collections.singletonList(value);
                } else {
                    // Generate YAML that looks like this: key: value\n
                    value = Collections.singletonMap(this.getYamlPath().name(), value);
                }

                // Store it
                this.yaml.setValue(this.serializeYamlValue(value));
            }
        }
    }
}
