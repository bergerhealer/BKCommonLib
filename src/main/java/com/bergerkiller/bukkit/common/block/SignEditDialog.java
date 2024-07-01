package com.bergerkiller.bukkit.common.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutBlockChangeHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.TileEntityHandle;

/**
 * Initiates the display of a sign edit dialog the player can type into, and handles
 * the closing of this dialog by the player to confirm the edit.
 */
public abstract class SignEditDialog {
    private static Handler handler = null;
    private Material signType = MaterialUtil.getFirst("OAK_WALL_SIGN", "LEGACY_WALL_SIGN");

    /**
     * Called when this edit dialog is opened using {@link #open(Player)}
     *
     * @param player Player for which it was opened
     * @param lines Lines on the sign at the time of opening
     */
    public void onOpened(Player player, String[] lines) {}

    /**
     * Called when the player initiates closing of the dialog with lines
     * of text having been inputed.
     *
     * @param player Player that closed the dialog
     * @param lines Lines on the sign at the time of closing
     */
    public abstract void onClosed(Player player, String[] lines);

    /**
     * Called when the player initiates closing of the dialog without
     * having inputed any lines of text. By default calls {@link #onAborted(Player)}
     *
     * @param player Player that closed the dialog
     */
    public void onClosedWithoutLines(Player player) {
        this.onAborted(player);
    }

    /**
     * Called when this edit dialog is forcibly closed without the player
     * having been able to input lines of text
     *
     * @param player Player for which this dialog was aborted
     */
    public void onAborted(Player player) {}

    /**
     * Gets the Material type of sign for which an edit dialog is shown
     *
     * @return Sign Material Type
     */
    public final Material getSignType() {
        return signType;
    }

    /**
     * Sets a new Material type of sign for which an edit dialog will be shown
     *
     * @param signType Sign Material Type
     * @return this dialog
     */
    public final SignEditDialog setSignType(Material signType) {
        this.signType = signType;
        return this;
    }

    /**
     * Opens a new sign editing dialog for the player specified. Initial contents
     * will be blank.
     * 
     * @param player Player for which to open the dialog
     * @return Whether opening was successful. If false, BKCommonLib is disabled or
     *         the player is already viewing a sign edit dialog
     */
    public final boolean open(Player player) {
        return open(player, new String[] { "", "", "", "" });
    }

    /**
     * Opens a new sign editing dialog for the player specified.
     * 
     * @param player Player for which to open the dialog
     * @param initialLines Initial lines of text to show on the opened dialog
     * @return Whether opening was successful. If false, BKCommonLib is disabled or
     *         the player is already viewing a sign edit dialog
     */
    public final boolean open(Player player, String[] initialLines) {
        if (!CommonPlugin.hasInstance()) {
            return false;
        }
        Handler handler = SignEditDialog.handler;
        if (handler != null && handler.getMetadata(player) != null) {
            return false;
        }

        // Try to find the most suitable block to put down a temporary sign for editing
        // Ideally this is an air block that has a low impact if changed
        // If we can't, we'll change some other type of block, which might have unwanted side-effects
        // Checks at most ~100 blocks behind the player FOV
        Location eyeLocation = player.getEyeLocation();
        BlockFace lookDirection = FaceUtil.vectorToBlockFace(eyeLocation.getDirection(), false);
        BlockFace backDirection = FaceUtil.yawToFace(eyeLocation.getYaw() - 90.0f, false).getOppositeFace();
        Block signBlock = eyeLocation.getBlock().getRelative(backDirection);
        if (lookDirection != BlockFace.DOWN) {
            signBlock = signBlock.getRelative(BlockFace.DOWN);
        }
        {
            Block curr = signBlock;
            for (int k = 0; k < 10; k++) {
                Block airBlock = findAirBlockAbove(curr);
                if (airBlock != null) {
                    signBlock = airBlock;
                    break;
                }
                curr = curr.getRelative(backDirection);
            }
        }

        PlayerMetadata metadata = new PlayerMetadata(this, player, signBlock);
        if (handler == null) {
            Handler h = new Handler();
            h.players = Collections.singletonMap(player, metadata);
            h.enable();
            SignEditDialog.handler = h;
        } else {
            synchronized (handler) {
                if (handler.players.containsKey(player)) {
                    return false; // wtf
                }

                Map<Player, PlayerMetadata> newPlayers = new HashMap<Player, PlayerMetadata>(handler.players);
                newPlayers.put(player, metadata);
                handler.players = newPlayers;
            }
        }

        this.handleOpen(metadata, backDirection, initialLines);

        return true;
    }

    private static Block findAirBlockAbove(Block startBlock) {
        for (int n = 0; n < 10; n++) {
            if (MaterialUtil.ISAIR.get(startBlock)) {
                return startBlock;
            } else {
                startBlock = startBlock.getRelative(BlockFace.UP);
            }
        }
        return null;
    }

    private final void handleOpen(PlayerMetadata metadata, BlockFace backDirection, String[] lines) {
        EntityPlayerHandle entityPlayer = EntityPlayerHandle.fromBukkit(metadata.player);
        IntVector3 coordinates = IntVector3.coordinatesOf(metadata.signBlock);

        // Send a block change packet for a sign at the sign coordinates
        metadata.sendBlock(BlockData.fromMaterial(signType).setState("facing", backDirection));

        // Update the lines of text on this sign client-sided
        metadata.player.sendSignChange(metadata.signBlock.getLocation(), lines);

        // Open the dialog
        entityPlayer.openSignEditWindow(coordinates);

        this.onOpened(metadata.player, lines);
    }

    /**
     * Forces the client to close the dialog and fires the onAborted event
     *
     * @param metadata
     */
    private final void handleAbort(PlayerMetadata metadata) {
        EntityPlayerHandle.fromBukkit(metadata.player).closeSignEditWindow();

        // Restore the original block
        metadata.sendOriginalBlock();

        this.onAborted(metadata.player);
    }

    /**
     * Called on the main thread when the player completes the dialog
     *
     * @param metadata
     * @param lines
     */
    private final void handleClosed(PlayerMetadata metadata, String[] lines) {
        boolean isAllEmpty = true;
        for (String line : lines) {
            if (!line.isEmpty()) {
                isAllEmpty = false;
                break;
            }
        }

        // Restore the original block
        metadata.sendOriginalBlock();

        if (isAllEmpty) {
            this.onClosedWithoutLines(metadata.player);
        } else {
            this.onClosed(metadata.player, lines);
        }
    }

    /**
     * Forcibly closes this sign edit dialog for all players it was previously
     * opened for using {@link #open(Player)}
     */
    public final void abort() {
        abort(m -> m.dialog == SignEditDialog.this);
    }

    /**
     * Forcibly closes this sign edit dialog for a single player. If it was
     * not previously opened for this player, does nothing.
     *
     * @param player
     */
    public final void abort(Player player) {
        abort(m -> m.dialog == SignEditDialog.this && m.player == player);
    }

    private static void abort(Predicate<PlayerMetadata> filter) {
        Handler handler = SignEditDialog.handler;
        if (handler != null) {
            List<PlayerMetadata> removed = new ArrayList<>();
            synchronized (handler) {
                Map<Player, PlayerMetadata> newPlayers = new HashMap<>(handler.players);
                Iterator<PlayerMetadata> iter = newPlayers.values().iterator();
                while (iter.hasNext()) {
                    PlayerMetadata meta = iter.next();
                    if (filter.test(meta)) {
                        removed.add(meta);
                        iter.remove();
                    }
                }
                handler.players = newPlayers;
            }

            removed.forEach(m -> m.dialog.handleAbort(m));

            handler.disableIfNoPlayers();
        }
    }

    private static class PlayerMetadata {
        public final SignEditDialog dialog;
        public final Player player;
        public final Block signBlock;
        public final IntVector3 coordinates;

        public PlayerMetadata(SignEditDialog dialog, Player player, Block signBlock) {
            this.dialog = dialog;
            this.player = player;
            this.signBlock = signBlock;
            this.coordinates = IntVector3.coordinatesOf(signBlock);
        }

        public void sendOriginalBlock() {
            // Send the original Block information. If it was a tile entity, also update its NBT
            sendBlock(WorldUtil.getBlockData(signBlock));
            TileEntityHandle tileEntity = WorldHandle.fromBukkit(signBlock.getWorld())
                    .getTileEntity(coordinates);
            if (tileEntity != null) {
                CommonPacket packet = tileEntity.getUpdatePacket();
                if (packet != null) {
                    PacketUtil.sendPacket(player, packet);
                }
            }
        }

        public void sendBlock(BlockData blockData) {
            PacketUtil.sendPacket(player, PacketPlayOutBlockChangeHandle.createNew(coordinates, blockData));
        }
    }

    private static class Handler implements Listener, PacketListener {
        private Map<Player, PlayerMetadata> players = Collections.emptyMap();

        public void disableIfNoPlayers() {
            if (players.isEmpty() && this == SignEditDialog.handler) {
                if (CommonPlugin.hasInstance()) {
                    this.disable();
                }
                SignEditDialog.handler = null;
            }
        }

        public void disable() {
            CommonPlugin.getInstance().unregister((PacketListener) this);
            CommonUtil.unregisterListener((Listener) this);
        }

        public void enable() {
            CommonPlugin.getInstance().register((PacketListener) this, PacketType.IN_UPDATE_SIGN);
            CommonPlugin.getInstance().register((Listener) this);
        }

        public PlayerMetadata getMetadata(Player player) {
            return getOrRemoveMetadata(player, false);
        }

        public PlayerMetadata removeMetadata(Player player) {
            return getOrRemoveMetadata(player, true);
        }

        private PlayerMetadata getOrRemoveMetadata(Player player, boolean remove) {
            PlayerMetadata metadata = players.get(player);
            if (metadata == null) {
                return null;
            }

            synchronized (this) {
                metadata = players.get(player);
                if (metadata == null) {
                    return null;
                }

                if (remove) {
                    Map<Player, PlayerMetadata> newPlayers = new HashMap<>(players);
                    newPlayers.remove(player);
                    players = newPlayers;
                }

                return metadata;
            }
        }

        @Override
        public void onPacketReceive(PacketReceiveEvent event) {
            if (event.getType() == PacketType.IN_UPDATE_SIGN) {
                // Find metadata of editing a sign
                final PlayerMetadata metadata = removeMetadata(event.getPlayer());
                if (metadata == null) {
                    return;
                }

                // At this point we always abort editing. Just don't know yet how it's handled.
                IntVector3 position = event.getPacket().read(PacketType.IN_UPDATE_SIGN.position);
                if (position == null || !metadata.coordinates.equals(position)) {
                    // Unexpected state - abort. Must do on main thread.
                    CommonUtil.nextTick(() -> {
                        metadata.dialog.handleAbort(metadata);
                        disableIfNoPlayers();
                    });
                    return;
                }

                event.setCancelled(true); // Don't let the server handle it/cancel editing

                ChatText[] lines_chattext = event.getPacket().read(PacketType.IN_UPDATE_SIGN.lines);
                if (lines_chattext == null || lines_chattext.length != 4) {
                    CommonUtil.nextTick(() -> {
                        metadata.dialog.handleAbort(metadata);
                        disableIfNoPlayers();
                    });
                    return;
                }

                final String[] lines = new String[lines_chattext.length];
                for (int i = 0; i < lines.length; i++) {
                    ChatText ct = lines_chattext[i];
                    lines[i] = (ct == null) ? "" : ct.getMessage();
                }

                CommonUtil.nextTick(() -> {
                    metadata.dialog.handleClosed(metadata, lines);
                    disableIfNoPlayers();
                });
            }
        }

        @Override
        public void onPacketSend(PacketSendEvent event) {
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerQuit(PlayerQuitEvent event) {
            PlayerMetadata metadata = removeMetadata(event.getPlayer());
            if (metadata != null) {
                metadata.dialog.onAborted(metadata.player);
                disableIfNoPlayers();
            }
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerTeleport(PlayerTeleportEvent event) {
            PlayerMetadata metadata = removeMetadata(event.getPlayer());
            if (metadata != null) {
                metadata.dialog.handleAbort(metadata);
                disableIfNoPlayers();
            }
        }
    }
}
