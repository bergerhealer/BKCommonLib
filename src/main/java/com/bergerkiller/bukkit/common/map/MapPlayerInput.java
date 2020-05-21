package com.bergerkiller.bukkit.common.map;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.TickTracker;
import com.bergerkiller.bukkit.common.controller.Tickable;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.server.AxisAlignedBBHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityLivingHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutEntityTeleportHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutSpawnEntityLivingHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;

/**
 * Input controller for virtual map navigation and UI.
 * A single instance manages the input coming from a single player.
 */
public class MapPlayerInput implements Tickable {
    private int last_dx, last_dy, last_dz;
    private int curr_dx, curr_dy, curr_dz;
    private int recv_dx, recv_dy, recv_dz;
    private int key_repeat_timer;
    private int ticks_without_input = 0;
    private boolean has_input;
    private int _fakeMountId = -1;
    private Vector _fakeMountLastPos = new Vector();
    private boolean _fakeMountShown = false;
    private boolean _isIntercepting = false;
    private boolean _newInterceptState = false;
    private final TickTracker _inputTickTracker = new TickTracker();
    public final Player player;

    public MapPlayerInput(Player player) {
        this.player = player;
        this._inputTickTracker.setRunnable(() -> {
            _isIntercepting = _newInterceptState;
            _newInterceptState = false;

            updateInterception(_isIntercepting);
            if (!MapPlayerInput.this.player.isInsideVehicle() && !_fakeMountShown) {
                receiveInput(0, 0, 0);
            }

            last_dx = curr_dx;
            last_dy = curr_dy;
            last_dz = curr_dz;
            curr_dx = recv_dx;
            curr_dy = recv_dy;
            curr_dz = recv_dz;
        });
        reset();
    }

    /**
     * Gets whether a fake mount is currently spawned and mounted by the player to intercept input
     * 
     * @return True if the fake mount is shown, False if not.
     */
    public final boolean isFakeMountShown() {
        return this._fakeMountShown;
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
     * For how many ticks a key has been held down
     * 
     * @return repeat tick count
     */
    public int getRepeat() {
        return this.key_repeat_timer;
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
     * Called when the player is no longer online
     */
    public void onDisconnected() {
        reset();
    }

    /**
     * Updates the internal input state.
     */
    @Override
    public void onTick() {
        this._inputTickTracker.update();

        // When too much time passes by without input from the player, something went really wrong
        // This is a timeout of about 2 seconds. We do this to prevent players being locked out.
        // When this timeout is reached, reset all inputs to 'none' (0/0/0)
        // Not doing so will cause the last input to repeat indefinitely.
        if (++this.ticks_without_input == 40) {
            if (this.curr_dx != 0 || this.curr_dy != 0 || this.curr_dz != 0) {
                this.receiveInput(0, 0, 0);
            }
            this.ticks_without_input = 500; // Stop
        }
    }

    /**
     * Refreshes a recipient display, calling key events on the display when
     * input is intercepted.
     * 
     * @param display to update
     * @param interceptInput whether input is intercepted
     */
    public void handleDisplayUpdate(MapDisplay display, boolean interceptInput) {
        this._inputTickTracker.update();
        this._newInterceptState |= interceptInput;

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
                    MapKeyEvent event = new MapKeyEvent(display, this, key);
                    display.onKeyPressed(event); // Repeat!
                    display.onKey(event);
                    display.getRootWidget().onKeyPressed(event);
                    display.getRootWidget().onKey(event);
                } else if (press_a) {
                    MapKeyEvent event = new MapKeyEvent(display, this, key);
                    display.onKeyReleased(event);
                    display.getRootWidget().onKeyReleased(event);
                }
            }
        } if (hasChanges()) {
            for (MapPlayerInput.Key key : MapPlayerInput.Key.values()) {
                boolean press_a = wasPressed(key);
                boolean press_b = isPressed(key);
                if (!press_a && press_b) {
                    MapKeyEvent event = new MapKeyEvent(display, this, key);
                    display.onKeyPressed(event);
                    display.getRootWidget().onKeyPressed(event);
                } else if (press_a && !press_b) {
                    MapKeyEvent event = new MapKeyEvent(display, this, key);
                    display.onKeyReleased(event);
                    display.getRootWidget().onKeyReleased(event);
                }
                if (press_b) {
                    MapKeyEvent event = new MapKeyEvent(display, this, key);
                    display.onKey(event);
                    display.getRootWidget().onKey(event);
                }
            }
        } else {
            for (MapPlayerInput.Key key : MapPlayerInput.Key.values()) {
                if (isPressed(key)) {
                    MapKeyEvent event = new MapKeyEvent(display, this, key);
                    display.onKey(event);
                    display.getRootWidget().onKey(event);
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
        ticks_without_input = 0;
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
        if (!player.isFlying() && !player.isOnGround()) {
            // Check if there is a block below the player, which means onGround is actually true
            // This is because, when the player is 'floating' while intercepting, onGround stays false
            EntityHandle playerHandle = EntityHandle.fromBukkit(player);
            double half_width = 0.5 * (double) playerHandle.getWidth();
            double below = 0.1;
            AxisAlignedBBHandle below_bounds = AxisAlignedBBHandle.createNew(
                    playerHandle.getLocX() - half_width,
                    playerHandle.getLocY() - below,
                    playerHandle.getLocZ() - half_width,
                    playerHandle.getLocX() + half_width,
                    playerHandle.getLocY(),
                    playerHandle.getLocZ() + half_width
            );
            if (WorldHandle.fromBukkit(player.getWorld()).isNotCollidingWithBlocks(playerHandle, below_bounds)) {
                updateInputInterception(false);
                return;
            }
        }

        // Allowed
        this.updateInputInterception(true);
    }

    private void updateInputInterception(boolean intercept) {
        if (!intercept && _fakeMountShown) {
            _fakeMountShown = false;

            // Despawn the mount
            PacketUtil.sendPacket(player, PacketType.OUT_ENTITY_DESTROY.newInstance(this._fakeMountId));

            // Resend current player position to the player
            Location loc = this.player.getLocation();
            PacketPlayOutPositionHandle positionPacket = PacketPlayOutPositionHandle.createAbsolute(loc);
            PacketUtil.sendPacket(player, positionPacket);
            return;
        }

        if (intercept) {
            // Get expected position of the mount
            Vector pos = player.getLocation().toVector();
            pos.setZ(pos.getZ() + 0.1);
            pos.setY(pos.getY() + 0.002);

            if (!_fakeMountShown) {
                _fakeMountShown = true;

                // Generate unique mount Id (we can re-use it)
                if (this._fakeMountId == -1) {
                    this._fakeMountId = EntityUtil.getUniqueEntityId();
                }

                // Store initial position
                this._fakeMountLastPos = pos;

                // Spawn the mount
                {
                    DataWatcher data = new DataWatcher();
                    data.set(EntityHandle.DATA_FLAGS, (byte) (EntityHandle.DATA_FLAG_INVISIBLE));
                    data.set(EntityLivingHandle.DATA_HEALTH, 10.0F);

                    PacketPlayOutSpawnEntityLivingHandle packet = PacketPlayOutSpawnEntityLivingHandle.createNew();
                    packet.setEntityId(this._fakeMountId);
                    packet.setEntityUUID(UUID.randomUUID());
                    packet.setEntityType(EntityType.CHICKEN);
                    packet.setPosX(pos.getX());
                    packet.setPosY(pos.getY());
                    packet.setPosZ(pos.getZ());
                    PacketUtil.sendEntityLivingSpawnPacket(player, packet, data);
                }
                {
                    if (PacketType.OUT_MOUNT.getType() != null) {
                        CommonPacket packet = PacketType.OUT_MOUNT.newInstance();
                        packet.write(PacketType.OUT_MOUNT.entityId, this._fakeMountId);
                        packet.write(PacketType.OUT_MOUNT.mountedEntityIds, new int[] {player.getEntityId()});
                        PacketUtil.sendPacket(player, packet);
                    } else {
                        CommonPacket packet = PacketType.OUT_ENTITY_ATTACH.newInstance();
                        packet.write(PacketType.OUT_ENTITY_ATTACH.vehicleId, this._fakeMountId);
                        packet.write(PacketType.OUT_ENTITY_ATTACH.passengerId, player.getEntityId());
                        PacketUtil.sendPacket(player, packet);
                    }
                }
            }

            // When player position changes, refresh mount position with a simple teleport packet
            if (this._fakeMountId != -1 && !pos.equals(this._fakeMountLastPos)) {
                this._fakeMountLastPos = pos;

                PacketPlayOutEntityTeleportHandle tp_packet = PacketPlayOutEntityTeleportHandle.createNew(
                        this._fakeMountId,
                        pos.getX(), pos.getY(), pos.getZ(),
                        0.0f, 0.0f, false);
                PacketUtil.sendPacket(player, tp_packet);
            }
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
