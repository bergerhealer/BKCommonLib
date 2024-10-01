package com.bergerkiller.bukkit.common.internal;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSignOpenEvent;

/**
 * Listener used on 1.20.2+ to detect the PlayerSignOpenEvent event.
 * This event is used to track whether a player has opened an edit dialog
 * for a sign.
 */
class CommonSignOpenListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onSignOpen(PlayerSignOpenEvent event) {
        if (event.getCause() != PlayerSignOpenEvent.Cause.PLACE) {
            CommonListener.editedSignBlocks.put(event.getPlayer(), event.getSign().getBlock());
        }
    }
}
