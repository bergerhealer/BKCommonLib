package com.bergerkiller.bukkit.common.math;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.bukkit.util.Vector;

/**
 * Implementation of 8-size VectorList optimized for 512 bit SIMD
 */
final class VectorListOctoSIMD512Impl implements VectorList {
    private static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_512;
    public static final Factory FACTORY = new Factory() {
        @Override
        public VectorList copyOf(VectorList vectorValues) {
            if (vectorValues instanceof VectorListOctoSIMD512Impl) {
                return vectorValues; // Immutable
            } else {
                return new VectorListOctoSIMD512Impl(vectorValues.size(), vectorValues.vectorIterator());
            }
        }

        @Override
        public VectorList createWith(int size, VectorIterator iterator) {
            return new VectorListOctoSIMD512Impl(size, iterator);
        }
    };

    private final DoubleVector xVec, yVec, zVec;

    private VectorListOctoSIMD512Impl(int size, VectorIterator iterator) {
        if (size != 8) {
            throw new IllegalArgumentException("This SIMD512 implementation requires exactly 8 points");
        }

        double[] xArr = new double[8];
        double[] yArr = new double[8];
        double[] zArr = new double[8];

        for (int i = 0; i < 8 && iterator.advance(); i++) {
            xArr[i] = iterator.x();
            yArr[i] = iterator.y();
            zArr[i] = iterator.z();
        }

        this.xVec = DoubleVector.fromArray(SPECIES, xArr, 0);
        this.yVec = DoubleVector.fromArray(SPECIES, yArr, 0);
        this.zVec = DoubleVector.fromArray(SPECIES, zArr, 0);
    }

    @Override
    public int size() {
        return 8;
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
                if (nextIndex >= 8) return false;

                index = nextIndex;
                x = xVec.lane(index);
                y = yVec.lane(index);
                z = zVec.lane(index);
                return true;
            }
        };
    }

    @Override
    public Projection projectAxis(double axisX, double axisY, double axisZ) {
        DoubleVector ax = DoubleVector.broadcast(SPECIES, axisX);
        DoubleVector ay = DoubleVector.broadcast(SPECIES, axisY);
        DoubleVector az = DoubleVector.broadcast(SPECIES, axisZ);

        DoubleVector projection = xVec.mul(ax)
                .add(yVec.mul(ay))
                .add(zVec.mul(az));

        double min = projection.reduceLanes(VectorOperators.MIN);
        double max = projection.reduceLanes(VectorOperators.MAX);

        return new Projection(min, max);
    }

    @Override
    public String toString() {
        return VectorListBasicImpl.genericToString(this);
    }
}
