package com.bergerkiller.bukkit.common.conversion.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.block.Block;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.map.MapCursor;
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
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatMessageType;
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
import com.bergerkiller.bukkit.common.wrappers.ScoreboardAction;
import com.bergerkiller.generated.net.minecraft.EnumChatFormatHandle;
import com.bergerkiller.generated.net.minecraft.core.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.core.NonNullListHandle;
import com.bergerkiller.generated.net.minecraft.core.Vector3fHandle;
import com.bergerkiller.generated.net.minecraft.network.chat.ChatMessageTypeHandle;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.shapes.VoxelShapeHandle;
import com.bergerkiller.generated.net.minecraft.sounds.SoundCategoryHandle;
import com.bergerkiller.generated.net.minecraft.util.EntitySliceHandle;
import com.bergerkiller.generated.net.minecraft.world.EnumDifficultyHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectListHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypesHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EnumMainHandHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.item.crafting.RecipeItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.level.ChunkCoordIntPairHandle;
import com.bergerkiller.generated.net.minecraft.world.level.EnumGamemodeHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.net.minecraft.world.level.saveddata.maps.MapIconHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.Vec3DHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftArtHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftChunkHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftWorldHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.data.CraftBlockDataHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.entity.CraftEntityHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.potion.CraftPotionUtilHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;

public class HandleConversion {

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

    @ConverterMethod(output="net.minecraft.server.level.WorldServer")
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

    @ConverterMethod(output="net.minecraft.world.level.chunk.Chunk")
    public static Object toChunkHandle(org.bukkit.Chunk chunk) {
        try {
            return CraftChunkHandle.T.getHandle.invoker.invoke(chunk);
        } catch (RuntimeException ex) {
            if (CraftChunkHandle.T.isAssignableFrom(chunk)) {
                throw ex;
            } else {
                return null;
            }
        }
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
                    handle.setDurability(itemStack.getDurability());
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

    @ConverterMethod(output="T extends net.minecraft.world.level.block.entity.TileEntity")
    public static Object toTileEntityHandle(org.bukkit.block.BlockState blockState) {
        return BlockStateConversion.INSTANCE.blockStateToTileEntity(blockState);
    }

    @ConverterMethod(output="T extends net.minecraft.world.level.block.entity.TileEntity")
    public static Object getTileEntityHandle(org.bukkit.block.Block block) {
        Object blockPosition = BlockPositionHandle.createNew(block.getX(), block.getY(), block.getZ());
        return WorldHandle.T.getTileEntity.raw.invoke(toWorldHandle(block.getWorld()), blockPosition);
    }

    @ConverterMethod(output="net.minecraft.world.IInventory")
    public static Object toIInventoryHandle(InventoryBase inventory) {
        return inventory.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.world.IInventory")
    public static Object toIInventoryHandle(org.bukkit.inventory.Inventory inventory) {
        if (CraftInventoryHandle.T.isAssignableFrom(inventory)) {
            return CraftInventoryHandle.T.getHandle.raw.invoke(inventory);
        } else {
            return null;
        }
    }

    @ConverterMethod(output="net.minecraft.network.syncher.DataWatcher")
    public static Object toDataWatcherHandle(com.bergerkiller.bukkit.common.wrappers.DataWatcher dataWatcher) {
        return dataWatcher.getRawHandle();
    }

    @ConverterMethod(input="net.minecraft.world.entity.Entity", output="net.minecraft.network.syncher.DataWatcher")
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

    @ConverterMethod(output="T extends net.minecraft.world.level.block.state.IBlockData")
    public static Object toIBlockDataHandle(com.bergerkiller.bukkit.common.wrappers.BlockData blockData) {
        return blockData.getData();
    }

    @ConverterMethod(input="net.minecraft.world.level.block.Block", output="T extends net.minecraft.world.level.block.state.IBlockData")
    public static Object toIBlockDataHandleFromBlock(Object nmsBlockHandle) {
        return BlockHandle.T.getBlockData.raw.invoke(nmsBlockHandle);
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod(output="net.minecraft.world.level.EnumGamemode")
    public static Object toEnumGamemodeHandle(org.bukkit.GameMode gameMode) {
        return EnumGamemodeHandle.T.getById.raw.invoke(gameMode.getValue());
    }

    @ConverterMethod(input="org.bukkit.inventory.MainHand", output="net.minecraft.world.EnumHand", optional=true)
    public static Object mainHandToEnumHandHandle(Object mainHand) {
        return HumanHand.fromMainHand(mainHand).toNMSEnumHand(null);
    }

    @ConverterMethod(output="net.minecraft.world.EnumHand", optional=true)
    public static Object humanHandToEnumHandHandle(HumanHand hand) {
        return hand.toNMSEnumHand(null);
    }

    @ConverterMethod(output="org.bukkit.inventory.MainHand", optional=true)
    public static Object humanHandToMainHandHandle(HumanHand hand) {
        return hand.toMainHand();
    }

    @ConverterMethod(output="net.minecraft.world.entity.EnumMainHand", optional=true)
    public static Object humanHandToEnumMainHandHandle(HumanHand hand) {
        if (hand == HumanHand.LEFT) {
            return EnumMainHandHandle.LEFT.getRaw();
        } else {
            return EnumMainHandHandle.RIGHT.getRaw();
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

    @ConverterMethod(output="net.minecraft.world.level.ChunkCoordIntPair")
    public static Object toChunkCoordIntPairHandle(IntVector2 intVector2) {
        return ChunkCoordIntPairHandle.T.fromIntVector2Raw.invoker.invoke(null, intVector2);
    }

    @ConverterMethod(output="net.minecraft.core.BlockPosition")
    public static Object toBlockPositionHandle(IntVector3 intVector3) {
        return BlockPositionHandle.T.fromIntVector3Raw.invoker.invoke(null, intVector3);
    }

    @ConverterMethod(output="net.minecraft.core.BlockPosition")
    public static Object toBlockPositionHandle(Block block) {
        return BlockPositionHandle.T.fromBukkitBlockRaw.invoker.invoke(null, block);
    }

    @ConverterMethod(output="net.minecraft.world.phys.Vec3D")
    public static Object toVec3DHandle(Vector vector) {
        return Vec3DHandle.T.fromBukkitRaw.invoker.invoke(null, vector);
    }

    @ConverterMethod(output="net.minecraft.core.Vector3f")
    public static Object toVector3fHandle(Vector vector) {
        return Vector3fHandle.T.fromBukkitRaw.invoker.invoke(null, vector);
    }

    @ConverterMethod(output="net.minecraft.world.entity.player.PlayerAbilities")
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

    @ConverterMethod(output="net.minecraft.world.EnumDifficulty")
    public static Object toEnumDifficultyHandle(Integer difficultyId) {
        return EnumDifficultyHandle.T.getById.raw.invoke(difficultyId);
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod(output="net.minecraft.world.EnumDifficulty")
    public static Object toEnumDifficultyHandle(Difficulty difficulty) {
        return EnumDifficultyHandle.T.getById.raw.invoke(difficulty.getValue());
    }

    @ConverterMethod(output="net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore.EnumScoreboardAction")
    public static Object toEnumScoreboardActionHandle(ScoreboardAction action) {
        return action.getHandle();
    }

    @ConverterMethod(output="net.minecraft.world.level.chunk.ChunkSection")
    public static Object toChunkSectionHandle(ChunkSection section) {
        return section.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.world.effect.MobEffectList")
    public static Object toMobEffectListHandle(PotionEffectType potionEffectType) {
        @SuppressWarnings("deprecation")
        int id = potionEffectType.getId();

        return MobEffectListHandle.T.fromId.invoke(id);
    }

    @ConverterMethod(output="net.minecraft.world.effect.MobEffect")
    public static Object toMobEffectHandle(PotionEffect potionEffect) {
        return CraftPotionUtilHandle.fromBukkit(potionEffect);
    }

    @ConverterMethod(output="net.minecraft.world.level.MobSpawnerAbstract")
    public static Object toMobSpawnerAbstractHandle(MobSpawner mobSpawner) {
        return mobSpawner.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.network.syncher.DataWatcherObject<V>")
    public static Object toDataWatcherObjectHandle(DataWatcher.Key<?> keyWrapper) {
        return keyWrapper.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.network.syncher.DataWatcher.Item<V>")
    public static Object toDataWatcherItemHandle(DataWatcher.Item<?> itemWrapper) {
        return itemWrapper.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.resources.ResourceKey<V>")
    public static Object toResourceKeyHandle(ResourceKey<?> resourceKeyWrapper) {
        return resourceKeyWrapper.getRawHandle();
    }

    @ConverterMethod(output="net.minecraft.world.level.saveddata.maps.MapIcon")
    public static Object toMapIconHandle(MapCursor cursor) {
        return MapIconHandle.fromCursor(cursor).getRaw();
    }

    @ConverterMethod
    public static String worldTypeToString(org.bukkit.WorldType worldType) {
        return worldType.name();
    }

    @ConverterMethod(output="net.minecraft.network.chat.IChatBaseComponent")
    public static Object toChatBaseComponent(ChatText text) {
        return text.getRawHandle();
    }

    @ConverterMethod
    public static String getChatTextMessage(ChatText text) {
        return text.getMessage();
    }

    @ConverterMethod(output="net.minecraft.world.entity.EnumItemSlot")
    public static Object toEnumItemSlotHandle(EquipmentSlot equipmentSlot) {
        return ItemSlotConversion.getEnumItemSlot(equipmentSlot);
    }

    @ConverterMethod(output="net.minecraft.network.chat.ChatMessageType", optional=true)
    public static Object toChatMessageTypeHandle(ChatMessageType chatMessageType) {
        return ChatMessageTypeHandle.getRawById(chatMessageType.getId());
    }

    @ConverterMethod()
    public static byte getChatMessageTypeId(ChatMessageType chatMessageType) {
        return chatMessageType.getId();
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
    @ConverterMethod(output="net.minecraft.world.item.crafting.RecipeItemStack", optional=true)
    public static Object toRecipeItemStackHandle(CraftInputSlot slot) {
        return RecipeItemStackHandle.createRawRecipeItemStack(Arrays.asList(slot.getChoices()));
    }

    @ConverterMethod(output="net.minecraft.resources.MinecraftKey")
    public static Object getMinecraftKeyFromName(String name) {
        return MinecraftKeyHandle.T.createNew.raw.invoke(name);
    }

    @ConverterMethod(input="net.minecraft.resources.MinecraftKey")
    public static String getNameFromMinecraftKey(Object minecraftKeyHandle) {
        return minecraftKeyHandle.toString();
    }

    @ConverterMethod(input="net.minecraft.sounds.SoundCategory", optional=true)
    public static String getNameFromSoundCategory(Object soundCategoryHandle) {
        return SoundCategoryHandle.T.getName.invoker.invoke(soundCategoryHandle);
    }

    @ConverterMethod(output="net.minecraft.sounds.SoundCategory", optional=true)
    public static Object getSoundCategoryFromName(String soundCategoryName) {
        Object result = SoundCategoryHandle.T.byName.raw.invoke(soundCategoryName);
        if (result == null) {
            result = SoundCategoryHandle.T.byName.raw.invoke("master");
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

    @ConverterMethod(input="net.minecraft.util.EntitySlice<?>", optional=true)
    public static List<Object> cbEntitySliceToList(Object nmsEntitySliceHandle) {
        if (CommonCapabilities.REVISED_CHUNK_ENTITY_SLICE) {
            return new EntitySliceProxy_1_8_3<Object>(EntitySliceHandle.createHandle(nmsEntitySliceHandle));
        } else {
            return new EntitySliceProxy_1_8<Object>(EntitySliceHandle.createHandle(nmsEntitySliceHandle));
        }
    }

    @ConverterMethod(output="net.minecraft.util.EntitySlice<net.minecraft.world.entity.Entity>", optional=true)
    public static Object cbListToEntitySlice(List<?> entitySliceList) {
        if (entitySliceList instanceof EntitySliceProxy_1_8_3) {
            return ((EntitySliceProxy_1_8_3<?>) entitySliceList).getHandle().getRaw();
        }
        if (entitySliceList instanceof EntitySliceProxy_1_8) {
            return ((EntitySliceProxy_1_8<?>) entitySliceList).getHandle().getRaw();
        }
        EntitySliceHandle entitySlice = EntitySliceHandle.createNew(EntityHandle.T.getType());
        for (Object value : entitySliceList) {
            entitySlice.add(value);
        }
        return entitySlice.getRaw();
    }

    @ConverterMethod(output="net.minecraft.world.level.levelgen.HeightMap")
    public static Object toHeightMapHandle(HeightMap heightmap) {
        return heightmap.getRawHandle();
    }

    @ConverterMethod
    public static EntityTypesHandle toEntityTypesHandleWrapperFromEntityClass(Class<?> entityClass) {
        return CommonEntityType.getNMSEntityTypeByEntityClass(entityClass);
    }

    @ConverterMethod(output="net.minecraft.world.entity.EntityTypes", optional=true)
    public static Object toEntityTypesHandleFromEntityClass(Class<?> entityClass) {
        return toEntityTypesHandleWrapperFromEntityClass(entityClass).getRaw();
    }

    @ConverterMethod(input="net.minecraft.world.level.block.state.IBlockData", output="org.bukkit.block.data.BlockData", optional=true)
    public static Object bukkitBlockDataFromIBlockData(Object nmsIBlockdataHandle) {
        return CraftBlockDataHandle.T.fromData.raw.invoke(nmsIBlockdataHandle);
    }

    @ConverterMethod(output="org.bukkit.block.data.BlockData", optional=true)
    public static Object bukkitBlockDataFromBlockData(BlockData blockData) {
        return CraftBlockDataHandle.T.fromData.invoke(blockData);
    }

    @ConverterMethod(input="List<net.minecraft.world.phys.AxisAlignedBB>", output="net.minecraft.world.phys.shapes.VoxelShape", optional=true)
    public static Object voxelShapeFromAxisAlignedBBList(List<?> axisAlignedBBHandles) {
        return VoxelShapeHandle.createRawFromAABB(axisAlignedBBHandles);
    }

    @ConverterMethod(output="net.minecraft.EnumChatFormat")
    public static Object chatColorToEnumChatFormatHandle(ChatColor color) {
        return EnumChatFormatHandle.byChar(color.getChar()).getRaw();
    }

    @ConverterMethod
    public static int chatColorToEnumChatFormatIndex(ChatColor color) {
        return EnumChatFormatHandle.byChar(color.getChar()).getId();
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

    @ConverterMethod(input="net.minecraft.world.level.dimension.DimensionManager")
    public static DimensionType dimensionFromDimensionManager(Object nmsDimensionManagerHandle) {
        return DimensionType.fromDimensionManagerHandle(nmsDimensionManagerHandle);
    }

    @ConverterMethod(output="net.minecraft.world.level.dimension.DimensionManager")
    public static Object dimensionManagerFromDimension(DimensionType dimension) {
        return dimension.getDimensionManagerHandle();
    }

    @ConverterMethod(input="List<net.minecraft.world.phys.AxisAlignedBB>")
    public static java.util.stream.Stream<VoxelShapeHandle> axisAlignedBBListToVoxelShapeStream(List<?> axisAlignedBBList) {
        return MountiplexUtil.toStream(VoxelShapeHandle.createHandle(voxelShapeFromAxisAlignedBBList(axisAlignedBBList)));
    }

    // Since Minecraft 1.9
    @ConverterMethod(output="net.minecraft.server.EnumInteractionResult", optional = true)
    public static Object nmsEnumInteractionResultFromInteractionResult(InteractionResult result) {
        return result.getRawHandle();
    }
}
