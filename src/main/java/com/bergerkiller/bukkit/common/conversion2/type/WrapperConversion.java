package com.bergerkiller.bukkit.common.conversion2.type;

import net.minecraft.server.v1_11_R1.Block;
import net.minecraft.server.v1_11_R1.Chunk;
import net.minecraft.server.v1_11_R1.Container;
import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EnumDifficulty;
import net.minecraft.server.v1_11_R1.EnumHand;
import net.minecraft.server.v1_11_R1.IInventory;
import net.minecraft.server.v1_11_R1.InventoryCrafting;
import net.minecraft.server.v1_11_R1.InventoryMerchant;
import net.minecraft.server.v1_11_R1.Item;
import net.minecraft.server.v1_11_R1.ItemStack;
import net.minecraft.server.v1_11_R1.MapIcon;
import net.minecraft.server.v1_11_R1.MobEffect;
import net.minecraft.server.v1_11_R1.PlayerInventory;
import net.minecraft.server.v1_11_R1.TileEntity;
import net.minecraft.server.v1_11_R1.TileEntityBeacon;
import net.minecraft.server.v1_11_R1.TileEntityBrewingStand;
import net.minecraft.server.v1_11_R1.TileEntityFurnace;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventoryBeacon;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventoryBrewer;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventoryFurnace;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventoryMerchant;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventoryPlayer;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_11_R1.potion.CraftPotionUtil;
import org.bukkit.craftbukkit.v1_11_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.MainHand;
import org.bukkit.map.MapCursor;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.bukkit.common.wrappers.ScoreboardAction;
import com.bergerkiller.bukkit.common.wrappers.UseAction;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntity;
import com.bergerkiller.reflection.net.minecraft.server.NMSEnumGamemode;
import com.bergerkiller.reflection.net.minecraft.server.NMSMobEffect;
import com.bergerkiller.reflection.net.minecraft.server.NMSTileEntity;
import com.bergerkiller.reflection.net.minecraft.server.NMSVector;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorldType;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftBlockState;

public class WrapperConversion {

    @ConverterMethod(input="net.minecraft.server.Entity", output="T extends org.bukkit.entity.Entity")
    public static org.bukkit.entity.Entity toEntity(Object nmsEntityHandle) {
        final Entity handle = (Entity) nmsEntityHandle;
        if (handle.world == null) {
            // We need this to avoid NPE's for non-spawned entities!
            org.bukkit.entity.Entity entity = NMSEntity.bukkitEntity.get(handle);
            if (entity == null) {
                entity = NMSEntity.createEntity(handle);
                NMSEntity.bukkitEntity.set(handle, entity);
            }
            return entity;
        } else {
            return handle.getBukkitEntity();
        }
    }

    @ConverterMethod(input="net.minecraft.server.World")
    public static org.bukkit.World toWorld(Object nmsWorldHandle) {
        return ((net.minecraft.server.v1_11_R1.World) nmsWorldHandle).getWorld();
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
        //Used to read NMSTileEntity.world !!!
        return ((TileEntity) nmsTileEntityHandle).getWorld().getWorld();
    }

    @ConverterMethod(input="net.minecraft.server.Chunk")
    public static org.bukkit.Chunk toChunk(Object nmsChunkHandle) {
        return ((Chunk) nmsChunkHandle).bukkitChunk;
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
        return CBCraftBlockState.toBlockState(nmsTileEntityHandle);
    }

    @ConverterMethod
    public static org.bukkit.block.BlockState getBlockState(org.bukkit.block.Block block) {
        return block.getState();
    }

    @ConverterMethod(input="net.minecraft.server.DataWatcher")
    public static com.bergerkiller.bukkit.common.wrappers.DataWatcher toDataWatcher(Object nmsDataWatcherHandle) {
        return new com.bergerkiller.bukkit.common.wrappers.DataWatcher(nmsDataWatcherHandle);
    }

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
        return CraftMagicNumbers.getMaterial((Item) nmsItemHandle);
    }

    @ConverterMethod(input="net.minecraft.server.Block")
    public static org.bukkit.Material toMaterialFromBlockHandle(Object nmsBlockHandle) {
        return CraftMagicNumbers.getMaterial((Block) nmsBlockHandle);
    }

    @ConverterMethod
    public static org.bukkit.Material parseMaterial(String text) {
        return ParseUtil.parseMaterial(text, null);
    }

    @ConverterMethod(input="net.minecraft.server.ItemStack")
    public static org.bukkit.inventory.ItemStack toItemStack(Object nmsItemStackHandle) {
        return CraftItemStack.asCraftMirror((ItemStack) nmsItemStackHandle);
    }

    @ConverterMethod
    public static org.bukkit.inventory.ItemStack parseItemStack(String text) {
        return ItemParser.parse(text).getItemStack();
    }

    @ConverterMethod(input="net.minecraft.server.InventoryCrafting")
    public static CraftingInventory toCraftingInventory(Object nmsInventoryCraftingHandle) {
        return new CraftInventoryCrafting((InventoryCrafting) nmsInventoryCraftingHandle, null);
    }

    @ConverterMethod(input="net.minecraft.server.PlayerInventory")
    public static org.bukkit.inventory.PlayerInventory toPlayerInventory(Object nmsPlayerInventoryHandle) {
        return new CraftInventoryPlayer((PlayerInventory) nmsPlayerInventoryHandle);
    }

    @ConverterMethod(input="net.minecraft.server.TileEntityFurnace")
    public static org.bukkit.inventory.FurnaceInventory toFurnaceInventory(Object nmsTileEntityFurnaceHandle) {
        return new CraftInventoryFurnace((TileEntityFurnace) nmsTileEntityFurnaceHandle);
    }

    @ConverterMethod(input="net.minecraft.server.TileEntityBrewingStand")
    public static org.bukkit.inventory.BrewerInventory toBrewerInventory(Object nmsTileEntityBrewingStandHandle) {
        return new CraftInventoryBrewer((TileEntityBrewingStand) nmsTileEntityBrewingStandHandle);
    }

    @ConverterMethod(input="net.minecraft.server.InventoryMerchant")
    public static org.bukkit.inventory.MerchantInventory toMerchantInventory(Object nmsInventoryMerchantHandle) {
        return new CraftInventoryMerchant((InventoryMerchant) nmsInventoryMerchantHandle);
    }

    @ConverterMethod(input="net.minecraft.server.TileEntityBeacon") 
    public static org.bukkit.inventory.BeaconInventory toBeaconInventory(Object nmsTileEntityBeaconHandle) {
        return new CraftInventoryBeacon((TileEntityBeacon) nmsTileEntityBeaconHandle);
    }

    @ConverterMethod(input="net.minecraft.server.IInventory")
    public static org.bukkit.inventory.Inventory toInventory(Object nmsIInventoryHandle) {
        return new CraftInventory((IInventory) nmsIInventoryHandle);
    }

    @ConverterMethod(input="net.minecraft.server.Container")
    public static org.bukkit.inventory.InventoryView toInventoryView(Object nmsContainerHandle)  {
        return ((Container) nmsContainerHandle).getBukkitView();
    }

    @ConverterMethod
    public static org.bukkit.inventory.Inventory toInventory(org.bukkit.inventory.InventoryView inventoryView) {
        return inventoryView.getTopInventory();
    }

    @ConverterMethod(input="net.minecraft.server.EnumDifficulty")
    public static org.bukkit.Difficulty toDifficulty(Object nmsEnumDifficultyHandle) {
        return Difficulty.getByValue(((EnumDifficulty) nmsEnumDifficultyHandle).a());
    }

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

    @ConverterMethod(input="net.minecraft.server.EnumGamemode")
    public static org.bukkit.GameMode toGameMode(Object nmsEnumGamemodeHandle) {
        return org.bukkit.GameMode.getByValue(NMSEnumGamemode.egmId.get(nmsEnumGamemodeHandle));
    }

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
        switch((EnumHand) nmsEnumHandHandle) {
        case OFF_HAND:
            return MainHand.LEFT;
        case MAIN_HAND:
            return MainHand.RIGHT;
        }
        return null;
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

    @ConverterMethod
    public static <T> LongHashMap<T> toLongHashMap(it.unimi.dsi.fastutil.longs.Long2ObjectMap<T> longObjectMap) {
        return new LongHashMap<T>(longObjectMap);
    }

    @ConverterMethod(input="org.bukkit.craftbukkit.util.LongHashSet")
    public static LongHashSet toLOngHashSet(Object cbLongHashSetHandle) {
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

    @ConverterMethod
    public static BlockData getBlockData(org.bukkit.inventory.ItemStack item) {
        Material type = item.getType();
        if (!type.isBlock()) {
            return null;
        }
        return BlockData.fromMaterialData(type, item.getDurability());
    }

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
        return CraftPotionUtil.toBukkit((MobEffect) nmsMobEffectHandle);
    }

    @ConverterMethod(input="net.minecraft.server.DataWatcherObject<T>")
    public static <T> com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key<T> toKey(Object nmsDataWatcherObjectHandle) {
        return new com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key<T>(nmsDataWatcherObjectHandle);
    }

    @ConverterMethod(input="net.minecraft.server.DataWatcher.Item<T>")
    public static <T> com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item<?> toDataWatcherItem(Object nmsDataWatcherItemHandle) {
        return new com.bergerkiller.bukkit.common.wrappers.DataWatcher.Item<T>(nmsDataWatcherItemHandle);
    }

    @ConverterMethod(input="net.minecraft.server.MapIcon")
    public static MapCursor toMapCursor(Object nmsMapCursorHandle) {
        // public MapCursor(byte x, byte y, byte direction, byte type, boolean visible)
        // public MapIcon(Type paramType, byte paramByte1, byte paramByte2, byte paramByte3)
        MapIcon icon = (MapIcon) nmsMapCursorHandle;
        return new MapCursor(icon.getX(), icon.getY(), icon.getRotation(), icon.getType(), true);
    }

    @ConverterMethod(input="net.minecraft.server.IChatBaseComponent")
    public static ChatText toChatText(Object iChatBaseComponentHandle) {
        return ChatText.fromComponent(iChatBaseComponentHandle);
    }
}
