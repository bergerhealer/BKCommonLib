package com.bergerkiller.bukkit.common.map.util;

public class Vector2f {
    public float x;
    public float y;

    public Vector2f() {
        this.x = 0.0f;
        this.y = 0.0f;
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float distance(Vector2f p) {
        double dx = ((double) p.x - (double) this.x);
        double dy = ((double) p.y - (double) this.y);
        return (float) Math.sqrt(dx*dx+dy*dy);
    }

    @Override
    public String toString() {
        return "{x=" + x + ", y=" + y + "}";
    }
}
