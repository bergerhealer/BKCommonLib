# CommonUtil and LogicUtil

`CommonUtil` and `LogicUtil` are broad utility classes. Their responsibilities overlap, but `CommonUtil` focuses on server- and Bukkit-related helpers, while `LogicUtil` contains general-purpose logic helpers. This guide covers server tick timing, next-tick dispatch, `RunOnceTask`, copy-on-write caches, collection synchronization, and generic casts.

## Key files

- [`CommonUtil`](../src/main/java/com/bergerkiller/bukkit/common/utils/CommonUtil.java): server-aware and Bukkit-related utilities, including server tick access and next-tick scheduling.
- [`LogicUtil`](../src/main/java/com/bergerkiller/bukkit/common/utils/LogicUtil.java): general logic helpers, including copy-on-write caches, collection synchronization, and generic cast helpers.
- [`RunOnceTask`](../src/main/java/com/bergerkiller/bukkit/common/RunOnceTask.java): thread-safe delayed task that coalesces repeated scheduling requests.
- [`CommonNextTickExecutor`](../src/main/java/com/bergerkiller/bukkit/common/internal/CommonNextTickExecutor.java): internal executor used by `CommonUtil.nextTick()` and `getNextTickExecutor()`.

## Server tick timing

`CommonUtil.getServerTicks()` returns the server's tick counter, incremented once per server tick. Use it to measure elapsed ticks, track expiration, or deduplicate work within a tick. It measures server ticks rather than wall-clock time, so real-time delays vary with server performance.

For elapsed intervals, subtract the starting tick from the current tick. This also handles the counter wrapping around, provided the interval is shorter than half its integer range:

```java
int startedAt = CommonUtil.getServerTicks();
// Later:
if (CommonUtil.getServerTicks() - startedAt >= timeoutTicks) {
    expire();
}
```

## Deferring work to the next tick

`CommonUtil.nextTick(runnable)` is thread-safe and dispatches a task to run on the next server tick. Use it when an operation must happen after the current event or callback has finished, or when an action needs to be deferred for cleanup. It is not a background-thread executor; the task runs as server-thread work. If called from a `nextTick` callback, it runs no earlier than the following tick.

```java
CommonUtil.nextTick(() -> finishAfterCurrentEvent());
```

Use `CommonUtil.getNextTickExecutor()` when an `Executor` is needed, including for `CompletableFuture` continuations. If work should run immediately when already on the main thread, use `CommonUtil.getMainThreadExecutor()` instead.

## Coalescing scheduled work with RunOnceTask

`RunOnceTask` is useful when many callers can request the same deferred action. Calling `start()` repeatedly while the task is already scheduled keeps a single pending execution; after it runs, it can be scheduled again. The class is thread-safe.

Use `restart(delay)` when each new request should cancel and postpone the pending execution. Use `runNowIfScheduled()` to cancel a pending run and perform it immediately. `start()` uses a one-tick delay by default.

## Lock-free reads with synchronizeCopyOnWrite

`LogicUtil.synchronizeCopyOnWrite()` is a helper for caches that are read frequently and updated occasionally. It first reads the current mapping without taking the lock. If the key is missing, it synchronizes on the supplied lock, checks again against the latest mapping, and invokes the computer only if the key is still absent. This keeps cache hits off the lock while serializing cache misses.

The helper does not make a mutable map safe by itself. The mapping must be treated as an immutable snapshot: the computer should copy the current mapping, apply the update to the copy, and publish the new snapshot rather than mutating the existing one. The field holding the snapshot must also be safely published for lock-free reads (for example, with a `volatile` field). Return `null` from the getter to indicate a cache miss; therefore, this pattern does not cache `null` values.

See the [examples](examples/common-logic-utilities.md) for a complete cache pattern and task recipes.

## Synchronizing collections

`LogicUtil.synchronizeList()` and `synchronizeUnordered()` update an existing collection to match a supplied collection of values while calling hooks for items that are added or removed. Use them when those transitions have side effects, such as creating/destroying a view or applying/removing focus, and avoid rebuilding the whole collection on every update.

Choose `synchronizeList()` when the target is an ordinary `List`, the source order should be reflected, and you need to match source values to potentially different target item types using `isItem()`. It is also suitable for one-off synchronization of a collection you already own. Choose `synchronizeUnordered()` when order does not matter and both collections contain the same value type with compatible `contains()` semantics.

Choose [`SortedIdentityCache`](collections.md#sortedidentitycache) when you repeatedly synchronize a stable ordered source of key objects and want to retain a derived value for each key instance. It combines identity-keyed lookup, ordered iteration of cached values, and add/remove lifecycle callbacks in one purpose-built collection. It avoids `synchronizeList()`'s iterator overhead, but is not a general replacement: keys are matched only by identity, it is not thread-safe, and it maintains its own cache structure. In short, use the synchronization methods to reconcile collections you already have; use `SortedIdentityCache` when the mapping and its synchronized iteration order are themselves the collection you need.

Both methods return whether the target collection changed. `ItemSynchronizer.isItem(item, value)` decides whether an existing target item corresponds to a source value. `onAdded(value)` creates or returns the target item to store; `onRemoved(item)` handles an item that is leaving.

`synchronizeList()` preserves source iteration order in the target list. It searches ahead for an existing matching item and moves it into the required position, reusing it without calling add/remove hooks. It calls `onAdded` for new values and `onRemoved` only for values that disappear, not for items merely reordered.

`synchronizeUnordered()` is for sets or other collections whose order does not matter. It removes target items absent from the source and adds missing values. Unlike `synchronizeList()`, it relies on `values.contains()` and `collection.contains()`; `ItemSynchronizer.isItem()` is not called, and both collections must use compatible value types and equality semantics. It avoids traversing the source when it is empty, and avoids the source iterator when no additions are needed.

See the [examples](examples/common-logic-utilities.md) for both patterns.

## Generic unsafe casts

`LogicUtil.unsafeCast(value)` gives a value an inferred generic type without a runtime type check. It can remove an unchecked-cast warning where the programmer already knows a type relationship that Java's generic type system cannot express conveniently. The cast does not validate the object: an incompatible value may fail later when read or used as the requested type.

Use it narrowly, only when the runtime invariant is clear and documented by the surrounding API or code. Prefer ordinary typed APIs, a checked `Class.cast()`, or a wildcard type where possible. Do not use it to silence warnings when the actual element types are uncertain. `CommonUtil.unsafeCast()` is a deprecated compatibility alias; new code should use `LogicUtil.unsafeCast()`.

This is useful for generic wrappers and APIs whose raw runtime types are known but whose compile-time generic signatures are less specific. The [examples](examples/common-logic-utilities.md) show how to isolate such a cast and its required invariant.
