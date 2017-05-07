package com.bergerkiller.bukkit.common.conversion;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.HandleConverter;
import com.bergerkiller.bukkit.common.conversion.type.PrimitiveArrayConverter;
import com.bergerkiller.bukkit.common.conversion.type.PrimitiveConverter;
import com.bergerkiller.bukkit.common.conversion.type.PropertyConverter;
import com.bergerkiller.bukkit.common.conversion.type.TextFormatConverter;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConverter;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.wrappers.*;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.ConverterRegistry;
import com.bergerkiller.mountiplex.conversion.type.CollectionConverter;
import com.bergerkiller.mountiplex.conversion.type.EmptyConverterUnsafe;
import com.bergerkiller.mountiplex.conversion.type.ObjectArrayConverter;

import net.minecraft.server.v1_11_R1.EnumDirection;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Conversion extends ConverterRegistry {

    // Misc
    @SuppressWarnings("rawtypes")
    public static final EmptyConverterUnsafe NONE = EmptyConverterUnsafe.INSTANCE;
    // Primitives
    public static final PrimitiveConverter<String> toString = PrimitiveConverter.toString;
    public static final PrimitiveConverter<Byte> toByte = PrimitiveConverter.toByte;
    public static final PrimitiveConverter<Short> toShort = PrimitiveConverter.toShort;
    public static final PrimitiveConverter<Integer> toInt = PrimitiveConverter.toInt;
    public static final PrimitiveConverter<Long> toLong = PrimitiveConverter.toLong;
    public static final PrimitiveConverter<Float> toFloat = PrimitiveConverter.toFloat;
    public static final PrimitiveConverter<Double> toDouble = PrimitiveConverter.toDouble;
    public static final PrimitiveConverter<Boolean> toBool = PrimitiveConverter.toBool;
    public static final PrimitiveConverter<Character> toChar = PrimitiveConverter.toChar;
    // Handles
    public static final HandleConverter toEntityHandle = HandleConverter.toEntityHandle;
    public static final HandleConverter toWorldHandle = HandleConverter.toWorldHandle;
    public static final HandleConverter toChunkHandle = HandleConverter.toChunkHandle;
    public static final HandleConverter toItemStackHandle = HandleConverter.toItemStackHandle;
    public static final HandleConverter toItemHandle = HandleConverter.toItemHandle;
    public static final HandleConverter toTileEntityHandle = HandleConverter.toTileEntityHandle;
    public static final HandleConverter toInventoryHandle = HandleConverter.toInventoryHandle;
    public static final HandleConverter toDataWatcherHandle = HandleConverter.toDataWatcherHandle;
    public static final HandleConverter toDataWatcherObjectHandle = HandleConverter.toDataWatcherObjectHandle;
    public static final HandleConverter toDataWatcherItemHandle = HandleConverter.toDataWatcherItemHandle;
    public static final HandleConverter toNBTTagHandle = HandleConverter.toNBTTagHandle;
    public static final HandleConverter toBlockHandle = HandleConverter.toBlockHandle;
    public static final HandleConverter toGameModeHandle = HandleConverter.toGameModeHandle;
    public static final HandleConverter toWorldTypeHandle = HandleConverter.toWorldHandle;
    public static final HandleConverter toDifficultyHandle = HandleConverter.toDifficultyHandle;
    public static final HandleConverter toPacketHandle = HandleConverter.toPacketHandle;
    public static final HandleConverter toVec3DHandle = HandleConverter.toVec3DHandle;
    public static final HandleConverter toChunkCoordIntPairHandle = HandleConverter.toChunkCoordIntPairHandle;
    public static final HandleConverter toBlockPositionHandle = HandleConverter.toBlockPositionHandle;
    public static final HandleConverter toPlayerAbilitiesHandle = HandleConverter.toPlayerAbilitiesHandle;
    public static final HandleConverter toEntityTrackerHandle = HandleConverter.toEntityTrackerHandle;
    public static final HandleConverter toLongHashMapHandle = HandleConverter.toLongHashMapHandle;
    public static final HandleConverter toLongHashSetHandle = HandleConverter.toLongHashSetHandle;
    public static final HandleConverter toIntHashMapHandle = HandleConverter.toIntHashMapHandle;
    public static final HandleConverter toUseActionHandle = HandleConverter.toUseActionHandle;
    public static final HandleConverter toScoreboardActionHandle = HandleConverter.toScoreboardActionHandle;
    public static final HandleConverter toMainHandHandle = HandleConverter.toMainHandHandle;
    public static final HandleConverter toBlockDataHandle = HandleConverter.toBlockDataHandle;
    public static final HandleConverter toChunkSectionHandle = HandleConverter.toChunkSectionHandle;
    public static final HandleConverter toMobEffectList = HandleConverter.toMobEffectList;
    public static final HandleConverter toMobEffect = HandleConverter.toMobEffect;
    public static final HandleConverter toMapIconHandle = HandleConverter.toMapIconHandle;
    // Wrappers
    public static final WrapperConverter<Entity> toEntity = WrapperConverter.create(Entity.class);
    public static final Converter<Player> toPlayer = WrapperConverter.create(Player.class);
    public static final Converter<HumanEntity> toHumanEntity = WrapperConverter.create(HumanEntity.class);
    public static final Converter<Item> toItem = toEntity.cast(Item.class);
    public static final WrapperConverter<World> toWorld = WrapperConverter.create(World.class);
    public static final WrapperConverter<Chunk> toChunk = WrapperConverter.create(Chunk.class);
    public static final WrapperConverter<Block> toBlock = WrapperConverter.create(Block.class);
    public static final WrapperConverter<BlockState> toBlockState = WrapperConverter.create(BlockState.class);
    public static final WrapperConverter<CommonTag> toCommonTag = WrapperConverter.create(CommonTag.class);
    public static final WrapperConverter<DataWatcher> toDataWatcher = WrapperConverter.create(DataWatcher.class);
    public static final WrapperConverter<DataWatcher.Key<?>> toDataWatcherKey = WrapperConverter.create(DataWatcher.Key.class);
    public static final WrapperConverter<DataWatcher.Item<?>> toDataWatcherItem = WrapperConverter.create(DataWatcher.Item.class);
    public static final WrapperConverter<ItemStack> toItemStack = WrapperConverter.create(ItemStack.class);
    public static final WrapperConverter<Material> toMaterial = WrapperConverter.create(Material.class);
    public static final WrapperConverter<Inventory> toInventory = WrapperConverter.create(Inventory.class);
    public static final WrapperConverter<Difficulty> toDifficulty = WrapperConverter.create(Difficulty.class);
    public static final WrapperConverter<WorldType> toWorldType = WrapperConverter.create(WorldType.class);
    public static final WrapperConverter<GameMode> toGameMode = WrapperConverter.create(GameMode.class);
    public static final WrapperConverter<CommonPacket> toCommonPacket = WrapperConverter.create(CommonPacket.class);
    public static final WrapperConverter<IntVector2> toIntVector2 = WrapperConverter.create(IntVector2.class);
    public static final WrapperConverter<IntVector3> toIntVector3 = WrapperConverter.create(IntVector3.class);
    public static final WrapperConverter<Vector> toVector = WrapperConverter.create(Vector.class);
    public static final WrapperConverter<PlayerAbilities> toPlayerAbilities = WrapperConverter.create(PlayerAbilities.class);
    public static final WrapperConverter<EntityTracker> toEntityTracker = WrapperConverter.create(EntityTracker.class);
    public static final WrapperConverter<LongHashSet> toLongHashSet = WrapperConverter.create(LongHashSet.class);
    public static final WrapperConverter<LongHashMap<Object>> toLongHashMap = WrapperConverter.create(LongHashMap.class);
    public static final WrapperConverter<IntHashMap<Object>> toIntHashMap = WrapperConverter.create(IntHashMap.class);
    public static final WrapperConverter<ScoreboardAction> toScoreboardAction = WrapperConverter.create(ScoreboardAction.class);
    public static final WrapperConverter<UseAction> toUseAction = WrapperConverter.create(UseAction.class);
    public static final WrapperConverter<org.bukkit.inventory.MainHand> toMainHand = WrapperConverter.create(org.bukkit.inventory.MainHand.class);
    public static final WrapperConverter<BlockData> toBlockData = WrapperConverter.create(BlockData.class);
    public static final WrapperConverter<ChunkSection> toChunkSection = WrapperConverter.create(ChunkSection.class);
    public static final WrapperConverter<PotionEffectType> toPotionEffectType = WrapperConverter.create(PotionEffectType.class);
    public static final WrapperConverter<PotionEffect> toPotionEffect = WrapperConverter.create(PotionEffect.class);
    public static final WrapperConverter<MapCursor> toMapCursor = WrapperConverter.create(MapCursor.class);
    // Text Format Conversion
    public static final TextFormatConverter<String> chatComponentToText = TextFormatConverter.chatComponentToText;
    public static final TextFormatConverter<Object> textToChatComponent = TextFormatConverter.textToChatComponent;
    public static final TextFormatConverter<String> chatComponentToJson = TextFormatConverter.chatComponentToJson;
    public static final TextFormatConverter<Object> jsonToChatComponent = TextFormatConverter.jsonToChatComponent;
    public static final TextFormatConverter<String> chatJsonToText = TextFormatConverter.jsonToText;
    public static final TextFormatConverter<String> chatTextToJson = TextFormatConverter.textToJson;
    // Arrays
    public static final Converter<ItemStack[]> toItemStackArr = toItemStack.toArray();
    public static final Converter<Object[]> toItemStackHandleArr = toItemStackHandle.toArray();
    public static final ObjectArrayConverter<Object[]> toObjectArr = ObjectArrayConverter.toObjectArr;
    public static final PrimitiveArrayConverter<boolean[]> toBoolArr = PrimitiveArrayConverter.toBoolArr;
    public static final PrimitiveArrayConverter<char[]> toCharArr = PrimitiveArrayConverter.toCharArr;
    public static final PrimitiveArrayConverter<byte[]> toByteArr = PrimitiveArrayConverter.toByteArr;
    public static final PrimitiveArrayConverter<short[]> toShortArr = PrimitiveArrayConverter.toShortArr;
    public static final PrimitiveArrayConverter<int[]> toIntArr = PrimitiveArrayConverter.toIntArr;
    public static final PrimitiveArrayConverter<long[]> toLongArr = PrimitiveArrayConverter.toLongArr;
    public static final PrimitiveArrayConverter<float[]> toFloatArr = PrimitiveArrayConverter.toFloatArr;
    public static final PrimitiveArrayConverter<double[]> toDoubleArr = PrimitiveArrayConverter.toDoubleArr;
    // Collections
    public static final CollectionConverter<List<?>> toList = CollectionConverter.toList;
    public static final CollectionConverter<Set<?>> toSet = CollectionConverter.toSet;
    public static final CollectionConverter<Collection<?>> toCollection = CollectionConverter.toCollection;
    // Properties
    @Deprecated
    public static final PropertyConverter<Integer> toItemId = PropertyConverter.toItemId;
    public static final PropertyConverter<Material> toItemMaterial = PropertyConverter.toItemMaterial;
    public static final PropertyConverter<Integer> toPaintingFacingId = PropertyConverter.toPaintingFacingId;
    public static final PropertyConverter<EnumDirection> toPaintingFacing = PropertyConverter.toPaintingFacing;
    public static final PropertyConverter<EntityType> toMinecartType = PropertyConverter.toMinecartType;
    public static final PropertyConverter<Object> toGameProfileFromId = PropertyConverter.toGameProfileFromId;
    public static final PropertyConverter<UUID> toGameProfileId = PropertyConverter.toGameProfileId;

    static {
        registerAll(Conversion.class);
    }
}