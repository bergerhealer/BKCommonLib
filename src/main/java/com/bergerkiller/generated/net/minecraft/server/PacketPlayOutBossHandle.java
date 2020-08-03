package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutBoss</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.server.PacketPlayOutBoss")
public abstract class PacketPlayOutBossHandle extends PacketHandle {
    /** @See {@link PacketPlayOutBossClass} */
    public static final PacketPlayOutBossClass T = Template.Class.create(PacketPlayOutBossClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutBossHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract UUID getEntityUUID();
    public abstract void setEntityUUID(UUID value);
    public abstract Object getAction();
    public abstract void setAction(Object value);
    public abstract Object getChat();
    public abstract void setChat(Object value);
    public abstract float getProgress();
    public abstract void setProgress(float value);
    public abstract Object getBossBarColor();
    public abstract void setBossBarColor(Object value);
    public abstract Object getBossBarStyle();
    public abstract void setBossBarStyle(Object value);
    public abstract boolean isUnknown1();
    public abstract void setUnknown1(boolean value);
    public abstract boolean isUnknown2();
    public abstract void setUnknown2(boolean value);
    public abstract boolean isUnknown3();
    public abstract void setUnknown3(boolean value);
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

