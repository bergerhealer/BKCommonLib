package com.bergerkiller.bukkit.common.map.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.bergerkiller.bukkit.common.events.map.MapClickEvent;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapDisplayEvents;
import com.bergerkiller.bukkit.common.map.MapPlayerInput.Key;
import com.bergerkiller.bukkit.common.map.util.MapWidgetNavigator;

/**
 * A single rectangular element on the map
 */
public class MapWidget implements MapDisplayEvents {
    protected MapWidgetRoot root;
    protected MapWidget parent;
    protected MapDisplay display;
    protected MapDisplay.Layer layer;
    protected MapCanvas view;
    private int _x, _y, _width, _height; // live bounds (relative to parent)
    private int _lastX, _lastY, _lastWidth, _lastHeight; // last drawn bounds (absolute)
    private boolean _invalidated;
    private boolean _focusable;
    private boolean _attached;
    private boolean _boundsChanged;
    private List<MapWidget> _children;

    public MapWidget() {
        this._invalidated = true;
        this._boundsChanged = true;
        this._focusable = false;
        this._attached = false;
        this._children = Collections.emptyList();
        this.display = null;
        this.layer = null;
        this.parent = null;
        this.root = null;
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
     * Called when the bounding box area of this widget was changed
     */
    public void onBoundsChanged() {
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
     * Gets whether this widget currently has focus. The focused widget is the widget which
     * will be activated when the user presses spacebar.
     * 
     * @return True if focused
     */
    public final boolean isFocused() {
        return (this.root != null) && (this == this.root.getFocusedWidget());
    }

    /**
     * Gives this widget the focus. When the user presses spacebar, this will be the widget
     * that is activated.
     */
    public final void focus() {
        if (this.root != null) {
            this.root.setFocusedWidget(this);
        }
    }

    /**
     * Gets whether this widget has been activated. The activated widget is the widget to which
     * all user input (W/A/S/D/Spacebar/Shift) is redirected.
     * 
     * @return True if activated
     */
    public final boolean isActivated() {
        return (this.root != null) && (this == this.root.getActivatedWidget());
    }

    public final void activate() {
        if (this.root != null) {
            this.root.setActivatedWidget(this);
        }
    }

    /**
     * Gets whether this map widget is a potential widget that can be focused
     * by navigating using W/A/S/D. If true, this widget can be focused
     * without changing the activated widget. If false, the widget can only be
     * focused by changing the currently activated widget.<br>
     * <br>
     * If this widget is not {@link #isFocusable()} this always returns false.
     * 
     * @return True if this widget can be focused by navigating to it
     */
    public final boolean isNavigableFocus() {
        if (!this.isFocusable() || this.root == null) {
            return false;
        }
        MapWidget activated = this.root.getActivatedWidget();
        MapWidget parent = this.parent;
        while (parent != null) {
            if (parent == activated) {
                return true;
            }
            parent = parent.parent;
        }
        return false;
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
        widget.root = this.root;
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
        this._boundsChanged = true;
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
                this.root = this.parent.root;
                this.display = this.parent.display;
                this.layer = this.parent.layer.next();
                this.refreshView();
            } else if (this.layer == null && this.display != null) {
                // This is done for the root node (covers entire display)
                this.layer = this.display.getTopLayer().next();
                this.view = this.layer;
            }
            this.onAttached();

            // If the attached widget can be focused, and no widget is focused yet, focus it
            // Only do this if this current widget has an activated parent
            if (this.root.getFocusedWidget() == null && this.isNavigableFocus()) {
                this.focus();
            }
        }

        // Also attach (new) children
        for (MapWidget child : this._children) {
            child.handleAttach();
        }
    }

    // Handles onTick() of this widget and all children
    private final void handleTick() {
        this.onTick();
        if (this._boundsChanged) {
            this._boundsChanged = false;
            this.onBoundsChanged();
        }
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

        // For all invalidated children, if their bounding box intersects with another widget,
        // invalidate the intersected widgets too to enable proper overlay. This is a work-around
        // for an annoying to use z-buffer...
        for (MapWidget child : this._children) {
            if (child._invalidated) {
                for (MapWidget otherChild : this._children) {
                    if (otherChild == child || otherChild._invalidated) {
                        continue;
                    }
                    if (
                        (otherChild.getX() + otherChild.getWidth()) >= child.getX() &&
                        otherChild.getX() <= (child.getX() + child.getWidth()) &&
                        (otherChild.getY() + otherChild.getHeight()) >= child.getY() &&
                        otherChild.getY() <= (child.getY() + child.getHeight())
                    ) {
                        otherChild._invalidated = true;
                    }
                }
            }
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

    /**
     * Handles key navigation of the widgets, changing which widgets are focused and activated.
     * 
     * @param event
     */
    protected void handleNavigation(MapKeyEvent event) {
        MapWidget focused = this.root.getFocusedWidget();
        if (event.getKey() == Key.ENTER) {
            // Activate the currently focused widget when activated
            // If none exists, we can't do this and the enter key should be handled by the widget
            if (focused != null) {
                focused.activate();
            }
        } else if (event.getKey() == Key.BACK) {
            // De-activate ourselves, moving control back to the closest parent that can be focused
            MapWidget tmp = this.parent;
            while (tmp != null) {
                if (tmp.isFocusable()) {
                    break;
                }
                tmp = tmp.parent;
            }
            if (tmp == null) {
                tmp = this.root;
            }
            tmp.activate();
        } else {
            // W/A/S/D controls to switch the focused widget
            // Collect all the widgets eligible for focusing
            // Then use the widget navigator helper class to select the right widget
            List<MapWidget> widgets = MapWidgetNavigator.getFocusableWidgets(this);
            if (!widgets.isEmpty()) {
                if (focused == null) {
                    widgets.get(0).focus();
                } else {
                    MapWidgetNavigator.getNextWidget(widgets, focused, event.getKey()).focus();
                }
            }
        }
    }

    @Override
    public void onKey(MapKeyEvent event) {
    }

    @Override
    public void onKeyPressed(MapKeyEvent event) {
        this.handleNavigation(event);
    }

    @Override
    public void onKeyReleased(MapKeyEvent event) {
    }

    @Override
    public void onLeftClick(MapClickEvent event) {
    }

    @Override
    public void onRightClick(MapClickEvent event) {
    }

    @Override
    public void onMapItemChanged() {
    }
}
