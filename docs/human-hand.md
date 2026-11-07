# Human Hands

`HumanHand` and `HumanHandRole` provide cross-version access to a human entity's hands and held items, including servers as old as Minecraft 1.8.9. They are useful for inventory handling and for interpreting packet APIs that identify which hand a player used.

## Physical hands and roles

The two enums describe different things:

| Type | Values | Meaning |
|---|---|---|
| `HumanHand` | `LEFT`, `RIGHT` | The physical hand, relative to the player. |
| `HumanHandRole` | `MAIN`, `OFF` | The main or off-hand inventory role. |

The player's main-hand setting determines which physical hand has each role. For a left-handed player, `LEFT` is `MAIN` and `RIGHT` is `OFF`; for a right-handed player, the opposite is true. Do not assume that `RIGHT` always means `MAIN`.

```java
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.HumanHandRole;

HumanHand mainHand = HumanHand.getMainHand(player);
HumanHand offHand = HumanHand.getOffHand(player);
HumanHandRole leftRole = HumanHand.LEFT.getRoleOf(player);
HumanHand mainFromRole = HumanHandRole.MAIN.getHandOf(player);
```

Both enums also provide `opposite()`. `HumanHandRole.fromHandOf(player, hand)` is another way to resolve a physical hand to its role. These conversions assume a right main hand if the human entity is `null`.

## Held items

Use the role when accessing a main- or off-hand inventory slot; use `HumanHand` when you have a physical left/right hand:

```java
import org.bukkit.inventory.ItemStack;

ItemStack mainItem = HumanHandRole.MAIN.getHeldItem(player);
ItemStack offItem = HumanHandRole.OFF.getHeldItem(player);
HumanHandRole.OFF.setHeldItem(player, replacement);

ItemStack leftItem = HumanHand.getHeldItem(player, HumanHand.LEFT);
HumanHand.setHeldItem(player, HumanHand.RIGHT, replacement);
```

`HumanHand` also provides `getItemInMainHand`, `getItemInOffHand`, `setItemInMainHand`, and `setItemInOffHand` convenience methods. Held-item methods require a non-null human entity. On Minecraft 1.8.9 and earlier, there is no off-hand inventory slot: reading it returns `null` and setting it has no effect. The physical main hand defaults to `RIGHT` and the off hand to `LEFT` on those versions.

## Packet and interaction APIs

Many interaction packet handles use `HumanHandRole` because the protocol identifies the main or off hand, not necessarily left or right. For example, `ServerboundUseItemPacketHandle`, `ServerboundUseItemOnPacketHandle`, `ServerboundInteractPacketHandle`, and `ServerboundPunchPacketHandle` expose the hand role; their `getHand(player)` helpers resolve it to a physical hand using the player's setting.

```java
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.wrappers.HumanHandRole;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ServerboundUseItemPacketHandle;

@Override
public void onPacketReceive(PacketReceiveEvent event) {
    if (event.getType() == PacketType.IN_USE_ITEM) {
        ServerboundUseItemPacketHandle packet =
                ServerboundUseItemPacketHandle.createHandle(event.getPacket().getHandle());
        HumanHandRole role = packet.getHandRole();
        if (role == HumanHandRole.OFF) {
            // Handle an off-hand item-use action.
        }
    }
}
```

Some handles provide `withHandRole(...)` or `withHand(...)` to create a modified packet; replace the original using `event.setPacket(...)` when appropriate. For equipment update packets, `ClientboundSetEquipmentPacketHandle` instead uses Bukkit `EquipmentSlot` values, so do not treat a hand role as an equipment slot. Inventory and entity access still require appropriate thread handling; see [Packet Protocol](protocol.md).
