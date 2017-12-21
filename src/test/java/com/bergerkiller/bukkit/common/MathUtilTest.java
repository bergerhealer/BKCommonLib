package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.util.Vector;
import org.junit.Test;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.math.Vector3;

public class MathUtilTest {

    @Test
    public void testMatrixRotation() {
        Matrix4x4 transform = new Matrix4x4();
        transform.translateRotate(2.0, 3.0, 4.0, -12.0f, 34.0f);

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
        assertEquals(-12.0, yawPitchRoll.getX(), 0.001);
        assertEquals(34.0, yawPitchRoll.getY(), 0.001);
        assertEquals(30.0, yawPitchRoll.getZ(), 0.001);

        transform.scale(1.5, 2.5, 3.5);
        yawPitchRoll = transform.getYawPitchRoll();
        assertEquals(-16.729, yawPitchRoll.getX(), 0.001);
        assertEquals(43.496, yawPitchRoll.getY(), 0.001);
        assertEquals(30.787, yawPitchRoll.getZ(), 0.001);

        // Try a bunch of random yaw/pitch/roll values and see that they all work
        Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            Vector result;
            Vector rotation = new Vector(180.0 * rand.nextDouble() - 90.0,
                                         180.0 * rand.nextDouble() - 90.0,
                                         180.0 * rand.nextDouble() - 90.0);

            transform = new Matrix4x4();
            transform.rotateYawPitchRoll(rotation);
            result = transform.getYawPitchRoll();
            assertEquals(rotation.getX(), result.getX(), 0.001);
            assertEquals(rotation.getY(), result.getY(), 0.001);
            assertEquals(rotation.getZ(), result.getZ(), 0.001);
        }
    }

    @Test
    public void testQuaternionYPRRollSuppression() {
        // Try many different kind of rotations, and verify that roll is always kept between -90 and 90
        for (int i = 0; i < 10000; i++) {
            Quaternion q = Quaternion.fromYawPitchRoll(360.0 * Math.random(),
                                                       360.0 * Math.random(),
                                                       360.0 * Math.random());

            Vector ypr = q.getYawPitchRoll();
            assertTrue("mynum is out of range: " + ypr.getZ(), ypr.getZ() >= -90.0 && ypr.getZ() <= 90.0);

            // Also check that the yaw/pitch/roll is actually valid
            Quaternion q_verify = Quaternion.fromYawPitchRoll(ypr);
            testQuaternionsEqual(q, q_verify);
        }
    }

    @Test
    public void testQuaternionRotation() {
        // Perform the same rotations with a normal 4x4 transform matrix, and a quaternion
        Matrix4x4 transform = new Matrix4x4();
        Quaternion quaternion = new Quaternion();
        transform.rotateX(20.0);
        quaternion.multiply(Quaternion.fromAxisAngles(1.0, 0.0, 0.0, 20.0));
        transform.rotateY(-33.4);
        quaternion.multiply(Quaternion.fromAxisAngles(0.0, 1.0, 0.0, -33.4));
        transform.rotateZ(12.4);
        quaternion.multiply(Quaternion.fromAxisAngles(0.0, 0.0, 1.0, 12.4));
        Matrix4x4 quaternion_transform = quaternion.toMatrix4x4();

        // Confirm that the quaternion transformation is closely equal to the other one
        double[] a = new double[16];
        double[] b = new double[16];
        transform.toArray(a);
        quaternion_transform.toArray(b);
        for (int i = 0; i < 16; i++) {
            assertEquals(a[i], b[i], 0.0001);
        }

        // Also confirm that the yaw/pitch/roll values are equal
        // There will always be some error involved, because the calculations are different
        Vector ypr_a = transform.getYawPitchRoll();
        Vector ypr_b = quaternion.getYawPitchRoll();
        assertEquals(ypr_a.getX(), ypr_b.getX(), 0.001);
        assertEquals(ypr_a.getY(), ypr_b.getY(), 0.001);
        assertEquals(ypr_a.getZ(), ypr_b.getZ(), 0.001);

        // Verify that the matrix turned into a Quaternion is the same
        transform = new Matrix4x4();
        quaternion = new Quaternion();
        transform.translate(5.0, 23.3, -63.2);
        transform.rotateYawPitchRoll(33.2, -53.2, 12.5);
        quaternion.rotateYawPitchRoll(33.2, -53.2, 12.5);
        Quaternion transform_to_quat = transform.getRotation();
        assertEquals(quaternion.getX(), transform_to_quat.getX(), 0.00001);
        assertEquals(quaternion.getY(), transform_to_quat.getY(), 0.00001);
        assertEquals(quaternion.getZ(), transform_to_quat.getZ(), 0.00001);
        assertEquals(quaternion.getW(), transform_to_quat.getW(), 0.00001);

        // Also test the optimized Quaternion rotateX/Y/Z functions
        quaternion = new Quaternion();
        quaternion.rotateX(20.0);
        quaternion.rotateY(-33.4);
        quaternion.rotateZ(12.4);
        quaternion_transform = quaternion.toMatrix4x4();
        quaternion_transform.toArray(b);
        for (int i = 0; i < 16; i++) {
            assertEquals(a[i], b[i], 0.0001);
        }

        // Also test Matrix multiplication with a quaternion
        // This time, the matrix is in a state that is not the identity matrix
        transform = new Matrix4x4();
        transform.translate(20.0, 32.0, -53.0);
        transform.rotateYawPitchRoll(53.0, 34.0, 90.0);
        transform.translate(100.3, -33.2, 95.3);
        quaternion_transform = transform.clone();

        quaternion = new Quaternion();
        transform.rotateYawPitchRoll(-50.3, 34.0, 12.03);
        quaternion.rotateYawPitchRoll(-50.3, 34.0, 12.03);
        quaternion_transform.rotate(quaternion);

        transform.toArray(a);
        quaternion_transform.toArray(b);
        for (int i = 0; i < 16; i++) {
            assertEquals(a[i], b[i], 0.0001);
        }

        // Verify that the 2D vector based rotateX/Y/Z functions for
        // Quaternion and Matrix4x4 do the same thing.
        transform = new Matrix4x4();
        quaternion = new Quaternion();
        transform.rotateX(2.0, 3.0);
        quaternion.rotateX(2.0, 3.0);
        transform.rotateY(-1.3, 2.5);
        quaternion.rotateY(-1.3, 2.5);
        transform.rotateZ(-0.6, 1.12);
        quaternion.rotateZ(-0.6, 1.12);
        quaternion_transform = quaternion.toMatrix4x4();
        transform.toArray(a);
        quaternion_transform.toArray(b);
        for (int i = 0; i < 16; i++) {
            assertEquals(a[i], b[i], 0.0001);
        }
    }

    @Test
    public void testQuaternionFromToRotation() {
        // Test fromToRotation with random vectors
        for (int i = 0; i < 10000; i++) {
            Vector u = randUnitVec();
            Vector v = randUnitVec();
            u.multiply(0.2 + 2.0 * Math.random());
            v.multiply(0.2 + 2.0 * Math.random());

            Quaternion a = Quaternion.fromToRotation(u, v);

            // Rotating the normalized vectors with this quaternion should work perfectly
            u.normalize();
            v.normalize();
            a.transformPoint(u);
            assertEquals(u.getX(), v.getX(), 0.0000001);
            assertEquals(u.getY(), v.getY(), 0.0000001);
            assertEquals(u.getZ(), v.getZ(), 0.0000001);
        }

        // Test forward to rotation being the same as fromToRotation
        for (int i = 0; i < 10000; i++) {
            Vector fwd_v = randUnitVec().multiply(0.2 * Math.random() * 2.0);
            Quaternion fwd_a = Quaternion.fromToRotation(new Vector(0.0, 0.0, 1.0), fwd_v);
            Quaternion fwd_b = Quaternion.fromLookDirection(fwd_v);
            assertEquals(fwd_a.getX(), fwd_b.getX(), 0.0000001);
            assertEquals(fwd_a.getY(), fwd_b.getY(), 0.0000001);
            assertEquals(fwd_a.getZ(), fwd_b.getZ(), 0.0000001);
            assertEquals(fwd_a.getW(), fwd_b.getW(), 0.0000001);
        }

        // Test a rotation with opposite vectors
        for (int i = 0; i < 10000; i++) {
            Vector u = randUnitVec();
            Vector v = u.clone().multiply(-1.0);

            Quaternion a = Quaternion.fromToRotation(u, v);

            // Rotating the vectors with this quaternion should work perfectly
            a.transformPoint(u);
            assertEquals(u.getX(), v.getX(), 0.0000001);
            assertEquals(u.getY(), v.getY(), 0.0000001);
            assertEquals(u.getZ(), v.getZ(), 0.0000001);
        }

        // Test forward rotation with its opposite vector
        {
            Vector fwd_v = new Vector(0.0, 0.0, -1.0);
            Quaternion q = Quaternion.fromLookDirection(fwd_v);
            Vector result = q.forwardVector();
            assertEquals(fwd_v.getX(), result.getX(), 1e-20);
            assertEquals(fwd_v.getY(), result.getY(), 1e-20);
            assertEquals(fwd_v.getZ(), result.getZ(), 1e-20);
        }

        // Test some special cases of opposite vectors
        List<Vector> specialOpposites = new ArrayList<Vector>();
        specialOpposites.add(new Vector(0.0, 0.0, 1.0));
        specialOpposites.add(new Vector(0.0, 0.0, -1.0));
        specialOpposites.add(new Vector(0.0, 1.0, 0.0));
        specialOpposites.add(new Vector(0.0, -1.0, 0.0));
        specialOpposites.add(new Vector(1.0, 0.0, 0.0));
        specialOpposites.add(new Vector(-1.0, 0.0, 0.0));
        for (Vector specialOpposite : specialOpposites) {
            Vector u = specialOpposite.clone();
            Vector v = u.clone().multiply(-1.0);

            Quaternion a = Quaternion.fromToRotation(u, v);

            // Rotating the vectors with this quaternion should work perfectly
            a.transformPoint(u);
            assertEquals(u.getX(), v.getX(), 0.0000001);
            assertEquals(u.getY(), v.getY(), 0.0000001);
            assertEquals(u.getZ(), v.getZ(), 0.0000001);
        }
    }

    @Test
    public void testQuaternionFromLookDirection() {
        // Try many random vectors, find a random orthogonal vector to it, and verify fromLookDirection works
        // The up and forward vectors of the produced quaternion should be the same as the input
        for (int i = 0; i < 10000; i++) {
            Vector dir = randUnitVec().multiply(0.2 + 2.0 * Math.random());
            Vector up = randOrtho(dir).multiply(0.2 + 2.0 * Math.random());

            Quaternion q = Quaternion.fromLookDirection(dir, up);

            dir.normalize();
            up.normalize();

            Vector result_dir = q.forwardVector();
            Vector result_up = q.upVector();
            assertEquals(dir.getX(), result_dir.getX(), 0.00001);
            assertEquals(dir.getY(), result_dir.getY(), 0.00001);
            assertEquals(dir.getZ(), result_dir.getZ(), 0.00001);
            assertEquals(up.getX(), result_up.getX(), 0.01);
            assertEquals(up.getY(), result_up.getY(), 0.01);
            assertEquals(up.getZ(), result_up.getZ(), 0.01);
        }

        // Verify that up-vectors that aren't perfectly orthogonal also work
        {
            Vector dir = new Vector(0.0, 0.0, 1.0);
            Vector up = new Vector(0.0, 3.0, -4.0);
            Quaternion q = Quaternion.fromLookDirection(dir, up);
            Vector result_dir = q.forwardVector();
            Vector result_up = q.upVector();
            assertEquals(dir.getX(), result_dir.getX(), 0.00001);
            assertEquals(dir.getY(), result_dir.getY(), 0.00001);
            assertEquals(dir.getZ(), result_dir.getZ(), 0.00001);
            assertEquals(0.0, result_up.getX(), 0.01);
            assertEquals(1.0, result_up.getY(), 0.01);
            assertEquals(0.0, result_up.getZ(), 0.01);
        }
    }

    @Test
    public void testQuaternionSlerp() {
        double a0 = 45.0;
        double a1 = 135.0;
        Quaternion q0 = Quaternion.fromAxisAngles(0.0, 1.0, 0.0, a0);
        Quaternion q1 = Quaternion.fromAxisAngles(0.0, 1.0, 0.0, a1);
        for (double t = 0.0; t <= 1.0; t += 0.001) {
            Quaternion q = Quaternion.slerp(q0, q1, t);
            Vector f = q.forwardVector();
            double angle_expected = (1.0-t) * a0 + t * a1;
            double angle_actual = Math.toDegrees(Math.atan2(f.getX(), f.getZ()));
            assertEquals(angle_expected, angle_actual, 0.0000000000001);
        }
    }

    // creates a random vector orthogonal to another vector
    private static Vector randOrtho(Vector dir) {
        // Get a valid vector perpendicular to dir
        Vector c;
        if (dir.getY() != 0.0 || dir.getZ() != 0.0) {
            c = new Vector(1.0, 0.0, 0.0);
        } else {
            c = new Vector(0.0, 1.0, 0.0);
        }
        c = dir.getCrossProduct(c);

        // Rotate randomly between 0 ... 360 degrees
        Quaternion q = Quaternion.fromAxisAngles(dir, Math.random() * 360.0);
        q.transformPoint(c);
        c.normalize();
        return c;
    }

    // random number between -1.0 and 1.0
    private static double randUnit() {
        return -2.0 * Math.random() + 1.0;
    }

    // random unit vector
    private static Vector randUnitVec() {
        return new Vector(randUnit(), randUnit(), randUnit()).normalize();
    }

    // checks that two quaternions have the same forward/up vector
    private static void testQuaternionsEqual(Quaternion expected, Quaternion actual) {
        testVectorsEqual(expected.forwardVector(), actual.forwardVector());
        testVectorsEqual(expected.upVector(), actual.upVector());
    }

    // checks that two vectors are equal
    private static void testVectorsEqual(Vector expected, Vector actual) {
        assertEquals(expected.getX(), actual.getX(), 1e-10);
        assertEquals(expected.getY(), actual.getY(), 1e-10);
        assertEquals(expected.getZ(), actual.getZ(), 1e-10);
    }
}
