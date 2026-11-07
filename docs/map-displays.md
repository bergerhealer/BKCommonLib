# Map Displays

`MapDisplay` is BKCommonLib's virtual, callback-driven API for drawing a custom interface on Minecraft map items. A display can be shown in a player's hand or across a group of item frames; BKCommonLib manages its lifecycle, map tiles, viewer updates, and input. This guide covers the plugin-facing API. Resource-pack rendering and widget controls have their own guides, and the maintainer implementation is described in [Map Display internals](internals/map-displays.md).

## Key files

- [`MapDisplay`](../src/main/java/com/bergerkiller/bukkit/common/map/MapDisplay.java): display lifecycle, callbacks, viewers, item creation, layers, and drawing state.
- [`MapDisplayProperties`](../src/main/java/com/bergerkiller/bukkit/common/map/MapDisplayProperties.java): map item metadata and display construction.
- [`MapCanvas`](../src/main/java/com/bergerkiller/bukkit/common/map/MapCanvas.java): palette-based drawing, text, textures, models, and canvas views.
- [`MapTexture`](../src/main/java/com/bergerkiller/bukkit/common/map/MapTexture.java): reusable canvas textures, including images loaded from plugin JAR resources.
- [`MapDisplayEvents`](../src/main/java/com/bergerkiller/bukkit/common/map/MapDisplayEvents.java): callback contracts shared by displays and widgets.
- [`MapDisplayTile`](../src/main/java/com/bergerkiller/bukkit/common/map/MapDisplayTile.java): one 128-by-128 tile of a display.

## Creating a display and its item

Subclass `MapDisplay`, then create an item that records the plugin and display class in its metadata. The simple factory requires a public no-argument constructor:

```java
public class StatusDisplay extends MapDisplay {
    @Override
    public void onAttached() {
        getLayer().draw(MapFont.MINECRAFT, 4, 4, MapColorPalette.COLOR_WHITE, "Ready");
    }
}

ItemStack item = MapDisplay.createMapItem(StatusDisplay.class);
```

Use `MapDisplayProperties.createNew(...)` instead when the item needs a custom name, icon color, item type, or plugin-defined metadata. Its properties can be changed before calling `getMapItem()`. The metadata travels with the item and can be read in the display through its `properties` field. If constructor arguments are needed, use a no-argument constructor and pass that configuration through the item properties rather than expecting the API to call a parameterized constructor.

Give the resulting item to a player or put it in an item frame to display it. BKCommonLib initializes the associated class and manages map IDs; plugins should not assign map IDs or send map packets themselves. A single item-frame display is one 128-by-128 tile. Adjacent frames with the same display item can be combined automatically into a larger tiled canvas. `MapDisplayProperties.fillItemFrames()` can also fill an existing frame cluster, including empty neighboring frames, with the display item.

## Lifecycle and viewers

The display is virtual: its drawn pixels are not saved as a world map. BKCommonLib starts and stops it as its map session is needed.

| Callback or operation | When it is used |
|---|---|
| `onAttached()` | Called after the display is initialized and started. Set up state, layers, and widgets here. It can run again if the display is restarted or its tiled resolution changes. |
| `onTick()` | Called on the server thread during display updates. Update state and draw only when content changes. It runs when no one is viewing by default; `setUpdateWithoutViewers(false)` opts out of those ticks. |
| `onDetached()` | Called before a running display is stopped or reset. Release resources owned by the display here. |
| `onMapItemChanged()` | Called after the associated map item changes. |
| `onStatusChanged(MapStatusEvent)` | Receives status notifications sent with `sendStatusChange()`. Widgets receive the notification before the display. |

### Shared and per-player instances

`setGlobal(true)` is the default. BKCommonLib reuses the running global display for other players who view the same map, so they share one display instance, its state, and its rendered contents. Set `setGlobal(false)` in `onAttached()` when each player should receive a separate display instance with independent state and contents. A non-global display is not automatically shared with subsequent viewers. This setting is about instance and visual-state sharing; it is separate from input interception and from whether `onTick()` runs without viewers.

Owners, viewers, holders, and the controlling player are distinct states; use `getOwners()`, `getViewers()`, `isViewing()`, `isHolding()`, and `isControlling()` for the question your feature needs.

### Session lifetime

`setSessionMode(MapSessionMode)` controls when owners are removed from a display session and whether the session continues. The default, `ONLINE`, keeps each owner for as long as they remain online; if the last owner goes offline, the session ends and `onDetached()` runs. The other modes are:

| Mode | Owner/session lifetime |
|---|---|
| `VIEWING` | An owner is removed when they stop viewing the map. The session ends when it has no viewers. |
| `HOLDING` | An owner is removed when they stop holding the map. The session ends when nobody holds it. |
| `FOREVER` | The session remains even when all owners go offline; owners can be restored when they reconnect. |

Choose the mode according to whether the display should preserve its state while its owners are away, or be torn down as soon as it is no longer actively viewed or held. It controls automatic session cleanup; it does not replace `onDetached()` cleanup for resources the display owns.

`setRunning()` and `update()` are implementation-level controls, not plugin API. API callers must never call them; BKCommonLib owns the display lifecycle and update loop, starting and stopping displays as map usage changes.

## Input and interaction callbacks

Input is delivered only when it is intercepted for an owner. Call `setReceiveInputWhenHolding(true)` to intercept controls while an owner holds the map in their main hand, or use `setReceiveInput(player, true)` for a specific owner. Interception prevents normal player movement while enabled, so use it only for interfaces that need steering controls. `getInput(player)` exposes the current `MapPlayerInput` state.

`onKeyPressed()` and `onKeyReleased()` are edge callbacks; `onKey()` repeats while a key remains down. The logical directions map to A/D (left/right), W/S (up/down), Space (enter), and Shift (back). Map-frame clicks are delivered through `onLeftClick()` and `onRightClick()`. `onItemDrop()` can handle an inventory item dropped onto the display and return `true` to cancel the normal drop. `onBlockInteract()` receives a player's block interaction while they are holding the display; it may set the interaction result to indicate that the interface handled it. See [Map Widgets](map-widgets.md) for focus-based navigation and [Map Widget Menus](examples/map-widgets-menu.md) for popup menus.

## Drawing with layers

`getLayer()` returns layer 0. `getLayer(z)` retrieves or creates a layer at a signed z-index: larger z values are drawn in front of lower ones, and negative z values can be used for backgrounds beneath layer 0. `getBottomLayer()` and `getTopLayer()` find the extremes. An untouched pixel is transparent, so lower layers show through it. For example, draw a full-screen background on `getLayer(-1)` and buttons or text on `getLayer(0)` or above.

Each layer is a `MapCanvas`. Layer drawing updates the composed display and marks changed areas for synchronization. `clearLayers()` removes layers and drawn content (but not markers); `invalidate()` requests a full refresh. `getBuffer()` is read-only: callers must never write to its returned array. Writes bypass layer composition and dirty-area tracking, leaving display contents and client updates inconsistent. Use the canvas drawing methods to change pixels.

Vanilla map markers are managed separately from canvas layers and are automatically routed to the correct tile on multi-item-frame displays. See the [Map Markers guide](map-markers.md) for creating and positioning them.

### MapCanvas drawing reference

Map colors are palette indices stored as `byte`, not RGB pixels. Use `MapColorPalette` constants or its color conversion helpers when starting from RGB. A canvas uses top-left coordinates, and each map tile is 128-by-128 pixels.

| Operation | Methods |
|---|---|
| Read and write pixels or raw palette data | `readPixel()`, `writePixel()`, `readPixels()`, `writePixels()`, `writePixelsFill()`, `drawRawData()` |
| Draw or clear individual pixels and shapes | `drawPixel()`, `drawLine()`, `drawContour()`, `drawRectangle()`, `fillRectangle()`, `clearRectangle()`, `fill()`, `clear()` |
| Copy, transform, or move image data | `draw()`, `remap()`, `drawMove()`, `movePixels()` |
| Render text | `calcFontSize()`, `draw(MapFont, ...)`, `setAlignment()`, `setSpacing()` |
| Render textures, items, models, and quads | `draw(MapCanvas, ...)`, `drawItem()`, `fillItem()`, `drawModel()`, `drawQuad()` |
| Create drawing views | `getView()` creates a translated viewport; `getClip()` restricts drawing to a rectangular area. |

`draw()` uses the canvas blend mode and supports a color factor. To replace pixels directly, set the blend mode to `NONE`. `drawRawData()` can combine blend mode, a color factor, a brush mask, and depth testing. Masks are set with `setBrushMask()` or `setRelativeBrushMask()`.

Blend modes operate on Minecraft map-palette colors, not arbitrary RGB values. The modes that combine colors use BKCommonLib's precomputed palette mappings to select a map color for the result:

| Mode | Effect |
|---|---|
| `NONE` | Replaces the destination with the drawn color, including transparent pixels. Use it to overwrite or clear pixels. |
| `OVERLAY` | Draws only non-transparent source pixels; transparent source pixels leave the destination unchanged. This is the default. |
| `AVERAGE` | Averages source and destination colors, useful for color mixing or softening an image over existing content. |
| `ADD` | Adds source color values to destination colors, producing brighter effects such as highlights or glow-like overlays. |
| `SUBTRACT` | Subtracts source color values from destination colors, producing darker effects. |
| `MULTIPLY` | Multiplies source and destination colors, useful for tinting or shading. |

Select the mode with `setBlendMode()` before drawing. The mode applies to subsequent blend-aware drawing operations on that canvas, so restore the previous mode when different operations on the same canvas need different behavior.

Text uses `MapFont<K>`, which maps keys to character sprites. `MapFont.MINECRAFT` and `MapFont.TINY` are built in; custom fonts can map other keys, such as item types, to `MapTexture` sprites. `calcFontSize()` measures text before layout, while alignment and spacing configure subsequent draws.

For pseudo-3D content, `drawModel()` renders a `Model` with a scale/rotation or transform matrix, and `drawQuad()` projects a texture onto a quad. `setLightOptions()` affects model lighting. `setDrawDepth()` enables a per-pixel depth buffer; `clearDepthBuffer()`, `getDepth()`, and `hasMoreDepth()` support layered rendering where transparent pixels may reveal deeper content. `drawItem()` and `fillItem()` use a [`MapResourcePack`](map-resource-pack.md) to resolve the model and render an inventory-style icon. See [Rendering 3D Models on Map Canvases](examples/map-model-drawing.md) for an example drawing item and block models with lighting and a shared camera transform.

### MapTexture images and sprites

`MapTexture` is a mutable `MapCanvas` implementation primarily used to load and draw images, usually PNGs, as reusable sprites or backgrounds. `MapTexture.loadPluginResource(plugin, "images/status.png")` loads an image packaged in the plugin JAR using the path relative to the JAR's resource root. `MapTexture.loadResource(SomeClass.class, "/images/status.png")` loads through a class's classloader. Both decode common image formats and convert pixels to Minecraft's map color palette. The loaded texture can be drawn onto a display layer with `draw()`.

Inside a `MapDisplay`, `loadTexture("images/status.png")` is a convenience method that loads the image from the owning plugin's JAR. `MapTexture.fromImageFile()` and `fromStream()` cover disk files and caller-provided streams; `createEmpty()` and `fromRawData()` are available when the texture is generated rather than loaded from an image. Resource-loading failures are reported as `TextureLoadException`.

## Runtime requirements

Map rendering uses Java AWT and is not supported on a headless JVM. Map displays also rely on their resource assets being available; for model or item rendering, read the [Map Resource Packs guide](map-resource-pack.md) and load the pack during plugin startup to avoid a lazy download or initialization stall during gameplay.
