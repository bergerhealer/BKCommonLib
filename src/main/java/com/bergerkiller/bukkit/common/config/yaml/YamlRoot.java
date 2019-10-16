package com.bergerkiller.bukkit.common.config.yaml;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import com.bergerkiller.bukkit.common.collections.StringTreeNode;

/**
 * The root of a yaml document tree. The root stores the mapping
 * from yaml path to the entry that represents it.
 */
public class YamlRoot {
    private final Map<YamlPath, YamlEntry> _entries;

    public YamlRoot() {
        this._entries = new HashMap<YamlPath, YamlEntry>();
    }

    /**
     * Clears all entries stored in this root
     */
    public void clear() {
        this._entries.clear();
    }

    /**
     * Detaches an entry from this root. If the entry is a node, the node is given a new
     * root. The entry is not removed from the parent node, which is the responsibility
     * of the caller. After detaching the input entry will not refer to anything.
     * 
     * @param entry        The entry to detach
     * @param removeEntry  Whether to remove the entry itself, or only remove node descendants
     */
    public void detach(YamlEntry entry) {
        this.removeEntry(entry);
        if (entry.isNodeValue()) {
            moveToRoot(entry, null, new YamlRoot(), YamlPath.ROOT, new StringTreeNode());
        }
    }

    /**
     * Moves an entry and all sub-entries out of this root, into a new one, at
     * the new path specified. All nodes and values get a new entry with the updated
     * path and yaml. Other properties are preserved.<br>
     * <br>
     * <b>Note: </b>the input entry is not removed, only the children, so the entry can be repurposed
     * to store different data. Call {@link #removeEntry(entry)} to remove the entry before
     * calling this function if this is desired, or use {@link #detach(entry)}.
     * 
     * @param entry      The entry to move out of this root into a new one
     * @param newParent  The new parent node for the entry, null if it is a root node
     * @param newRoot    The new root to store the entry in
     * @param newPath    The new (root) path of the entry
     * @param newYaml    The new yaml String tree node for the entry
     * @return The new entry, null if the entry referred to a non-node value and no parent was set.
     *         It is up to the caller to store the entry in the right place (node children).
     */
    public YamlEntry moveToRoot(YamlEntry entry, YamlNode newParent, YamlRoot newRoot, YamlPath newPath, StringTreeNode newYaml) {
        // Create the replacement entry, which is at a new path
        // Preserve certain properties from the original entry, such as the header
        YamlEntry newEntry = new YamlEntry(newParent, newPath, newYaml);
        newEntry.assignProperties(entry);
        newEntry.value = entry.value;
        newRoot._entries.put(newPath, newEntry);

        // Special handling for nodes
        if (entry.isNodeValue()) {
            // Store the new entry in the node
            YamlNode node = entry.getNodeValue();
            node._root = newRoot;
            node._entry = newEntry;

            // Recursively operate on the children of the node
            ListIterator<YamlEntry> iter = node._children.listIterator();
            while (iter.hasNext()) {
                YamlEntry childEntry = iter.next();
                this.removeEntry(childEntry);

                YamlPath newChildPath = newPath.child(childEntry.getPath().name());
                StringTreeNode newChildYaml = newYaml.add();
                iter.set(this.moveToRoot(childEntry, node, newRoot, newChildPath, newChildYaml));
            }
        }

        return newEntry;
    }

    public void updateEntryPath(YamlEntry entry, YamlPath newPath) {
        this._entries.remove(entry.getPath());
        this._entries.put(newPath, entry);
    }

    public void removeEntry(YamlEntry entry) {
        entry.yaml.remove();
        this._entries.remove(entry.getPath());
    }

    public void putEntry(YamlEntry entry) {
        this._entries.put(entry.getPath(), entry);
    }

    public YamlEntry getEntryIfExists(YamlPath parent, String path) {
        return this._entries.get(parent.child(path));
    }

    public YamlEntry getEntryIfExists(YamlPath path) {
        return this._entries.get(path);
    }

    public YamlEntry getEntry(YamlPath parent, String path) {
        return getEntry(parent.child(path));
    }

    public YamlEntry getEntry(YamlPath path) {
        return getEntry(path, false);
    }

    private YamlEntry getEntry(YamlPath path, boolean isListNode) {
        YamlEntry entry = this._entries.get(path);
        if (entry == null) {
            YamlEntry parentEntry = getEntry(path.parent(), path.isList());
            YamlNode parentNode = isListNode ? parentEntry.createListNodeValue() : parentEntry.createNodeValue();
            entry = parentNode.createChildEntry(parentNode._children.size(), path);
        }
        return entry;
    }
}
