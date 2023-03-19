package com.bergerkiller.bukkit.common.internal.network;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.network.NetworkManagerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.network.PlayerConnectionHandle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;

/**
 * Fallback packet handler which uses an injected PlayerConnection replacement
 */
public class CommonPacketHandler extends PacketHandlerHooked {

    /**
     * Known plugins that malfunction with the default packet handler
     */
    private static final String[] incompatibilities = {"Spout"};

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

    private static void failPacketListener(String pluginName) {
        showFailureMessage("a plugin conflict, namely " + pluginName);
    }

    private static void showFailureMessage(String causeName) {
    	Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to hook up a PlayerConnection to listen for received and sent packets");
    	Logging.LOGGER_NETWORK.log(Level.SEVERE, "This was caused by " + causeName);
        Logging.LOGGER_NETWORK.log(Level.SEVERE, "Install ProtocolLib to restore protocol compatibility");
        Logging.LOGGER_NETWORK.log(Level.SEVERE, "Dev-bukkit: http://dev.bukkit.org/server-mods/protocollib/");
    }

    @io.netty.channel.ChannelHandler.Sharable
    public static class CommonChannelListener extends ChannelDuplexHandler {

        public static void bind(Player player) {
            Object entityPlayer = HandleConversion.toEntityHandle(player);
            PlayerConnectionHandle playerConnection = EntityPlayerHandle.T.playerConnection.get(entityPlayer);
            if (playerConnection == null) {
                return; // already disconnected
            }
            Object networkManager = playerConnection.getNetworkManager();
            Channel channel = NetworkManagerHandle.T.channel.get(networkManager);
            CommonChannelListener listener = new CommonChannelListener(player);
            try {
                channel.pipeline().addBefore("packet_handler", "bkcommonlib", listener);
            } catch (NoSuchElementException ex) {
                // If for some reason packet_handler does not exist, add it at the first place as a fallback
                // This sometimes happens when the player login isn't complete, or some other odd reason
                channel.pipeline().addFirst("bkcommonlib", listener);
            }
        }

        public static void unbind(Player player) {
            Object entityPlayer = HandleConversion.toEntityHandle(player);
            PlayerConnectionHandle playerConnection = EntityPlayerHandle.T.playerConnection.get(entityPlayer);
            if (playerConnection == null) {
                return; // already disconnected
            }
            Object networkManager = playerConnection.getNetworkManager();
            final Channel channel = NetworkManagerHandle.T.channel.get(networkManager);
            channel.eventLoop().submit(() -> {
                channel.pipeline().remove("bkcommonlib");
                return null;
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
            if (handler.handlePacketSend(player, msg, false)) {
                super.write(ctx, msg, promise);
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (handler.handlePacketReceive(player, msg, false)) {
                super.channelRead(ctx, msg);
            }
        }
    }
}
