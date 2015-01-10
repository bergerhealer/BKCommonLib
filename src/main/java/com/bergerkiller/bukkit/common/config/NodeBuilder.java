package com.bergerkiller.bukkit.common.config;

import java.util.LinkedList;

import com.bergerkiller.bukkit.common.utils.StringUtil;

/**
 * Can handle node formatting lines to keep track of the currently active node
 */
public class NodeBuilder {
	private LinkedList<String> nodes = new LinkedList<String>();
	private final int indent;

	/**
	 * Constructs a new Node Builder
	 * 
	 * @param indent is the space count between a node and it's sub-node
	 */
	public NodeBuilder(int indent) {
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
		int nodeIndex = preceedingSpaces / this.indent;
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
}
