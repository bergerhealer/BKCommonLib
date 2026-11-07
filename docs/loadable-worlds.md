# LoadableWorld

`LoadableWorld` describes a world or dimension that exists on disk, whether or not Bukkit currently has it loaded. It gives plugins a server-format-independent way to discover worlds, inspect their folders, and prepare a `WorldCreator` to load one. It handles differences between the traditional Spigot world-folder layout and Paper's newer dimension layout, as well as supported hybrid servers.

Use `LoadableWorld` when a plugin needs to list existing worlds or dimensions, locate world data files, or load a world that may currently be unloaded. It does not load a world just by listing or finding it.

## Listing worlds and dimensions

`LoadableWorld.listAll()` scans the server's world storage and returns loadable world entries. A Paper world may contain multiple dimensions, so results are not necessarily one entry per top-level world folder. Use `getDisplayName()` for presentation and `getNames()` for names accepted by `find()`:

```java
for (LoadableWorld loadableWorld : LoadableWorld.listAll()) {
    getLogger().info(loadableWorld.getDisplayName()
            + " [" + loadableWorld.getFormat() + "]"
            + (loadableWorld.isLoaded() ? " (loaded)" : " (unloaded)"));
}
```

In the newer dimension layout, multiple entries can share a short name. The display name is expanded to distinguish them, and `getNames()` provides the lookup names, which can include a parent-world/dimension form such as `world/overworld`. If a name is ambiguous, `find(name)` may return `null`; use an unambiguous name from `getNames()`.

## Finding and loading a world

`LoadableWorld.find(name)` looks up an existing loadable world or dimension. A non-null result means it can be addressed, not that it is already loaded. Check `getWorld()` or `isLoaded()` for loaded state. To load it, create a Bukkit world from the provided `WorldCreator`:

```java
LoadableWorld loadableWorld = LoadableWorld.find("world");
if (loadableWorld != null) {
    World world = loadableWorld.getWorld();
    if (world == null) {
        world = Bukkit.createWorld(loadableWorld.getWorldCreator());
    }
}
```

`LoadableWorld.of(world)` retrieves the corresponding loadable-world information for an already-loaded Bukkit `World`.

## World files and formats

Use the interface's location methods instead of constructing paths from assumptions about server folder structure:

| Method | Meaning |
|---|---|
| `getRootFolder()` | Root data folder for the world. |
| `getDimensionFolder()` | Folder containing this dimension's region data. |
| `getLevelFile()` | `level.dat` location; depending on format it may contain data for multiple dimensions. |
| `getRegionFolder()` | Region-file folder, or `null` when no region data exists. |
| `getFormat()` | Storage format used to locate and load the world. |
| `getWorldCreator()` | Configured Bukkit creator for loading this entry. |

`Format.SPIGOT` represents the traditional layout with a folder per world. `Format.SPIGOT_CONVERTED` describes a legacy world that Paper will migrate to its newer layout when loaded. `Format.PAPER` represents the newer layout where dimensions are stored under the main world's dimensions folder. Most plugin code should not need to branch on these values; the accessors and `WorldCreator` account for the differences.

These methods expose filesystem locations, but they do not make arbitrary file operations safe while a world is being modified or saved. Avoid directly editing world files; use Bukkit or BKCommonLib world APIs for gameplay changes.
