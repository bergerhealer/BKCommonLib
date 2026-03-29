package com.bergerkiller.bukkit.common.conversion.type;

import java.util.Arrays;
import java.util.List;

import com.bergerkiller.bukkit.common.internal.logic.ChunkHandleTracker;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypeHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.proxy.EntitySliceProxy_1_8;
import com.bergerkiller.bukkit.common.internal.proxy.EntitySliceProxy_1_8_3;
import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;
import com.bergerkiller.bukkit.common.inventory.InventoryBase;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.resources.ParticleType;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockStateChange;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.HeightMap;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.bukkit.common.wrappers.InteractionResult;
import com.bergerkiller.bukkit.common.wrappers.InventoryClickType;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.bukkit.common.wrappers.MobSpawner;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.generated.net.minecraft.ChatFormattingHandle;
import com.bergerkiller.generated.net.minecraft.core.NonNullListHandle;
import com.bergerkiller.generated.net.minecraft.core.RotationsHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.shapes.VoxelShapeHandle;
import com.bergerkiller.generated.net.minecraft.sounds.SoundSourceHandle;
import com.bergerkiller.generated.net.minecraft.util.ClassInstanceMultiMapHandle;
import com.bergerkiller.generated.net.minecraft.world.DifficultyHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectInstanceHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.HumanoidArmHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.item.crafting.IngredientHandle;
import com.bergerkiller.generated.net.minecraft.world.level.ChunkPosHandle;
import com.bergerkiller.generated.net.minecraft.world.level.GameTypeHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.Vec3Handle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftArtHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftWorldHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.data.CraftBlockDataHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.entity.CraftEntityHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;

public class HandleConversion {

    @ConverterMethod(output="net.minecraft.nbt.CompoundTag")
    public static Object serializeBlockStateChange(BlockStateChange blockStateChange) {
        return blockStateChange.serialize().getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.world.level.block.entity.BlockEntityType")
    public static Object toTileEntityTypesHandle(BlockStateType blockStateType) {
        return blockStateType.getRawHandle();
    }

    @ConverterMethod
    public static int blockStateTypeToId(BlockStateType blockStateType) {
        return blockStateType.getSerializedId();
    }

    @ConverterMethod(output="net.minecraft.resources.Identifier")
    public static Object blockStateTypeToKey(BlockStateType blockStateType) {
        return blockStateType.getKey().getRaw();
    }

    @ConverterMethod(output="T extends net.minecraft.world.entity.Entity")
    public static Object toEntityHandle(org.bukkit.entity.Entity entity) {
        try {
            return CraftEntityHandle.T.getHandle.invoker.invoke(entity);
        } catch (RuntimeException ex) {
            if (CraftEntityHandle.T.isAssignableFrom(entity)) {
                throw ex;
            } else {
                return null;
            }
        }
    }

    @ConverterMethod(output="net.minecraft.server.level.ServerLevel")
    public static Object toWorldHandle(org.bukkit.World world) {
        try {
            return CraftWorldHandle.T.getHandle.invoker.invoke(world);
        } catch (RuntimeException ex) {
            if (CraftWorldHandle.T.isAssignableFrom(world)) {
                throw ex;
            } else {
                return null;
            }
        }
    }

    @ConverterMethod(output="net.minecraft.world.level.chunk.LevelChunk")
    public static Object toChunkHandle(org.bukkit.Chunk chunk) {
        return ChunkHandleTracker.INSTANCE.getChunkHandle(chunk);
    }

    @ConverterMethod(output="net.minecraft.world.item.ItemStack", acceptsNull = true)
    public static Object toItemStackHandle(org.bukkit.inventory.ItemStack itemStack) {
        Object raw_handle = null;
        if (itemStack != null) {
            if (CraftItemStackHandle.T.isAssignableFrom(itemStack)) {
                raw_handle = CraftItemStackHandle.T.handle.get(itemStack);
            } else {
                if (CommonBootstrap.isTestMode()) {
                    // Fallback under test - does not go into production!
                    ItemStackHandle handle = ItemStackHandle.newInstance(itemStack.getType());
                    handle.setAmountField(itemStack.getAmount());
                    handle.setDamageValue(itemStack.getDurability());
                    return handle.getRaw();
                }

                raw_handle = CraftItemStackHandle.asNMSCopy(itemStack);
            }
        }
        if (raw_handle == null && CommonCapabilities.ITEMSTACK_EMPTY_STATE) {
            raw_handle = ItemStackHandle.EMPTY_ITEM.getRaw();
        }
        return raw_handle;
    }

    @ConverterMethod(output="T extends net.minecraft.world.level.block.entity.BlockEntity")
    public static Object toTileEntityHandle(org.bukkit.block.BlockState blockState) {
        return BlockStateConversion.INSTANCE.blockStateToTileEntity(blockState);
    }

    @ConverterMethod(output="T extends net.minecraft.world.level.block.entity.BlockEntity")
    public static Object getTileEntityHandle(org.bukkit.block.Block block) {
        Object blockPosition = BlockPosHandle.createNew(block.getX(), block.getY(), block.getZ());
        return LevelHandle.T.getTileEntity.raw.invoke(toWorldHandle(block.getWorld()), blockPosition);
    }

    @ConverterMethod(output="net.minecraft.world.Container")
    public static Object toIInventoryHandle(InventoryBase inventory) {
        return inventory.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.world.Container")
    public static Object toIInventoryHandle(org.bukkit.inventory.Inventory inventory) {
        if (CraftInventoryHandle.T.isAssignableFrom(inventory)) {
            return CraftInventoryHandle.T.getHandle.raw.invoke(inventory);
        } else {
            return null;
        }
    }

    @ConverterMethod(output="net.minecraft.network.syncher.SynchedEntityData")
    public static Object toDataWatcherHandle(com.bergerkiller.bukkit.common.wrappers.DataWatcher dataWatcher) {
        return dataWatcher.getRawHandle();
    }

    @ConverterMethod(input="net.minecraft.world.entity.Entity", output="net.minecraft.network.syncher.SynchedEntityData")
    public static Object toDataWatcherHandle(Object nmsEntityHandle) {
        return EntityHandle.T.getDataWatcher.raw.invoke(nmsEntityHandle);
    }

    @ConverterMethod(output="T extends net.minecraft.world.item.Item")
    public static Object toItemHandle(org.bukkit.Material material) {
        return CraftMagicNumbersHandle.getItemFromMaterial(material);
    }

    @ConverterMethod(output="T extends net.minecraft.world.level.block.Block")
    public static Object toBlockHandle(org.bukkit.Material material) {
        return CraftMagicNumbersHandle.getBlockFromMaterial(material);
    }

    @ConverterMethod(output="T extends net.minecraft.world.level.block.Block")
    public static Object toBlockHandle(com.bergerkiller.bukkit.common.wrappers.BlockData blockData) {
        return blockData.getBlockRaw();
    }

    @ConverterMethod(output="T extends net.minecraft.world.level.block.state.BlockState")
    public static Object toIBlockDataHandle(com.bergerkiller.bukkit.common.wrappers.BlockData blockData) {
        return blockData.getData();
    }

    @ConverterMethod(input="net.minecraft.world.level.block.Block", output="T extends net.minecraft.world.level.block.state.BlockState")
    public static Object toIBlockDataHandleFromBlock(Object nmsBlockHandle) {
        return BlockHandle.T.getBlockData.raw.invoke(nmsBlockHandle);
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod(output="net.minecraft.world.level.GameType")
    public static Object toEnumGamemodeHandle(org.bukkit.GameMode gameMode) {
        return GameTypeHandle.T.getById.raw.invoke(gameMode.getValue());
    }

    @ConverterMethod(input="org.bukkit.inventory.MainHand", output="net.minecraft.world.InteractionHand", optional=true)
    public static Object mainHandToEnumHandHandle(Object mainHand) {
        return HumanHand.fromMainHand(mainHand).toNMSEnumHand(null);
    }

    @ConverterMethod(output="net.minecraft.world.InteractionHand", optional=true)
    public static Object humanHandToEnumHandHandle(HumanHand hand) {
        return hand.toNMSEnumHand(null);
    }

    @ConverterMethod(output="org.bukkit.inventory.MainHand", optional=true)
    public static Object humanHandToMainHandHandle(HumanHand hand) {
        return hand.toMainHand();
    }

    @ConverterMethod(output="net.minecraft.world.entity.HumanoidArm", optional=true)
    public static Object humanHandToEnumMainHandHandle(HumanHand hand) {
        if (hand == HumanHand.LEFT) {
            return HumanoidArmHandle.LEFT.getRaw();
        } else {
            return HumanoidArmHandle.RIGHT.getRaw();
        }
    }

    @ConverterMethod(output="T extends net.minecraft.network.protocol.Packet")
    public static Object toPacketHandle(CommonPacket commonPacket) {
        return commonPacket.getHandle();
    }

    @ConverterMethod(output="T extends net.minecraft.network.protocol.Packet<?>")
    public static Object toPacketHandle2(CommonPacket commonPacket) {
        return commonPacket.getHandle();
    }

    @ConverterMethod(output="net.minecraft.world.level.ChunkPos")
    public static Object toChunkCoordIntPairHandle(IntVector2 intVector2) {
        return ChunkPosHandle.T.fromIntVector2Raw.invoker.invoke(null, intVector2);
    }

    @ConverterMethod(output="net.minecraft.core.BlockPos")
    public static Object toBlockPositionHandle(IntVector3 intVector3) {
        return BlockPosHandle.T.fromIntVector3Raw.invoker.invoke(null, intVector3);
    }

    @ConverterMethod(output="net.minecraft.core.BlockPos")
    public static Object toBlockPositionHandle(Block block) {
        return BlockPosHandle.T.fromBukkitBlockRaw.invoker.invoke(null, block);
    }

    @ConverterMethod(output="net.minecraft.world.phys.Vec3")
    public static Object toVec3DHandle(Vector vector) {
        return Vec3Handle.T.fromBukkitRaw.invoker.invoke(null, vector);
    }

    @ConverterMethod(output="net.minecraft.core.Rotations")
    public static Object toVector3fHandle(Vector vector) {
        return RotationsHandle.T.fromBukkitRaw.invoker.invoke(null, vector);
    }

    @ConverterMethod(output="net.minecraft.world.entity.player.Abilities")
    public static Object toPlayerAbilitiesHandle(PlayerAbilities playerAbilities) {
        return playerAbilities.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.server.level.EntityTracker")
    public static Object toEntityTrackerHandle(EntityTracker entityTracker) {
        return entityTracker.getRawHandle();
    }

    @ConverterMethod(output="com.bergerkiller.bukkit.common.internal.LongHashSet")
    public static Object toLongHashSetHandle(LongHashSet longHashSetWrapper) {
        return longHashSetWrapper.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.util.IntHashMap<T>")
    public static Object toRawIntHashMapHandle(IntHashMap<?> intHashMapWrapper) {
        return intHashMapWrapper.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.world.Difficulty")
    public static Object toEnumDifficultyHandle(Integer difficultyId) {
        return DifficultyHandle.T.getById.raw.invoke(difficultyId);
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod(output="net.minecraft.world.Difficulty")
    public static Object toEnumDifficultyHandle(Difficulty difficulty) {
        return DifficultyHandle.T.getById.raw.invoke(difficulty.getValue());
    }

    @ConverterMethod(output="net.minecraft.world.level.chunk.LevelChunkSection")
    public static Object toChunkSectionHandle(ChunkSection section) {
        return section.getRawHandle();
    }

    @ConverterMethod
    @SuppressWarnings("deprecation")
    public static int fromPotionEffectTypeToId(PotionEffectType potionEffectType) {
        return potionEffectType.getId();
    }

    @ConverterMethod(output="net.minecraft.world.effect.MobEffect")
    public static Object toMobEffectListHandle(PotionEffectType potionEffectType) {
        @SuppressWarnings("deprecation")
        int id = potionEffectType.getId();

        return MobEffectHandle.T.fromId.invoke(id);
    }

    @ConverterMethod(output="net.minecraft.world.effect.MobEffectInstance")
    public static Object toMobEffectHandle(PotionEffect potionEffect) {
        return MobEffectInstanceHandle.T.fromBukkit.raw.invoke(potionEffect);
    }

    @ConverterMethod(output="net.minecraft.world.level.BaseSpawner")
    public static Object toMobSpawnerAbstractHandle(MobSpawner mobSpawner) {
        return mobSpawner.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.network.syncher.EntityDataAccessor<V>")
    public static Object toDataWatcherObjectHandle(DataWatcher.Key<?> keyWrapper) {
        return keyWrapper.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.network.syncher.SynchedEntityData.DataItem<V>")
    public static Object toDataWatcherItemHandle(DataWatcher.Item<?> itemWrapper) {
        return itemWrapper.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.network.syncher.SynchedEntityData.DataValue<V>")
    public static Object toDataWatcherPackedItemHandle(DataWatcher.PackedItem<?> itemWrapper) {
        return itemWrapper.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.resources.ResourceKey<V>")
    public static Object toResourceKeyHandle(ResourceKey<?> resourceKeyWrapper) {
        return resourceKeyWrapper.getRawHandle();
    }

    @ConverterMethod
    public static String worldTypeToString(org.bukkit.WorldType worldType) {
        return worldType.name();
    }

    @ConverterMethod(output="net.minecraft.network.chat.Component")
    public static Object toChatBaseComponent(ChatText text) {
        return text.getRawHandle();
    }

    @ConverterMethod
    public static String getChatTextMessage(ChatText text) {
        return text.getMessage();
    }

    @SuppressWarnings("unchecked")
    @ConverterMethod(output="net.minecraft.core.NonNullList<E>", optional=true)
    public static <E> Object toNonNullListHandle(List<E> list) {
        List<E> result = (List<E>) NonNullListHandle.create();
        result.addAll(list);
        return result;
    }

    // <= 1.11.2
    @ConverterMethod(output="net.minecraft.world.item.ItemStack")
    public static Object toDefaultItemStackHandle(CraftInputSlot slot) {
        return toItemStackHandle(slot.getDefaultChoice());
    }

    // 1.12 =>
    @ConverterMethod(output="net.minecraft.world.item.crafting.Ingredient", optional=true)
    public static Object toRecipeItemStackHandle(CraftInputSlot slot) {
        return IngredientHandle.createRawRecipeItemStack(Arrays.asList(slot.getChoices()));
    }

    @ConverterMethod(output="net.minecraft.resources.Identifier")
    public static Object getMinecraftKeyFromName(String name) {
        return IdentifierHandle.T.createNew.raw.invoke(name);
    }

    @ConverterMethod(input="net.minecraft.resources.Identifier")
    public static String getNameFromMinecraftKey(Object minecraftKeyHandle) {
        return minecraftKeyHandle.toString();
    }

    @ConverterMethod(input="net.minecraft.sounds.SoundSource", optional=true)
    public static String getNameFromSoundCategory(Object soundCategoryHandle) {
        return SoundSourceHandle.T.getName.invoker.invoke(soundCategoryHandle);
    }

    @ConverterMethod(output="net.minecraft.sounds.SoundSource", optional=true)
    public static Object getSoundCategoryFromName(String soundCategoryName) {
        Object result = SoundSourceHandle.T.byName.raw.invoke(soundCategoryName);
        if (result == null) {
            result = SoundSourceHandle.T.byName.raw.invoke("master");
        }
        return result;
    }

    @ConverterMethod(output="net.minecraft.server.InventoryClickType", optional=true)
    public static Object inventoryClickTypeToHandle(InventoryClickType inventoryClickType) {
        Class<?> type = CommonUtil.getClass("net.minecraft.world.inventory.InventoryClickType");
        if (type != null) {
            Object[] values = type.getEnumConstants();
            int id = inventoryClickType.getId();
            if (id >= 0 && id < values.length) {
                return values[id];
            } else {
                return values[0]; // fallback
            }
        }
        return null;
    }

    @ConverterMethod
    public static int inventoryClickTypeToId(InventoryClickType inventoryClickType) {
        return inventoryClickType.getId();
    }

    @ConverterMethod(input="net.minecraft.util.ClassInstanceMultiMap<?>", optional=true)
    public static List<Object> cbEntitySliceToList(Object nmsEntitySliceHandle) {
        if (CommonCapabilities.REVISED_CHUNK_ENTITY_SLICE) {
            return new EntitySliceProxy_1_8_3<Object>(ClassInstanceMultiMapHandle.createHandle(nmsEntitySliceHandle));
        } else {
            return new EntitySliceProxy_1_8<Object>(ClassInstanceMultiMapHandle.createHandle(nmsEntitySliceHandle));
        }
    }

    @ConverterMethod(output="net.minecraft.util.ClassInstanceMultiMap<net.minecraft.world.entity.Entity>", optional=true)
    public static Object cbListToEntitySlice(List<?> entitySliceList) {
        if (entitySliceList instanceof EntitySliceProxy_1_8_3) {
            return ((EntitySliceProxy_1_8_3<?>) entitySliceList).getHandle().getRaw();
        }
        if (entitySliceList instanceof EntitySliceProxy_1_8) {
            return ((EntitySliceProxy_1_8<?>) entitySliceList).getHandle().getRaw();
        }
        ClassInstanceMultiMapHandle entitySlice = ClassInstanceMultiMapHandle.createNew(EntityHandle.T.getType());
        for (Object value : entitySliceList) {
            entitySlice.add(value);
        }
        return entitySlice.getRaw();
    }

    @ConverterMethod(output="net.minecraft.world.level.levelgen.Heightmap")
    public static Object toHeightMapHandle(HeightMap heightmap) {
        return heightmap.getRawHandle();
    }

    @ConverterMethod
    public static EntityTypeHandle toEntityTypesHandleWrapperFromEntityClass(Class<?> entityClass) {
        return CommonEntityType.getNMSEntityTypeByEntityClass(entityClass);
    }

    @ConverterMethod(output="net.minecraft.world.entity.EntityType", optional=true)
    public static Object toEntityTypesHandleFromEntityClass(Class<?> entityClass) {
        return Template.Handle.getRaw(toEntityTypesHandleWrapperFromEntityClass(entityClass));
    }

    @ConverterMethod(input="net.minecraft.world.level.block.state.BlockState", output="org.bukkit.block.data.BlockData", optional=true)
    public static Object bukkitBlockDataFromIBlockData(Object nmsIBlockdataHandle) {
        return CraftBlockDataHandle.T.fromData.raw.invoke(nmsIBlockdataHandle);
    }

    @ConverterMethod(output="org.bukkit.block.data.BlockData", optional=true)
    public static Object bukkitBlockDataFromBlockData(BlockData blockData) {
        return CraftBlockDataHandle.T.fromData.invoke(blockData);
    }

    @ConverterMethod(input="List<net.minecraft.world.phys.AABB>", output="net.minecraft.world.phys.shapes.VoxelShape", optional=true)
    public static Object voxelShapeFromAxisAlignedBBList(List<?> axisAlignedBBHandles) {
        return VoxelShapeHandle.createRawFromAABB(axisAlignedBBHandles);
    }

    @ConverterMethod(output="net.minecraft.ChatFormatting")
    public static Object chatColorToEnumChatFormatHandle(ChatColor color) {
        return ChatFormattingHandle.byChar(color.getChar()).getRaw();
    }

    @ConverterMethod
    public static int chatColorToEnumChatFormatIndex(ChatColor color) {
        return ChatFormattingHandle.byChar(color.getChar()).getId();
    }

    @ConverterMethod
    public static String artToInternalName(org.bukkit.Art art) {
        return CraftArtHandle.NotchToInternalName(CraftArtHandle.BukkitToNotch(art));
    }

    @ConverterMethod
    public static int artToInternalId(org.bukkit.Art art) {
        return CraftArtHandle.NotchToInternalId(CraftArtHandle.BukkitToNotch(art));
    }

    @ConverterMethod
    public static DimensionType dimensionFromId(int dimensionId) {
        return DimensionType.fromId(dimensionId);
    }

    @ConverterMethod
    public static int dimensionToId(DimensionType dimension) {
        return dimension.getId();
    }

    @ConverterMethod(input="net.minecraft.world.level.dimension.DimensionType")
    public static DimensionType dimensionFromDimensionManager(Object nmsDimensionManagerHandle) {
        return DimensionType.fromDimensionManagerHandle(nmsDimensionManagerHandle);
    }

    @ConverterMethod(output="net.minecraft.world.level.dimension.DimensionType")
    public static Object dimensionManagerFromDimension(DimensionType dimension) {
        return dimension.getDimensionManagerHandle();
    }

    @ConverterMethod(input="List<net.minecraft.world.phys.AABB>")
    public static java.util.stream.Stream<VoxelShapeHandle> axisAlignedBBListToVoxelShapeStream(List<?> axisAlignedBBList) {
        return MountiplexUtil.toStream(VoxelShapeHandle.createHandle(voxelShapeFromAxisAlignedBBList(axisAlignedBBList)));
    }

    // Since Minecraft 1.9
    @ConverterMethod(output="net.minecraft.world.InteractionResult", optional = true)
    public static Object nmsEnumInteractionResultFromInteractionResult(InteractionResult result) {
        return result.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.core.particles.ParticleType")
    public static Object toParticleHandle(ParticleType<?> particleType) {
        return particleType.getRawHandle();
    }
}
