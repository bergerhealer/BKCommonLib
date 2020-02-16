package com.bergerkiller.bukkit.common.math;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;

/**
 * A 3D box rotated in 3D space that allows for simple collision hit testing.
 * Radius is the x/y/z cube radius around the position. Orientation is around
 * the position also.
 */
public class OrientedBoundingBox {
    private final Vector position = new Vector();
    private final Vector radius = new Vector();
    private Quaternion orientation_inv = new Quaternion();

    /**
     * Default constructor with position {0,0,0}, size {0,0,0} and orientation
     * facing in the natural direction.
     */
    public OrientedBoundingBox() {
    }

    /**
     * Initializes and sets the position, size and orientation specified
     * 
     * @param position The position
     * @param size The size
     * @param orientation The orientation
     */
    public OrientedBoundingBox(Vector position, Vector size, Quaternion orientation) {
        setPosition(position);
        setSize(size);
        setOrientation(orientation);
    }

    /**
     * Gets the position of this oriented bounding box.
     * The returned value is a copy and can be modified.
     * 
     * @return position
     */
    public Vector getPosition() {
        return position.clone();
    }

    /**
     * Gets the size of this oriented bounding box.
     * The returned value is a copy and can be modified.
     * 
     * @return size
     */
    public Vector getSize() {
        return this.radius.clone().multiply(2.0);
    }

    /**
     * Gets the orientation of this oriented bounding box.
     * The returned value is a copy and can be modified.
     * 
     * @return orientation
     */
    public Quaternion getOrientation() {
        Quaternion q = this.orientation_inv.clone();
        q.invert();
        return q;
    }

    /**
     * Sets the position of this oriented bounding box.
     * 
     * @param pos The position to set to
     */
    public void setPosition(Vector pos) {
        MathUtil.setVector(position, pos);
    }

    /**
     * Sets the position of this oriented bounding box.
     * 
     * @param x The x-coordinate of the new position
     * @param y The y-coordinate of the new position
     * @param z The z-coordinate of the new position
     */
    public void setPosition(double x, double y, double z) {
        MathUtil.setVector(position, x, y, z);
    }

    /**
     * Sets the size of this oriented bounding box
     * 
     * @param size The size to set to
     */
    public void setSize(Vector size) {
        setSize(size.getX(), size.getY(), size.getZ());
    }

    /**
     * Sets the size of this oriented bounding box
     * 
     * @param sx The size along the x-axis
     * @param sy The size along the y-axis
     * @param sz The size along the z-axis
     */
    public void setSize(double sx, double sy, double sz) {
        MathUtil.setVector(this.radius, 0.5*sx, 0.5*sy, 0.5*sz);
    }

    /**
     * Sets the orientation of this oriented bounding box
     */
    public void setOrientation(Quaternion orientation) {
        this.orientation_inv.setTo(orientation);
        this.orientation_inv.invert();
    }

    /**
     * Performs a hit test to see whether this collision box is hit when
     * looked at from a known eye location
     * 
     * @param eyeLocation
     * @return distance to the box, Double.MAX_VALUE when not touching.
     */
    public double hittest(Location eyeLocation) {
        // Compute start point
        Vector p = eyeLocation.toVector().subtract(this.position);
        this.orientation_inv.transformPoint(p);

        // Check start point already inside box
        if (Math.abs(p.getX()) <= this.radius.getX() && Math.abs(p.getY()) <= this.radius.getY() && Math.abs(p.getZ()) <= this.radius.getZ()) {
            return 0.0;
        }

        // Compute direction after rotation
        Vector d = eyeLocation.getDirection();
        this.orientation_inv.transformPoint(d);

        // Check all 6 faces and find the intersection point with this axis
        // Then check whether these points are within the range of the box
        // If true, compute the distance from the start point and track the smallest value
        final double ERR = 1e-6;
        double min_distance = Double.MAX_VALUE;
        for (BlockFace dir : FaceUtil.BLOCK_SIDES) {
            double a, b, c;
            if (dir.getModX() != 0) {
                // x
                a = this.radius.getX() * dir.getModX();
                b = p.getX();
                c = d.getX();
            } else if (dir.getModY() != 0) {
                // y
                a = this.radius.getY() * dir.getModY();
                b = p.getY();
                c = d.getY();
            } else {
                // z
                a = this.radius.getZ() * dir.getModZ();
                b = p.getZ();
                c = d.getZ();
            }
            if (c == 0.0) {
                continue;
            }

            // Find how many steps of d (c) it takes to reach the box border (a) from p (b)
            double f = ((a - b) / c);
            if (f < 0.0) {
                continue;
            }

            // Check is potential minimum distance first
            if (f > min_distance) {
                continue;
            }

            // Check hit point within bounds of box
            if ((Math.abs(p.getX() + f * d.getX()) - this.radius.getX()) > ERR) {
                continue;
            }
            if ((Math.abs(p.getY() + f * d.getY()) - this.radius.getY()) > ERR) {
                continue;
            }
            if ((Math.abs(p.getZ() + f * d.getZ()) - this.radius.getZ()) > ERR) {
                continue;
            }

            // Since d is a unit vector, f is now the distance we need
            min_distance = f;
        }
        return min_distance;
    }
}
