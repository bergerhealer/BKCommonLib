package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Sound;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.CraftSound</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class CraftSoundHandle extends Template.Handle {
    /** @See {@link CraftSoundClass} */
    public static final CraftSoundClass T = new CraftSoundClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftSoundHandle.class, "org.bukkit.craftbukkit.CraftSound", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static CraftSoundHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static String getSoundName(Sound sound) {
        return T.getSoundName.invoker.invoke(null,sound);
    }

    /**
     * Stores class members for <b>org.bukkit.craftbukkit.CraftSound</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftSoundClass extends Template.Class<CraftSoundHandle> {
        public final Template.StaticMethod<String> getSoundName = new Template.StaticMethod<String>();

    }

}

