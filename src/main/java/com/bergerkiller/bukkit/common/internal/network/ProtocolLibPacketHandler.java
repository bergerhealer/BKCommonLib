package com.bergerkiller.bukkit.common.internal.network;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.PacketHandler;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.generated.net.minecraft.server.network.PlayerConnectionHandle;
import com.bergerkiller.mountiplex.logic.TextValueSequence;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.fast.InvalidArgumentCountException;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.injector.packet.PacketRegistry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;

/**
 * A packet handler implementation that uses ProtocolLib packet listeners
 */
public class ProtocolLibPacketHandler implements PacketHandler {

    public static final String LIB_ROOT = "com.comphenix.protocol.";
    private final List<CommonPacketMonitor> monitors = new ArrayList<CommonPacketMonitor>();
    private final List<CommonPacketListener> listeners = new ArrayList<CommonPacketListener>();
    private final SilentPacketQueue silentPacketQueueFallback = new SilentPacketQueue();
    private final SilentQueueCleanupTask silentQueueCleanupTask = new SilentQueueCleanupTask();
    private final FastMethod<Void> receivePacketMethod = new FastMethod<Void>();
    private final ThreadLocal<PacketEvent> currentHandledEvent = new ThreadLocal<>();
    private Class<?> loggedOutPlayerExceptionType = String.class;
    private boolean useSilentPacketQueue = false;
    private boolean isSendSilentPacketBroken = false;

    public static boolean isBundlePacketWorking() {
        // Unimportant on older MC
        if (!CommonCapabilities.HAS_BUNDLE_PACKET) {
            return true;
        }

        // Check for a >5.0.0 version of protocollib, where for sure it's supported
        Plugin p = CommonUtil.getPlugin("ProtocolLib");
        if (p != null) {
            // 5.0.0-SNAPSHOT-b621
            String ver = p.getDescription().getVersion();
            int dashIdx = ver.indexOf('-');
            if (dashIdx != -1) {
                ver = ver.substring(0, dashIdx);
            }
            if (TextValueSequence.evaluateText(ver, ">", "5.0.0")) {
                return true;
            }
        }

        // Support is unsure - check that the PacketEvent has a getBundle() to indicate its fixed
        try {
            PacketEvent.class.getMethod("getBundle");
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public void onPlayerJoin(Player player) {
    }

    @Override
    public boolean onEnable() {
        // Check whether all required classes are available
        Class<?> manager = CommonUtil.getClass(LIB_ROOT + "ProtocolManager");
        Class<?> packetContainer = CommonUtil.getClass(LIB_ROOT + "events.PacketContainer");
        if (manager == null || packetContainer == null) {
            return false;
        }

        // Forward-initialize this to avoid protocollib loading it
        CommonUtil.loadClass(InvalidArgumentCountException.class);

        // Exception thrown on older protocollib versions
        loggedOutPlayerExceptionType = CommonUtil.getClass(LIB_ROOT + "injector.PlayerLoggedOutException");
        if (loggedOutPlayerExceptionType == null) {
            loggedOutPlayerExceptionType = String.class; // Avoid NPE doing instanceof checks at runtime (never assignable)
        }

        // ProtocolLib renamed this typo with v5.0
        try {
            // 5.0+ API
            this.receivePacketMethod.init(manager.getMethod("receiveClientPacket", Player.class, packetContainer));
            this.receivePacketMethod.forceInitialization();
        } catch (Throwable t1) {
            // Try legacy pre-5.0 API
            try {
                // 5.0+ API
                this.receivePacketMethod.init(manager.getMethod("recieveClientPacket", Player.class, packetContainer));
                this.receivePacketMethod.forceInitialization();
            } catch (Throwable t2) {
                // Try legacy pre-5.0 API
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to initialize receiveClientPacket method for ProtocolLib", t1);
            }
        }

        // Detect silent packet bug
        if (containsSendSilentPacketBug()) {
            this.useSilentPacketQueue = true;
            this.isSendSilentPacketBroken = true;
        } else {
            this.useSilentPacketQueue = false;
            this.isSendSilentPacketBroken = false;
        }

        return true;
    }

    @Override
    public boolean onDisable() {
        ProtocolManager protocolManager = getProtocolManager();
        for (CommonPacketMonitor monitor : this.monitors) {
            protocolManager.removePacketListener(monitor);
        }
        for (CommonPacketListener listener : this.listeners) {
            protocolManager.removePacketListener(listener);
        }
        this.monitors.clear();
        this.listeners.clear();
        this.silentQueueCleanupTask.disable();
        if (!CommonUtil.isShuttingDown()) {
            Logging.LOGGER_NETWORK.warning("Reload detected! ProtocolLib does not officially support reloading the server!");
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                Logging.LOGGER_NETWORK.warning("Players are logged in. This is known to cause complete lock-out of the server!");
                Logging.LOGGER_NETWORK.warning("If you must absolutely reload, do so from the server terminal with no players logged in");
            }
        }
        return true;
    }

    @Override
    public String getName() {
        return "the ProtocolLib library";
    }

    @Override
    public Collection<Plugin> getListening(PacketType packetType) {
        Set<Plugin> plugins = new HashSet<Plugin>();
        // Obtain all plugins that have a listener (ignore monitors)
        boolean outGoing = packetType.isOutGoing();
        com.comphenix.protocol.PacketType comType = getPacketType(packetType);
        for (com.comphenix.protocol.events.PacketListener listener : getProtocolManager().getPacketListeners()) {
            final ListeningWhitelist whitelist;
            if (outGoing) {
                whitelist = listener.getSendingWhitelist();
            } else {
                whitelist = listener.getReceivingWhitelist();
            }
            if (whitelist.getPriority() != ListenerPriority.MONITOR && whitelist.getTypes().contains(comType)) {
                plugins.add(listener.getPlugin());
            }
        }
        return plugins;
    }

    private PacketContainer toContainer(Object rawPacket) {
        return new PacketContainer(getPacketType(rawPacket.getClass()), rawPacket);
    }

    @Override
    public void receivePacket(Player player, PacketType type, Object packet) {
        if (PlayerConnectionHandle.forPlayer(player) == null) {
            return; // NPC player is not connected
        }

        type.preprocess(packet);
        PacketContainer toReceive = toContainer(packet);
        try {
            this.receivePacketMethod.invoke(getProtocolManager(), player, toReceive);
        } catch (RuntimeException ex) {
            if (this.loggedOutPlayerExceptionType.isInstance(ex)) {
                return; // Ignore
            }
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Error while receiving packet:", e);
        }
    }

    @Override
    public void sendPacket(Player player, PacketType type, Object packet, boolean throughListeners) {
        PlayerConnectionHandle connection = PlayerConnectionHandle.forPlayer(player);
        if (connection == null) {
            return; // Player is an NPC or isn't connected
        }

        type.preprocess(packet);

        // Simplified logic for sending normally
        if (throughListeners) {
            connection.sendPacket(packet);
            return;
        }

        // See: onEnable()
        if (this.useSilentPacketQueue) {
            this.silentPacketQueueFallback.add(player, packet);
            this.silentQueueCleanupTask.kick();
        }

        if (this.isSendSilentPacketBroken) {
            // ProtocolLib had a linkage error while sending the packet before, or some other known bug
            // Instead we now use our own 'silent' packet queue to deal with this
            try {
                connection.sendPacket(packet);
            } catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Encountered an error during sendPacket()", t);
                try {
                    Logging.LOGGER_NETWORK.log(Level.SEVERE, "Packet: " + packet);
                } catch (Throwable printErr) { /* Ignore */ }
            }

        } else {
            // Silent - do not send it through listeners, only through monitors
            try {
                final PacketContainer toSend = toContainer(packet);
                getProtocolManager().sendServerPacket(player, toSend, null, false);
            } catch (RuntimeException ex) {
                if (this.loggedOutPlayerExceptionType.isInstance(ex)) {
                    return; // Ignore
                }
                throw ex;
            } catch (LinkageError err) {
                // Serious issue inside the ProtocolLib library. We cannot use it.
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "A severe error occurred inside ProtocolLib while trying to send a packet");
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Please look for an update of ProtocolLib to get this issue resolved");
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Error that occurred", err);

                // Enable the silent packet queue (if it wasn't already)
                // Remove the packet from the queue in case we added it previously
                this.silentPacketQueueFallback.take(player, packet);
                this.isSendSilentPacketBroken = true;
                this.useSilentPacketQueue = true;

                // Fallback
                sendPacket(player, type, packet, throughListeners);
            } catch (Throwable t) {
                throw new RuntimeException("Error while sending packet:", t);
            }
        }
    }

    @Override
    public void queuePacket(Player player, PacketType type, Object packet, boolean throughListeners) {
        // If run from a thread where we are handling a packet being listener or monitored, then
        // we can use the existing marker object to queue it in
        PacketEvent currentEvent;
        if ((currentEvent = this.currentHandledEvent.get()) != null) {
            // Must pre-process the packet first before queing it up
            type.preprocess(packet);

            // Store in the silent/suppression queue if used
            if (!throughListeners && this.useSilentPacketQueue) {
                this.silentPacketQueueFallback.add(player, packet);
            }

            // Turn into a ScheduledPacket
            try {
                final PacketContainer toSend = new PacketContainer(getPacketType(packet.getClass()), packet);
                final ScheduledPacket scheduled = new ScheduledPacket(toSend, player, throughListeners);
                currentEvent.schedule(scheduled);
            } catch (RuntimeException ex) {
                if (this.loggedOutPlayerExceptionType.isInstance(ex)) {
                    return; // Ignore
                }
                throw ex;
            } catch (LinkageError err) {
                // Serious issue inside the ProtocolLib library. We cannot use it.
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "A severe error occurred inside ProtocolLib while trying to schedule a packet");
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Please look for an update of ProtocolLib to get this issue resolved");
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Error that occurred", err);

                // Failure :(
            } catch (Throwable t) {
                throw new RuntimeException("Error while sending packet:", t);
            }

            return;
        }

        // There is no marker, so we can be reasonably certain it is safe to send the packet like normal
        this.sendPacket(player, type, packet, throughListeners);

        // Below was old logic for queuing packets, but we now use the NetworkMarker approach
        /*
        if (throughListeners || this.useSilentPacketQueue) {
            type.preprocess(packet);
            PlayerConnectionHandle connection = PlayerConnectionHandle.forPlayer(player);
            if (connection == null) {
                return;
            }

            if (!throughListeners) {
                this.silentPacketQueueFallback.add(player, packet);
            }

            connection.queuePacket(packet);
        } else {
            // Run next tick
            // TODO: Is there a ProtocolLib API to send a packet (not through listeners) and queue it up?
            //       https://www.spigotmc.org/threads/protocollib-send-packet-after-current-handled-packet.416910/
            CommonUtil.nextTick(() -> sendPacket(player, type, packet, throughListeners));
        }
        */
    }

    @Override
    public void removePacketListeners(Plugin plugin) {
        getProtocolManager().removePacketListeners(plugin);

        // Remove all listeners of this plugin
        Iterator<CommonPacketListener> list_iter = listeners.iterator();
        while (list_iter.hasNext()) {
            if (list_iter.next().getPlugin() == plugin) {
                list_iter.remove();
            }
        }

        // Remove all monitors of this plugin
        Iterator<CommonPacketMonitor> mon_iter = monitors.iterator();
        while (mon_iter.hasNext()) {
            if (mon_iter.next().getPlugin() == plugin) {
                mon_iter.remove();
            }
        }
    }

    @Override
    public void removePacketListener(PacketListener listener) {
        Iterator<CommonPacketListener> iter = listeners.iterator();
        while (iter.hasNext()) {
            CommonPacketListener cpl = iter.next();
            if (cpl.listener == listener) {
                getProtocolManager().removePacketListener(cpl);
                iter.remove();
            }
        }
    }

    @Override
    public void removePacketMonitor(PacketMonitor monitor) {
        Iterator<CommonPacketMonitor> iter = monitors.iterator();
        while (iter.hasNext()) {
            CommonPacketMonitor cpm = iter.next();
            if (cpm.monitor == monitor) {
                getProtocolManager().removePacketListener(cpm);
                iter.remove();
            }
        }
    }

    @Override
    public void addPacketListener(Plugin plugin, PacketListener listener, PacketType[] types) {
        CommonPacketListener commonListener = new CommonPacketListener(plugin, listener, types);
        getProtocolManager().addPacketListener(commonListener);
        this.listeners.add(commonListener);
    }

    @Override
    public void addPacketMonitor(Plugin plugin, PacketMonitor monitor, PacketType[] types) {
        CommonPacketMonitor commonMonitor = new CommonPacketMonitor(plugin, monitor, types);
        getProtocolManager().addPacketListener(commonMonitor);
        this.monitors.add(commonMonitor);
    }

    @Override
    public void transfer(PacketHandler to) {
        for (CommonPacketListener listener : listeners) {
            to.addPacketListener(listener.getPlugin(), listener.listener, listener.types);
        }
        for (CommonPacketMonitor monitor : monitors) {
            to.addPacketMonitor(monitor.getPlugin(), monitor.monitor, monitor.types);
        }
    }

    // There is a bug with protocollib where sending a silent packet one tick after the player
    // joins the server, ProtocolLib still notifies the packet listeners.
    // This here is a workaround for that, at least for BKCommonLib Packet Listeners.
    // See: https://github.com/PaperMC/Paper/issues/1934
    //
    // UPDATE 2-feb-2020
    // Looks like silent packet sending is completely broken on PaperSpigot
    // Their flushQueue() aborts when there are map chunk packets, causing a failure inside ProtocolLib
    // By default I now set isSendSilentPacketBroken to true to stop using ProtocolLib for this from now on
    // The reason the x-ray option appears to matter is because that increases the time the queue is stalled
    // See: https://github.com/dmulloy2/ProtocolLib/issues/763
    //
    // Update 5-mar-2020
    // Issue was fixed in an accepted PR, and will be completely fixed after the release of ProtocolLib 4.5.1.
    // This means when version 4.5.2 or newer is used, OR a class part of this fix exists on 4.5.1, we can
    // enable the protocollib's silent packet sending logic.
    private static boolean containsSendSilentPacketBug() {
        if (!Common.IS_PAPERSPIGOT_SERVER) {
            return false;
        }
        String protocolLibVersion = Bukkit.getPluginManager().getPlugin("ProtocolLib").getDescription().getVersion();
        int protocolLibVersionEnd = protocolLibVersion.indexOf('-');
        if (protocolLibVersionEnd != -1) {
            protocolLibVersion = protocolLibVersion.substring(0, protocolLibVersionEnd);
        }

        // Here we check using reflection whether the fix is in place (snapshots)
        if (protocolLibVersion.equals("4.5.1")) {
            try {
                Class.forName("com.comphenix.protocol.injector.netty.PacketFilterQueue");
                return false;
            } catch (ClassNotFoundException ex) {
                return true;
            }
        }

        // Bug exists on versions older than this
        return TextValueSequence.evaluateText(protocolLibVersion, "<", "4.5.1");
    }

    private static com.comphenix.protocol.PacketType getPacketType(PacketType commonType) {
        return getPacketType(commonType.getType());
    }

    private static com.comphenix.protocol.PacketType getPacketType(Class<?> packetClass) {
        return PacketRegistry.getPacketType(packetClass);
    }

    static ProtocolManager getProtocolManager() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        if (manager == null) {
            Logging.LOGGER.log(Level.SEVERE, "Unexpected NPE: ProtocolLibrary.getProtocolManager() is returning null!");
            Logging.LOGGER.log(Level.SEVERE, "This is caused by other plugins shading in ProtocolLib");
            Plugin plugin = CommonUtil.getPluginByClass(ProtocolManager.class);
            if (plugin != null) {
                PluginDescriptionFile pfile = plugin.getDescription();
                if (pfile != null) {
                    Logging.LOGGER.log(Level.SEVERE, "Please tell the developer(s) of " + pfile.getName() + " (" +
                            StringUtil.combineNames(pfile.getAuthors()) + ") to fix this problem, or remove the plugin");
                    Logging.LOGGER.log(Level.SEVERE, "Send the developer(s) the following information:");
                    Logging.LOGGER.log(Level.SEVERE, "Add <scope>provided</scope> to the protocollib dependency in the plugin's pom.xml");
                }
            }
            Logging.LOGGER.log(Level.SEVERE, "Please read: https://www.spigotmc.org/threads/protocollibrary-getprotocolmanager-returns-null.507594/#post-4171682");
            throw new NullPointerException("ProtocolLibrary.getProtocolManager() is null");
        }
        return manager;
    }

    static ListeningWhitelist getWhiteList(ListenerPriority priority, PacketType[] types, boolean receiving) {
        List<com.comphenix.protocol.PacketType> comTypes = new ArrayList<com.comphenix.protocol.PacketType>();
        for (PacketType type : types) {
            if ((!type.isOutGoing()) != receiving) {
                continue;
            }
            if (type.getType() == null) {
                continue;
            }
            com.comphenix.protocol.PacketType comType = getPacketType(type);
            if (comType != null) {
                comTypes.add(comType);
            }
        }
        return ListeningWhitelist.newBuilder().priority(priority).types(comTypes)
                .gamePhase(GamePhase.PLAYING).options(new ListenerOptions[] { ListenerOptions.ASYNC } ).build();
    }

    private class CommonPacketMonitor extends CommonPacketAdapter {

        public final PacketMonitor monitor;

        public CommonPacketMonitor(Plugin plugin, PacketMonitor monitor, PacketType[] types) {
            super(plugin, ListenerPriority.MONITOR, types);
            this.monitor = monitor;
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            // Check not temporary
            if (this.isTemporary(event)) {
                return;
            }

            monitor.onMonitorPacketReceive(new CommonPacket(event.getPacket().getHandle()), event.getPlayer());
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            // Check not temporary
            if (this.isTemporary(event)) {
                return;
            }

            ThreadLocal<PacketEvent> tCurrentEvent = currentHandledEvent;
            try {
                tCurrentEvent.set(event);
                monitor.onMonitorPacketSend(new CommonPacket(event.getPacket().getHandle()), event.getPlayer());
            } finally {
                tCurrentEvent.set(null);
            }
        }
    }

    private class CommonPacketListener extends CommonPacketAdapter {

        public final PacketListener listener;

        public CommonPacketListener(Plugin plugin, PacketListener listener, PacketType[] types) {
            super(plugin, ListenerPriority.NORMAL, types);
            this.listener = listener;
        }

        private void swapPacket(PacketEvent event, CommonPacket commonpacket) {
            PacketContainer container = toContainer(commonpacket.getHandle());

            // TODO: Cancel the packet and schedule a new one instead to avoid warning
            // Im not sure this actually works for receiving packets...
            //if (event.getPacketType() != container.getType()) {
            //    event.setCancelled(true);
            //}
            event.setPacket(container);
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            // Check not temporary
            if (this.isTemporary(event)) {
                return;
            }

            CommonPacket packet = new CommonPacket(event.getPacket().getHandle());
            PacketReceiveEvent receiveEvent = new PacketReceiveEvent(event.getPlayer(), packet);
            receiveEvent.setCancelled(event.isCancelled());
            listener.onPacketReceive(receiveEvent);
            event.setCancelled(receiveEvent.isCancelled());
            if (receiveEvent.getPacket() != packet) {
                swapPacket(event, receiveEvent.getPacket());
            }
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            // Check not silent
            // See: onEnable why this is here.
            Object raw_packet = event.getPacket().getHandle();
            if (useSilentPacketQueue &&
                silentPacketQueueFallback.take(event.getPlayer(), raw_packet))
            {
                return;
            }

            // Check not temporary
            if (this.isTemporary(event)) {
                return;
            }

            CommonPacket packet = new CommonPacket(raw_packet);
            PacketSendEvent sendEvent = new PacketSendEvent(event.getPlayer(), packet);
            sendEvent.setCancelled(event.isCancelled());

            event.setCancelled(handleSendDuring(event, () -> {
                listener.onPacketSend(sendEvent);
                return sendEvent.isCancelled();
            }));
            if (sendEvent.getPacket() != packet) {
                swapPacket(event, sendEvent.getPacket());
            }
        }
    }

    boolean handleSendDuring(PacketEvent event, BooleanSupplier sendAction) {
        ThreadLocal<PacketEvent> tCurrentEvent = currentHandledEvent;
        try {
            tCurrentEvent.set(event);
            return sendAction.getAsBoolean();
        } finally {
            tCurrentEvent.set(null);
        }
    }

    private static abstract class CommonPacketAdapter implements com.comphenix.protocol.events.PacketListener {

        private final Plugin plugin;
        public final PacketType[] types;
        private final ListeningWhitelist receiving;
        private final ListeningWhitelist sending;
        private final boolean eventHasIsTemporaryPlayerMethod;
        private final Class<?> temporaryPlayerClass;

        public CommonPacketAdapter(Plugin plugin, ListenerPriority priority, PacketType[] types) {
            this.plugin = plugin;
            this.types = types;
            this.receiving = getWhiteList(priority, types, true);
            this.sending = getWhiteList(priority, types, false);

            // Check that the PacketEvent has a isPlayerTemporary() method
            boolean hasIsTemporaryPlayerMethod = false;
            Class<?> tempPlayerClass = String.class; // fallback, always unassignable to player
            try {
                PacketEvent.class.getDeclaredMethod("isPlayerTemporary");
                hasIsTemporaryPlayerMethod = true;
            } catch (NoSuchMethodException ex) {
                // Note: this is probably never going to be used. The isPlayerTemporary method
                // probably existed since the dawn of time.

                // ProtocolLib uses a 'TemporaryPlayer' object to represent players before
                // they are spawned in the world. We want to ignore events of players that
                // use this, as it exposes plugins to a lot of potential errors.
                try {
                    tempPlayerClass = Class.forName("com.comphenix.protocol.injector.temporary.TemporaryPlayer");
                } catch (Throwable t1) {
                    try {
                        tempPlayerClass = Class.forName("com.comphenix.protocol.injector.server.TemporaryPlayer");
                    } catch (Throwable t2) {
                        Logging.LOGGER_NETWORK.warning("Failed to find ProtocolLib TemporaryPlayer class!");
                    }
                }
            }

            this.eventHasIsTemporaryPlayerMethod = hasIsTemporaryPlayerMethod;
            this.temporaryPlayerClass = tempPlayerClass;
        }

        /**
         * Gets whether the event involves a temporary player. A temporary player has no entity
         * id, no world and no location information.
         *
         * @param event Event to check if its for a temporary player
         * @return True if event is using a temporary player
         */
        protected boolean isTemporary(PacketEvent event) {
            if (eventHasIsTemporaryPlayerMethod) {
                return isEventPlayerTemporary(event);
            }

            Player player = event.getPlayer();
            return player == null || this.temporaryPlayerClass.isAssignableFrom(player.getClass());
        }

        private static boolean isEventPlayerTemporary(PacketEvent event) {
            return event.isPlayerTemporary();
        }

        @Override
        public Plugin getPlugin() {
            return plugin;
        }

        @Override
        public ListeningWhitelist getReceivingWhitelist() {
            return receiving;
        }

        @Override
        public ListeningWhitelist getSendingWhitelist() {
            return sending;
        }
    }

    private final class SilentQueueCleanupTask extends Task {
        private boolean _isScheduled = false;

        public SilentQueueCleanupTask() {
            super(CommonPlugin.getInstance());
        }

        @Override
        public synchronized void run() {
            if (ProtocolLibPacketHandler.this.silentPacketQueueFallback.cleanup()) {
                this._isScheduled = false;
                this.stop();
            }
        }

        public synchronized void disable() {
            if (this._isScheduled) {
                this._isScheduled = false;
                this.stop();
            }
        }

        public synchronized void kick() {
            if (!this._isScheduled) {
                this.start(20, 20);
                this._isScheduled = true;
            }
        }
    }
}
