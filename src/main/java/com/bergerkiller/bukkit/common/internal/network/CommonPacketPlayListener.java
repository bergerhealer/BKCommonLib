package com.bergerkiller.bukkit.common.internal.network;

import net.minecraft.server.EnumProtocol;
import net.minecraft.server.IChatBaseComponent;
import net.minecraft.server.Packet;
import net.minecraft.server.PacketListener;
import net.minecraft.server.PacketPlayInAbilities;
import net.minecraft.server.PacketPlayInArmAnimation;
import net.minecraft.server.PacketPlayInBlockDig;
import net.minecraft.server.PacketPlayInBlockPlace;
import net.minecraft.server.PacketPlayInChat;
import net.minecraft.server.PacketPlayInClientCommand;
import net.minecraft.server.PacketPlayInCloseWindow;
import net.minecraft.server.PacketPlayInCustomPayload;
import net.minecraft.server.PacketPlayInEnchantItem;
import net.minecraft.server.PacketPlayInEntityAction;
import net.minecraft.server.PacketPlayInFlying;
import net.minecraft.server.PacketPlayInHeldItemSlot;
import net.minecraft.server.PacketPlayInKeepAlive;
import net.minecraft.server.PacketPlayInListener;
import net.minecraft.server.PacketPlayInSetCreativeSlot;
import net.minecraft.server.PacketPlayInSettings;
import net.minecraft.server.PacketPlayInSteerVehicle;
import net.minecraft.server.PacketPlayInTabComplete;
import net.minecraft.server.PacketPlayInTransaction;
import net.minecraft.server.PacketPlayInUpdateSign;
import net.minecraft.server.PacketPlayInUseEntity;
import net.minecraft.server.PacketPlayInWindowClick;
import net.minecraft.server.PacketPlayOutAbilities;
import net.minecraft.server.PacketPlayOutAnimation;
import net.minecraft.server.PacketPlayOutAttachEntity;
import net.minecraft.server.PacketPlayOutBed;
import net.minecraft.server.PacketPlayOutBlockAction;
import net.minecraft.server.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.PacketPlayOutBlockChange;
import net.minecraft.server.PacketPlayOutChat;
import net.minecraft.server.PacketPlayOutCloseWindow;
import net.minecraft.server.PacketPlayOutCollect;
import net.minecraft.server.PacketPlayOutCraftProgressBar;
import net.minecraft.server.PacketPlayOutCustomPayload;
import net.minecraft.server.PacketPlayOutEntity;
import net.minecraft.server.PacketPlayOutEntityDestroy;
import net.minecraft.server.PacketPlayOutEntityEffect;
import net.minecraft.server.PacketPlayOutEntityEquipment;
import net.minecraft.server.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.PacketPlayOutEntityMetadata;
import net.minecraft.server.PacketPlayOutEntityStatus;
import net.minecraft.server.PacketPlayOutEntityTeleport;
import net.minecraft.server.PacketPlayOutEntityVelocity;
import net.minecraft.server.PacketPlayOutExperience;
import net.minecraft.server.PacketPlayOutExplosion;
import net.minecraft.server.PacketPlayOutGameStateChange;
import net.minecraft.server.PacketPlayOutHeldItemSlot;
import net.minecraft.server.PacketPlayOutKeepAlive;
import net.minecraft.server.PacketPlayOutKickDisconnect;
import net.minecraft.server.PacketPlayOutListener;
import net.minecraft.server.PacketPlayOutLogin;
import net.minecraft.server.PacketPlayOutMap;
import net.minecraft.server.PacketPlayOutMapChunk;
import net.minecraft.server.PacketPlayOutMapChunkBulk;
import net.minecraft.server.PacketPlayOutMultiBlockChange;
import net.minecraft.server.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.PacketPlayOutOpenSignEditor;
import net.minecraft.server.PacketPlayOutOpenWindow;
import net.minecraft.server.PacketPlayOutPlayerInfo;
import net.minecraft.server.PacketPlayOutPosition;
import net.minecraft.server.PacketPlayOutRemoveEntityEffect;
import net.minecraft.server.PacketPlayOutRespawn;
import net.minecraft.server.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.PacketPlayOutScoreboardObjective;
import net.minecraft.server.PacketPlayOutScoreboardScore;
import net.minecraft.server.PacketPlayOutScoreboardTeam;
import net.minecraft.server.PacketPlayOutSetSlot;
import net.minecraft.server.PacketPlayOutSpawnEntity;
import net.minecraft.server.PacketPlayOutSpawnEntityExperienceOrb;
import net.minecraft.server.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.PacketPlayOutSpawnEntityPainting;
import net.minecraft.server.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.PacketPlayOutSpawnPosition;
import net.minecraft.server.PacketPlayOutStatistic;
import net.minecraft.server.PacketPlayOutTabComplete;
import net.minecraft.server.PacketPlayOutTileEntityData;
import net.minecraft.server.PacketPlayOutTransaction;
import net.minecraft.server.PacketPlayOutUpdateAttributes;
import net.minecraft.server.PacketPlayOutUpdateHealth;
import net.minecraft.server.PacketPlayOutUpdateSign;
import net.minecraft.server.PacketPlayOutUpdateTime;
import net.minecraft.server.PacketPlayOutWindowItems;
import net.minecraft.server.PacketPlayOutWorldEvent;
import net.minecraft.server.PacketPlayOutWorldParticles;

public class CommonPacketPlayListener implements PacketPlayInListener, PacketPlayOutListener {
	private final PacketListener baseListener;

	public CommonPacketPlayListener(PacketListener baseListener) {
		if (!(baseListener instanceof PacketPlayInListener) || !(baseListener instanceof PacketPlayOutListener)) {
			throw new RuntimeException("Expected a base listener that implements both in and outgoing listening!");
		}
		this.baseListener = baseListener;
	}

	private boolean onReceive(Packet packet) {
		return true;
	}

	private boolean onSend(Packet packet) {
		return true;
	}

	@Override
	public void a() {
		baseListener.a();
	}

	@Override
	public void a(IChatBaseComponent arg0) {
		baseListener.a(arg0);
	}

	@Override
	public void a(EnumProtocol arg0, EnumProtocol arg1) {
		baseListener.a(arg0, arg1);
	}

	@Override
	public void a(PacketPlayInArmAnimation arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInChat arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInTabComplete arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInClientCommand arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInSettings arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInTransaction arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInEnchantItem arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInWindowClick arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInCloseWindow arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInCustomPayload arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInUseEntity arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInKeepAlive arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInFlying arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInAbilities arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInBlockDig arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInEntityAction arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInSteerVehicle arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInHeldItemSlot arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInSetCreativeSlot arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInUpdateSign arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayInBlockPlace arg0) {
		if (onReceive(arg0)) {
			((PacketPlayInListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutSpawnEntity arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutSpawnEntityExperienceOrb arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutSpawnEntityWeather arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutSpawnEntityLiving arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutScoreboardObjective arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutSpawnEntityPainting arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutNamedEntitySpawn arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutAnimation arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutStatistic arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutBlockBreakAnimation arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutOpenSignEditor arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutTileEntityData arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutBlockAction arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutBlockChange arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutChat arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutTabComplete arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutMultiBlockChange arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutMap arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutTransaction arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutCloseWindow arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutWindowItems arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutOpenWindow arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutCraftProgressBar arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutSetSlot arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutCustomPayload arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutKickDisconnect arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutBed arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutEntityStatus arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutAttachEntity arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutExplosion arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutGameStateChange arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutKeepAlive arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutMapChunk arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutMapChunkBulk arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutWorldEvent arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutLogin arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutEntity arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutPosition arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutWorldParticles arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutAbilities arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutPlayerInfo arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutEntityDestroy arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutRemoveEntityEffect arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutRespawn arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutEntityHeadRotation arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutHeldItemSlot arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutScoreboardDisplayObjective arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutEntityMetadata arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutEntityVelocity arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutEntityEquipment arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutExperience arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutUpdateHealth arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutScoreboardTeam arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutScoreboardScore arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutSpawnPosition arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutUpdateTime arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutUpdateSign arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutNamedSoundEffect arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutCollect arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutEntityTeleport arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutUpdateAttributes arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}

	@Override
	public void a(PacketPlayOutEntityEffect arg0) {
		if (onSend(arg0)) {
			((PacketPlayOutListener) baseListener).a(arg0);
		}
	}
}
