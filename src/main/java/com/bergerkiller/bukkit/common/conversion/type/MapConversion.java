package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.map.MapMarker;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.world.level.saveddata.maps.MapDecorationTypeHandle;
import com.bergerkiller.generated.net.minecraft.world.level.saveddata.maps.MapIconHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import org.bukkit.map.MapCursor;

/**
 * Conversions related to Minecraft Maps (held or item frames).
 */
public class MapConversion {

    @ConverterMethod(output="net.minecraft.world.level.saveddata.maps.MapIcon")
    public static Object toMapIconHandle(MapCursor cursor) {
        return MapIconHandle.fromCursor(cursor).getRaw();
    }

    @ConverterMethod(input="net.minecraft.world.level.saveddata.maps.MapIcon")
    public static MapCursor toMapCursor(Object nmsMapCursorHandle) {
        return MapIconHandle.createHandle(nmsMapCursorHandle).toCursor();
    }

    @ConverterMethod(input="net.minecraft.core.Holder<net.minecraft.world.level.saveddata.maps.MapDecorationType>", optional=true)
    public static Holder<MapDecorationTypeHandle> holderFromNMSHolder(Object nmsHolder) {
        return Holder.fromHandle(nmsHolder, MapDecorationTypeHandle::createHandle);
    }

    @ConverterMethod(output="net.minecraft.core.Holder<net.minecraft.world.level.saveddata.maps.MapDecorationType>", optional=true)
    public static Object nmsHolderFromHolder(Holder<MapDecorationTypeHandle> holder) {
        return holder.toRawHolder();
    }

    @ConverterMethod(input="net.minecraft.world.level.saveddata.maps.MapDecorationType")
    public static Holder<MapDecorationTypeHandle> holderFromDecorationType(Object nmsDecorationType) {
        return Holder.directWrap(nmsDecorationType, MapDecorationTypeHandle::createHandle);
    }

    @ConverterMethod(output="net.minecraft.world.level.saveddata.maps.MapDecorationType")
    public static Object decorationTypeFromHolder(Holder<MapDecorationTypeHandle> holder) {
        return holder.rawValue();
    }

    @ConverterMethod(output="net.minecraft.world.level.saveddata.maps.MapDecorationType")
    public static Object decorationTypeFromId(byte typeId) {
        return getTypeFromId(typeId).getHandle().rawValue();
    }

    @ConverterMethod(input="net.minecraft.core.Holder<net.minecraft.world.level.saveddata.maps.MapDecorationType>", optional=true)
    public static MapMarker.Type getTypeFromNMSHolder(Object nmsHolder) {
        return MapMarker.Type.fromHandle(holderFromNMSHolder(nmsHolder));
    }

    @ConverterMethod(output="net.minecraft.core.Holder<net.minecraft.world.level.saveddata.maps.MapDecorationType>", optional=true)
    public static Object getNMSHolderFromType(MapMarker.Type type) {
        return type.getHandle().toRawHolder();
    }

    @ConverterMethod(input="net.minecraft.world.level.saveddata.maps.MapDecorationType")
    public static MapMarker.Type getTypeFromDecorationType(Object nmsDecorationType) {
        return MapMarker.Type.fromHandle(holderFromDecorationType(nmsDecorationType));
    }

    @ConverterMethod(output="net.minecraft.world.level.saveddata.maps.MapDecorationType")
    public static Object getDecorationTypeFromType(MapMarker.Type type) {
        return type.getHandle().rawValue();
    }

    @ConverterMethod
    @Deprecated
    public static MapMarker.Type getTypeFromId(byte typeId) {
        return MapMarker.Type.fromLegacyId(typeId);
    }

    @ConverterMethod
    @Deprecated
    public static byte getIdFromType(MapMarker.Type type) {
        return type.id();
    }
}
