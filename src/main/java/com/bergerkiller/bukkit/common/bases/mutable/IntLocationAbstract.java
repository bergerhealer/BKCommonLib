package com.bergerkiller.bukkit.common.bases.mutable;

import org.bukkit.Location;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.utils.MathUtil;

public abstract class IntLocationAbstract extends IntVectorAbstract {
	/**
	 * Gets the world
	 * 
	 * @return the World
	 */
	public abstract World getWorld();

	/**
	 * Sets the world
	 * 
	 * @param world to set to
	 * @return this instance
	 */
	public abstract IntLocationAbstract setWorld(World world);

	@Override
	public abstract IntLocationAbstract setX(int x);

	@Override
	public abstract IntLocationAbstract setY(int y);

	@Override
	public abstract IntLocationAbstract setZ(int z);

	/**
	 * Gets the yaw angle
	 * 
	 * @return yaw angle
	 */
	public abstract int getYaw();

	/**
	 * Gets the pitch angle
	 * 
	 * @return pitch angle
	 */
	public abstract int getPitch();

	/**
	 * Sets the yaw angle
	 * 
	 * @param yaw angle to set to
	 * @return this instance
	 */
	public abstract IntLocationAbstract setYaw(int yaw);

	/**
	 * Sets the pitch angle
	 * 
	 * @param pitch angle to set to
	 * @return this instance
	 */
	public abstract IntLocationAbstract setPitch(int pitch);

	public IntLocationAbstract setLocZero() {
		super.setZero();
		return this;
	}

	@Override
	public IntLocationAbstract setZero() {
		return setLocZero().setYaw(0).setPitch(0);
	}

	public IntLocationAbstract set(IntLocationAbstract value) {
		super.set(value.getX(), value.getY(), value.getZ());
		return setWorld(value.getWorld()).setYaw(value.getYaw()).setPitch(value.getPitch());
	}

	@Override
	public IntLocationAbstract set(int x, int y, int z) {
		super.set(x, y, z);
		return this;
	}

	public IntLocationAbstract set(int x, int y, int z, int yaw, int pitch) {
		return set(x, y, z).setRotation(yaw, pitch);
	}

	public IntLocationAbstract setRotation(int yaw, int pitch) {
		return setYaw(yaw).setPitch(pitch);
	}

	public Location toLocation() {
		return new Location(getWorld(), getX(), getY(), getZ(), getYaw(), getPitch());
	}

	public IntLocationAbstract addYaw(int yaw) {
		return setYaw(getYaw() + yaw);
	}

	public IntLocationAbstract addPitch(int pitch) {
		return setPitch(getPitch() + pitch);
	}

	public float getYawDifference(int yawcomparer) {
		return MathUtil.getAngleDifference(this.getYaw(), yawcomparer);
	}

	public float getYawDifference(IntLocationAbstract location) {
		return getYawDifference(location.getYaw());
	}

	public float getPitchDifference(int pitchcomparer) {
		return MathUtil.getAngleDifference(this.getPitch(), pitchcomparer);
	}

	public float getPitchDifference(IntLocationAbstract location) {
		return getPitchDifference(location.getPitch());
	}

	@Override
	public String toString() {
		final World w = getWorld();
		return "{world=" + (w == null ? "null" : w.getName()) + 
				", x=" + getX() + ", y=" + getY() + ", z=" + getZ() + 
				", yaw=" + getYaw() + ", pitch=" + getPitch() + "}";
	}
}
