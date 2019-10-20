package com.bergerkiller.bukkit.common.config;

import java.util.List;

import com.bergerkiller.bukkit.common.config.yaml.YamlEntry;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;

public class ConfigurationNode extends YamlNodeAbstract<ConfigurationNode> {

    public ConfigurationNode() {
        super();
    }

    protected ConfigurationNode(YamlEntry entry) {
        super(entry);
    }

    @Override
    protected ConfigurationNode createNode(YamlEntry entry) {
        return new ConfigurationNode(entry);
    }

    /**
     * Stores a list of nodes at the path specified, creates one if not present.
     * If the list is null or empty, any original value at the path is removed.<br>
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
    @Override
    public void setNodeList(String path, List<ConfigurationNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            this.remove(path);
        } else {
            super.setNodeList(path, nodes);
        }
    }

    /**
     * Sets a value at a certain path. If the value is null, any value mapped to
     * the path is removed.<br>
     * <br>
     * If the value is a node, it will be parented to this node's tree. Changes to the node
     * then impact this tree. If the node is already parented, it is cloned and later
     * changes to the node will not impact this tree.
     *
     * @param path to set
     * @param value to set to
     */
    @Override
    public void set(String path, Object value) {
        if (value == null) {
            super.remove(path);
        } else {
            super.set(path, value);
        }
    }

    /*
     * These methods are now in YamlNodeAbstract but return a generic type
     * To prevent compilation errors, override to perform the cast in here
     * This is not needed for generic types (lists, maps)
     */

    @Override
    public ConfigurationNode getParent() {
        YamlNodeAbstract<?> parent = this.getYamlParent();
        while (parent != null && !(parent instanceof ConfigurationNode)) {
            parent = parent.getYamlParent();
        }
        return (ConfigurationNode) parent;
    }

    @Override
    public ConfigurationNode getNode(String path) {
        return super.getNode(path);
    }

    @Override
    public ConfigurationNode clone() {
        return super.clone();
    }

    /*
     * The 'read' logic allowed automatic deletion of unused parts of yaml documents.
     * It is much cleaner to migrate 'unused' parts. It has been deprecated.
     */

    @Deprecated
    public void setRead(String path) {
    }

    @Deprecated
    public void setRead() {
    }

    @Deprecated
    public void trim() {
    }

    @Deprecated
    public boolean isRead(String path) {
        return true;
    }

    @Deprecated
    public boolean isRead() {
        return true;
    }
}
