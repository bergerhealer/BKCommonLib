package com.bergerkiller.bukkit.common.bases;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * Defines the minimum and maximum block coordinates of a cuboid block
 * area. Contains methods to use this cuboid.
 */
public final class IntCuboid {
    public final IntVector3 min;
    public final IntVector3 max;

    /**
     * Cuboid from 0/0/0 to 0/0/0, containing no blocks at all
     */
    public static final IntCuboid ZERO = new IntCuboid(IntVector3.ZERO, IntVector3.ZERO);
    /**
     * Cuboids from Integer.MIN_VALUE to Integer.MAX_VALUE, including all possible
     * block coordinates
     */
    public static final IntCuboid ALL = new IntCuboid(
            new IntVector3(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE),
            new IntVector3(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));

    private IntCuboid(IntVector3 min, IntVector3 max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Creates a new IntCuboid
     *
     * @param min Minimum coordinates of the cuboid, inclusive
     * @param max Maximum coordinates of the cuboid, exclusive
     * @return IntCuboid
     */
    public static IntCuboid create(IntVector3 min, IntVector3 max) {
        if (min == null) {
            throw new IllegalArgumentException("Minimum position is null");
        }
        if (max == null) {
            throw new IllegalArgumentException("Maximum position is null");
        }
        return new IntCuboid(min, max);
    }

    /**
     * Gets whether the minimum X-coordinate is 'unlimited', meaning it is set
     * to Integer.MIN_VALUE to indicate there is no limit.
     *
     * @return Whether the minimum X-coordinate is infinite
     */
    public boolean isMinXUnlimited() {
        return this.min.x == Integer.MIN_VALUE;
    }

    /**
     * Gets whether the minimum Y-coordinate is 'unlimited', meaning it is set
     * to Integer.MIN_VALUE to indicate there is no limit.
     *
     * @return Whether the minimum Y-coordinate is infinite
     */
    public boolean isMinYUnlimited() {
        return this.min.y == Integer.MIN_VALUE;
    }

    /**
     * Gets whether the minimum Z-coordinate is 'unlimited', meaning it is set
     * to Integer.MIN_VALUE to indicate there is no limit.
     *
     * @return Whether the minimum Z-coordinate is infinite
     */
    public boolean isMinZUnlimited() {
        return this.min.z == Integer.MIN_VALUE;
    }

    /**
     * Gets whether the maximum X-coordinate is 'unlimited', meaning it is set
     * to Integer.MAX_VALUE to indicate there is no limit.
     *
     * @return Whether the maximum X-coordinate is infinite
     */
    public boolean isMaxXUnlimited() {
        return this.max.x == Integer.MIN_VALUE;
    }

    /**
     * Gets whether the maximum Y-coordinate is 'unlimited', meaning it is set
     * to Integer.MAX_VALUE to indicate there is no limit.
     *
     * @return Whether the maximum Y-coordinate is infinite
     */
    public boolean isMaxYUnlimited() {
        return this.max.y == Integer.MIN_VALUE;
    }

    /**
     * Gets whether the maximum Z-coordinate is 'unlimited', meaning it is set
     * to Integer.MAX_VALUE to indicate there is no limit.
     *
     * @return Whether the maximum Z-coordinate is infinite
     */
    public boolean isMaxZUnlimited() {
        return this.max.z == Integer.MIN_VALUE;
    }

    /**
     * Checks if a position is contained within this cuboid
     *
     * @param x
     * @param y
     * @param z
     * @return True if position is inside this cuboid
     */
    public boolean contains(int x, int y, int z) {
        return x >= min.x && y >= min.y && z >= min.z &&
               x < max.x && y < max.y && z < max.z;
    }

    /**
     * Checks if a position is contained within this cuboid
     *
     * @param position
     * @return True if position is inside this cuboid
     */
    public boolean contains(IntVector3 position) {
        return position.x >= min.x && position.y >= min.y && position.z >= min.z &&
               position.x < max.x && position.y < max.y && position.z < max.z;
    }

    /**
     * Checks if a block is contained within this cuboid
     *
     * @param block
     * @return True if block is inside this cuboid
     */
    public boolean contains(Block block) {
        return contains(block.getX(), block.getY(), block.getZ());
    }

    /**
     * Checks if a position is contained within this cuboid
     *
     * @param position
     * @return True if position is inside this cuboid
     */
    public boolean contains(Vector position) {
        return contains(position.getBlockX(), position.getBlockY(), position.getBlockZ());
    }

    /**
     * Checks if a location is contained within this cuboid
     *
     * @param location
     * @return True if location is inside this cuboid
     */
    public boolean contains(Location location) {
        return contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Gets the distance between a position and the edge of this cuboid.
     * If inside the cuboid, returns 0.
     *
     * @param position
     * @return distance from the edge of the cuboid
     */
    public double distance(Vector position) {
        return Math.sqrt(distanceSquared(position));
    }

    /**
     * Gets the distance squared between a position and the edge of this cuboid.
     * If inside the cuboid, returns 0.
     *
     * @param position
     * @return distance squared from the edge of the cuboid
     */
    public double distanceSquared(Vector position) {
        double dx, dy, dz;
        if (position.getX() < min.x) {
            dx = min.x - position.getX();
        } else if (position.getX() >= max.x) {
            dx = position.getX() - max.x;
        } else {
            dx = 0.0;
        }
        if (position.getY() < min.y) {
            dy = min.y - position.getY();
        } else if (position.getY() >= max.y) {
            dy = position.getY() - max.y;
        } else {
            dy = 0.0;
        }
        if (position.getZ() < min.z) {
            dz = min.z - position.getZ();
        } else if (position.getZ() >= max.z) {
            dz = position.getZ() - max.z;
        } else {
            dz = 0.0;
        }
        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public int hashCode() {
        return 31 * this.min.hashCode() + this.max.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof IntCuboid) {
            IntCuboid other = (IntCuboid) o;
            return this.min.equals(other.min) && this.max.equals(other.max);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "{min=" + this.min + ", max=" + this.max + "}";
    }
}
