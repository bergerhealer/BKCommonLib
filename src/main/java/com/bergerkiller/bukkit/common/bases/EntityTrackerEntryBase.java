package com.bergerkiller.bukkit.common.bases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingCollection;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerEntryRef;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;

import net.minecraft.server.v1_4_R1.EntityPlayer;
import net.minecraft.server.v1_4_R1.EntityTrackerEntry;
import net.minecraft.server.v1_4_R1.MathHelper;
import net.minecraft.server.v1_4_R1.Packet;

/**
 * Base class extension of EntityTrackerEntry.
 * Really wish we could turn this into a wrapper<>base system instead...
 */
public class EntityTrackerEntryBase extends EntityTrackerEntry {
	private static final SafeMethod<Void> onTickMethod = ClassTemplate.create(EntityTrackerEntryBase.class).getMethod("onTick");
	private final boolean isOnTickOverrided;
	/**
	 * This field (contained in the super class) should not be used, use getViewers() instead
	 */
	@Deprecated
	public final byte trackedPlayers = 0;

	/**
	 * This field (contained in the super class) should not be used, use getTracker() instead
	 */
	@Deprecated
	public final byte tracker = 0;

	public EntityTrackerEntryBase(org.bukkit.entity.Entity entity, int viewableDistance, int updateRate, boolean mobile) {
		super(CommonNMS.getNative(entity), viewableDistance, updateRate, mobile);
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
	 * Gets all the viewers this Entity Tracker Entry has
	 * 
	 * @return viewers
	 */
	public Collection<Player> getViewers() {
		return new ConvertingCollection<Player>(super.trackedPlayers, ConversionPairs.player);
	}

	/**
	 * Gets the view distance for this tracker
	 * 
	 * @return view distance
	 */
	public int getViewDistance() {
		return this.b;
	}

	/**
	 * Gets whether the position of the tracker has changed<br>
	 * Note: This is NOT tracker.positionChanged - that variable is wrongly named!
	 * 
	 * @return True if the tracker changed position, False if not
	 */
	public boolean isTrackerPositionChanged() {
		return EntityRef.positionChanged.get(super.tracker);
	}

	/**
	 * Gets whether the velocity of the tracker has changed
	 * 
	 * @return True if the tracker changed velocity, False if not
	 */
	public boolean isTrackerVelocityChanged() {
		return EntityRef.velocityChanged.get(super.tracker);
	}

	public void setTrackerPositionChanged(boolean changed) {
		EntityRef.positionChanged.set(super.tracker, changed);
	}

	public void setTrackerVelocityChanged(boolean changed) {
		EntityRef.velocityChanged.set(super.tracker, changed);
	}

	public int getTrackerProtocolX() {
		return super.tracker.as.a(super.tracker.locX);
	}

	public int getTrackerProtocolY() {
		return MathUtil.floor(super.tracker.locY * 32.0);
	}

	public int getTrackerProtocolZ() {
		return super.tracker.as.a(super.tracker.locZ);
	}

	public int getTrackerProtocolYaw() {
		return MathHelper.d(super.tracker.yaw * 256.0f / 360.0f);
	}

	public int getTrackerProtocolPitch() {
		return MathHelper.d(super.tracker.pitch * 256.0f / 360.0f);
	}

	public DataWatcher getTrackerMetaData() {
		return new DataWatcher(super.tracker.getDataWatcher());
	}

	public int getTrackerId() {
		return super.tracker.id;
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
		this.removeViewer(CommonNMS.getPlayer(entityplayer));
	}

	/**
	 * @deprecated use {@link removePlayer()} instead
	 */
	@Deprecated
	@Override
	public void clear(EntityPlayer entityplayer) {
		this.removeViewer(CommonNMS.getPlayer(entityplayer));
	}

	public synchronized void removeViewer(Player player) {
		super.clear(CommonNMS.getNative(player));
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

	public synchronized void broadcastPacket(Object packet) {
		broadcastPacket(packet, false);
	}

	public synchronized void broadcastPacket(Object packet, boolean self) {
		if (self) {
			super.broadcastIncludingSelf((Packet) packet);
		} else {
			super.broadcast((Packet) packet);
		}
	}
   
	private synchronized void updateViewers(Collection<Player> viewers) {
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
		this.updatePlayers(viewers);
	}

	/**
	 * Removes all viewers and updates them again one by one.
	 * This method can be used to re-send this entity to all viewers thread-safe.
	 * Benefit of this method over teleport is that there is no entity movement smoothing.
	 */
	public synchronized void doRespawn() {
		this.doInstantDestroy();
		super.trackedPlayers.clear();
		EntityTrackerEntryRef.synched.set(this, false);
		// All viewers will receive the new data: make sure position/rotation is synched
		this.xLoc = this.getTrackerProtocolX();
		this.yLoc = this.getTrackerProtocolY();
		this.zLoc = this.getTrackerProtocolZ();
		this.xRot = this.getTrackerProtocolYaw();
		this.yRot = this.getTrackerProtocolPitch();
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
			this.updateViewers(CommonNMS.getPlayers(arg0));
			// perform on tick logic
			this.onTick();
		} else {
			super.track(arg0);
		}
	}

	/**
	 * Performs per-tick logic for this Entity Tracker entry
	 */
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
		updatePlayers(CommonNMS.getPlayers(arg0));
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
		this.updatePlayer(CommonNMS.getPlayer(arg0));
	}

	public synchronized void updatePlayer(Player player) {
		super.updatePlayer(CommonNMS.getNative(player));
	}

	/**
	 * Performs entity removal logic for this entity, for the player specified
	 * 
	 * @param player to 'destroy' (hide) this entity for
	 */
	@SuppressWarnings("unchecked")
	public void doDestroy(Player player) {
		CommonNMS.getNative(player).removeQueue.add(getTrackerId());
	}

	/**
	 * Instantly sends destroy messages for this entity to all viewing players, without a tick delay
	 */
	public void doInstantDestroy() {
		this.broadcastPacket(PacketFields.DESTROY_ENTITY.newInstance(this.getTrackerId()));
	}

	/**
	 * Instantly sends a destroy message for this entity to the player specified, without a tick delay
	 * 
	 * @param player to send the destroy message to
	 */
	public void doInstantDestroy(Player player) {
		PacketUtil.sendPacket(player, PacketFields.DESTROY_ENTITY.newInstance(this.getTrackerId()));
	}

	/**
	 * Gets a packet to spawn this entity for the client<br>
	 * Note: Overriding it has no effect for the internal implementation
	 * 
	 * @return Entity spawn packet
	 */
	public CommonPacket getSpawnPacket() {
		return EntityTrackerEntryRef.getSpawnPacket(this);
	}
}
