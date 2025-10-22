package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a read-only collection of 3D vertex point coordinates.
 * Used by the {@link OrientedBoundingBox} to represent the box in 3D
 * and perform more complex calculations.
 */
public interface VertexPoints extends Iterable<Vector> {
    /**
     * Gets a single vertex point coordinate
     *
     * @param index Index of the point (0 - 7)
     * @return Point vector
     */
    Vector get(int index);

    /**
     * Gets the total number of box vertex points
     *
     * @return Point count
     */
    int size();

    /**
     * Turns the List of Vector point coordinates into a VertexPoints representation
     *
     * @param points Points
     * @return new VertexPoints
     */
    static VertexPoints of(Collection<Vector> points) {
        return new VertexPointsBasicImpl(points);
    }

    /**
     * Constructs a new mutable ArrayList with all the points
     *
     * @return List of Vector points
     */
    default List<Vector> asList() {
        int size = size();
        List<Vector> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(get(i));
        }
        return list;
    }

    @Override
    default Iterator<Vector> iterator() {
        return new Iterator<Vector>() {
            private final PointIterator iter = pointIterator();
            private boolean hasNext = iter.next();

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public Vector next() {
                Vector v = iter.toVector();
                hasNext = iter.next();
                return v;
            }
        };
    }

    /**
     * Creates a new PointIterator, which offers a more performant way to iterate
     * the points of this vertex point collection.
     *
     * @return Point Iterator
     */
    default PointIterator pointIterator() {
        return new PointIterator() {
            @Override
            public boolean next() {
                int size = size();
                int nextIndex = index + 1;
                if (nextIndex >= size)
                    return false;

                Vector v = get(nextIndex);
                index = nextIndex;
                x = v.getX();
                y = v.getY();
                z = v.getZ();
                return true;
            }
        };
    }

    /**
     * Calls the callback function with all the points of this vertex point collection
     *
     * @param consumer Callback
     */
    default void forAllPoints(PointConsumer consumer) {
        pointIterator().forEachRemaining(consumer);
    }

    /**
     * Performs a projection of these points onto an axis
     *
     * @param axis Axis Vector
     * @return Projection min / max values
     */
    default Projection projectAxis(Vector axis) {
        return projectAxis(axis.getX(), axis.getY(), axis.getZ());
    }

    /**
     * Performs a projection of these points onto an axis
     *
     * @param axisX X-coordinate of the axis
     * @param axisY Y-coordinate of the axis
     * @param axisZ Z-coordinate of the axis
     * @return Projection min / max values
     */
    default Projection projectAxis(double axisX, double axisY, double axisZ) {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        PointIterator iter = this.pointIterator();
        while (iter.next()) {
            double projection = iter.x() * axisX + iter.y() * axisY + iter.z() * axisZ;
            min = Math.min(min, projection);
            max = Math.max(max, projection);
        }
        return new Projection(min, max);
    }

    /**
     * Checks if two collections of vertex points overlap on a given axis or are separated.
     * Used for the <a href="https://dyn4j.org/2010/01/sat/">Separating Axis Theorem</a>
     *
     * @param vertexPoints1 The vertices of the first collection
     * @param vertexPoints2 The vertices of the second collection
     * @param axisX X-coordinate of the axis
     * @param axisY Y-coordinate of the axis
     * @param axisZ Z-coordinate of the axis
     * @return True if the projections are separated, false if they overlap
     * @see #projectAxis(double, double, double) 
     */
    static boolean hasSeparatedAxes(VertexPoints vertexPoints1, VertexPoints vertexPoints2, double axisX, double axisY, double axisZ) {
        return Projection.isSeparated(
                vertexPoints1.projectAxis(axisX, axisY, axisZ),
                vertexPoints2.projectAxis(axisX, axisY, axisZ)
        );
    }

    /**
     * Checks if two collections of vertex points overlap on a given axis or are separated.
     * Used for the <a href="https://dyn4j.org/2010/01/sat/">Separating Axis Theorem</a>
     *
     * @param vertexPoints1 The vertices of the first collection
     * @param vertexPoints2 The vertices of the second collection
     * @param axis The axis Vector to check
     * @return True if the projections are separated, false if they overlap
     * @see #projectAxis(Vector)
     */
    static boolean hasSeparatedAxes(VertexPoints vertexPoints1, VertexPoints vertexPoints2, Vector axis) {
        return Projection.isSeparated(
                vertexPoints1.projectAxis(axis),
                vertexPoints2.projectAxis(axis)
        );
    }

    /**
     * Tests whether two collections of vertex points overlap or not. Requires an external source providing
     * the normal vector axis for the faces of the vertex points.
     * See: <a href="https://dyn4j.org/2010/01/sat/">Separating Axis Theorem</a>
     *
     * @param vertexPoints1 The vertices of the first collection
     * @param vertexPoints2 The vertices of the second collection
     * @param axisIterator Iterator that produces axis vectors to test for. It's possible it is not
     *                     fully consumed if we discover early there is separation.
     * @return True if the vertices share an overlapped area, False if they are completely separate
     */
    static boolean areVerticesOverlapping(VertexPoints vertexPoints1, VertexPoints vertexPoints2, PointIterator axisIterator) {
        while (axisIterator.next()) {

            if (hasSeparatedAxes(vertexPoints1, vertexPoints2, axisIterator.x(), axisIterator.y(), axisIterator.z())) {
                return false; // Separated axis found - no overlap
            }
        }

        // No separated axis found - they overlap
        return true;
    }

    /**
     * Stores the result of a projection of vertex points onto an axis
     */
    final class Projection {
        public final double min, max;

        public Projection(double min, double max) {
            this.min = min;
            this.max = max;
        }

        public static boolean isSeparated(Projection proj1, Projection proj2) {
            return proj1.max < proj2.min || proj2.max < proj1.min;
        }
    }

    /**
     * A more performant point iteration that avoids the allocation of Vector
     * objects.
     */
    abstract class PointIterator {
        protected int index = -1;
        protected double x, y, z;

        /**
         * Advances to the next point. Returns false when the end of iteration is reached.
         * Call this before accessing the other properties of this class.
         *
         * @return True if the next point is available, False if not
         */
        public abstract boolean next();

        /**
         * Gets the index of the current point. This counts how many iterations
         * have elapsed.
         *
         * @return Index
         */
        public final int index() {
            return index;
        }

        /**
         * Gets the X-coordinate
         *
         * @return X-coordinate
         */
        public final double x() {
            return x;
        }

        /**
         * Gets the Y-coordinate
         *
         * @return Y-coordinate
         */
        public final double y() {
            return y;
        }

        /**
         * Gets the Z-coordinate
         *
         * @return Z-coordinate
         */
        public final double z() {
            return z;
        }

        /**
         * Gets the x/y/z coordinates as a Vector
         *
         * @return Vector
         */
        public final Vector toVector() {
            return new Vector(x, y, z);
        }

        /**
         * Loads the x/y/z coordinates into a Vector
         *
         * @param v Vector to write the x/y/z into
         * @return Vector
         */
        public final Vector toVector(Vector v) {
            v.setX(x);
            v.setY(y);
            v.setZ(z);
            return v;
        }

        /**
         * Calls {@link #next()} until the end is reached and then calls the consumer
         * callback with all of the point information.
         *
         * @param consumer Point Consumer
         */
        public void forEachRemaining(PointConsumer consumer) {
            while (next()) {
                consumer.accept(index, x, y, z);
            }
        }

        /**
         * Loads the x/y/z Vector value into this iterator.
         * For use by the implementation only.
         *
         * @param v Vector
         */
        protected void load(Vector v) {
            x = v.getX();
            y = v.getY();
            z = v.getZ();
        }

        /**
         * Tries to create a cross product between two vectors, and if non-zero, normalizes it and
         * loads it into this iterator and returns true. If not successful (zero-length result),
         * returns false.
         *
         * @param left Left-hand operand for the cross product
         * @param right Right-hand operand for the cross product
         * @return True if a valid normalized cross product was loaded into this iterator
         */
        protected boolean tryLoadNormalizedCrossProduct(Vector left, Vector right) {
            double x = left.getY() * right.getZ() - right.getY() * left.getZ();
            double y = left.getZ() * right.getX() - right.getZ() * left.getX();
            double z = left.getX() * right.getY() - right.getX() * left.getY();
            double lengthSquared = (x*x) + (y*y) + (z*z);
            if (lengthSquared < 1e-6) {
                return false; // Axes are parallel, no need to test
            } else {
                double normFactor = MathUtil.getNormalizationFactorLS(lengthSquared);
                this.x = x * normFactor;
                this.y = y * normFactor;
                this.z = z * normFactor;
                return true;
            }
        }
    }

    /**
     * Simple 3D vector point consumer
     */
    @FunctionalInterface
    interface PointConsumer {
        void accept(int index, double x, double y, double z);
    }
}
