package com.bergerkiller.bukkit.common.conversion;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.ConversionTypes;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.bukkit.common.wrappers.UseAction;

import static com.bergerkiller.bukkit.common.conversion.Conversion.*;
import com.bergerkiller.bukkit.common.wrappers.ScoreboardAction;

/**
 * Stores all Converter pairs available
 */
public class ConversionPairs {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final ConverterPair NONE = ConversionTypes.NONE.formPair(ConversionTypes.NONE);
    public static final ConverterPair<Object, Entity> entity = toEntityHandle.formPair(toEntity);
    public static final ConverterPair<Object, Player> player = toEntityHandle.formPair(toPlayer);
    public static final ConverterPair<List<Object>, List<Player>> playerList = toPlayerHandleList.formPair(toPlayerList);
    public static final ConverterPair<Set<Object>, Set<Player>> playerSet = toPlayerHandleSet.formPair(toPlayerSet);
    public static final ConverterPair<List<Object>, List<ItemStack>> itemStackList = toItemStackHandleList.formPair(toItemStackList);
    public static final ConverterPair<Object[], ItemStack[]> itemStackArr = toItemStackHandleArr.formPair(toItemStackArr);
    public static final ConverterPair<Object, World> world = toWorldHandle.formPair(toWorld);
    public static final ConverterPair<Object, Chunk> chunk = toChunkHandle.formPair(toChunk);
    public static final ConverterPair<Object, ItemStack> itemStack = toItemStackHandle.formPair(toItemStack);
    public static final ConverterPair<Object, Inventory> inventory = toInventoryHandle.formPair(toInventory);
    public static final ConverterPair<Object, Difficulty> difficulty = toDifficultyHandle.formPair(toDifficulty);
    public static final ConverterPair<Object, GameMode> gameMode = toGameModeHandle.formPair(toGameMode);
    public static final ConverterPair<Object, WorldType> worldType = toWorldTypeHandle.formPair(toWorldType);
    public static final ConverterPair<Object, DataWatcher> dataWatcher = toDataWatcherHandle.formPair(toDataWatcher);
    public static final ConverterPair<Object, CommonTag> commonTag = toNBTTagHandle.formPair(toCommonTag);
    public static final ConverterPair<Integer, BlockFace> paintingFacing = toPaintingFacingId.formPair(toPaintingFacing);
    public static final ConverterPair<Object, IntVector3> chunkCoordinates = toChunkCoordinatesHandle.formPair(toIntVector3);
    public static final ConverterPair<Object, IntVector3> chunkPosition = toChunkPositionHandle.formPair(toIntVector3);
    public static final ConverterPair<Object, IntVector2> chunkIntPair = toChunkCoordIntPairHandle.formPair(toIntVector2);
    public static final ConverterPair<Object, PlayerAbilities> playerAbilities = toPlayerAbilitiesHandle.formPair(toPlayerAbilities);
    public static final ConverterPair<Object, EntityTracker> entityTracker = toEntityTrackerHandle.formPair(toEntityTracker);
    public static final ConverterPair<Object, LongHashSet> longHashSet = toLongHashSetHandle.formPair(toLongHashSet);
    public static final ConverterPair<Object, LongHashMap<Object>> longHashMap = toLongHashMapHandle.formPair(toLongHashMap);
    public static final ConverterPair<Object, IntHashMap<Object>> intHashMap = toIntHashMapHandle.formPair(toIntHashMap);
    public static final ConverterPair<Object, BlockState> blockState = toTileEntityHandle.formPair(toBlockState);
    public static final ConverterPair<Object, Material> block = toBlockHandle.formPair(toMaterial);
    public static final ConverterPair<Object, Material> item = toItemHandle.formPair(toMaterial);
    public static final ConverterPair<Object, ScoreboardAction> scoreboardAction = toScoreboardActionHandle.formPair(toScoreboardAction);
    public static final ConverterPair<Object, UseAction> useAction = toUseActionHandle.formPair(toUseAction);
    public static final ConverterPair<Object, UUID> gameProfileId = toGameProfileFromId.formPair(toGameProfileId);
}
