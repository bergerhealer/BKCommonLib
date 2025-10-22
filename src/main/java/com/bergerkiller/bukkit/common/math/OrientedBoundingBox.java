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
    private final Quaternion orientation = new Quaternion();
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
        return this.orientation;
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
            this.orientation.setIdentity();
        } else {
            this.is_orientation_set = true;
            this.orientation.setTo(orientation);
        }
    }

    /**
     * Tests whether a point vector is inside this bounding box. Equivalent to testing
     * {@link #distanceToPoint(Vector)} is inside.
     *
     * @param point The point to test inside
     * @return True if the point is inside this oriented bounding box
     */
    public boolean isInside(Vector point) {
        return isInside(point.getX(), point.getY(), point.getZ());
    }

    /**
     * Tests whether a point vector is inside this bounding box. Equivalent to testing
     * {@link #distanceToPoint(Vector)} is inside.
     *
     * @param x X-coordinate of the point to test inside
     * @param y Y-coordinate of the point to test inside
     * @param z Z-coordinate of the point to test inside
     * @return True if the point is inside this oriented bounding box
     */
    public boolean isInside(double x, double y, double z) {
        // Transform the point into the local space of the bounding box
        Vector localPoint = new Vector(x, y, z).subtract(this.position);
        if (this.is_orientation_set) {
            this.orientation.invTransformPoint(localPoint);
        }

        // Test this local point is within the AABB
        return testLocalPointInside(localPoint.getX(), localPoint.getY(), localPoint.getZ());
    }

    /**
     * Calculates the shortest distance from a point to this oriented bounding box
     * and returns the hit test result, including the closest position, distance,
     * and normal vector.
     *
     * @param point The point to calculate the distance to.
     * @return A HitTestResult containing the closest position, distance, and normal vector.
     */
    public HitTestResult distanceToPoint(Vector point) {
        // Transform the point into the local space of the bounding box
        Vector localPoint = point.clone().subtract(this.position);
        if (this.is_orientation_set) {
            this.orientation.invTransformPoint(localPoint);
        }

        // Calculate the closest point on the box in local space
        Vector rad = this.radius;
        Vector closestPoint = new Vector(
                MathUtil.clamp(localPoint.getX(), -rad.getX(), rad.getX()),
                MathUtil.clamp(localPoint.getY(), -rad.getY(), rad.getY()),
                MathUtil.clamp(localPoint.getZ(), -rad.getZ(), rad.getZ())
        );

        // If inside the box, return an inside result and skip the normal vector / rotation stuff
        if (localPoint.equals(closestPoint)) {
            MathUtil.setVector(closestPoint, point);
            return HitTestResult.inside(closestPoint);
        }

        // Calculate the normal vector
        Vector normal = new Vector();
        double dx = Math.abs(closestPoint.getX() - localPoint.getX());
        double dy = Math.abs(closestPoint.getY() - localPoint.getY());
        double dz = Math.abs(closestPoint.getZ() - localPoint.getZ());
        if (dx > dy && dx > dz) {
            normal.setX(Math.signum(localPoint.getX()));
        } else if (dy > dz) {
            normal.setY(Math.signum(localPoint.getY()));
        } else {
            normal.setZ(Math.signum(localPoint.getZ()));
        }

        // Transform the closest point and normal back to world space
        if (this.is_orientation_set) {
            this.orientation.transformPoint(closestPoint);
            this.orientation.transformPoint(normal);
        }
        closestPoint.add(this.position);

        // Return the hit test result
        return new HitTestResult(closestPoint, normal, point.distance(closestPoint));
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
        return performHitTest(startX, startY, startZ, dirX, dirY, dirZ).distance();
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
        return performHitTest(startPosition, startDirection).distance();
    }

    /**
     * Performs a hit test to see whether this collision box is hit when
     * looked at from a known eye location
     *
     * @param eyeLocation
     * @return distance to the box, Double.MAX_VALUE when not touching.
     */
    public double hitTest(Location eyeLocation) {
        return performHitTest(eyeLocation).distance();
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
     * @return hit test results, including the distance, position and surface normal
     */
    public HitTestResult performHitTest(double startX, double startY, double startZ,
                                        double dirX, double dirY, double dirZ
    ) {
        if (this.is_orientation_set) {
            // Easier to handle all that stuff with actual bukkit vectors
            // Quaternion makes us lose performance anyway so micro-optimizing isn't needed.
            return performHitTest(new Vector(startX, startY, startZ), new Vector(dirX, dirY, dirZ));
        }

        // Compute start point
        Vector midPos = this.position;
        double px = startX - midPos.getX();
        double py = startY - midPos.getY();
        double pz = startZ - midPos.getZ();

        // Check start point already inside box
        if (testLocalPointInside(px, py, pz)) {
            return HitTestResult.inside(new Vector(startX, startY, startZ));
        }

        // Perform the actual hit-test, then compute the non-localized position if it hit
        HitTestResult result = hitTestBase(px, py, pz, dirX, dirY, dirZ);
        if (result.success()) {
            if (result.inside()) {
                result.position.setX(startX);
                result.position.setY(startY);
                result.position.setZ(startZ);
            } else {
                result.position.setX(startX + result.distance() * dirX);
                result.position.setY(startY + result.distance() * dirY);
                result.position.setZ(startZ + result.distance() * dirZ);
                if (this.is_orientation_set) {
                    this.orientation.transformPoint(result.normal);
                }
            }
        }
        return result;
    }

    /**
     * Performs a hit test to see whether this collision box is hit when
     * looked at from a known position into a certain direction.
     *
     * @param startPosition Start position coordinates
     * @param startDirection Direction of the ray
     * @return hit test results, including the distance, position and surface normal
     */
    public HitTestResult performHitTest(Vector startPosition, Vector startDirection) {
        // Compute start point
        Vector p = startPosition.clone().subtract(this.position);
        if (this.is_orientation_set) {
            this.orientation.invTransformPoint(p);
        }

        // Check start point already inside box
        if (testLocalPointInside(p.getX(), p.getY(), p.getZ())) {
            return HitTestResult.inside(startPosition);
        }

        // Compute direction after rotation
        Vector d = startDirection;
        if (this.is_orientation_set) {
            d = d.clone();
            this.orientation.invTransformPoint(d);
        }

        // Perform the actual hit-test, then compute the non-localized position if it hit
        HitTestResult result = hitTestBase(p, d);
        if (result.success()) {
            if (result.inside()) {
                result.position.copy(startPosition);
            } else {
                result.position.setX(startPosition.getX() + result.distance() * startDirection.getX());
                result.position.setY(startPosition.getY() + result.distance() * startDirection.getY());
                result.position.setZ(startPosition.getZ() + result.distance() * startDirection.getZ());
                if (this.is_orientation_set) {
                    this.orientation.transformPoint(result.normal);
                }
            }
        }
        return result;
    }

    /**
     * Performs a hit test to see whether this collision box is hit when
     * looked at from a known eye location
     *
     * @param eyeLocation
     * @return hit test results, including the distance, position and surface normal
     */
    public HitTestResult performHitTest(Location eyeLocation) {
        return performHitTest(eyeLocation.toVector(), eyeLocation.getDirection());
    }

    private HitTestResult hitTestBase(Vector localPos, Vector localDir) {
        return hitTestBase(localPos.getX(), localPos.getY(), localPos.getZ(),
                           localDir.getX(), localDir.getY(), localDir.getZ());
    }

    private HitTestResult hitTestBase(double localPosX, double localPosY, double localPosZ,
                                      double localDirX, double localDirY, double localDirZ
    ) {
        // Check all 6 faces and find the intersection point with this axis
        // Then check whether these points are within the range of the box
        // If true, compute the distance from the start point and track the smallest value
        final double ERR = 1e-6;
        double min_distance = Double.MAX_VALUE;
        BlockFace min_dir = null;
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
            min_dir = dir;
        }

        // Not hit the box at all?
        if (min_distance == Double.MAX_VALUE) {
            return HitTestResult.MISSED;
        }

        // Inside?
        if (min_distance <= 0.0) {
            return HitTestResult.inside(new Vector());
        }

        // Compute exact position and normal vector from the distance and face
        return new HitTestResult(new Vector(), FaceUtil.faceToVector(min_dir), min_distance);
    }

    private boolean testLocalPointInside(double x, double y, double z) {
        Vector rad = this.radius;
        return Math.abs(x) <= rad.getX() && Math.abs(y) <= rad.getY() && Math.abs(z) <= rad.getZ();
    }

    /**
     * Checks if this OBB overlaps with another OBB using SAT.
     *
     * @param other The other oriented bounding box
     * @return True if the boxes overlap, false otherwise
     */
    public boolean hasOverlap(OrientedBoundingBox other) {
        return VertexPoints.areVerticesOverlapping(this.getVertices(), other.getVertices(),
                createSeparatingAxisIterator(this.getOrientation(), other.getOrientation()));
    }

    /**
     * Tests if another OrientedBoundingBox is fully inside of this OrientedBoundingBox.
     * Note: does not test intersection, for that use {@link #hasOverlap(OrientedBoundingBox)}
     *
     * @param other The OrientedBoundingBox to test is inside this one
     * @return True if the specified OBB is completely inside this one, false otherwise
     */
    public boolean isInside(OrientedBoundingBox other) {
        VertexPoints.PointIterator iter = other.getVertices().pointIterator();
        while (iter.next()) {
            if (!this.isInside(iter.x(), iter.y(), iter.z())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the 8 vertices of this OBB in world coordinates.
     *
     * @return An array of 8 vertices
     */
    public VertexPoints getVertices() {
        //TODO: Cache
        VertexPoints.BoxBuilder boxBuilder = VertexPoints.boxBuilder()
                .halfSize(radius);
        if (is_orientation_set) {
            boxBuilder = boxBuilder.rotate(orientation);
        }
        return boxBuilder.translate(position).build();
    }

    /**
     * Returns a PointIterator that iterates over the normalized axis for comparing two oriented
     * bounding box orientations.
     *
     * @param box1Ori Orientation of box 1
     * @param box2Ori Orientation of box 2
     * @return PointIterator
     * @see VertexPoints#areVerticesOverlapping(VertexPoints, VertexPoints, VertexPoints.PointIterator) 
     */
    public static VertexPoints.PointIterator createSeparatingAxisIterator(Quaternion box1Ori, Quaternion box2Ori) {
        return box1Ori.equals(box2Ori) ? new SATAxisIteratorAligned(box1Ori)
                                       : new SATAxisIteratorMisaligned(box1Ori, box2Ori);
    }

    /**
     * Generates all the axis vectors required for SAT. This is used for testing
     * intersection between two oriented bounding boxes. This is for when the two
     * boxes do not share the same orientation.
     */
    private static final class SATAxisIteratorMisaligned extends VertexPoints.PointIterator {
        private final Vector box1Right, box1Up, box1Forward, box2Right, box2Up, box2Forward;
        private int producerIdx = 0;

        public SATAxisIteratorMisaligned(Quaternion box1Ori, Quaternion box2Ori) {
            box1Right = box1Ori.rightVector();
            box1Up = box1Ori.upVector();
            box1Forward = box1Ori.forwardVector();
            box2Right = box2Ori.rightVector();
            box2Up = box2Ori.upVector();
            box2Forward = box2Ori.forwardVector();
        }

        @Override
        public boolean next() {
            while (producerIdx < 15) {
                switch (producerIdx++) {
                    // Box 1 axes
                    case 0: load(box1Right); break;
                    case 1: load(box1Up); break;
                    case 2: load(box1Forward); break;

                    // Box 2 axes
                    case 3: load(box2Right); break;
                    case 4: load(box2Up); break;
                    case 5: load(box2Forward); break;

                    // Same-axis cross products
                    case 6: if (!tryLoadNormalizedCrossProduct(box1Right, box2Right)) continue; break;
                    case 7: if (!tryLoadNormalizedCrossProduct(box1Up, box2Up)) continue; break;
                    case 8: if (!tryLoadNormalizedCrossProduct(box1Forward, box2Forward)) continue; break;

                    // Mixed cross products
                    case 9:  if (!tryLoadNormalizedCrossProduct(box1Right, box2Up)) continue; break;
                    case 10: if (!tryLoadNormalizedCrossProduct(box1Right, box2Forward)) continue; break;
                    case 11: if (!tryLoadNormalizedCrossProduct(box1Up, box2Forward)) continue; break;
                    case 12: if (!tryLoadNormalizedCrossProduct(box1Up, box2Right)) continue; break;
                    case 13: if (!tryLoadNormalizedCrossProduct(box1Forward, box2Up)) continue; break;
                    case 14: if (!tryLoadNormalizedCrossProduct(box1Forward, box2Right)) continue; break;
                }

                index++;
                return true;
            }

            return false;
        }
    }

    /**
     * Generates all the axis vectors required for SAT. This is used for testing
     * intersection between two oriented bounding boxes. This is for when the two
     * boxes do share the same orientation.
     */
    private static final class SATAxisIteratorAligned extends VertexPoints.PointIterator {
        private final Vector right, up, forward;
        private int producerIdx = 0;

        public SATAxisIteratorAligned(Quaternion ori) {
            right = ori.rightVector();
            up = ori.upVector();
            forward = ori.forwardVector();
        }

        @Override
        public boolean next() {
            while (producerIdx < 9) {
                switch (producerIdx++) {
                    // Box 1 axes
                    case 0: load(right); break;
                    case 1: load(up); break;
                    case 2: load(forward); break;

                    // Mixed cross products
                    case 3: if (!tryLoadNormalizedCrossProduct(right, up)) continue; break;
                    case 4: if (!tryLoadNormalizedCrossProduct(right, forward)) continue; break;
                    case 5: if (!tryLoadNormalizedCrossProduct(up, forward)) continue; break;
                    case 6: if (!tryLoadNormalizedCrossProduct(up, right)) continue; break;
                    case 7: if (!tryLoadNormalizedCrossProduct(forward, up)) continue; break;
                    case 8: if (!tryLoadNormalizedCrossProduct(forward, right)) continue; break;
                }

                index++;
                return true;
            }

            return false;
        }
    }

    /**
     * The results from firing a point ray at an oriented bounding box.
     * The vectors contained inside are copies and can be safely modified,
     * but will modify this hit test result.
     */
    public static class HitTestResult {
        /**
         * A constant result for if the ray misses
         */
        public static final HitTestResult MISSED = new HitTestResult(null, null, Double.MAX_VALUE);

        private final Vector position;
        private final Vector normal;
        private final double distance;

        public HitTestResult(Vector position, Vector normal, double distance) {
            this.position = position;
            this.normal = normal;
            this.distance = distance;
        }

        /**
         * Gets whether the hit test was a success. If true, then a hit position
         * , normal vector and distance are available. If false, then these
         * properties are null or set to Double.MAX_VALUE.
         *
         * @return True if successful
         */
        public boolean success() {
            return distance != Double.MAX_VALUE;
        }

        /**
         * Gets whether the hit test result was inside the bounding box itself.
         *
         * @return True if inside the bounding box
         */
        public boolean inside() {
            return distance == 0.0;
        }

        /**
         * The exact position coordinates on the box where the ray hit
         *
         * @return hit position. Null if it missed the box.
         */
        public Vector position() {
            return position;
        }

        /**
         * Normal vector of the surface of the box where the ray hit.
         * Is {0, 0, 0} if inside the bounding box itself.
         *
         * @return normal vector at ray collision. Null if it missed the box.
         */
        public Vector normal() {
            return normal;
        }

        /**
         * The distance between the ray origin and the surface of the
         * box hit. Is 0 if the ray started inside the box.
         *
         * @return distance between ray origin and box
         */
        public double distance() {
            return distance;
        }

        /**
         * Creates a new hit test result that is inside the bounding box
         *
         * @param position Position of the start of the ray
         * @return inside result
         */
        public static HitTestResult inside(Vector position) {
            return new HitTestResult(position, new Vector(), 0.0);
        }
    }
}
