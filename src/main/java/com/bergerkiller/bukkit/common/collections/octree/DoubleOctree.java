package com.bergerkiller.bukkit.common.collections.octree;

import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.utils.MathUtil;

/**
 * Wraps the {@link Octree} to store data at double x/y/z coordinates, instead of int.
 * This is done by giving each 1x1x1 area a linked list of entries stored there.
 * If a lot of values are stored inside single 1x1x1 areas, this container is not recommended.<br>
 * <br>
 * Within 1x1x1 blocks, when iterating the elements, the sorting of the elements will be ascending.
 * First sorted by x-coordinate, then by y-coordinate, then by z-coordinate.<br>
 * <br>
 * The block coordinate of a coordinate is the Double floor value.
 * 
 * @param <T> The value type of the DoubleOctree
 */
public class DoubleOctree<T> implements DoubleOctreeIterable<T> {
    private final Octree<Entry<T>> tree;
    private final DoubleOctreeIterator<T> remove_iter;

    public DoubleOctree() {
        this.tree = new Octree<Entry<T>>();
        this.remove_iter = new DoubleOctreeIterator<T>(this.tree.remove_iter);
    }

    /**
     * Clears all the contents of this Double Octree, freeing all memory associated with it.
     */
    public void clear() {
        this.tree.clear();
    }

    @Override
    public DoubleOctreeIterator<T> iterator() {
        return new DoubleOctreeIterator<T>(this.tree.iterator());
    }

    /**
     * Gets a view of the contents of this tree that lay inside a cuboid area.
     * The maximum coordinate includes all values inside that block of values.
     * If this is {1,2,3} then this will includes coordinates such as {1.2, 2.99, 3.0}.
     * 
     * @param min coordinates of the cuboid (inclusive)
     * @param max coordinates of the cuboid (inclusive)
     * @return iterable
     */
    public DoubleOctreeIterable<T> cuboid(final IntVector3 min, final IntVector3 max) {
        return new DoubleOctreeIterable<T>() {
            @Override
            public DoubleOctreeIterator<T> iterator() {
                return new DoubleOctreeIterator<T>(new OctreeCuboidIterator<Entry<T>>(DoubleOctree.this.tree, min, max));
            }
        };
    }

    /**
     * Gets a view of the contents of this tree that lay inside a single 1x1x1 block area.
     * The maximum coordinate includes all values inside that block of values.
     * If this is {1,2,3} then this will includes coordinates such as {1.2, 2.99, 3.0}.
     * 
     * @param block coordinate
     * @return iterable
     */
    public DoubleOctreeIterable<T> block(final IntVector3 block) {
        return new DoubleOctreeIterable<T>() {
            @Override
            public DoubleOctreeIterator<T> iterator() {
                return new DoubleOctreeIterator<T>(new OctreePointIterator<Entry<T>>(DoubleOctree.this.tree, block));
            }
        };
    }

    /**
     * Checks whether a value is stored at the coordinates specified.
     * Also returns true if the value stored is null.
     * 
     * @param x The X-coordinate
     * @param y The Y-coordinate
     * @param z The Z-coordinate
     * @return True if a value is stored at the coordinates, False if not
     */
    public boolean contains(double x, double y, double z) {
        return this.getEntry(x, y, z) != null;
    }

    /**
     * Gets the value stored at the specified coordinates.
     * 
     * @param x The X-coordinate
     * @param y The Y-coordinate
     * @param z The Z-coordinate
     * @return Value stored at these coordinates. Null if nothing, or null, is stored.
     */
    public T get(double x, double y, double z) {
        return value(this.getEntry(x, y, z));
    }

    /**
     * Puts a new entry at the specified coordinates, returning the previous value
     * 
     * @param x The X-coordinate
     * @param y The Y-coordinate
     * @param z The Z-coordinate
     * @param value to store
     * @return previous value at these coordinates, null if none existed
     */
    public T put(double x, double y, double z, T value) {
        return value(putEntry(new Entry<T>(x, y, z, value)));
    }

    /**
     * Removes an entry stored at the specified coordinates, returning the value of the entry
     * removed.
     * 
     * @param x The Y-coordinate
     * @param y The Y-coordinate
     * @param z The Y-coordinate
     * @return previous value at these coordinates, null if none existed
     */
    public T remove(double x, double y, double z) {
        return value(removeEntry(x, y, z));
    }

    /**
     * Moves an entry from the old position to a new position as efficiently as possible.
     * The lookup is optimized in several ways to speed this up:
     * <ul>
     * <li>If the position is unchanged, only the value is updated
     * <li>If the old and new position are in the same 1x1x1 block, chain lookup is only done once
     * <li>Traversing the underlying tree is done while excluding regions that contain neither old nor new entry
     * </ul>
     * 
     * @param oldX      The X-coordinate of the old position
     * @param oldY      The Y-coordinate of the old position
     * @param oldZ      The Z-coordinate of the old position
     * @param newX      The X-coordinate of the desired new position
     * @param newY      The Y-coordinate of the desired new position
     * @param newZ      The Z-coordinate of the desired new position
     * @param newValue  The value to store at the new position
     * @return the result of the move, which will be SUCCESS if the tree was changed
     * @see {@link #moveEntry(Entry, Entry)}
     */
    public MoveResult move(double oldX, double oldY, double oldZ, double newX, double newY, double newZ, T newValue) {
        return moveEntry(new Entry<T>(oldX, oldY, oldZ, null), new Entry<T>(newX, newY, newZ, newValue));
    }

    /**
     * Checks whether the value stored at the coordinates of an entry,
     * match the value of the entry. The {@link Object#equals(Object)} method
     * is used to perform the final check. If the value of the entry
     * and of what is stored are both null, the two are assumed equal.
     * 
     * @param entry The entry to find
     * @return True if the entry specified is contained, False if not
     */
    public boolean containsEntry(Entry<T> entry) {
        Entry<T> existing = this.getEntry(entry.getX(), entry.getY(), entry.getZ());
        if (existing == null) {
            return false;
        } else if (existing == entry) {
            return true;
        } else if (existing.getValue() == null) {
            return entry.getValue() == null;
        } else {
            return existing.getValue().equals(entry.getValue());
        }
    }

    /**
     * Gets the entry stored at the coordinates specified
     * 
     * @param x The X-coordinate
     * @param y The Y-coordinate
     * @param z The Z-coordinate
     * @return Entry stored at these coordinates. Null if nothing is stored.
     */
    public Entry<T> getEntry(double x, double y, double z) {
        Entry<T> entry = this.tree.get(MathUtil.floor(x), MathUtil.floor(y), MathUtil.floor(z));
        while (entry != null && (entry.getX() != x || entry.getY() != y || entry.getZ() != z)) {
            entry = entry.next;
        }
        return entry;
    }

    /**
     * Puts a new entry into this tree. The entry can be reused
     * for other uses to reduce the memory footprint, since it is immutable.
     * 
     * @param value
     * @return previous entry at the coordinates of the entry, if one existed
     */
    public Entry<T> putEntry(Entry<T> value) {
        int index = this.tree.getValueIndex(MathUtil.floor(value.getX()),
                                            MathUtil.floor(value.getY()),
                                            MathUtil.floor(value.getZ()), true);

        // Retrieve entry, if it is null, then nothing was stored in this 1x1x1 block
        Entry<T> currentEntry = this.tree.getValueAtIndex(index);
        if (currentEntry == null) {
            this.tree.putValueAtIndex(index, value);
            return null;
        }

        // Go by all the sorted entries in the chain until we find it
        Entry<T> previous = null;
        do {
            int compare = currentEntry.compareTo(value);
            if (compare >= 0) {
                // Found an entry equal to or beyond the one we want
                if (previous == null) {
                    // Insert as first entry in the tree
                    previous = value;
                    this.tree.putValueAtIndex(index, previous);
                } else {
                    // Insert in between the previous and current entry
                    previous.next = value;
                }
                if (compare == 0) {
                    // Remove the current entry (and return it)
                    value.next = currentEntry.next;
                    currentEntry.next = null;
                    return currentEntry;
                } else {
                    // Nothing was replaced
                    value.next = currentEntry;
                    return null;
                }
            }

            // Keep looking
            previous = currentEntry;
            currentEntry = currentEntry.next;
        } while (currentEntry != null);

        // Append to the end of the chain
        previous.next = value;
        value.next = null;
        return null;
    }

    /**
     * Removes an entry stored at the coordinates specified, returning the entry that was
     * removed.
     * 
     * @param x The X-coordinate
     * @param y The Y-coordinate
     * @param z The Z-coordinate
     * @return removed entry, or null if no entry was stored here
     */
    public Entry<T> removeEntry(double x, double y, double z) {
        this.remove_iter.reset();
        this.tree.remove_iter.reset(MathUtil.floor(x), MathUtil.floor(y), MathUtil.floor(z));
        while (this.remove_iter.hasNext()) {
            Entry<T> entry = this.remove_iter.nextEntry();
            int compare = entry.compareTo(x, y, z);
            if (compare == 0) {
                this.remove_iter.remove();
                return entry;
            } else if (compare > 0) {
                break;
            }
        }
        return null;
    }

    /**
     * Moves an entry from the old position to a new position as efficiently as possible.
     * The lookup is optimized in several ways to speed this up:
     * <ul>
     * <li>If the position is unchanged, only the value is updated
     * <li>If the old and new position are in the same 1x1x1 block, chain lookup is only done once
     * <li>Traversing the underlying tree is done while excluding regions that contain neither old nor new entry
     * </ul>
     * 
     * @param oldEntry to replace
     * @param newEntry to replace oldEntry with
     * @return the result of the move, which will be SUCCESS if the tree was changed
     */
    public MoveResult moveEntry(Entry<T> oldEntry, Entry<T> newEntry) {
        // If unchanged, only verify that the old entry exists, and replace the value
        if (oldEntry.equalsCoord(newEntry.getX(), newEntry.getY(), newEntry.getZ())) {
            if (this.putEntry(newEntry) == null) {
                this.removeEntry(newEntry.getX(), newEntry.getY(), newEntry.getZ());
                return MoveResult.NOT_FOUND;
            } else {
                return MoveResult.SUCCESS;
            }
        }

        // Find the 1x1x1 block coordinates and value index of the old and new entry
        int oldBlockX = MathUtil.floor(oldEntry.getX());
        int oldBlockY = MathUtil.floor(oldEntry.getY());
        int oldBlockZ = MathUtil.floor(oldEntry.getZ());
        int newBlockX = MathUtil.floor(newEntry.getX());
        int newBlockY = MathUtil.floor(newEntry.getY());
        int newBlockZ = MathUtil.floor(newEntry.getZ());
        int oldIndex = this.tree.getValueIndex(oldBlockX, oldBlockY, oldBlockZ, false);
        if (oldIndex == 0) {
            return MoveResult.NOT_FOUND;
        }

        // If block is unchanged, we only have to find a single entry and modify the chain
        if (oldBlockX == newBlockX && oldBlockY == newBlockY && oldBlockZ == newBlockZ) {
            return moveEntryInChain(oldIndex, oldEntry, newEntry);
        }

        // TODO: Optimize this lookup, since we can walk the root of the tree once for both blocks
        int newIndex = this.tree.getValueIndex(newBlockX, newBlockY, newBlockZ, true);
        MoveResult result = moveEntryBetweenChains(oldIndex, newIndex, oldEntry, newEntry);
        if (result == MoveResult.NOT_FOUND && this.tree.getValueAtIndex(newIndex) == null) {
            this.tree.remove(newBlockX, newBlockY, newBlockZ);
        }
        return result;
    }

    // Moves an entry from one chain to another
    private MoveResult moveEntryBetweenChains(int indexFrom, int indexTo, Entry<T> oldEntry, Entry<T> newEntry) {
        // Find the entry that comes before the node to remove from the old chain
        boolean entryBeforeOldEntryIsFirstEntry;
        Entry<T> entryBeforeOldEntry;
        {
            Entry<T> currentEntry = this.tree.getValueAtIndex(indexFrom);
            int compare = currentEntry.compareTo(oldEntry);
            if (compare == 0) {
                // First entry of chain, use setValueAtIndex to update it
                entryBeforeOldEntry = currentEntry;
                entryBeforeOldEntryIsFirstEntry = true;
            } else if (compare > 0) {
                // Expected before this entry, so missing
                return MoveResult.NOT_FOUND;
            } else {
                // Find it in the chain
                while (true) {
                    if (currentEntry.next == null || (compare = currentEntry.next.compareTo(oldEntry)) > 0) {
                        return MoveResult.NOT_FOUND;
                    }
                    if (compare == 0) {
                        entryBeforeOldEntry = currentEntry;
                        entryBeforeOldEntryIsFirstEntry = false;
                        break;
                    }
                    currentEntry = currentEntry.next;
                }
            }
        }

        // Find out if the new entry already exists, or otherwise, what entry to stick it after
        {
            Entry<T> currentEntry = this.tree.getValueAtIndex(indexTo);
            if (currentEntry == null) {
                // Store at the beginning
                newEntry.next = null;
                this.tree.putValueAtIndex(indexTo, newEntry);
            } else {
                int compare = currentEntry.compareTo(newEntry);
                if (compare < 0) {
                    // Insert at the beginning
                    newEntry.next = currentEntry;
                    this.tree.putValueAtIndex(indexTo, newEntry);
                } else if (compare == 0) {
                    return MoveResult.TARGET_OCCUPIED;
                } else {
                    // Walk the chain and attempt to insert it
                    while (true) {
                        if (currentEntry.next == null || (compare = currentEntry.next.compareTo(newEntry)) < 0) {
                            newEntry.next = currentEntry.next;
                            currentEntry.next = newEntry;
                            break;
                        } else if (compare == 0) {
                            return MoveResult.TARGET_OCCUPIED;
                        }
                        currentEntry = currentEntry.next;
                    }
                }
            }
        }

        // We can remove the old entry now we have successfully stored the new entry
        if (entryBeforeOldEntryIsFirstEntry) {
            this.tree.putValueAtIndex(indexFrom, entryBeforeOldEntry.next);
        } else {
            entryBeforeOldEntry.next = entryBeforeOldEntry.next.next;
        }

        return MoveResult.SUCCESS;
    }

    // Moves an entry inside the chain of the same 1x1x1 block
    private MoveResult moveEntryInChain(int index, Entry<T> oldEntry, Entry<T> newEntry) {
        final Entry<T> entryAtIndex = this.tree.getValueAtIndex(index);
        int compare;

        // Special care with the very first entry of the chain
        {
            // Check whether the old entry is the start of the chain, or 'before' (missing)
            if ((compare = entryAtIndex.compareTo(oldEntry)) == 0) {
                // Shortcut when only a single entry is stored in a 1x1x1 block, or when
                // the new entry is put in the front of the chain
                Entry<T> currentEntry = entryAtIndex.next;
                if (currentEntry == null || (compare = currentEntry.compareTo(newEntry)) > 0) {
                    newEntry.next = currentEntry;
                    this.tree.putValueAtIndex(index, newEntry);
                    return MoveResult.SUCCESS;
                } else if (compare == 0) {
                    // The next entry is the same as the new entry, then we cannot store it
                    return MoveResult.TARGET_OCCUPIED;
                }

                // Find where in the chain we must store the next entry
                while (currentEntry.next != null) {
                    compare = currentEntry.next.compareTo(newEntry);
                    if (compare > 0) {
                        // Put it right after this entry
                        break;
                    } else if (compare == 0) {
                        return MoveResult.TARGET_OCCUPIED;
                    } else {
                        currentEntry = currentEntry.next;
                    }
                }

                // Insert after the entry where we stopped
                newEntry.next = currentEntry.next;
                currentEntry.next = newEntry;
                this.tree.putValueAtIndex(index, entryAtIndex.next);
                return MoveResult.SUCCESS;
            } else if (compare > 0) {
                return MoveResult.NOT_FOUND;
            }

            // Check whether the new entry should be the new start of the chain
            if ((compare = entryAtIndex.compareTo(newEntry)) > 0) {
                // We know entryAtIndex is not oldEntry, but find where the oldEntry is, then
                Entry<T> currentEntry = entryAtIndex;
                while (currentEntry.next != null) {
                    compare = currentEntry.next.compareTo(oldEntry);
                    if (compare == 0) {
                        // Found it! Do the swap!
                        newEntry.next = entryAtIndex;
                        currentEntry.next = currentEntry.next.next;
                        this.tree.putValueAtIndex(index, newEntry);
                        return MoveResult.SUCCESS;
                    } else if (compare > 0) {
                        // Old entry was expected here, but isn't
                        return MoveResult.NOT_FOUND;
                    } else {
                        currentEntry = currentEntry.next;
                    }
                }

                // Could not find oldEntry in the chain
                return MoveResult.NOT_FOUND;
            } else if (compare == 0) {
                return MoveResult.TARGET_OCCUPIED;
            }
        }

        // At this point we know we are replacing entries inside the chain
        // Do we expect to find a place for the old entry before or after the new entry?
        if (oldEntry.compareTo(newEntry) < 0) {
            // We expect to find the old entry before the new entry
            Entry<T> entryBeforeOldEntry = null;

            // Loop until we find the old entry
            Entry<T> currentEntry = entryAtIndex;
            while (true) {
                if (currentEntry.next == null || (compare = currentEntry.next.compareTo(oldEntry)) > 0) {
                    return MoveResult.NOT_FOUND;
                } else if (compare == 0) {
                    entryBeforeOldEntry = currentEntry;
                    currentEntry = currentEntry.next.next;
                    break;
                } else {
                    currentEntry = currentEntry.next;
                }
            }

            // First loop, check if the new entry directly follows the parent of the old entry
            // In that case the new entry will replace the old entry
            if (currentEntry == null || (compare = currentEntry.compareTo(newEntry)) > 0) {
                newEntry.next = currentEntry;
                entryBeforeOldEntry.next = newEntry;
                return MoveResult.SUCCESS;
            } else if (compare == 0) {
                return MoveResult.TARGET_OCCUPIED;
            }

            // Loop until we find a place for the new entry
            while (true) {
                if (currentEntry.next == null || (compare = currentEntry.next.compareTo(newEntry)) > 0) {
                    newEntry.next = currentEntry.next;
                    entryBeforeOldEntry.next = entryBeforeOldEntry.next.next;
                    currentEntry.next = newEntry;
                    return MoveResult.SUCCESS;
                } else if (compare == 0) {
                    return MoveResult.TARGET_OCCUPIED;
                } else {
                    currentEntry = currentEntry.next;
                }
            }
        } else {
            // We expect to find the new entry before the old entry
            Entry<T> entryBeforeNewEntry = null;

            // Loop until we find a place for the new entry
            Entry<T> currentEntry = entryAtIndex;
            while (true) {
                if (currentEntry.next == null || (compare = currentEntry.next.compareTo(newEntry)) > 0) {
                    entryBeforeNewEntry = currentEntry;
                    currentEntry = currentEntry.next;
                    break;
                } else if (compare == 0) {
                    return MoveResult.TARGET_OCCUPIED;
                } else {
                    currentEntry = currentEntry.next;
                }
            }

            // First loop, check if the old entry directly follows the parent of the new entry
            // In that case we must carefully reorder the entries
            if (currentEntry == null || (compare = currentEntry.compareTo(oldEntry)) > 0) {
                return MoveResult.NOT_FOUND;
            } else if (compare == 0) {
                newEntry.next = currentEntry.next;
                entryBeforeNewEntry.next = newEntry;
                return MoveResult.SUCCESS;
            }

            // Loop until we find the old entry
            while (true) {
                if (currentEntry.next == null || (compare = currentEntry.next.compareTo(oldEntry)) > 0) {
                    return MoveResult.NOT_FOUND;
                } else if (compare == 0) {
                    newEntry.next = entryBeforeNewEntry.next;
                    currentEntry.next = currentEntry.next.next;
                    entryBeforeNewEntry.next = newEntry;
                    return MoveResult.SUCCESS;
                } else {
                    currentEntry = currentEntry.next;
                }
            }
        }
    }

    private static <T> T value(Entry<T> entry) {
        return (entry != null) ? entry.getValue() : null;
    }

    /**
     * Immutable entry in a Double Octree.
     * The properties of this object will not change when the tree
     * is later modified.
     * 
     * @param <T> value type
     */
    public static final class Entry<T> implements Comparable<Entry<T>> {
        private final double x;
        private final double y;
        private final double z;
        private final T value;
        protected Entry<T> next;

        public Entry(Vector position, T value) {
            this(position.getX(), position.getY(), position.getZ(), value);
        }

        public Entry(double x, double y, double z, T value) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.value = value;
            this.next = null;
        }

        /**
         * Gets the X-coordinate of where the value is stored
         * 
         * @return X-coordinate
         */
        public double getX() {
            return this.x;
        }

        /**
         * Gets the Y-coordinate of where the value is stored
         * 
         * @return Y-coordinate
         */
        public double getY() {
            return this.y;
        }

        /**
         * Gets the Z-coordinate of where the value is stored
         * 
         * @return Z-coordinate
         */
        public double getZ() {
            return this.z;
        }

        /**
         * Gets the value being stored
         * 
         * @return value
         */
        public T getValue() {
            return this.value;
        }

        @Override
        public int compareTo(Entry<T> other) {
            return this.compareTo(other.x, other.y, other.z);
        }

        /**
         * Compares the x/y/z of this entry with the x/y/z specified.
         * 
         * @param x The X-coordinate
         * @param y The Y-coordinate
         * @param z The Z-coordinate
         * @return 0, less than 0 or greater than 0 depending on order
         * @see {@link #compareTo(Entry)}
         */
        public int compareTo(double x, double y, double z) {
            int comp;
            if ((comp = Double.compare(this.x, x)) == 0) {
                if ((comp = Double.compare(this.y, y)) == 0) {
                    comp = Double.compare(this.z, z);
                }
            }
            return comp;
        }

        /**
         * Gets whether the x/y/z of this entry equals the x/y/z specified
         * 
         * @param x The X-coordinate
         * @param y The Y-coordinate
         * @param z The Z-coordinate
         * @return True if the coordinates are equal
         */
        public boolean equalsCoord(double x, double y, double z) {
            return this.x == x && this.y == y && this.z == z;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            } else if (other instanceof Entry) {
                Entry<?> otherEntry = (Entry<?>) other;
                if (this.getX() != otherEntry.getX() ||
                    this.getY() != otherEntry.getY() |
                    this.getZ() != otherEntry.getZ())
                {
                    return false;
                }
                if (this.getValue() == null) {
                    return otherEntry.getValue() == null;
                } else {
                    return this.getValue().equals(otherEntry.getValue());
                }
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            //TODO: Not so good?
            return Double.hashCode(this.x) + (Double.hashCode(this.z) << 8) + (Double.hashCode(this.y) << 16);
        }

        @Override
        public String toString() {
            return "{x:" + this.x + ", y:" + this.y + ", z:" + this.z + ", value:" + this.value + "}";
        }
    }

    /**
     * A possible outcome of the {@link #moveEntry()} operation
     */
    public static enum MoveResult {
        /**
         * Operation was successful
         */
        SUCCESS,
        /**
         * The original entry could not be found
         */
        NOT_FOUND,
        /**
         * The target position is already occupied by a different entry
         */
        TARGET_OCCUPIED
    }
}
