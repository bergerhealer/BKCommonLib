package com.bergerkiller.bukkit.common.internal.network;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

import net.minecraft.server.*;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.reflection.SafeField;
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
	private static final List<PlayerConnection> serverPlayerConnections = SafeField.get(CommonNMS.getMCServer().ag(), "c");

	@Override
	public String getName() {
		return "a PlayerConnection hook";
	}

	@Override
	public boolean onEnable() {
		for (String incompatibility : incompatibilities) {
			if (CommonUtil.isPluginInDirectory(incompatibility)) {
				// Fail!
				failPacketListener(incompatibility);
				return false;
			}
		}
		CommonPlayerConnection.bindAll();
		return true;
	}

	@Override
	public boolean onDisable() {
		CommonPlayerConnection.unbindAll();
		return true;
	}

	@Override
	public void onPlayerJoin(Player player) {
		CommonPlayerConnection.bind(player);
	}

	@Override
	public void sendSilentPacket(Player player, Object packet) {
		final Object connection = EntityPlayerRef.playerConnection.get(Conversion.toEntityHandle.convert(player));
		PlayerConnectionRef.sendPacket(connection, new CommonSilentPacket(packet));
	}

	@Override
	public long getPendingBytes(Player player) {
		return calculatePendingBytes(player);
	}

	protected static long calculatePendingBytes(Player player) {
		final Object playerHandle = Conversion.toEntityHandle.convert(player);
		final Object playerConnection = EntityPlayerRef.playerConnection.get(playerHandle);
		final Object nm = PlayerConnectionRef.networkManager.get(playerConnection);
		// We can only work on Network manager implementations, INetworkManager implementations are unknown to us
		if (!NetworkManagerRef.TEMPLATE.isInstance(nm)) {
			return 0L;
		}
		Object lockObject = NetworkManagerRef.lockObject.get(nm);
		if (lockObject == null) {
			return 0L;
		}
		List<Object> low = NetworkManagerRef.lowPriorityQueue.get(nm);
		List<Object> high = NetworkManagerRef.highPriorityQueue.get(nm);
		if (low == null || high == null) {
			return 0L;
		}
		long queuedsize = 0;
		synchronized (lockObject) {
			for (Object p : low) {
				queuedsize += PacketFields.DEFAULT.getPacketSize(p) + 1;
			}
			for (Object p : high) {
				queuedsize += PacketFields.DEFAULT.getPacketSize(p) + 1;
			}
		}
		return queuedsize;
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

	private static class CommonPlayerConnection extends PlayerConnection {
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

		public static void bindAll() {
			for (Player player : CommonUtil.getOnlinePlayers()) {
				bind(player);
			}
		}

		public static void unbindAll() {
			for (Player player : CommonUtil.getOnlinePlayers()) {
				unbind(player);
			}
		}

		private static boolean isReplaceable(Object playerConnection) {
			return playerConnection instanceof CommonPlayerConnection || playerConnection.getClass() == PlayerConnection.class;
		}

		private static void setPlayerConnection(final EntityPlayer ep, final PlayerConnection connection) {
			if (isReplaceable(ep.playerConnection)) {
				// Set it
				ep.playerConnection = connection;
				// Register
				registerPlayerConnection(ep, connection, true);
				// Perform a little check-up in 10 ticks
				if (CommonPlugin.hasInstance()) {
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

		private static void registerPlayerConnection(final EntityPlayer ep, final PlayerConnection connection, boolean retry) {
			synchronized (serverPlayerConnections) {
				// Replace existing
				ListIterator<PlayerConnection> iter = serverPlayerConnections.listIterator();
				while (iter.hasNext()) {
					if (iter.next().player == ep) {
						iter.set(connection);
						return;
					}
				}
				if (!retry) {
					StringBuilder msg = new StringBuilder(100);
					msg.append("Failed to ");
					if (connection instanceof CommonPlayerConnection) {
						msg.append("register");
					} else {
						msg.append("unregister");
					}
					msg.append(" PlayerConnection proxy for ").append(ep.getBukkitEntity().getName()).append("...bad things may happen!");
					CommonPlugin.LOGGER.log(Level.SEVERE, msg.toString());
					return;
				}
				// We failed to set it in one go...
				// Set the next tick but then fail
				CommonUtil.nextTick(new Runnable() {
					public void run() {
						registerPlayerConnection(ep, connection, false);
					}
				});
			}
		}

		public static void bind(Player player) {
			final EntityPlayer ep = CommonNMS.getNative(player);
			if (ep.playerConnection instanceof CommonPlayerConnection) {
				return;
			}
			setPlayerConnection(ep, new CommonPlayerConnection(CommonNMS.getMCServer(), ep));
		}

		public static void unbind(Player player) {
			final EntityPlayer ep = CommonNMS.getNative(player);
			final PlayerConnection previous = ep.playerConnection;
			if (previous instanceof CommonPlayerConnection) {
				PlayerConnection replacement = ((CommonPlayerConnection) previous).previous;
				PlayerConnectionRef.TEMPLATE.transfer(previous, replacement);
				setPlayerConnection(ep, replacement);
			}
		}

		@Override
		public void a(Packet0KeepAlive packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet100OpenWindow packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void handleContainerClose(Packet101CloseWindow packet) {
			if (this.canConfirm(packet))
				super.handleContainerClose(packet);
		}

		@Override
		public void a(Packet102WindowClick packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet103SetSlot packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet104WindowItems packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet105CraftProgressBar packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet106Transaction packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet107SetCreativeSlot packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet108ButtonClick packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet10Flying packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet130UpdateSign packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet131ItemData packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet132TileEntityData packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet14BlockDig packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet15Place packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet16BlockItemSwitch packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet17EntityLocationAction packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet18ArmAnimation packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet19EntityAction packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet1Login packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet200Statistic packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet201PlayerInfo packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet202Abilities packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet203TabComplete packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet204LocaleAndViewDistance packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet205ClientCommand packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet20NamedEntitySpawn packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet22Collect packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet23VehicleSpawn packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet24MobSpawn packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet250CustomPayload packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet252KeyResponse packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet253KeyRequest packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet254GetInfo packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet255KickDisconnect packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet25EntityPainting packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet26AddExpOrb packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet28EntityVelocity packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet29DestroyEntity packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet2Handshake packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet30Entity packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet34EntityTeleport packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet35EntityHeadRotation packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet38EntityStatus packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet39AttachEntity packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet3Chat packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet40EntityMetadata packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet41MobEffect packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet42RemoveMobEffect packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet43SetExperience packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet4UpdateTime packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet51MapChunk packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet52MultiBlockChange packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet53BlockChange packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet54PlayNoteBlock packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet55BlockBreakAnimation packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet56MapChunkBulk packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet5EntityEquipment packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet60Explosion packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet61WorldEvent packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet62NamedSoundEffect packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet6SpawnPosition packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet70Bed packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet71Weather packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet7UseEntity packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet8UpdateHealth packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet9Respawn packet) {
			if(this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet27PlayerInput packet) {
			if (this.canConfirm(packet))
				super.a(packet);
		}

		@Override
		public void a(Packet133OpenTileEntity packet) {
			if (this.canConfirm(packet))
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
