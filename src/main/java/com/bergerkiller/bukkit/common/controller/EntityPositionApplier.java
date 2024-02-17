package com.bergerkiller.bukkit.common.controller;

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Sets the absolute x/y/z position of an Entity.
 * Also includes some convenience methods to get the current
 * values of the Entity, and some (head) yaw/pitch setters/getters.
 */
public interface EntityPositionApplier {

    /**
     * Sets the x/y/z position coordinates of the entity.
     * (Head) Yaw and pitch are not affected.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param z Z-coordinate
     */
    void setPosition(double x, double y, double z);

    /**
     * Gets the current x/y/z position coordinates of the entity.
     *
     * @return Entity position
     */
    Vector getPosition();

    /**
     * Sets the yaw of the body of the Entity
     *
     * @param yaw Body yaw
     */
    void setBodyYaw(float yaw);

    /**
     * Sets the yaw of the head of the Entity - where the Entity looks.
     * Only works for entities that have a head.
     *
     * @param yaw Head Yaw
     */
    void setHeadYaw(float yaw);

    /**
     * Sets the pitch of the head of the Entity - where the Entity looks.
     * Only works for entities that have a head.
     *
     * @param pitch Head Pitch
     */
    void setHeadPitch(float pitch);

    /**
     * Gets the yaw of the body of the Entity
     *
     * @return Body yaw
     */
    float getBodyYaw();

    /**
     * Gets the yaw of the head of the Entity.
     * Returns 0.0 for entities that do not have a head.
     *
     * @return Head yaw
     */
    float getHeadYaw();

    /**
     * Gets the pitch of the head of the Entity.
     * Returns 0.0 for entities that do not have a head.
     *
     * @return Head pitch
     */
    float getHeadPitch();

    /**
     * Sets the position coordinate of the entity.
     * (Head) Yaw and pitch are not affected.
     *
     * @param position Position
     */
    default void setPosition(Location position) {
        setPosition(position.getX(), position.getY(), position.getZ());
    }

    /**
     * Sets the position coordinate of the entity.
     * (Head) Yaw and pitch are not affected.
     *
     * @param position Position
     */
    default void setPosition(Vector position) {
        setPosition(position.getX(), position.getY(), position.getZ());
    }
}
