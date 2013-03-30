package com.bergerkiller.bukkit.common.internal;

import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.server.v1_5_R2.Entity;
import net.minecraft.server.v1_5_R2.EntityPlayer;
import net.minecraft.server.v1_5_R2.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_5_R2.CraftChunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.EntityMap;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityHook;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;

@SuppressWarnings("unused")
class CommonListener implements Listener {
	private static final MethodAccessor<Void> chunkBreakLink = new SafeMethod<Void>(CommonUtil.getCBClass("CraftChunk"), "breakLink");

	@EventHandler(priority = EventPriority.MONITOR)
	private void onPluginEnable(final PluginEnableEvent event) {
		CommonPlugin plugin = CommonPlugin.getInstance();
		String name = LogicUtil.fixNull(event.getPlugin().getName(), "");
		for (PluginBase pb : CommonPlugin.getInstance().plugins) {
			pb.updateDependency(event.getPlugin(), name, true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onPluginDisable(PluginDisableEvent event) {
		CommonPlugin plugin = CommonPlugin.getInstance();
		String name = LogicUtil.fixNull(event.getPlugin().getName(), "");
		for (PluginBase pb : CommonPlugin.getInstance().plugins) {
			pb.updateDependency(event.getPlugin(), name, false);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onWorldInit(final WorldInitEvent event) {
		CommonUtil.nextTick(new Runnable() {
			public void run() {
				CommonPlugin.getInstance().notifyWorldAdded(event.getWorld());
			}
		});
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

	@EventHandler(priority = EventPriority.LOWEST)
	private void onVehicleEnter(final VehicleEnterEvent event) {
		if (event.isCancelled()) {
			return;
		}
		// Set the vehicle and passenger handles for Hook entities
		// This is required to avoid problems with replaced Entities
		if (CommonNMS.getNative(event.getVehicle()).dead) {
			// Find the real Entity and redirect the call
			final org.bukkit.entity.Entity realVehicle = EntityUtil.getEntity(event.getEntered().getWorld(), event.getVehicle().getUniqueId());
			if (realVehicle != null && realVehicle != event.getVehicle()) {
				// Perform the event again for the right Bukkit entity/Handle
				event.setCancelled(true);
				realVehicle.setPassenger(event.getEntered());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onPlayerJoin(PlayerJoinEvent event) {
		if (CommonPlugin.getInstance().isUsingFallBackPacketListener()) {
			CommonPlayerConnection.bind(event.getPlayer());
		}
	}
}
