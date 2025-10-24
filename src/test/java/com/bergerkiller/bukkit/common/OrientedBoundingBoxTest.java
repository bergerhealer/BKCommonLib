package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import com.bergerkiller.bukkit.common.math.OrientedBoundingBox;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.math.VectorList;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import org.bukkit.util.Vector;
import org.junit.Ignore;
import org.junit.Test;

public class OrientedBoundingBoxTest {

    @Ignore
    @Test
    public void benchmarkBoxIntersection() {
        OrientedBoundingBox box_a = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(4, 4, 4), null);
        OrientedBoundingBox box_b = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(2.3, 2.3, 2.3),
                Quaternion.fromYawPitchRoll(45.0, 45.0, 45.0));

        // Current:
        //   VectorListBasicImpl: 8.8s
        //   VectorListOctoSIMD256Impl: 5.8s
        for (long l = 0; l < 10000000; l++) {
            assertTrue(VectorList.areVerticesOverlapping(box_a.getVertices(), box_b.getVertices(),
                    OrientedBoundingBox.createSeparatingAxisIterator(box_a.getOrientation(), box_b.getOrientation())));
        }
    }

    @Test
    public void testBoxIntersectionInside() {
        OrientedBoundingBox box_a = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(4, 4, 4), null);
        OrientedBoundingBox box_b = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(2.3, 2.3, 2.3),
                Quaternion.fromYawPitchRoll(45.0, 45.0, 45.0));

        assertTrue(box_a.isInside(box_b));
        assertFalse(box_b.isInside(box_a));
        assertTrue(box_a.hasOverlap(box_b));
        assertTrue(box_b.hasOverlap(box_a));
    }

    @Test
    public void testBoxIntersectionRotatedIn() {
        OrientedBoundingBox box_a = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(4, 4, 4),
                Quaternion.fromYawPitchRoll(0.0, 90, 0.0));
        OrientedBoundingBox box_b = new OrientedBoundingBox(new Vector(3.9, 0, 0), new Vector(4, 4, 4),
                Quaternion.fromYawPitchRoll(0.0, -90, 0.0));

        assertFalse(box_a.isInside(box_b));
        assertFalse(box_b.isInside(box_a));
        assertTrue(box_a.hasOverlap(box_b));
        assertTrue(box_b.hasOverlap(box_a));
    }

    @Test
    public void testBoxIntersectionRotatedOut() {
        OrientedBoundingBox box_a = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(4, 4, 4),
                Quaternion.fromYawPitchRoll(0.0, 90, 0.0));
        OrientedBoundingBox box_b = new OrientedBoundingBox(new Vector(12.0, 0.0, 0.0), new Vector(4, 4, 4),
                Quaternion.fromYawPitchRoll(0.0, -90, 0.0));

        assertFalse(box_a.isInside(box_b));
        assertFalse(box_b.isInside(box_a));
        assertFalse(box_a.hasOverlap(box_b));
        assertFalse(box_b.hasOverlap(box_a));
    }

    @Test
    public void testBoxIntersectionSimpleYes() {
        OrientedBoundingBox box_a = new OrientedBoundingBox(new Vector(50.0, 0.0, -20.0), new Vector(10, 2, 2), null);
        OrientedBoundingBox box_b = new OrientedBoundingBox(new Vector(50.0, 0.0, -20.0), new Vector(2, 2, 10), null);

        assertFalse(box_a.isInside(box_b));
        assertFalse(box_b.isInside(box_a));
        assertTrue(box_a.hasOverlap(box_b));
        assertTrue(box_b.hasOverlap(box_a));
    }

    @Test
    public void testBoxIntersectionSimpleNo() {
        OrientedBoundingBox box_a = new OrientedBoundingBox(new Vector(50.0, 0.0, -10.0), new Vector(10, 2, 2), null);
        OrientedBoundingBox box_b = new OrientedBoundingBox(new Vector(50.0, 0.0, -20.0), new Vector(2, 2, 10), null);

        assertFalse(box_a.isInside(box_b));
        assertFalse(box_b.isInside(box_a));
        assertFalse(box_a.hasOverlap(box_b));
        assertFalse(box_b.hasOverlap(box_a));
    }

    @Test
    public void testPointInsideRotated() {
        OrientedBoundingBox box = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(4, 4, 4),
                Quaternion.fromYawPitchRoll(0.0, 45, 0.0));

        // Test a point that is inside the box, but it would only be if rotated 45 degrees
        // If rotation isn't taken into account, this test would fail
        Vector point = new Vector(1, 0, 1);
        assertTrue(box.isInside(point));
    }

    @Test
    public void testDistanceToPointInside() {
        OrientedBoundingBox box = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(4, 4, 4),
                Quaternion.fromYawPitchRoll(0.0, 45, 0.0));

        // Test a point that is inside the box, but it would only be if rotated 45 degrees
        // If rotation isn't taken into account, this test would fail
        Vector start = new Vector(2.5, 0, 0);
        OrientedBoundingBox.HitTestResult result = box.distanceToPoint(start);

        assertTrue(result.success());
        assertTrue(result.inside());
        assertTrue(box.isInside(start));
        assertEquals(0.0, result.distance(), 1e-10);
    }

    @Test
    public void testDistanceToPointWithRotation() {
        OrientedBoundingBox box = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(4, 4, 4),
                Quaternion.fromYawPitchRoll(0.0, 45, 0.0));

        // Test a ray that hits the surface of the box
        // Box has a 2-wide edge from origin, so starting at 7 we expect a distance of 5
        Vector start = new Vector(7, 0, 0);
        OrientedBoundingBox.HitTestResult result = box.distanceToPoint(start);

        assertTrue(result.success());
        assertFalse(result.inside());
        assertFalse(box.isInside(start));
        assertEquals(7.0 - Math.sqrt(4*4 + 4*4) / 2, result.distance(), 1e-10);
        MathUtilTest.testVectorsEqual(new Vector(Math.sqrt(4*4 + 4*4) / 2, 0, 0), result.position(), 1e-10);

        // Note: is on edge of cube so might as well be the other edge
        MathUtilTest.testVectorsEqual(new Vector(MathUtil.HALFROOTOFTWO, 0, -MathUtil.HALFROOTOFTWO), result.normal(), 1e-8);
    }

    @Test
    public void testDistanceToPointX() {
        OrientedBoundingBox box = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(4, 4, 4), null);

        // Test a ray that hits the surface of the box
        // Box has a 2-wide edge from origin, so starting at 7 we expect a distance of 5
        Vector start = new Vector(7, 0, 0);
        OrientedBoundingBox.HitTestResult result = box.distanceToPoint(start);

        assertTrue(result.success());
        assertFalse(result.inside());
        assertFalse(box.isInside(start));
        assertEquals(5.0, result.distance(), 0.0);
        assertEquals(new Vector(2, 0, 0), result.position());
        assertEquals(new Vector(1, 0, 0), result.normal());
    }

    @Test
    public void testDistanceToPointY() {
        OrientedBoundingBox box = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(4, 4, 4), null);

        // Test a ray that hits the surface of the box
        // Box has a 2-wide edge from origin, so starting at 7 we expect a distance of 5
        Vector start = new Vector(0, -7, 0);
        OrientedBoundingBox.HitTestResult result = box.distanceToPoint(start);

        assertTrue(result.success());
        assertFalse(result.inside());
        assertFalse(box.isInside(start));
        assertEquals(5.0, result.distance(), 0.0);
        assertEquals(new Vector(0, -2, 0), result.position());
        assertEquals(new Vector(0, -1, 0), result.normal());
    }

    @Test
    public void testDistanceToPointZ() {
        OrientedBoundingBox box = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(4, 4, 4), null);

        // Test a ray that hits the surface of the box
        // Box has a 2-wide edge from origin, so starting at 7 we expect a distance of 5
        Vector start = new Vector(0, 0, -7);
        OrientedBoundingBox.HitTestResult result = box.distanceToPoint(start);

        assertTrue(result.success());
        assertFalse(result.inside());
        assertFalse(box.isInside(start));
        assertEquals(5.0, result.distance(), 0.0);
        assertEquals(new Vector(0, 0, -2), result.position());
        assertEquals(new Vector(0, 0, -1), result.normal());
    }

    @Test
    public void testHitTestOutsideBox() {
        OrientedBoundingBox box = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(2, 2, 2), null);

        // Test a ray that misses the box
        Vector start = new Vector(5, 5, 5);
        Vector direction = new Vector(1, 1, 1).normalize();
        OrientedBoundingBox.HitTestResult result = box.performHitTest(start, direction);

        assertFalse(result.success());
        assertEquals(Double.MAX_VALUE, result.distance(), 0.0);
    }

    @Test
    public void testHitTestInsideBox() {
        OrientedBoundingBox box = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(2, 2, 2), null);

        // Test a ray starting inside the box
        Vector start = new Vector(0.5, 0.5, 0.5);
        Vector direction = new Vector(1, 0, 0).normalize();
        OrientedBoundingBox.HitTestResult result = box.performHitTest(start, direction);

        assertTrue(result.success());
        assertTrue(result.inside());
        assertEquals(0.0, result.distance(), 0.0);
    }

    @Test
    public void testHitTestSurfaceHitWithRotation() {
        OrientedBoundingBox box = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(4, 4, 4),
                Quaternion.fromYawPitchRoll(0.0, 45, 0.0));

        // Test a ray that hits the surface of the box
        // Box has a 2-wide edge from origin, so starting at 7 we expect a distance of 5
        Vector start = new Vector(7, 0, 0);
        Vector direction = new Vector(-1, 0, 0).normalize();
        OrientedBoundingBox.HitTestResult result = box.performHitTest(start, direction);

        assertTrue(result.success());
        assertFalse(result.inside());
        assertEquals(7.0 - Math.sqrt(4*4 + 4*4) / 2, result.distance(), 1e-10);
        MathUtilTest.testVectorsEqual(new Vector(Math.sqrt(4*4 + 4*4) / 2, 0, 0), result.position(), 1e-10);

        // Note: is on edge of cube so might as well be the other edge
        MathUtilTest.testVectorsEqual(new Vector(MathUtil.HALFROOTOFTWO, 0, -MathUtil.HALFROOTOFTWO), result.normal(), 1e-8);
    }

    @Test
    public void testHitTestSurfaceHitX() {
        OrientedBoundingBox box = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(4, 4, 4), null);

        // Test a ray that hits the surface of the box
        // Box has a 2-wide edge from origin, so starting at 7 we expect a distance of 5
        Vector start = new Vector(7, 0, 0);
        Vector direction = new Vector(-1, 0, 0).normalize();
        OrientedBoundingBox.HitTestResult result = box.performHitTest(start, direction);

        assertTrue(result.success());
        assertFalse(result.inside());
        assertEquals(5.0, result.distance(), 0.0);
        assertEquals(new Vector(2, 0, 0), result.position());
        assertEquals(new Vector(1, 0, 0), result.normal());
    }

    @Test
    public void testHitTestSurfaceHitY() {
        OrientedBoundingBox box = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(4, 4, 4), null);

        // Test a ray that hits the surface of the box
        // Box has a 2-wide edge from origin, so starting at 7 we expect a distance of 5
        Vector start = new Vector(0, -7, 0);
        Vector direction = new Vector(0, 1, 0).normalize();
        OrientedBoundingBox.HitTestResult result = box.performHitTest(start, direction);

        assertTrue(result.success());
        assertFalse(result.inside());
        assertEquals(5.0, result.distance(), 0.0);
        assertEquals(new Vector(0, -2, 0), result.position());
        assertEquals(new Vector(0, -1, 0), result.normal());
    }

    @Test
    public void testHitTestSurfaceHitZ() {
        OrientedBoundingBox box = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(4, 4, 4), null);

        // Test a ray that hits the surface of the box
        // Box has a 2-wide edge from origin, so starting at 7 we expect a distance of 5
        Vector start = new Vector(0, 0, -7);
        Vector direction = new Vector(0, 0, 1).normalize();
        OrientedBoundingBox.HitTestResult result = box.performHitTest(start, direction);

        assertTrue(result.success());
        assertFalse(result.inside());
        assertEquals(5.0, result.distance(), 0.0);
        assertEquals(new Vector(0, 0, -2), result.position());
        assertEquals(new Vector(0, 0, -1), result.normal());
    }

    @Test
    public void testSetSize() {
        OrientedBoundingBox box = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(4, 4, 4), null);

        // Verify initial size
        assertEquals(new Vector(4, 4, 4), box.getSize());

        // Update size
        box.setSize(new Vector(6, 6, 6));
        assertEquals(new Vector(6, 6, 6), box.getSize());

        // Update size with individual components
        box.setSize(8, 8, 8);
        assertEquals(new Vector(8, 8, 8), box.getSize());
    }

    @Test
    public void testSetPosition() {
        OrientedBoundingBox box = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(2, 2, 2), null);

        // Update position
        box.setPosition(new Vector(10, 10, 10));
        assertEquals(new Vector(10, 10, 10), box.getPosition());
    }

    @Test
    public void testSetOrientation() {
        OrientedBoundingBox box = new OrientedBoundingBox(new Vector(0, 0, 0), new Vector(2, 2, 2), null);

        // Update orientation
        Quaternion orientation = Quaternion.fromAxisAngles(0, 1, 0, 90);
        box.setOrientation(orientation);
        assertEquals(orientation, box.getOrientation());
    }
}