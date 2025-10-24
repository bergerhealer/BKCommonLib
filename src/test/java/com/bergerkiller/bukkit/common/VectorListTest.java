package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.math.VectorList;
import com.bergerkiller.bukkit.common.math.VectorListMutable;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import org.bukkit.util.Vector;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the correct functioning of the VectorList
 */
public class VectorListTest {

    @Test
    public void testMutableGetSet() {
        VectorListMutable vectorList = VectorListMutable.create(8);
        assertEquals(8, vectorList.size());
        for (int i = 0; i < 8; i++) {
            vectorList.set(i, i, i + 1, i + 2);

            Vector v = vectorList.get(i);
            assertEquals(i, v.getX(), 1e-10);
            assertEquals(i + 1, v.getY(), 1e-10);
            assertEquals(i + 2, v.getZ(), 1e-10);
        }
    }

    @Test
    public void testMutableRotate() {
        VectorListMutable vectorList = VectorListMutable.create(2);
        vectorList.set(0, 10.0, 0.0, 0.0);
        vectorList.set(1, 0.0, 0.0, 10.0);

        vectorList.rotateYawPitchRoll(0.0, 90.0, 0.0);
        MathUtilTest.testVectorsEqual(new Vector(0.0, 0.0, 10.0), vectorList.get(0), 1e-10);
        MathUtilTest.testVectorsEqual(new Vector(-10.0, 0.0, 0.0), vectorList.get(1), 1e-10);

        vectorList.rotateYawPitchRoll(90.0, 0.0, 0.0);
        MathUtilTest.testVectorsEqual(new Vector(0.0, -10.0, 0.0), vectorList.get(0), 1e-10);
        MathUtilTest.testVectorsEqual(new Vector(-10.0, 0.0, 0.0), vectorList.get(1), 1e-10);
    }

    @Test
    public void testMutableTranslate() {
        VectorListMutable vectorList = VectorListMutable.create(2);
        vectorList.set(0, 10.0, 0.0, 0.0);
        vectorList.set(1, 0.0, 0.0, 10.0);

        vectorList.translate(1.0, 2.0, 3.0);
        MathUtilTest.testVectorsEqual(new Vector(11.0, 2.0, 3.0), vectorList.get(0), 1e-10);
        MathUtilTest.testVectorsEqual(new Vector(1.0, 2.0, 13.0), vectorList.get(1), 1e-10);
    }

    @Test
    public void testAxisProjection() {
        VectorListMutable box = VectorListMutable.createBoxVertices(2.0, 2.0, 2.0);
        box.rotateY(45.0);

        VectorList list = box.immutable();
        VectorList.Projection projection = list.projectAxis(1.0, 0.0, 0.0);
        assertEquals(-2.0 * MathUtil.HALFROOTOFTWO, projection.min, 1e-8);
        assertEquals(2.0 * MathUtil.HALFROOTOFTWO, projection.max, 1e-8);
    }
}
