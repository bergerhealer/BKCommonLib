package com.bergerkiller.bukkit.common.map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.internal.CommonMapController.ViewStack;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.CommonMapController.MapDisplayInfo;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;

/**
 * Maintains information about the player owners that own the session of a Map Display.
 */
public class MapSession {
    public final HashSet<UUID> owners = new HashSet<UUID>();
    public final ArrayList<Owner> onlineOwners = new ArrayList<Owner>();
    public final MapDisplay display;
    public boolean hasHolders = false;
    public boolean hasViewers = false;
    public boolean hasNewViewers = false;
    public MapSessionMode mode = MapSessionMode.ONLINE;

    public MapSession(MapDisplay display) {
        this.display = display;
    }

    /**
     * Updates the session. Returns false if the session should be ended.
     * 
     * @return False when the session should be ended
     */
    public boolean update() {
        MapDisplayInfo info = this.display.getMapInfo();
        if (info == null) {
            return false; // can't keep a session around for a map that does not exist!
        }

        // Update players and their input
        this.hasHolders = false;
        this.hasViewers = false;
        this.hasNewViewers = false;
        if (!this.onlineOwners.isEmpty()) {
            Iterator<Owner> onlineIter = this.onlineOwners.iterator();
            while (onlineIter.hasNext()) {
                Owner owner = onlineIter.next();

                // Update input
                owner.input.handleDisplayUpdate(this.display, owner.interceptInput);

                // Check if online. Remove offline players if not set FOREVER
                if (!owner.player.isOnline()) {
                    if (this.mode != MapSessionMode.FOREVER) {
                        this.owners.remove(owner.player.getUniqueId());
                    }
                    onOwnerRemoved(owner);
                    onlineIter.remove();
                    continue;
                }

                // Check if holding the map
                owner.controlling = info.isMap(HumanHand.getItemInMainHand(owner.player));
                owner.holding = owner.controlling || info.isMap(HumanHand.getItemInOffHand(owner.player));
                if (!owner.holding && this.mode == MapSessionMode.HOLDING) {
                    this.owners.remove(owner.player.getUniqueId());
                    onOwnerRemoved(owner);
                    onlineIter.remove();
                    continue;
                }

                // Check if viewing the map at all
                owner.wasViewing = owner.viewing;
                owner.viewing = owner.holding || info.frameViewers.contains(owner.player);
                if (!owner.viewing && this.mode == MapSessionMode.VIEWING) {
                    this.owners.remove(owner.player.getUniqueId());
                    onOwnerRemoved(owner);
                    onlineIter.remove();
                    continue;
                }

                // Update state
                this.hasHolders |= owner.holding;
                this.hasViewers |= owner.viewing;
                this.hasNewViewers |= owner.isNewViewer();
            }
        }

        // Session end condition
        switch (this.mode) {
        case FOREVER:
            return true;
        case ONLINE:
            return !onlineOwners.isEmpty();
        case VIEWING:
            return this.hasViewers;
        case HOLDING:
            return this.hasHolders;
        }
        return true;
    }

    public void addOwner(Player ownerPlayer) {
        if (this.owners.add(ownerPlayer.getUniqueId())) {
            Owner owner = new Owner(ownerPlayer, this.display);
            this.onlineOwners.add(owner);
            onOwnerAdded(owner);
        }
    }

    public boolean removeOwner(Player ownerPlayer) {
        if (this.owners.remove(ownerPlayer.getUniqueId())) {
            Iterator<Owner> iter = this.onlineOwners.iterator();
            while (iter.hasNext()) {
                Owner owner = iter.next();
                if (owner.player == ownerPlayer) {
                    onOwnerRemoved(owner);
                    iter.remove();
                    return true;
                }
            }
        }
        return false;
    }

    public void initOwners() {
        for (Owner owner : this.onlineOwners) {
            onOwnerAdded(owner);
        }
    }

    private void onOwnerAdded(Owner owner) {
        // Add to map display by-player mapping
        MapDisplayInfo info = this.display.getMapInfo();
        if (info != null) {
            ViewStack views = info.getStack(owner.player);
            if (views.stack.isEmpty()) {
                views.stack.addLast(this.display);
            } else {
                MapDisplay previousDisplay = views.stack.getLast();
                if (previousDisplay != this.display) {
                    previousDisplay.removeOwner(owner.player);
                    views.stack.addLast(previousDisplay);
                    views.stack.addLast(this.display);
                }
            }
        }
    }

    private void onOwnerRemoved(Owner owner) {
        // Remove map display from by-player mapping
        MapDisplayInfo info = this.display.getMapInfo();
        if (info != null) {
            ViewStack views = info.getStack(owner.player);
            if (!views.stack.isEmpty() && views.stack.getLast() == this.display) {
                // Stop intercepting input
                owner.interceptInput = false;
                owner.input.handleDisplayUpdate(this.display, false);

                // Remove self
                views.stack.removeLast();

                // If an older display is in the stack, switch to it
                if (!views.stack.isEmpty()) {
                    views.stack.getLast().addOwner(owner.player);
                }
            } else {
                // Simply remove the player from anywhere in the view stack
                views.stack.remove(this.display);
            }
        }
    }

    public void updatePlayerOnline(Player player) {
        if (this.mode == MapSessionMode.FOREVER && this.owners.contains(player.getUniqueId())) {
            if (!this.onlineOwners.contains(player)) {
                Owner owner = new Owner(player, this.display);
                this.onlineOwners.add(owner);
                this.onOwnerAdded(owner);
            }
        }
    }

    public static class Owner {
        public final Player player;
        public final MapDisplay display;
        public final MapClip clip = new MapClip();
        public final MapPlayerInput input;
        public boolean interceptInput = false; /* Input is intercepted */
        public boolean wasViewing; /* Was viewing previously */
        public boolean viewing; /* Viewing in his hands, or on item frames */
        public boolean holding; /* Holding in either hand */
        public boolean controlling; /* Holding in his main hand */

        public Owner(Player player, MapDisplay display) {
            this.player = player;
            this.display = display;
            this.controlling = false;
            this.viewing = false;
            this.holding = false;
            this.wasViewing = false;
            this.input = CommonPlugin.getInstance().getMapController().getPlayerInput(this.player);
            this.clip.markEverythingDirty();
        }

        public boolean isNewViewer() {
            return this.viewing && !this.wasViewing;
        }

        public void updateMap(List<CommonPacket> packets) {
            this.clip.clearDirty();
            for (CommonPacket packet : packets) {
                PacketUtil.sendPacket(this.player, packet, false);
            }
        }
    }
}
