package com.bergerkiller.bukkit.common.map.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapDisplay;

/**
 * A single rectangular element on the map
 */
public class MapWidget {
    protected MapWidget parent;
    protected MapDisplay display;
    protected MapDisplay.Layer layer;
    protected MapCanvas view;
    private int _x, _y, _width, _height; // live bounds (relative to parent)
    private int _lastX, _lastY, _lastWidth, _lastHeight; // last drawn bounds (absolute)
    private boolean _invalidated;
    private boolean _focusable;
    private boolean _focused;
    private boolean _attached;
    private List<MapWidget> _children;

    public MapWidget() {
        this._invalidated = true;
        this._focusable = false;
        this._focused = false;
        this._attached = false;
        this._children = Collections.emptyList();
        this.display = null;
        this.layer = null;
        this.parent = null;
        this._lastX = this._x = 0;
        this._lastY = this._y = 0;
        this._lastWidth = this._width = 0;
        this._lastHeight = this._height = 0;
    }

    /**
     * Called when the widget is attached to a map display
     */
    public void onAttached() {
    }

    /**
     * Called when a widget is removed from a map display
     */
    public void onDetached() {
    }

    /**
     * Called every tick to refresh this widget.
     * The base {@link #update()} automatically calls onDraw() when the widget is invalidated.
     */
    public void onTick() {
    }

    /**
     * Called when the widget should be re-drawn onto the map display.
     * When this is called, the old area the widget was displayed has already been cleared.
     */
    public void onDraw() {
    }

    /**
     * Gets the x-position of this widget relative to the parent
     * 
     * @return x-position
     */
    public final int getX() {
        return this._x;
    }

    /**
     * Gets the y-position of this widget relative to the parent
     * 
     * @return y-position
     */
    public final int getY() {
        return this._y;
    }

    /**
     * Gets the width of this widget
     * 
     * @return width
     */
    public final int getWidth() {
        return this._width;
    }

    /**
     * Gets the height of this widget
     * 
     * @return height
     */
    public final int getHeight() {
        return this._height;
    }

    /**
     * Gets the x-position of this widget as drawn onto the map display
     * 
     * @return absolute x-position
     */
    public final int getAbsoluteX() {
        return (this.parent == null) ? this._x : (this._x + this.parent.getAbsoluteX());
    }

    /**
     * Gets the y-position of this widget as drawn onto the map display
     * 
     * @return absolute y-position
     */
    public final int getAbsoluteY() {
        return (this.parent == null) ? this._y : (this._y + this.parent.getAbsoluteY());
    }

    /**
     * Gets whether or not it is possible for this widget to be focused
     * 
     * @return True if this widget can receive focus
     */
    public final boolean isFocusable() {
        return this._focusable;
    }

    /**
     * Sets whether or not it is possible for this widget to be focused
     * 
     * @param focusable to set to
     */
    public final void setFocusable(boolean focusable) {
        this._focusable = focusable;
    }

    /**
     * Gets whether this widget currently has focus
     * 
     * @return True if focused
     */
    public final boolean isFocused() {
        return this._focused;
    }

    /**
     * Sets whether this widget currently has focus.
     * 
     * @param focused to set to
     */
    public final void setFocused(boolean focused) {
        if (this._focused != focused) {
            this._focused = focused;
            this.invalidate();
        }
    }

    /**
     * Gets an immutable list of children of this widget.
     * These children are drawn on top of this widget.
     * 
     * @return list of children
     */
    public final List<MapWidget> getWidgets() {
        return this._children;
    }

    /**
     * Removes all previously added widgets
     */
    public final void clearWidgets() {
        Iterator<MapWidget> child_iter = this._children.iterator();
        while (child_iter.hasNext()) {
            child_iter.next().handleDetach();
            child_iter.remove();
        }
    }

    /**
     * Adds a new child widget to this widget
     * 
     * @param child to add
     */
    public final void addWidget(MapWidget widget) {
        if (this._children.isEmpty()) {
            this._children = new ArrayList<MapWidget>(1);
        }
        widget.parent = this;
        widget._attached = false;
        this._children.add(widget);
    }

    /**
     * Removes a child widget from this widget. The old area the widget occupied is cleared.
     * 
     * @param child to remove
     * @return True if the child widget was removed
     */
    public final boolean removeWidget(MapWidget widget) {
        if (this._children.isEmpty() || !this._children.remove(widget)) {
            return false;
        }

        widget.handleDetach();
        return true;
    }

    /**
     * Removes all the displayed contents of this widget
     * 
     * @return this widget
     */
    public final MapWidget clear() {
        if (this.layer != null) {
            this.layer.clearRectangle(this._x, this._y, this._width, this._height);
        }
        return this;
    }

    /**
     * Invalidates the widget, causing it to be redrawn in the future
     */
    public final void invalidate() {
        this._invalidated = true;
    }

    /**
     * Performs {@link #onTick()} and {@link #onDraw()} update operations, and other widget refreshing logic
     * needed to run in the background. Attaches newly added widgets before any updates are performed.
     */
    public final void update() {
        this.handleAttach();
        this.clearInvalidatedAreas();
        this.handleTick();
        this.clearInvalidatedAreas();
        this.handleDraw();
    }

    /**
     * Changes the position of this widget relative to the parent
     * 
     * @param x - position
     * @param y - position
     * @return this widget
     */
    public final MapWidget setPosition(int x, int y) {
        return this.setBounds(x, y, this._width, this._height);
    }

    /**
     * Changes the size of this widget
     * 
     * @param width
     * @param height
     * @return this widget
     */
    public final MapWidget setSize(int width, int height) {
        return this.setBounds(this._x, this._y, width, height);
    }

    /**
     * Changes the bounds of this widget
     * 
     * @param x - position relative to the parent
     * @param y - position relative to the parent
     * @param width
     * @param height
     * @return this widget
     */
    public final MapWidget setBounds(int x, int y, int width, int height) {
        this._x = x;
        this._y = y;
        this._width = width;
        this._height = height;
        this.refreshView();
        this.invalidate();
        return this;
    }

    private final void refreshView() {
        if (this.layer != null) {
            this.view = this.layer.getView(this.getAbsoluteX(), this.getAbsoluteY(), this._width, this._height);
        }
    }

    // Convenience method, see below
    private final void clearInvalidatedAreas() {
        if (this.parent != null) {
            this.clearInvalidatedAreas(this.parent.getAbsoluteX(), this.parent.getAbsoluteY());
        } else {
            this.clearInvalidatedAreas(0, 0);
        }
    }

    /*
     * Walks down the widget tree, clearing the areas behind widgets that have been
     * invalidated and were previously drawn. The absolute x/y is an optimization
     * to make it easier to calculate the new bounds.
     */
    private final void clearInvalidatedAreas(int absoluteX, int absoluteY) {
        // Add position of this widget to absoluteX/Y
        absoluteX += this._x;
        absoluteY += this._y;

        // Only do this when the widget has been drawn before
        if (this._lastWidth != 0 && this._lastHeight != 0) {

            // Detect changes in bounds and invalidate when it happens
            if (this._lastWidth != this._width ||
                this._lastHeight != this._height ||
                this._lastX != absoluteX ||
                this._lastY != absoluteY) {
                this.invalidate();
            }

            // Clear old region when invalidated
            if (this._invalidated) {
                this.layer.clearRectangle(this._lastX, this._lastY, this._lastWidth, this._lastHeight);
                this._lastWidth = 0;
                this._lastHeight = 0;
            }
        }

        // Repeat for children of this widget
        for (MapWidget child : this._children) {
            child.clearInvalidatedAreas(absoluteX, absoluteY);
        }
    }

    // Handles onAttached() (and parent display/view information) of this widget and all children
    private final void handleAttach() {
        if (!this._attached) {
            this._attached = true;
            if (this.parent != null) {
                // This is done for children of widgets
                this.display = this.parent.display;
                this.layer = this.parent.layer.next();
                this.refreshView();
            } else if (this.layer == null && this.display != null) {
                // This is done for the root node (covers entire display)
                this.layer = this.display.getLayer();
                this.view = this.layer;
            }
            this.onAttached();
        }

        // Also attach (new) children
        for (MapWidget child : this._children) {
            child.handleAttach();
        }
    }

    // Handles onTick() of this widget and all children
    private final void handleTick() {
        this.onTick();
        for (MapWidget child : this._children) {
            child.handleTick();
        }
    }

    // Convenience method, see below
    private final void handleDraw() {
        if (this.parent != null) {
            this.handleDraw(this.parent.getAbsoluteX(), this.parent.getAbsoluteY());
        } else {
            this.handleDraw(0, 0);
        }
    }

    // Handles the drawing of this widget (if invalidated) and all children
    private final void handleDraw(int absoluteX, int absoluteY) {
        // Add own coordinates
        absoluteX += this._x;
        absoluteY += this._y;

        // If invalidated, redraw
        if (this._invalidated) {
            this.view = this.layer.getView(absoluteX, absoluteY, this._width, this._height);
            this.onDraw();
            this._lastX = absoluteX;
            this._lastY = absoluteY;
            this._lastWidth = this._width;
            this._lastHeight = this._height;
            this._invalidated = false;
        }

        // Draw children of this widget, relative to the current coordinates
        for (MapWidget child : this._children) {
            child.handleDraw(absoluteX, absoluteY);
        }
    }

    // Handles detachment of this widget and all its children
    private final void handleDetach() {
        if (this._attached) {
            this._attached = false;

            // Detach children first
            Iterator<MapWidget> child_iter = this._children.iterator();
            while (child_iter.hasNext()) {
                child_iter.next().handleDetach();
                child_iter.remove();
            }

            // Fire onDetached
            this.onDetached();

            // Clear our occupied area on the canvas
            this.clear();
        }
    }
}
