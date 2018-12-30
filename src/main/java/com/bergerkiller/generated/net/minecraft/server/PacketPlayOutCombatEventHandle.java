package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutCombatEvent</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PacketPlayOutCombatEventHandle extends Template.Handle {
    /** @See {@link PacketPlayOutCombatEventClass} */
    public static final PacketPlayOutCombatEventClass T = new PacketPlayOutCombatEventClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutCombatEventHandle.class, "net.minecraft.server.PacketPlayOutCombatEvent", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static PacketPlayOutCombatEventHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Object getEventType();
    public abstract void setEventType(Object value);
    public abstract int getEntityId1();
    public abstract void setEntityId1(int value);
    public abstract int getEntityId2();
    public abstract void setEntityId2(int value);
    public abstract int getTickDuration();
    public abstract void setTickDuration(int value);
    public abstract ChatText getMessage();
    public abstract void setMessage(ChatText value);
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

