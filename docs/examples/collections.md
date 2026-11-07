# Collections Examples

These recipes focus on collection patterns where BKCommonLib's specialized implementations have a clear advantage. They are mutable unless noted otherwise; see the [specialized collections guide](../collections.md) for behavior and concurrency details.

## Share many equivalent immutable sets

Start with one root and derive sets from it. Keeping the same root allows repeated add/remove sequences to reuse cached set instances:

```java
ImmutableCachedSet<Player> noViewers = ImmutableCachedSet.createNew();
ImmutableCachedSet<Player> oneViewer = noViewers.add(playerA);
ImmutableCachedSet<Player> sameViewerSet = noViewers.add(playerA);

assert oneViewer == sameViewerSet;

ImmutableCachedSet<Player> twoViewers = oneViewer.add(playerB);
ImmutableCachedSet<Player> afterLeave = twoViewers.remove(playerA);
```

The values are immutable, so `oneViewer` remains unchanged after deriving `twoViewers`. Each particle or other owner can retain whichever set describes its current membership without owning a separate mutable backing set.

## Iterate a copy-on-write snapshot

Use a snapshot when a collection may be modified while another thread iterates it. A write to the original detaches its backing data if the snapshot still refers to it:

```java
ImplicitlySharedList<Entity> entities = new ImplicitlySharedList<>();
entities.addAll(initialEntities);

try (ImplicitlySharedList<Entity> snapshot = entities.clone()) {
    for (Entity entity : snapshot) {
        inspect(entity);
    }
}
```

The snapshot sees the contents as they were when cloned; later list changes do not alter its iteration. Close snapshots in all paths using try-with-resources. For a full iteration without early exit, `entities.cloneAndForEach(this::inspect)` is a shorter alternative.

When the loop will consume every element, `cloneAsIterable()` creates and closes the snapshot automatically:

```java
for (Entity entity : entities.cloneAsIterable()) {
    inspect(entity);
}
```

Do not break or return early from this loop: the internal snapshot is released when iteration reaches the end. Use an explicit cloned list in try-with-resources when iteration may stop early.

## Cache derived values for stable identity keys

Create a cache with a synchronizer, then resynchronize it from the ordered source list. Existing key instances retain their associated cached values:

```java
SortedIdentityCache<Entity, CachedEntityState> states =
        SortedIdentityCache.create(CachedEntityState::new);

states.sync(world.getEntities());
for (CachedEntityState state : states) {
    state.update();
}
```

If cached values own resources, implement `Synchronizer.onRemoved(key, value)` to release them when a key disappears. Feed the source in a stable order; do not rely on the cache to sort values or reorder them to match an arbitrarily changing traversal.

## Queue only values marked for update

Keep one tracker for each long-lived value. Repeated `set(true)` calls do not enqueue duplicate pending entries, and `set(false)` can cancel a pending update:

```java
FastTrackedUpdateSet<Particle> pendingUpdates = new FastTrackedUpdateSet<>();
FastTrackedUpdateSet.Tracker<Particle> tracker = pendingUpdates.track(particle);

tracker.set(true);  // Queue once
tracker.set(true);  // Still one pending update
pendingUpdates.forEachAndClear(Particle::update);

tracker.set(true);  // Can be queued again after processing
```

If a value is removed permanently, call `tracker.untrack()` so later state changes cannot queue it. `iterateAndClear()` can be used instead when the consumer needs to control iteration; a partial iteration can resume from where it stopped on the next iterator.

## Query a spatial region

Use `Octree` when every integer coordinate stores at most one value:

```java
Octree<BlockData> blocks = new Octree<>();
blocks.put(blockX, blockY, blockZ, data);

IntCuboid query = IntCuboid.create(
        new IntVector3(-8, 0, -8),
        new IntVector3(8, 16, 8));
for (BlockData value : blocks.cuboid(query)) {
    inspect(value);
}
```

The `IntCuboid` maximum is exclusive. For exact integer endpoints that are both included, use `blocks.cuboid(min, max)` instead.

The octree iterator also exposes the integer coordinates for the value most recently returned by `next()`:

```java
OctreeIterator<BlockData> iter = blocks.cuboid(query).iterator();
while (iter.hasNext()) {
    BlockData value = iter.next();
    int x = iter.getX();
    int y = iter.getY();
    int z = iter.getZ();
    inspect(value, x, y, z);
}
```

Read the coordinates before calling `next()` again; they describe the last returned value.

Use `DoubleOctree` for fractional positions or multiple values per position:

```java
DoubleOctree<Entity> spatialIndex = new DoubleOctree<>();
Location position = entity.getLocation();
spatialIndex.add(position.getX(), position.getY(), position.getZ(), entity);

IntVector3 minBlock = new IntVector3(-4, 0, -4);
IntVector3 maxBlock = new IntVector3(4, 8, 4);
for (Entity entity : spatialIndex.cuboid(minBlock, maxBlock)) {
    inspect(entity);
}
```

To retrieve the exact stored double coordinates alongside each value, use `DoubleOctreeIterator`:

```java
DoubleOctreeIterator<Entity> iter = spatialIndex.cuboid(minBlock, maxBlock).iterator();
while (iter.hasNext()) {
    Entity entity = iter.next();
    double x = iter.getX();
    double y = iter.getY();
    double z = iter.getZ();
    inspect(entity, x, y, z);
}
```

`getBlockX/Y/Z()` return the containing integer cell coordinates instead. As with `OctreeIterator`, coordinate accessors describe the value most recently returned by `next()`. The double-coordinate cuboid query includes all entries in the selected integer cells, including fractional positions in the maximum cell. Use `block(blockCoord)` to query one 1x1x1 cell, and `move(oldX, oldY, oldZ, value, newX, newY, newZ)` to update a value's position.
