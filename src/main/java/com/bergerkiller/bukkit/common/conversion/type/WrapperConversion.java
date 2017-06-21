package com.bergerkiller.bukkit.common.conversion.type;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.MainHand;
import org.bukkit.map.MapCursor;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatMessageType;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.bukkit.common.wrappers.ScoreboardAction;
import com.bergerkiller.bukkit.common.wrappers.UseAction;
import com.bergerkiller.generated.net.minecraft.server.ChatMessageTypeHandle;
import com.bergerkiller.generated.net.minecraft.server.ChunkHandle;
import com.bergerkiller.generated.net.minecraft.server.ContainerHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumDifficultyHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumGamemodeHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumHandHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumItemSlotHandle;
import com.bergerkiller.generated.net.minecraft.server.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.server.MapIconHandle;
import com.bergerkiller.generated.net.minecraft.server.RecipeItemStackHandle;
import com.bergerkiller.generated.net.minecraft.server.TileEntityHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
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
import com.bergerkiller.reflection.net.minecraft.server.NMSMobEffect;
import com.bergerkiller.reflection.net.minecraft.server.NMSTileEntity;
import com.bergerkiller.reflection.net.minecraft.server.NMSVector;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorldType;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftEntity;

public class WrapperConversion {

    @ConverterMethod(input="net.minecraft.server.Entity", output="T extends org.bukkit.entity.Entity")
    public static org.bukkit.entity.Entity toEntity(Object nmsEntityHandle) {
        if (EntityHandle.T.world.raw.get(nmsEntityHandle) == null) {
            // We need this to avoid NPE's for non-spawned entities!
            org.bukkit.entity.Entity entity = EntityHandle.T.bukkitEntityField.get(nmsEntityHandle);
            if (entity == null) {
                entity = CBCraftEntity.getEntity.invoke(null, Bukkit.getServer(), nmsEntityHandle);
                EntityHandle.T.bukkitEntityField.set(nmsEntityHandle, entity);
            }
            return entity;
        } else {
            return EntityHandle.T.getBukkitEntity.invoke(nmsEntityHandle);
        }
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
        return NMSTileEntity.getBlock(nmsTileEntityHandle);
    }

    @ConverterMethod(input="net.minecraft.server.TileEntity")
    public static org.bukkit.block.BlockState toBlockState(Object nmsTileEntityHandle) {
        return BlockStateConversion.tileEntityToBlockState(nmsTileEntityHandle);
    }

    @ConverterMethod
    public static org.bukkit.block.BlockState getBlockState(org.bukkit.block.Block block) {
        return BlockStateConversion.blockToBlockState(block);
    }

    @ConverterMethod(input="net.minecraft.server.DataWatcher")
    public static com.bergerkiller.bukkit.common.wrappers.DataWatcher toDataWatcher(Object nmsDataWatcherHandle) {
        return new com.bergerkiller.bukkit.common.wrappers.DataWatcher(nmsDataWatcherHandle);
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod
    public static org.bukkit.Material getMaterialFromId(Number materialId) {
        return Material.getMaterial(materialId.intValue());
    }

    @ConverterMethod
    public static org.bukkit.Material getMaterialFromBlock(org.bukkit.block.Block block) {
        return block.getType();
    }

    @ConverterMethod(input="net.minecraft.server.Item")
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

    @ConverterMethod(input="net.minecraft.server.ItemStack", acceptsNull = true)
    public static org.bukkit.inventory.ItemStack toItemStack(Object nmsItemStackHandle) {
        if (nmsItemStackHandle == null || ItemStackHandle.T.typeField.raw.get(nmsItemStackHandle) == null) {
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

    @ConverterMethod(input="net.minecraft.server.IInventory")
    public static org.bukkit.inventory.Inventory toInventory(Object nmsIInventoryHandle) {
        return CraftInventoryHandle.createNew(nmsIInventoryHandle);
    }

    @ConverterMethod(input="net.minecraft.server.Container")
    public static org.bukkit.inventory.InventoryView toInventoryView(Object nmsContainerHandle) {
        return ContainerHandle.T.getBukkitView.invoke(nmsContainerHandle);
    }

    @ConverterMethod
    public static org.bukkit.inventory.Inventory toInventory(org.bukkit.inventory.InventoryView inventoryView) {
        return inventoryView.getTopInventory();
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod(input="net.minecraft.server.EnumDifficulty")
    public static org.bukkit.Difficulty toDifficulty(Object nmsEnumDifficultyHandle) {
        Integer id = EnumDifficultyHandle.T.getId.invoke(nmsEnumDifficultyHandle);
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

    @ConverterMethod(input="net.minecraft.server.WorldType")
    public static org.bukkit.WorldType toWorldType(Object nmsWorldTypeHandle) {
        return org.bukkit.WorldType.getByName(NMSWorldType.name.get(nmsWorldTypeHandle));
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

    @ConverterMethod(input="net.minecraft.server.EnumHand")
    public static org.bukkit.inventory.MainHand toMainHand(Object nmsEnumHandHandle) {
        if (nmsEnumHandHandle == EnumHandHandle.MAIN_HAND.getRaw()) {
            return MainHand.RIGHT;
        } else if (nmsEnumHandHandle == EnumHandHandle.OFF_HAND.getRaw()) {
            return MainHand.LEFT;
        } else {
            return null;
        }
    }

    @ConverterMethod(input="net.minecraft.server.Packet")
    public static CommonPacket toCommonPacket(Object nmsPacketHandle) {
        return new CommonPacket(nmsPacketHandle);
    }

    @ConverterMethod(input="net.minecraft.server.ChunkCoordIntPair")
    public static IntVector2 toIntVector2(Object nmsChunkCoordIntPairHandle) {
        return NMSVector.getPair(nmsChunkCoordIntPairHandle);
    }

    @ConverterMethod(input="net.minecraft.server.BlockPosition")
    public static IntVector3 toIntVector3(Object nmsBlockPositionHandle) {
        return NMSVector.getPosition(nmsBlockPositionHandle);
    }

    @ConverterMethod
    public static IntVector3 toIntVector3FromVector(Vector vector) {
        return new IntVector3(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    @ConverterMethod(input="net.minecraft.server.Vec3D")
    public static Vector toVector(Object nmsVec3DHandle) {
        return NMSVector.getVec(nmsVec3DHandle);
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

    @ConverterMethod(input="org.bukkit.craftbukkit.util.LongHashSet")
    public static LongHashSet toLongHashSet(Object cbLongHashSetHandle) {
        return new LongHashSet(cbLongHashSetHandle);
    }

    @ConverterMethod(input="net.minecraft.server.IntHashMap<T>")
    public static <T> IntHashMap<T> toIntHashMap(Object nmsIntHashMapHandle) {
        return new IntHashMap<T>(nmsIntHashMapHandle);
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
        return new ChunkSection(nmsChunkSectionHandle);
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod(input="net.minecraft.server.MobEffectList")
    public static PotionEffectType toPotionEffectType(Object nmsMobEffectListHandle) {
        int id = NMSMobEffect.List.getId.invoke(null, nmsMobEffectListHandle);
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

    @ConverterMethod(input="net.minecraft.server.DataWatcherObject<T>")
    public static <T> com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key<T> toKey(Object nmsDataWatcherObjectHandle) {
        return new com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key<T>(nmsDataWatcherObjectHandle);
    }

    @ConverterMethod(input="net.minecraft.server.DataWatcher.Item<T>")
    public static <T> com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item<T> toDataWatcherItem(Object nmsDataWatcherItemHandle) {
        return new com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item<T>(nmsDataWatcherItemHandle);
    }

    @SuppressWarnings("deprecation")
    @ConverterMethod(input="net.minecraft.server.MapIcon")
    public static MapCursor toMapCursor(Object nmsMapCursorHandle) {
        // public MapCursor(byte x, byte y, byte direction, byte type, boolean visible)
        // public MapIcon(Type paramType, byte paramByte1, byte paramByte2, byte paramByte3)
        MapIconHandle icon = MapIconHandle.createHandle(nmsMapCursorHandle);
        return new MapCursor(icon.getX(), icon.getY(), icon.getDirection(), icon.getTypeId(), true);
    }

    @ConverterMethod(output="net.minecraft.server.MapIcon.Type", optional=true)
    public static Object mapIconTypeIdToEnum(byte typeId) {
        if (MapIconHandle.TypeHandle.T.isValid()) {
            return MapIconHandle.TypeHandle.T.fromId.raw.invokeVA(typeId);
        } else {
            throw new UnsupportedOperationException("Map Icon Type enum not supported");
        }
    }

    @ConverterMethod(input="net.minecraft.server.IChatBaseComponent")
    public static ChatText toChatText(Object iChatBaseComponentHandle) {
        return ChatText.fromComponent(iChatBaseComponentHandle);
    }

    @ConverterMethod(input="net.minecraft.server.EnumItemSlot")
    public static EquipmentSlot toEquipmentSlot(Object enumItemSlotHandle) {
        return EnumItemSlotHandle.createHandle(enumItemSlotHandle).toBukkit();
    }

    @ConverterMethod(input="net.minecraft.server.ChatMessageType", optional=true)
    public static ChatMessageType toChatMessageType(Object nmsChatMessageType) {
        return ChatMessageType.getById(ChatMessageTypeHandle.T.getId.invoke(nmsChatMessageType).byteValue());
    }

    @ConverterMethod()
    public static ChatMessageType toChatMessageType(byte chatMessageTypeId) {
        return ChatMessageType.getById(chatMessageTypeId);
    }

    @SuppressWarnings({ "unchecked" })
    @ConverterMethod(input="net.minecraft.server.NonNullList<E>", optional=true)
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
    @ConverterMethod(input="net.minecraft.server.RecipeItemStack", optional=true)
    public static CraftInputSlot toCraftInputSlot(Object recipeItemStackHandle) {
        return new CraftInputSlot(RecipeItemStackHandle.T.choices.get(recipeItemStackHandle));
    }

}
