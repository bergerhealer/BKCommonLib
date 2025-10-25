package com.bergerkiller.bukkit.common.math;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.bukkit.util.Vector;

/**
 * A group of vectors stored using SIMD DoubleVector, of any lane length.
 * Can make use of any vector species passed to the constructor
 */
final class VectorListSIMDImpl implements VectorList {

    public static Factory createFactoryFor(final VectorSpecies<Double> species) {
        return new Factory() {
            final int requiredSize = species.length();

            /** Buffer used to instantiate the double vectors efficiently */
            final ThreadLocal<double[]> threadLocalBuffer = ThreadLocal.withInitial(() -> new double[3 * requiredSize]);

            @Override
            public boolean isOptimizedSize(int size) {
                return size == requiredSize;
            }

            @Override
            public int getRequiredSize() {
                return requiredSize;
            }

            @Override
            public VectorList copyOf(VectorList vectorValues) {
                if (
                        vectorValues instanceof VectorListSIMDImpl
                                && ((VectorListSIMDImpl) vectorValues).species == species
                ) {
                    return vectorValues; // Immutable
                } else if (vectorValues.size() != requiredSize) {
                    throw new IllegalArgumentException("This SIMD implementation requires exactly " + requiredSize + " vectors");
                } else {
                    return new VectorListSIMDImpl(species, threadLocalBuffer.get(), vectorValues.vectorIterator());
                }
            }

            @Override
            public VectorList createWith(int size, VectorIterator iterator) {
                if (size != requiredSize) {
                    throw new IllegalArgumentException("This SIMD implementation requires exactly " + requiredSize + " vectors");
                }

                return new VectorListSIMDImpl(species, threadLocalBuffer.get(), iterator);
            }
        };
    }

    private final VectorSpecies<Double> species;
    private final DoubleVector xVec, yVec, zVec;

    VectorListSIMDImpl(VectorSpecies<Double> species, final double[] buffer, VectorIterator iterator) {
        final int size = species.length();

        for (int i = 0; i < size && iterator.advance(); i++) {
            buffer[i] = iterator.x();
            buffer[i + size] = iterator.y();
            buffer[i + size + size] = iterator.z();
        }

        this.species = species;
        this.xVec = DoubleVector.fromArray(species, buffer, 0);
        this.yVec = DoubleVector.fromArray(species, buffer, size);
        this.zVec = DoubleVector.fromArray(species, buffer, size + size);
    }

    @Override
    public int size() {
        return species.length();
    }

    @Override
    public Vector get(int index) {
        return new Vector(
                xVec.lane(index),
                yVec.lane(index),
                zVec.lane(index)
        );
    }

    @Override
    public Vector get(int index, Vector into) {
        into.setX(xVec.lane(index));
        into.setY(yVec.lane(index));
        into.setZ(zVec.lane(index));
        return into;
    }

    @Override
    public VectorIterator vectorIterator() {
        return new VectorIterator() {
            @Override
            public boolean advance() {
                int nextIndex = index + 1;
                if (nextIndex >= size()) return false;

                index = nextIndex;
                x = xVec.lane(index);
                y = yVec.lane(index);
                z = zVec.lane(index);
                return true;
            }
        };
    }

    @Override
    public VectorIterator vectorIterator(final int offset, final int length) {
        return new VectorIterator() {
            @Override
            public boolean advance() {
                int nextIndex = index + 1;
                if (nextIndex >= length) return false;

                index = nextIndex;
                final int pos = index + offset;
                x = xVec.lane(pos);
                y = yVec.lane(pos);
                z = zVec.lane(pos);
                return true;
            }
        };
    }

    @Override
    public Projection projectAxis(double axisX, double axisY, double axisZ) {
        VectorSpecies<Double> species = DoubleVector.SPECIES_PREFERRED;
        DoubleVector ax = DoubleVector.broadcast(species, axisX);
        DoubleVector ay = DoubleVector.broadcast(species, axisY);
        DoubleVector az = DoubleVector.broadcast(species, axisZ);

        DoubleVector projection = xVec.mul(ax)
                .add(yVec.mul(ay))
                .add(zVec.mul(az));

        double min = projection.reduceLanes(VectorOperators.MIN);
        double max = projection.reduceLanes(VectorOperators.MAX);

        return new Projection(min, max);
    }

    @Override
    public void crossProduct(VectorList rightList, VectorConsumer consumer) {
        if (!(rightList instanceof VectorListSIMDImpl)) {
            VectorList.super.crossProduct(rightList, consumer);
            return;
        }

        VectorListSIMDImpl right = (VectorListSIMDImpl) rightList;

        DoubleVector cx = yVec.mul(right.zVec).sub(right.yVec.mul(zVec));
        DoubleVector cy = zVec.mul(right.xVec).sub(right.zVec.mul(xVec));
        DoubleVector cz = xVec.mul(right.yVec).sub(right.xVec.mul(yVec));

        // Write it to the result iterator
        final int size = species.length();
        for (int lane = 0; lane < size; lane++) {
            if (!consumer.acceptVector(cx.lane(lane), cy.lane(lane), cz.lane(lane))) {
                throw new IllegalArgumentException("Consumer can't store the full cross-product result");
            }
        }
    }

    @Override
    public String toString() {
        return VectorListBasicImpl.genericToString(this);
    }
}
