package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.Item</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class ItemHandle extends Template.Handle {
    /** @See {@link ItemClass} */
    public static final ItemClass T = new ItemClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ItemHandle.class, "net.minecraft.server.Item");

    /* ============================================================================== */

    public static ItemHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public int getMaxStackSize() {
        return T.getMaxStackSize.invoke(getRaw());
    }

    public boolean usesDurability() {
        return T.usesDurability.invoke(getRaw());
    }

    /**
     * Stores class members for <b>net.minecraft.server.Item</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ItemClass extends Template.Class<ItemHandle> {
        public final Template.Method<Integer> getMaxStackSize = new Template.Method<Integer>();
        public final Template.Method<Boolean> usesDurability = new Template.Method<Boolean>();

    }

}

