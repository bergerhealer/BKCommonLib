package com.bergerkiller.bukkit.common.internal.map;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.map.MapDisplayTile;
import com.bergerkiller.bukkit.common.map.MapSession;
import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;
import com.bergerkiller.bukkit.common.map.util.MapUUID;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.EntityItemFrameHandle;
import com.google.common.collect.SetMultimap;

class MapDisplayItemMapIdUpdater extends Task {
    private final CommonMapController controller;

    // This counter is incremented every time a new map Id is added to the mapping
    // Every 1000 map ids we do a cleanup to free up slots for maps that no longer exist on the server
    // This is required, otherwise we can run out of the 32K map Ids we have available given enough uptime
    private static final int GENERATION_COUNTER_CLEANUP_INTERVAL = 1000;

    public MapDisplayItemMapIdUpdater(JavaPlugin plugin, CommonMapController controller) {
        super(plugin);
        this.controller = controller;
    }

    @Override
    public void run() {
        synchronized (controller) {
            updateMapIds();
        }
    }

    public void updateMapIds() {
        // Remove non-existing maps from the internal mapping
        if (controller.idGenerationCounter > GENERATION_COUNTER_CLEANUP_INTERVAL) {
            controller.idGenerationCounter = 0;

            // Find all map UUIDs that exist on the server
            HashSet<MapUUID> validUUIDs = new HashSet<MapUUID>();
            for (Set<EntityItemFrameHandle> itemFrameSet : controller.itemFrameEntities.values()) {
                for (EntityItemFrameHandle itemFrame : itemFrameSet) {
                    MapUUID mapUUID = controller.getItemFrameMapUUID(itemFrame);
                    if (mapUUID != null) {
                        validUUIDs.add(mapUUID);
                    }
                }
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack item = inv.getItem(i);
                    UUID mapUUID = CommonMapUUIDStore.getMapUUID(item);
                    if (mapUUID != null) {
                        validUUIDs.add(new MapUUID(mapUUID));
                    }
                }
            }

            // Perform the cleanup (synchronized access required!)
            controller.cleanupUnusedUUIDs(validUUIDs);
        }

        // Refresh items known to clients when Map Ids are re-assigned
        // Swap around the tmp and main set every tick
        final SetMultimap<UUID, MapUUID> dirtyMaps = controller.swapDirtyMapUUIDs();
        if (!dirtyMaps.isEmpty()) {
            // Refresh all player inventories that contain this map
            // This will result in new SetItemSlot packets being sent, refreshing the map Id
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack item = inv.getItem(i);
                    UUID uuid = CommonMapUUIDStore.getMapUUID(item);
                    if (dirtyMaps.containsKey(uuid)) {
                        inv.setItem(i, item.clone());
                    }
                }
            }

            // Refresh all item frames that display this map
            // This will result in a new EntityMetadata packets being sent, refreshing the map Id
            // After updating all item frames, resend the maps
            dirtyMaps.keySet().stream()
                .map(controller.maps::get)
                .filter(Objects::nonNull)
                .forEach(info -> {
                    // Refresh item of all affected item frames
                    // This re-sends metadata packets
                    final Set<MapUUID> mapUUIDs = dirtyMaps.get(info.getUniqueId());
                    for (ItemFrameInfo itemFrameInfo : info.getItemFrames()) {
                        if (mapUUIDs.contains(itemFrameInfo.lastMapUUID)) {
                            itemFrameInfo.itemFrameHandle.refreshItem();
                        }
                    }

                    // Resend map data for all affected tiles
                    for (MapSession session : info.getSessions()) {
                        for (MapDisplayTile tile : session.tiles) {
                            if (mapUUIDs.contains(tile.getMapTileUUID())) {
                                session.onlineOwners.forEach(o -> o.sendDirtyTile(tile));
                            }
                        }
                    }
                });

            // Done processing, wipe
            dirtyMaps.clear();
        }
    }
}
