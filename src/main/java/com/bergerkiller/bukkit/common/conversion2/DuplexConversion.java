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

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.bukkit.common.wrappers.ScoreboardAction;
import com.bergerkiller.bukkit.common.wrappers.UseAction;
import com.bergerkiller.mountiplex.conversion.ConverterPair;
import com.bergerkiller.mountiplex.conversion2.type.DuplexConverter;

public class DuplexConversion {
    @SuppressWarnings({"rawtypes"})
    public static final DuplexConverter NONE = ConversionPairs.NONE.toNew();

    public static final DuplexConverter<Object, Entity> entity = ConversionPairs.entity.toNew();
    public static final DuplexConverter<Object, Player> player = ConversionPairs.player.toNew();
    public static final DuplexConverter<Object[], ItemStack[]> itemStackArr = ConversionPairs.itemStackArr.toNew();
    public static final DuplexConverter<Object, World> world = ConversionPairs.world.toNew();
    public static final DuplexConverter<Object, Chunk> chunk = ConversionPairs.chunk.toNew();
    public static final DuplexConverter<Object, ItemStack> itemStack = ConversionPairs.itemStack.toNew();
    public static final DuplexConverter<Object, Inventory> inventory = ConversionPairs.inventory.toNew();
    public static final DuplexConverter<Object, Difficulty> difficulty = ConversionPairs.difficulty.toNew();
    public static final DuplexConverter<Object, GameMode> gameMode = ConversionPairs.gameMode.toNew();
    public static final DuplexConverter<Object, WorldType> worldType = ConversionPairs.worldType.toNew();
    public static final DuplexConverter<Object, DataWatcher> dataWatcher = ConversionPairs.dataWatcher.toNew();
    public static final DuplexConverter<Object, DataWatcher.Key<?>> dataWatcherKey = ConversionPairs.dataWatcherKey.toNew();
    public static final DuplexConverter<Object, DataWatcher.Item<?>> dataWatcherItem = ConversionPairs.dataWatcherItem.toNew();
    public static final DuplexConverter<Object, CommonTag> commonTag = ConversionPairs.commonTag.toNew();
    public static final DuplexConverter<Object, CommonTagCompound> commonTagCompound = ConversionPairs.commonTagCompound.toNew();
    public static final DuplexConverter<Object, CommonTagList> commonTagList = ConversionPairs.commonTagList.toNew();
    public static final DuplexConverter<Integer, EnumDirection> paintingFacing = ConversionPairs.paintingFacing.toNew();
    public static final DuplexConverter<Object, IntVector3> blockPosition = ConversionPairs.blockPosition.toNew();
    public static final DuplexConverter<Object, IntVector2> chunkIntPair = ConversionPairs.chunkIntPair.toNew();
    public static final DuplexConverter<Object, Vector> vector = ConversionPairs.vector.toNew();
    public static final DuplexConverter<Object, PlayerAbilities> playerAbilities = ConversionPairs.playerAbilities.toNew();
    public static final DuplexConverter<Object, EntityTracker> entityTracker = ConversionPairs.entityTracker.toNew();
    public static final DuplexConverter<Object, LongHashSet> longHashSet = ConversionPairs.longHashSet.toNew();
    public static final DuplexConverter<Object, LongHashMap<Object>> longHashMap = ConversionPairs.longHashMap.toNew();
    public static final DuplexConverter<Object, IntHashMap<Object>> intHashMap = ConversionPairs.intHashMap.toNew();
    public static final DuplexConverter<Object, BlockState> blockState = ConversionPairs.blockState.toNew();
    public static final DuplexConverter<Object, Material> block = ConversionPairs.block.toNew();
    public static final DuplexConverter<Object, Material> item = ConversionPairs.item.toNew();
    public static final DuplexConverter<Object, UseAction> useAction = ConversionPairs.useAction.toNew();
    public static final DuplexConverter<Object, UUID> gameProfileId = ConversionPairs.gameProfileId.toNew();
    public static final DuplexConverter<Object, ScoreboardAction> scoreboardAction = ConversionPairs.scoreboardAction.toNew();
    public static final DuplexConverter<Object, MainHand> mainHand = ConversionPairs.mainHand.toNew();
    public static final DuplexConverter<Object, BlockData> blockData = ConversionPairs.blockData.toNew();
    public static final DuplexConverter<Object, ChunkSection> chunkSection = ConversionPairs.chunkSection.toNew();
    public static final DuplexConverter<Object, PotionEffectType> potionEffectType = ConversionPairs.potionEffectType.toNew();
    public static final DuplexConverter<Object, PotionEffect> potionEffect = ConversionPairs.potionEffect.toNew();
    public static final DuplexConverter<Object, MapCursor> mapCursor = ConversionPairs.mapCursor.toNew();
    // Text format conversion <>
    public static final DuplexConverter<Object, String> jsonChatComponent = ConversionPairs.jsonChatComponent.toNew();
    public static final DuplexConverter<Object, String> textChatComponent = ConversionPairs.textChatComponent.toNew();
    public static final DuplexConverter<String, String> jsonChatText = ConversionPairs.jsonChatText.toNew();

    // Collection element transformation
    public static final DuplexConverter<List<Object>, List<Entity>> entityList = ConversionPairs.entityList.toNew();
    public static final DuplexConverter<List<Object>, List<Player>> playerList = ConversionPairs.playerList.toNew();
    public static final DuplexConverter<Set<Object>, Set<Player>> playerSet = ConversionPairs.playerSet.toNew();
    public static final DuplexConverter<Collection<Object>, Collection<Chunk>> chunkCollection = ConversionPairs.chunkCollection.toNew();
    public static final DuplexConverter<List<Object>, List<ItemStack>> itemStackList = ConversionPairs.itemStackList.toNew();
    public static final DuplexConverter<List<Object>, List<DataWatcher>> dataWatcherList = ConversionPairs.dataWatcherList.toNew();
    public static final DuplexConverter<Object[], String[]> textChatComponentArray = ConversionPairs.textChatComponentArray.toNew();
    public static final DuplexConverter<Object[], ChunkSection[]> chunkSectionArray = ConversionPairs.chunkSectionArray.toNew();
    public static final DuplexConverter<List<Object>, List<DataWatcher.Item<?>>> dataWatcherItemList = ConversionPairs.dataWatcherItemList.toNew();
    public static final DuplexConverter<Object[], MapCursor[]> mapCursorArray = ConversionPairs.mapCursorArray.toNew();
    public static final DuplexConverter<Collection<Object>, Collection<BlockState>> blockStateCollection = ConversionPairs.blockStateCollection.toNew();
}
