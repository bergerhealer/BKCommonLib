package com.bergerkiller.bukkit.common.internal.logic;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.RunOnceTask;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.generated.net.minecraft.server.EntityPlayerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Handler for Minecraft 1.8 - 1.8.8. There are no portal create events being used,
 * and the portal travel agent has simple methods to find and create portals at
 * coordinates. On these versions there are also methods to create end platforms.
 * Can't use the 'isViewingCredits' trick used on 1.9 because the server doesn't update
 * this property in time when the player exits the credits screen. Instead, we use
 * a tick-delayed task to check for portal enter events, and cancel them appropriately
 * if fired while the player is viewing credits.
 */
public class PortalHandler_1_8 extends PortalHandler {
    private final PortalTravelAgentHandle _pta = Template.Class.create(PortalTravelAgentHandle.class, Common.TEMPLATE_RESOLVER);
    private Set<Player> _ignorePortalEventPlayers = new HashSet<>();
    private RunOnceTask _ignorePortalEventPlayersCleanup;

    public PortalHandler_1_8() {
    }

    @Override
    public void enable(CommonPlugin plugin) {
        // Cleans up players ignored for a single tick. If something broke, this
        // prevents a memory leak.
        _ignorePortalEventPlayersCleanup = RunOnceTask.create(plugin, () -> _ignorePortalEventPlayers.clear());

        // Listener to disable player portal events pre-emptively while players are viewing credits
        // This is required, otherwise other plugins get very confused and teleport players out of the
        // credits screen.
        plugin.register(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR)
            public void onEntityPortalEnter(EntityPortalEnterEvent event) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();
                    if (EntityPlayerHandle.fromBukkit(player).isViewingCredits()) {
                        _ignorePortalEventPlayers.add(player);
                        _ignorePortalEventPlayersCleanup.start();
                    }
                }
            }

            @EventHandler(priority = EventPriority.LOWEST)
            public void onPortalEvent(PlayerPortalEvent event) {
                if (_ignorePortalEventPlayers.remove(event.getPlayer())) {
                    event.setCancelled(true);
                }
            }
        });
    }

    @Override
    public void disable(CommonPlugin plugin) {
        _ignorePortalEventPlayersCleanup.runNowIfScheduled();
    }

    @Override
    public void forceInitialization() {
        _pta.forceInitialization();
    }

    @Override
    public Block findNetherPortal(Block startBlock, int radius) {
        return _pta.findNetherPortal(startBlock, radius);
    }

    @Override
    public Block createNetherPortal(Block startBlock, BlockFace orientation, Entity initiator) {
        if (_pta.createNetherPortal(startBlock, 16)) {
            return _pta.findNetherPortal(startBlock, 16);
        } else {
            return null;
        }
    }

    @Override
    public void markNetherPortal(Block netherPortalBlock) {
        // No portals are stored on 1.14 and before
    }

    @Override
    public Block findEndPlatform(World world) {
        return _pta.findOrCreateEndPlatform(world, false);
    }

    @Override
    public Block createEndPlatform(World world, Entity initiator) {
        return _pta.findOrCreateEndPlatform(world, true);
    }

    @Override
    public boolean isMainEndWorld(World world) {
        return _pta.isMainEndWorld(world);
    }

    @Override
    public void showEndCredits(Player player) {
        _ignorePortalEventPlayers.add(player);
        _ignorePortalEventPlayersCleanup.start();
        EntityPlayerHandle ep = EntityPlayerHandle.fromBukkit(player);
        _pta.showEndCredits(HandleConversion.toEntityHandle(player), ep.hasSeenCredits());
        ep.setHasSeenCredits(true);
    }

    @Template.Optional
    @Template.InstanceType("net.minecraft.server.PortalTravelAgent")
    public static abstract class PortalTravelAgentHandle extends Template.Class<Template.Handle> {

        /* 
         * <SHOW_END_CREDITS>
         * public static void showEndCredits(Object entityPlayerRaw, boolean seenCredits) {
         *     EntityPlayer player = (EntityPlayer) entityPlayerRaw;
         *     player.world.kill(player);
         *     if (!player.viewingCredits) {
         *         player.viewingCredits = true;
         *         player.playerConnection.sendPacket(new PacketPlayOutGameStateChange(4, seenCredits ? 0.0F : 1.0F));
         *     }
         * }
         */
        @Template.Generated("%SHOW_END_CREDITS%")
        public abstract void showEndCredits(Object entityPlayerRaw, boolean seenCredits);

        /*
         * <IS_MAIN_END_WORLD>
         * public static boolean isMainEndWorld(org.bukkit.World world) {
         *     WorldServer world = ((org.bukkit.craftbukkit.CraftWorld) world).getHandle();
         *     return world.dimension == 1;
         * }
         */
        @Template.Generated("%IS_MAIN_END_WORLD%")
        public abstract boolean isMainEndWorld(World world);

        /*
         * <FIND_NETHER_PORTAL>
         * public static org.bukkit.block.Block findNetherPortal(org.bukkit.block.Block startBlock, int createRadius) {
         *     WorldServer world = ((org.bukkit.craftbukkit.CraftWorld) startBlock.getWorld()).getHandle();
         *     BlockPosition blockposition = new BlockPosition(startBlock.getX(), startBlock.getY(), startBlock.getZ());
         *     PortalTravelAgent agent = new PortalTravelAgent(world);
         *     BlockPosition result = agent.findPortal((double) startBlock.getX(),
         *                                             (double) startBlock.getY(),
         *                                             (double) startBlock.getZ(),
         *                                             createRadius);
         *     if (result == null) {
         *         return null;
         *     }
         *     return startBlock.getWorld().getBlockAt(result.getX(), result.getY(), result.getZ());
         * }
         */
        @Template.Generated("%FIND_NETHER_PORTAL%")
        public abstract Block findNetherPortal(Block startBlock, int createRadius);

        /*
         * <CREATE_NETHER_PORTAL>
         * public static boolean createNetherPortal(org.bukkit.block.Block startBlock, int createRadius) {
         *     WorldServer world = ((org.bukkit.craftbukkit.CraftWorld) startBlock.getWorld()).getHandle();
         *     PortalTravelAgent agent = new PortalTravelAgent(world);
         *     return agent.createPortal((double) startBlock.getX() + 0.5,
         *                               (double) startBlock.getY(),
         *                               (double) startBlock.getZ() + 0.5,
         *                               createRadius);
         * }
         */
        @Template.Generated("%CREATE_NETHER_PORTAL%")
        public abstract boolean createNetherPortal(Block startBlock, int createRadius);

        /*
         * <FIND_OR_CREATE_END_PLATFORM>
         * public static org.bukkit.block.Block findOrCreateEndPlatform(org.bukkit.World bworld, boolean create) {
         *     #require net.minecraft.server.PortalTravelAgent private BlockPosition findEndPortal(BlockPosition portal);
         *     #require net.minecraft.server.PortalTravelAgent private BlockPosition createEndPortal(double x, double y, double z);
         * 
         *     WorldServer world = ((org.bukkit.craftbukkit.CraftWorld) bworld).getHandle();
         *     PortalTravelAgent agent = new PortalTravelAgent(world);
         * 
         *     World the_end_world = MinecraftServer.getServer().getWorldServer(1);
         *     BlockPosition platformPos = (the_end_world == null) ? null : the_end_world.worldProvider.h();
         *     if (platformPos == null) {
         *         platformPos = new BlockPosition(100, 50, 0);
         *     }
         * 
         *     if (create) {
         *         BlockPosition position = agent#createEndPortal((double)platformPos.getX(),(double)platformPos.getY(),(double)platformPos.getZ());
         *         return bworld.getBlockAt(platformPos.getX(), platformPos.getY()-2, platformPos.getZ());
         *     } else {
         *         BlockPosition position = agent#findEndPortal(platformPos);
         *         if (position == null) {
         *             return null;
         *         } else {
         *             return bworld.getBlockAt(position.getX(), position.getY()-1, position.getZ());
         *         }
         *     }
         * }
         */
        @Template.Generated("%FIND_OR_CREATE_END_PLATFORM%")
        public abstract Block findOrCreateEndPlatform(World world, boolean create);
    }
}
