package com.bergerkiller.bukkit.common.internal.logic;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;

/**
 * Handles the searching and automatic genration of nether portals
 */
public abstract class PortalHandler implements LazyInitializedObject {
    public static final PortalHandler INSTANCE;
    static {
        if (CommonBootstrap.evaluateMCVersion(">=", "1.14.1")) {
            INSTANCE = new PortalHandler_1_14_1();
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.14")) {
            INSTANCE = new PortalHandler_1_14();
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.9")) {
            INSTANCE = new PortalHandler_1_9();
        } else {
            INSTANCE = new PortalHandler_1_8();
        }
    }

    /**
     * Called during BKCommonLib plugin initialization to initialize internal logic
     * that requires a plugin.
     * 
     * @param plugin
     */
    public abstract void enable(CommonPlugin plugin);

    /**
     * Called during BKCommonLib plugin disabling to disable this logic and cancel
     * any tasks that were started.
     * 
     * @param plugin
     */
    public abstract void disable(CommonPlugin plugin);

    /**
     * Shows the end credits screen to a player, the same way it would
     * if the player entered the end. As part of this, the player
     * will respawn later.
     * 
     * @param player The player to show end credits to
     */
    public abstract void showEndCredits(Player player);

    /**
     * Gets whether a world is the main end world of the server. On this world,
     * by default, credits show when players enter the portal.
     * 
     * @param world The world to check
     * @return True if the world in question is the main end world
     */
    public abstract boolean isMainEndWorld(World world);

    /**
     * Searches for an existing end platform on the world.
     * If the platform is not there in full, then null is returned,
     * prompting {@link #createEndPlatform(World, Entity)} to make a new one.
     * 
     * @param world
     * @return end platform block position found, null if no such platform exists
     */
    public abstract Block findEndPlatform(World world);

    /**
     * Creates the default end platform on the world
     * 
     * @param world The world on which to create the end platform
     * @param initiator The entity that caused this portal to form, can be null
     * @return end platform block position created, null if creation failed
     */
    public abstract Block createEndPlatform(World world, Entity initiator);

    /**
     * On versions of Minecraft where this is needed, marks the block specified
     * as a nether portal block, allowing it to be found again in the future
     * using {@link #findNetherPortal(Block)}.
     * 
     * @param netherPortalBlock Block to mark as a useful nether portal
     */
    public abstract void markNetherPortal(Block netherPortalBlock);

    /**
     * Searches for an existing Portal from startBlock.
     * 
     * @param startBlock Block from which to start searching for portals
     * @param radius The radius to look in the world for portals
     * @return found portal frame Block, null if not found
     */
    public abstract Block findNetherPortal(Block startBlock, int radius);

    /**
     * Creates a new nether portal near startBlock. The entity argument
     * is used to define the orientation, and is passed along the
     * PortalCreateEvent if present. Entity can be null.<br>
     * <br>
     * If no place to create the portal could be found, or the creation was cancelled
     * by a plugin, then <i>null</i> is returned.
     * 
     * @param startBlock Block from which to start looking for a place to build
     * @param orientation BlockFace orientation for the portal, if not n/e/s/w, is random
     * @param initiator The entity that caused this portal to form, can be null
     * @return created portal frame block, null if the portal could not be created
     */
    public abstract Block createNetherPortal(Block startBlock, BlockFace orientation, Entity initiator);
}
