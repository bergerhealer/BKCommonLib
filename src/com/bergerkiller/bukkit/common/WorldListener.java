package com.bergerkiller.bukkit.common;

import java.util.List;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IWorldAccess;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

public class WorldListener implements IWorldAccess {	
	public final WorldServer world;
	private boolean isEnabled = false;
	public WorldListener(World world) {
		this.world = (WorldServer) world;
	}

	private static SafeField<List<IWorldAccess>> accesslist = new SafeField<List<IWorldAccess>>(World.class, "u");

	public static boolean isValid() {
		return accesslist.isValid();
	}

	public boolean enable() {
		if (isValid()) {
			accesslist.get(this.world).add(this);
			this.isEnabled = true;
			return true;
		} else {
			return false;
		}
	}

	public boolean disable() {
		if (isValid()) {
			accesslist.get(this.world).remove(this);
			this.isEnabled = false;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isEnabled() {
		return this.isEnabled;
	}

	@Override
	public final void a(Entity arg0) {
		if (arg0 == null) return;
		this.onEntityAdd(arg0);
	}

	@Override
	public final void b(Entity arg0) {
		if (arg0 == null) return;
		this.onEntityRemove(arg0);
	}

	@Override
	public final void a(int arg0, int arg1, int arg2) {
		this.onNotify(arg0, arg1, arg2);
	}

	@Override
	public final void a(int arg0, int arg1, int arg2, TileEntity arg3) {
		this.onTileEntityAdd(arg3, arg0, arg1, arg2);
	}

	@Override
	public void a(String name, double x, double y, double z, float yaw, float pitch) {}

	@Override
	public void a(EntityHuman human, int code, int x, int y, int z, int dat) {}

	@Override
	public void a(String name, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6) {}

	@Override
	public void b(int x, int y, int z) {}

	@Override
	public void a(String name, int x, int y, int z) {}

	@Override
	public void a(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {}

	public void onTileEntityAdd(TileEntity tile, int x, int y, int z) {}
	public void onEntityAdd(Entity entity) {}
	public void onEntityRemove(Entity entity) {}
	public void onNotify(int x, int y, int z) {}
}
