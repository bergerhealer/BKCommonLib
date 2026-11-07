# BKCommonLib Overview

BKCommonLib is a Bukkit server plugin and library for plugin authors who need convenient access to Minecraft features that vary across server versions. It supplies plugin-facing APIs for common operations and a compatibility layer for server internals, so dependent plugins can rely on BKCommonLib rather than implementing each version-specific integration themselves.

The primary package is `com.bergerkiller.bukkit.common`. BKCommonLib targets Bukkit-derived servers and supports multiple Minecraft/server versions; actual compatibility depends on the running server and the specific capability a feature needs.

## Key files and source areas

- [`Common.java`](../src/main/java/com/bergerkiller/bukkit/common/Common.java): Public constants and shared entry points, including server/version information and compatibility status.
- [`CommonBootstrap.java`](../src/main/java/com/bergerkiller/bukkit/common/internal/CommonBootstrap.java): Initializes server detection, compatibility checks, templates, and related shared infrastructure.
- [`PluginBase.java`](../src/main/java/com/bergerkiller/bukkit/common/PluginBase.java): Optional extended plugin base with lifecycle, dependency, command, permission, and localization helpers.
- [`common/server/`](../src/main/java/com/bergerkiller/bukkit/common/server/): Server detection and implementation-specific behavior.
- [`src/main/templates/`](../src/main/templates/): Mountiplex templates describing version-dependent Minecraft and server types.
- [`common/config/`](../src/main/java/com/bergerkiller/bukkit/common/config/): YAML-backed configuration nodes, file configuration, and serialization; see the [Configuration API](configuration.md) for node behavior, defaults, listeners, and saving.
- [`common/nbt/`](../src/main/java/com/bergerkiller/bukkit/common/nbt/): Cross-version NBT wrappers used by utilities and item, entity, and block APIs; see the [CommonTag API](commontag.md).
- [`common/inventory/`](../src/main/java/com/bergerkiller/bukkit/common/inventory/): Item and inventory APIs, including the cross-version [`CommonItemStack`](../src/main/java/com/bergerkiller/bukkit/common/inventory/CommonItemStack.java) wrapper; see the [CommonItemStack guide](common-item-stack.md) for item properties and plugin-owned NBT metadata.
- [`common/protocol/`](../src/main/java/com/bergerkiller/bukkit/common/protocol/): Packet types, packet wrappers, listeners, and protocol integration.
- [`common/map/`](../src/main/java/com/bergerkiller/bukkit/common/map/): Virtual map displays, drawing, map resources, and interactive widgets.
- [`common/math/`](../src/main/java/com/bergerkiller/bukkit/common/math/): Vectors, quaternions, matrices, and geometry.
- [`common/wrappers/`](../src/main/java/com/bergerkiller/bukkit/common/wrappers/), [`common/entity/`](../src/main/java/com/bergerkiller/bukkit/common/entity/), and [`common/inventory/`](../src/main/java/com/bergerkiller/bukkit/common/inventory/): Compatibility-oriented APIs for Minecraft values, entities, and items.
- [`common/utils/`](../src/main/java/com/bergerkiller/bukkit/common/utils/): General helpers for world, block, entity, item, material, parsing, and other commonly used operations.
- [`common/events/`](../src/main/java/com/bergerkiller/bukkit/common/events/): Additional server and packet events exposed to plugins.

## Architecture and relationships

BKCommonLib's public API is mainly organized under `com.bergerkiller.bukkit.common`. Plugins can use the Bukkit-facing classes in that package directly, or extend `PluginBase` where its lifecycle and helper behavior is useful.

Minecraft server internals change between versions and distributions. BKCommonLib detects the runtime server through its server layer, initializes compatibility infrastructure, and uses Mountiplex templates and generated handles to access version-dependent types. Conversion APIs and wrappers bridge between Bukkit-facing values and those internals. Server-dependent code should use the library's compatibility APIs and capability checks rather than assuming a specific internal class or feature exists.

The feature packages build on that foundation:

- Configuration nodes and file configurations provide hierarchical YAML storage and typed access.
- CommonTag wrappers expose Minecraft NBT data in a version-independent form used across BKCommonLib APIs.
- Protocol APIs identify and construct packets; packet utilities send them, and packet listeners and events expose incoming and outgoing traffic.
- Map displays combine virtual map updates, drawing APIs, map resources, player input, and widgets to create interactive interfaces.
- Math and geometry APIs model spatial values and transformations used by map, entity, and world features.
- Wrappers, entity APIs, inventory APIs, and utilities provide common operations without requiring each plugin to repeat low-level compatibility work.

These are related API areas, not a strict dependency chain: plugins can use individual facilities as needed, while many higher-level features rely on shared server detection, conversion, wrappers, and utilities.

## Common task entry points

- To access a cross-version server feature, begin with the relevant public wrapper or utility package and check its capability requirements. If no suitable API exists, inspect the related template and compatibility implementation before adding a version-specific path.
- To read or write YAML configuration, start with the [Configuration API](configuration.md) and its [examples](examples/configuration.md); maintainers can follow the [YAML implementation](internals/yaml.md).
- To inspect or serialize NBT data, see the [CommonTag API](commontag.md) and [examples](examples/commontag.md).
- To create or edit an item across server versions, or store plugin-owned item identifiers and metadata, see the [CommonItemStack guide](common-item-stack.md) and [CommonTag API](commontag.md).
- To read or change a block's material and properties across versions, see the [BlockData guide](block-data.md).
- To access held items or interpret main/off-hand interaction packets across versions, see the [Human Hands guide](human-hand.md).
- To find recipes by output item or craft recipe results from an inventory, see the [crafting recipes guide](crafting-recipes.md).
- To create or inspect packets, start with [`PacketType`](../src/main/java/com/bergerkiller/bukkit/common/protocol/PacketType.java) and [`CommonPacket`](../src/main/java/com/bergerkiller/bukkit/common/protocol/CommonPacket.java). For observing or changing traffic, see [`PacketListener`](../src/main/java/com/bergerkiller/bukkit/common/protocol/PacketListener.java) and packet events under `common/events/`.
- To decide which packet to intercept or send, consult the [Packet Protocol guide](protocol.md) for packet listeners, monitors, and the available packet types.
- To build and send entity metadata, see the [DataWatcher guide](protocol-datawatcher.md) for typed keys, reusable prototypes, and spawn/change packets.
- To implement an interactive map, start with [`MapDisplay`](../src/main/java/com/bergerkiller/bukkit/common/map/MapDisplay.java), then follow its map canvas, input events, and widgets.
- To create a virtual map interface, see the [Map Displays guide](map-displays.md), [Map Markers guide](map-markers.md), [Map Widgets guide](map-widgets.md), and [Map Resource Packs guide](map-resource-pack.md). Examples cover [custom widgets](examples/map-widgets-simple.md), [popup menus](examples/map-widgets-menu.md), and [3D model drawing](examples/map-model-drawing.md). Maintainers can follow the [map display implementation](internals/map-displays.md).
- To play a named sound for one player or for players near a world location, see the [Sound Effects guide](sound-effects.md).
- To work with spatial transformations, start with [`Matrix4x4`](../src/main/java/com/bergerkiller/bukkit/common/math/Matrix4x4.java), [`Quaternion`](../src/main/java/com/bergerkiller/bukkit/common/math/Quaternion.java), and the related vector and geometry types; see the [maths guide](math.md) and [math examples](examples/math.md).
- To choose a specialized collection for snapshots, high-volume shared sets, pending updates, stable identity mappings, or spatial lookup, see the [collections guide](collections.md) and [collections examples](examples/collections.md).
- To keep world or block references when a Bukkit world may be unloaded, see the [offline worlds and blocks guide](offline-worlds.md).
- To list worlds and dimensions that exist on disk, inspect their format-independent storage paths, or load one, see the [LoadableWorld guide](loadable-worlds.md).
- To detect sign text changes or work with front/back sign text, see the [SignChangeTracker guide](sign-change-tracker.md) and [examples](examples/sign-change-tracker.md).
- To defer cross-chunk work until required chunks are loaded without forcing synchronous loads, see the [ChunkFutureProvider guide](chunk-future-provider.md) and [examples](examples/chunk-future-provider.md).
- To keep chunks loaded while processing them asynchronously, especially when loading large areas without synchronous-load lag, see the [ForcedChunk guide](forced-chunks.md). Maintainers can follow its [internal implementation](internals/forced-chunks.md).
- To build commands with the shaded Cloud Command Framework, start with [`CloudSimpleHandler`](../src/main/java/com/bergerkiller/bukkit/common/cloud/CloudSimpleHandler.java), the [BKCommonLib Cloud guide](cloud-command-framework.md), and the [dependency/shading how-to](../CLOUD_HOWTO.md).
- To declare and register permission nodes with defaults and descriptions, see the [permission examples](examples/permissions.md).
- To manage independently enabled subsystems or select one conditional implementation, see the [LibraryComponent lifecycle guide](library-components.md).
- To customize supported entity behavior, install an [Entity Controller](entity-controllers.md) from `EntityAddEvent`; maintainers can follow the [hook and replacement implementation](internals/entity-controllers.md).
- To listen for BKCommonLib entity/chunk lifecycle, pre-spawn, sign-edit, advancement-progress, or bulk block-change events, see the [general events guide](events.md). Map-display and packet events are documented separately.
- To maintain the mapping between Bukkit entity types, version-specific NMS classes, and BKCommonLib entity wrappers, see the [CommonEntityType internals guide](internals/common-entity-type.md). Plugin-facing code should generally use Bukkit `EntityType`.
- To measure elapsed server ticks, defer work to the next tick, coalesce delayed work, or build a read-mostly cache, see the [CommonUtil and LogicUtil guide](common-logic-utilities.md) and [examples](examples/common-logic-utilities.md).
- To schedule a plugin-owned delayed or repeating task, see the [Task guide](tasks.md).
- To understand server initialization or a compatibility issue, trace [`CommonBootstrap`](../src/main/java/com/bergerkiller/bukkit/common/internal/CommonBootstrap.java), the relevant class under `common/server/`, and the corresponding template under `src/main/templates/`.
- To diagnose template resolution, remapping, or generated-handle setup, see the maintainer [template and runtime resolution guide](internals/templates.md) and Mountiplex's [template syntax reference](https://github.com/bergerhealer/Mountiplex/blob/master/docs/template-syntax.md).

## External dependencies and boundaries

BKCommonLib builds against the Spigot API and includes Mountiplex for version-dependent reflection and template support. It also integrates with server networking and may optionally interact with other plugins or libraries. The Bukkit API is the normal plugin-facing boundary; direct interaction with internal server classes is compatibility-sensitive and should be routed through BKCommonLib's established templates, wrappers, and conversion facilities.

For terminology, see the [glossary](glossary.md). For rules on adding or maintaining pages, see [About This Documentation](about-documentation.md).
