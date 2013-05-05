package com.bergerkiller.bukkit.common.bases.mutable;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;

/**
 * An abstract Class for dealing with mutable Integer x/y/z components
 */
public abstract class IntVectorAbstract {
	public final IntegerAbstract x = new IntegerAbstract() {
		public int get() {return IntVectorAbstract.this.getX();}
		public IntegerAbstract set(int value) {IntVectorAbstract.this.setX(value); return this;}
	};
	public final IntegerAbstract y = new IntegerAbstract() {
		public int get() {return IntVectorAbstract.this.getY();}
		public IntegerAbstract set(int value) {IntVectorAbstract.this.setY(value); return this;}
	};
	public final IntegerAbstract z = new IntegerAbstract() {
		public int get() {return IntVectorAbstract.this.getZ();}
		public IntegerAbstract set(int value) {IntVectorAbstract.this.setZ(value); return this;}
	};

	/**
	 * Gets the X-component
	 * 
	 * @return the X-component
	 */
	public abstract int getX();

	/**
	 * Gets the Y-component
	 * 
	 * @return the Y-component
	 */
	public abstract int getY();

	/**
	 * Gets the Z-component
	 * 
	 * @return the Z-component
	 */
	public abstract int getZ();

	/**
	 * Sets the X-component
	 * 
	 * @param x value to set to
	 * @return this same instance
	 */
	public abstract IntVectorAbstract setX(int x);

	/**
	 * Sets the Y-component
	 * 
	 * @param y value to set to
	 * @return this same instance
	 */
	public abstract IntVectorAbstract setY(int y);

	/**
	 * Sets the Z-component
	 * 
	 * @param z value to set to
	 * @return this same instance
	 */
	public abstract IntVectorAbstract setZ(int z);

	public IntVectorAbstract setZero() {
		return setX(0).setY(0).setZ(0);
	}

	public IntVectorAbstract set(int x, int y, int z) {
		return setX(x).setY(y).setZ(z);
	}

	public IntVectorAbstract set(IntVectorAbstract value) {
		return set(value.getX(), value.getY(), value.getZ());
	}

	public IntVectorAbstract set(IntVector3 value) {
		return set(value.x, value.y, value.z);
	}

	public IntVector3 vector() {
		return new IntVector3(getX(), getY(), getZ());
	}

	public IntVectorAbstract add(int x, int y, int z) {
		return setX(getX() + x).setY(getY() + y).setZ(getZ() + z);
	}

	public IntVectorAbstract add(IntVector3 vector) {
		return add(vector.x, vector.y, vector.z);
	}

	public IntVectorAbstract add(IntVectorAbstract value) {
		return add(value.getX(), value.getY(), value.getZ());
	}

	public IntVectorAbstract add(BlockFace face, int length) {
		return add(length * face.getModX(), length * face.getModY(), length * face.getModZ());
	}

	public IntVectorAbstract add(IntVectorAbstract value, int length) {
		return add(length * value.getX(), length * value.getY(), length * value.getZ());
	}

	public IntVectorAbstract subtract(int x, int y, int z) {
		return setX(getX() - x).setY(getY() - y).setZ(getZ() - z);
	}

	public IntVectorAbstract subtract(IntVector3 vector) {
		return subtract(vector.x, vector.y, vector.z);
	}

	public IntVectorAbstract subtract(IntVectorAbstract value) {
		return subtract(value.getX(), value.getY(), value.getZ());
	}

	public IntVectorAbstract subtract(BlockFace face, int length) {
		return subtract(length * face.getModX(), length * face.getModY(), length * face.getModZ());
	}

	public IntVectorAbstract subtract(IntVectorAbstract value, int length) {
		return subtract(length * value.getX(), length * value.getY(), length * value.getZ());
	}

	public IntVectorAbstract multiply(int mx, int my, int mz) {
		return setX(getX() * mx).setY(getY() * my).setZ(getZ() * mz);
	}

	public IntVectorAbstract multiply(IntVector3 vector) {
		return multiply(vector.x, vector.y, vector.z);
	}

	public IntVectorAbstract divide(int dx, int dy, int dz) {
		return setX(getX() / dx).setY(getY() / dy).setZ(getZ() / dz);
	}

	public IntVectorAbstract divide(IntVector3 vector) {
		return divide(vector.x, vector.y, vector.z);
	}

	public IntVectorAbstract multiply(int factor) {
		return multiply(factor, factor, factor);
	}

	public IntVectorAbstract divide(int factor) {
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
