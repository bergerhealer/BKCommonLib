package com.bergerkiller.bukkit.common.map;

/**
 * Keeps track of the dirty areas of a canvas
 */
public class MapClip {
    public int dirty_x1, dirty_y1, dirty_x2, dirty_y2;
    public boolean dirty;
    public boolean everything;

    public MapClip() {
        this.dirty_x1 = 0;
        this.dirty_x2 = 0;
        this.dirty_y1 = 0;
        this.dirty_y2 = 0;
        this.dirty = false;
        this.everything = false;
    }

    /**
     * Gets the x-coordinate of the top-left dirty pixel
     * 
     * @return top-left dirty pixel x-coordinate
     */
    public final int getX() {
        return dirty_x1;
    }

    /**
     * Gets the y-coordinate of the top-left dirty pixel
     * 
     * @return top-left dirty pixel y-coordinate
     */
    public final int getY() {
        return dirty_y1;
    }

    /**
     * Gets the width of the dirty area
     * 
     * @return dirty area width
     */
    public final int getWidth() {
        return dirty_x2 - dirty_x1 + 1;
    }

    /**
     * Gets the height of the dirty area
     * 
     * @return dirty area height
     */
    public final int getHeight() {
        return dirty_y2 - dirty_y1 + 1;
    }

    /**
     * Marks a single pixel as dirty
     * 
     * @param x - coordinate of the pixel
     * @param y - coordinate of the pixel
     */
    public final void markDirty(int x, int y) {
        if (!this.dirty) {
            this.dirty = true;
            this.dirty_x1 = this.dirty_x2 = x;
            this.dirty_y1 = this.dirty_y2 = y;
        } else if (!this.everything) {
            if (x < this.dirty_x1) 
                this.dirty_x1 = x;
            else if (x > this.dirty_x2)
                this.dirty_x2 = x;
            if (y < this.dirty_y1) 
                this.dirty_y1 = y;
            else if (y > this.dirty_y2)
                this.dirty_y2 = y;
        }
    }

    /**
     * Marks a rectangular pixel area dirty
     * 
     * @param x - coordinate of the top-left corner of the rectangle
     * @param y - coordinate of the top-left corner of the rectangle
     * @param w - width of the rectangle
     * @param h - height of the rectangle
     */
    public final void markDirty(int x, int y, int w, int h) {
        int x2 = x + w - 1;
        int y2 = y + h - 1;
        if (!this.dirty) {
            this.dirty = true;
            this.dirty_x1 = x;
            this.dirty_x2 = x2;
            this.dirty_y1 = y;
            this.dirty_y2 = y2;
        } else if (!this.everything) {
            if (x < this.dirty_x1)
                this.dirty_x1 = x;
            if (y < this.dirty_y1)
                this.dirty_y1 = y;
            if (x2 > this.dirty_x2)
                this.dirty_x2 = x2;
            if (y2 > this.dirty_y2)
                this.dirty_y2 = y2;
        }
    }

    /**
     * Marks a pixel area dirty that is also dirty in another clip
     * 
     * @param clip containing the dirty areas
     */
    public final void markDirty(MapClip clip) {
        if (!this.dirty) {
            this.dirty = true;
            this.dirty_x1 = clip.dirty_x1;
            this.dirty_x2 = clip.dirty_x2;
            this.dirty_y1 = clip.dirty_y1;
            this.dirty_y2 = clip.dirty_y2;
        } else if (!this.everything) {
            if (clip.dirty_x1 < this.dirty_x1)
                this.dirty_x1 = clip.dirty_x1;
            if (clip.dirty_y1 < this.dirty_y1)
                this.dirty_y1 = clip.dirty_y1;
            if (clip.dirty_x2 > this.dirty_x2)
                this.dirty_x2 = clip.dirty_x2;
            if (clip.dirty_y2 > this.dirty_y2)
                this.dirty_y2 = clip.dirty_y2;
        }
    }

    /**
     * Marks everything dirty, requiring all contents to be updated
     */
    public final void markEverythingDirty() {
        this.dirty = true;
        this.everything = true;
    }

    /**
     * Clears the dirty state, nothing is dirty anymore
     */
    public final void clearDirty() {
        this.dirty = false;
        this.everything = false;
    }

    /**
     * Takes out the dirty pixels of a rectangular area
     * 
     * @param x - coordinate of the top-left corner of the area
     * @param y - coordinate of the top-left corner of the area
     * @param width of the area
     * @param height of the area
     * @return the area
     */
    public final MapClip getArea(int x, int y, int width, int height) {
        MapClip result = new MapClip();
        if (!this.dirty) {
            result.clearDirty();
        } else if (this.everything) {
            result.markEverythingDirty();
        } else if (this.dirty_x1 <= x && this.dirty_y1 <= y && this.dirty_x2 >= (x+width) && this.dirty_y2 >= (y+height)) {
            // The area is inside a much larger area that is all dirty, so the entire clip area is dirty
            result.markEverythingDirty();
        } else if (this.dirty_x1 > (x + width) || this.dirty_y1 > (y + height)) {
            // Dirty area is out of bounds (too far to the right)
            result.clearDirty();
        } else if (this.dirty_x2 < x || this.dirty_y2 < y) {
            // Dirty area is out of bounds (too far to the left)
            result.clearDirty();
        } else {
            // Crop out the area
            result.dirty = true;
            result.everything = false;
            result.dirty_x1 = Math.max(this.dirty_x1, x) - x;
            result.dirty_y1 = Math.max(this.dirty_y1, y) - y;
            result.dirty_x2 = Math.min(this.dirty_x2, x + width - 1) - x;
            result.dirty_y2 = Math.min(this.dirty_y2, y + height - 1) - y;
        }
        return result;
    }
}
