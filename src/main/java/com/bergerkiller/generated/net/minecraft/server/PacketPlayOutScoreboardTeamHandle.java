package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutScoreboardTeam</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutScoreboardTeamHandle extends PacketHandle {
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
    public String getName() {
        return T.name.get(getRaw());
    }

    public void setName(String value) {
        T.name.set(getRaw(), value);
    }

    public String getDisplayName() {
        return T.displayName.get(getRaw());
    }

    public void setDisplayName(String value) {
        T.displayName.set(getRaw(), value);
    }

    public String getPrefix() {
        return T.prefix.get(getRaw());
    }

    public void setPrefix(String value) {
        T.prefix.set(getRaw(), value);
    }

    public String getSuffix() {
        return T.suffix.get(getRaw());
    }

    public void setSuffix(String value) {
        T.suffix.set(getRaw(), value);
    }

    public String getVisibility() {
        return T.visibility.get(getRaw());
    }

    public void setVisibility(String value) {
        T.visibility.set(getRaw(), value);
    }

    public int getChatFormat() {
        return T.chatFormat.getInteger(getRaw());
    }

    public void setChatFormat(int value) {
        T.chatFormat.setInteger(getRaw(), value);
    }

    public Collection<String> getPlayers() {
        return T.players.get(getRaw());
    }

    public void setPlayers(Collection<String> value) {
        T.players.set(getRaw(), value);
    }

    public int getMode() {
        return T.mode.getInteger(getRaw());
    }

    public void setMode(int value) {
        T.mode.setInteger(getRaw(), value);
    }

    public int getFriendlyFire() {
        return T.friendlyFire.getInteger(getRaw());
    }

    public void setFriendlyFire(int value) {
        T.friendlyFire.setInteger(getRaw(), value);
    }

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

