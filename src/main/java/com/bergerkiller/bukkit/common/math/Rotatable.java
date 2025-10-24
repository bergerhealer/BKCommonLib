package com.bergerkiller.bukkit.common.math;

import org.bukkit.util.Vector;

/**
 * An object that can be transformed with a rotation using Quaternions
 * or angle degrees
 */
public interface Rotatable {
    /**
     * Transforms by a Quaternion rotation
     *
     * @param qx Quaternion x
     * @param qy Quaternion y
     * @param qz Quaternion z
     * @param qw Quaternion w
     */
    void rotateByQuaternion(double qx, double qy, double qz, double qw);

    /**
     * Transforms by a Quaternion rotation
     *
     * @param quat to rotate with
     * @see #rotateByQuaternion(double, double, double, double)
     */
    default void rotate(Quaternion quat) {
        rotateByQuaternion(quat.getX(), quat.getY(), quat.getZ(), quat.getW());
    }

    /**
     * Transforms with a rotation transformation about the X-axis
     *
     * @param angleDegrees the angle to rotate about the X axis in degrees
     */
    default void rotateX(double angleDegrees) {
        if (angleDegrees != 0.0) {
            double r = 0.5 * Math.toRadians(angleDegrees);
            double fy = Math.cos(r);
            double fz = Math.sin(r);
            rotateByQuaternion(fz, 0.0, 0.0, fy);
        }
    }

    /**
     * Transforms with a rotation transformation 180 degrees around the X-axis
     */
    default void rotateXFlip() {
        rotateByQuaternion(1.0, 0.0, 0.0, 0.0);
    }

    /**
     * Transforms with a rotation transformation about the Y-axis
     *
     * @param angleDegrees the angle to rotate about the Y axis in degrees
     */
    default void rotateY(double angleDegrees) {
        if (angleDegrees != 0.0) {
            double r = 0.5 * Math.toRadians(angleDegrees);
            double fx = Math.cos(r);
            double fz = Math.sin(r);
            rotateByQuaternion(0.0, fz, 0.0, fx);
        }
    }

    /**
     * Transforms with a rotation transformation 180 degrees around the Y-axis
     */
    default void rotateYFlip() {
        rotateByQuaternion(0.0, 1.0, 0.0, 0.0);
    }

    /**
     * Transforms with a rotation transformation about the Z-axis
     *
     * @param angleDegrees the angle to rotate about the Z axis in degrees
     */
    default void rotateZ(double angleDegrees) {
        if (angleDegrees != 0.0) {
            double r = 0.5 * Math.toRadians(angleDegrees);
            double fx = Math.cos(r);
            double fy = Math.sin(r);
            rotateByQuaternion(0.0, 0.0, fy, fx);
        }
    }

    /**
     * Transforms with a rotation transformation 180 degrees around the Z-axis
     */
    default void rotateZFlip() {
        rotateByQuaternion(0.0, 0.0, 1.0, 0.0);
    }

    /**
     * Transforms with a rotation transformation in yaw/pitch/roll, based on the Minecraft
     * coordinate system. This will differ slightly from the standard rotateX/Y/Z functions.
     *
     * @param rotation (x=pitch, y=yaw, z=roll)
     */
    default void rotateYawPitchRoll(Vector3 rotation) {
        rotateYawPitchRoll(rotation.x, rotation.y, rotation.z);
    }

    /**
     * Transforms with a rotation transformation in yaw/pitch/roll, based on the Minecraft
     * coordinate system. This will differ slightly from the standard rotateX/Y/Z functions.
     *
     * @param rotation (x=pitch, y=yaw, z=roll)
     */
    default void rotateYawPitchRoll(Vector rotation) {
        rotateYawPitchRoll(rotation.getX(), rotation.getY(), rotation.getZ());
    }

    /**
     * Transforms with a rotation transformation in yaw/pitch/roll, based on the Minecraft
     * coordinate system. This will differ slightly from the standard rotateX/Y/Z functions.
     *
     * @param pitch rotation (X)
     * @param yaw rotation (Y)
     * @param roll rotation (Z)
     */
    default void rotateYawPitchRoll(double pitch, double yaw, double roll) {
        this.rotateY(-yaw);
        this.rotateX(pitch);
        this.rotateZ(roll);
    }

    /**
     * Transforms with a rotation transformation in yaw/pitch/roll, based on the Minecraft
     * coordinate system. This will differ slightly from the standard rotateX/Y/Z functions.
     *
     * @param pitch rotation (X)
     * @param yaw rotation (Y)
     * @param roll rotation (Z)
     */
    default void rotateYawPitchRoll(float pitch, float yaw, float roll) {
        this.rotateY(-yaw);
        this.rotateX(pitch);
        this.rotateZ(roll);
    }

    /**
     * Transforms with a rotation around an axis
     *
     * @param axis Axis Vector
     * @param angleDegrees Angle in degrees to rotate
     */
    default void rotateAxis(Vector axis, double angleDegrees) {
        rotateAxis(axis.getX(), axis.getY(), axis.getZ(), angleDegrees);
    }

    /**
     * Transforms with a rotation around an axis
     *
     * @param axisX Axis X-vector coordinate
     * @param axisY Axis Y-vector coordinate
     * @param axisZ Axis Z-vector coordinate
     * @param angleDegrees Angle in degrees to rotate
     */
    default void rotateAxis(double axisX, double axisY, double axisZ, double angleDegrees) {
        this.rotate(Quaternion.fromAxisAngles(axisX, axisY, axisZ, angleDegrees));
    }
}
