package com.bergerkiller.generated.net.minecraft.world.level.saveddata.maps;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import org.bukkit.map.MapCursor.Type;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.saveddata.maps.MapDecorationType</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.saveddata.maps.MapDecorationType")
public abstract class MapDecorationTypeHandle extends Template.Handle {
    /** @see MapDecorationTypeClass */
    public static final MapDecorationTypeClass T = Template.Class.create(MapDecorationTypeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static MapDecorationTypeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static List<Holder<MapDecorationTypeHandle>> getValues() {
        return T.getValues.invoke();
    }

    public abstract MinecraftKeyHandle getName();
    public abstract boolean isShownOnItemFrame();
    public abstract Type toBukkit();
    public abstract byte getId();
    /**
     * Stores class members for <b>net.minecraft.world.level.saveddata.maps.MapDecorationType</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MapDecorationTypeClass extends Template.Class<MapDecorationTypeHandle> {
        public final Template.StaticMethod.Converted<List<Holder<MapDecorationTypeHandle>>> getValues = new Template.StaticMethod.Converted<List<Holder<MapDecorationTypeHandle>>>();

        public final Template.Method.Converted<MinecraftKeyHandle> getName = new Template.Method.Converted<MinecraftKeyHandle>();
        public final Template.Method<Boolean> isShownOnItemFrame = new Template.Method<Boolean>();
        public final Template.Method<Type> toBukkit = new Template.Method<Type>();
        public final Template.Method<Byte> getId = new Template.Method<Byte>();

    }

}

