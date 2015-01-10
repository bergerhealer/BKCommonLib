package com.bergerkiller.bukkit.common.entity.type;

import net.minecraft.server.v1_8_R1.IInventory;

import org.bukkit.Location;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.bergerkiller.bukkit.common.controller.DefaultEntityInventoryController;
import com.bergerkiller.bukkit.common.controller.EntityInventoryController;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.CommonEntityInventory;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityInventoryHook;

/**
 * Base class for Minecart entities with an Inventory
 *
 * @param <T> - type of Minecart with Inventory
 */
public abstract class CommonMinecartInventory<T extends Minecart & InventoryHolder> extends CommonMinecart<T> implements CommonEntityInventory<T> {

    public CommonMinecartInventory(T base) {
        super(base);
    }

    @Override
    public Inventory getInventory() {
        return entity.getInventory();
    }

    @Override
    public void update() {
        getHandle(IInventory.class).update();
    }

    @Override
    public boolean spawn(Location at) {
        if (super.spawn(at)) {
            getInventoryController().onAttached();
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setInventoryController(EntityInventoryController controller) {
        this.prepareHook();
        if (controller == null) {
            controller = new DefaultEntityInventoryController();
        }
        getInventoryController().bind(null);
        controller.bind(this);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public EntityInventoryController<CommonEntity<T>> getInventoryController() {
        if (isHooked()) {
            return (EntityInventoryController<CommonEntity<T>>) getHandle(NMSEntityInventoryHook.class).getInventoryController();
        }
        final EntityInventoryController controller = new DefaultEntityInventoryController();
        controller.bind(this);
        return controller;
    }
}
