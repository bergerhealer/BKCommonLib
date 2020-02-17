package com.bergerkiller.bukkit.common.map;

import java.util.List;
import java.util.UUID;

import org.bukkit.map.MapCursor;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.map.util.MapUUID;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;

/**
 * A single 128x128 map display tile. Multiple tiles may be used to make up a single map display.
 */
public class MapDisplayTile {
    private static final int RESOLUTION = 128;

    private final MapUUID uuid;
    public final int tileX, tileY;

    public MapDisplayTile(UUID mapDisplayUUID, int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.uuid = new MapUUID(mapDisplayUUID, this.tileX, this.tileY);
    }

    public void addUpdatePackets(MapDisplay display, List<CommonPacket> packets, MapClip clip) {
        int mapId = CommonPlugin.getInstance().getMapController().getMapId(this.uuid);

        CommonPacket mapUpdate = PacketType.OUT_MAP.newInstance();
        mapUpdate.write(PacketType.OUT_MAP.cursors, new MapCursor[0]);
        mapUpdate.write(PacketType.OUT_MAP.itemId, mapId);
        mapUpdate.write(PacketType.OUT_MAP.scale, (byte) 1);
        mapUpdate.write(PacketType.OUT_MAP.track, false);

        int startX = this.tileX * RESOLUTION;
        int startY = this.tileY * RESOLUTION;
        int stride = display.getWidth();
        int srcPos = (startY * stride) + startX;
        int dstPos = 0;

        byte[] liveBuffer = display.getLiveBuffer();
        MapClip tileClip = (clip == null) ? null : clip.getArea(startX, startY, RESOLUTION, RESOLUTION);
        if (tileClip == null || tileClip.everything) {
            mapUpdate.write(PacketType.OUT_MAP.xmin, 0);
            mapUpdate.write(PacketType.OUT_MAP.ymin, 0);
            mapUpdate.write(PacketType.OUT_MAP.width, RESOLUTION);
            mapUpdate.write(PacketType.OUT_MAP.height, RESOLUTION);

            // Copy the full 128x128 tile area
            byte[] pixels = new byte[RESOLUTION * RESOLUTION];
            for (int y = 0; y < RESOLUTION; y++) {
                System.arraycopy(liveBuffer, srcPos, pixels, dstPos, RESOLUTION);
                srcPos += stride;
                dstPos += RESOLUTION;
            }
            mapUpdate.write(PacketType.OUT_MAP.pixels, pixels);
        } else {
            if (!tileClip.dirty) {
                return; // no updates for this tile
            }

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

            mapUpdate.write(PacketType.OUT_MAP.xmin, tileClip.getX());
            mapUpdate.write(PacketType.OUT_MAP.ymin, tileClip.getY());
            mapUpdate.write(PacketType.OUT_MAP.width, w);
            mapUpdate.write(PacketType.OUT_MAP.height, h);
            mapUpdate.write(PacketType.OUT_MAP.pixels, pixels);
        }
        packets.add(mapUpdate);
    }
}
