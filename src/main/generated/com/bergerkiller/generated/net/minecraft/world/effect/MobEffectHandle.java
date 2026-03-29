package com.bergerkiller.generated.net.minecraft.world.effect;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import org.bukkit.potion.PotionEffectType;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.effect.MobEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.effect.MobEffect")
public abstract class MobEffectHandle extends Template.Handle {
    /** @see MobEffectClass */
    public static final MobEffectClass T = Template.Class.create(MobEffectClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static MobEffectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static MobEffectHandle fromBukkit(PotionEffectType effectType) {
        return T.fromBukkit.invoke(effectType);
    }

    public static Holder<MobEffectHandle> holderFromBukkit(PotionEffectType effectType) {
        return T.holderFromBukkit.invoke(effectType);
    }

    public static PotionEffectType holderToBukkit(Holder<MobEffectHandle> mobEffectList) {
        return T.holderToBukkit.invoke(mobEffectList);
    }

    public static int getId(MobEffectHandle mobeffectlist) {
        return T.getId.invoke(mobeffectlist);
    }

    public static MobEffectHandle fromId(int id) {
        return T.fromId.invoke(id);
    }

    public abstract PotionEffectType toBukkit();
    /**
     * Stores class members for <b>net.minecraft.world.effect.MobEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MobEffectClass extends Template.Class<MobEffectHandle> {
        public final Template.StaticMethod.Converted<MobEffectHandle> fromBukkit = new Template.StaticMethod.Converted<MobEffectHandle>();
        public final Template.StaticMethod.Converted<Holder<MobEffectHandle>> holderFromBukkit = new Template.StaticMethod.Converted<Holder<MobEffectHandle>>();
        public final Template.StaticMethod.Converted<PotionEffectType> holderToBukkit = new Template.StaticMethod.Converted<PotionEffectType>();
        public final Template.StaticMethod.Converted<Integer> getId = new Template.StaticMethod.Converted<Integer>();
        public final Template.StaticMethod.Converted<MobEffectHandle> fromId = new Template.StaticMethod.Converted<MobEffectHandle>();

        public final Template.Method<PotionEffectType> toBukkit = new Template.Method<PotionEffectType>();

    }

}

