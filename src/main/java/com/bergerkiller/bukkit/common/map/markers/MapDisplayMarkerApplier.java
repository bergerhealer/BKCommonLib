package com.bergerkiller.bukkit.common.map.markers;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Applies the marker objects inside a marker tile to a PacketPlayOutMap
 */
@Template.Package("net.minecraft.server")
@Template.Import("com.bergerkiller.bukkit.common.map.markers.MapDisplayMarkerTile")
@Template.Import("com.bergerkiller.bukkit.common.map.MapMarker")
@Template.Import("com.bergerkiller.bukkit.common.wrappers.ChatText")
@Template.InstanceType("net.minecraft.server.PacketPlayOutMap")
public abstract class MapDisplayMarkerApplier extends Template.Class<Template.Handle> {
    /*
     * <MAP_APPLY_MARKERS>
     * public static void apply(Object rawPacket, MapDisplayMarkerTile tile) {
     *     PacketPlayOutMap packet = (PacketPlayOutMap) rawPacket;
     *     MapIcon[] cursors = new MapIcon[tile.getMarkerCount()];
     *     for (int i = 0; i < cursors.length; i++) {
     *         // Prepare arguments
     *         com.bergerkiller.bukkit.common.map.MapMarker marker = tile.getMarker(i);
     * #if version >= 1.11
     *         MapIcon$Type type = MapIcon$Type.a(marker.getType().id());
     * #endif
     *         byte x = tile.encodeX(marker.getPositionX());
     *         byte y = tile.encodeY(marker.getPositionY());
     *         byte rot = tile.encodeRotation(marker.getRotation());
     * #if version >= 1.13
     *         ChatText caption_ct = marker.getFormattedCaption();
     *         IChatBaseComponent caption = (caption_ct==null)?null:((IChatBaseComponent) caption_ct.clone().getRawHandle());
     * #endif
     * 
     *         // Create MapIcon and assign to array
     * #if version >= 1.13
     *         cursors[i] = new MapIcon(type, x, y, rot, caption);
     * #elseif version >= 1.11
     *         cursors[i] = new MapIcon(type, x, y, rot);
     * #else
     *         cursors[i] = new MapIcon(marker.getType().id(), x, y, rot);
     * #endif
     *     }
     * 
     *     // Assign cursors[] array to cursors field of packet using reflection
     * #if version >= 1.14
     *     #require net.minecraft.server.PacketPlayOutMap private MapIcon[] cursors:e;
     * #elseif version >= 1.9
     *     #require net.minecraft.server.PacketPlayOutMap private MapIcon[] cursors:d;
     * #else
     *     #require net.minecraft.server.PacketPlayOutMap private MapIcon[] cursors:c;
     * #endif
     *     packet#cursors = cursors;
     * }
     */
    @Template.Generated("%MAP_APPLY_MARKERS%")
    public abstract void apply(Object nmsMapPacketHandle, MapDisplayMarkerTile tile);
}
