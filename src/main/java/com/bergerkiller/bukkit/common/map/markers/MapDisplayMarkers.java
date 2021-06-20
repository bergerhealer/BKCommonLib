package com.bergerkiller.bukkit.common.map.markers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.map.MapDisplayTile;
import com.bergerkiller.bukkit.common.map.MapMarker;
import com.bergerkiller.bukkit.common.map.MapSession;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.UniqueHash;

public class MapDisplayMarkers {
    public static final UniqueHash RANDOM_NAME_SOURCE = new UniqueHash();
    public static final MapDisplayMarkerApplier APPLIER = Template.Class.create(MapDisplayMarkerApplier.class, Common.TEMPLATE_RESOLVER);
    private final Map<String, Entry> markersById = new HashMap<>();
    private final Map<IntVector2, MapDisplayMarkerTile> markersByTile = new HashMap<>();

    public void clear() {
        markersById.clear();
        for (MapDisplayMarkerTile tile : markersByTile.values()) {
            tile.clear();
        }
    }

    public Collection<MapMarker> values() {
        return Collections.unmodifiableCollection(markersById.values()
                .stream().map(e -> e.value).collect(Collectors.toList()));
    }

    public MapMarker get(String id) {
        Entry e = markersById.get(id);
        return (e == null) ? null : e.value;
    }

    public MapMarker add(MapMarker marker) {
        MapDisplayMarkerTile tileForMarker = computeTileAtPosition(marker.getPositionX(), marker.getPositionY());
        Entry previousEntry = markersById.put(marker.getId(), new Entry(marker, tileForMarker));
        tileForMarker.add(marker);

        // Replacing an existing marker, requires cleanup
        if (previousEntry != null) {
            previousEntry.setTile(null);
        }

        return marker;
    }

    public void updateVisible(MapMarker marker, boolean new_visible) {
        if (marker.isVisible() != new_visible) {
            Entry entry = markersById.get(marker.getId());
            if (entry != null) {
                entry.setTile(new_visible ? computeTileAtPosition(marker.getPositionX(), marker.getPositionY()) : null);
            }
        }
    }

    public void update(MapMarker marker) {
        Entry entry = markersById.get(marker.getId());
        if (entry != null && entry.tile != null) {
            entry.tile.setChanged(true);
        }
    }

    public MapMarker remove(String id) {
        Entry removedEntry = markersById.remove(id);
        if (removedEntry != null) {
            removedEntry.setTile(null);
            return removedEntry.value;
        } else {
            return null;
        }
    }

    public boolean remove(MapMarker marker) {
        Entry removedEntry = markersById.remove(marker.getId());
        if (removedEntry == null) {
            return false;
        } else if (removedEntry.value != marker) {
            // Oops. Double-remove. Original marker isn't stored here.
            markersById.put(removedEntry.value.getId(), removedEntry);
            return false;
        } else {
            removedEntry.setTile(null);
            return true;
        }
    }

    public void move(MapMarker marker, double new_x, double new_y) {
        if (!marker.isVisible()) {
            return; // not stored in tiles
        }
        Entry oldEntry = markersById.get(marker.getId());
        if (oldEntry == null) {
            return; // not stored
        }

        MapDisplayMarkerTile new_tile = computeTileAtPosition(new_x, new_y);
        if (oldEntry.tile == new_tile) {
            // Verify the change in position is significant enough to move the actual icon
            // We do this by checking whether the byte representation changed
            if (oldEntry.tile.encodeX(marker.getPositionX()) != oldEntry.tile.encodeX(new_x) ||
                oldEntry.tile.encodeY(marker.getPositionY()) != oldEntry.tile.encodeY(new_y))
            {
                oldEntry.tile.setChanged(true);
            }
        } else {
            oldEntry.setTile(new_tile);
        }
    }

    public void synchronize(MapSession session) {
        if (markersByTile.isEmpty()) {
            return; // skip
        }

        for (MapDisplayTile displayedTile : session.tiles) {
            MapDisplayMarkerTile tile = markersByTile.get(displayedTile.tile);
            if (tile == null || !tile.isChanged()) {
                continue;
            }

            // Has changes, verify whether all owners have received them
            MapDisplayTile.Update mapUpdate = null;
            for (MapSession.Owner owner : session.onlineOwners) {
                // Already synchronized during map content update
                if (tile.isSynchronized(owner.player)) {
                    continue;
                }

                // Requires update, ask the tile to do this
                if (mapUpdate == null) {
                    mapUpdate = new MapDisplayTile.Update(displayedTile.tile, displayedTile.getMapId());
                    APPLIER.apply(mapUpdate.packet.getRaw(), tile);
                } else {
                    mapUpdate = mapUpdate.clone();
                }

                // Send to this player
                PacketUtil.sendPacket(owner.player, mapUpdate.packet, false);
            }
        }

        // Go by all tiles and clean them up
        Iterator<MapDisplayMarkerTile> iter = markersByTile.values().iterator();
        while (iter.hasNext()) {
            MapDisplayMarkerTile tile = iter.next();
            if (tile.isEmpty()) {
                iter.remove();
            } else {
                tile.setChanged(false);
            }
        }
    }

    // Informs that the marker data for a player, for a tile, has just been synchronized
    public void setMarkersSynchronized(Player viewer, MapDisplayTile.Update mapUpdate) {
        MapDisplayMarkerTile tile = markersByTile.get(mapUpdate.tile);
        if (tile != null) {
            tile.setSynchronized(viewer);
        }
    }

    // Adds marker data to the update packet for a map
    // Returns true if a change of markers was transmitted
    // Meaning, if true, these synchronized markers are important
    public boolean addMarkersToUpdate(Player viewer, MapDisplayTile.Update mapUpdate) {
        MapDisplayMarkerTile tile = markersByTile.get(mapUpdate.tile);
        if (tile == null) {
            mapUpdate.packet.setCursors(Collections.emptyList());
            return false;
        } else {
            APPLIER.apply(mapUpdate.packet.getRaw(), tile);
            return tile.isChangedFor(viewer);
        }
    }

    private MapDisplayMarkerTile computeTileAtPosition(double x, double y) {
        int tileX = MathUtil.floor(x) >> 7;
        int tileY = MathUtil.floor(y) >> 7;
        return markersByTile.computeIfAbsent(new IntVector2(tileX, tileY), MapDisplayMarkerTile::new);
    }

    public static final class Entry {
        public final MapMarker value;
        public MapDisplayMarkerTile tile;

        public Entry(MapMarker value, MapDisplayMarkerTile tile) {
            this.value = value;
            this.tile = tile;
        }

        public void setTile(MapDisplayMarkerTile new_tile) {
            if (tile != null) {
                tile.remove(value);
                tile = null;
            }
            if (new_tile != null) {
                tile = new_tile;
                new_tile.add(value);
            }
        }
    }
}
