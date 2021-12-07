package com.bergerkiller.bukkit.common.internal.logic;

import java.util.concurrent.atomic.AtomicReference;

import com.bergerkiller.bukkit.common.bases.DeferredSupplier;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.bukkit.common.wrappers.BlockStateChange;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutUpdateSignHandle;

/**
 * Handles sign changes using the UPDATE_SIGN packet. No other block state updates
 * are listened for.
 */
class BlockStateChangePacketHandler_1_8_to_1_9_2 extends BlockStateChangePacketHandler {
    private static final String[] LINE_META_KEYS = new String[] { "Text1", "Text2", "Text3", "Text4" };

    @Override
    public void enable() {
        register(PacketType.OUT_UPDATE_SIGN, (player, commonPacket, listener) -> {
            final PacketPlayOutUpdateSignHandle packet = PacketPlayOutUpdateSignHandle.createHandle(commonPacket.getHandle());

            // Stores decoded String lines that it was before, for easier comparison
            // Only set if the deferred metadata supplier is ever called
            final AtomicReference<String[]> linesBeforeRef = new AtomicReference<>();

            // Initialize the metadata once called
            final DeferredSupplier<CommonTagCompound> metadataSupplier = DeferredSupplier.of(() -> {
                ChatText[] lines = packet.getLines();
                CommonTagCompound metadata = new CommonTagCompound();
                String[] linesBefore = new String[4];
                for (int n = 0; n < 4; n++) {
                    metadata.putValue(LINE_META_KEYS[n], linesBefore[n] = lines[n].getJson());
                }
                linesBeforeRef.set(linesBefore);
                return metadata;
            });

            // Errors are handled upstream
            if (!listener.onBlockChange(player, BlockStateChange.deferred(
                   packet.getPosition(), BlockStateType.SIGN, metadataSupplier, () -> true)))
            {
                return false;
            }

            // Only if getMetadata() was ever even called
            if (metadataSupplier.isInitialized()) {
                // Check if the lines in metadata changed compared to the lines in the packet
                // If they are, write them all back (re-serialize them as chat components)
                // Don't touch lines that weren't modified to retain any original json formatting
                CommonTagCompound metadata = metadataSupplier.get();
                String[] linesBefore = linesBeforeRef.get();
                ChatText[] newLines = packet.getLines().clone();
                boolean changed = false;
                for (int n = 0; n < 4; n++) {
                    String newLine = metadata.getValue(LINE_META_KEYS[n], "");
                    if (!linesBefore[n].equals(newLine)) {
                        newLines[n] = ChatText.fromJson(newLine);
                        changed = true;
                    }
                }
                if (changed) {
                    packet.setLines(newLines);
                }
            }

            return true;
        });
    }
}
