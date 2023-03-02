package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import com.bergerkiller.bukkit.common.utils.StringUtil;

import java.util.LinkedList;

/**
 * Can handle node formatting lines to keep track of the currently active node
 */
public class NodeBuilder implements YamlPath.Supplier {

    private LinkedList<String> nodes = new LinkedList<String>();
    private int indent;

    /**
     * Constructs a new Node Builder
     *
     * @param indent is the space count between a node and it's sub-node
     *        use value of -1 to auto-detect.
     */
    public NodeBuilder(int indent) {
        this.indent = indent;
    }

    /**
     * Resets the builder to a state as if it just has been constructed
     * 
     * @param indent
     */
    public void reset(int indent) {
        this.nodes.clear();
        this.indent = indent;
    }

    /**
     * Gets the level of indent currently detected or configured.
     * Is -1 if not yet detected.
     * 
     * @return indent
     */
    public int getIndent() {
        return this.indent;
    }

    /**
     * Updates the indent value to use (number of spaces)
     * 
     * @param indent
     */
    public void setIndent(int indent) {
        this.indent = indent;
    }

    /**
     * Handles the reading input of a new line
     *
     * @param line to handle
     * @param preceedingSpaces in front of the line (indentation spaces)
     * @return True if a node was handled, False if not
     */
    public boolean handle(String line, int preceedingSpaces) {
        if (line.startsWith("#")) {
            return false;
        }
        int nodeIndex;
        if (this.indent == -1) {
            if (preceedingSpaces > 0) {
                this.indent = preceedingSpaces;
                nodeIndex = 1;
            } else {
                nodeIndex = 0;
            }
        } else {
            nodeIndex = preceedingSpaces / this.indent;
        }

        String nodeName = StringUtil.getLastBefore(line, ":");
        if (!nodeName.isEmpty()) {
            // Calculate current path
            while (this.nodes.size() >= nodeIndex + 1) {
                this.nodes.pollLast();
            }
            nodes.offerLast(nodeName);
            return true;
        }
        return false;
    }

    /**
     * Handles the reading input of a new line
     *
     * @param line to handle
     * @param contentStart the start offset of the line, also defines indent
     * @param contentEnd the exclusive index of the last character of the line
     * @return True if a node was handled, False if not
     */
    public boolean handle(StringBuilder line, int contentStart, int contentEnd) {
        if (contentStart == contentEnd || line.charAt(contentStart) == '#') {
            return false;
        }

        int nodeIndex;
        if (this.indent == -1) {
            if (contentStart > 0) {
                this.indent = contentStart;
                nodeIndex = 1;
            } else {
                nodeIndex = 0;
            }
        } else {
            nodeIndex = contentStart / this.indent;
        }
        int nodeNameEndIndex = line.lastIndexOf(":", contentEnd);
        if (nodeNameEndIndex == -1 || nodeNameEndIndex <= contentStart) {
            return false;
        }

        // Calculate current path and update node index
        String nodeName = line.substring(contentStart, nodeNameEndIndex);
        while (this.nodes.size() >= nodeIndex + 1) {
            this.nodes.pollLast();
        }
        nodes.offerLast(nodeName);
        return true;
    }

    /**
     * Gets the name of the current node
     *
     * @return node name
     */
    public String getName() {
        return this.nodes.peekLast();
    }

    /**
     * Gets how deep the current node is in the tree hierarchy
     *
     * @return node depth
     */
    public int getDepth() {
        return this.nodes.size();
    }

    /**
     * Gets the path to the currently active node
     *
     * @return current path
     */
    public String getPath() {
        return StringUtil.join(".", this.nodes);
    }

    /**
     * Gets the YamlPath to the currently active node
     * 
     * @return current path
     */
    @Override
    public YamlPath getYamlPath() {
        YamlPath p = YamlPath.ROOT;
        for (String name : this.nodes) {
            p = p.child(name);
        }
        return p;
    }
}
