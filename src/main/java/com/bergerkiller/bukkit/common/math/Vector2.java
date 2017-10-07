package com.bergerkiller.bukkit.common.math;

public class Vector2 {
    public double x;
    public double y;

    public Vector2() {
        this.x = 0.0;
        this.y = 0.0;
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distance(Vector2 p) {
        double dx = (p.x - this.x);
        double dy = (p.y - this.y);
        return Math.sqrt(dx*dx+dy*dy);
    }

    @Override
    public String toString() {
        return "{x=" + x + ", y=" + y + "}";
    }
}
