package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutBoss</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public class PacketPlayOutBossHandle extends PacketHandle {
    /** @See {@link PacketPlayOutBossClass} */
    public static final PacketPlayOutBossClass T = new PacketPlayOutBossClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutBossHandle.class, "net.minecraft.server.PacketPlayOutBoss");

    /* ============================================================================== */

    public static PacketPlayOutBossHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public UUID getEntityUUID() {
        return T.entityUUID.get(getRaw());
    }

    public void setEntityUUID(UUID value) {
        T.entityUUID.set(getRaw(), value);
    }

    public Object getAction() {
        return T.action.get(getRaw());
    }

    public void setAction(Object value) {
        T.action.set(getRaw(), value);
    }

    public Object getChat() {
        return T.chat.get(getRaw());
    }

    public void setChat(Object value) {
        T.chat.set(getRaw(), value);
    }

    public float getProgress() {
        return T.progress.getFloat(getRaw());
    }

    public void setProgress(float value) {
        T.progress.setFloat(getRaw(), value);
    }

    public Object getBossBarColor() {
        return T.bossBarColor.get(getRaw());
    }

    public void setBossBarColor(Object value) {
        T.bossBarColor.set(getRaw(), value);
    }

    public Object getBossBarStyle() {
        return T.bossBarStyle.get(getRaw());
    }

    public void setBossBarStyle(Object value) {
        T.bossBarStyle.set(getRaw(), value);
    }

    public boolean isUnknown1() {
        return T.unknown1.getBoolean(getRaw());
    }

    public void setUnknown1(boolean value) {
        T.unknown1.setBoolean(getRaw(), value);
    }

    public boolean isUnknown2() {
        return T.unknown2.getBoolean(getRaw());
    }

    public void setUnknown2(boolean value) {
        T.unknown2.setBoolean(getRaw(), value);
    }

    public boolean isUnknown3() {
        return T.unknown3.getBoolean(getRaw());
    }

    public void setUnknown3(boolean value) {
        T.unknown3.setBoolean(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutBoss</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutBossClass extends Template.Class<PacketPlayOutBossHandle> {
        public final Template.Field<UUID> entityUUID = new Template.Field<UUID>();
        public final Template.Field.Converted<Object> action = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<Object> chat = new Template.Field.Converted<Object>();
        public final Template.Field.Float progress = new Template.Field.Float();
        public final Template.Field.Converted<Object> bossBarColor = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<Object> bossBarStyle = new Template.Field.Converted<Object>();
        public final Template.Field.Boolean unknown1 = new Template.Field.Boolean();
        public final Template.Field.Boolean unknown2 = new Template.Field.Boolean();
        public final Template.Field.Boolean unknown3 = new Template.Field.Boolean();

    }

}

