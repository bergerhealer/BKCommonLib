package com.bergerkiller.bukkit.common.internal;

import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerConnectionRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

import net.minecraft.server.v1_4_R1.*;

class CommonPacketListener extends PlayerConnection {
	private static final List<PlayerConnection> serverPlayerConnections = SafeField.get(CommonUtil.getMCServer().ae(), "d");
	private final PlayerConnection previous;

	public CommonPacketListener(MinecraftServer minecraftserver, EntityPlayer entityplayer) {
		super(minecraftserver, entityplayer.playerConnection.networkManager, entityplayer);
		previous = entityplayer.playerConnection;
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
		return playerConnection instanceof CommonPacketListener || playerConnection.getClass() == PlayerConnection.class;
	}

	private static void setPlayerConnection(final EntityPlayer ep, final PlayerConnection connection) {
		final boolean hasCommon = CommonPlugin.getInstance() != null;
		if (isReplaceable(ep.playerConnection)) {
			// Set it
			ep.playerConnection = connection;
			// Perform a little check-up in 10 ticks
			new Task(CommonPlugin.getInstance()) {
				@Override
				public void run() {
					if (hasCommon && ep.playerConnection != connection) {
						// Player connection has changed!
						CommonPlugin.getInstance().failPacketListener(ep.playerConnection.getClass());
					}
				}
			}.start(10);
		} else if (hasCommon) {
			// Plugin conflict!
			CommonPlugin.getInstance().failPacketListener(ep.playerConnection.getClass());
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
			if (!retry && CommonPlugin.getInstance() != null) {
				CommonPlugin.getInstance().log(Level.SEVERE, "Failed to (un)register PlayerConnection proxy...bad things may happen!");
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
		if (ep.playerConnection instanceof CommonPacketListener) {
			return;
		}
		setPlayerConnection(ep, new CommonPacketListener(CommonUtil.getMCServer(), ep));
	}

	public static void unbind(Player player) {
		final EntityPlayer ep = CommonNMS.getNative(player);
		final PlayerConnection previous = ep.playerConnection;
		if (previous instanceof CommonPacketListener) {
			PlayerConnection replacement = ((CommonPacketListener) previous).previous;
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
		final CommonPlugin instance = CommonPlugin.getInstance();
		if (instance == null) {
			return true;
		}
		return instance.onPacketReceive(CommonNMS.getPlayer(this.player), packet);
	}

	@Override
	public void sendPacket(Packet packet) {
		final CommonPlugin instance = CommonPlugin.getInstance();
		if (instance == null || instance.onPacketSend(CommonNMS.getPlayer(this.player), packet)) {
			super.sendPacket(packet);
		}
	}
}
