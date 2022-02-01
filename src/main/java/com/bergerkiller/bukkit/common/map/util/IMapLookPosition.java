package com.bergerkiller.bukkit.common.map.util;

import org.bukkit.entity.ItemFrame;

import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;

/**
 * Interface with all the information provided when a player
 * looks at a map display.
 */
public interface IMapLookPosition {

    /**
     * Gets the ItemFrame Metadata information of the ItemFrame
     * that was looked at.
     *
     * @return item frame metadata information
     */
    ItemFrameInfo getItemFrameInfo();

    /**
     * Gets the ItemFrame that was looked at
     * 
     * @return item frame
     */
    ItemFrame getItemFrame();

    /**
     * Gets the x-coordinate of the pixel that the player looked at
     * 
     * @return looked at x-coordinate
     */
    int getX();

    /**
     * Gets the y-coordinate of the pixel that the player looked at
     * 
     * @return looked at y-coordinate
     */
    int getY();

    /**
     * Gets the x-coordinate of the pixel that the player looked at
     * with floating point (sub-pixel) precision.
     * 
     * @return looked at x-coordinate
     */
    double getDoubleX();

    /**
     * Gets the y-coordinate of the pixel that the player looked at
     * with floating point (sub-pixel) precision.
     * 
     * @return looked at y-coordinate
     */
    double getDoubleY();

    /**
     * Gets the distance away from the display the player is from where
     * was looked at. The distance from eye to canvas.
     *
     * @return distance
     */
    double getDistance();

    /**
     * Gets whether the result is within bounds of the item frame. This is the case
     * when the player is clearly looking inside the bounds of the display, and
     * is false when the player is looking near the edge or away from the display.
     * Only really useful when querying the look position relative to a particular
     * item frame. May be false for normal click events when players click near the
     * edge of item frames, with the coordinates clamped in that case.
     *
     * @return True if the player looks exactly within an item frame's bounds
     */
    boolean isWithinBounds();

    /**
     * Gets the distance from the edge of the item frame that the player is looking at.
     * Is 0 when {@link #isWithinBounds()} is true. Is {@link Double#MAX_VALUE} when
     * looking away from the item frame entirely.
     *
     * @return Edge distance
     */
    double getEdgeDistance();
}
