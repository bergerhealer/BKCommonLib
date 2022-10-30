![Logo](https://github.com/bergerhealer/BKCommonLib/blob/master/misc/BKCommonLib_logo.png?raw=true)

<!-- rewrite once spigot "[⤓ Modrinth](https://modrinth.com/plugin/bkcommonlib) / [⤓ Spigot](https://www.spigotmc.org/resources/bkcommonlib.39590/) / " "" -->
〚 [⤓ Modrinth](https://modrinth.com/plugin/bkcommonlib) / [⤓ Spigot](https://www.spigotmc.org/resources/bkcommonlib.39590/) / [⤓ Jenkins  Dev Builds](https://ci.mg-dev.eu/job/BKCommonLib/) / [Source on GitHub](https://github.com/bergerhealer/BKCommonLib) / [Javadocs](https://ci.mg-dev.eu/javadocs/BKCommonLib/) / [Discord](https://discord.gg/wvU2rFgSnw) 〛

## About BKCommonLib
**BKCommonLib** is a Paper/Spigot/Bukkit server plugin and library which houses a large selection of utilities, API's, frameworks and performance-critical code. It was made to eliminate all use of Minecraft Server Code (aka NMS) from plugins themselves, improving their stability.

It made it so that when Minecraft updates, only this library has to be updated. Plugins using it generally don't require updates anymore, except to stay compatible with BKCommonLib itself.

### Compatible Servers
  - **Spigot**, PaperMC, Pufferfish, Purpur, Tuinity, TacoSpigot
  - Most other forks of the above should work fine
  - **Forge Paper hybrids**, with limitations:
    - Mohist 1.12.2 and 1.16.5
    - Magma 1.12.2
    - Arclight 1.12.2, 1.15.2, 1.16.5 and 1.18.2
    - CatServer 1.12.2

### Backwards-Compatible
The latest version of BKCommonLib is compatible with **all Minecraft versions 1.8 and above**. This means version 1.19 of BKCommonLib should work on Spigot 1.12.2, Paper 1.16.5, Pufferfish 1.18.2 and so on.

**Do not install an older version of BKCommonLib for older versions of Minecraft.**

## Support
If Minecraft just updated or Spottedleaf made some ground-breaking optimizations in Paper, you might find that BKCommonLib stops working. Bergerkiller is probably on the case, and there might already be development builds available that fix your problem over at our build server:

**Development Builds:** [https://ci.mg-dev.eu/job/BKCommonLib/](https://ci.mg-dev.eu/job/BKCommonLib/)

Report any problems to us on the [GitHub Issue Tracker](https://github.com/bergerhealer/BKCommonLib/issues) or join our [Discord Server](https://discord.gg/wvU2rFgSnw) for assistance. Issue reports for older server versions are welcome too.

Tutorials and example projects are lacking a bit, but you can ask for API help on the discord. We'll help.

## Features
<details>
<summary>Includes Mountiplex General Purpose Java Reflection Library</summary>

#####
[**Mountiplex**](https://github.com/bergerhealer/Mountiplex) is core to BKCommonLib's ability to support so many different Minecraft Server versions and even forge at the same time. It combines the strengths of [ASM](https://github.com/llbit/ow2-asm), [Javassist](https://github.com/jboss-javassist/javassist) and [Objenesis](http://objenesis.org/) with a _Template Engine_ to generate compatible code at runtime. To achieve this it supports template declarations for macros, reflection and remapping and molds this into a compiletime-generated interface.

This means you don't have to compile different classes for every different permutation of paper/spigot/forge/version and the millions of forks people create. If someone changes something, add an _#if - #endif_ block and you're set!

[Here is an example template for various packets to demonstrate the power of this approach](https://github.com/bergerhealer/BKCommonLib/blob/master/src/main/templates/com/bergerkiller/templates/net/minecraft/network/protocol_packets_other.txt)

  - At-runtime class generation with Handles, reflection, template engine
  - Dynamic type/name remapping to support forge/multi-version/Mojang Mappings
  - Parse version strings, compare them. Also inside templates.
  - Detect existence of types, methods and fields and generate an appropriate compatible implementation
  - Access private members with _#require_ and call them with _#name_ anywhere in the code
  - Maven Mojo tasks to generate the interfaces or convert block comments into strings (jdk8 multiline string back-support)
  - Type Conversion
    - Automatically convert one value type to another using a type <> type registry
    - Example: Use Bukkit Entity in API, convert to net.minecraft Entity for use in generated code
    - Compatible with NBT / YAML to store any custom type and convert automatically
    - Declare conversions inline in your template:
      ```java
      // Fields
      public final (IntVector3) BlockPosition position;

      // Instance methods
      public (List<org.bukkit.entity.Entity>) List<Entity> findEntities() {
          // Code
      }
      ```
******
</details>
<details>
<summary>Includes Cloud Command Framework</summary>

#####
BKCommonLib includes the [**Cloud Command Framework**](https://github.com/bergerhealer/cloud). Write clean commands with annotations or builder pattern, complete with suggestions, permission handling and localization.
BKCommonLib adds a few default utilities to get set up for Paper/Spigot servers even faster.
******
</details>
<details>
<summary>PluginBase Framework</summary>

#####
**PluginBase** is a base class plugins can implement instead of _JavaPlugin_ that provides access to a lot of convenient features:
  - Native support for this [Plugin Preloader](https://github.com/bergerhealer/PluginPreloader)
  - Callbacks for when other plugins enable, to handle dynamic loading/unloading of soft dependency logic
  - Read the plugin.yml to store custom metadata
  - Validates that all dependencies of the plugin are actually enabled
  - Simple helper methods to register (packet) listeners
  - Plugin.yml _classdepend_ feature: load classes from other plugins without requiring that plugin loads before yours
  - Default command handlers to read the plugin version and build number
  - Permission API
    - Allows people to set up a plugin without requiring a permission manager, with simple OP rules
    - Very basic enum/static based Permission API to store your permission constants
    - Adds support for * wildcard, regardless of what permission manager (or none) is used
    - Makes the default (op/not_op/false/true) user-configurable using PermissionDefaults.yml
    - Throw/handle an exception when a player lacks permission
  - Localization API
    - Very basic enum/static based Localization API
    - Automatically generates a Localization.yml that users can customize
    - Supports placeholders using %0% %1% etc.
******
</details>
<details>
<summary>Server Events</summary>

#####
Defaults to the Paper implementation if a Paper server is used. Makes the event available on non-Paper servers as well.
  - Chunk Load/Unload Entities event
  - CreaturePreSpawnEvent
  - Entity Add/Remove(FromWorld)/RemoveFromServer events
  - MultiBlockChangeEvent (WorldEdit integration)
******
</details>
<details>
<summary>YAML Configuration</summary>

#####
Bukkit's Configuration API is dreadful. It's slow, defaults require shading in resources and working with nodes and lists is cumbersome. BKCommonLib's YAML library changes all of that:
  - Uses SnakeYAML only for data<>text serialization
  - Efficient memory storage model
  - Get with a default value acts like python's **setdefault**. Easy default configurations!
  - Supports comment headers for every key, including a global header for the file itself
  - Disables SnakeYAML's document size limitations by default
  - Automatically converts legacy Minecraft chat style characters to & and back
  - Completely disables anchors and has some format parsing relaxations
  - Cleanly get and set enums, stored as user-readable strings
  - Efficient node lists, node cloning, iteration
  - Multi-version support for ItemStack de-serialization
  - Register change listeners: callback is called when a node or nested node is modified
  - Fast Auto-Save functionality
    - Minimizes serialization overhead by caching past text representations
    - Asynchronous writing to file when saving
    - Global lock on the file blocks a future load() if a save() is still pending
    - Suitable for large file storage / NoSQL database-like access

##### Example
```java
FileConfiguration config = new FileConfiguration(myPlugin, "file.yml");
config.load();

config.setHeader("This is the header at the top of the file");
config.addHeader("This adds a new line");

config.setHeader("coolName", "\nSets the cool name. Empty whitespace above.");
config.addHeader("coolName", "Yup, this is on a new line too");
String coolName = config.get("coolName", "DefaultCoolName");

config.setHeader("stuff", "\nThis is some stuff");
ConfigurationNode stuff = config.getNode("stuff");
boolean stuffEnabled = stuff.get("enabled", false);
int stuffCount = stuff.get("count", 0);

// Clone the stuff settings, modify, show yaml
ConfigurationNode stuffCopy = stuff.clone();
stuffCopy.set("count", 20);
System.out.println(stuffCopy.toString());

config.save(); // Non-blocking!
```
******
</details>
<details>
<summary>NBT - CommonTag API</summary>

#####
Comes with an interface to the server's internal **NBT Tag** library. Used extensively when interfacing with Minecraft Server API's.
  - Simple user-friendly wrapper for NBT
  - Operates on the server's actual internal NBT library, so no copying when interacting with the server
  - Serialize/Deserialize from/to (compressed) byte data - read server .dat files
  - Read and Modify NBT of items
  - Read and Modify Player Profiles, level.dat or esoteric things like Mob Potion Effects
******
</details>
<details>
<summary>ForcedChunk - Chunk load tickets / Chunk Loader</summary>

#####
Makes it easy to load a chunk without stalling the main thread, and **keep it loaded**. Important when chunks must stay loaded to tick entities inside or to load chunks to process block data inside. Very easy to use.
  - Asynchronous chunk loading
  - Keep a chunk area loaded
  - Supports multiple chunk load tickets for the same chunk
  - Track load tickets with objects, RAII AutoCloseable
  - Tickets can be created/closed from other threads
  - Radius can be specified. Radius of 2+ will allow for entities to be ticked.

##### Example
```java
final ForcedChunk chunk = ForcedChunk.load(world, chunk_x, chunk_z);
chunk.getChunkAsync().thenAccept(chunk -> {
    // Work with the chunk
    for (BlockState state : chunk.getBlockStates()) {
        System.out.println(state);
    }

    // Release the chunk ticket. Could keep it around and the chunk stays loaded.
    chunk.close();
});
```
******
</details>
<details>
<summary>Chunk Future Provider</summary>

#####
Makes the **Chunk Load and Unload (Entities) Bukkit Events** available through a **Java CompletableFuture API**. Execute logic when a particular chunk is loaded or unloaded without writing your own EventHandler processing queues. Clean up work when a chunk that just loaded, unloads again. Or perform work in a Chunk once all neighbouring chunks are loaded, too. Aims to prevent synchronous loading of chunks, which negatively impact server performance.

Futures are automatically cancelled when the **premise** for them is ended. For example, a future waiting for all the neighbours of a chunk to be loaded is cancelled if the chunk at the center of it is unloaded.

**Suggested use cases**: discovering multi-block structures, reading redstone state of signs, spawn custom entities or start world events

##### Example
```java
private ChunkFutureProvider provider; // = ChunkFutureProvider.of(myPlugin);

@EventHandler
public void onChunkLoad(ChunkLoadEvent event) {
    // When the chunk and all its 8 neighbours are loaded, do work in the chunk
    // If the input chunk unloads, this future is cancelled.
    provider.whenAllNeighboursLoaded(event.getChunk(),
                    ChunkNeighbourList.neighboursOf(event.getChunk(), 1))
            .thenAccept(this::doWorkInChunk);
}

public void doWorkInChunk(Chunk chunk) {
    // Check block states, possibly entering inside neighbouring chunks
    // We know neighbours are loaded too, so no sync chunk loading! Yay!
}
```
******
</details>
<details>
<summary>Block Utilities</summary>

#### Offline Block/World
  - Store blocks/worlds in your plugin without risking memory leaks
  - Track worlds by UUIDs and access the loaded World without HashMap lookups
  - Track offline blocks, efficiently convert them to loaded Bukkit Blocks
  - OfflineWorld is compatible with identity hashmaps
  - OfflineBlock can be safely used as a key in hashmaps
#### SignChangeTracker
  - Routinely call update() and you know whether any change to the sign occurred
  - Knows whether the sign changed without an expensive lookup of the block entity / block data
  - Know whether the sign was removed/unloaded
  - Will detect changes from (sign edit) plugins and the /data command
  - Powerful server-wide sign tracking if combined with the chunk future provider
#### BlockData
  - Replaces Bukkit's BlockData API for cross-version support
  - Fast getting/setting of BlockData in a World
  - Access block properties like opacity, emission, opaque faces, render options (map displays), Bukkit types
  - Provides access to the legacy Material Bukkit API. Adds support for new block types. (Legacy support)
******
</details>
<details>
<summary>Interactive Minecraft Maps with Map Displays</summary>

#### [**Map Display API**](https://wiki.traincarts.net/p/Map_Display)
<img src="https://wiki.traincarts.net/images/thumb/9/9e/Mapdisplay_menu.png/800px-Mapdisplay_menu.png" width="30%"/>
&nbsp;
<img src="https://wiki.traincarts.net/images/thumb/f/fb/Mapdisplay_maplands.png/800px-Mapdisplay_maplands.png" width="30%"/>
&nbsp;
<img src="https://wiki.traincarts.net/images/thumb/f/f9/Attachment_editor_wooden_car.png/800px-Attachment_editor_wooden_car.png" width="30%"/>

#### Bukkit's Map API is awful. BKCommonLib does it better.
  - Draw 2D/3D contents onto Minecraft maps or texture buffers
  - Every map display comes with its own event loop / event callbacks like onTick()
  - Automatically loaded up using Item Metadata. Create an item and you're done!
  - Automatically assigns Map Ids for you
  - Supports ItemFrame tiling natively - large image displays
  - Scientifically-backed RGB -> Map Color conversion
  - Supports rendering of Minecraft assets (blocks, items) and Resource packs
  - Color blending, depth buffer, 256 canvas layers
  - Left/right-click handling of item frames with clicked pixel coordinates provided
  - Widgets
    - Uses vehicle steering controls to control (W/A/S/D/Space/Sneak)
    - Built-in menu navigation / focus / activation logic
    - Automatically tracks clip areas that need redrawing for you
    - Comes with buttons, text, tab view and more built-in widgets and menus
******
</details>
<details>
<summary>Entity Controllers</summary>

#####
By extending server Entity classes at runtime, this API makes it possible to completely alter the behavior of entities on the server. This is core to how [TrainCarts](https://github.com/bergerhealer/TrainCarts) operates.
  - The entity _onTick()_ can be overrided to run your own routines
  - Run entity _onMove()_ or omit it, handle block and entity collisions
  - Hotswap existing entities at runtime with custom behavior while keeping data consistent
  - Override entity network synchronization (packets)
******
</details>
<details>
<summary>Protocol API</summary>

#####
Provides a fairly complete Multi-Version compatible **Packet API**, particularly geared towards creating *fake entities*.
  - If installed, uses ProtocolLib automatically
  - Packet Listeners and Monitors. Modify packets, cancel packets.
  - Send packets, with the option to bypass listeners
  - <code>EntityUtil.getUniqueEntityId()</code> to spawn fake entities
  - Lots of packets have a full API to modify fields inside, cross-version compatible
    - Entity Movement packets include protocol conversion of the x/y/z/yaw/pitch
    - DataWatcher: Entity metadata packet can be fully inspected/modified/created
  - Spawn armorstands, control pose and appearance
  - _VehicleMountController_ to put one entity as passenger of another, or spectate. Handles out-of-order spawn/destroy packets.
******
</details>
<details>
<summary>Java Utilities</summary>

#### Collections
  - Access to FastUtil's Int/LongHashMap collections
  - Implicitly shared collections (copy on concurrent write)
  - Immutable cached collections (safely work with millions of sets of players)
  - Octree / DoubleOctree - Map data to 3D space, query cuboids efficiently
  - FastTrackedUpdateSet
    - Efficiently request or cancel an update for a recipient
    - Efficiently iterate those that need to update (from a task, for example)
  - SortedIdentityCache
    - Map one collection type to another and cache the mapped results
    - Track when elements are added or removed from a third-party collection
    - Heavily optimized for iteration / element order when synchronizing
  - BlockFaceSet - Store BlockFace values, in a set
#### Math
  - 3D Transformation - Quaternion and Matrix4x4 with yaw/pitch/roll rotation conversions
  - 3D Rotatable Bounding Box with hit-testing
  - IntVector2 (chunk coordinates) and IntVector3 (block coordinates)
  - BlockFace logic/rotation/maths
  - Vector maths
  - Fast Trigonometric functions
#### LogicUtil
  - Functional interfaces for consumers/suppliers which can throw
  - Helper methods for Java 8 Stream API
  - Helper methods for working with CompletableFutures
  - Cloning collections, cloning with type resolved at runtime
  - synchronizeCopyOnWrite - updating an immutable mapping / performance caches
#### General I/O
  - Hastebin uploader/downloader client
  - ByteArrayIOStream - Byte Array <> Stream API
  - Bit Packet/IO-streams - encode/decode a bitstream
  - AsyncTextWriter - used by yaml, asynchronous encoding of text and writing to file, with future
#### LibraryComponent
  - Track logic of your plugin and enable/disable them in the correct order
  - Enable different components based on conditions, such as server version
  - Error handling
******
</details>
<details>
<summary>Miscellaneous</summary>

#####
There are many more features hidden inside utility classes. Look around.
  - Efficiently iterate world/chunk (block) entities without creating garbage arrays or list copies
  - ChatText: Simple Chat Component API
  - HumanHand: Support off-hand and held items in a cross-version compatible way
  - Inventory utilities
  - Parsing of input text into numbers, materials and more
  - Deregister event listeners
  - Read the current server tick value
  - Main thread Task delegation and synchronization
  - For developers: DebugUtil to modify behavior/parameters at runtime
  - Check the game version of a connected player (ViaVersion / ProtocolSupport)
  - SignEditDialog
    - Show a popup to a Player to input the 4 lines of text of a sign
    - Specify the initial text on the sign
    - No actual sign block required
******
</details>

## Dependency Information
Want to use BKCommonLib in your own projects? Use the following repository and dependency information. Remember to update the version as necessary.

Want to use the included [Cloud Command Framework](https://github.com/bergerhealer/cloud)? Follow [these instructions](https://github.com/bergerhealer/BKCommonLib/blob/master/CLOUD_HOWTO.md) to properly relocate your imports.

<details open>
<summary>Maven</summary>

```xml
<repositories>
    <repository>
        <id>MG-Dev Jenkins CI Maven Repository</id>
        <url>https://ci.mg-dev.eu/plugin/repository/everything</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.bergerkiller.bukkit</groupId>
        <artifactId>BKCommonLib</artifactId>
        <version>1.19.2-v2</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```
</details>
<details>
<summary>Gradle (Experimental)</summary>

```groovy
repositories {
    maven {
        url = 'https://ci.mg-dev.eu/plugin/repository/everything'
        content {
            includeGroup 'com.bergerkiller.bukkit'
            includeGroup 'org.bergerhealer.cloud.commandframework'
            includeGroup 'com.bergerkiller.mountiplex' 
        }
    }
}

dependencies {
    compileOnly 'com.bergerkiller.bukkit:BKCommonLib:1.19.2-v2'
}
```
</details>

#### Building BKCommonLib
To build BKCommonLib yourself you will need to run [Build Tools](https://www.spigotmc.org/wiki/buildtools/) beforehand. Otherwise tests will fail and maven will complain. No actual server code is linked during compiling, hence the dependency is type test.

You do not need to build Spigot to use BKCommonLib as a dependency, but you might need to if you run **unit tests**.

## License
Bergerkiller, the project owner, is generally fine with people copy-pasting code from the library and using it in their own plugins. Please do not copy complicated frameworks like the Map Display API, you will fail and the end-result will probably end up causing incompatiblities with BKCommonLib itself.

If you're unsure, ask in our Discord server.

BKCommonLib shades in/uses the following libraries, further License conditions may apply:
- [Cloud Command Framework](https://github.com/incendo/cloud) (custom fork)
- [ASM](https://github.com/llbit/ow2-asm)
- [Javassist](https://github.com/jboss-javassist/javassist)
- [Objenesis](http://objenesis.org/)
- [SnakeYAML](https://github.com/snakeyaml/snakeyaml)
- [Google GSON](https://github.com/google/gson)

<details>
<summary>MIT License, Boring stuff here just so I don't get sued</summary>

<pre>MIT License

Copyright (C) 2013-2015 bergerkiller Copyright (C) 2016-2022 Berger Healer

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, and/or sublicense the Software,
and to permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.</pre>
</details>

## Donate
If you really like my work and want to give something in return, feel free to donate something small to me over PayPal using the button down below. Thank you! :)

[![Hello](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.me/teambergerhealer)
