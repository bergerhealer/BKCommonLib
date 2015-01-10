package com.bergerkiller.bukkit.common.entity.nms;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.entity.Entity;

import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.EntityTrackerEntry;

import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerEntryRef;

public class NMSEntityTrackerEntry extends EntityTrackerEntry {

    private EntityNetworkController<?> controller;

    /**
     * Initializes a new Entity Tracker Entry hook
     *
     * @param entity that this tracker entry belongs to
     */
    public NMSEntityTrackerEntry(final Entity entity) {
        super(CommonNMS.getNative(entity), 80, 3, true);
        // Fix these two: Wrongly set in Constructor
        //this.xLoc = tracker.as.a(tracker.locX);
        //this.zLoc = tracker.as.a(tracker.locZ);
        // Set proper update interval/viewdistance/mobile
        final CommonEntityType type = CommonEntityType.byNMSEntity(tracker);
        EntityTrackerEntryRef.isMobile.set(this, type.networkIsMobile);
        EntityTrackerEntryRef.updateInterval.set(this, type.networkUpdateInterval);
        EntityTrackerEntryRef.viewDistance.set(this, type.networkViewDistance);
    }

    public EntityNetworkController<?> getController() {
        return controller;
    }

    public void setController(EntityNetworkController<?> controller) {
        this.controller = controller;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void track(List list) {
        updateTrackers(list);
        EntityTrackerEntryRef.timeSinceLocationSync.set(this, EntityTrackerEntryRef.timeSinceLocationSync.get(this) + 1);
        try {
            controller.onSync();
        } catch (Throwable t) {
            CommonPlugin.LOGGER_NETWORK.log(Level.SEVERE, "Failed to synchronize:");
            t.printStackTrace();
        }
        this.m++;
    }

    @SuppressWarnings("rawtypes")
    private void updateTrackers(List list) {
        if (EntityTrackerEntryRef.synched.get(this)) {
            double lastSyncX = EntityTrackerEntryRef.prevX.get(this);
            double lastSyncY = EntityTrackerEntryRef.prevY.get(this);
            double lastSyncZ = EntityTrackerEntryRef.prevZ.get(this);
            if (tracker.e(lastSyncX, lastSyncY, lastSyncZ) <= 16.0) {
                return;
            }
        }
        // Update tracking data
        EntityTrackerEntryRef.prevX.set(this, tracker.locX);
        EntityTrackerEntryRef.prevY.set(this, tracker.locY);
        EntityTrackerEntryRef.prevZ.set(this, tracker.locZ);
        EntityTrackerEntryRef.synched.set(this, true);
        this.scanPlayers(list);
    }

    @Override
    public void a() {
        try {
            controller.makeHiddenForAll();
        } catch (Throwable t) {
            CommonPlugin.LOGGER_NETWORK.log(Level.SEVERE, "Failed to hide for all viewers:");
            t.printStackTrace();
        }
    }

    @Override
    public void clear(EntityPlayer entityplayer) {
        try {
            controller.removeViewer(CommonNMS.getPlayer(entityplayer));
        } catch (Throwable t) {
            CommonPlugin.LOGGER_NETWORK.log(Level.SEVERE, "Failed to remove viewer:");
            t.printStackTrace();
        }
    }

    @Override
    public void a(EntityPlayer entityplayer) {
        try {
            controller.removeViewer(CommonNMS.getPlayer(entityplayer));
        } catch (Throwable t) {
            CommonPlugin.LOGGER_NETWORK.log(Level.SEVERE, "Failed to remove viewer:");
            t.printStackTrace();
        }
    }

    @Override
    public void updatePlayer(EntityPlayer entityplayer) {
        if (entityplayer != tracker) {
            try {
                controller.updateViewer(CommonNMS.getPlayer(entityplayer));
            } catch (Throwable t) {
                CommonPlugin.LOGGER_NETWORK.log(Level.SEVERE, "Failed to update viewer:");
                t.printStackTrace();
            }
        }
    }
}
