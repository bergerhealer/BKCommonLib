package com.bergerkiller.bukkit.common.map;

/**
 * A mode of propagating an event through a map widget hierarchy
 */
public enum MapEventPropagation {
    /**
     * Sends the event to this widget, and then to the child widgets, recursively.
     * This uses the 'capturing' method of event propagation.
     */
    UPSTREAM,
    /**
     * Sends the event to this widget, and then to the parent widgets, recursively.
     * This uses the 'bubbling' method of event propagation.
     * At the very end, the map display also gets to handle the event.
     */
    DOWNSTREAM,
    /**
     * Broadcasts the event to all widgets attached to the display, starting at the very root
     * working upwards. At the very end, the map display also gets to handle the event.
     */
    BROADCAST
}
