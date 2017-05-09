package com.bergerkiller.bukkit.common.conversion2;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.server.v1_11_R1.EnumDirection;

import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
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

import static com.bergerkiller.mountiplex.conversion.type.DuplexConverter.pair;
import static com.bergerkiller.bukkit.common.conversion.Conversion.*;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.bukkit.common.wrappers.ScoreboardAction;
import com.bergerkiller.bukkit.common.wrappers.UseAction;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;

public class DuplexConversion {
    @SuppressWarnings({"rawtypes"})
    public static final DuplexConverter NONE = DuplexConverter.createNull(TypeDeclaration.OBJECT);

    public static final DuplexConverter<Object, Entity> entity = pair(toEntity, toEntityHandle);
    public static final DuplexConverter<Object, Player> player = pair(toPlayer, toEntityPlayerHandle);
    public static final DuplexConverter<Object[], ItemStack[]> itemStackArr = pair(toItemStackArr, toItemStackHandleArr);
    public static final DuplexConverter<Object, World> world = pair(toWorld, toWorldHandle);
    public static final DuplexConverter<Object, Chunk> chunk = pair(toChunk, toChunkHandle);
    public static final DuplexConverter<Object, ItemStack> itemStack = pair(toItemStack, toItemStackHandle);
    public static final DuplexConverter<Object, Inventory> inventory = pair(toInventory, toInventoryHandle);
    public static final DuplexConverter<Object, Difficulty> difficulty = pair(toDifficulty, toDifficultyHandle);
    public static final DuplexConverter<Object, GameMode> gameMode = pair(toGameMode, toGameModeHandle);
    public static final DuplexConverter<Object, WorldType> worldType = pair(toWorldType, toWorldTypeHandle);
    public static final DuplexConverter<Object, DataWatcher> dataWatcher = pair(toDataWatcher, toDataWatcherHandle);
    public static final DuplexConverter<Object, DataWatcher.Key<?>> dataWatcherKey = pair(toDataWatcherKey, toDataWatcherObjectHandle);
    public static final DuplexConverter<Object, DataWatcher.Item<?>> dataWatcherItem = pair(toDataWatcherItem, toDataWatcherItemHandle);
    public static final DuplexConverter<Object, CommonTag> commonTag = pair(toCommonTag, toNBTTagHandle);
    public static final DuplexConverter<Object, CommonTagCompound> commonTagCompound = findNMS("NBTTagCompound", CommonTagCompound.class);
    public static final DuplexConverter<Object, CommonTagList> commonTagList = findNMS("NBTTagList", CommonTagList.class);
    public static final DuplexConverter<Integer, EnumDirection> paintingFacing = pair(toPaintingFacing, toPaintingFacingId);
    public static final DuplexConverter<Object, IntVector3> blockPosition = pair(toIntVector3, toBlockPositionHandle);
    public static final DuplexConverter<Object, IntVector2> chunkIntPair = pair(toIntVector2, toChunkCoordIntPairHandle);
    public static final DuplexConverter<Object, Vector> vector = pair(toVector, toVec3DHandle);
    public static final DuplexConverter<Object, PlayerAbilities> playerAbilities = pair(toPlayerAbilities, toPlayerAbilitiesHandle);
    public static final DuplexConverter<Object, EntityTracker> entityTracker = pair(toEntityTracker, toEntityTrackerHandle);
    public static final DuplexConverter<Object, LongHashSet> longHashSet = pair(toLongHashSet, toLongHashSetHandle);
    public static final DuplexConverter<Object, LongHashMap<Object>> longHashMap = pair(toLongHashMap, toLongHashMapHandle);
    public static final DuplexConverter<Object, IntHashMap<Object>> intHashMap = pair(toIntHashMap, toIntHashMapHandle);
    public static final DuplexConverter<Object, BlockState> blockState = pair(toBlockState, toTileEntityHandle);
    public static final DuplexConverter<Object, Material> block = pair(toMaterial, toBlockHandle);
    public static final DuplexConverter<Object, Material> item = pair(toMaterial, toItemHandle);
    public static final DuplexConverter<Object, UseAction> useAction = pair(toUseAction, toUseActionHandle);
    public static final DuplexConverter<Object, UUID> gameProfileId = pair(toGameProfileId, toGameProfileFromId);
    public static final DuplexConverter<Object, ScoreboardAction> scoreboardAction = pair(toScoreboardAction, toScoreboardActionHandle);
    public static final DuplexConverter<Object, MainHand> mainHand = pair(toMainHand, toMainHandHandle);
    public static final DuplexConverter<Object, BlockData> blockData = pair(toBlockData, toBlockDataHandle);
    public static final DuplexConverter<Object, ChunkSection> chunkSection = pair(toChunkSection, toChunkSectionHandle);
    public static final DuplexConverter<Object, PotionEffectType> potionEffectType = pair(toPotionEffectType, toMobEffectList);
    public static final DuplexConverter<Object, PotionEffect> potionEffect = pair(toPotionEffect, toMobEffect);
    public static final DuplexConverter<Object, MapCursor> mapCursor = pair(toMapCursor, toMapIconHandle);
    public static final DuplexConverter<Object, ChatText> chatText = pair(toChatText, toChatComponentHandle);

    // Collection element transformation
    public static final DuplexConverter<List<Object>, List<Entity>> entityList = pairElem(List.class, entity);
    public static final DuplexConverter<List<Object>, List<Player>> playerList = pairElem(List.class, player);
    public static final DuplexConverter<Set<Object>, Set<Player>> playerSet = pairElem(Set.class, player);
    public static final DuplexConverter<Collection<Object>, Collection<Chunk>> chunkCollection = pairElem(Collection.class, chunk);
    public static final DuplexConverter<List<Object>, List<ItemStack>> itemStackList = pairElem(List.class, itemStack);
    public static final DuplexConverter<List<Object>, List<DataWatcher>> dataWatcherList = pairElem(List.class, dataWatcher);
    public static final DuplexConverter<List<Object>, List<DataWatcher.Item<?>>> dataWatcherItemList = pairElem(List.class, dataWatcherItem);
    public static final DuplexConverter<Collection<Object>, Collection<BlockState>> blockStateCollection = pairElem(Collection.class, blockState);
    public static final DuplexConverter<Object[], ChatText[]> chatTextArray = pairArray(chatText);
    public static final DuplexConverter<Object[], ChunkSection[]> chunkSectionArray = pairArray(chunkSection);
    public static final DuplexConverter<Object[], MapCursor[]> mapCursorArray = pairArray(mapCursor);

    @SuppressWarnings("unchecked")
    private static final <T> T pairElem(Class<?> type, DuplexConverter<?, ?> elementConverter) {
        TypeDeclaration input = TypeDeclaration.createGeneric(type, elementConverter.input);
        TypeDeclaration output = TypeDeclaration.createGeneric(type, elementConverter.output);
        return (T) Conversion.findDuplex(input, output);
    }

    @SuppressWarnings("unchecked")
    private static final <T> T pairArray(DuplexConverter<?, ?> elementConverter) {
        TypeDeclaration input = TypeDeclaration.createArray(elementConverter.input.type);
        TypeDeclaration output = TypeDeclaration.createArray(elementConverter.output.type);
        return (T) Conversion.findDuplex(input, output);
    }

    private static final <T> T findNMS(String nmsClassName, Class<?> output) {
        return find(CommonUtil.getNMSClass(nmsClassName), output);
    }

    @SuppressWarnings("unchecked")
    private static final <T> T find(Class<?> input, Class<?> output) {
        return (T) Conversion.findDuplex(input, output);
    }
}
