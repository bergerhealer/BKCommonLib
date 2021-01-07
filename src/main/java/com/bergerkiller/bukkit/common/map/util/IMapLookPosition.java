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
}
