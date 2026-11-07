# Offline Worlds and Blocks

The `com.bergerkiller.bukkit.common.offline` package represents worlds and block positions without requiring a currently loaded Bukkit `World` or `Block`. `OfflineWorld` is a canonical wrapper around a world's UUID, `OfflineBlock` pairs that world identity with integer block coordinates, and `OfflineWorldMap` stores values by offline-world identity.

## Key files

- [`OfflineWorld`](../src/main/java/com/bergerkiller/bukkit/common/offline/OfflineWorld.java): canonical UUID-backed world reference with optional access to its loaded Bukkit `World`.
- [`OfflineBlock`](../src/main/java/com/bergerkiller/bukkit/common/offline/OfflineBlock.java): world plus block coordinates, usable as a hash-map or set key.
- [`OfflineWorldMap`](../src/main/java/com/bergerkiller/bukkit/common/offline/OfflineWorldMap.java): identity-keyed map with overloads for `OfflineWorld` and Bukkit `World`.
- [`OfflineWorldLoadedChangeListener`](../src/main/java/com/bergerkiller/bukkit/common/offline/OfflineWorldLoadedChangeListener.java): internal lifecycle component that tracks loaded-world changes.

## OfflineWorld

Use `OfflineWorld.of(uuid)` when a world may not be loaded, or `OfflineWorld.of(world)` when starting from a Bukkit world. `of` returns a canonical instance for each UUID within the running process, so world wrappers can be compared with `==` and used as identity-map keys. `OfflineWorld.NONE` represents the absent/null world and has no UUID or loaded Bukkit world.

The UUID-to-`OfflineWorld` cache retains each instance for the lifetime of the server; entries are not removed when a world unloads. Only pass UUIDs that identify real worlds you intend to track. Calling `of` with arbitrary or unbounded UUIDs can grow this cache until the server restarts.

You can call `getLoadedWorld()` whenever needed; it is cheap, so you do not need to cache the result. It returns `null` if the world is unloaded.

```java
OfflineWorld worldRef = OfflineWorld.of(worldUuid);
World loadedWorld = worldRef.getLoadedWorld();
if (loadedWorld != null) {
    // Perform an operation that requires a live Bukkit World
}
```

Use `getBlockAt(x, y, z)` or `getBlockAt(IntVector3)` to create an `OfflineBlock` without loading a world. `getLoadedBlockAt(...)` returns a Bukkit block only if the world is currently loaded; otherwise it returns `null`.

## OfflineBlock

An `OfflineBlock` is the pair `(OfflineWorld, x, y, z)`. Its coordinates are integer block coordinates, and `getRelative(...)` returns another offline block without resolving a Bukkit world or block. `OfflineBlock.of(block)` adapts an existing Bukkit block.

Equality compares coordinate values and the canonical `OfflineWorld` by identity. This makes separately created `OfflineBlock` values for the same world UUID and coordinates equal and suitable as hash keys. Its hash code is based on the position alone, so blocks at the same coordinates in different worlds can collide; equality still distinguishes them correctly.

`getLoadedBlock()` returns the Bukkit block if its world is loaded, or `null` otherwise. Do not retain a loaded Bukkit `Block` as the offline identity; retain the `OfflineBlock` and resolve it when needed.

```java
OfflineBlock savedPosition = worldRef.getBlockAt(x, y, z);
OfflineBlock neighbor = savedPosition.getRelative(BlockFace.NORTH);
Block liveBlock = neighbor.getLoadedBlock();
```

`OfflineBlock.readFrom(...)` and `writeTo(...)` serialize the world's UUID followed by the integer coordinates, allowing a position to be persisted independently of the world's load state.

## OfflineWorldMap

Use `OfflineWorldMap<V>` for per-world data when worlds may unload and later reload. It accepts both `OfflineWorld` and Bukkit `World` keys for `get`, `put`, `remove`, and `computeIfAbsent`; a Bukkit world is converted to its canonical offline wrapper.

The map uses an `IdentityHashMap`, relying on canonical `OfflineWorld` instances. It also caches the most recently read key and value, which speeds up repeated `get` or `computeIfAbsent` calls for the same world. Writes and removals invalidate that one-entry lookup cache. This is a performance optimization, not a different consistency model. `OfflineWorldMap` is not thread-safe.

```java
OfflineWorldMap<WorldState> states = new OfflineWorldMap<>();
WorldState state = states.computeIfAbsent(worldRef, WorldState::new);

World loadedWorld = worldRef.getLoadedWorld();
if (loadedWorld != null) {
    states.put(loadedWorld, state);
}
```

`keySet()`, `values()`, and `entrySet()` expose the underlying map's live views. Coordinate access and world lifecycle operations according to Bukkit's threading requirements.

## Internal lifecycle tracking

`OfflineWorldLoadedChangeListener` is registered as a BKCommonLib library component. It installs loaded-world suppliers and updates them from world initialization, load, and unload events. It also clears the optional Bukkit-`World`-to-`OfflineWorld` identity cache periodically and on unload to avoid retaining stale world objects. Plugin authors should use `OfflineWorld` and should not call its internal component initialization methods.
