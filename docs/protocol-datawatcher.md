# Entity Metadata with DataWatcher

`DataWatcher` stores and tracks entity metadata: values such as entity flags, a custom name, and entity-specific properties. Metadata is sent when an entity is spawned and in separate packets when its state changes. It is particularly useful when creating client-visible entities with packets without spawning a real server entity.

Metadata keys are typed `DataWatcher.Key<T>` constants, generally named `DATA_<NAME>` on the appropriate generated entity handle. For example, `EntityHandle.DATA_FLAGS` is a `Key<Byte>` for common entity flags, while `ArmorStandHandle.DATA_ARMORSTAND_FLAGS` is a `Key<Byte>` for armor-stand-specific flags. Use keys appropriate for the entity type being sent, including inherited keys from its parent entity types.

## Create a prototype first

When creating multiple metadata instances with the same keys, build a `DataWatcher.Prototype` once and use it to create individual watchers. Constructing each watcher from scratch and registering keys one by one repeatedly initializes and resizes internal structures, which can cause lag. A prototype prepares the key layout and initial values once so instances can be created efficiently.

This example creates metadata for an invisible, small armor stand, using flags from both `EntityHandle` and `ArmorStandHandle`:

```java
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle;

private static final DataWatcher.Prototype ARMOR_STAND_METADATA =
        DataWatcher.Prototype.build()
                .setClientByteDefault(EntityHandle.DATA_FLAGS, 0)
                .setFlag(EntityHandle.DATA_FLAGS, EntityHandle.DATA_FLAG_INVISIBLE, true)
                .setClientByteDefault(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, 0)
                .setFlag(ArmorStandHandle.DATA_ARMORSTAND_FLAGS,
                        ArmorStandHandle.DATA_FLAG_IS_SMALL, true)
                .create();

// Create one watcher per independently managed metadata state
DataWatcher metadata = ARMOR_STAND_METADATA.create();
```

`setClientDefault(...)` and `setClientByteDefault(...)` specify the value a newly spawned client entity already has. Set these **before** assigning a different initial value; matching client defaults can then be omitted from spawn metadata on supported versions. Without a client default, a newly registered value is treated as needing to be sent. `set(...)` and `setByte(...)` configure the watcher's initial values, while `setFlag(...)` changes individual bits without replacing the other flags.

Keep the prototype and reuse it. Each `prototype.create()` produces an independent watcher. To derive another configuration, use `prototype.modify()`, change its builder, and call `create()` on that builder.

## Read and update values

The key's type determines the value accepted by `set(key, value)` and returned by `get(key)`. For byte flags, `setByte` accepts an integer and `setFlag` updates selected bits:

```java
boolean small = metadata.getFlag(
        ArmorStandHandle.DATA_ARMORSTAND_FLAGS, ArmorStandHandle.DATA_FLAG_IS_SMALL);
metadata.setFlag(EntityHandle.DATA_FLAGS, EntityHandle.DATA_FLAG_GLOWING, true);
```

`get(key)` throws if the key is not watched; `tryGet(key, fallback)` supplies a fallback for an unregistered key. `forceSet(key, value)` marks a value as changed even when it is equal to the current value. Some metadata keys are not supported on every Minecraft version; use properties appropriate for the target versions.

## Send metadata

The spawn packet and metadata must reference the same entity ID. For the living-entity and named-player spawn packet handles, BKCommonLib provides helpers that account for versions where metadata is embedded in the spawn packet versus sent separately:

```java
import com.bergerkiller.bukkit.common.utils.PacketUtil;

// spawnPacket is a configured ClientboundAddMobPacketHandle for the entity.
PacketUtil.sendEntityLivingSpawnPacket(viewer, spawnPacket, metadata);

// For a configured ClientboundAddPlayerPacketHandle, use instead:
// PacketUtil.sendNamedEntitySpawnPacket(viewer, playerSpawnPacket, metadata);
```

These helpers accept their respective spawn packet types, which are not applicable to every entity or Minecraft version. When using a separate spawn packet, send the spawn first and follow it with `ClientboundSetEntityDataPacketHandle.createForSpawn(entityId, metadata)`. This constructs the initial metadata packet without consuming pending changes. Set up the packet with a valid entity type, ID, UUID, position, and other required spawn data for the target version.

For an existing client-visible entity, send changed metadata separately:

```java
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import org.bukkit.entity.Player;

metadata.setFlag(EntityHandle.DATA_FLAGS, EntityHandle.DATA_FLAG_GLOWING, true);
ClientboundSetEntityDataPacketHandle update =
        ClientboundSetEntityDataPacketHandle.createForChanges(entityId, metadata);
for (Player viewer : viewers) {
    PacketUtil.sendPacket(viewer, update);
}
```

`createForChanges` packs and clears the watcher's changed state. Create the update **once** and send that same packet to every intended viewer; constructing a new change packet for each viewer would consume the changes on the first call. For a newly tracking viewer, use `createForSpawn` to send the current non-default metadata without clearing pending changes. Prototype instances start with no pending changes, so use spawn metadata for their initial state rather than `createForChanges`.

These packets update the client representation; a standalone `DataWatcher` does not itself change a real Bukkit entity's server-side properties. See [Packet Protocol](protocol.md) for listeners, sending, and thread-safety considerations.
