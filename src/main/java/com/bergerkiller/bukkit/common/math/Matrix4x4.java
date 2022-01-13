package com.bergerkiller.bukkit.common.math;

import org.bukkit.Location;
import org.bukkit.World;
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
     * Gets the rotation transformation performed as a Quaternion
     * 
     * @return rotation quaternion
     */
    public final Quaternion getRotation() {
        double tr = m00 + m11 + m22;
        if (tr > 0) {
            return new Quaternion(m21-m12, m02-m20, m10-m01, 1.0 + tr);
        } else if ((m00 > m11) & (m00 > m22)) {
            return new Quaternion(1.0+m00-m11-m22, m01+m10, m02+m20, m21-m12);
        } else if (m11 > m22) {
            return new Quaternion(m01+m10, 1.0+m11-m00-m22, m12+m21, m02-m20);
        } else {
            return new Quaternion(m02+m20, m12+m21, 1.0+m22-m00-m11, m10-m01);
        }
    }

    /**
     * Deduces the pitch component (x) of {@link #getYawPitchRoll()}
     * 
     * @return pitch
     */
    public final double getRotationPitch() {
        /* == This portion is repeated and copied from getRotation() == */
        double x, y, z, w, tr = m00 + m11 + m22;
        if (tr > 0) {
            x = m21-m12; y = m02-m20; z = m10-m01; w = 1.0 + tr;
        } else if ((m00 > m11) && (m00 > m22)) {
            x = 1.0+m00-m11-m22; y = m01+m10; z = m02+m20; w = m21-m12;
        } else if (m11 > m22) {
            x = m01+m10; y = 1.0+m11-m00-m22; z = m12+m21; w = m02-m20;
        } else {
            x = m02+m20; y = m12+m21; z = 1.0+m22-m00-m11; w = m10-m01;
        }
        double f = MathUtil.getNormalizationFactor(x, y, z, w);
        x *= f; y *= f; z *= f; w *= f;
        /* ============================================================ */

        return Quaternion.getPitch(x, y, z, w);
    }

    /**
     * Deduces the yaw component (y) of {@link #getYawPitchRoll()}
     * 
     * @return yaw
     */
    public final double getRotationYaw() {
        /* == This portion is repeated and copied from getRotation() == */
        double x, y, z, w, tr = m00 + m11 + m22;
        if (tr > 0) {
            x = m21-m12; y = m02-m20; z = m10-m01; w = 1.0 + tr;
        } else if ((m00 > m11) && (m00 > m22)) {
            x = 1.0+m00-m11-m22; y = m01+m10; z = m02+m20; w = m21-m12;
        } else if (m11 > m22) {
            x = m01+m10; y = 1.0+m11-m00-m22; z = m12+m21; w = m02-m20;
        } else {
            x = m02+m20; y = m12+m21; z = 1.0+m22-m00-m11; w = m10-m01;
        }
        double f = MathUtil.getNormalizationFactor(x, y, z, w);
        x *= f; y *= f; z *= f; w *= f;
        /* ============================================================ */

        return Quaternion.getYaw(x, y, z, w);
    }

    /**
     * Deduces the roll component (z) of {@link #getYawPitchRoll()}
     * 
     * @return roll
     */
    public final double getRotationRoll() {
        /* == This portion is repeated and copied from getRotation() == */
        double x, y, z, w, tr = m00 + m11 + m22;
        if (tr > 0) {
            x = m21-m12; y = m02-m20; z = m10-m01; w = 1.0 + tr;
        } else if ((m00 > m11) && (m00 > m22)) {
            x = 1.0+m00-m11-m22; y = m01+m10; z = m02+m20; w = m21-m12;
        } else if (m11 > m22) {
            x = m01+m10; y = 1.0+m11-m00-m22; z = m12+m21; w = m02-m20;
        } else {
            x = m02+m20; y = m12+m21; z = 1.0+m22-m00-m11; w = m10-m01;
        }
        double f = MathUtil.getNormalizationFactor(x, y, z, w);
        x *= f; y *= f; z *= f; w *= f;
        /* ============================================================ */

        return Quaternion.getRoll(x, y, z, w);
    }

    /**
     * Deduces the yaw/pitch/roll values in degrees that this matrix transforms objects with
     * 
     * @return axis rotations: {x=pitch, y=yaw, z=roll}
     */
    public final Vector getYawPitchRoll() {
        /* == This portion is repeated and copied from getRotation() == */
        double x, y, z, w, tr = m00 + m11 + m22;
        if (tr > 0) {
            x = m21-m12; y = m02-m20; z = m10-m01; w = 1.0 + tr;
        } else if ((m00 > m11) && (m00 > m22)) {
            x = 1.0+m00-m11-m22; y = m01+m10; z = m02+m20; w = m21-m12;
        } else if (m11 > m22) {
            x = m01+m10; y = 1.0+m11-m00-m22; z = m12+m21; w = m02-m20;
        } else {
            x = m02+m20; y = m12+m21; z = 1.0+m22-m00-m11; w = m10-m01;
        }
        double f = MathUtil.getNormalizationFactor(x, y, z, w);
        x *= f; y *= f; z *= f; w *= f;
        /* ============================================================ */

        return Quaternion.getYawPitchRoll(x, y, z, w);
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
     * Multiplies this matrix with a translation transformation.
     * 
     * @param translation
     */
    public final void translate(Vector translation) {
        this.translate(translation.getX(), translation.getY(), translation.getZ());
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
     * Multiplies a {@link #translation(Vector)} matrix with this matrix,
     * writing the result to this matrix. This results in a world-relative
     * translation. Rotation/scale/shear information of this matrix have no
     * effect on the translation performed.
     *
     * @param translation
     */
    public final void worldTranslate(Vector3 translation) {
        worldTranslate(translation.x, translation.y, translation.z);
    }

    /**
     * Multiplies a {@link #translation(Vector)} matrix with this matrix,
     * writing the result to this matrix. This results in a world-relative
     * translation. Rotation/scale/shear information of this matrix have no
     * effect on the translation performed.
     *
     * @param translation
     */
    public final void worldTranslate(Vector translation) {
        worldTranslate(translation.getX(), translation.getY(), translation.getZ());
    }

    /**
     * Multiplies a {@link #translation(Vector)} matrix with this matrix,
     * writing the result to this matrix. This results in a world-relative
     * translation. Rotation/scale/shear information of this matrix have no
     * effect on the translation performed.
     *
     * @param dx translation
     * @param dy translation
     * @param dz translation
     */
    public final void worldTranslate(double dx, double dy, double dz) {
        // Equivalent to:
        // Matrix4x4.multiply(Matrix4x4.translation(dx, dy, dz), this, this);

        this.m00 += dx * this.m30;
        this.m01 += dx * this.m31;
        this.m02 += dx * this.m32;
        this.m03 += dx * this.m33;

        this.m10 += dy * this.m30;
        this.m11 += dy * this.m31;
        this.m12 += dy * this.m32;
        this.m13 += dy * this.m33;

        this.m20 += dz * this.m30;
        this.m21 += dz * this.m31;
        this.m22 += dz * this.m32;
        this.m23 += dz * this.m33;
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
        this.translateRotate(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }

    /**
     * Translates and rotates this Matrix with position and rotation information
     * 
     * @param x position
     * @param y position
     * @param z position
     * @param pitch rotation (X)
     * @param yaw rotation (Y)
     */
    public final void translateRotate(double x, double y, double z, float pitch, float yaw) {
        this.translate(x, y, z);
        this.rotateYawPitchRoll(pitch, yaw, 0.0f);
    }

    /**
     * Translates and rotates this Matrix with position and rotation information
     * 
     * @param x position
     * @param y position
     * @param z position
     * @param pitch rotation (X)
     * @param yaw rotation (Y)
     * @param roll rotation (Z)
     */
    public final void translateRotate(double x, double y, double z, float pitch, float yaw, float roll) {
        this.translate(x, y, z);
        this.rotateYawPitchRoll(pitch, yaw, roll);
    }

    /**
     * Multiplies this matrix with another, storing the result in this matrix
     * 
     * @param mRight the right-hand side matrix to multiply with
     */
    public final void multiply(Matrix4x4 mRight) {
        multiply(this, mRight, this);
    }

    /**
     * Stores the result of a matrix multiplication in this matrix.
     * One of the input matrices is allowed to be the same instance as this matrix.
     * 
     * @param mLeft Left side of the matrix multiplication
     * @param mRight Right side of the matrix multiplication
     */
    public final void storeMultiply(Matrix4x4 mLeft, Matrix4x4 mRight) {
        double m00, m01, m02, m03;
        double m10, m11, m12, m13;
        double m20, m21, m22, m23;
        double m30, m31, m32, m33;

        m00 = mLeft.m00*mRight.m00 + mLeft.m01*mRight.m10 +
                mLeft.m02*mRight.m20 + mLeft.m03*mRight.m30;
        m01 = mLeft.m00*mRight.m01 + mLeft.m01*mRight.m11 +
                mLeft.m02*mRight.m21 + mLeft.m03*mRight.m31;
        m02 = mLeft.m00*mRight.m02 + mLeft.m01*mRight.m12 +
                mLeft.m02*mRight.m22 + mLeft.m03*mRight.m32;
        m03 = mLeft.m00*mRight.m03 + mLeft.m01*mRight.m13 +
                mLeft.m02*mRight.m23 + mLeft.m03*mRight.m33;

        m10 = mLeft.m10*mRight.m00 + mLeft.m11*mRight.m10 +
                mLeft.m12*mRight.m20 + mLeft.m13*mRight.m30;
        m11 = mLeft.m10*mRight.m01 + mLeft.m11*mRight.m11 +
                mLeft.m12*mRight.m21 + mLeft.m13*mRight.m31;
        m12 = mLeft.m10*mRight.m02 + mLeft.m11*mRight.m12 +
                mLeft.m12*mRight.m22 + mLeft.m13*mRight.m32;
        m13 = mLeft.m10*mRight.m03 + mLeft.m11*mRight.m13 +
                mLeft.m12*mRight.m23 + mLeft.m13*mRight.m33;

        m20 = mLeft.m20*mRight.m00 + mLeft.m21*mRight.m10 +
                mLeft.m22*mRight.m20 + mLeft.m23*mRight.m30;
        m21 = mLeft.m20*mRight.m01 + mLeft.m21*mRight.m11 +
                mLeft.m22*mRight.m21 + mLeft.m23*mRight.m31;
        m22 = mLeft.m20*mRight.m02 + mLeft.m21*mRight.m12 +
                mLeft.m22*mRight.m22 + mLeft.m23*mRight.m32;
        m23 = mLeft.m20*mRight.m03 + mLeft.m21*mRight.m13 +
                mLeft.m22*mRight.m23 + mLeft.m23*mRight.m33;

        m30 = mLeft.m30*mRight.m00 + mLeft.m31*mRight.m10 +
                mLeft.m32*mRight.m20 + mLeft.m33*mRight.m30;
        m31 = mLeft.m30*mRight.m01 + mLeft.m31*mRight.m11 +
                mLeft.m32*mRight.m21 + mLeft.m33*mRight.m31;
        m32 = mLeft.m30*mRight.m02 + mLeft.m31*mRight.m12 +
                mLeft.m32*mRight.m22 + mLeft.m33*mRight.m32;
        m33 = mLeft.m30*mRight.m03 + mLeft.m31*mRight.m13 +
                mLeft.m32*mRight.m23 + mLeft.m33*mRight.m33;

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
        double x = m00*point.getX() + m01*point.getY() + m02*point.getZ() + m03;
        double y = m10*point.getX() + m11*point.getY() + m12*point.getZ() + m13;
        double z = m20*point.getX() + m21*point.getY() + m22*point.getZ() + m23;
        point.setX(x);
        point.setY(y);
        point.setZ(z);
    }

    /**
     * Transforms a 3D point vector using this transformation matrix.
     * The result is written to the input point.
     * 
     * @param point to transform
     */
    public final void transformPoint(Vector3 point) {
        double x = m00*point.x + m01*point.y + m02*point.z + m03;
        double y = m10*point.x + m11*point.y + m12*point.z + m13;
        double z = m20*point.x + m21*point.y + m22*point.z + m23;
        point.x = x;
        point.y = y;
        point.z = z;
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
        return new Vector3(m03, m13, m23);
    }

    /**
     * Obtains the absolute position vector of this matrix, equivalent to performing
     * transformPoint with a zero vector.
     * 
     * @return position vector
     */
    public Vector toVector() {
        return new Vector(m03, m13, m23);
    }

    /**
     * Obtains the absolute position vector and rotation yaw/pitch information of this matrix
     * 
     * @param world The world to use for the Location
     * @return location
     */
    public Location toLocation(World world) {
        Vector ypr = this.getYawPitchRoll();
        return new Location(world, m03, m13, m23, (float) ypr.getY(), (float) ypr.getX());
    }

    @Override
    public Matrix4x4 clone() {
        return new Matrix4x4(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Matrix4x4) {
            Matrix4x4 m = (Matrix4x4) o;
            return m00 == m.m00 && m01 == m.m01 && m02 == m.m02 && m03 == m.m03 &&
                   m10 == m.m10 && m11 == m.m11 && m12 == m.m12 && m13 == m.m13 &&
                   m20 == m.m20 && m21 == m.m21 && m22 == m.m22 && m23 == m.m23 &&
                   m30 == m.m30 && m31 == m.m31 && m32 == m.m32 && m33 == m.m33;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "{" +
            m00 + ", " + m01 + ", " + m02 + ", " + m03 + "\n " +
            m10 + ", " + m11 + ", " + m12 + ", " + m13 + "\n " +
            m20 + ", " + m21 + ", " + m22 + ", " + m23 + "\n " +
            m30 + ", " + m31 + ", " + m32 + ", " + m33 + "}";
    }

    /**
     * Creates a 4x4 matrix from 3 columns of a 3x3 matrix
     * 
     * @param v0 column 0
     * @param v1 column 1
     * @param v2 column 2
     * @return 4x4 matrix
     */
    public static Matrix4x4 fromColumns3x3(Vector v0, Vector v1, Vector v2) {
        return new Matrix4x4(
                v0.getX(), v1.getX(), v2.getX(), 0.0,
                v0.getY(), v1.getY(), v2.getY(), 0.0,
                v0.getZ(), v1.getZ(), v2.getZ(), 0.0,
                0.0, 0.0, 0.0, 1.0);
    }

    /**
     * Creates a 4x4 matrix by using the Location information of an Entity.
     * This is equivalent to calling {@link #translateRotate(Location)} on an
     * identity matrix.
     * 
     * @param location
     * @return transformation matrix for location
     */
    public static Matrix4x4 fromLocation(Location location) {
        Matrix4x4 result = translation(location.getX(), location.getY(), location.getZ());
        result.rotateYawPitchRoll(location.getPitch(), location.getYaw(), 0.0f);
        return result;
    }

    /**
     * Creates a new 4x4 identity matrix. This has the initial values:
     * <pre>
     * 1 0 0 0
     * 0 1 0 0
     * 0 0 1 0
     * 0 0 0 1
     * </pre>
     * 
     * @return identity matrix
     */
    public static Matrix4x4 identity() {
        return new Matrix4x4();
    }

    /**
     * Returns a new 4x4 identity matrix translated by the specified amount.
     *
     * @param position Translation vector
     * @return translated identity matrix
     */
    public static Matrix4x4 translation(Vector3 position) {
        return translation(position.x, position.y, position.z);
    }

    /**
     * Returns a new 4x4 identity matrix translated by the specified amount.
     *
     * @param position Translation vector
     * @return translated identity matrix
     */
    public static Matrix4x4 translation(Vector position) {
        return translation(position.getX(), position.getY(), position.getZ());
    }

    /**
     * Returns a new 4x4 identity matrix translated by the specified amount.
     *
     * @param x X-translation
     * @param y Y-translation
     * @param z Z-translation
     * @return translated identity matrix
     */
    public static Matrix4x4 translation(double x, double y, double z) {
        return new Matrix4x4(1.0, 0.0, 0.0, x,
                             0.0, 1.0, 0.0, y,
                             0.0, 0.0, 1.0, z,
                             0.0, 0.0, 0.0, 1.0);
    }

    /**
     * Computes the difference transformation between two matrices
     * 
     * @param m1 Old transformation matrix
     * @param m2 New transformation matrix
     * @return Matrix that transforms the old matrix into the new matrix
     */
    public static Matrix4x4 diff(Matrix4x4 m1, Matrix4x4 m2) {
        Matrix4x4 diff = m1.clone();
        diff.invert();
        diff.multiply(m2);
        return diff;
    }

    /**
     * Computes the difference rotation transformation between two matrices
     * 
     * @param m1 Old transformation matrix
     * @param m2 New transformation matrix
     * @return Quaternion that rotates {@link #getRotation()} of the old matrix into the new matrix
     */
    public static Quaternion diffRotation(Matrix4x4 m1, Matrix4x4 m2) {
        return diff(m1, m2).getRotation();
    }

    /**
     * Multiplies two matrices together, returning a new matrix with the result.
     * 
     * @param mLeft Left matrix of the matrix multiplication
     * @param mRight Right matrix of the matrix multiplication
     * @return Result of the multiplication
     */
    public static Matrix4x4 multiply(Matrix4x4 mLeft, Matrix4x4 mRight) {
        Matrix4x4 result = new Matrix4x4();
        result.storeMultiply(mLeft, mRight);
        return result;
    }

    /**
     * Multiplies two matrices together, storing the result in another matrix.
     * The result matrix is allowed to be the same instance as one of the input matrices.
     * 
     * @param mLeft Left matrix of the matrix multiplication
     * @param mRight Right matrix of the matrix multiplication
     * @param mResult Result of the multiplication is written to this matrix
     */
    public static void multiply(Matrix4x4 mLeft, Matrix4x4 mRight, Matrix4x4 mResult) {
        mResult.storeMultiply(mLeft, mRight);
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
