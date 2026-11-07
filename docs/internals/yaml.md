# YAML Configuration Internals

**Audience:** BKCommonLib maintainers diagnosing or changing the configuration implementation. Plugin authors should use [`ConfigurationNode`](../../src/main/java/com/bergerkiller/bukkit/common/config/ConfigurationNode.java) and [`FileConfiguration`](../../src/main/java/com/bergerkiller/bukkit/common/config/FileConfiguration.java); the `Yaml*` classes described here are implementation machinery, not the normal configuration API.

This page describes the YAML tree model, path and entry bookkeeping, collection views, and parsing/serialization pipeline. For caller-facing behavior such as defaults, conversion, and file saving, see the [Configuration API](../configuration.md).

## Key files and structure

- [`YamlNodeAbstract`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlNodeAbstract.java): Shared tree-node implementation: child entries, typed access, tree mutations, listeners, cloning, and YAML I/O.
- [`YamlNode`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlNode.java): Default concrete node implementation.
- [`YamlListNode`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlListNode.java): List-backed node whose child paths are positional list elements.
- [`YamlEntry`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlEntry.java): Stores a key's value and metadata, tracks listeners, and maintains its cached YAML fragment.
- [`YamlRoot`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlRoot.java): Indexes entries by their absolute `YamlPath` and handles creation, movement, and detachment.
- [`YamlPath`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlPath.java): Immutable structured path, including list-index path parts.
- [`YamlSerializer`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlSerializer.java) and [`YamlDeserializer`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlDeserializer.java): YAML formatting and parsing, including custom type deserialization.
- [`YamlChangeListener`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlChangeListener.java) and [`YamlChangeListenerRelative`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlChangeListenerRelative.java): Listener contract and path-relative adapter.
- [`YamlNodeLinkedValue`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlNodeLinkedValue.java), [`YamlNodeIndexedValueList`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlNodeIndexedValueList.java), and [`YamlNodeLazyCreateValueList`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlNodeLazyCreateValueList.java): List and collection views that link external collection operations to a tree entry.
- Map and collection views:
  - [`YamlNodeMapProxy`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlNodeMapProxy.java): Exposes a node's immediate children through a backed `Map`.
  - [`YamlNodeEntrySetProxy`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlNodeEntrySetProxy.java): Backed entry-set view for a node's key/value entries.
  - [`YamlNodeKeySetProxy`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlNodeKeySetProxy.java): Backed key-set view, exposing either string keys or structured paths.
  - [`YamlNodeDeepKeySetProxy`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlNodeDeepKeySetProxy.java): Backed key-set view that includes descendants recursively.
  - [`YamlNodeValueCollectionProxy`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlNodeValueCollectionProxy.java): Backed view of values in a mapping node.
  - [`YamlNodeMappedIterator`](../../src/main/java/com/bergerkiller/bukkit/common/config/yaml/YamlNodeMappedIterator.java): Shallow or deep iterator that maps entries to values and supports mutation of the current entry.

## Tree and ownership

`YamlNodeAbstract` owns an insertion-ordered list of child `YamlEntry` objects and a reference to a `YamlRoot`. A node's root entry contains the node itself; each child entry holds its key or list-index path, value, header, listener state, and serialized fragment. `YamlRoot` maintains a path-to-entry index so path lookups can find entries without recursively walking every child.

`YamlNode` supplies the ordinary node type used for nested mappings. `YamlListNode` also uses the node machinery but implements `List<Object>` and gives its children index paths. Inserting or removing list elements therefore updates the paths of later entries and the root index.

`ConfigurationNode` extends `YamlNodeAbstract` with the public configuration-node type. `BasicConfiguration` and `FileConfiguration` build on `ConfigurationNode` for root formatting and file lifecycle behavior. Internal node operations are shared by these public-facing classes; external plugin documentation should describe the ConfigurationNode API rather than treating `YamlNodeAbstract` as the entry point.

Assigning a map or collection as an entry value materializes it as nested tree nodes or a list node. Assigning an already-parented node requires copying its entries so a child entry is not simultaneously owned by two trees. Removing a nested node detaches its contents into a standalone tree. Clone and copy operations similarly rebuild entry ownership and path indexes, with listener preservation depending on the operation.

## Paths and live views

`YamlPath` is a structured immutable path: each element links to its parent, with a special path element for list indexes. Dotted strings can be parsed as paths; `childWithName` appends a literal key as one component when the key itself contains separators or list-like characters. Paths are used both for root-index lookups and as event payloads.

Collection accessors expose proxies rather than copying all node data into separate Java collections. `YamlNodeMapProxy` provides the shallow map interface; its key, value, and entry views are backed by the node. Key-set variants expose keys as strings or structured paths, with the deep-key set recursively including descendants. The mapped iterator supports operations on entries while walking a node.

`YamlNodeIndexedValueList` presents indexed mapping children as a list, including conversion and key ordering behavior. `YamlNodeLazyCreateValueList` defers creating or attaching the underlying list representation until a mutating operation needs it. `YamlNodeLinkedValue` marks collections whose operations are bound to an entry, so assignment can connect the view to the target tree. These adapters are internal mechanisms for the collection-shaped operations on nodes.

## Change propagation

Listeners registered on an entry are combined with listeners inherited from its ancestors. When a value, header, child, or list structure changes, the entry invokes the effective listeners with its path. `YamlChangeListenerRelative` adapts that path to the node where the listener was registered, so callbacks receive a relative path rather than a root-global one. Listener exceptions are logged so a failing callback does not abort the tree mutation or prevent other callbacks from running.

Listeners are recalculated and propagated when tree structure or listener registration changes. Moving or cloning nodes must keep the listener arrays consistent with the new parent chain; this is one reason entry ownership and reparenting are handled centrally rather than by editing the child lists alone.

## Cached YAML generation

Each `YamlEntry` owns a `StringTreeNode` fragment and tracks whether its own YAML needs regeneration and whether its descendants need checking. A mutation invalidates the changed entry and marks ancestors to inspect their children. When YAML output is requested, generation proceeds only through invalidated branches; unaffected child fragments are reused. The cached fragments therefore make repeated saves efficient when updates touch only a small portion of a large tree.

`YamlSerializer` formats individual values and keys, including headers and indentation. It is a synchronized singleton reused by entries. Its output avoids YAML anchors/aliases by disabling SnakeYAML's object-reference tracking structures; repeated object references are serialized as values rather than emitted as anchors.

## Parsing and type conversion

`YamlDeserializer` parses serialized text into a mapping, captures headers and indentation, and provides custom construction for Bukkit-serializable values. The node-loading path then installs that result in the tree. Serializer and deserializer instances are shared singletons; their entry points synchronize access to the underlying SnakeYAML machinery.

Typed node reads use BKCommonLib's parsing/conversion facilities rather than restricting values to the Java type SnakeYAML initially produced. YAML mapping deserialization also recognizes ItemStacks and delegates to `ItemStackDeserializer`, which handles Bukkit and Paper encodings and can migrate data written by newer server versions. See the [Configuration API](../configuration.md) for user-visible conversion and forward-compatibility behavior.

## Debugging entry points and gotchas

- If a node read or write resolves to the wrong entry, trace path construction in `YamlPath`, lookup/creation in `YamlRoot`, then the child list and entry ownership in `YamlNodeAbstract` and `YamlEntry`.
- If a listener is missing, verify both the entry's directly registered listeners and the inherited listener arrays recalculated by `YamlEntry`; also check relative-path adaptation.
- If an update causes a stale or unexpectedly regenerated YAML fragment, inspect invalidation in `YamlEntry.markYamlChanged()` and traversal in `YamlEntry.generateYaml()`.
- If a YAML value is malformed or has unexpected formatting, inspect the parser/serializer boundary and the specific representation in `YamlDeserializer` or `YamlSerializer`.
- Because cached fragments and path indexes are coupled to tree entries, code that reparents or clones children must preserve root-index membership, paths, listener ownership, and cache invalidation together.
