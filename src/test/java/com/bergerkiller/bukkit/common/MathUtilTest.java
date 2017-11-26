package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.bukkit.util.Vector;
import org.junit.Test;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Vector3;

public class MathUtilTest {

    @Test
    public void testMatrixRotation() {
        Matrix4x4 transform = new Matrix4x4();
        transform.translateRotate(2.0, 3.0, 4.0, 34.0f, -12.0f);

        Vector3 vec = new Vector3();
        transform.transformPoint(vec);
        assertEquals(2.0, vec.x, 0.00001);
        assertEquals(3.0, vec.y, 0.00001);
        assertEquals(4.0, vec.z, 0.00001);

        Vector yawPitchRoll = transform.getYawPitchRoll();
        assertEquals(-12.0, yawPitchRoll.getX(), 0.001);
        assertEquals(34.0, yawPitchRoll.getY(), 0.001);
        assertEquals(0.0, yawPitchRoll.getZ(), 0.001);

        transform.rotateZ(30.0);
        yawPitchRoll = transform.getYawPitchRoll();
        assertEquals(-13.790, yawPitchRoll.getX(), 0.001);
        assertEquals(27.155, yawPitchRoll.getY(), 0.001);
        assertEquals(29.279, yawPitchRoll.getZ(), 0.001);

        transform.scale(1.5, 2.5, 3.5);
        yawPitchRoll = transform.getYawPitchRoll();
        assertEquals(-18.963, yawPitchRoll.getX(), 0.001);
        assertEquals(27.155, yawPitchRoll.getY(), 0.001);
        assertEquals(47.190, yawPitchRoll.getZ(), 0.001);
    }
}
