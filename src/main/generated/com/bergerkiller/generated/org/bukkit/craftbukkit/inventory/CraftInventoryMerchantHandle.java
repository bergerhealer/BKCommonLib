package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.MerchantInventory;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.inventory.CraftInventoryMerchant</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.craftbukkit.inventory.CraftInventoryMerchant")
public abstract class CraftInventoryMerchantHandle extends Template.Handle {
    /** @see CraftInventoryMerchantClass */
    public static final CraftInventoryMerchantClass T = Template.Class.create(CraftInventoryMerchantClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CraftInventoryMerchantHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static MerchantInventory createNew(Object nmsInventoryMerchant) {
        return T.createNew.invoke(nmsInventoryMerchant);
    }

    /**
     * Stores class members for <b>org.bukkit.craftbukkit.inventory.CraftInventoryMerchant</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftInventoryMerchantClass extends Template.Class<CraftInventoryMerchantHandle> {
        public final Template.StaticMethod.Converted<MerchantInventory> createNew = new Template.StaticMethod.Converted<MerchantInventory>();

    }

}

