package com.bergerkiller.bukkit.common.internal;

import net.minecraft.server.WorldServer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.utils.WorldUtil;

@SuppressWarnings("unused")
class CommonListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR)
	private void onPluginEnable(final PluginEnableEvent event) {
		for (PluginBase pb : CommonPlugin.plugins) {
			pb.updateDependency(event.getPlugin(), true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onPluginDisable(PluginDisableEvent event) {
		for (PluginBase pb : CommonPlugin.plugins) {
			pb.updateDependency(event.getPlugin(), false);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onWorldInit(WorldInitEvent event) {
		if (CommonPlugin.worldListeners.containsKey(event.getWorld())) {
			return;
		}
		WorldServer world = WorldUtil.getNative(event.getWorld());
		CommonWorldListener listener = new CommonWorldListener(world);
		listener.enable();
		CommonPlugin.worldListeners.put(event.getWorld(), listener);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onWorldUnload(WorldUnloadEvent event) {
		if (event.isCancelled()) {
			return;
		}
		CommonWorldListener listener = CommonPlugin.worldListeners.remove(event.getWorld());
		if (listener != null) {
			listener.disable();
		}
	}
}
