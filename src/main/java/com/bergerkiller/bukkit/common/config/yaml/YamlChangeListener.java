package com.bergerkiller.bukkit.common.config.yaml;

/**
 * Listener for being notified of changes to a Yaml node structure
 */
@FunctionalInterface
public interface YamlChangeListener {

    /**
     * Called when the value of a node or the node itself changes.
     * When a child is re-ordered, then that counts as a change on
     * the parent node holding the children.
     *
     * @param path Yaml Path to the node or value that changed. This path
     *             is relative to the root of the Yaml tree.
     */
    void onNodeChanged(YamlPath path);
}
