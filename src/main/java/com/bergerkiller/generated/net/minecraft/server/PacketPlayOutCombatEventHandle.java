package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutCombatEvent</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutCombatEventHandle extends Template.Handle {
    /** @See {@link PacketPlayOutCombatEventClass} */
    public static final PacketPlayOutCombatEventClass T = new PacketPlayOutCombatEventClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutCombatEventHandle.class, "net.minecraft.server.PacketPlayOutCombatEvent");

    /* ============================================================================== */

    public static PacketPlayOutCombatEventHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public Object getEventType() {
        return T.eventType.get(getRaw());
    }

    public void setEventType(Object value) {
        T.eventType.set(getRaw(), value);
    }

    public int getEntityId1() {
        return T.entityId1.getInteger(getRaw());
    }

    public void setEntityId1(int value) {
        T.entityId1.setInteger(getRaw(), value);
    }

    public int getEntityId2() {
        return T.entityId2.getInteger(getRaw());
    }

    public void setEntityId2(int value) {
        T.entityId2.setInteger(getRaw(), value);
    }

    public int getTickDuration() {
        return T.tickDuration.getInteger(getRaw());
    }

    public void setTickDuration(int value) {
        T.tickDuration.setInteger(getRaw(), value);
    }

    public ChatText getMessage() {
        return T.message.get(getRaw());
    }

    public void setMessage(ChatText value) {
        T.message.set(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutCombatEvent</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutCombatEventClass extends Template.Class<PacketPlayOutCombatEventHandle> {
        public final Template.Field.Converted<Object> eventType = new Template.Field.Converted<Object>();
        public final Template.Field.Integer entityId1 = new Template.Field.Integer();
        public final Template.Field.Integer entityId2 = new Template.Field.Integer();
        public final Template.Field.Integer tickDuration = new Template.Field.Integer();
        public final Template.Field.Converted<ChatText> message = new Template.Field.Converted<ChatText>();

    }

}

