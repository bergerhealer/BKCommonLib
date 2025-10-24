package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.stream.Collector;

/**
 * An immutable VectorList. Size is initialized up-front, and it has various
 * streaming methods to fill up the initial values.
 */
public interface VectorList extends Iterable<Vector> {
    /**
     * Factory for initializing new VectorList objects with various forms of initialization.
     * Includes optimizations for SIMD, if the JDK vector API is enabled and available
     * on this system.
     */
    Factory FACTORY = VectorListFactorySelector.initFactory();

    /**
     * Creates a new immutable copy of an existing vector list
     *
     * @param vectorValues List of vector values
     * @return New VectorList
     */
    static VectorList copyOf(VectorList vectorValues) {
        return FACTORY.copyOf(vectorValues);
    }

    /**
     * Creates a new immutable copy of an existing vector list
     *
     * @param vectorValues List of vector values
     * @return New VectorList
     */
    static VectorList copyOf(Collection<Vector> vectorValues) {
        return FACTORY.copyOf(vectorValues);
    }

    /**
     * Creates a new immutable vector list, initialized with the values from
     * the iterator specified. The size must be known up-front.
     *
     * @param size Size of the vector list
     * @param iterator Iterator to fill the vector list. If not enough values
     *                 are found, the remainder are left uninitialized (0/0/0)
     * @return New VectorList
     */
    static VectorList createWith(int size, VectorList.VectorIterator iterator) {
        return FACTORY.createWith(size, iterator);
    }

    /**
     * Gets the number of vectors stored in this VectorList
     *
     * @return Size
     */
    int size();

    /**
     * Gets a single vector as a Bukkit Vector
     *
     * @param index Vector index
     * @return New Vector
     */
    Vector get(int index);

    /**
     * Gets a single vector as a Bukkit Vector
     *
     * @param index Vector index
     * @param into Vector to load the value into
     * @return Input into Vector
     */
    Vector get(int index, Vector into);

    /**
     * Gets a more performant vector iterator that avoids the allocation of Vector
     * objects.
     *
     * @return VectorIterator
     */
    VectorIterator vectorIterator();

    @Override
    default Iterator<Vector> iterator() {
        return new Iterator<Vector>() {
            VectorIterator iter = null;
            boolean hasNext;

            @Override
            public boolean hasNext() {
                if (iter == null) {
                    iter = vectorIterator();
                    hasNext = iter.advance();
                }
                return hasNext;
            }

            @Override
            public Vector next() {
                if (!hasNext) {
                    throw new NoSuchElementException();
                }
                Vector value = iter.toVector();
                hasNext = iter.advance();
                return value;
            }
        };
    }

    /**
     * Collects all values as Bukkit Vector values, filling the collector
     * specified.
     *
     * @param collector Stream Collector, e.g. Collector.toList()
     * @return Collection
     * @param <R> Returned collection type
     * @param <A> Accumulator type
     */
    default <R, A> R collect(Collector<? super Vector, A, R> collector) {
        return vectorIterator().collectRemaining(collector);
    }

    /**
     * Calls the callback function with all the vectors of this vector list
     *
     * @param consumer Callback
     */
    default void forEach(VectorConsumer consumer) {
        vectorIterator().forEachRemaining(consumer);
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
        VectorIterator iter = this.vectorIterator();
        while (iter.advance()) {
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
    static boolean hasSeparatedAxes(VectorList vertexPoints1, VectorList vertexPoints2, double axisX, double axisY, double axisZ) {
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
    static boolean hasSeparatedAxes(VectorList vertexPoints1, VectorList vertexPoints2, Vector axis) {
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
    static boolean areVerticesOverlapping(VectorList vertexPoints1, VectorList vertexPoints2, VectorIterator axisIterator) {
        while (axisIterator.advance()) {
            if (hasSeparatedAxes(vertexPoints1, vertexPoints2, axisIterator.x(), axisIterator.y(), axisIterator.z())) {
                return false; // Separated axis found - no overlap
            }
        }

        // No separated axis found - they overlap
        return true;
    }

    /**
     * A more performant vector iterator that avoids the allocation of Vector
     * objects.
     */
    abstract class VectorIterator {
        protected int index = -1;
        protected double x, y, z;

        /**
         * Advances to the next vector. Returns false when the end of iteration is reached.
         * Call this before accessing the other properties of this class.
         *
         * @return True if the next vector is available, False if not
         */
        public abstract boolean advance();

        /**
         * Gets the index of the current vector. This counts how many iterations
         * have elapsed. Before {@link #advance()} is called, this returns -1
         * to indicate iteration hasn't started yet.
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
         * @param into Vector to write the x/y/z into
         * @return Vector
         */
        public final Vector toVector(Vector into) {
            into.setX(x);
            into.setY(y);
            into.setZ(z);
            return into;
        }

        /**
         * Calls {@link #advance()} until the end is reached, filling the collector
         * specified.
         *
         * @param collector Stream Collector, e.g. Collector.toList()
         * @return Collection
         * @param <R> Returned collection type
         * @param <A> Accumulator type
         */
        public <R, A> R collectRemaining(Collector<? super Vector, A, R> collector) {
            A container = collector.supplier().get();
            BiConsumer<A, ? super Vector> accumulator = collector.accumulator();

            while (advance()) {
                accumulator.accept(container, toVector());
            }

            return collector.finisher().apply(container);
        }

        /**
         * Calls {@link #advance()} until the end is reached and then calls the consumer
         * callback with all the vector information.
         *
         * @param consumer Vector Consumer
         */
        public void forEachRemaining(VectorConsumer consumer) {
            while (advance()) {
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
            this.x = v.getX();
            this.y = v.getY();
            this.z = v.getZ();
        }

        /**
         * Loads the x/y/z Vector value into this iterator.
         * For use by the implementation only.
         *
         * @param x New vector X-coordinate
         * @param y New vector Y-coordinate
         * @param z New vector Z-coordinate
         */
        protected void load(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
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

        /**
         * Creates a new VectorIterator that iterates all the Bukkit Vectors
         * in an Iterable (List, Collection).
         *
         * @param vectors Iterable of Bukkit Vectors
         * @return VectorIterator
         */
        public static VectorIterator iterate(Iterable<Vector> vectors) {
            return new VectorIterator() {
                Iterator<Vector> iter = null;

                @Override
                public boolean advance() {
                    if (iter == null) {
                        iter = vectors.iterator();
                    }
                    if (iter.hasNext()) {
                        ++index;
                        load(iter.next());
                        return true;
                    } else {
                        return false;
                    }
                }
            };
        }

        /**
         * Creates a new VectorIterator that iterates the same x/y/z value for
         * infinite iterations.
         *
         * @param value Filled vector value
         * @return new VectorIterator
         */
        public static VectorIterator iterateFilled(Vector value) {
            return iterateFilled(value, -1);
        }

        /**
         * Creates a new VectorIterator that iterates the same x/y/z value for
         * all iterations.
         *
         * @param value Filled vector value
         * @param limit Limit of the number of iterations until {@link #advance()} returns false.
         *              Use -1 for infinite iterations.
         * @return new VectorIterator
         */
        public static VectorIterator iterateFilled(Vector value, int limit) {
            return new FilledVectorIterator(value.getX(), value.getY(), value.getZ(), limit);
        }

        /**
         * Creates a new VectorIterator that iterates the same x/y/z value for
         * infinite iterations.
         *
         * @param x Filled vector X-coordinate
         * @param y Filled vector Y-coordinate
         * @param z Filled vector Z-coordinate
         * @return new VectorIterator
         */
        public static VectorIterator iterateFilled(double x, double y, double z) {
            return iterateFilled(x, y, z, -1);
        }

        /**
         * Creates a new VectorIterator that iterates the same x/y/z value for
         * all iterations.
         *
         * @param x Filled vector X-coordinate
         * @param y Filled vector Y-coordinate
         * @param z Filled vector Z-coordinate
         * @param limit Limit of the number of iterations until {@link #advance()} returns false.
         *              Use -1 for infinite iterations.
         * @return new VectorIterator
         */
        public static VectorIterator iterateFilled(double x, double y, double z, int limit) {
            return new FilledVectorIterator(x, y, z, limit);
        }

        private static final class FilledVectorIterator extends VectorIterator {
            private final int limit;

            public FilledVectorIterator(double x, double y, double z, int limit) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.limit = limit;
            }

            @Override
            public boolean advance() {
                final int limit = this.limit;
                if (limit != -1) {
                    int nextIndex = index + 1;
                    if (nextIndex >= limit) {
                        return false;
                    }
                }

                ++index;
                return true;
            }
        }
    }

    /**
     * Stores the result of a projection of vectors onto an axis
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

        @Override
        public String toString() {
            return "Projection{min=" + min + ", max=" + max + "}";
        }
    }

    /**
     * Factory interface for creating new vector lists.
     */
    interface Factory {
        /**
         * Creates a new immutable copy of an existing vector list
         *
         * @param vectorValues List of vector values
         * @return New VectorList
         */
        VectorList copyOf(VectorList vectorValues);

        /**
         * Creates a new immutable vector list, initialized with the values from
         * the iterator specified. The size must be known up-front.
         *
         * @param size Size of the vector list
         * @param iterator Iterator to fill the vector list. If not enough values
         *                 are found, the remainder are left uninitialized (0/0/0)
         * @return New VectorList
         */
        VectorList createWith(int size, VectorList.VectorIterator iterator);

        /**
         * Creates a new immutable copy of an existing vector list
         *
         * @param vectorValues List of vector values
         * @return New VectorList
         */
        default VectorList copyOf(Collection<Vector> vectorValues) {
            return createWith(vectorValues.size(), VectorList.VectorIterator.iterate(vectorValues));
        }
    }

    /**
     * Simple 3D vector consumer
     */
    @FunctionalInterface
    interface VectorConsumer {
        void accept(int index, double x, double y, double z);
    }
}
