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
        if (handleInstance == null) return null;
        PacketPlayOutMapHandle handle = new PacketPlayOutMapHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public int getItemId() {
        return T.itemId.getInteger(instance);
    }

    public void setItemId(int value) {
        T.itemId.setInteger(instance, value);
    }

    public byte getScale() {
        return T.scale.getByte(instance);
    }

    public void setScale(byte value) {
        T.scale.setByte(instance, value);
    }

    public MapCursor[] getCursors() {
        return T.cursors.get(instance);
    }

    public void setCursors(MapCursor[] value) {
        T.cursors.set(instance, value);
    }

    public int getXmin() {
        return T.xmin.getInteger(instance);
    }

    public void setXmin(int value) {
        T.xmin.setInteger(instance, value);
    }

    public int getYmin() {
        return T.ymin.getInteger(instance);
    }

    public void setYmin(int value) {
        T.ymin.setInteger(instance, value);
    }

    public int getWidth() {
        return T.width.getInteger(instance);
    }

    public void setWidth(int value) {
        T.width.setInteger(instance, value);
    }

    public int getHeight() {
        return T.height.getInteger(instance);
    }

    public void setHeight(int value) {
        T.height.setInteger(instance, value);
    }

    public byte[] getPixels() {
        return T.pixels.get(instance);
    }

    public void setPixels(byte[] value) {
        T.pixels.set(instance, value);
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

