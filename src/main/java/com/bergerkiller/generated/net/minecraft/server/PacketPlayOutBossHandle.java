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
        if (handleInstance == null) return null;
        PacketPlayOutBossHandle handle = new PacketPlayOutBossHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public UUID getEntityUUID() {
        return T.entityUUID.get(instance);
    }

    public void setEntityUUID(UUID value) {
        T.entityUUID.set(instance, value);
    }

    public Object getAction() {
        return T.action.get(instance);
    }

    public void setAction(Object value) {
        T.action.set(instance, value);
    }

    public Object getChat() {
        return T.chat.get(instance);
    }

    public void setChat(Object value) {
        T.chat.set(instance, value);
    }

    public float getProgress() {
        return T.progress.getFloat(instance);
    }

    public void setProgress(float value) {
        T.progress.setFloat(instance, value);
    }

    public Object getBossBarColor() {
        return T.bossBarColor.get(instance);
    }

    public void setBossBarColor(Object value) {
        T.bossBarColor.set(instance, value);
    }

    public Object getBossBarStyle() {
        return T.bossBarStyle.get(instance);
    }

    public void setBossBarStyle(Object value) {
        T.bossBarStyle.set(instance, value);
    }

    public boolean isUnknown1() {
        return T.unknown1.getBoolean(instance);
    }

    public void setUnknown1(boolean value) {
        T.unknown1.setBoolean(instance, value);
    }

    public boolean isUnknown2() {
        return T.unknown2.getBoolean(instance);
    }

    public void setUnknown2(boolean value) {
        T.unknown2.setBoolean(instance, value);
    }

    public boolean isUnknown3() {
        return T.unknown3.getBoolean(instance);
    }

    public void setUnknown3(boolean value) {
        T.unknown3.setBoolean(instance, value);
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

