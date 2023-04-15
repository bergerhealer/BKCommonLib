package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.List;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
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

@ClassHook.HookPackage("net.minecraft.server")
public class EntityTrackerEntryHook_1_8_to_1_13_2 extends ClassHook<EntityTrackerEntryHook_1_8_to_1_13_2> implements EntityTrackerEntryHook {
    private EntityNetworkController<?> controller;
    private ViewableLogic viewable;

    public EntityNetworkController<?> getController() {
        return controller;
    }

    public void setController(EntityNetworkController<?> controller) {
        this.controller = controller;
        this.viewable = (controller == null) ? null : new ViewableLogic(controller);
    }

    @HookMethod("public void scanPlayers(List<EntityHuman> list)")
    public void scanPlayers(List<?> list) {
        base.scanPlayers(list);
    }

    @HookMethod("public void track(List<EntityHuman> list)")
    public void track(List<?> list) {
        EntityTrackerEntryStateHandle handle = EntityTrackerEntryStateHandle.createHandle(instance());
        if (handle.checkTrackNeeded()) {
            scanPlayers(list);
        }
        handle.setTimeSinceLocationSync(handle.getTimeSinceLocationSync() + 1);
        try {
            controller.onTick();
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to synchronize", t);
        }
        handle.setTickCounter(handle.getTickCounter() + 1);
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

    @HookMethod("public void clear(EntityPlayer entityplayer)")
    public void clear(Object entityplayer) {
        try {
            controller.removeViewer(Conversion.toPlayer.convert(entityplayer));
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to remove viewer", t);
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

    @HookMethod("public void updatePlayer(EntityPlayer entityplayer)")
    public void updatePlayer(Object entityplayer) {
        if (entityplayer != controller.getEntity().getHandle()) {
            try {
                // Add or remove the viewer depending on whether this entity is viewable by the viewer
                Player viewer = (Player) WrapperConversion.toEntity(entityplayer);
                if (viewable.isViewable(viewer)) {
                    controller.addViewer(viewer);
                } else {
                    controller.removeViewer(viewer);
                }
            } catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to update viewer", t);
            }
        }
    }

    // isViewable() logic as used on 1.8 - 1.13.2. Not really meant to be used on 1.14+
    public static class ViewableLogic {
        private final EntityNetworkController<?> controller;

        public ViewableLogic(EntityNetworkController<?> controller) {
            this.controller = controller;
        }

        public void handleRespawnBlindness(Player viewer) {
            CommonEntity<?> pendingEntity = controller.getEntity();
            if (pendingEntity == null || pendingEntity.getWorld() != viewer.getWorld()) {
                return; // not bound or wrong world
            }
            if (isViewable(viewer)) {
                controller.addViewer(viewer);
            }
        }

        /**
         * Checks whether a particular viewer can see this entity
         *
         * @param viewer
         * @return True if visible
         * @deprecated Not a reliable way to check whether this entity is viewable. This is subject
         *             to changes done in servers or forks of them.
         */
        public final boolean isViewable(Player viewer) {
            // If viewer has blindness due to respawning, do not make it visible just yet
            // When blindness runs out, perform an updateViewer again to make this entity visible quickly
            if (!CommonPlugin.getInstance().getPlayerMeta(viewer).respawnBlindnessCheck(this)) {
                return false;
            }

            return isViewable_self_or_passenger(viewer);
        }

        private boolean isViewable_self_or_passenger(Player viewer) {
            CommonEntity<?> entity = controller.getEntity();
            if (!com.bergerkiller.generated.org.bukkit.entity.EntityHandle.T.isSeenBy.invoker.invoke(entity.getEntity(), viewer)) {
                return false;
            }
            if (isViewable_self(viewer)) {
                return true;
            }
            for (Entity passenger : entity.getPassengers()) {
                EntityNetworkController<?> network = CommonEntity.get(passenger).getNetworkController();
                if (network != null && (new ViewableLogic(network)).isViewable_self_or_passenger(viewer)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isViewable_self(Player viewer) {
            CommonEntity<?> entity = controller.getEntity();

            // Viewer is a passenger of this Entity
            for (Entity passenger : entity.getPassengers()) {
                if (viewer.equals(passenger)) {
                    return true;
                }
            }
            // View range check
            final int dx = MathUtil.floor(Math.abs(EntityUtil.getLocX(viewer) - controller.locSynched.getX()));
            final int dz = MathUtil.floor(Math.abs(EntityUtil.getLocZ(viewer) - controller.locSynched.getZ()));
            int view = controller.getViewDistance();
            if (dx > view || dz > view) {
                return false;
            }
            // The entity is in a chunk not seen by the viewer
            if (!EntityHandle.T.isIgnoreChunkCheck.invoke(entity.getHandle())
                    && !PlayerUtil.isChunkVisible(viewer, entity.getChunkX(), entity.getChunkZ())) {
                return false;
            }
            // Entity is a Player hidden from sight for the viewer?
            if (entity.getEntity() instanceof Player && !viewer.canSee((Player) entity.getEntity())) {
                return false;
            }
            // It can be seen
            return true;
        }
    }
}
