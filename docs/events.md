# General Events

BKCommonLib provides additional Bukkit events for entity and chunk lifecycle, mob spawning, sign editing, advancement progress, and bulk world changes. This page covers the general-purpose events in `com.bergerkiller.bukkit.common.events`; map-display and packet events are intentionally covered separately.

Register these events with Bukkit's normal `@EventHandler` mechanism. Cancellation is supported only by the events explicitly marked cancellable below.

## Entity lifecycle and movement

| Event | When it fires | Notes |
|---|---|---|
| [`EntityAddEvent`](../src/main/java/com/bergerkiller/bukkit/common/events/EntityAddEvent.java) | An entity is added to a world. | Provides the entity and the destination world. This is a useful point to inspect newly added entities or assign an entity controller; see [Entity Controllers](entity-controllers.md). |
| [`EntityRemoveEvent`](../src/main/java/com/bergerkiller/bukkit/common/events/EntityRemoveEvent.java) | An entity is removed from a world. | Provides the entity and the world it was removed from. This represents world removal, not necessarily final removal from the server. |
| [`EntityRemoveFromServerEvent`](../src/main/java/com/bergerkiller/bukkit/common/events/EntityRemoveFromServerEvent.java) | An entity is fully removed from the server and BKCommonLib cleans up its tracking. | Distinct from `EntityRemoveEvent`; use it when tracking an entity's full server lifetime rather than a world-level removal. It has no separate world property. |
| [`EntityMoveEvent`](../src/main/java/com/bergerkiller/bukkit/common/events/EntityMoveEvent.java) | An entity's position or rotation changed during a server tick. | Not cancellable. Provides from/to coordinates and rotation, or fills a caller-provided `Location`. It may fire frequently; avoid expensive work in handlers. BKCommonLib reuses its event instance while dispatching movement notifications, so do not retain the event object beyond the handler. |

Entity removal and re-addition can occur during operations such as world changes; do not assume every `EntityRemoveEvent` means the entity's overall server lifetime ended. Use the event that matches the lifecycle boundary you need.

## Chunk entity lifecycle

| Event | When it fires | Notes |
|---|---|---|
| [`ChunkLoadEntitiesEvent`](../src/main/java/com/bergerkiller/bukkit/common/events/ChunkLoadEntitiesEvent.java) | After the entities belonging to a chunk have finished loading. | This is specifically about entity readiness. Depending on the server/version, it can occur after the chunk itself loaded. |
| [`ChunkUnloadEntitiesEvent`](../src/main/java/com/bergerkiller/bukkit/common/events/ChunkUnloadEntitiesEvent.java) | After the entities belonging to a chunk have unloaded. | The chunk itself may remain loaded. Do not treat this as equivalent to Bukkit's `ChunkUnloadEvent`. |

These events let plugins react to entity readiness without inferring it from a chunk's block-data lifecycle. See the [ChunkFutureProvider guide](chunk-future-provider.md) for asynchronous tracking and waiting for chunk/entity readiness.

## Spawn, sign, and advancement events

| Event | When it fires | Cancellation and data |
|---|---|---|
| [`CreaturePreSpawnEvent`](../src/main/java/com/bergerkiller/bukkit/common/events/CreaturePreSpawnEvent.java) | Before one or more mobs are spawned at or around a location by the supported natural/spawner spawn paths. | A performance-oriented, cancellable event: cancelling can reject a group of mobs before Minecraft creates the individual entity instances and incurs their construction overhead. Provides Bukkit `EntityType`, `CreatureSpawnEvent.SpawnReason`, and a nearby spawn `Location`. This is not a replacement for `CreatureSpawnEvent`, which applies to individual entity spawns and other reasons. |
| [`SignEditTextEvent`](../src/main/java/com/bergerkiller/bukkit/common/events/SignEditTextEvent.java) | When a player places a sign, edits sign text, or places a pre-filled sign. | Cancellable; cancellation rolls back the edit or placement as appropriate. Provides the player, sign block, edited side, edit reason, and access to both sides' text. The line accessor is only valid during event handling; do not retain it. If another plugin cancels the underlying Bukkit sign-change event, this event is not fired. |
| [`PlayerAdvancementProgressEvent`](../src/main/java/com/bergerkiller/bukkit/common/events/PlayerAdvancementProgressEvent.java) | Before a player is awarded progress for an advancement criterion. | Cancellable; cancellation prevents that criterion progress from being recorded and prevents `PlayerAdvancementDoneEvent` from firing for completion based on that progress. Provides the player, advancement, and criterion name. |

For `CreaturePreSpawnEvent`, the location is the spawn attempt's location or nearby block position, not necessarily the exact position of each entity when the event covers a group. Its main advantage is avoiding the work of constructing entities that a plugin would immediately reject in a later per-entity spawn event. BKCommonLib only installs the compatibility hooks while listeners exist, to avoid the extra work when the event is unused.

For `SignEditTextEvent`, `EditReason` distinguishes `PLACE`, `EDIT`, and `CTRL_PICK_PLACE`. The event gives cross-version access through `SignSideLineAccessor`; prefer that over relying on the Bukkit `Sign` block state for compatibility-sensitive code.

## Bulk block changes

[`MultiBlockChangeEvent`](../src/main/java/com/bergerkiller/bukkit/common/events/MultiBlockChangeEvent.java) reports that a large operation changed blocks across one or more chunks. It provides the world, a `RegionChangeSource`, and the set of affected chunk coordinates; it does not enumerate every changed block and is not cancellable. It is intended for synchronizing plugin-maintained block data after bulk/out-of-band changes such as edits made by world-editing tools.

## Key files

- [`CommonPlugin`](../src/main/java/com/bergerkiller/bukkit/common/internal/CommonPlugin.java): dispatches entity add/remove lifecycle events and runs periodic cleanup.
- [`CommonEventFactory`](../src/main/java/com/bergerkiller/bukkit/common/events/CommonEventFactory.java): creates movement and pre-spawn notifications.
- [`CommonRegionChangeTracker`](../src/main/java/com/bergerkiller/bukkit/common/internal/CommonRegionChangeTracker.java): dispatches bulk block-change notifications.
- [`AdvancementDataPlayerHook`](../src/main/java/com/bergerkiller/bukkit/common/internal/hooks/AdvancementDataPlayerHook.java): intercepts criterion progress before it is recorded.
- [`CommonListener`](../src/main/java/com/bergerkiller/bukkit/common/internal/CommonListener.java): bridges Bukkit sign events to `SignEditTextEvent`.
