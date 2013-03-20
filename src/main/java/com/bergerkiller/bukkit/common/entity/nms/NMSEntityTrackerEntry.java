package com.bergerkiller.bukkit.common.entity.nms;

import java.util.List;

import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerEntryRef;
import com.bergerkiller.bukkit.common.utils.MathUtil;

import net.minecraft.server.v1_5_R1.*;

public class NMSEntityTrackerEntry extends EntityTrackerEntry {
	private EntityNetworkController<?> controller;

	public NMSEntityTrackerEntry(Entity entity, int i, int j, boolean flag) {
		super(entity, i, j, flag);
		this.xLoc = protLoc(entity.locX);
		this.yLoc = MathUtil.floor(entity.locY * 32.0D);
		this.zLoc = protLoc(entity.locZ);
	}

	public void setController(EntityNetworkController<?> controller) {
		this.controller = controller;
	}

	public EntityNetworkController<?> getController() {
		return controller;
	}

	public int protRot(float rot) {
		return MathUtil.floor(rot * 256.0f / 360.0f);
	}

	public int protLoc(double loc) {
		return tracker.at.a(loc);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void track(List list) {
		synchronized (controller) {
			updateTrackers(list);
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
			if (super.tracker.e(lastSyncX, lastSyncY, lastSyncZ) <= 16.0) {
				return;
			}
		}
		// Update tracking data
		EntityTrackerEntryRef.prevX.set(this, super.tracker.locX);
		EntityTrackerEntryRef.prevY.set(this, super.tracker.locY);
		EntityTrackerEntryRef.prevZ.set(this, super.tracker.locZ);
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
		if (entityplayer != this.tracker) {
			controller.updateViewer(CommonNMS.getPlayer(entityplayer));
		}
	}
}
