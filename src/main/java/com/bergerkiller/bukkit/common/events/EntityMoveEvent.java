package com.bergerkiller.bukkit.common.events;

import com.bergerkiller.generated.net.minecraft.server.EntityHandle;

import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Fired when an entity moves
 */
public class EntityMoveEvent extends EntityEvent {

    private static final HandlerList handlers = new HandlerList();
    private EntityHandle entityHandle;

    public EntityMoveEvent() {
        super(null);
    }

    /**
     * Sets the Entity represented by this move event<br>
     * <b>Only called internally by the event creator!</b>
     *
     * @param entity to set to
     */
    public void setEntity(EntityHandle entityHandle) {
        this.entityHandle = entityHandle;
        this.entity = this.entityHandle.toBukkit();
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
        return entityHandle.getLastX();
    }

    /**
     * Gets the Y-coordinate value before the current tick
     *
     * @return Last Y-coordinate
     */
    public double getFromY() {
        return entityHandle.getLastY();
    }

    /**
     * Gets the Z-coordinate value before the current tick
     *
     * @return Last Z-coordinate
     */
    public double getFromZ() {
        return entityHandle.getLastZ();
    }

    /**
     * Gets the yaw angle value before the current tick
     *
     * @return Last yaw angle in degrees
     */
    public float getFromYaw() {
        return entityHandle.getLastYaw();
    }

    /**
     * Gets the pitch angle value before the current tick
     *
     * @return Last pitch angle in degrees
     */
    public float getFromPitch() {
        return entityHandle.getLastPitch();
    }

    /**
     * Gets the X-coordinate value of the current tick
     *
     * @return Current X-coordinate
     */
    public double getToX() {
        return entityHandle.getLocX();
    }

    /**
     * Gets the Y-coordinate value of the current tick
     *
     * @return Current Y-coordinate
     */
    public double getToY() {
        return entityHandle.getLocY();
    }

    /**
     * Gets the Z-coordinate value of the current tick
     *
     * @return Current Z-coordinate
     */
    public double getToZ() {
        return entityHandle.getLocZ();
    }

    /**
     * Gets the yaw angle value of the current tick
     *
     * @return Current yaw angle in degrees
     */
    public float getToYaw() {
        return entityHandle.getYaw();
    }

    /**
     * Gets the pitch angle value of the current tick
     *
     * @return Current pitch angle in degrees
     */
    public float getToPitch() {
        return entityHandle.getPitch();
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
