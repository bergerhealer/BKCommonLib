package com.bergerkiller.generated.net.minecraft.world.level.storage;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.storage.ValueOutput</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.storage.ValueOutput")
public abstract class ValueOutputHandle extends Template.Handle {
    /** @see ValueOutputClass */
    public static final ValueOutputClass T = Template.Class.create(ValueOutputClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ValueOutputHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void putString(String key, String value);
    /**
     * Stores class members for <b>net.minecraft.world.level.storage.ValueOutput</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ValueOutputClass extends Template.Class<ValueOutputHandle> {
        public final Template.Method<Void> putString = new Template.Method<Void>();

    }

}

