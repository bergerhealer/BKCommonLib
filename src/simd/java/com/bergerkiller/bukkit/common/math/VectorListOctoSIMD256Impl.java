package com.bergerkiller.bukkit.common.math;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.bukkit.util.Vector;

import java.util.Arrays;

/**
 * Implementation of 8-size VectorList optimized for 256 bit SIMD
 */
final class VectorListOctoSIMD256Impl implements VectorList {
    private static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_256;
    public static final Factory FACTORY = new Factory() {
        @Override
        public VectorList copyOf(VectorList vectorValues) {
            if (vectorValues instanceof VectorListOctoSIMD256Impl) {
                return vectorValues; // Immutable
            } else {
                return new VectorListOctoSIMD256Impl(vectorValues.size(), vectorValues.vectorIterator());
            }
        }

        @Override
        public VectorList createWith(int size, VectorIterator iterator) {
            return new VectorListOctoSIMD256Impl(size, iterator);
        }
    };

    private final DoubleVector x0, x1, y0, y1, z0, z1;

    private VectorListOctoSIMD256Impl(int size, VectorIterator iterator) {
        if (size != 8) {
            throw new IllegalArgumentException("This SIMD256 implementation requires exactly 8 points");
        }

        double[] tmp = new double[12];

        for (int i = 0; i < 4 && iterator.advance(); i++) {
            tmp[i] = iterator.x();
            tmp[i + 4] = iterator.y();
            tmp[i + 8] = iterator.z();
        }
        this.x0 = DoubleVector.fromArray(SPECIES, tmp, 0);
        this.y0 = DoubleVector.fromArray(SPECIES, tmp, 4);
        this.z0 = DoubleVector.fromArray(SPECIES, tmp, 8);

        for (int i = 0; i < 4; i++) {
            if (iterator.advance()) {
                tmp[i] = iterator.x();
                tmp[i + 4] = iterator.y();
                tmp[i + 8] = iterator.z();
            } else {
                tmp[i] = 0.0;
                tmp[i + 4] = 0.0;
                tmp[i + 8] = 0.0;
            }
        }
        this.x1 = DoubleVector.fromArray(SPECIES, tmp, 0);
        this.y1 = DoubleVector.fromArray(SPECIES, tmp, 4);
        this.z1 = DoubleVector.fromArray(SPECIES, tmp, 8);
    }

    @Override
    public int size() {
        return 8;
    }

    @Override
    public Vector get(int index) {
        if (index < 4) {
            return new Vector(
                    x0.lane(index),
                    y0.lane(index),
                    z0.lane(index)
            );
        } else {
            int i = index - 4;
            return new Vector(
                    x1.lane(i),
                    y1.lane(i),
                    z1.lane(i)
            );
        }
    }

    @Override
    public Vector get(int index, Vector into) {
        if (index < 4) {
            into.setX(x0.lane(index));
            into.setY(y0.lane(index));
            into.setZ(z0.lane(index));
        } else {
            int i = index - 4;
            into.setX(x1.lane(i));
            into.setY(y1.lane(i));
            into.setZ(z1.lane(i));
        }
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
                if (index < 4) {
                    x = x0.lane(index);
                    y = y0.lane(index);
                    z = z0.lane(index);
                } else {
                    int i = index - 4;
                    x = x1.lane(i);
                    y = y1.lane(i);
                    z = z1.lane(i);
                }
                return true;
            }
        };
    }

    @Override
    public Projection projectAxis(double axisX, double axisY, double axisZ) {
        DoubleVector ax = DoubleVector.broadcast(SPECIES, axisX);
        DoubleVector ay = DoubleVector.broadcast(SPECIES, axisY);
        DoubleVector az = DoubleVector.broadcast(SPECIES, axisZ);

        DoubleVector proj0 = x0.mul(ax).add(y0.mul(ay)).add(z0.mul(az));
        DoubleVector proj1 = x1.mul(ax).add(y1.mul(ay)).add(z1.mul(az));

        double min = Math.min(
                proj0.reduceLanes(VectorOperators.MIN),
                proj1.reduceLanes(VectorOperators.MIN)
        );
        double max = Math.max(
                proj0.reduceLanes(VectorOperators.MAX),
                proj1.reduceLanes(VectorOperators.MAX)
        );

        return new Projection(min, max);
    }

    @Override
    public String toString() {
        return VectorListBasicImpl.genericToString(this);
    }
}
