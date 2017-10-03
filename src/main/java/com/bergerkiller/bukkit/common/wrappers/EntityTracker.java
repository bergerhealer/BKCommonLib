package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerHandle;

import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Wrapper class for the Entity Tracker
 */
public class EntityTracker extends BasicWrapper<EntityTrackerHandle> {

    public EntityTracker(Object entityTrackerHandle) {
        setHandle(EntityTrackerHandle.createHandle(entityTrackerHandle));
    }

    /**
     * Sends a packet to all nearby players (and self if it is a player)
     *
     * @param entity near which viewers should be sent
     * @param packet to send
     */
    public void sendPacket(Entity entity, CommonPacket packet) {
        handle.sendPacketToEntity(entity, packet);
    }

    /**
     * Sends a packet to all nearby players (and self if it is a player)
     *
     * @param entity near which viewers should be sent
     * @param packet to send
     */
    public void sendPacket(Entity entity, Object packet) {
        sendPacket(entity, new CommonPacket(packet));
    }

    /**
     * Sends spawn packets to the player for all entities contained in the chunk
     *
     * @param player to send spawn packets to
     * @param chunk containing the entities to update
     */
    public void spawnEntities(Player player, Chunk chunk) {
        handle.spawnEntities(player, chunk);
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
        handle.untrackPlayer(player);
    }

    /**
     * Informs all entities of a new player viewer, sending possible spawn
     * packets
     *
     * @param player to update
     */
    public void updateViewer(Player player) {
        for (EntityTrackerEntryHandle entry : handle.getEntries()) {
            if (entry.getTracker().toBukkit() != player) {
                entry.updatePlayer(player);
            }
        }
    }

    /**
     * Adds an entity to this entity tracker, creating a new entity tracker
     * entry if needed
     *
     * @param entity to start tracking
     */
    public void startTracking(Entity entity) {
        handle.trackEntity(entity);
    }

    /**
     * Removes an entity from this entity tracker. This call will result in
     * entity destroy packets being sent to nearby players.
     *
     * @param entity to remove
     */
    public void stopTracking(Entity entity) {
        handle.untrackEntity(entity);
    }

    /**
     * Sets the entity tracker entry for an entity
     *
     * @param entity to set the tracker entry for
     * @param entityTrackerEntry to set to
     * @return previously set entity tracker entry, null if there was none
     */
    public EntityTrackerEntryHandle setEntry(Entity entity, EntityTrackerEntryHandle entityTrackerEntry) {
        EntityTrackerEntryHandle previous;
        final int id = entity.getEntityId();

        // Set in tracked entities map, replacing the original entry
        IntHashMap<?> trackedMap = handle.getTrackedEntities();
        previous = EntityTrackerEntryHandle.createHandle(trackedMap.remove(id));
        trackedMap.put(id, entityTrackerEntry);

        // Replace in entry set
        Set<EntityTrackerEntryHandle> trackers = handle.getEntries();
        trackers.remove(previous);
        trackers.add(entityTrackerEntry);
        return previous;
    }

    /**
     * Gets the entity tracker entry of an entity
     *
     * @param entity to get the entry of
     * @return entity tracker entry
     */
    public EntityTrackerEntryHandle getEntry(Entity entity) {
        return EntityTrackerEntryHandle.createHandle(this.handle.getTrackedEntities().get(entity.getEntityId()));
    }

    /**
     * Gets the entity tracker entry of an entity Id
     *
     * @param id of the entity to get the entry of
     * @return entity tracker entry
     */
    public EntityTrackerEntryHandle getEntry(int id) {
        return EntityTrackerEntryHandle.createHandle(this.handle.getTrackedEntities().get(id));
    }
}
