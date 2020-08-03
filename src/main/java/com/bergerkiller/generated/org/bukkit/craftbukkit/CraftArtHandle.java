package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Art;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.CraftArt</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.craftbukkit.CraftArt")
public abstract class CraftArtHandle extends Template.Handle {
    /** @See {@link CraftArtClass} */
    public static final CraftArtClass T = Template.Class.create(CraftArtClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CraftArtHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Art NotchToBukkit(Object art) {
        return T.NotchToBukkit.invoke(art);
    }

    public static Object BukkitToNotch(Art art) {
        return T.BukkitToNotch.invoker.invoke(null,art);
    }

    public static Object NotchFromInternalId(int internalId) {
        return T.NotchFromInternalId.invoker.invoke(null,internalId);
    }

    public static int NotchToInternalId(Object art) {
        return T.NotchToInternalId.invoke(art);
    }

    public static Object NotchFromInternalName(String internalName) {
        return T.NotchFromInternalName.invoker.invoke(null,internalName);
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
        public final Template.StaticMethod<Object> BukkitToNotch = new Template.StaticMethod<Object>();
        public final Template.StaticMethod<Object> NotchFromInternalId = new Template.StaticMethod<Object>();
        public final Template.StaticMethod.Converted<Integer> NotchToInternalId = new Template.StaticMethod.Converted<Integer>();
        public final Template.StaticMethod<Object> NotchFromInternalName = new Template.StaticMethod<Object>();
        public final Template.StaticMethod.Converted<String> NotchToInternalName = new Template.StaticMethod.Converted<String>();

    }

}

