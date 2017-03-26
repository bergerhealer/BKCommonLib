package com.bergerkiller.bukkit.common.bases.mutable;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * An abstract Class for dealing with mutable Integer x/y/z components
 */
public abstract class LongVectorAbstract {

    public final LongAbstract x = new LongAbstract() {
        public long get() {
            return LongVectorAbstract.this.getX();
        }

        public LongAbstract set(long value) {
            LongVectorAbstract.this.setX(value);
            return this;
        }
    };
    public final LongAbstract y = new LongAbstract() {
        public long get() {
            return LongVectorAbstract.this.getY();
        }

        public LongAbstract set(long value) {
            LongVectorAbstract.this.setY(value);
            return this;
        }
    };
    public final LongAbstract z = new LongAbstract() {
        public long get() {
            return LongVectorAbstract.this.getZ();
        }

        public LongAbstract set(long value) {
            LongVectorAbstract.this.setZ(value);
            return this;
        }
    };

    /**
     * Gets the X-component
     *
     * @return the X-component
     */
    public abstract long getX();

    /**
     * Gets the Y-component
     *
     * @return the Y-component
     */
    public abstract long getY();

    /**
     * Gets the Z-component
     *
     * @return the Z-component
     */
    public abstract long getZ();

    /**
     * Sets the X-component
     *
     * @param x value to set to
     * @return this same instance
     */
    public abstract LongVectorAbstract setX(long x);

    /**
     * Sets the Y-component
     *
     * @param y value to set to
     * @return this same instance
     */
    public abstract LongVectorAbstract setY(long y);

    /**
     * Sets the Z-component
     *
     * @param z value to set to
     * @return this same instance
     */
    public abstract LongVectorAbstract setZ(long z);

    public LongVectorAbstract setZero() {
        return setX(0).setY(0).setZ(0);
    }

    public LongVectorAbstract set(long x, long y, long z) {
        return setX(x).setY(y).setZ(z);
    }

    public LongVectorAbstract set(LongVectorAbstract value) {
        return set(value.getX(), value.getY(), value.getZ());
    }

    public LongVectorAbstract set(IntVector3 value) {
        return set(value.x, value.y, value.z);
    }

    public IntVector3 vector() {
        return new IntVector3(getX(), getY(), getZ());
    }

    public LongVectorAbstract add(long x, long y, long z) {
        return setX(getX() + x).setY(getY() + y).setZ(getZ() + z);
    }

    public LongVectorAbstract add(IntVector3 vector) {
        return add(vector.x, vector.y, vector.z);
    }

    public LongVectorAbstract add(LongVectorAbstract value) {
        return add(value.getX(), value.getY(), value.getZ());
    }

    public LongVectorAbstract add(BlockFace face, long length) {
        return add(length * face.getModX(), length * face.getModY(), length * face.getModZ());
    }

    public LongVectorAbstract add(LongVectorAbstract value, long length) {
        return add(length * value.getX(), length * value.getY(), length * value.getZ());
    }

    public LongVectorAbstract subtract(long x, long y, long z) {
        return setX(getX() - x).setY(getY() - y).setZ(getZ() - z);
    }

    public LongVectorAbstract subtract(IntVector3 vector) {
        return subtract(vector.x, vector.y, vector.z);
    }

    public LongVectorAbstract subtract(LongVectorAbstract value) {
        return subtract(value.getX(), value.getY(), value.getZ());
    }

    public LongVectorAbstract subtract(BlockFace face, long length) {
        return subtract(length * face.getModX(), length * face.getModY(), length * face.getModZ());
    }

    public LongVectorAbstract subtract(LongVectorAbstract value, long length) {
        return subtract(length * value.getX(), length * value.getY(), length * value.getZ());
    }

    public LongVectorAbstract multiply(long mx, long my, long mz) {
        return setX(getX() * mx).setY(getY() * my).setZ(getZ() * mz);
    }

    public LongVectorAbstract multiply(IntVector3 vector) {
        return multiply(vector.x, vector.y, vector.z);
    }

    public LongVectorAbstract divide(long dx, long dy, long dz) {
        return setX(getX() / dx).setY(getY() / dy).setZ(getZ() / dz);
    }

    public LongVectorAbstract divide(IntVector3 vector) {
        return divide(vector.x, vector.y, vector.z);
    }

    public LongVectorAbstract multiply(long factor) {
        return multiply(factor, factor, factor);
    }

    public LongVectorAbstract divide(long factor) {
        return divide(factor, factor, factor);
    }

    public double length() {
        return MathUtil.length(getX(), getY(), getZ());
    }

    public double lengthSquared() {
        return MathUtil.lengthSquared(getX(), getY(), getZ());
    }

    public double distance(double x, double y, double z) {
        return MathUtil.distance(getX(), getY(), getZ(), x, y, z);
    }

    public double distanceSquared(double x, double y, double z) {
        return MathUtil.distanceSquared(getX(), getY(), getZ(), x, y, z);
    }

    public double distance(VectorAbstract other) {
        return distance(other.getX(), other.getY(), other.getZ());
    }

    public double distanceSquared(VectorAbstract other) {
        return distanceSquared(other.getX(), other.getY(), other.getZ());
    }

    public double distance(Location other) {
        return distance(other.getX(), other.getY(), other.getZ());
    }

    public double distanceSquared(Location other) {
        return distanceSquared(other.getX(), other.getY(), other.getZ());
    }

    public double distance(Vector other) {
        return distance(other.getX(), other.getY(), other.getZ());
    }

    public double distanceSquared(Vector other) {
        return distanceSquared(other.getX(), other.getY(), other.getZ());
    }

    public double distance(Entity other) {
        return distance(EntityUtil.getLocX(other), EntityUtil.getLocY(other), EntityUtil.getLocZ(other));
    }

    public double distanceSquared(Entity other) {
        return distanceSquared(EntityUtil.getLocX(other), EntityUtil.getLocY(other), EntityUtil.getLocZ(other));
    }

    public double distance(Block block) {
        return distance(block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5);
    }

    public double distanceSquared(Block block) {
        return distanceSquared(block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5);
    }

    public double distance(CommonEntity<?> other) {
        return distance(other.loc);
    }

    public double distanceSquared(CommonEntity<?> other) {
        return distanceSquared(other.loc);
    }

    public Vector offsetTo(double x, double y, double z) {
        return new Vector(x - getX(), y - getY(), z - getZ());
    }

    public Vector offsetTo(Location l) {
        return offsetTo(l.getX(), l.getY(), l.getZ());
    }

    public Vector offsetTo(CommonEntity<?> entity) {
        return offsetTo(entity.getEntity());
    }

    public Vector offsetTo(org.bukkit.entity.Entity e) {
        return offsetTo(EntityUtil.getLocX(e), EntityUtil.getLocY(e), EntityUtil.getLocZ(e));
    }

    @Override
    public String toString() {
        return "{x=" + getX() + ", y=" + getY() + ", z=" + getZ() + "}";
    }
}
