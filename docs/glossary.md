# Glossary

Defines terms used throughout the BKCommonLib documentation. See the [overview](overview.md) for how the library's modules fit together.

## Minecraft and server terms

- **Bukkit API**: The plugin-facing API exposed by Bukkit-derived servers, including Spigot and Paper. BKCommonLib commonly accepts Bukkit types at its public API boundary.
- **CraftBukkit**: The server implementation layer that connects Bukkit APIs to Minecraft server internals.
- **NMS**: Minecraft server implementation classes, conventionally referred to as net.minecraft server code. These internals and their names can change between Minecraft versions and server distributions.
- **Server implementation**: A particular Bukkit-derived or compatible server distribution, such as Spigot, Paper, or Purpur. BKCommonLib detects the running implementation and selects compatible behavior.
- **Minecraft version**: The version of the game or server internals in use. A player's client version may differ from the server version; see `PlayerGameInfo` in the [packet guide](protocol.md#client-game-version).
- **Block state / BlockData**: Minecraft's block state combines a material and properties such as facing or power. BKCommonLib calls this `BlockData`; Bukkit's `BlockState` is a block snapshot that can also expose block-entity data. See [BlockData](block-data.md).
- **Block entity**: Additional data associated with certain blocks, such as a chest inventory or sign text. It is distinct from the material and properties represented by BKCommonLib `BlockData`.

## BKCommonLib architecture

- **BKCommonLib**: A Bukkit server plugin and library that provides plugin-facing APIs, utilities, and compatibility layers over Minecraft server internals.
- **Mountiplex**: The reflection, template, conversion, and runtime class-generation library used by BKCommonLib to access version-dependent classes and members. It is included as a dependency of BKCommonLib.
- **Template**: A Mountiplex declaration describing version-dependent types or members. Templates can express alternatives for different server versions; generated handles provide the corresponding access API.
- **Handle**: A generated or reflection-backed API for interacting with an underlying Minecraft or server object without directly compiling against its implementation class.
- **Wrapper**: A BKCommonLib API object that presents a more stable or convenient interface around a Bukkit or Minecraft value, such as block data, item stacks, or entity data.
- **Capability**: A named indication that a particular server or BKCommonLib behavior is available. Use capability checks when behavior depends on support, rather than assuming a server version alone guarantees it.
- **Conversion**: A registered mapping between related representations, such as Bukkit and Minecraft types, used to translate values at API and internal boundaries.
- **Library component**: A lifecycle-managed BKCommonLib subsystem that can be enabled, initialized, or stopped as part of the library's component system.

## Common APIs and data

- **Packet**: A protocol message exchanged between server and client. `OUT_` packet types go to the client and `IN_` types come from it; see [Packet Protocol](protocol.md).
- **Packet listener / monitor**: A listener can inspect, modify, replace, or cancel packets; a monitor observes them without changing the outcome. See [Packet Protocol](protocol.md#listening-to-packets).
- **DataWatcher**: A typed collection of entity metadata used in spawn and metadata-update packets. A reusable prototype efficiently creates watchers with the same keys and initial values; see [DataWatcher](protocol-datawatcher.md).
- **Map display**: A virtual, plugin-defined map presentation that draws content and receives player input; see [Map Displays](map-displays.md).
- **Map canvas / layer**: A drawing surface and its depth-ordered content within a map display; see [Map Displays](map-displays.md).
- **Map widget**: A component in a map display's UI tree. Focus selects an input target; activation scopes navigation to a widget's descendants. See [Map Widgets](map-widgets.md).
- **Map marker / tile**: A marker is a vanilla map decoration. A tiled display spans multiple 128-by-128 maps; markers move between tiles automatically. See [Map Markers](map-markers.md).
- **Map resource pack**: An API for loading textures and block or item models for map rendering; see [Map Resource Packs](map-resource-pack.md).
- **Configuration node**: A hierarchical node in BKCommonLib's YAML-oriented configuration model. It stores values and child nodes and provides typed access and defaults; see the [Configuration API](configuration.md).
- **CommonTag**: BKCommonLib's cross-version wrapper for Minecraft NBT data. `CommonTagCompound` maps keys to tags and `CommonTagList` represents tag lists; see the [CommonTag API](commontag.md).
- **CommonItemStack**: A compatibility-oriented wrapper for inspecting and changing item properties and plugin-owned metadata; see [CommonItemStack](common-item-stack.md).
- **Human hand / hand role**: `HumanHand` identifies a physical left or right hand; `HumanHandRole` identifies the main or off hand, according to the player's setting. See [Human Hands](human-hand.md).
- **CraftRecipe**: A normalized crafting recipe with input requirements and outputs, usable against an inventory; see [Crafting Recipes](crafting-recipes.md).
- **Offline world/block**: BKCommonLib's UUID-backed `OfflineWorld` and coordinate-backed `OfflineBlock`, which represent a world or block without requiring a currently loaded Bukkit `World` or `Block`; see [Offline Worlds and Blocks](offline-worlds.md).
- **Loadable world**: An on-disk world or dimension, loaded or unloaded, whose paths and loading configuration are exposed through `LoadableWorld`; see [LoadableWorld](loadable-worlds.md).
- **Sign change tracker**: A `SignChangeTracker` monitors a sign's text, block state, and backing entity, with line access through `SignSide.FRONT` or `SignSide.BACK`; see [SignChangeTracker](sign-change-tracker.md).
- **Chunk future provider**: An event-driven API for waiting for chunk or chunk-entity lifecycle events and tracking whether a main chunk's required neighbours are loaded; see [ChunkFutureProvider](chunk-future-provider.md).
- **Forced chunk**: An owned ticket used to keep a chunk loaded and obtain it asynchronously. Each owner must release its ticket; see [ForcedChunk](forced-chunks.md).
- **Common entity**: A BKCommonLib API around a Bukkit entity that provides additional operations and compatibility-aware access to entity behavior.
- **Entity controller**: An installed controller that customizes supported entity lifecycle, movement, interaction, or collision behavior; see [Entity Controllers](entity-controllers.md).
- **Resource key / sound effect**: `ResourceKey<SoundEffect>` identifies a named sound event for playback to a player or at a world location; see [Sound Effects](sound-effects.md).
- **Permission constant**: A declared permission node with a default and description, registered through `PluginBase`; see [Permission Examples](examples/permissions.md).
- **Cloud command integration**: `CloudSimpleHandler` sets up BKCommonLib's integration with the Cloud command framework; see [Cloud Command Framework](cloud-command-framework.md).
- **Specialized collection**: A collection designed for a particular access pattern, such as copy-on-write snapshots, shared immutable sets, or spatial lookup; see [Specialized Collections](collections.md).
- **3D math**: Vector, quaternion, matrix, and geometry APIs used to represent positions, directions, rotations, transforms, and bounds.
- **Tick task**: Work scheduled through the server tick lifecycle. `Task` provides plugin-owned delayed or repeating work; `RunOnceTask` coalesces repeated requests. See [Task](tasks.md) and [CommonUtil and LogicUtil](common-logic-utilities.md).
