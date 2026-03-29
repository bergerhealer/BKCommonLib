package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket")
public abstract class ClientboundBlockUpdatePacketHandle extends PacketHandle {
    /** @see ClientboundBlockUpdatePacketClass */
    public static final ClientboundBlockUpdatePacketClass T = Template.Class.create(ClientboundBlockUpdatePacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundBlockUpdatePacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundBlockUpdatePacketHandle createNewNull() {
        return T.createNewNull.invoke();
    }

    public static ClientboundBlockUpdatePacketHandle createNew(IntVector3 position, BlockData blockData) {
        return T.createNew.invoke(position, blockData);
    }

    public abstract IntVector3 getPosition();
    public abstract void setPosition(IntVector3 value);
    public abstract BlockData getBlockData();
    public abstract void setBlockData(BlockData value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundBlockUpdatePacketClass extends Template.Class<ClientboundBlockUpdatePacketHandle> {
        public final Template.Field.Converted<IntVector3> position = new Template.Field.Converted<IntVector3>();
        public final Template.Field.Converted<BlockData> blockData = new Template.Field.Converted<BlockData>();

        public final Template.StaticMethod.Converted<ClientboundBlockUpdatePacketHandle> createNewNull = new Template.StaticMethod.Converted<ClientboundBlockUpdatePacketHandle>();
        public final Template.StaticMethod.Converted<ClientboundBlockUpdatePacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundBlockUpdatePacketHandle>();

    }

}

