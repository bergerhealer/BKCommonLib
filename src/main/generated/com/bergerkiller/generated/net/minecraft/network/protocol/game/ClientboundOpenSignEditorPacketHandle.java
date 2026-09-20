package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.block.SignSide;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket")
public abstract class ClientboundOpenSignEditorPacketHandle extends PacketHandle {
    /** @see ClientboundOpenSignEditorPacketClass */
    public static final ClientboundOpenSignEditorPacketClass T = Template.Class.create(ClientboundOpenSignEditorPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundOpenSignEditorPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundOpenSignEditorPacketHandle createNew(IntVector3 signPosition, SignSide side) {
        return T.createNew.invoke(signPosition, side);
    }

    public abstract IntVector3 getSignPosition();
    public abstract SignSide getSide();
    public boolean isFrontText() {
        return getSide().isFront();
    }

    public static ClientboundOpenSignEditorPacketHandle createNew(IntVector3 signPosition) {
        return createNew(signPosition, SignSide.FRONT);
    }

    public static ClientboundOpenSignEditorPacketHandle createNew(IntVector3 signPosition, boolean isFrontText) {
        return createNew(signPosition, SignSide.byFront(isFrontText));
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundOpenSignEditorPacketClass extends Template.Class<ClientboundOpenSignEditorPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundOpenSignEditorPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundOpenSignEditorPacketHandle>();

        public final Template.Method.Converted<IntVector3> getSignPosition = new Template.Method.Converted<IntVector3>();
        public final Template.Method.Converted<SignSide> getSide = new Template.Method.Converted<SignSide>();

    }

}

