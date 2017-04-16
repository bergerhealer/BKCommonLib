package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.events.MapViewEvent;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.hooks.WorldListenerHook;
import com.bergerkiller.bukkit.common.map.InteractiveMapDisplay;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.scoreboards.CommonScoreboard;
import com.bergerkiller.bukkit.common.scoreboards.CommonTeam;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;

@SuppressWarnings("unused")
class CommonListener implements Listener, PacketListener {

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPluginEnable(final PluginEnableEvent event) {
        String name = LogicUtil.fixNull(event.getPlugin().getName(), "");
        for (PluginBase pb : CommonPlugin.getInstance().plugins) {
            pb.updateDependency(event.getPlugin(), name, true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPluginDisable(PluginDisableEvent event) {
        String name = LogicUtil.fixNull(event.getPlugin().getName(), "");
        for (PluginBase pb : CommonPlugin.getInstance().plugins) {
            pb.updateDependency(event.getPlugin(), name, false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onWorldInit(final WorldInitEvent event) {
        ChunkProviderServerHook.hook(event.getWorld());
        CommonUtil.nextTick(new Runnable() {
            public void run() {
                CommonPlugin.getInstance().notifyWorldAdded(event.getWorld());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onWorldLoad(WorldLoadEvent event) {
        WorldListenerHook.hook(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onWorldUnload(WorldUnloadEvent event) {
        WorldListenerHook.unhook(event.getWorld());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onVehicleEnter(final VehicleEnterEvent event) {
        // Set the vehicle and passenger handles for Hook entities
        // This is required to avoid problems with replaced Entities
        if (CommonNMS.getNative(event.getVehicle()).dead) {
            // Find the real Entity and redirect the call
            final org.bukkit.entity.Entity realVehicle = EntityUtil.getEntity(event.getEntered().getWorld(), event.getVehicle().getUniqueId());
            if (realVehicle != null && realVehicle != event.getVehicle()) {
                // Perform the event again for the right Bukkit entity/Handle
                event.setCancelled(true);
                realVehicle.setPassenger(event.getEntered());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerQuit(PlayerQuitEvent event) {
        CommonScoreboard.removePlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerJoin(PlayerJoinEvent event) {
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

    /*
     * This is a temporary workaround until the VehicleExitEvent works again
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getPlayer().getVehicle() == event.getRightClicked() && event.getRightClicked() instanceof Vehicle) {
            // Call a player exit event
            final Vehicle vehicle = (Vehicle) event.getRightClicked();
            event.setCancelled(CommonUtil.callEvent(new VehicleExitEvent(vehicle, event.getPlayer())).isCancelled());
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        InteractiveMapDisplay.handlePacket(event);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        InteractiveMapDisplay.handlePacket(event);
    }
}
