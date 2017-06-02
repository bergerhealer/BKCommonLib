package com.bergerkiller.generated.org.bukkit.craftbukkit.potion;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.potion.PotionEffect;

public class CraftPotionUtilHandle extends Template.Handle {
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

    public static final class CraftPotionUtilClass extends Template.Class<CraftPotionUtilHandle> {
        public final Template.StaticMethod.Converted<PotionEffect> toBukkit = new Template.StaticMethod.Converted<PotionEffect>();
        public final Template.StaticMethod.Converted<Object> fromBukkit = new Template.StaticMethod.Converted<Object>();

    }

}

