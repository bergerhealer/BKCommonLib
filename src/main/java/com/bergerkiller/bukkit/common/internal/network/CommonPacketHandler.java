package com.bergerkiller.bukkit.common.internal.network;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Queue;
import java.util.logging.Level;

import net.minecraft.server.*;
import net.minecraft.util.io.netty.util.concurrent.GenericFutureListener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;
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
		CommonPlayerConnection.bindAll(true);
		return true;
	}

	@Override
	public boolean onDisable() {
		// Unbind all hooks - but don't do a check since we are disabling
		// Can not create new tasks at that point
		CommonPlayerConnection.unbindAll(false);
		return true;
	}

	@Override
	public void onPlayerJoin(Player player) {
		CommonPlayerConnection.bind(player, true);
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

	private static void failPacketListener(Class<?> playerConnectionType) {
		Plugin plugin = CommonUtil.getPluginByClass(playerConnectionType);
		if (plugin == null) {
			showFailureMessage("an unknown source, class: " + playerConnectionType.getName());
		} else {
			failPacketListener(plugin.getName());
		}
		if (CommonPlugin.hasInstance()) {
			CommonPlugin.getInstance().onCriticalFailure();
		}
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

	public static class CommonPlayerConnection extends PlayerConnection {
		private final PlayerConnection previous;
		private final PacketHandlerHooked handler;

		static {
			// Verify that all receiver methods in PlayerConnection are overrided
			for (Method method : PlayerConnection.class.getDeclaredMethods()) {
				if (method.getReturnType() != void.class || method.getParameterTypes().length != 1 
						|| !Modifier.isPublic(method.getModifiers())) {
					continue;
				}
				Class<?> arg = method.getParameterTypes()[0];
				if (!Packet.class.isAssignableFrom(arg) || arg == Packet.class) {
					continue;
				}
				SafeMethod<Void> commonMethod = new SafeMethod<Void>(method);
				if (!commonMethod.isOverridedIn(CommonPlayerConnection.class)) {
					// NOT OVERRIDED!
					StringBuilder msg = new StringBuilder(200);
					msg.append("Receiver handler ").append(method.getName());
					msg.append('(').append(arg.getSimpleName()).append(')');
					msg.append(" is not overrided!");
					CommonPlugin.LOGGER_NETWORK.log(Level.WARNING, msg.toString());
				}
			}
		}

		private CommonPlayerConnection(MinecraftServer minecraftserver, EntityPlayer entityplayer) {
			super(minecraftserver, entityplayer.playerConnection.networkManager, entityplayer);
			previous = entityplayer.playerConnection;
			handler = (PacketHandlerHooked) CommonPlugin.getInstance().getPacketHandler();
			PlayerConnectionRef.TEMPLATE.transfer(previous, this);
		}

		public static void bindAll(boolean doSafetyCheck) {
			for (Player player : CommonUtil.getOnlinePlayers()) {
				bind(player, doSafetyCheck);
			}
		}

		public static void unbindAll(boolean doSafetyCheck) {
			for (Player player : CommonUtil.getOnlinePlayers()) {
				unbind(player, doSafetyCheck);
			}
		}

		private static boolean isReplaceable(Object playerConnection) {
			return playerConnection instanceof CommonPlayerConnection || playerConnection.getClass() == PlayerConnection.class;
		}

		private static void setPlayerConnection(final EntityPlayer ep, final PlayerConnection connection, boolean doSafetyCheck) {
			if (isReplaceable(ep.playerConnection)) {
				// Set it
				ep.playerConnection = connection;
				// Perform a little check-up in 10 ticks
				if (doSafetyCheck && CommonPlugin.hasInstance()) {
					new Task(CommonPlugin.getInstance()) {
						@Override
						public void run() {
							if (ep.playerConnection != connection) {
								// Player connection has changed!
								failPacketListener(ep.playerConnection.getClass());
							}
						}
					}.start(10);
				}
			} else {
				// Plugin conflict!
				failPacketListener(ep.playerConnection.getClass());
			}
		}

		public static void bind(Player player, boolean doSafetyCheck) {
			final EntityPlayer ep = CommonNMS.getNative(player);
			if (ep.playerConnection instanceof CommonPlayerConnection) {
				return;
			}
			setPlayerConnection(ep, new CommonPlayerConnection(CommonNMS.getMCServer(), ep), doSafetyCheck);
		}

		public static void unbind(Player player, boolean doSafetyCheck) {
			final EntityPlayer ep = CommonNMS.getNative(player);
			final PlayerConnection previous = ep.playerConnection;
			if (previous instanceof CommonPlayerConnection) {
				PlayerConnection replacement = ((CommonPlayerConnection) previous).previous;
				PlayerConnectionRef.TEMPLATE.transfer(previous, replacement);
				setPlayerConnection(ep, replacement, doSafetyCheck);
			}
		}

		@Override
		public void a(PacketPlayInKeepAlive packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInAbilities packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInArmAnimation packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInBlockDig packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInBlockPlace packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInChat packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInClientCommand packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInCloseWindow packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInCustomPayload packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInEnchantItem packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInEntityAction packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInFlying packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInHeldItemSlot packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInSetCreativeSlot packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInSettings packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInSteerVehicle packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInTabComplete packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInTransaction packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInUpdateSign packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInUseEntity packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(PacketPlayInWindowClick packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		private boolean canConfirm(Packet packet) {
			return handler.handlePacketReceive(CommonNMS.getPlayer(this.player), packet, false);
		}

		@Override
		public void sendPacket(Packet packet) {
			if (handler.handlePacketSend(CommonNMS.getPlayer(this.player), packet, false)) {
				super.sendPacket(packet);
			}
		}
	}
}
