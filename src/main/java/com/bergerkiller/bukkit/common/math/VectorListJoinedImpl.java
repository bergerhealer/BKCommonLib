package com.bergerkiller.bukkit.common.math;

import org.bukkit.util.Vector;

/**
 * A view on two VectorList contents joined together
 */
final class VectorListJoinedImpl implements VectorList {
    private final VectorList first, second;
    private final int size;

    public VectorListJoinedImpl(VectorList first, VectorList second) {
        this.first = first;
        this.second = second;
        this.size = first.size() + second.size();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Vector get(int index) {
        final VectorList first = this.first;
        final int firstSize = first.size();
        if (index < firstSize) {
            return first.get(index);
        } else {
            return second.get(index - firstSize);
        }
    }

    @Override
    public Vector get(int index, Vector into) {
        final VectorList first = this.first;
        final int firstSize = first.size();
        if (index < firstSize) {
            return first.get(index, into);
        } else {
            return second.get(index - firstSize, into);
        }
    }

    @Override
    public VectorIterator vectorIterator(int offset, int length) {
        return rangeVectorIterator(first, second, offset, length);
    }

    @Override
    public VectorIterator vectorIterator() {
        return VectorIterator.join(first.vectorIterator(), second.vectorIterator());
    }

    public static VectorIterator rangeVectorIterator(VectorList first, VectorList second, int offset, int length) {
        final int firstSize = first.size();
        if (offset >= firstSize) {
            return second.vectorIterator(offset - firstSize, length - firstSize);
        } else if ((length + offset) <= firstSize) {
            return first.vectorIterator(offset, length);
        } else {
            VectorIterator ja = first.vectorIterator(offset, firstSize - offset);
            VectorIterator jb = second.vectorIterator(firstSize, length - firstSize);
            return VectorIterator.join(ja, jb);
        }
    }
}
