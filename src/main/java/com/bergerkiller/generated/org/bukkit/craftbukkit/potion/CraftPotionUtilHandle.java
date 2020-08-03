package com.bergerkiller.generated.org.bukkit.craftbukkit.potion;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.potion.PotionEffect;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.potion.CraftPotionUtil</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.craftbukkit.potion.CraftPotionUtil")
public abstract class CraftPotionUtilHandle extends Template.Handle {
    /** @See {@link CraftPotionUtilClass} */
    public static final CraftPotionUtilClass T = Template.Class.create(CraftPotionUtilClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CraftPotionUtilHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PotionEffect toBukkit(Object nmsMobEffect) {
        return T.toBukkit.invoke(nmsMobEffect);
    }

    public static Object fromBukkit(PotionEffect effect) {
        return T.fromBukkit.invoker.invoke(null,effect);
    }

    /**
     * Stores class members for <b>org.bukkit.craftbukkit.potion.CraftPotionUtil</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftPotionUtilClass extends Template.Class<CraftPotionUtilHandle> {
        public final Template.StaticMethod.Converted<PotionEffect> toBukkit = new Template.StaticMethod.Converted<PotionEffect>();
        public final Template.StaticMethod<Object> fromBukkit = new Template.StaticMethod<Object>();

    }

}

