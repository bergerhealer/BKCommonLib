package com.bergerkiller.bukkit.common.internal;

import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.server.v1_4_R1.EntityPlayer;
import net.minecraft.server.v1_4_R1.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.bergerkiller.bukkit.common.EntityMap;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.NativeUtil;

@SuppressWarnings("unused")
class CommonListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR)
	private void onPluginEnable(final PluginEnableEvent event) {
		CommonPlugin plugin = CommonPlugin.getInstance();
		String name = LogicUtil.fixNull(event.getPlugin().getName(), "");
		for (PluginBase pb : CommonPlugin.getInstance().plugins) {
			pb.updateDependency(event.getPlugin(), name, true);
		}
		
		if(name.equalsIgnoreCase("ProtocolLib")) {
			//ProtcolLib has been activated, lets stop our protocol engines
			CommonPacketListener.unbindAll();
			
			//Lets notify BKCommonLib it has been enabled
			plugin.isProtocolLibEnabled = true;
			
			//Disable the connection update task if enabled
			if(plugin.playerConnectionTask.isRunning())
				plugin.playerConnectionTask.stop();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onPluginDisable(PluginDisableEvent event) {
		CommonPlugin plugin = CommonPlugin.getInstance();
		String name = LogicUtil.fixNull(event.getPlugin().getName(), "");
		for (PluginBase pb : CommonPlugin.getInstance().plugins) {
			pb.updateDependency(event.getPlugin(), name, false);
		}
		
		if(name.equalsIgnoreCase("ProtocolLib")) {
			//Oh no, ProtocolLib has been disabled
			//Lets init the players to our system
			CommonPacketListener.bindAll();
			
			//Lets notify BKCommonLib it has been disabled
			plugin.isProtocolLibEnabled = false;
			
			//Enable the connection update task if disabled
			if(!plugin.playerConnectionTask.isRunning())
				plugin.playerConnectionTask.start(1, 1);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onWorldInit(WorldInitEvent event) {
		CommonPlugin.getInstance().notifyWorldAdded(event.getWorld());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onWorldUnload(WorldUnloadEvent event) {
		if (event.isCancelled()) {
			return;
		}
		CommonWorldListener listener = CommonPlugin.getInstance().worldListeners.remove(event.getWorld());
		if (listener != null) {
			listener.disable();
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!CommonPlugin.getInstance().isProtocolLibEnabled) {
			CommonPacketListener.bind(event.getPlayer());
		}
	}
}
