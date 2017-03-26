package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityTracker;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Wrapper class for the Entity Tracker
 */
public class EntityTracker extends BasicWrapper {

    public EntityTracker(Object entityTrackerHandle) {
        setHandle(entityTrackerHandle);
    }

    /**
     * Sends a packet to all nearby players (and self if it is a player)
     *
     * @param entity near which viewers should be sent
     * @param packet to send
     */
    public void sendPacket(Entity entity, CommonPacket packet) {
        sendPacket(entity, packet.getHandle());
    }

    /**
     * Sends a packet to all nearby players (and self if it is a player)
     *
     * @param entity near which viewers should be sent
     * @param packet to send
     */
    public void sendPacket(Entity entity, Object packet) {
        NMSEntityTracker.sendPacket(handle, entity, packet);
    }

    /**
     * Sends spawn packets to the player for all entities contained in the chunk
     *
     * @param player to send spawn packets to
     * @param chunk containing the entities to update
     */
    public void spawnEntities(Player player, Chunk chunk) {
        NMSEntityTracker.spawnEntities(handle, player, chunk);
    }

    /**
     * Removes a player viewer, resulting in all entities being removed for the
     * viewer. If the player is still referenced in the entity tracker in some
     * way, the entities will be re-sent at a later time automatically. This
     * method acts as a 'total respawn' system for that reason.
     *
     * @param player to remove from this Entity Tracker
     */
    public void removeViewer(Player player) {
        NMSEntityTracker.removeViewer(handle, player);
    }

    /**
     * Informs all entities of a new player viewer, sending possible spawn
     * packets
     *
     * @param player to update
     */
    public void updateViewer(Player player) {
        NMSEntityTracker.updatePlayer(handle, player);
    }

    /**
     * Adds an entity to this entity tracker, creating a new entity tracker
     * entry if needed
     *
     * @param entity to start tracking
     */
    public void startTracking(Entity entity) {
        NMSEntityTracker.startTracking(handle, entity);
    }

    /**
     * Removes an entity from this entity tracker. This call will result in
     * entity destroy packets being sent to nearby players.
     *
     * @param entity to remove
     */
    public void stopTracking(Entity entity) {
        NMSEntityTracker.stopTracking(handle, entity);
    }

    /**
     * Sets the entity tracker entry for an entity
     *
     * @param entity to set the tracker entry for
     * @param entityTrackerEntry to set to
     * @return previously set entity tracker entry, null if there was none
     */
    public Object setEntry(Entity entity, Object entityTrackerEntry) {
        return NMSEntityTracker.setEntry(handle, entity, entityTrackerEntry);
    }

    /**
     * Gets the entity tracker entry of an entity
     *
     * @param entity to get the entry of
     * @return entity tracker entry
     */
    public Object getEntry(Entity entity) {
        return NMSEntityTracker.getEntry(handle, entity);
    }

    /**
     * Gets the entity tracker entry of an entity Id
     *
     * @param id of the entity to get the entry of
     * @return entity tracker entry
     */
    public Object getEntry(int id) {
        return NMSEntityTracker.getEntry(handle, id);
    }
}
