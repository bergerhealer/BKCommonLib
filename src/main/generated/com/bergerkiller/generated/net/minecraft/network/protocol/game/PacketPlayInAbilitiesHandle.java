package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInAbilities</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInAbilities")
public abstract class PacketPlayInAbilitiesHandle extends PacketHandle {
    /** @see PacketPlayInAbilitiesClass */
    public static final PacketPlayInAbilitiesClass T = Template.Class.create(PacketPlayInAbilitiesClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInAbilitiesHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean isFlying();
    public abstract void setIsFlying(boolean value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInAbilities</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInAbilitiesClass extends Template.Class<PacketPlayInAbilitiesHandle> {
        public final Template.Field.Boolean isFlying = new Template.Field.Boolean();

    }

}

