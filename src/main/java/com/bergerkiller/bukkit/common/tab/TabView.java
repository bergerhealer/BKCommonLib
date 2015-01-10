package com.bergerkiller.bukkit.common.tab;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.FromToCounter;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.CommonTabController;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Represents a live view of a single Player List a player sees when pressing tab.
 * Changes in this view can be sent to multiple players or all players on the server.
 * Many methods are available to ease the setting of text and ping values in this view.<br><br>
 * 
 * To obtain a TabView instance, call the {@link #createTab(int, int)} method with the desired
 * dimensions of the tab specified. It is only possible to create new tabs this way
 * when enabling your plugin. To create new Tab View instances at runtime, call {@link #clone()}
 * or {@link #cloneResize(int, int)}. Pay close attention to the Java Docs of these methods!
 */
public abstract class TabView {
	/**
	 * The maximum allowed width of a Tab View (amount of columns)
	 */
	public static final int MAX_WIDTH = 3;
	/**
	 * The maximum allowed height of a Tab View (amount of rows)
	 */
	public static final int MAX_HEIGHT = 20;
	/**
	 * A ping value constant that shows a single green line.
	 */
	public static final int PING_1 = 1000;
	/**
	 * A ping value constant that shows two green lines
	 */
	public static final int PING_2 = 600;
	/**
	 * A ping value constant that shows three green lines
	 */
	public static final int PING_3 = 300;
	/**
	 * A ping value constant that shows four green lines
	 */
	public static final int PING_4 = 150;
	/**
	 * A ping value constant that shows five green lines
	 */
	public static final int PING_5 = 0;
	/**
	 * A ping value constant that shows 'X - no connection'
	 */
	public static final int PING_NONE = -1;
	/**
	 * A ping value constant that shows all green lines
	 */
	public static final int PING_FULL = PING_5;
	/**
	 * A ping value constant that represents the default ping value, which is equal to {@link #PING_1}
	 */
	public static final int PING_DEFAULT = PING_1;
	/**
	 * A text value constant that represents the default 'no text', which shows empty (no) text
	 */
	public static final String TEXT_DEFAULT = "";
	/**
	 * A TabView constant that denotes the default view, that is, the player names and their ping
	 */
	public static final TabView DEFAULT = new TabViewDefault();
	/**
	 * A TabView constant that denotes an empty view, where all available slots are cleared
	 */
	public static final TabView EMPTY = new TabViewEmpty();

	/**
	 * Gets the width of this Tab View
	 * 
	 * @return Tab View width
	 */
	public abstract int getWidth();

	/**
	 * Gets the height of this Tab View
	 * 
	 * @return Tab View height
	 */
	public abstract int getHeight();

	/**
	 * Gets the total amount of slots of this Tab View.
	 * This is equal to [width x height]
	 * 
	 * @return total slot count
	 */
	public int getSlotCount() {
		return getWidth() * getHeight();
	}

	/**
	 * Sets the displayed ping of a slot
	 * 
	 * @param x - coordinate of the slot
	 * @param y - coordinate of the slot
	 * @param ping to display
	 */
	public abstract void setPing(int x, int y, int ping);

	/**
	 * Sets the displayed text of a slot
	 * 
	 * @param x - coordinate of the slot
	 * @param y - coordinate of the slot
	 * @param text to display
	 */
	public abstract void setText(int x, int y, String text);

	/**
	 * Sets the displayed ping and text of a slot
	 * 
	 * @param x - coordinate of the slot
	 * @param y - coordinate of the slot
	 * @param text to display
	 * @param ping to display
	 */
	public abstract void set(int x, int y, String text, int ping);

	/**
	 * Sets all the text and ping values of a single column. Points outside of the text or ping array
	 * bounds are ignored. If the text or ping arrays are null, the values are ignored.
	 * The text is set from top to bottom
	 * 
	 * @param column - x-coordinate of the column
	 * @param text values, null or empty to ignore
	 * @param ping values, null or empty to ignore
	 */
	public void setColumn(int column, String[] text, int[] ping) {
		setArea(column, 0, column, getHeight() - 1, text, ping);
	}

	/**
	 * Sets all the text and ping values of a single row. Points outside of the text or ping array
	 * bounds are ignored. If the text or ping arrays are null, the values are ignored.
	 * The text is set from top to bottom<br><br>
	 * 
	 * To set the values in reverse, simply use y2 for y1.
	 * 
	 * @param column - x-coordinate of the column
	 * @param y1 - y-coordinate of point 1 (start) of the column (inclusive)
	 * @param y2 - y-coordinate of point 2 (end) of the column (inclusive)
	 * @param text values, null or empty to ignore
	 * @param ping values, null or empty to ignore
	 */
	public void setColumn(int column, int y1, int y2, String[] text, int[] ping) {
		setArea(column, y1, column, y2, text, ping);
	}

	/**
	 * Sets all the text and ping values of a single row. Points outside of the text or ping array
	 * bounds are ignored. If the text or ping arrays are null, the values are ignored.
	 * The text is set from left to right
	 * 
	 * @param row - y-coordinate of the row
	 * @param text values, null or empty to ignore
	 * @param ping values, null or empty to ignore
	 */
	public void setRow(int row, String[] text, int[] ping) {
		setArea(0, row, getWidth() - 1, row, text, ping);
	}

	/**
	 * Sets all the text and ping values of a single row. Points outside of the text or ping array
	 * bounds are ignored. If the text or ping arrays are null, the values are ignored.
	 * The text is set from left to right<br><br>
	 * 
	 * To set the values in reverse, simply use x2 for x1.
	 * 
	 * @param row - y-coordinate of the row
	 * @param x1 - x-coordinate of point 1 (start) of the row (inclusive)
	 * @param x2 - x-coordinate of point 2 (end) of the row (inclusive)
	 * @param text values, null or empty to ignore
	 * @param ping values, null or empty to ignore
	 */
	public void setRow(int row, int x1, int x2, String[] text, int[] ping) {
		setArea(x1, row, x2, row, text, ping);
	}

	/**
	 * Sets all the text and ping values of an area. Points outside of the text or ping array
	 * bounds are ignored. If the text or ping arrays are null, the values are ignored.
	 * The text is set from left to right<br><br>
	 * 
	 * To set the values in reverse, simply use x2 for x1 and y2 for y1.<br><br>
	 * 
	 * @param x1 - x-coordinate of point 1 (inclusive)
	 * @param y1 - y-coordinate of point 1 (inclusive)
	 * @param x2 - x-coordinate of point 2 (inclusive)
	 * @param y2 - y-coordinate of point 2 (inclusive)
	 * @param text values, null or empty to ignore
	 * @param ping values, null or empty to ignore
	 */
	public void setArea(int x1, int y1, int x2, int y2, String[] text, int[] ping) {
		boundsCheck(x1, y1);
		boundsCheck(x2, y2);
		final boolean hasText = text != null && text.length > 0;
		final boolean hasPing = ping != null && ping.length > 0;
		if (!hasText && !hasPing) {
			return;
		}
		// Initialize all counters
		FromToCounter counterX = new FromToCounter(x1, x2);
		FromToCounter counterY = new FromToCounter(y1, y2);
		FromToCounter textCounter = new FromToCounter();
		FromToCounter pingCounter = new FromToCounter();
		if (hasText) {
			textCounter.reset(0, text.length - 1);
		}
		if (hasPing) {
			pingCounter.reset(0, ping.length - 1);
		}
		// Start processing the area
		while (counterX.hasNext()) {
			counterX.next();
			counterY.reset();
			while (counterY.hasNext()) {
				counterY.next();
				if (textCounter.hasNext()) {
					if (pingCounter.hasNext()) {
						// Text and ping are available
						set(counterX.get(), counterY.get(), text[textCounter.next()], ping[pingCounter.next()]);
					} else {
						// Only text is available
						setText(counterX.get(), counterY.get(), text[textCounter.next()]);
					}
				} else if (pingCounter.hasNext()) {
					// Only ping is available
					setPing(counterX.get(), counterY.get(), ping[pingCounter.next()]);
				}
			}
		}
	}

	/**
	 * Sets all text and ping values in the area to the text and ping values specified.
	 * Ordering of xy1 and xy2 is not important.
	 * 
	 * @param x1 - x-coordinate of point 1 (inclusive)
	 * @param y1 - y-coordinate of point 1 (inclusive)
	 * @param x2 - x-coordinate of point 2 (inclusive)
	 * @param y2 - y-coordinate of point 2 (inclusive)
	 * @param text to use
	 * @param ping to use
	 */
	public void fillArea(int x1, int y1, int x2, int y2, String text, int ping) {
		boundsCheck(x1, y1);
		boundsCheck(x2, y2);
		// Initialize all counters
		FromToCounter counterX = new FromToCounter(x1, x2);
		FromToCounter counterY = new FromToCounter(y1, y2);
		// Start processing the area
		while (counterX.hasNext()) {
			counterX.next();
			counterY.reset();
			while (counterY.hasNext()) {
				counterY.next();
				set(counterX.get(), counterY.get(), text, ping);
			}
		}
	}

	/**
	 * Fills an entire row with the text and ping specified.
	 * 
	 * @param row to fill
	 * @param x1 - x-coordinate of point 1 of the row (inclusive)
	 * @param x2 - x-coordinate of point 2 of the row (inclusive)
	 * @param text to use
	 * @param ping to use
	 */
	public void fillRow(int row, int x1, int x2, String text, int ping) {
		fillArea(x1, row, x2, row, text, ping);
	}

	/**
	 * Fills an entire row with the text and ping specified.
	 * 
	 * @param row to fill
	 * @param text to use
	 * @param ping to use
	 */
	public void fillRow(int row, String text, int ping) {
		fillRow(row, 0, getWidth() - 1, text, ping);
	}

	/**
	 * Fills an entire column with the text and ping specified.
	 * 
	 * @param column to fill
	 * @param y1 - y-coordinate of point 1 of the row (inclusive)
	 * @param y2 - y-coordinate of point 2 of the row (inclusive)
	 * @param text to use
	 * @param ping to use
	 */
	public void fillColumn(int column, int y1, int y2, String text, int ping) {
		fillArea(column, y1, column, y2, text, ping);
	}

	/**
	 * Fills an entire column with the text and ping specified.
	 * 
	 * @param column to fill
	 * @param text to use
	 * @param ping to use
	 */
	public void fillColumn(int column, String text, int ping) {
		fillColumn(column, 0, getHeight() - 1, text, ping);
	}

	/**
	 * Sets all slots of this Tab View to the text and ping values
	 * 
	 * @param text value to set to
	 * @param ping value to set to
	 */
	public void fillAll(String text, int ping) {
		int x, y;
		for (x = 0; x < getWidth(); x++) {
			for (y = 0; y < getHeight(); y++) {
				set(x, y, text, ping);
			}
		}
	}

	/**
	 * Sets all the contents of this Tab View to the text and ping arrays.
	 * Elements outside of these arrays or if the arrays are null are not set.
	 * 
	 * @param text array, null or empty to ignore
	 * @param ping array, null or empty to ignore
	 */
	public void setAll(String[] text, int[] ping) {
		setArea(0, 0, getWidth() - 1, getHeight() - 1, text, ping);
	}

	/**
	 * Sets all the contents of this Tab View to contain the contents of the Tab View.
	 * If the Tab View is larger than this one, only a portion that can fit in this Tab View
	 * is transferred over.
	 * 
	 * @param view to set to
	 */
	public void setAll(TabView view) {
		int width = Math.min(this.getWidth(), view.getWidth());
		int height = Math.min(this.getHeight(), view.getHeight());
		int x, y;
		for (x = 0; x < width; x++) {
			for (y = 0; y < height; y++) {
				this.set(x, y, view.getText(x, y), view.getPing(x, y));
			}
		}
	}

	/**
	 * Sets all the text values of an area. Points outside of the text array bounds are ignored.
	 * If the text array is null, the values are ignored. The text is set from left to right.<br><br>
	 * 
	 * To set the values in reverse, simply use x2 for x1 and y2 for y1.<br><br>
	 * 
	 * This is an overload for
	 * {@link #setArea(int, int, int, int, String[], int[]) setArea(x1, y1, x2, y2, text, ping)}
	 * to allow variable arguments instead of fixed arrays.
	 * 
	 * @param x1 - x-coordinate of point 1 (inclusive)
	 * @param y1 - y-coordinate of point 2 (inclusive)
	 * @param x2 - x-coordinate of point 1 (inclusive)
	 * @param y2 - y-coordinate of point 2 (inclusive)
	 * @param text to set to, null or empty to ignore (nothing happens then)
	 */
	public void setAreaText(int x1, int y1, int x2, int y2, String... text) {
		setArea(x1, y1, x2, y2, text, null);
	}

	/**
	 * Sets all the text and ping values of a single row.
	 * Points outside of the text array bounds are ignored.
	 * The text is set from left to right.<br><br>
	 * 
	 * To set the values in reverse, simply use x2 for x1.<br><br>
	 * 
	 * This is an overload for
	 * {@link #setRow(int, int, int, String[], int[]) setRow(row, x1, x2, text, ping)}
	 * to allow variable arguments instead of fixed arrays.
	 * 
	 * @param row - y-coordinate of the row
	 * @param x1 - x-coordinate of point 1 (start) of the row (inclusive)
	 * @param x2 - x-coordinate of point 2 (end) of the row (inclusive)
	 * @param text
	 */
	public void setRowText(int row, int x1, int x2, String... text) {
		this.setRow(row, x1, x2, text, null);
	}

	/**
	 * Sets all the text and ping values of a single row.
	 * Points outside of the text array bounds are ignored.
	 * The text is set from left to right.<br><br>
	 * 
	 * This is an overload for
	 * {@link #setRow(int, String[], int[]) setRow(row, text, ping)}
	 * to allow variable arguments instead of fixed arrays.
	 * 
	 * @param row to set
	 * @param text to set to
	 */
	public void setRowText(int row, String... text) {
		this.setRow(row, text, null);
	}

	/**
	 * Sets all the text and ping values of a single row.
	 * Points outside of the text array bounds are ignored.
	 * The text is set from top to bottom.<br><br>
	 * 
	 * To set the values in reverse, simply use y2 for y1.<br><br>
	 * 
	 * This is an overload for
	 * {@link #setColumn(int, int, int, String[], int[]) setColumn(column, y1, y2, text, ping)}
	 * to allow variable arguments instead of fixed arrays.
	 * 
	 * @param column - x-coordinate of the column
	 * @param y1 - y-coordinate of point 1 (start) of the column (inclusive)
	 * @param y2 - y-coordinate of point 2 (end) of the column (inclusive)
	 * @param text
	 */
	public void setColumnText(int column, int y1, int y2, String... text) {
		this.setColumn(column, y1, y2, text, null);
	}

	/**
	 * Sets all the text values of a single column. Points outside of the text or ping array
	 * bounds are ignored. The text is set from top to bottom.<br><br>
	 * 
	 * This is an overload for
	 * {@link #setColumn(int, String[], int[]) setColumn(column, text, ping)}
	 * to allow variable arguments instead of fixed arrays.
	 * 
	 * @param column - x-coordinate of the column
	 * @param text
	 */
	public void setColumnText(int column, String... text) {
		setColumn(column, text, null);
	}

	/**
	 * Sets all the text contents of this Tab View to the text array.
	 * Elements outside of this array or if the array is null are not set.<br><br>
	 * 
	 * This is an overload of {@link #setAll(String[], int[]) setAll(text, ping)} to allow variable arguments instead of fixed arrays.
	 * 
	 * @param text to set to, null or empty to ignore (nothing happens then)
	 */
	public void setAllText(String... text) {
		setAll(text, null);
	}

	/**
	 * Gets the text shown in the slot at the x and y coordinates specified
	 * 
	 * @param x - coordinate of the slot
	 * @param y - coordinate of the slot
	 * @return text set for that slot
	 */
	public abstract String getText(int x, int y);

	/**
	 * Gets the ping shown in the slot at the x and y coordinates specified
	 * 
	 * @param x - coordinate of the slot
	 * @param y - coordinate of the slot
	 * @return ping set for that slot
	 */
	public abstract int getPing(int x, int y);

	/**
	 * Gets whether the current Tab View is displayed to a player
	 * 
	 * @param player to check
	 * @return True if this Tab View is displayed to the Player, False if not
	 */
	public boolean isDisplayedTo(Player player) {
		return getController().getCurrentTab(player) == this;
	}

	/**
	 * Displays this Tab View to the player specified.
	 * The player is added to the viewers, previous viewers are not removed.
	 * If you wish to hide this Tab View again, use displayTo on the {@link #DEFAULT} or {@link #EMPTY} constants.
	 * 
	 * @param player
	 */
	public void displayTo(Player player) {
		getController().showTab(player, this);
	}

	/**
	 * Displays this Tab View to all online players and
	 * all players that will join in the future.
	 */
	public void displayToAll() {
		getController().showTabToAll(this);
	}

	@Override
	public TabView clone() {
		final int width = getWidth();
		final int height = getHeight();
		TabView clonedView = new TabViewBasic(width, height);
		// Transfer information
		int x, y;
		for (x = 0; x < width; x++) {
			for (y = 0; y < height; y++) {
				clonedView.set(x, y, this.getText(x, y), this.getPing(x, y));
			}
		}
		// Done
		return clonedView;
	}

	/**
	 * Creates a new clone of this TabView that is of a smaller size than this one.
	 * 
	 * @param newWidth for the tab, smaller than the width of this tab
	 * @param newHeight for the tab, smaller than the height of this tab
	 * @return cloned tab of the new size
	 */
	public TabView cloneResize(int newWidth, int newHeight) {
		if (newWidth > getWidth() || newHeight > getWidth()) {
			throw new IllegalArgumentException("Size {" + newWidth + ", " + newHeight + 
					"} is not a sub-size of {" + getWidth() + ", " + getHeight() + "}");
		}
		TabView clonedView = new TabViewBasic(newWidth, newHeight);
		// Transfer information
		int x, y;
		for (x = 0; x < newWidth; x++) {
			for (y = 0; y < newHeight; y++) {
				clonedView.set(x, y, this.getText(x, y), this.getPing(x, y));
			}
		}
		// Done
		return clonedView;
	}

	/**
	 * Creates a new Tab View that is a sub-area of this Tab View.
	 * Any changes made to this returned Tab View operate on this original Tab View.
	 * 
	 * @param x - coordinate of the top-left start position in this Tab View
	 * @param y - coordinate of the top-left start position in this Tab View
	 * @param width of the sub-region in this Tab View
	 * @param height of the sub-region in this Tab View
	 * @return a Tab View instance that is a sub-view of this one
	 * @throws IndexOutOfBoundsException if the area specified is not a sub-area of this one
	 */
	public TabView subView(int x, int y, int width, int height) {
		boundsCheck(x, y);
		if (x + width > this.getWidth()) {
			throw new IndexOutOfBoundsException("Sub-area is too wide (x{" + x + "} + width{" + width + "} > " + getWidth() + ")");
		}
		if (y + height > this.getHeight()) {
			throw new IndexOutOfBoundsException("Sub-area is too high (x{" + x + "} + width{" + width + "} > " + getWidth() + ")");
		}
		return new TabViewSubView(this, x, y, width, height);
	}

	/**
	 * Checks whether a coordinate is part of this Tab View.
	 * 
	 * @param x - coordinate
	 * @param y - coordinate
	 */
	protected void boundsCheck(int x, int y) {
		if (x < 0 || x > this.getWidth() || y < 0 || y > this.getHeight()) {
			throw new IndexOutOfBoundsException("The coordinate [" + x + ", " + y + "] is out of bounds!");
		}
	}

	/**
	 * Gets the index (for a 1-dimensional array) from an x and y coordinate.
	 * Also performs a bounds check.
	 * 
	 * @param x - coordinate
	 * @param y - coordinate
	 * @return 1D-index
	 */
	protected int getIndex(int x, int y) {
		boundsCheck(x, y);
		return x + this.getWidth() * y;
	}

	/**
	 * Creates a new TabView. This method can only be called
	 * while enabling your plugin. Do not call this method while
	 * players are joining or logging in - it does NOT work!<br><br>
	 * 
	 * If you need tab instances created at runtime, use clones from one
	 * tab created when enabling your plugin. You can use smaller sizes than the
	 * original tab, but you can not use larger sizes. You are restricted
	 * to defining the maximum tab size you expect to need when enabling.
	 * 
	 * @param width for the new tab
	 * @param height for the new tab
	 * @return new Tab
	 */
	public static TabView createTab(int width, int height) {
		if (CommonUtil.isServerStarted()) {
			throw new IllegalStateException("Can not create new tabs while the server is running, create your tabs while enabling and optionally clone those");
		}
		if (width < 1 || height < 1 || width > MAX_WIDTH || height > MAX_HEIGHT) {
			throw new IllegalArgumentException("Dimension {" + width + ", " + height + "} is outside of the allowed bounds!");
		}
		getController().requestNewSize(width, height);
		return new TabViewBasic(width, height);
	}

	/**
	 * Obtains the Tab Controller used to communicate changes to the server
	 * 
	 * @return tab controller
	 */
	protected static CommonTabController getController() {
		return CommonPlugin.getInstance().getTabController();
	}
}
