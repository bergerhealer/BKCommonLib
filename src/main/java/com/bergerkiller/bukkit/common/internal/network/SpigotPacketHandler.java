package com.bergerkiller.bukkit.common.internal.network;

import io.netty.channel.Channel;

import java.util.List;

import net.minecraft.server.v1_5_R2.Connection;
import net.minecraft.server.v1_5_R2.INetworkManager;
import net.minecraft.server.v1_5_R2.Packet;
import net.minecraft.server.v1_5_R2.PlayerConnection;

import org.bukkit.entity.Player;
import org.spigotmc.netty.NettyNetworkManager;
import org.spigotmc.netty.PacketListener;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.classes.EntityPlayerRef;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerConnectionRef;

/**
 * A packet handler implementation that uses Spigot's netty service
 */
public class SpigotPacketHandler extends PacketHandlerHooked {
	private static final ClassTemplate<?> NETTY_NETWORK_TEMPLATE = ClassTemplate.create(NettyNetworkManager.class);
	private static final FieldAccessor<Boolean> netty_connected = NETTY_NETWORK_TEMPLATE.getField("connected");
	private static final FieldAccessor<List<Object>> netty_queue = NETTY_NETWORK_TEMPLATE.getField("highPriorityQueue");
	private static final FieldAccessor<Channel> netty_channel = NETTY_NETWORK_TEMPLATE.getField("channel");
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

	@Override
	public void sendSilentPacket(Player player, Object packet) {
		Object conn = EntityPlayerRef.playerConnection.get(Conversion.toEntityHandle.convert(player));
		if (conn != null) {
			Object networkManager = PlayerConnectionRef.networkManager.get(conn);
			if (networkManager instanceof NettyNetworkManager) {
				NettyNetworkManager netty = (NettyNetworkManager) networkManager;
				if (!netty_connected.get(netty)) {
					return;
				}

				// Send it to Netty, bypassing the PacketListener queue
				netty_queue.get(netty).add(packet);
				netty_channel.get(netty).write(packet);
				return;
			}
		}
		// Just send it in the default fashion
		sendPacket(player, packet, true);
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
