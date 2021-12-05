package com.bergerkiller.bukkit.common.internal.logic;

import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;

public abstract class EntityTypingHandler implements LibraryComponent {
    public static final EntityTypingHandler INSTANCE = LibraryComponentSelector.forModule(EntityTypingHandler.class)
            .runFirst(CommonBootstrap::initServer)
            .addVersionOption(null, "1.12.2", EntityTypingHandler_1_8::new)
            .addVersionOption("1.13", "1.13.2", EntityTypingHandler_1_13::new)
            .addVersionOption("1.14", null, EntityTypingHandler_1_14::new)
            .update();

    /**
     * Looks up an Entity Tracker Entry hook for network controllers, given an Entity Tracker Entry instance
     * 
     * @param entityTrackerEntryHandle
     * @return Entity Tracker Entry Hook, if available
     */
    public abstract EntityTrackerEntryHook getEntityTrackerEntryHook(Object entityTrackerEntryHandle);

    /**
     * Creates a new EntityTrackerEntryHook wrapping around an entry
     * 
     * @param entityTrackerEntryHandle
     * @return hooked entry
     */
    public abstract Object hookEntityTrackerEntry(Object entityTrackerEntryHandle);

    /**
     * Creates an entity tracker entry with the right configuration without actually registering it inside the server.
     * This allows reading the network configuration such as view distance and update interval for an Entity.
     * 
     * @param entityTracker for which to create an entry
     * @param entity to create an EntityTrackerEntry for
     * @return EntityTrackerEntry
     */
    public abstract EntityTrackerEntryHandle createEntityTrackerEntry(EntityTracker entityTracker, Entity entity);

    /**
     * Obtains the Class of the entity that spawns for an EntityTypes instance.
     * Implementation callback.
     * 
     * @param nmsEntityTypesInstance
     * @return NMS Entity Class
     */
    public abstract Class<?> getClassFromEntityTypes(Object nmsEntityTypesInstance);
}
