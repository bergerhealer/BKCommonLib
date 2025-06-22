package com.bergerkiller.generated.net.minecraft.world.level.storage;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.storage.ValueInput</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.storage.ValueInput")
public abstract class ValueInputHandle extends Template.Handle {
    /** @see ValueInputClass */
    public static final ValueInputClass T = Template.Class.create(ValueInputClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ValueInputHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.level.storage.ValueInput</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ValueInputClass extends Template.Class<ValueInputHandle> {
    }

}

