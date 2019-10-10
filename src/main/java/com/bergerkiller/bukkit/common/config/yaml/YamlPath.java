package com.bergerkiller.bukkit.common.config.yaml;

/**
 * A path to a node or value inside a YAML configuration tree.
 * Each path consists of the name of the last part of the path, and
 * a recursive parent path.
 */
public final class YamlPath {
    private final YamlPath parent;
    private final String name;
    private final int hashcode;
    private final int depth;

    /**
     * Constructs a new YamlPath using a .-delimited path string.
     * 
     * @param path String
     */
    public YamlPath(String path) {
        this(null, path);
    }

    /**
     * Constructs a new YamlPath extending another path
     * 
     * @param parent YamlPath to extend, null if this is a root path
     * @param path String to extend the base path with, can be .-delimited or a name
     */
    public YamlPath(YamlPath parent, String path) {
        // Turn .-delimited parts in the path into recursive parent paths
        int startIndex = 0;
        int endIndex;
        while ((endIndex = path.indexOf('.', startIndex)) != -1) {
            parent = new YamlPath(parent, path.substring(startIndex, endIndex));
            startIndex = endIndex + 1;
        }
        this.parent = parent;
        this.name = path.substring(startIndex);
        if (this.parent == null) {
            this.hashcode = 961 + (31 * this.name.hashCode());
            this.depth = 0;
        } else {
            this.hashcode = 961 + (31 * this.name.hashCode()) + this.parent.hashCode();
            this.depth = this.parent.getDepth() + 1;
        }
    }

    /**
     * Gets the parent path of this YamlPath
     * 
     * @return parent YamlPath
     */
    public YamlPath getParent() {
        return this.parent;
    }

    /**
     * Gets the name of the last part of this YamlPath
     * 
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the depth of this path, which is the amount of
     * sub-nodes below this path.
     * 
     * @return depth, 0 if {@link #getParent()} == null
     */
    public int getDepth() {
        return this.depth;
    }

    @Override
    public int hashCode() {
        return this.hashcode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof YamlPath) {
            YamlPath p1 = this;
            YamlPath p2 = (YamlPath) o;
            if (p1.getDepth() != p2.getDepth()) {
                return false;
            }

            do {
                if (!p1.name.equals(p2.name)) {
                    return false;
                }
                p1 = p1.getParent();
                p2 = p2.getParent();
            } while (p1 != null);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(this.name);        
        YamlPath path = this;
        while ((path = path.parent) != null) {
            str.insert(0, '.');
            str.insert(0, path.name);
        }
        return str.toString();
    }
}
