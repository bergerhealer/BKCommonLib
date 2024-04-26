package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectListHandle;
import org.bukkit.potion.PotionEffect;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutEntityEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutEntityEffect")
public abstract class PacketPlayOutEntityEffectHandle extends PacketHandle {
    /** @see PacketPlayOutEntityEffectClass */
    public static final PacketPlayOutEntityEffectClass T = Template.Class.create(PacketPlayOutEntityEffectClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutEntityEffectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutEntityEffectHandle createNew(int entityId, PotionEffect effect, boolean blend) {
        return T.createNew.invoke(entityId, effect, blend);
    }

    public abstract int getEffectAmplifier();
    public abstract void setEffectAmplifier(int amplifier);
    public abstract Holder<MobEffectListHandle> getEffect();
    public abstract void setEffect(Holder<MobEffectListHandle> effect);

    public static final int FLAG_AMBIENT = 1;
    public static final int FLAG_VISIBLE = 2;
    public static final int FLAG_SHOW_ICON = 4;


    public org.bukkit.potion.PotionEffectType getPotionEffectType() {
        return MobEffectListHandle.holderToBukkit(getEffect());
    }

    public void setPotionEffectType(org.bukkit.potion.PotionEffectType effectType) {
        setEffect(MobEffectListHandle.holderFromBukkit(effectType));
    }

    public static PacketPlayOutEntityEffectHandle createNew(int entityId, org.bukkit.potion.PotionEffect effect) {
        return createNew(entityId, effect, false);
    }
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract int getEffectDurationTicks();
    public abstract void setEffectDurationTicks(int value);
    public abstract byte getFlags();
    public abstract void setFlags(byte value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutEntityEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutEntityEffectClass extends Template.Class<PacketPlayOutEntityEffectHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Integer effectDurationTicks = new Template.Field.Integer();
        public final Template.Field.Byte flags = new Template.Field.Byte();

        public final Template.StaticMethod.Converted<PacketPlayOutEntityEffectHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutEntityEffectHandle>();

        public final Template.Method<Integer> getEffectAmplifier = new Template.Method<Integer>();
        public final Template.Method<Void> setEffectAmplifier = new Template.Method<Void>();
        public final Template.Method.Converted<Holder<MobEffectListHandle>> getEffect = new Template.Method.Converted<Holder<MobEffectListHandle>>();
        public final Template.Method.Converted<Void> setEffect = new Template.Method.Converted<Void>();

    }

}

