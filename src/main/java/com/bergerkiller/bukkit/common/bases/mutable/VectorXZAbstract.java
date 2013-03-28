package com.bergerkiller.bukkit.common.bases.mutable;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;

public abstract class VectorXZAbstract {
	public final DoubleAbstract x = new DoubleAbstract() {
		public double get() {return VectorXZAbstract.this.getX();}
		public DoubleAbstract set(double value) {VectorXZAbstract.this.setX(value); return this;}
	};
	public final DoubleAbstract z = new DoubleAbstract() {
		public double get() {return VectorXZAbstract.this.getZ();}
		public DoubleAbstract set(double value) {VectorXZAbstract.this.setZ(value); return this;}
	};

	/**
	 * Gets the X-component
	 * 
	 * @return the X-component
	 */
	public abstract double getX();

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
	public abstract VectorXZAbstract setX(double x);

	/**
	 * Sets the Z-component
	 * 
	 * @param z value to set to
	 * @return this same instance
	 */
	public abstract VectorXZAbstract setZ(double z);

	public VectorXZAbstract setZero() {
		return setX(0.0).setZ(0.0);
	}

	public VectorXZAbstract set(double x, double z) {
		return setX(x).setZ(z);
	}

	public VectorXZAbstract set(VectorXZAbstract value) {
		return set(value.getX(), value.getZ());
	}

	public IntVector2 floor() {
		return new IntVector2(x.getFloor(), z.getFloor());
	}
	
	public IntVector2 toBlock() {
		return new IntVector2(x.block(), z.block());
	}

	public VectorXZAbstract add(double x, double z) {
		return setX(getX() + x).setZ(getZ() + z);
	}

	public VectorXZAbstract add(Vector vector) {
		return add(vector.getX(), vector.getZ());
	}

	public VectorXZAbstract add(VectorXZAbstract value) {
		return add(value.getX(), value.getZ());
	}

	public VectorXZAbstract add(VectorXZAbstract value, double length) {
		return add(length * value.getX(), length * value.getZ());
	}

	public VectorXZAbstract add(BlockFace face, double length) {
		return add(length * face.getModX(), length * face.getModZ());
	}

	public VectorXZAbstract subtract(double x, double z) {
		return setX(getX() - x).setZ(getZ() - z);
	}

	public VectorXZAbstract subtract(Vector vector) {
		return subtract(vector.getX(), vector.getZ());
	}

	public VectorXZAbstract subtract(VectorXZAbstract value) {
		return subtract(value.getX(), value.getZ());
	}

	public VectorXZAbstract subtract(BlockFace face, double length) {
		return subtract(length * face.getModX(), length * face.getModZ());
	}

	public VectorXZAbstract subtract(VectorXZAbstract value, double length) {
		return subtract(length * value.getX(), length * value.getZ());
	}

	public VectorXZAbstract multiply(double mx, double mz) {
		return setX(getX() * mx).setZ(getZ() * mz);
	}

	public VectorXZAbstract multiply(Vector vector) {
		return multiply(vector.getX(), vector.getZ());
	}

	public VectorXZAbstract multiply(double factor) {
		return multiply(factor, factor);
	}

	public VectorXZAbstract divide(double dx, double dz) {
		return setX(getX() / dx).setZ(getZ() / dz);
	}

	public VectorXZAbstract divide(Vector vector) {
		return divide(vector.getX(), vector.getZ());
	}

	public VectorXZAbstract divide(double factor) {
		return divide(factor, factor);
	}

	public VectorXZAbstract fixNaN() {
		x.fixNaN();
		z.fixNaN();
		return this;
	}

	public VectorXZAbstract fixNaN(double defx, double defz) {
		x.fixNaN(defx);
		z.fixNaN(defz);
		return this;
	}

	public IntVector2 chunk() {
		return new IntVector2(x.chunk(), z.chunk());
	}

	public double length() {
		return MathUtil.length(getX(), getZ());
	}

	public double lengthSquared() {
		return MathUtil.lengthSquared(getX(), getZ());
	}

	public double distance(double x,  double z) {
		return MathUtil.distance(getX(), getZ(), x, z);
	}

	public double distanceSquared(double x,  double z) {
		return MathUtil.distance(getX(), getZ(), x, z);
	}

	public double distance(VectorAbstract other) {
		return distance(other.getX(), other.getZ());
	}

	public double distanceSquared(VectorAbstract other) {
		return distanceSquared(other.getX(), other.getZ());
	}

	public double distance(VectorXZAbstract other) {
		return distance(other.getX(), other.getZ());
	}

	public double distanceSquared(VectorXZAbstract other) {
		return distanceSquared(other.getX(), other.getZ());
	}

	public double distance(Location other) {
		return distance(other.getX(), other.getZ());
	}

	public double distanceSquared(Location other) {
		return distanceSquared(other.getX(), other.getZ());
	}

	public double distance(Vector other) {
		return distance(other.getX(), other.getZ());
	}

	public double distanceSquared(Vector other) {
		return distanceSquared(other.getX(), other.getZ());
	}

	public double distance(Block block) {
		return distance(block.getX() + 0.5, block.getZ() + 0.5);
	}

	public double distanceSquared(Block block) {
		return distanceSquared(block.getX() + 0.5, block.getZ() + 0.5);
	}

	public double distance(Entity other) {
		return distance(EntityUtil.getLocX(other), EntityUtil.getLocZ(other));
	}

	public double distanceSquared(Entity other) {
		return distanceSquared(EntityUtil.getLocX(other), EntityUtil.getLocZ(other));
	}

	public double distance(CommonEntity<?> other) {
		return distance(other.loc);
	}

	public double distanceSquared(CommonEntity<?> other) {
		return distanceSquared(other.loc);
	}

	@Override
	public String toString() {
		return "{x=" + getX() + ", z=" + getZ() + "}";
	}
}
