package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.level.saveddata.maps.MapIconHandle;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import org.bukkit.map.MapCursor;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutMap")
public abstract class PacketPlayOutMapHandle extends PacketHandle {
    /** @see PacketPlayOutMapClass */
    public static final PacketPlayOutMapClass T = Template.Class.create(PacketPlayOutMapClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutMapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutMapHandle createNew(Builder builder) {
        return T.createNew.invoke(builder);
    }

    public abstract int getMapId();
    public abstract byte getScale();
    public abstract boolean isLocked();
    public abstract boolean hasCursors();
    public abstract List<MapCursor> getCursors();
    public abstract int getStartX();
    public abstract int getStartY();
    public abstract int getWidth();
    public abstract int getHeight();
    public abstract boolean hasPixels();
    public abstract byte[] getPixels();

    public static class Builder implements Cloneable {
        private int mapId = 0;
        private byte scale = (byte) 1;
        private boolean locked = false;
        private java.util.Optional<java.util.List<MapIconHandle>> mapIcons = java.util.Optional.empty();
        private boolean hasData = false;
        private int startX, startY;
        private int width, height;
        private byte[] colors = null;

        private static final DuplexConverter<MapIconHandle, MapCursor> mapCursorHandleConversion = new DuplexConverter<MapIconHandle, MapCursor>(MapIconHandle.class, MapCursor.class) {
            @Override
            public MapCursor convertInput(MapIconHandle mapIconHandle) {
                return mapIconHandle.toCursor();
            }

            @Override
            public MapIconHandle convertOutput(MapCursor mapCursor) {
                return MapIconHandle.fromCursor(mapCursor);
            }
        };

        private Builder() {
        }

        public int get_mapId() {
            return this.mapId;
        }

        public byte get_scale() {
            return scale;
        }

        public boolean get_locked() {
            return locked;
        }

        public java.util.Optional<java.util.List<org.bukkit.map.MapCursor>> get_cursors() {
            return mapIcons.map(list -> new ConvertingList<>(list, mapCursorHandleConversion));
        }

        public java.util.Optional<java.util.List<MapIconHandle>> get_cursors_nms() {
            return mapIcons;
        }

        public boolean has_data() {
            return hasData;
        }

        public int get_data_startX() {
            return startX;
        }

        public int get_data_startY() {
            return startY;
        }

        public int get_data_width() {
            return width;
        }

        public int get_data_height() {
            return height;
        }

        public byte[] get_data_colors() {
            return colors;
        }

        public Builder mapId(int mapId) {
            this.mapId = mapId;
            return this;
        }

        public Builder scale(byte scale) {
            this.scale = scale;
            return this;
        }

        public Builder locked(boolean locked) {
            this.locked = locked;
            return this;
        }

        private List<MapIconHandle> convertCursors(java.util.List<org.bukkit.map.MapCursor> cursors) {
            return new ConvertingList<>(cursors, mapCursorHandleConversion.reverse());
        }

        public Builder no_cursors() {
            return this.cursors_nms(java.util.Collections.emptyList());
        }

        public Builder cursors(java.util.List<org.bukkit.map.MapCursor> cursors) {
            return this.cursors_nms(convertCursors(cursors));
        }

        public Builder cursors_nms(java.util.List<MapIconHandle> rawCursors) {
            this.mapIcons = java.util.Optional.ofNullable(rawCursors);
            return this;
        }

        public Builder add_cursors(java.util.List<org.bukkit.map.MapCursor> cursors) {
            return add_cursors_nms(convertCursors(cursors));
        }

        public Builder add_cursors_nms(java.util.List<MapIconHandle> rawCursors) {
            if (!this.mapIcons.isPresent()) {
                this.cursors_nms(rawCursors);
            } else if (rawCursors != null && !rawCursors.isEmpty()) {
                java.util.List<MapIconHandle> existing = this.mapIcons.get();
                java.util.List<MapIconHandle> newList = new java.util.ArrayList<>(existing.size() + rawCursors.size());
                newList.addAll(existing);
                newList.addAll(rawCursors);
                this.mapIcons = java.util.Optional.of(newList);
            }
            return this;
        }

        public Builder data(int startX, int startY, int width, int height, byte[] colors) {
            this.startX = startX;
            this.startY = startY;
            this.width = width;
            this.height = height;
            this.colors = colors;
            this.hasData = true;
            return this;
        }

        @Override
        public Builder clone() {
            Builder copy = new Builder();
            copy.mapId = this.mapId;
            copy.scale = this.scale;
            copy.locked = this.locked;
            copy.mapIcons = this.mapIcons;
            copy.startX = this.startX;
            copy.startY = this.startY;
            copy.width = this.width;
            copy.height = this.height;
            copy.colors = this.colors;
            copy.hasData = this.hasData;
            return copy;
        }

        public PacketPlayOutMapHandle create() {
            return createNew(this);
        }
    }

    public static Builder build() {
        return new Builder();
    }

    public Builder mutable() {
        Builder b = build()
            .mapId(getMapId())
            .scale(getScale())
            .locked(isLocked());
        if (hasCursors()) {
            b.cursors(getCursors());
        }
        if (hasPixels()) {
            b.data(getStartX(), getStartY(), getWidth(), getHeight(), getPixels());
        }
        return b;
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutMapClass extends Template.Class<PacketPlayOutMapHandle> {
        public final Template.StaticMethod.Converted<PacketPlayOutMapHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutMapHandle>();

        public final Template.Method<Integer> getMapId = new Template.Method<Integer>();
        public final Template.Method<Byte> getScale = new Template.Method<Byte>();
        public final Template.Method<Boolean> isLocked = new Template.Method<Boolean>();
        public final Template.Method<Boolean> hasCursors = new Template.Method<Boolean>();
        public final Template.Method.Converted<List<MapCursor>> getCursors = new Template.Method.Converted<List<MapCursor>>();
        public final Template.Method<Integer> getStartX = new Template.Method<Integer>();
        public final Template.Method<Integer> getStartY = new Template.Method<Integer>();
        public final Template.Method<Integer> getWidth = new Template.Method<Integer>();
        public final Template.Method<Integer> getHeight = new Template.Method<Integer>();
        public final Template.Method<Boolean> hasPixels = new Template.Method<Boolean>();
        public final Template.Method<byte[]> getPixels = new Template.Method<byte[]>();

    }

}

