package com.bergerkiller.bukkit.common.conversion.type;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.map.MapCursor;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.resources.ResourceCategory;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatMessageType;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
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
import com.bergerkiller.bukkit.common.wrappers.UseAction;
import com.bergerkiller.generated.net.minecraft.EnumChatFormatHandle;
import com.bergerkiller.generated.net.minecraft.core.BaseBlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.core.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.core.Vector3fHandle;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.server.ChatMessageTypeHandle;
import com.bergerkiller.generated.net.minecraft.server.ChunkCoordIntPairHandle;
import com.bergerkiller.generated.net.minecraft.server.ChunkHandle;
import com.bergerkiller.generated.net.minecraft.server.ChunkSectionHandle;
import com.bergerkiller.generated.net.minecraft.server.ContainerHandle;
import com.bergerkiller.generated.net.minecraft.server.DataWatcherHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumGamemodeHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumItemSlotHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumMainHandHandle;
import com.bergerkiller.generated.net.minecraft.server.HeightMapHandle;
import com.bergerkiller.generated.net.minecraft.server.MapIconHandle;
import com.bergerkiller.generated.net.minecraft.server.MobEffectListHandle;
import com.bergerkiller.generated.net.minecraft.server.SoundEffectHandle;
import com.bergerkiller.generated.net.minecraft.server.TileEntityHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.generated.net.minecraft.world.EnumDifficultyHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypesHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.item.crafting.RecipeItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.Vec3DHandle;
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
import com.bergerkiller.generated.org.bukkit.craftbukkit.potion.CraftPotionUtilHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;

public class WrapperConversion {

    @ConverterMethod(input="net.minecraft.world.entity.Entity", output="T extends org.bukkit.entity.Entity")
    public static org.bukkit.entity.Entity toEntity(Object nmsEntityHandle) {
        return EntityHandle.T.getBukkitEntity.invoker.invoke(nmsEntityHandle);
    }

    @ConverterMethod(input="net.minecraft.server.World")
    public static org.bukkit.World toWorld(Object nmsWorldHandle) {
        return (org.bukkit.World) WorldHandle.T.getWorld.raw.invoke(nmsWorldHandle);
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

    @ConverterMethod(input="net.minecraft.server.TileEntity")
    public static org.bukkit.World getWorldFromTileEntity(Object nmsTileEntityHandle) {
        return toWorld(TileEntityHandle.T.getWorld.raw.invoke(nmsTileEntityHandle));
    }

    @ConverterMethod(input="net.minecraft.server.Chunk")
    public static org.bukkit.Chunk toChunk(Object nmsChunkHandle) {
        return ChunkHandle.T.bukkitChunk.get(nmsChunkHandle);
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

    @ConverterMethod(input="net.minecraft.server.TileEntity")
    public static org.bukkit.block.Block getBlockFromTileEntity(Object nmsTileEntityHandle) {
        TileEntityHandle handle = TileEntityHandle.createHandle(nmsTileEntityHandle);
        BlockPositionHandle pos = handle.getPosition();
        return handle.getWorld().getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
    }

    @ConverterMethod(input="net.minecraft.server.TileEntity")
    public static org.bukkit.block.BlockState toBlockState(Object nmsTileEntityHandle) {
        return BlockStateConversion.INSTANCE.tileEntityToBlockState(nmsTileEntityHandle);
    }

    @ConverterMethod
    public static org.bukkit.block.BlockState getBlockState(org.bukkit.block.Block block) {
        return BlockStateConversion.INSTANCE.blockToBlockState(block);
    }

    @ConverterMethod(input="net.minecraft.server.DataWatcher")
    public static com.bergerkiller.bukkit.common.wrappers.DataWatcher toDataWatcher(Object nmsDataWatcherHandle) {
        return new com.bergerkiller.bukkit.common.wrappers.DataWatcher(nmsDataWatcherHandle);
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

    @ConverterMethod(input="net.minecraft.server.Block")
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

    @ConverterMethod(input="net.minecraft.server.InventoryCrafting")
    public static CraftingInventory toCraftingInventory(Object nmsInventoryCraftingHandle) {
        return CraftInventoryCraftingHandle.createNew(nmsInventoryCraftingHandle, null);
    }

    @ConverterMethod(input="net.minecraft.server.PlayerInventory")
    public static org.bukkit.inventory.PlayerInventory toPlayerInventory(Object nmsPlayerInventoryHandle) {
        return CraftInventoryPlayerHandle.createNew(nmsPlayerInventoryHandle);
    }

    @ConverterMethod(input="net.minecraft.server.TileEntityFurnace")
    public static org.bukkit.inventory.FurnaceInventory toFurnaceInventory(Object nmsTileEntityFurnaceHandle) {
        return CraftInventoryFurnaceHandle.createNew(nmsTileEntityFurnaceHandle);
    }

    @ConverterMethod(input="net.minecraft.server.TileEntityBrewingStand")
    public static org.bukkit.inventory.BrewerInventory toBrewerInventory(Object nmsTileEntityBrewingStandHandle) {
        return CraftInventoryBrewerHandle.createNew(nmsTileEntityBrewingStandHandle);
    }

    @ConverterMethod(input="net.minecraft.server.InventoryMerchant")
    public static org.bukkit.inventory.MerchantInventory toMerchantInventory(Object nmsInventoryMerchantHandle) {
        return CraftInventoryMerchantHandle.createNew(nmsInventoryMerchantHandle);
    }

    @ConverterMethod(input="net.minecraft.server.TileEntityBeacon") 
    public static org.bukkit.inventory.BeaconInventory toBeaconInventory(Object nmsTileEntityBeaconHandle) {
        return CraftInventoryBeaconHandle.createNew(nmsTileEntityBeaconHandle);
    }

    @ConverterMethod(input="net.minecraft.world.IInventory")
    public static org.bukkit.inventory.Inventory toInventory(Object nmsIInventoryHandle) {
        return CraftInventoryHandle.createNew(nmsIInventoryHandle);
    }

    @ConverterMethod(input="net.minecraft.server.Container")
    public static org.bukkit.inventory.InventoryView toInventoryView(Object nmsContainerHandle) {
        return ContainerHandle.T.getBukkitView.invoker.invoke(nmsContainerHandle);
    }

    @ConverterMethod
    public static org.bukkit.inventory.Inventory toInventory(org.bukkit.inventory.InventoryView inventoryView) {
        return inventoryView.getTopInventory();
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod(input="net.minecraft.world.EnumDifficulty")
    public static org.bukkit.Difficulty toDifficulty(Object nmsEnumDifficultyHandle) {
        Integer id = EnumDifficultyHandle.T.getId.invoker.invoke(nmsEnumDifficultyHandle);
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
    @ConverterMethod(input="net.minecraft.server.EnumGamemode")
    public static org.bukkit.GameMode toGameMode(Object nmsEnumGamemodeHandle) {
        return org.bukkit.GameMode.getByValue(EnumGamemodeHandle.T.id.get(nmsEnumGamemodeHandle));
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

    @ConverterMethod(input="net.minecraft.world.EnumHand", output="org.bukkit.inventory.MainHand", optional=true)
    public static Object fromEnumHandToMainHand(Object nmsEnumHandHandle) {
        return HumanHand.fromNMSEnumHand(null, nmsEnumHandHandle).toMainHand();
    }

    @ConverterMethod(input="net.minecraft.world.EnumHand", optional=true)
    public static HumanHand fromEnumHandToHumanHand(Object nmsEnumHandHandle) {
        return HumanHand.fromNMSEnumHand(null, nmsEnumHandHandle);
    }

    @ConverterMethod(input="org.bukkit.inventory.MainHand", optional=true)
    public static HumanHand fromMainHandToHumanHand(Object mainHand) {
        return HumanHand.fromMainHand(mainHand);
    }

    @ConverterMethod(input="net.minecraft.server.EnumMainHand", optional=true)
    public static HumanHand humanHandToEnumMainHandHandle(Object nmsEnumMainHandHandle) {
        if (nmsEnumMainHandHandle == EnumMainHandHandle.LEFT.getRaw()) {
            return HumanHand.LEFT;
        } else {
            return HumanHand.RIGHT;
        }
    }

    @ConverterMethod(input="net.minecraft.server.Packet")
    public static CommonPacket toCommonPacket(Object nmsPacketHandle) {
        return new CommonPacket(nmsPacketHandle);
    }

    @ConverterMethod(input="net.minecraft.server.ChunkCoordIntPair")
    public static IntVector2 toIntVector2(Object nmsChunkCoordIntPairHandle) {
        return ChunkCoordIntPairHandle.T.toIntVector2.invoker.invoke(nmsChunkCoordIntPairHandle);
    }

    @ConverterMethod(input="net.minecraft.core.BlockPosition")
    public static IntVector3 toIntVector3(Object nmsBlockPositionHandle) {
        return BaseBlockPositionHandle.T.toIntVector3.invoker.invoke(nmsBlockPositionHandle);
    }

    @ConverterMethod
    public static IntVector3 toIntVector3FromVector(Vector vector) {
        return new IntVector3(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    @ConverterMethod(input="net.minecraft.server.Vec3D")
    public static Vector toVector(Object nmsVec3DHandle) {
        return Vec3DHandle.T.toBukkit.invoker.invoke(nmsVec3DHandle);
    }

    @ConverterMethod(input="net.minecraft.core.Vector3f")
    public static Vector fromVector3fToVector(Object nmsVector3fHandle) {
        Vector3fHandle handle = Vector3fHandle.createHandle(nmsVector3fHandle);
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

    @ConverterMethod(input="net.minecraft.server.PlayerAbilities")
    public static PlayerAbilities toPlayerAbilities(Object nmsPlayerAbilitiesHandle) {
        return new PlayerAbilities(nmsPlayerAbilitiesHandle);
    }

    @ConverterMethod(input="net.minecraft.server.EntityTracker")
    public static EntityTracker toEntityTracker(Object nmsEntityTrackerHandle) {
        return new EntityTracker(nmsEntityTrackerHandle);
    }

    @ConverterMethod(input="com.bergerkiller.bukkit.common.internal.LongHashSet")
    public static LongHashSet toLongHashSet(Object cbLongHashSetHandle) {
        return new LongHashSet(cbLongHashSetHandle);
    }

    @ConverterMethod(input="net.minecraft.server.IntHashMap<T>")
    public static <T> IntHashMap<T> toIntHashMap(Object nmsIntHashMapHandle) {
        return new IntHashMap<T>(nmsIntHashMapHandle);
    }

    @ConverterMethod(input="net.minecraft.server.IntHashMap<?>")
    public static IntHashMap<Object> toRawIntHashMap(Object nmsIntHashMapHandle) {
        return new IntHashMap<Object>(nmsIntHashMapHandle);
    }

    @ConverterMethod(input="net.minecraft.server.PacketPlayInUseEntity.EnumEntityUseAction")
    public static UseAction toUseAction(Object nmsEnumEntityUseActionHandle) {
        return UseAction.fromHandle(nmsEnumEntityUseActionHandle);
    }

    @ConverterMethod(input="net.minecraft.server.PacketPlayOutScoreboardScore.EnumScoreboardAction")
    public static ScoreboardAction toScoreboardAction(Object nmsEnumScoreboardActionHandle) {
        return ScoreboardAction.fromHandle(nmsEnumScoreboardActionHandle);
    }

    @ConverterMethod(input="net.minecraft.server.IBlockData")
    public static BlockData toBlockData(Object nmsIBlockDataHandle) {
        return BlockData.fromBlockData(nmsIBlockDataHandle);
    }

    @ConverterMethod(input="net.minecraft.server.Block")
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
        if (!type.isBlock()) {
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

    @ConverterMethod(input="net.minecraft.server.ChunkSection")
    public static ChunkSection toChunkSection(Object nmsChunkSectionHandle) {
        return new ChunkSection(ChunkSectionHandle.createHandle(nmsChunkSectionHandle));
    }

    @ConverterMethod(input="net.minecraft.server.MobEffectList")
    public static PotionEffectType toPotionEffectType(Object nmsMobEffectListHandle) {
        int id = MobEffectListHandle.T.getId.invoke(nmsMobEffectListHandle);

        @SuppressWarnings("deprecation")
        PotionEffectType type = PotionEffectType.getById(id);

        if (type != null) {
            return type;
        } else {
            return null;
        }
    }

    @ConverterMethod(input="net.minecraft.server.MobEffect")
    public static PotionEffect toPotionEffect(Object nmsMobEffectHandle) {
        return CraftPotionUtilHandle.toBukkit(nmsMobEffectHandle);
    }

    @ConverterMethod(input="net.minecraft.server.DataWatcherObject")
    public static <T> com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key<T> toKey(Object nmsDataWatcherObjectHandle) {
        return new com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key<T>(nmsDataWatcherObjectHandle);
    }

    @ConverterMethod(input="net.minecraft.server.DataWatcher.Item")
    public static <T> com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item<T> toDataWatcherItem(Object nmsDataWatcherItemHandle) {
        DataWatcherHandle.ItemHandle handle = DataWatcherHandle.ItemHandle.createHandle(nmsDataWatcherItemHandle);
        return new com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item<T>(handle);
    }

    @ConverterMethod(input="net.minecraft.server.ResourceKey")
    public static <T> com.bergerkiller.bukkit.common.resources.ResourceKey<T> toResourceKey(Object nmsResourceKeyHandle) {
        return com.bergerkiller.bukkit.common.resources.ResourceKey.fromResourceKeyHandle(nmsResourceKeyHandle);
    }

    @ConverterMethod(input="net.minecraft.server.MapIcon")
    public static MapCursor toMapCursor(Object nmsMapCursorHandle) {
        return MapIconHandle.createHandle(nmsMapCursorHandle).toCursor();
    }

    @ConverterMethod(output="net.minecraft.server.MapIcon.Type", optional=true)
    public static Object mapIconTypeIdToEnum(byte typeId) {
        if (MapIconHandle.TypeHandle.T.isValid()) {
            return MapIconHandle.TypeHandle.T.fromId.raw.invoke(typeId);
        } else {
            throw new UnsupportedOperationException("Map Icon Type enum not supported");
        }
    }

    @ConverterMethod(input="net.minecraft.server.IChatBaseComponent")
    public static ChatText toChatText(Object iChatBaseComponentHandle) {
        return ChatText.fromComponent(iChatBaseComponentHandle);
    }

    @ConverterMethod
    public static ChatText fromMessageToChatText(String message) {
        return ChatText.fromMessage(message);
    }

    @ConverterMethod(input="net.minecraft.server.EnumItemSlot")
    public static EquipmentSlot toEquipmentSlot(Object enumItemSlotHandle) {
        return ItemSlotConversion.getEquipmentSlot(enumItemSlotHandle);
    }

    @ConverterMethod(input="net.minecraft.server.EnumItemSlot")
    public static int enumItemSlotToIndex(Object nmsEnumItemSlot) {
        return EnumItemSlotHandle.T.getIndex.invoker.invoke(nmsEnumItemSlot);
    }

    @ConverterMethod
    public static int equipmentSlotToIndex(EquipmentSlot equipmentSlot) {
        return enumItemSlotToIndex(HandleConversion.toEnumItemSlotHandle(equipmentSlot));
    }

    @ConverterMethod(output="net.minecraft.server.EnumItemSlot")
    public static Object enumItemSlotFromIndex(int legacyEquipmentIndex) {
        return EnumItemSlotHandle.fromIndexRaw(legacyEquipmentIndex);
    }

    @ConverterMethod(input="net.minecraft.server.ChatMessageType", optional=true)
    public static ChatMessageType toChatMessageType(Object nmsChatMessageType) {
        return ChatMessageType.getById(ChatMessageTypeHandle.T.getId.invoker.invoke(nmsChatMessageType).byteValue());
    }

    @ConverterMethod()
    public static ChatMessageType toChatMessageType(byte chatMessageTypeId) {
        return ChatMessageType.getById(chatMessageTypeId);
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
    @ConverterMethod(input="net.minecraft.world.item.crafting.RecipeItemStack", optional=true)
    public static CraftInputSlot toCraftInputSlot(Object recipeItemStackHandle) {
        List<org.bukkit.inventory.ItemStack> choices = RecipeItemStackHandle.T.getChoices.invoke(recipeItemStackHandle);
        if (choices == null) {
            throw new RuntimeException("Choices result field is null");
        }
        return new CraftInputSlot(choices);
    }

    @ConverterMethod(input="net.minecraft.server.SoundEffect")
    public static ResourceKey<SoundEffect> soundEffectToResourceKey(Object nmsSoundEffectHandle) {
        return ResourceCategory.sound_effect.createKey(SoundEffectHandle.T.name.get(nmsSoundEffectHandle));
    }

    @ConverterMethod(output="net.minecraft.server.SoundEffect")
    public static Object soundEffectFromResourceKey(ResourceKey<SoundEffect> soundKey) {
        if (SoundEffectHandle.T.opt_getRegistry.isAvailable()) {
            Object mcKey = soundKey.getName().getRaw();
            Object effect = SoundEffectHandle.T.opt_getRegistry.invoke().get(mcKey);
            if (effect == null) {
                effect = SoundEffectHandle.T.constr_minecraftkey.raw.newInstance(mcKey);
            }
            return effect;
        } else {
            return SoundEffectHandle.createNew(MinecraftKeyHandle.createNew(soundKey.getPath()));
        }
    }

    @ConverterMethod
    public static ResourceKey<SoundEffect> soundNameToResourceKey(String name) {
        return SoundEffect.fromName(name);
    }

    @ConverterMethod
    public static String soundNameFromResourceKey(ResourceKey<SoundEffect> key) {
        return key.getPath();
    }

    @ConverterMethod(output="net.minecraft.resources.MinecraftKey")
    public static Object resourceKeyToMinecraftKey(ResourceKey<?> key) {
        return key.getName().getRaw();
    }

    @ConverterMethod(input="net.minecraft.resources.MinecraftKey")
    public static ResourceKey<SoundEffect> minecraftKeyToSoundEffectResourceKey(Object minecraftKeyHandle) {
        return ResourceCategory.sound_effect.createKey(MinecraftKeyHandle.createHandle(minecraftKeyHandle));
    }

    @ConverterMethod(input="net.minecraft.server.MobSpawnerAbstract")
    public static MobSpawner toMobSpawner(Object nmsMobSpawnerAbstractHandle) {
        return new MobSpawner(nmsMobSpawnerAbstractHandle);
    }

    @ConverterMethod(input="net.minecraft.server.InventoryClickType", optional=true)
    public static InventoryClickType inventoryClickTypeFromHandle(Object nmsInventoryClickType) {
        return InventoryClickType.byId(((Enum<?>) nmsInventoryClickType).ordinal());
    }

    @ConverterMethod
    public static InventoryClickType inventoryClickTypeFromId(int id) {
        return InventoryClickType.byId(id);
    }

    private static final BlockFace[] enumDirectionValues = { BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST };

    @ConverterMethod(input="net.minecraft.core.EnumDirection")
    public static BlockFace blockFaceFromEnumDirection(Object nmsEnumDirectionHandle) {
        return enumDirectionValues[((Enum<?>) nmsEnumDirectionHandle).ordinal()];
    }

    @ConverterMethod(output="net.minecraft.core.EnumDirection")
    public static Object blockFaceToEnumDirection(BlockFace direction) {
        Class<?> enumClass = CommonUtil.getNMSClass("EnumDirection");
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

    @ConverterMethod(input="net.minecraft.server.HeightMap")
    public static HeightMap heightMapFromHandle(Object handle) {
        return new HeightMap(HeightMapHandle.createHandle(handle));
    }

    @ConverterMethod(input="net.minecraft.server.EntityTypes", optional=true)
    public static Class<?> entityClassFromEntityTypes(Object nmsEntityTypesHandle) {
        return EntityTypesHandle.T.getEntityClassInst.invoke(nmsEntityTypesHandle);
    }

    @ConverterMethod(input="org.bukkit.block.data.BlockData", optional=true)
    public static BlockData blockDataFromBukkit(Object bukkitBlockData) {
        return CraftBlockDataHandle.T.getState.invoke(bukkitBlockData);
    }

    @ConverterMethod(input="org.bukkit.block.data.BlockData", output="net.minecraft.server.IBlockData", optional=true)
    public static Object iblockdataHandleFromBukkit(Object bukkitBlockData) {
        return CraftBlockDataHandle.T.getState.raw.invoke(bukkitBlockData);
    }

    @ConverterMethod(input="net.minecraft.EnumChatFormat")
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
        return chatColorFromEnumChatFormatHandle(EnumChatFormatHandle.byId(nmsEnumChatFormatIndex).getRaw());
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
    @ConverterMethod(input="net.minecraft.server.EnumInteractionResult", optional = true)
    public static InteractionResult interactionResultFromNMSEnumInteractionResult(Object nmsEnumInteractionResultHandle) {
        return InteractionResult.fromHandle(nmsEnumInteractionResultHandle);
    }
}
