package com.bergerkiller.generated.net.minecraft.world.effect;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import org.bukkit.potion.PotionEffect;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.effect.MobEffectInstance</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.effect.MobEffectInstance")
public abstract class MobEffectInstanceHandle extends Template.Handle {
    /** @see MobEffectInstanceClass */
    public static final MobEffectInstanceClass T = Template.Class.create(MobEffectInstanceClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static MobEffectInstanceHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static MobEffectInstanceHandle fromNBT(CommonTagCompound compound) {
        return T.fromNBT.invoke(compound);
    }

    public static MobEffectInstanceHandle fromBukkit(PotionEffect effect) {
        return T.fromBukkit.invoke(effect);
    }

    public abstract PotionEffect toBukkit();
    public abstract Holder<MobEffectHandle> getEffectList();
    public abstract void setEffectList(Holder<MobEffectHandle> value);
    public abstract int getDuration();
    public abstract void setDuration(int value);
    public abstract int getAmplifier();
    public abstract void setAmplifier(int value);
    public abstract boolean isAmbient();
    public abstract void setAmbient(boolean value);
    public abstract boolean isParticles();
    public abstract void setParticles(boolean value);
    /**
     * Stores class members for <b>net.minecraft.world.effect.MobEffectInstance</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MobEffectInstanceClass extends Template.Class<MobEffectInstanceHandle> {
        public final Template.Field.Converted<Holder<MobEffectHandle>> effectList = new Template.Field.Converted<Holder<MobEffectHandle>>();
        public final Template.Field.Integer duration = new Template.Field.Integer();
        public final Template.Field.Integer amplifier = new Template.Field.Integer();
        public final Template.Field.Boolean ambient = new Template.Field.Boolean();
        public final Template.Field.Boolean particles = new Template.Field.Boolean();

        public final Template.StaticMethod.Converted<MobEffectInstanceHandle> fromNBT = new Template.StaticMethod.Converted<MobEffectInstanceHandle>();
        public final Template.StaticMethod.Converted<MobEffectInstanceHandle> fromBukkit = new Template.StaticMethod.Converted<MobEffectInstanceHandle>();

        public final Template.Method<PotionEffect> toBukkit = new Template.Method<PotionEffect>();

    }

}

