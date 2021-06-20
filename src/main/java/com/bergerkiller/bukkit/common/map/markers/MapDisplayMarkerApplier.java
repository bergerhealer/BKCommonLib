package com.bergerkiller.bukkit.common.map.markers;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Applies the marker objects inside a marker tile to a PacketPlayOutMap
 */
@Template.Package("net.minecraft.world.level.saveddata.maps")
@Template.Import("net.minecraft.network.chat.IChatBaseComponent")
@Template.Import("net.minecraft.network.protocol.game.PacketPlayOutMap")
@Template.Import("com.bergerkiller.bukkit.common.map.markers.MapDisplayMarkerTile")
@Template.Import("com.bergerkiller.bukkit.common.map.MapMarker")
@Template.Import("com.bergerkiller.bukkit.common.wrappers.ChatText")
@Template.InstanceType("net.minecraft.world.level.saveddata.maps.MapIcon")
public abstract class MapDisplayMarkerApplier extends Template.Class<Template.Handle> {
    /*
     * <MAP_APPLY_MARKERS>
     * public static void apply(Object rawPacket, MapDisplayMarkerTile tile) {
     *     net.minecraft.network.protocol.game.PacketPlayOutMap packet = (net.minecraft.network.protocol.game.PacketPlayOutMap) rawPacket;
     *     int numMarkers = tile.getMarkerCount();
     * 
     * #if version >= 1.17
     *     List cursors = new ArrayList(numMarkers);
     * #else
     *     MapIcon[] cursors = new MapIcon[numMarkers];
     * #endif
     * 
     *     for (int i = 0; i < numMarkers; i++) {
     *         // Prepare arguments
     *         com.bergerkiller.bukkit.common.map.MapMarker marker = tile.getMarker(i);
     * 
     * #if version >= 1.11
     *         MapIcon$Type type = MapIcon$Type.a(marker.getType().id());
     * #endif
     * 
     *         byte x = tile.encodeX(marker.getPositionX());
     *         byte y = tile.encodeY(marker.getPositionY());
     *         byte rot = tile.encodeRotation(marker.getRotation());
     * 
     * #if version >= 1.13
     *         ChatText caption_ct = marker.getFormattedCaption();
     *         IChatBaseComponent caption = (caption_ct==null)?null:((IChatBaseComponent) caption_ct.clone().getRawHandle());
     * #endif
     * 
     *         // Create MapIcon and assign to array
     * #if version >= 1.17
     *         cursors.add(new MapIcon(type, x, y, rot, caption));
     * #elseif version >= 1.13
     *         cursors[i] = new MapIcon(type, x, y, rot, caption);
     * #elseif version >= 1.11
     *         cursors[i] = new MapIcon(type, x, y, rot);
     * #else
     *         cursors[i] = new MapIcon(marker.getType().id(), x, y, rot);
     * #endif
     *     }
     * 
     *     // Assign cursors[] array to cursors field of packet using reflection
     * #if version >= 1.17
     *     #require net.minecraft.network.protocol.game.PacketPlayOutMap List<net.minecraft.world.level.saveddata.maps.MapIcon> decorations;
     *     packet#decorations = cursors;
     * #else
     *   #if version >= 1.14
     *     #require net.minecraft.network.protocol.game.PacketPlayOutMap private net.minecraft.world.level.saveddata.maps.MapIcon[] cursors:e;
     *   #elseif version >= 1.9
     *     #require net.minecraft.network.protocol.game.PacketPlayOutMap private net.minecraft.world.level.saveddata.maps.MapIcon[] cursors:d;
     *   #else
     *     #require net.minecraft.network.protocol.game.PacketPlayOutMap private net.minecraft.world.level.saveddata.maps.MapIcon[] cursors:c;
     *   #endif
     *     packet#cursors = cursors;
     * #endif
     * }
     */
    @Template.Generated("%MAP_APPLY_MARKERS%")
    public abstract void apply(Object nmsMapPacketHandle, MapDisplayMarkerTile tile);
}
