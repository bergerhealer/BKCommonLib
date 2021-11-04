package com.bergerkiller.bukkit.common.internal.map;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.events.map.MapAction;
import com.bergerkiller.bukkit.common.events.map.MapClickEvent;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.util.MapLookPosition;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Result of a findLookingAt() operation
 */
class LookAtSearchResult {
    public final MapDisplay display;
    public final MapLookPosition lookPosition;

    public LookAtSearchResult(MapDisplay display, MapLookPosition lookPosition) {
        this.display = display;
        this.lookPosition = lookPosition;
    }

    /**
     * Handles the click event for this looking-at information
     *
     * @param player
     * @param action
     * @return Event result, can check {@link MapClickEvent#isCancelled()}
     */
    public MapClickEvent click(Player player, MapAction action) {
        // Fire event
        MapClickEvent event = new MapClickEvent(player, this.lookPosition, this.display, action);
        CommonUtil.callEvent(event);
        if (!event.isCancelled()) {
            if (action == MapAction.LEFT_CLICK) {
                event.getDisplay().onLeftClick(event);
                event.getDisplay().getRootWidget().onLeftClick(event);
            } else {
                event.getDisplay().onRightClick(event);
                event.getDisplay().getRootWidget().onRightClick(event);
            }
        }
        return event;
    }
}
