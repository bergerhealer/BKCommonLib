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
    @Template.Generated("public static void apply(Object rawPacket, MapDisplayMarkerTile tile) {\n" +
            "    PacketPlayOutMap packet = (PacketPlayOutMap) rawPacket;\n" +
            "    MapIcon[] cursors = new MapIcon[tile.getMarkerCount()];\n" +
            "    for (int i = 0; i < cursors.length; i++) {\n" +

                     // Prepare arguments
            "        com.bergerkiller.bukkit.common.map.MapMarker marker = tile.getMarker(i);\n" +
            "#if version >= 1.11\n" +
            "        MapIcon$Type type = MapIcon$Type.a(marker.getType().id());\n" +
            "#endif\n" +
            "        byte x = tile.encodeX(marker.getPositionX());\n" +
            "        byte y = tile.encodeY(marker.getPositionY());\n" +
            "        byte rot = tile.encodeRotation(marker.getRotation());\n" +
            "#if version >= 1.13\n" +
            "        ChatText caption_ct = marker.getFormattedCaption();\n" +
            "        IChatBaseComponent caption = (caption_ct==null)?null:((IChatBaseComponent) caption_ct.clone().getRawHandle());\n" +
            "#endif\n" +

                     // Create MapIcon and assign to array
            "#if version >= 1.13\n" +
            "        cursors[i] = new MapIcon(type, x, y, rot, caption);\n" +
            "#elseif version >= 1.11\n" +
            "        cursors[i] = new MapIcon(type, x, y, rot);\n" +
            "#else\n" +
            "        cursors[i] = new MapIcon(marker.getType().id(), x, y, rot);\n" +
            "#endif\n" +
            "    }\n" +

                 // Assign cursors[] array to cursors field of packet using reflection
            "#if version >= 1.14\n" +
            "    #require net.minecraft.server.PacketPlayOutMap private MapIcon[] cursors:e;\n" +
            "#elseif version >= 1.9\n" +
            "    #require net.minecraft.server.PacketPlayOutMap private MapIcon[] cursors:d;\n" +
            "#else\n" +
            "    #require net.minecraft.server.PacketPlayOutMap private MapIcon[] cursors:c;\n" +
            "#endif\n" +
            "    packet#cursors = cursors;\n" +
            "}")
    public abstract void apply(Object nmsMapPacketHandle, MapDisplayMarkerTile tile);
}
