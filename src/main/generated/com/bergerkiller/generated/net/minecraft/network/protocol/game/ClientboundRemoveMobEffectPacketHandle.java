package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket")
public abstract class ClientboundRemoveMobEffectPacketHandle extends PacketHandle {
    /** @see ClientboundRemoveMobEffectPacketClass */
    public static final ClientboundRemoveMobEffectPacketClass T = Template.Class.create(ClientboundRemoveMobEffectPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundRemoveMobEffectPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundRemoveMobEffectPacketHandle createNew(int entityId, Holder<MobEffectHandle> effect) {
        return T.createNew.invoke(entityId, effect);
    }

    public abstract int getEntityId();
    public abstract Holder<MobEffectHandle> getEffect();
    public org.bukkit.potion.PotionEffectType getPotionEffectType() {
        return MobEffectHandle.holderToBukkit(getEffect());
    }

    public static ClientboundRemoveMobEffectPacketHandle createNew(int entityId, org.bukkit.potion.PotionEffectType effectType) {
        return createNew(entityId, MobEffectHandle.holderFromBukkit(effectType));
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundRemoveMobEffectPacketClass extends Template.Class<ClientboundRemoveMobEffectPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundRemoveMobEffectPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundRemoveMobEffectPacketHandle>();

        public final Template.Method<Integer> getEntityId = new Template.Method<Integer>();
        public final Template.Method.Converted<Holder<MobEffectHandle>> getEffect = new Template.Method.Converted<Holder<MobEffectHandle>>();

    }

}

