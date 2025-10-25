package com.bergerkiller.bukkit.common.math;

import jdk.incubator.vector.DoubleVector;
import org.bukkit.util.Vector;

/**
 * Implementation of 8-size VectorList optimized for 256 bit SIMD. Makes use of two instances
 * of {@link VectorListSIMDImpl} under the hood.
 */
final class VectorListSIMD256DoubledImpl implements VectorList {
    public static final Factory FACTORY = new Factory() {
        @Override
        public int getRequiredSize() {
            return 8;
        }

        @Override
        public VectorList copyOf(VectorList vectorValues) {
            if (vectorValues instanceof VectorListSIMD256DoubledImpl) {
                return vectorValues; // Immutable
            } else if (vectorValues.size () != 8) {
                throw new IllegalArgumentException("This SIMD Doubled implementation requires exactly 8 points");
            } else {
                return new VectorListSIMD256DoubledImpl(vectorValues.vectorIterator());
            }
        }

        @Override
        public VectorList createWith(int size, VectorIterator iterator) {
            if (size != 8) {
                throw new IllegalArgumentException("This SIMD Doubled implementation requires exactly 8 points");
            }
            return new VectorListSIMD256DoubledImpl(iterator);
        }
    };

    private final VectorListSIMDImpl a, b;

    /** Buffer used to instantiate the double vectors efficiently */
    private static final ThreadLocal<double[]> threadLocalBuffer = ThreadLocal.withInitial(() -> new double[3 * 4]);

    private VectorListSIMD256DoubledImpl(VectorIterator iterator) {
        final double[] buffer = threadLocalBuffer.get();
        a = new VectorListSIMDImpl(DoubleVector.SPECIES_256, buffer, iterator);
        b = new VectorListSIMDImpl(DoubleVector.SPECIES_256, buffer, iterator);
    }

    @Override
    public int size() {
        return 8;
    }

    @Override
    public Vector get(int index) {
        if (index < 4) {
            return a.get(index);
        } else {
            return b.get(index - 4);
        }
    }

    @Override
    public Vector get(int index, Vector into) {
        if (index < 4) {
            a.get(index, into);
        } else {
            b.get(index - 4, into);
        }
        return into;
    }

    @Override
    public VectorIterator vectorIterator() {
        return VectorIterator.join(a.vectorIterator(), b.vectorIterator());
    }

    @Override
    public VectorIterator vectorIterator(int offset, int length) {
        return VectorListJoinedImpl.rangeVectorIterator(a, b, offset, length);
    }

    @Override
    public Projection projectAxis(double axisX, double axisY, double axisZ) {
        Projection pa = a.projectAxis(axisX, axisY, axisZ);
        Projection pb = b.projectAxis(axisX, axisY, axisZ);
        return new Projection(Math.min(pa.min, pb.min), Math.max(pa.max, pb.max));
    }

    @Override
    public void crossProduct(VectorList rightList, VectorConsumer consumer) {
        if (!(rightList instanceof VectorListSIMD256DoubledImpl)) {
            VectorList.super.crossProduct(rightList, consumer);
            return;
        }

        VectorListSIMD256DoubledImpl right = (VectorListSIMD256DoubledImpl) rightList;
        a.crossProduct(right.a, consumer);
        b.crossProduct(right.b, consumer);
    }

    @Override
    public String toString() {
        return VectorListBasicImpl.genericToString(this);
    }
}
