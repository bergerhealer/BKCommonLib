package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundSetExperiencePacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundSetExperiencePacket")
public abstract class ClientboundSetExperiencePacketHandle extends PacketHandle {
    /** @see ClientboundSetExperiencePacketClass */
    public static final ClientboundSetExperiencePacketClass T = Template.Class.create(ClientboundSetExperiencePacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundSetExperiencePacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract float getExperienceProgress();
    public abstract void setExperienceProgress(float value);
    public abstract int getTotalExperience();
    public abstract void setTotalExperience(int value);
    public abstract int getExperienceLevel();
    public abstract void setExperienceLevel(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundSetExperiencePacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundSetExperiencePacketClass extends Template.Class<ClientboundSetExperiencePacketHandle> {
        public final Template.Field.Float experienceProgress = new Template.Field.Float();
        public final Template.Field.Integer totalExperience = new Template.Field.Integer();
        public final Template.Field.Integer experienceLevel = new Template.Field.Integer();

    }

}

