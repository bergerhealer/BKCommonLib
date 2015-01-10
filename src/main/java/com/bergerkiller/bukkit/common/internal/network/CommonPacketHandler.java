package com.bergerkiller.bukkit.common.internal.network;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import net.minecraft.server.v1_8_R1.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.GenericFutureListener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;
import com.bergerkiller.bukkit.common.reflection.classes.EntityPlayerRef;
import com.bergerkiller.bukkit.common.reflection.classes.NetworkManagerRef;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerConnectionRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Fallback packet handler which uses an injected PlayerConnection replacement
 */
public class CommonPacketHandler extends PacketHandlerHooked {

    /**
     * Known plugins that malfunction with the default packet handler
     */
    private static final String[] incompatibilities = {"Spout"};
    /*
     * Used for silent packet sending
     */
    private Object[] emptyGenericFutureListener;
    private SafeConstructor<?> queuedPacketConstructor;

    @Override
    public String getName() {
        return "a PlayerConnection hook";
    }

    @Override
    public boolean onEnable() {
        if (!super.onEnable()) {
            return false;
        }
        for (String incompatibility : incompatibilities) {
            if (CommonUtil.isPluginInDirectory(incompatibility)) {
                // Fail!
                failPacketListener(incompatibility);
                return false;
            }
        }

        // Initialize queued packet logic for silent sending
        ClassTemplate<?> queuedPacketTemplate = NMSClassTemplate.create("QueuedPacket");
        this.emptyGenericFutureListener = new GenericFutureListener[0];
        this.queuedPacketConstructor = queuedPacketTemplate.getConstructor(PacketType.DEFAULT.getType(), GenericFutureListener[].class);
        if (!this.queuedPacketConstructor.isValid()) {
            return false;
        }

        // Bind and done
        for (Player player : Bukkit.getOnlinePlayers()) {
            CommonChannelListener.bind(player);
        }

        return true;
    }

    @Override
    public boolean onDisable() {
		// Unbind all hooks - but don't do a check since we are disabling
        // Can not create new tasks at that point
        for (Player player : Bukkit.getOnlinePlayers()) {
            CommonChannelListener.unbind(player);
        }

        return true;
    }

    @Override
    public void onPlayerJoin(Player player) {
        CommonChannelListener.bind(player);
    }

    @Override
    public void sendSilentPacket(Player player, Object packet) {
        // Instead of using sendPacket, we sneakily insert the packet into the queue
        Object networkManager = EntityPlayerRef.getNetworkManager(player);
        Queue<Object> pollQueue = NetworkManagerRef.highPriorityQueue.get(networkManager);
        pollQueue.add(this.queuedPacketConstructor.newInstance(packet, this.emptyGenericFutureListener));
    }

    @Override
    public long getPendingBytes(Player player) {
        return calculatePendingBytes(player);
    }

    private static void failPacketListener(String pluginName) {
        showFailureMessage("a plugin conflict, namely " + pluginName);
    }

    private static void showFailureMessage(String causeName) {
        CommonPlugin.LOGGER_NETWORK.log(Level.SEVERE, "Failed to hook up a PlayerConnection to listen for received and sent packets");
        CommonPlugin.LOGGER_NETWORK.log(Level.SEVERE, "This was caused by " + causeName);
        CommonPlugin.LOGGER_NETWORK.log(Level.SEVERE, "Install ProtocolLib to restore protocol compatibility");
        CommonPlugin.LOGGER_NETWORK.log(Level.SEVERE, "Dev-bukkit: http://dev.bukkit.org/server-mods/protocollib/");
    }

    public static class CommonChannelListener extends ChannelDuplexHandler {

        public static void bind(Player player) {
            Object entityPlayer = Conversion.toEntityHandle.convert(player);
            Object playerConnection = EntityPlayerRef.playerConnection.get(entityPlayer);
            Object networkManager = PlayerConnectionRef.networkManager.get(playerConnection);
            Channel channel = NetworkManagerRef.channel.get(networkManager);
            channel.pipeline().addBefore("packet_handler", "bkcommonlib", new CommonChannelListener(player));
        }

        public static void unbind(Player player) {
            Object entityPlayer = Conversion.toEntityHandle.convert(player);
            Object playerConnection = EntityPlayerRef.playerConnection.get(entityPlayer);
            Object networkManager = PlayerConnectionRef.networkManager.get(playerConnection);
            final Channel channel = NetworkManagerRef.channel.get(networkManager);
            channel.eventLoop().submit(new Callable<Object>() {

                @Override
                public Object call() throws Exception {
                    channel.pipeline().remove("bkcommonlib");
                    return null;
                }
            });
        }

        private final PacketHandlerHooked handler;
        private final Player player;

        public CommonChannelListener(Player player) {
            this.handler = (PacketHandlerHooked) CommonPlugin.getInstance().getPacketHandler();
            this.player = player;
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            Packet packet = (Packet) msg;
            if (handler.handlePacketSend(player, packet, false)) {
                super.write(ctx, msg, promise);
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Packet packet = (Packet) msg;
            if (handler.handlePacketReceive(player, packet, false)) {
                super.channelRead(ctx, msg);
            }
        }
    }
}
