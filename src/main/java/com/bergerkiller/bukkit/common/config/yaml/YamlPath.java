package com.bergerkiller.bukkit.common.config.yaml;

/**
 * A path to a node or value inside a YAML configuration tree.
 * Each path consists of the name of the last part of the path, and
 * a recursive parent path.
 */
public class YamlPath {
    /**
     * Root yaml path with an empty name and no parent path
     */
    public static final YamlPath ROOT = new YamlPath();

    private final YamlPath parent;
    private final String name;
    private final int depth;
    private final int hashcode;

    // Constructor for ROOT
    private YamlPath() {
        this.parent = null;
        this.name = "";
        this.depth = 0;
        this.hashcode = 0;
    }

    // Constructor used in the static factory methods, parent cannot be null
    private YamlPath(YamlPath parent, String name) {
        this.parent = parent;
        this.name = name;
        this.depth = parent.depth + 1;
        this.hashcode = this.name.hashCode() + 31 * this.parent.hashcode;
    }

    /**
     * Creates a new YamlPath using a .-delimited path string.
     * 
     * @param path
     * @return creates yaml path
     */
    public static YamlPath create(String path) {
        return createChild(ROOT, path);
    }

    private static YamlPath createChild(YamlPath parent, String path) {
        if (path.isEmpty()) {
            // If path is empty, then return the parent path
            return parent;
        } else {
            // Turn .-delimited parts in the path into additional recursive parents
            int startIndex = 0;
            while (startIndex < path.length()) {
                char c = path.charAt(startIndex);

                // Skip path separator characters
                if (c == '.') {
                    startIndex++;
                    continue;
                }

                // Detect list elements, which start with [ and end with ]
                // They must contain 0-9 digit characters inside for the index
                if (c == '[' && (path.length()-startIndex) >= 3) {
                    int i = startIndex;
                    int listIndex = 0;
                    boolean foundDigit = false;
                    int listEndIndex = -1;
                    while (++i < path.length()) {
                        char digit = path.charAt(i);
                        if (digit >= '0' && digit <= '9') {
                            foundDigit = true;
                            listIndex *= 10;
                            listIndex += (digit-'0');
                        } else if (digit == ']') {
                            listEndIndex = i + 1;
                            break;
                        } else {
                            break;
                        }
                    }
                    if (foundDigit && listEndIndex != -1) {
                        parent = new YamlPathListElement(parent, listIndex);
                        startIndex = listEndIndex;
                        continue;
                    }
                }

                // Anything else is a path node element, including malformed list elements
                // Select up to the next . or [
                int i = startIndex;
                while (true) {
                    if (++i >= path.length()) {
                        // End of path found, take all from start index to the end
                        parent = new YamlPath(parent, path.substring(startIndex));
                        startIndex = path.length();
                        break;
                    }
                    c = path.charAt(i);
                    if (c == '.' || c == '[') {
                        parent = new YamlPath(parent, path.substring(startIndex, i));
                        startIndex = i;
                        break;
                    }
                }
            }

            // Done
            return parent;
        }
    }

    /**
     * Gets whether this is a root path, which is equivalent to an
     * empty path String. This will be the {@link #ROOT} instance.
     * There are no deeper parents.
     * 
     * @return True if this is a root path
     */
    public boolean isRoot() {
        return this == ROOT;
    }

    /**
     * Gets whether this path refers to a list element, which means
     * the name contains an index. For example: [12]
     * 
     * @return True if this is a path to a list element
     */
    public boolean isListElement() {
        return false;
    }

    /**
     * Gets the index to the parent node's list this path refers to.
     * If this is not a list as indicated by {@link #isListElement()} then this
     * method returns -1.
     * 
     * @return list index
     */
    public int listIndex() {
        return -1;
    }

    /**
     * Gets the parent path of this YamlPath
     * 
     * @return parent YamlPath, null if this is a root path
     */
    public YamlPath parent() {
        return this.parent;
    }

    /**
     * Gets a child path of this path. The child path can contain
     * multiple path parts to refer to a deeper child. If the child
     * path is empty, this path is returned instead.
     *
     * @param childPath The path to the child
     * @return child path
     */
    public YamlPath child(String childPath) {
        return createChild(this, childPath);
    }

    /**
     * Gets a child path of this path. The child path can contain
     * multiple path parts to refer to a deeper child. If the child
     * path is root, this path is returned instead.
     *
     * @param childPath The YamlPath to append to this path to make a child
     * @return child path
     */
    public YamlPath child(YamlPath childPath) {
        if (childPath.isRoot()) {
            return this;
        } else {
            return child(childPath.parent()).child(childPath.name());
        }
    }

    /**
     * Gets a child path of this path. The {@link #name()} of the node
     * specified is appended to this path. This is slightly more
     * efficient than {@link #child(String)} because it eliminates name
     * format parsing that would otherwise occur.
     *
     * @param nameOfPath YamlPath whose name to use
     * @return child path
     */
    public YamlPath childWithName(YamlPath nameOfPath) {
        return nameOfPath.appendNameTo(this);
    }

    /**
     * Creates a YamlPath which is this path, but relative to the absolute
     * path specified. If no such path is possible, returns null. This
     * method can also be used to check whether this path starts with
     * the absolute path.<br>
     * <br>
     * If this path and the absolute path are equal, then
     * {@link YamlPath#ROOT} is returned.
     *
     * @param absolutePath Absolute YamlPath relative to which a path
     *                     must be found
     * @return This path relative to absolutePath, or null if not possible
     */
    public YamlPath makeRelative(YamlPath absolutePath) {
        // Number of elements this path is deeper than the absolute path
        int depthDiff = this.depth() - absolutePath.depth();
        if (depthDiff < 0) {
            return null; // Impossible
        } else if (depthDiff == 0) {
            return this.equals(absolutePath) ? ROOT : null;
        }

        // Find the common parent which must be equal to the absolute path
        // Collect all nodes in-between
        YamlPath[] relativeParts = new YamlPath[depthDiff];
        YamlPath commonParent = this;
        do {
            relativeParts[--depthDiff] = commonParent;
            commonParent = commonParent.parent;
        } while (depthDiff > 0);
        if (!commonParent.equals(absolutePath)) {
            return null;
        }

        // Okay! Create a new path with just the elements beyond commonParent
        YamlPath result = YamlPath.ROOT;
        for (YamlPath relativePart : relativeParts) {
            result = relativePart.appendNameTo(result);
        }
        return result;
    }

    /**
     * Joins two YAML paths together, making the second path be parented
     * to the first path. If either path is {@link YamlPath#ROOT}, returns
     * the other path in full.
     *
     * @param firstPath First path
     * @param secondPath Second path to append to first path
     * @return Joined path
     * @see #makeRelative(YamlPath)
     */
    public static YamlPath join(YamlPath firstPath, YamlPath secondPath) {
        if (firstPath.isRoot()) {
            return secondPath;
        } else if (secondPath.isRoot()) {
            return firstPath;
        }

        // Convert second path into path parts
        // This avoids having to do nasty slow recursion to reverse-iterate
        int depth = secondPath.depth;
        YamlPath[] parts = new YamlPath[depth];
        {
            YamlPath p = secondPath;
            while (--depth >= 0) {
                parts[depth] = p;
                p = p.parent;
            }
        }

        // Make a new path
        YamlPath result = firstPath;
        for (YamlPath p : parts) {
            result = result.childWithName(p);
        }
        return result;
    }

    /**
     * Creates a child path of this path to a list element
     * 
     * @param listIndex The index of the element in the list
     * @return list child path
     */
    public YamlPath listChild(int listIndex) {
        return new YamlPathListElement(this, listIndex);
    }

    /**
     * Gets the name of the last part of this YamlPath.
     * If this is a list element, the [] portion is omitted.
     * 
     * @return name
     */
    public String name() {
        return this.name;
    }

    protected YamlPath appendNameTo(YamlPath parent) {
        return new YamlPath(parent, this.name);
    }

    /**
     * Gets the depth of this path, which is the amount of
     * sub-nodes below this path.
     * 
     * @return depth, 0 if {@link #parent()} == null
     */
    public int depth() {
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
            while (p1.name.equals(p2.name)) {
                p1 = p1.parent();
                p2 = p2.parent();
                if (p1 == p2) {
                    return true;
                } else if (p1 == null || p2 == null) {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (this.isRoot()) {
            return "";
        } else {
            // Child of root
            StringBuilder str = new StringBuilder(64);
            YamlPath path = this;
            do {
                path.insertBeginning(str);
                path = path.parent;
            } while (!path.isRoot());
            return str.toString();
        }
    }

    protected void insertBeginning(StringBuilder str) {
        str.insert(0, this.name);
        if (!this.parent.isRoot()) {
            str.insert(0, '.');
        }
    }

    /**
     * YamlPath implementation for the list element of a list of values.
     * Is automatically detected with the [] idiom.
     */
    private static final class YamlPathListElement extends YamlPath {
        private final int index;

        public YamlPathListElement(YamlPath parent, int index) {
            super(parent, Integer.toString(index));
            this.index = index;
        }

        private YamlPathListElement(YamlPath parent, String name, int index) {
            super(parent, name);
            this.index = index;
        }

        @Override
        public int listIndex() {
            return this.index;
        }

        @Override
        public boolean isListElement() {
            return true;
        }

        @Override
        protected YamlPathListElement appendNameTo(YamlPath parent) {
            return new YamlPathListElement(parent, this.name(), this.index);
        }

        @Override
        protected void insertBeginning(StringBuilder str) {
            str.insert(0, ']');
            str.insert(0, this.name());
            str.insert(0, '[');
        }
    }

    /**
     * Provides a yaml path
     */
    @FunctionalInterface
    public interface Supplier {
        /**
         * Gets the path location of this entry or node
         *
         * @return yaml path
         */
        YamlPath getYamlPath();
    }
}
