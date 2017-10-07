package com.bergerkiller.bukkit.common.math;

public class Vector4 {
    public double x;
    public double y;
    public double z;
    public double w;

    public Vector4() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.w = 0.0;
    }

    public Vector4(double x, double y, double z, double w)
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
