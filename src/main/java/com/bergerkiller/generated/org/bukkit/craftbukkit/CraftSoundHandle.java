package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Sound;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.CraftSound</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class CraftSoundHandle extends Template.Handle {
    /** @See {@link CraftSoundClass} */
    public static final CraftSoundClass T = new CraftSoundClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftSoundHandle.class, "org.bukkit.craftbukkit.CraftSound");

    /* ============================================================================== */

    public static CraftSoundHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftSoundHandle handle = new CraftSoundHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static String getSoundName(Sound sound) {
        return T.getSoundName.invoke(sound);
    }

    /**
     * Stores class members for <b>org.bukkit.craftbukkit.CraftSound</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftSoundClass extends Template.Class<CraftSoundHandle> {
        public final Template.StaticMethod<String> getSoundName = new Template.StaticMethod<String>();

    }

}

