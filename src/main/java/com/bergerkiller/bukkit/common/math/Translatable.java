package com.bergerkiller.bukkit.common.math;

import org.bukkit.util.Vector;

/**
 * An object that can be transformed with a translation vector
 */
public interface Translatable {

    /**
     * Translates by a vector amount
     *
     * @param dx Delta-X translation
     * @param dy Delta-Y translation
     * @param dz Delta-Z translation
     */
    void translate(double dx, double dy, double dz);

    /**
     * Translates by a vector amount
     *
     * @param delta Delta translation
     */
    default void translate(Vector3 delta) {
        translate(delta.x, delta.y, delta.z);
    }

    /**
     * Translates by a vector amount
     *
     * @param delta Delta translation
     */
    default void translate(Vector delta) {
        this.translate(delta.getX(), delta.getY(), delta.getZ());
    }
}
