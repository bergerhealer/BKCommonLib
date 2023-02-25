package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.BeaconInventory;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.inventory.CraftInventoryBeacon</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.craftbukkit.inventory.CraftInventoryBeacon")
public abstract class CraftInventoryBeaconHandle extends Template.Handle {
    /** @see CraftInventoryBeaconClass */
    public static final CraftInventoryBeaconClass T = Template.Class.create(CraftInventoryBeaconClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CraftInventoryBeaconHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static BeaconInventory createNew(Object nmsTileEntityBeacon) {
        return T.createNew.invoke(nmsTileEntityBeacon);
    }

    /**
     * Stores class members for <b>org.bukkit.craftbukkit.inventory.CraftInventoryBeacon</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftInventoryBeaconClass extends Template.Class<CraftInventoryBeaconHandle> {
        public final Template.StaticMethod.Converted<BeaconInventory> createNew = new Template.StaticMethod.Converted<BeaconInventory>();

    }

}

