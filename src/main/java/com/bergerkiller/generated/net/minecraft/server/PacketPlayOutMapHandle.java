package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.map.MapCursor;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutMapHandle extends PacketHandle {
    /** @See {@link PacketPlayOutMapClass} */
    public static final PacketPlayOutMapClass T = new PacketPlayOutMapClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutMapHandle.class, "net.minecraft.server.PacketPlayOutMap");

    /* ============================================================================== */

    public static PacketPlayOutMapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public int getItemId() {
        return T.itemId.getInteger(getRaw());
    }

    public void setItemId(int value) {
        T.itemId.setInteger(getRaw(), value);
    }

    public byte getScale() {
        return T.scale.getByte(getRaw());
    }

    public void setScale(byte value) {
        T.scale.setByte(getRaw(), value);
    }

    public MapCursor[] getCursors() {
        return T.cursors.get(getRaw());
    }

    public void setCursors(MapCursor[] value) {
        T.cursors.set(getRaw(), value);
    }

    public int getXmin() {
        return T.xmin.getInteger(getRaw());
    }

    public void setXmin(int value) {
        T.xmin.setInteger(getRaw(), value);
    }

    public int getYmin() {
        return T.ymin.getInteger(getRaw());
    }

    public void setYmin(int value) {
        T.ymin.setInteger(getRaw(), value);
    }

    public int getWidth() {
        return T.width.getInteger(getRaw());
    }

    public void setWidth(int value) {
        T.width.setInteger(getRaw(), value);
    }

    public int getHeight() {
        return T.height.getInteger(getRaw());
    }

    public void setHeight(int value) {
        T.height.setInteger(getRaw(), value);
    }

    public byte[] getPixels() {
        return T.pixels.get(getRaw());
    }

    public void setPixels(byte[] value) {
        T.pixels.set(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutMapClass extends Template.Class<PacketPlayOutMapHandle> {
        public final Template.Field.Integer itemId = new Template.Field.Integer();
        public final Template.Field.Byte scale = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Boolean track = new Template.Field.Boolean();
        public final Template.Field.Converted<MapCursor[]> cursors = new Template.Field.Converted<MapCursor[]>();
        public final Template.Field.Integer xmin = new Template.Field.Integer();
        public final Template.Field.Integer ymin = new Template.Field.Integer();
        public final Template.Field.Integer width = new Template.Field.Integer();
        public final Template.Field.Integer height = new Template.Field.Integer();
        public final Template.Field<byte[]> pixels = new Template.Field<byte[]>();

    }

}

