package com.bergerkiller.bukkit.common.conversion;

import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.PropertyConverter;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;

/**
 * Stores all Converter pairs available
 */
public class ConversionPairs {
	public static final ConverterPair<Object, Entity> entity = Conversion.toEntityHandle.formPair(Conversion.toEntity);
	public static final ConverterPair<Object, World> world = Conversion.toWorldHandle.formPair(Conversion.toWorld);
	public static final ConverterPair<Object, Chunk> chunk = Conversion.toChunkHandle.formPair(Conversion.toChunk);
	public static final ConverterPair<Object, ItemStack> itemStack = Conversion.toItemStackHandle.formPair(Conversion.toItemStack);
	public static final ConverterPair<Byte, Difficulty> difficulty = PropertyConverter.toDifficultyId.formPair(Conversion.toDifficulty);
	public static final ConverterPair<Object, GameMode> gameMode = Conversion.toGameModeHandle.formPair(Conversion.toGameMode);
	public static final ConverterPair<Object, WorldType> worldType = Conversion.toWorldTypeHandle.formPair(Conversion.toWorldType);
	public static final ConverterPair<Object, DataWatcher> dataWatcher = Conversion.toDataWatcherHandle.formPair(Conversion.toDataWatcher);
	public static final ConverterPair<Object, CommonTag> commonTag = Conversion.toNBTTagHandle.formPair(Conversion.toCommonTag);
	public static final ConverterPair<Integer, BlockFace> paintingFacing = Conversion.toPaintingFacingId.formPair(Conversion.toPaintingFacing);
	public static final ConverterPair<Object, IntVector3> chunkCoordinates = Conversion.toChunkCoordinatesHandle.formPair(Conversion.toIntVector3);
	public static final ConverterPair<Object, IntVector3> chunkPosition = Conversion.toChunkPositionHandle.formPair(Conversion.toIntVector3);
	public static final ConverterPair<Object, IntVector2> chunkIntPair = Conversion.toChunkCoordIntPairHandle.formPair(Conversion.toIntVector2);
	public static final ConverterPair<Object, PlayerAbilities> playerAbilities = Conversion.toPlayerAbilitiesHandle.formPair(Conversion.toPlayerAbilities);
}
