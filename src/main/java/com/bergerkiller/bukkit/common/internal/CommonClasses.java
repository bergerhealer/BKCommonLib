package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Common;

class CommonClasses {
	/* 
	 * In here we pre-load several classes to prevent unexpected ClassNotFound exceptions
	 * This is required, as someone might be calling one of our classes from the main server thread
	 * Then the main server thread class loader is used, which is unable to find (our) net.minecraft.server references
	 */
	public static void init() {
		// Conversion
		Common.loadClasses(Common.COMMON_ROOT + ".conversion.Conversion");
		// Reflection classes
		loadRef("BlockState", "ChunkProviderServer", "CraftScheduler", "CraftServer", "CraftTask", "EntityMinecart", "EntityPlayer");
		loadRef("Entity", "EntityTrackerEntry", "EntityTracker", "EntityTypes", "LongHashMapEntry", "LongHashMap", "NetworkManager");
		loadRef("PlayerChunk", "PlayerChunkMap", "PluginDescriptionFile", "RegionFileCache", "ChunkRegionLoader", "Recipe");
		loadRef("NBT", "RegionFile", "TileEntity", "WorldServer", "Chunk", "ChunkSection", "Block", "Chunk", "World", "WorldServer");
		// Internal
		loadCommon("internal.CommonWorldListener", "internal.CommonTabController");
		// Utility classes (only those that interact with nms)
		loadUtil("Block", "Chunk", "Common", "EntityProperty", "Entity", "Item", "Material", "Native", "NBT", "Packet");
		loadUtil("Recipe", "Stream", "World");
		// Remaining classes
		loadCommon("nbt.NBTTagInfo", "reflection.classes.PacketFieldClasses", "entity.CommonEntityType", "collections.CollectionBasics");
		loadCommon("scoreboards.CommonScoreboard", "scoreboards.CommonTeam");
		loadCommon("protocol.PacketType", "protocol.PacketFields");
	}

	private static void loadRef(String... classNames) {
		for (int i = 0; i < classNames.length; i++) {
			classNames[i] = "reflection.classes." + classNames[i] + "Ref";
		}
		loadCommon(classNames);
	}

	private static void loadUtil(String... classNames) {
		for (int i = 0; i < classNames.length; i++) {
			classNames[i] = "utils." + classNames[i] + "Util";
		}
		loadCommon(classNames);
	}

	private static void loadCommon(String... classNames) {
		for (int i = 0; i < classNames.length; i++) {
			classNames[i] = Common.COMMON_ROOT + "." + classNames[i];
		}
		Common.loadClasses(classNames);
	}
}
