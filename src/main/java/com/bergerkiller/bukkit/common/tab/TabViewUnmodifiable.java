package com.bergerkiller.bukkit.common.tab;

abstract class TabViewUnmodifiable extends TabView {
	@Override
	public void setPing(int x, int y, int ping) {
		throw new UnsupportedOperationException("This TabView is unmodifiable");
	}

	@Override
	public void setText(int x, int y, String text) {
		throw new UnsupportedOperationException("This TabView is unmodifiable");
	}

	@Override
	public void set(int x, int y, String text, int ping) {
		throw new UnsupportedOperationException("This TabView is unmodifiable");
	}
}
