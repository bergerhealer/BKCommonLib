package com.bergerkiller.bukkit.common.utils;

import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Squid;

import com.bergerkiller.bukkit.common.collections.StringMap;

/**
 * Contains entity naming and grouping functions to categorize entities
 */
public class EntityGroupingUtil {
	private static final StringMap<EntityCategory> entityCategories = new StringMap<EntityCategory>();

	static {
		// Note: These categories are ONLY used to map by name
		for (EntityType type : EntityType.values()) {
			entityCategories.putLower(getName(type), getCategory(type));
		}
	}

	public static EntityCategory getCategory(String name) {
		return LogicUtil.fixNull(entityCategories.getLower(name), EntityCategory.OTHER);
	}

	public static EntityCategory getCategory(EntityType type) {
		return getCategory(type.getEntityClass());
	}

	public static EntityCategory getCategory(Class<? extends Entity> entityClass) {
		if (isAnimal(entityClass)) {
			return EntityCategory.ANIMAL;
		} else if (isMonster(entityClass)) {
			return EntityCategory.MONSTER;
		} else if (isNPC(entityClass)) {
			return EntityCategory.NPC;
		} else {
			return EntityCategory.OTHER;
		}
	}

	public static boolean isMob(String name) {
		return getCategory(name).isMob();
	}

	public static boolean isNPC(String name) {
		return getCategory(name) == EntityCategory.NPC;
	}

	public static boolean isAnimal(String name) {
		return getCategory(name) == EntityCategory.ANIMAL;
	}

	public static boolean isMonster(String name) {
		return getCategory(name) == EntityCategory.MONSTER;
	}

	public static boolean isMob(Entity entity) {
		return isMob(entity.getClass());
	}

	public static boolean isMob(EntityType entityType) {
		return isMob(entityType.getEntityClass());
	}

	public static boolean isMob(Class<? extends Entity> entityClass) {
		return isAnimal(entityClass) || isMonster(entityClass);
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

	public static boolean isAnimal(Entity entity) {
		return isAnimal(entity.getClass());
	}

	public static boolean isAnimal(EntityType entityType) {
		return isAnimal(entityType.getEntityClass());
	}

	public static boolean isAnimal(Class<? extends Entity> entityClass) {
		return entityClass != null && (Animals.class.isAssignableFrom(entityClass) || Squid.class.isAssignableFrom(entityClass));
	}

	public static boolean isMonster(Entity entity) {
		return isMonster(entity.getClass());
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
			if (typeEntityClass != null && typeEntityClass.isAssignableFrom(entityClass)) {
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
		String name = type.getName();
		if (name == null) {
			Class<?> clazz = type.getEntityClass();
			if (clazz == null) {
				return "unknown";
			} else {
				name = clazz.getSimpleName();
			}
		}
		return name.toLowerCase(Locale.ENGLISH);
	}

	/**
	 * Represents a certain category of entities
	 */
	public static enum EntityCategory {
		ANIMAL(true), MONSTER(true), NPC(true), OTHER(false);

		private final boolean isMob;

		private EntityCategory(boolean isMob) {
			this.isMob = isMob;
		}

		public boolean isMob() {
			return isMob;
		}
	}
}
