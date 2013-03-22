package com.bergerkiller.bukkit.common.bases.mutable;

import org.bukkit.Location;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.MathUtil;

public abstract class LocationAbstract extends VectorAbstract {
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
	public abstract LocationAbstract setWorld(World world);

	@Override
	public abstract LocationAbstract setX(double x);

	@Override
	public abstract LocationAbstract setY(double y);

	@Override
	public abstract LocationAbstract setZ(double z);

	/**
	 * Gets the yaw angle
	 * 
	 * @return yaw angle
	 */
	public abstract float getYaw();

	/**
	 * Gets the pitch angle
	 * 
	 * @return pitch angle
	 */
	public abstract float getPitch();

	/**
	 * Sets the yaw angle
	 * 
	 * @param yaw angle to set to
	 * @return this instance
	 */
	public abstract LocationAbstract setYaw(float yaw);

	/**
	 * Sets the pitch angle
	 * 
	 * @param pitch angle to set to
	 * @return this instance
	 */
	public abstract LocationAbstract setPitch(float pitch);

	public LocationAbstract setLocZero() {
		super.setZero();
		return this;
	}

	@Override
	public LocationAbstract setZero() {
		return setLocZero().setYaw(0.0f).setPitch(0.0f);
	}

	public LocationAbstract set(Location value) {
		super.set(value.getX(), value.getY(), value.getZ());
		return setWorld(value.getWorld()).setYaw(value.getYaw()).setPitch(value.getPitch());
	}

	public LocationAbstract set(LocationAbstract value) {
		super.set(value.getX(), value.getY(), value.getZ());
		return setWorld(value.getWorld()).setYaw(value.getYaw()).setPitch(value.getPitch());
	}

	public Location toLocation() {
		return new Location(getWorld(), getX(), getY(), getZ(), getYaw(), getPitch());
	}

	public LocationAbstract addYaw(float yaw) {
		return setYaw(getYaw() + yaw);
	}

	public LocationAbstract addPitch(float pitch) {
		return setYaw(getPitch() + pitch);
	}

	public float getYawDifference(float yawcomparer) {
		return MathUtil.getAngleDifference(this.getYaw(), yawcomparer);
	}

	public float getYawDifference(LocationAbstract location) {
		return getYawDifference(location.getYaw());
	}

	public float getYawDifference(CommonEntity<?> entity) {
		return getYawDifference(entity.loc);
	}

	public float getPitchDifference(float pitchcomparer) {
		return MathUtil.getAngleDifference(this.getPitch(), pitchcomparer);
	}

	public float getPitchDifference(LocationAbstract location) {
		return getPitchDifference(location.getPitch());
	}

	public float getPitchDifference(CommonEntity<?> entity) {
		return getPitchDifference(entity.loc);
	}
}
