package com.bergerkiller.bukkit.common.protocol;

import net.minecraft.server.v1_4_R1.EntityPlayer;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class PacketManager {
	private CommonPlugin plugin;
	public static PacketManager instance;
	public boolean libaryInstalled = false;
	
	public PacketManager(CommonPlugin plugin) {
		this.plugin = plugin;
		instance = this;
	}
	
	public void enable() {
		PluginManager pm = plugin.getServer().getPluginManager();
		Plugin lib = pm.getPlugin("ProtocolLib");
		
		if(lib != null) {
			libaryInstalled = true;
			ProtocolLib.enable(plugin);
		}
		
		pm.registerEvents(new ProtocolListener(), plugin);
		
		//fix Disconnect.Spam kick happening w/o reason
		if(!libaryInstalled) {
			new Task(plugin) {
				@Override
				public void run() {
					for(Object obj : CommonUtil.getMCServer().getPlayerList().players) {
						EntityPlayer player = (EntityPlayer)obj;
						if(!player.playerConnection.disconnected) {
							player.playerConnection.d();
						}
					}
				}
			}.start(1, 1);
		}
	}
}
