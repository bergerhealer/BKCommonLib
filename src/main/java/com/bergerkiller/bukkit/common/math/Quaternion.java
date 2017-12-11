package com.bergerkiller.bukkit.common.math;

import org.bukkit.util.Vector;

/**
 * A quaternion for performing rotations in 3D space.
 * The quaternion is automatically normalized.
 */
public class Quaternion implements Cloneable {
    private double x, y, z, w;

    public Quaternion() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.w = 1.0;
    }

    public Quaternion(Quaternion quat) {
        this.x = quat.x;
        this.y = quat.y;
        this.z = quat.z;
        this.w = quat.w;
    }

    public Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.normalize();
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double getW() {
        return this.w;
    }

    /**
     * Transforms a point, applying the rotation of this quaternion with 0,0,0 as origin.
     * 
     * @param point to rotate using this quaternion
     */
    public void transformPoint(Vector point) {
        //TODO: Optimize so it doesn't have to use a 4x4 matrix for this!
        Matrix4x4 m = new Matrix4x4();
        m.rotate(this);
        m.translate(point);
        Vector result = m.toVector();
        point.setX(result.getX());
        point.setY(result.getY());
        point.setZ(result.getZ());
    }

    public void multiply(Quaternion quat) {
        double x = this.w * quat.x + this.x * quat.w + this.y * quat.z - this.z * quat.y;
        double y = this.w * quat.y + this.y * quat.w + this.z * quat.x - this.x * quat.z;
        double z = this.w * quat.z + this.z * quat.w + this.x * quat.y - this.y * quat.x;
        double w = this.w * quat.w - this.x * quat.x - this.y * quat.y - this.z * quat.z;
        this.x = x; this.y = y; this.z = z; this.w = w;
        this.normalize();
    }

    /**
     * Multiplies this quaternion with a rotation transformation in yaw/pitch/roll, based on the Minecraft
     * coordinate system. This will differ slightly from the standard rotateX/Y/Z functions.
     * 
     * @param rotation (x=pitch, y=yaw, z=roll)
     */
    public final void rotateYawPitchRoll(Vector3 rotation) {
        rotateYawPitchRoll(rotation.y, rotation.x, rotation.z);
    }

    /**
     * Multiplies this quaternion with a rotation transformation in yaw/pitch/roll, based on the Minecraft
     * coordinate system. This will differ slightly from the standard rotateX/Y/Z functions.
     * 
     * @param rotation (x=pitch, y=yaw, z=roll)
     */
    public final void rotateYawPitchRoll(Vector rotation) {
        rotateYawPitchRoll(rotation.getY(), rotation.getX(), rotation.getZ());
    }

    /**
     * Multiplies this quaternion with a rotation transformation in yaw/pitch/roll, based on the Minecraft
     * coordinate system. This will differ slightly from the standard rotateX/Y/Z functions.
     * 
     * @param yaw rotation
     * @param pitch rotation
     * @param roll rotation
     */
    public final void rotateYawPitchRoll(double yaw, double pitch, double roll) {
        this.rotateY(-yaw);
        this.rotateX(pitch);
        this.rotateZ(roll);
    }

    /**
     * Deduces the yaw/pitch/roll values in degrees that this quaternion transforms objects with
     * 
     * @return axis rotations: {x=pitch, y=yaw, z=roll}
     */
    public final Vector getYawPitchRoll() {
        final double roll;
        final double pitch;
        double yaw;
        final double test = w * x - y * z;
        if (Math.abs(test) < 0.4999) {
            roll = Math.atan2(2 * (w * z + x * y), 1 - 2 * (x * x + z * z));
            pitch = Math.asin(2 * test);
            yaw = Math.atan2(2 * (w * y + z * x), 1 - 2 * (x * x + y * y));
        } else {
            final int sign = (test < 0) ? -1 : 1;
            roll = 0;
            pitch = sign * Math.PI / 2;
            yaw = -sign * 2 * Math.atan2(z, w);
        }
        if (yaw > Math.PI) {
            yaw -= 2.0 * Math.PI;
        } else if (yaw < -Math.PI) {
            yaw += 2.0 * Math.PI;
        }
        return new Vector(Math.toDegrees(pitch), Math.toDegrees(-yaw), Math.toDegrees(roll));        
    }

    public void rotateX(double angleDegrees) {
        if (angleDegrees != 0.0) {
            double r = 0.5 * Math.toRadians(angleDegrees);
            double s = Math.sin(r);
            double c = Math.cos(r);
            double x = this.x * c + this.w * s;
            double y = this.y * c + this.z * s;
            double z = this.z * c - this.y * s;
            double w = this.w * c - this.x * s;
            this.x = x; this.y = y; this.z = z; this.w = w;
            this.normalize();
        }
    }

    public void rotateY(double angleDegrees) {
        if (angleDegrees != 0.0) {
            double r = 0.5 * Math.toRadians(angleDegrees);
            double s = Math.sin(r);
            double c = Math.cos(r);
            double x = this.x * c - this.z * s;
            double y = this.y * c + this.w * s;
            double z = this.z * c + this.x * s;
            double w = this.w * c - this.y * s;
            this.x = x; this.y = y; this.z = z; this.w = w;
            this.normalize();
        }
    }

    public void rotateZ(double angleDegrees) {
        if (angleDegrees != 0.0) {
            double r = 0.5 * Math.toRadians(angleDegrees);
            double s = Math.sin(r);
            double c = Math.cos(r);
            double x = this.x * c + this.y * s;
            double y = this.y * c - this.x * s;
            double z = this.z * c + this.w * s;
            double w = this.w * c - this.z * s;
            this.x = x; this.y = y; this.z = z; this.w = w;
            this.normalize();
        }
    }

    /**
     * Converts the rotation transformations defined in this quaternion to a 
     * 4x4 transformation matrix. This is as if the unit matrix was multiplied
     * with this quaternion.
     * 
     * @return 4x4 transformation matrix.
     */
    public Matrix4x4 toMatrix4x4() {
        return new Matrix4x4(
                1.0 - 2.0*y*y - 2.0*z*z, 2.0*x*y - 2.0*z*w, 2.0*x*z + 2.0*y*w, 0.0,
                2.0*x*y + 2.0*z*w, 1.0 - 2.0*x*x - 2.0*z*z, 2.0*y*z - 2.0*x*w, 0.0,
                2.0*x*z - 2.0*y*w, 2.0*y*z + 2.0*x*w, 1.0 - 2.0*x*x - 2.0*y*y, 0.0,
                0.0, 0.0, 0.0, 1.0
        );
    }

    public void invert() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    // https://stackoverflow.com/a/12934750
    private void normalize() {
        double length_sq = (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
        double f;
        if (Math.abs(1.0 - length_sq) < 2.107342e-08) {
            f = (2.0 / (1.0 + length_sq));
        } else {
            f = 1.0 / Math.sqrt(length_sq);
        }
        this.x *= f; this.y *= f; this.y *= f; this.w *= f;
    }

    @Override
    public Quaternion clone() {
        return new Quaternion(this);
    }

    @Override
    public String toString() {
        return "{" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "}";
    }

    /**
     * Creates a quaternion for a rotation around an axis
     * 
     * @param axis
     * @param angleDegrees
     * @return quaternion for the rotation around the axis
     */
    public static Quaternion fromAxisAngles(Vector3 axis, double angleDegrees) {
        return fromAxisAngles(axis.x, axis.y, axis.z, angleDegrees);
    }

    /**
     * Creates a quaternion for a rotation around an axis
     * 
     * @param axis
     * @param angleDegrees
     * @return quaternion for the rotation around the axis
     */
    public static Quaternion fromAxisAngles(Vector axis, double angleDegrees) {
        return fromAxisAngles(axis.getX(), axis.getY(), axis.getZ(), angleDegrees);
    }

    /**
     * Creates a quaternion for a rotation around an axis
     * 
     * @param axisX
     * @param axisY
     * @param axisZ
     * @param angleDegrees
     * @return quaternion for the rotation around the axis
     */
    public static Quaternion fromAxisAngles(double axisX, double axisY, double axisZ, double angleDegrees) {
        double r = 0.5 * Math.toRadians(angleDegrees);
        double f = Math.sin(r);
        return new Quaternion(f*axisX, f*axisY, f*axisZ, Math.cos(r));
    }
}
