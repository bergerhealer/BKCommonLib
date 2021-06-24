package com.bergerkiller.bukkit.common.collections;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;

/**
 * Stores many entities that exist on worlds, mapped by entity id.
 * Is thread-safe.
 */
public class EntityByIdWorldMap {
    private final IntHashMap<EntitySlot> entitiesById = new IntHashMap<>();
    private final Set<EntitySlot> slots = new HashSet<>();
    private int markCounter = 0;

    /**
     * Gets the entity by entity id on a given world
     *
     * @param world World to look for the entity
     * @param entityId ID of the entity
     * @return Entity on the world with this entity id
     */
    public synchronized Entity get(World world, int entityId) {
        EntitySlot slot = this.entitiesById.get(entityId);
        while (slot != null) {
            if (slot.world == world) {
                return slot.entity;
            }
            slot = slot.next;
        }
        return null;
    }

    /**
     * Adds a single entity to this mapping. If an existing entity exists
     * by this id, it is overidden.
     *
     * @param world World the entity is on
     * @param entity The entity
     */
    public synchronized void add(World world, Entity entity) {
        addAndGetSlot(world, entity);
    }

    private EntitySlot addAndGetSlot(World world, Entity entity) {
        int id = entity.getEntityId();
        EntitySlot slot = this.entitiesById.get(id);
        if (slot == null) {
            slot = new EntitySlot(world, entity, id);
            this.entitiesById.put(id, slot);
            this.slots.add(slot);
            return slot;
        } else {
            while (true) {
                if (slot.world == world) {
                    slot.entity = entity;
                    return slot;
                }
                EntitySlot next = slot.next;
                if (next == null) {
                    break;
                } else {
                    slot = next;
                }
            }

            // Append a new entry to linked list
            slot.next = new EntitySlot(world, entity, id);
            slot = slot.next;
            this.slots.add(slot);
            return slot;
        }
    }

    /**
     * Removes an entry from this mapping.
     *
     * @param world World the entity was on
     * @param entity The entity
     */
    public synchronized void remove(World world, Entity entity) {
        int id = entity.getEntityId();
        EntitySlot slot = this.entitiesById.remove(id);
        if (slot != null) {
            // Most common case: only one slot for this one entity
            if (slot.world == world && slot.entity == entity) {
                this.slots.remove(slot);
                if (slot.next != null) {
                    this.entitiesById.put(id, slot.next);
                }
                return;
            }

            // First slot is not our slot, put it back in
            this.entitiesById.put(id,  slot);

            // Find it in other slots, and remove the slot from the linked
            // list if found.
            EntitySlot next = slot;
            while ((next = next.next) != null) {
                if (next.world == world && next.entity == entity) {
                    this.slots.remove(next);
                    slot.next = next.next;
                    break;
                }
            }
        }
    }

    /**
     * Removes all entities previously mapped to a given world
     *
     * @param world World to clear
     */
    public synchronized void clear(World world) {
        Iterator<EntitySlot> iter = this.slots.iterator();
        while (iter.hasNext()) {
            EntitySlot slot = iter.next();
            if (slot.world == world) {
                iter.remove();
                removeSlotFromMap(slot);
            }
        }
    }

    /**
     * Instantly synchronizes the mapping of entity id to entity
     * for all the entities currently on a world. Entities that
     * were mapped but don't exist on the world anymore are removed
     * from these mappings.
     *
     * @param world World
     */
    public synchronized void sync(World world) {
        // Add all entities and mark the slots we set
        int mark = ++markCounter;
        for (Entity entity : WorldUtil.getEntities(world)) {
            addAndGetSlot(world, entity).mark = mark;
        }

        // Slots of the same world with an out of date mark
        // did not exist on the world and will be removed.
        Iterator<EntitySlot> iter = this.slots.iterator();
        while (iter.hasNext()) {
            EntitySlot slot = iter.next();
            if (slot.world == world && slot.mark != mark) {
                iter.remove();
                removeSlotFromMap(slot);
            }
        }
    }

    private void removeSlotFromMap(EntitySlot slot) {
        EntitySlot root = this.entitiesById.remove(slot.entityId);
        if (root == slot) {
            // First entry is our entry, put back the next linked list item
            // if one exists. Usually not the case, so we're already done!
            if (root.next != null) {
                this.entitiesById.put(slot.entityId, root.next);
            }
        } else {
            // Not the first entry, re-add it and go down the linked list chain
            // to find the slot, and remove it if found.
            this.entitiesById.put(slot.entityId, root);

            EntitySlot next;
            while ((next = root.next) != null) {
                if (next == slot) {
                    root.next = slot.next;
                    break;
                } else {
                    root = next;
                }
            }
        }
    }

    /**
     * Stores the entity/entities of a given ID.
     * Also stores the world it was stored in for verification.
     * If one entity id refers to different entities on different worlds,
     * then this slot acts as a linked list to support that.
     */
    private static final class EntitySlot {
        public final World world;
        public Entity entity;
        public final int entityId;
        public int mark;
        public EntitySlot next;

        public EntitySlot(World world, Entity entity, int entityId) {
            this.world = world;
            this.entity = entity;
            this.entityId = entityId;
            this.mark = 0;
            this.next = null;
        }
    }
}
