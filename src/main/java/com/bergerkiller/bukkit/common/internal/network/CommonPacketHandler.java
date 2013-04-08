package com.bergerkiller.bukkit.common.internal.network;

import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

import net.minecraft.server.v1_5_R2.EntityPlayer;
import net.minecraft.server.v1_5_R2.MinecraftServer;
import net.minecraft.server.v1_5_R2.Packet;
import net.minecraft.server.v1_5_R2.Packet0KeepAlive;
import net.minecraft.server.v1_5_R2.Packet100OpenWindow;
import net.minecraft.server.v1_5_R2.Packet102WindowClick;
import net.minecraft.server.v1_5_R2.Packet103SetSlot;
import net.minecraft.server.v1_5_R2.Packet104WindowItems;
import net.minecraft.server.v1_5_R2.Packet105CraftProgressBar;
import net.minecraft.server.v1_5_R2.Packet106Transaction;
import net.minecraft.server.v1_5_R2.Packet107SetCreativeSlot;
import net.minecraft.server.v1_5_R2.Packet108ButtonClick;
import net.minecraft.server.v1_5_R2.Packet10Flying;
import net.minecraft.server.v1_5_R2.Packet130UpdateSign;
import net.minecraft.server.v1_5_R2.Packet131ItemData;
import net.minecraft.server.v1_5_R2.Packet132TileEntityData;
import net.minecraft.server.v1_5_R2.Packet14BlockDig;
import net.minecraft.server.v1_5_R2.Packet15Place;
import net.minecraft.server.v1_5_R2.Packet16BlockItemSwitch;
import net.minecraft.server.v1_5_R2.Packet17EntityLocationAction;
import net.minecraft.server.v1_5_R2.Packet18ArmAnimation;
import net.minecraft.server.v1_5_R2.Packet19EntityAction;
import net.minecraft.server.v1_5_R2.Packet1Login;
import net.minecraft.server.v1_5_R2.Packet200Statistic;
import net.minecraft.server.v1_5_R2.Packet201PlayerInfo;
import net.minecraft.server.v1_5_R2.Packet202Abilities;
import net.minecraft.server.v1_5_R2.Packet203TabComplete;
import net.minecraft.server.v1_5_R2.Packet204LocaleAndViewDistance;
import net.minecraft.server.v1_5_R2.Packet205ClientCommand;
import net.minecraft.server.v1_5_R2.Packet20NamedEntitySpawn;
import net.minecraft.server.v1_5_R2.Packet22Collect;
import net.minecraft.server.v1_5_R2.Packet23VehicleSpawn;
import net.minecraft.server.v1_5_R2.Packet24MobSpawn;
import net.minecraft.server.v1_5_R2.Packet250CustomPayload;
import net.minecraft.server.v1_5_R2.Packet252KeyResponse;
import net.minecraft.server.v1_5_R2.Packet253KeyRequest;
import net.minecraft.server.v1_5_R2.Packet254GetInfo;
import net.minecraft.server.v1_5_R2.Packet255KickDisconnect;
import net.minecraft.server.v1_5_R2.Packet25EntityPainting;
import net.minecraft.server.v1_5_R2.Packet26AddExpOrb;
import net.minecraft.server.v1_5_R2.Packet28EntityVelocity;
import net.minecraft.server.v1_5_R2.Packet29DestroyEntity;
import net.minecraft.server.v1_5_R2.Packet2Handshake;
import net.minecraft.server.v1_5_R2.Packet30Entity;
import net.minecraft.server.v1_5_R2.Packet34EntityTeleport;
import net.minecraft.server.v1_5_R2.Packet35EntityHeadRotation;
import net.minecraft.server.v1_5_R2.Packet38EntityStatus;
import net.minecraft.server.v1_5_R2.Packet39AttachEntity;
import net.minecraft.server.v1_5_R2.Packet3Chat;
import net.minecraft.server.v1_5_R2.Packet40EntityMetadata;
import net.minecraft.server.v1_5_R2.Packet41MobEffect;
import net.minecraft.server.v1_5_R2.Packet42RemoveMobEffect;
import net.minecraft.server.v1_5_R2.Packet43SetExperience;
import net.minecraft.server.v1_5_R2.Packet4UpdateTime;
import net.minecraft.server.v1_5_R2.Packet51MapChunk;
import net.minecraft.server.v1_5_R2.Packet52MultiBlockChange;
import net.minecraft.server.v1_5_R2.Packet53BlockChange;
import net.minecraft.server.v1_5_R2.Packet54PlayNoteBlock;
import net.minecraft.server.v1_5_R2.Packet55BlockBreakAnimation;
import net.minecraft.server.v1_5_R2.Packet56MapChunkBulk;
import net.minecraft.server.v1_5_R2.Packet5EntityEquipment;
import net.minecraft.server.v1_5_R2.Packet60Explosion;
import net.minecraft.server.v1_5_R2.Packet61WorldEvent;
import net.minecraft.server.v1_5_R2.Packet62NamedSoundEffect;
import net.minecraft.server.v1_5_R2.Packet6SpawnPosition;
import net.minecraft.server.v1_5_R2.Packet70Bed;
import net.minecraft.server.v1_5_R2.Packet71Weather;
import net.minecraft.server.v1_5_R2.Packet7UseEntity;
import net.minecraft.server.v1_5_R2.Packet8UpdateHealth;
import net.minecraft.server.v1_5_R2.Packet9Respawn;
import net.minecraft.server.v1_5_R2.PlayerConnection;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.reflection.SafeField;
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

	@Override
	public String getName() {
		return "a PlayerConnection hook";
	}

	@Override
	public boolean onEnable() {
		for (String incompatibility : incompatibilities) {
			Plugin plugin = CommonUtil.getPlugin(incompatibility);
			if (plugin != null) {
				// Fail!
				failPacketListener(plugin.getClass());
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

	private static void failPacketListener(Class<?> playerConnectionType) {
		Plugin plugin = CommonUtil.getPluginByClass(playerConnectionType);
		CommonPlugin.LOGGER_NETWORK.log(Level.SEVERE, "Failed to hook up a PlayerConnection to listen for received and sent packets");
		if (plugin == null) {
			CommonPlugin.LOGGER_NETWORK.log(Level.SEVERE, "This was caused by an unknown source, class: " + playerConnectionType.getName());
		} else {
			CommonPlugin.LOGGER_NETWORK.log(Level.SEVERE, "This was caused by a plugin conflict, namely " + plugin.getName());
		}
		CommonPlugin.LOGGER_NETWORK.log(Level.SEVERE, "Install ProtocolLib to restore protocol compatibility");
		CommonPlugin.LOGGER_NETWORK.log(Level.SEVERE, "Dev-bukkit: http://dev.bukkit.org/server-mods/protocollib/");
	}

	private static class CommonPlayerConnection extends PlayerConnection {
		private static final List<PlayerConnection> serverPlayerConnections = SafeField.get(CommonUtil.getMCServer().ae(), "c");
		private final PlayerConnection previous;
		private final PacketHandlerHooked handler;

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
			final boolean hasCommon = CommonPlugin.hasInstance();
			if (isReplaceable(ep.playerConnection)) {
				// Set it
				ep.playerConnection = connection;
				// Perform a little check-up in 10 ticks
				if (CommonPlugin.hasInstance()) {
					new Task(CommonPlugin.getInstance()) {
						@Override
						public void run() {
							if (hasCommon && ep.playerConnection != connection) {
								// Player connection has changed!
								failPacketListener(ep.playerConnection.getClass());
								CommonPlugin.getInstance().onCriticalFailure();
							}
						}
					}.start(10);
				}
			} else if (hasCommon) {
				// Plugin conflict!
				failPacketListener(ep.playerConnection.getClass());
				CommonPlugin.getInstance().onCriticalFailure();
				return;
			}
			registerPlayerConnection(ep, connection, true);
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
					CommonPlugin.LOGGER.log(Level.SEVERE, "Failed to (un)register PlayerConnection proxy...bad things may happen!");
					return;
				}
				// We failed to remove it in one go...
				// Remove the old one the next tick but then fail
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
			setPlayerConnection(ep, new CommonPlayerConnection(CommonUtil.getMCServer(), ep));
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
