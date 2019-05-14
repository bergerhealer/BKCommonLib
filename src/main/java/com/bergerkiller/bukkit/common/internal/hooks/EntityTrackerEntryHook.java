package com.bergerkiller.bukkit.common.internal.hooks;

import com.bergerkiller.bukkit.common.controller.EntityNetworkController;

public interface EntityTrackerEntryHook {
    public EntityNetworkController<?> getController();
    public void setController(EntityNetworkController<?> controller);
}
