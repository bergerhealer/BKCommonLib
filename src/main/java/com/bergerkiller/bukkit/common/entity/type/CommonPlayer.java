package com.bergerkiller.bukkit.common.entity.type;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.EntityPlayerHandle;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Collection;
import java.util.Iterator;

/**
 * A Common Entity implementation for Players
 */
public class CommonPlayer extends CommonLivingEntity<Player> {

    public CommonPlayer(Player entity) {
        super(entity);
    }

    public String getName() {
        return entity.getName();
    }

    public String getCustomName() {
        return entity.getCustomName();
    }

    public void setCustomName(String customName) {
        entity.setCustomName(customName);
    }

    public boolean isCustomNameVisible() {
        return entity.isCustomNameVisible();
    }

    public void setCustomNameVisible(boolean visible) {
        entity.setCustomNameVisible(visible);
    }

    @Override
    public boolean teleport(Location location, TeleportCause cause) {
        // First, perform the default teleport logic
        if (!super.teleport(location, cause)) {
            return false;
        }

        // Properly move the player to the new location (changed chunks?)
        // This was causing strange in-between world bugs
        // And since without it it works fine too...it is going to be disabled for a while
        // I added this for some reason, if this reason is found, please look into a way of fixing the bugs
        //CommonNMS.getNative(getWorld()).getPlayerChunkMap().movePlayer(getHandle(EntityPlayer.class));

        // Broken as all heck!!!
        // It now uses PlayerChunkMap and PlayerChunk instances to manage the sending of chunks
        // This is completely incompatible with a "chunk send queue" as was originally implemented

        // Instantly send the chunk the vehicle is currently in
        // This avoid the player losing track of the vehicle because the chunk is missing
        //final IntVector2 chunk = loc.xz.chunk();
        //if (getChunkSendQueue().remove(chunk)) {
        //    PacketUtil.sendChunk(getEntity(), getWorld().getChunkAt(chunk.x, chunk.z));
        //}

        // Tell all other entities to send spawn packets
        WorldUtil.getTracker(getWorld()).updateViewer(entity);
        return true;
    }

    /**
     * Sends a packet to this player
     *
     * @param packet to send
     */
    public void sendPacket(CommonPacket packet) {
        PacketUtil.sendPacket(entity, packet);
    }

    /**
     * Sends a packet to this player
     *
     * @param packet to send
     * @param throughListeners option: True to allow modification, False not
     */
    public void sendPacket(CommonPacket packet, boolean throughListeners) {
        PacketUtil.sendPacket(entity, packet, throughListeners);
    }

    /**
     * Clears the entire entity removal queue and sends entity destroy packets
     * to all nearby player viewers
     */
    public void flushEntityRemoveQueue() {
        final Collection<Integer> ids = getEntityRemoveQueue();
        if (ids.isEmpty()) {
            return;
        }
        // Take care of more than 127 entities (multiple packets)
        while (ids.size() >= 128) {
            final int[] rawIds = new int[127];
            Iterator<Integer> iter = ids.iterator();
            for (int i = 0; i < rawIds.length; i++) {
                rawIds[i] = iter.next().intValue();
                iter.remove();
            }
            sendPacket(PacketType.OUT_ENTITY_DESTROY.newInstance(rawIds));
        }
        // Remove any remaining entities
        sendPacket(PacketType.OUT_ENTITY_DESTROY.newInstance(ids));
        ids.clear();
    }

    /**
     * Gets a list of Entity Ids that are pending for removal (destroy) packets
     *
     * @return list of entity ids to send destroy packets for
     */
    public Collection<Integer> getEntityRemoveQueue() {
        return PlayerUtil.getEntityRemoveQueue(entity);
    }

    /**
     * Gets the block location of the respawn point for the player.
     * If none is available, null is returned instead.
     * 
     * @return spawn point coordinates, or null if none are available
     */
    public Block getSpawnPoint() {
        EntityPlayerHandle handle = EntityPlayerHandle.createHandle(this.getHandle());
        World world = handle.getSpawnWorld();
        IntVector3 coords = handle.getSpawnCoord();
        if (world != null && coords != null) {
            return coords.toBlock(world);
        } else {
            return null;
        }
    }

    /**
     * Sets the block location of the respawn point for the human entity.
     * To clear the respawn point and set it to 'none', set it to null.
     * 
     * @param spawnPoint to set to
     */
    public void setSpawnPoint(Block spawnPoint) {
        EntityPlayerHandle handle = EntityPlayerHandle.createHandle(this.getHandle());
        if (spawnPoint == null) {
            handle.setSpawnWorld(null);
            handle.setSpawnCoord(null);
        } else {
            handle.setSpawnWorld(spawnPoint.getWorld());
            handle.setSpawnCoord(new IntVector3(spawnPoint));
        }
    }
}
