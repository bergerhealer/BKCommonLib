# Map Display Internals

**Audience:** BKCommonLib maintainers.

Map displays virtualize Minecraft map IDs and map update packets. Item-frame displays are tracked as connected frame clusters, with each frame mapped to a tile of a display UUID. During outgoing packet handling, BKCommonLib rewrites map item IDs to per-display tile IDs, then forwards dirty canvas regions as map update packets to the relevant viewers. Plugin-facing lifecycle, canvas, and drawing behavior is documented in the [Map Displays guide](../map-displays.md).

## Key files

- [`CommonMapController`](../../src/main/java/com/bergerkiller/bukkit/common/internal/map/CommonMapController.java): owns frame/map registries, map-ID allocation, packet interception, and cluster discovery.
- [`ItemFrameInfo`](../../src/main/java/com/bergerkiller/bukkit/common/map/binding/ItemFrameInfo.java): tracks one frame's map item, tile coordinates, and network viewers.
- [`MapDisplayInfo`](../../src/main/java/com/bergerkiller/bukkit/common/map/binding/MapDisplayInfo.java): groups frames and sessions by display UUID and tracks resolution and frame viewers.
- [`MapSession`](../../src/main/java/com/bergerkiller/bukkit/common/map/MapSession.java): tracks owners, viewing/holding state, per-owner dirty clips, and map update delivery.
- [`MapDisplayTile`](../../src/main/java/com/bergerkiller/bukkit/common/map/MapDisplayTile.java): extracts a tile update from the composed display buffer and builds its map packet.
- [`MapClip`](../../src/main/java/com/bergerkiller/bukkit/common/map/MapClip.java): tracks dirty rectangles for the display and individual owners.
- [`CommonMapUUIDStore`](../../src/main/java/com/bergerkiller/bukkit/common/internal/map/CommonMapUUIDStore.java): reads display UUID metadata and manipulates the underlying vanilla map ID.

## Tracking and combining item frames

`CommonMapController` observes item-frame add/remove and chunk/world load events. It maintains frame metadata and checks item changes before binding a frame to a `MapDisplayInfo`. All frames whose map items share a dynamic display UUID refer to the same display info; each frame's `MapUUID` additionally stores its tile X/Y coordinate.

For tiled maps, `findCluster()` groups connected frames in one world that face the same direction, occupy the same plane, and carry the same dynamic map UUID. The connected component must be adjacent; disconnected islands are not combined. The cluster orientation and frame positions determine each frame's tile coordinate, with the top-left display position serving as the grid origin according to the display's facing/rotation. A frame's item is not enough by itself to define a tiled display: matching display UUID and compatible frame geometry are required. Empty adjacent frames can be included when `MapDisplayProperties.fillItemFrames()` is used to populate a cluster.

If neighboring chunks needed to resolve a cluster are not loaded, `ItemFrameInfo` parks the recalculation until the chunk dependencies are available. `MapDisplayInfo.refreshResolution()` derives the display's pixel dimensions from loaded tile coordinates; each tile contributes 128 pixels in each direction. Tile 0,0 is always retained because it also represents the held map. Frame viewer tracking is synchronized with the server entity tracker and contributes to the display's viewer set.

## Map ID virtualization and packet rewriting

Map display items store a stable UUID in their custom data rather than relying on a globally persistent vanilla map ID. `CommonMapController` maintains a bidirectional mapping from `(display UUID, tile X, tile Y)` to an available integer map ID. A separate generated ID is used for each tile; static vanilla map IDs are preserved and reserved so dynamic displays do not collide with them.

When a map item is sent in an outgoing inventory contents or slot packet (`OUT_WINDOW_ITEMS` / `OUT_WINDOW_SET_SLOT`), `handleItemSync()` assigns the dynamic ID for tile 0,0. For item-frame entity metadata (`OUT_ENTITY_METADATA`), the controller uses that frame's tracked tile coordinate and rewrites the map ID in the item metadata. It removes server-only display metadata before sending a virtualized inventory item. The client therefore sees ordinary map items with the IDs needed for each displayed tile, while the persistent UUID remains authoritative on the server.

Outgoing map data packets (`OUT_MAP`) are observed to reserve map IDs used by static maps. Dynamic display updates are built separately by `MapDisplayTile` with the allocated tile ID. Creative-slot input has a matching restoration path because a client's item stack contains its visible map ID but not all server-side display metadata; the controller merges the original item data back instead of accepting the virtual ID as a persistent map ID.

## Dirty clips and viewer updates

Each display layer writes into its own palette buffer. When a write affects the visible composite pixel, `MapDisplay.Layer` updates the live buffer and marks the display clip dirty. Transparency changes can expose a lower layer, so composition and dirty tracking must remain coupled; direct writes through a layer's raw `getBuffer()` bypass this logic.

The display clip describes changed pixels for the current update. Each `MapSession.Owner` also keeps a separate clip for pixels that player has not yet received. During `MapDisplay.flushCanvasChanges()`, the changed area is combined with each owner's pending clip and converted into tile-local regions. `MapDisplayTile` copies only the relevant pixel rectangle from the live buffer into the tile's 128-by-128 map update packet. Out-of-view owners accumulate dirty state instead of receiving updates; when they view the map again, their pending clip is sent. New viewers and viewers whose client state needs refreshing receive a full tile update.

When several viewers have matching state, `MapSession.Owner.updateMap()` can reuse the same tile packet updates; per-viewer packet clones are made where marker synchronization requires independent state. Map markers are synchronized separately after texture changes. `MapDisplay.invalidate()` marks the full display dirty, while `flushCanvasChanges()` can be called to send separated partial updates before the normal end-of-tick flush.

## Change entry points

- To change which server events or packets maintain map/frame state, inspect `CommonMapController` registration, packet listener methods, and entity/chunk event handlers.
- To change frame-cluster membership or tile coordinate calculation, inspect `CommonMapController.findCluster()` and `ItemFrameInfo.recalculateUUIDInCluster()`.
- To change map UUID or dynamic ID allocation, inspect `CommonMapUUIDStore` and the map-ID methods in `CommonMapController`; preserve static ID reservations and the creative-slot restoration path.
- To change display sizing or tile membership, inspect `MapDisplayInfo` and its interaction with `ItemFrameInfo`.
- To change dirty-area calculation or packet forwarding, inspect `MapDisplay.Layer`, `MapClip`, `MapSession.Owner`, `MapDisplay.flushCanvasChanges()`, and `MapDisplayTile`.
