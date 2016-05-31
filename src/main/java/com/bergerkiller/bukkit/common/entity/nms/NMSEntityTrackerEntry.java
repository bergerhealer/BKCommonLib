package com.bergerkiller.bukkit.common.entity.nms;

import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerEntryRef;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.EntityTrackerEntry;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.logging.Level;

public class NMSEntityTrackerEntry extends EntityTrackerEntry {

    private EntityNetworkController<?> controller;
    Entity ent = null;

    /**
     * Initializes a new Entity Tracker Entry hook
     *
     * @param entity that this tracker entry belongs to
     */
    public NMSEntityTrackerEntry(final Entity entity) {
        super(CommonNMS.getNative(entity), 80, 3, 0, true);
        ent = entity;
//        // Fix these two: Wrongly set in Constructor
//        this.xLoc = EntityNetworkController.a(CommonNMS.getNative(entity).locX);
//        this.zLoc = EntityNetworkController.a(CommonNMS.getNative(entity).locZ);
        // Set proper update interval/viewdistance/mobile
        final CommonEntityType type = CommonEntityType.byNMSEntity(CommonNMS.getNative(entity));
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
        this.a++;
    }

    @SuppressWarnings("rawtypes")
    private void updateTrackers(List list) {
        if (EntityTrackerEntryRef.synched.get(this)) {
            double lastSyncX = EntityTrackerEntryRef.prevX.get(this);
            double lastSyncY = EntityTrackerEntryRef.prevY.get(this);
            double lastSyncZ = EntityTrackerEntryRef.prevZ.get(this);
            if (CommonNMS.getNative(ent).e(lastSyncX, lastSyncY, lastSyncZ) <= 16.0) {
                return;
            }
        }
        // Update tracking data
        EntityTrackerEntryRef.prevX.set(this, CommonNMS.getNative(ent).locX);
        EntityTrackerEntryRef.prevY.set(this, CommonNMS.getNative(ent).locY);
        EntityTrackerEntryRef.prevZ.set(this, CommonNMS.getNative(ent).locZ);
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
        if (entityplayer != CommonNMS.getNative(ent)) {
            try {
                controller.updateViewer(CommonNMS.getPlayer(entityplayer));
            } catch (Throwable t) {
                CommonPlugin.LOGGER_NETWORK.log(Level.SEVERE, "Failed to update viewer:");
                t.printStackTrace();
            }
        }
    }
}
