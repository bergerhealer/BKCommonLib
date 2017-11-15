package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.generated.net.minecraft.server.AttributeMapServerHandle;
import com.bergerkiller.generated.net.minecraft.server.ChunkHandle;
import com.bergerkiller.generated.net.minecraft.server.DedicatedPlayerListHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHumanHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityItemHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityLivingHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerHandle;
import com.bergerkiller.generated.net.minecraft.server.ItemHandle;
import com.bergerkiller.generated.net.minecraft.server.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftServerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Contains utility functions to get to the net.minecraft.server core in the
 * CraftBukkit library.<br>
 * This Class should only be used internally by BKCommonLib, as it exposes NMS
 * and CraftBukkit types.<br>
 * Where possible, methods in this Class will delegate to Conversion
 * constants.<br>
 * Do NOT use these methods in your converters, it might fail with stack
 * overflow exceptions.
 */
public class CommonNMS {
    private static WorldServerHandle dummyTrackerWorld = null; // lazy-initialized once
    private static EntityTrackerHandle dummyTracker = null; // lazy-initialized once

    public static boolean isItemEmpty(Object rawItemStackHandle) {
        if (rawItemStackHandle == null) {
            return true;
        }
        if (ItemStackHandle.T.isEmpty.isAvailable()) {
            return ItemStackHandle.T.isEmpty.invoke(rawItemStackHandle);
        } else {
            return false;
        }
    }

    public static ChunkHandle getHandle(org.bukkit.Chunk chunk) {
        return ChunkHandle.createHandle(HandleConversion.toChunkHandle(chunk));
    }

    public static ItemStackHandle getHandle(org.bukkit.inventory.ItemStack stack) {
        return ItemStackHandle.createHandle(Conversion.toItemStackHandle.convert(stack));
    }

    public static EntityHandle getHandle(org.bukkit.entity.Entity entity) {
        return getHandle(entity, EntityHandle.T);
    }

    public static EntityItemHandle getHandle(org.bukkit.entity.Item item) {
        return getHandle(item, EntityItemHandle.T);
    }

    public static EntityLivingHandle getHandle(LivingEntity l) {
        return getHandle(l, EntityLivingHandle.T);
    }

    public static EntityHumanHandle getHandle(HumanEntity h) {
        return getHandle(h, EntityHumanHandle.T);
    }

    public static EntityPlayerHandle getHandle(Player p) {
        return getHandle(p, EntityPlayerHandle.T);
    }

    public static Object getRawHandle(org.bukkit.entity.Entity e, Template.Class<?> type) {
        return CommonUtil.tryCast(Conversion.toEntityHandle.convert(e), type.getType());
    }

    public static <T extends Template.Handle> T getHandle(org.bukkit.entity.Entity e, Template.Class<T> type) {
        Object rawInstance = HandleConversion.toEntityHandle(e);
        if (type.isAssignableFrom(rawInstance)) {
            return type.createHandle(rawInstance);
        } else {
            return null;
        }
    }

    public static WorldServerHandle getHandle(org.bukkit.World world) {
        return WorldServerHandle.createHandle(HandleConversion.toWorldHandle(world));
    }

    public static ItemHandle getItem(org.bukkit.Material material) {
        return material == null ? null : ItemHandle.createHandle(HandleConversion.toItemHandle(material));
    }

    /**
     * Gets the native Minecraft Server which contains the main logic
     *
     * @return Minecraft Server
     */
    public static MinecraftServerHandle getMCServer() {
        return MinecraftServerHandle.instance();
    }

    /**
     * Gets the native Minecraft Server Player List, which keeps track of
     * player-related information
     *
     * @return Minecraft Server Player List
     */
    public static DedicatedPlayerListHandle getPlayerList() {
        return CraftServerHandle.instance().getPlayerList();
    }

    public static AttributeMapServerHandle getEntityAttributes(org.bukkit.entity.LivingEntity entity) {
        return EntityLivingHandle.T.getAttributeMap.invoke(HandleConversion.toEntityHandle(entity));
    }

    /**
     * Creates an entity tracker entry with the right configuration without actually registering it inside the server.
     * This allows reading the network configuration such as view distance and update interval for an Entity.
     * 
     * @param entity to create a dummy EntityTrackerEntry for
     * @return dummy EntityTrackerEntry
     */
    @SuppressWarnings("unchecked")
    public static EntityTrackerEntryHandle createDummyTrackerEntry(Entity entity) {
        EntityTrackerEntryHandle createdEntry = null;
        try {
            // Initialize a dummy world, whose only use is providing access to the 'players' field.
            // This field is used by the EntityTracker to scan for players
            // We explicitly set this to an empty list to guarantee no spawn packets are produced
            if (dummyTrackerWorld == null) {
                dummyTrackerWorld = WorldServerHandle.T.newHandleNull();
                WorldHandle.T.players.raw.set(dummyTrackerWorld.getRaw(), Collections.emptyList());
            }

            // Initialize the dummy tracker without calling any methods/constructors
            if (dummyTracker == null) {
                dummyTracker = EntityTrackerHandle.T.newHandleNull();
                EntityTrackerHandle.T.entries.raw.set(dummyTracker.getRaw(), new HashSet<Object>());
                EntityTrackerHandle.T.world.raw.set(dummyTracker.getRaw(), dummyTrackerWorld.getRaw());
                dummyTracker.setTrackedEntities(new IntHashMap<Object>());
            }
            dummyTracker.getEntries().clear();

            // Track it!
            dummyTracker.setViewDistance((Bukkit.getViewDistance()-1) * 16);
            IntHashMap<?> tracked = dummyTracker.getTrackedEntities();
            tracked.clear();
            dummyTracker.trackEntity(entity);

            // Retrieve it from the mapping
            List<IntHashMap.Entry<Object>> entries = ((IntHashMap<Object>) tracked).entries();
            if (!entries.isEmpty()) {
                createdEntry = EntityTrackerEntryHandle.createHandle(entries.get(0).getValue());
            } else {
                Logging.LOGGER_REFLECTION.once(Level.WARNING, "No dummy entry created for " + entity.getName() + ", resolving to defaults");
            }
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.once(Level.SEVERE, "Failed to create dummy entry", t);
        }
        if (createdEntry == null) {
            createdEntry = EntityTrackerEntryHandle.createNew(entity, 80, (Bukkit.getViewDistance()-1) * 16, 3, true); // defaults
        }

        // Only on MC >= 1.10.2
        // Bugfix: Add all current passengers to the passengers field right now
        // We must do this so that the next updatePlayer() update is properly synchronized
        if (EntityTrackerEntryHandle.T.opt_passengers.isAvailable()) {
            EntityTrackerEntryHandle.T.opt_passengers.set(createdEntry.getRaw(), (new ExtendedEntity<Entity>(entity)).getPassengers());
        }

        // Only on MC <= 1.8.8
        // Bugfix: Add the current vehicle to the vehicle field right now
        // We must do this so that the next updatePlayer() update is properly synchronized
        if (EntityTrackerEntryHandle.T.opt_vehicle.isAvailable()) {
            EntityTrackerEntryHandle.T.opt_vehicle.set(createdEntry.getRaw(), entity.getVehicle());
        }

        return createdEntry;
    }
}
