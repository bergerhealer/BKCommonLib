package com.bergerkiller.bukkit.common.conversion;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.PropertyConverter;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.*;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.type.InputConverter;
import com.bergerkiller.mountiplex.conversion.type.NullConverter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Conversion {

    // Misc
    @SuppressWarnings("rawtypes")
    public static final Converter NONE = new NullConverter(Object.class, Object.class);
    // Primitives
    public static final InputConverter<String> toString = getConverterTo(String.class);
    public static final InputConverter<Byte> toByte = getConverterTo(byte.class);
    public static final InputConverter<Short> toShort = getConverterTo(short.class);
    public static final InputConverter<Integer> toInt = getConverterTo(int.class);
    public static final InputConverter<Long> toLong = getConverterTo(long.class);
    public static final InputConverter<Float> toFloat = getConverterTo(float.class);
    public static final InputConverter<Double> toDouble = getConverterTo(double.class);
    public static final InputConverter<Boolean> toBool = getConverterTo(boolean.class);
    public static final InputConverter<Character> toChar = getConverterTo(char.class);
    // Handles
    public static final InputConverter<Object> toEntityHandle = getConverterToHandle("net.minecraft.server.Entity");
    public static final InputConverter<Object> toEntityPlayerHandle = getConverterToHandle("net.minecraft.server.EntityPlayer");
    public static final InputConverter<Object> toWorldHandle = getConverterToHandle("net.minecraft.server.World");
    public static final InputConverter<Object> toChunkHandle = getConverterToHandle("net.minecraft.server.Chunk");
    public static final InputConverter<Object> toItemStackHandle = getConverterToHandle("net.minecraft.server.ItemStack");
    public static final InputConverter<Object> toItemHandle = getConverterToHandle("net.minecraft.server.Item");
    public static final InputConverter<Object> toTileEntityHandle = getConverterToHandle("net.minecraft.server.TileEntity");
    public static final InputConverter<Object> toInventoryHandle = getConverterToHandle("net.minecraft.server.IInventory");
    public static final InputConverter<Object> toDataWatcherHandle = getConverterToHandle("net.minecraft.server.DataWatcher");
    public static final InputConverter<Object> toDataWatcherObjectHandle = getConverterToHandle("net.minecraft.server.DataWatcherObject");
    public static final InputConverter<Object> toDataWatcherItemHandle = getConverterToHandle("net.minecraft.server.DataWatcher.Item");
    public static final InputConverter<Object> toNBTTagHandle = getConverterToHandle("net.minecraft.server.NBTBase");
    public static final InputConverter<Object> toBlockHandle = getConverterToHandle("net.minecraft.server.Block");
    public static final InputConverter<Object> toGameModeHandle = getConverterToHandle("net.minecraft.server.EnumGamemode");
    public static final InputConverter<Object> toWorldTypeHandle = getConverterToHandle("net.minecraft.server.WorldType");
    public static final InputConverter<Object> toDifficultyHandle = getConverterToHandle("net.minecraft.server.EnumDifficulty");
    public static final InputConverter<Object> toPacketHandle = getConverterToHandle("net.minecraft.server.Packet");
    public static final InputConverter<Object> toVec3DHandle = getConverterToHandle("net.minecraft.server.Vec3D");
    public static final InputConverter<Object> toChunkCoordIntPairHandle = getConverterToHandle("net.minecraft.server.ChunkCoordIntPair");
    public static final InputConverter<Object> toBlockPositionHandle = getConverterToHandle("net.minecraft.server.BlockPosition");
    public static final InputConverter<Object> toPlayerAbilitiesHandle = getConverterToHandle("net.minecraft.server.PlayerAbilities");
    public static final InputConverter<Object> toEntityTrackerHandle = getConverterToHandle("net.minecraft.server.EntityTracker");
    public static final InputConverter<Object> toLongHashSetHandle = getConverterToHandle("org.bukkit.craftbukkit.util.LongHashSet");
    public static final InputConverter<Object> toIntHashMapHandle = getConverterToHandle("net.minecraft.server.IntHashMap");
    public static final InputConverter<Object> toUseActionHandle = getConverterToHandle("net.minecraft.server.PacketPlayInUseEntity.EnumEntityUseAction");
    public static final InputConverter<Object> toScoreboardActionHandle = getConverterToHandle("net.minecraft.server.PacketPlayOutScoreboardScore.EnumScoreboardAction");
    public static final InputConverter<Object> toMainHandHandle = getConverterToHandle("net.minecraft.server.EnumHand");
    public static final InputConverter<Object> toBlockDataHandle = getConverterToHandle("net.minecraft.server.IBlockData");
    public static final InputConverter<Object> toChunkSectionHandle = getConverterToHandle("net.minecraft.server.ChunkSection");
    public static final InputConverter<Object> toMobEffectList = getConverterToHandle("net.minecraft.server.MobEffectList");
    public static final InputConverter<Object> toMobEffect = getConverterToHandle("net.minecraft.server.MobEffect");
    public static final InputConverter<Object> toMapIconHandle = getConverterToHandle("net.minecraft.server.MapIcon");
    public static final InputConverter<Object> toChatComponentHandle = getConverterToHandle("net.minecraft.server.IChatBaseComponent");
    public static final InputConverter<Object> toEnumItemSlotHandle = getConverterToHandle("net.minecraft.server.EnumItemSlot");
    // Wrappers
    public static final InputConverter<Entity> toEntity = getConverterTo(Entity.class);
    public static final InputConverter<Player> toPlayer = getConverterTo(Player.class);
    public static final InputConverter<HumanEntity> toHumanEntity = getConverterTo(HumanEntity.class);
    public static final InputConverter<Item> toItem = getConverterTo(Item.class);
    public static final InputConverter<World> toWorld = getConverterTo(World.class);
    public static final InputConverter<Chunk> toChunk = getConverterTo(Chunk.class);
    public static final InputConverter<Block> toBlock = getConverterTo(Block.class);
    public static final InputConverter<BlockState> toBlockState = getConverterTo(BlockState.class);
    public static final InputConverter<CommonTag> toCommonTag = getConverterTo(CommonTag.class);
    public static final InputConverter<DataWatcher> toDataWatcher = getConverterTo(DataWatcher.class);
    public static final InputConverter<DataWatcher.Key<?>> toDataWatcherKey = CommonUtil.unsafeCast(getConverterTo(DataWatcher.Key.class));
    public static final InputConverter<DataWatcher.Item<?>> toDataWatcherItem = CommonUtil.unsafeCast(getConverterTo(DataWatcher.Item.class));
    public static final InputConverter<ItemStack> toItemStack = getConverterTo(ItemStack.class);
    public static final InputConverter<Material> toMaterial = getConverterTo(Material.class);
    public static final InputConverter<Inventory> toInventory = getConverterTo(Inventory.class);
    public static final InputConverter<Difficulty> toDifficulty = getConverterTo(Difficulty.class);
    public static final InputConverter<WorldType> toWorldType = getConverterTo(WorldType.class);
    public static final InputConverter<GameMode> toGameMode = getConverterTo(GameMode.class);
    public static final InputConverter<CommonPacket> toCommonPacket = getConverterTo(CommonPacket.class);
    public static final InputConverter<IntVector2> toIntVector2 = getConverterTo(IntVector2.class);
    public static final InputConverter<IntVector3> toIntVector3 = getConverterTo(IntVector3.class);
    public static final InputConverter<Vector> toVector = getConverterTo(Vector.class);
    public static final InputConverter<PlayerAbilities> toPlayerAbilities = getConverterTo(PlayerAbilities.class);
    public static final InputConverter<EntityTracker> toEntityTracker = getConverterTo(EntityTracker.class);
    public static final InputConverter<LongHashSet> toLongHashSet = getConverterTo(LongHashSet.class);
    public static final InputConverter<IntHashMap<Object>> toIntHashMap = CommonUtil.unsafeCast(getConverterTo(IntHashMap.class));
    public static final InputConverter<ScoreboardAction> toScoreboardAction = getConverterTo(ScoreboardAction.class);
    public static final InputConverter<UseAction> toUseAction = getConverterTo(UseAction.class);
    public static final InputConverter<org.bukkit.inventory.MainHand> toMainHand = getConverterTo(org.bukkit.inventory.MainHand.class);
    public static final InputConverter<BlockData> toBlockData = getConverterTo(BlockData.class);
    public static final InputConverter<ChunkSection> toChunkSection = getConverterTo(ChunkSection.class);
    public static final InputConverter<PotionEffectType> toPotionEffectType = getConverterTo(PotionEffectType.class);
    public static final InputConverter<PotionEffect> toPotionEffect = getConverterTo(PotionEffect.class);
    public static final InputConverter<MapCursor> toMapCursor = getConverterTo(MapCursor.class);
    public static final InputConverter<ChatText> toChatText = getConverterTo(ChatText.class);
    public static final InputConverter<EquipmentSlot> toEquipmentSlot = getConverterTo(EquipmentSlot.class);
    // Arrays
    public static final InputConverter<ItemStack[]> toItemStackArr = getConverterTo(ItemStack[].class);
    public static final InputConverter<Object[]> toItemStackHandleArr = getConverterTo(TypeDeclaration.createArray(CommonUtil.getNMSClass("ItemStack")));
    public static final InputConverter<Object[]> toObjectArr = getConverterTo(Object[].class);
    public static final InputConverter<boolean[]> toBoolArr = getConverterTo(boolean[].class);
    public static final InputConverter<char[]> toCharArr = getConverterTo(char[].class);
    public static final InputConverter<byte[]> toByteArr = getConverterTo(byte[].class);
    public static final InputConverter<short[]> toShortArr = getConverterTo(short[].class);
    public static final InputConverter<int[]> toIntArr = getConverterTo(int[].class);
    public static final InputConverter<long[]> toLongArr = getConverterTo(long[].class);
    public static final InputConverter<float[]> toFloatArr = getConverterTo(float[].class);
    public static final InputConverter<double[]> toDoubleArr = getConverterTo(double[].class);;
    // Properties (these are not actually registered.)
    @Deprecated
    public static final PropertyConverter<Integer> toItemId = PropertyConverter.toItemId;
    public static final PropertyConverter<Material> toItemMaterial = PropertyConverter.toItemMaterial;
    public static final PropertyConverter<Integer> toPaintingFacingId = PropertyConverter.toPaintingFacingId;
    public static final PropertyConverter<Object> toPaintingFacing = PropertyConverter.toPaintingFacing;
    public static final PropertyConverter<EntityType> toMinecartType = PropertyConverter.toMinecartType;
    public static final PropertyConverter<Object> toGameProfileFromId = PropertyConverter.toGameProfileFromId;
    public static final PropertyConverter<UUID> toGameProfileId = PropertyConverter.toGameProfileId;

    @SuppressWarnings("unchecked")
    private static InputConverter<Object> getConverterToHandle(String className) {
        return (InputConverter<Object>) getConverterTo(CommonUtil.getClass(className, false));
    }

    @SuppressWarnings("unchecked")
    private static <T> InputConverter<T> getConverterTo(TypeDeclaration type) {
        return (InputConverter<T>) com.bergerkiller.mountiplex.conversion.Conversion.find(type);
    }

    private static <T> InputConverter<T> getConverterTo(Class<T> type) {
        return com.bergerkiller.mountiplex.conversion.Conversion.find(type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(Object input, T defaultValue) {
        return convert(input, (Class<T>) defaultValue.getClass(), defaultValue);
    }

    public static <T> T convert(Object input, Class<T> type, T defaultValue) {
        return com.bergerkiller.mountiplex.conversion.Conversion.find(type).convert(input, defaultValue);
    }
}