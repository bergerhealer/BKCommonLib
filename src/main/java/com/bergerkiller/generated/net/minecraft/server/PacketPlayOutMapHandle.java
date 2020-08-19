package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.map.MapCursor;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.PacketPlayOutMap")
public abstract class PacketPlayOutMapHandle extends PacketHandle {
    /** @See {@link PacketPlayOutMapClass} */
    public static final PacketPlayOutMapClass T = Template.Class.create(PacketPlayOutMapClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutMapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PacketPlayOutMapHandle createNew() {
        return T.constr.newInstance();
    }

    /* ============================================================================== */

    public abstract boolean isLocked();
    public abstract void setLocked(boolean locked);
    public abstract boolean isTrack();
    public abstract void setTrack(boolean track);
    public abstract int getItemId();
    public abstract void setItemId(int value);
    public abstract byte getScale();
    public abstract void setScale(byte value);
    public abstract MapCursor[] getCursors();
    public abstract void setCursors(MapCursor[] value);
    public abstract int getXmin();
    public abstract void setXmin(int value);
    public abstract int getYmin();
    public abstract void setYmin(int value);
    public abstract int getWidth();
    public abstract void setWidth(int value);
    public abstract int getHeight();
    public abstract void setHeight(int value);
    public abstract byte[] getPixels();
    public abstract void setPixels(byte[] value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutMapClass extends Template.Class<PacketPlayOutMapHandle> {
        public final Template.Constructor.Converted<PacketPlayOutMapHandle> constr = new Template.Constructor.Converted<PacketPlayOutMapHandle>();

        public final Template.Field.Integer itemId = new Template.Field.Integer();
        public final Template.Field.Byte scale = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Boolean track = new Template.Field.Boolean();
        @Template.Optional
        public final Template.Field.Boolean locked = new Template.Field.Boolean();
        public final Template.Field.Converted<MapCursor[]> cursors = new Template.Field.Converted<MapCursor[]>();
        public final Template.Field.Integer xmin = new Template.Field.Integer();
        public final Template.Field.Integer ymin = new Template.Field.Integer();
        public final Template.Field.Integer width = new Template.Field.Integer();
        public final Template.Field.Integer height = new Template.Field.Integer();
        public final Template.Field<byte[]> pixels = new Template.Field<byte[]>();

        public final Template.Method<Boolean> isLocked = new Template.Method<Boolean>();
        public final Template.Method<Void> setLocked = new Template.Method<Void>();
        public final Template.Method<Boolean> isTrack = new Template.Method<Boolean>();
        public final Template.Method<Void> setTrack = new Template.Method<Void>();

    }

}

