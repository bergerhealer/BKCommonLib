package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.block.SignSide;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundSignUpdatePacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundSignUpdatePacket")
public abstract class ServerboundSignUpdatePacketHandle extends PacketHandle {
    /** @see ServerboundSignUpdatePacketClass */
    public static final ServerboundSignUpdatePacketClass T = Template.Class.create(ServerboundSignUpdatePacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundSignUpdatePacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ServerboundSignUpdatePacketHandle createNew(IntVector3 blockPos, List<ChatText> lines, SignSide side) {
        return T.createNew.invoke(blockPos, lines, side);
    }

    public abstract IntVector3 getPosition();
    public abstract List<ChatText> getLines();
    public abstract SignSide getSide();
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundSignUpdatePacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundSignUpdatePacketClass extends Template.Class<ServerboundSignUpdatePacketHandle> {
        public final Template.StaticMethod.Converted<ServerboundSignUpdatePacketHandle> createNew = new Template.StaticMethod.Converted<ServerboundSignUpdatePacketHandle>();

        public final Template.Method.Converted<IntVector3> getPosition = new Template.Method.Converted<IntVector3>();
        public final Template.Method.Converted<List<ChatText>> getLines = new Template.Method.Converted<List<ChatText>>();
        public final Template.Method.Converted<SignSide> getSide = new Template.Method.Converted<SignSide>();

    }

}

