package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import org.bukkit.Chunk;

/**
 * Keeps track of what the NMS Chunk handle is of Bukkit chunks.
 * Here because Spigot refuses to fix a bug in the chunk unload event, of the
 * handle not being accessible...
 */
public interface ChunkHandleTracker extends LibraryComponent {
    ChunkHandleTracker INSTANCE = LibraryComponentSelector.forModule(ChunkHandleTracker.class)
            .runFirst(CommonBootstrap::initServer)
            .addWhen("Spigot-1.21-broken",
                    v -> !Common.IS_PAPERSPIGOT_SERVER &&
                            Common.evaluateMCVersion(">=", "1.21") &&
                            Common.evaluateMCVersion("<=", "1.21.1"),
                    ChunkHandleTracker_Spigot_1_21::new)
            .setDefaultComponent(ChunkHandleTracker_Default::new)
            .update();

    /**
     * Starts tracking chunks
     *
     * @param plugin
     */
    void startTracking(CommonPlugin plugin);

    /**
     * Stops tracking chunks
     */
    void stopTracking();

    /**
     * Gets the NMS Handle of a Chunk
     *
     * @param chunk Bukkit Chunk
     * @return NMS Handle
     */
    Object getChunkHandle(Chunk chunk);
}
