package com.bergerkiller.bukkit.common.utils;

import net.minecraft.server.v1_4_6.EntityPlayer;
import net.minecraft.server.v1_4_6.Packet;
import net.minecraft.server.v1_4_6.Packet29DestroyEntity;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.natives.NativeSilentPacket;

public class PacketUtil {
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
			packet = new NativeSilentPacket(packet);
		}
		ep.playerConnection.sendPacket(packet);
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

	public static void broadcastPacket(Packet packet, boolean throughListeners) {
		for (Player player : CommonUtil.getOnlinePlayers()) {
			sendPacket(player, packet, throughListeners);
		}
	}

	public static void broadcastPacketNearby(Location location, double radius, Packet packet) {
		broadcastPacketNearby(location.getWorld(), location.getX(), location.getY(), location.getZ(), radius, packet);
	}

	public static void broadcastPacketNearby(org.bukkit.World world, double x, double y, double z, double radius, Packet packet) {
		CommonUtil.getServerConfig().sendPacketNearby(x, y, z, radius, NativeUtil.getNative(world).dimension, packet);
	}
}
