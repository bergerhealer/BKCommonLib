package com.bergerkiller.bukkit.common.tab;

final class TabViewEmpty extends TabViewUnmodifiable {
	@Override
	public int getWidth() {
		return getController().getWidth();
	}

	@Override
	public int getHeight() {
		return getController().getHeight();
	}

	@Override
	public String getText(int x, int y) {
		return TEXT_DEFAULT;
	}

	@Override
	public int getPing(int x, int y) {
		return PING_DEFAULT;
	}
}
