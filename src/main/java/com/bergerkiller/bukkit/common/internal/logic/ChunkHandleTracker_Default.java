package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftChunkHandle;
import org.bukkit.Chunk;

/**
 * Default tracker. Doesn't really do anything. Is used on Spigot 1.20 and
 * all versions of Paper where this sort of crap isn't needed.
 */
class ChunkHandleTracker_Default implements ChunkHandleTracker {
    @Override
    public void enable() throws Throwable {
    }

    @Override
    public void disable() throws Throwable {
    }

    @Override
    public void startTracking(CommonPlugin plugin) {
    }

    @Override
    public void stopTracking() {
    }

    @Override
    public Object getChunkHandle(Chunk chunk) {
        return getHandle(chunk);
    }

    protected static Object getHandle(Chunk chunk) {
        try {
            return CraftChunkHandle.T.getHandle.invoker.invoke(chunk);
        } catch (RuntimeException ex) {
            if (CraftChunkHandle.T.isAssignableFrom(chunk)) {
                throw ex;
            } else {
                return null;
            }
        }
    }
}
