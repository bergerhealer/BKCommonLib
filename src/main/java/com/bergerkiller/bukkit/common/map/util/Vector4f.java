package com.bergerkiller.bukkit.common.map.util;

public class Vector4f {
    public float x;
    public float y;
    public float z;
    public float w;

    public Vector4f() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 0.0f;
    }

    public Vector4f(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public String toString() {
        return "{x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + "}";
    }
}
