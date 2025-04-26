package com.bergerkiller.bukkit.common.map.markers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.map.MapMarker;

/**
 * Metadata information about markers for a single 128x128 tile
 */
public class MapDisplayMarkerTile {
    private final IntVector2 coordinates;
    private final double offsetX, offsetY;
    private final List<MapMarker> visibleMarkers = new ArrayList<>();
    private final Set<Player> sentToPlayers = new HashSet<Player>();
    private boolean changed = false;

    public MapDisplayMarkerTile(IntVector2 coordinates) {
        this.coordinates = coordinates;
        this.offsetX = (double) (coordinates.x << 7);
        this.offsetY = (double) (coordinates.z << 7);
    }

    public IntVector2 getCoordinates() {
        return coordinates;
    }

    public boolean isEmpty() {
        return visibleMarkers.isEmpty();
    }

    public int getMarkerCount() {
        return visibleMarkers.size();
    }

    public MapMarker getMarker(int index) {
        return visibleMarkers.get(index);
    }

    public void setChanged(boolean new_changed) {
        changed = new_changed;
        sentToPlayers.clear();
    }

    public boolean isChanged() {
        return changed;
    }

    public void clear() {
        if (!visibleMarkers.isEmpty()) {
            visibleMarkers.clear();
            changed = true;
        }
    }

    public void add(MapMarker marker) {
        visibleMarkers.add(marker);
        changed = true;
    }

    public void remove(MapMarker marker) {
        visibleMarkers.remove(marker);
        changed = true;
    }

    public boolean isSynchronized(Player viewer) {
        return sentToPlayers.contains(viewer);
    }

    public void setSynchronized(Player viewer) {
        if (changed) {
            sentToPlayers.add(viewer);
        }
    }

    // Changes were sent to this player, so for this player,
    // the tile markers have not changed.
    public boolean isChangedFor(Player viewer) {
        return isChanged() && !sentToPlayers.contains(viewer);
    }

    public byte encodeX(double positionX) {
        return encodeToByte(2.0 * (positionX - offsetX));
    }

    public byte encodeY(double positionY) {
        return encodeToByte(2.0 * (positionY - offsetY));
    }

    public byte encodeRotation(double rotation) {
        return (byte) ((int) ((rotation + 180.0) / (360.0 / 16.0)) & 15);
    }

    private static byte encodeToByte(double value) {
        if (value <= 0.0) {
            return (byte) Byte.MIN_VALUE;
        } else if (value >= 256.0) {
            return (byte) Byte.MAX_VALUE;
        } else {
            return (byte) (value - 128.0);
        }
    }
}
