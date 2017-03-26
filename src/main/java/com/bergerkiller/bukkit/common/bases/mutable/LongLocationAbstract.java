package com.bergerkiller.bukkit.common.bases.mutable;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import org.bukkit.Location;
import org.bukkit.World;

public abstract class LongLocationAbstract extends LongVectorAbstract {

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
    public abstract LongLocationAbstract setWorld(World world);

    @Override
    public abstract LongLocationAbstract setX(long x);

    @Override
    public abstract LongLocationAbstract setY(long y);

    @Override
    public abstract LongLocationAbstract setZ(long z);

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
    public abstract LongLocationAbstract setYaw(int yaw);

    /**
     * Sets the pitch angle
     *
     * @param pitch angle to set to
     * @return this instance
     */
    public abstract LongLocationAbstract setPitch(int pitch);

    public LongLocationAbstract setLocZero() {
        super.setZero();
        return this;
    }

    @Override
    public LongLocationAbstract setZero() {
        return setLocZero().setYaw(0).setPitch(0);
    }

    public LongLocationAbstract set(LongLocationAbstract value) {
        super.set(value.getX(), value.getY(), value.getZ());
        return setWorld(value.getWorld()).setYaw(value.getYaw()).setPitch(value.getPitch());
    }

    @Override
    public LongLocationAbstract set(long x, long y, long z) {
        super.set(x, y, z);
        return this;
    }

    public LongLocationAbstract set(long x, long y, long z, int yaw, int pitch) {
        return set(x, y, z).setRotation(yaw, pitch);
    }

    public LongLocationAbstract setRotation(int yaw, int pitch) {
        return setYaw(yaw).setPitch(pitch);
    }

    public Location toLocation() {
        return new Location(getWorld(), getX(), getY(), getZ(), getYaw(), getPitch());
    }

    public LongLocationAbstract addYaw(int yaw) {
        return setYaw(getYaw() + yaw);
    }

    public LongLocationAbstract addPitch(int pitch) {
        return setPitch(getPitch() + pitch);
    }

    public float getYawDifference(int yawcomparer) {
        return MathUtil.getAngleDifference(this.getYaw(), yawcomparer);
    }

    public float getYawDifference(LongLocationAbstract location) {
        return getYawDifference(location.getYaw());
    }

    public float getPitchDifference(int pitchcomparer) {
        return MathUtil.getAngleDifference(this.getPitch(), pitchcomparer);
    }

    public float getPitchDifference(LongLocationAbstract location) {
        return getPitchDifference(location.getPitch());
    }

    @Override
    public String toString() {
        final World w = getWorld();
        return "{world=" + (w == null ? "null" : w.getName())
                + ", x=" + getX() + ", y=" + getY() + ", z=" + getZ()
                + ", yaw=" + getYaw() + ", pitch=" + getPitch() + "}";
    }
}
