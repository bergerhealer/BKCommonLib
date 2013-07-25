package com.bergerkiller.bukkit.common.tab;

class TabViewSubView extends TabView {
	private final TabView parent;
	private final int offset_x, offset_y, width, height;

	public TabViewSubView(TabView parent, int offset_x, int offset_y, int width, int height) {
		this.parent = parent;
		this.offset_x = offset_x;
		this.offset_y = offset_y;
		this.width = width;
		this.height = height;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public void setPing(int x, int y, int ping) {
		boundsCheck(x, y);
		this.parent.setPing(x + offset_x, y + offset_y, ping);
	}

	@Override
	public void setText(int x, int y, String text) {
		boundsCheck(x, y);
		this.parent.setText(x + offset_x, y + offset_y, text);
	}

	@Override
	public void set(int x, int y, String text, int ping) {
		boundsCheck(x, y);
		this.parent.set(x + offset_x, y + offset_y, text, ping);
	}

	@Override
	public String getText(int x, int y) {
		boundsCheck(x, y);
		return this.parent.getText(x + offset_x, y + offset_y);
	}

	@Override
	public int getPing(int x, int y) {
		boundsCheck(x, y);
		return this.parent.getPing(x + offset_x, y + offset_y);
	}
}
