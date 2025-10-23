package com.bergerkiller.bukkit.common.math;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.bukkit.util.Vector;

import java.util.Collection;

/**
 * Implementation of 8-vertex count VertexPoints optimized for 512 bit SIMD
 */
final class VertexPointsSIMD512Impl implements VertexPoints {
    private static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_512;

    private final DoubleVector xVec, yVec, zVec;

    public VertexPointsSIMD512Impl(Collection<Vector> points) {
        if (points.size() != 8) {
            throw new IllegalArgumentException("This SIMD512 implementation requires exactly 8 points");
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
    public PointIterator pointIterator() {
        return new PointIterator() {
            @Override
            public boolean next() {
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

    public static class BoxBuilder extends VertexPointsBasicImpl.BoxBuilder {
        @Override
        public VertexPoints build() {
            return new VertexPointsSIMD512Impl(points);
        }
    }
}
