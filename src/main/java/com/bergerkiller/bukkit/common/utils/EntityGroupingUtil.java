package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.StringMapCaseInsensitive;
import org.bukkit.Material;
import org.bukkit.entity.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

/**
 * Contains entity naming and grouping functions to categorize entities
 */
public class EntityGroupingUtil {

    private static final StringMapCaseInsensitive<Set<EntityCategory>> entityCategories = new StringMapCaseInsensitive<Set<EntityCategory>>();
    private static final EnumMap<EntityType, String> typeNames = new EnumMap<EntityType, String>(EntityType.class);

    static {
        // Note: These categories are ONLY used to map by name
        for (EntityType type : EntityType.values()) {
            // Obtain type name
            @SuppressWarnings("deprecation")
            String name = type.getName();
            if (name == null) {
                Class<?> clazz = type.getEntityClass();
                if (clazz == null) {
                    name = "unknown";
                } else {
                    name = clazz.getSimpleName();
                }
            }
            name = name.toLowerCase(Locale.ENGLISH);

            // Store it
            if (entityCategories.containsKey(name)) {
                Set<EntityCategory> x = entityCategories.get(name);
                x.addAll(getCategories(type));
                entityCategories.put(name, x);
            } else {
                Set<EntityCategory> x = new HashSet<EntityCategory>();
                x.addAll(getCategories(type));
                entityCategories.put(name, x);
            }
            typeNames.put(type, name);
        }
    }

    /**
     * @deprecated Use Set<EntityCategory> getCategories(name)
     */
    @Deprecated
    public static EntityCategory getCategory(String name) {
        Set<EntityCategory> x = getCategories(name);
        for (EntityCategory entityCategory : EntityCategory.values()) {
            if (entityCategory.isLegacyMob() && x.contains(entityCategory)) {
                return entityCategory;
            }
        }
        return EntityCategory.OTHER;
    }

    /**
     * @deprecated Use Set<EntityCategory> getCategories(entityType)
     */
    @Deprecated
    public static EntityCategory getCategory(EntityType type) {
        return getCategory(type.getEntityClass());
    }

    /**
     * @deprecated Use Set<EntityCategory> getCategories(entityClass)
     */
    @Deprecated
    public static EntityCategory getCategory(Class<? extends Entity> entityClass) {
        Set<EntityCategory> x = getCategories(entityClass);
        for (EntityCategory entityCategory : EntityCategory.values()) {
            if (entityCategory.isLegacyMob() && x.contains(entityCategory)) {
                return entityCategory;
            }
        }
        return EntityCategory.OTHER;
    }


    /**
     * Returns true if an entity is a mob
     *
     * @param name Name of the entity
     * @return true if entity is a mob, false if entity is not a mob
     */
    public static boolean isMob(String name) {
        return !(isEntityType(name, EntityCategory.OTHER));
    }

    /**
     * Returns true if an entity is a NPC
     * @deprecated Use isEntityType(name, EntityCategory.NPC) instead
     */
    @Deprecated
    public static boolean isNPC(String name) {
        return isEntityType(name, EntityCategory.NPC);
    }

    /**
     * Returns true if an entity is an Animal
     * @deprecated Use isEntityType(name, EntityCategory.ANIMAL) instead
     */
    @Deprecated
    public static boolean isAnimal(String name) {
        return isEntityType(name, EntityCategory.ANIMAL);
    }

    /**
     * Returns true if an entity is a Monster
     * @deprecated Use isEntityType(name, EntityCategory.MONSTER) instead
     */
    @Deprecated
    public static boolean isMonster(String name) {
        return isEntityType(name, EntityCategory.MONSTER);
    }

    public static boolean isMob(Entity entity) {
        return entity != null && isMob(entity.getClass());
    }

    public static boolean isMob(EntityType entityType) {
        return isMob(entityType.getEntityClass());
    }

    public static boolean isMob(Class<? extends Entity> entityClass) {
        Set<EntityCategory> resultSet = getCategories(entityClass);
        return !(resultSet.contains(EntityCategory.OTHER));
    }

    /**
     * Returns true if an entity is a NPC
     * @deprecated Use isEntityType(entity, EntityCategory.NPC) instead
     */
    @Deprecated
    public static boolean isNPC(Entity entity) {
        return isEntityType(entity, EntityCategory.NPC);
    }

    /**
     * Returns true if an entity is a NPC
     * @deprecated Use isEntityType(entityType, EntityCategory.NPC) instead
     */
    @Deprecated
    public static boolean isNPC(EntityType entityType) {
        return isEntityType(entityType, EntityCategory.NPC);
    }

    /**
     * Returns true if an entity is a NPC
     * @deprecated Use isEntityType(class, EntityCategory.NPC) instead
     */
    @Deprecated
    public static boolean isNPC(Class<? extends Entity> entityClass) {
        return isEntityTypeClass(entityClass, EntityCategory.NPC);
    }

    /**
     * Returns true if an entity is an Animal
     * @deprecated Use isEntityType(entity, EntityCategory.ANIMAL) instead
     */
    @Deprecated
    public static boolean isAnimal(Entity entity) {
        return isEntityType(entity, EntityCategory.ANIMAL);
    }

    /**
     * Returns true if an entity is an Animal
     * @deprecated Use isEntityType(entityType, EntityCategory.ANIMAL) instead
     */
    @Deprecated
    public static boolean isAnimal(EntityType entityType) {
        return isEntityType(entityType, EntityCategory.ANIMAL);
    }

    /**
     * Returns true if an entity is an Animal
     * @deprecated Use isEntityType(class, EntityCategory.ANIMAL) instead
     */
    @Deprecated
    public static boolean isAnimal(Class<? extends Entity> entityClass) {
        return isEntityTypeClass(entityClass, EntityCategory.ANIMAL);
    }

    /**
     * Returns true if an entity is a Monster
     * @deprecated Use isEntityType(entity, EntityCategory.MONSTER) instead
     */
    @Deprecated
    public static boolean isMonster(Entity entity) {
        return isEntityType(entity, EntityCategory.MONSTER);
    }

    /**
     * Returns true if an entity is a Monster
     * @deprecated Use isEntityType(entityType, EntityCategory.MONSTER) instead
     */
    @Deprecated
    public static boolean isMonster(EntityType entityType) {
        return isEntityType(entityType, EntityCategory.MONSTER);
    }

    /**
     * Returns true if an entity is a Monster
     * @deprecated Use isEntityType(name, EntityCategory.MONSTER) instead
     */
    @Deprecated
    public static boolean isMonster(Class<? extends Entity> entityClass) {
        return isEntityTypeClass(entityClass, EntityCategory.MONSTER);
    }

    /**
     * Returns true if an entity is a killer bunny
     *
     * @param entity The entity to check
     * @result Returns true if an entity is a killer bunny
     */
    public static boolean isKillerBunny(Entity entity) {
        if (isEntityTypeClass(entity, Rabbit.class)) {
            return ((Rabbit)entity).getRabbitType() == Rabbit.Type.THE_KILLER_BUNNY;
        }
        return false;
    }

    /**
     * Returns true if an entity is tamed
     *
     * @param entity The entity to check
     * @result Returns true if an entity is tamed
     */
    public static boolean isTamed(Entity entity) {
        if (isEntityType(entity, EntityCategory.TAMEABLE)) {
            return ((Tameable)entity).isTamed();
        }
        return false;
    }

    /**
     * Jockeys:<br>
     * - Skeleton riding spider<br>
     * - Zombie riding chicken
     *
     * @param entity The entity to check
     * @result Returns true if the entity is a jockey
     */
    public static boolean isJockey(Entity entity) {
        if (isEntityTypeClass(entity, Spider.class)) {
            return hasMobOnTopOrBottom(entity, Zombie.class);
        } else if (isEntityTypeClass(entity, Chicken.class)) {
            return hasMobOnTopOrBottom(entity, Skeleton.class);
        } else if (isEntityTypeClass(entity, Zombie.class)) {
            return hasMobOnTopOrBottom(entity, Chicken.class);
        } else if (isEntityTypeClass(entity, Skeleton.class)) {
            return hasMobOnTopOrBottom(entity, Spider.class);
        }
        return false;
    }

    private static boolean hasMobOnTopOrBottom(Entity e, Class<?> searchMobType) {
        List<Entity> mobs = e.getNearbyEntities(0.5, 1.0, 0.5);
        for (Entity mob : mobs) {
            if (isEntityTypeClass(mob, searchMobType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a set of all matching categories
     *
     * @param name The name of the entity type
     * @return Set<EntityCategory> of all matching categories
     */
    public static Set<EntityCategory> getCategories(String name) {
        Set<EntityCategory> x = new HashSet<EntityCategory>();
        x.add(EntityCategory.OTHER);
        return LogicUtil.fixNull(entityCategories.get(name), x);
    }

    /**
     * Returns a set of all matching categories
     *
     * @param type The EntityType
     * @return Set<EntityCategory> of all matching categories
     */
    public static Set<EntityCategory> getCategories(EntityType type) {
        return getCategories(type.getEntityClass());
    }

    /**
     * Returns a set of all matching categories
     *
     * @param entity The Entity
     * @return Set<EntityCategory> of all matching categories
     */
    public static Set<EntityCategory> getCategories(Entity entity) {
        Set<EntityCategory> resultSet = new HashSet<EntityCategory>();
        if (isKillerBunny(entity)) {
            resultSet.add(EntityCategory.KILLER_BUNNY);
            resultSet.add(EntityCategory.ANIMAL);
            resultSet.add(EntityCategory.HOSTILE);
        }
        if (isTamed(entity)) {
            resultSet.add(EntityCategory.TAMED);
        }
        if (isJockey(entity)) {
            resultSet.add(EntityCategory.JOCKEY);
            resultSet.add(EntityCategory.MONSTER);
            resultSet.add(EntityCategory.ANIMAL);
            resultSet.add(EntityCategory.HOSTILE);
        }
        addMatchingCategories(resultSet, entity);
        if (resultSet.size() == 0) {
            resultSet.add(EntityCategory.OTHER);
        }
        return resultSet;
    }

    /**
     * Returns a set of all matching categories
     *
     * @param entityClass The Entity class
     * @return Set<EntityCategory> of all matching categories
     */
    public static Set<EntityCategory> getCategories(Class<? extends Entity> entityClass) {
        Set<EntityCategory> resultSet = new HashSet<EntityCategory>();
        addMatchingCategories(resultSet, entityClass);
        if (resultSet.size() == 0) {
            resultSet.add(EntityCategory.OTHER);
        }
        return resultSet;
    }

    private static void addMatchingCategories(Set<EntityCategory> resultSet, Entity e) {
        addMatchingCategories(resultSet, e.getClass());
    }

    private static void addMatchingCategories(Set<EntityCategory> resultSet, Class<?> class1) {
        if (class1 == null) {
            return;
        }
        for (EntityCategory entityCategory : EntityCategory.values()) {
            Set<Class<?>> entityClasses = entityCategory.getEntityClasses();
            for (Class<?> entityClass : entityClasses) {
                if (entityClass.isAssignableFrom(class1)) {
                    resultSet.add(entityCategory);
                    break;
                }
            }
        }
    }

    /**
     * Returns true if the entity named "name" belongs to the specified entity category
     *
     * @param name The Entity name
     * @param entityCategory The entity category to consider
     * @return True if the specified entity belongs to the specified entity category
     */
    public static boolean isEntityType(String name, EntityCategory entityCategory) {
        return getCategories(name).contains(entityCategory);
    }

    /**
     * Returns true if the entity belongs to the specified class of entities
     *
     * @param entity The Entity object
     * @param entityClass The class to consider if the entity belongs
     * @return True if the specified entity belongs to the specified entity class
     */
    public static boolean isEntityTypeClass(Entity entity, Class<?> entityClass) {
        return entity != null && isEntityTypeClass(entity.getClass(), entityClass);
    }

    /**
     * Returns true if the entity belongs to any of the given entity classes
     *
     * @param entity The Entity object
     * @param entityClassSet A set of classes to be considered
     * @return True if the specified entity belongs to any of the classes in entityClassSet
     */
    public static boolean isEntityTypeClass(Entity entity, Set<Class<?>> entityClassSet) {
        if (entity != null) {
            for (Class<?> myClass : entityClassSet) {
                if (isEntityTypeClass(entity.getClass(), myClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if the entity belongs to the given entity category
     *
     * @param entity The Entity object
     * @param entityCategory Entity category to consider
     * @return True if the specified entity belongs to the entityCategory
     */
    public static boolean isEntityType(Entity entity, EntityCategory entityCategory) {
        return entity != null && getCategories(entity.getClass()).contains(entityCategory);
    }

    /**
     * Returns true if the entity belongs to any of the categories in the given set
     *
     * @param entity The Entity object
     * @param entityCategorySet Set of entity categories to consider
     * @return True if the specified entity belongs to any of the categories in entityCategorySet
     */
    public static boolean isEntityType(Entity entity, Set<EntityCategory> entityCategorySet) {
        if (entity != null) {
            Set<EntityCategory> thisEntityCategories = getCategories(entity.getClass());
            if (thisEntityCategories.size() == 1 && thisEntityCategories.contains(EntityCategory.OTHER)) {
                if (entityCategorySet.size() == 1 && entityCategorySet.contains(EntityCategory.OTHER)) {
                    return true;
                }
                return false;
            }
            for (EntityCategory x : entityCategorySet) {
                if (thisEntityCategories.contains(x)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if the entity type belongs to the given entity category
     *
     * @param entityType The Entity type
     * @param entityCategory The entity category to consider
     * @return True if the specified entity type belongs to the given entity category
     */
    public static boolean isEntityType(EntityType entityType, EntityCategory entityCategory) {
        return getCategories(entityType.getEntityClass()).contains(entityCategory);
    }

    /**
     * Returns true if the class of entity is assignable to another class
     *
     * @param entityClass The class of entity
     * @param testEntityClass The class to consider whether the entity class is assignable
     * @return True if the specified entity class is assignable to the specified test class
     */
    public static boolean isEntityTypeClass(Class<?> entityClass, Class<?> testEntityClass) {
        return testEntityClass.isAssignableFrom(entityClass);
    }

    /**
     * Returns true if the class of entity belongs to the specified entity category
     *
     * @param entityClass The class of entity
     * @param entityCategory The category to consider if the entity class belongs
     * @return True if the class of entity belongs to the specified entity category
     */
    public static boolean isEntityTypeClass(Class<?> entityClass, EntityCategory entityCategory) {
        for (Class<?> myClass : entityCategory.getEntityClasses()) {
            if (myClass.isAssignableFrom(entityClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the lower-cased name of a given Entity<br>
     * - Items will get the name 'item' with the item type appended to it<br>
     * - Falling blocks will get the name 'falling' with the block type appended
     * to it
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
            return getName(entity.getType());
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
        return entityClass.getSimpleName().toLowerCase(Locale.ENGLISH);
    }

    /**
     * Gets the lower-cased name of a given Entity Type
     *
     * @param type to get the name of
     * @return Entity Type name
     */
    public static String getName(EntityType type) {
        return typeNames.get(type);
    }

    /**
     * Represents a certain category of entities
     */
    public static enum EntityCategory {
        KILLER_BUNNY(true, false, "isKillerBunny"),
        TAMED(true, false, "isTamed"),
        JOCKEY(true, false, "isJockey"),
        ANIMAL(true, true, Animals.class, Squid.class),
        MONSTER(true, true, Monster.class, Slime.class, Ghast.class, Golem.class),
        NPC(true, true, NPC.class),
        PASSIVE(true, false, Bat.class, Chicken.class, Cow.class, MushroomCow.class, Pig.class, Sheep.class, Squid.class, Villager.class),
        NEUTRAL(true, false, CaveSpider.class, Enderman.class, Spider.class, PigZombie.class),
        HOSTILE(true, false, Blaze.class, Creeper.class, Guardian.class, Endermite.class, Ghast.class, MagmaCube.class, Silverfish.class, Skeleton.class, Slime.class, Witch.class, Zombie.class),
        TAMEABLE(true, false, Tameable.class),
        UTILITY(true, false, Golem.class),
        BOSS(true, false, EnderDragon.class, EnderDragonPart.class, Wither.class),
        OTHER(false, true);

        private final boolean isMob;
        private boolean isLegacyMob;
        private Set<Class<?>> entityClasses;
        private Method method;

        private EntityCategory(boolean isMob, boolean isLegacyMob) {
            this.isMob = isMob;
            this.isLegacyMob = isLegacyMob;
            this.setEntityClasses();
            this.setMethod(null);
        }

        private EntityCategory(boolean isMob, boolean isLegacyMob, Class<?>...classes) {
            this.isMob = isMob;
            this.isLegacyMob = isLegacyMob;
            this.setMethod(null);
            this.setEntityClasses(classes);
        }

        private EntityCategory(boolean isMob, boolean isLegacyMob, String methodName) {
            this.isMob = isMob;
            this.isLegacyMob = isLegacyMob;
            this.setEntityClasses();
            try {
                Method methodTry = (EntityGroupingUtil.class).getMethod(methodName, Entity.class);
                this.setMethod(methodTry);
            } catch (Exception e) {
            	Logging.LOGGER.log(Level.WARNING, String.format("Unable to reference method %s(Entity) in EntityGroupingUtil: %s", methodName, e.getMessage()));
                this.setMethod(null);
            }
        }

        private void setEntityClasses() {
            this.entityClasses = new HashSet<Class<?>>();
        }

        public boolean isMob() {
            return isMob;
        }

        /*
         * Adding this so the calls to getCategory() still return what they used to,
         * whereas calls to getCategories() will return the full list.
         */
        public boolean isLegacyMob() {
            return isLegacyMob;
        }

        public Set<Class<?>> getEntityClasses() {
            return entityClasses;
        }

        public void setEntityClasses(Set<Class<?>> entityClasses) {
            this.entityClasses = entityClasses;
        }

        public void setEntityClasses(Class<?>[] entityClasses) {
            this.setEntityClasses(new HashSet<Class<?>>(Arrays.asList(entityClasses)));
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public boolean contains(Class<?> entityClass) {
            return this.getEntityClasses().contains(entityClass);
        }
    }
}
