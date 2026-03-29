package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.Difficulty;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket")
public abstract class ClientboundChangeDifficultyPacketHandle extends PacketHandle {
    /** @see ClientboundChangeDifficultyPacketClass */
    public static final ClientboundChangeDifficultyPacketClass T = Template.Class.create(ClientboundChangeDifficultyPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundChangeDifficultyPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Difficulty getDifficulty();
    public abstract void setDifficulty(Difficulty value);
    public abstract boolean isHardcore();
    public abstract void setHardcore(boolean value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundChangeDifficultyPacketClass extends Template.Class<ClientboundChangeDifficultyPacketHandle> {
        public final Template.Field.Converted<Difficulty> difficulty = new Template.Field.Converted<Difficulty>();
        public final Template.Field.Boolean hardcore = new Template.Field.Boolean();

    }

}

