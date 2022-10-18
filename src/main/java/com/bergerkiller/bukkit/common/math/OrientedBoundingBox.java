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
    private final Quaternion orientation_inv = new Quaternion();
    private boolean is_orientation_set = false;

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
     * Creates a new naturally-oriented bounding box with the two edge coordinates
     * as specified. p2 can have coordinates before p1. The size of the box is defined
     * by the difference in position between the two points.<br>
     * <br>
     * Naturally oriented means that it is not rotated at all. Identity quaternion.
     *
     * @param p1 Position one of the bounding box
     * @param p2 Position two of the bounding box
     * @return Oriented bounding box
     */
    public static OrientedBoundingBox naturalFromTo(Vector p1, Vector p2) {
        OrientedBoundingBox box = new OrientedBoundingBox();
        box.setPosition(p1.clone().add(p2).multiply(0.5));
        box.setSize(Math.abs(p2.getX() - p1.getX()),
                    Math.abs(p2.getY() - p1.getY()),
                    Math.abs(p2.getZ() - p1.getZ()));
        return box;
    }

    /**
     * Gets the middle position of this oriented bounding box.
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
     * Sets the middle position of this oriented bounding box.
     * 
     * @param pos The position to set to
     */
    public void setPosition(Vector pos) {
        MathUtil.setVector(position, pos);
    }

    /**
     * Sets the middle position of this oriented bounding box.
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
     *
     * @param orientation The orientation. Passing null will reset it to it's
     *                    natural orientation.
     */
    public void setOrientation(Quaternion orientation) {
        if (orientation == null) {
            this.is_orientation_set = false;
            this.orientation_inv.setIdentity();
        } else {
            this.orientation_inv.setTo(orientation);
            this.orientation_inv.invert();
        }
    }

    /**
     * Performs a hit test to see whether this collision box is hit when
     * looked at from a known position into a certain direction.
     * 
     * @param startX Start position of the ray, X-coordinate
     * @param startY Start position of the ray, Y-coordinate
     * @param startZ Start position of the ray, Z-coordinate
     * @param dirX Ray direction unit vector, X-coordinate
     * @param dirY Ray direction unit vector, Y-coordinate
     * @param dirZ Ray direction unit vector, Z-coordinate
     * @return distance to the box, Double.MAX_VALUE when not touching.
     */
    public double hitTest(double startX, double startY, double startZ,
                          double dirX, double dirY, double dirZ
    ) {
        if (this.is_orientation_set) {
            // Easier to handle all that stuff with actual bukkit vectors
            // Quaternion makes us lose performance anyway so micro-optimizing isn't needed.
            return hitTest(new Vector(startX, startY, startZ), new Vector(dirX, dirY, dirZ));
        }

        // Compute start point
        Vector midPos = this.position;
        double px = startX - midPos.getX();
        double py = startY - midPos.getY();
        double pz = startZ - midPos.getZ();

        // Check start point already inside box
        Vector rad = this.radius;
        if (Math.abs(px) <= rad.getX() && Math.abs(py) <= rad.getY() && Math.abs(pz) <= rad.getZ()) {
            return 0.0;
        }

        return hitTestBase(px, py, pz, dirX, dirY, dirZ);
    }

    /**
     * Performs a hit test to see whether this collision box is hit when
     * looked at from a known position into a certain direction.
     * 
     * @param startPosition Start position coordinates
     * @param startDirection Direction of the ray
     * @return distance to the box, Double.MAX_VALUE when not touching.
     */
    public double hitTest(Vector startPosition, Vector startDirection) {
        // Compute start point
        Vector p = startPosition.clone().subtract(this.position);
        if (this.is_orientation_set) {
            this.orientation_inv.transformPoint(p);
        }

        // Check start point already inside box
        Vector rad = this.radius;
        if (Math.abs(p.getX()) <= rad.getX() && Math.abs(p.getY()) <= rad.getY() && Math.abs(p.getZ()) <= rad.getZ()) {
            return 0.0;
        }

        // Compute direction after rotation
        Vector d = startDirection;
        if (this.is_orientation_set) {
            d = d.clone();
            this.orientation_inv.transformPoint(d);
        }

        return hitTestBase(p, d);
    }

    /**
     * Performs a hit test to see whether this collision box is hit when
     * looked at from a known eye location
     * 
     * @param eyeLocation
     * @return distance to the box, Double.MAX_VALUE when not touching.
     */
    public double hitTest(Location eyeLocation) {
        // Compute start point
        Vector p = eyeLocation.toVector().subtract(this.position);
        if (this.is_orientation_set) {
            this.orientation_inv.transformPoint(p);
        }

        // Check start point already inside box
        Vector rad = this.radius;
        if (Math.abs(p.getX()) <= rad.getX() && Math.abs(p.getY()) <= rad.getY() && Math.abs(p.getZ()) <= rad.getZ()) {
            return 0.0;
        }

        // Compute direction after rotation
        Vector d = eyeLocation.getDirection();
        if (this.is_orientation_set) {
            this.orientation_inv.transformPoint(d);
        }

        return hitTestBase(p, d);
    }

    private double hitTestBase(Vector localPos, Vector localDir) {
        return hitTestBase(localPos.getX(), localPos.getY(), localPos.getZ(),
                           localDir.getX(), localDir.getY(), localDir.getZ());
    }

    private double hitTestBase(double localPosX, double localPosY, double localPosZ,
                               double localDirX, double localDirY, double localDirZ
    ) {
        // Check all 6 faces and find the intersection point with this axis
        // Then check whether these points are within the range of the box
        // If true, compute the distance from the start point and track the smallest value
        final double ERR = 1e-6;
        double min_distance = Double.MAX_VALUE;
        Vector rad = this.radius;
        for (BlockFace dir : FaceUtil.BLOCK_SIDES) {
            double a, b, c;
            if (dir.getModX() != 0) {
                // x
                a = rad.getX() * dir.getModX();
                b = localPosX;
                c = localDirX;
            } else if (dir.getModY() != 0) {
                // y
                a = rad.getY() * dir.getModY();
                b = localPosY;
                c = localDirY;
            } else {
                // z
                a = rad.getZ() * dir.getModZ();
                b = localPosZ;
                c = localDirZ;
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
            if ((Math.abs(localPosX + f * localDirX) - rad.getX()) > ERR) {
                continue;
            }
            if ((Math.abs(localPosY + f * localDirY) - rad.getY()) > ERR) {
                continue;
            }
            if ((Math.abs(localPosZ + f * localDirZ) - rad.getZ()) > ERR) {
                continue;
            }

            // Since d is a unit vector, f is now the distance we need
            min_distance = f;
        }
        return min_distance;
    }
}
