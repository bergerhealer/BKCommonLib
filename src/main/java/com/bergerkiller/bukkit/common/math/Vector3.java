package com.bergerkiller.bukkit.common.math;

import org.bukkit.util.Vector;

public class Vector3 {
    public double x;
    public double y;
    public double z;

    public Vector3() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    public Vector3(Vector vector) {
        this(vector.getX(), vector.getY(), vector.getZ());
    }

    public Vector3(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector2 getXY() {
        return new Vector2(x, y);
    }

    public Vector2 getXZ() {
        return new Vector2(x, z);
    }

    /**
     * Returns a new vector with the x/y/z values inverted.
     * 
     * @return negated vector
     */
    public Vector3 negate() {
        return new Vector3(-x, -y, -z);
    }

    /**
     * Returns a new normalized vector of this vector (length = 1)
     * 
     * @return normalized vector
     */
    public Vector3 normalize() {
        double len = Math.sqrt(x*x+y*y+z*z);
        return new Vector3((x/len), (y/len), (z/len));
    }

    public double distanceSquared(Vector3 v) {
        double dx = (this.x - v.x);
        double dy = (this.y - v.y);
        double dz = (this.z - v.z);
        return (dx * dx) + (dy * dy) + (dz * dz);
    }

    @Override
    public Vector3 clone() {
        return new Vector3(x, y, z);
    }

    @Override
    public String toString() {
        return "{x=" + x + ", y=" + y + ", z=" + z + "}";
    }

    public boolean equals(Vector3 p) {
        return p.x == x && p.y == y && p.z == z;
    }

    public boolean equals(Vector v) {
        return v.getX() == x && v.getY() == y && v.getZ() == z;
    }

    /**
     * Returns the cross product of two vectors
     * 
     * @param v1 first vector
     * @param v2 second vector
     * @return cross product
     */
    public static Vector3 cross(Vector3 v1, Vector3 v2) {
        return new Vector3(
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
    public static Vector3 subtract(Vector3 v1, Vector3 v2) {
        return new Vector3(
                v1.x - v2.x,
                v1.y - v2.y,
                v1.z - v2.z
        );
    }

    public static Vector3 add(Vector3 v1, Vector3 v2) {
        return new Vector3(
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
    public static Vector3 average(Vector3 v1, Vector3 v2) {
        return new Vector3(
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
    public static double dot(Vector3 v1, Vector3 v2) {
        return (v1.x*v2.x + v1.y*v2.y + v1.z*v2.z);
    }
}
