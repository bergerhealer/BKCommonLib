package com.bergerkiller.bukkit.common.map.util;

import java.util.ArrayList;
import java.util.List;

import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;

/**
 * Helper class for deducing the best next widget to move to from an existing widget
 */
public class MapWidgetNavigator {

    public static List<MapWidget> getFocusableWidgets(MapWidget widget) {
        List<MapWidget> result = new ArrayList<MapWidget>();
        addFocusableWidgets(result, widget);
        return result;
    }

    public static void addFocusableWidgets(List<MapWidget> result, MapWidget widget) {
        for (MapWidget child : widget.getWidgets()) {
            if (child.isFocusable()) {
                result.add(child);
            } else {
                addFocusableWidgets(result, child);
            }
        }
    }

    public static MapWidget getNextWidget(List<MapWidget> widgets, MapWidget current, MapPlayerInput.Key key) {
        // Find the widget with smallest weight value
        int minWeight = Integer.MAX_VALUE;
        MapWidget result = current;
        for (MapWidget widget : widgets) {
            int weight = Integer.MAX_VALUE;
            if (key == MapPlayerInput.Key.LEFT) {
                weight = getWeightLeft(current, widget, key);
            } else if (key == MapPlayerInput.Key.RIGHT) {
                weight = getWeightRight(current, widget, key);
            } else if (key == MapPlayerInput.Key.UP) {
                weight = getWeightUp(current, widget, key);
            } else if (key == MapPlayerInput.Key.DOWN) {
                weight = getWeightDown(current, widget, key);
            }
            if (weight < minWeight) {
                minWeight = weight;
                result = widget;
            }
        }
        return result;
    }

    private static int getWeightLeft(MapWidget current, MapWidget widget, MapPlayerInput.Key key) {
        // Widget to the left of the current widget?
        // If so, the weight is the distance between the widget's right bound and the current widget's left bound
        int x = current.getX();
        int y1 = current.getY();
        int y2 = current.getY() + current.getHeight();
        int weight = x - widget.getX();
        int weight_a = (widget.getY() - y2);
        int weight_b = (y1 - (widget.getY() + widget.getHeight()));
        if (weight > 0) {
            return wab(weight, weight_a, weight_b);
        }

        return Integer.MAX_VALUE;
    }

    private static int getWeightRight(MapWidget current, MapWidget widget, MapPlayerInput.Key key) {
        // Widget to the right of the current widget?
        // If so, the weight is the distance between the widget's left bound and the current widget's right bound
        int x = current.getX() + current.getWidth();
        int y1 = current.getY();
        int y2 = current.getY() + current.getHeight();
        int weight = (widget.getX() + widget.getWidth()) - x;
        int weight_a = (widget.getY() - y2);
        int weight_b = (y1 - (widget.getY() + widget.getHeight()));
        if (weight > 0) {
            return wab(weight, weight_a, weight_b);
        }

        return Integer.MAX_VALUE;
    }

    private static int getWeightUp(MapWidget current, MapWidget widget, MapPlayerInput.Key key) {
        // Widget above the current widget?
        // If so, the weight is the distance between the widget's lower bound and the current widget's upper bound
        int y = current.getY();
        int x1 = current.getX();
        int x2 = current.getX() + current.getWidth();
        int weight = y - widget.getY();
        int weight_a = (widget.getX() - x2);
        int weight_b = (x1 - (widget.getX() + widget.getWidth()));
        if (weight > 0) {
            return wab(weight, weight_a, weight_b);
        }

        return Integer.MAX_VALUE;
    }

    private static int getWeightDown(MapWidget current, MapWidget widget, MapPlayerInput.Key key) {
        // Widget below the current widget?
        // If so, the weight is the distance between the widget's upper bound and the current widget's lower bound
        int y = current.getY() + current.getHeight();
        int x1 = current.getX();
        int x2 = current.getX() + current.getWidth();
        int weight = (widget.getY() + widget.getHeight()) - y;
        int weight_a = (widget.getX() - x2);
        int weight_b = (x1 - (widget.getX() + widget.getWidth()));
        if (weight > 0) {
            return wab(weight, weight_a, weight_b);
        }

        return Integer.MAX_VALUE;
    }

    private static int wab(int weight, int weight_a, int weight_b) {
        if (weight_a > 0) {
            return w(1, weight_a * weight);
        } else if (weight_b > 0) {
            return w(1, weight_b * weight);
        } else {
            return w(0, weight);
        }
    }
    
    private static int w(int n, int weight) {
        return (n * 100000) + weight;
    }
}
