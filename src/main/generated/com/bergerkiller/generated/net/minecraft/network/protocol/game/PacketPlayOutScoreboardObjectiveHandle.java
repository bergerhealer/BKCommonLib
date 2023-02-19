package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective")
public abstract class PacketPlayOutScoreboardObjectiveHandle extends PacketHandle {
    /** @See {@link PacketPlayOutScoreboardObjectiveClass} */
    public static final PacketPlayOutScoreboardObjectiveClass T = Template.Class.create(PacketPlayOutScoreboardObjectiveClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
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
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutScoreboardObjectiveClass extends Template.Class<PacketPlayOutScoreboardObjectiveHandle> {
        public final Template.Field<String> name = new Template.Field<String>();
        public final Template.Field.Converted<ChatText> displayName = new Template.Field.Converted<ChatText>();
        public final Template.Field.Converted<Object> criteria = new Template.Field.Converted<Object>();
        public final Template.Field.Integer action = new Template.Field.Integer();

    }

}

