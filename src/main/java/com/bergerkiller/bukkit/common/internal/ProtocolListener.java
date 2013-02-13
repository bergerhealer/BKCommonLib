package com.bergerkiller.bukkit.common.internal;

import net.minecraft.server.v1_4_R1.EntityPlayer;
import net.minecraft.server.v1_4_R1.MinecraftServer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.NativeUtil;

public class ProtocolListener implements Listener {
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		CommonPlugin manager = CommonPlugin.getInstance();
		
		if(!manager.libaryInstalled) {
			EntityPlayer ep = NativeUtil.getNative(player);
			MinecraftServer server = CommonUtil.getMCServer();
			ep.playerConnection = new PacketConnection(server, ep.playerConnection.networkManager, ep);
		}
	}
}
