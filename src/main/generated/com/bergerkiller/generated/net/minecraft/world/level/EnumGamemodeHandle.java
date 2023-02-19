package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.EnumGamemode</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.EnumGamemode")
public abstract class EnumGamemodeHandle extends Template.Handle {
    /** @See {@link EnumGamemodeClass} */
    public static final EnumGamemodeClass T = Template.Class.create(EnumGamemodeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EnumGamemodeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static EnumGamemodeHandle getById(int id) {
        return T.getById.invoke(id);
    }

    public abstract int getId();
    /**
     * Stores class members for <b>net.minecraft.world.level.EnumGamemode</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EnumGamemodeClass extends Template.Class<EnumGamemodeHandle> {
        public final Template.StaticMethod.Converted<EnumGamemodeHandle> getById = new Template.StaticMethod.Converted<EnumGamemodeHandle>();

        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

