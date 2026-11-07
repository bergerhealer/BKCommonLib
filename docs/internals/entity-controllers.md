# Entity Controller Internals

**Audience:** BKCommonLib maintainers.

Entity controllers customize server-side entity behavior by hooking the entity's NMS methods. Since the entity instance is replaced to add those hooks, controller installation also has to repair references held by Bukkit, the world, entity trackers, chunks, vehicles, and passengers. This guide describes that hook and replacement path; the public callbacks are documented in the [Entity Controllers guide](../entity-controllers.md).

## Key files

- [`EntityHook`](../../src/main/java/com/bergerkiller/bukkit/common/internal/hooks/EntityHook.java): Mountiplex class hook and routing from NMS calls to controller callbacks.
- [`CommonEntity`](../../src/main/java/com/bergerkiller/bukkit/common/entity/CommonEntity.java): controller assignment, hooked-instance creation, reference replacement entry point, and unhooking.
- [`EntityAddRemoveHandler`](../../src/main/java/com/bergerkiller/bukkit/common/internal/logic/EntityAddRemoveHandler.java): server-specific selection and lifecycle for entity bookkeeping.
- [`EntityAddRemoveHandler_1_8_to_1_13_2`](../../src/main/java/com/bergerkiller/bukkit/common/internal/logic/EntityAddRemoveHandler_1_8_to_1_13_2.java), [`EntityAddRemoveHandler_1_14_to_1_16_5`](../../src/main/java/com/bergerkiller/bukkit/common/internal/logic/EntityAddRemoveHandler_1_14_to_1_16_5.java), [`EntityAddRemoveHandler_1_17`](../../src/main/java/com/bergerkiller/bukkit/common/internal/logic/EntityAddRemoveHandler_1_17.java), [`EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem`](../../src/main/java/com/bergerkiller/bukkit/common/internal/logic/EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem.java), and [`EntityAddRemoveHandler_1_21_Paper_ChunkSystem`](../../src/main/java/com/bergerkiller/bukkit/common/internal/logic/EntityAddRemoveHandler_1_21_Paper_ChunkSystem.java): version- and distribution-specific reference replacement.
- [`EntityMoveHandler`](../../src/main/java/com/bergerkiller/bukkit/common/internal/logic/EntityMoveHandler.java) and its versioned implementations: movement physics and insertion points for block/entity collision behavior.
- [`DebugUtil`](../../src/main/java/com/bergerkiller/bukkit/common/utils/DebugUtil.java): deep reference-search helpers used to diagnose missed replacements.

## Hook and callback routing

`EntityHook` extends Mountiplex `ClassHook` and targets NMS `Entity`. Its annotated hook methods use version conditions where server method signatures or behavior differ. The hook stores the active `EntityController` and routes relevant calls—such as ticking, damage, interaction, removal, passenger positioning, and entity collision—to it. Where the controller's default implementation preserves vanilla behavior, it calls back into the original entity implementation through the hook's base handle.

Movement is handled separately by `EntityMoveHandler`: its version-specific implementations mirror the server movement and collision logic, while calling controller callbacks at the appropriate collision points. `EntityController` owns the movement handler and its switches for block collision, block activation, entity collision, and collision bounds.

Hook callbacks catch and log exceptions so an exception in controller code does not escape unnoticed into an NMS call. When changing a hook, preserve the base behavior and error handling unless the callback is deliberately replacing that behavior.

## Replacing and restoring an entity

`CommonEntity.setController()` calls `prepareHook()` before binding a non-default controller. If the entity is not already hooked, `CommonEntityType` and `EntityHook` create a hooked instance of the same recognized NMS entity type, transferring the entity's state. Unsupported custom entity implementations are rejected rather than being treated as an ordinary vanilla entity.

`CommonEntity.replaceEntity()` updates references around the replacement, including:

- The existing Bukkit `CraftEntity` wrapper's handle and, where applicable, its inventory handle.
- Vehicle/passenger relationships and entity-owned references.
- Server world storage, entity and chunk/section storage, and entity-tracking entries through `EntityAddRemoveHandler.INSTANCE`.

The old instance is marked removed, the new instance is marked valid, and the server-side replacement is repeated on the next tick to catch references that were not yet visible during the first pass. The selected `EntityAddRemoveHandler` is chosen by Minecraft version and, on Paper, by chunk-system implementation. Keep each implementation complete for its server's world storage, entity trackers, and chunk/section indexes.

When a hooked entity is detached and restored, BKCommonLib transfers its state back to an unhooked instance and runs the same replacement path. Entity removal cleanup also detaches assigned controllers. Maintain the ordering of this cleanup with related entity and tracking logic; stale references to either instance can break lookups or later entity operations.

## Reference-search debugging

The versioned replacement handlers contain commented-out calls to `DebugUtil.logInstances(oldEntity.getRaw())` (with local variable names varying by implementation). These are intended as an opt-in diagnostic when checking whether the previous NMS instance is still referenced after replacement. `logInstances()` recursively searches the server object graph and logs references; it is **very slow** and must not be enabled in a release build. Use it temporarily while investigating a replacement gap, then leave it disabled.

## Change entry points

- To change which NMS calls reach controllers, update `EntityHook` and verify its version-conditioned hook signatures.
- To change movement or collision behavior, update the applicable `EntityMoveHandler_*` implementation and preserve the matching server movement semantics.
- To add or repair server reference replacement, update the selected `EntityAddRemoveHandler_*`; inspect all supported server branches and Paper chunk-system variants.
- To diagnose a stale old-entity reference, inspect `CommonEntity.replaceEntity()` and the matching handler's replacement coverage, then use the disabled `DebugUtil.logInstances(...)` call only in a local diagnostic build.
