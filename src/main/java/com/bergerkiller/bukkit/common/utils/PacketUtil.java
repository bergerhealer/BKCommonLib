package com.bergerkiller.bukkit.common.utils;

import java.util.ArrayList;
import java.util.Map;

import net.minecraft.server.v1_4_R1.EntityPlayer;
import net.minecraft.server.v1_4_R1.Packet;
import net.minecraft.server.v1_4_R1.Packet29DestroyEntity;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.ProtocolLib;
import com.bergerkiller.bukkit.common.natives.NativeSilentPacket;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.protocol.PacketListener;

@SuppressWarnings("unchecked")
public class PacketUtil {
	public static final ArrayList<PacketListener>[] listeners = new ArrayList[256];
	public static final Map<Class<?>, Integer> packetsToIds = PacketFields.DEFAULT.<Map<Class<?>, Integer>>getField("a").get(null);

	public static Packet getEntityDestroyPacket(org.bukkit.entity.Entity... entities) {
		int[] ids = new int[entities.length];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = entities[i].getEntityId();
		}
		return new Packet29DestroyEntity(ids);
	}

	public static void sendPacket(Player player, Packet packet) {
		sendPacket(player, packet, true);
	}

	public static void sendPacket(Player player, Packet packet, boolean throughListeners) {
		EntityPlayer ep = NativeUtil.getNative(player);
		if (packet == null || player == null)
			return;
		if (ep.playerConnection == null || ep.playerConnection.disconnected)
			return;
		if (!throughListeners) {
			if(CommonPlugin.getInstance().libaryInstalled) {
				ProtocolLib.sendSilenVanillaPacket(player, packet);
				return;
			} else
				packet = new NativeSilentPacket(packet);
		}
		ep.playerConnection.sendPacket(packet);
	}
	
	public static void sendCommonPacket(Player player, CommonPacket packet, boolean throughListeners) {
		if (packet == null || player == null)
			return;
		
		EntityPlayer ep = NativeUtil.getNative(player);
		Packet toSend = (Packet) packet.getHandle();
		
		if (ep.playerConnection == null || ep.playerConnection.disconnected)
			return;
		if (!throughListeners) {
			if(CommonPlugin.getInstance().libaryInstalled) {
				ProtocolLib.sendSilenVanillaPacket(player, toSend);
				return;
			} else
				toSend = new NativeSilentPacket((Packet) packet.getHandle());
		}
		ep.playerConnection.sendPacket(toSend);
	}

	public static void broadcastChunkPacket(org.bukkit.Chunk chunk, Packet packet, boolean throughListeners) {
		if (chunk == null || packet == null) {
			return;
		}
		
		for (Player player : WorldUtil.getPlayers(chunk.getWorld())) {
			if (EntityUtil.isNearChunk(player, chunk.getX(), chunk.getZ(), CommonUtil.VIEW)) {
				sendPacket(player, packet, throughListeners);
			}
		}
	}

	public static void broadcastBlockPacket(Block block, Packet packet, boolean throughListeners) {
		broadcastBlockPacket(block.getWorld(), block.getX(), block.getZ(), packet, throughListeners);
	}

	public static void broadcastBlockPacket(org.bukkit.World world, final int x, final int z, Packet packet, boolean throughListeners) {
		if (world == null || packet == null) {
			return;
		}
		for (Player player : WorldUtil.getPlayers(world)) {
			if (EntityUtil.isNearBlock(player, x, z, CommonUtil.BLOCKVIEW)) {
				sendPacket(player, packet, throughListeners);
			}
		}
	}
	
	public static boolean callPacketReceiveEvent(Player player, Object packet) {
		if(player == null || packet == null)
			return true;
		
		return callPacketReceiveEvent(player, packet, PacketFields.DEFAULT.packetID.get(packet));
	}

	public static boolean callPacketReceiveEvent(Player player, Object packet, int id) {
		if(player == null || packet == null)
			return true;
		
		if(!LogicUtil.nullOrEmpty(listeners[id])) {
			CommonPacket cp = new CommonPacket(packet, id);
			PacketReceiveEvent ev = new PacketReceiveEvent(player, cp);
			
			for(PacketListener listener : listeners[id]) {
				listener.onPacketReceive(ev);
			}
			
			return !ev.isCancelled();
		} else
			return true;
	}
	
	public static boolean callPacketSendEvent(Player player, Object packet) {
		if(player == null || packet == null)
			return true;
		
		return callPacketSendEvent(player, packet, PacketFields.DEFAULT.packetID.get(packet));
	}
	
	public static boolean callPacketSendEvent(Player player, Object packet, int id) {
		if(player == null || packet == null)
			return true;
		
		if(!LogicUtil.nullOrEmpty(listeners[id])) {
			CommonPacket cp = new CommonPacket(packet, id);
			PacketSendEvent ev = new PacketSendEvent(player, cp);
			
			for(PacketListener listener : listeners[id]) {
				listener.onPacketSend(ev);
			}
			
			return !ev.isCancelled();
		} else
			return true;
	}

	public static void broadcastPacket(Packet packet, boolean throughListeners) {
		for (Player player : CommonUtil.getOnlinePlayers()) {
			sendPacket(player, packet, throughListeners);
		}
	}
	
	public static void addPacketListener(PacketListener listener, PacketType... packets) {
		if(listener == null || LogicUtil.nullOrEmpty(packets))
			return;
		
		for(PacketType packet : packets) {
			int id = PacketFields.DEFAULT.packetID.get(packet.getPacket());
			if(listeners[id] == null)
				listeners[id] = new ArrayList<PacketListener>();
			listeners[id].add(listener);
		}
	}
	
	public static void removePacketListener(PacketListener listener) {
		if(listener == null)
			return;
		
		for(int i = 0; i <= 256; i++) {
			if(!LogicUtil.nullOrEmpty(listeners[i])) {
				if(listeners[i].contains(listener))
					listeners[i].remove(listener);
			}
		}
	}

	public static void broadcastPacketNearby(Location location, double radius, Packet packet) {
		broadcastPacketNearby(location.getWorld(), location.getX(), location.getY(), location.getZ(), radius, packet);
	}

	public static void broadcastPacketNearby(org.bukkit.World world, double x, double y, double z, double radius, Packet packet) {
		CommonUtil.getServerConfig().sendPacketNearby(x, y, z, radius, NativeUtil.getNative(world).dimension, packet);
	}
}
