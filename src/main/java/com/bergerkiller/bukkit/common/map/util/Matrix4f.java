package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.utils.MathUtil;

public class Matrix4f {
    public float m00;
    public float m01;
    public float m02;
    public float m03;
    public float m10;
    public float m11;
    public float m12;
    public float m13;
    public float m20;
    public float m21;
    public float m22;
    public float m23;
    public float m30;
    public float m31;
    public float m32;
    public float m33;

    /**
     * Constructs a new 4x4 matrix, initialized as an identity matrix:
     * <pre>
     * 1 0 0 0
     * 0 1 0 0
     * 0 0 1 0
     * 0 0 0 1
     * </pre>
     */
    public Matrix4f() {
        this.setIdentity();
    }

    /**
     * Constructs a new 4x4 matrix using the 16 values specified
     */
    public Matrix4f(
            float m00, float m01, float m02, float m03,
            float m10, float m11, float m12, float m13,
            float m20, float m21, float m22, float m23,
            float m30, float m31, float m32, float m33)
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
    public Matrix4f(Matrix4f matrix) {
        this.set(matrix);
    }

    /**
     * Sets all 16 values of this 4x4 matrix
     */
    public final void set(
            float m00, float m01, float m02, float m03,
            float m10, float m11, float m12, float m13,
            float m20, float m21, float m22, float m23,
            float m30, float m31, float m32, float m33)
    {
        this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
        this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
        this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
        this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33;
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
        this.m00 = (float) m00; this.m01 = (float) m01; this.m02 = (float) m02; this.m03 = (float) m03;
        this.m10 = (float) m10; this.m11 = (float) m11; this.m12 = (float) m12; this.m13 = (float) m13;
        this.m20 = (float) m20; this.m21 = (float) m21; this.m22 = (float) m22; this.m23 = (float) m23;
        this.m30 = (float) m30; this.m31 = (float) m31; this.m32 = (float) m32; this.m33 = (float) m33;
    }

    /**
     * Sets this matrix to all values of another 4x4 matrix
     * 
     * @param m matrix to set to
     */
    public final void set(Matrix4f m) {
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
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
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
        values[0]  = m00; values[1]  = m01; values[2]  = m02; values[3]  = m03;
        values[4]  = m10; values[5]  = m11; values[6]  = m12; values[7]  = m13;
        values[8]  = m20; values[9]  = m21; values[10] = m22; values[11] = m23;
        values[12] = m30; values[13] = m31; values[14] = m32; values[15] = m33;
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
     * Multiplies this matrix with a rotation transformation about the X-axis
     * 
     * @param angle the angle to rotate about the X axis in degrees
     */
    public final void rotateX(float angle)
    {
        float sinAngle = (float) Math.sin((double) angle * MathUtil.DEGTORAD);
        float cosAngle = (float) Math.cos((double) angle * MathUtil.DEGTORAD);

        Matrix4f tmp = new Matrix4f();
        tmp.m11 = cosAngle;
        tmp.m12 = -sinAngle;
        tmp.m21 = sinAngle;
        tmp.m22 = cosAngle;
        this.multiply(tmp);
    }

    /**
     * Multiplies this matrix with a rotation transformation about the Y-axis
     * 
     * @param angle the angle to rotate about the Y axis in degrees
     */
    public final void rotateY(float angle)
    {
        float sinAngle = (float) Math.sin((double) angle * MathUtil.DEGTORAD);
        float cosAngle = (float) Math.cos((double) angle * MathUtil.DEGTORAD);

        Matrix4f tmp = new Matrix4f();
        tmp.m00 = cosAngle;
        tmp.m02 = sinAngle;
        tmp.m20 = -sinAngle;
        tmp.m22 = cosAngle;
        this.multiply(tmp);
    }

    /**
     * Multiplies this matrix with a rotation transformation about the Z-axis
     * 
     * @param angle the angle to rotate about the Z axis in degrees
     */
    public final void rotateZ(float angle)
    {
        float sinAngle = (float) Math.sin((double) angle * MathUtil.DEGTORAD);
        float cosAngle = (float) Math.cos((double) angle * MathUtil.DEGTORAD);

        Matrix4f tmp = new Matrix4f();
        tmp.m00 = cosAngle;
        tmp.m01 = -sinAngle;
        tmp.m10 = sinAngle;
        tmp.m11 = cosAngle;
        this.multiply(tmp);
    }

    /**
     * Multiplies this matrix with a translation transformation
     * 
     * @param translation
     */
    public final void translate(Vector3f translation) {
        this.translate(translation.x, translation.y, translation.z);
    }

    /**
     * Multiplies this matrix with a translation transformation
     * 
     * @param dx translation
     * @param dy translation
     * @param dz translation
     */
    public final void translate(float dx, float dy, float dz) {
        Matrix4f tmp = new Matrix4f();
        tmp.m03 = dx;
        tmp.m13 = dy;
        tmp.m23 = dz;
        this.multiply(tmp);
    }

    /**
     * Multiplies this matrix with a scale transformation
     * 
     * @param scale
     */
    public final void scale(Vector3f scale) {
        this.scale(scale.x, scale.y, scale.z);
    }

    /**
     * Multiplies this matrix with a scale transformation
     * 
     * @param sx scale
     * @param sy scale
     * @param sz scale
     */
    public final void scale(float sx, float sy, float sz) {
        Matrix4f tmp = new Matrix4f();
        tmp.m00 = sx;
        tmp.m11 = sy;
        tmp.m22 = sz;
        this.multiply(tmp);
    }

    /**
     * Multiplies this matrix with a scale transformation
     * 
     * @param scale
     */
    public final void scale(float scale) {
        this.scale(scale, scale, scale);
    }

    /**
     * Multiplies this matrix with another, storing the result in this matrix
     * 
     * @param m1 the other matrix to multiply with
     */
    public final void multiply(Matrix4f m1)
    {
        float m00, m01, m02, m03;
        float m10, m11, m12, m13;
        float m20, m21, m22, m23;
        float m30, m31, m32, m33;

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
    public final void transformPoint(Vector3f point) {
        float x,y;
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
    public final void transformPoint(Vector4f point) {
        float x = m00 * point.x + m01 * point.y + m02 * point.z + m03 * point.w;
        float y = m10 * point.x + m11 * point.y + m12 * point.z + m13 * point.w;
        float z = m20 * point.x + m21 * point.y + m22 * point.z + m23 * point.w;
        float w = m30 * point.x + m31 * point.y + m32 * point.z + m33 * point.w;
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

    // From https://math.stackexchange.com/questions/296794
    public static Matrix4f computeProjectionMatrix(Vector3f p[])
    {
        Matrix4f m = new Matrix4f(
                p[0].x, p[1].x, p[2].x, 0.0f,
                p[0].y, p[1].y, p[2].y, 1.0f,
                p[0].z, p[1].z, p[2].z, 0.0f,
                1, 1, 1, 0);

        //TODO: For some reason we need to add a very small value to p[3].y to avoid glitching out
        // Any reason why? Should this value be calculated from somewhere? View clipping plane?
        // Or maybe the matrix inversion simply cannot handle an y value without it.
        Vector4f p3 = new Vector4f(p[3].x, p[3].y + 0.001f, p[3].z, 1.0f);
        Matrix4f mInv = new Matrix4f(m);
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
