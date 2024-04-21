package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutUpdateTime</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutUpdateTime")
public abstract class PacketPlayOutUpdateTimeHandle extends PacketHandle {
    /** @see PacketPlayOutUpdateTimeClass */
    public static final PacketPlayOutUpdateTimeClass T = Template.Class.create(PacketPlayOutUpdateTimeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutUpdateTimeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract long getGameTime();
    public abstract void setGameTime(long value);
    public abstract long getDayTime();
    public abstract void setDayTime(long value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutUpdateTime</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutUpdateTimeClass extends Template.Class<PacketPlayOutUpdateTimeHandle> {
        public final Template.Field.Long gameTime = new Template.Field.Long();
        public final Template.Field.Long dayTime = new Template.Field.Long();

    }

}

