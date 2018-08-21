package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Art;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.CraftArt</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class CraftArtHandle extends Template.Handle {
    /** @See {@link CraftArtClass} */
    public static final CraftArtClass T = new CraftArtClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftArtHandle.class, "org.bukkit.craftbukkit.CraftArt");

    /* ============================================================================== */

    public static CraftArtHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Art NotchToBukkit(Object art) {
        return T.NotchToBukkit.invoke(art);
    }

    public static Object BukkitToNotch(Art art) {
        return T.BukkitToNotch.invoke(art);
    }

    public static Object NotchFromInternalId(int internalId) {
        return T.NotchFromInternalId.invoke(internalId);
    }

    public static int NotchToInternalId(Object art) {
        return T.NotchToInternalId.invoke(art);
    }

    public static Object NotchFromInternalName(String internalName) {
        return T.NotchFromInternalName.invoke(internalName);
    }

    public static String NotchToInternalName(Object art) {
        return T.NotchToInternalName.invoke(art);
    }

    /**
     * Stores class members for <b>org.bukkit.craftbukkit.CraftArt</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftArtClass extends Template.Class<CraftArtHandle> {
        public final Template.StaticMethod.Converted<Art> NotchToBukkit = new Template.StaticMethod.Converted<Art>();
        public final Template.StaticMethod.Converted<Object> BukkitToNotch = new Template.StaticMethod.Converted<Object>();
        public final Template.StaticMethod.Converted<Object> NotchFromInternalId = new Template.StaticMethod.Converted<Object>();
        public final Template.StaticMethod.Converted<Integer> NotchToInternalId = new Template.StaticMethod.Converted<Integer>();
        public final Template.StaticMethod.Converted<Object> NotchFromInternalName = new Template.StaticMethod.Converted<Object>();
        public final Template.StaticMethod.Converted<String> NotchToInternalName = new Template.StaticMethod.Converted<String>();

    }

}

