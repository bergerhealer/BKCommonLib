package com.bergerkiller.bukkit.common.utils;

import java.util.List;

import net.minecraft.server.Chunk;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PacketUtil {
	public static void sendPacket(Player player, Packet packet, boolean throughListeners) {
		sendPacket(EntityUtil.getNative(player), packet, throughListeners);
	}
	public static void sendPacket(EntityPlayer player, Packet packet, boolean throughListeners) {
		if (packet == null || player == null) return;
		if (player.netServerHandler == null || player.netServerHandler.disconnected) return;
		if (throughListeners) {
			player.netServerHandler.sendPacket(packet);
		} else if (player.netServerHandler.networkManager != null) {
			player.netServerHandler.networkManager.queue(packet);
		}
	}
	
	public static void broadcastChunkPacket(org.bukkit.Chunk chunk, Packet packet, boolean throughListeners) {
		broadcastChunkPacket(WorldUtil.getNative(chunk), packet, throughListeners);
	}	
	@SuppressWarnings("unchecked")
	public static void broadcastChunkPacket(Chunk chunk, Packet packet, boolean throughListeners) {
		if (chunk == null || packet == null) return;
		for (EntityPlayer ep : (List<EntityPlayer>) chunk.world.players) { 
			if (EntityUtil.isNearChunk(ep, chunk.x, chunk.z, CommonUtil.view)) {
				sendPacket(ep, packet, throughListeners);
			}
		}
	}
	
	public static void broadcastBlockPacket(Block block, Packet packet, boolean throughListeners) {
		broadcastBlockPacket(block.getWorld(), block.getX(), block.getZ(), packet, throughListeners);
	}
	public static void broadcastBlockPacket(org.bukkit.World world, final int x, final int z, Packet packet, boolean throughListeners) {
		broadcastBlockPacket(WorldUtil.getNative(world), x, z, packet, throughListeners);
	}
	@SuppressWarnings("unchecked")
	public static void broadcastBlockPacket(World world, final int x, final int z, Packet packet, boolean throughListeners) {
		if (world == null || packet == null) return;
		for (EntityPlayer ep :  (List<EntityPlayer>) world.players) {
			if (EntityUtil.isNearBlock(ep, x, z, CommonUtil.blockView)) {
				sendPacket(ep, packet, throughListeners);
			}
		}
	}
	
	public static void broadcastPacket(Packet packet, boolean throughListeners) {
		for (EntityPlayer ep : CommonUtil.getOnlinePlayers()) {
			sendPacket(ep, packet, throughListeners);
		}
	}
	
	public static void broadcastPacketNearby(Location location, double radius, Packet packet) {
		broadcastPacketNearby(location.getWorld(), location.getX(), location.getY(), location.getZ(), radius, packet);
	}
	public static void broadcastPacketNearby(org.bukkit.World world, double x, double y, double z, double radius, Packet packet) {
		broadcastPacketNearby(WorldUtil.getNative(world), x, y, z, radius, packet);
	}
	public static void broadcastPacketNearby(World world, double x, double y, double z, double radius, Packet packet) {
		CommonUtil.getServerConfig().sendPacketNearby(x, y, z, radius, ((WorldServer) world).dimension, packet);
	}
}
