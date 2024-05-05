package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.ChatColor;
import java.util.Collection;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam")
public abstract class PacketPlayOutScoreboardTeamHandle extends PacketHandle {
    /** @see PacketPlayOutScoreboardTeamClass */
    public static final PacketPlayOutScoreboardTeamClass T = Template.Class.create(PacketPlayOutScoreboardTeamClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutScoreboardTeamHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutScoreboardTeamHandle createNew() {
        return T.createNew.invoke();
    }

    public abstract ChatText getDisplayName();
    public abstract void setDisplayName(ChatText displayName);
    public abstract ChatText getPrefix();
    public abstract void setPrefix(ChatText prefix);
    public abstract ChatText getSuffix();
    public abstract void setSuffix(ChatText suffix);
    public abstract String getVisibility();
    public abstract void setVisibility(String visibility);
    public abstract String getCollisionRule();
    public abstract void setCollisionRule(String rule);
    public abstract ChatColor getColor();
    public abstract void setColor(ChatColor color);
    public abstract int getTeamOptionFlags();
    public abstract void setTeamOptionFlags(int teamOptionFlags);
    public static final int METHOD_ADD = 0;
    public static final int METHOD_REMOVE = 1;
    public static final int METHOD_CHANGE = 2;
    public static final int METHOD_JOIN = 3;
    public static final int METHOD_LEAVE = 4;
    public abstract int getMethod();
    public abstract void setMethod(int value);
    public abstract String getName();
    public abstract void setName(String value);
    public abstract Collection<String> getPlayers();
    public abstract void setPlayers(Collection<String> value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutScoreboardTeamClass extends Template.Class<PacketPlayOutScoreboardTeamHandle> {
        public final Template.Field.Integer method = new Template.Field.Integer();
        public final Template.Field<String> name = new Template.Field<String>();
        public final Template.Field<Collection<String>> players = new Template.Field<Collection<String>>();

        public final Template.StaticMethod.Converted<PacketPlayOutScoreboardTeamHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutScoreboardTeamHandle>();

        public final Template.Method.Converted<ChatText> getDisplayName = new Template.Method.Converted<ChatText>();
        public final Template.Method.Converted<Void> setDisplayName = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<ChatText> getPrefix = new Template.Method.Converted<ChatText>();
        public final Template.Method.Converted<Void> setPrefix = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<ChatText> getSuffix = new Template.Method.Converted<ChatText>();
        public final Template.Method.Converted<Void> setSuffix = new Template.Method.Converted<Void>();
        public final Template.Method<String> getVisibility = new Template.Method<String>();
        public final Template.Method<Void> setVisibility = new Template.Method<Void>();
        public final Template.Method<String> getCollisionRule = new Template.Method<String>();
        public final Template.Method<Void> setCollisionRule = new Template.Method<Void>();
        public final Template.Method.Converted<ChatColor> getColor = new Template.Method.Converted<ChatColor>();
        public final Template.Method.Converted<Void> setColor = new Template.Method.Converted<Void>();
        public final Template.Method<Integer> getTeamOptionFlags = new Template.Method<Integer>();
        public final Template.Method<Void> setTeamOptionFlags = new Template.Method<Void>();

    }

}

