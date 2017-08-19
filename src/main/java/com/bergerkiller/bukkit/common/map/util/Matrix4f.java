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

    public Matrix4f() {
        this.m00 = 0.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m03 = 0.0f;

        this.m10 = 0.0f;
        this.m11 = 0.0f;
        this.m12 = 0.0f;
        this.m13 = 0.0f;

        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 0.0f;
        this.m23 = 0.0f;

        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 0.0f;
    }

    /**
     * Constructs and initializes a Matrix4f from the specified 16 values.
     * @param m00 the [0][0] element
     * @param m01 the [0][1] element
     * @param m02 the [0][2] element
     * @param m03 the [0][3] element
     * @param m10 the [1][0] element
     * @param m11 the [1][1] element
     * @param m12 the [1][2] element
     * @param m13 the [1][3] element
     * @param m20 the [2][0] element
     * @param m21 the [2][1] element
     * @param m22 the [2][2] element
     * @param m23 the [2][3] element
     * @param m30 the [3][0] element
     * @param m31 the [3][1] element
     * @param m32 the [3][2] element
     * @param m33 the [3][3] element
     */
    public Matrix4f(float m00, float m01, float m02, float m03,
            float m10, float m11, float m12, float m13,
            float m20, float m21, float m22, float m23,
            float m30, float m31, float m32, float m33)
    {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;

        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;

        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;

        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }

    public final void setIdentity()
    {
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m03 = 0.0f;

        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m13 = 0.0f;

        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
        this.m23 = 0.0f;

        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }

    /**
     * Sets the value of this matrix to a counter clockwise rotation
     * about the x axis.
     * @param angle the angle to rotate about the X axis in radians
     */
    public final void rotateX(float angle)
    {
        float   sinAngle, cosAngle;

        sinAngle = (float) Math.sin((double) angle * MathUtil.DEGTORAD);
        cosAngle = (float) Math.cos((double) angle * MathUtil.DEGTORAD);

        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m03 = 0.0f;

        this.m10 = 0.0f;
        this.m11 = cosAngle;
        this.m12 = -sinAngle;
        this.m13 = 0.0f;

        this.m20 = 0.0f;
        this.m21 = sinAngle;
        this.m22 = cosAngle;
        this.m23 = 0.0f;

        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }

    /**
     * Sets the value of this matrix to a counter clockwise rotation
     * about the y axis.
     * @param angle the angle to rotate about the Y axis in radians
     */
    public final void rotateY(float angle)
    {
        float   sinAngle, cosAngle;

        sinAngle = (float) Math.sin((double) angle * MathUtil.DEGTORAD);
        cosAngle = (float) Math.cos((double) angle * MathUtil.DEGTORAD);

        this.m00 = cosAngle;
        this.m01 = 0.0f;
        this.m02 = sinAngle;
        this.m03 = 0.0f;

        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m13 = 0.0f;

        this.m20 = -sinAngle;
        this.m21 = 0.0f;
        this.m22 = cosAngle;
        this.m23 = 0.0f;

        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }

    /**
     * Sets the value of this matrix to a counter clockwise rotation
     * about the z axis.
     * @param angle the angle to rotate about the Z axis in radians
     */
    public final void rotateZ(float angle)
    {
        float   sinAngle, cosAngle;

        sinAngle = (float) Math.sin((double) angle * MathUtil.DEGTORAD);
        cosAngle = (float) Math.cos((double) angle * MathUtil.DEGTORAD);

        this.m00 = cosAngle;
        this.m01 = -sinAngle;
        this.m02 = 0.0f;
        this.m03 = 0.0f;

        this.m10 = sinAngle;
        this.m11 = cosAngle;
        this.m12 = 0.0f;
        this.m13 = 0.0f;

        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
        this.m23 = 0.0f;

        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }

    /**
     * Sets the value of this matrix to a translate matrix with
     * the passed translation value.
     * @param v1 the translation amount
     */
    public final void set(Vector3f v1)
    {
    this.m00 = (float) 1.0;
    this.m01 = (float) 0.0;
    this.m02 = (float) 0.0;
    this.m03 = v1.x;

    this.m10 = (float) 0.0;
    this.m11 = (float) 1.0;
    this.m12 = (float) 0.0;
    this.m13 = v1.y;

    this.m20 = (float) 0.0;
    this.m21 = (float) 0.0;
    this.m22 = (float) 1.0;
    this.m23 = v1.z;

    this.m30 = (float) 0.0;
    this.m31 = (float) 0.0;
    this.m32 = (float) 0.0;
    this.m33 = (float) 1.0;
}
    
    /**
     * Sets the value of this transform to a scale and translation matrix;
     * the scale is not applied to the translation and all of the matrix
     * values are modified.
     * @param scale the scale factor for the matrix
     * @param t1 the translation amount
     */
    public final void set(float scale, Vector3f t1)
    {
    this.m00 = scale;
    this.m01 = (float) 0.0;
    this.m02 = (float) 0.0;
    this.m03 = t1.x;

    this.m10 = (float) 0.0;
    this.m11 = scale;
    this.m12 = (float) 0.0;
    this.m13 = t1.y;

    this.m20 = (float) 0.0;
    this.m21 = (float) 0.0;
    this.m22 = scale;
    this.m23 = t1.z;

    this.m30 = (float) 0.0;
    this.m31 = (float) 0.0;
    this.m32 = (float) 0.0;
    this.m33 = (float) 1.0;
}
    
    /**
     * Sets the value of this matrix to the result of multiplying itself
     * with matrix m1.
     * @param m1 the other matrix
     */
    public final void multiply(Matrix4f m1)
    {
        float       m00, m01, m02, m03,
                    m10, m11, m12, m13,
                    m20, m21, m22, m23,
                    m30, m31, m32, m33;  // vars for temp result matrix

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
     * Transforms the point parameter with this Matrix4f and
     * places the result into pointOut.  The fourth element of the
     * point input paramter is assumed to be one.
     * @param point  the input point to be transformed.
     * @param pointOut  the transformed point
     */
      public final void transform(Vector3f point, Vector3f pointOut)
      {
          float x,y;
          x = m00*point.x + m01*point.y + m02*point.z + m03;
          y = m10*point.x + m11*point.y + m12*point.z + m13;
          pointOut.z = m20*point.x + m21*point.y + m22*point.z + m23;
          pointOut.x = x;
          pointOut.y = y;
      }
    
}
