# SignChangeTracker

`SignChangeTracker` efficiently tracks a sign at a block position. Call `update()` to detect text changes, changes to the sign block state or backing block entity, and removal. It also provides direct line access through `SignLineAccessor`; use `SignSide.FRONT` and `SignSide.BACK` to select which side to read or edit.

## Key files

- [`SignChangeTracker`](../src/main/java/com/bergerkiller/bukkit/common/block/SignChangeTracker.java): tracks a sign's live state and implements `SignLineAccessor`.
- [`SignSide`](../src/main/java/com/bergerkiller/bukkit/common/block/SignSide.java): selects a side and reads or writes its lines.
- [`SignLineAccessor`](../src/main/java/com/bergerkiller/bukkit/common/block/SignLineAccessor.java) and [`SignSideLineAccessor`](../src/main/java/com/bergerkiller/bukkit/common/block/SignSideLineAccessor.java): common line APIs and a view narrowed to one side.
- [`SignSideMap`](../src/main/java/com/bergerkiller/bukkit/common/block/SignSideMap.java): stores separate values for front and back.

## Creating and updating a tracker

Create a tracker with `SignChangeTracker.track(sign)` or `SignChangeTracker.track(block)`. The tracker retains the block position and a snapshot of the sign state. `update()` checks whether that state is still current and returns `true` when the backing sign entity was reloaded, the text or block data changed, or the sign was removed. It returns `false` when nothing changed.

Check `isRemoved()` after an update that reports a change. If the sign was removed, `getSign()` returns `null`, and methods requiring a live sign state, including line access, throw `IllegalStateException`. When a sign entity is replaced or its text changes, `update()` refreshes the tracker's internal state so subsequent accessors read the new state.

```java
SignChangeTracker tracker = SignChangeTracker.track(signBlock);

if (tracker.update()) {
    if (tracker.isRemoved()) {
        stopTracking(tracker);
    } else {
        handleSignChanged(tracker);
    }
}
```

Calling `update()` can cause the sign's chunk to be synchronously loaded when the tracker needs to find or reacquire its backing sign entity. Track signs whose positions are relevant to the feature, and avoid polling large numbers of signs in unloaded chunks. For event-driven chunk load/unload tracking, see the [ChunkFutureProvider guide](chunk-future-provider.md); it can be used to refresh sign trackers when their chunks load and stop using them when chunks unload.

The tracker does not poll automatically. Call `update()` before reading cached state when changes may have happened since the last check. Alternatively, `liveUpdatingAccessor()` returns a `SignLineAccessor` that invokes `update()` before each text read; use it when a live accessor is more convenient than managing update calls yourself.

## Reading and writing both sides

`SignChangeTracker` implements `SignLineAccessor`. Use `getLine(side, index)` and `getLines(side)` to read text, or `setLine(side, index, text)` to change it. There are also explicit front/back methods and formatted `ChatText` methods such as `getFormattedFrontLine` and `setFormattedBackLine`. A `SignSideLineAccessor` returned by `accessLinesOfSide(side)` provides generic formatted-line methods for the selected side.

The sign has four lines per side, indexed from `0` to `3`. `SignSide.FRONT` and `SignSide.BACK` provide a readable side selector. `SignSide.BACK` is supported on Minecraft 1.20 and later; on older versions, back-side reads return empty lines and writes do nothing. Use `SignSide.isSupported()` when the feature depends on back text being available.

```java
SignSide side = SignSide.FRONT;
String firstLine = tracker.getLine(side, 0);
tracker.setLine(side, 0, "Updated");
```

Line setters update the sign in the world. The returned `Sign` from `getSign()` is refreshed by `update()`, but Bukkit sign text APIs can be slow or behave inconsistently across server implementations; use the tracker's line accessors instead.

To pass a side-specific accessor to code that only works with one side, call `accessLinesOfSide(side)`. `SignSideMap<T>` is a simple companion for keeping per-side metadata and reading or writing it by `SignSide`.

See the [examples](examples/sign-change-tracker.md) for change polling and side-specific access.
