package com.bergerkiller.bukkit.common.map.util;

public class Matrix3f {
    public float m00;
    public float m01;
    public float m02;
    public float m10;
    public float m11;
    public float m12;
    public float m20;
    public float m21;
    public float m22;

    public Matrix3f(
        float m00, float m01, float m02,
        float m10, float m11, float m12,
        float m20, float m21, float m22)
    {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }

    public Matrix3f(Matrix3f m)
    {
        this.m00 = m.m00;
        this.m01 = m.m01;
        this.m02 = m.m02;
        this.m10 = m.m10;
        this.m11 = m.m11;
        this.m12 = m.m12;
        this.m20 = m.m20;
        this.m21 = m.m21;
        this.m22 = m.m22;
    }

    // From http://www.dr-lex.be/random/matrix_inv.html
    public void invert()
    {
        float invDet = 1.0f / determinant();
        float nm00 = m22 * m11 - m21 * m12;
        float nm01 = -(m22 * m01 - m21 * m02);
        float nm02 = m12 * m01 - m11 * m02;
        float nm10 = -(m22 * m10 - m20 * m12);
        float nm11 = m22 * m00 - m20 * m02;
        float nm12 = -(m12 * m00 - m10 * m02);
        float nm20 = m21 * m10 - m20 * m11;
        float nm21 = -(m21 * m00 - m20 * m01);
        float nm22 = m11 * m00 - m10 * m01;
        m00 = nm00 * invDet;
        m01 = nm01 * invDet;
        m02 = nm02 * invDet;
        m10 = nm10 * invDet;
        m11 = nm11 * invDet;
        m12 = nm12 * invDet;
        m20 = nm20 * invDet;
        m21 = nm21 * invDet;
        m22 = nm22 * invDet;
    }

    // From http://www.dr-lex.be/random/matrix_inv.html
    public float determinant()
    {
        return
            m00 * (m11 * m22 - m12 * m21) +
            m01 * (m12 * m20 - m10 * m22) +
            m02 * (m10 * m21 - m11 * m20);
    }

    public final void mul(float factor)
    {
        m00 *= factor;
        m01 *= factor;
        m02 *= factor;

        m10 *= factor;
        m11 *= factor;
        m12 *= factor;

        m20 *= factor;
        m21 *= factor;
        m22 *= factor;
    }

    public void transform(Vector3f p)
    {
        float x = m00 * p.x + m01 * p.y + m02 * p.z;
        float y = m10 * p.x + m11 * p.y + m12 * p.z;
        float z = m20 * p.x + m21 * p.y + m22 * p.z;
        p.x = x;
        p.y = y;
        p.z = z;
    }

    public void transform(Vector2f pp)
    {
        Vector3f p = new Vector3f((float) pp.x, (float) pp.y, 1.0f);
        transform(p);
        pp.x = p.x / p.z;
        pp.y = p.y / p.z;
    }

    public void mul(Matrix3f m)
    {
        float nm00 = m00 * m.m00 + m01 * m.m10 + m02 * m.m20;
        float nm01 = m00 * m.m01 + m01 * m.m11 + m02 * m.m21;
        float nm02 = m00 * m.m02 + m01 * m.m12 + m02 * m.m22;

        float nm10 = m10 * m.m00 + m11 * m.m10 + m12 * m.m20;
        float nm11 = m10 * m.m01 + m11 * m.m11 + m12 * m.m21;
        float nm12 = m10 * m.m02 + m11 * m.m12 + m12 * m.m22;

        float nm20 = m20 * m.m00 + m21 * m.m10 + m22 * m.m20;
        float nm21 = m20 * m.m01 + m21 * m.m11 + m22 * m.m21;
        float nm22 = m20 * m.m02 + m21 * m.m12 + m22 * m.m22;

        m00 = nm00;
        m01 = nm01;
        m02 = nm02;
        m10 = nm10;
        m11 = nm11;
        m12 = nm12;
        m20 = nm20;
        m21 = nm21;
        m22 = nm22;
    }


    // From https://math.stackexchange.com/questions/296794
    public static Matrix3f computeProjectionMatrix(Vector2f p0[], Vector2f p1[])
    {
        Matrix3f m0 = computeProjectionMatrix(p0);
        Matrix3f m1 = computeProjectionMatrix(p1);
        m1.invert();
        m0.mul(m1);
        return m0;
    }

    // From https://math.stackexchange.com/questions/296794
    public static Matrix3f computeProjectionMatrix(Vector2f p[])
    {
        Matrix3f m = new Matrix3f(
            (float) p[0].x, (float) p[1].x, (float) p[2].x,
            (float) p[0].y, (float) p[1].y, (float) p[2].y,
            1, 1, 1);
        Vector3f p3 = new Vector3f((float) p[3].x, (float) p[3].y, 1);
        Matrix3f mInv = new Matrix3f(m);
        mInv.invert();
        mInv.transform(p3);
        m.m00 *= p3.x;
        m.m01 *= p3.y;
        m.m02 *= p3.z;
        m.m10 *= p3.x;
        m.m11 *= p3.y;
        m.m12 *= p3.z;
        m.m20 *= p3.x;
        m.m21 *= p3.y;
        m.m22 *= p3.z;
        return m;
    }
}
