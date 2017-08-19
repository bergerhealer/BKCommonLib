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

    public Vector2f toVector2f() {
        return new Vector2f(x, z);
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
