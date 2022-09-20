package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;

@ClassHook.HookImport("net.minecraft.server.level.EntityPlayer")
@ClassHook.HookPackage("net.minecraft.server")
@ClassHook.HookLoadVariables("com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
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
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to hide for all viewers", t);
        }
    }

    @HookMethod("public void removeViewer:???(EntityPlayer entityplayer)")
    public void removeViewer(Object entityplayer) {
        try {
            controller.removeViewer(Conversion.toPlayer.convert(entityplayer));
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to remove viewer", t);
        }
    }

    private void controllerUpdateViewer(Player viewer) {
        // Add or remove the viewer depending on whether this entity is viewable by the viewer
        if (controller.isViewable(viewer)) {
            controller.addViewer(viewer);
        } else {
            controller.removeViewer(viewer);
        }
    }

    @HookMethod("public void updatePlayer(EntityPlayer entityplayer)")
    public void updatePlayer(Object entityplayer) {
        try {
            if (entityplayer != controller.getEntity().getHandle()) {
                Player viewer = (Player) WrapperConversion.toEntity(entityplayer);

                // For paper: check whether player reported as removed from the tracked-set
                // This does cause a false-positive when the entity is first made visible to players (player joins server)
                // However, it is quickly rectified the next tick, so this is no big deal.
                if (EntityTrackerEntryHandle.T.isTrackingStoppedPaper.invoke(instance(), entityplayer)) {
                    controller.removeViewer(viewer);
                    return;
                }

                controllerUpdateViewer(viewer);
            }
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to update viewer", t);
        }
    }

    // Overrided so we don't do the isTrackingStoppedPaper check when this method is called
    // During this time, the Paper tracked player set isn't initialized yet
    @SuppressWarnings("rawtypes")
    @HookMethodCondition("version >= 1.18")
    @HookMethod("public void updatePlayers(java.util.List list)")
    public void updatePlayers(List players) {
        for (Object entityplayer : players) {
            try {
                if (entityplayer != controller.getEntity().getHandle()) {
                    Player viewer = (Player) WrapperConversion.toEntity(entityplayer);
                    controllerUpdateViewer(viewer);
                }
            } catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to update viewer", t);
            }
        }
    }

    // 1.14 - 1.17. For some reason dynamic remapping of the name doesn't work for the List.
    @SuppressWarnings("rawtypes")
    @HookMethodCondition("version < 1.18")
    @HookMethod("public void track(java.util.List list)")
    public void track(List players) {
        updatePlayers(players);
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

    @ClassHook.HookImport("net.minecraft.server.level.EntityPlayer")
    @ClassHook.HookPackage("net.minecraft.server")
    public class StateHook extends ClassHook<StateHook> {

        @HookMethod("public void onTick:???()")
        public void onTick() {
            EntityTrackerEntryStateHandle handle = EntityTrackerEntryStateHandle.createHandle(instance());
            handle.setTimeSinceLocationSync(handle.getTimeSinceLocationSync() + 1);
            try {
                controller.onTick();
            } catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to synchronize", t);
            }
            handle.setTickCounter(handle.getTickCounter() + 1);
        }

        // This hook is only used on Purpur server to handle adding a player as a viewer
        // Normally we handle updatePlayer() causing this method to never be called
        @HookMethod(value="public void onViewerAdded_tuinity:???(EntityPlayer entityplayer)", optional=true)
        public void addViewerPurpur(Object entityplayer) {
            try {
                // Before this method was called, the player was added as viewer to the viewers mapping
                // Remove from this mapping so that isViewable can cancel it, and addViewer() works as expected
                EntityTrackerEntryStateHandle.T.removeViewerFromMap_tuinity.invoker.invoke(instance(), entityplayer);

                // If viewable, add as a viewer (and add it back to the mapping)
                Player viewer = (Player) WrapperConversion.toEntity(entityplayer);
                if (controller.isViewable(viewer)) {
                    controller.addViewer(viewer);
                }
            } catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to add viewer", t);
            }
        }
    }
}
