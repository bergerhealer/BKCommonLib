package com.bergerkiller.bukkit.common.math;

import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Iterator;

/**
 * A mutable collection of 3D vectors. The amount of vectors cannot be changed,
 * but their values can be.
 */
public interface VectorListMutable extends VectorList, Rotatable, Translatable {

    /**
     * Creates a new mutable vector list of a given size, initially filled
     * with all-0 values.
     *
     * @param size Number of stored vectors
     * @return New VectorListMutable
     */
    static VectorListMutable create(int size) {
        return new VectorListMutableBasicImpl(size);
    }

    /**
     * Creates a new mutable vector list of size 8, set with the corner points
     * of an axis-aligned box centered at 0/0/0 with the sizes specified.
     *
     * @param size Axis-aligned size of the box
     * @return New VectorListMutable
     */
    static VectorListMutable createBoxVertices(Vector size) {
        return createBoxVertices(size.getX(), size.getY(), size.getZ());
    }

    /**
     * Creates a new mutable vector list of size 8, set with the corner points
     * of an axis-aligned box centered at 0/0/0 with the sizes specified.
     *
     * @param sx X-size of the axis-aligned box
     * @param sy Y-size of the axis-aligned box
     * @param sz Z-size of the axis-aligned box
     * @return New VectorListMutable
     */
    static VectorListMutable createBoxVertices(double sx, double sy, double sz) {
        return createBoxVerticesWithHalfSize(0.5 * sx, 0.5 * sy, 0.5 * sz);
    }

    /**
     * Creates a new mutable vector list of size 8, set with the corner points
     * of an axis-aligned box centered at 0/0/0 with the half-sizes specified.
     *
     * @param halfSize Half size of the axis-aligned box
     * @return New VectorListMutable
     */
    static VectorListMutable createBoxVerticesWithHalfSize(Vector halfSize) {
        return createBoxVerticesWithHalfSize(halfSize.getX(), halfSize.getY(), halfSize.getZ());
    }

    /**
     * Creates a new mutable vector list of size 8, set with the corner points
     * of an axis-aligned box centered at 0/0/0 with the half-sizes specified.
     *
     * @param hsx Half X-size of the axis-aligned box
     * @param hsy Half Y-size of the axis-aligned box
     * @param hsz Half Z-size of the axis-aligned box
     * @return New VectorListMutable
     */
    static VectorListMutable createBoxVerticesWithHalfSize(double hsx, double hsy, double hsz) {
        VectorListMutable list = create(8);
        for (int n = 0; n < 8; n++) {
            list.set(n,
                    (n & 0x4) != 0 ? hsx : -hsx,
                    (n & 0x2) != 0 ? hsy : -hsy,
                    (n & 0x1) != 0 ? hsz : -hsz
            );
        }
        return list;
    }

    /**
     * Creates a new mutable copy of an existing vector list
     *
     * @param vectorValues List of vector values
     * @return New VectorListMutable
     */
    static VectorListMutable copyOf(VectorList vectorValues) {
        return new VectorListMutableBasicImpl(vectorValues);
    }

    /**
     * Creates a new mutable copy of an existing vector list
     *
     * @param vectorValues List of vector values
     * @return New VectorListMutable
     */
    static VectorListMutable copyOf(Collection<Vector> vectorValues) {
        return createWith(vectorValues.size(), VectorList.VectorIterator.iterate(vectorValues));
    }

    /**
     * Creates a new mutable vector list, initialized with the values from
     * the iterator specified. The size must be known up-front.
     *
     * @param size Size of the vector list
     * @param iterator Iterator to fill the vector list. If not enough values
     *                 are found, the remainder are left uninitialized (0/0/0)
     * @return New VectorListMutable
     */
    static VectorListMutable createWith(int size, VectorList.VectorIterator iterator) {
        return new VectorListMutableBasicImpl(size, iterator);
    }

    /**
     * Sets a single vector
     *
     * @param index Vector index
     * @param x New Vector X-value
     * @param y New Vector Y-value
     * @param z New Vector Z-value
     */
    void set(int index, double x, double y, double z);

    /**
     * Returns a new MutableVectorIterator. Use this to efficiently iterate
     * and mutate a subset of this list of vectors
     *
     * @param offset Offset vector count
     * @param length Number of vectors to iterate, -1 for no length limit.
     * @return New MutableVectorIterator
     */
    @Override
    MutableVectorIterator vectorIterator(int offset, int length);

    /**
     * Sets a single vector using a Bukkit Vector
     *
     * @param index Vector index
     * @param value Vector value
     */
    default void set(int index, Vector value) {
        set(index, value.getX(), value.getY(), value.getZ());
    }

    /**
     * Returns a new MutableVectorIterator. Use this to efficiently iterate
     * and mutate this list of vectors
     *
     * @return New MutableVectorIterator
     */
    @Override
    default MutableVectorIterator vectorIterator() {
        return vectorIterator(0, size());
    }

    /**
     * Creates a copy of this mutable vector list
     *
     * @return Clone
     */
    default VectorListMutable copy() {
        return copyOf(this);
    }

    /**
     * Creates an immutable copy of this mutable vector list
     *
     * @return Immutable VectorList
     */
    default VectorList immutable() {
        return VectorList.copyOf(this);
    }

    @Override
    default void rotateByQuaternion(double qx, double qy, double qz, double qw) {
        MutableVectorIterator iter = vectorIterator();
        while (iter.advance()) {
            iter.rotateByQuaternion(qx, qy, qz, qw);
        }
    }

    @Override
    default void translate(double dx, double dy, double dz) {
        MutableVectorIterator iter = vectorIterator();
        while (iter.advance()) {
            iter.translate(dx, dy, dz);
        }
    }

    /**
     * A more performant vector iterator that avoids the allocation of Vector
     * objects. Is mutable so also allows updating the values being iterated over.
     */
    abstract class MutableVectorIterator extends VectorIterator implements Rotatable, Translatable, VectorConsumer {

        /**
         * Sets a new x/y/z value for the Vector at the current index
         *
         * @param x New X-value
         * @param y New Y-value
         * @param z New Z-value
         */
        public abstract void set(double x, double y, double z);

        /**
         * Calls {@link #advance()} followed by {@link #set(double, double, double)}
         * to set the value. This allows this method to be used as a vector sink,
         * "adding" the value to a mutable vector list.
         *
         * @param x New X-value
         * @param y New Y-value
         * @param z New Z-value
         * @return True if the advance() succeeded and the value was stored
         */
        @Override
        public boolean acceptVector(double x, double y, double z) {
            if (advance()) {
                set(x, y, z);
                return true;
            } else {
                return false;
            }
        }

        /**
         * Calls {@link #advance()} followed by {@link #set(Vector)}
         * to set the value. This allows this method to be used as a vector sink,
         * "adding" the value to a mutable vector list.
         *
         * @param value New Vector value
         * @return True if the advance() succeeded and the value was stored
         */
        @Override
        public boolean acceptVector(Vector value) {
            if (advance()) {
                set(value);
                return true;
            } else {
                return false;
            }
        }

        /**
         * Multiplies the value of the Vector with a multiplier
         *
         * @param mx Multiplier for the X-value
         * @param my Multiplier for the Y-value
         * @param mz Multiplier for the Z-value
         */
        public void multiply(double mx, double my, double mz) {
            set(x() * mx, y() * my, z() * mz);
        }

        /**
         * Adds another Vector to this Vector
         *
         * @param dx Added X-value
         * @param dy Added Y-value
         * @param dz Added Z-value
         */
        public void add(double dx, double dy, double dz) {
            set(x() + dx, y() + dy, z() + dz);
        }

        /**
         * Sets a new Vector value for the Vector at the current index
         *
         * @param value New Vector value
         */
        public void set(Vector value) {
            set(value.getX(), value.getY(), value.getZ());
        }

        @Override
        public void rotateByQuaternion(double qx, double qy, double qz, double qw) {
            double px = x();
            double py = y();
            double pz = z();
            set(
                    px + 2.0 * ( px*(-qy*qy-qz*qz) + py*(qx*qy-qz*qw)  + pz*(qx*qz+qy*qw)  ),
                    py + 2.0 * ( px*(qx*qy+qz*qw)  + py*(-qx*qx-qz*qz) + pz*(qy*qz-qx*qw)  ),
                    pz + 2.0 * ( px*(qx*qz-qy*qw)  + py*(qy*qz+qx*qw)  + pz*(-qx*qx-qy*qy) )
            );
        }

        @Override
        public void translate(double dx, double dy, double dz) {
            set(x() + dx, y() + dy, z() + dz);
        }

        /**
         * Creates a new MutableVectorIterator that iterates all the Bukkit Vectors
         * in an Iterable (List, Collection). The set method can be used to change
         * the value of the Vector last iterated over.
         *
         * @param vectors Iterable of Bukkit Vectors
         * @return MutableVectorIterator
         */
        public static MutableVectorIterator iterate(Iterable<Vector> vectors) {
            return new MutableVectorIterator() {
                Iterator<Vector> iter = null;
                Vector lastValue = null;

                @Override
                public boolean advance() {
                    if (iter == null) {
                        iter = vectors.iterator();
                    }
                    if (iter.hasNext()) {
                        ++index;
                        load(lastValue = iter.next());
                        return true;
                    } else {
                        return false;
                    }
                }

                @Override
                public void set(double x, double y, double z) {
                    lastValue.setX(x);
                    lastValue.setY(y);
                    lastValue.setZ(z);

                    this.x = x;
                    this.y = y;
                    this.z = z;
                }
            };
        }

        /**
         * Joins two mutable vector iterators, first iterating the first and then the second.
         * Set operations will update the iterator last iterated over.
         *
         * @param first First MutableVectorIterator
         * @param second Second MutableVectorIterator
         * @return Joined MutableVectorIterator
         */
        public static MutableVectorIterator join(final MutableVectorIterator first, final MutableVectorIterator second) {
            return new MutableVectorIterator() {
                MutableVectorIterator curr = first;

                @Override
                public void set(double x, double y, double z) {
                    curr.set(x, y, z);
                }

                @Override
                public boolean advance() {
                    MutableVectorIterator curr = this.curr;
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
    }
}
