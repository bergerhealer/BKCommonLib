package com.bergerkiller.bukkit.common.server;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.reflection.classes.EntityPlayerRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;

public class SportBukkitServer extends CraftBukkitServer {
	private Task removeQueueFlusher;

	@Override
	public boolean init() {
		if (!super.init()) {
			return false;
		}
		return Bukkit.getServer().getVersion().contains("SportBukkit");
	}

	@Override
	public void postInit() {
		super.postInit();
		// Checkup that the Entity Remove queue for players is indeed missing
		if (SafeField.contains(EntityPlayerRef.TEMPLATE.getType(), "removeQueue")) {
			CommonPlugin.LOGGER.log(Level.WARNING, "Entity Removal queue of SportBukkit was added again! (update needed?)");
		}
	}

	@Override
	public List<Integer> getEntityRemoveQueue(Player player) {
		return CommonPlugin.getInstance().getPlayerMeta(player).getRemoveQueue();
	}

	@Override
	public void enable(CommonPlugin plugin) {
		removeQueueFlusher = new EntityRemoveQueueFlusher(plugin).start(1, 1);
	}

	@Override
	public void disable(CommonPlugin plugin) {
		Task.stop(removeQueueFlusher);
		removeQueueFlusher = null;
	}

	@Override
	public String getServerName() {
		return "SportBukkit";
	}

	private class EntityRemoveQueueFlusher extends Task {

		public EntityRemoveQueueFlusher(JavaPlugin plugin) {
			super(plugin);
		}

		@Override
		public void run() {
			for (Player player : CommonUtil.getOnlinePlayers()) {
				List<Integer> idList = getEntityRemoveQueue(player);
				if (!idList.isEmpty()) {
					PacketUtil.sendPacket(player, PacketType.OUT_ENTITY_DESTROY.newInstance(idList));
					idList.clear();
				}
			}
		}
	}
}
