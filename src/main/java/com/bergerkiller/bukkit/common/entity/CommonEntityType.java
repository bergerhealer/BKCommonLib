package com.bergerkiller.bukkit.common.entity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.logging.Level;

import net.minecraft.server.v1_8_R1.IInventory;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityClassBuilder;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityHook;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityHookImpl;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityInventoryHookImpl;
import com.bergerkiller.bukkit.common.entity.type.CommonLivingEntity;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;
import com.bergerkiller.bukkit.common.reflection.classes.WorldRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Stores all internal information about an Entity
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CommonEntityType {

    public static final CommonEntityType UNKNOWN = new CommonEntityType(EntityType.UNKNOWN, "", 80, 3, true);
    private static final ClassMap<CommonEntityType> byNMS = new ClassMap<CommonEntityType>();
    private static final EnumMap<EntityType, CommonEntityType> byEntityType = new EnumMap<EntityType, CommonEntityType>(EntityType.class);

    public final ClassTemplate<?> nmsType;
    public final ClassTemplate<?> commonType;
    public final ClassTemplate<?> bukkitType;
    private final SafeConstructor<?> nmsConstructor;
    private NMSEntityClassBuilder hookBuilder;
    private final SafeConstructor<?> commonConstructor;
    public final EntityType entityType;
    public final int networkUpdateInterval;
    public final int networkViewDistance;
    public final boolean networkIsMobile;

    public CommonEntityType(EntityType entityType, String nmsName, int networkViewDistance, int networkUpdateInterval, boolean networkIsMobile) {
        // Properties first
        this.networkUpdateInterval = networkUpdateInterval;
        this.networkViewDistance = networkViewDistance;
        this.networkIsMobile = networkIsMobile;
        this.entityType = entityType;

        // Default 'UNKNOWN' instance
        if (LogicUtil.nullOrEmpty(nmsName)) {
            this.nmsType = NMSClassTemplate.create("Entity");
            this.commonType = ClassTemplate.create(CommonEntity.class);
            this.bukkitType = ClassTemplate.create(Entity.class);
            this.nmsConstructor = null;
            this.hookBuilder = null;
            this.commonConstructor = this.commonType.getConstructor(Entity.class);
            return;
        }

        // Obtain Bukkit type
        this.bukkitType = ClassTemplate.create(entityType.getEntityClass());

        // Obtain Common class type and constructor
        Class<?> type = CommonUtil.getClass(Common.COMMON_ROOT + ".entity.type.Common" + nmsName);
        Class<?> entityClass = this.bukkitType.getType();
        if (type == null) {
            // No specifics - try to find a sub-category
            if (LivingEntity.class.isAssignableFrom(this.bukkitType.getType())) {
                type = CommonLivingEntity.class;
                entityClass = LivingEntity.class;
            } else {
                type = CommonEntity.class;
                entityClass = Entity.class;
            }
        }
        this.commonType = ClassTemplate.create(type);
        this.commonConstructor = this.commonType.getConstructor(entityClass);

        // Obtain NMS class type and constructor
        type = CommonUtil.getClass(Common.NMS_ROOT + ".Entity" + nmsName);
        if (type == null) {
            this.nmsType = new ClassTemplate();
            this.nmsConstructor = null;
        } else {
            this.nmsType = ClassTemplate.create(type);
            if (entityType == EntityType.PLAYER) {
                this.nmsConstructor = null;
            } else {
                this.nmsConstructor = this.nmsType.getConstructor(WorldRef.TEMPLATE.getType());
            }
        }
    }

    public boolean hasNMSEntity() {
        return nmsConstructor != null;
    }

    public <T extends Entity> CommonEntity<T> createCommonEntity(T entity) {
        return (CommonEntity<T>) this.commonConstructor.newInstance(entity);
    }

    public Object createNMSEntity() {
        return nmsConstructor.newInstance(new Object[]{null});
    }

    public NMSEntityHook createNMSHookEntity(CommonEntity<?> commonEntity) {
        if (!this.hasNMSEntity()) {
            throw new RuntimeException("Entity of type " + entityType + " has no Hook Entity support!");
        }
        // Initialize a new hook builder if needed (this operation is slightly slow!)
        if (this.hookBuilder == null) {
            List<Class<?>> callbacks = new ArrayList<Class<?>>();
            callbacks.add(NMSEntityHookImpl.class);
            if (IInventory.class.isAssignableFrom(this.nmsType.getType())) {
                callbacks.add(NMSEntityInventoryHookImpl.class);
            }
            this.hookBuilder = new NMSEntityClassBuilder(this.nmsType.getType(), callbacks);
        }
        // Create a new instance
        return (NMSEntityHook) this.hookBuilder.create(commonEntity);
    }

    /*
     * Getters
     */
    public static CommonEntityType byEntityType(EntityType type) {
        return LogicUtil.fixNull(byEntityType.get(type), UNKNOWN);
    }

    public static CommonEntityType byEntity(Entity entity) {
        return byNMSEntity(Conversion.toEntityHandle.convert(entity));
    }

    public static CommonEntityType byNMSEntityClass(Class<?> entityClass) {
        return LogicUtil.fixNull(byNMS.get(entityClass), UNKNOWN);
    }

    public static CommonEntityType byNMSEntity(Object entityHandle) {
        return LogicUtil.fixNull(byNMS.get(entityHandle), UNKNOWN);
    }

    /*
     * Registration of all entity types
     */
    private static void add(EntityType entityType, String nmsName, int networkViewDistance, int networkUpdateInterval, boolean networkIsMobile) {
        try {
            final CommonEntityType info = new CommonEntityType(entityType, nmsName, networkViewDistance, networkUpdateInterval, networkIsMobile);
            byNMS.put(info.nmsType, info);
            if (info.entityType != EntityType.UNKNOWN) {
                byEntityType.put(info.entityType, info);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    static {
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
        add(EntityType.MINECART, "MinecartRideable", 80, 2, true);
        add(EntityType.MINECART_CHEST, "MinecartChest", 80, 2, true);
        add(EntityType.MINECART_FURNACE, "MinecartFurnace", 80, 2, true);
        add(EntityType.MINECART_TNT, "MinecartTNT", 80, 2, true);
        add(EntityType.MINECART_HOPPER, "MinecartHopper", 80, 2, true);
        add(EntityType.MINECART_MOB_SPAWNER, "MinecartMobSpawner", 80, 2, true);
        add(EntityType.MINECART_COMMAND, "MinecartCommandBlock", 80, 2, true);
        add(EntityType.BOAT, "Boat", 80, 2, true);

        // Mobs
        add(EntityType.BAT, "Bat", 80, 3, false);
        add(EntityType.BLAZE, "Blaze", 80, 3, true);
        add(EntityType.CAVE_SPIDER, "CaveSpider", 80, 3, true);
        add(EntityType.CHICKEN, "Chicken", 80, 3, true);
        add(EntityType.COW, "Cow", 80, 3, true);
        add(EntityType.CREEPER, "Creeper", 80, 3, true);
        add(EntityType.ENDERMAN, "Enderman", 80, 3, true);
        add(EntityType.ENDERMITE, "Endermite", 80, 3, true);
        add(EntityType.ENDER_DRAGON, "EnderDragon", 160, 3, true);
        add(EntityType.GHAST, "Ghast", 80, 3, true);
        add(EntityType.GIANT, "GiantZombie", 80, 3, true);
        add(EntityType.GUARDIAN, "Guardian", 80, 3, true);
        add(EntityType.HORSE, "Horse", 80, 3, true);
        add(EntityType.IRON_GOLEM, "IronGolem", 80, 3, true);
        add(EntityType.MAGMA_CUBE, "MagmaCube", 80, 3, true);
        add(EntityType.MUSHROOM_COW, "MushroomCow", 80, 3, true);
        add(EntityType.OCELOT, "Ocelot", 80, 3, true);
        add(EntityType.PIG, "Pig", 80, 3, true);
        add(EntityType.PIG_ZOMBIE, "PigZombie", 80, 3, true);
        add(EntityType.RABBIT, "Rabbit", 80, 3, true);
        add(EntityType.SHEEP, "Sheep", 80, 3, true);
        add(EntityType.SILVERFISH, "Silverfish", 80, 3, true);
        add(EntityType.SKELETON, "Skeleton", 80, 3, true);
        add(EntityType.SLIME, "Slime", 80, 3, true);
        add(EntityType.SNOWMAN, "Snowman", 80, 3, true);
        add(EntityType.SPIDER, "Spider", 80, 3, true);
        add(EntityType.SQUID, "Squid", 64, 3, true);
        add(EntityType.VILLAGER, "Villager", 80, 3, true);
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
        add(EntityType.ARMOR_STAND, "ArmorStand", 160, 10, true); // Check if it chrashes
        add(EntityType.LEASH_HITCH, "Leash", 160, Integer.MAX_VALUE, false);

        // Check that all entity types are registered properly
        EntityType[] invalidTypes = {EntityType.UNKNOWN, EntityType.WEATHER, EntityType.LIGHTNING, EntityType.COMPLEX_PART};
        for (EntityType type : EntityType.values()) {
            if (LogicUtil.contains(type, invalidTypes)) {
                continue;
            }
            if (!byEntityType.containsKey(type)) {
                CommonPlugin.LOGGER.log(Level.WARNING, "Entity Type '" + type + "' is not registered as CommonEntityType");
            }
        }
    }
}
