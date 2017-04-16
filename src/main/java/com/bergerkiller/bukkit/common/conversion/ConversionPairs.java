package com.bergerkiller.bukkit.common.conversion;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.ConversionTypes;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.*;
import net.minecraft.server.v1_11_R1.EnumDirection;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.bergerkiller.bukkit.common.conversion.Conversion.*;

/**
 * Stores all Converter pairs available
 */
public class ConversionPairs {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final ConverterPair NONE = ConversionTypes.NONE.formPair(ConversionTypes.NONE);
    
    public static final ConverterPair<Object, Entity> entity = toEntityHandle.formPair(toEntity);
    public static final ConverterPair<Object, Player> player = toEntityHandle.formPair(toPlayer);
    public static final ConverterPair<Object[], ItemStack[]> itemStackArr = toItemStackHandleArr.formPair(toItemStackArr);
    public static final ConverterPair<Object, World> world = toWorldHandle.formPair(toWorld);
    public static final ConverterPair<Object, Chunk> chunk = toChunkHandle.formPair(toChunk);
    public static final ConverterPair<Object, ItemStack> itemStack = toItemStackHandle.formPair(toItemStack);
    public static final ConverterPair<Object, Inventory> inventory = toInventoryHandle.formPair(toInventory);
    public static final ConverterPair<Object, Difficulty> difficulty = toDifficultyHandle.formPair(toDifficulty);
    public static final ConverterPair<Object, GameMode> gameMode = toGameModeHandle.formPair(toGameMode);
    public static final ConverterPair<Object, WorldType> worldType = toWorldTypeHandle.formPair(toWorldType);
    public static final ConverterPair<Object, DataWatcher> dataWatcher = toDataWatcherHandle.formPair(toDataWatcher);
    public static final ConverterPair<Object, DataWatcher.Key<?>> dataWatcherKey = toDataWatcherObjectHandle.formPair(toDataWatcherKey);
    public static final ConverterPair<Object, DataWatcher.Item<?>> dataWatcherItem = toDataWatcherItemHandle.formPair(toDataWatcherItem);
    public static final ConverterPair<Object, CommonTag> commonTag = toNBTTagHandle.formPair(toCommonTag);
    public static final ConverterPair<Object, CommonTagCompound> commonTagCompound = CommonUtil.unsafeCast(commonTag);
    public static final ConverterPair<Object, CommonTagList> commonTagList = CommonUtil.unsafeCast(commonTag);
    public static final ConverterPair<Integer, EnumDirection> paintingFacing = toPaintingFacingId.formPair(toPaintingFacing);
    public static final ConverterPair<Object, IntVector3> blockPosition = toBlockPositionHandle.formPair(toIntVector3);
    public static final ConverterPair<Object, IntVector2> chunkIntPair = toChunkCoordIntPairHandle.formPair(toIntVector2);
    public static final ConverterPair<Object, Vector> vector = toVec3DHandle.formPair(toVector);
    public static final ConverterPair<Object, PlayerAbilities> playerAbilities = toPlayerAbilitiesHandle.formPair(toPlayerAbilities);
    public static final ConverterPair<Object, EntityTracker> entityTracker = toEntityTrackerHandle.formPair(toEntityTracker);
    public static final ConverterPair<Object, LongHashSet> longHashSet = toLongHashSetHandle.formPair(toLongHashSet);
    public static final ConverterPair<Object, LongHashMap<Object>> longHashMap = toLongHashMapHandle.formPair(toLongHashMap);
    public static final ConverterPair<Object, IntHashMap<Object>> intHashMap = toIntHashMapHandle.formPair(toIntHashMap);
    public static final ConverterPair<Object, BlockState> blockState = toTileEntityHandle.formPair(toBlockState);
    public static final ConverterPair<Object, Material> block = toBlockHandle.formPair(toMaterial);
    public static final ConverterPair<Object, Material> item = toItemHandle.formPair(toMaterial);
    public static final ConverterPair<Object, UseAction> useAction = toUseActionHandle.formPair(toUseAction);
    public static final ConverterPair<Object, UUID> gameProfileId = toGameProfileFromId.formPair(toGameProfileId);
    public static final ConverterPair<Object, ScoreboardAction> scoreboardAction = toScoreboardActionHandle.formPair(toScoreboardAction);
    public static final ConverterPair<Object, MainHand> mainHand = toMainHandHandle.formPair(toMainHand);
    public static final ConverterPair<Object, BlockData> blockData = toBlockDataHandle.formPair(toBlockData);
    public static final ConverterPair<Object, ChunkSection> chunkSection = toChunkSectionHandle.formPair(toChunkSection);
    public static final ConverterPair<Object, PotionEffectType> potionEffectType = toMobEffectList.formPair(toPotionEffectType);
    public static final ConverterPair<Object, PotionEffect> potionEffect = toMobEffect.formPair(toPotionEffect);
    public static final ConverterPair<Object, MapCursor> mapCursor = toMapIconHandle.formPair(toMapCursor);
    // Text format conversion <>
    public static final ConverterPair<Object, String> jsonChatComponent = jsonToChatComponent.formPair(chatComponentToJson);
    public static final ConverterPair<Object, String> textChatComponent = textToChatComponent.formPair(chatComponentToText);
    public static final ConverterPair<String, String> jsonChatText = chatJsonToText.formPair(chatTextToJson);

    // Collection element transformation
    public static final ConverterPair<List<Object>, List<Entity>> entityList = entity.toList();
    public static final ConverterPair<List<Object>, List<Player>> playerList = player.toList();
    public static final ConverterPair<Set<Object>, Set<Player>> playerSet = player.toSet();
    public static final ConverterPair<Collection<Object>, Collection<Chunk>> chunkCollection = chunk.toCollection();
    public static final ConverterPair<List<Object>, List<ItemStack>> itemStackList = itemStack.toList();
    public static final ConverterPair<List<Object>, List<DataWatcher>> dataWatcherList = dataWatcher.toList();
    public static final ConverterPair<Object[], String[]> textChatComponentArray = textChatComponent.toArray();
    public static final ConverterPair<Object[], ChunkSection[]> chunkSectionArray = chunkSection.toArray();
    public static final ConverterPair<List<Object>, List<DataWatcher.Item<?>>> dataWatcherItemList = dataWatcherItem.toList();
    public static final ConverterPair<Object[], MapCursor[]> mapCursorArray = mapCursor.toArray();

}
