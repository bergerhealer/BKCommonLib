package com.bergerkiller.bukkit.common.utils;

import java.util.ArrayList;
import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Squid;

/**
 * Contains entity naming and grouping functions to categorize entities
 */
@SuppressWarnings("deprecation")
public class EntityGroupingUtil {
	public static final String[] animalNames;
	public static final String[] monsterNames;
	public static final String[] npcNames;

	static {
		ArrayList<String> animals = new ArrayList<String>();
		ArrayList<String> monsters = new ArrayList<String>();
		ArrayList<String> npcs = new ArrayList<String>();
		try {
			for (EntityType type : EntityType.values()) {
				if (isAnimal(type)) {
					animals.add(getName(type));
				}
				if (isMonster(type)) {
					monsters.add(getName(type));
				}
				if (isNPC(type)) {
					npcs.add(getName(type));
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		animalNames = animals.toArray(new String[0]);
		monsterNames = monsters.toArray(new String[0]);
		npcNames = npcs.toArray(new String[0]);
	}

	public static boolean isMob(String name) {
		return isAnimal(name) || isMonster(name) || isNPC(name);
	}

	public static boolean isNPC(String name) {
		return LogicUtil.contains(name.toLowerCase(Locale.ENGLISH), npcNames);
	}

	public static boolean isAnimal(String name) {
		return LogicUtil.contains(name.toLowerCase(Locale.ENGLISH), animalNames);
	}

	public static boolean isMonster(String name) {
		return LogicUtil.contains(name.toLowerCase(Locale.ENGLISH), monsterNames);
	}

	public static boolean isMob(net.minecraft.server.Entity entity) {
		return isMob(entity.getBukkitEntity());
	}

	public static boolean isMob(Entity entity) {
		return isMob(entity.getClass());
	}

	public static boolean isMob(CreatureType type) {
		return isMob(type.toEntityType());
	}

	public static boolean isMob(EntityType entityType) {
		return isMob(entityType.getEntityClass());
	}

	public static boolean isMob(Class<? extends Entity> entityClass) {
		return isAnimal(entityClass) || isMonster(entityClass);
	}

	public static boolean isNPC(net.minecraft.server.Entity entity) {
		return isNPC(entity.getBukkitEntity());
	}

	public static boolean isNPC(Entity entity) {
		return isNPC(entity.getClass());
	}

	public static boolean isNPC(EntityType entityType) {
		return isNPC(entityType.getEntityClass());
	}

	public static boolean isNPC(Class<? extends Entity> entityClass) {
		return entityClass != null && NPC.class.isAssignableFrom(entityClass);
	}

	public static boolean isAnimal(net.minecraft.server.Entity entity) {
		return isAnimal(entity.getBukkitEntity());
	}

	public static boolean isAnimal(Entity entity) {
		return isAnimal(entity.getClass());
	}

	public static boolean isAnimal(CreatureType type) {
		return isAnimal(type.toEntityType());
	}

	public static boolean isAnimal(EntityType entityType) {
		return isAnimal(entityType.getEntityClass());
	}

	public static boolean isAnimal(Class<? extends Entity> entityClass) {
		return entityClass != null && (Animals.class.isAssignableFrom(entityClass) || Squid.class.isAssignableFrom(entityClass));
	}

	public static boolean isMonster(net.minecraft.server.Entity entity) {
		return isMonster(entity.getBukkitEntity());
	}

	public static boolean isMonster(Entity entity) {
		return isMonster(entity.getClass());
	}

	public static boolean isMonster(CreatureType type) {
		return isMonster(type.toEntityType());
	}

	public static boolean isMonster(EntityType entityType) {
		return isMonster(entityType.getEntityClass());
	}

	public static boolean isMonster(Class<? extends Entity> entityClass) {
		return entityClass != null && (Monster.class.isAssignableFrom(entityClass) || Slime.class.isAssignableFrom(entityClass));
	}

	/**
	 * Gets the lower-cased name of a given Entity<br>
	 * - Items will get the name 'item' with the item type appended to it<br>
	 * - Falling blocks will get the name 'falling' with the block type appended to it
	 * 
	 * @param entity to get the name of
	 * @return Entity name
	 */
	public static String getName(net.minecraft.server.Entity entity) {
		return getName(entity.getBukkitEntity());
	}

	/**
	 * Gets the lower-cased name of a given Entity<br>
	 * - Items will get the name 'item' with the item type appended to it<br>
	 * - Falling blocks will get the name 'falling' with the block type appended to it
	 * 
	 * @param entity to get the name of
	 * @return Entity name
	 */
	public static String getName(Entity entity) {
		if (entity == null) {
			return "";
		} else if (entity instanceof Item) {
			Material mat = ((Item) entity).getItemStack().getType();
			if (mat == null || mat == Material.AIR) {
				return "item";
			} else {
				return "item" + mat.toString().toLowerCase(Locale.ENGLISH);
			}
		} else if (entity instanceof FallingBlock) {
			Material mat = ((FallingBlock) entity).getMaterial();
			if (mat == null || mat == Material.AIR) {
				return "fallingblock";
			} else {
				return "falling" + mat.toString().toLowerCase(Locale.ENGLISH);
			}
		} else {
			return getName(entity.getClass());
		}
	}

	/**
	 * Gets the lower-cased name of a given Entity Class
	 * 
	 * @param entityClass to get the name of
	 * @return Entity name
	 */
	public static String getName(Class<? extends Entity> entityClass) {
		if (entityClass == null) {
			return "";
		}
		for (EntityType type : EntityType.values()) {
			Class<?> typeEntityClass = type.getEntityClass();
			if (typeEntityClass != null && typeEntityClass.isInstance(entityClass)) {
				return getName(type);
			}
		}
		return entityClass.getSimpleName();
	}

	/**
	 * Gets the lower-cased name of a given Entity Type
	 * 
	 * @param type to get the name of
	 * @return Entity Type name
	 */
	public static String getName(EntityType type) {
		// Bukkit, please fix your naming?
		if (type == EntityType.OCELOT) {
			return "ocelot";
		}
		return type.getName().toLowerCase(Locale.ENGLISH);
	}

	/**
	 * Gets the lower-cased name of a given Creature Type
	 * 
	 * @param type to get the name of
	 * @return Creature Type name
	 */
	public static String getName(CreatureType type) {
		return getName(type.toEntityType());
	}
}
