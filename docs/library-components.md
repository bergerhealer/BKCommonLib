# LibraryComponent Lifecycle API

`LibraryComponent` is a generic lifecycle contract for independently enabled and disabled pieces of a plugin or library. `LibraryComponentList` and `LibraryComponentSelector` keep track of the components that successfully start, report failures with context, and disable active components in reverse order. This replaces manually duplicating startup and shutdown order as the plugin grows.

## Replacing paired lifecycle calls

Without a component holder, every component added in `onEnable()` also needs a corresponding reverse-order call in `onDisable()`:

```java
public void onEnable() {
    soundEngine.enable(this);
    particleEngine.enable(this);
}

public void onDisable() {
    particleEngine.disable();
    soundEngine.disable();
}
```

With `LibraryComponentList`, register each component once as it is enabled. The list records successful starts and handles reverse-order shutdown:

```java
public final class ExamplePlugin extends JavaPlugin {
    private LibraryComponentList<ExamplePlugin> components;

    @Override
    public void onEnable() {
        components = LibraryComponentList.forPlugin(this);
        components.enable("sound engine", new SoundEngine(this));
        components.enable("particle engine", new ParticleEngine(this));
    }

    @Override
    public void onDisable() {
        if (components != null) {
            components.disable();
        }
    }
}
```

Each component implements `LibraryComponent` and gets its environment (here, the plugin) through its constructor. Components enabled in the order sound then particle will be disabled particle then sound.

## `LibraryComponent`

Implement `enable()` and `disable()` for the work owned by one component. The component may be a subsystem, listener, manager, or any other unit with a clear start/stop lifecycle; the API does not impose a specific component hierarchy.

`LibraryComponentList<E>` holds multiple independent components. Its environment is passed to support checks and creator functions. `LibraryComponentList.forPlugin(plugin)` is a convenience factory that uses the plugin as the environment and supplies its logger and name for diagnostics. For other environments, construct the list with `(environment, logger, identifier)`.

The list supports:

- `enable(component)` or `enable(identifier, component)` to start an existing instance.
- `enableCreate(factory)` to create a component from the environment, or `enable(conditional)` for conditional creation.
- `enableForVersions(identifier, minVersion, maxVersion, factory)` to enable only within an inclusive Minecraft version range. An empty or null bound is unbounded.
- `disable()` to disable all successfully started components in reverse order. It may be called repeatedly; after the tracked components have been drained, later calls have nothing else to disable.

For version-independent conditions, create one with `LibraryComponent.when(identifier, predicate, factory)`. Predicates receive the environment, and factories only run when the predicate passes.

### Failure handling

Support checks, creation, and `enable()` failures are logged with the holder/component identifiers; the failure is saved as `getLastError()`. A failed or unsupported component is not added to the list. If a component's own `enable()` partially initializes and then throws, the list cannot know how to roll it back and will not call its `disable()`; make component initialization transactional or clean up its own partial startup before propagating the failure.

When disabling, each exception is logged and the holder continues disabling earlier components, so one broken shutdown does not prevent other cleanup. Use stable, descriptive identifiers with `enable(identifier, component)` or a custom `Conditional`; they make lifecycle errors easier to diagnose.

`runFirst(runnable)` queues an initializer for the holder's environment. Initializers run in registration order the next time a component is enabled/selected, before component setup. If one fails, the current attempt aborts, its error is logged and retained, and that initializer is retried on a later attempt. If initialization must happen immediately, execute it explicitly rather than relying on this deferred hook.

## Selecting one implementation

Use `LibraryComponentSelector<E, L>` when several components implement the same API and only one should be active—for example, choosing an implementation based on server capabilities. A common pattern is to make the selected implementation a static `INSTANCE` on the API type, so the rest of the library always calls the same interface while the selector chooses a server-suitable implementation:

```java
public abstract class SoundEngine implements LibraryComponent {
    public static final SoundEngine INSTANCE = LibraryComponentSelector.forModule(SoundEngine.class)
            .setDefaultComponent(DisabledSoundEngine::new)
            .addWhen("Server sound API", env -> ServerSoundEngine.isSupported(),
                     ServerSoundEngine::new)
            .addWhen("Fallback sound API", env -> true, FallbackSoundEngine::new)
            .update();

    public abstract void play(String sound);
}
```

The `INSTANCE` field initializes the selector when `SoundEngine` is first referenced. The `update()` result is the selected component itself, so callers use `SoundEngine.INSTANCE.play(...)` without depending on which implementation was chosen. BKCommonLib uses this pattern for APIs such as [`BlockStateChangePacketHandler.INSTANCE`](../src/main/java/com/bergerkiller/bukkit/common/internal/logic/BlockStateChangePacketHandler.java). `forModule(...)` uses no environment (`Void`); use `forPlugin(plugin)` or construct a selector with an environment when selection depends on plugin state. `addVersionOption(minVersion, maxVersion, factory)` is a convenience for inclusive Minecraft-version conditions.

Options are checked in registration order. `update()` enables the first supported implementation that initializes successfully and disables the previous implementation when switching. A failed support check is logged and skipped; a component that fails to create or enable does not block trying later options. Since a static `INSTANCE` is selected when the type is initialized, it is best for server/environment conditions that are stable for the process lifetime. The one-line pattern does not retain the selector itself, so use it for implementations that do not need later reevaluation or selector-managed shutdown. For resources that need explicit cleanup, or conditions that can change, retain the selector (and call `update()` or `disable()` as needed) and expose the currently selected implementation through an accessor.

`setDefaultComponent(...)` is optional. When configured, its component is used if no option can be enabled and is restored when the selector is disabled; it is commonly a no-op implementation or one that reports unavailability. Without a default, `get()` returns `null` when no implementation is active.

`LibraryComponentSelector` itself implements `LibraryComponent`, so a selector can also be added to a `LibraryComponentList`. Calling the selector's `enable()` performs `update()`; disabling it stops the selected component and restores its default if configured. Call `update()` again when external conditions may have changed and a different implementation should be selected.

## Related: Bukkit plugin and service dependencies

`LibraryComponent` is for managing components owned by the library or plugin using it: feature modules, alternative implementations, and other units whose lifecycle the owner explicitly controls. For an optional *other Bukkit plugin* or Bukkit service, use [SoftDependency](https://github.com/bergerhealer/SoftDependency) instead. It detects the dependency and manages its lifecycle around Bukkit's plugin lifecycle, enabling it after the owning plugin starts and disabling it before the owner shuts down; its `get()` result is available when the dependency is enabled.

SoftDependency is included in BKCommonLib, but is also distributed as a small standalone library and does not require BKCommonLib. Use the right mechanism for each layer: a `SoftDependency` can determine whether an external plugin/service is available, while a `LibraryComponentSelector` can then choose among this plugin's own implementations.

## Key files

- [`LibraryComponent`](../src/main/java/com/bergerkiller/bukkit/common/component/LibraryComponent.java): lifecycle and conditional component contracts.
- [`LibraryComponentHolder`](../src/main/java/com/bergerkiller/bukkit/common/component/LibraryComponentHolder.java): shared environment, logging, initializer, and error handling.
- [`LibraryComponentList`](../src/main/java/com/bergerkiller/bukkit/common/component/LibraryComponentList.java): ordered enablement and reverse-order cleanup.
- [`LibraryComponentSelector`](../src/main/java/com/bergerkiller/bukkit/common/component/LibraryComponentSelector.java): prioritized implementation selection and fallback behavior.
