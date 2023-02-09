package com.bergerkiller.bukkit.common.collections.octree;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
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
     * Gets an unmodifiable view of all the values inside this tree
     * 
     * @return values
     */
    public Collection<T> values() {
        return new DoubleOctreeValues<T>(this.tree.values());
    }

    private static final class DoubleOctreeValues<T> extends AbstractCollection<T> {
        private final Collection<Entry<T>> rootEntries;

        public DoubleOctreeValues(Collection<Entry<T>> rootEntries) {
            this.rootEntries = rootEntries;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private Iterator<Entry<T>> rootEntryIter = rootEntries.iterator();
                private Entry<T> nextEntry = null;

                @Override
                public boolean hasNext() {
                    if (this.nextEntry == null) {
                        if (this.rootEntryIter.hasNext()) {
                            this.nextEntry = this.rootEntryIter.next();
                        } else {
                            return false;
                        }
                    }
                    return true;
                }

                @Override
                public T next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException("No more elements inside the tree");
                    }
                    T value = this.nextEntry.getValue();
                    this.nextEntry = this.nextEntry.next;
                    return value;
                }
            };
        }

        @Override
        public int size() {
            int size = 0;
            Iterator<T> iter = this.iterator();
            while (iter.hasNext()) {
                size++;
                iter.next();
            }
            return size;
        }
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
        return () -> new DoubleOctreeIterator<T>(new OctreeCuboidIterator<Entry<T>>(DoubleOctree.this.tree, min, max));
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
        return () -> new DoubleOctreeIterator<T>(new OctreePointIterator<Entry<T>>(DoubleOctree.this.tree, block));
    }

    /**
     * Checks whether a value is stored at the coordinates specified.
     * 
     * @param x      The X-coordinate
     * @param y      The Y-coordinate
     * @param z      The Z-coordinate
     * @param value  The value expected to be stored here
     * @return True if a value is stored at the coordinates, False if not
     */
    public boolean contains(double x, double y, double z, Object value) {
        Entry<T> entry = this.tree.get(MathUtil.floor(x), MathUtil.floor(y), MathUtil.floor(z));
        if (entry != null) {
            do {
                int compare = entry.compareTo(x, y, z);
                if (compare > 0) {
                    return false;
                } else if (compare == 0 && LogicUtil.bothNullOrEqual(entry.getValue(), value)) {
                    return true;
                }
            } while ((entry = entry.next) != null);
        }
        return false;
    }

    /**
     * Gets the values stored at the specified coordinates.
     * 
     * @param x The X-coordinate
     * @param y The Y-coordinate
     * @param z The Z-coordinate
     * @return Values stored at these coordinates
     */
    public Collection<T> get(double x, double y, double z) {
        Entry<T> firstEntry = this.getFirstEntry(x, y, z);
        return (firstEntry == null) ? Collections.emptyList() : new PositionCollection<T>(firstEntry);
    }

    /**
     * Adds a new entry at the specified coordinates
     * 
     * @param x      The X-coordinate
     * @param y      The Y-coordinate
     * @param z      The Z-coordinate
     * @param value  The value to store
     */
    public void add(double x, double y, double z, T value) {
        addEntry(new Entry<T>(x, y, z, value));
    }

    /**
     * Removes an entry stored at the coordinates specified, returning the entry that was
     * removed.
     * 
     * @param x      The X-coordinate
     * @param y      The Y-coordinate
     * @param z      The Z-coordinate
     * @param value  The value of the entry to remove
     * @return True if an entry was found and removed, False otherwise
     */
    public boolean remove(double x, double y, double z, Object value) {
        this.remove_iter.reset();
        this.tree.remove_iter.reset(MathUtil.floor(x), MathUtil.floor(y), MathUtil.floor(z));
        while (this.remove_iter.hasNext()) {
            Entry<T> entry = this.remove_iter.nextEntry();
            int compare = entry.compareTo(x, y, z);
            if (compare > 0) {
                break;
            } else if (compare == 0 && LogicUtil.bothNullOrEqual(entry.getValue(), value)) {
                this.remove_iter.remove();
                return true;
            } else if (compare > 0) {
                break;
            }
        }
        return false;
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
     * @param oldValue  The value stored at the old position
     * @param newX      The X-coordinate of the desired new position
     * @param newY      The Y-coordinate of the desired new position
     * @param newZ      The Z-coordinate of the desired new position
     * @param newValue  The value to store at the new position
     * @return True if the old value was found and moved, False otherwise
     * @see {@link #moveEntry(Entry, Entry)}
     */
    public boolean move(double oldX, double oldY, double oldZ, T oldValue, double newX, double newY, double newZ, T newValue) {
        return moveEntry(new Entry<T>(oldX, oldY, oldZ, oldValue), new Entry<T>(newX, newY, newZ, newValue));
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
     * @param value     The value stored at the old position that will be moved to a new position
     * @param newX      The X-coordinate of the desired new position
     * @param newY      The Y-coordinate of the desired new position
     * @param newZ      The Z-coordinate of the desired new position
     * @return True if the old value was found and moved, False otherwise
     * @see {@link #moveEntry(Entry, Entry)}
     */
    public boolean move(double oldX, double oldY, double oldZ, T value, double newX, double newY, double newZ) {
        return moveEntry(new Entry<T>(oldX, oldY, oldZ, value), new Entry<T>(newX, newY, newZ, value));
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
    public boolean containsEntry(Entry<?> entry) {
        Entry<T> existing = this.getFirstEntry(entry.getX(), entry.getY(), entry.getZ());
        if (existing == null) {
            return false;
        } else if (existing.valueEquals(entry)) {
            return true;
        }
        existing = existing.next;
        while (existing != null && existing.equalsCoord(entry.getX(), entry.getY(), entry.getZ())) {
            if (existing.valueEquals(entry)) {
                return true;
            } else {
                existing = existing.next;
            }
        }
        return false;
    }

    /**
     * Gets the first entry stored that represents the coordinates specified
     * 
     * @param x The X-coordinate
     * @param y The Y-coordinate
     * @param z The Z-coordinate
     * @return First entry stored at these coordinates. Null if nothing is stored.
     */
    private Entry<T> getFirstEntry(double x, double y, double z) {
        int compare;
        Entry<T> entry = this.tree.get(MathUtil.floor(x), MathUtil.floor(y), MathUtil.floor(z));
        while (true) {
            if (entry == null || (compare = entry.compareTo(x, y, z)) > 0) {
                return null;
            } else if (compare == 0) {
                return entry;
            } else {
                entry = entry.next;
            }
        }
    }

    /**
     * Puts a new entry into this tree. The entry can be reused
     * for other uses to reduce the memory footprint, since it is immutable.
     * 
     * @param value
     */
    @SuppressWarnings("unchecked")
    public void addEntry(Entry<? extends T> value) {
        int index = this.tree.getValueIndex(value.getBlockX(),
                                            value.getBlockY(),
                                            value.getBlockZ(), true);

        // Retrieve entry, if it is null, then nothing was stored in this 1x1x1 block
        Entry<T> currentEntry = this.tree.getValueAtIndex(index);
        if (currentEntry == null) {
            this.tree.putValueAtIndex(index, (Entry<T>) value);
            return;
        }

        // Go by all the sorted entries in the chain until we find it
        Entry<T> previous = null;
        do {
            int compare = currentEntry.compareTo(value);
            if (compare >= 0) {
                // Found an entry equal to or beyond the one we want
                if (previous == null) {
                    // Insert as first entry in the tree
                    previous = (Entry<T>) value;
                    this.tree.putValueAtIndex(index, previous);
                } else {
                    // Insert in between the previous and current entry
                    previous.next = (Entry<T>) value;
                }
                ((Entry<T>) value).next = currentEntry;
                return;
            }

            // Keep looking
            previous = currentEntry;
            currentEntry = currentEntry.next;
        } while (currentEntry != null);

        // Append to the end of the chain
        previous.next = (Entry<T>) value;
        value.next = null;
    }

    /**
     * Removes an entry stored inside this octree.
     * 
     * @param entry The entry to remove
     * @return True if an entry was found and removed, False otherwise
     */
    public boolean removeEntry(Entry<?> entry) {
        return remove(entry.getX(), entry.getY(), entry.getZ(), entry.getValue());
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
     * @param oldEntry to replace, null to only add a new entry
     * @param newEntry to replace oldEntry with, null to remove the old entry
     * @return the result of the move, which will be SUCCESS if the tree was changed
     */
    @SuppressWarnings("unchecked")
    public boolean moveEntry(Entry<? extends T> oldEntry, Entry<? extends T> newEntry) {
        // Special cases where we do not move, but instead add or remove
        if (oldEntry == null) {
            if (newEntry != null) {
                this.addEntry(newEntry);
            }
            return true;
        } else if (newEntry == null) {
            return this.removeEntry(oldEntry);
        } else if (oldEntry.equals(newEntry)) {
            return this.containsEntry(oldEntry);
        }

        // Find the 1x1x1 block coordinates and value index of the old and new entry
        int oldBlockX = oldEntry.getBlockX();
        int oldBlockY = oldEntry.getBlockY();
        int oldBlockZ = oldEntry.getBlockZ();
        int newBlockX = newEntry.getBlockX();
        int newBlockY = newEntry.getBlockY();
        int newBlockZ = newEntry.getBlockZ();
        int oldIndex = this.tree.getValueIndex(oldBlockX, oldBlockY, oldBlockZ, false);
        if (oldIndex == 0) {
            return false;
        }

        // If block is unchanged, we only have to find a single entry and modify the chain
        if (oldBlockX == newBlockX && oldBlockY == newBlockY && oldBlockZ == newBlockZ) {
            return moveEntryInChain(oldIndex, (Entry<T>) oldEntry, (Entry<T>) newEntry);
        }

        // Remove the entry at the old block
        {
            Entry<T> currentEntry = this.tree.getValueAtIndex(oldIndex);
            int compare = currentEntry.compareTo(oldEntry);
            if (compare == 0 && currentEntry.valueEquals(oldEntry)) {
                // First entry of chain, use setValueAtIndex to update it
                if (currentEntry.next == null) {
                    this.tree.remove(oldBlockX, oldBlockY, oldBlockZ);
                } else {
                    this.tree.putValueAtIndex(oldIndex, currentEntry.next);
                }
            } else if (compare > 0) {
                // Expected before this entry, so missing
                return false;
            } else {
                // Find it in the chain
                while (true) {
                    if (currentEntry.next == null || (compare = currentEntry.next.compareTo(oldEntry)) > 0) {
                        return false;
                    } else if (compare == 0 && currentEntry.next.valueEquals(oldEntry)) {
                        currentEntry.next = currentEntry.next.next;
                        break;
                    } else {
                        currentEntry = currentEntry.next;
                    }
                }
            }
        }

        // Add the new entry
        this.addEntry(newEntry);
        return true;
    }

    // Moves an entry inside the chain of the same 1x1x1 block
    private boolean moveEntryInChain(int index, Entry<T> oldEntry, Entry<T> newEntry) {
        final Entry<T> entryAtIndex = this.tree.getValueAtIndex(index);
        int compare;

        // Special care with the very first entry of the chain
        {
            // Check whether the old entry is the start of the chain, or 'before' (missing)
            if ((compare = entryAtIndex.compareTo(oldEntry)) == 0 && entryAtIndex.valueEquals(oldEntry)) {
                // Shortcut when only a single entry is stored in a 1x1x1 block, or when
                // the new entry is put in the front of the chain
                Entry<T> currentEntry = entryAtIndex.next;
                if (currentEntry == null || (compare = currentEntry.compareTo(newEntry)) > 0) {
                    newEntry.next = currentEntry;
                    this.tree.putValueAtIndex(index, newEntry);
                    return true;
                }

                // Find where in the chain we must store the next entry
                while (currentEntry.next != null && (compare = currentEntry.next.compareTo(newEntry)) < 0) {
                    currentEntry = currentEntry.next;
                }

                // Insert after the entry where we stopped
                newEntry.next = currentEntry.next;
                currentEntry.next = (Entry<T>) newEntry;
                this.tree.putValueAtIndex(index, entryAtIndex.next);
                return true;
            } else if (compare > 0) {
                return false;
            }

            // Check whether the new entry should be the new start of the chain
            if ((compare = entryAtIndex.compareTo(newEntry)) >= 0) {
                // We know entryAtIndex is not oldEntry, but find where the oldEntry is, then
                Entry<T> currentEntry = entryAtIndex;
                while (currentEntry.next != null) {
                    compare = currentEntry.next.compareTo(oldEntry);
                    if (compare == 0 && currentEntry.next.valueEquals(oldEntry)) {
                        // Found it! Do the swap!
                        newEntry.next = entryAtIndex;
                        currentEntry.next = currentEntry.next.next;
                        this.tree.putValueAtIndex(index, newEntry);
                        return true;
                    } else if (compare > 0) {
                        // Old entry was expected here, but isn't
                        return false;
                    } else {
                        currentEntry = currentEntry.next;
                    }
                }

                // Could not find oldEntry in the chain
                return false;
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
                    return false;
                } else if (compare == 0 && currentEntry.next.valueEquals(oldEntry)) {
                    entryBeforeOldEntry = currentEntry;
                    currentEntry = currentEntry.next.next;
                    break;
                } else {
                    currentEntry = currentEntry.next;
                }
            }

            // First loop, check if the new entry directly follows the parent of the old entry
            // In that case the new entry will replace the old entry
            if (currentEntry == null || (compare = currentEntry.compareTo(newEntry)) >= 0) {
                newEntry.next = currentEntry;
                entryBeforeOldEntry.next = newEntry;
                return true;
            }

            // Loop until we find a place for the new entry
            while (true) {
                if (currentEntry.next == null || (compare = currentEntry.next.compareTo(newEntry)) >= 0) {
                    newEntry.next = currentEntry.next;
                    entryBeforeOldEntry.next = entryBeforeOldEntry.next.next;
                    currentEntry.next = newEntry;
                    return true;
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
                if (currentEntry.next == null || (compare = currentEntry.next.compareTo(newEntry)) >= 0) {
                    entryBeforeNewEntry = currentEntry;
                    currentEntry = currentEntry.next;
                    break;
                } else {
                    currentEntry = currentEntry.next;
                }
            }

            // First loop, check if the old entry directly follows the parent of the new entry
            // In that case we must carefully reorder the entries
            if (currentEntry == null || (compare = currentEntry.compareTo(oldEntry)) > 0) {
                return false;
            } else if (compare == 0 && currentEntry.valueEquals(oldEntry)) {
                newEntry.next = currentEntry.next;
                entryBeforeNewEntry.next = newEntry;
                return true;
            }

            // Loop until we find the old entry
            while (true) {
                if (currentEntry.next == null || (compare = currentEntry.next.compareTo(oldEntry)) > 0) {
                    return false;
                } else if (compare == 0 && currentEntry.next.valueEquals(oldEntry)) {
                    newEntry.next = entryBeforeNewEntry.next;
                    currentEntry.next = currentEntry.next.next;
                    entryBeforeNewEntry.next = newEntry;
                    return true;
                } else {
                    currentEntry = currentEntry.next;
                }
            }
        }
    }

    /**
     * Collection of values at a single x/y/z coordinate. Must store at least one
     * entry.
     */
    private static final class PositionCollection<T> extends AbstractCollection<T> {
        private final Entry<T> root;

        public PositionCollection(Entry<T> root) {
            this.root = root;
        }

        private Entry<T> getNext(Entry<T> entry) {
            Entry<T> next = entry.next;
            if (next != null &&
                next.getX() == this.root.getX() &&
                next.getY() == this.root.getY() &&
                next.getZ() == this.root.getZ())
            {
                return next;
            } else {
                return null;
            }
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                Entry<T> next = root;

                @Override
                public boolean hasNext() {
                    return this.next != null;
                }

                @Override
                public T next() {
                    if (this.next == null) {
                        throw new NoSuchElementException("No next element available");
                    }
                    T result = this.next.getValue();
                    this.next = getNext(this.next);
                    return result;
                }
            };
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            int size = 0;
            Entry<T> entry = this.root;
            do {
                size++;
            } while ((entry = getNext(entry)) != null);
            return size;
        }
    }

    /**
     * Immutable entry in a Double Octree.
     * The properties of this object will not change when the tree
     * is later modified.
     * 
     * @param <T> value type
     */
    public static final class Entry<T> implements Comparable<Entry<?>> {
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
         * Gets the X-coordinate of the 1x1x1 block where the value is stored
         * 
         * @return Block X-coordinate
         */
        public int getBlockX() {
            return MathUtil.floor(this.x);
        }

        /**
         * Gets the Y-coordinate of the 1x1x1 block where the value is stored
         * 
         * @return Block Y-coordinate
         */
        public int getBlockY() {
            return MathUtil.floor(this.y);
        }

        /**
         * Gets the Z-coordinate of the 1x1x1 block where the value is stored
         * 
         * @return Block Z-coordinate
         */
        public int getBlockZ() {
            return MathUtil.floor(this.z);
        }

        /**
         * Gets a new 3D Vector storing the x/y/z coordinates of where the value is stored
         * 
         * @return coordinates vector
         */
        public Vector toVector() {
            return new Vector(this.x, this.y, this.z);
        }

        /**
         * Gets the squared distance between the position of this entry and the
         * coordinates specified.
         * 
         * @param x The X-coordinate
         * @param y The Y-coordinate
         * @param z The Z-coordinate
         * @return Distance squared between this entry and the coordinates specified
         */
        public double distanceSquared(double x, double y, double z) {
            return MathUtil.distanceSquared(this.x, this.y, this.z, x, y, z);
        }

        /**
         * Gets the squared distance between the position of this entry and the
         * coordinates specified.
         * 
         * @param pos The coordinates
         * @return Distance squared between this entry and the coordinates specified
         */
        public double distanceSquared(Vector pos) {
            return distanceSquared(pos.getX(), pos.getY(), pos.getZ());
        }

        /**
         * Gets the distance between the position of this entry and the
         * coordinates specified.
         * 
         * @param x The X-coordinate
         * @param y The Y-coordinate
         * @param z The Z-coordinate
         * @return Distance between this entry and the coordinates specified
         */
        public double distance(double x, double y, double z) {
            return Math.sqrt(distanceSquared(x, y, z));
        }

        /**
         * Gets the distance between the position of this entry and the
         * coordinates specified.
         * 
         * @param pos The coordinates
         * @return Distance between this entry and the coordinates specified
         */
        public double distance(Vector pos) {
            return distance(pos.getX(), pos.getY(), pos.getZ());
        }

        /**
         * Gets the value being stored
         * 
         * @return value
         */
        public T getValue() {
            return this.value;
        }

        /**
         * Gets whether the value of this entry equals the value specified.
         * Also returns true if both are null.
         * 
         * @param value
         * @return True if equals
         */
        public boolean valueEquals(Object value) {
            return LogicUtil.bothNullOrEqual(this.value, value);
        }

        /**
         * Gets whether the value of this entry equals the value in the entry specified.
         * Also returns true if both are null.
         * 
         * @param entry
         * @return True if equals
         */
        public boolean valueEquals(Entry<?> entry) {
            return this == entry || valueEquals(entry.getValue());
        }

        @Override
        public int compareTo(Entry<?> other) {
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

        /**
         * Gets whether the x/y/z of this entry equals the x/y/z in the vector specified
         * 
         * @param pos The coordinates to compare against
         * @return True if the coordinates are equal
         */
        public boolean equalsCoord(Vector pos) {
            return this.x == pos.getX() && this.y == pos.getY() && this.z == pos.getZ();
        }

        /**
         * Gets whether the x/y/z coordinates of the 1x1x1 block this entry is stored in equals
         * the x/y/z block coordinates specified
         * 
         * @param x The block X-coordinate
         * @param y The block Y-coordinate
         * @param z The block Z-coordinate
         * @return True if the coordinates are equal
         */
        public boolean equalsBlockCoord(int x, int y, int z) {
            return this.getBlockX() == x && this.getBlockY() == y && this.getBlockZ() == z;
        }

        /**
         * Gets whether the x/y/z coordinates of the 1x1x1 block this entry is stored in equals
         * the x/y/z block coordinates specified
         * 
         * @param blockCoord The coordinates
         * @return True if the coordinates are equal
         */
        public boolean equalsBlockCoord(IntVector3 blockCoord) {
            return equalsBlockCoord(blockCoord.x, blockCoord.y, blockCoord.z);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            } else if (other instanceof Entry) {
                Entry<?> otherEntry = (Entry<?>) other;
                return this.getX() == otherEntry.getX() &&
                       this.getY() == otherEntry.getY() &&
                       this.getZ() == otherEntry.getZ() &&
                       this.valueEquals(otherEntry.getValue());
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

        /**
         * Creates a new entry
         * 
         * @param pos    The position of the entry
         * @param value  The value of the entry
         * @return New Entry
         */
        public static <T> Entry<T> create(Vector pos, T value) {
            return new Entry<T>(pos, value);
        }

        /**
         * Creates a new entry
         * 
         * @param x      The X-coordinate of the entry
         * @param y      The Y-coordinate of the entry
         * @param z      The Z-coordinate of the entry
         * @param value  The value of the entry
         * @return New Entry
         */
        public static <T> Entry<T> create(double x, double y, double z, T value) {
            return new Entry<T>(x, y, z, value);
        }
    }
}
