# Mojang mapping migration (Spigot -> Mojang)

On 2026-03-31 BKCommonLib migrated its generated NMS Handle class names from Spigot-style mappings to Mojang (mojmap) mappings. From BKCommonLib v2.0.0 onwards,
generated handle classes use the Mojang-style names shown below.

Use these tables to update imports, references, or your own generated templates that relies on net.minecraft.server imports being remapped.

---

## Packets

During the mojang-map migration, a large number of packet handle classes were renamed from the old Spigot-style `PacketPlayIn` / `PacketPlayOut` names to
Mojang-style `Serverbound...Packet` / `Clientbound...Packet` names.

### Clientbound (server -> client) packet renames

| Old (Spigot-style) | New (Mojang-style) |
|---|---|
| PacketPlayOutSpawnEntityHandle | ClientboundAddEntityPacketHandle |
| PacketPlayOutSpawnEntityExperienceOrbHandle | ClientboundAddExperienceOrbPacketHandle |
| PacketPlayOutSpawnEntityLivingHandle | ClientboundAddMobPacketHandle |
| PacketPlayOutSpawnEntityPaintingHandle | ClientboundAddPaintingPacketHandle |
| PacketPlayOutNamedEntitySpawnHandle | ClientboundAddPlayerPacketHandle |
| PacketPlayOutAnimationHandle | ClientboundAnimatePacketHandle |
| PacketPlayOutBlockBreakAnimationHandle | ClientboundBlockDestructionPacketHandle |
| PacketPlayOutTileEntityDataHandle | ClientboundBlockEntityDataPacketHandle |
| PacketPlayOutBlockActionHandle | ClientboundBlockEventPacketHandle |
| PacketPlayOutBlockChangeHandle | ClientboundBlockUpdatePacketHandle |
| PacketPlayOutServerDifficultyHandle | ClientboundChangeDifficultyPacketHandle |
| PacketPlayOutCloseWindowHandle | ClientboundContainerClosePacketHandle |
| PacketPlayOutWindowItemsHandle | ClientboundContainerSetContentPacketHandle |
| PacketPlayOutWindowDataHandle | ClientboundContainerSetDataPacketHandle |
| PacketPlayOutSetSlotHandle | ClientboundContainerSetSlotPacketHandle |
| PacketPlayOutSetCooldownHandle | ClientboundCooldownPacketHandle |
| PacketPlayOutCustomSoundEffectHandle | ClientboundCustomSoundPacketHandle |
| PacketPlayOutEntityStatusHandle | ClientboundEntityEventPacketHandle |
| PacketPlayOutUnloadChunkHandle | ClientboundForgetLevelChunkPacketHandle |
| PacketPlayOutMapChunkHandle | ClientboundLevelChunkWithLightPacketHandle |
| PacketPlayOutWorldEventHandle | ClientboundLevelEventPacketHandle |
| PacketPlayOutWorldParticlesHandle | ClientboundLevelParticlesPacketHandle |
| PacketPlayOutLoginHandle | ClientboundLoginPacketHandle |
| PacketPlayOutMapHandle | ClientboundMapItemDataPacketHandle |
| PacketPlayOutEntityHandle | ClientboundMoveEntityPacketHandle |
| PacketPlayOutVehicleMoveHandle | ClientboundMoveVehiclePacketHandle |
| PacketPlayOutOpenWindowHandle | ClientboundOpenScreenPacketHandle |
| PacketPlayOutOpenSignEditorHandle | ClientboundOpenSignEditorPacketHandle |
| PacketPlayOutAbilitiesHandle | ClientboundPlayerAbilitiesPacketHandle |
| PacketPlayOutPositionHandle | ClientboundPlayerPositionPacketHandle |
| PacketPlayOutEntityDestroyHandle | ClientboundRemoveEntitiesPacketHandle |
| PacketPlayOutRespawnHandle | ClientboundRespawnPacketHandle |
| PacketPlayOutEntityHeadRotationHandle | ClientboundRotateHeadPacketHandle |
| PacketPlayOutCameraHandle | ClientboundSetCameraPacketHandle |
| PacketPlayOutScoreboardDisplayObjectiveHandle | ClientboundSetDisplayObjectivePacketHandle |
| PacketPlayOutEntityMetadataHandle | ClientboundSetEntityDataPacketHandle |
| PacketPlayOutAttachEntityHandle | ClientboundSetEntityLinkPacketHandle |
| PacketPlayOutEntityVelocityHandle | ClientboundSetEntityMotionPacketHandle |
| PacketPlayOutEntityEquipmentHandle | ClientboundSetEquipmentPacketHandle |
| PacketPlayOutExperienceHandle | ClientboundSetExperiencePacketHandle |
| PacketPlayOutUpdateHealthHandle | ClientboundSetHealthPacketHandle |
| PacketPlayOutHeldItemSlotHandle | ClientboundSetHeldSlotPacketHandle |
| PacketPlayOutScoreboardObjectiveHandle | ClientboundSetObjectivePacketHandle |
| PacketPlayOutMountHandle | ClientboundSetPassengersPacketHandle |
| PacketPlayOutScoreboardTeamHandle | ClientboundSetPlayerTeamPacketHandle |
| PacketPlayOutScoreboardScoreHandle | ClientboundSetScorePacketHandle |
| PacketPlayOutUpdateTimeHandle | ClientboundSetTimePacketHandle |
| PacketPlayOutNamedSoundEffectHandle | ClientboundSoundPacketHandle |
| PacketPlayOutStopSoundHandle | ClientboundStopSoundPacketHandle |
| PacketPlayOutPlayerListHeaderFooterHandle | ClientboundTabListPacketHandle |
| PacketPlayOutCollectHandle | ClientboundTakeItemEntityPacketHandle |
| PacketPlayOutEntityTeleportHandle | ClientboundTeleportEntityPacketHandle |
| PacketPlayOutAdvancementsHandle | ClientboundUpdateAdvancementsPacketHandle |
| PacketPlayOutEntityEffectHandle | ClientboundUpdateMobEffectPacketHandle |

### Serverbound (client -> server) packet renames

| Old (Spigot-style) | New (Mojang-style) |
|---|---|
| PacketPlayInTeleportAcceptHandle | ServerboundAcceptTeleportationPacketHandle |
| PacketPlayInChatHandle | ServerboundChatPacketHandle |
| PacketPlayInClientCommandHandle | ServerboundClientCommandPacketHandle |
| PacketPlayInEnchantItemHandle | ServerboundContainerButtonClickPacketHandle |
| PacketPlayInWindowClickHandle | ServerboundContainerClickPacketHandle |
| PacketPlayInCloseWindowHandle | ServerboundContainerClosePacketHandle |
| PacketPlayInUseEntityHandle | ServerboundInteractPacketHandle |
| PacketPlayInFlyingHandle | ServerboundMovePlayerPacketHandle |
| PacketPlayInVehicleMoveHandle | ServerboundMoveVehiclePacketHandle |
| PacketPlayInBoatMoveHandle | ServerboundPaddleBoatPacketHandle |
| PacketPlayInAbilitiesHandle | ServerboundPlayerAbilitiesPacketHandle |
| PacketPlayInBlockDigHandle | ServerboundPlayerActionPacketHandle |
| PacketPlayInEntityActionHandle | ServerboundPlayerCommandPacketHandle |
| PacketPlayInSteerVehicleHandle | ServerboundPlayerInputPacketHandle |
| PacketPlayInEnchantItemHandle | ServerboundSetCreativeModeSlotPacketHandle |
| PacketPlayInSetCreativeSlotHandle | ServerboundSetCreativeModeSlotPacketHandle |
| PacketPlayInUseItemHandle | ServerboundUseItemPacketHandle |
| PacketPlayInUseItemOnHandle | ServerboundUseItemOnPacketHandle |

### Protocol.common packet moves

Some packets were moved from `net.minecraft.network.protocol.game` into `net.minecraft.network.protocol.common` during the migration. Below are notable examples:

| Old (Spigot-style, game package)          | New (Mojang-style, common package)      |
|-------------------------------------------|-----------------------------------------|
| PacketPlayOutCustomPayloadHandle          | ClientboundCustomPayloadPacketHandle    | 
| PacketPlayOutDisconnectHandle             | ClientboundDisconnectPacketHandle       | 
| PacketPlayOutKeepAliveHandle              | ClientboundKeepAlivePacketHandle        | 
| PacketPlayOutResourcePackSendHandle       | ClientboundResourcePackPushPacketHandle |
| PacketPlayOutPlayerListHeaderFooterHandle | ClientboundTabListPacketHandle          |

---

## Entity handle renames

The migration renamed many entity-related Handle classes. These are commonly used for DataWatcher/entity metadata access; update references accordingly.

| Old (Spigot-style) | New (Mojang-style) |
|---|---|
| EntityLivingHandle | LivingEntityHandle |
| EntityInsentientHandle | MobHandle |
| EntityAgeableHandle | AgeableMobHandle |
| EntityBatHandle | BatHandle |
| EntityEnderCrystalHandle | EndCrystalHandle |
| EntityArmorStandHandle | ArmorStandHandle |
| EntityHangingHandle | HangingEntityHandle |
| EntityItemFrameHandle | ItemFrameHandle |
| EntityItemHandle | ItemEntityHandle |
| EntityShulkerHandle | ShulkerHandle |
| EntitySlimeHandle | SlimeHandle |
| EntityHumanHandle | PlayerHandle |
| PlayerAbilitiesHandle | AbilitiesHandle |
| PlayerInventoryHandle | InventoryHandle |
| EntityFishingHookHandle | FishingHookHandle |
| EntityBoatHandle | BoatHandle |
| EntityMinecartAbstractHandle | AbstractMinecartHandle |
| EntityMinecartTNTHandle | MinecartTNTHandle |
| EntityMinecartFurnaceHandle | MinecartFurnaceHandle |
| EntityMinecartHopperHandle | MinecartHopperHandle |
| EntityMinecartMobSpawnerHandle | MinecartSpawnerHandle |
| EntityMinecartCommandBlockHandle | MinecartCommandBlockHandle |
| EntityTypesHandle | EntityTypeHandle |
| EntityDisplayHandle | DisplayHandle |
| EntityVehicleHandle | VehicleEntityHandle |

## Miscellaneous Handle classes

The table below contains other notable Handle classes in `src/main/generated/com/bergerkiller/generated` that were renamed:

| Old (Spigot-style)         | New (Mojang-style)               |
|----------------------------|----------------------------------|
| IBlockDataHandle           | BlockStateHandle                 |
| IBlockStateHandle          | PropertyHandle                   |
| NibbleArrayHandle          | DataLayerHandle                  |
| WorldHandle                | LevelHandle                      |
| WorldServerHandle          | ServerLevelHandle                |
| DataWatcherHandle | SynchedEntityDataHandle |
| DataWatcherObjectHandle | EntityDataAccessorHandle |
| DataWatcherRegistryHandle | EntityDataSerializersHandle |
| EnumGamemodeHandle         | GameTypeHandle                   |
| CraftingManagerHandle      | RecipeManagerHandle              |
| FurnaceRecipeHandle        | SmeltingRecipeHandle             |
| MobSpawnerAbstractHandle   | BaseSpawnerHandle                |
| ForcedChunkHandle          | ForcedChunksSavedDataHandle      |
| ChunkCoordIntPairHandle    | ChunkPosHandle                   |
| IBlockAccessHandle         | BlockGetterHandle                |
| BlockFenceHandle           | FenceBlockHandle                 |
| BlockFenceGateHandle       | FenceGateBlockHandle             |
| BlockCobbleWallHandle      | WallBlockHandle                  |
| TileEntityFurnaceHandle    | AbstractFurnaceBlockEntityHandle |
| TileEntityHandle           | BlockEntityHandle                |
| TileEntityTypesHandle      | BlockEntityTypeHandle            |
| TileEntityHopperHandle     | HopperBlockEntityHandle          |
| TileEntitySignHandle       | SignBlockEntityHandle            |
| ChunkHandle                | LevelChunkHandle                 |
| ChunkSectionHandle         | LevelChunkSectionHandle          |
| DataPaletteBlockHandle     | PalettedContainerHandle          |
| DimensionManagerHandle     | DimensionTypeHandle              |
| HeightMapHandle            | HeightmapHandle                  |
| MapIconHandle              | MapDecorationHandle              |
| AxisAlignedBBHandle        | AABBHandle                       |
| MovingObjectPositionHandle | HitResultHandle                  |
| Vec3DHandle                | Vec3Handle                       |
| EnumSkyBlockHandle         | LightLayerHandle                 |
| SoundEffectTypeHandle      | SoundTypeHandle                  |
| EnumMoveTypeHandle         | MoverTypeHandle                  |
| EnumMainHandHandle         | HumanoidArmHandle                |
| EnumItemSlotHandle         | EquipmentSlotHandle              |

There may have been some package location changes too, as the new names closely resemble where Mojang classes exist as of 1.21.11.

---

### If you can't find a match: use the visualization test

If you have a spigot class name (handle or not) that you cannot find, you can use the test helper in `src/test/java/com/bergerkiller/bukkit/common/SpigotMappingTest.java`:

1. Open `SpigotMappingTest.testVisualizeMappings()` and replace the example argument (currently `"HoverEvent"`) with the fully-qualified Spigot class name you want to visualize, for example:

```java
// example: visualize the mapping for the old Spigot packet class
mappings.visualizeMapping("net.minecraft.network.protocol.game.PacketPlayOutStatistic");
```

2. Run that single test method from your IDE or from Gradle. From the repository root you can run:

```bash
# run only the one test (remove @Ignore or run via your IDE test runner)
./gradlew test --tests "com.bergerkiller.bukkit.common.SpigotMappingTest.testVisualizeMappings"
```

3. The visualization prints the mappings known for that name and will show the associated Mojang name(s) and versions. That helps find the corresponding new generated class name.

It does a contains check, so you can also use keywords. If it generates too much output you can use the more specific fully-qualified class name with the `visualizeMappingsForSpigotClass` / `visualizeMappingsForMojangClass` methods instead.

---
