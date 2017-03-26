package com.bergerkiller.bukkit.common.entity.type;

import net.minecraft.server.v1_11_R1.IInventory;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Base class for Minecart entities with an Inventory
 *
 * @param <T> - type of Minecart with Inventory
 */
public abstract class CommonMinecartInventory<T extends Minecart & InventoryHolder> extends CommonMinecart<T> {

    public CommonMinecartInventory(T base) {
        super(base);
    }

    public Inventory getInventory() {
        return entity.getInventory();
    }

    public void update() {
        getHandle(IInventory.class).update();
    }

}
