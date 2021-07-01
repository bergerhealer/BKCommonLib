package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;

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

    // Disabled: this method doesn't work like how it used to on 1.14
    ///**
    // * Sends spawn packets to the player for all entities contained in the chunk
    // *
    // * @param player to send spawn packets to
    // * @param chunk containing the entities to update
    // */
    //public void spawnEntities(Player player, Chunk chunk) {
    //    handle.spawnEntities(player, chunk);
    //}

    /**
     * Removes a player viewer, resulting in all entities being removed for the
     * viewer. If the player is still referenced in the entity tracker in some
     * way, the entities will be re-sent at a later time automatically. This
     * method acts as a 'total respawn' system for that reason.
     *
     * @param player to remove from this Entity Tracker
     */
    public void removeViewer(Player player) {
        for (EntityTrackerEntryHandle entry : handle.getEntries()) {
            entry.removeViewer(player);
        }
    }

    /**
     * Informs all entities of a new player viewer, sending possible spawn
     * packets
     *
     * @param player to update
     */
    public void updateViewer(Player player) {
        for (EntityTrackerEntryHandle entry : handle.getEntries()) {
            if (entry.getEntity().getBukkitEntity() != player) {
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
        // On PaperSpigot, the entry is also stored in the entity itself
        if (EntityHandle.T.tracker.isAvailable()) {
            EntityHandle.T.tracker.set(HandleConversion.toEntityHandle(entity), entityTrackerEntry);
        }

        return handle.putEntry(entity.getEntityId(), entityTrackerEntry);
    }
    
    /**
     * Gets the entity tracker entry of an entity
     *
     * @param entity to get the entry of
     * @return entity tracker entry
     */
    public EntityTrackerEntryHandle getEntry(Entity entity) {
        return getEntry(entity.getEntityId());
    }

    /**
     * Gets the entity tracker entry of an entity Id
     *
     * @param entityId of the entity to get the entry of
     * @return entity tracker entry
     */
    public EntityTrackerEntryHandle getEntry(int entityId) {
        return this.handle.getEntry(entityId);
    }

    /**
     * Removes and gets the entity tracker entry of an entity Id
     *
     * @param entityId of the entity to remove the entry of
     * @return entity tracker entry that was removed, or null if not found
     */
    public EntityTrackerEntryHandle removeEntry(int entityId) {
        return this.handle.putEntry(entityId, null);
    }

    /**
     * Checks whether a certain entry is contained and managed by this tracker
     * 
     * @param entityTrackerEntry
     * @return True if the entity tracker entry is contained
     */
    public boolean containsEntry(EntityTrackerEntryHandle entityTrackerEntry) {
        return this.handle.getEntries().contains(entityTrackerEntry);
    }
}
