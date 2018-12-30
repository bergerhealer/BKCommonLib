package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutScoreboardObjective</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PacketPlayOutScoreboardObjectiveHandle extends Template.Handle {
    /** @See {@link PacketPlayOutScoreboardObjectiveClass} */
    public static final PacketPlayOutScoreboardObjectiveClass T = new PacketPlayOutScoreboardObjectiveClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutScoreboardObjectiveHandle.class, "net.minecraft.server.PacketPlayOutScoreboardObjective", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static PacketPlayOutScoreboardObjectiveHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract String getName();
    public abstract void setName(String value);
    public abstract ChatText getDisplayName();
    public abstract void setDisplayName(ChatText value);
    public abstract Object getCriteria();
    public abstract void setCriteria(Object value);
    public abstract int getAction();
    public abstract void setAction(int value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutScoreboardObjective</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutScoreboardObjectiveClass extends Template.Class<PacketPlayOutScoreboardObjectiveHandle> {
        public final Template.Field<String> name = new Template.Field<String>();
        public final Template.Field.Converted<ChatText> displayName = new Template.Field.Converted<ChatText>();
        public final Template.Field.Converted<Object> criteria = new Template.Field.Converted<Object>();
        public final Template.Field.Integer action = new Template.Field.Integer();

    }

}

