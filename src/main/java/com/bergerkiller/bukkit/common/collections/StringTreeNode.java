package com.bergerkiller.bukkit.common.collections;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
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
    private FlatChildren _flatChildren;
    private final CharArrayBuffer _buffer;
    private int _totalLength;
    private boolean _changed;

    /**
     * Creates a new root String tree node with empty value
     */
    public StringTreeNode() {
        this._parent = null;
        this._children = Collections.emptyList();
        this._flatChildren = new FlatChildrenRoot();
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
        this._flatChildren = new FlatChildrenRoot();
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
     * Moves this tree node to another index relative to other siblings of the same
     * parent node.
     * 
     * @param index this tree node should get
     * @return True if the order changed, False if nothing changed
     */
    public boolean setIndex(int index) {
        if (this._parent == null) {
            return false;
        } else if (index < 0 || index >= this._parent._children.size()) {
            throw new IndexOutOfBoundsException("Index " +  index + " is out of range");
        } else if (this._parent.get(index) != this) {
            int oldIndex = this._parent._children.indexOf(this);
            if (oldIndex != -1 && oldIndex != index) {
                int move_offset = this._parent._children.get(index)._flatChildren.offset - this._flatChildren.offset;
                this._flatChildren.shift(move_offset);

                this._parent._children.remove(oldIndex);
                this._parent._children.add(index, this);
                this._parent.markChanged(0);

                return true;
            }
        }
        return false;
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
        this.markChangedIfLengthChanged(this._buffer.update(value));
    }

    /**
     * Sets the value of this tree node
     * 
     * @param value
     */
    public void setValueSequence(CharSequence value) {
        this.markChangedIfLengthChanged(this._buffer.update(value));
    }

    /**
     * Removes this String tree node from the parent. It will remain functional
     * as a root node and can be added to another node afterwards.
     *
     * @throws IllegalStateException If this node has no parent
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
        this._flatChildren = this._flatChildren.remove(parent);
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
     * @throws IllegalStateException If the node being inserted already has a parent node
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
     * @throws IndexOutOfBoundsException If the index is beyond the range of children
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
     * @throws IllegalStateException If the node being inserted already has a parent node
     * @throws IndexOutOfBoundsException If the index is beyond the range of children
     */
    public StringTreeNode insert(int index, StringTreeNode node) {
        if (node._parent != null) {
            throw new IllegalStateException("The node already has a parent. Clone or remove the node first.");
        }
        if (index < 0 || index > this._children.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds (0 <= index <= " + this._children.size() + ")");
        }

        int flatOffset;
        if (this._children.isEmpty()) {
            this._children = Collections.singletonList(node);
            flatOffset = 0;
        } else {
            if (this._children.size() == 1) {
                this._children = new ArrayList<StringTreeNode>(this._children);
            }

            if (index == 0) {
                flatOffset = 0;
            } else {
                flatOffset = this._children.get(index - 1)._flatChildren.end() - this._flatChildren.offset;
            }

            this._children.add(index, node);
        }
        node._parent = this;
        this._flatChildren.insert(this, node, flatOffset);

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
        // Create a new text buffer for the root node to use
        char[] buffer = this.toCharArray();
        int position = 0;

        // Initialize a new root node
        StringTreeNode clone_root = new StringTreeNode();
        position = clone_root.cloneAssignBuffers(this, buffer, position);

        // Give root node a new FlatChildrenRoot with a copy of the original buffer
        FlatChildrenRoot clone_root_flat_children = new FlatChildrenRoot(this._flatChildren);
        clone_root._flatChildren = clone_root_flat_children;

        // The children of the clone must be offset this amount to be correctly positioned
        int flat_children_offset = -this._flatChildren.offset;

        // Make use of the flat children information to instantly clone all nodes in this subtree
        // This avoids having to use any recursion!
        StringTreeNode[] flat = clone_root_flat_children.flat;
        for (int i = 0; i < flat.length; i++) {
            // Prepare a clone, swap it out in the flat array right away
            StringTreeNode child_clone = new StringTreeNode();
            StringTreeNode child = flat[i];
            flat[i] = child_clone;

            position = child_clone.cloneAssignBuffers(child, buffer, position);

            // For now, store the original node's children. We will correct this later.
            child_clone._children = child._children;

            // Assign the clone a valid FlatChildren section in the array
            child_clone._flatChildren = child._flatChildren.offset(clone_root_flat_children, flat_children_offset);

            // Parent always comes before children, so we can already assign the parent clone
            if (child._parent == this) {
                child_clone._parent = clone_root;
            } else {
                child_clone._parent = flat[child._parent._flatChildren.parent() + flat_children_offset];
            }
        }

        // Now that all children at every depth level have been cloned, re-assign the children
        // Currently they still hold the original information
        clone_root.cloneAssignChildren(this._children, flat, flat_children_offset);
        for (StringTreeNode child : flat) {
            child.cloneAssignChildren(child._children, flat, flat_children_offset);
        }

        return clone_root;
    }

    private int cloneAssignBuffers(StringTreeNode source, char[] buffer, int position) {
        int len = source._buffer.length();
        _buffer.assign(buffer, position, len);
        _totalLength = source._totalLength;
        return position + len;
    }

    private void cloneAssignChildren(List<StringTreeNode> children, StringTreeNode[] flat, int flat_children_offset) {
        int size = children.size();
        if (size == 1) {
            StringTreeNode child_copy = flat[children.get(0)._flatChildren.parent() + flat_children_offset];
            _children = Collections.singletonList(child_copy);
        } else if (size > 1) {
            ArrayList<StringTreeNode> subchildren = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                StringTreeNode subchild_copy = flat[children.get(i)._flatChildren.parent() + flat_children_offset];
                subchildren.add(subchild_copy);
            }
            _children = subchildren;
        }
    }

    /**
     * Concatenates the value of this tree node and all children recursively into
     * a single sequence stored in a {@link CharBuffer}. If there were few changes to the value of this node
     * or the children, this operation is optimized for that.
     * 
     * @return the value of this node and all children concatenated to a CharBuffer
     */
    public CharBuffer toCharBuffer() {
        return CharBuffer.wrap(this.toCharArray());
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

            // Move data of all flat children (recursive children)
            if (_flatChildren.length > 0) {
                StringTreeNode[] flat = _flatChildren.buffer();
                int index = _flatChildren.offset - 1;
                int index_end_minus_one = _flatChildren.end() - 1;
                do {
                    StringTreeNode child = flat[++index];
                    if (child._changed) {
                        child._changed = false;

                        // Changed, move original data to the new buffer
                        position = child._buffer.moveToBuffer(buffer, position);
                    } else {
                        // Unchanged, selfBuffer stores the full String contents we need
                        // This means we can perform a single copy for our own buffer
                        // and the buffer contents of all our children.
                        child._buffer.copyTo(buffer, position, child._totalLength);
                        position = child._buffer.swapBuffer(buffer, position);

                        // Remaining flat children must have their buffers swapped, too
                        // Iterating the flatChildren of this node is more efficient for this
                        int childNumFlatChildren = child._flatChildren.length;
                        while (--childNumFlatChildren >= 0) {
                            position = flat[++index]._buffer.swapBuffer(buffer, position);
                        }
                    }
                } while (index < index_end_minus_one);
            }

            // Done!
            return position;
        } else {
            // Unchanged, selfBuffer stores the full String contents we need
            // This means we can perform a single copy for our own buffer
            // and the buffer contents of all our children.
            _buffer.copyTo(buffer, position, _totalLength);

            // Swap the buffer out for the new buffer, including all recursive (flat) children
            position = _buffer.swapBuffer(buffer, position);
            StringTreeNode[] flat = _flatChildren.buffer();
            int index = _flatChildren.offset - 1;
            int index_end = _flatChildren.end();
            while (++index < index_end) {
                position = flat[index]._buffer.swapBuffer(buffer, position);
            }

            return position;
        }
    }

    private void markChangedIfLengthChanged(int length_change) {
        if (length_change != 0) {
            markChanged(length_change);
        }
    }

    private void markChanged(int length_change) {
        StringTreeNode node = this;
        do {
            node._totalLength += length_change;
            node._changed = true;
        } while ((node = node._parent) != null);
    }

    /**
     * Test function only: verifies that the Flat Children array tracked by this tree
     * is correct. Is very slow and only to be used under test!
     */
    public void verifyFlatChildren() {
        // Find root of tree
        StringTreeNode parent = this;
        while (parent._parent != null) {
            parent = parent._parent;
        }

        // Verify the root array is correct at all - which covers any mistakes in the array itself
        StringTreeNode[] exp_flat_children = parent.computeFlatChildren();
        StringTreeNode[] flat_children = parent._flatChildren.buffer();
        if (exp_flat_children.length > flat_children.length) {
            throw new IllegalStateException("Less flat children (" + flat_children.length +
                    ") than needed (" + exp_flat_children.length + ")");
        }
        for (int i = 0; i < exp_flat_children.length; i++) {
            StringTreeNode cur = flat_children[i];
            StringTreeNode exp = exp_flat_children[i];
            if (cur == exp) {
                continue;
            }

            throw new IllegalStateException("Children are wrong! Expected [" + exp + "] but was [" + cur + "]");
        }

        // Verify each individual entry - this covers incorrect sub-section offsets and lengths
        for (StringTreeNode child : exp_flat_children) {
            int offset = child._flatChildren.parent();
            if (offset < 0) {
                throw new IllegalStateException("Child offset is bad (negative)!: " + offset);
            }
            if (child != child._flatChildren.root.flat[offset]) {
                throw new IllegalStateException("Child offset is bad!");
            }

            if (!Arrays.equals(child._flatChildren.toArray(), child.computeFlatChildren())) {
                throw new IllegalStateException("Oh no the child is bad!");
            }
        }
    }

    // Test only
    private StringTreeNode[] computeFlatChildren() {
        ArrayList<StringTreeNode> tmp = new ArrayList<>(this._flatChildren.length);
        addChildrenRecursive(this, tmp);
        return tmp.toArray(new StringTreeNode[tmp.size()]);
    }

    // Test only
    private void addChildrenRecursive(StringTreeNode node, List<StringTreeNode> dst) {
        for (StringTreeNode child : node._children) {
            dst.add(child);
            addChildrenRecursive(child, dst);
        }
    }

    private static class FlatChildren {
        /** Root child list which stores the actual flat children array */
        public final FlatChildrenRoot root;
        /**
         * Offset into the flat children array of the first child.
         * With the exception of root, the node holding this FlatChildren instance
         * is stored at offset-1.
         */
        public int offset;
        /** Number of flat children that exist */
        public int length;

        // Only used by root
        protected FlatChildren() {
            this.root = (FlatChildrenRoot) this;
            this.offset = 0;
            this.length = 0;
        }

        private FlatChildren(FlatChildrenRoot root, int offset, int length) {
            this.root = root;
            this.offset = offset;
            this.length = length;
        }

        public StringTreeNode[] buffer() {
            return root.flat;
        }

        public StringTreeNode[] toArray() {
            return Arrays.copyOfRange(root.flat, offset, end());
        }

        /**
         * Gets the offset where the parent node of this node is stored.
         * This is offset - 1.
         *
         * @return parent node offset in the flat array
         */
        public int parent() {
            return offset - 1;
        }

        /**
         * Gets the exclusive end index of the last flat child
         *
         * @return end index
         */
        public int end() {
            return offset + length;
        }

        /**
         * Inserts a new range of flat children after this range.
         * The entry offset/length before/after are updated and
         * space is allocated in the flat children array.
         *
         * @param parent Parent node to which a node is added
         * @param node Node to add after this flat children list
         * @param num_flat_children_prior Total number of flat children prior to where this
         *                                new group is inserted. 0 inserts it at the front,
         *                                length at the back.
         */
        public void insert(StringTreeNode parent, StringTreeNode node, int num_flat_children_prior) {
            StringTreeNode[] flat = root.flat;
            int flat_length = root.length;
            int self_offset = this.offset;
            int self_end = self_offset + num_flat_children_prior;

            // Include the node itself being inserted
            int node_num_flat_children = node._flatChildren.length;
            int length_increase = node_num_flat_children + 1;

            // Update the length of recursive parents and offsets of nodes that follow
            adjustOffsets(parent, length_increase, self_end);

            // Original flat children of the node. We want to keep this, as it's efficient to copy
            // Note: only valid when the node is a root node! Must check parent is null, first.
            StringTreeNode[] node_flat_children = node._flatChildren.buffer();

            // Update the flat children array to create a gap for the children to be inserted
            // Already assign the node at the first position
            int new_flat_length = flat_length + length_increase;
            if (new_flat_length > flat.length) {
                // Array must be grown to accommodate the new elements
                // Grow 25% extra to reduce the number of array re-allocations
                StringTreeNode[] flat_updated = new StringTreeNode[(new_flat_length * 4) / 3];
                System.arraycopy(flat, 0, flat_updated, 0, self_end);
                flat_updated[self_end] = node;
                System.arraycopy(node_flat_children, 0, flat_updated, self_end + 1, node_num_flat_children);
                System.arraycopy(flat, self_end, flat_updated, self_end + length_increase, flat_length - self_end);
                root.flat = flat_updated;
            } else {
                // Move some elements around and then copy self + children to the location
                System.arraycopy(flat, self_end, flat, self_end + length_increase, flat_length - self_end);
                flat[self_end] = node;
                System.arraycopy(node_flat_children, 0, flat, self_end + 1, node_num_flat_children);
            }

            // Refresh the FlatChildren instance of the node itself
            FlatChildren new_flatchildren = new FlatChildren(root, self_end + 1, node_num_flat_children);
            new_flatchildren.offsetChildren(self_end + 1);
            node._flatChildren = new_flatchildren;
        }

        /**
         * Removes this FlatChildren entry and its parent node from the flat array
         */
        public FlatChildrenRoot remove(StringTreeNode parent) {
            // Create a new root for the to be removed node
            FlatChildrenRoot new_root = new FlatChildrenRoot(this);

            // Store the before-removal values, as they might change during processing
            StringTreeNode[] flat = root.flat;
            int flat_length = root.length;
            int remove_start = this.parent();
            int remove_end = this.end();
            int length_decrease = this.length + 1;

            // Update the length of recursive parents and offsets of nodes that follow
            adjustOffsets(parent, -length_decrease, remove_start);

            // Update the flat array buffer
            int new_flat_length = flat_length - length_decrease;
            if ((2*new_flat_length) < flat.length) {
                // New size is 50% or less of original buffer size
                // Create a new array to free up some memory
                StringTreeNode[] flat_updated = new StringTreeNode[new_flat_length];
                System.arraycopy(flat, 0, flat_updated, 0, remove_start);
                System.arraycopy(flat, remove_end, flat_updated, remove_start, new_flat_length - remove_start);
                root.flat = flat_updated;
            } else {
                // Move elements around, but keep the original array the same length
                // Wipe elements at the end that remain (set null) to avoid memory leaks
                System.arraycopy(flat, remove_end, flat, remove_start, new_flat_length - remove_start);
                Arrays.fill(flat, new_flat_length, flat_length, null);
            }

            // Assign new flat children to this now-detached node. Create a new root for this.
            new_root.offsetChildren(-this.offset);
            return new_root;
        }

        private void adjustOffsets(StringTreeNode parent, int length_increase, int end_offset) {
            StringTreeNode[] flat = root.flat;
            int flat_length = root.length;

            for (StringTreeNode p = parent; p != null; p = p._parent) {
                p._flatChildren.length += length_increase;
            }

            for (int i = end_offset; i < flat_length; i++) {
                flat[i]._flatChildren.offset += length_increase;
            }
        }

        /**
         * Moves this flat children entry by an offset. Other entries between
         * the old position and the new position are updated as well.
         *
         * @param offset Number of flat child items to move
         */
        public void shift(int move_offset) {
            StringTreeNode[] flat = root.flat;

            // Back up the old contents being removed
            int self_start = this.parent();
            int self_end = this.end();
            StringTreeNode[] flat_moved = Arrays.copyOfRange(flat, self_start, self_end);

            // Move entries that sit within the shifted region up or down
            {
                int shift_src, shift_dst;
                if (move_offset >= 0) {
                    shift_src = self_end;
                    shift_dst = self_start;
                } else {
                    shift_src = self_start + move_offset;
                    shift_dst = self_end + move_offset;
                }

                int shift_change = shift_dst - shift_src;
                int shift_len = Math.abs(move_offset);
                System.arraycopy(flat, shift_src, flat, shift_dst, shift_len);
                for (int i = 0; i < shift_len; i++) {
                    flat[shift_dst + i]._flatChildren.offset += shift_change;
                }
            }

            // Re-insert the entries we moved
            System.arraycopy(flat_moved, 0, flat, self_start + move_offset, self_end - self_start);
            for (StringTreeNode child : flat_moved) {
                child._flatChildren.offset += move_offset;
            }
        }

        public FlatChildren offset(FlatChildrenRoot new_root, int offset_change) {
            return new FlatChildren(new_root, this.offset + offset_change, this.length);
        }

        public void offsetChildren(int offset_change) {
            FlatChildrenRoot root = this.root;
            StringTreeNode[] flat = root.flat;
            int index = this.offset - 1;
            int index_end = this.end();
            while (++index < index_end) {
                StringTreeNode child = flat[index];
                child._flatChildren = child._flatChildren.offset(root, offset_change);
            }
        }
    }

    private static final class FlatChildrenRoot extends FlatChildren {
        private static final StringTreeNode[] NO_FLAT_CHILDREN = new StringTreeNode[0];

        /**
         * Stores a flattened array of all the children, relative to root.
         * All children address a range within this larger array.
         * The array might be larger than the length of this root!
         */
        public StringTreeNode[] flat;

        public FlatChildrenRoot() {
            this.flat = NO_FLAT_CHILDREN;
        }

        public FlatChildrenRoot(FlatChildren copy) {
            this.flat = copy.toArray();
            this.length = copy.length;
        }

        @Override
        public StringTreeNode[] toArray() {
            return flat.clone();
        }
    }
}
