package com.bergerkiller.bukkit.common.map.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.util.MapWidgetNavigator;

/**
 * The root node of a map display widget view. It is here that the display and main display layer
 * are initialized. Other widgets can be added to this main widget recursively, and all will
 * be updated with an appropriate z-level assigned.
 */
public class MapWidgetRoot extends MapWidget {
    private MapWidget _focusedWidget = null;
    private MapWidget _activatedWidget = this;
    private MapWidget _focusChangeFrom = this;
    private List<MapWidget> _focusHistory = new ArrayList<MapWidget>();

    public MapWidgetRoot(MapDisplay display) {
        this.parent = null;
        this.display = display;
        this.root = this;
    }

    public MapWidget getFocusedWidget() {
        return this._focusedWidget;
    }

    public void setFocusedWidget(MapWidget widget) {
        // When NULL is used, remove all focused widgets
        MapWidget prevFocus = this._focusedWidget;
        if (widget == null) {
            this._focusedWidget = null;
            this._focusChangeFrom = null;
            if (prevFocus != null) {
                prevFocus.handleRefreshFocus();
                prevFocus.invalidate();
            }
            return;
        }

        // Check not already focused. If it is, only disable automatic focus changes.
        if (this._focusedWidget == widget) {
            this._focusChangeFrom = null;
            return;
        }

        // Find the widget that must be activated in order for this widget to be focused
        // First make sure that it is indeed activated to prevent weird navigation bugs
        MapWidget tmpParent = widget.parent;
        while (tmpParent != null && !tmpParent.isFocusable()) {
            tmpParent = tmpParent.parent;
        }
        if (tmpParent == null) {
            // Can't activate. Has no parent that can be activated.
            // This is usually the sign of a detached widget.
            return;
        }

        // Ensure activated
        if (tmpParent != this._activatedWidget) {
            this.setActivatedWidget(tmpParent);
        }

        // Perform the focus change
        this._focusedWidget = widget;
        if (prevFocus != null) {
            prevFocus.handleRefreshFocus();
            prevFocus.invalidate();
        }
        this.pushFocus(widget);
        if (this._focusedWidget != null) {
            this._focusedWidget.invalidate();
        }
        this._focusChangeFrom = null;
    }

    private void pushFocus(MapWidget widget) {
        if (widget != null) {
            this._focusHistory.remove(widget);
            this._focusHistory.add(widget);
            this.cleanupFocusHistory();
        }
    }
    
    private void cleanupFocusHistory() {
        // Remove widget from focus history that don't exist anymore
        Iterator<MapWidget> iter = this._focusHistory.iterator();
        while (iter.hasNext()) {
            if (iter.next().root != this) {
                iter.remove();
            }
        }
    }

    @Override
    public void clearWidgets() {
        super.clearWidgets();
        this._focusedWidget = null;
        this._activatedWidget = this;
        this._focusChangeFrom = this;
        this._focusHistory.clear();
    }

    public MapWidget getActivatedWidget() {
        return this._activatedWidget;
    }

    public void setActivatedWidget(MapWidget widget) {
        if (this._activatedWidget == widget) {
            return;
        }
        this.pushFocus(this._activatedWidget);
        if (this._activatedWidget != null) {
            this._activatedWidget.invalidate();
            this._activatedWidget.onDeactivate();
        }
        if (widget == null) {
            // Find the first parent of the current activated widget which can be activated
            MapWidget tmp = this._activatedWidget.parent;
            while (tmp != null) {
                if (tmp.isFocusable()) {
                    break;
                }
                tmp = tmp.parent;
            }
            if (tmp == null) {
                tmp = this;
            }
            this._activatedWidget = tmp;
            this._focusChangeFrom = tmp;
        } else {
            // Set the activated widget to the widget specified
            // Activate a parent widget of the widget if the widget is not focusable
            MapWidget tmp = widget;
            while (tmp != null && !tmp.isFocusable()) {
                tmp = tmp.parent;
            }
            if (tmp == null) {
                tmp = this;
            }

            this._activatedWidget = tmp;
            this._focusChangeFrom = widget;
        }
        this._activatedWidget.invalidate();
    }

    @Override
    public void onTick() {
        if (this._focusChangeFrom != null) {

            // Find all potential widgets that can be focused
            List<MapWidget> widgets = MapWidgetNavigator.getFocusableWidgets(this._focusChangeFrom);
            if (!widgets.isEmpty()) {
                // Check focus history in reverse order (newest - oldest) to see if we can focus
                // By default pick the first child
                MapWidget result = widgets.get(0);
                for (int i = this._focusHistory.size() - 1; i >= 0; i--) {
                    if (widgets.contains(this._focusHistory.get(i))) {
                        result = this._focusHistory.get(i);
                        break;
                    }
                }
                this.setFocusedWidget(result);
            } else {
                // If none exists, set focus to null to indicate no further focus is possible
                this.setFocusedWidget(null);
            }
        }
    }
    
    @Override
    public void onAttached() {
        this.setFocusable(true);
        this.setBounds(0, 0, display.getWidth(), display.getHeight());
        this.activate();
    }

}
