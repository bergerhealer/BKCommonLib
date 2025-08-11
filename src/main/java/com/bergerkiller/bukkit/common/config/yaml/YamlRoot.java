package com.bergerkiller.bukkit.common.config.yaml;

import java.util.HashMap;
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
     * of the caller. After detaching the input entry will not refer to anything.<br>
     * <br>
     * Listeners registered on this entry or its children are migrated to the new copy.
     * 
     * @param entry The entry to detach
     */
    public void detach(YamlEntry entry) {
        this.removeEntry(entry);
        if (entry.isAbstractNode()) {
            entry.copyToParent(null, new YamlRoot(), YamlPath.ROOT, new StringTreeNode(), true);
        }
    }

    public void updateEntryPath(YamlEntry entry, YamlPath newPath) {
        // Remove entry at the old path. If a different entry is already stored
        // there now, restore that entry
        YamlEntry removed = this._entries.remove(entry.getYamlPath());
        if (removed != entry && removed != null) {
            this._entries.put(entry.getYamlPath(), removed);
        }

        // Store the entry at the new path
        this._entries.put(newPath, entry);
    }

    protected void removeEntry(YamlEntry entry) {
        // In some situations the entry is already removed from everything
        // This avoids issues due to double-removal.
        if (entry.disposed) {
            return;
        }

        entry.yaml.remove();
        this._entries.remove(entry.getYamlPath());
        entry.disposed = true;
    }

    public void putEntry(YamlEntry entry) {
        this._entries.put(entry.getYamlPath(), entry);
    }

    public YamlEntry createEntryIfAbsent(YamlPath parent, String path) {
        return createEntryIfAbsent(parent.child(path));
    }

    public YamlEntry createEntryIfAbsent(YamlPath parent, YamlPath relativePath) {
        return createEntryIfAbsent(parent.child(relativePath));
    }

    public YamlEntry createEntryIfAbsent(YamlPath path) {
        return this._entries.containsKey(path) ? null : createEntry(path, false);
    }

    public YamlEntry getEntryIfExists(YamlPath parent, String path) {
        return this._entries.get(parent.child(path));
    }

    public YamlEntry getEntryIfExists(YamlPath parent, YamlPath relativePath) {
        return this._entries.get(YamlPath.join(parent, relativePath));
    }

    public YamlEntry getEntryIfExists(YamlPath path) {
        return this._entries.get(path);
    }

    public YamlEntry getEntry(YamlPath parent, String path) {
        return getEntry(parent.child(path));
    }

    public YamlEntry getEntry(YamlPath parent, YamlPath relativePath) {
        return getEntry(YamlPath.join(parent, relativePath));
    }

    public YamlEntry getEntry(YamlPath path) {
        return getEntry(path, false);
    }

    private YamlEntry getEntry(YamlPath path, boolean isListNode) {
        YamlEntry entry = this._entries.get(path);
        return (entry != null) ? entry : createEntry(path, isListNode);
    }

    private YamlEntry createEntry(YamlPath path, boolean isListNode) {
        YamlEntry parentEntry = getEntry(path.parent(), path.isListElement());
        YamlNodeAbstract<?> parentNode = isListNode ? parentEntry.createListNodeValue() : parentEntry.createNodeValue();
        YamlEntry newEntry = parentNode.createChildEntry(parentNode._children.size(), path);
        parentEntry.callChangeListeners();
        return newEntry;
    }
}
