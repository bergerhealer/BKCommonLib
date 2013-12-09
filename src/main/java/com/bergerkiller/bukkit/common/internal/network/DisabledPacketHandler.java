package com.bergerkiller.bukkit.common.internal.network;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.PacketHandler;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.reflection.classes.EntityPlayerRef;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerConnectionRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;

public class DisabledPacketHandler implements PacketHandler {

	@Override
	public void removePacketListeners(Plugin plugin) {
	}

	@Override
	public void removePacketListener(PacketListener listener) {
	}

	@Override
	public void removePacketMonitor(PacketMonitor monitor) {
	}

	@Override
	public void addPacketListener(Plugin plugin, PacketListener listener, PacketType[] types) {
	}

	@Override
	public void addPacketMonitor(Plugin plugin, PacketMonitor monitor, PacketType[] types) {
	}

	@Override
	public void sendPacket(Player player, Object packet, boolean throughListeners) {
		if (!throughListeners) {
			throw new RuntimeException("Non-listened packet sending is non-functional right now (Update needed in BKCommonLib!)");
		}
		Object handle = Conversion.toEntityHandle.convert(player);
		if (!handle.getClass().equals(CommonUtil.getNMSClass("EntityPlayer"))) {
			return;
		}
		if (!PacketType.DEFAULT.isInstance(packet) || PlayerUtil.isDisconnected(player)) {
			return;
		}
		final Object connection = EntityPlayerRef.playerConnection.get(handle);
		PlayerConnectionRef.sendPacket(connection, packet);
	}

	@Override
	public void receivePacket(Player player, Object packet) {
	}

	@Override
	public Collection<Plugin> getListening(PacketType packetType) {
		return Collections.emptyList();
	}

	@Override
	public void transfer(PacketHandler to) {
	}

	@Override
	public String getName() {
		return "absolutely 'wow it is' NOTHING";
	}

	@Override
	public boolean onEnable() {
		CommonPlugin.LOGGER_NETWORK.log(Level.SEVERE, "Network handler is DISABLED: All packet handling routines are broken!");
		return true;
	}

	@Override
	public boolean onDisable() {
		return true;
	}

	@Override
	public void onPlayerJoin(Player player) {
	}

	@Override
	public long getPendingBytes(Player player) {
		return 0;
	}
}
