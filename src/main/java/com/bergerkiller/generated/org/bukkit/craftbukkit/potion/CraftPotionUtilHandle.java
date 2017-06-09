package com.bergerkiller.generated.org.bukkit.craftbukkit.potion;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.potion.PotionEffect;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.potion.CraftPotionUtil</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class CraftPotionUtilHandle extends Template.Handle {
    /** @See {@link CraftPotionUtilClass} */
    public static final CraftPotionUtilClass T = new CraftPotionUtilClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftPotionUtilHandle.class, "org.bukkit.craftbukkit.potion.CraftPotionUtil");

    /* ============================================================================== */

    public static CraftPotionUtilHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftPotionUtilHandle handle = new CraftPotionUtilHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static PotionEffect toBukkit(Object nmsMobEffect) {
        return T.toBukkit.invokeVA(nmsMobEffect);
    }

    public static Object fromBukkit(PotionEffect effect) {
        return T.fromBukkit.invokeVA(effect);
    }

    /**
     * Stores class members for <b>org.bukkit.craftbukkit.potion.CraftPotionUtil</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftPotionUtilClass extends Template.Class<CraftPotionUtilHandle> {
        public final Template.StaticMethod.Converted<PotionEffect> toBukkit = new Template.StaticMethod.Converted<PotionEffect>();
        public final Template.StaticMethod.Converted<Object> fromBukkit = new Template.StaticMethod.Converted<Object>();

    }

}

