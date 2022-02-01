package com.bergerkiller.bukkit.common.map.util;

import org.bukkit.entity.ItemFrame;

import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;
import com.bergerkiller.bukkit.common.utils.MathUtil;

/**
 * The position a player looks at on a map display
 */
public final class MapLookPosition implements IMapLookPosition {
    private final ItemFrameInfo itemFrame;
    private final double px;
    private final double py;
    private final double distance;
    private final double edgeDistance;

    @Deprecated
    public MapLookPosition(ItemFrameInfo itemFrame, double px, double py, double distance, boolean withinBounds) {
        this(itemFrame, px, py, distance, withinBounds ? 0.0 : Double.MAX_VALUE);
    }

    public MapLookPosition(ItemFrameInfo itemFrame, double px, double py, double distance, double edgeDistance) {
        this.itemFrame = itemFrame;
        this.px = px;
        this.py = py;
        this.distance = distance;
        this.edgeDistance = edgeDistance;
    }

    @Override
    public ItemFrameInfo getItemFrameInfo() {
        return this.itemFrame;
    }

    @Override
    public ItemFrame getItemFrame() {
        return this.itemFrame.itemFrame;
    }

    @Override
    public int getX() {
        return MathUtil.floor(this.px);
    }

    @Override
    public int getY() {
        return MathUtil.floor(this.py);
    }

    @Override
    public double getDoubleX() {
        return this.px;
    }

    @Override
    public double getDoubleY() {
        return this.py;
    }

    @Override
    public double getDistance() {
        return this.distance;
    }

    @Override
    public boolean isWithinBounds() {
        return this.edgeDistance <= 0.0;
    }

    @Override
    public double getEdgeDistance() {
        return this.edgeDistance;
    }

    @Override
    public String toString() {
        return "{px=" + this.px + ", py=" + this.py + ", distance=" + this.distance + "}";
    }
}
