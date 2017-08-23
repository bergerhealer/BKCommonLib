package com.bergerkiller.bukkit.common.map.util;

public class Vector3f {
    public float x;
    public float y;
    public float z;

    public Vector3f() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }
    
    public Vector3f(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector2f getXY() {
        return new Vector2f(x, y);
    }
    
    public Vector2f getXZ() {
        return new Vector2f(x, z);
    }

    /**
     * Returns a new vector with the x/y/z values inverted.
     * 
     * @return negated vector
     */
    public Vector3f negate() {
        return new Vector3f(-x, -y, -z);
    }

    @Override
    public Vector3f clone() {
        return new Vector3f(x, y, z);
    }

    @Override
    public String toString() {
        return "{x=" + x + ", y=" + y + ", z=" + z + "}";
    }
}
