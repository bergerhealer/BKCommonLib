# CommonUtil and LogicUtil Examples

These examples cover [server tick timing, next-tick scheduling, `RunOnceTask`, copy-on-write caches, collection synchronization, and generic casts](../common-logic-utilities.md).

## Expiring an entry after a number of server ticks

Store the tick when the timeout begins, then compare the elapsed tick count. This avoids relying on wall-clock time and remains correct across integer counter wraparound for ordinary timeout intervals.

```java
private final int createdAtTick = CommonUtil.getServerTicks();

boolean isExpired(int timeoutTicks) {
    return CommonUtil.getServerTicks() - createdAtTick >= timeoutTicks;
}
```

## Deferring work until after the current callback

`nextTick()` also works when called from another thread. The callback itself runs as server-thread work on a later tick.

```java
CommonUtil.nextTick(() -> {
    if (entity.isValid()) {
        updateEntity(entity);
    }
});
```

## Coalescing repeated refresh requests

Keep one `RunOnceTask` per refresh operation. Any number of calls to `start()` before it runs result in one scheduled refresh.

```java
private final RunOnceTask refreshTask;

MyPluginFeature(Plugin plugin) {
    this.refreshTask = RunOnceTask.create(plugin, this::refresh);
}

void requestRefresh() {
    refreshTask.start();
}
```

Use `refreshTask.restart(delayTicks)` instead if every request should postpone the refresh by the full delay.

## Caching values with copy-on-write snapshots

The helper serializes cache misses. The cache field below is `volatile` so threads performing lock-free reads see published snapshots. Each update creates a new map and never modifies a previously published map.

```java
private final Object cacheLock = new Object();
private volatile Map<String, Result> cache = Collections.emptyMap();

Result getOrCompute(String key) {
    return LogicUtil.synchronizeCopyOnWrite(
            cacheLock,
            () -> cache,
            key,
            Map::get,
            (current, missingKey) -> {
                Result result = compute(missingKey);
                Map<String, Result> updated = new HashMap<>(current);
                updated.put(missingKey, result);
                cache = Collections.unmodifiableMap(updated);
                return result;
            });
}
```

The getter and computer must agree on what counts as a miss: a `null` result from `Map::get` triggers computation. If `compute()` can return `null`, use a non-null sentinel or another representation if that result should be cached.

## Synchronizing an ordered list with lifecycle hooks

Here, the source is the desired sequence of models and the target list contains corresponding widgets. Existing widgets are reused and moved if the model order changes; creation and disposal only run when membership changes.

```java
LogicUtil.synchronizeList(widgets, models, new LogicUtil.ItemSynchronizer<Model, Widget>() {
    @Override
    public boolean isItem(Widget widget, Model model) {
        return widget.getModel() == model;
    }

    @Override
    public Widget onAdded(Model model) {
        return createWidget(model);
    }

    @Override
    public void onRemoved(Widget widget) {
        widget.dispose();
    }
});
```

Use `==` in `isItem()` when object identity defines the relationship; use `equals()` or a stable key comparison when values should match by value instead.

## Synchronizing an unordered viewer set

For an unordered target such as a `Set`, `synchronizeUnordered()` invokes hooks only for viewers entering or leaving the set. Matching uses the set/collection's `contains()` semantics, not `isItem()`.

```java
LogicUtil.synchronizeUnordered(viewers, newViewers,
        new LogicUtil.ItemSynchronizer<Player, Player>() {
    @Override
    public boolean isItem(Player item, Player value) {
        return item == value; // Not used by synchronizeUnordered()
    }

    @Override
    public Player onAdded(Player player) {
        spawnPreview(player);
        return player;
    }

    @Override
    public void onRemoved(Player player) {
        destroyPreview(player);
    }
});
```

## Isolating an unsafe generic cast

Only use `unsafeCast()` when another API guarantees the runtime element type. Keep that invariant next to the cast; the helper itself cannot check it.

```java
Object rawValue = getValueFromApi();
// The API contract guarantees this value is a List<String>.
List<String> names = LogicUtil.unsafeCast(rawValue);
```

If that guarantee is not available, validate at runtime instead of using an unsafe cast:

```java
if (rawValue instanceof List<?>) {
    for (Object value : (List<?>) rawValue) {
        if (value instanceof String) {
            useName((String) value);
        }
    }
}
```
