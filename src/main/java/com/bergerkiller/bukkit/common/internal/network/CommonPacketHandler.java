package com.bergerkiller.bukkit.common.internal.network;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.SafeConstructor;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityPlayer;
import com.bergerkiller.reflection.net.minecraft.server.NMSNetworkManager;
import com.bergerkiller.reflection.net.minecraft.server.NMSPlayerConnection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.server.v1_11_R1.NetworkManager;
import net.minecraft.server.v1_11_R1.Packet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.logging.Level;

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
        Class[] possible = NetworkManager.class.getDeclaredClasses();
        Class qp = null;
        for (Class p : possible) {
            if (p.getName().endsWith("QueuedPacket")) {
                qp = p;
            }
        }
        ClassTemplate<?> queuedPacketTemplate = ClassTemplate.create(qp);
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
    public long getPendingBytes(Player player) {
        return calculatePendingBytes(player);
    }

    private static void failPacketListener(String pluginName) {
        showFailureMessage("a plugin conflict, namely " + pluginName);
    }

    private static void showFailureMessage(String causeName) {
    	Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to hook up a PlayerConnection to listen for received and sent packets");
    	Logging.LOGGER_NETWORK.log(Level.SEVERE, "This was caused by " + causeName);
        Logging.LOGGER_NETWORK.log(Level.SEVERE, "Install ProtocolLib to restore protocol compatibility");
        Logging.LOGGER_NETWORK.log(Level.SEVERE, "Dev-bukkit: http://dev.bukkit.org/server-mods/protocollib/");
    }

    public static class CommonChannelListener extends ChannelDuplexHandler {

        public static void bind(Player player) {
            Object entityPlayer = Conversion.toEntityHandle.convert(player);
            Object playerConnection = NMSEntityPlayer.playerConnection.get(entityPlayer);
            Object networkManager = NMSPlayerConnection.networkManager.get(playerConnection);
            Channel channel = NMSNetworkManager.channel.get(networkManager);
            channel.pipeline().addBefore("packet_handler", "bkcommonlib", new CommonChannelListener(player));
        }

        public static void unbind(Player player) {
            Object entityPlayer = Conversion.toEntityHandle.convert(player);
            Object playerConnection = NMSEntityPlayer.playerConnection.get(entityPlayer);
            Object networkManager = NMSPlayerConnection.networkManager.get(playerConnection);
            final Channel channel = NMSNetworkManager.channel.get(networkManager);
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
