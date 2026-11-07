# ChunkFutureProvider

`ChunkFutureProvider` is an event-driven API for reacting to chunk lifecycle changes without loading chunks just to check their state. Its key use is to wait until a main chunk and the neighbouring chunks an operation may touch are all loaded. Minecraft operations can cross chunk boundaries; accessing a neighbouring block or its data while that chunk is unloaded can trigger a synchronous chunk load and cause a server lag spike.

The provider observes load/unload events and reports when the required chunks are ready. It does not load chunks itself, except for the explicitly synchronous `ofSyncLoad()` variant.

## Key files

- [`ChunkFutureProvider`](../src/main/java/com/bergerkiller/bukkit/common/chunk/ChunkFutureProvider.java): public future, tracker, listener, and neighbour-list APIs.
- [`ChunkFutureProviderImpl`](../src/main/java/com/bergerkiller/bukkit/common/chunk/ChunkFutureProviderImpl.java): internal Bukkit event handlers and tracker implementation.

## Choosing a provider

- `ChunkFutureProvider.of(plugin)` is for main-thread use only. Call it on the main thread; all future completions and tracker callbacks run on the main thread.
- `ChunkFutureProvider.ofThreadSafe(plugin)` permits provider operations, including tracker registration, from other threads. Its futures and callbacks still complete on the main thread.
- `ChunkFutureProvider.ofSyncLoad()` is a special synchronous loader, not an event-driven watcher. It loads requested chunks immediately and supports only `whenLoaded` and helpers built on it. Do not use it to avoid synchronous loads.

For normal event-driven tracking, choose `of()` or `ofThreadSafe()` according to where the provider will be called.

For a complete pattern that seeds main chunks from Bukkit load events and keeps a map containing only chunks whose neighbourhood is ready, see the [active chunk registry example](examples/chunk-future-provider.md#keep-a-registry-of-active-chunks).

## Waiting for a main chunk and its neighbours

Use `trackNeighboursLoaded(mainChunk, neighbours, listener)` to observe whether the main chunk's required neighbour set is fully loaded. `mainChunk` must already be loaded and remains the anchor for the tracker. Construct the neighbour list with `ChunkNeighbourList.neighboursOf(mainChunk, 1)` for the eight surrounding chunks, or create a custom list and add the exact chunks the operation can reach.

The provider tracks each listed neighbour and maintains whether all of them are loaded. Once initial state is known, it calls `onRegistered(tracker)`. Inspect `tracker.isLoaded()` there to determine whether the area is already ready. After registration, `onLoaded` and `onUnloaded` report transitions of the whole neighbour set, not individual chunk events. A single neighbour unloading changes the area from ready to not ready; it must not be accessed until all required neighbours are loaded again.

The tracker is tied to the main chunk's lifetime. If the main chunk unloads, the provider cancels the neighbour tracking; if the area was ready, `onUnloaded` is called before `onCancelled`. Call `tracker.cancel()` when the feature no longer needs notifications. Cancellation stops callbacks and releases tracking state.

```java
ChunkFutureProvider provider = ChunkFutureProvider.of(plugin);
ChunkFutureProvider.ChunkNeighbourList neighbours =
        ChunkFutureProvider.ChunkNeighbourList.neighboursOf(mainChunk, 1);

provider.trackNeighboursLoaded(mainChunk, neighbours, new ChunkFutureProvider.ChunkStateListener() {
    @Override
    public void onRegistered(ChunkFutureProvider.ChunkStateTracker tracker) {
        if (tracker.isLoaded()) {
            activateForLoadedArea(tracker.getChunk());
        }
    }

    @Override
    public void onLoaded(ChunkFutureProvider.ChunkStateTracker tracker) {
        activateForLoadedArea(tracker.getChunk());
    }

    @Override
    public void onUnloaded(ChunkFutureProvider.ChunkStateTracker tracker) {
        deactivateForUnloadedArea(tracker.getChunk());
    }

    @Override
    public void onCancelled(ChunkFutureProvider.ChunkStateTracker tracker) {
        stopTrackingArea();
    }
});
```

In the callbacks, `getChunk()` returns the main chunk while it is loaded. It is also valid during `onUnloaded`; after that callback, it becomes `null`. Use `isLoaded()` to decide whether operations that may cross chunk boundaries are safe.

## Other lifecycle futures and trackers

`whenLoaded`, `whenUnloaded`, `whenEntitiesLoaded`, and `whenEntitiesUnloaded` return one-shot `CompletableFuture`s for the corresponding state transition. If the requested state already holds, the future is completed immediately. Otherwise it completes when the relevant event occurs. Cancelling a pending future unregisters its internal waiter. Entity-state futures can be used instead of `ChunkLoadEntitiesEvent` and `ChunkUnloadEntitiesEvent`; for repeated load/unload tracking, re-arm the futures after each transition or use those events. There is no continuous entity-state `ChunkStateTracker`.

Use `trackLoaded(world, chunkX, chunkZ, listener)` when changes to one chunk's loaded state should be observed continuously. It follows the same registration and transition callbacks as neighbour tracking, but tracks one chunk rather than a group.

`whenNeighbourLoaded(mainChunk, x, z)` waits for another chunk while the main chunk remains loaded; unloading the main chunk cancels the future. `readNeighbourBlockData(mainChunk, block)` builds on that behavior and reads the block data only after its chunk is loaded. `whenAllNeighboursLoaded(mainChunk, neighbours)` is the one-time future form of neighbour readiness; use `trackNeighboursLoaded` instead when readiness can be lost and regained over time.

For sign trackers, this event-driven lifecycle can refresh active sign state on chunk load and stop using trackers when chunks unload; see the [SignChangeTracker guide](sign-change-tracker.md).
