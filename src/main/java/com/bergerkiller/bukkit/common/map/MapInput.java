package com.bergerkiller.bukkit.common.map;

/**
 * Input controller for virtual map navigation and UI
 */
public class MapInput {
    private int last_dx, last_dy, last_dz;
    private int curr_dx, curr_dy, curr_dz;
    private int recv_dx, recv_dy, recv_dz;
    private int key_repeat_timer;
    private boolean has_input;

    public MapInput() {
        reset();
    }

    /**
     * Gets whether input is available at all
     * 
     * @return True if input is available, False if not
     */
    public boolean hasInput() {
        return has_input;
    }

    /**
     * Gets whether there were any key-press changes
     * 
     * @return True if there were changes, False if not
     */
    public boolean hasChanges() {
        return curr_dx != last_dx || curr_dy != last_dy || curr_dz != last_dz;
    }

    /**
     * Gets whether the user pressed down a key long enough for it to start auto-repeating
     * 
     * @return True if repeating, False if not
     */
    public boolean isRepeating() {
        return key_repeat_timer > 15;
    }

    /**
     * Gets whether a particular key is pressed down.
     * 
     * @param key to query
     * @return True if pressed down, False if not
     */
    public boolean isPressed(Key key) {
        switch (key) {
        case LEFT:  return curr_dx < 0;
        case RIGHT: return curr_dx > 0;
        case DOWN:  return curr_dy > 0;
        case UP:    return curr_dy < 0;
        case ENTER: return curr_dz > 0;
        case BACK:  return curr_dz < 0;
        }
        return false;
    }

    /**
     * Gets whether a particular key was pressed down before.
     * Combine this with {@link #isPressed(Key)} to monitor key changes.
     * 
     * @param key to query
     * @return True if it was pressed down, False if not
     */
    public boolean wasPressed(Key key) {
        switch (key) {
        case LEFT:  return last_dx < 0;
        case RIGHT: return last_dx > 0;
        case DOWN:  return last_dy > 0;
        case UP:    return last_dy < 0;
        case ENTER: return last_dz > 0;
        case BACK:  return last_dz < 0;
        }
        return false;
    }

    /**
     * Gets the left/right movement state (-1, 0, or 1).
     * Pressing A will result in -1, pressing D will result in 1.
     * 
     * @return left/right state
     */
    public int getLeftRight() {
        return curr_dx;
    }

    /**
     * Gets the up/down movement state (-1, 0, or 1).
     * Pressing W will result in -1, pressing S will result in 1.
     * 
     * @return up/down state
     */
    public int getUpDown() {
        return curr_dy;
    }

    /**
     * Gets the back/enter movement state (-1, 0, or 1).
     * Pressing shift will result in -1, pressing Spacebar will result in 1.
     * 
     * @return back/enter state
     */
    public int getBackEnter() {
        return curr_dz;
    }

    /**
     * User started pressing down 'left' (A)
     * 
     * @return True if the state of {@link #left()} changed to true
     */
    public boolean leftPressed() {
        return curr_dx < 0 && last_dx >= 0;
    }

    /**
     * User stopped pressing down 'left' (A)
     * 
     * @return True if the state of {@link #left()} changed to false
     */
    public boolean leftReleased() {
        return last_dx < 0 && curr_dx >= 0;
    }

    /**
     * User pressing down 'left' (A)
     * 
     * @return True if 'left' is pressed
     */
    public boolean left() {
        return curr_dx < 0;
    }

    /**
     * User started pressing down 'right' (D)
     * 
     * @return True if the state of {@link #right()} changed to true
     */
    public boolean rightPressed() {
        return curr_dx > 0 && last_dx <= 0;
    }

    /**
     * User stopped pressing down 'right' (D)
     * 
     * @return True if the state of {@link #right()} changed to false
     */
    public boolean rightReleased() {
        return last_dx > 0 && curr_dx <= 0;
    }

    /**
     * User pressing down 'right' (D)
     * 
     * @return True if 'right' is pressed
     */
    public boolean right() {
        return curr_dx > 0;
    }

    /**
     * User started pressing down 'up' (W)
     * 
     * @return True if the state of {@link #up()} changed to true
     */
    public boolean upPressed() {
        return curr_dy < 0 && last_dy >= 0;
    }

    /**
     * User stopped pressing down 'up' (W)
     * 
     * @return True if the state of {@link #up()} changed to false
     */
    public boolean upReleased() {
        return last_dy < 0 && curr_dy >= 0;
    }

    /**
     * User pressing down 'up' (W)
     * 
     * @return True if 'up' is pressed
     */
    public boolean up() {
        return curr_dy < 0;
    }

    /**
     * User started pressing down 'down' (S)
     * 
     * @return True if the state of {@link #down()} changed to true
     */
    public boolean downPressed() {
        return curr_dy > 0 && last_dy <= 0;
    }

    /**
     * User stopped pressing down 'down' (S)
     * 
     * @return True if the state of {@link #down()} changed to false
     */
    public boolean downReleased() {
        return last_dy > 0 && curr_dy <= 0;
    }

    /**
     * User pressing down 'down' (S)
     * 
     * @return True if 'down' is pressed
     */
    public boolean down() {
        return curr_dy > 0;
    }

    /**
     * User started pressing down 'enter' (Spacebar)
     * 
     * @return True if the state of {@link #enter()} changed to true
     */
    public boolean enterPressed() {
        return curr_dz > 0 && last_dz <= 0;
    }

    /**
     * User stopped pressing down 'enter' (Spacebar)
     * 
     * @return True if the state of {@link #down()} changed to false
     */
    public boolean enterReleased() {
        return last_dz > 0 && curr_dz <= 0;
    }

    /**
     * User pressing down 'enter' (Spacebar)
     * 
     * @return True if 'enter' is pressed
     */
    public boolean enter() {
        return curr_dz > 0;
    }

    /**
     * User started pressing down 'back' (shift)
     * 
     * @return True if the state of {@link #back()} changed to true
     */
    public boolean backPressed() {
        return curr_dz < 0 && last_dz >= 0;
    }

    /**
     * User stopped pressing down 'back' (shift)
     * 
     * @return True if the state of {@link #back()} changed to false
     */
    public boolean backReleased() {
        return last_dz < 0 && curr_dz >= 0;
    }

    /**
     * User pressing down 'back' (Shift)
     * 
     * @return True if 'back' is pressed
     */
    public boolean back() {
        return curr_dz < 0;
    }

    /**
     * Resets the input to the default state of 'no input'
     */
    public void reset() {
        curr_dx = curr_dy = curr_dz = 0;
        last_dx = last_dy = last_dz = 0;
        recv_dx = recv_dy = recv_dz = 0;
        key_repeat_timer = 0;
        has_input = false;
    }

    /**
     * Performs a tick update on the input, shifting states around
     */
    public void doTick() {
        last_dx = curr_dx;
        last_dy = curr_dy;
        last_dz = curr_dz;
        curr_dx = recv_dx;
        curr_dy = recv_dy;
        curr_dz = recv_dz;
    }

    /**
     * Updates the received input state
     * 
     * @param dx - left/right
     * @param dy - up/down
     * @param dz - enter/back
     */
    public void update(int dx, int dy, int dz) {
        recv_dx = dx;
        recv_dy = dy;
        recv_dz = dz;
        has_input = true;
        if (dx != 0 || dy != 0 || dz != 0) {
            key_repeat_timer++;
        } else {
            key_repeat_timer = 0;
        }
    }

    /**
     * The key belonging to a key change event for a player
     */
    public static enum Key {
        LEFT(-1, 0, 0), RIGHT(1, 0, 0), UP(0, -1, 0), DOWN(0, 1, 0), ENTER(0, 0, 1), BACK(0, 0, -1);

        private final int _dx, _dy, _dz;

        private Key(int dx, int dy, int dz) {
            this._dx = dx;
            this._dy = dy;
            this._dz = dz;
        }

        /**
         * Gets the horizontal pixel offset associated with this key
         * 
         * @return delta-x
         */
        public int dx() {
            return this._dx;
        }

        /**
         * Gets the vertical pixel offset associated with this key
         * 
         * @return delta-y
         */
        public int dy() {
            return this._dy;
        }

        /**
         * Gets the depth pixel offset associated with this key
         * 
         * @return delta-z
         */
        public int dz() {
            return this._dz;
        }
    }
}
