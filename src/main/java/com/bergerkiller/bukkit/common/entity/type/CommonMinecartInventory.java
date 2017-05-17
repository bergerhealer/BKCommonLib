package com.bergerkiller.bukkit.common.entity.type;

import org.bukkit.entity.Minecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.bergerkiller.generated.net.minecraft.server.IInventoryHandle;

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
        this.handle.cast(IInventoryHandle.T).update(); 
    }

}
