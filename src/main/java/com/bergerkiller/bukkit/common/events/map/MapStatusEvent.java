package com.bergerkiller.bukkit.common.events.map;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Event broadcast by widgets, allowing other widgets to be informed of status changes
 */
public class MapStatusEvent {
    private final String name;
    private final Object argument;

    public MapStatusEvent(String name, Object argument) {
        this.name = name;
        this.argument = argument;
    }

    /**
     * Gets the name of the status that changed
     * 
     * @return status name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the argument of this status event
     * 
     * @return argument
     */
    public Object getArgument() {
        return this.argument;
    }

    /**
     * Gets the argument of this status event, attempting a cast to a certain type.
     * If the casting fails, null is returned instead.
     * 
     * @param type
     * @return argument cast to the type
     */
    public <T> T getArgument(Class<T> type) {
        return CommonUtil.tryCast(this.argument, type);
    }
}
