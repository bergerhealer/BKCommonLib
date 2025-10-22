package com.bergerkiller.bukkit.common.math;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Stores all of the vertex points in a simple double[] array
 */
class VertexPointsBasicImpl implements VertexPoints {
    /** Size */
    private final int size;
    /** Stores 3x lanes of the point coordinates */
    private final double[] points;

    public VertexPointsBasicImpl(Collection<Vector> points) {
        this(points.size());

        int startPos = 0;
        for (Vector point : points) {
            set(startPos++, point);
        }
    }

    public VertexPointsBasicImpl(int size) {
        this.size = size;
        this.points = new double[size * 3];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Vector get(int index) {
        double[] points = this.points;
        int size = this.size;
        int xPos = index;
        int yPos = xPos + size;
        int zPos = yPos + size;
        return new Vector(points[xPos], points[yPos], points[zPos]);
    }

    @Override
    public PointIterator pointIterator() {
        return new PointIterator() {
            @Override
            public boolean next() {
                int size = size();
                int pos = index + 1;
                if (pos >= size)
                    return false;

                double[] points = VertexPointsBasicImpl.this.points;
                index = pos;
                x = points[pos];
                pos += size;
                y = points[pos];
                pos += size;
                z = points[pos];
                return true;
            }
        };
    }

    protected void set(int index, Vector value) {
        double[] points = this.points;
        int size = this.size;
        int pos = index;
        points[pos] = value.getX();
        pos += size;
        points[pos] = value.getY();
        pos += size;
        points[pos] = value.getZ();
    }

    public static final class BoxBuilder implements VertexPoints.BoxBuilder {
        private final List<Vector> points;

        public BoxBuilder() {
            points = new ArrayList<Vector>(8);
            for (int n = 0; n < 8; n++) {
                points.add(new Vector());
            }
        }

        @Override
        public VertexPoints.BoxBuilder halfSize(double hsx, double hsy, double hsz) {
            for (int n = 0; n < 8; n++) {
                Vector v = points.get(n);
                v.setX((n & 0x4) != 0 ? hsx : -hsx);
                v.setY((n & 0x2) != 0 ? hsy : -hsy);
                v.setZ((n & 0x1) != 0 ? hsz : -hsz);
            }
            return this;
        }

        @Override
        public VertexPoints.BoxBuilder rotate(Quaternion rotation) {
            for (Vector v : points) {
                rotation.transformPoint(v);
            }
            return this;
        }

        @Override
        public VertexPoints.BoxBuilder translate(Vector translation) {
            for (Vector v : points) {
                v.add(translation);
            }
            return this;
        }

        @Override
        public VertexPoints build() {
            return new VertexPointsBasicImpl(points);
        }
    }
}
