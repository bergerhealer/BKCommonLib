# Packet Protocol

BKCommonLib exposes Minecraft's client/server packet stream through `PacketUtil`, `PacketListener`, `PacketMonitor`, `CommonPacket`, and `PacketType`. Packet names and fields vary between Minecraft versions; `PacketType` provides cross-version identifiers, while generated `*PacketHandle` classes expose packet fields where a handle is available.

For entity metadata sent at spawn or updated separately, see [Entity Metadata with DataWatcher](protocol-datawatcher.md).

## Client game version

When choosing packet-specific behavior or deciding whether a client supports a feature, use `PlayerGameInfo` to get the game version of the player. When ViaVersion is installed, BKCommonLib uses it to detect the version the player is using; otherwise, the information defaults to the server's native game version.

```java
import com.bergerkiller.bukkit.common.protocol.PlayerGameInfo;

PlayerGameInfo gameInfo = PlayerGameInfo.of(player);
if (gameInfo.evaluateVersion(">=", "1.20.5")) {
    // Use behavior supported by clients running 1.20.5 or newer
}
```

`version()` returns the latest possible version for the player, and `evaluateVersion(operand, version)` supports comparisons such as `>=`, `<`, and `==`. This is useful when sending packets or selecting client-specific packet handling; do not assume every connected client uses the server's protocol version.

## Listening to packets

Use a `PacketMonitor` to observe traffic without changing it. Use a `PacketListener` when the packet must be modified, replaced, or cancelled. Both interfaces receive outgoing (server-to-client) and incoming (client-to-server) callbacks, and the `PacketType` arguments filter which packets are delivered.

```java
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {
    private final PacketMonitor monitor = new PacketMonitor() {
        @Override
        public void onMonitorPacketSend(CommonPacket packet, Player player) {
            getLogger().fine("Sending " + packet.getType() + " to " + player.getName());
        }

        @Override
        public void onMonitorPacketReceive(CommonPacket packet, Player player) {
            getLogger().fine("Received " + packet.getType() + " from " + player.getName());
        }
    };

    @Override
    public void onEnable() {
        PacketUtil.addPacketMonitor(this, monitor,
                PacketType.OUT_ENTITY_SPAWN,
                PacketType.IN_INTERACT);
    }

    @Override
    public void onDisable() {
        PacketUtil.removePacketMonitor(monitor);
    }
}
```

Listeners use the same registration pattern. In `PacketReceiveEvent` or `PacketSendEvent`, `event.getType()` identifies the packet, `event.getPacket()` returns its `CommonPacket`, and `event.getPlayer()` identifies the connection. A listener can change fields through the packet handle, replace the packet with `event.setPacket(...)`, or cancel it with `event.setCancelled(true)`. Monitors do not receive a cancellable event and cannot change packet contents.

Minecraft packets are increasingly immutable, so changing fields on the existing packet may not be possible or appropriate. In that case, create a replacement packet and call `event.setPacket(replacement)`; the replacement can be either a `CommonPacket` or a generated `PacketHandle`. This replaces the packet that will be processed or sent.

```java
private final PacketListener listener = new PacketListener() {
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getType() == PacketType.IN_CHAT) {
            // Inspect or modify the incoming packet, or cancel it.
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        // Inspect outgoing packets registered for this listener.
    }
};

// Register, for example, during onEnable():
PacketUtil.addPacketListener(this, listener, PacketType.IN_CHAT);

// Unregister during onDisable(), if the plugin is not being disabled by Bukkit:
PacketUtil.removePacketListener(listener);
```

`PacketUtil.removePacketListeners(plugin)` removes every listener and monitor registered for a plugin. Registration through `PluginBase.register(...)` is a convenience alternative that associates the registration with that plugin.

### Threading

Incoming packet handling normally runs on the main thread, while outgoing packets can be sent from any thread. Still, guard code that accesses Bukkit state with `CommonUtil.isMainThread()` rather than relying on the current packet path or caller. Packet send callbacks must be thread-safe and should not access Bukkit state that is restricted to the main thread without scheduling it there.

When a packet contains an entity ID, `WorldUtil.getEntityById(world, entityId)` is a thread-safe way to look up the Bukkit entity, including from packet callbacks. This only makes the lookup safe; interacting with the returned entity or other Bukkit state may still need to happen on the main thread. Capture the ID and world, then schedule that work with `CommonUtil.nextTick()` when necessary.

If an incoming packet requires main-thread work and the callback is off the main thread, cancel the original packet and schedule the needed work with `CommonUtil.nextTick()`. Capture the specific values needed before scheduling rather than retaining the event. For example:

```java
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundContainerClosePacketHandle;
import org.bukkit.entity.Player;

@Override
public void onPacketReceive(PacketReceiveEvent event) {
    if (event.getType() == PacketType.IN_WINDOW_CLOSE && !CommonUtil.isMainThread()) {
        ServerboundContainerClosePacketHandle packet =
                ServerboundContainerClosePacketHandle.createHandle(event.getPacket().getHandle());
        int windowId = packet.getWindowId();
        Player player = event.getPlayer();

        event.setCancelled(true);
        CommonUtil.nextTick(() -> handleWindowClose(player, windowId));
    }
}
```

Because the original packet is cancelled, its normal server-side handling will not occur; `handleWindowClose` represents the plugin's main-thread logic for that action. Do not keep or use the packet event after the callback returns. If the callback is already on the main thread, handle it there without cancelling the packet. `CommonUtil.nextTick()` is also useful for deferring work that should not run inside packet processing.

Do not call `sendPacket()` from inside a packet listener or monitor; doing so can cause packet loss or unpredictable ordering. If a callback needs to send a follow-up packet, use `PacketUtil.queuePacket(...)`, which queues it after the current packet.

### Reading packet fields

Use the packet type to check what the event contains, then wrap its raw handle with the matching generated handle class. The handle exposes cross-version getters and setters for fields BKCommonLib supports:

```java
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundContainerClosePacketHandle;
import com.bergerkiller.bukkit.common.protocol.PacketType;

@Override
public void onPacketReceive(PacketReceiveEvent event) {
    if (event.getType() == PacketType.IN_WINDOW_CLOSE) {
        ServerboundContainerClosePacketHandle packet =
                ServerboundContainerClosePacketHandle.createHandle(event.getPacket().getHandle());
        int windowId = packet.getWindowId();
        // Inspect the window id, or change it with packet.setWindowId(...).
    }
}
```

Use the handle matching the packet type; wrapping a packet with an unrelated handle is invalid. The packet-type table lists the handle class to use where one is available.

For packets identifying the player's main or off hand, see [Human Hands](human-hand.md) for hand roles, physical left/right hands, and inventory access across versions.

## Sending and receiving packets

`PacketUtil.sendPacket(player, packet)` sends an outgoing server-to-client packet and lets packet listeners process it. Pass a generated `PacketHandle` or a `CommonPacket`. The three-argument overload accepts `throughListeners`; passing `false` is the silent mode, which sends the packet without passing it through packet listeners. The default overload uses `true`.

```java
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacketHandle;

ClientboundSetHeldSlotPacketHandle packet = /* create and configure packet */;
PacketUtil.sendPacket(player, packet);        // Through packet listeners
PacketUtil.sendPacket(player, packet, false); // Silent: skips packet listeners
```

Do not call `sendPacket()` from inside a packet listener or monitor; doing so can cause packet loss or unpredictable ordering. If a callback needs to send a follow-up packet, use `PacketUtil.queuePacket(...)`, which queues it after the current packet.

`PacketUtil.receivePacket(player, packet)` does the opposite: it simulates a packet sent by that player's client and passes it to the server's packet handling path. It accepts a `PacketHandle` or `CommonPacket`; there is no silent-mode parameter. This is a low-level API for cases where server-side packet processing is intentionally needed, not a way to send a packet to the client.

```java
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundContainerClosePacketHandle;

ServerboundContainerClosePacketHandle packet = /* create and configure packet */;
PacketUtil.receivePacket(player, packet); // Process as if received from this player's client
```

## Packet types

`OUT_` packet types travel from the server to the client; `IN_` types travel from the client to the server. The middle column names the generated packet handle used for version-compatible field access, when one exists. Select a type by the operation you want to observe or change, rather than by a raw protocol ID; packet IDs and packet layouts vary by protocol state and Minecraft version. Descriptions use the protocol packet purposes summarized by the [Minecraft protocol reference](https://wikivg.booky.dev/Protocol).

### Outgoing: server to client

| `PacketType` constant | Packet handle | Use this for |
|---|---|---|
| `DEFAULT` | `PacketHandle` | Generic base packet type, not a particular gameplay packet. Register a concrete packet type instead. |
| `OUT_ABILITIES` | `ClientboundPlayerAbilitiesPacketHandle` | Updating the player's invulnerability, flight, build mode, and movement speeds. |
| `OUT_ADVANCEMENTS` | `ClientboundUpdateAdvancementsPacketHandle` | Sending advancement definitions, progress, and removal updates to the advancement screen. |
| `OUT_BLOCK_ACTION` | `ClientboundBlockEventPacketHandle` | A block's special action or animation, such as a note-block sound or a container event. |
| `OUT_COLLECT` | `ClientboundTakeItemEntityPacketHandle` | Showing an item or experience orb being picked up by an entity. |
| `OUT_CUSTOM_PAYLOAD` | `ClientboundCustomPayloadPacketHandle` | Sending plugin, mod, or other custom-channel data to the client. |
| `OUT_EXPERIENCE` | `ClientboundSetExperiencePacketHandle` | Updating the player's experience bar, level, or total experience. |
| `OUT_EXPLOSION` | — | Sending an explosion effect and its associated knockback or block interaction. |
| `OUT_GAME_STATE_CHANGE` | — | Changing client game state, such as weather transitions or other global game events. |
| `OUT_HELD_ITEM_SLOT` | `ClientboundSetHeldSlotPacketHandle` | Selecting the player's active hotbar slot on the client. |
| `OUT_KEEP_ALIVE` | `ClientboundKeepAlivePacketHandle` | Connection keep-alive challenge/response tracking. |
| `OUT_KICK_DISCONNECT` | `ClientboundDisconnectPacketHandle` | Disconnecting the client with a reason or message. |
| `OUT_LOGIN` | `ClientboundLoginPacketHandle` | Initial play-state login data, including player/world setup. |
| `OUT_MAP` | `ClientboundMapItemDataPacketHandle` | Updating filled-map pixels, scale, decorations, or tracking position. |
| `OUT_MAP_CHUNK` | `ClientboundLevelChunkWithLightPacketHandle` | Sending chunk terrain and lighting data to the client. |
| `OUT_PLAYER_LIST_HEADER_FOOTER` | `ClientboundTabListPacketHandle` | Changing the header or footer shown in the player list. |
| `OUT_NAMED_SOUND_EFFECT` | `ClientboundSoundPacketHandle` | Playing a registered Minecraft sound at a world location. |
| `OUT_OPEN_SIGN_EDITOR` | `ClientboundOpenSignEditorPacketHandle` | Opening the sign-editing screen for a sign position. |
| `OUT_PLAYER_INFO_UPDATE` | `ClientboundPlayerInfoUpdatePacketHandle` | Adding or updating player-list data such as game mode, latency, profile, or display name. |
| `OUT_PLAYER_INFO_REMOVE` | `ClientboundPlayerInfoRemovePacketHandle` | Removing profiles from the player list. |
| `OUT_POSITION` | `ClientboundPlayerPositionPacketHandle` | Teleporting or correcting the receiving player's position. |
| `OUT_ROTATION` | `ClientboundPlayerRotationPacketHandle` | Correcting the receiving player's yaw or pitch. |
| `OUT_RESPAWN` | `ClientboundRespawnPacketHandle` | Placing the player into a world/dimension or respawning them. |
| `OUT_SPAWN_POSITION` | `ClientboundSetDefaultSpawnPositionPacketHandle` | Updating the world spawn position and its angle. |
| `OUT_STATISTIC` | — | Updating the client's statistics. |
| `OUT_TAB_COMPLETE` | — | Sending command-line completion suggestions to the client. |
| `OUT_TILE_ENTITY_DATA` | `ClientboundBlockEntityDataPacketHandle` | Updating block-entity data, such as sign text or a block entity's client state. |
| `OUT_UPDATE_HEALTH` | `ClientboundSetHealthPacketHandle` | Updating player health, food, and saturation. |
| `OUT_UPDATE_TIME` | `ClientboundSetTimePacketHandle` | Updating world age and day time shown to the client. |
| `OUT_WORLD_EVENT` | `ClientboundLevelEventPacketHandle` | Playing a world-level event effect at a location, such as block breaking or a dispenser activating. |
| `OUT_WORLD_PARTICLES` | `ClientboundLevelParticlesPacketHandle` | Spawning particles in the world. |
| `OUT_BLOCK_BREAK_ANIMATION` | `ClientboundBlockDestructionPacketHandle` | Showing a cracking/break-progress overlay on a block. |
| `OUT_BLOCK_CHANGE` | `ClientboundBlockUpdatePacketHandle` | Changing the client-visible state of one block. |
| `OUT_BOSS` | — | Creating, updating, or removing a boss bar. |
| `OUT_CAMERA` | `ClientboundSetCameraPacketHandle` | Changing the entity the client uses as its camera. |
| `OUT_CUSTOM_SOUND_EFFECT` | `ClientboundCustomSoundPacketHandle` | Playing a namespaced/custom sound effect at a location or source. |
| `OUT_RESOURCE_PACK_PUSH` | `ClientboundResourcePackPushPacketHandle` | Asking the client to download or apply a resource pack. |
| `OUT_RESOURCE_PACK_POP` | `ClientboundResourcePackPopPacketHandle` | Removing or clearing an applied server resource pack. |
| `OUT_SERVER_DIFFICULTY` | `ClientboundChangeDifficultyPacketHandle` | Updating the difficulty value known to the client. |
| `OUT_SET_COOLDOWN` | `ClientboundCooldownPacketHandle` | Showing an item cooldown to the client. |
| `OUT_UNLOAD_CHUNK` | `ClientboundForgetLevelChunkPacketHandle` | Telling the client to discard a chunk. |
| `OUT_SCOREBOARD_DISPLAY_OBJECTIVE` | `ClientboundSetDisplayObjectivePacketHandle` | Selecting the scoreboard objective displayed in a HUD slot. |
| `OUT_SCOREBOARD_OBJECTIVE` | `ClientboundSetObjectivePacketHandle` | Creating, updating, or removing a scoreboard objective. |
| `OUT_SCOREBOARD_SCORE` | `ClientboundSetScorePacketHandle` | Setting a score entry for a scoreboard objective. |
| `OUT_SCOREBOARD_SCORE_RESET` | `ClientboundResetScorePacketHandle` | Removing a score entry. |
| `OUT_SCOREBOARD_TEAM` | `ClientboundSetPlayerTeamPacketHandle` | Creating or changing a scoreboard team, membership, or team display options. |
| `OUT_WINDOW_CLOSE` | `ClientboundContainerClosePacketHandle` | Closing an inventory/container screen on the client. |
| `OUT_WINDOW_DATA` | `ClientboundContainerSetDataPacketHandle` | Updating a container property, such as a furnace or brewing progress value. |
| `OUT_WINDOW_OPEN` | `ClientboundOpenScreenPacketHandle` | Opening an inventory/container screen. |
| `OUT_WINDOW_SET_SLOT` | `ClientboundContainerSetSlotPacketHandle` | Updating one inventory or container slot. |
| `OUT_WINDOW_ITEMS` | `ClientboundContainerSetContentPacketHandle` | Replacing the contents of a container/player inventory window. |
| `OUT_ENTITY_SPAWN` | `ClientboundAddEntityPacketHandle` | Spawning a non-player entity or object on the client. |
| `OUT_ENTITY_SPAWN_NAMED` | `ClientboundAddPlayerPacketHandle` | Spawning a player entity by profile. |
| `OUT_ENTITY_SPAWN_EXPORB` | `ClientboundAddExperienceOrbPacketHandle` | Spawning an experience orb on versions that use a dedicated packet. |
| `OUT_ENTITY_SPAWN_LIVING` | `ClientboundAddMobPacketHandle` | Spawning a living mob with its initial position and orientation. |
| `OUT_ENTITY_SPAWN_PAINTING` | `ClientboundAddPaintingPacketHandle` | Spawning a painting at a position and facing. |
| `OUT_ENTITY_SPAWN_WITHER` | `PacketPlayOutSpawnEntityWeatherHandle` | Spawning a weather entity/effect, notably a lightning bolt. |
| `OUT_ENTITY_DESTROY` | `ClientboundRemoveEntitiesPacketHandle` | Removing one or more entities from the client world. |
| `OUT_ENTITY_ATTACH` | `ClientboundSetEntityLinkPacketHandle` | Attaching a leash between an entity and its holder. Vehicle/passenger relationships use `OUT_MOUNT`. |
| `OUT_ENTITY_EFFECT_ADD` | `ClientboundUpdateMobEffectPacketHandle` | Adding or updating a status effect on an entity. |
| `OUT_ENTITY_EFFECT_REMOVE` | `ClientboundRemoveMobEffectPacketHandle` | Removing a status effect from an entity. |
| `OUT_ENTITY_EQUIPMENT` | `ClientboundSetEquipmentPacketHandle` | Updating the equipment worn or held by an entity. |
| `OUT_ENTITY_HEAD_ROTATION` | `ClientboundRotateHeadPacketHandle` | Rotating an entity's head independently of its body. |
| `OUT_ENTITY_LOOK` | `ClientboundMoveEntityPacketHandle` | Updating an entity's rotation without relative position movement. |
| `OUT_ENTITY_ANIMATION` | `ClientboundAnimatePacketHandle` | Playing a built-in entity animation, such as arm swing or hurt animation. |
| `OUT_ENTITY_METADATA` | `ClientboundSetEntityDataPacketHandle` | Updating synchronized entity metadata, including flags and display properties. |
| `OUT_ENTITY_STATUS` | `ClientboundEntityEventPacketHandle` | Sending a built-in entity status event, such as hurt, death, or a mob-specific state. |
| `OUT_ENTITY_TELEPORT` | `ClientboundEntityPositionSyncPacketHandle` | Setting an entity's absolute synchronized position and rotation. |
| `OUT_ENTITY_VELOCITY` | `ClientboundSetEntityMotionPacketHandle` | Setting an entity's velocity or motion. |
| `OUT_ENTITY_MOVE` | `ClientboundMoveEntityPacketHandle` | Moving an entity by a relative position delta. |
| `OUT_ENTITY_MOVE_LOOK` | `ClientboundMoveEntityPacketHandle` | Moving an entity by a relative position delta and updating its rotation. |
| `OUT_ENTITY_UPDATE_ATTRIBUTES` | `ClientboundUpdateAttributesPacketHandle` | Updating entity attributes such as max health or movement speed. |
| `OUT_MOUNT` | `ClientboundSetPassengersPacketHandle` | Updating the vehicle/passenger relationship for entities. |
| `OUT_VEHICLE_MOVE` | `ClientboundMoveVehiclePacketHandle` | Updating the position and rotation of the vehicle the client controls. |
| `OUT_UPDATE_SIGN` | `PacketPlayOutUpdateSignHandle` | Updating sign text on Minecraft 1.8.8. Newer versions use block-entity data. |
| `OUT_BUNDLE` | `ClientboundBundlePacketHandle` | Grouping multiple packets to be processed together in one network tick. |

### Incoming: client to server

| `PacketType` constant | Packet handle | Use this for |
|---|---|---|
| `IN_ABILITIES` | `ServerboundPlayerAbilitiesPacketHandle` | Client-reported player ability state, such as whether the player is flying. |
| `IN_BOAT_MOVE` | `ServerboundPaddleBoatPacketHandle` | The client reporting left/right paddle input for a boat. |
| `IN_CHAT` | `ServerboundChatPacketHandle` | A player sending a chat message. |
| `IN_CLIENT_COMMAND` | `ServerboundClientCommandPacketHandle` | Client requests such as respawn or requesting statistics. |
| `IN_CUSTOM_PAYLOAD` | — | Plugin, mod, or other custom-channel data sent by the client. |
| `IN_PLAYER_COMMAND` | `ServerboundPlayerCommandPacketHandle` | Entity actions such as starting to sneak/sprint, leaving a bed, or horse jump; not chat commands. |
| `IN_LOOK` | `ServerboundMovePlayerPacketHandle` | Player rotation/look direction without position movement. |
| `IN_POSITION` | `ServerboundMovePlayerPacketHandle` | Player position movement without a rotation change. |
| `IN_POSITION_LOOK` | `ServerboundMovePlayerPacketHandle` | Player position and rotation movement. |
| `IN_HELD_ITEM_SLOT` | `ServerboundSetCarriedItemPacketHandle` | The player selecting a hotbar slot. |
| `IN_KEEP_ALIVE` | `ServerboundKeepAlivePacketHandle` | Client response to a server keep-alive packet. |
| `IN_SPECTATE` | `ServerboundTeleportToEntityPacketHandle` | A spectator requesting to view another entity. |
| `IN_SET_CREATIVE_SLOT` | `ServerboundSetCreativeModeSlotPacketHandle` | A creative-mode player changing an inventory slot. |
| `IN_SETTINGS` | `ServerboundClientInformationPacketHandle` | Client preferences such as locale, view distance, chat mode, and skin-part visibility. |
| `IN_STEER_VEHICLE` | `ServerboundPlayerInputPacketHandle` | Player movement and jump/sneak input; older versions use this for vehicle steering. |
| `IN_TAB_COMPLETE` | — | A client request for command-line completion suggestions. |
| `IN_TELEPORT_ACCEPT` | `ServerboundAcceptTeleportationPacketHandle` | Client acknowledgement of a server-requested teleport. |
| `IN_UPDATE_SIGN` | `ServerboundSignUpdatePacketHandle` | A player submitting edited sign text. |
| `IN_PLAYER_ACTION` | `ServerboundPlayerActionPacketHandle` | Block digging, dropping items, swapping hands, and related player actions. |
| `IN_PUNCH` | `ServerboundPunchPacketHandle` | The player swinging an arm. |
| `IN_SWING` | `ServerboundPunchPacketHandle` | Deprecated alias for `IN_PUNCH`; use `IN_PUNCH`. |
| `IN_INTERACT` | `ServerboundInteractPacketHandle` | General interaction with an entity, including interact-at-position actions. |
| `IN_ATTACK` | `ServerboundAttackPacketHandle` | The player attacking an entity. |
| `IN_USE_ITEM` | `ServerboundUseItemPacketHandle` | The player using an item in a hand, not targeting a block. |
| `IN_USE_ITEM_ON` | `ServerboundUseItemOnPacketHandle` | The player using an item on a block (for example, placing a block). |
| `IN_VEHICLE_MOVE` | `ServerboundMoveVehiclePacketHandle` | Client-reported movement of the vehicle being controlled. |
| `IN_CLIENT_TICK_END` | — | Client marker indicating the end of a client tick on versions that send it. |
| `IN_WINDOW_CLOSE` | `ServerboundContainerClosePacketHandle` | The player closing an inventory/container screen. |
| `IN_WINDOW_ENCHANT_ITEM` | `ServerboundContainerButtonClickPacketHandle` | A button press in a container, such as an enchanting-table option. |
| `IN_WINDOW_RESOURCEPACK_STATUS` | `ServerboundResourcePackPacketHandle` | Client response to a server resource-pack request, such as accepted or declined. |
| `IN_WINDOW_CLICK` | `ServerboundContainerClickPacketHandle` | A player clicking or moving items in an inventory/container window. |

The handle names above are generated cross-version accessors for the corresponding packet classes. For less common or version-specific packet purposes, consult the linked protocol reference; `PacketType` only includes the packet types BKCommonLib explicitly exposes.
