package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import org.bukkit.World;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutUpdateSign</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public class PacketPlayOutUpdateSignHandle extends Template.Handle {
    /** @See {@link PacketPlayOutUpdateSignClass} */
    public static final PacketPlayOutUpdateSignClass T = new PacketPlayOutUpdateSignClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutUpdateSignHandle.class, "net.minecraft.server.PacketPlayOutUpdateSign");

    /* ============================================================================== */

    public static PacketPlayOutUpdateSignHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public World getWorld() {
        return T.world.get(getRaw());
    }

    public void setWorld(World value) {
        T.world.set(getRaw(), value);
    }

    public IntVector3 getPosition() {
        return T.position.get(getRaw());
    }

    public void setPosition(IntVector3 value) {
        T.position.set(getRaw(), value);
    }

    public ChatText[] getLines() {
        return T.lines.get(getRaw());
    }

    public void setLines(ChatText[] value) {
        T.lines.set(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutUpdateSign</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutUpdateSignClass extends Template.Class<PacketPlayOutUpdateSignHandle> {
        public final Template.Field.Converted<World> world = new Template.Field.Converted<World>();
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted<IntVector3>();
        public final Template.Field.Converted<ChatText[]> lines = new Template.Field.Converted<ChatText[]>();

    }

}

