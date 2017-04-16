package com.bergerkiller.bukkit.common.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.events.PacketEvent;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntity;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityLiving;

/**
 * A virtual player map is only updated for a single player, enabling an interactive personal session.
 * Only a single player can ever control the map. When a new player is attached to an player map, the old
 * player loses control.
 */
public class VirtualMapSingle extends VirtualMap {
    private static final HashMap<Player, ArrayList<VirtualMapSingle>> ALL_MAPS = new HashMap<Player, ArrayList<VirtualMapSingle>>();
    private Player _player = null;
    private boolean _interceptInput = true;
    private int _fakeMountId = -1;
    private boolean _fakeMountShown = false;
    protected final VirtualMapInput input = new VirtualMapInput();

    /**
     * Called right after the player and map item become known
     */
    public void onAttached() {
    }

    /**
     * Called right before the player and map item disappear, and this
     * virtual map ceases to function.
     */
    public void onDetached() {
    }

    @Override
    public void onTick() {}

    /**
     * Callback function called every tick while a key is pressed down.
     * These callbacks are called before {@link #onTick()}.
     * 
     * @param key that is pressing down
     */
    public void onKey(VirtualMapInput.Key key) {}

    /**
     * Callback function called when a key changed from not-pressed to pressed down
     * These callbacks are called before {@link #onTick()}.
     * 
     * @param key that was pressed down
     */
    public void onKeyPressed(VirtualMapInput.Key key) {}

    /**
     * Callback function called when a key changed from pressed to not pressed down
     * These callbacks are called before {@link #onTick()}.
     * 
     * @param key that was released
     */
    public void onKeyReleased(VirtualMapInput.Key key) {}

    /**
     * Gets the player associated with this map
     * 
     * @return player
     */
    public Player getPlayer() {
        return _player;
    }

    /**
     * Gets the live input of the virtual map
     * 
     * @return input control
     */
    public VirtualMapInput getInput() {
        return input;
    }

    /**
     * Gets whether player input is intercepted for controlling this map.
     * If intercepted, the player can not move around.
     * 
     * @return True if input is intercepted, False if not.
     */
    public boolean isInterceptingInput() {
        return this._interceptInput;
    }

    /**
     * Sets whether player input is intercepted for controlling this map.
     * If intercepted, the player can not move around.
     * 
     * @param interceptInput option
     */
    public void setInterceptingInput(boolean interceptInput) {
        this._interceptInput = interceptInput;
    }

    @Override
    protected void doTick(boolean mapVisible) {
        if (mapVisible) {
            this.doInputInterception();
            this.doInput();
        } else {
            this.input.reset();
            this.updateInputInterception(false);
        }
        super.doTick(mapVisible);
    }

    @Override
    @Deprecated
    protected void start(JavaPlugin plugin, ItemStack mapItem) {
        throw new UnsupportedOperationException("Single-player virtual maps can not be started without a player");
    }

    /**
     * Starts updating this map
     * 
     * @param plugin that owns this map
     * @param player that will be viewing this map
     * @param mapItem associated with the map to update
     */
    public void start(JavaPlugin plugin, Player player, ItemStack mapItem) {
        if (player == null) {
            throw new IllegalArgumentException("Player can not be null");
        }

        // Prepare global mapping
        ArrayList<VirtualMapSingle> list = ALL_MAPS.get(player);
        if (list == null) {
            list = new ArrayList<VirtualMapSingle>();
            ALL_MAPS.put(player, list);
        } else {
            // Stop old virtual maps for this map
            int itemId = getMapId(mapItem);
            for (VirtualMapSingle old_map : list) {
                if (old_map.itemId == itemId) {
                    old_map.stop();
                    break;
                }
            }
        }

        // Start self
        super.start(plugin, mapItem);
        this.addViewer(player);
        this._player = player;

        // Add to mapping
        list.add(this);
        ALL_MAPS.put(player, list);
        this.onAttached();
    }

    @Override
    public void stop() {
        Player player = this._player;
        this.onDetached();
        this.updateInputInterception(false);
        super.stop();
        this.removeViewer(this._player);
        this._player = null;

        // Remove from global mapping
        ArrayList<VirtualMapSingle> list = ALL_MAPS.get(player);
        if (list != null) {
            list.remove(this);
            if (list.isEmpty()) {
                ALL_MAPS.remove(player);
            }
        }
    }

    private void doInput() {
        if (!this._player.isInsideVehicle() && !this._fakeMountShown) {
            input.update(0, 0, 0);
        }
        input.doTick();
        if (input.isRepeating()) {
            for (VirtualMapInput.Key key : VirtualMapInput.Key.values()) {
                boolean press_a = input.wasPressed(key);
                boolean press_b = input.isPressed(key);
                if (press_b) {
                    this.onKeyPressed(key); // Repeat!
                    this.onKey(key);
                } else if (press_a) {
                    this.onKeyReleased(key);
                }
            }
        } if (input.hasChanges()) {
            for (VirtualMapInput.Key key : VirtualMapInput.Key.values()) {
                boolean press_a = input.wasPressed(key);
                boolean press_b = input.isPressed(key);
                if (!press_a && press_b) {
                    this.onKeyPressed(key);
                } else if (press_a && !press_b) {
                    this.onKeyReleased(key);
                }
                if (press_b) {
                    this.onKey(key);
                }
            }
        } else {
            for (VirtualMapInput.Key key : VirtualMapInput.Key.values()) {
                if (input.isPressed(key)) {
                    this.onKey(key);
                }
            }
        }
    }

    private void doInputInterception() {
        // Check if actually holding this map in the main hand
        if (this.itemId != getMapId(this._player.getInventory().getItemInMainHand())) {
            updateInputInterception(false);
            return;
        }

        // If the player is already inside a vehicle, we can not fake-mount him
        if (this._player.isInsideVehicle()) {
            updateInputInterception(false);
            return;
        }

        // Verify the player isn't flying, it results in a kick
        if (!this._player.isFlying() && !NMSEntity.onGround.get(Conversion.toEntityHandle.convert(this._player))) {
            updateInputInterception(false);
            return;
        }

        // Allowed
        this.updateInputInterception(true);
    }

    @SuppressWarnings("deprecation")
    private void updateInputInterception(boolean intercept) {
        if (this._fakeMountShown && !intercept) {
            this._fakeMountShown = false;

            // Despawn the mount
            PacketUtil.sendPacket(this._player, PacketType.OUT_ENTITY_DESTROY.newInstance(this._fakeMountId));
            return;
        }

        if (!this._fakeMountShown && intercept) {
            this._fakeMountShown = true;

            // Generate unique mount Id (we can re-use it)
            if (this._fakeMountId == -1) {
                this._fakeMountId = EntityUtil.getUniqueEntityId();
            }

            // Spawn the mount
            Location loc = this._player.getLocation();
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
                PacketUtil.sendPacket(this._player, packet);
            }
            {
                CommonPacket packet = PacketType.OUT_MOUNT.newInstance();
                packet.write(PacketType.OUT_MOUNT.entityId, this._fakeMountId);
                packet.write(PacketType.OUT_MOUNT.mountedEntityIds, new int[] {this._player.getEntityId()});
                PacketUtil.sendPacket(this._player, packet);
            }
            return;
        }
    }

    /**
     * Checks whether a particular item is a valid map item a new Virtual Single Map can be attached to.
     * If another virtual map is already attached for this player, this function returns false.
     * 
     * @param player to check
     * @param mapItem of the map to check
     * @return True if a virtual single map can be attached, False if not
     */
    public static boolean canAttach(Player player, ItemStack mapItem) {
        int id = getMapId(mapItem);
        if (id != -1) {
            ArrayList<VirtualMapSingle> maps = ALL_MAPS.get(player);
            if (maps != null) {
                for (VirtualMapSingle map : maps) {
                    if (map.itemId == id) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Searches the registry for the virtual single map attached to a certain map item for a player
     * 
     * @param player to check
     * @param mapItem to find the virtual map for
     * @return Virtual Map, or null if not found
     */
    public static VirtualMapSingle findMap(Player player, ItemStack mapItem) {
        int id = getMapId(mapItem);
        if (id != -1) {
            ArrayList<VirtualMapSingle> maps = ALL_MAPS.get(player);
            if (maps != null) {
                for (VirtualMapSingle map : maps) {
                    if (map.itemId == id) {
                        return map;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Detaches all virtual player maps a single player is carrying
     * 
     * @param player to detach all maps from
     */
    public static void detachAll(Player player) {
        ArrayList<VirtualMapSingle> list = ALL_MAPS.get(player);
        if (list != null) {
            while (!list.isEmpty()) {
                VirtualMapSingle map = list.get(0);
                map.stop();
                if (!list.isEmpty() && list.get(0) == map) {
                    list.remove(0); // guarantee removal from mapping
                }
            }
        }
    }

    /**
     * Detaches all virtual player maps associated with a certain map item
     * 
     * @param mapItem to detach all maps for
     */
    public static void detachAll(ItemStack mapItem) {
        int mapItemId = getMapId(mapItem);
        if (mapItemId == -1) {
            return;
        }
        ArrayList<VirtualMapSingle> maps = new ArrayList<VirtualMapSingle>();
        for (ArrayList<VirtualMapSingle> list : ALL_MAPS.values()) {
            for (VirtualMapSingle map : list) {
                if (map.itemId == mapItemId) {
                    maps.add(map);
                }
            }
        }
        for (VirtualMapSingle map : maps) {
            map.stop();
        }
    }

    /**
     * Internal use only
     * 
     * @param event that occurred
     */
    public static void handlePacket(PacketEvent event) {
        // Check if any virtual single maps are attached to this map
        if (event.getType() == PacketType.OUT_MAP) {
            ArrayList<VirtualMapSingle> maps = ALL_MAPS.get(event.getPlayer());
            if (maps != null) {
                int itemid = event.getPacket().read(PacketType.OUT_MAP.itemId);
                for (VirtualMapSingle map : maps) {
                    if (map.itemId == itemid) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        // Handle input coming from the player for the map
        if (event.getType() == PacketType.IN_STEER_VEHICLE) {
            Player p = event.getPlayer();
            int mapItemId = getMapId(p.getInventory().getItemInMainHand());
            if (mapItemId == -1) {
                return;
            }

            CommonPacket packet = event.getPacket();
            ArrayList<VirtualMapSingle> list = ALL_MAPS.get(p);
            if (list != null) {
                for (VirtualMapSingle map : list) {
                    if (map.itemId == mapItemId) {
                        int dx = (int) -Math.signum(packet.read(PacketType.IN_STEER_VEHICLE.sideways));
                        int dy = (int) -Math.signum(packet.read(PacketType.IN_STEER_VEHICLE.forwards));
                        int dz = 0;
                        if (packet.read(PacketType.IN_STEER_VEHICLE.unmount)) {
                            dz -= 1;
                        }
                        if (packet.read(PacketType.IN_STEER_VEHICLE.jump)) {
                            dz += 1;
                        }
                        map.input.update(dx, dy, dz);
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
}
