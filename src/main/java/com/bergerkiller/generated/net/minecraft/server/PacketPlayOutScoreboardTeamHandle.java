package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutScoreboardTeam</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PacketPlayOutScoreboardTeamHandle extends PacketHandle {
    /** @See {@link PacketPlayOutScoreboardTeamClass} */
    public static final PacketPlayOutScoreboardTeamClass T = new PacketPlayOutScoreboardTeamClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutScoreboardTeamHandle.class, "net.minecraft.server.PacketPlayOutScoreboardTeam");

    /* ============================================================================== */

    public static PacketPlayOutScoreboardTeamHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public void setCollisionRule(String rule) {
        if (T.collisionRule.isAvailable()) {
            T.collisionRule.set(getRaw(), rule);
        }
    }

    public String getCollisionRule() {
        if (T.collisionRule.isAvailable()) {
            return T.collisionRule.get(getRaw());
        } else {
            return "always";
        }
    }
    public abstract String getName();
    public abstract void setName(String value);
    public abstract String getDisplayName();
    public abstract void setDisplayName(String value);
    public abstract String getPrefix();
    public abstract void setPrefix(String value);
    public abstract String getSuffix();
    public abstract void setSuffix(String value);
    public abstract String getVisibility();
    public abstract void setVisibility(String value);
    public abstract int getChatFormat();
    public abstract void setChatFormat(int value);
    public abstract Collection<String> getPlayers();
    public abstract void setPlayers(Collection<String> value);
    public abstract int getMode();
    public abstract void setMode(int value);
    public abstract int getFriendlyFire();
    public abstract void setFriendlyFire(int value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutScoreboardTeam</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutScoreboardTeamClass extends Template.Class<PacketPlayOutScoreboardTeamHandle> {
        public final Template.Field<String> name = new Template.Field<String>();
        public final Template.Field<String> displayName = new Template.Field<String>();
        public final Template.Field<String> prefix = new Template.Field<String>();
        public final Template.Field<String> suffix = new Template.Field<String>();
        public final Template.Field<String> visibility = new Template.Field<String>();
        @Template.Optional
        public final Template.Field<String> collisionRule = new Template.Field<String>();
        public final Template.Field.Integer chatFormat = new Template.Field.Integer();
        public final Template.Field<Collection<String>> players = new Template.Field<Collection<String>>();
        public final Template.Field.Integer mode = new Template.Field.Integer();
        public final Template.Field.Integer friendlyFire = new Template.Field.Integer();

    }

}

