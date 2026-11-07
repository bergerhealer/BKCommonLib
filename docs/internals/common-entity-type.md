# CommonEntityType

**Audience:** BKCommonLib maintainers.

`CommonEntityType` is BKCommonLib's internal registry for relating Bukkit entity types to version-dependent Minecraft entity classes, common wrapper classes, and entity identifiers. Although some public APIs expose this type, plugin authors should generally use Bukkit's `EntityType`; `CommonEntityType` is primarily compatibility infrastructure, not a second plugin-facing entity taxonomy.

## Key files

- [`CommonEntityType`](../../src/main/java/com/bergerkiller/bukkit/common/entity/CommonEntityType.java): registry, type mappings, lookup methods, and hooked entity construction.
- [`TypeNameLookup`](../../src/main/java/com/bergerkiller/bukkit/common/entity/TypeNameLookup.java): compatibility matching for entity names and aliases.
- [`EntityTypingHandler`](../../src/main/java/com/bergerkiller/bukkit/common/internal/logic/EntityTypingHandler.java) and [`EntityTypingHandler_1_14`](../../src/main/java/com/bergerkiller/bukkit/common/internal/logic/EntityTypingHandler_1_14.java): version-specific resolution of an NMS entity type to its concrete entity class.
- [`entity_types.txt`](../../src/main/templates/com/bergerkiller/templates/net/minecraft/entity/entity_types.txt): version-dependent NMS entity-type lookup methods.
- [`CommonEntity`](../../src/main/java/com/bergerkiller/bukkit/common/entity/CommonEntity.java): uses the registry to select wrappers, validate controller support, and create entities.
- [`CommonBootstrap`](../../src/main/java/com/bergerkiller/bukkit/common/internal/CommonBootstrap.java): registers conversion support for this type.

## Registry and mappings

Static initialization first initializes the common server compatibility layer, then builds a registry entry for each Bukkit `EntityType`. Each entry connects the Bukkit enum to the best-known NMS entity class, Bukkit entity class, and BKCommonLib `CommonEntity` wrapper class. The wrapper mapping is selected by Bukkit type assignability, with specific wrappers such as players, items, and minecart variants taking precedence over generic living-entity wrappers.

NMS classes are resolved from Minecraft's entity-type registry when possible, with explicit fallbacks for special entity types and differences in historical names. A type that cannot be mapped is represented with invalid templates and sentinel identifiers; lookup methods return the shared `UNKNOWN` entry rather than `null` for unrecognized types or handles. Registry initialization logs failures for server entity types it cannot resolve, except types recognized as custom by the server.

### Resolving an NMS entity type to its class

From Minecraft 1.14 onward, an NMS entity-type registry entry does not directly expose its concrete entity class. `CommonEntityType` asks `EntityTypingHandler` to resolve it. The 1.14+ implementation first inspects the generic type information on static fields in the NMS entity-types class, which cheaply identifies most built-in types. It explicitly registers several types whose constructors can fire events or otherwise have side effects, avoiding construction for those entries.

For a type not found by that inspection, the handler calls the NMS `EntityType.create(...)` factory with a synthetic `ServerLevel` named `zzdummyzz`, then reads the returned entity's runtime class. This dummy level is not a real, loaded Bukkit world. BKCommonLib builds it with the world data and runtime services needed by entity constructors and supplies version-/server-specific configuration fields. The temporary entity exists only to discover its class; it is not added to a world. Resolved classes are cached by entity-type object identity.

This fallback is a fragile startup path. Entity constructors can transitively access world state, registries, configuration, clocks, environment attributes, server services, or plugin-specific fields that are normally initialized on a real world. If a constructor expects state the dummy level does not provide, registry initialization or a later class lookup can fail before the server/plugin is fully available. When investigating startup failures around entity-type mapping, inspect the failing entity constructor chain and the dummy-world initialization in `EntityTypingHandler_1_14.Handler.initWorldServer()`; add narrowly scoped dummy-world support or a safe explicit class registration rather than adding the type to the general construction path blindly.

Older versions use different mechanisms: through 1.12.2, entity classes are obtained from legacy mappings; on 1.13, the entity-type entry exposes its class directly. Keep these paths distinct from the 1.14+ factory fallback.

The registry maintains lookup paths for Bukkit `EntityType`, NMS entity classes and instances, NMS entity-type handles, and legacy/current entity identifiers. `entityTypeId` is Minecraft's entity-type identifier where available. `objectTypeId` and `objectExtraData` support older object/spawn representations, including a legacy mapping when the modern NMS type identifier is unavailable. `moveTicks` is an internal movement synchronization interval, with a different default for minecarts.

`entityTypeNames` comes from BKCommonLib's alias table. `TypeNameLookup` uses these names to parse older, newer, or inexact entity names and can return either the corresponding Bukkit type or registry entry. Keep aliases aligned with Bukkit naming changes; do not assume every server version has every enum constant.

## Important lookup behavior

- Use `byEntityType()` when starting from Bukkit's enum. Unknown or unsupported values resolve to `UNKNOWN`.
- Use `byEntity()` or `byNMSEntity()` when starting from a Bukkit entity or NMS handle. `byNMSEntityClass()` is useful when only the runtime NMS class is available.
- Use `byNMSEntityType()` or `byNMSEntityTypeRaw()` for the NMS entity-type registry value. The raw variant can fall back to resolving the handle's entity class.
- Use `byName()` for exact Bukkit enum names and `parseNameToCommonEntityType()` for alias/inexact parsing. The parse method returns `null` when no name matches, unlike registry lookup methods that return `UNKNOWN`.

These are internal mapping operations. Plugin-facing APIs that accept or return entity kinds should normally use Bukkit `EntityType`, converting at the BKCommonLib boundary where needed.

## Hooked entity creation

`createNMSHookEntity(location)` constructs a new NMS entity instance with an `EntityHook` already installed, wraps it in the appropriate `CommonEntity` subtype, initializes its location, and binds a default controller without firing the attachment callback. This is used by `CommonEntity.spawn()` so a custom controller can be bound before the entity is added to the world. The method is internal plumbing: callers are responsible for the lifecycle and world insertion sequence.

`createCommonEntityNull()` creates an intentionally uninitialized hooked object. It is not a valid entity to spawn; `CommonEntity.createNull()` warns that doing so can cause serious bugs. Do not use it as a shortcut for normal entity construction.

## Change entry points

- To support a Bukkit type added or renamed by a server release, inspect the name aliases and NMS resolution/fallback logic, then verify registry initialization against the affected server versions.
- To change which BKCommonLib wrapper represents an entity, inspect the ordered `commonPairs` mapping. More specific Bukkit classes must remain ahead of their supertypes.
- To change entity spawning or controller-hook construction, follow `CommonEntity.spawn()` and `CommonEntityType.createNMSHookEntity()` together; preserve the requirement that the hook exists before the entity is inserted.
