package com.bergerkiller.bukkit.common.math;

import org.bukkit.util.Vector;

/**
 * An immutable Vector list implementation backed by a double[] array.
 */
class VectorListBasicImpl implements VectorList {
    /** Size */
    protected final int size;
    /** Stores 3x lanes of the point coordinates */
    protected final double[] points;

    protected VectorListBasicImpl(VectorList vectors) {
        this.size = vectors.size();
        if (vectors instanceof VectorListBasicImpl) {
            VectorListBasicImpl vectorsImpl = (VectorListBasicImpl) vectors;
            this.points = vectorsImpl.points.clone();
        } else {
            this.points = new double[3 * this.size];
            load(vectors.vectorIterator());
        }
    }

    protected VectorListBasicImpl(int size, VectorList.VectorIterator iter) {
        this(size);
        load(iter);
    }

    protected VectorListBasicImpl(int size) {
        this.size = size;
        this.points = new double[3 * size];
    }

    private void load(VectorList.VectorIterator iter) {
        int pos = 0;
        final double[] points = this.points;
        while (iter.advance()) {
            points[pos++] = iter.x();
            points[pos++] = iter.y();
            points[pos++] = iter.z();
        }
    }

    @Override
    public final int size() {
        return size;
    }

    @Override
    public final Vector get(int index) {
        final double[] points = this.points;
        int pos = 3 * index;
        double x = points[pos++];
        double y = points[pos++];
        double z = points[pos];
        return new Vector(x, y, z);
    }

    @Override
    public final Vector get(int index, Vector into) {
        final double[] points = this.points;
        int pos = 3 * index;
        into.setX(points[pos++]);
        into.setY(points[pos++]);
        into.setZ(points[pos]);
        return into;
    }

    @Override
    public VectorIterator vectorIterator() {
        return new VectorIterator() {
            @Override
            public boolean advance() {
                final int nextIndex = index + 1;
                if (nextIndex >= size()) {
                    return false;
                }

                index = nextIndex;

                int pos = 3 * nextIndex;
                final double[] points = VectorListBasicImpl.this.points;
                x = points[pos++];
                y = points[pos++];
                z = points[pos];
                return true;
            }
        };
    }

    @Override
    public String toString() {
        return genericToString(this);
    }

    public static String genericToString(VectorList vectorList) {
        StringBuilder str = new StringBuilder();
        VectorIterator iter = vectorList.vectorIterator();
        str.append(vectorList.getClass().getSimpleName())
                .append("<size=").append(vectorList.size()).append("> [");
        while (iter.advance()) {
            str.append("\n  {")
                    .append(iter.x()).append(" / ")
                    .append(iter.y()).append(" / ")
                    .append(iter.z()).append("}");
        }
        str.append("\n]");
        return str.toString();
    }
}
