package com.bergerkiller.bukkit.common.entity.nms;

import java.util.List;

import org.bukkit.entity.Entity;

import net.minecraft.server.v1_5_R2.EntityPlayer;
import net.minecraft.server.v1_5_R2.EntityTrackerEntry;

import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.entity.CommonEntityTypeStore;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerEntryRef;

public class NMSEntityTrackerEntry extends EntityTrackerEntry {
	private final EntityNetworkController<?> controller;

	/**
	 * Initializes a new Entity Tracker Entry hook
	 * 
	 * @param controller for the tracker entry
	 * @param previous entity tracker entry that was set to load state from, use Null to ignore
	 */
	public NMSEntityTrackerEntry(final Entity entity, final EntityNetworkController<?> controller, final Object previous) {
		super(CommonNMS.getNative(entity), 80, 3, true);
		this.controller = controller;
		if (previous == null) {
			// Fix these two: Wrongly set in Constructor
			this.xLoc = tracker.at.a(tracker.locX);
			this.zLoc = tracker.at.a(tracker.locZ);
			// Set proper update interval/viewdistance/mobile
			final CommonEntityType type = CommonEntityTypeStore.byNMSEntity(tracker);
			this.controller.setMobile(type.networkIsMobile);
			this.controller.setUpdateInterval(type.networkUpdateInterval);
			this.controller.setViewDistance(type.networkViewDistance);
		} else {
			// Apply all updated live data from the old entity tracker
			EntityTrackerEntryRef.TEMPLATE.transfer(previous, this);
		}
	}

	public EntityNetworkController<?> getController() {
		return controller;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void track(List list) {
		synchronized (controller) {
			updateTrackers(list);
			EntityTrackerEntryRef.timeSinceLocationSync.set(this, EntityTrackerEntryRef.timeSinceLocationSync.get(this) + 1);
			controller.onSync();
			this.m++;
		}
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
		controller.makeHiddenForAll();
	}

	@Override
	public void clear(EntityPlayer entityplayer) {
		controller.removeViewer(CommonNMS.getPlayer(entityplayer));
	}

	@Override
	public void a(EntityPlayer entityplayer) {
		controller.removeViewer(CommonNMS.getPlayer(entityplayer));
	}

	@Override
	public void updatePlayer(EntityPlayer entityplayer) {
		if (entityplayer != tracker) {
			controller.updateViewer(CommonNMS.getPlayer(entityplayer));
		}
	}
}
