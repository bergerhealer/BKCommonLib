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

    /**
     * Returns a new normalized vector of this vector (length = 1)
     * 
     * @return normalized vector
     */
    public Vector3f normalize() {
        double len = Math.sqrt(x*x+y*y+z*z);
        return new Vector3f((float) (x/len), (float) (y/len), (float) (z/len));
    }

    public float distanceSquared(Vector3f v) {
        float dx = (this.x - v.x);
        float dy = (this.y - v.y);
        float dz = (this.z - v.z);
        return (dx * dx) + (dy * dy) + (dz * dz);
    }

    @Override
    public Vector3f clone() {
        return new Vector3f(x, y, z);
    }

    @Override
    public String toString() {
        return "{x=" + x + ", y=" + y + ", z=" + z + "}";
    }

    public boolean equals(Vector3f p) {
        return p.x == x && p.y == y && p.z == z;
    }

    /**
     * Returns the cross product of two vectors
     * 
     * @param v1 first vector
     * @param v2 second vector
     * @return cross product
     */
    public static Vector3f cross(Vector3f v1, Vector3f v2) {
        return new Vector3f(
                v1.y*v2.z - v1.z*v2.y,
                v2.x*v1.z - v2.z*v1.x,
                v1.x*v2.y - v1.y*v2.x
        );
    }

    /**
     * Returns the vector subtraction of two vectors (v1 - v2)
     * 
     * @param v1
     * @param v2
     * @return subtracted vector
     */
    public static Vector3f subtract(Vector3f v1, Vector3f v2) {
        return new Vector3f(
                v1.x - v2.x,
                v1.y - v2.y,
                v1.z - v2.z
        );
    }

    public static Vector3f add(Vector3f v1, Vector3f v2) {
        return new Vector3f(
                v1.x + v2.x,
                v1.y + v2.y,
                v1.z + v2.z
        );
    }
    
    /**
     * Returns the vector average of two vectors ((v1 + v2) / 2)
     * 
     * @param v1
     * @param v2
     * @return average vector
     */
    public static Vector3f average(Vector3f v1, Vector3f v2) {
        return new Vector3f(
                (v1.x + v2.x) / 2.0f,
                (v1.y + v2.y) / 2.0f,
                (v1.z + v2.z) / 2.0f
        );
    }

    /**
     * Returns the vector dot product of two vectors (v1 . v2)
     * 
     * @param v1
     * @param v2
     * @return vector dot product
     */
    public static float dot(Vector3f v1, Vector3f v2) {
        return (v1.x*v2.x + v1.y*v2.y + v1.z*v2.z);
    }
}
