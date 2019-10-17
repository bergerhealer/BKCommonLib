package com.bergerkiller.bukkit.common.config.yaml;

/**
 * Default YamlNode implementation
 */
public class YamlNode extends YamlNodeAbstract<YamlNode> {
    /**
     * Creates a new empty Yaml Root Node
     */
    public YamlNode() {
        super();
    }

    /**
     * Creates a new child node for an entry, or a new root node
     * if the entry is null.
     * 
     * @param entry
     */
    protected YamlNode(YamlEntry entry) {
        super(entry);
    }

    @Override
    protected YamlNode createNode(YamlEntry entry) {
        return new YamlNode(entry);
    }
}
