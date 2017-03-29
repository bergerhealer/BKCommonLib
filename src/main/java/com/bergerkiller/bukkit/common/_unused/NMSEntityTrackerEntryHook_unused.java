package com.bergerkiller.bukkit.common._unused;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityTracker;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityTrackerEntry;

import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.EntityTrackerEntry;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.logging.Level;

public class NMSEntityTrackerEntryHook_unused extends EntityTrackerEntry {

    private EntityNetworkController<?> controller;
    Entity ent = null;

    /**
     * Initializes a new Entity Tracker Entry hook
     *
     * @param entity that this tracker entry belongs to
     */
    public NMSEntityTrackerEntryHook_unused(final Entity entity) {
        super(CommonNMS.getNative(entity), 80, CommonNMS.getMCServer().getPlayerList().d(), 3, true);
        ent = entity;
//        // Fix these two: Wrongly set in Constructor
//        this.xLoc = EntityNetworkController.a(CommonNMS.getNative(entity).locX);
//        this.zLoc = EntityNetworkController.a(CommonNMS.getNative(entity).locZ);
        // Set proper update interval/viewdistance/mobile

        Object dummyEntry = NMSEntityTracker.createDummyEntry(entity);
        NMSEntityTrackerEntry.isMobile.transfer(dummyEntry, this);
        NMSEntityTrackerEntry.updateInterval.transfer(dummyEntry, this);
        NMSEntityTrackerEntry.viewDistance.transfer(dummyEntry, this);
        NMSEntityTrackerEntry.playerViewDistance.transfer(dummyEntry, this);
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
        NMSEntityTrackerEntry.timeSinceLocationSync.set(this, NMSEntityTrackerEntry.timeSinceLocationSync.get(this) + 1);
        try {
            controller.onSync();
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to synchronize:");
            t.printStackTrace();
        }

        // Equivalent, but faster
        //NMSEntityTrackerEntry.tickCounter.set(this, NMSEntityTrackerEntry.tickCounter.get(this) + 1);
        this.a++;
    }

    @SuppressWarnings("rawtypes")
    private void updateTrackers(List list) {
        if (NMSEntityTrackerEntry.synched.get(this)) {
            double lastSyncX = NMSEntityTrackerEntry.prevX.get(this);
            double lastSyncY = NMSEntityTrackerEntry.prevY.get(this);
            double lastSyncZ = NMSEntityTrackerEntry.prevZ.get(this);
            if (CommonNMS.getNative(ent).e(lastSyncX, lastSyncY, lastSyncZ) <= 16.0) {
                return;
            }
        }
        // Update tracking data
        NMSEntityTrackerEntry.prevX.set(this, CommonNMS.getNative(ent).locX);
        NMSEntityTrackerEntry.prevY.set(this, CommonNMS.getNative(ent).locY);
        NMSEntityTrackerEntry.prevZ.set(this, CommonNMS.getNative(ent).locZ);
        NMSEntityTrackerEntry.synched.set(this, true);
        this.scanPlayers(list);
    }

    @Override
    public void a() {
        try {
            controller.makeHiddenForAll();
        } catch (Throwable t) {
        	Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to hide for all viewers:");
            t.printStackTrace();
        }
    }

    @Override
    public void clear(EntityPlayer entityplayer) {
        try {
            controller.removeViewer(Conversion.toPlayer.convert(entityplayer));
        } catch (Throwable t) {
        	Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to remove viewer:");
            t.printStackTrace();
        }
    }

    @Override
    public void a(EntityPlayer entityplayer) {
        try {
            controller.removeViewer(Conversion.toPlayer.convert(entityplayer));
        } catch (Throwable t) {
        	Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to remove viewer:");
            t.printStackTrace();
        }
    }

    @Override
    public void updatePlayer(EntityPlayer entityplayer) {
        if (entityplayer != CommonNMS.getNative(ent)) {
            try {
                controller.updateViewer(Conversion.toPlayer.convert(entityplayer));
            } catch (Throwable t) {
            	Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to update viewer:");
                t.printStackTrace();
            }
        }
    }
}
