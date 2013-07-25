package com.bergerkiller.bukkit.common.tab;

import java.util.Arrays;

class TabViewBasic extends TabView {
	private final int width, height;
	private final String[] text;
	private final int[] ping;

	public TabViewBasic(int width, int height) {
		this(width, height, true);
	}

	private TabViewBasic(int width, int height, boolean transferDefault) {
		this.width = width;
		this.height = height;
		this.text = new String[this.getSlotCount()];
		this.ping = new int[this.getSlotCount()];
		if (transferDefault) {
			Arrays.fill(this.text, TEXT_DEFAULT);
			Arrays.fill(this.ping, PING_DEFAULT);
		}
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
		this.ping[getIndex(x, y)] = ping;
		getController().setPing(this, x, y, ping);
	}

	@Override
	public void setText(int x, int y, String text) {
		this.text[getIndex(x, y)] = text;
		getController().setText(this, x, y, text);
	}

	@Override
	public void set(int x, int y, String text, int ping) {
		int index = getIndex(x, y);
		this.text[index] = text;
		this.ping[index] = ping;
		getController().setSlot(this, x, y, text, ping);
	}

	@Override
	public String getText(int x, int y) {
		return this.text[getIndex(x, y)];
	}

	@Override
	public int getPing(int x, int y) {
		return this.ping[getIndex(x, y)];
	}

	@Override
	public TabView clone() {
		TabViewBasic clonedResult = new TabViewBasic(getWidth(), getHeight(), false);
		System.arraycopy(this.text, 0, clonedResult.text, 0, this.getSlotCount());
		System.arraycopy(this.ping, 0, clonedResult.ping, 0, this.getSlotCount());
		return clonedResult;
	}

	@Override
	public void fillAll(String text, int ping) {
		Arrays.fill(this.text, text);
		Arrays.fill(this.ping, ping);
		getController().reloadAll(this);
	}

	@Override
	public void setAll(TabView view) {
		if (view instanceof TabViewBasic && view.getWidth() == this.getWidth() && view.getHeight() == this.getHeight()) {
			TabViewBasic other = (TabViewBasic) view;
			System.arraycopy(other.text, 0, this.text, 0, this.getSlotCount());
			System.arraycopy(other.ping, 0, this.ping, 0, this.getSlotCount());
			getController().reloadAll(this);
		} else {
			super.setAll(view);
		}
	}
}
