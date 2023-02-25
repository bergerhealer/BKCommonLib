package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutAdvancements</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutAdvancements")
public abstract class PacketPlayOutAdvancementsHandle extends PacketHandle {
    /** @see PacketPlayOutAdvancementsClass */
    public static final PacketPlayOutAdvancementsClass T = Template.Class.create(PacketPlayOutAdvancementsClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutAdvancementsHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean isInitial();
    public abstract void setInitial(boolean value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutAdvancements</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutAdvancementsClass extends Template.Class<PacketPlayOutAdvancementsHandle> {
        public final Template.Field.Boolean initial = new Template.Field.Boolean();

    }

}

