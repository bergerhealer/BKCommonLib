package com.bergerkiller.generated.net.minecraft.server;

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
@Template.InstanceType("net.minecraft.server.PacketPlayOutUpdateSign")
public abstract class PacketPlayOutUpdateSignHandle extends Template.Handle {
    /** @See {@link PacketPlayOutUpdateSignClass} */
    public static final PacketPlayOutUpdateSignClass T = Template.Class.create(PacketPlayOutUpdateSignClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutUpdateSignHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract World getWorld();
    public abstract void setWorld(World value);
    public abstract IntVector3 getPosition();
    public abstract void setPosition(IntVector3 value);
    public abstract ChatText[] getLines();
    public abstract void setLines(ChatText[] value);
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

