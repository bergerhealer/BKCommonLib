package com.bergerkiller.bukkit.common.math;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.bukkit.util.Vector;

import java.util.Collection;

/**
 * Implementation of 8-vertex count VertexPoints optimized for 256 bit SIMD
 */
final class VertexPointsSIMD256Impl implements VertexPoints {
    private static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_256;

    private final DoubleVector x0, x1, y0, y1, z0, z1;

    public VertexPointsSIMD256Impl(Collection<Vector> points) {
        if (points.size() != 8) {
            throw new IllegalArgumentException("This SIMD256 implementation requires exactly 8 points");
        }

        double[] xArr = new double[8];
        double[] yArr = new double[8];
        double[] zArr = new double[8];

        int i = 0;
        for (Vector v : points) {
            xArr[i] = v.getX();
            yArr[i] = v.getY();
            zArr[i] = v.getZ();
            i++;
        }

        this.x0 = DoubleVector.fromArray(SPECIES, xArr, 0);
        this.x1 = DoubleVector.fromArray(SPECIES, xArr, 4);
        this.y0 = DoubleVector.fromArray(SPECIES, yArr, 0);
        this.y1 = DoubleVector.fromArray(SPECIES, yArr, 4);
        this.z0 = DoubleVector.fromArray(SPECIES, zArr, 0);
        this.z1 = DoubleVector.fromArray(SPECIES, zArr, 4);
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
    public PointIterator pointIterator() {
        return new PointIterator() {
            @Override
            public boolean next() {
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

    public static class BoxBuilder extends VertexPointsBasicImpl.BoxBuilder {
        @Override
        public VertexPoints build() {
            return new VertexPointsSIMD256Impl(points);
        }
    }
}