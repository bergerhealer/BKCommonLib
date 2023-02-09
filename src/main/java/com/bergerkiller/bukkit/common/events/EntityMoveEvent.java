package com.bergerkiller.bukkit.common.events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;

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
     * @param entityHandle Handle of the Entity to set to
     */
    public void setEntity(EntityHandle entityHandle) {
        this.entityHandle = entityHandle;
        this.entity = this.entityHandle.getBukkitEntity();
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
     * Gets the old from position and rotation and stores it into the input Location.
     * With how often this is called, it is recommended to re-use the Location object.
     * 
     * @param from to update with the from position and rotation
     * @return from
     */
    public Location getFrom(Location from) {
        if (from != null) {
            from.setWorld(this.entity.getWorld());
            from.setX(this.entityHandle.getLastX());
            from.setY(this.entityHandle.getLastY());
            from.setZ(this.entityHandle.getLastZ());
            from.setYaw(this.entityHandle.getLastYaw());
            from.setPitch(this.entityHandle.getLastPitch());
        }
        return from;
    }

    /**
     * Gets the new to position and rotation and stores it into the input Location.
     * With how often this is called, it is recommended to re-use the Location object.
     * 
     * @param to to update with the new position and rotation
     * @return to
     */
    public Location getTo(Location to) {
        return this.entity.getLocation(to);
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
