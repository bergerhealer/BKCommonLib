package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.logging.Level;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryStateHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;

public class EntityTrackerEntryHook_1_14 extends ClassHook<EntityTrackerEntryHook_1_14> implements EntityTrackerEntryHook {
    private EntityNetworkController<?> controller;
    private final StateHook stateHook = new StateHook();

    @Override
    public EntityNetworkController<?> getController() {
        return controller;
    }

    @Override
    public void setController(EntityNetworkController<?> controller) {
        this.controller = controller;
    }

    @HookMethod("public void hideForAll:???()")
    public void hideForAll() {
        try {
            controller.makeHiddenForAll();

            // This is usually only called when the entity tracker entry is removed from the mapping
            // Detect that it is, and if it is, call onDetached() on the network controller
            CommonEntity<?> entity = controller.getEntity();
            if (entity != null) {
                World world = entity.getWorld();
                EntityTrackerEntryHandle eh = EntityTrackerEntryHandle.createHandle(instance());
                if (world != null && !WorldUtil.getTracker(world).containsEntry(eh)) {
                    controller.bind(null, null);
                }
            }
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to hide for all viewers:");
            t.printStackTrace();
        }
    }

    @HookMethod("public void removeViewer:???(EntityPlayer entityplayer)")
    public void removeViewer(Object entityplayer) {
        try {
            controller.removeViewer(Conversion.toPlayer.convert(entityplayer));
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to remove viewer:");
            t.printStackTrace();
        }
    }

    @HookMethod("public void updatePlayer(EntityPlayer entityplayer)")
    public void updatePlayer(Object entityplayer) {
        if (entityplayer != controller.getEntity().getHandle()) {
            try {
                controller.updateViewer(Conversion.toPlayer.convert(entityplayer));
            } catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to update viewer:");
                t.printStackTrace();
            }
        }
    }

    @Override
    public <T> T hook(T object) {
        object = super.hook(object);

        // Also hook the stored State value
        Object state = EntityTrackerEntryHandle.T.getState.raw.invoke(object);
        state = this.stateHook.hook(state);
        EntityTrackerEntryHandle.T.setState.raw.invoke(object, state);

        return object;
    }

    public class StateHook extends ClassHook<StateHook> {

        @HookMethod("public void onTick:???()")
        public void onTick() {
            EntityTrackerEntryStateHandle handle = EntityTrackerEntryStateHandle.createHandle(instance());
            handle.setTimeSinceLocationSync(handle.getTimeSinceLocationSync() + 1);
            try {
                controller.onTick();
            } catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to synchronize:");
                t.printStackTrace();
            }
            handle.setTickCounter(handle.getTickCounter() + 1);
        }
    }
}
