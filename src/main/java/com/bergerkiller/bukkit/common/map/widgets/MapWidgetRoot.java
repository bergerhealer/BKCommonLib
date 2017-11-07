package com.bergerkiller.bukkit.common.map.widgets;

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

    public MapWidgetRoot(MapDisplay display) {
        this.parent = null;
        this.display = display;
        this.root = this;
    }

    public MapWidget getFocusedWidget() {
        return this._focusedWidget;
    }

    public void setFocusedWidget(MapWidget widget) {
        if (this._focusedWidget == widget) {
            return;
        }

        if (this._focusedWidget != null) {
            this._focusedWidget.invalidate();
        }
        this._focusedWidget = widget;
        if (this._focusedWidget != null) {
            this._focusedWidget.invalidate();
        }
    }

    public MapWidget getActivatedWidget() {
        return this._activatedWidget;
    }

    public void setActivatedWidget(MapWidget widget) {
        if (this._activatedWidget == widget) {
            return;
        }

        MapWidget oldActivatedWidget = this._activatedWidget;
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
        } else {
            // Set the activated widget to the widget specified
            this._activatedWidget = widget;
        }
        this._activatedWidget.invalidate();

        // Find all potential widgets that can be focused
        List<MapWidget> widgets = MapWidgetNavigator.getFocusableWidgets(this._activatedWidget);
        if (widgets.contains(oldActivatedWidget)) {
            // If the previously activated widget is a possible focusable widget of the new one,
            // we focus the previously activated widget. This correctly implements the forward/backward logic.
            this.setFocusedWidget(oldActivatedWidget);
        } else if (!widgets.isEmpty()) {
            // The first child of the activated widget that can be focused is given the focus
            this.setFocusedWidget(widgets.get(0));
        } else {
            // If none exists, set focus to null to indicate no further focus is possible
            this.setFocusedWidget(null);
        }
    }

    @Override
    public void onAttached() {
        this.setFocusable(true);
        this.setBounds(0, 0, display.getWidth(), display.getHeight());
        this.activate();
    }

}
