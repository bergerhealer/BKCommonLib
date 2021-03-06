package com.bergerkiller.bukkit.common.internal.logic;

import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;

public abstract class EntityTypingHandler {
    public static final EntityTypingHandler INSTANCE = createInstance();

    private static EntityTypingHandler createInstance() {
        CommonBootstrap.initServer();
        try {
            if (Common.evaluateMCVersion(">=", "1.14")) {
                return new EntityTypingHandler_1_14();
            } else if (Common.evaluateMCVersion(">=", "1.13")) {
                return new EntityTypingHandler_1_13();
            } else {
                return new EntityTypingHandler_1_8();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

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
