package com.bergerkiller.generated.net.minecraft.world.level.saveddata.maps;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.map.MapMarker.Type;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import org.bukkit.map.MapCursor;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.saveddata.maps.MapDecoration</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.saveddata.maps.MapDecoration")
public abstract class MapDecorationHandle extends Template.Handle {
    /** @see MapDecorationClass */
    public static final MapDecorationClass T = Template.Class.create(MapDecorationClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static MapDecorationHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static MapDecorationHandle createNew(Type type, byte x, byte y, byte direction, ChatText title) {
        return T.createNew.invoke(type, x, y, direction, title);
    }

    public static MapDecorationHandle fromCursor(MapCursor cursor) {
        return T.fromCursor.invoke(cursor);
    }

    public abstract MapCursor toCursor();
    public abstract Type getType();
    public abstract byte getX();
    public abstract byte getY();
    public abstract byte getDirection();
    public static MapDecorationHandle createNew(Type type, byte x, byte y, byte direction) {
        return createNew(type, x, y, direction, null);
    }
    /**
     * Stores class members for <b>net.minecraft.world.level.saveddata.maps.MapDecoration</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MapDecorationClass extends Template.Class<MapDecorationHandle> {
        public final Template.StaticMethod.Converted<MapDecorationHandle> createNew = new Template.StaticMethod.Converted<MapDecorationHandle>();
        public final Template.StaticMethod.Converted<MapDecorationHandle> fromCursor = new Template.StaticMethod.Converted<MapDecorationHandle>();

        public final Template.Method<MapCursor> toCursor = new Template.Method<MapCursor>();
        public final Template.Method.Converted<Type> getType = new Template.Method.Converted<Type>();
        public final Template.Method<Byte> getX = new Template.Method<Byte>();
        public final Template.Method<Byte> getY = new Template.Method<Byte>();
        public final Template.Method<Byte> getDirection = new Template.Method<Byte>();

    }

}

