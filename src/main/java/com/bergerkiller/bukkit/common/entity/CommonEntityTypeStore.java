package com.bergerkiller.bukkit.common.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.bergerkiller.bukkit.common.ClassInstanceMap;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

public class CommonEntityTypeStore {
	public static final CommonEntityType UNKNOWN = new CommonEntityType(EntityType.UNKNOWN, "Entity", 80, 3, true);
	private static final ClassInstanceMap<CommonEntityType> byNMS = new ClassInstanceMap<CommonEntityType>();

	private static void add(EntityType entityType, String nmsName, int networkViewDistance, int networkUpdateInterval, boolean networkIsMobile) {
		final CommonEntityType info = new CommonEntityType(entityType, nmsName, networkViewDistance, networkUpdateInterval, networkIsMobile);
		byNMS.put(info.nmsType, info);
	}

	public static CommonEntityType byEntity(Entity entity) {
		return byNMSEntity(Conversion.toEntityHandle.convert(entity));
	}

	public static CommonEntityType byNMSEntity(Object entityHandle) {
		return LogicUtil.fixNull(byNMS.get(entityHandle), UNKNOWN);
	}

	static{
		// Register all available entities here
		// Misc
		add(EntityType.PLAYER, "Player", 512, 2, true);
		add(EntityType.FISHING_HOOK, "FishingHook", 64, 5, true);
		add(EntityType.ARROW, "Arrow", 64, 20, false);
		add(EntityType.SMALL_FIREBALL, "SmallFireball", 64, 10, false);
		add(EntityType.FIREBALL, "Fireball", 64, 10, false);
		add(EntityType.SNOWBALL, "Snowball", 64, 10, true);
		add(EntityType.ENDER_PEARL, "EnderPearl", 64, 10, true);
		add(EntityType.ENDER_SIGNAL, "EnderSignal", 64, 4, true);
		add(EntityType.EGG, "Egg", 64, 10, true);
		add(EntityType.SPLASH_POTION, "Potion", 64, 10, true);
		add(EntityType.THROWN_EXP_BOTTLE, "ThrownExpBottle", 64, 10, true);
		add(EntityType.FIREWORK, "Fireworks", 64, 10, true);
		add(EntityType.DROPPED_ITEM, "Item", 64, 20, true);
		add(EntityType.EXPERIENCE_ORB, "ExperienceOrb", 160, 20, true);

		// Vehicles
		add(EntityType.MINECART, "MinecartRideable", 80, 3, true);
		add(EntityType.MINECART_CHEST, "MinecartChest", 80, 3, true);
		add(EntityType.MINECART_FURNACE, "MinecartFurnace", 80, 3, true);
		add(EntityType.MINECART_TNT, "MinecartTNT", 80, 3, true);
		add(EntityType.MINECART_HOPPER, "MinecartHopper", 80, 3, true);
		add(EntityType.BOAT, "Boat", 80, 3, true);

		// Mobs
		add(EntityType.BAT, "Bat", 80, 3, false);
		add(EntityType.BLAZE, "Blaze", 80, 3, true);
		add(EntityType.CAVE_SPIDER, "CaveSpider", 80, 3, true);
		add(EntityType.CHICKEN, "Chicken", 80, 3, true);
		add(EntityType.COW, "Cow", 80, 3, true);
		add(EntityType.CREEPER, "Creeper", 80, 3, true);
		add(EntityType.ENDERMAN, "Enderman", 80, 3, true);
		add(EntityType.ENDER_DRAGON, "EnderDragon", 160, 3, true);
		add(EntityType.GHAST, "Ghast", 80, 3, true);
		add(EntityType.GIANT, "GiantZombie", 80, 3, true);
		add(EntityType.IRON_GOLEM, "IronGolem", 80, 3, true);
		add(EntityType.MAGMA_CUBE, "MagmaCube", 80, 3, true);
		add(EntityType.MUSHROOM_COW, "MushroomCow", 80, 3, true);
		add(EntityType.OCELOT, "Ocelot", 80, 3, true);
		add(EntityType.PIG, "Pig", 80, 3, true);
		add(EntityType.PIG_ZOMBIE, "PigZombie", 80, 3, true);
		add(EntityType.SHEEP, "Sheep", 80, 3, true);
		add(EntityType.SILVERFISH, "Silverfish", 80, 3, true);
		add(EntityType.SKELETON, "Skeleton", 80, 3, true);
		add(EntityType.SLIME, "Slime", 80, 3, true);
		add(EntityType.SNOWMAN, "Snowman", 80, 3, true);
		add(EntityType.SPIDER, "Spider", 80, 3, true);
		add(EntityType.SQUID, "Squid", 64, 3, true);
		add(EntityType.WITCH, "Witch", 80, 3, true);
		add(EntityType.WITHER, "Wither", 80, 3, false);
		add(EntityType.WITHER_SKULL, "WitherSkull", 80, 3, true);
		add(EntityType.WOLF, "Wolf", 80, 3, true);
		add(EntityType.ZOMBIE, "Zombie", 80, 3, true);

		// Blocks/Tiles
		add(EntityType.PRIMED_TNT, "TNTPrimed", 160, 10, true);
		add(EntityType.FALLING_BLOCK, "FallingBlock", 160, 20, true);
		add(EntityType.PAINTING, "Painting", 160, Integer.MAX_VALUE, false);
		add(EntityType.ENDER_CRYSTAL, "EnderCrystal", 256, Integer.MAX_VALUE, false);
		add(EntityType.ITEM_FRAME, "ItemFrame", 160, Integer.MAX_VALUE, false);
	}
}
