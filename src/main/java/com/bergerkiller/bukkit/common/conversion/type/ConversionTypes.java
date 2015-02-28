package com.bergerkiller.bukkit.common.conversion.type;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Converter;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.bukkit.common.wrappers.ScoreboardAction;
import com.bergerkiller.bukkit.common.wrappers.UseAction;

public class ConversionTypes {

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
    // Arrays
    public static final ObjectArrayConverter<ItemStack[]> toItemStackArr = ObjectArrayConverter.toItemStackArr;
    public static final ObjectArrayConverter<Object[]> toItemStackHandleArr = ObjectArrayConverter.toItemStackHandleArr;
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
    public static final CollectionTypeConverter<List<Player>, List<?>> toPlayerList = CollectionTypeConverter.toPlayerList;
    public static final CollectionTypeConverter<List<Object>, List<?>> toPlayerHandleList = CollectionTypeConverter.toPlayerHandleList;
    public static final CollectionTypeConverter<Set<Player>, Set<?>> toPlayerSet = CollectionTypeConverter.toPlayerSet;
    public static final CollectionTypeConverter<Set<Object>, Set<?>> toPlayerHandleSet = CollectionTypeConverter.toPlayerHandleSet;
    public static final CollectionTypeConverter<List<ItemStack>, List<?>> toItemStackList = CollectionTypeConverter.toItemStackList;
    public static final CollectionTypeConverter<List<Object>, List<?>> toItemStackHandleList = CollectionTypeConverter.toItemStackHandleList;
    public static final CollectionConverter<Set<?>> toSet = CollectionConverter.toSet;
    // Handles
    public static final HandleConverter toEntityHandle = HandleConverter.toEntityHandle;
    public static final HandleConverter toWorldHandle = HandleConverter.toWorldHandle;
    public static final HandleConverter toChunkHandle = HandleConverter.toChunkHandle;
    public static final HandleConverter toItemStackHandle = HandleConverter.toItemStackHandle;
    public static final HandleConverter toItemHandle = HandleConverter.toItemHandle;
    public static final HandleConverter toTileEntityHandle = HandleConverter.toTileEntityHandle;
    public static final HandleConverter toInventoryHandle = HandleConverter.toInventoryHandle;
    public static final HandleConverter toDataWatcherHandle = HandleConverter.toDataWatcherHandle;
    public static final HandleConverter toNBTTagHandle = HandleConverter.toNBTTagHandle;
    public static final HandleConverter toBlockHandle = HandleConverter.toBlockHandle;
    public static final HandleConverter toGameModeHandle = HandleConverter.toGameModeHandle;
    public static final HandleConverter toWorldTypeHandle = HandleConverter.toWorldHandle;
    public static final HandleConverter toDifficultyHandle = HandleConverter.toDifficultyHandle;
    public static final HandleConverter toPacketHandle = HandleConverter.toPacketHandle;
    public static final HandleConverter toVec3DHandle = HandleConverter.toVec3DHandle;
    public static final HandleConverter toChunkCoordIntPairHandle = HandleConverter.toChunkCoordIntPairHandle;
    public static final HandleConverter toChunkCoordinatesHandle = HandleConverter.toChunkCoordinatesHandle;
    public static final HandleConverter toChunkPositionHandle = HandleConverter.toChunkPositionHandle;
    public static final HandleConverter toPlayerAbilitiesHandle = HandleConverter.toPlayerAbilitiesHandle;
    public static final HandleConverter toEntityTrackerHandle = HandleConverter.toEntityTrackerHandle;
    public static final HandleConverter toLongHashMapHandle = HandleConverter.toLongHashMapHandle;
    public static final HandleConverter toLongHashSetHandle = HandleConverter.toLongHashSetHandle;
    public static final HandleConverter toIntHashMapHandle = HandleConverter.toIntHashMapHandle;
    public static final HandleConverter toScoreboardActionHandle = HandleConverter.toScoreboardActionHandle;
    public static final HandleConverter toUseActionHandle = HandleConverter.toUseActionHandle;
    // Wrappers
    public static final WrapperConverter<Entity> toEntity = WrapperConverter.toEntity;
    public static final Converter<Player> toPlayer = WrapperConverter.toEntity.cast(Player.class);
    public static final WrapperConverter<World> toWorld = WrapperConverter.toWorld;
    public static final WrapperConverter<Chunk> toChunk = WrapperConverter.toChunk;
    public static final WrapperConverter<Block> toBlock = WrapperConverter.toBlock;
    public static final WrapperConverter<BlockState> toBlockState = WrapperConverter.toBlockState;
    public static final WrapperConverter<CommonTag> toCommonTag = WrapperConverter.toCommonTag;
    public static final WrapperConverter<DataWatcher> toDataWatcher = WrapperConverter.toDataWatcher;
    public static final WrapperConverter<ItemStack> toItemStack = WrapperConverter.toItemStack;
    public static final WrapperConverter<Material> toMaterial = WrapperConverter.toMaterial;
    public static final WrapperConverter<Inventory> toInventory = WrapperConverter.toInventory;
    public static final WrapperConverter<Difficulty> toDifficulty = WrapperConverter.toDifficulty;
    public static final WrapperConverter<WorldType> toWorldType = WrapperConverter.toWorldType;
    public static final WrapperConverter<GameMode> toGameMode = WrapperConverter.toGameMode;
    public static final WrapperConverter<CommonPacket> toCommonPacket = WrapperConverter.toCommonPacket;
    public static final WrapperConverter<IntVector2> toIntVector2 = WrapperConverter.toIntVector2;
    public static final WrapperConverter<IntVector3> toIntVector3 = WrapperConverter.toIntVector3;
    public static final WrapperConverter<Vector> toVector = WrapperConverter.toVector;
    public static final WrapperConverter<PlayerAbilities> toPlayerAbilities = WrapperConverter.toPlayerAbilities;
    public static final WrapperConverter<EntityTracker> toEntityTracker = WrapperConverter.toEntityTracker;
    public static final WrapperConverter<LongHashSet> toLongHashSet = WrapperConverter.toLongHashSet;
    public static final WrapperConverter<LongHashMap<Object>> toLongHashMap = WrapperConverter.toLongHashMap;
    public static final WrapperConverter<IntHashMap<Object>> toIntHashMap = WrapperConverter.toIntHashMap;
    public static final WrapperConverter<ScoreboardAction> toScoreboardAction = WrapperConverter.toScoreboardAction;
    public static final WrapperConverter<UseAction> toUseAction = WrapperConverter.toUseAction;
    // Properties
    @Deprecated
    public static final PropertyConverter<Integer> toItemId = PropertyConverter.toItemId;
    public static final PropertyConverter<Material> toItemMaterial = PropertyConverter.toItemMaterial;
    public static final PropertyConverter<Integer> toPaintingFacingId = PropertyConverter.toPaintingFacingId;
    public static final PropertyConverter<BlockFace> toPaintingFacing = PropertyConverter.toPaintingFacing;
    public static final PropertyConverter<EntityType> toMinecartType = PropertyConverter.toMinecartType;
    public static final PropertyConverter<Object> toGameProfileFromId = PropertyConverter.toGameProfileFromId;
    public static final PropertyConverter<UUID> toGameProfileId = PropertyConverter.toGameProfileId;
}
