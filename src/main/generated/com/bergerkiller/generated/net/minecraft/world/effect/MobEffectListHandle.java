package com.bergerkiller.generated.net.minecraft.world.effect;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import org.bukkit.potion.PotionEffectType;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.effect.MobEffectList</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.effect.MobEffectList")
public abstract class MobEffectListHandle extends Template.Handle {
    /** @see MobEffectListClass */
    public static final MobEffectListClass T = Template.Class.create(MobEffectListClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static MobEffectListHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static MobEffectListHandle fromBukkit(PotionEffectType effectType) {
        return T.fromBukkit.invoke(effectType);
    }

    public static Holder<MobEffectListHandle> holderFromBukkit(PotionEffectType effectType) {
        return T.holderFromBukkit.invoke(effectType);
    }

    public static PotionEffectType holderToBukkit(Holder<MobEffectListHandle> mobEffectList) {
        return T.holderToBukkit.invoke(mobEffectList);
    }

    public static int getId(MobEffectListHandle mobeffectlist) {
        return T.getId.invoke(mobeffectlist);
    }

    public static MobEffectListHandle fromId(int id) {
        return T.fromId.invoke(id);
    }

    public abstract PotionEffectType toBukkit();
    /**
     * Stores class members for <b>net.minecraft.world.effect.MobEffectList</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MobEffectListClass extends Template.Class<MobEffectListHandle> {
        public final Template.StaticMethod.Converted<MobEffectListHandle> fromBukkit = new Template.StaticMethod.Converted<MobEffectListHandle>();
        public final Template.StaticMethod.Converted<Holder<MobEffectListHandle>> holderFromBukkit = new Template.StaticMethod.Converted<Holder<MobEffectListHandle>>();
        public final Template.StaticMethod.Converted<PotionEffectType> holderToBukkit = new Template.StaticMethod.Converted<PotionEffectType>();
        public final Template.StaticMethod.Converted<Integer> getId = new Template.StaticMethod.Converted<Integer>();
        public final Template.StaticMethod.Converted<MobEffectListHandle> fromId = new Template.StaticMethod.Converted<MobEffectListHandle>();

        public final Template.Method<PotionEffectType> toBukkit = new Template.Method<PotionEffectType>();

    }

}

