# ForcedChunk

`ForcedChunk` is an owned Minecraft chunk ticket and exposes a `CompletableFuture` for its requested chunk. Its radius controls the ticket's load level and thus how actively the center chunk and its neighbors are processed; it is not a hard-edged area where every chunk is loaded identically. Use this API when chunks must be loaded without blocking the server thread. In particular, loading a large area one chunk at a time with synchronous Bukkit calls can cause severe lag; `ForcedChunk` requests asynchronous loading and lets the server process it over time.

## Key files

- [`ForcedChunk`](../src/main/java/com/bergerkiller/bukkit/common/chunk/ForcedChunk.java): public ticket handle, ownership operations, chunk metadata, and async result.
- [`ForcedChunkManager`](../src/main/java/com/bergerkiller/bukkit/common/chunk/ForcedChunkManager.java): common manager contract and ownership creation.
- [`CommonForcedChunkManager`](../src/main/java/com/bergerkiller/bukkit/common/internal/CommonForcedChunkManager.java): runtime implementation; see the maintainer [implementation guide](internals/forced-chunks.md).

## Creating and releasing tickets

Call `ForcedChunk.load(world, chunkX, chunkZ)` or the `Chunk` overload to create a ticket. The default radius is `2`. The radius selects how active the ticket's center chunk is, and Minecraft propagates progressively less active load states outward in square rings. It is not a hard-edged area where every chunk is loaded or ticked identically.

The center chunk's state for each radius is:

| Radius | Center chunk state | Processing |
|---|---|---|
| 0 | Border | Chunk data is accessible, but game processing is not active. |
| 1 | Block-ticking | Block/redstone processing is active, but entities are not ticked. Often called a lazy chunk. |
| 2 or higher | Entity-ticking | Entities and normal game processing are active. |

With the default radius `2`, the center chunk is entity-ticking, its first surrounding ring is block-ticking (for example, redstone can process there, but entities do not tick), and the next ring is border-only. Radius `1` makes the center block-ticking; radius `0` makes it border-only, not an actively ticking chunk. Radius `3` extends entity-ticking to the first surrounding ring. In general, increasing the radius makes the center and some neighboring chunks more active; each additional ring becomes less active.

These are Minecraft ticket levels, not a guarantee that only those chunks are loaded: other tickets (such as nearby players or another plugin's tickets) can give chunks a stronger level, and generation can involve additional chunks. On 1.13.1–1.13.2, BKCommonLib uses Minecraft's saved forced-chunk mechanism, which does not apply the supplied radius; pre-1.13.1 servers use a legacy unload-prevention path. Do not assume the modern ring states on those versions. See the Minecraft Wiki's [chunk ticket levels](https://minecraft.wiki/w/Chunk#Ticket_levels) for the underlying Java Edition model.

Always close the ticket when the work is finished. Use try-with-resources when the ticket is scoped to one operation; retain it as a field when the chunk should stay loaded over time. Multiple tickets for the same world, coordinates, and radius share a managed entry, and the chunk stops being forced only after all owners release their references.

`clone()` creates another owner of the same ticket. Close each clone independently. `move(other)` transfers the other handle's ownership into this one and closes the source handle. `ForcedChunk.none()` creates an empty handle that can be used as a destination for `move()` later.

## Waiting for the chunk

The load is requested when the `ForcedChunk` is created. Use `getChunkAsync()` to receive the resulting Bukkit `Chunk` without blocking. The asynchronous load completion is dispatched on the server thread, so non-async future continuations normally run there too; keep callback work lightweight and schedule expensive computation elsewhere if appropriate.

Avoid calling `getChunk()` to wait for an unloaded chunk: it uses the synchronous Bukkit lookup path and can cause the very chunk-load stall this API is meant to avoid. `getWorld()`, `getX()`, `getZ()`, `getRadius()`, and `getChunkAsync()` require an open handle and throw `IllegalStateException` after it is closed. `isNone()` and `close()` are safe to call on a closed handle.

If a requested chunk does not load within five minutes while still forced, `getChunkAsync()` completes exceptionally with `ForcedChunkLoadTimeoutException`. Handle exceptional completion if the operation needs to recover from failed or timed-out loads.

## Implementation notes

The public API documents ownership and async behavior. For how tickets are reference-counted, translated to server tickets, loaded asynchronously, timed out, and cleaned up, see the maintainer [ForcedChunk implementation guide](internals/forced-chunks.md).
