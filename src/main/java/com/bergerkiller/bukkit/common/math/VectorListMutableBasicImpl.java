package com.bergerkiller.bukkit.common.math;

/**
 * A mutable Vector list implementation backed by a double[] array.
 */
class VectorListMutableBasicImpl extends VectorListBasicImpl implements VectorListMutable {

    public VectorListMutableBasicImpl(VectorList vectors) {
        super(vectors);
    }

    public VectorListMutableBasicImpl(int size, VectorList.VectorIterator iter) {
        super(size, iter);
    }

    public VectorListMutableBasicImpl(int size) {
        super(size);
    }

    @Override
    public void set(int index, double x, double y, double z) {
        final double[] points = this.points;
        int pos = 3 * index;
        points[pos++] = x;
        points[pos++] = y;
        points[pos] = z;
    }

    @Override
    public MutableVectorIterator vectorIterator() {
        return new MutableVectorIterator() {
            @Override
            public void set(double x, double y, double z) {
                VectorListMutableBasicImpl.this.set(index, x, y, z);
            }

            @Override
            public boolean advance() {
                final int nextIndex = index + 1;
                if (nextIndex >= size()) {
                    return false;
                }

                index = nextIndex;

                int pos = 3 * nextIndex;
                final double[] points = VectorListMutableBasicImpl.this.points;
                x = points[pos++];
                y = points[pos++];
                z = points[pos];
                return true;
            }
        };
    }

    @Override
    public MutableVectorIterator vectorIterator(final int offset, final int length) {
        return new MutableVectorIterator() {
            int position = -1;

            @Override
            public void set(double x, double y, double z) {
                final double[] points = VectorListMutableBasicImpl.this.points;
                int pos = this.position;
                points[pos++] = x;
                points[pos++] = y;
                points[pos] = z;
            }

            @Override
            public boolean advance() {
                final int nextIndex = index + 1;
                if (nextIndex >= length) {
                    return false;
                }

                index = nextIndex;

                int position = 3 * (nextIndex + offset);
                this.position = position;

                final double[] points = VectorListMutableBasicImpl.this.points;
                x = points[position++];
                y = points[position++];
                z = points[position];
                return true;
            }
        };
    }
}
