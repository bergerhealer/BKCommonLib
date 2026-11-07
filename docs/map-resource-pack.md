# Map Resource Packs

`MapResourcePack` reads Minecraft client resources and compatible resource-pack archives so plugins can inspect textures and models or render block and item artwork onto maps. It supports the built-in vanilla resources, the server's configured pack, and custom packs layered over a base pack.

## Key files

- [`MapResourcePack`](../src/main/java/com/bergerkiller/bukkit/common/map/MapResourcePack.java): pack creation, resource loading, model lookup, item rendering, and archive searches.
- [`VanillaResourcePack`](../src/main/java/com/bergerkiller/bukkit/common/map/util/VanillaResourcePack.java): vanilla client resources exposed as `MapResourcePack.VANILLA`.
- [`MapCanvas`](../src/main/java/com/bergerkiller/bukkit/common/map/MapCanvas.java): rendering entry points such as `drawItem()` and `drawModel()`.
- [`Model`](../src/main/java/com/bergerkiller/bukkit/common/map/util/Model.java) and [`ItemModel`](../src/main/java/com/bergerkiller/bukkit/common/map/util/ItemModel.java): decoded block/item models and item model selection data.

## Select and load a pack

`MapResourcePack.VANILLA` contains the default Minecraft client resources. `MapResourcePack.SERVER` uses the resources configured by the server, including its `server.properties` resource pack when one is set. Create a custom pack with `MapResourcePack.builder()`, optionally choosing a base pack, archive path or URL, resource-pack hash, and preferred pack format, then call `build()`.

Call `load()` on the pack during plugin startup, before gameplay code first asks it for a texture or model. Loading can install the matching official Minecraft client jar if the resources are not already available, and URL-based packs may need to be fetched. Lazy loading still works, but it can stall the server on the first resource lookup; BKCommonLib logs a warning when a resource pack is first loaded lazily. Calling `load()` also initializes its parent packs.

```java
private final MapResourcePack resources = MapResourcePack.VANILLA;

@Override
public void onEnable() {
    resources.load();
}
```

Client assets are Mojang-owned and are installed separately; BKCommonLib does not distribute Minecraft client assets. Users must comply with Minecraft's EULA.

## Models, textures, and item icons

Use `getTexture(path)` to load a texture by pack path. Namespaced paths can address resources outside the default `minecraft` namespace. `getModel(path)` reads a model, and returns a placeholder model if it cannot be loaded. `getModelInfo(path)` reads model metadata without preparing render geometry. `getBlockModel(...)` accepts a Bukkit block, world coordinates, material, `BlockData`, or `BlockRenderOptions`; use render options when the exact block state matters.

`getItemModel(item)` resolves the model selected for a Bukkit `ItemStack` or `CommonItemStack`. `getItemModelConfig(item)` exposes the item model configuration, and `getItemTexture(item, width, height)` renders an inventory-slot-style icon. These APIs resolve the models and textures in the selected pack; a custom pack overlays its base pack and can override its resources.

## Listing item model overrides

Use the override-listing API to discover the custom item models configured by a resource pack and the unique item variants that select them:

1. Call `listOverriddenItemModelNames()` to get the item model names overridden in the pack and its overlays. Names omit the file extension; non-`minecraft` namespaces are included as `namespace:item_name`.
2. For each name, call `getItemModelConfig(name)`.
3. Call `listAllOverrides()` on the resulting `ItemModel` to flatten its model-selection tree into `ItemModelOverride` entries.
4. For each entry, `getItemStack()` may provide a `CommonItemStack` representing an item configured to match the override. `getOverrideModels()` returns the model names (`MinecraftModel.model`) selected by that entry. `isMatching(item)` tests a candidate item against the entry's predicate, and `isMatchingAlways()` identifies entries that do not require a predicate.

This works with both newer item-model definitions and older predicate overrides; BKCommonLib normalizes them into the same listing API. The item stack is optional because some predicates or configurations cannot be represented by modifying a base item. Also, a flattened list is a practical listing rather than a mathematically exact enumeration: identical predicates in separate branches may produce duplicates. Consumers that build a catalog of unique custom items should check the optional stack, inspect its selected model names, and deduplicate according to their own item/model identity rules.

To draw the results, use `MapCanvas.drawItem(pack, item, x, y, width, height)` or `fillItem(pack, item)` for an inventory-style icon, and `drawModel(pack.getBlockModel(...), ...)` for a 3D model. For a complete example rendering 3D item and block models with lighting, see [Rendering 3D Models on Map Canvases](examples/map-model-drawing.md). See the [Map Displays guide](map-displays.md#mapcanvas-drawing-reference) for canvas drawing, depth, and lighting operations.

## Resource discovery

`listResources()` and `listDirectories()` search by `ResourceType`, optional namespace, and folder. Their overloads or `SearchOptions` control whether parent packs are included and whether the search is deep. Returned resource names omit their file extension and can be passed to methods such as `getTexture()` or `getConfig()`. `listNamespaces()` enumerates the namespaces available in the pack hierarchy.

`getConfig(path)` reads a YAML configuration resource and caches the result. Clone the returned `ConfigurationNode` before modifying it, so edits do not mutate the cached resource. `openResource(type, path)` provides a stream-oriented route for resource types when decoded textures, models, or config nodes are not needed.

`clearCache()` clears model and texture caches after resources have changed; it does not reload or replace the pack archive itself.
