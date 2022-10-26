package com.bergerkiller.bukkit.common.block;

import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.TileEntitySignHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockHandle;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;

/**
 * Efficiently detects when the text contents of a Sign change, when the
 * sign's backing block entity unloads and re-loads, and when the sign
 * block is destroyed in the world.
 *
 * Detecting will actively load the chunk the sign is in.
 */
public class SignChangeTracker implements Cloneable {
    private final Block block;
    private Sign state;
    private BlockData blockData;
    private TileEntitySignHandle tileEntity;
    private Object[] lastRawLines;

    // Need to swap what implementation we're using for certain server types
    private static final Function<Block, SignChangeTracker> constructor;
    static {
        Function<Block, SignChangeTracker> constr = SignChangeTracker::new; // Default
        if (Common.evaluateMCVersion("<=", "1.12.2") && Common.SERVER.isForgeServer()) {
            try {
                final FastField<List<Object>> tileEntityListField = new FastField<>();
                tileEntityListField.init(Resolver.resolveAndGetDeclaredField(WorldHandle.T.getType(), "tileEntityList"));
                constr = block -> {
                    List<Object> worldTileEntities = tileEntityListField.get(HandleConversion.toWorldHandle(block.getWorld()));
                    return new SignChangeTrackerMohistLegacy(block, worldTileEntities);
                };
            } catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "[Mohist Compat] Failed to find World tileEntityList field", t);
            }
        }
        constructor = constr;
    }

    protected SignChangeTracker(Block block) {
        this.block = block;
        this.state = null;
        this.tileEntity = null;
        this.lastRawLines = null;
    }

    private void initState(Sign state) {
        this.state = state;
        this.loadTileEntity(TileEntitySignHandle.fromBukkit(state));
    }

    private void loadTileEntity(TileEntitySignHandle tile) {
        if (tile == null) {
            this.tileEntity = null;
            this.lastRawLines = null;
            this.blockData = null;
        } else {
            this.tileEntity = tile;
            this.lastRawLines = tile.getRawLines().clone();

            // Note: it is possible for a TileEntity to be retrieved while it's added to a Chunk,
            // but not yet to a World. Especially on 1.12.2 and before. For that reason, we got to
            // check whether the World was assigned to the tile entity. If not, we cannot use the tile
            // entity's property method, as it throws a NPE.
            this.blockData = this.checkRemoved(tile) ? WorldUtil.getBlockData(this.block) : tile.getBlockData();
        }
    }

    /**
     * Gets the World the Sign is on
     *
     * @return World
     */
    public World getWorld() {
        return this.block.getWorld();
    }

    /**
     * Gets the X-coordinate where the Sign is located
     *
     * @return Sign location X-coordinate
     */
    public int getX() {
        return this.block.getX();
    }

    /**
     * Gets the Y-coordinate where the Sign is located
     *
     * @return Sign location Y-coordinate
     */
    public int getY() {
        return this.block.getY();
    }

    /**
     * Gets the Z-coordinate where the Sign is located
     *
     * @return Sign location Z-coordinate
     */
    public int getZ() {
        return this.block.getZ();
    }

    /**
     * Gets the BlockFace the Sign is attached to
     *
     * @return Sign attached face
     */
    public BlockFace getAttachedFace() {
        try {
            return this.blockData.getAttachedFace();
        } catch (NullPointerException ex) {
            if (this.isRemoved()) {
                throw new IllegalStateException("Sign is removed");
            } else {
                throw ex;
            }
        }
    }

    /**
     * Gets the BlockFace the Sign faces. For sign posts, this is the rotation.
     * For wall signs, it is the opposite of {@link #getAttachedFace()}.
     *
     * @return Sign facing
     */
    public BlockFace getFacing() {
        try {
            return this.blockData.getFacingDirection();
        } catch (NullPointerException ex) {
            if (this.isRemoved()) {
                throw new IllegalStateException("Sign is removed");
            } else {
                throw ex;
            }
        }
    }

    /**
     * Gets whether the Sign is attached to a particular Block
     *
     * @param block Block to check
     * @return True if the Sign is attached to the Block
     * @see #getAttachedFace()
     */
    public boolean isAttachedTo(Block block) {
        Block signBlock = this.getBlock();
        BlockFace attachedFace = this.getAttachedFace();
        return (block.getX() - signBlock.getX()) == attachedFace.getModX() &&
               (block.getY() - signBlock.getY()) == attachedFace.getModY() &&
               (block.getZ() - signBlock.getZ()) == attachedFace.getModZ();
    }

    /**
     * Returns true if the sign was broken/removed from the World. If true, then
     * {@link #getSign()} will return null
     *
     * @return True if removed
     */
    public boolean isRemoved() {
        return this.state == null;
    }

    /**
     * Gets the Block where the sign is at
     *
     * @return Sign Block
     */
    public Block getBlock() {
        return this.block;
    }

    /**
     * Gets the BlockData of the sign. Returns null if {@link #isRemoved()}.
     * Only {@link #update()} updates the return value of this method.
     *
     * @return Sign Block Data
     */
    public BlockData getBlockData() {
        return this.blockData;
    }

    /**
     * Gets a live-updated Sign instance.
     * Only {@link #update()} updates the return value of this method.
     *
     * @return Sign
     */
    public Sign getSign() {
        return this.state;
    }

    /**
     * Checks the sign state to see if changes have occurred. If the sign Block Entity
     * reloaded, or the sign text contents changed, true is returned. If there were
     * no changes then false is returned.<br>
     * <br>
     * If the sign was removed entirely, then true is also returned, which should be
     * checked with {@link #isRemoved()}. When removed, the {@link #getSign()} will
     * return null.
     *
     * @return True if changes occurred, or the sign was removed
     */
    public boolean update() {
        TileEntitySignHandle tileEntity = this.tileEntity;

        // Check world to see if a tile entity now exists at this Block.
        // Reading tile entities is slow, so avoid doing that if we can.
        if (tileEntity == null) {
            return tryLoadFromWorld(); // If found, initializes and returns true
        }

        // Ask the TileEntity we already have whether it was removed from the World
        // If it was, we must re-set and re-check for the sign.
        if (checkRemoved(tileEntity)) {
            if (tryLoadFromWorld()) {
                return true; // Backing TileEntity instance changed, so probably changed
            } else {
                this.state = null;
                this.tileEntity = null;
                this.blockData = null;
                this.lastRawLines = null;
                return true; // Sign is gone
            }
        }

        // Check when BlockData changes. This is when a sign is rotated, or changed from wall sign to sign post
        boolean blockDataChanged = false;
        {
            Object newBlockDataRaw = tileEntity.getRawBlockData();
            if (newBlockDataRaw != this.blockData.getData()) {
                this.blockData = BlockData.fromBlockData(newBlockDataRaw);
                blockDataChanged = true;
            }
        }

        // Check for sign lines that change. For this, we check the internal IChatBaseComponent contents
        return detectChangedLines(tileEntity) || blockDataChanged;
    }

    protected boolean checkRemoved(TileEntitySignHandle tileEntity) {
        return tileEntity.isRemoved();
    }

    private boolean detectChangedLines(TileEntitySignHandle tileEntity) {
        Object[] oldRawLines = this.lastRawLines;
        Object[] newRawLines = tileEntity.getRawLines();
        int numLines = newRawLines.length;
        if (oldRawLines.length != numLines) {
            this.lastRawLines = newRawLines.clone();
            return true; // Never happens, really
        } else {
            int line = 0;
            while (line < numLines) {
                Object newLine = newRawLines[line];
                if (oldRawLines[line] != newLine) {
                    oldRawLines[line] = newLine;

                    // Detected a change. Re-create the Sign state with the updated lines,
                    // and return true to indicate the change.
                    while (++line < numLines) {
                        oldRawLines[line] = newRawLines[line];
                    }
                    this.state = this.tileEntity.toBukkit();
                    return true;
                }

                line++;
            }
            return false;
        }
    }

    private boolean tryLoadFromWorld() {
        Block block = this.block;
        Object rawTileEntity;
        if (MaterialUtil.ISSIGN.get(block) && // Initiates sync chunk load if needed
            (rawTileEntity = CraftBlockHandle.getBlockTileEntity(block)) != null &&
            TileEntitySignHandle.T.isAssignableFrom(rawTileEntity)
        ) {
            TileEntitySignHandle tileEntity = TileEntitySignHandle.createHandle(rawTileEntity);
            this.state = tileEntity.toBukkit();
            this.loadTileEntity(tileEntity);
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "SignChangeTracker{block=" + this.block + ", sign=" + this.getSign() + "}";
    }

    @Override
    public SignChangeTracker clone() {
        SignChangeTracker clone = new SignChangeTracker(this.block);
        clone.state = this.state;
        clone.blockData = this.blockData;
        clone.tileEntity = this.tileEntity;
        clone.lastRawLines = this.lastRawLines;
        return clone;
    }

    /**
     * Tracks the changes done to a Sign
     *
     * @param sign The sign to track
     * @return Sign change tracker
     */
    public static SignChangeTracker track(Sign sign) {
        if (sign == null) {
            throw new IllegalArgumentException("Sign is null");
        } else {
            SignChangeTracker tracker = constructor.apply(sign.getBlock());
            tracker.initState(sign);
            return tracker;
        }
    }

    /**
     * Tracks the changes done to a Sign
     *
     * @param sign The sign to track
     * @return Sign change tracker
     */
    public static SignChangeTracker track(Block signBlock) {
        SignChangeTracker tracker = constructor.apply(signBlock);
        Sign state = BlockUtil.getSign(signBlock);
        if (state != null) {
            tracker.initState(state);
        }
        return tracker;
    }

    /**
     * Some old 1.12.2 versions of the server had a bug that TileEntity isRemoved() didn't work at all.
     * This tracker checks the index at which a TileEntity is stored in a List, and checks that it
     * has been removed from it that way.
     */
    private static class SignChangeTrackerMohistLegacy extends SignChangeTracker {
        private final List<Object> worldTileEntities;
        private int lastIndex = -1;
 
        protected SignChangeTrackerMohistLegacy(Block block, List<Object> worldTileEntities) {
            super(block);
            this.worldTileEntities = worldTileEntities;
        }

        @Override
        protected boolean checkRemoved(TileEntitySignHandle tileEntity) {
            List<Object> list = this.worldTileEntities;
            Object rawTileEntity = tileEntity.getRaw();
            if (lastIndex >= 0 && lastIndex < list.size() && list.get(lastIndex) == rawTileEntity) {
                return false;
            }
            lastIndex = list.indexOf(rawTileEntity);
            return lastIndex == -1;
        }
    }
}
