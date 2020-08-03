package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutPlayerListHeaderFooter</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.PacketPlayOutPlayerListHeaderFooter")
public abstract class PacketPlayOutPlayerListHeaderFooterHandle extends PacketHandle {
    /** @See {@link PacketPlayOutPlayerListHeaderFooterClass} */
    public static final PacketPlayOutPlayerListHeaderFooterClass T = Template.Class.create(PacketPlayOutPlayerListHeaderFooterClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutPlayerListHeaderFooterHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract ChatText getHeader();
    public abstract void setHeader(ChatText value);
    public abstract ChatText getFooter();
    public abstract void setFooter(ChatText value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutPlayerListHeaderFooter</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutPlayerListHeaderFooterClass extends Template.Class<PacketPlayOutPlayerListHeaderFooterHandle> {
        public final Template.Field.Converted<ChatText> header = new Template.Field.Converted<ChatText>();
        public final Template.Field.Converted<ChatText> footer = new Template.Field.Converted<ChatText>();

    }

}

