# BKCommonLib Documentation

Architecture and usage documentation for BKCommonLib. Start with the [overview](overview.md), then read focused pages as needed.

## Documentation guidelines

- [About This Documentation](about-documentation.md) — rules for writing and maintaining project documentation.

## Architecture

- [Overview](overview.md) — project purpose, major modules, relationships, and common task entry points.
- [Glossary](glossary.md) — definitions of BKCommonLib and Minecraft server terminology.

## Reference

- [Summary](SUMMARY.md) — compact project context for deciding whether BKCommonLib applies to a task.
- [Configuration API](configuration.md) — hierarchical YAML nodes, defaults, conversion, change listeners, and file saving.
- [CommonTag (NBT) API](commontag.md) — cross-version NBT tags, typed tag/value access, read-only empty compounds, and binary serialization.
- [CommonItemStack](common-item-stack.md) — cross-version item properties, Bukkit/NMS wrapper ownership, custom NBT metadata, and inventory update behavior.
- [BlockData](block-data.md) — reading and updating block material and properties across versions.
- [Human hands](human-hand.md) — cross-version main/off-hand inventory access and left/right hand roles in interactions and packets.
- [Crafting recipes](crafting-recipes.md) — finding crafting recipes by output and crafting them directly from inventory contents.
- [Maths and spatial transforms](math.md) — vectors, integer block/chunk coordinates, quaternions, matrices, transform composition, coordinate-key packing, and BlockFace direction helpers.
- [Specialized collections](collections.md) — cached immutable sets, copy-on-write snapshots, identity caches, update tracking, and spatial octrees.
- [Offline worlds and blocks](offline-worlds.md) — UUID-backed world references, offline block coordinates, loaded Bukkit access, and per-world maps.
- [LoadableWorld](loadable-worlds.md) — discovering on-disk worlds and dimensions, resolving their storage locations, and loading them across Spigot/Paper layouts.
- [CommonUtil and LogicUtil](common-logic-utilities.md) — server tick timing, next-tick scheduling, `RunOnceTask`, and lock-free copy-on-write cache reads.
- [Task](tasks.md) — scheduling and cancelling plugin-owned synchronous tasks.
- [Sound effects](sound-effects.md) — playing sound keys for individual players or players near a world location.
- [SignChangeTracker](sign-change-tracker.md) — detecting sign text and block-state changes, tracking removals, and reading or setting front/back sign text.
- [ChunkFutureProvider](chunk-future-provider.md) — event-driven chunk and entity lifecycle futures, state trackers, and readiness of a main chunk plus its required neighbours.
- [ForcedChunk](forced-chunks.md) — owned chunk tickets, asynchronous loading, completion callbacks, radius behavior, and cleanup.
- [Cloud Command Framework](cloud-command-framework.md) — BKCommonLib's `CloudSimpleHandler`, parsers, suggestions, injection, localization, and command integration.
- [LibraryComponent lifecycle](library-components.md) — managing component startup and reverse-order shutdown, conditional components, error handling, and implementation selection.
- [Entity Controllers](entity-controllers.md) — installing entity controllers and customizing lifecycle, movement, interaction, and collision behavior.
- [General Events](events.md) — BKCommonLib events for entity and chunk lifecycle, pre-spawn checks, sign edits, advancement progress, and bulk block changes.
- [Map Displays](map-displays.md) — creating map display items, lifecycle callbacks, input, z-layers, and the MapCanvas drawing API.
- [Map Markers](map-markers.md) — adding vanilla map markers to displays and moving them across multi-item-frame tiles.
- [Map Resource Packs](map-resource-pack.md) — loading vanilla, server, or custom packs and rendering block/item models and textures.
- [Map Widgets](map-widgets.md) — composing interactive map controls with focus and W/A/S/D, Space, and Shift navigation.
- [Packet Protocol](protocol.md) — registering packet listeners and monitors and choosing among BKCommonLib's packet types.
- [Entity metadata with DataWatcher](protocol-datawatcher.md) — typed metadata keys, reusable prototypes, and spawn/change packets.

## Examples

- [Configuration examples](examples/configuration.md) — recipes for defaults, nested sections, lists, node lists, headers, listeners, copying, and saving.
- [CommonTag examples](examples/commontag.md) — recipes for typed values, tag objects, nested compounds and lists, read-only access, item custom data, and binary serialization.
- [Maths examples](examples/math.md) — hierarchical object transforms, orientation interpolation, block/chunk coordinates, packed long keys, and BlockFace conversions.
- [Collections examples](examples/collections.md) — recipes for shared sets, safe snapshots, identity caches, pending updates, and spatial queries.
- [CommonUtil and LogicUtil examples](examples/common-logic-utilities.md) — recipes for tick-based expiration, deferred callbacks, coalesced tasks, and copy-on-write caches.
- [SignChangeTracker examples](examples/sign-change-tracker.md) — recipes for polling tracker updates, accessing a selected side, and storing per-side state.
- [ChunkFutureProvider examples](examples/chunk-future-provider.md) — recipes for waiting for a fully loaded neighbourhood, a single neighbour, or a custom chunk set.
- [Map model drawing](examples/map-model-drawing.md) — render 3D item and block models from a resource pack onto map textures with isometric transforms and lighting.
- [Map widget example](examples/map-widgets-simple.md) — create a custom focusable widget, draw focus-sensitive content, and give the player its map display item.
- [Map widget menus](examples/map-widgets-menu.md) — compose popup menus from nested windows and buttons with separate activation scopes.
- [Permission examples](examples/permissions.md) — declare permission constants with defaults and descriptions, then register them through the plugin lifecycle.

## Internals

- [YAML implementation](internals/yaml.md) — internal YAML tree, live views, change propagation, and serialization for BKCommonLib maintainers.
- [Templates and runtime resolution](internals/templates.md) — template source and generated handles, bootstrap ordering, Mojang/Spigot and hybrid-server remapping, inline generated logic, and the test-server fixture for BKCommonLib maintainers.
- [ForcedChunk implementation](internals/forced-chunks.md) — ticket reference counting, server ticket integration, async load and callback flow, timeouts, unload behavior, and leak cleanup for maintainers.
- [Entity Controller implementation](internals/entity-controllers.md) — NMS class hooks, entity-instance replacement, server-specific reference repair, and debugging replacement gaps.
- [CommonEntityType internals](internals/common-entity-type.md) — Bukkit-to-NMS entity mapping, wrapper selection, identifiers, name aliases, and hooked entity construction.
- [Map Display internals](internals/map-displays.md) — item-frame tiling, virtual map-ID packet rewriting, dirty clips, and viewer update flow for maintainers.
