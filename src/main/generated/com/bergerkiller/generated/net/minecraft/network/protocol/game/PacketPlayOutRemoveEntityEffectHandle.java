package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectListHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutRemoveEntityEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutRemoveEntityEffect")
public abstract class PacketPlayOutRemoveEntityEffectHandle extends PacketHandle {
    /** @see PacketPlayOutRemoveEntityEffectClass */
    public static final PacketPlayOutRemoveEntityEffectClass T = Template.Class.create(PacketPlayOutRemoveEntityEffectClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutRemoveEntityEffectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutRemoveEntityEffectHandle createNew(int entityId, Holder<MobEffectListHandle> effect) {
        return T.createNew.invoke(entityId, effect);
    }

    public abstract int getEntityId();
    public abstract Holder<MobEffectListHandle> getEffect();
    public org.bukkit.potion.PotionEffectType getPotionEffectType() {
        return MobEffectListHandle.holderToBukkit(getEffect());
    }

    public static PacketPlayOutRemoveEntityEffectHandle createNew(int entityId, org.bukkit.potion.PotionEffectType effectType) {
        return createNew(entityId, MobEffectListHandle.holderFromBukkit(effectType));
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutRemoveEntityEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutRemoveEntityEffectClass extends Template.Class<PacketPlayOutRemoveEntityEffectHandle> {
        public final Template.StaticMethod.Converted<PacketPlayOutRemoveEntityEffectHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutRemoveEntityEffectHandle>();

        public final Template.Method<Integer> getEntityId = new Template.Method<Integer>();
        public final Template.Method.Converted<Holder<MobEffectListHandle>> getEffect = new Template.Method.Converted<Holder<MobEffectListHandle>>();

    }

}

