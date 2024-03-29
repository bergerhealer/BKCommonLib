package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.PlayerInventory;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.inventory.CraftInventoryPlayer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.craftbukkit.inventory.CraftInventoryPlayer")
public abstract class CraftInventoryPlayerHandle extends Template.Handle {
    /** @see CraftInventoryPlayerClass */
    public static final CraftInventoryPlayerClass T = Template.Class.create(CraftInventoryPlayerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CraftInventoryPlayerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PlayerInventory createNew(Object nmsPlayerInventory) {
        return T.constr_nmsPlayerInventory.newInstance(nmsPlayerInventory);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>org.bukkit.craftbukkit.inventory.CraftInventoryPlayer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftInventoryPlayerClass extends Template.Class<CraftInventoryPlayerHandle> {
        public final Template.Constructor.Converted<PlayerInventory> constr_nmsPlayerInventory = new Template.Constructor.Converted<PlayerInventory>();

    }

}

