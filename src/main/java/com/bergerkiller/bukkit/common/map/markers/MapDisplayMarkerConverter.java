package com.bergerkiller.bukkit.common.map.markers;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

import java.util.List;

/**
 * Computes the MapIcon value for markers assigned to a map display tile
 */
@Template.Package("net.minecraft.world.level.saveddata.maps")
@Template.Import("net.minecraft.network.chat.IChatBaseComponent")
@Template.Import("com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutMapHandle.Builder")
@Template.Import("com.bergerkiller.bukkit.common.map.markers.MapDisplayMarkerTile")
@Template.Import("com.bergerkiller.bukkit.common.map.MapMarker")
@Template.Import("com.bergerkiller.bukkit.common.wrappers.ChatText")
@Template.InstanceType("net.minecraft.world.level.saveddata.maps.MapIcon")
public abstract class MapDisplayMarkerConverter extends Template.Class<Template.Handle> {
    /*
     * <MAP_ENCODE_MARKERS>
     * public static List<Object> getMapIcons(MapDisplayMarkerTile tile) {
     *     int numMarkers = tile.getMarkerCount();
     *
     *     List cursors = new ArrayList(numMarkers);
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
     *         // Create MapIcon and assign to list
     * #if version >= 1.13
     *         cursors.add(new MapIcon(type, x, y, rot, caption));
     * #elseif version >= 1.11
     *         cursors.add(new MapIcon(type, x, y, rot));
     * #else
     *         cursors.add(new MapIcon(marker.getType().id(), x, y, rot));
     * #endif
     *     }
     *
     *     return cursors;
     * }
     */
    @Template.Generated("%MAP_ENCODE_MARKERS%")
    public abstract List<Object> getMapIcons(MapDisplayMarkerTile tile);
}
