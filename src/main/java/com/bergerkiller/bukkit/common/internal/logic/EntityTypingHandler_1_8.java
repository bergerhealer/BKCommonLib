package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook_1_8_to_1_13_2;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

class EntityTypingHandler_1_8 extends EntityTypingHandler {
    private final WorldServerHandle dummyTrackerWorld;
    private final EntityTrackerHandle dummyTracker;
    private final IntHashMap<Object> entriesMap;
    private final Collection<Object> entries;
    private final FastMethod<Object> fallbackConstructor;

    public EntityTypingHandler_1_8() {
        // Initialize a dummy world, whose only use is providing access to the 'players' field.
        // This field is used by the EntityTracker to scan for players
        // We explicitly set this to an empty list to guarantee no spawn packets are produced
        this.dummyTrackerWorld = WorldServerHandle.T.newHandleNull();
        SafeField.create(WorldHandle.T.getType(), "players", List.class).set(this.dummyTrackerWorld.getRaw(), Collections.emptyList());

        // Initialize the dummy tracker without calling any methods/constructors
        this.entriesMap = new IntHashMap<Object>();
        this.dummyTracker = EntityTrackerHandle.T.newHandleNull();
        this.entries = tryInitEntries(this.dummyTracker.getRaw());
        EntityTrackerHandle.T.setWorld.raw.invoke(dummyTracker.getRaw(), dummyTrackerWorld.getRaw());
        SafeField.set(this.dummyTracker.getRaw(), "trackedEntities", this.entriesMap.getRawHandle());

        // Find the fallback constructor for EntityTrackerEntry if track() fails to create one
        this.fallbackConstructor = new FastMethod<Object>();
        {
            ClassResolver resolver = new ClassResolver();
            resolver.setPackage("net.minecraft.server.level");
            resolver.setDeclaredClass(EntityTrackerEntryStateHandle.T.getType());
            MethodDeclaration fallbackConstructorMethod = new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                    "public static EntityTrackerEntry createNew(Entity entity, int viewDistance, int playerViewDistance, int updateInterval, boolean isMobile) {\n" +
                    "#if exists net.minecraft.server.level.EntityTrackerEntry public EntityTrackerEntry(net.minecraft.world.entity.Entity entity, int viewDistance, int playerViewDistance, int updateInterval, boolean isMobile);\n" +
                    "    return new EntityTrackerEntry(entity, viewDistance, playerViewDistance, updateInterval, isMobile);\n" +
                    "#else\n" +
                    "    return new EntityTrackerEntry(entity, viewDistance, updateInterval, isMobile);\n" +
                    "#endif\n" +
                    "}"
            ));
            this.fallbackConstructor.init(fallbackConstructorMethod);  
        }
    }

    @SuppressWarnings("unchecked")
    private static Collection<Object> tryInitEntries(Object instance) {
        try {
            Field entriesField = EntityTrackerHandle.T.getType().getDeclaredField("c");
            entriesField.setAccessible(true);
            Collection<Object> result;
            if (entriesField.getType().equals(Set.class) || entriesField.getType().equals(HashSet.class)) {
                result = new HashSet<>();
            } else if (entriesField.getType().equals(List.class)) {
                result = new ArrayList<>();
            } else if (entriesField.getType().getName().equals("me.rastrian.dev.utils.IndexedLinkedHashSet")) {
                result = (Collection<Object>) entriesField.getType().getConstructor().newInstance();
            } else {
                throw new UnsupportedOperationException("Unsupported entries set type: " + entriesField.getType());
            }

            entriesField.set(instance, result);
            return result;
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find EntityTracker flat entries 'c' set", t);
            return new ArrayList<>();
        }
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public Class<?> getClassFromEntityTypes(Object nmsEntityTypesInstance) {
        return null; // EntityTypes instances don't exist, so we can ignore this method
    }

    @Override
    public EntityTrackerEntryHandle createEntityTrackerEntry(EntityTracker entityTracker, Entity entity) {
        EntityTrackerEntryHandle createdEntry = null;
        try {
            // Reset
            this.entries.clear();
            this.entriesMap.clear();

            // Track it!
            dummyTracker.setTrackingDistance((Bukkit.getViewDistance()-1) * 16);
            dummyTracker.trackEntity(entity);

            // Retrieve it from the mapping
            List<IntHashMap.Entry<Object>> entries = this.entriesMap.entries();
            if (!entries.isEmpty()) {
                createdEntry = EntityTrackerEntryHandle.createHandle(entries.get(0).getValue());
            } else {
                Logging.LOGGER_REFLECTION.once(Level.WARNING, "No dummy entry created for " + entity.getName() + ", resolving to defaults");
            }
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.once(Level.SEVERE, "Failed to create dummy entry", t);
        }
        if (createdEntry == null) {
            Object nmsEntity = HandleConversion.toEntityHandle(entity);
            createdEntry = EntityTrackerEntryHandle.createHandle(this.fallbackConstructor.invoke(null,
                    nmsEntity, 80, (Bukkit.getViewDistance()-1) * 16, 3, true)); // defaults
        }

        // Only on MC >= 1.10.2
        // Bugfix: Add all current passengers to the passengers field right now
        // We must do this so that the next updatePlayer() update is properly synchronized
        if (EntityTrackerEntryStateHandle.T.opt_passengers.isAvailable()) {
            EntityTrackerEntryStateHandle.T.opt_passengers.set(createdEntry.getRaw(), (new ExtendedEntity<Entity>(entity)).getPassengers());
        }

        // Only on MC <= 1.8.8
        // Bugfix: Add the current vehicle to the vehicle field right now
        // We must do this so that the next updatePlayer() update is properly synchronized
        if (EntityTrackerEntryStateHandle.T.opt_vehicle.isAvailable()) {
            EntityTrackerEntryStateHandle.T.opt_vehicle.set(createdEntry.getRaw(), entity.getVehicle());
        }

        return createdEntry;
    }

    @Override
    public EntityTrackerEntryHook getEntityTrackerEntryHook(Object entityTrackerEntryHandle) {
        return EntityTrackerEntryHook_1_8_to_1_13_2.get(entityTrackerEntryHandle, EntityTrackerEntryHook_1_8_to_1_13_2.class);
    }

    @Override
    public Object hookEntityTrackerEntry(Object entityTrackerEntryHandle) {
        return new EntityTrackerEntryHook_1_8_to_1_13_2().hook(entityTrackerEntryHandle);
    }
}
