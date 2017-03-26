package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.List;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.reflection.ClassHook;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntity;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityTrackerEntry;

public class EntityTrackerHook extends ClassHook<EntityTrackerHook> {
    private EntityNetworkController<?> controller;

    public EntityNetworkController<?> getController() {
        return controller;
    }

    public void setController(EntityNetworkController<?> controller) {
        this.controller = controller;
    }

    @HookMethod("public void scanPlayers(List list)")
    public void scanPlayers(List<?> list) {
        base.scanPlayers(list);
    }

    @HookMethod("public void track(List list)")
    public void track(List<?> list) {
        Object handle = instance();
        updateTrackers(list);
        NMSEntityTrackerEntry.timeSinceLocationSync.set(handle, NMSEntityTrackerEntry.timeSinceLocationSync.get(handle) + 1);
        try {
            controller.onSync();
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to synchronize:");
            t.printStackTrace();
        }
        NMSEntityTrackerEntry.tickCounter.set(handle, NMSEntityTrackerEntry.tickCounter.get(handle) + 1);
    }

    @HookMethod("public void a()")
    public void hideForAll() {
        try {
            controller.makeHiddenForAll();
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to hide for all viewers:");
            t.printStackTrace();
        }
    }

    @HookMethod("public void clear(EntityPlayer entityplayer)")
    public void clear(Object entityplayer) {
        try {
            controller.removeViewer(Conversion.toPlayer.convert(entityplayer));
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to remove viewer:");
            t.printStackTrace();
        }
    }

    @HookMethod("public void a(EntityPlayer entityplayer)")
    public void removeViewer(Object entityplayer) {
        try {
            controller.removeViewer(Conversion.toPlayer.convert(entityplayer));
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to remove viewer:");
            t.printStackTrace();
        }
    }

    @HookMethod("public void updatePlayer(EntityPlayer entityplayer)")
    public void updatePlayer(Object entityplayer) {
        if (entityplayer != controller.getEntity().getHandle()) {
            try {
                controller.updateViewer(Conversion.toPlayer.convert(entityplayer));
            } catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to update viewer:");
                t.printStackTrace();
            }
        }
    }

    private void updateTrackers(List<?> list) {
        Object entityHandle = controller.getEntity().getHandle();
        Object handle = instance();
        if (NMSEntityTrackerEntry.synched.get(handle)) {
            double lastSyncX = NMSEntityTrackerEntry.prevX.get(handle);
            double lastSyncY = NMSEntityTrackerEntry.prevY.get(handle);
            double lastSyncZ = NMSEntityTrackerEntry.prevZ.get(handle);
            double distance = NMSEntity.calculateDistance.invoke(entityHandle, lastSyncX, lastSyncY, lastSyncZ);
            if (distance <= 16.0) {
                return;
            }
        }
        // Update tracking data
        NMSEntityTrackerEntry.prevX.set(handle, NMSEntity.locX.get(entityHandle));
        NMSEntityTrackerEntry.prevY.set(handle, NMSEntity.locY.get(entityHandle));
        NMSEntityTrackerEntry.prevZ.set(handle, NMSEntity.locZ.get(entityHandle));
        NMSEntityTrackerEntry.synched.set(handle, true);
        scanPlayers(list);
    }
}
