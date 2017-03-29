package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.reflection.net.minecraft.server.NMSPlayerChunk;
import com.bergerkiller.reflection.net.minecraft.server.NMSPlayerChunkMap;

import net.minecraft.server.v1_11_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.PlayerChunkMap;
import net.minecraft.server.v1_11_R1.WorldServer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class PlayerChunkMapBase extends PlayerChunkMap {

    public PlayerChunkMapBase(World world, int viewDistance) {
        super((WorldServer) Conversion.toWorldHandle.convert(world), viewDistance);
    }

    //Removed deprecated method

    /**
     * @deprecated use
     * {@link #containsPlayer(Player, int, int) containsPlayer(player, x, z)}
     * instead
     */
    @Deprecated
    @Override
    public final boolean a(EntityPlayer entityplayer, int x, int z) {
        return this.containsPlayer(Conversion.toPlayer.convert(entityplayer), x, z);
    }

    /**
     * @deprecated use {@link #addChunksToSend(Player)} instead
     */
    @Deprecated
    public final void b(EntityPlayer entityplayer) {
        this.addChunksToSend(Conversion.toPlayer.convert(entityplayer));
    }

    /**
     * @deprecated use {@link #addPlayer(Player)} instead
     */
    @Deprecated
    @Override
    public void addPlayer(EntityPlayer arg0) {
        this.addPlayer(Conversion.toPlayer.convert(arg0));
    }

    /**
     * @deprecated use {@link #movePlayer(Player)} instead
     */
    @Deprecated
    @Override
    public void movePlayer(EntityPlayer arg0) {
        this.movePlayer(Conversion.toPlayer.convert(arg0));
    }

    /**
     * @deprecated use {@link #removePlayer(Player)} instead
     */
    @Deprecated
    @Override
    public void removePlayer(EntityPlayer arg0) {
        removePlayer(Conversion.toPlayer.convert(arg0));
    }

    public Object getPlayerChunk(Object playerChunk) {
        return playerChunk;
    }

    /**
     * Updates player movement
     *
     * @param player to update
     */
    public void movePlayer(Player player) {
        EntityPlayer entityplayer = CommonNMS.getNative(player);
        int i = (int) entityplayer.locX >> 4;
        int j = (int) entityplayer.locZ >> 4;
        double d0 = entityplayer.d - entityplayer.locX;
        double d1 = entityplayer.e - entityplayer.locZ;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 >= 64.0D) {
            int k = (int) entityplayer.d >> 4;
            int l = (int) entityplayer.e >> 4;
            int i1 = NMSPlayerChunkMap.radius.get(this);
            int j1 = i - k;
            int k1 = j - l;
            List<ChunkCoordIntPair> chunksToLoad = new LinkedList<ChunkCoordIntPair>();

            if ((j1 != 0) || (k1 != 0)) {
                for (int l1 = i - i1; l1 <= i + i1; l1++) {
                    for (int i2 = j - i1; i2 <= j + i1; i2++) {
                        if (!NMSPlayerChunkMap.shouldUnload.invoke(this, l1, i2, k, l, i1)) {
                            chunksToLoad.add(new ChunkCoordIntPair(l1, i2));
                        }

                        if (!NMSPlayerChunkMap.shouldUnload.invoke(this, l1 - j1, i2 - k1, i, j, i1)) {
                            Object playerchunk = getPlayerChunk(NMSPlayerChunkMap.getChunk.invoke(this, l1 - j1, i2 - k1, false));

                            if (playerchunk != null) {
                                NMSPlayerChunk.unload.invoke(playerchunk, entityplayer);
                            }
                        }
                    }
                }

                b(entityplayer);
                entityplayer.d = entityplayer.locX;
                entityplayer.e = entityplayer.locZ;

                Collections.sort(chunksToLoad, new ChunkCoordComparator(entityplayer));
                for (ChunkCoordIntPair pair : chunksToLoad) {
                    Object playerchunk = NMSPlayerChunkMap.getChunk.invoke(this, pair.x, pair.z, true);
                    NMSPlayerChunk.load.invoke(playerchunk, entityplayer);
                }

                if ((j1 > 1) || (j1 < -1) || (k1 > 1) || (k1 < -1)) {
                    Collections.sort(chunksToLoad, new ChunkCoordComparator(entityplayer));
                }
            }
        }
    }

    /**
     * Adds a new player
     *
     * @param player to add
     */
    public void addPlayer(Player player) {
        super.addPlayer(CommonNMS.getNative(player));
    }

    /**
     * Removes an existing player
     *
     * @param player to remove
     */
    public void removePlayer(Player player) {
        EntityPlayer entityplayer = CommonNMS.getNative(player);
        int i = (int) entityplayer.d >> 4;
        int j = (int) entityplayer.e >> 4;
        int radius = NMSPlayerChunkMap.radius.get(this);

        for (int k = i - radius; k <= i + radius; k++) {
            for (int l = j - radius; l <= j + radius; l++) {
                Object playerchunk = getPlayerChunk(NMSPlayerChunkMap.getChunk.invoke(this, k, l, false));

                if (playerchunk != null) {
                    NMSPlayerChunk.unload.invoke(playerchunk, entityplayer);
                }
            }
        }

        NMSPlayerChunkMap.managedPlayers.get(this).remove(entityplayer);
    }

    /**
     * Adds all chunks near a player to the chunk sending queue of a player
     *
     * @param player to add the chunks to send to
     */
    public void addChunksToSend(Player player) {
        super.addPlayer(CommonNMS.getNative(player));
    }

    /**
     * Gets whether a player is registered for a Chunk. If this is the case, the
     * player is liable for entity or block updates from entities or blocks in
     * the chunk.
     *
     * @param player to check
     * @param chunkX of the Chunk
     * @param chunkZ of the Chunk
     * @return True if the player is contained, False if not
     */
    public boolean containsPlayer(Player player, int chunkX, int chunkZ) {
        return super.a(CommonNMS.getNative(player), chunkX, chunkZ);
    }

    /**
     * Gets the world from this PlayerManager<br>
     * Is called by the PlayerChunkInstance initializer as well
     *
     * @return WorldServer
     */
    public WorldServer getWorld() {
        return super.getWorld();
    }

    /**
     * This is nuts bro o,o
     */
    private static class ChunkCoordComparator implements
            Comparator<ChunkCoordIntPair> {

        private int x;
        private int z;

        public ChunkCoordComparator(EntityPlayer entityplayer) {
            this.x = ((int) entityplayer.locX >> 4);
            this.z = ((int) entityplayer.locZ >> 4);
        }

        public int compare(ChunkCoordIntPair a, ChunkCoordIntPair b) {
            if (a.equals(b)) {
                return 0;
            }

            int ax = a.x - this.x;
            int az = a.z - this.z;
            int bx = b.x - this.x;
            int bz = b.z - this.z;

            int result = (ax - bx) * (ax + bx) + (az - bz) * (az + bz);
            if (result != 0) {
                return result;
            }

            if (ax < 0) {
                if (bx < 0) {
                    return bz - az;
                }
                return -1;
            }

            if (bx < 0) {
                return 1;
            }
            return az - bz;
        }
    }
}
