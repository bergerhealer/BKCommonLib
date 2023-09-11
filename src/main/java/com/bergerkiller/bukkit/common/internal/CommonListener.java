package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.collections.ImmutableCachedSet;
import com.bergerkiller.bukkit.common.internal.logic.CreaturePreSpawnHandler;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.PluginLoaderHandler;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.scoreboards.CommonScoreboard;
import com.bergerkiller.bukkit.common.scoreboards.CommonTeam;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import com.bergerkiller.mountiplex.reflection.util.FastField;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class CommonListener implements Listener {
    /**
     * This is used by BlockData to detect block physics occurring
     */
    public static boolean BLOCK_PHYSICS_FIRED = false;
    /**
     * Stores all caches of immutable player sets created
     */
    private static final List<WeakReference<ImmutableCachedSet<Player>>> CACHED_IMMUTABLE_PLAYER_SETS = new ArrayList<>();
    /**
     * Reach deep into our own code!
     */
    private static final FastField<PluginLoaderHandler> pluginLoaderHandlerField = new FastField<>();
    static {
        try {
            pluginLoaderHandlerField.init(PluginBase.class.getDeclaredField("pluginLoaderHandler"));
        } catch (NoSuchFieldException e) {
            pluginLoaderHandlerField.initUnavailable("Field pluginLoaderHandler isn't there. This can't be!");
        }
    }

    public static void registerImmutablePlayerSet(ImmutableCachedSet<Player> set) {
        synchronized (CACHED_IMMUTABLE_PLAYER_SETS) {
            CACHED_IMMUTABLE_PLAYER_SETS.add(new WeakReference<>(set));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onPluginEnable(final PluginEnableEvent event) {
        if (!PluginLoaderHandler.isPluginFullyEnabled(event.getPlugin())) {
            return;
        }

        String name = LogicUtil.fixNull(event.getPlugin().getName(), "");
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin != event.getPlugin() && plugin.isEnabled() && plugin instanceof PluginBase) {
                PluginBase pb = (PluginBase) plugin;
                try {
                    pluginLoaderHandlerField.get(pb).onPluginLoaded(event.getPlugin());
                    pb.updateDependency(event.getPlugin(), name, true);
                } catch (Throwable t) {
                    pb.getLogger().log(Level.SEVERE, "Failed to handle updateDependency", t);
                }
            }
        }
        CommonPlugin.flushSaveOperations(event.getPlugin());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onPluginDisable(PluginDisableEvent event) {
        String name = LogicUtil.fixNull(event.getPlugin().getName(), "");
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin != event.getPlugin() && plugin.isEnabled() && plugin instanceof PluginBase) {
                PluginBase pb = (PluginBase) plugin;
                try {
                    pb.updateDependency(event.getPlugin(), name, false);
                } catch (Throwable t) {
                    pb.getLogger().log(Level.SEVERE, "Failed to handle updateDependency", t);
                }
            }
        }
        CommonPlugin.flushSaveOperations(event.getPlugin());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onWorldInit(final WorldInitEvent event) {
        CreaturePreSpawnHandler.INSTANCE.onWorldEnabled(event.getWorld());
        CommonUtil.nextTick(() -> CommonPlugin.getInstance().notifyWorldAdded(event.getWorld()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onWorldLoad(WorldLoadEvent event) {
        EntityAddRemoveHandler.INSTANCE.onWorldEnabled(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onWorldUnload(WorldUnloadEvent event) {
        EntityAddRemoveHandler.INSTANCE.onWorldDisabled(event.getWorld());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    protected void onVehicleEnter(final VehicleEnterEvent event) {
        // Set the vehicle and passenger handles for Hook entities
        // This is required to avoid problems with replaced Entities
        if (EntityHandle.fromBukkit(event.getVehicle()).isDestroyed()) {
            // Find the real Entity and redirect the call
            final org.bukkit.entity.Entity realVehicle = EntityUtil.getEntity(event.getEntered().getWorld(), event.getVehicle().getUniqueId());
            if (realVehicle != null && realVehicle != event.getVehicle()) {
                // Perform the event again for the right Bukkit entity/Handle
                event.setCancelled(true);
                ExtendedEntity<Entity> extRealVehicle = new ExtendedEntity<Entity>(realVehicle);
                extRealVehicle.addPassenger(event.getEntered());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onPlayerQuit(PlayerQuitEvent event) {
        CommonScoreboard.removePlayer(event.getPlayer());
        CommonPlugin.getInstance().getVehicleMountManager().remove(event.getPlayer());
        removeFromCachedImmutablePlayerSets(event.getPlayer());
    }

    private void removeFromCachedImmutablePlayerSets(Player player) {
        synchronized (CACHED_IMMUTABLE_PLAYER_SETS) {
            Iterator<WeakReference<ImmutableCachedSet<Player>>> iter = CACHED_IMMUTABLE_PLAYER_SETS.iterator();
            while (iter.hasNext()) {
                ImmutableCachedSet<Player> set = iter.next().get();
                if (set == null) {
                    iter.remove();
                } else {
                    set.releaseFromCache(player);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CommonPlugin.getInstance().getPacketHandler().onPlayerJoin(player);

        // Scoreboard team init
        CommonTeam team = CommonScoreboard.get(player).getTeam();
        if (!team.shouldSendToAll()) {
            team.send(player); //Send player team to player
        }
        for (CommonTeam ct : CommonScoreboard.getTeams()) {
            if (ct.shouldSendToAll()) {
                ct.send(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        CommonPlugin.getInstance().getPlayerMeta(event.getPlayer()).initiateRespawnBlindness();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getFrom() != null && event.getTo() != null && event.getFrom().getWorld() != event.getTo().getWorld()) {
            CommonPlugin.getInstance().getPlayerMeta(event.getPlayer()).initiateRespawnBlindness();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onBlockPhysics(BlockPhysicsEvent event) {
        // When block physics events occur while looking up the render options
        // for a block, log a warning of this (once).
        BLOCK_PHYSICS_FIRED = true;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerItemDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        MapDisplay display;

        // Try map held in main hand first
        display = MapDisplay.getViewedDisplay(p, HumanHand.getItemInMainHand(event.getPlayer()));
        if (display != null) {
            ItemStack item = event.getItemDrop().getItemStack();
            if (display.onItemDrop(p, item) || display.getRootWidget().onItemDrop(p, item)) {
                event.setCancelled(true);
                return;
            }
        }

        // Try map held in off hand second
        display = MapDisplay.getViewedDisplay(p, HumanHand.getItemInOffHand(event.getPlayer()));
        if (display != null) {
            ItemStack item = event.getItemDrop().getItemStack();
            if (display.onItemDrop(p, item) || display.getRootWidget().onItemDrop(p, item)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractWithBlock(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        MapDisplay display;

        // Try map held in main hand first
        display = MapDisplay.getViewedDisplay(p, HumanHand.getItemInMainHand(event.getPlayer()));
        if (display != null) {
            display.onBlockInteract(event);
            display.getRootWidget().onBlockInteract(event);
        }

        // Try map held in off hand second
        display = MapDisplay.getViewedDisplay(p, HumanHand.getItemInOffHand(event.getPlayer()));
        if (display != null) {
            display.onBlockInteract(event);
            display.getRootWidget().onBlockInteract(event);
        }
    }

    /*
     * This is a temporary workaround until the VehicleExitEvent works again
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getPlayer().getVehicle() == event.getRightClicked() && event.getRightClicked() instanceof Vehicle) {
            // Call a player exit event
            final Vehicle vehicle = (Vehicle) event.getRightClicked();
            event.setCancelled(CommonUtil.callEvent(new VehicleExitEvent(vehicle, event.getPlayer())).isCancelled());
        }
    }
}
