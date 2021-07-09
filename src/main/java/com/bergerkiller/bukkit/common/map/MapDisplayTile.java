package com.bergerkiller.bukkit.common.map;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.map.util.MapUUID;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutMapHandle;

/**
 * A single 128x128 map display tile. Multiple tiles may be used to make up a single map display.
 */
public class MapDisplayTile {
    /**
     * The resolution of a single tile
     */
    public static final int RESOLUTION = 128;

    private final MapUUID uuid;
    public final int tileX, tileY;
    public final IntVector2 tile;

    public MapDisplayTile(UUID mapDisplayUUID, int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.tile = new IntVector2(tileX, tileY);
        this.uuid = new MapUUID(mapDisplayUUID, this.tileX, this.tileY);
    }

    public MapUUID getMapTileUUID() {
        return this.uuid;
    }

    public int getMapId() {
        return CommonPlugin.getInstance().getMapController().getMapId(this.uuid);
    }

    public void addTileUpdate(MapDisplay display, Player viewer, List<Update> updates, MapClip clip) {
        Update update = getTileUpdate(display, viewer, clip);
        if (update != null) {
            updates.add(update);
        }
    }

    public Update getTileUpdate(MapDisplay display, Player viewer, MapClip clip) {
        int startX = this.tileX * RESOLUTION;
        int startY = this.tileY * RESOLUTION;

        // If this tile is out of bounds of the display resolution, do nothing
        // This happening is technically a bug! But this at least prevents an error spam
        if (startX >= display.getWidth() || startY >= display.getHeight()) {
            //System.err.println("Invalid tile [" + this.tileX + ", " + this.tileY + "] added to display!");
            return null;
        }

        Update mapUpdate = new Update(tile, getMapId());

        boolean markersChanged = display.getMarkerManager().addMarkersToUpdate(viewer, mapUpdate);

        int stride = display.getWidth();
        int srcPos = (startY * stride) + startX;
        int dstPos = 0;

        byte[] liveBuffer = display.getLiveBuffer();
        if (liveBuffer == null) {
            return null; // Display was not started
        }

        MapClip tileClip = (clip == null) ? null : clip.getArea(startX, startY, RESOLUTION, RESOLUTION);
        if (tileClip == null || tileClip.isEverythingDirty()) {
            // Copy the full 128x128 tile area
            byte[] pixels = new byte[RESOLUTION * RESOLUTION];
            for (int y = 0; y < RESOLUTION; y++) {
                System.arraycopy(liveBuffer, srcPos, pixels, dstPos, RESOLUTION);
                srcPos += stride;
                dstPos += RESOLUTION;
            }
            mapUpdate.packet.setPixelData(0, 0, RESOLUTION, RESOLUTION, pixels);
            return mapUpdate;
        } else if (tileClip.isDirty()) {
            int w = tileClip.getWidth();
            int h = tileClip.getHeight();
            byte[] pixels = new byte[w * h];

            srcPos += tileClip.getY() * stride;
            srcPos += tileClip.getX();
            for (int y = 0; y < h; y++) {
                System.arraycopy(liveBuffer, srcPos, pixels, dstPos, w);
                srcPos += stride;
                dstPos += w;
            }

            mapUpdate.packet.setPixelData(tileClip.getX(), tileClip.getY(), w, h, pixels);
            return mapUpdate;
        } else {
            // No updates for this tile, only send if markers changed
            return markersChanged ? mapUpdate : null;
        }
    }

    /**
     * A single update packet for a tile
     */
    public static final class Update implements Cloneable {
        public final IntVector2 tile;
        public final PacketPlayOutMapHandle packet;

        public Update(IntVector2 tile, int mapId) {
            this.tile = tile;
            this.packet = PacketPlayOutMapHandle.createNew(); // Note: fully initializes a valid packet!
            this.packet.setMapId(mapId);
        }

        private Update(IntVector2 tile, PacketPlayOutMapHandle packet) {
            this.tile = tile;
            this.packet = packet;
        }

        @Override
        public Update clone() {
            return new Update(tile, PacketPlayOutMapHandle.createHandle(
                            PacketType.OUT_MAP.cloneInstance(packet.getRaw())));
        }
    }
}
