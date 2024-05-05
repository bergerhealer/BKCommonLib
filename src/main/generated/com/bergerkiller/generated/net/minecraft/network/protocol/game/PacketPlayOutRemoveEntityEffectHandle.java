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

    public static PacketPlayOutRemoveEntityEffectHandle createNew() {
        return T.createNew.invoke();
    }

    public org.bukkit.potion.PotionEffectType getPotionEffectType() {
        return MobEffectListHandle.holderToBukkit(getEffect());
    }

    public void setPotionEffectType(org.bukkit.potion.PotionEffectType effectType) {
        setEffect(MobEffectListHandle.holderFromBukkit(effectType));
    }

    public static PacketPlayOutRemoveEntityEffectHandle createNew(int entityId, org.bukkit.potion.PotionEffectType effectType) {
        PacketPlayOutRemoveEntityEffectHandle handle = createNew();
        handle.setEntityId(entityId);
        handle.setPotionEffectType(effectType);
        return handle;
    }

    public static PacketPlayOutRemoveEntityEffectHandle createNew(int entityId, Holder<MobEffectListHandle> effect) {
        PacketPlayOutRemoveEntityEffectHandle handle = createNew();
        handle.setEntityId(entityId);
        handle.setEffect(effect);
        return handle;
    }
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract Holder<MobEffectListHandle> getEffect();
    public abstract void setEffect(Holder<MobEffectListHandle> value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutRemoveEntityEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutRemoveEntityEffectClass extends Template.Class<PacketPlayOutRemoveEntityEffectHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Converted<Holder<MobEffectListHandle>> effect = new Template.Field.Converted<Holder<MobEffectListHandle>>();

        public final Template.StaticMethod.Converted<PacketPlayOutRemoveEntityEffectHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutRemoveEntityEffectHandle>();

    }

}

