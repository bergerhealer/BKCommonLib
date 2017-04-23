package com.bergerkiller.bukkit.common.map;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntity;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityLiving;

/**
 * Input controller for virtual map navigation and UI.
 * A single instance manages the input coming from a single player.
 */
public class MapPlayerInput {
    private int last_dx, last_dy, last_dz;
    private int curr_dx, curr_dy, curr_dz;
    private int recv_dx, recv_dy, recv_dz;
    private int key_repeat_timer;
    private boolean has_input;
    private int _fakeMountId = -1;
    private boolean _fakeMountShown = false;
    private boolean _isIntercepting = false;
    public final Player player;

    public MapPlayerInput(Player player) {
        this.player = player;
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
        updateInputInterception(false);
        curr_dx = curr_dy = curr_dz = 0;
        last_dx = last_dy = last_dz = 0;
        recv_dx = recv_dy = recv_dz = 0;
        key_repeat_timer = 0;
        has_input = false;
    }

    /**
     * Performs a tick update on the input interception and event handling
     */
    public void doTick(MapDisplay display, boolean interceptInput) {
        this._isIntercepting = interceptInput;
        if (this.player.isOnline()) {
            updateInterception(interceptInput);
            if (!player.isInsideVehicle() && !_fakeMountShown) {
                receiveInput(0, 0, 0);
            }
            last_dx = curr_dx;
            last_dy = curr_dy;
            last_dz = curr_dz;
            curr_dx = recv_dx;
            curr_dy = recv_dy;
            curr_dz = recv_dz;
        } else {
            reset();
        }

        // Check if there are any receiving displays at all
        if (!interceptInput) {
            return;
        }

        // Send input events to the MapDisplay
        if (isRepeating()) {
            for (MapPlayerInput.Key key : MapPlayerInput.Key.values()) {
                boolean press_a = wasPressed(key);
                boolean press_b = isPressed(key);
                if (press_b) {
                    display.onKeyPressed(new MapKeyEvent(display, this.player, key)); // Repeat!
                    display.onKey(new MapKeyEvent(display, this.player, key));
                } else if (press_a) {
                    display.onKeyReleased(new MapKeyEvent(display, this.player, key));
                }
            }
        } if (hasChanges()) {
            for (MapPlayerInput.Key key : MapPlayerInput.Key.values()) {
                boolean press_a = wasPressed(key);
                boolean press_b = isPressed(key);
                if (!press_a && press_b) {
                    display.onKeyPressed(new MapKeyEvent(display, this.player, key));
                } else if (press_a && !press_b) {
                    display.onKeyReleased(new MapKeyEvent(display, this.player, key));
                }
                if (press_b) {
                    display.onKey(new MapKeyEvent(display, this.player, key));
                }
            }
        } else {
            for (MapPlayerInput.Key key : MapPlayerInput.Key.values()) {
                if (isPressed(key)) {
                    display.onKey(new MapKeyEvent(display, this.player, key));
                }
            }
        }
    }

    /**
     * Updates the received input state, receiving input information.
     * If no receivers are interested in this input, False is returned.
     * 
     * @param dx - left/right
     * @param dy - up/down
     * @param dz - enter/back
     * @return whether input will be handled by a map display
     */
    public boolean receiveInput(int dx, int dy, int dz) {
        recv_dx = dx;
        recv_dy = dy;
        recv_dz = dz;
        has_input = true;
        if (dx != 0 || dy != 0 || dz != 0) {
            key_repeat_timer++;
        } else {
            key_repeat_timer = 0;
        }
        return this._isIntercepting;
    }

    private void updateInterception(boolean intercept) {
        // No receivers, do not do input interception
        if (!intercept) {
            updateInputInterception(false);
            return;
        }

        // If the player is already inside a vehicle, we can not fake-mount him
        if (player.isInsideVehicle()) {
            updateInputInterception(false);
            return;
        }

        // Verify the player isn't flying, it results in a kick
        if (!player.isFlying() && !NMSEntity.onGround.get(Conversion.toEntityHandle.convert(player))) {
            updateInputInterception(false);
            return;
        }

        // Allowed
        this.updateInputInterception(true);
    }

    @SuppressWarnings("deprecation")
    private void updateInputInterception(boolean intercept) {
        if (!intercept && _fakeMountShown) {
            _fakeMountShown = false;

            // Despawn the mount
            PacketUtil.sendPacket(player, PacketType.OUT_ENTITY_DESTROY.newInstance(this._fakeMountId));
            return;
        }

        if (intercept && !_fakeMountShown) {
            _fakeMountShown = true;

            // Generate unique mount Id (we can re-use it)
            if (this._fakeMountId == -1) {
                this._fakeMountId = EntityUtil.getUniqueEntityId();
            }

            // Spawn the mount
            Location loc = player.getLocation();
            {
                DataWatcher data = new DataWatcher();
                data.set(NMSEntity.DATA_FLAGS, (byte) (NMSEntity.DATA_FLAG_INVISIBLE));
                data.set(NMSEntityLiving.DATA_HEALTH, 10.0F);

                CommonPacket packet = PacketType.OUT_ENTITY_SPAWN_LIVING.newInstance();
                packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.entityId, this._fakeMountId);
                packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.entityType, (int) EntityType.PIG.getTypeId());
                packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.x, loc.getX());
                packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.y, loc.getY() - 0.28);
                packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.z, loc.getZ());
                packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.UUID, UUID.randomUUID());
                packet.write(PacketType.OUT_ENTITY_SPAWN_LIVING.dataWatcher, data);
                PacketUtil.sendPacket(player, packet);
            }
            {
                CommonPacket packet = PacketType.OUT_MOUNT.newInstance();
                packet.write(PacketType.OUT_MOUNT.entityId, this._fakeMountId);
                packet.write(PacketType.OUT_MOUNT.mountedEntityIds, new int[] {player.getEntityId()});
                PacketUtil.sendPacket(player, packet);
            }
            return;
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
