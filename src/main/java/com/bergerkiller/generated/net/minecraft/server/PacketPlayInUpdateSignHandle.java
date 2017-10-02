package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.ChatText;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInUpdateSign</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayInUpdateSignHandle extends PacketHandle {
    /** @See {@link PacketPlayInUpdateSignClass} */
    public static final PacketPlayInUpdateSignClass T = new PacketPlayInUpdateSignClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInUpdateSignHandle.class, "net.minecraft.server.PacketPlayInUpdateSign");

    /* ============================================================================== */

    public static PacketPlayInUpdateSignHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

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
     * Stores class members for <b>net.minecraft.server.PacketPlayInUpdateSign</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInUpdateSignClass extends Template.Class<PacketPlayInUpdateSignHandle> {
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted<IntVector3>();
        public final Template.Field.Converted<ChatText[]> lines = new Template.Field.Converted<ChatText[]>();

    }

}

