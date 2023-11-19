package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.logging.Level;

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

    /**
     * Helper method: initializes a recursive Paper ConfigurationPart tree of configuration entries
     * to its default values.
     *
     * @param config Root config
     */
    public static void initConfigurationPartRecurse(Object config) {
        Class<?> configurationPartType;
        try {
            configurationPartType = Class.forName("io.papermc.paper.configuration.ConfigurationPart");
        } catch (ClassNotFoundException e) {
            return;
        }

        // Go by all declared fields and if they are an instance of ConfigurationPart, and currently
        // not set to a value, try to construct the class type using an empty constructor.
        // If successful, recurse.
        for (Field f : config.getClass().getFields()) {
            Class<?> fieldType = f.getType();
            if (!(configurationPartType.isAssignableFrom(fieldType))) {
                continue;
            }
            try {
                f.setAccessible(true);
                if (f.get(config) != null) {
                    continue;
                }
            } catch (Throwable t) {
                continue;
            }

            Constructor<?> ctor_noarg = null;
            Constructor<?> ctor_parentarg = null;
            try {
                ctor_noarg = fieldType.getConstructor();
            } catch (Throwable t) {}
            try {
                ctor_parentarg = fieldType.getConstructor(config.getClass());
            } catch (Throwable t) {}
            if (ctor_noarg == null && ctor_parentarg == null) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find constructor for " + fieldType.getName());
                continue;
            }

            Object childConfigPart;
            try {
                if (ctor_noarg != null) {
                    childConfigPart = ctor_noarg.newInstance();
                } else if (ctor_parentarg != null) {
                    childConfigPart = ctor_parentarg.newInstance(config);
                } else {
                    continue;
                }
                f.set(config, childConfigPart);
            } catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to construct field " + f.getName() + " type " + fieldType.getName(), t);
                continue;
            }

            // Recurse
            initConfigurationPartRecurse(childConfigPart);
        }
    }
}
