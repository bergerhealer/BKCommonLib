package com.bergerkiller.bukkit.common.tab;

import java.util.Iterator;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;

final class TabViewDefault extends TabViewUnmodifiable {

	@Override
	public int getWidth() {
		return getController().getDefaultWidth();
	}

	@Override
	public int getHeight() {
		return getController().getDefaultHeight();
	}

	@Override
	public String getText(int x, int y) {
		Player player = getPlayer(x, y);
		return player == null ? "" : player.getPlayerListName();
	}

	@Override
	public int getPing(int x, int y) {
		Player player = getPlayer(x, y);
		return player == null ? 0 : PlayerUtil.getPing(player);
	}

	private Player getPlayer(int x, int y) {
		int index = x + this.getWidth() * y - 1;
		Iterator<Player> iter = CommonUtil.getOnlinePlayers().iterator();
		for (int i = 0; i < index && iter.hasNext(); i++) {
			iter.next();
		}
		return iter.hasNext() ? iter.next() : null;
	}
}
