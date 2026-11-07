# ChunkFutureProvider Examples

These examples show how to [wait for chunk state without forcing chunks to load](../chunk-future-provider.md), including readiness of a main chunk and its neighbours.

## Keep a registry of active chunks

Use ordinary Bukkit `ChunkLoadEvent`s to seed a tracker for each loaded main chunk. The provider then waits for the required neighbours and reports when the area becomes safe to use. Keep the active values in a `Map<Chunk, ActiveChunk>`; remove them when the neighbour area becomes unavailable or the main chunk unloads.

The provider callbacks and Bukkit chunk events run on the main thread when using `ChunkFutureProvider.of(plugin)`, so these `HashMap`s are accessed on that thread. The active value is created only when the neighbourhood is ready and disposed when it stops being ready:

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.Plugin;

final class ActiveChunkRegistry implements Listener {
    private final ChunkFutureProvider provider;
    private final Map<Chunk, ActiveChunk> activeChunks = new HashMap<>();

    ActiveChunkRegistry(Plugin plugin) {
        this.provider = ChunkFutureProvider.of(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                seed(chunk);
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        seed(event.getChunk());
    }

    private void seed(Chunk mainChunk) {
        ChunkFutureProvider.ChunkNeighbourList neighbours =
                ChunkFutureProvider.ChunkNeighbourList.neighboursOf(mainChunk, 1);
        provider.trackNeighboursLoaded(mainChunk, neighbours, new ChunkFutureProvider.ChunkStateListener() {
            @Override
            public void onRegistered(ChunkFutureProvider.ChunkStateTracker state) {
                if (state.isLoaded()) {
                    activate(state.getChunk());
                }
            }

            @Override
            public void onLoaded(ChunkFutureProvider.ChunkStateTracker state) {
                activate(state.getChunk());
            }

            @Override
            public void onUnloaded(ChunkFutureProvider.ChunkStateTracker state) {
                deactivate(mainChunk);
            }

            @Override
            public void onCancelled(ChunkFutureProvider.ChunkStateTracker state) {
                deactivate(mainChunk);
            }
        });
    }

    private void activate(Chunk chunk) {
        if (!activeChunks.containsKey(chunk)) {
            ActiveChunk active = new ActiveChunk(chunk);
            activeChunks.put(chunk, active);
            active.onActivated();
        }
    }

    private void deactivate(Chunk chunk) {
        ActiveChunk active = activeChunks.remove(chunk);
        if (active != null) {
            active.onDeactivated();
        }
    }

    void disable() {
        for (Chunk chunk : new ArrayList<>(activeChunks.keySet())) {
            deactivate(chunk);
        }
    }

    private static final class ActiveChunk {
        final Chunk chunk;

        ActiveChunk(Chunk chunk) {
            this.chunk = chunk;
        }

        void onActivated() {
            // Initialize custom state that may safely access neighbouring chunks
        }

        void onDeactivated() {
            // Release state or stop work that depends on neighbouring chunks
        }
    }
}
```

The constructor registers listeners before scanning currently loaded chunks, so already-loaded main chunks are seeded too. The provider cancels neighbour tracking automatically when the main chunk unloads and calls `onCancelled()`, so there is no need to listen for `ChunkUnloadEvent` or retain tracker handles. `activeChunks` contains only chunks whose required neighbourhood is loaded. If a neighbour unloads, the main chunk remains tracked but its active value is removed. When all neighbours load again, a fresh active value is created. Call `disable()` from the owning plugin's shutdown path to dispose active values.

### Seed only after chunk entities load

If custom state requires the main chunk's entities to be present, use BKCommonLib's [`ChunkLoadEntitiesEvent`](../../src/main/java/com/bergerkiller/bukkit/common/events/ChunkLoadEntitiesEvent.java) and [`ChunkUnloadEntitiesEvent`](../../src/main/java/com/bergerkiller/bukkit/common/events/ChunkUnloadEntitiesEvent.java) instead of Bukkit's chunk load/unload events for the `seed()` and cleanup handlers. Unlike full chunk unload, entity unload does not automatically cancel `trackNeighboursLoaded()`, because the main chunk itself remains loaded. This variant must retain each tracker handle (for example, in a `Map<Chunk, ChunkStateTracker>`) so the entity-unload handler can cancel it. Remove the handle from that map in `onCancelled()` as well, since the main chunk itself may unload:

```java
@EventHandler
public void onChunkEntitiesLoad(ChunkLoadEntitiesEvent event) {
    seed(event.getChunk());
}

@EventHandler
public void onChunkEntitiesUnload(ChunkUnloadEntitiesEvent event) {
    Chunk mainChunk = event.getChunk();
    ChunkFutureProvider.ChunkStateTracker tracker = trackers.remove(mainChunk);
    if (tracker != null) {
        tracker.cancel();
    }
    deactivate(mainChunk);
}
```

These BKCommonLib events fire as chunk entities load and unload; a chunk can remain loaded after its entities unload. With this variant, `activeChunks` therefore contains chunks whose **main-chunk entities** are loaded and whose required neighbouring **chunks** are loaded. `trackNeighboursLoaded()` tracks chunk state, not entity state for every neighbour. If the operation also requires neighbour entities, wait for their entity-load state separately with `whenEntitiesLoaded()` before accessing them.

There is no API that directly lists all entity-loaded chunks. To seed already-loaded entity chunks during startup, iterate each world's loaded chunks and filter them with `WorldUtil.isChunkEntitiesLoaded(chunk)` before calling `seed(chunk)`:

```java
for (World world : Bukkit.getWorlds()) {
    for (Chunk chunk : world.getLoadedChunks()) {
        if (WorldUtil.isChunkEntitiesLoaded(chunk)) {
            seed(chunk);
        }
    }
}
```

Register the entity load/unload listeners before this scan so subsequent changes are observed.

### Use entity-state futures instead of entity events

`ChunkFutureProvider` also exposes one-shot `whenEntitiesLoaded()` and `whenEntitiesUnloaded()` futures. They can replace the entity events when code is organized around futures. They report one transition each; unlike `trackNeighboursLoaded()`, they do not provide a continuous entity-state tracker. The neighbour tracker still needs to be cancelled if the main chunk's entities unload, because the chunk itself may remain loaded:

```java
private void waitForEntitiesAndTrack(Chunk mainChunk) {
    provider.whenEntitiesLoaded(mainChunk).thenAccept(loadedChunk -> {
        ChunkFutureProvider.ChunkNeighbourList neighbours =
                ChunkFutureProvider.ChunkNeighbourList.neighboursOf(loadedChunk, 1);

        ChunkFutureProvider.ChunkStateTracker tracker =
                provider.trackNeighboursLoaded(loadedChunk, neighbours, listenerFor(loadedChunk));

        provider.whenEntitiesUnloaded(loadedChunk).thenRun(tracker::cancel);
    });
}
```

Calling `whenEntitiesLoaded()` for a chunk whose entities are already loaded returns an already-completed future, so this also works to seed currently entity-loaded chunks. To continuously re-arm this pattern for each later entity load, schedule another `whenEntitiesLoaded()` after the unload transition. If using the entity events, the startup scan described above is still needed because events only report subsequent transitions.

## Activate only when the neighbourhood is ready

The listener is notified about the initial state and later transitions. The main chunk must already be loaded. A radius-one neighbour list contains the eight chunks around it.

```java
ChunkFutureProvider provider = ChunkFutureProvider.of(plugin);
ChunkFutureProvider.ChunkNeighbourList neighbours =
        ChunkFutureProvider.ChunkNeighbourList.neighboursOf(mainChunk, 1);

ChunkFutureProvider.ChunkStateTracker tracker =
        provider.trackNeighboursLoaded(mainChunk, neighbours, new ChunkFutureProvider.ChunkStateListener() {
    @Override
    public void onRegistered(ChunkFutureProvider.ChunkStateTracker state) {
        if (state.isLoaded()) {
            activate(state.getChunk());
        }
    }

    @Override
    public void onLoaded(ChunkFutureProvider.ChunkStateTracker state) {
        activate(state.getChunk());
    }

    @Override
    public void onUnloaded(ChunkFutureProvider.ChunkStateTracker state) {
        deactivate(state.getChunk());
    }

    @Override
    public void onCancelled(ChunkFutureProvider.ChunkStateTracker state) {
        releaseState();
    }
});
```

The provider calls `onRegistered()` after determining initial state. Inspect `isLoaded()` there and initialize the active state if the neighbourhood is already ready. Later, a neighbour unload triggers `onUnloaded()` for the group, and readiness returning triggers `onLoaded()` again. Main-chunk unload cancels this tracker automatically. Call `tracker.cancel()` yourself when the feature stops before then.

## Wait for one neighbouring chunk

Use `whenNeighbourLoaded()` when one specific neighbouring chunk is needed temporarily. If the main chunk unloads before the neighbour loads, the future is cancelled.

```java
provider.whenNeighbourLoaded(mainChunk, neighbourChunkX, neighbourChunkZ)
        .thenAccept(neighbour -> inspectNeighbour(neighbour));
```

Use `readNeighbourBlockData()` when the operation needs only block data. It waits for the block's chunk before reading, preventing an access to an unloaded neighbouring chunk from causing a synchronous load.

## Build a custom required-neighbour set

For operations that reach beyond the eight immediate neighbours, construct the exact set of chunks the operation may access:

```java
ChunkFutureProvider.ChunkNeighbourList neighbours =
        ChunkFutureProvider.ChunkNeighbourList.create();
neighbours.add(mainChunk.getWorld(), chunkX, chunkZ);
neighbours.add(mainChunk.getWorld(), otherChunkX, otherChunkZ);

provider.trackNeighboursLoaded(mainChunk, neighbours, listener);
```

The list describes chunks required in addition to the main chunk. Include every chunk that the operation may touch; the provider only reports readiness for the coordinates supplied. For the common eight-neighbour case, use `neighboursOf(mainChunk, 1)`.
