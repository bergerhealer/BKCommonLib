package com.bergerkiller.bukkit.common.conversion.type;

import java.util.List;
import java.util.function.Consumer;

import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.CustomModelData;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.bukkit.common.wrappers.PlayerRespawnPoint;
import com.bergerkiller.bukkit.common.wrappers.PlayerRespawnPointNearBlock;
import com.bergerkiller.generated.net.minecraft.nbt.CompoundTagHandle;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeHandle;
import com.bergerkiller.generated.net.minecraft.world.inventory.AbstractContainerMenuHandle;
import com.bergerkiller.generated.net.minecraft.world.item.component.CustomModelDataHandle;
import com.bergerkiller.generated.net.minecraft.world.level.GameTypeHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.BlockEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle;
import com.bergerkiller.generated.net.minecraft.world.level.levelgen.HeightmapHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.Vec3Handle;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.proxy.NBTTagCompoundConsumer;
import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.bukkit.common.resources.ParticleType;
import com.bergerkiller.bukkit.common.resources.ResourceCategory;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockStateChange;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
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
import com.bergerkiller.generated.net.minecraft.core.Vec3iHandle;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.core.RotationsHandle;
import com.bergerkiller.generated.net.minecraft.network.syncher.SynchedEntityDataHandle;
import com.bergerkiller.generated.net.minecraft.sounds.SoundEventHandle;
import com.bergerkiller.generated.net.minecraft.world.DifficultyHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectInstanceHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypeHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EquipmentSlotHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.HumanoidArmHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.item.crafting.IngredientHandle;
import com.bergerkiller.generated.net.minecraft.world.level.ChunkPosHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftArtHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.data.CraftBlockDataHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryBeaconHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryBrewerHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryCraftingHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryFurnaceHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryMerchantHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryPlayerHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;

public class WrapperConversion {

    @ConverterMethod(input="net.minecraft.server.level.ServerPlayer.RespawnConfig", acceptsNull = true)
    public static PlayerRespawnPoint toRespawnPoint(Object respawnConfig) {
        if (respawnConfig == null) {
            return PlayerRespawnPoint.NONE;
        } else {
            return new PlayerRespawnPointNearBlock(ServerPlayerHandle.RespawnConfigHandle.createHandle(respawnConfig));
        }
    }

    @ConverterMethod(output="net.minecraft.server.level.ServerPlayer.RespawnConfig")
    public static Object toRespawnConfigFromPoint(PlayerRespawnPoint respawnPoint) {
        if (respawnPoint instanceof PlayerRespawnPointNearBlock) {
            return ((PlayerRespawnPointNearBlock) respawnPoint).getHandle().getRaw();
        } else {
            return null;
        }
    }

    @ConverterMethod(input="net.minecraft.world.item.component.CustomModelData")
    public static CustomModelData handleToCustomModelData(Object handle) {
        return new CustomModelData(CustomModelDataHandle.createHandle(handle));
    }

    @ConverterMethod(output="net.minecraft.world.item.component.CustomModelData")
    public static Object customModelDataToHandle(CustomModelData cmd) {
        return cmd.getRawHandle();
    }

    @ConverterMethod(output="java.util.function.Consumer<net.minecraft.nbt.CompoundTag>")
    public static Consumer<Object> createNBTTagCompoundConsumer(Consumer<CommonTagCompound> consumer) {
        return new NBTTagCompoundConsumer(consumer);
    }

    // Plugs a gap 1.8 - 1.16 where some fields used IAttribute (but were really AttributeBase)
    @ConverterMethod(input="net.minecraft.world.entity.ai.attributes.IAttribute", optional=true)
    public static Holder<AttributeHandle> iAttributeToHolder(Object nmsIAttribute) {
        return attributeBaseToHolder(nmsIAttribute);
    }

    @ConverterMethod(input="net.minecraft.world.entity.ai.attributes.Attribute")
    public static Holder<AttributeHandle> attributeBaseToHolder(Object nmsAttributeBase) {
        return Holder.directWrap(nmsAttributeBase, AttributeHandle::createHandle);
    }

    @ConverterMethod(output="net.minecraft.world.entity.ai.attributes.Attribute")
    public static Object holderToAttributeBase(Holder<AttributeHandle> attributeHolder) {
        return attributeHolder.rawValue();
    }

    @ConverterMethod
    public static Holder<MobEffectHandle> holderFromBukkit(org.bukkit.potion.PotionEffectType effectType) {
        return null;
    }

    @ConverterMethod
    public static org.bukkit.potion.PotionEffectType holderToBukkit(Holder<MobEffectHandle> MobEffectList) {
        return null;
    }

    @ConverterMethod(input="net.minecraft.world.effect.MobEffect")
    public static Holder<MobEffectHandle> mobEffectListToHolder(Object nmsMobEffectList) {
        return Holder.directWrap(nmsMobEffectList, MobEffectHandle::createHandle);
    }

    @ConverterMethod(output="net.minecraft.world.effect.MobEffect")
    public static Object holderToMobEffectList(Holder<MobEffectHandle> holder) {
        return holder.rawValue();
    }

    @ConverterMethod(input="net.minecraft.nbt.CompoundTag")
    public static BlockStateChange deserializeBlockStateChange(Object nmsNBTTagCompoundMetadataHandle) {
        return BlockStateChange.fromMetadataPacked(CommonTagCompound.create(
                CompoundTagHandle.createHandle(nmsNBTTagCompoundMetadataHandle)));
    }

    @ConverterMethod(input="net.minecraft.world.level.block.entity.BlockEntityType")
    public static BlockStateType toBlockStateType(Object nmsTileEntityTypesHandle) {
        return BlockStateType.fromTileEntityTypesHandle(nmsTileEntityTypesHandle);
    }

    @ConverterMethod
    public static BlockStateType tileEntityTypesIdToBlockStateType(int id) {
        return BlockStateType.bySerializedId(id);
    }

    @ConverterMethod(input="net.minecraft.resources.Identifier")
    public static BlockStateType tileEntityTypesKeyToBlockStateType(Object nmsMinecraftKeyHandle) {
        return BlockStateType.byKey(IdentifierHandle.createHandle(nmsMinecraftKeyHandle));
    }

    @ConverterMethod(input="net.minecraft.world.entity.Entity", output="T extends org.bukkit.entity.Entity")
    public static org.bukkit.entity.Entity toEntity(Object nmsEntityHandle) {
        return EntityHandle.T.getBukkitEntity.invoker.invoke(nmsEntityHandle);
    }

    @ConverterMethod(input="net.minecraft.world.level.Level")
    public static org.bukkit.World toWorld(Object nmsWorldHandle) {
        return (org.bukkit.World) LevelHandle.T.getWorld.raw.invoke(nmsWorldHandle);
    }

    @ConverterMethod
    public static org.bukkit.World getWorld(Location location) {
        return location.getWorld();
    }

    @ConverterMethod
    public static org.bukkit.World getWorld(org.bukkit.block.Block block) {
        return block.getWorld();
    }

    @ConverterMethod
    public static org.bukkit.World getWorld(org.bukkit.block.BlockState blockState) {
        return blockState.getWorld();
    }

    @ConverterMethod
    public static org.bukkit.World getWorld(org.bukkit.entity.Entity entity) {
        return entity.getWorld();
    }

    @ConverterMethod(input="net.minecraft.world.level.block.entity.BlockEntity")
    public static org.bukkit.World getWorldFromTileEntity(Object nmsTileEntityHandle) {
        return toWorld(BlockEntityHandle.T.getWorld.raw.invoke(nmsTileEntityHandle));
    }

    @ConverterMethod(input="net.minecraft.world.level.chunk.LevelChunk")
    public static org.bukkit.Chunk toChunk(Object nmsChunkHandle) {
        return LevelChunkHandle.T.getBukkitChunk.invoker.invoke(nmsChunkHandle);
    }

    @ConverterMethod
    public static org.bukkit.Chunk getChunk(org.bukkit.block.Block block) {
        return block.getChunk();
    }

    @ConverterMethod
    public static org.bukkit.Chunk getChunk(org.bukkit.block.BlockState blockState) {
        return blockState.getChunk();
    }

    @ConverterMethod
    public static org.bukkit.Chunk getChunk(org.bukkit.Location location) {
        return location.getChunk();
    }

    @ConverterMethod
    public static org.bukkit.block.Block getBlock(org.bukkit.Location location) {
        return location.getBlock();
    }

    @ConverterMethod
    public static org.bukkit.block.Block getBlock(org.bukkit.block.BlockState blockState) {
        return blockState.getBlock();
    }

    @ConverterMethod(input="net.minecraft.world.level.block.entity.BlockEntity")
    public static org.bukkit.block.Block getBlockFromTileEntity(Object nmsTileEntityHandle) {
        BlockEntityHandle handle = BlockEntityHandle.createHandle(nmsTileEntityHandle);
        BlockPosHandle pos = handle.getPosition();
        return handle.getWorld().getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod(input="net.minecraft.world.level.block.entity.BlockEntity")
    public static org.bukkit.block.BlockState toBlockState(Object nmsTileEntityHandle) {
        return BlockStateConversion.INSTANCE.tileEntityToBlockState(nmsTileEntityHandle);
    }

    @ConverterMethod
    public static org.bukkit.block.BlockState getBlockState(org.bukkit.block.Block block) {
        return BlockStateConversion.INSTANCE.blockToBlockState(block);
    }

    @ConverterMethod(input="net.minecraft.network.syncher.SynchedEntityData")
    public static com.bergerkiller.bukkit.common.wrappers.DataWatcher toDataWatcher(Object nmsDataWatcherHandle) {
        return com.bergerkiller.bukkit.common.wrappers.DataWatcher.createForHandle(nmsDataWatcherHandle);
    }

    @Deprecated
    @ConverterMethod
    public static org.bukkit.Material getMaterialFromId(Number materialId) {
        return CommonLegacyMaterials.getMaterialFromId(materialId.intValue());
    }

    @ConverterMethod
    public static org.bukkit.Material getMaterialFromBlock(org.bukkit.block.Block block) {
        return block.getType();
    }

    @ConverterMethod(input="net.minecraft.world.item.Item")
    public static org.bukkit.Material toMaterialFromItemHandle(Object nmsItemHandle) {
        return CraftMagicNumbersHandle.getMaterialFromItem(nmsItemHandle);
    }

    @ConverterMethod(input="net.minecraft.world.level.block.Block")
    public static org.bukkit.Material toMaterialFromBlockHandle(Object nmsBlockHandle) {
        return CraftMagicNumbersHandle.getMaterialFromBlock(nmsBlockHandle);
    }

    @ConverterMethod
    public static org.bukkit.Material parseMaterial(String text) {
        return ParseUtil.parseMaterial(text, null);
    }

    @ConverterMethod(input="net.minecraft.world.item.ItemStack", acceptsNull = true)
    public static org.bukkit.inventory.ItemStack toItemStack(Object nmsItemStackHandle) {
        if (nmsItemStackHandle == null || ItemStackHandle.T.getTypeField.raw.invoke(nmsItemStackHandle) == null) {
            return null;
        } else {
            return CraftItemStackHandle.asCraftMirror(nmsItemStackHandle);
        }
    }

    @ConverterMethod
    public static org.bukkit.inventory.ItemStack parseItemStack(String text) {
        return ItemParser.parse(text).getItemStack();
    }

    @ConverterMethod(input="net.minecraft.world.inventory.CraftingContainer")
    public static CraftingInventory toCraftingInventory(Object nmsInventoryCraftingHandle) {
        return CraftInventoryCraftingHandle.createNew(nmsInventoryCraftingHandle, null);
    }

    @ConverterMethod(input="net.minecraft.world.entity.player.Inventory")
    public static org.bukkit.inventory.PlayerInventory toPlayerInventory(Object nmsPlayerInventoryHandle) {
        return CraftInventoryPlayerHandle.createNew(nmsPlayerInventoryHandle);
    }

    @ConverterMethod(input="net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity")
    public static org.bukkit.inventory.FurnaceInventory toFurnaceInventory(Object nmsTileEntityFurnaceHandle) {
        return CraftInventoryFurnaceHandle.createNew(nmsTileEntityFurnaceHandle);
    }

    @ConverterMethod(input="net.minecraft.world.level.block.entity.BrewingStandBlockEntity")
    public static org.bukkit.inventory.BrewerInventory toBrewerInventory(Object nmsTileEntityBrewingStandHandle) {
        return CraftInventoryBrewerHandle.createNew(nmsTileEntityBrewingStandHandle);
    }

    @ConverterMethod(input="net.minecraft.world.inventory.MerchantContainer")
    public static org.bukkit.inventory.MerchantInventory toMerchantInventory(Object nmsInventoryMerchantHandle) {
        return CraftInventoryMerchantHandle.createNew(nmsInventoryMerchantHandle);
    }

    @ConverterMethod(input="net.minecraft.world.level.block.entity.BeaconBlockEntity")
    public static org.bukkit.inventory.BeaconInventory toBeaconInventory(Object nmsTileEntityBeaconHandle) {
        return CraftInventoryBeaconHandle.createNew(nmsTileEntityBeaconHandle);
    }

    @ConverterMethod(input="net.minecraft.world.Container")
    public static org.bukkit.inventory.Inventory toInventory(Object nmsIInventoryHandle) {
        return CraftInventoryHandle.createNew(nmsIInventoryHandle);
    }

    @ConverterMethod(input="net.minecraft.world.inventory.AbstractContainerMenu")
    public static org.bukkit.inventory.InventoryView toInventoryView(Object nmsContainerHandle) {
        return AbstractContainerMenuHandle.T.getBukkitView.invoker.invoke(nmsContainerHandle);
    }

    @ConverterMethod
    public static org.bukkit.inventory.Inventory toInventory(org.bukkit.inventory.InventoryView inventoryView) {
        return inventoryView.getTopInventory();
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod(input="net.minecraft.world.Difficulty")
    public static org.bukkit.Difficulty toDifficulty(Object nmsEnumDifficultyHandle) {
        Integer id = DifficultyHandle.T.getId.invoker.invoke(nmsEnumDifficultyHandle);
        return Difficulty.getByValue(id.intValue());
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod
    public static org.bukkit.Difficulty fromId(Number id) {
        return Difficulty.getByValue(id.intValue());
    }

    @ConverterMethod
    public static org.bukkit.Difficulty parseDifficulty(String text) {
        return ParseUtil.parseEnum(Difficulty.class, text, null);
    }

    @ConverterMethod
    public static org.bukkit.WorldType parseWorldType(String text) {
        return ParseUtil.parseEnum(org.bukkit.WorldType.class, text, null);
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod(input="net.minecraft.world.level.GameType")
    public static org.bukkit.GameMode toGameMode(Object nmsEnumGamemodeHandle) {
        return org.bukkit.GameMode.getByValue(GameTypeHandle.createHandle(nmsEnumGamemodeHandle).getId());
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod
    public static org.bukkit.GameMode getGameModeById(Number id) {
        return org.bukkit.GameMode.getByValue(id.intValue());
    }
    
    @ConverterMethod
    public static org.bukkit.GameMode parseGameMode(String text) {
        return ParseUtil.parseEnum(org.bukkit.GameMode.class, text, null);
    }

    @ConverterMethod(input="net.minecraft.world.InteractionHand", output="org.bukkit.inventory.MainHand", optional=true)
    public static Object fromEnumHandToMainHand(Object nmsEnumHandHandle) {
        return HumanHand.fromNMSEnumHand(null, nmsEnumHandHandle).toMainHand();
    }

    @ConverterMethod(input="net.minecraft.world.InteractionHand", optional=true)
    public static HumanHand fromEnumHandToHumanHand(Object nmsEnumHandHandle) {
        return HumanHand.fromNMSEnumHand(null, nmsEnumHandHandle);
    }

    @ConverterMethod(input="org.bukkit.inventory.MainHand", optional=true)
    public static HumanHand fromMainHandToHumanHand(Object mainHand) {
        return HumanHand.fromMainHand(mainHand);
    }

    @ConverterMethod(input="net.minecraft.world.entity.HumanoidArm", optional=true)
    public static HumanHand humanHandToEnumMainHandHandle(Object nmsEnumMainHandHandle) {
        if (nmsEnumMainHandHandle == HumanoidArmHandle.LEFT.getRaw()) {
            return HumanHand.LEFT;
        } else {
            return HumanHand.RIGHT;
        }
    }

    @ConverterMethod(input="net.minecraft.network.protocol.Packet")
    public static CommonPacket toCommonPacket(Object nmsPacketHandle) {
        return new CommonPacket(nmsPacketHandle);
    }

    @ConverterMethod(input="net.minecraft.world.level.ChunkPos")
    public static IntVector2 toIntVector2(Object nmsChunkCoordIntPairHandle) {
        return ChunkPosHandle.T.toIntVector2.invoker.invoke(nmsChunkCoordIntPairHandle);
    }

    @ConverterMethod(input="net.minecraft.core.BlockPos")
    public static IntVector3 toIntVector3(Object nmsBlockPositionHandle) {
        return Vec3iHandle.T.toIntVector3.invoker.invoke(nmsBlockPositionHandle);
    }

    @ConverterMethod
    public static IntVector3 toIntVector3FromVector(Vector vector) {
        return new IntVector3(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    @ConverterMethod(input="net.minecraft.world.phys.Vec3")
    public static Vector toVector(Object nmsVec3DHandle) {
        return Vec3Handle.T.toBukkit.invoker.invoke(nmsVec3DHandle);
    }

    @ConverterMethod(input="net.minecraft.core.Rotations")
    public static Vector fromVector3fToVector(Object nmsVector3fHandle) {
        RotationsHandle handle = RotationsHandle.createHandle(nmsVector3fHandle);
        return new Vector(handle.getX(), handle.getY(), handle.getZ());
    }

    @ConverterMethod
    public static Vector toVectorFromLocation(Location location) {
        return new Vector(location.getX(), location.getY(), location.getZ());
    }

    @ConverterMethod
    public static Vector toVectorFromIntVector3(IntVector3 vector) {
        return new Vector(vector.x, vector.y, vector.z);
    }

    @ConverterMethod(input="net.minecraft.world.entity.player.Abilities")
    public static PlayerAbilities toPlayerAbilities(Object nmsPlayerAbilitiesHandle) {
        return new PlayerAbilities(nmsPlayerAbilitiesHandle);
    }

    @ConverterMethod(input="net.minecraft.server.level.EntityTracker")
    public static EntityTracker toEntityTracker(Object nmsEntityTrackerHandle) {
        return new EntityTracker(nmsEntityTrackerHandle);
    }

    @ConverterMethod(input="com.bergerkiller.bukkit.common.internal.LongHashSet")
    public static LongHashSet toLongHashSet(Object cbLongHashSetHandle) {
        return new LongHashSet(cbLongHashSetHandle);
    }

    @ConverterMethod(input="net.minecraft.util.IntHashMap<T>")
    public static <T> IntHashMap<T> toIntHashMap(Object nmsIntHashMapHandle) {
        return new IntHashMap<T>(nmsIntHashMapHandle);
    }

    @ConverterMethod(input="net.minecraft.util.IntHashMap<?>")
    public static IntHashMap<Object> toRawIntHashMap(Object nmsIntHashMapHandle) {
        return new IntHashMap<Object>(nmsIntHashMapHandle);
    }

    @ConverterMethod(input="net.minecraft.world.level.block.state.BlockState")
    public static BlockData toBlockData(Object nmsIBlockDataHandle) {
        return BlockData.fromBlockData(nmsIBlockDataHandle);
    }

    @ConverterMethod(input="net.minecraft.world.level.block.Block")
    public static BlockData toBlockDataFromBlock(Object nmsBlockHandle) {
        return BlockData.fromBlock(nmsBlockHandle);
    }

    @ConverterMethod
    public static BlockData getBlockData(Material material) {
        return BlockData.fromMaterial(material);
    }

    @ConverterMethod
    public static BlockData getBlockData(MaterialData data) {
        return BlockData.fromMaterialData(data);
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod
    public static BlockData getBlockData(org.bukkit.inventory.ItemStack item) {
        Material type = item.getType();
        if (!MaterialUtil.isBlock(type)) {
            return null;
        }
        return BlockData.fromMaterialData(type, item.getDurability());
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod
    public static BlockData parseBlockData(String text) {
        ItemParser parser = ItemParser.parse(text);
        if (!parser.hasType()) {
            return null;
        }
        if (parser.hasData()) {
            return BlockData.fromMaterialData(parser.getType(), parser.getData());
        } else {
            return BlockData.fromMaterial(parser.getType());
        }
    }

    @ConverterMethod
    @SuppressWarnings("deprecation")
    public static PotionEffectType toPotionEffectTypeFromId(int id) {
        return PotionEffectType.getById(id);
    }

    @ConverterMethod(input="net.minecraft.world.effect.MobEffect")
    public static PotionEffectType toPotionEffectType(Object nmsMobEffectListHandle) {
        int id = MobEffectHandle.T.getId.invoke(nmsMobEffectListHandle);

        @SuppressWarnings("deprecation")
        PotionEffectType type = PotionEffectType.getById(id);

        if (type != null) {
            return type;
        } else {
            return null;
        }
    }

    @ConverterMethod(input="net.minecraft.world.effect.MobEffectInstance")
    public static PotionEffect toPotionEffect(Object nmsMobEffectHandle) {
        return MobEffectInstanceHandle.T.toBukkit.invoke(nmsMobEffectHandle);
    }

    @ConverterMethod(input="net.minecraft.network.syncher.EntityDataAccessor")
    public static <T> com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key<T> toKey(Object nmsDataWatcherObjectHandle) {
        return new com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key<T>(nmsDataWatcherObjectHandle);
    }

    @ConverterMethod(input="net.minecraft.network.syncher.SynchedEntityData.DataItem")
    public static <T> com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item<T> toDataWatcherItem(Object nmsDataWatcherItemHandle) {
        SynchedEntityDataHandle.DataItemHandle handle = SynchedEntityDataHandle.DataItemHandle.createHandle(nmsDataWatcherItemHandle);
        return new com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item<T>(handle);
    }

    @ConverterMethod(input="net.minecraft.network.syncher.SynchedEntityData.DataValue")
    public static <T> com.bergerkiller.bukkit.common.wrappers.DataWatcher.PackedItem<T> toDataWatcherPackedItem(Object nmsDataWatcherPackedItemHandle) {
        return com.bergerkiller.bukkit.common.wrappers.DataWatcher.PackedItem.fromHandle(nmsDataWatcherPackedItemHandle);
    }

    @ConverterMethod(input="net.minecraft.resources.ResourceKey")
    public static <T> com.bergerkiller.bukkit.common.resources.ResourceKey<T> toResourceKey(Object nmsResourceKeyHandle) {
        return com.bergerkiller.bukkit.common.resources.ResourceKey.fromResourceKeyHandle(nmsResourceKeyHandle);
    }

    @ConverterMethod(input="net.minecraft.network.chat.Component")
    public static ChatText toChatText(Object iChatBaseComponentHandle) {
        return ChatText.fromComponent(iChatBaseComponentHandle);
    }

    @ConverterMethod
    public static ChatText fromMessageToChatText(String message) {
        return ChatText.fromMessage(message);
    }

    @ConverterMethod(input="net.minecraft.world.entity.EquipmentSlot")
    public static int enumItemSlotToFilterFlag(Object nmsEnumItemSlot) {
        return EquipmentSlotHandle.T.getFilterFlag.invoker.invoke(nmsEnumItemSlot);
    }

    @ConverterMethod(output="net.minecraft.world.entity.EquipmentSlot")
    public static Object enumItemSlotFromFilterFlag(int legacyEquipmentIndex) {
        return EquipmentSlotHandle.fromFilterFlagRaw(legacyEquipmentIndex);
    }

    @SuppressWarnings({ "unchecked" })
    @ConverterMethod(input="net.minecraft.core.NonNullList<E>", optional=true)
    public static <E> List<E> toList(Object nonNullListHandle) {
        return (List<E>) nonNullListHandle;
    }

    // <= 1.11.2
    @ConverterMethod()
    public static CraftInputSlot toCraftInputSlot(org.bukkit.inventory.ItemStack defaultChoice) {
        defaultChoice = defaultChoice.clone();
        defaultChoice.setAmount(1);
        return new CraftInputSlot(new org.bukkit.inventory.ItemStack[] { defaultChoice });
    }

    // 1.12 =>
    @ConverterMethod(input="net.minecraft.world.item.crafting.Ingredient", optional=true)
    public static CraftInputSlot toCraftInputSlot(Object recipeItemStackHandle) {
        List<org.bukkit.inventory.ItemStack> choices = IngredientHandle.T.getChoices.invoke(recipeItemStackHandle);
        if (choices == null) {
            throw new RuntimeException("Choices result field is null");
        }
        return new CraftInputSlot(choices);
    }

    @ConverterMethod(input="net.minecraft.sounds.SoundEvent")
    public static ResourceKey<SoundEffect> soundEffectToResourceKey(Object nmsSoundEffectHandle) {
        return ResourceCategory.sound_effect.createKey(SoundEventHandle.T.name.get(nmsSoundEffectHandle));
    }

    @ConverterMethod(output="net.minecraft.sounds.SoundEvent")
    public static Object soundEffectFromResourceKey(ResourceKey<SoundEffect> soundKey) {
        Object mcKey = soundKey.getName().getRaw();
        Object effect = SoundEventHandle.T.byKey.raw.invoke(mcKey);
        if (effect == null) {
            effect = SoundEventHandle.T.createVariableRangeEvent.raw.invoke(mcKey);
        }
        return effect;
    }

    @ConverterMethod
    public static ResourceKey<SoundEffect> soundNameToResourceKey(String name) {
        return SoundEffect.fromName(name);
    }

    @ConverterMethod
    public static String soundNameFromResourceKey(ResourceKey<SoundEffect> key) {
        return key.getPath();
    }

    @ConverterMethod(output="net.minecraft.resources.Identifier")
    public static Object resourceKeyToMinecraftKey(ResourceKey<?> key) {
        return key.getName().getRaw();
    }

    @ConverterMethod(input="net.minecraft.resources.Identifier")
    public static ResourceKey<SoundEffect> minecraftKeyToSoundEffectResourceKey(Object minecraftKeyHandle) {
        return ResourceCategory.sound_effect.createKey(IdentifierHandle.createHandle(minecraftKeyHandle));
    }

    @ConverterMethod(input="net.minecraft.world.level.BaseSpawner")
    public static MobSpawner toMobSpawner(Object nmsMobSpawnerAbstractHandle) {
        return new MobSpawner(nmsMobSpawnerAbstractHandle);
    }

    @ConverterMethod(input="net.minecraft.world.inventory.ContainerInput", optional=true)
    public static InventoryClickType inventoryClickTypeFromHandle(Object nmsInventoryClickType) {
        return InventoryClickType.byId(((Enum<?>) nmsInventoryClickType).ordinal());
    }

    @ConverterMethod
    public static InventoryClickType inventoryClickTypeFromId(int id) {
        return InventoryClickType.byId(id);
    }

    private static final BlockFace[] enumDirectionValues = { BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST };

    @ConverterMethod(input="net.minecraft.core.Direction")
    public static BlockFace blockFaceFromEnumDirection(Object nmsEnumDirectionHandle) {
        return enumDirectionValues[((Enum<?>) nmsEnumDirectionHandle).ordinal()];
    }

    @ConverterMethod(output="net.minecraft.core.Direction")
    public static Object blockFaceToEnumDirection(BlockFace direction) {
        Class<?> enumClass = CommonUtil.getClass("net.minecraft.core.Direction");
        if (enumClass != null) {
            Object[] values = enumClass.getEnumConstants();
            for (int i = 0; i < enumDirectionValues.length; i++) {
                if (enumDirectionValues[i] == direction) {
                    return values[i];
                }
            }
            return values[2]; // default NORTH
        }
        return null;
    }

    @ConverterMethod(input="net.minecraft.world.level.levelgen.Heightmap")
    public static HeightMap heightMapFromHandle(Object handle) {
        return new HeightMap(HeightmapHandle.createHandle(handle));
    }

    @ConverterMethod(input="net.minecraft.world.entity.EntityType", optional=true)
    public static Class<?> entityClassFromEntityTypes(Object nmsEntityTypesHandle) {
        return EntityTypeHandle.T.getEntityClassInst.invoke(nmsEntityTypesHandle);
    }

    @ConverterMethod(input="org.bukkit.block.data.BlockData", optional=true)
    public static BlockData blockDataFromBukkit(Object bukkitBlockData) {
        return CraftBlockDataHandle.T.getState.invoke(bukkitBlockData);
    }

    @ConverterMethod(input="org.bukkit.block.data.BlockData", output="net.minecraft.world.level.block.state.BlockState", optional=true)
    public static Object iblockdataHandleFromBukkit(Object bukkitBlockData) {
        return CraftBlockDataHandle.T.getState.raw.invoke(bukkitBlockData);
    }

    @ConverterMethod(input="net.minecraft.ChatFormatting")
    public static ChatColor chatColorFromEnumChatFormatHandle(Object nmsEnumChatFormat) {
        String s = nmsEnumChatFormat.toString();
        if (s.length() >= 2) {
            return ChatColor.getByChar(s.charAt(1));
        } else {
            return ChatColor.RESET;
        }
    }

    @ConverterMethod
    public static ChatColor chatColorFromEnumChatFormatIndex(int nmsEnumChatFormatIndex) {
        return chatColorFromEnumChatFormatHandle(ChatFormattingHandle.byId(nmsEnumChatFormatIndex).getRaw());
    }

    @ConverterMethod
    public static org.bukkit.Art artFromInternalName(String internalName) {
        return CraftArtHandle.NotchToBukkit(CraftArtHandle.NotchFromInternalName(internalName));
    }

    @ConverterMethod
    public static org.bukkit.Art artFromInternalId(int internalId) {
        return CraftArtHandle.NotchToBukkit(CraftArtHandle.NotchFromInternalId(internalId));
    }

    // Since Minecraft 1.9
    @ConverterMethod(input="net.minecraft.world.InteractionResult", optional = true)
    public static InteractionResult interactionResultFromNMSEnumInteractionResult(Object nmsEnumInteractionResultHandle) {
        return InteractionResult.fromHandle(nmsEnumInteractionResultHandle);
    }

    @ConverterMethod(input="net.minecraft.core.particles.ParticleType<?>", acceptsNull=true)
    public static ParticleType<?> toParticleType(Object nmsParticleHandle) {
        return ParticleType.byNMSParticleHandle(nmsParticleHandle);
    }
}
