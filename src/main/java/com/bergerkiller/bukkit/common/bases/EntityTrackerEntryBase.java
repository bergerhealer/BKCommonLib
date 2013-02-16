package com.bergerkiller.bukkit.common.bases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerEntryRef;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.NativeUtil;

import net.minecraft.server.v1_4_R1.EntityPlayer;
import net.minecraft.server.v1_4_R1.EntityTrackerEntry;
import net.minecraft.server.v1_4_R1.MathHelper;
import net.minecraft.server.v1_4_R1.Packet;

public class EntityTrackerEntryBase extends EntityTrackerEntry {
	private static final SafeMethod<Void> onTickMethod = ClassTemplate.create(EntityTrackerEntryBase.class).getMethod("onTick");
	private final boolean isOnTickOverrided;

	public EntityTrackerEntryBase(org.bukkit.entity.Entity entity, int viewableDistance, int j, boolean mobile) {
		super(NativeUtil.getNative(entity), viewableDistance, j, mobile);
		this.isOnTickOverrided = onTickMethod.isOverridedIn(getClass());
	}

	public org.bukkit.entity.Entity getTrackerVehicle() {
		return EntityTrackerEntryRef.vehicle.get(this);
	}

	public void setTrackerVehicle(org.bukkit.entity.Entity vehicle) {
		EntityTrackerEntryRef.vehicle.set(this, vehicle);
	}

	public void setTracker(org.bukkit.entity.Entity tracker) {
		EntityTrackerEntryRef.tracker.set(this, tracker);
	}

	public org.bukkit.entity.Entity getTracker() {
		return EntityTrackerEntryRef.tracker.get(this);
	}

	/**
	 * Gets whether the position of the tracker has changed<br>
	 * Note: This is NOT tracker.positionChanged - that variable is wrongly named!
	 * 
	 * @return True if the tracker changed position, False if not
	 */
	public boolean isTrackerPositionChanged() {
		return EntityRef.positionChanged.get(tracker);
	}

	/**
	 * Gets whether the velocity of the tracker has changed
	 * 
	 * @return True if the tracker changed velocity, False if not
	 */
	public boolean isTrackerVelocityChanged() {
		return tracker.velocityChanged;
	}

	public int getTrackerProtocolX() {
		return this.tracker.as.a(this.tracker.locX);
	}

	public int getTrackerProtocolY() {
		return MathUtil.floor(this.tracker.locY * 32.0D);
	}

	public int getTrackerProtocolZ() {
		return this.tracker.as.a(this.tracker.locZ);
	}

	public int getTrackerProtocolYaw() {
		return MathHelper.d(this.tracker.yaw * 256.0f / 360.0f);
	}

	public int getTrackerProtocolPitch() {
		return MathHelper.d(this.tracker.pitch * 256.0f / 360.0f);
	}

	public int getTrackerId() {
		return tracker.id;
	}

	/**
	 * @deprecated use {@link broadcastDestroyPackets()} instead
	 */
	@Deprecated
	@Override
	public final void a() {
		this.broadcastDestroyPackets();
	}

	public void broadcastDestroyPackets() {
		super.a();
	}

	/**
	 * @deprecated use {@link removePlayer()} instead
	 */
	@Deprecated
	@Override
	public final void a(EntityPlayer entityplayer) {
		this.removeViewer(NativeUtil.getPlayer(entityplayer));
	}

	/**
	 * @deprecated use {@link removePlayer()} instead
	 */
	@Deprecated
	@Override
	public void clear(EntityPlayer entityplayer) {
		this.removeViewer(NativeUtil.getPlayer(entityplayer));
	}

	public void removeViewer(Player player) {
		super.clear(NativeUtil.getNative(player));
	}

	/**
	 * @deprecated use {@link broadcastPacket()} instead
	 */
	@Deprecated
	@Override
	public final void broadcast(Packet arg0) {
		this.broadcastPacket(arg0, false);
	}

	/**
	 * @deprecated use {@link broadcastPacket()} instead
	 */
	@Deprecated
	@Override
	public void broadcastIncludingSelf(Packet packet) {
		this.broadcastPacket(packet, true);
	}

	public void broadcastPacket(Object packet, boolean self) {
		if (self) {
			super.broadcastIncludingSelf((Packet) packet);
		} else {
			super.broadcast((Packet) packet);
		}
	}

	private void updateViewers(Collection<Player> viewers) {
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
		this.updatePlayers(viewers);
	}

	/**
	 * @deprecated use {@link onTick()} instead
	 */
	@Deprecated
	@Override
	@SuppressWarnings("rawtypes")
	public final void track(List arg0) {
		if (isOnTickOverrided) {
			// Update viewers
			this.updateViewers(NativeUtil.getPlayers(arg0));
			// perform on tick logic
			this.onTick();
		} else {
			super.track(arg0);
		}
	}

	/**
	 * Performs per-tick logic for this Entity Tracker entry
	 */
	@SuppressWarnings({"unchecked"})
	public void onTick() {
		super.track(new ArrayList<Object>(trackedPlayers));
	}

	/**
	 * @deprecated use {@link updatePlayers(Collection<Player>)} instead
	 */
	@Deprecated
	@Override
	@SuppressWarnings("rawtypes")
	public final void scanPlayers(List arg0) {
		updatePlayers(NativeUtil.getPlayers(arg0));
	}

	public void updatePlayers(Collection<Player> viewers) {
		for (Player player : viewers) {
			updatePlayer(player);
		}
	}

	/**
	 * @deprecated use {@link updatePlayer(Player)} instead
	 */
	@Deprecated
	@Override
	public final void updatePlayer(EntityPlayer arg0) {
		this.updatePlayer(NativeUtil.getPlayer(arg0));
	}

	public void updatePlayer(Player player) {
		super.updatePlayer(NativeUtil.getNative(player));
	}

	/**
	 * Gets a packet to spawn this entity for the client<br>
	 * Note: Overriding it has no effect for the internal implementation
	 * 
	 * @return Entity spawn packet
	 */
	public Object getSpawnPacket() {
		return EntityTrackerEntryRef.getSpawnPacket.invoke(this);
	}
}
