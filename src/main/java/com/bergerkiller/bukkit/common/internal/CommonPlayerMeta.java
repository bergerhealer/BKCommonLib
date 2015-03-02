package com.bergerkiller.bukkit.common.internal;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.server.SportBukkitServer;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * An instance of Player metadata stored by BKCommonLib
 */
public class CommonPlayerMeta {

    private final HashMap<Integer,Integer> visibleChunks = new HashMap<Integer,Integer>(441);
    private final WeakReference<Player> playerRef;
    private final List<Integer> removeQueue;

    @SuppressWarnings("unchecked")
    protected CommonPlayerMeta(Player player) {
        this.playerRef = new WeakReference<Player>(player);
        if (Common.SERVER instanceof SportBukkitServer) {
            removeQueue = new ArrayList<Integer>();
        } else {
            removeQueue = CommonNMS.getNative(player).removeQueue;
        }
    }

    public List<Integer> getRemoveQueue() {
        return removeQueue;
    }

    public void setQueueForRemoval(int entityId, boolean remove) {
        LogicUtil.addOrRemove(removeQueue, entityId, remove);
    }

    public Player getPlayer() {
        return playerRef.get();
    }

    public void clearVisibleChunks() {
        synchronized (visibleChunks) {
            visibleChunks.clear();
        }
    }

    public boolean isChunkVisible(int chunkX, int chunkZ) {
        synchronized (visibleChunks) {
        	return visibleChunks.keySet().contains(Integer.valueOf(chunkX)) && visibleChunks.values().contains(Integer.valueOf(chunkZ));
        }
    }

    public void setChunksAsVisible(int[] chunkX, int[] chunkZ) {
        if (chunkX.length != chunkZ.length) {
            throw new IllegalArgumentException("Chunk X and Z coordinate count is not the same");
        }
        synchronized (visibleChunks) {
            for (int i = 0; i < chunkX.length; i++) {
            	visibleChunks.put(Integer.valueOf(chunkX[i]), Integer.valueOf(chunkZ[i]));
            }
        }
    }

    public void setChunkVisible(int chunkX, int chunkZ, boolean visible) {
        synchronized (visibleChunks) {
            if (visible) {
                visibleChunks.put(chunkX, chunkZ);
            } else {
            	ArrayList<Integer> keys = new ArrayList<Integer>(visibleChunks.keySet());
				ArrayList<Integer> vals = new ArrayList<Integer>(visibleChunks.values());
				
				for(int i = 0; i < keys.size(); i++)
				{
					if(keys.get(i) == Integer.valueOf(chunkX) && vals.get(i) == Integer.valueOf(chunkZ))
					{
						keys.remove(i);
						vals.remove(i);
						break;
					}
				}
				visibleChunks.clear();
				for(int j = 0; j < keys.size(); j++)
				{
					visibleChunks.put(keys.get(j), vals.get(j));
				}
            }
        }
    }
}
