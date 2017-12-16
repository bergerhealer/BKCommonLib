package com.bergerkiller.bukkit.common.math;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.map.util.MatrixMath;
import com.bergerkiller.bukkit.common.map.util.Quad;
import com.bergerkiller.bukkit.common.utils.MathUtil;

public class Matrix4x4 implements Cloneable {
    public double m00, m01, m02, m03;
    public double m10, m11, m12, m13;
    public double m20, m21, m22, m23;
    public double m30, m31, m32, m33;

    /**
     * Constructs a new 4x4 matrix, initialized as an identity matrix:
     * <pre>
     * 1 0 0 0
     * 0 1 0 0
     * 0 0 1 0
     * 0 0 0 1
     * </pre>
     */
    public Matrix4x4() {
        this.setIdentity();
    }

    /**
     * Constructs a new 4x4 matrix using the 16 values specified
     */
    public Matrix4x4(
            double m00, double m01, double m02, double m03,
            double m10, double m11, double m12, double m13,
            double m20, double m21, double m22, double m23,
            double m30, double m31, double m32, double m33)
    {
        this.set(
                m00, m01, m02, m03,
                m10, m11, m12, m13,
                m20, m21, m22, m23,
                m30, m31, m32, m33
        );
    }

    /**
     * Constructs a new 4x4 matrix, copying the 16 values from another matrix
     * 
     * @param matrix to set to
     */
    public Matrix4x4(Matrix4x4 matrix) {
        this.set(matrix);
    }

    /**
     * Sets all 16 values of this 4x4 matrix
     */
    public final void set(
            double m00, double m01, double m02, double m03,
            double m10, double m11, double m12, double m13,
            double m20, double m21, double m22, double m23,
            double m30, double m31, double m32, double m33)
    {
        this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
        this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
        this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
        this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33;
    }

    /**
     * Sets this matrix to all values of another 4x4 matrix
     * 
     * @param m matrix to set to
     */
    public final void set(Matrix4x4 m) {
        this.set(
                m.m00, m.m01, m.m02, m.m03,
                m.m10, m.m11, m.m12, m.m13,
                m.m20, m.m21, m.m22, m.m23,
                m.m30, m.m31, m.m32, m.m33
        );
    }

    /**
     * Sets this matrix to the identity matrix:<br>
     * <pre>
     * 1 0 0 0
     * 0 1 0 0
     * 0 0 1 0
     * 0 0 0 1
     * </pre>
     */
    public final void setIdentity()
    {
        this.set(
                1.0, 0.0, 0.0, 0.0,
                0.0, 1.0, 0.0, 0.0,
                0.0, 0.0, 1.0, 0.0,
                0.0, 0.0, 0.0, 1.0
        );
    }

    /**
     * Sets this matrix to all the values in an array
     * 
     * @param values to set to (length 16 or less)
     */
    public final void set(float[] values) {
        this.set(
                values[0], values[1], values[2], values[3],
                values[4], values[5], values[6], values[7],
                values[8], values[9], values[10], values[11],
                values[12], values[13], values[14], values[15]
        );
    }

    /**
     * Sets this matrix to all the values in an array
     * 
     * @param values array to read from (length 16 or more)
     */
    public final void set(double[] values) {
        this.set(
                values[0], values[1], values[2], values[3],
                values[4], values[5], values[6], values[7],
                values[8], values[9], values[10], values[11],
                values[12], values[13], values[14], values[15]
        );
    }

    /**
     * Gets all 16 values from this 4x4 matrix and writes them to an array
     * 
     * @param values array to write to (length 16 or more)
     */
    public final void toArray(float[] values) {
        values[0]  = (float) m00; values[1]  = (float) m01; values[2]  = (float) m02; values[3]  = (float) m03;
        values[4]  = (float) m10; values[5]  = (float) m11; values[6]  = (float) m12; values[7]  = (float) m13;
        values[8]  = (float) m20; values[9]  = (float) m21; values[10] = (float) m22; values[11] = (float) m23;
        values[12] = (float) m30; values[13] = (float) m31; values[14] = (float) m32; values[15] = (float) m33;
    }

    /**
     * Gets all 16 values from this 4x4 matrix and writes them to an array
     * 
     * @param values array to write to (length 16 or more)
     */
    public final void toArray(double[] values) {
        values[0]  = m00; values[1]  = m01; values[2]  = m02; values[3]  = m03;
        values[4]  = m10; values[5]  = m11; values[6]  = m12; values[7]  = m13;
        values[8]  = m20; values[9]  = m21; values[10] = m22; values[11] = m23;
        values[12] = m30; values[13] = m31; values[14] = m32; values[15] = m33;
    }

    /**
     * General invert routine.  Inverts m1 and places the result in "this".
     * Note that this routine handles both the "this" version and the
     * non-"this" version.
     *
     * Also note that since this routine is slow anyway, we won't worry
     * about allocating a little bit of garbage.
     */
    public final boolean invert() {
        // Copy source matrix to t1tmp
        double mInput[] = new double[16];
        int row_perm[] = new int[4];
        this.toArray(mInput);
        if (!MatrixMath.luDecomposition(mInput, row_perm)) {
            // Matrix has no inverse
            return false;
        }

        // Perform back substitution on the identity matrix
        double mOutput[] = new double[16];
        for(int i=0;i<16;i++) mOutput[i] = 0.0;
        mOutput[0] = 1.0; mOutput[5] = 1.0; mOutput[10] = 1.0; mOutput[15] = 1.0;
        MatrixMath.luBacksubstitution(mInput, row_perm, mOutput);
        this.set(mOutput);
        return true;
    }

    /**
     * Multiplies this matrix with a rotation transformation defined in a Quaternion
     * 
     * @param quat to rotate with
     */
    public final void rotate(Quaternion quat) {
        double x = quat.getX();
        double y = quat.getY();
        double z = quat.getZ();
        double w = quat.getW();

        double q00 = 2.0 * (-y*y + -z*z);
        double q01 = 2.0 * ( x*y + -z*w);
        double q02 = 2.0 * ( x*z +  y*w);
        double q10 = 2.0 * ( x*y +  z*w);
        double q11 = 2.0 * (-x*x + -z*z);
        double q12 = 2.0 * ( y*z + -x*w);
        double q20 = 2.0 * ( x*z + -y*w);
        double q21 = 2.0 * ( y*z +  x*w);
        double q22 = 2.0 * (-x*x + -y*y);

        double a00, a01, a02;
        double a10, a11, a12;
        double a20, a21, a22;
        double a30, a31, a32;

        a00 = this.m00*q00 + this.m01*q10 + this.m02*q20;
        a01 = this.m00*q01 + this.m01*q11 + this.m02*q21;
        a02 = this.m00*q02 + this.m01*q12 + this.m02*q22;

        a10 = this.m10*q00 + this.m11*q10 + this.m12*q20;
        a11 = this.m10*q01 + this.m11*q11 + this.m12*q21;
        a12 = this.m10*q02 + this.m11*q12 + this.m12*q22;

        a20 = this.m20*q00 + this.m21*q10 + this.m22*q20;
        a21 = this.m20*q01 + this.m21*q11 + this.m22*q21;
        a22 = this.m20*q02 + this.m21*q12 + this.m22*q22;

        a30 = this.m30*q00 + this.m31*q10 + this.m32*q20;
        a31 = this.m30*q01 + this.m31*q11 + this.m32*q21;
        a32 = this.m30*q02 + this.m31*q12 + this.m32*q22;

        this.m00 += a00; this.m01 += a01; this.m02 += a02;
        this.m10 += a10; this.m11 += a11; this.m12 += a12;
        this.m20 += a20; this.m21 += a21; this.m22 += a22;
        this.m30 += a30; this.m31 += a31; this.m32 += a32;
    }

    /**
     * Multiplies this matrix with a rotation transformation about the X-axis
     * 
     * @param angle the angle to rotate about the X axis in degrees
     */
    public final void rotateX(double angle) {
        if (angle != 0.0) {
            double angleRad = Math.toRadians(angle);
            rotateX_unsafe(Math.cos(angleRad), Math.sin(angleRad));
        }
    }

    /**
     * Multiplies this matrix with a rotation transformation about the X-axis.
     * Instead of a single angle, the y and z of the rotated vector can be specified.
     * 
     * @param y
     * @param z
     */
    public final void rotateX(double y, double z) {
        double f = MathUtil.getNormalizationFactor(y, z);
        rotateX_unsafe(y * f, z * f);
    }

    private final void rotateX_unsafe(double cos, double sin) {
        double m01, m02;
        double m11, m12;
        double m21, m22;
        double m31, m32;

        m01 = this.m01*cos    + this.m02*sin;
        m02 = this.m01*(-sin) + this.m02*cos;

        m11 = this.m11*cos    + this.m12*sin;
        m12 = this.m11*(-sin) + this.m12*cos;

        m21 = this.m21*cos    + this.m22*sin;
        m22 = this.m21*(-sin) + this.m22*cos;

        m31 = this.m31*cos    + this.m32*sin;
        m32 = this.m31*(-sin) + this.m32*cos;

        this.m01 = m01; this.m02 = m02;
        this.m11 = m11; this.m12 = m12;
        this.m21 = m21; this.m22 = m22;
        this.m31 = m31; this.m32 = m32;
    }
    
    /**
     * Multiplies this matrix with a rotation transformation about the Y-axis
     * 
     * @param angle the angle to rotate about the Y axis in degrees
     */
    public final void rotateY(double angle) {
        if (angle != 0.0) {
            double angleRad = Math.toRadians(angle);
            rotateY_unsafe(Math.cos(angleRad), Math.sin(angleRad));
        }
    }

    /**
     * Multiplies this matrix with a rotation transformation about the Y-axis.
     * Instead of a single angle, the x and z of the rotated vector can be specified.
     * 
     * @param x
     * @param z
     */
    public final void rotateY(double x, double z) {
        double f = MathUtil.getNormalizationFactor(x, z);
        rotateY_unsafe(x * f, z * f);
    }

    private final void rotateY_unsafe(double cos, double sin) {
        double m00, m02;
        double m10, m12;
        double m20, m22;
        double m30, m32;

        m00 = this.m00*cos + this.m02*(-sin);
        m02 = this.m00*sin + this.m02*cos;

        m10 = this.m10*cos + this.m12*(-sin);
        m12 = this.m10*sin + this.m12*cos;

        m20 = this.m20*cos + this.m22*(-sin);
        m22 = this.m20*sin + this.m22*cos;

        m30 = this.m30*cos + this.m32*(-sin);
        m32 = this.m30*sin + this.m32*cos;

        this.m00 = m00; this.m02 = m02;
        this.m10 = m10; this.m12 = m12;
        this.m20 = m20; this.m22 = m22;
        this.m30 = m30; this.m32 = m32;
    }
    
    /**
     * Multiplies this matrix with a rotation transformation about the Z-axis
     * 
     * @param angle the angle to rotate about the Z axis in degrees
     */
    public final void rotateZ(double angle) {
        if (angle != 0.0) {
            double angleRad = Math.toRadians(angle);
            rotateZ_unsafe(Math.cos(angleRad), Math.sin(angleRad));
        }
    }

    /**
     * Multiplies this matrix with a rotation transformation about the Z-axis.
     * Instead of a single angle, the x and y of the rotated vector can be specified.
     * 
     * @param x
     * @param y
     */
    public final void rotateZ(double x, double y) {
        double f = MathUtil.getNormalizationFactor(x, y);
        rotateZ_unsafe(x * f, y * f);
    }

    private final void rotateZ_unsafe(double cos, double sin) {
        double m00, m01;
        double m10, m11;
        double m20, m21;
        double m30, m31;

        m00 = this.m00*cos    + this.m01*sin;
        m01 = this.m00*(-sin) + this.m01*cos;

        m10 = this.m10*cos    + this.m11*sin;
        m11 = this.m10*(-sin) + this.m11*cos;

        m20 = this.m20*cos    + this.m21*sin;
        m21 = this.m20*(-sin) + this.m21*cos;

        m30 = this.m30*cos    + this.m31*sin;
        m31 = this.m30*(-sin) + this.m31*cos;

        this.m00 = m00; this.m01 = m01;
        this.m10 = m10; this.m11 = m11;
        this.m20 = m20; this.m21 = m21;
        this.m30 = m30; this.m31 = m31;
    }
    
    /**
     * Multiplies this matrix with a rotation transformation in yaw/pitch/roll, based on the Minecraft
     * coordinate system. This will differ slightly from the standard rotateX/Y/Z functions.
     * 
     * @param rotation (x=pitch, y=yaw, z=roll)
     */
    public final void rotateYawPitchRoll(Vector3 rotation) {
        rotateYawPitchRoll(rotation.x, rotation.y, rotation.z);
    }

    /**
     * Multiplies this matrix with a rotation transformation in yaw/pitch/roll, based on the Minecraft
     * coordinate system. This will differ slightly from the standard rotateX/Y/Z functions.
     * 
     * @param rotation (x=pitch, y=yaw, z=roll)
     */
    public final void rotateYawPitchRoll(Vector rotation) {
        rotateYawPitchRoll(rotation.getX(), rotation.getY(), rotation.getZ());
    }

    /**
     * Multiplies this matrix with a rotation transformation in yaw/pitch/roll, based on the Minecraft
     * coordinate system. This will differ slightly from the standard rotateX/Y/Z functions.
     * 
     * @param pitch rotation (X)
     * @param yaw rotation (Y)
     * @param roll rotation (Z)
     */
    public final void rotateYawPitchRoll(double pitch, double yaw, double roll) {
        this.rotateY(-yaw);
        this.rotateX(pitch);
        this.rotateZ(roll);
    }

    /**
     * Multiplies this matrix with a rotation transformation in yaw/pitch/roll, based on the Minecraft
     * coordinate system. This will differ slightly from the standard rotateX/Y/Z functions.
     * 
     * @param pitch rotation (X)
     * @param yaw rotation (Y)
     * @param roll rotation (Z)
     */
    public final void rotateYawPitchRoll(float pitch, float yaw, float roll) {
        this.rotateY(-yaw);
        this.rotateX(pitch);
        this.rotateZ(roll);
    }

    /**
     * Deduces the yaw/pitch/roll values in degrees that this matrix transforms objects with
     * 
     * @return axis rotations: {x=pitch, y=yaw, z=roll}
     */
    public final Vector getYawPitchRoll() {
        float yaw = -MathUtil.atan2(m02, m22);
        float pitch = MathUtil.atan2(-m12, Math.sqrt(m11 * m11 + m10 * m10));
        float roll = MathUtil.atan2(m10, m11);
        return new Vector(pitch, yaw, roll);
    }

    /**
     * Multiplies this matrix with a translation transformation
     * 
     * @param translation
     */
    public final void translate(Vector3 translation) {
        this.translate(translation.x, translation.y, translation.z);
    }

    /**
     * Multiplies this matrix with a translation transformation
     * 
     * @param dx translation
     * @param dy translation
     * @param dz translation
     */
    public final void translate(double dx, double dy, double dz) {
        this.m03 += this.m00*dx + this.m01*dy + this.m02*dz;
        this.m13 += this.m10*dx + this.m11*dy + this.m12*dz;
        this.m23 += this.m20*dx + this.m21*dy + this.m22*dz;
        this.m33 += this.m30*dx + this.m31*dy + this.m32*dz;
    }

    /**
     * Multiplies this matrix with a translation transformation.
     * 
     * @param translation
     */
    public final void translate(Vector translation) {
        this.translate(translation.getX(), translation.getY(), translation.getZ());
    }

    /**
     * Multiplies this matrix with a scale transformation
     * 
     * @param scale
     */
    public final void scale(Vector3 scale) {
        this.scale(scale.x, scale.y, scale.z);
    }

    /**
     * Multiplies this matrix with a scale transformation
     * 
     * @param sx scale
     * @param sy scale
     * @param sz scale
     */
    public final void scale(double sx, double sy, double sz) {
        this.m00 *= sx; this.m10 *= sx; this.m20 *= sx; this.m30 *= sx;
        this.m01 *= sy; this.m11 *= sy; this.m21 *= sy; this.m31 *= sy;
        this.m02 *= sz; this.m12 *= sz; this.m22 *= sz; this.m32 *= sz;
    }

    /**
     * Multiplies this matrix with a scale transformation
     * 
     * @param scale
     */
    public final void scale(double scale) {
        this.scale(scale, scale, scale);
    }

    /**
     * Translates and rotates this Matrix with the position information of a Bukkit Location
     * 
     * @param location to translate and rotate by
     */
    public final void translateRotate(Location location) {
        this.translateRotate(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Translates and rotates this Matrix with position and rotation information
     * 
     * @param x position
     * @param y position
     * @param z position
     * @param yaw rotation
     * @param pitch rotation
     */
    public final void translateRotate(double x, double y, double z, float yaw, float pitch) {
        this.translate(x, y, z);
        this.rotateYawPitchRoll(pitch, yaw, 0.0f);
    }

    /**
     * Translates and rotates this Matrix with position and rotation information
     * 
     * @param x position
     * @param y position
     * @param z position
     * @param yaw rotation
     * @param pitch rotation
     * @param roll rotation
     */
    public final void translateRotate(double x, double y, double z, float yaw, float pitch, float roll) {
        this.translate(x, y, z);
        this.rotateYawPitchRoll(yaw, pitch, roll);
    }

    /**
     * Multiplies this matrix with another, storing the result in this matrix
     * 
     * @param m1 the other matrix to multiply with
     */
    public final void multiply(Matrix4x4 m1)
    {
        double m00, m01, m02, m03;
        double m10, m11, m12, m13;
        double m20, m21, m22, m23;
        double m30, m31, m32, m33;

        m00 = this.m00*m1.m00 + this.m01*m1.m10 +
                this.m02*m1.m20 + this.m03*m1.m30;
        m01 = this.m00*m1.m01 + this.m01*m1.m11 +
                this.m02*m1.m21 + this.m03*m1.m31;
        m02 = this.m00*m1.m02 + this.m01*m1.m12 +
                this.m02*m1.m22 + this.m03*m1.m32;
        m03 = this.m00*m1.m03 + this.m01*m1.m13 +
                this.m02*m1.m23 + this.m03*m1.m33;

        m10 = this.m10*m1.m00 + this.m11*m1.m10 +
                this.m12*m1.m20 + this.m13*m1.m30;
        m11 = this.m10*m1.m01 + this.m11*m1.m11 +
                this.m12*m1.m21 + this.m13*m1.m31;
        m12 = this.m10*m1.m02 + this.m11*m1.m12 +
                this.m12*m1.m22 + this.m13*m1.m32;
        m13 = this.m10*m1.m03 + this.m11*m1.m13 +
                this.m12*m1.m23 + this.m13*m1.m33;

        m20 = this.m20*m1.m00 + this.m21*m1.m10 +
                this.m22*m1.m20 + this.m23*m1.m30;
        m21 = this.m20*m1.m01 + this.m21*m1.m11 +
                this.m22*m1.m21 + this.m23*m1.m31;
        m22 = this.m20*m1.m02 + this.m21*m1.m12 +
                this.m22*m1.m22 + this.m23*m1.m32;
        m23 = this.m20*m1.m03 + this.m21*m1.m13 +
                this.m22*m1.m23 + this.m23*m1.m33;

        m30 = this.m30*m1.m00 + this.m31*m1.m10 +
                this.m32*m1.m20 + this.m33*m1.m30;
        m31 = this.m30*m1.m01 + this.m31*m1.m11 +
                this.m32*m1.m21 + this.m33*m1.m31;
        m32 = this.m30*m1.m02 + this.m31*m1.m12 +
                this.m32*m1.m22 + this.m33*m1.m32;
        m33 = this.m30*m1.m03 + this.m31*m1.m13 +
                this.m32*m1.m23 + this.m33*m1.m33;

        this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
        this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
        this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
        this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33;
    }

    /**
     * Transforms a 3D point vector using this transformation matrix.
     * The result is written to the input point.
     * 
     * @param point to transform
     */
    public final void transformPoint(Vector point) {
        Vector3 tmp = new Vector3(point);
        this.transformPoint(tmp);
        point.setX(tmp.x);
        point.setY(tmp.y);
        point.setZ(tmp.z);
    }

    /**
     * Transforms a 3D point vector using this transformation matrix.
     * The result is written to the input point.
     * 
     * @param point to transform
     */
    public final void transformPoint(Vector3 point) {
        double x,y;
        x = m00*point.x + m01*point.y + m02*point.z + m03;
        y = m10*point.x + m11*point.y + m12*point.z + m13;
        point.z = m20*point.x + m21*point.y + m22*point.z + m23;
        point.x = x;
        point.y = y;
    }

    /**
     * Transforms a 4D point vector using this transformation matrix.
     * The result is written to the input point.
     * 
     * @param point to transform
     */
    public final void transformPoint(Vector4 point) {
        double x = m00 * point.x + m01 * point.y + m02 * point.z + m03 * point.w;
        double y = m10 * point.x + m11 * point.y + m12 * point.z + m13 * point.w;
        double z = m20 * point.x + m21 * point.y + m22 * point.z + m23 * point.w;
        double w = m30 * point.x + m31 * point.y + m32 * point.z + m33 * point.w;
        point.x = x;
        point.y = y;
        point.z = z;
        point.w = w;
    }

    /**
     * Transforms all four points of a quad using this transformation matrix.
     * 
     * @param quad to transform
     */
    public final void transformQuad(Quad quad) {
        transformPoint(quad.p0);
        transformPoint(quad.p1);
        transformPoint(quad.p2);
        transformPoint(quad.p3);
    }

    /**
     * Obtains the absolute position vector of this matrix, equivalent to performing
     * transformPoint with a zero vector.
     * 
     * @return position vector
     */
    public Vector3 toVector3() {
        Vector3 result = new Vector3(0.0, 0.0, 0.0);
        this.transformPoint(result);
        return result;
    }

    /**
     * Obtains the absolute position vector of this matrix, equivalent to performing
     * transformPoint with a zero vector.
     * 
     * @return position vector
     */
    public Vector toVector() {
        Vector result = new Vector(0.0, 0.0, 0.0);
        this.transformPoint(result);
        return result;
    }

    @Override
    public Matrix4x4 clone() {
        return new Matrix4x4(this);
    }

    @Override
    public String toString() {
        return "{" +
            m00 + ", " + m01 + ", " + m02 + ", " + m03 + "\n " +
            m10 + ", " + m11 + ", " + m12 + ", " + m13 + "\n " +
            m20 + ", " + m21 + ", " + m22 + ", " + m23 + "\n " +
            m30 + ", " + m31 + ", " + m32 + ", " + m33 + "}";
    }

    // From https://math.stackexchange.com/questions/296794
    public static Matrix4x4 computeProjectionMatrix(Vector3 p[])
    {
        Matrix4x4 m = new Matrix4x4(
                p[0].x, p[1].x, p[2].x, 0.0f,
                p[0].y, p[1].y, p[2].y, 1.0f,
                p[0].z, p[1].z, p[2].z, 0.0f,
                1, 1, 1, 0);

        //TODO: For some reason we need to add a very small value to p[3].y to avoid glitching out
        // Any reason why? Should this value be calculated from somewhere? View clipping plane?
        // Or maybe the matrix inversion simply cannot handle an y value without it.
        Vector4 p3 = new Vector4(p[3].x, p[3].y + 0.001f, p[3].z, 1.0f);
        Matrix4x4 mInv = new Matrix4x4(m);
        if (!mInv.invert()) {
            return null;
        }
        mInv.transformPoint(p3);

        m.m00 *= p3.x;
        m.m01 *= p3.y;
        m.m02 *= p3.z;
        m.m03 *= p3.w;

        m.m10 *= p3.x;
        m.m11 *= p3.y;
        m.m12 *= p3.z;
        m.m13 *= p3.w;

        m.m20 *= p3.x;
        m.m21 *= p3.y;
        m.m22 *= p3.z;
        m.m23 *= p3.w;

        m.m30 *= p3.x;
        m.m31 *= p3.y;
        m.m32 *= p3.z;
        m.m33 *= p3.w;

        return m;
    }
}
