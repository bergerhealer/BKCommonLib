package com.bergerkiller.bukkit.common.internal.map;

import java.util.UUID;

import com.bergerkiller.bukkit.common.entity.PlayerInstancePhase;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.SortedIdentityCache;
import com.bergerkiller.bukkit.common.events.map.MapShowEvent;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;

/**
 * Continuously checks if a map item is being held by a player
 */
class MapDisplayHeldMapUpdater extends Task {
    private final CommonMapController controller;
    private final SortedIdentityCache<Player, MapViewEntry> entries = SortedIdentityCache.create(MapViewEntry::new);

    public MapDisplayHeldMapUpdater(JavaPlugin plugin, CommonMapController controller) {
        super(plugin);
        this.controller = controller;
    }

    @Override
    public void run() {
        entries.sync(PlayerInstancePhase.getAlivePlayers());
        entries.forEach(MapViewEntry::update);
    }

    private class MapViewEntry {
        public final Player player;
        public ItemStack lastLeftHand = null;
        public ItemStack lastRightHand = null;

        public MapViewEntry(Player player) {
            this.player = player;
        }

        public void update() {
            ItemStack currLeftHand = PlayerUtil.getItemInHand(this.player, HumanHand.LEFT);
            ItemStack currRightHand = PlayerUtil.getItemInHand(this.player, HumanHand.RIGHT);

            if (CommonMapUUIDStore.isMap(currLeftHand) 
                    && !mapEquals(currLeftHand, lastLeftHand) 
                    && !mapEquals(currLeftHand, lastRightHand)) {
                // Left hand now has a map! We did not swap hands, either.
                controller.handleMapShowEvent(new MapShowEvent(player, HumanHand.LEFT, currLeftHand));
            }
            if (CommonMapUUIDStore.isMap(currRightHand) 
                    && !mapEquals(currRightHand, lastRightHand) 
                    && !mapEquals(currRightHand, lastLeftHand)) {
                // Right hand now has a map! We did not swap hands, either.
                controller.handleMapShowEvent(new MapShowEvent(player, HumanHand.RIGHT, currRightHand));
            }

            lastLeftHand = currLeftHand;
            lastRightHand = currRightHand;
        }

        private final boolean mapEquals(ItemStack item1, ItemStack item2) {
            UUID mapUUID1 = CommonMapUUIDStore.getMapUUID(item1);
            UUID mapUUID2 = CommonMapUUIDStore.getMapUUID(item2);
            return mapUUID1 != null && mapUUID2 != null && mapUUID1.equals(mapUUID2);
        }
    }
}
