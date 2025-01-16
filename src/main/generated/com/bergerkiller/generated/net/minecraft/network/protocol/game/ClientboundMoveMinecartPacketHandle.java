package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.NewMinecartBehaviorHandle.LerpStepHandle;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundMoveMinecartPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundMoveMinecartPacket")
public abstract class ClientboundMoveMinecartPacketHandle extends PacketHandle {
    /** @see ClientboundMoveMinecartPacketClass */
    public static final ClientboundMoveMinecartPacketClass T = Template.Class.create(ClientboundMoveMinecartPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundMoveMinecartPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundMoveMinecartPacketHandle createNew(int entityId, List<LerpStepHandle> lerpSteps) {
        return T.createNew.invoke(entityId, lerpSteps);
    }

    public abstract int getEntityId();
    public abstract List<LerpStepHandle> getLerpSteps();
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundMoveMinecartPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundMoveMinecartPacketClass extends Template.Class<ClientboundMoveMinecartPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundMoveMinecartPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundMoveMinecartPacketHandle>();

        public final Template.Method<Integer> getEntityId = new Template.Method<Integer>();
        public final Template.Method.Converted<List<LerpStepHandle>> getLerpSteps = new Template.Method.Converted<List<LerpStepHandle>>();

    }

}

