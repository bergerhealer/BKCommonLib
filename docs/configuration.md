# Configuration API

BKCommonLib's configuration API is a hierarchical, map-like data tree that can be read from and written to YAML files. Its nodes can also be created, cloned, and used independently of files. This is useful both for ordinary plugin settings and for in-memory structured data that benefits from the same access, conversion, and change-tracking behavior.

The API exists in part to make frequently read and updated configuration practical on a live server. Compared with using SnakeYAML or Bukkit's configuration API directly, it provides change listeners, asynchronous file writes, efficient updates when only some values change, readable files without emitted YAML anchors, integration with BKCommonLib's conversion system, and support for loading ItemStacks encoded with a future Minecraft data version on older servers.

## Key files

- [`ConfigurationNode`](../src/main/java/com/bergerkiller/bukkit/common/config/ConfigurationNode.java): General-purpose configuration tree node, including typed access, defaults, and operations on nested values.
- [`BasicConfiguration`](../src/main/java/com/bergerkiller/bukkit/common/config/BasicConfiguration.java): Root configuration node with YAML indentation handling.
- [`FileConfiguration`](../src/main/java/com/bergerkiller/bukkit/common/config/FileConfiguration.java): A `BasicConfiguration` associated with a file, with load and save operations.

## Node and file model

`ConfigurationNode` is the reusable data structure; it does not need a file or plugin to exist. Create one for temporary data, clone it, or use one as part of a larger data model. When a node is assigned beneath another node it becomes part of that tree; if it is already parented elsewhere, BKCommonLib clones it so both trees remain independent.

`BasicConfiguration` adds root-level YAML formatting behavior. `FileConfiguration` adds a file association and explicit `load()`, `save()`, and `saveSync()` operations. Calling `save()` schedules asynchronous file output; use `saveSync()` when the caller must wait for the save to finish. `ConfigurationNode` can also load or save YAML strings and streams independently of `FileConfiguration`.

The configuration tree preserves child ordering and supports headers (comments) attached to nodes and values. Maps and collections assigned as values are represented as nested nodes and lists, so callers can work with structured data without managing separate YAML serialization objects.

## Typed access and defaults

Typed getters convert stored values when possible. The two-argument `get(path, defaultValue)` treats the default as both the fallback and the expected value type. If the stored value is missing or cannot be converted to that type, the default is returned and written into the node. This makes first-load default creation part of normal configuration loading: defaults become part of the configuration being used and can later be saved, rather than maintained in a separate defaults file.

Use `getOrDefault(path, defaultValue)` when a fallback is needed but should not be written. Use the typed `get(path, type)` overload when a missing or unconvertible value should return `null` rather than persist a default. The `get(path, defaultValue)` overload requires a non-null default because its runtime type defines the conversion target.

Getters use BKCommonLib's parsing and conversion facilities, so supported conversions can include more than YAML's raw scalar types. See [`ParseUtil`](../src/main/java/com/bergerkiller/bukkit/common/utils/ParseUtil.java) and the [conversion package](../src/main/java/com/bergerkiller/bukkit/common/conversion/) for the shared conversion behavior.

## Change listeners

Register a change listener on a `ConfigurationNode` to be notified when that node or any descendant changes. The callback receives the changed path relative to the node where the listener was registered. A listener can also be registered at a particular existing path to observe only that value and its descendants. Listeners can be removed or cleared.

Listeners are useful for reacting to edits without polling the tree—for example, updating derived state when a section changes. A default inserted by a typed getter is a mutation, so it can also notify listeners.

## Serialization and saving

`FileConfiguration.save()` serializes the configuration to text synchronously on the calling thread, then dispatches the file I/O to run asynchronously. This keeps access to the configuration tree on the caller's thread while avoiding a blocking disk write; the encoding step itself is not moved to a background thread. Saves for the same file are ordered, and a later `load()` waits for an outstanding save to that file. `saveSync()` waits for the asynchronous write to finish; file-level flush methods are also available when coordinating shutdown or other lifecycle boundaries.

YAML output does not emit anchors or aliases. Anchors can make configuration files harder for users to understand and edit, so repeated values are written directly rather than represented as references.

## ItemStack compatibility

ItemStacks encoded with a future Minecraft data version can be loaded from configuration on older servers. This is useful when users exchange configuration files between servers running different Minecraft versions.

## Usage

For common plugin settings, use `FileConfiguration` to load a file, get values with defaults, and save the resulting configuration:

```java
FileConfiguration config = new FileConfiguration(plugin);
config.load();
boolean enabled = config.get("feature.enabled", true);
config.save();
```

For structured data that does not need a file, use `ConfigurationNode` directly. Use `getNode(path)` for a child section and `clone()` when an independent copy is needed.

For recipes covering lists, lists of nodes, headers, change listeners, copying, and saving, see the [Configuration API examples](examples/configuration.md).

## Relationships and conventions

- `FileConfiguration` extends the configuration-node model; it is not required for in-memory configuration data.
- Defaults passed to mutating getters are part of the live configuration and may be serialized when saved.
- Choose `save()` to keep file I/O off the calling thread and `saveSync()` when completion must be guaranteed before continuing. YAML encoding still occurs synchronously.
- Treat Bukkit/server objects and thread-sensitive values according to their own lifecycle rules; only the file I/O is asynchronous, not access to configuration values during YAML encoding.

The YAML tree and serializer classes are implementation details, intended for BKCommonLib maintainers diagnosing or changing the configuration internals. See the [internal YAML implementation](internals/yaml.md) when working on those internals. For project terms, see the [glossary](glossary.md).
