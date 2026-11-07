# ForcedChunk Implementation

**Audience:** BKCommonLib maintainers changing the forced-chunk lifecycle, server ticket integration, asynchronous loading, or leak diagnostics. Plugin authors should use [`ForcedChunk`](../forced-chunks.md) and must close every ticket they own.

## Key implementation files

- [`ForcedChunk`](../../src/main/java/com/bergerkiller/bukkit/common/chunk/ForcedChunk.java): atomic wrapper around a shared `ForcedChunkEntry`; implements clone, transfer, and idempotent close.
- [`ForcedChunkManager`](../../src/main/java/com/bergerkiller/bukkit/common/chunk/ForcedChunkManager.java): public manager abstraction, creator configuration, and cleaner hookup.
- [`CommonForcedChunkManager`](../../src/main/java/com/bergerkiller/bukkit/common/internal/CommonForcedChunkManager.java): world/radius maps, reference counts, server ticket application, async loading, timeout handling, and unload compatibility.
- [`ForcedChunkCleaner`](../../src/main/java/com/bergerkiller/bukkit/common/chunk/ForcedChunkCleaner.java): JDK Cleaner implementation and JDK 8 fallback for diagnosing and releasing leaked handles.
- [`ForcedChunkLoadTimeoutException`](../../src/main/java/com/bergerkiller/bukkit/common/chunk/ForcedChunkLoadTimeoutException.java): exceptional completion used when an active load request times out.
- [`CommonNextTickExecutor`](../../src/main/java/com/bergerkiller/bukkit/common/internal/CommonNextTickExecutor.java): main-thread dispatch used for async load completion callbacks.

## Ownership and reference counting

Each `ForcedChunk` stores an `AtomicReference<ForcedChunkEntry>`. `close()` atomically clears the handle and decrements the entry once; repeated closes are harmless. `clone()` increments the same entry's reference count and returns another independently owned handle. `move()` transfers the entry reference out of the source and releases the destination's prior entry.

`CommonForcedChunkManager.Entry` uses an atomic counter for add/remove requests that may originate off the main thread, and a main-thread counter for applying the resulting forced state. Off-thread mutations schedule a manager update; `ForcedWorld.sync()` drains pending deltas and applies a server-facing force/unforce transition only when the effective count crosses zero. Entries are shared by world identity, radius, and packed chunk coordinates, so all owners of an identical request contribute to one count.

The manager's maps are copy-on-write snapshots for lock-free lookups. A `ForcedWorld` groups entries by world and radius, while a second identity map supports world-based queries. A last-access cache accelerates repeated lookups for the same world/radius. This is bookkeeping only; server ticket mutations are applied on the main thread.

## From ownership to a loaded chunk

When an entry becomes forced, `ForcedWorld.setForced()` applies a server chunk ticket on versions with the chunk-ticket API (`HAS_CHUNK_TICKET_API`, Minecraft 1.13.1+). On versions without that API, the manager prevents the corresponding chunk unload and later requests unloading after the final ticket is removed.

The radius argument describes ticket strength, not a hard-edged set of equally active chunks. In `server_level_worldserver.txt`, Minecraft 1.14+ uses a ticket level of `33 - radius` (expressed as `ChunkLevel.byStatus(FULL) - radius` in recent versions). Vanilla propagates that level in square rings, adding one per chunk of Chebyshev distance. For a chunk at distance `d`, the propagated level is `33 - radius + d`. On the modern level scale, 31 or lower is entity-ticking, 32 is block-ticking, 33 is border, and 34 or higher is inaccessible. Thus radius 2 yields an entity-ticking center, a block-ticking first ring, a border second ring, and inaccessible chunks beyond, absent other tickets. Radius 0 is not "load only this chunk for ticking": it requests level 33 at the center. The 1.13.1–1.13.2 saved forced-chunk path does not apply the radius argument, and earlier versions use the legacy unload-prevention path. See the public [radius and load-level explanation](../forced-chunks.md#creating-and-releasing-tickets) and the Minecraft Wiki's [chunk ticket levels](https://minecraft.wiki/w/Chunk#Ticket_levels).

The same transition starts the async load request. `Entry.startLoadingAsync()` first checks whether the chunk is already loaded. Otherwise it asks the version-specific `ServerChunkCacheHandle` for its async executor and invokes the async chunk-load operation. `Entry.accept()` handles the server callback: a successful chunk result is handed to the future completion executor; a failed result is retried while the entry remains forced.

`CommonForcedChunkManager` uses a `CommonNextTickExecutor` backed by a plugin task for load-result completion. This moves callback execution onto the server thread rather than running arbitrary continuation logic on the server's chunk-loading worker. CompletableFuture non-async continuations then run on that completion thread.

Calling `ForcedChunk.getChunk()` is not part of this asynchronous pipeline. It calls Bukkit's synchronous `World.getChunkAt()` path and completes the current future if successful. Callers handling large areas should use `getChunkAsync()` and must not call `getChunk()` as a way to wait.

## Timeouts and cancellation

The manager's `ChunkLoadTimeoutTracker` records pending load requests by tick. After five minutes, it completes the future exceptionally with `ForcedChunkLoadTimeoutException` if the entry is still forced and the future is still pending. This bounds indefinite waits but does not silently release the ticket; normal ownership cleanup is still required.

When the final owner closes, the manager removes the entry, releases the server ticket or legacy unload protection, and resets its pending async future. World unload removes all mappings, disables entries, releases tickets, and clears references to the Bukkit world and server handle to avoid retaining unloaded worlds.

## Leak diagnostics and plugin lifecycle

`CommonPlugin` constructs and enables `CommonForcedChunkManager`. Its `trackForcedChunkCreationStack` setting controls whether new handles capture their creation stack immediately. When a handle is garbage-collected without being closed, `ForcedChunkCleaner` logs the leak and releases the entry. If tracking was disabled, the first leak is logged without its creation stack and enables stack tracking for subsequently created handles. This cleaner is a safety net, not a lifecycle strategy: code must close tickets explicitly.

On plugin shutdown, the manager stops its timeout and pending-update tasks, unloads its forced worlds, disables async callback dispatch, and shuts down the cleaner. Server-specific vanilla forced-chunk NBT construction in [`ForcedChunkSupplier`](../../src/main/java/com/bergerkiller/bukkit/common/internal/logic/ForcedChunkSupplier.java) is compatibility glue for NMS and is distinct from plugin-owned `ForcedChunk` tickets.
