package com.bergerkiller.bukkit.common.map.widgets;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.events.map.MapClickEvent;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.events.map.MapStatusEvent;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapDisplayEvents;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapPlayerInput.Key;
import com.bergerkiller.bukkit.common.map.util.MapWidgetNavigator;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * A single rectangular element on the map
 */
public class MapWidget implements MapDisplayEvents {
    protected MapWidgetRoot root;
    protected MapWidget parent;
    protected MapDisplay display;
    protected MapDisplay.Layer layer;
    protected MapCanvas view;
    private int _z; // depth value can be changed if desired
    private int _x, _y, _width, _height; // live bounds (relative to parent)
    private int _lastX, _lastY, _lastWidth, _lastHeight; // last drawn bounds (absolute)
    private boolean _enabled;
    private boolean _invalidated;
    private boolean _focusable;
    private boolean _attached;
    private boolean _boundsChanged;
    private boolean _wasFocused;
    private boolean _retainChildren;
    private boolean _clipParent;
    private boolean _visible;
    private MapWidget[] _children;

    public MapWidget() {
        this._wasFocused = false;
        this._enabled = true;
        this._invalidated = true;
        this._boundsChanged = true;
        this._focusable = false;
        this._attached = false;
        this._retainChildren = false;
        this._clipParent = false;
        this._visible = true;
        this._children = new MapWidget[0];
        this.display = null;
        this.layer = null;
        this.parent = null;
        this.root = null;
        this._z = 0;
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
     * Called right after the user presses spacebar to 'enter' this widget.
     * This function is only called when {@link #setFocusable(boolean)} is set to true.
     * By default this function will activate and enter the element, allowing input
     * to be handled by this widget. By overriding this base functionality can be disabled.
     */
    public void onActivate() {
        if (this.root != null) {
            this.root.setActivatedWidget(this);
        }
    }

    /**
     * Called right before a new widget becomes activated when this widget was previously
     * activated. This method is always called at most once after {@link #onActivate()}
     * is called.
     */
    public void onDeactivate() {
    }

    /**
     * Called right after this item is focused using W/A/S/D navigation controls
     */
    public void onFocus() {
    }

    /**
     * Called right before this item loses the focus
     */
    public void onBlur() {
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
     * Gets the parent node of this widget
     * 
     * @return parent node, null if this is the root node
     */
    public final MapWidget getParent() {
        return this.parent;
    }

    /**
     * Gets the child widget to which keyboard/mouse input and events are redirected
     * 
     * @return next input widget
     */
    public final MapWidget getNextInputWidget() {
        MapWidget focused = this.getInputWidget();

        // Propagate the event down the widget tree to the focused widget
        // If the focused widget is not accessible from this widget, stop propagating
        MapWidget tmp = focused;
        while (tmp != null && tmp.parent != this) {
            tmp = tmp.parent;
        }
        return tmp;
    }

    /**
     * Changes the depth offset of this widget relative to the parent.
     * By increasing this value it can be ensured that this widget sits on top of another widget.
     * The default depth offset is 0, putting it on equal depth as other children.
     * 
     * @param z depth offset
     */
    public final void setDepthOffset(int z) {
        this._z = z;
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
     * Gets the map display this widget is attached to. Returns null if not attached yet.
     * 
     * @return map display
     */
    public final MapDisplay getDisplay() {
        return this.display;
    }

    /**
     * Gets whether this widget is visible
     * 
     * @return True if visible
     */
    public final boolean isVisible() {
        return this._visible;
    }

    /**
     * Sets whether this widget is visible
     * 
     * @param visible
     * @return this map widget
     */
    public final MapWidget setVisible(boolean visible) {
        if (this._visible != visible) {
            this._visible = visible;
            this.invalidate();
        }
        return this;
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
     * Gets whether or not the view area of this widget is clipped by the view area
     * of the parent. When true, only areas on top of the parent can be drawn on.
     * When false, this widget can draw outside of the parent's bounds.
     * 
     * @return True if the view area is clipped by the parent's view area
     */
    public final boolean isClipParent() {
        return this._clipParent;
    }

    /**
     * Sets whether or not the view area of this widget is clipped by the view area
     * of the parent. When true, only areas on top of the parent can be drawn on.
     * When false, this widget can draw outside of the parent's bounds.
     * 
     * @param clipParent Whether the view area is clipped by the parent's view area
     */
    public final void setClipParent(boolean clipParent) {
        if (this._clipParent != clipParent) {
            this._clipParent = clipParent;
            this.refreshView();
        }
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
     * Gets whether this widget has been activated. All the focusable children of the activated
     * widget can be switched between using W/A/S/D. Only focusable widgets can become activated.
     * 
     * @return True if activated
     */
    public final boolean isActivated() {
        return (this.root != null) && (this == this.root.getActivatedWidget());
    }

    /**
     * Activates this widget, but only when the widget is enabled.
     * Equivalent to calling {@link #onActivate()} directly.
     */
    public final void activate() {
        if (this.isEnabled()) {
            this.onActivate();
        }
    }

    /**
     * If this widget or a child thereof is activated, this method de-activates that widget.
     * It will in turn activate a parent of this widget.
     */
    public final void deactivate() {
        if (this.root == null) {
            return;
        }
        boolean isChildOrSelfActivated = false;
        MapWidget activated = this.root.getActivatedWidget();
        while (activated != null) {
            if (activated == this) {
                isChildOrSelfActivated = true;
                break;
            } else {
                activated = activated.parent;
            }
        }
        if (isChildOrSelfActivated) {
            MapWidget parent = this.parent;
            while (parent != null && !parent.isFocusable()) {
                parent = parent.parent;
            }
            if (parent == null) {
                parent = this.root;
            }
            if (parent != null) {
                parent.activate();
            }
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
     * Gets whether this widget is currently enabled using {@link #setEnabled(enabled)}.
     * If the parent of this widget is not enabled, this method returns false as well.
     * 
     * @return True if this widget and the parent widgets are enabled
     */
    public final boolean isEnabled() {
        return this._enabled && (this.parent == null || this.parent.isEnabled());
    }

    /**
     * Sets whether this widget is currently enabled. If a parent of this widget is not
     * enabled, then {@link #isEnabled()} will continue to return false.
     * 
     * @param enabled
     * @return this widget
     */
    public final MapWidget setEnabled(boolean enabled) {
        if (this._enabled != enabled) {
            this._enabled = enabled;
            this.invalidate();
        }
        return this;
    }

    /**
     * Gets an immutable list of children of this widget.
     * These children are drawn on top of this widget.
     * The returned list is guaranteed to be left unmodified when additional children
     * are added or removed from this widget, making it safe to use for iterating.
     * 
     * @return list of children
     */
    public final List<MapWidget> getWidgets() {
        return Arrays.asList(this._children);
    }

    /**
     * Gets a widget stored at a particular index in the list of child widgets.
     * Returns null if no widget is stored at this index.
     * 
     * @param index
     * @return widget at the index
     */
    public final MapWidget getWidget(int index) {
        return (index >= 0 && index < this._children.length) ? this._children[index] : null;
    }

    /**
     * Gets the number of child widgets
     * 
     * @return widget count
     */
    public final int getWidgetCount() {
        return this._children.length;
    }

    /**
     * Removes this widget from its parent. If this widget or a child thereof was previously
     * focused, the focus will automatically switch to the appropriate alternative.
     * This method is equivalent to calling:
     * <pre>
     * widget.getParent().removeWidget(widget);
     * </pre>
     */
    public final void removeWidget() {
        if (this.parent != null) {
            this.parent.removeWidget(this);
        }
    }

    /**
     * Removes all previously added widgets
     */
    public void clearWidgets() {
        MapWidget[] old_children = this._children;
        for (MapWidget old_child : old_children) {
            old_child.handleDetach();
        }

        // If the old_children and children are one and the same, there are no changes
        // Then we can use a shortcut to keep things simple
        // If this is not the case, we must only remove those widgets we have actually detached
        if (this._children == old_children) {
            this._children = new MapWidget[0];
        } else {
            ArrayList<MapWidget> remaining = new ArrayList<MapWidget>(Arrays.asList(this._children));
            for (MapWidget old_child : old_children) {
                for (int i = 0; i < remaining.size(); i++) {
                    if (remaining.get(i) == old_child) {
                        remaining.remove(i);
                        break;
                    }
                }
            }
            this._children = LogicUtil.toArray(remaining, MapWidget.class);
        }
    }

    /**
     * Adds a new child widget to this widget
     * 
     * @param widget to add
     * @return input widget (can be used for chaining calls)
     */
    public final <T extends MapWidget> T addWidget(T widget) {
        this._children = java.util.Arrays.copyOf(this._children, this._children.length + 1);
        this._children[this._children.length - 1] = widget;
        this.handleWidgetAdded(widget);
        return widget;
    }

    /**
     * Replaces a widget with a new widget at the exact same index.
     * If the old widget is not contained, the new widget is added instead.
     * 
     * @param oldWidget to replace
     * @param newWidget to replace oldWidget with
     * @return newWidget (for chained calls)
     */
    public final <T extends MapWidget> T swapWidget(MapWidget oldWidget, T newWidget) {
        MapWidget[] old_children = this._children;
        int oldIndex = -1;
        for (int i = 0; i < old_children.length; i++) {
            if (old_children[i] == oldWidget) {
                oldIndex = i;
                break;
            }
        }
        if (oldIndex == -1) {
            return this.addWidget(newWidget);
        }

        // Perform detachment of the old widget
        oldWidget.handleDetach();

        // If the old_children and children are one and the same, there are no changes
        // Then we can use a shortcut to keep things simple
        if (old_children == this._children) {
            this._children = this._children.clone();
            this._children[oldIndex] = newWidget;
            this.handleWidgetAdded(newWidget);
            return newWidget;
        }

        // If this is not the case, we must only replace those widgets we have actually detached
        for (int i = 0; i < this._children.length; i++) {
            if (this._children[i] == oldWidget) {
                this._children = this._children.clone();
                this._children[i] = newWidget;
                this.handleWidgetAdded(newWidget);
                return newWidget;
            }
        }

        // Old widget no longer exists as a child. Just add the new widget and be done with it
        return this.addWidget(newWidget);
    }

    private final void handleWidgetAdded(MapWidget child) {
        child.root = this.root;
        child.parent = this;
        child._attached = false;
        child._invalidated = true;
        child._boundsChanged = true;
        for (MapWidget subChild : child.getWidgets()) {
            child.handleWidgetAdded(subChild);
        }
    }

    /**
     * Removes a child widget from this widget. The old area the widget occupied is cleared.
     * 
     * @param child to remove
     * @return True if the child widget was removed
     */
    public final boolean removeWidget(MapWidget widget) {
        // Check if the widget is a child of this widget at all
        int index = -1;
        for (int i = 0; i < this._children.length; i++) {
            if (this._children[i] == widget) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return false;
        }

        // Handles detachment. This calls user code, which could result in further changes to widgets.
        // To prevent weird bugs, the widget is still marked a child while it handles handleDetach()
        widget.handleDetach();

        // In case the order of the widget was changed, find it again
        if (index >= this._children.length || this._children[index] != widget) {
            index = -1;
            for (int i = 0; i < this._children.length; i++) {
                if (this._children[i] == widget) {
                    index = i;
                    break;
                }
            }
        }

        // Remove the widget at the index
        if (index != -1) {
            MapWidget[] new_children = new MapWidget[this._children.length - 1];
            int dest_idx = 0;
            for (int i = 0; i < this._children.length; i++) {
                if (i != index) {
                    new_children[dest_idx++] = this._children[i];
                }
            }
            this._children = new_children;
        }

        return true;
    }

    /**
     * Removes all the displayed contents of this widget
     * 
     * @return this widget
     */
    public final MapWidget clear() {
        if (this.view != null) {
            this.view.clear();
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
     * Sets whether child widgets are retained when this widget is removed from a parent.
     * When retained, the child widgets are re-added when this widget is re-attached to a parent.
     * 
     * @param retain whether to retain the widgets
     * @return this map widget
     */
    public final MapWidget setRetainChildWidgets(boolean retain) {
        this._retainChildren = retain;
        return this;
    }

    /**
     * Broadcasts a status change to all widgets of the display this widget is attached to
     * 
     * @param name of the status change
     */
    public final void sendStatusChange(String name) {
        sendStatusChange(MapEventPropagation.BROADCAST, name, null);
    }

    /**
     * Broadcasts a status change to all widgets of the display this widget is attached to
     * 
     * @param name of the status change
     * @param argument for the status change
     */
    public final void sendStatusChange(String name, Object argument) {
        sendStatusChange(MapEventPropagation.BROADCAST, name, argument);
    }

    /**
     * Sends a status change to all widgets, the propagation mode defining who receives the status change
     * and in what order.
     * 
     * @param propagationMode of sending the status change
     * @param name of the status change
     */
    public final void sendStatusChange(MapEventPropagation propagationMode, String name) {
        sendStatusChange(propagationMode, name, null);
    }

    /**
     * Sends a status change to all widgets, the propagation mode defining who receives the status change
     * and in what order.
     * 
     * @param propagationMode of sending the status change
     * @param name of the status change
     * @param argument for the status change
     */
    public final void sendStatusChange(MapEventPropagation propagationMode, String name, Object argument) {
        switch (propagationMode) {
        case BROADCAST:
            if (this.display != null) {
                this.display.sendStatusChange(name, argument);
            }
            break;
        case UPSTREAM:
            sendStatusChangeUpstream(this, new MapStatusEvent(name, argument));
            break;
        case DOWNSTREAM:
            sendStatusChangeDownstream(this, new MapStatusEvent(name, argument));
            break;
        }
    }

    private static void sendStatusChangeUpstream(MapWidget widget, MapStatusEvent event) {
        widget.onStatusChanged(event);
        for (MapWidget child : widget.getWidgets()) {
            sendStatusChangeUpstream(child, event);
        }
    }

    private static void sendStatusChangeDownstream(MapWidget widget, MapStatusEvent event) {
        widget.onStatusChanged(event);
        if (widget.parent != null) {
            sendStatusChangeDownstream(widget.parent, event);
        } else if (widget.display != null) {
            widget.display.onStatusChanged(event);
        }
    }

    /**
     * Performs {@link #onTick()} and {@link #onDraw()} update operations, and other widget refreshing logic
     * needed to run in the background. Attaches newly added widgets before any updates are performed.
     * This method is called by the internal implementation.
     */
    void performTickUpdates() {
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
     * Changes the size of this widget
     * 
     * @param dimension
     * @return this widget
     */
    public final MapWidget setSize(Dimension dimension) {
        return this.setSize(dimension.width, dimension.height);
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
            this.refreshView(this.getAbsoluteX(), this.getAbsoluteY());
        }
    }

    private final void refreshView(int absoluteX, int absoluteY) {
        this.view = this.createParentClip(this.layer).getView(absoluteX, absoluteY, this._width, this._height);
    }

    // Creates the view of the layer canvas, clipping parent areas if enabled
    private final MapCanvas createParentClip(MapDisplay.Layer layer) {
        // If no clipping is used or possible, return the layer itself
        if (!this._clipParent || this.parent == null) {
            return layer;
        }

        // Check whether this widget sits entirely within the parent's widget
        // If so, no clipping is required here, and ask the parent only
        if (this._x >= 0 && this._y >= 0 &&
            (this._x + this._width) < this.parent.getWidth() &&
            (this._y + this._height) < this.parent.getHeight())
        {
            return this.parent.createParentClip(layer);
        }

        // Clip it
        return this.parent.createParentClip(layer).getClip(
                this.parent.getAbsoluteX(), this.parent.getAbsoluteY(),
                this.parent.getWidth(), this.parent.getHeight());
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

        // Only do this when the widget has been drawn before (and is not root)
        if (this != this.root && this.layer != null && this._lastWidth > 0 && this._lastHeight > 0) {

            // Detect changes in bounds and invalidate when it happens
            if (this._lastWidth != this._width ||
                this._lastHeight != this._height ||
                this._lastX != absoluteX ||
                this._lastY != absoluteY) {
                this.invalidate();
            }

            // Clear old region when invalidated
            if (this._invalidated) {
                this.createParentClip(this.layer).clearRectangle(this._lastX, this._lastY, this._lastWidth, this._lastHeight);
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
            } else if (this.display != null) {
                // This is done for the root node (covers entire display)
                this.layer = this.display.getTopLayer().next();
            }

            // Use the depth offset to switch layers
            if (this.layer != null) {
                int n = this._z;
                while (n > 0) {
                    this.layer = this.layer.next();
                    n--;
                }
                while (n < 0) {
                    this.layer = this.layer.previous();
                    n++;
                }
            }

            this.refreshView();

            this.onAttached();

            // If the attached widget can be focused, and no widget is focused yet, focus it
            // Only do this if this current widget has an activated parent
            if (this.root != null && this.root.getFocusedWidget() == null && this.isNavigableFocus()) {
                this.focus();
            }
        }

        // Also attach (new) children
        // During onAttached() or focus() it can get detached again - so check that here
        if (this._attached) {
            for (MapWidget child : this._children) {
                child.handleAttach();
            }
        }
    }

    // Handles onTick() of this widget and all children
    private final void handleTick() {
        if (!this._attached) {
            return;
        }
        this.onTick();
        if (this._boundsChanged) {
            this._boundsChanged = false;
            this.onBoundsChanged();
        }
        this.handleRefreshFocus();
        for (MapWidget child : this._children) {
            child.handleTick();
        }
    }

    // Refreshes the focus, firing onFocus() or onBlur() events
    final void handleRefreshFocus() {
        if (this._wasFocused != this.isFocused()) {
            this._wasFocused = !this._wasFocused;
            if (this._wasFocused) {
                this.onFocus();
            } else {
                this.onBlur();
            }
        }
    }

    // Convenience method, see below
    private final void handleDraw() {
        if (this.parent != null) {
            boolean parentVisible = true;
            MapWidget tmp = this.parent;
            do {
                parentVisible &= tmp.isVisible();
                tmp = tmp.parent;
            } while (tmp != null);

            this.handleDraw(this.parent.getAbsoluteX(), this.parent.getAbsoluteY(), parentVisible);
        } else {
            this.handleDraw(0, 0, true);
        }
    }

    // Handles the drawing of this widget (if invalidated) and all children
    private final void handleDraw(int absoluteX, int absoluteY, boolean visible) {
        // If not attached yet, don't draw
        if (!this._attached) {
            return;
        }

        // Add own coordinates
        absoluteX += this._x;
        absoluteY += this._y;

        // If self is not visible, don't draw
        visible &= this.isVisible();

        // If invalidated, redraw
        if (this._invalidated) {
            this.refreshView(absoluteX, absoluteY);
            if (this != this.root && visible) {
                this.onDraw();
            }
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
                        otherChild.invalidate();
                    }
                }
            }
        }

        // Draw children of this widget, relative to the current coordinates
        for (MapWidget child : this._children) {
            child.handleDraw(absoluteX, absoluteY, visible);
        }
    }

    // Handles detachment of this widget and all its children
    void handleDetach() {
        if (this._attached) {
            this._attached = false;

            // If this widget or child thereof is activated, de-activate ourselves
            // This moves the activation back to whoever is behind is, fixing focus bugs
            this.deactivate();

            // Detach children first
            if (this._retainChildren) {
                // Retain them. Only fire onDetached() for the children.
                for (MapWidget old_child : this._children) {
                    old_child.handleDetach();
                }
            } else {
                this.clearWidgets();
            }

            // Fire onDetached
            this.onDetached();

            // Clear our occupied area on the canvas
            this.clear();

            // Detach ourselves completely
            this.display = null;
            this.view = null;
            this.layer = null;
            this.root = null;
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
            this.deactivate();
        } else {
            // W/A/S/D controls to switch the focused widget
            // Collect all the widgets eligible for focusing
            // Then use the widget navigator helper class to select the right widget
            List<MapWidget> widgets = MapWidgetNavigator.getFocusableWidgets(this);
            MapWidget nextFocus = null;
            if (focused != null) {
                nextFocus = focused.navigateNextWidget(widgets, event.getKey());
            } else if (!widgets.isEmpty()) {
                nextFocus = widgets.get(0);
            }
            if (nextFocus != null) {
                nextFocus.focus();
            }
        }
    }

    /**
     * Selects the next widget to give focus to after receiving player input to navigate around.
     * Can be overridden to adjust the automatic build-in navigation logic.
     * The method is called for the widget that currently receives focus.
     * 
     * @param widgets that can be focused next
     * @param key that was pressed
     * @return widget to be focused, null to cancel
     */
    protected MapWidget navigateNextWidget(List<MapWidget> widgets, MapPlayerInput.Key key) {
        return MapWidgetNavigator.getNextWidget(widgets, this, key);
    }

    @Override
    public void onKey(MapKeyEvent event) {
        MapWidget next = this.getNextInputWidget();
        if (next != null) next.onKey(event);
    }

    @Override
    public void onKeyPressed(MapKeyEvent event) {
        if (this == this.getInputWidget()) {
            // Let the activated widget decide the next widget to switch to
            this.root.getActivatedWidget().handleNavigation(event);
            return;
        }

        MapWidget next = this.getNextInputWidget();
        if (next != null) next.onKeyPressed(event);
    }

    @Override
    public void onKeyReleased(MapKeyEvent event) {
        MapWidget next = this.getNextInputWidget();
        if (next != null) next.onKeyReleased(event);
    }

    @Override
    public void onLeftClick(MapClickEvent event) {
        MapWidget next = this.getNextInputWidget();
        if (next != null) next.onLeftClick(event);
    }

    @Override
    public void onRightClick(MapClickEvent event) {
        MapWidget next = this.getNextInputWidget();
        if (next != null) next.onRightClick(event);
    }

    @Override
    public void onStatusChanged(MapStatusEvent event) {
    }

    @Override
    public void onMapItemChanged() {
    }

    @Override
    public boolean onItemDrop(Player player, ItemStack item) {
        MapWidget next = this.getNextInputWidget();
        return next != null && next.onItemDrop(player, item);
    }

    private MapWidget getInputWidget() {
        if (this.root == null) {
            return null;
        }
        MapWidget input = this.root.getFocusedWidget();
        if (input == null) {
            input = this.root.getActivatedWidget();
        }
        return input;
    }

}
