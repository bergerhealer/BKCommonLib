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
 * An abstract Class for dealing with mutable x/y/z components
 */
public abstract class VectorAbstract {
	/**
	 * Gets a referenced Vector version for only the x and z coordinates
	 */
	public final VectorXZAbstract xz = new VectorXZAbstract() {
		public double getX() {return VectorAbstract.this.getX();}
		public double getZ() {return VectorAbstract.this.getZ();}
		public VectorXZAbstract setX(double x) {VectorAbstract.this.setX(x); return this;}
		public VectorXZAbstract setZ(double z) {VectorAbstract.this.setZ(z); return this;}
	};
	public final DoubleAbstract x = new DoubleAbstract() {
		public double get() {return VectorAbstract.this.getX();}
		public DoubleAbstract set(double value) {VectorAbstract.this.setX(value); return this;}
	};
	public final DoubleAbstract y = new DoubleAbstract() {
		public double get() {return VectorAbstract.this.getY();}
		public DoubleAbstract set(double value) {VectorAbstract.this.setY(value); return this;}
	};
	public final DoubleAbstract z = new DoubleAbstract() {
		public double get() {return VectorAbstract.this.getZ();}
		public DoubleAbstract set(double value) {VectorAbstract.this.setZ(value); return this;}
	};

	/**
	 * Gets the X-component
	 * 
	 * @return the X-component
	 */
	public abstract double getX();

	/**
	 * Gets the Y-component
	 * 
	 * @return the Y-component
	 */
	public abstract double getY();

	/**
	 * Gets the Z-component
	 * 
	 * @return the Z-component
	 */
	public abstract double getZ();

	/**
	 * Sets the X-component
	 * 
	 * @param x value to set to
	 * @return this same instance
	 */
	public abstract VectorAbstract setX(double x);

	/**
	 * Sets the Y-component
	 * 
	 * @param y value to set to
	 * @return this same instance
	 */
	public abstract VectorAbstract setY(double y);

	/**
	 * Sets the Z-component
	 * 
	 * @param z value to set to
	 * @return this same instance
	 */
	public abstract VectorAbstract setZ(double z);

	public VectorAbstract setZero() {
		return setX(0.0).setY(0.0).setZ(0.0);
	}

	public VectorAbstract set(double x, double y, double z) {
		return setX(x).setY(y).setZ(z);
	}

	public VectorAbstract set(VectorAbstract value) {
		return set(value.getX(), value.getY(), value.getZ());
	}

	public VectorAbstract set(Vector value) {
		return set(value.getX(), value.getY(), value.getZ());
	}

	public IntVector3 floor() {
		return new IntVector3(x.getFloor(), y.getFloor(), z.getFloor());
	}

	public IntVector3 block() {
		return new IntVector3(x.block(), y.block(), z.block());
	}

	public Vector vector() {
		return new Vector(getX(), getY(), getZ());
	}

	public VectorAbstract add(double x, double y, double z) {
		return setX(getX() + x).setY(getY() + y).setZ(getZ() + z);
	}

	public VectorAbstract add(Vector vector) {
		return add(vector.getX(), vector.getY(), vector.getZ());
	}

	public VectorAbstract add(VectorAbstract value) {
		return add(value.getX(), value.getY(), value.getZ());
	}

	public VectorAbstract add(BlockFace face, double length) {
		return add(length * face.getModX(), length * face.getModY(), length * face.getModZ());
	}

	public VectorAbstract add(VectorAbstract value, double length) {
		return add(length * value.getX(), length * value.getY(), length * value.getZ());
	}

	public VectorAbstract subtract(double x, double y, double z) {
		return setX(getX() - x).setY(getY() - y).setZ(getZ() - z);
	}

	public VectorAbstract subtract(Vector vector) {
		return subtract(vector.getX(), vector.getY(), vector.getZ());
	}

	public VectorAbstract subtract(VectorAbstract value) {
		return subtract(value.getX(), value.getY(), value.getZ());
	}

	public VectorAbstract subtract(BlockFace face, double length) {
		return subtract(length * face.getModX(), length * face.getModY(), length * face.getModZ());
	}

	public VectorAbstract subtract(VectorAbstract value, double length) {
		return subtract(length * value.getX(), length * value.getY(), length * value.getZ());
	}

	public VectorAbstract multiply(double mx, double my, double mz) {
		return setX(getX() * mx).setY(getY() * my).setZ(getZ() * mz);
	}

	public VectorAbstract multiply(Vector vector) {
		return multiply(vector.getX(), vector.getY(), vector.getZ());
	}

	public VectorAbstract divide(double dx, double dy, double dz) {
		return setX(getX() / dx).setY(getY() / dy).setZ(getZ() / dz);
	}

	public VectorAbstract divide(Vector vector) {
		return divide(vector.getX(), vector.getY(), vector.getZ());
	}

	public VectorAbstract multiply(double factor) {
		return multiply(factor, factor, factor);
	}

	public VectorAbstract divide(double factor) {
		return divide(factor, factor, factor);
	}

	public VectorAbstract fixNaN() {
		x.fixNaN();
		y.fixNaN();
		z.fixNaN();
		return this;
	}

	public VectorAbstract fixNaN(double defx, double defy, double defz) {
		x.fixNaN(defx);
		y.fixNaN(defy);
		z.fixNaN(defz);
		return this;
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
