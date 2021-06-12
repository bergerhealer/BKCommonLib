package com.bergerkiller.generated.net.minecraft.world.item;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.item.CreativeModeTab</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.item.CreativeModeTab")
public abstract class CreativeModeTabHandle extends Template.Handle {
    /** @See {@link CreativeModeTabClass} */
    public static final CreativeModeTabClass T = Template.Class.create(CreativeModeTabClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    public static final CreativeModeTabHandle SEARCH = T.SEARCH.getSafe();
    /* ============================================================================== */

    public static CreativeModeTabHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.item.CreativeModeTab</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CreativeModeTabClass extends Template.Class<CreativeModeTabHandle> {
        public final Template.StaticField.Converted<CreativeModeTabHandle> SEARCH = new Template.StaticField.Converted<CreativeModeTabHandle>();

    }

}

