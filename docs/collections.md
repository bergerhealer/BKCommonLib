# Specialized Collections

BKCommonLib's collections package contains purpose-built structures for high-volume snapshots, copy-on-write reads, stable identity-based mappings, pending-update tracking, and spatial queries. Use the standard Java collections for general-purpose storage; these types are useful when their specific memory or update pattern matches the problem.

## Key files

- [`ImmutableCachedSet`](../src/main/java/com/bergerkiller/bukkit/common/collections/ImmutableCachedSet.java): immutable sets whose results are shared through an operation cache.
- [`ImplicitlySharedList`](../src/main/java/com/bergerkiller/bukkit/common/collections/ImplicitlySharedList.java), [`ImplicitlySharedSet`](../src/main/java/com/bergerkiller/bukkit/common/collections/ImplicitlySharedSet.java), and [`ImplicitlySharedHolder`](../src/main/java/com/bergerkiller/bukkit/common/collections/ImplicitlySharedHolder.java): copy-on-write containers for cheaply creating snapshots.
- [`SortedIdentityCache`](../src/main/java/com/bergerkiller/bukkit/common/collections/SortedIdentityCache.java): identity-keyed mapping that synchronizes values against an ordered source.
- [`FastTrackedUpdateSet`](../src/main/java/com/bergerkiller/bukkit/common/collections/FastTrackedUpdateSet.java): a list-backed queue of values that need processing, with per-value tracker handles.
- [`Octree`](../src/main/java/com/bergerkiller/bukkit/common/collections/octree/Octree.java) and [`DoubleOctree`](../src/main/java/com/bergerkiller/bukkit/common/collections/octree/DoubleOctree.java): spatial indexes for integer and double-precision 3D coordinates.

Longer usage recipes are in the [collections examples](examples/collections.md).

## ImmutableCachedSet

Use `ImmutableCachedSet` when many owners need sets with heavily overlapping contents, especially when they reach those sets through repeated add/remove operations. A particle, for example, can retain the immutable set of players currently viewing it without each particle allocating an identical backing `HashSet`. The cache is associated with a root created by `createNew()`; derive related sets from that root so repeated operations can return the same cached set instance.

The set never mutates in place: `add`, `remove`, `addAll`, and `removeAll` return a new set (or `this` if nothing changes). Cached results are weakly held, so unused sets can be garbage-collected. Null elements are not supported. Different roots have separate caches, so create one root for a group of sets intended to share results rather than creating a new root for each value.

The immutable contents can be read concurrently, and the shared cache uses a concurrent map; the API is designed to support concurrent add/remove operations on sets derived from the same root. A multithreaded test exercises this usage. If two threads create the same contents simultaneously, they may race to populate the cache and receive distinct but equal set instances, so do not use reference identity as a correctness guarantee across concurrent operations. Elements themselves must have stable, thread-safe `equals()` and `hashCode()` behavior while used in a set. `releaseFromCache(...)` is cache cleanup rather than a synchronization mechanism; coordinate it with set updates if you require deterministic cache eviction.

```java
ImmutableCachedSet<Player> viewers = ImmutableCachedSet.createNew();
viewers = viewers.add(player);
particle.setViewers(viewers);
```

## ImplicitlySharedList and ImplicitlySharedSet

These are copy-on-write containers: `clone()` initially shares the backing collection, and the first mutation of either copy detaches it. This makes snapshots cheap and lets one thread iterate a snapshot while another copy is modified, without cloning the underlying list or set preemptively at every call site.

For a complete iteration that must not observe concurrent changes, use `cloneAsIterable()` or explicitly clone and close the snapshot:

```java
try (ImplicitlySharedList<Entity> snapshot = entities.clone()) {
    for (Entity entity : snapshot) {
        inspect(entity);
    }
}
```

Close explicit clones when finished to release their shared reference and avoid unnecessary future copies. `cloneAsIterable()` closes its internal copy when fully consumed; avoid early termination with it, or use an explicit try-with-resources clone instead. Sharing applies to the collection structure, not to the objects stored inside it. These classes coordinate access to their backing data, but do not make mutable elements thread-safe or make concurrent writes to the same logical collection a substitute for application-level synchronization.

## SortedIdentityCache

Use `SortedIdentityCache<K, V>` to keep a derived value for each key from a source collection, such as cached per-entity state for the entities in a world's entity list. Keys use object identity (`==`), not `equals()`. A `Synchronizer` creates a value when a new key appears and can clean up when a key is removed. `sync(source)` retains cached values for existing key instances and creates/removes values as necessary.

The source should have a stable, meaningful iteration order between synchronizations. `sync(source)` updates the cache's iteration order to follow the source order, even when the same keys appear in a different order; it reuses the values associated with those key instances. Its boolean result reports additions and removals, not a pure reordering, so it can return `false` even though iteration order changed. It is not a sorting collection and is a poor fit when source order is arbitrary or constantly shuffled. The cache is not thread-safe. `create(...)` uses a compact array-backed implementation; `createLinked(...)` is an alternative for small collections or bulk insertion patterns.

Use [`LogicUtil.synchronizeList()` or `synchronizeUnordered()`](common-logic-utilities.md#synchronizing-collections) when reconciling an existing list or set-like collection, especially when source values need a custom correspondence to target items or when the source is not an identity-keyed mapping. `SortedIdentityCache` is a better fit when repeated synchronization of an ordered source should maintain a derived value per key object, with identity-based lookup and ordered iteration of those cached values. It avoids `synchronizeList()`'s iterator overhead, but is less general: keys match strictly by identity, the cache is not thread-safe, and it owns a specialized mapping rather than modifying an arbitrary existing collection. See the [usage comparison](common-logic-utilities.md#synchronizing-collections) for the complementary collection-synchronization methods.

```java
SortedIdentityCache<Entity, CachedEntityState> cache =
        SortedIdentityCache.create(CachedEntityState::new);
cache.sync(world.getEntities());
```

## FastTrackedUpdateSet

`FastTrackedUpdateSet<E>` tracks only values that have changed and need later processing. Call `track(value)` once to create a `Tracker<E>` for a value; retain that tracker alongside the value's state. `tracker.set(true)` queues the value once, and `tracker.set(false)` cancels it if it has not yet been consumed. `iterateAndClear()` returns the pending values and resets their queued state as they are consumed. `forEachAndClear(...)` is convenient when all pending values can be processed in one callback.

This avoids hash-based set operations on every state change, which can be useful for particle updates and synchronization queues. Trackers can be added or updated while an iteration is in progress, but the collection is not generally thread-safe; coordinate callers if they access it from multiple threads. Disabling the set clears it and invalidates trackers created before the disable.

```java
FastTrackedUpdateSet<Particle> changed = new FastTrackedUpdateSet<>();
FastTrackedUpdateSet.Tracker<Particle> tracker = changed.track(particle);
tracker.set(true);
changed.forEachAndClear(Particle::update);
```

## Octree and DoubleOctree

Use `Octree<T>` to associate one value with each integer `(x, y, z)` coordinate. `put` replaces the value at a coordinate; `get` and `remove` use the exact integer coordinate. Its tree structure can prune regions outside a query, making `cuboid(...)` useful when spatial queries should visit only values in a bounded area rather than scan every stored point. The `IntVector3` bounds overload is inclusive at both ends. The `IntCuboid` overload follows `IntCuboid`'s exclusive maximum convention.

Use `DoubleOctree<T>` when values have fractional positions or multiple values may share a coordinate. It indexes positions within integer 1x1x1 cells and retains each entry's exact double coordinates. `cuboid(min, max)` selects whole cells between inclusive integer block coordinates; it does not define arbitrary fractional min/max bounds. `block(...)` selects the values in one cell. When moving a value, `move(...)` can update its indexed location without a separate remove-and-add sequence. Avoid putting a very large number of entries into a single cell, where the per-cell entry chain reduces the benefit of the tree.

Both trees expose iterators for the full contents and for cuboid queries. They are mutable spatial indexes, not concurrent collections; synchronize access if multiple threads may modify or query the same tree.

```java
DoubleOctree<Entity> entities = new DoubleOctree<>();
entities.add(position.getX(), position.getY(), position.getZ(), entity);

for (Entity nearby : entities.cuboid(minBlock, maxBlock)) {
    inspect(nearby);
}
```

The spatial structures are under [`common/collections/octree/`](../src/main/java/com/bergerkiller/bukkit/common/collections/octree/). `OctreeIterator` and `DoubleOctreeIterator` expose coordinate and entry access when a query needs both the value and its indexed position.
