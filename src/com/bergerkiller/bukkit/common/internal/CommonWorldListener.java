package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayList;

import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveEvent;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.World;
import net.minecraft.server.WorldManager;
import net.minecraft.server.WorldServer;

class CommonWorldListener extends WorldManager {
	private boolean isEnabled = false;
	private ArrayList<EntityPlayer> addedPlayers = new ArrayList<EntityPlayer>();

	public CommonWorldListener(World world) {
		super(CommonUtil.getMCServer(), (WorldServer) world);
	}

	public static boolean isValid() {
		return WorldServerRef.accessList.isValid();
	}

	/**
	 * Enables the listener<br>
	 * Will send entity add messages for all current entities
	 */
	@SuppressWarnings("unchecked")
	public void enable() {
		if (isValid()) {
			WorldServerRef.accessList.get(this.world).add(this);
			this.addedPlayers.addAll(this.world.players);
			this.isEnabled = true;
		} else {
			new RuntimeException("Failed to listen in World").printStackTrace();
		}
	}

	/**
	 * Disables the listener
	 */
	public void disable() {
		if (isValid()) {
			WorldServerRef.accessList.get(this.world).remove(this);
			this.addedPlayers.clear();
			this.isEnabled = false;
		}
	}

	public boolean isEnabled() {
		return this.isEnabled;
	}

	@Override
	public final void a(Entity added) {
		if (added != null) {
			// Add entity
			if (added instanceof EntityPlayer) {
				if (this.addedPlayers.contains(added)) {
					return;
				} else {
					this.addedPlayers.add((EntityPlayer) added);
				}
			}
			CommonUtil.callEvent(new EntityAddEvent(added.getBukkitEntity()));
		}
	}

	@Override
	public final void b(Entity removed) {
		if (removed != null) {
			// Remove entity
			if (removed instanceof EntityPlayer) {
				if (!this.addedPlayers.remove(removed)) {
					return;
				}
			}
			CommonUtil.callEvent(new EntityRemoveEvent(removed.getBukkitEntity()));
		}
	}

	@Override
	public final void a(int arg0, int arg1, int arg2) {
		// Block notify (physics)
	}

	@Override
	public void a(int arg0, int arg1, int arg2, int arg3, int arg4) {
	}

	@Override
	public void a(String name, double x, double y, double z, float yaw, float pitch) {
	}

	@Override
	public void a(EntityHuman human, int code, int x, int y, int z, int dat) {
	}

	@Override
	public void a(String name, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6) {
	}

	@Override
	public void b(int x, int y, int z) {
	}

	@Override
	public void a(String name, int x, int y, int z) {
	}

	@Override
	public void a(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
	}
}
