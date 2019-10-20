package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.config.yaml.YamlDeserializer;

/**
 * A basic YAML configuration implementation
 */
public class BasicConfiguration extends ConfigurationNode {
    private int indent = 2;

    @Override
    public String getPath() {
        return "";
    }

    /**
     * Sets the indentation of sub-nodes
     *
     * @param indent size
     */
    public void setIndent(int indent) {
        this.indent = indent;
    }

    /**
     * Gets the indentation of sub-nodes
     *
     * @return indent size
     */
    public int getIndent() {
        return this.indent;
    }

    @Override
    public void loadDeserializerOutput(YamlDeserializer.Output output) {
        this.indent = output.indent;
        super.loadDeserializerOutput(output);
    }
}
