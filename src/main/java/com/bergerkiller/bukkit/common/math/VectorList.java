package com.bergerkiller.bukkit.common.math;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import org.bukkit.util.Vector;

import java.util.Arrays;
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
     * Creates a new immutable copy of an array of vectors
     *
     * @param vectorValues List of vector values
     * @return
     */
    static VectorList copyOf(Vector... vectorValues) {
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
     * Creates a view on two VectorLists, first containing the elements of the first
     * list, and then the second. Use copyOf of this joined list if it should be
     * optimized into a single backing buffer.
     *
     * @param first First VectorList
     * @param second Second VectorList
     * @return Joined VectorList View
     */
    static VectorList join(VectorList first, VectorList second) {
        return new VectorListJoinedImpl(first, second);
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
     * objects, for a subset of this list.
     *
     * @param offset Offset vector count
     * @param length Number of vectors to iterate
     * @return VectorIterator
     */
    VectorIterator vectorIterator(int offset, int length);

    /**
     * Gets a more performant vector iterator that avoids the allocation of Vector
     * objects.
     *
     * @return VectorIterator
     */
    default VectorIterator vectorIterator() {
        return vectorIterator(0, size());
    }

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
     * Calls the callback function with all the vectors of this vector list.
     * Returns true if the consumer accepted all values, or false if
     * it signalled false.
     *
     * @param consumer Callback
     * @return True if all values have been consumed, false if the consumer
     *         aborted iteration.
     */
    default boolean forEach(VectorConsumer consumer) {
        return vectorIterator().forEachRemaining(consumer);
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
     * Performs a cross product between all the vectors of this list (left-hand) and the
     * specified vectors (right-hand)
     *
     * @param right Right-hand side of the cross product. Must be the same size.
     * @return New VectorList with the cross product result
     * @throws IllegalArgumentException If this vector list size differs from the right
     * @see VectorIterator#performCrossProduct
     */
    default VectorList crossProduct(VectorList right) {
        VectorListMutable result = VectorListMutable.create(this.size());
        this.crossProduct(right, result);
        return result;
    }

    /**
     * Performs a cross product between all the vectors of this list (left-hand) and the
     * specified vectors (right-hand), and stores it into the mutable vector list result.
     *
     * @param right Right-hand side of the cross product. Must be the same size.
     * @param result Mutable VectorList to write the cross product results into.
     *               Must be able to at least store the results.
     * @throws IllegalArgumentException If this vector list size differs from the right, or the
     *                                  mutable result vector list can't store the full result.
     * @see VectorIterator#performCrossProduct
     */
    default void crossProduct(VectorList right, VectorListMutable result) {
        try {
            crossProduct(right, result.vectorIterator());
        } catch (IllegalArgumentException ex) {
            if (result.size() < this.size()) {
                throw new IllegalArgumentException("Result mutable vector list (size=" + result.size() +
                        ") can't store the cross product results (size=" + this.size() + ")");
            } else {
                throw ex;
            }
        }
    }

    /**
     * Performs a cross product between all the vectors of this list (left-hand) and the
     * specified vectors (right-hand)<br>
     * <br>
     * Implementations should use
     * {@link VectorListMutable.MutableVectorIterator#acceptVector(double, double, double)}
     * to store the results.
     *
     * @param right Right-hand side of the cross product. Must be the same size.
     * @param consumer Consumer to write the cross product results into.
     * @throws IllegalArgumentException If this vector list size differs from the right, or the
     *                                  consumer can't store the full result.
     * @see VectorIterator#performCrossProduct
     */
    default void crossProduct(VectorList right, VectorConsumer consumer) {
        try {
            VectorIterator.performCrossProduct(this.vectorIterator(), right.vectorIterator(), consumer);
        } catch (IllegalArgumentException ex) {
            if (this.size() != right.size()) {
                throw new IllegalArgumentException("Left list (size=" + this.size() +
                        ") has a different size than the right size (size=" + right.size() + ")");
            } else {
                throw ex;
            }
        }
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
         * to indicate iteration hasn't started yet.<br>
         * <br>
         * When using the vector iterator with an offset, this index will still be 0
         * for the first element.
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
         * callback with all the vector information. Aborts iteration if the
         * consumer signals it cannot accept more values, in which case
         * this method returns false.
         *
         * @param consumer Vector Consumer
         */
        public boolean forEachRemaining(VectorConsumer consumer) {
            while (advance()) {
                if (!consumer.acceptVector(x, y, z)) {
                    return false;
                }
            }
            return true;
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
         * Iterates both the left and right vector iterator in parallel and computes the cross product
         * for all the vectors found. Result are written to the consumer.
         * Consumer must be capable of storing all the results, or an error is thrown.
         *
         * @param leftIter Iterator for the cross product left-hand side vectors
         * @param rightIter Iterator for the cross product right-hand side vectors
         * @param consumer VectorConsumer to write the cross product result vectors into
         * @throws IllegalArgumentException If the iterators advance in incompatible ways.
         */
        public static void performCrossProduct(VectorIterator leftIter, VectorIterator rightIter, VectorConsumer consumer) {
            while (true) {
                if (!leftIter.advance()) {
                    if (!rightIter.advance()) {
                        break;
                    } else {
                        throw new IllegalArgumentException("Cross-product right iterator advanced beyond the left iterator");
                    }
                } else if (!rightIter.advance()) {
                    throw new IllegalArgumentException("Cross-product left iterator advanced beyond the right iterator");
                }

                boolean accepted = consumer.acceptVector(
                        leftIter.y() * rightIter.z() - rightIter.y() * leftIter.z(),
                        leftIter.z() * rightIter.x() - rightIter.z() * leftIter.x(),
                        leftIter.x() * rightIter.y() - rightIter.x() * leftIter.y()
                );
                if (!accepted) {
                    throw new IllegalArgumentException("Vector consumer could not accept all of the cross-product results");
                }
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

        /**
         * Joins two vector iterators, first iterating the first and then the second.
         *
         * @param first First VectorIterator
         * @param second Second VectorIterator
         * @return Joined VectorIterator
         */
        public static VectorIterator join(final VectorIterator first, final VectorIterator second) {
            return new VectorIterator() {
                VectorIterator curr = first;

                @Override
                public boolean advance() {
                    VectorIterator curr = this.curr;
                    if (!curr.advance()) {
                        if (curr == second) {
                            return false;
                        } else {
                            this.curr = curr = second;
                            if (!curr.advance()) {
                                return false;
                            }
                        }
                    }

                    this.x = curr.x();
                    this.y = curr.y();
                    this.z = curr.z();
                    ++this.index;
                    return true;
                }
            };
        }

        private static final class FilledVectorIterator extends VectorIterator {
            private final int lastIndex;

            public FilledVectorIterator(double x, double y, double z, int limit) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.lastIndex = (limit - 1);
            }

            @Override
            public boolean advance() {
                final int lastIndex = this.lastIndex;
                if (lastIndex != -2 && index >= lastIndex) {
                    return false;
                } else {
                    ++index;
                    return true;
                }
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
         * Gets the required vector list size that this factory supports. Other
         * sizes will throw errors. A value of -1 indicates any size is supported.
         *
         * @return Required size for creating vectors, or -1 if any is supported
         */
        default int getRequiredSize() {
            return -1;
        }

        /**
         * Gets whether a particular vector list size has special SIMD optimizations
         * available to it.
         *
         * @param size Size
         * @return True if optimized
         */
        default boolean isOptimizedSize(int size) {
            return false;
        }

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
         * Creates a new immutable copy of an array of vectors
         *
         * @param vectorValues List of vector values
         * @return
         */
        default VectorList copyOf(Vector... vectorValues) {
            return copyOf(Arrays.asList(vectorValues));
        }

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
     * Consumes vector values. Acts as a sink for vectors.
     */
    @FunctionalInterface
    interface VectorConsumer {
        /**
         * Advances internally and stores the value specified. If there was no room
         * to store the new value, false is returned.
         *
         * @param x New X-value
         * @param y New Y-value
         * @param z New Z-value
         * @return True if the value was accepted and more values can be received.
         *         False if the consumer can no longer accept new values, and
         *         iteration should abort.
         */
        boolean acceptVector(double x, double y, double z);

        /**
         * Advances internally and stores the value specified. If there was no room
         * to store the new value, false is returned.
         *
         * @param value New Vector value
         * @return True if the value was accepted and more values can be received.
         *         False if the consumer can no longer accept new values, and
         *         iteration should abort.
         */
        default boolean acceptVector(Vector value) {
            return acceptVector(value.getX(), value.getY(), value.getZ());
        }
    }
}
