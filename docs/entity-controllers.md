# Entity Controllers

`EntityController` lets a plugin customize the behavior of a supported Bukkit entity by handling selected parts of its server-side lifecycle, movement, interactions, and collisions. Install a controller when an entity is added to a world, then override only the callbacks whose behavior needs to change. The default implementations preserve vanilla behavior for most callbacks.

## Key files

- [`EntityController`](../src/main/java/com/bergerkiller/bukkit/common/controller/EntityController.java): callbacks, collision options, and vanilla behavior delegation.
- [`CommonEntityController`](../src/main/java/com/bergerkiller/bukkit/common/entity/CommonEntityController.java): controller binding and attachment lifecycle.
- [`CommonEntity`](../src/main/java/com/bergerkiller/bukkit/common/entity/CommonEntity.java): support checks, controller assignment, and the Bukkit-facing entity wrapper.
- [`EntityAddEvent`](../src/main/java/com/bergerkiller/bukkit/common/events/EntityAddEvent.java): BKCommonLib event fired when an entity is added to a world.

## Lifecycle and installation

Listen for `EntityAddEvent`, filter to the entity types your controller supports, then get the `CommonEntity` wrapper and call `setController()`. Check `hasControllerSupport()` first: `setController()` cannot hook custom entity classes introduced by other plugins or entity types BKCommonLib does not recognize.

```java
@EventHandler(priority = EventPriority.LOWEST)
public void onEntityAdded(EntityAddEvent event) {
    if (!(event.getEntity() instanceof Minecart)) {
        return;
    }

    CommonEntity<?> entity = CommonEntity.get(event.getEntity());
    if (entity.hasControllerSupport()) {
        entity.setController(new MinecartController());
    }
}
```

`setController()` hooks the entity if needed, detaches any previous controller, and attaches the new one. Passing `null` selects a default controller; it does not remove vanilla behavior. `onAttached()` is called after the controller is bound and the entity is fully spawned, and is the first point where `getEntity()` is available. `onDetached()` runs while the controller can still access its entity, and is the place to release controller-owned resources. Replacing a controller calls the old controller's `onDetached()` before binding the new one.

## Spawning with a controller

For entities created by your plugin, use `CommonEntity.spawn()` to assign the controller before adding the entity to the world:

```java
MinecartController controller = new MinecartController();
CommonEntity.spawn(EntityType.MINECART, location, controller, null);
```

The controller is bound before the entity is added, so it is already active when the entity enters the world. BKCommonLib calls `onAttached()` after the entity has been added; use that callback to initialize logic that needs a spawned entity. The final `null` uses the normal entity network tracking behavior.

## Callbacks

Override callbacks on `EntityController<T>` for the behavior you want to customize. Unless the intent is to replace vanilla behavior, call `super` from an override whose default implementation delegates to Minecraft.

| Callback | Purpose and default |
|---|---|
| `onTick()` | Called during the entity's tick; delegates to vanilla ticking by default. |
| `onMove(MoveType, dx, dy, dz)` | Performs movement and collision handling; the default runs BKCommonLib's movement path. |
| `onPush(dx, dy, dz)` | Handles an external push; delegates to vanilla behavior by default. |
| `onDamage(DamageSource, damage)` | Handles incoming damage; delegates to vanilla damage handling by default. |
| `onDie(killed)` | Handles death or destruction. The default continues vanilla removal. Not calling `super.onDie(killed)` cancels that removal. It may be called more than once, is not called for chunk unload or cross-world teleport, and `killed` is always `true` through Minecraft 1.16.5. |
| `onInteractBy(HumanEntity, HumanHand, Vector)` | Handles interaction and returns an `InteractionResult`; defaults to vanilla interaction. The location can be `null` on versions before 26.1. |
| `onEntityCollision(Entity)` | Decides whether a collision with another entity is allowed; return `false` to ignore it. Defaults to `true`. If allowed, `onEntityBump(Entity)` follows. |
| `onEntityBump(Entity)` | Applies the bump response when another entity hits this one; defaults to vanilla velocity handling. |
| `onBlockCollision(Block, BlockFace)` | Decides whether a collision with a block is allowed; return `false` to ignore it. Defaults to `true`. |
| `onPositionPassenger(Entity, EntityPositionApplier)` | Sets a passenger's position and orientation; defaults to vanilla positioning. Use the applier rather than teleporting the passenger. |
| `onItemSet(index, ItemStack)` | Handles an inventory slot update for entities with inventories; defaults to vanilla handling. |
| `getLocalizedName()` | Supplies the entity's localized name; defaults to the server's entity name. |
| `isPlayerTakeable()` | Controls whether a sole player passenger can log out while taking the vehicle with them; defaults to `true`. |

`setBlockCollisionEnabled` and `setEntityCollisionEnabled` disable the corresponding collision physics calculations themselves, not just their callbacks. This can significantly improve performance for entities that do not need those interactions. These are behavioral changes as well as optimizations: disabling block collisions lets the entity pass through the floor, while disabling entity collisions lets it pass through other entities. `setBlockActivationEnabled` separately controls effects such as pressure plates and detector rails reacting to movement. `setBlockCollisionBounds()` changes the bounds used for block collision checks.

## Key relationships

The add event is the usual point for selecting entities and assigning controllers. `CommonEntity` handles hook installation and controller replacement; the controller owns the custom behavior and its attached-state resources. See the [maintainer guide](internals/entity-controllers.md) for how BKCommonLib routes callbacks and replaces entity instances.
