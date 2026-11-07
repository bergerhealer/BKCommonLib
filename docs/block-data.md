# BlockData

BKCommonLib's `BlockData` represents a block's material together with its properties, such as facing direction, redstone power, or whether it is waterlogged. It provides cross-version access to these values without separately querying the block's type and state.

## A note on names

Minecraft calls the material-and-properties combination a *block state*. BKCommonLib calls it `BlockData`. This is distinct from Bukkit's `org.bukkit.block.BlockState`, which is a snapshot of a block at a location and can expose additional block-entity data for blocks such as chests and signs. Bukkit also has its own `org.bukkit.block.data.BlockData` type; the examples below use `com.bergerkiller.bukkit.common.wrappers.BlockData`.

Use BKCommonLib `BlockData` for material and properties, not for inventories, sign text, or other block-entity contents.

## Read and write world blocks

`WorldUtil.getBlockData` reads the material and properties together. This avoids separate type and state lookups when both are needed. `WorldUtil.setBlockData` applies a `BlockData` to the world and performs physics updates.

```java
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import org.bukkit.Material;

BlockData data = WorldUtil.getBlockData(block);
Material material = data.getType();

// Copy the material and properties to another block
WorldUtil.setBlockData(otherBlock, data);
```

Both methods also have overloads taking a `World` and coordinates or an `IntVector3`. Reading or changing a world block should follow the server's normal main-thread requirements. Copying `BlockData` does not copy block-entity contents.

`WorldUtil.setBlockDataFast(...)` is a separate alternative that does **not** perform physics updates; use it only when that behavior is intended. `WorldUtil.setBlockType(...)` sets a material's default block data with physics updates.

## Read and change properties

Use `getProperty(name, type)` to read a property and `setProperty(name, value)` to obtain an updated `BlockData`. The set operation returns the updated value; it does not apply the change to the world. Store the return value and use `WorldUtil.setBlockData` to apply it:

```java
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

BlockData data = WorldUtil.getBlockData(block);
if (data.isType(Material.LEVER)) {
    BlockFace facing = data.getProperty("facing", BlockFace.class);
    Boolean powered = data.getProperty("powered", Boolean.class);

    BlockData updated = data.setProperty("powered", true);
    WorldUtil.setBlockData(block, updated);
}
```

Available properties depend on the block and Minecraft version. Check `getPropertyKey(name)` for `null` before using a property that might not exist. `getProperties()` lists the available `BlockProperty<?>` keys; each key exposes its `name()` and possible `values()`. The `getProperty(propertyKey)` and `setProperty(propertyKey, value)` overloads also accept these keys.

## Create and serialize block data

`BlockData.fromMaterial(material)` obtains default block data for a Bukkit material. Change its properties by chaining `setProperty` calls before applying it:

```java
BlockData data = BlockData.fromMaterial(Material.FURNACE)
        .setProperty("facing", BlockFace.EAST)
        .setProperty("lit", true);
WorldUtil.setBlockData(block, data);

String serialized = data.serializeToString();
BlockData restored = BlockData.fromString(serialized);
```

`serializeToString()` preserves the material and properties in a cross-version-compatible representation. `fromString(...)` returns `null` if it cannot deserialize the input; validate values read from configuration before using them.

Other `BlockData` methods expose properties such as solidity, context-dependent opacity, and step/place/break sound keys. For playing those sound keys, see [Sound Effects](sound-effects.md).

## MaterialData API

BKCommonLib still uses Bukkit's legacy `org.bukkit.material.MaterialData` API for compatibility with Minecraft versions from before Bukkit had its block-data API. Bukkit has deprecated `MaterialData`, and its legacy implementations are unreliable for modern materials. BKCommonLib applies custom compatibility patches, but this API is only reliably supported for:

- Rails
- Signs, including hanging signs
- Pressure plates
- Buttons
- Chests and furnaces
- Redstone torches and comparators

`BlockData.getMaterialData()` returns a cached **read-only** value; do not modify it. Use `newMaterialData()` if a mutable copy is needed. Prefer `BlockData` property access for other materials rather than relying on legacy `MaterialData` behavior.
