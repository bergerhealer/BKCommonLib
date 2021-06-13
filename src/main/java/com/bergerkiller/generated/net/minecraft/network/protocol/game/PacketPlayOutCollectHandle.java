package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutCollect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutCollect")
public abstract class PacketPlayOutCollectHandle extends PacketHandle {
    /** @See {@link PacketPlayOutCollectClass} */
    public static final PacketPlayOutCollectClass T = Template.Class.create(PacketPlayOutCollectClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutCollectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getCollectedItemId();
    public abstract void setCollectedItemId(int value);
    public abstract int getCollectorEntityId();
    public abstract void setCollectorEntityId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutCollect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutCollectClass extends Template.Class<PacketPlayOutCollectHandle> {
        public final Template.Field.Integer collectedItemId = new Template.Field.Integer();
        public final Template.Field.Integer collectorEntityId = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer unknown = new Template.Field.Integer();

    }

}

