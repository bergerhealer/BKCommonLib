package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.InventoryMerchant</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class InventoryMerchantHandle extends IInventoryHandle {
    /** @See {@link InventoryMerchantClass} */
    public static final InventoryMerchantClass T = new InventoryMerchantClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(InventoryMerchantHandle.class, "net.minecraft.server.InventoryMerchant", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static InventoryMerchantHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Object getMerchant();
    public abstract void setMerchant(Object value);
    /**
     * Stores class members for <b>net.minecraft.server.InventoryMerchant</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class InventoryMerchantClass extends Template.Class<InventoryMerchantHandle> {
        public final Template.Field.Converted<Object> merchant = new Template.Field.Converted<Object>();

    }

}

