package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.Sound;

public class CraftSoundHandle extends Template.Handle {
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
        return T.getSoundName.invokeVA(sound);
    }

    public static final class CraftSoundClass extends Template.Class<CraftSoundHandle> {
        public final Template.StaticMethod<String> getSoundName = new Template.StaticMethod<String>();

    }

}

