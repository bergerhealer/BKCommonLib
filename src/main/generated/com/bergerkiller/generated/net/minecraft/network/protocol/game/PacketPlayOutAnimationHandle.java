package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutAnimation</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutAnimation")
public abstract class PacketPlayOutAnimationHandle extends PacketHandle {
    /** @see PacketPlayOutAnimationClass */
    public static final PacketPlayOutAnimationClass T = Template.Class.create(PacketPlayOutAnimationClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutAnimationHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract int getAction();
    public abstract void setAction(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutAnimation</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutAnimationClass extends Template.Class<PacketPlayOutAnimationHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Integer action = new Template.Field.Integer();

    }

}

