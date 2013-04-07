package com.bergerkiller.bukkit.common.internal;

import net.minecraft.server.v1_5_R2.Connection;
import net.minecraft.server.v1_5_R2.INetworkManager;
import net.minecraft.server.v1_5_R2.Packet;
import net.minecraft.server.v1_5_R2.PlayerConnection;

import org.bukkit.entity.Player;
import org.spigotmc.netty.PacketListener;

import com.bergerkiller.bukkit.common.protocol.PacketFields;

public class SpigotPacketListener extends PacketListener {
	public static boolean ENABLED = true;
	private final CommonPacketHandler handler;
	
	public SpigotPacketListener() {
		this.handler = (CommonPacketHandler) CommonPlugin.getInstance().getPacketHandler();
		PacketListener.register(this, CommonPlugin.getInstance());
	}
	
	@Override
	public Packet packetReceived(INetworkManager networkManager, Connection connection, Packet packet) {
		if(!ENABLED || !(connection instanceof PlayerConnection)) {
			return super.packetReceived(networkManager, connection, packet);
		}
		
		PlayerConnection playerConnection = (PlayerConnection) connection;
		Player player = CommonNMS.getPlayer(playerConnection.player);
		if(handler.onPacketReceive(player, packet, false)) {
			return super.packetReceived(networkManager, playerConnection, packet);
		} else {
			return null;
		}
	}
	
	@Override
	public Packet packetQueued(INetworkManager networkManager, Connection connection, Packet packet) {
		if(!ENABLED || !(connection instanceof PlayerConnection)) {
			if(ENABLED) {
				int packetId = PacketFields.DEFAULT.packetID.get(packet);
				if(packetId == 237) { //Strange bug....
					return null;
				}
			}
			
			return super.packetQueued(networkManager, connection, packet);
		}
		
		PlayerConnection playerConnection = (PlayerConnection) connection;
		Player player = CommonNMS.getPlayer(playerConnection.player);
		if(handler.onPacketSend(player, packet, false)) {
			return super.packetQueued(networkManager, playerConnection, packet);
		} else {
			return null;
		}
	}
}
