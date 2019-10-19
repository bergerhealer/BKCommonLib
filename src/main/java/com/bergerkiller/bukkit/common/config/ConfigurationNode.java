package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ConfigurationNode implements Cloneable {

    private final MemorySection source;
    private final Map<String, String> headers;
    private final Set<String> readkeys;

    public ConfigurationNode() {
        this(new HashSet<String>(), new HashMap<String, String>(), new YamlConfiguration());
    }

    private ConfigurationNode(ConfigurationNode source, String root) {
        this.readkeys = source.readkeys;
        this.headers = source.headers;
        MemorySection sect = (MemorySection) source.source.getConfigurationSection(root);
        if (sect == null) {
            this.source = (MemorySection) source.source.createSection(root);
        } else {
            this.source = sect;
        }
        this.setRead();
    }

    private ConfigurationNode(final Set<String> readkeys, final Map<String, String> headers, final MemorySection source) {
        this.readkeys = readkeys;
        this.source = source;
        this.headers = headers;
    }

    /**
     * Checks if this node has a parent, or if it a root node
     *
     * @return True if it has a parent, False if not
     */
    public boolean hasParent() {
        return this.source.getParent() != null;
    }

    /**
     * Checks if this node has sub-nodes or values
     *
     * @return True if this node is empty, False if not
     */
    public boolean isEmpty() {
        return this.getKeys().isEmpty();
    }

    /**
     * Gets the parent configuration node of this Node
     *
     * @return parent node
     */
    public ConfigurationNode getParent() {
        MemorySection sec = (MemorySection) this.source.getParent();
        if (sec == null) {
            return null;
        }
        return new ConfigurationNode(this.readkeys, this.headers, sec);
    }

    /**
     * Gets the full path of a value or node relative to this Configuration Node
     *
     * @param append path, the path to the value or node relative to this Node
     * @return The full path
     */
    public String getPath(String append) {
        String p = this.getPath();
        if (LogicUtil.nullOrEmpty(append)) {
            return p;
        }
        if (LogicUtil.nullOrEmpty(p)) {
            return append;
        }
        return p + "." + append;
    }

    /**
     * Gets the full path of this Configuration Node
     *
     * @return node path
     */
    public String getPath() {
        return this.source.getCurrentPath();
    }

    /**
     * Gets the name of this Configuration Node
     *
     * @return node name
     */
    public String getName() {
        return this.source.getName();
    }

    /**
     * Gets the header of this Node
     *
     * @return The header
     */
    public String getHeader() {
        return this.headers.get(this.getPath());
    }

    /**
     * Gets the header of a value or node
     *
     * @param path to the value or node
     * @return Header at the path specified
     */
    public String getHeader(String path) {
        return this.headers.get(this.getPath(path));
    }

    /**
     * Removes the header for this Node
     */
    public void removeHeader() {
        this.setHeader(null);
    }

    /**
     * Removes the header for a certain value or node
     *
     * @param path to the value or node
     */
    public void removeHeader(String path) {
        this.setHeader(path, null);
    }

    /**
     * Sets the header displayed above this configuration node
     *
     * @param header to set to
     */
    public void setHeader(String header) {
        this.setHeader("", header);
    }

    /**
     * Sets the header displayed above a given configuration node or value
     *
     * @param path to the node or value the header is for
     * @param header to set to
     */
    public void setHeader(String path, String header) {
        if (header == null) {
            this.headers.remove(this.getPath(path));
        } else {
            this.headers.put(this.getPath(path), header);
        }
    }

    /**
     * Adds a new line to the header displayed above this configuration node
     *
     * @param header line to add
     */
    public void addHeader(String header) {
        this.addHeader("", header);
    }

    /**
     * Adds a new line to the header displayed above a given configuration node
     * or value
     *
     * @param path to the node or value the header is for
     * @param header line to add
     */
    public void addHeader(String path, String header) {
        String oldheader = this.getHeader(path);
        if (oldheader == null) {
            this.setHeader(path, header);
        } else {
            this.setHeader(path, oldheader + "\n" + header);
        }
    }

    /**
     * Gets all the headers displayed for this Node and the sub-nodes
     *
     * @return A mapping of keys vs. headers
     */
    public Map<String, String> getHeaders() {
        String root = this.getPath();
        Map<String, String> rval = new HashMap<String, String>(this.headers.size());
        if (LogicUtil.nullOrEmpty(root)) {
            rval.putAll(this.headers);
        } else {
            for (Map.Entry<String, String> entry : this.headers.entrySet()) {
                if (entry.getKey().startsWith(root)) {
                    rval.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return rval;
    }

    /**
     * Clears all the headers displayed for this node and the values and
     * sub-nodes in this node
     */
    public void clearHeaders() {
        String root = this.getPath();
        if (root == null || root.length() == 0) {
            this.headers.clear();
        } else {
            Iterator<Map.Entry<String, String>> iter = this.headers.entrySet().iterator();
            while (iter.hasNext()) {
                if (iter.next().getKey().startsWith(root)) {
                    iter.remove();
                }
            }
        }

    }

    /**
     * Gets the Memory Section represented by this Configuration Node
     *
     * @return Memory Section of this Node
     */
    public MemorySection getSection() {
        return this.source;
    }

    /**
     * Gets the YamlConfiguration root source for this Configuration
     *
     * @return Yaml Configuration root
     */
    public YamlConfiguration getSource() {
        return (YamlConfiguration) this.source.getRoot();
    }

    /**
     * Checks if a node is contained at the path specified
     *
     * @param path to check at
     * @return True if it is a node, False if not
     */
    public boolean isNode(String path) {
        return this.source.isConfigurationSection(path);
    }

    /**
     * Gets the node at the path specified, creates one if not present
     *
     * @param path to get a node
     * @return the node
     */
    public ConfigurationNode getNode(String path) {
        return new ConfigurationNode(this, path);
    }

    /**
     * Gets a sorted list of configuration nodes at a particular path.
     * Changes in the list are not reflected back into this configuration.
     * The list will be sorted by the key value of the nodes, and not by
     * the order in which they appear in the YAML.
     * 
     * @param path
     * @return list of configuration nodes
     */
    public List<ConfigurationNode> getNodeList(String path) {
        ArrayList<ConfigurationNode> list = new ArrayList<ConfigurationNode>(this.getNode(path).getNodes());
        Collections.sort(list, (o1, o2) -> {
            String n1 = o1.getName();
            String n2 = o2.getName();
            if (ParseUtil.isNumeric(n1) && ParseUtil.isNumeric(n2)) {
                try {
                    int num1 = Integer.parseInt(n1);
                    int num2 = Integer.parseInt(n2);
                    return Integer.compare(num1, num2);
                } catch (NumberFormatException ex) {}
            }
            return n1.compareTo(n2);
        });
        return list;
    }

    /**
     * Stores the nodes in the list in the order they appear in this configuration,
     * giving them integer key name starting with 1. This will result in a list of
     * 1,2,3,etc. keys with the nodes being added to this configuration node.
     * Changes to the nodes after adding do not have an effect.
     * 
     * @param path to set
     * @param list of nodes to add
     */
    public void setNodeList(String path, List<ConfigurationNode> list) {
        // Get root of this configuration
        Configuration root = this.source.getRoot();

        // Clone the list with the nodes cloned that are directly originating from here
        // This prevents nasty problems where setting a node thats in the list will corrupt it
        if (root != null) {
            list = new ArrayList<ConfigurationNode>(list);
            for (int i = 0; i < list.size(); i++) {
                ConfigurationNode node = list.get(i);
                if (node != null && node.source.getRoot() == root) {
                    list.set(i, node.clone());
                }
            }
        }

        // Load all items in the (cloned) List
        ConfigurationNode baseNode = this.getNode(path);
        baseNode.clear();
        for (int i = 0; i < list.size(); i++) {
            baseNode.set(Integer.toString(i + 1), list.get(i));
        }
    }

    /**
     * Gets all configuration nodes sorted by insertion order
     *
     * @return Set of configuration nodes
     */
    public Set<ConfigurationNode> getNodes() {
        Set<ConfigurationNode> rval = new LinkedHashSet<ConfigurationNode>();
        for (String path : this.getKeys()) {
            if (this.isNode(path)) {
                rval.add(this.getNode(path));
            }
        }
        return rval;
    }

    /**
     * Gets all the values mapped to the keys
     *
     * @return map of the values
     */
    public Map<String, Object> getValues() {
        return this.source.getValues(false);
    }

    /**
     * Gets all the values mapped to the keys of the type specified
     *
     * @param type to convert to
     * @return map of the converted values
     */
    public <T> Map<String, T> getValues(Class<T> type) {
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
     * Gets all the keys of the values sorted in the order they exist in the YAML file
     *
     * @return key set
     */
    public Set<String> getKeys() {
        return this.source.getKeys(false);
    }

    /**
     * Clones this ConfigurationNode in a way that it no longer references the
     * backing file configuration
     */
    @Override
    public ConfigurationNode clone() {
        ConfigurationNode cloned = new ConfigurationNode();
        cloned.setHeader(this.getHeader());
        for (String key : this.getKeys()) {
            if (this.isNode(key)) {
                cloned.set(key, this.getNode(key).clone());
            } else {
                cloned.set(key, this.get(key));
            }
        }
        return cloned;
    }

    public boolean isRead() {
        return this.isRead(null);
    }

    public boolean isRead(String path) {
        return this.readkeys.contains(this.getPath(path));
    }

    public void setRead() {
        this.setRead(null);
    }

    public void setRead(String path) {
        this.setReadFullPath(this.getPath(path));
    }

    private void setReadFullPath(String path) {
        if (this.readkeys.add(path)) {
            int dotindex = path.lastIndexOf('.');
            if (dotindex > 0) {
                this.setReadFullPath(path.substring(0, dotindex));
            }
        }
    }

    /**
     * Trims away all unread values
     */
    public void trim() {
        for (String key : this.getKeys()) {
            if (this.isRead(key)) {
                if (this.isNode(key)) {
                    this.getNode(key).trim();
                }
            } else {
                this.remove(key);
            }
        }
    }

    /**
     * Checks if a value is contained at the path specified
     *
     * @param path to check at
     * @return True if a value is contained, False if not
     */
    public boolean contains(String path) {
        return this.source.contains(path);
    }

    /**
     * Clears all values
     */
    public void clear() {
        for (String key : this.getKeys()) {
            this.remove(key);
        }
    }

    /**
     * Removes the value at the path specified
     *
     * @param path to remove at
     */
    public void remove(String path) {
        this.set(path, null);
    }

    /**
     * Sets a value at a certain path
     *
     * @param path to set
     * @param value to set to
     */
    public void set(String path, Object value) {
        if (value != null) {
            this.setRead(path);
            if (value instanceof ConfigurationNode) {
                value = ((ConfigurationNode) value).getSource();
            }
            if (value instanceof ConfigurationSection) {
                // Copy the full configuration section into this configuration
                if (this.contains(path)) {
                    this.remove(path);
                }
                ConfigurationNode node = this.getNode(path);
                ConfigurationSection section = (ConfigurationSection) value;
                for (String key : section.getKeys(true)) {
                    Object sub_value = section.get(key);
                    if (!(sub_value instanceof ConfigurationSection)) {
                        node.set(key, sub_value);
                    }
                }
                return;
            }
            if (value.getClass().isEnum()) {
                String text = value.toString();
                if (text.equals("true")) {
                    value = true;
                } else if (text.equals("false")) {
                    value = false;
                } else {
                    value = text;
                }
            }
        }
        this.source.set(path, value);
    }

    /**
     * Gets the value at the path as a List
     *
     * @param path to get at
     * @return The value as a List
     */
    public List getList(String path) {
        this.setRead(path);
        return this.source.getList(path);
    }

    /**
     * Gets the value at the path as a List of a given type
     *
     * @param path to get at
     * @param type of the list to get
     * @return The value as a list of the type
     */
    public <T> List<T> getList(String path, Class<T> type) {
        return this.getList(path, type, new ArrayList<T>());
    }

    /**
     * Gets the value at the path as a List of a given type
     *
     * @param path to get at
     * @param type of the list to get
     * @param def list to return if not contained
     * @return The value as a list of the type
     */
    public <T> List<T> getList(String path, Class<T> type, List<T> def) {
        List list = this.getList(path);
        if (list != null) {
            def = new ArrayList<T>();
            T val;
            for (Object o : list) {
                val = ParseUtil.convert(o, type);
                if (val != null) {
                    def.add(val);
                }
            }
        }
        this.set(path, def);
        return def;
    }

    /**
     * Gets the raw value at the path specified
     *
     * @param path to get at
     * @return the raw value
     */
    public Object get(String path) {
        this.setRead(path);
        return this.source.get(path);
    }

    /**
     * Gets the raw value at the path as the type specified
     *
     * @param path to get at
     * @param type of value to get
     * @return the converted value, or null if not found or invalid
     */
    public <T> T get(String path, Class<T> type) {
        return this.get(path, type, null);
    }

    /**
     * Gets the raw value at the path as the type specified<br>
     * <b>The def value is used to get the type, it can not be null!</b>
     *
     * @param path to get at
     * @param def value to return
     * @return the converted value, or the default value if not found or invalid
     */
    public <T> T get(String path, T def) {
        return this.get(path, (Class<T>) def.getClass(), def);
    }

    /**
     * Gets the raw value at the path as the type specified
     *
     * @param path to get at
     * @param type of value to get
     * @param def value to return
     * @return the converted value, or the default value if not found or invalid
     */
    public <T> T get(String path, Class<T> type, T def) {
        Object rawValue = this.get(path);
        if (type == String.class && rawValue instanceof String[]) {
            // Special conversion to line-by-line String
            // This is needed, as it saves line-split Strings as such
            return (T) StringUtil.join("\n", (String[]) rawValue);
        }
        T rval = ParseUtil.convert(this.get(path), type, null);
        if (rval == null) {
            rval = def;
            this.set(path, rval);
        }
        return rval;
    }

    /**
     * Shares a single value with a target collection:<br>
     * - Writes the value from this node to the target if possible<br>
     * - Writes the value from the target to this node alternatively<br>
     * - If no value was found at all, both the target and this node get the
     * default value
     *
     * @param target to share the value with
     * @param path to the value to share
     * @param def value to use if no value was found
     */
    public void shareWith(Map<String, Object> target, String path, Object def) {
        Object value = this.get(path);
        if (value != null) {
            target.put(path, value);
        } else {
            value = target.get(path);
            if (value == null) {
                value = def;
                target.put(path, value);
            }
            this.set(path, value);
        }
    }

    @Override
    public String toString() {
        DumperOptions yamlOptions = new DumperOptions();
        YamlRepresenter yamlRepresenter = new YamlRepresenter();
        Yaml yaml = new Yaml(new YamlConstructor(), yamlRepresenter, yamlOptions);
        yamlOptions.setIndent(2);
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return yaml.dump(source.getValues(false));
    }
}
