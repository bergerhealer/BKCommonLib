package com.bergerkiller.bukkit.common.map;

import java.util.List;

import org.bukkit.map.MapCursor;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;

/**
 * A single 128x128 map display tile. Multiple tiles may be used to make up a single map display.
 */
public class MapDisplayTile {
    private static final int RESOLUTION = 128;

    private MapDisplay map;
    private int tileX, tileY;

    public void setDisplay(MapDisplay map, int tileX, int tileY) {
        this.map = map;
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public void addUpdatePackets(List<CommonPacket> packets, MapClip clip) {
        short mapId = CommonPlugin.getInstance().getMapController().getMapId(this.map.getMapInfo().uuid);

        CommonPacket mapUpdate = PacketType.OUT_MAP.newInstance();
        mapUpdate.write(PacketType.OUT_MAP.cursors, new MapCursor[0]);
        mapUpdate.write(PacketType.OUT_MAP.itemId, (int) mapId);
        mapUpdate.write(PacketType.OUT_MAP.scale, (byte) 1);
        mapUpdate.write(PacketType.OUT_MAP.track, false);

        int startX = this.tileX * RESOLUTION;
        int startY = this.tileY * RESOLUTION;
        int stride = this.map.getWidth();
        int srcPos = (startY * stride) + startX;
        int dstPos = 0;

        byte[] liveBuffer = this.map.getLiveBuffer();
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
