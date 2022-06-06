package com.bergerkiller.generated.net.minecraft.world.effect;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import org.bukkit.potion.PotionEffect;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.effect.MobEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.effect.MobEffect")
public abstract class MobEffectHandle extends Template.Handle {
    /** @See {@link MobEffectClass} */
    public static final MobEffectClass T = Template.Class.create(MobEffectClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static MobEffectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static MobEffectHandle fromNBT(CommonTagCompound compound) {
        return T.fromNBT.invoke(compound);
    }

    public static MobEffectHandle fromBukkit(PotionEffect effect) {
        return T.fromBukkit.invoke(effect);
    }

    public abstract PotionEffect toBukkit();
    public abstract MobEffectListHandle getEffectList();
    public abstract void setEffectList(MobEffectListHandle value);
    public abstract int getDuration();
    public abstract void setDuration(int value);
    public abstract int getAmplifier();
    public abstract void setAmplifier(int value);
    public abstract boolean isAmbient();
    public abstract void setAmbient(boolean value);
    public abstract boolean isNoCounter();
    public abstract void setNoCounter(boolean value);
    public abstract boolean isParticles();
    public abstract void setParticles(boolean value);
    /**
     * Stores class members for <b>net.minecraft.world.effect.MobEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MobEffectClass extends Template.Class<MobEffectHandle> {
        public final Template.Field.Converted<MobEffectListHandle> effectList = new Template.Field.Converted<MobEffectListHandle>();
        public final Template.Field.Integer duration = new Template.Field.Integer();
        public final Template.Field.Integer amplifier = new Template.Field.Integer();
        public final Template.Field.Boolean ambient = new Template.Field.Boolean();
        public final Template.Field.Boolean noCounter = new Template.Field.Boolean();
        public final Template.Field.Boolean particles = new Template.Field.Boolean();

        public final Template.StaticMethod.Converted<MobEffectHandle> fromNBT = new Template.StaticMethod.Converted<MobEffectHandle>();
        public final Template.StaticMethod.Converted<MobEffectHandle> fromBukkit = new Template.StaticMethod.Converted<MobEffectHandle>();

        public final Template.Method<PotionEffect> toBukkit = new Template.Method<PotionEffect>();

    }

}

