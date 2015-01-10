package com.bergerkiller.bukkit.common.events;

import net.minecraft.server.v1_8_R1.Entity;

import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

import com.bergerkiller.bukkit.common.internal.CommonNMS;

/**
 * Fired when an entity moves
 */
public class EntityMoveEvent extends EntityEvent {

    private static final HandlerList handlers = new HandlerList();
    private Entity nativeEntity;

    public EntityMoveEvent() {
        super(null);
    }

    /**
     * Sets the Entity represented by this move event<br>
     * <b>Only called internally by the event creator!</b>
     *
     * @param entity to set to
     */
    public void setEntity(Entity entity) {
        this.nativeEntity = entity;
        this.entity = CommonNMS.getEntity(entity);
    }

    /**
     * Gets the world in which the entity moved
     *
     * @return Last and current Entity World
     */
    public World getWorld() {
        return this.entity.getWorld();
    }

    /**
     * Gets the X-coordinate value before the current tick
     *
     * @return Last X-coordinate
     */
    public double getFromX() {
        return nativeEntity.lastX;
    }

    /**
     * Gets the Y-coordinate value before the current tick
     *
     * @return Last Y-coordinate
     */
    public double getFromY() {
        return nativeEntity.lastY;
    }

    /**
     * Gets the Z-coordinate value before the current tick
     *
     * @return Last Z-coordinate
     */
    public double getFromZ() {
        return nativeEntity.lastZ;
    }

    /**
     * Gets the yaw angle value before the current tick
     *
     * @return Last yaw angle in degrees
     */
    public float getFromYaw() {
        return nativeEntity.lastYaw;
    }

    /**
     * Gets the pitch angle value before the current tick
     *
     * @return Last pitch angle in degrees
     */
    public float getFromPitch() {
        return nativeEntity.lastPitch;
    }

    /**
     * Gets the X-coordinate value of the current tick
     *
     * @return Current X-coordinate
     */
    public double getToX() {
        return nativeEntity.locX;
    }

    /**
     * Gets the Y-coordinate value of the current tick
     *
     * @return Current Y-coordinate
     */
    public double getToY() {
        return nativeEntity.locY;
    }

    /**
     * Gets the Z-coordinate value of the current tick
     *
     * @return Current Z-coordinate
     */
    public double getToZ() {
        return nativeEntity.locZ;
    }

    /**
     * Gets the yaw angle value of the current tick
     *
     * @return Current yaw angle in degrees
     */
    public float getToYaw() {
        return nativeEntity.yaw;
    }

    /**
     * Gets the pitch angle value of the current tick
     *
     * @return Current pitch angle in degrees
     */
    public float getToPitch() {
        return nativeEntity.pitch;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
