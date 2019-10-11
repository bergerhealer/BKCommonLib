package com.bergerkiller.bukkit.common.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A single node, or root, of a tree of String values.
 * The complete String of the entire tree can be obtained from
 * the root node and is automatically updated by changes to its children.
 */
public class StringTreeNode {
    private StringTreeNode _parent;
    private List<StringTreeNode> _children;
    private final CharArrayBuffer _buffer;
    private int _totalLength;
    private boolean _changed;

    /**
     * Creates a new root String tree node with empty value
     */
    public StringTreeNode() {
        this._parent = null;
        this._children = Collections.emptyList();
        this._buffer = new CharArrayBuffer();
        this._totalLength = 0;
        this._changed = false;
    }

    /**
     * Creates a new root String tree node with the value specified
     * 
     * @param value
     */
    public StringTreeNode(String value) {
        this._parent = null;
        this._children = Collections.emptyList();
        this._buffer = new CharArrayBuffer(value);
        this._totalLength = this._buffer.length();
        this._changed = false;
    }

    /**
     * Gets the parent of this node
     * 
     * @return parent, or null if this is a root node
     */
    public StringTreeNode parent() {
        return this._parent;
    }

    /**
     * Gets the index of this node relative to the parent
     * 
     * @return index of this node, -1 if this is a root node
     */
    public int index() {
        if (this._parent == null) {
            return -1;
        } else {
            return this._parent._children.indexOf(this);
        }
    }

    /**
     * Gets how many sub-nodes are contained below this node
     * 
     * @return count
     */
    public int size() {
        return this._children.size();
    }

    /**
     * Gets the value of this tree node
     * 
     * @return value as a String
     */
    public String getValue() {
        return this._buffer.toString();
    }

    /**
     * Gets the value of this tree node as a CharSequence.
     * This avoids the creation of an additional String.
     * 
     * @return value as a CharSequence
     */
    public CharSequence getValueSequence() {
        return this._buffer;
    }

    /**
     * Sets the value of this tree node
     * 
     * @param value
     */
    public void setValue(String value) {
        this.markChanged(this._buffer.update(value));
    }

    /**
     * Sets the value of this tree node
     * 
     * @param value
     */
    public void setValueSequence(CharSequence value) {
        this.markChanged(this._buffer.update(value));
    }

    /**
     * Removes this String tree node from the parent. It will remain functional
     * as a root node and can be added to another node afterwards.
     */
    public void remove() {
        if (this._parent == null) {
            throw new IllegalStateException("This is a root node and cannot be removed");
        }

        StringTreeNode parent = this._parent;
        this._parent = null;
        if (parent._children.size() == 1) {
            parent._children = Collections.emptyList();
        } else {
            parent._children.remove(this);
        }
        parent.markChanged(-this._totalLength);
    }

    /**
     * Gets a child String tree node of this node.
     * 
     * @param index of the node
     * @return child String tree node at this index
     */
    public StringTreeNode get(int index) {
        return this._children.get(index);
    }

    /**
     * Adds a new String tree node as a child of this node.
     * The contents of this node will be dynamically appended.
     * 
     * @return added String tree node with a default length of 0
     */
    public StringTreeNode add() {
        return add(new StringTreeNode());
    }

    /**
     * Adds an existing String tree node as a child of this node.
     * The contents of this node will be dynamically appended.
     * 
     * @param node
     * @return added String tree node
     */
    public StringTreeNode add(StringTreeNode node) {
        return insert(this._children.size(), node);
    }

    /**
     * Inserts a new String tree node as a child of this node at the specified
     * position. The contents of this node will be dynamically updated.
     * 
     * @param index to insert the node at
     * @return inserted String tree node with a default length of 0
     */
    public StringTreeNode insert(int index) {
        return insert(index, new StringTreeNode());
    }

    /**
     * Inserts an existing String tree node as a child of this node at the specified
     * position. The contents of this node will be dynamically updated.
     * 
     * @param index to insert the node at
     * @param node to insert
     * @return inserted String tree node
     */
    public StringTreeNode insert(int index, StringTreeNode node) {
        if (node._parent != null) {
            throw new IllegalStateException("The node already has a parent. Clone or remove the node first.");
        }
        if (index < 0 || index > this._children.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds (0 <= index <= " + this._children.size() + ")");
        }
        if (this._children.isEmpty()) {
            this._children = Collections.singletonList(node);
        } else {
            if (this._children.size() == 1) {
                this._children = new ArrayList<StringTreeNode>(this._children);
            }
            this._children.add(index, node);
        }
        node._parent = this;
        this.markChanged(node._totalLength);
        return node;
    }

    /**
     * Clones this StringTreeNode and all child nodes below it.
     * The clone will have a detached parent and changes to the clone
     * will not cause changes to this node or its children.
     * A single char[] buffer will back the entirety of this tree.
     */
    @Override
    public StringTreeNode clone() {
        return clone(this.toCharArray(), 0);
    }

    private StringTreeNode clone(char[] buffer, int position) {
        StringTreeNode clone = new StringTreeNode();
        clone._totalLength = this._totalLength;
        clone._buffer.assign(buffer, position, this._buffer.length());
        position += this._buffer.length();

        int child_count = this._children.size();
        if (child_count == 1) {
            StringTreeNode childClone = this._children.get(0).clone(buffer, position);
            childClone._parent = clone;
            clone._children = Collections.singletonList(childClone);
            clone._totalLength += childClone._totalLength;
        } else if (child_count > 0) {
            clone._children = new ArrayList<StringTreeNode>(child_count);
            for (StringTreeNode child : this._children) {
                StringTreeNode childClone = child.clone(buffer, position);
                childClone._parent = clone;
                childClone._totalLength = child._totalLength;
                clone._children.add(childClone);
                position += childClone._totalLength;
            }
        }

        return clone;
    }

    /**
     * Concatenates the value of this tree node and all children recursively into
     * a single long char[] array. If there were few changes to the value of this node
     * or the children, this operation is optimized for that.
     * 
     * @return the value of this node and all children concatenated to a new char[] array
     */
    public char[] toCharArray() {
        if (this._changed) {
            char[] result = new char[this._totalLength];
            this.toCharArray(result, 0);
            return result;
        } else {
            char[] result = new char[this._totalLength];
            this._buffer.copyTo(result, 0, this._totalLength);
            return result;
        }
    }

    /**
     * Concatenates the value of this tree node and all children recursively into
     * a single long String. If there were few changes to the value of this node
     * or the children, this operation is optimized for that.
     * 
     * @return the value of this node and all children concatenated to a String
     */
    @Override
    public String toString() {
        if (this._changed) {
            char[] result = new char[this._totalLength];
            this.toCharArray(result, 0);
            return new String(result);
        } else {
            return this._buffer.copyToString(this._totalLength);
        }
    }

    private int toCharArray(char[] buffer, int position) {
        if (this._changed) {
            this._changed = false;

            // Changed, move original data to the new buffer
            position = this._buffer.moveToBuffer(buffer, position);

            // Recursively call toString() on the children as well
            if (!this._children.isEmpty()) {
                for (StringTreeNode child : this._children) {
                    position = child.toCharArray(buffer, position);
                }
            }

            // Done!
            return position;
        } else {
            // Unchanged, selfBuffer stores the full String contents we need
            // This means we can perform a single copy for our own buffer
            // and the buffer contents of all our children.
            this._buffer.copyTo(buffer, position, this._totalLength);

            // Swap the buffer out for the new buffer, recursively
            return this.swapBuffer(buffer, position);
        }
    }

    private int swapBuffer(char[] buffer, int position) {
        position = this._buffer.swapBuffer(buffer, position);
        for (StringTreeNode node : this._children) {
            position = node.swapBuffer(buffer, position);
        }
        return position;
    }

    private void markChanged(int length_change) {
        if (length_change != 0) {
            StringTreeNode node = this;
            do {
                node._totalLength += length_change;
                node._changed = true;
            } while ((node = node._parent) != null);
        }
    }
}
