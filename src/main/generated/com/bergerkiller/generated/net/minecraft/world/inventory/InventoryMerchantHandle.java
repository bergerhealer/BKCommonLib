package com.bergerkiller.generated.net.minecraft.world.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.world.IInventoryHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.inventory.InventoryMerchant</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.inventory.InventoryMerchant")
public abstract class InventoryMerchantHandle extends IInventoryHandle {
    /** @see InventoryMerchantClass */
    public static final InventoryMerchantClass T = Template.Class.create(InventoryMerchantClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static InventoryMerchantHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Object getMerchant();
    public abstract void setMerchant(Object value);
    /**
     * Stores class members for <b>net.minecraft.world.inventory.InventoryMerchant</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class InventoryMerchantClass extends Template.Class<InventoryMerchantHandle> {
        public final Template.Field.Converted<Object> merchant = new Template.Field.Converted<Object>();

    }

}

