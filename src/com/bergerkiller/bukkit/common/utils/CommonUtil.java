package com.bergerkiller.bukkit.common.utils;

import java.util.List;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerConfigurationManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class CommonUtil {
	public static final int view = Bukkit.getServer().getViewDistance();
	public static final int viewWidth = view + view + 1;
	public static final int chunkArea = viewWidth * viewWidth;
	public static final int blockView = 32 + (view << 4);
	
	public static void sendMessage(Object sender, Object message) {
		if (message != null) {
			String msg = message.toString();
			if (msg.length() > 0) {
				if (sender instanceof CommandSender) {
					if (!(sender instanceof Player)) {
						message = ChatColor.stripColor(msg);
					}
					for (String line : msg.split("\n")) {
						((CommandSender) sender).sendMessage(line);
					}
				}
			}
		}
	}
	
	public static void sendListMessage(Object sender, String delimiter, Object[] items) {
		String msgpart = null;
		String item;
		for (Object oitem : items) {
			item = oitem.toString();
			//display it
			if (msgpart == null || msgpart.length() + item.length() < 70) {
				if (msgpart == null) {
					msgpart = item;
				} else {
					msgpart += ChatColor.WHITE + delimiter + item;
				}
			} else {
				sendMessage(sender, msgpart);
				msgpart = item;
			}
		}
		sendMessage(sender, msgpart);
	}
	
	public static <T extends Event> T callEvent(T event) {
		Bukkit.getServer().getPluginManager().callEvent(event);
		return event;
	}
	
	public static MinecraftServer getMCServer() {
		return getCraftServer().getServer();
	}
	
	public static CraftServer getCraftServer() {
		return (CraftServer) Bukkit.getServer();
	}
	
	public static ServerConfigurationManager getServerConfig() {
		return getCraftServer().getHandle();
	}
	
	@SuppressWarnings("unchecked")
	public static List<EntityPlayer> getOnlinePlayers() {
		return (List<EntityPlayer>) getServerConfig().players;
	}
	
	public static void heartbeat() {
		broadcast("HEARTBEAT: " + System.currentTimeMillis());
	}
	public static void broadcast(Object message) {
		if (message != null) {
			for (EntityPlayer ep : getOnlinePlayers()) {
				if (ep.netServerHandler == null) continue;
				ep.netServerHandler.sendMessage(message.toString());
			}
		}
	}
	
}
