package com.bergerkiller.bukkit.common.internal.network;

import net.minecraft.server.v1_5_R2.Connection;
import net.minecraft.server.v1_5_R2.INetworkManager;
import net.minecraft.server.v1_5_R2.Packet;
import net.minecraft.server.v1_5_R2.PlayerConnection;

import org.bukkit.entity.Player;
import org.spigotmc.netty.PacketListener;

import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;

/**
 * A packet handler implementation that uses Spigot's netty service
 */
public class SpigotPacketHandler extends PacketHandlerHooked {
	private static SpigotPacketListener listener;

	@Override
	public String getName() {
		return "the Spigot server 'Netty' service";
	}

	@Override
	public boolean onEnable() {
		if (listener == null) {
			listener = new SpigotPacketListener();
			listener.enable(this);
			PacketListener.register(listener, CommonPlugin.getInstance());
		} else {
			listener.enable(this);
		}
		return true;
	}

	@Override
	public boolean onDisable() {
		if (listener != null) {
			listener.disable();
		}
		return true;
	}

	@Override
	public void onPlayerJoin(Player player) {
	}

	private static class SpigotPacketListener extends PacketListener {
		private SpigotPacketHandler handler;

		public void enable(SpigotPacketHandler handler) {
			this.handler = handler;
		}

		public void disable() {
			this.handler = null;
		}

		@Override
		public Packet packetReceived(INetworkManager networkManager, Connection connection, Packet packet) {
			if (handler == null || !(connection instanceof PlayerConnection)) {
				return super.packetReceived(networkManager, connection, packet);
			}
			PlayerConnection playerConnection = (PlayerConnection) connection;
			Player player = CommonNMS.getPlayer(playerConnection.player);
			if (handler.handlePacketReceive(player, packet, false)) {
				return super.packetReceived(networkManager, playerConnection, packet);
			} else {
				return null;
			}
		}

		@Override
		public Packet packetQueued(INetworkManager networkManager, Connection connection, Packet packet) {
			if(handler == null || !(connection instanceof PlayerConnection)) {
				return super.packetQueued(networkManager, connection, packet);
			}

			// This really should be fixed another way - it MAY NOT HAPPEN
			// int packetId = PacketFields.DEFAULT.packetID.get(packet);
			// if (packetId == 237) { //Strange bug....
			// 	return null;
			// }

			PlayerConnection playerConnection = (PlayerConnection) connection;
			Player player = CommonNMS.getPlayer(playerConnection.player);
			if (handler.handlePacketSend(player, packet, false)) {
				return super.packetQueued(networkManager, playerConnection, packet);
			} else {
				return null;
			}
		}
	}
}
