package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.map.MapCursor;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutMap")
public abstract class PacketPlayOutMapHandle extends PacketHandle {
    /** @See {@link PacketPlayOutMapClass} */
    public static final PacketPlayOutMapClass T = Template.Class.create(PacketPlayOutMapClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutMapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutMapHandle createNew() {
        return T.createNew.invoke();
    }

    public abstract int getStartX();
    public abstract void setStartX(int startx);
    public abstract int getStartY();
    public abstract void setStartY(int starty);
    public abstract int getWidth();
    public abstract void setWidth(int width);
    public abstract int getHeight();
    public abstract void setHeight(int height);
    public abstract byte[] getPixels();
    public abstract void setPixels(byte[] pixels);
    public abstract void setPixelData(int startX, int startY, int width, int height, byte[] pixels);
    public abstract boolean isLocked();
    public abstract void setLocked(boolean locked);
    public abstract boolean isTrack();
    public abstract void setTrack(boolean track);
    public abstract int getMapId();
    public abstract void setMapId(int value);
    public abstract byte getScale();
    public abstract void setScale(byte value);
    public abstract List<MapCursor> getCursors();
    public abstract void setCursors(List<MapCursor> value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutMapClass extends Template.Class<PacketPlayOutMapHandle> {
        public final Template.Field.Integer mapId = new Template.Field.Integer();
        public final Template.Field.Byte scale = new Template.Field.Byte();
        public final Template.Field.Converted<List<MapCursor>> cursors = new Template.Field.Converted<List<MapCursor>>();

        public final Template.StaticMethod.Converted<PacketPlayOutMapHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutMapHandle>();

        public final Template.Method<Integer> getStartX = new Template.Method<Integer>();
        public final Template.Method<Void> setStartX = new Template.Method<Void>();
        public final Template.Method<Integer> getStartY = new Template.Method<Integer>();
        public final Template.Method<Void> setStartY = new Template.Method<Void>();
        public final Template.Method<Integer> getWidth = new Template.Method<Integer>();
        public final Template.Method<Void> setWidth = new Template.Method<Void>();
        public final Template.Method<Integer> getHeight = new Template.Method<Integer>();
        public final Template.Method<Void> setHeight = new Template.Method<Void>();
        public final Template.Method<byte[]> getPixels = new Template.Method<byte[]>();
        public final Template.Method<Void> setPixels = new Template.Method<Void>();
        public final Template.Method<Void> setPixelData = new Template.Method<Void>();
        public final Template.Method<Boolean> isLocked = new Template.Method<Boolean>();
        public final Template.Method<Void> setLocked = new Template.Method<Void>();
        public final Template.Method<Boolean> isTrack = new Template.Method<Boolean>();
        public final Template.Method<Void> setTrack = new Template.Method<Void>();

    }

}

