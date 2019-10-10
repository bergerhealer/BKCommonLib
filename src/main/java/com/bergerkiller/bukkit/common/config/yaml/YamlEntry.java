package com.bergerkiller.bukkit.common.config.yaml;

import java.util.Collections;

/**
 * A single entry inside the YAML document.
 * Stores the name, value, header and cached serialized YAML String.
 * Automatically regenerates the yaml when required.
 */
public class YamlEntry {
    private String name;
    private Object value;
    private String header;
    private String yaml;

    public YamlEntry() {
        this.name = "";
        this.value = null;
        this.header = "";
        this.yaml = null;
    }

    /**
     * Gets the name to which this entry's value is bound
     * 
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets a new name to which this entry's value is bound
     * 
     * @param name to set to
     */
    public void setName(String name) {
        this.name = name;
        this.yaml = null;
    }

    /**
     * Gets the current value of this entry
     * 
     * @return value
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * Sets a new value for this entry
     * 
     * @param value to set to
     */
    public void setValue(Object value) {
        this.value = value;
        this.yaml = null;
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
        this.yaml = null;
    }

    /**
     * Serializes the contents of this entry to a YAML-encoded
     * String. This includes the (optional) header, path name and value.
     * If the value stored is a YAML node, then the entire node is serialized
     * as well.
     * 
     * @return YAML-encoded String
     */
    public String getYaml() {
        if (this.yaml == null) {
            this.yaml = YamlSerializer.INSTANCE.serialize(Collections.singletonMap(name, value), header);
        }
        return this.yaml;
    }
}
