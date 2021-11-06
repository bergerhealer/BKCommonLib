package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;

/**
 * Tracks the item frames that require updates and/or checking
 * of whether the item in the item frame has changed. Offers a method
 * to prioritize an item frame, and otherwise checks the item frame
 * less often.
 */
class ItemFrameUpdateList {
    private ItemFrameInfo.UpdateEntry first = null;
    private ItemFrameInfo.UpdateEntry last = null;

    /**
     * Gets the first update entry - the start of the chain
     * of all entries.
     *
     * @return first entry
     */
    public ItemFrameInfo.UpdateEntry first() {
        return first;
    }

    /**
     * Adds a new item frame entry, is automatically prioritized
     */
    public void add(ItemFrameInfo.UpdateEntry entry) {
        entry.added = true;
        entry.prioritized = true;
        if (first == null) {
            first = last = entry;
        } else {
            first.prev = entry;
            entry.next = first;
            first = entry;
        }
    }

    /**
     * Moves an update entry to the beginning of the list and
     * prioritizes it.
     *
     * @param entry
     */
    public void prioritize(ItemFrameInfo.UpdateEntry entry) {
        if (!entry.prioritized && entry.added) {
            entry.prioritized = true;
            if (entry != first) {
                if (entry == last) {
                    last = entry.prev;
                    last.next = null;
                } else {
                    detachMiddle(entry);
                }
                first.prev = entry;
                entry.next = first;
                entry.prev = null;
                first = entry;
            }
        }
    }

    /**
     * Moves a range of entries from the start to the end of the linked list, so they
     * will be updated last. The entries should have prioritized set to false before.
     *
     * @param chainStart Start of the chain (inclusive)
     * @param chainEnd End of the chain (inclusive)
     */
    public void moveRangeToEnd(ItemFrameInfo.UpdateEntry chainStart, ItemFrameInfo.UpdateEntry chainEnd) {
        if (chainEnd != last) {
            if (chainStart == first) {
                first = chainEnd.next;
                first.prev = null;
            } else {
                chainStart.prev.next = chainEnd.next;
                chainEnd.next.prev = chainStart.prev;
            }
            last.next = chainStart;
            chainStart.prev = last;
            last = chainEnd;
            chainEnd.next = null;
        }
    }

    /**
     * Removes an entry so it is no longer updated
     *
     * @param entry Entry to remove
     */
    public void remove(ItemFrameInfo.UpdateEntry entry) {
        if (entry.added) {
            entry.added = false;
            if (entry == first) {
                // Remove head (no prev)
                first = entry.next;
                if (first != null) {
                    first.prev = null;
                    entry.next = null;
                } else {
                    last = null; // empty
                }
            } else if (entry == last) {
                // Remove tail (no next)
                last = entry.prev;
                if (last != null) {
                    last.next = null;
                    entry.prev = null;
                }
            } else {
                // Remove in the middle
                detachMiddle(entry);
            }
        }
    }

    private static void detachMiddle(ItemFrameInfo.UpdateEntry entry) {
        entry.prev.next = entry.next;
        entry.next.prev = entry.prev;
    }
}
