package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Common;

class CommonClasses {
	/* 
	 * In here we pre-load several classes to prevent unexpected ClassNotFound exceptions
	 * This is required, as someone might be calling one of our classes from the main server thread
	 * Then the main server thread class loader is used, which is unable to find (our) net.minecraft.server references
	 */
	public static void init() {
		// Reflection classes
		loadRef("BlockState", "ChunkProviderServer", "CraftScheduler", "CraftServer", "CraftTask", "EntityMinecart", "EntityPlayer");
		loadRef("Entity", "EntityTrackerEntry", "EntityTracker", "EntityTypes", "LongHashMapEntry", "LongHashMap", "NetworkManager");
		loadRef("Packet51MapChunk", "Packet", "PlayerInstance", "PlayerManager", "PluginDescriptionFile", "RegionFileCache");
		loadRef("RegionFile", "TileEntity", "WorldServer", "Chunk", "ChunkSection");
		// Utility classes (only those that interact with nms)
		loadUtil("Block", "Chunk", "Common", "EntityProperty", "Entity", "Item", "Material", "Native", "NBT", "Packet");
		loadUtil("Recipe", "Stream", "World");
	}

	private static void loadRef(String... classNames) {
		for (int i = 0; i < classNames.length; i++) {
			classNames[i] = "com.bergerkiller.bukkit.common.reflection.classes." + classNames[i] + "Ref";
		}
		Common.loadClasses(classNames);
	}

	private static void loadUtil(String... classNames) {
		for (int i = 0; i < classNames.length; i++) {
			classNames[i] = "com.bergerkiller.bukkit.common.utils." + classNames[i] + "Util";
		}
		Common.loadClasses(classNames);
	}
}
