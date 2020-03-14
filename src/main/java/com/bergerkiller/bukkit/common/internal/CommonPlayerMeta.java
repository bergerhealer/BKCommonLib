package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.generated.net.minecraft.server.EntityPlayerHandle;

import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An instance of Player metadata stored by BKCommonLib
 */
public class CommonPlayerMeta {

    private final LongHashSet visibleChunks = new LongHashSet(441);
    private final WeakReference<Player> playerRef;
    private final List<Integer> removeQueue;
    private List<EntityNetworkController<?>> pendingViewerUpdates = Collections.emptyList();
    private int respawnBlindnessEndTick = 0;

    protected CommonPlayerMeta(Player player) {
        this.playerRef = new WeakReference<Player>(player);
        if (EntityPlayerHandle.T.removeQueue.isAvailable()) {
            removeQueue = EntityPlayerHandle.T.removeQueue.get(Conversion.toEntityHandle.convert(player));
        } else {
            removeQueue = new ArrayList<Integer>();
        }
    }

    public List<Integer> getRemoveQueue() {
        return this.removeQueue;
    }

    /**
     * Sends out destroy packets for all entity ids in the removal queue
     */
    public void syncRemoveQueue() {
        if (!this.removeQueue.isEmpty()) {
            CommonPacket packet = PacketType.OUT_ENTITY_DESTROY.newInstance(this.removeQueue);
            this.removeQueue.clear();
            Player p = this.playerRef.get();
            if (p != null) {
                PacketUtil.sendPacket(p, packet);
            }
        }
    }

    public Player getPlayer() {
        return playerRef.get();
    }

    /**
     * Checks whether this viewer is currently blind because of respawning recently.
     * If this is the case, the controller will be queued and updateViewer() will be
     * called once this blindness is over.
     * 
     * @param controller to queue if blind
     * @return True if not blind, False if blind
     */
    public boolean respawnBlindnessCheck(EntityNetworkController<?> controller) {
        if (this.respawnBlindnessEndTick != 0) {
            int num = this.respawnBlindnessEndTick - CommonUtil.getServerTicks();
            if (num > 0 && CommonPlugin.hasInstance()) {
                // Schedule updateViewer() at a later time
                if (this.pendingViewerUpdates.isEmpty()) {
                    this.pendingViewerUpdates = new ArrayList<EntityNetworkController<?>>();
                    new ProcessPendingViewerUpdatesTask().start(num);
                }
                this.pendingViewerUpdates.add(controller);
                return false;
            } else {
                this.respawnBlindnessEndTick = 0;
            }
        }
        return true;
    }

    /**
     * Initiates the blindness a player has while respawning onto a new world.
     * During this time entity spawn packets may not be received properly.
     */
    public void initiateRespawnBlindness() {
        this.respawnBlindnessEndTick = CommonUtil.getServerTicks() + 5;
    }

    @Deprecated
    public void clearVisibleChunks() {
        synchronized (visibleChunks) {
            visibleChunks.clear();
        }
    }

    /**
     * This stuff no longer works now!
     */
    @Deprecated
    public boolean isChunkVisible(int chunkX, int chunkZ) {
        synchronized (visibleChunks) {
            return visibleChunks.contains(chunkX, chunkZ);
        }
    }

    @Deprecated
    public void setChunksAsVisible(int[] chunkX, int[] chunkZ) {
        if (chunkX.length != chunkZ.length) {
            throw new IllegalArgumentException("Chunk X and Z coordinate count is not the same");
        }
        synchronized (visibleChunks) {
            for (int i = 0; i < chunkX.length; i++) {
                visibleChunks.add(chunkX[i], chunkZ[i]);
            }
        }
    }

    @Deprecated
    public void setChunkVisible(int chunkX, int chunkZ, boolean visible) {
        synchronized (visibleChunks) {
            if (visible) {
                visibleChunks.add(chunkX, chunkZ);
            } else {
                visibleChunks.remove(chunkX, chunkZ);
            }
        }
    }

    private final class ProcessPendingViewerUpdatesTask extends Task {

        public ProcessPendingViewerUpdatesTask() {
            super(CommonPlugin.getInstance());
        }

        @Override
        public void run() {
            Player viewer = CommonPlayerMeta.this.getPlayer();
            List<EntityNetworkController<?>> pendingUpdates = CommonPlayerMeta.this.pendingViewerUpdates;
            CommonPlayerMeta.this.pendingViewerUpdates = Collections.emptyList();
            if (viewer == null || !viewer.isOnline()) {
                return;
            }

            for (EntityNetworkController<?> pending : pendingUpdates) {
                CommonEntity<?> pendingEntity = pending.getEntity();
                if (pendingEntity == null || pendingEntity.getWorld() != viewer.getWorld()) {
                    continue; // not bound or wrong world
                }
                if (pending.isViewable(viewer)) {
                    pending.addViewer(viewer);
                }
            }
        }
    }
}
