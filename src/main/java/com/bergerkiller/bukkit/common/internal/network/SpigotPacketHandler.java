package com.bergerkiller.bukkit.common.internal.network;

import java.lang.reflect.Field;
import java.util.Queue;

import net.minecraft.server.Connection;
import net.minecraft.server.INetworkManager;
import net.minecraft.server.Packet;
import net.minecraft.server.PlayerConnection;

import org.bukkit.entity.Player;
import org.spigotmc.netty.NettyNetworkManager;
import org.spigotmc.netty.PacketListener;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.reflection.classes.EntityPlayerRef;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerConnectionRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * A packet handler implementation that uses Spigot's netty service
 */
public class SpigotPacketHandler extends PacketHandlerHooked {
	private static final FieldAccessor<PacketListener[]> bakedListeners = SafeField.create(PacketListener.class, "baked");
	private static final FieldAccessor<Queue<Packet>> realPacketQueue;
	private static final PacketListener[] EMPTY_PACKETLISTENER_ARRAY = new PacketListener[0];
	private static SpigotPacketListener listener;

	static {
		FieldAccessor<Queue<Packet>> realQueue = null;
		try {
			Field field = NettyNetworkManager.class.getDeclaredField("realQueue");
			realQueue = new SafeField<Queue<Packet>>(field);
		} catch (Throwable t) {
		}
		realPacketQueue = realQueue;
	}

	@Override
	public String getName() {
		return "the Spigot server 'Netty' service";
	}

	@Override
	public boolean onEnable() {
		if (!super.onEnable()) {
			return false;
		}
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
		// NOTE: Below code is all obtained from Spigot source
		// It may change at ANY time, so keeping it up-to-date is very important
		NettyNetworkManager netty = CommonUtil.tryCast(EntityPlayerRef.getNetworkManager(player), NettyNetworkManager.class);
		if (netty != null) {
			synchronized (netty) {
				// Temporarily clear the baked listeners
				final PacketListener[] oldBaked = bakedListeners.get(null);
				bakedListeners.set(null, EMPTY_PACKETLISTENER_ARRAY);

				// Queue
				netty.queue((Packet) packet);

				// Restore
				bakedListeners.set(null, oldBaked);
			}
			return;
		}
		// Just send it in the default fashion
		sendPacket(player, packet, true);
	}

	@Override
	public long getPendingBytes(Player player) {
		// Old version of Spigot - there is no way to get the size (it's internal)
		if (realPacketQueue == null) {
			return 0L;
		}
		final Object playerHandle = Conversion.toEntityHandle.convert(player);
		final Object playerConnection = EntityPlayerRef.playerConnection.get(playerHandle);
		final Object nm = PlayerConnectionRef.networkManager.get(playerConnection);
		// We can only work on Netty Network manager implementations, other INetworkManager implementations are unknown to us
		if (!(nm instanceof NettyNetworkManager)) {
			return 0L;
		}
		synchronized (nm) {
			Queue<Packet> queue = realPacketQueue.get(nm);
			long queuedsize = 0;
			for (Packet p : queue) {
				queuedsize += PacketFields.DEFAULT.getPacketSize(p) + 1;
			}
			return queuedsize;
		}
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
