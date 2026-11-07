# Configuration API Examples

These examples show common `ConfigurationNode` and `FileConfiguration` patterns. Snippets omit imports and assume `plugin` is the owning `JavaPlugin`; helper methods named in an example represent application-specific work. For API behavior and trade-offs, see the [Configuration API](../configuration.md).

Each YAML block shows the result of calling `toString()` on the root configuration after the preceding Java snippet. Examples start with an empty configuration unless stated otherwise; for standalone nodes, the output is that node's `toString()`.

## Load settings, insert defaults, and save

`get(path, defaultValue)` both returns and stores a missing or unconvertible value. This lets one configuration file be the source of truth for both user-edited settings and defaults. Set comments on the same node so they are written beside the setting.

```java
FileConfiguration config = new FileConfiguration(plugin);
config.load();

config.setHeader("enabled", "Whether this feature is enabled");
boolean enabled = config.get("enabled", true);

config.setHeader("limits.maxItems", "Maximum number of items to process");
int maxItems = config.get("limits.maxItems", 64);

config.save();
```

```yaml
# Whether this feature is enabled
enabled: true
limits:
  # Maximum number of items to process
  maxItems: 64
```

Use `getOrDefault(path, defaultValue)` when the fallback should not be inserted into the configuration. Use `get(path, Type.class)` when a missing or unconvertible value should return `null`. The default-value overload infers the expected type from the non-null default object.

## Read and update nested sections

Use dotted paths for concise access, or `getNode()` when several values belong to the same section. `getNodeIfExists()` avoids creating a section during a read.

```java
ConfigurationNode limits = config.getNode("limits");
int maxItems = limits.get("maxItems", 64);
boolean enabled = limits.get("enabled", true);

ConfigurationNode optional = config.getNodeIfExists("advanced");
if (optional != null) {
    String mode = optional.get("mode", "automatic");
}

limits.set("maxItems", maxItems + 16);
```

```yaml
limits:
  maxItems: 80
  enabled: true
```

`getNode(path)` creates the node if absent; `getNodeIfExists(path)` returns `null` when the path is absent or stores a non-node value.

## Work with typed lists

`getList(path, Type.class)` returns a modifiable list backed by the configuration tree. Mutating the returned list updates the node; typed access converts values where possible and removes values that cannot be converted.

```java
List<String> disabledWorlds = config.getList("disabledWorlds", String.class);
disabledWorlds.add("creative");
disabledWorlds.remove("temporary");

config.set("blockLimits", Arrays.asList(12, 24, 48));
List<Integer> blockLimits = config.getList("blockLimits", Integer.class);
int firstLimit = blockLimits.get(0);
```

```yaml
disabledWorlds:
  - creative
blockLimits:
  - 12
  - 24
  - 48
```

To install defaults for a missing list, pass a typed default list:

```java
List<String> allowedTypes = config.getList(
        "allowedTypes",
        String.class,
        Arrays.asList("minecart", "chest")
);
```

```yaml
allowedTypes:
  - minecart
  - chest
```

## Store lists of configuration nodes

Use `getNodeList()` when each list element contains multiple related values. The returned list is backed by the configuration. Add or edit nodes through it, and call `setNodeList()` when assigning a separately assembled list.

```java
List<ConfigurationNode> stops = config.getNodeList("route.stops", false);

ConfigurationNode first = new ConfigurationNode();
first.set("station", "central");
first.set("waitTicks", 40);
stops.add(first);

ConfigurationNode second = new ConfigurationNode();
second.set("station", "harbor");
second.set("waitTicks", 20);
stops.add(second);
```

```yaml
route:
  stops:
    - station: central
      waitTicks: 40
    - station: harbor
      waitTicks: 20
```

The `false` argument selects ordinary YAML sequence-style list storage when the list is first created. The no-argument overload uses the legacy indexed-node representation. When reading existing indexed node mappings, `getNodeList()` presents the children as a Java list; adding to that view may convert the stored keys to sequential indexes. Prefer sequence-style lists for new list data unless an existing format requires indexed mappings.

To build the list outside the configuration first:

```java
ConfigurationNode savedTrain = new ConfigurationNode();
List<ConfigurationNode> carts = new ArrayList<>();

ConfigurationNode cart = new ConfigurationNode();
cart.set("type", "rideable");
cart.getNode("model").set("name", "passenger");
carts.add(cart);

savedTrain.setNodeList("carts", carts);
```

```yaml
carts:
  0:
    type: rideable
    model:
      name: passenger
```

`setNodeList()` is useful when assembling the nodes separately from the configuration. When creating a new path, this overload uses the legacy indexed-node mapping representation; for sequence-style storage, create the view with `getNodeList(path, false)` and add nodes to that backed list. An empty or `null` node list removes the path.

BKCommonLib's [`ConfigTest`](../../src/test/java/com/bergerkiller/bukkit/common/ConfigTest.java) covers creation, insertion, nested fields, and file round-tripping for configuration node lists.

## Add comments to the file

Use `setHeader()` to replace the comments on a node or value. Use `addHeader()` to append lines, which is useful when assembling comments conditionally.

```java
config.setHeader("resourcePack", "\nResource pack to use");
config.addHeader("resourcePack", "Use 'server' to use the server resource pack.");
config.addHeader("resourcePack", "Use 'default' or an empty value to disable it.");
config.set("resourcePack", "server");

ConfigurationNode sounds = config.getNode("sounds");
sounds.setHeader("Global sound settings");
sounds.setHeader("hiss", "Whether to play the hiss sound");
sounds.set("hiss", true);
```

```yaml

# Resource pack to use
# Use 'server' to use the server resource pack.
# Use 'default' or an empty value to disable it.
resourcePack: server
# Global sound settings
sounds:
  # Whether to play the hiss sound
  hiss: true
```

A leading newline in the header leaves a blank line before the comment in the generated YAML.

## React to changes

Register a listener on the configuration or on a section to mark derived data as stale. The callback receives the changed path relative to the node where the listener was registered.

```java
AtomicBoolean needsReload = new AtomicBoolean(false);
config.addChangeListener(path -> needsReload.set(true));

config.set("limits.maxItems", 128);
if (needsReload.getAndSet(false)) {
    reloadDerivedState(config);
}
```

```yaml
limits:
  maxItems: 128
```

For section-specific tracking:

```java
ConfigurationNode sounds = config.getNode("sounds");
AtomicBoolean soundsNeedReload = new AtomicBoolean(false);
sounds.addChangeListener(path -> soundsNeedReload.set(true));

sounds.set("hiss", true);
if (soundsNeedReload.getAndSet(false)) {
    reloadSounds(sounds);
}
```

```yaml
sounds:
  hiss: true
```

Creating or removing child nodes can also notify listeners on their parent. Do not rely on exactly one callback per high-level operation; treat callbacks as invalidation signals and recompute from the current node state. The listener behavior is covered by BKCommonLib's [`YamlListenerTest`](../../src/test/java/com/bergerkiller/bukkit/common/YamlListenerTest.java).

## Copy and clone configuration trees

Use `clone()` to make an independent copy. Use `cloneInto(target)` to copy a node's contents into an existing target node; subsequent changes to either tree do not mutate the other's copied entries.

```java
ConfigurationNode defaults = new ConfigurationNode();
defaults.set("enabled", false);
defaults.set("privateData", "secret");

ConfigurationNode editable = defaults.clone();
editable.set("enabled", true);

ConfigurationNode exported = new ConfigurationNode();
editable.cloneInto(exported);
exported.remove("privateData");
```

`editable.toString()`:

```yaml
enabled: true
privateData: secret
```

`exported.toString()`:

```yaml
enabled: true
```

Use `setTo(source)` when the target should be replaced with the source contents, including removal of target keys that are absent from the source. This differs from merging: omitted keys do not remain in the target.

## Save without blocking on file I/O

`save()` converts the current configuration to YAML text synchronously, then dispatches the file write asynchronously. The tree is read for encoding on the calling thread; only the disk I/O runs in the background.

```java
FileConfiguration config = new FileConfiguration(plugin);
config.set("enabled", true);
config.save();
```

`config.toString()`:

```yaml
enabled: true
```

Use `saveSync()` if the caller must wait until the write completes. For example, a shutdown or handoff path may need to flush pending saves before continuing:

```java
FileConfiguration config = new FileConfiguration(plugin);
config.set("enabled", true);
config.saveSync();
```

`config.toString()` has the same output:

```yaml
enabled: true
```

`ConfigTest` exercises load, edit, save, and reload round-tripping. See the [Configuration API](../configuration.md) for per-file save ordering and flush operations.

## Related source

- [`ConfigTest`](../../src/test/java/com/bergerkiller/bukkit/common/ConfigTest.java): `ConfigurationNode` YAML round-tripping, typed reads, and node lists.
- [`YamlTest`](../../src/test/java/com/bergerkiller/bukkit/common/YamlTest.java): Nested lists, list mutation, path access, and copy behavior. This test also includes internal YAML implementation cases.
- [`YamlListenerTest`](../../src/test/java/com/bergerkiller/bukkit/common/YamlListenerTest.java): Change notifications for nested nodes, list edits, headers, and copy operations.
