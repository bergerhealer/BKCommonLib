package com.bergerkiller.bukkit.common.internal.hooks;

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

    @Override
    public <T> T hook(T object) {
        final T hookedTracker = super.hook(object);

        // Also hook the stored State value
        Object state = EntityTrackerEntryHandle.T.getState.raw.invoke(hookedTracker);
        state = this.stateHook.hook(state);
        EntityTrackerEntryHandle.T.setState.raw.invoke(hookedTracker, state);

        // Swap out the broadcast consumer field with one that refers to this tracker instead
        if (EntityTrackerEntryStateHandle.T.broadcastMethod.isAvailable()) {
            EntityTrackerEntryStateHandle.T.broadcastMethod.set(state, packet -> {
                EntityTrackerEntryHandle.T.broadcastRawPacket.raw.invoke(hookedTracker, packet);
            });
        }

        return hookedTracker;
    }

    @ClassHook.HookImport("net.minecraft.server.level.EntityPlayer")
    @ClassHook.HookPackage("net.minecraft.server")
    @ClassHook.HookLoadVariables("com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
    public class StateHook extends ClassHook<StateHook> {
        @HookMethod("public void onTick:???()")
        public void onTick() {
            try {
                EntityTrackerEntryStateHandle handle = controller.getStateHandle();
                handle.setTimeSinceLocationSync(handle.getTimeSinceLocationSync() + 1);
                controller.onTick();
                handle.setTickCounter(handle.getTickCounter() + 1);
            } catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to synchronize", t);
            }
        }

        @HookMethod("public void removePairing:???(EntityPlayer entityPlayer)")
        public void removePairing(Object entityplayer) {
            try {
                Player viewer = (Player) WrapperConversion.toEntity(entityplayer);
                controller.makeHidden(viewer);
            } catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to despawn controller entity for a viewer", t);
            }
        }

        @HookMethod("public void addPairing:???(EntityPlayer entityPlayer)")
        public void addPairing(Object entityplayer) {
            try {
                Player viewer = (Player) WrapperConversion.toEntity(entityplayer);
                controller.makeVisible(viewer);
            } catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to spawn controller entity for a viewer", t);
            }
        }
    }
}
