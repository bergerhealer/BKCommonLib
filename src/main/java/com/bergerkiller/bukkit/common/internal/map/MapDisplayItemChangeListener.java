package com.bergerkiller.bukkit.common.internal.map;

import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Listens for various events that indicate a change to an item frame
 * has occurred that may require checking if the item inside has changed.
 */
class MapDisplayItemChangeListener implements Listener {
    private final CommonMapController controller;

    public MapDisplayItemChangeListener(CommonMapController controller) {
        this.controller = controller;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame) {
            controller.updateItemFrame(event.getRightClicked().getEntityId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof ItemFrame) {
            controller.updateItemFrame(event.getEntity().getEntityId());
        }
    }
}
