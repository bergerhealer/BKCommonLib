package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.generated.net.minecraft.server.EntityPlayerHandle;

import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * An instance of Player metadata stored by BKCommonLib
 */
public class CommonPlayerMeta {

    private final LongHashSet visibleChunks = new LongHashSet(441);
    private final WeakReference<Player> playerRef;
    private final List<Integer> removeQueue;

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
}
