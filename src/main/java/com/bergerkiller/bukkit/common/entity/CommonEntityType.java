package com.bergerkiller.bukkit.common.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.type.CommonHumanEntity;
import com.bergerkiller.bukkit.common.entity.type.CommonLivingEntity;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.server.DimensionManagerHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityTypesHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.SafeConstructor;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorld;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;

/**
 * Stores all internal information about an Entity
 */
@SuppressWarnings({"unchecked", "deprecation"})
public class CommonEntityType {
    private static final StampedLock lock = new StampedLock();
    private static final Map<String, ObjectTypeInfo> objectTypes = new HashMap<String, ObjectTypeInfo>();
    private static final ClassMap<CommonEntityType> byNMS = new ClassMap<CommonEntityType>();
    private static final Map<Integer, CommonEntityType> byObjectTypeId = new HashMap<>();
    private static final Map<Integer, CommonEntityType> byEntityTypeId = new HashMap<>();
    private static final Map<Object, CommonEntityType> byNMSEntityType = new HashMap<>();
    private static final Map<Class<?>, EntityTypesHandle> entityTypesByClass = new HashMap<>();
    private static final CommonPair[] commonPairs;
    private static final EnumMap<EntityType, CommonEntityType> byEntityType = new EnumMap<EntityType, CommonEntityType>(EntityType.class);
    public static final CommonEntityType UNKNOWN = new CommonEntityType(EntityType.UNKNOWN, true);

    public final ClassTemplate<?> nmsType;
    public final ClassTemplate<?> commonType;
    public final ClassTemplate<?> bukkitType;
    private final SafeConstructor<?> commonConstructor;
    private final boolean hasWorldCoordConstructor;
    public final EntityType entityType;
    public final int entityTypeId;
    public final int objectTypeId;
    public final int objectExtraData;
    public final EntityTypesHandle nmsEntityType;

    private CommonEntityType(EntityType entityType, boolean nullInitialize) {
        // Properties first
        this.entityType = entityType;

        // A type that is not supported for construction
        if (nullInitialize) {
            this.nmsType = ClassTemplate.create((Class<?>) null);
            this.commonType = ClassTemplate.create(CommonEntity.class);
            this.bukkitType = ClassTemplate.create(Entity.class);
            this.commonConstructor = this.commonType.getConstructor(Entity.class);
            this.hasWorldCoordConstructor = false;
            this.entityTypeId = -1;
            this.nmsEntityType = null;
            this.objectTypeId = -1;
            this.objectExtraData = -1;
            return;
        }

        // Figure out what kind of NMS Class belongs to this EntityType by comparing with the internal NMS EntityTypes listing
        Class<?> nmsType = null;

        // Registered entity types
        // Create minecraft key from EntityType name
        String entityTypeName = entityType.getName();
        String entityTypeEnumName = entityType.name();
        if (entityType == EntityType.MINECART_FURNACE) {
            // New naming system had a bug, DAMMIT BUKKIT
            if (Common.evaluateMCVersion(">=", "1.11")) {
                entityTypeName = "furnace_minecart";
            }
        } else if (entityType == EntityType.MINECART_MOB_SPAWNER) {
            // Old naming system had a bug, DAMMIT BUKKIT
            if (Common.evaluateMCVersion("<", "1.11")) {
                entityTypeName = "MinecartSpawner";
            }
        } else if (entityTypeEnumName.equals("TIPPED_ARROW")) {
            // Old naming system had a bug, DAMMIT BUKKIT
            if (Common.evaluateMCVersion("<", "1.11")) {
                entityTypeName = "Arrow";
            }
        } else if (entityTypeEnumName.equals("PLAYER")) {
            entityTypeName = null; // Internal lookup fails!
        }
        if (entityTypeName != null) {
            nmsType = EntityTypesHandle.getEntityClass(entityTypeName);
        }

        // Fallbacks for when nmsType cannot be resolved from EntityTypes by name
        if (nmsType == null) {
            // Some special types that don't show up as a registered class
            String nmsName = null;

            // Types without a clear mapping
            if (entityTypeEnumName.equals("PLAYER")) {
                nmsName = "EntityPlayer";
            } else if (entityTypeEnumName.equals("FISHING_HOOK")) {
                nmsName = "EntityFishingHook";
            } else if (entityTypeEnumName.equals("LIGHTNING")) {
                nmsName = "EntityLightning";
            } else if (entityTypeEnumName.equals("WEATHER")) {
                nmsName = "EntityWeather";
            } else if (entityTypeEnumName.equals("COMPLEX_PART")) {
                nmsName = "EntityComplexPart";
            }

            // <= 1.10.2 (now removed)
            if (entityTypeEnumName.equals("EGG")) {
                nmsName = "EntityEgg";
            } else if (entityTypeEnumName.equals("AREA_EFFECT_CLOUD")) {
                nmsName = "EntityAreaEffectCloud";
            } else if (entityTypeEnumName.equals("SPLASH_POTION")) {
                nmsName = "EntityPotion";
            }

            // Added in >= 1.10.2
            if (entityTypeEnumName.equals("TIPPED_ARROW")) {
                nmsName = "EntityTippedArrow";
            } else if (entityTypeEnumName.equals("LINGERING_POTION")) {
                nmsName = "EntityPotion";
            }

            if (nmsName == null) {
                Logging.LOGGER_REGISTRY.log(Level.WARNING, "Entity type could not be registered: unknown type (" + entityType.toString() + ")");
            } else {
                nmsType = CommonUtil.getNMSClass(nmsName);
                if (nmsType == null) {
                    Logging.LOGGER_REGISTRY.log(Level.WARNING, "Entity type could not be registered: class not found (" + entityType.toString() + ") class=" + nmsName);
                }
            }
        }

        // Obtain Bukkit type
        this.bukkitType = ClassTemplate.create(entityType.getEntityClass());

        // Figure out some name postfix to use
        String typeName;
        if (nmsType != null) {
            typeName = nmsType.getSimpleName();
        } else if (this.bukkitType.isValid()) {
            typeName = this.bukkitType.getType().getName();
        } else {
            typeName = entityType.toString();
        }
        if (typeName.startsWith("Entity")) {
            typeName = typeName.substring(6);
        }

        // Obtain Common class type and constructor
        String commonTypeName = Common.COMMON_ROOT + ".entity.type.Common" + typeName;
        Class<?> commonType = CommonUtil.getClass(commonTypeName);
        Class<?> entityClass = this.bukkitType.getType();
        if (commonType == null && entityClass != null) {
            // No specifics - try to find a sub-category
            CommonPair foundPair = null;
            for (CommonPair pair : commonPairs) {
                if (pair.bukkitType.isAssignableFrom(entityClass)) {
                    foundPair = pair;
                    break;
                }
            }
            if (foundPair != null) {
                commonType = foundPair.commonType;
                entityClass = foundPair.bukkitType;
            } else {
                commonType = CommonEntity.class;
                entityClass = Entity.class;
            }
        }

        this.nmsType = ClassTemplate.create(nmsType);
        this.commonType = ClassTemplate.create(commonType);
        this.commonConstructor = this.commonType.getConstructor(entityClass);

        this.entityTypeId = EntityTypesHandle.getEntityTypeId(this.nmsType.getType());
        if (this.entityTypeId != -1) {
            byEntityTypeId.put(this.entityTypeId, this);
        }

        if (EntityTypesHandle.T.fromEntityClass.isAvailable() && nmsType != null) {
            this.nmsEntityType = getNMSEntityTypeByEntityClass(nmsType);
            if (this.nmsEntityType != null) {
                byNMSEntityType.put(this.nmsEntityType.getRaw(), this);
            }
        } else {
            this.nmsEntityType = null;
        }

        if (nmsType != null) {
            boolean hasConstructor = false;
            try {
                nmsType.getConstructor(CommonUtil.getNMSClass("World"), double.class, double.class, double.class);
                hasConstructor = true;
            } catch (Throwable t) {
            }
            this.hasWorldCoordConstructor = hasConstructor;
        } else {
            this.hasWorldCoordConstructor = false;
        }

        if (EntityTypesHandle.T.getTypeId.isAvailable()) {
            if (this.nmsEntityType != null) {
                this.objectTypeId = EntityTypesHandle.T.getTypeId.invoke(this.nmsEntityType.getRaw());
                this.objectExtraData = 0;
            } else {
                this.objectTypeId = -1;
                this.objectExtraData = -1;
            }
        } else {
            // Retrieve objectTypeId from internal mapping        
            ObjectTypeInfo objectInfo = objectTypes.get((entityType == null) ? "" : entityType.name());
            if (objectInfo != null) {
                // Take from mapping we have registered ourselves
                this.objectTypeId = objectInfo.typeId;
                this.objectExtraData = objectInfo.extraData;
            } else {
                this.objectTypeId = -1;
                this.objectExtraData = -1;
            }
        }
        if (this.objectTypeId != -1) {
            byObjectTypeId.put(this.objectTypeId, this);
        }
    }

    public <T extends Entity> CommonEntity<T> createCommonEntity(T entity) {
        return (CommonEntity<T>) this.commonConstructor.newInstance(entity);
    }

    public <T extends Entity> CommonEntity<T> createCommonEntityNull() {
        EntityHook hook = new EntityHook();
        hook.setStack(new Throwable());
        Object handle = hook.createInstance(this.nmsType.getType());

        CommonEntity<T> entity = createCommonEntityFromHandle(handle);

        DefaultEntityController controller = new DefaultEntityController();
        controller.bind(entity, false);

        return entity;
    }

    public <T extends Entity> CommonEntity<T> createCommonEntityFromHandle(Object handle) {
        Entity e = Conversion.toEntity.convert(handle); // getBukkitEntity
        return (CommonEntity<T>) createCommonEntity(e);
    }

    public <T extends Entity> CommonEntity<T> createNMSHookEntity(Location location) {
        World world = location.getWorld();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        if (world == null) {
            throw new IllegalArgumentException("Location has a null World");
        }

        EntityHook hook = new EntityHook();
        hook.setStack(new Throwable());
        Object handle;
        if (this.hasWorldCoordConstructor) {
            handle = hook.constructInstance(this.nmsType.getType(),
                    new Class<?>[] {NMSWorld.T.getType(), double.class, double.class, double.class},
                    new Object[] { Conversion.toWorldHandle.convert(world), x, y, z });
        } else if (CommonCapabilities.ENTITY_USES_ENTITYTYPES_IN_CONSTRUCTOR) {
            if (this.nmsEntityType == null) {
                throw new IllegalStateException("Type " + this.toString() + " cannot be constructed");
            }
            handle = hook.constructInstance(this.nmsType.getType(),
                    new Class<?>[] {EntityTypesHandle.T.getType(), NMSWorld.T.getType()},
                    new Object[] { this.nmsEntityType.getRaw(), Conversion.toWorldHandle.convert(world) });
        } else {
            handle = hook.constructInstance(this.nmsType.getType(),
                    new Class<?>[] {NMSWorld.T.getType()},
                    new Object[] { Conversion.toWorldHandle.convert(world) });
        }

        CommonEntity<T> entity = createCommonEntityFromHandle(handle);
        entity.loc.set(entity.last.set(location));

        // Refreshes bounding box when the constructor without x/y/z is called
        // The constructor with x/y/z parameters does this automatically
        if (!this.hasWorldCoordConstructor) {
            entity.setPosition(location.getX(), location.getY(), location.getZ());
        }

        // Debug: verify the 'dimension' field is set for the Entity
        if (DimensionManagerHandle.T.isAvailable()) {
            Object raw_dim = EntityHandle.T.dimension.raw.get(entity.getHandle());
            if (raw_dim == null) {
                throw new IllegalStateException("Entity dimension field is null");
            }
        }

        DefaultEntityController controller = new DefaultEntityController();
        controller.bind(entity, false);

        return entity;
    }

    public Object createNMSHookFromEntity(CommonEntity<?> entity) {
        EntityHook hook = new EntityHook();
        hook.setStack(new Throwable());
        return hook.hook(entity.getHandle());
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("CommonEntityType{");
        str.append("nms=").append((this.nmsType == null || !this.nmsType.isValid()) ? "null" : this.nmsType.getType().getSimpleName());
        str.append(", bukkit=").append((this.bukkitType == null || !this.bukkitType.isValid()) ? "null" : this.bukkitType.getType().getSimpleName());
        str.append(", enum=").append(this.entityType);
        str.append('}');
        return str.toString();
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

    public static CommonEntityType byObjectTypeId(int objectTypeId) {
        return LogicUtil.fixNull(byObjectTypeId.get(objectTypeId), UNKNOWN);
    }

    public static CommonEntityType byEntityTypeId(int entityTypeId) {
        return LogicUtil.fixNull(byEntityTypeId.get(entityTypeId), UNKNOWN);
    }

    public static CommonEntityType byNMSEntityType(EntityTypesHandle handle) {
        if (handle != null) {
            CommonEntityType type = byNMSEntityType.get(handle.getRaw());
            if (type != null) {
                return type;
            }

            // Try by class instead
            if (EntityTypesHandle.T.getEntityClassInst.isAvailable()) {
                Class<?> nmsType = EntityTypesHandle.T.getEntityClassInst.invoke(handle.getRaw());
                return byNMSEntityClass(nmsType);
            }
        }
        return UNKNOWN;
    }

    public static EntityTypesHandle getNMSEntityTypeByEntityClass(Class<?> entityClass) {
        // Look up from cache using a quick read lock
        long stamp = lock.readLock();
        try {
            EntityTypesHandle entityTypes = entityTypesByClass.get(entityClass);
            if (entityTypes != null) {
                return entityTypes;
            }
        } finally {
            lock.unlockRead(stamp);
        }

        // Query it and cache it, requiring exclusive write access
        stamp = lock.writeLock();
        try {
            EntityTypesHandle entityTypes = EntityTypesHandle.T.fromEntityClass.invoke(entityClass);
            entityTypesByClass.put(entityClass, entityTypes);
            return entityTypes;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    private static void registerObjectType(String entityTypeName, int objectId, int extraData) {
        ObjectTypeInfo info = new ObjectTypeInfo();
        info.typeId = objectId;
        info.extraData = extraData;
        objectTypes.put(entityTypeName, info);
    }

    private static class ObjectTypeInfo {
        public int typeId;
        public int extraData;
    }

    @SuppressWarnings("rawtypes")
    private static class CommonPair {
        public final Class<? extends Entity> bukkitType;
        public final Class<? extends CommonEntity> commonType;

        public CommonPair(Class<? extends Entity> bukkitType, Class<? extends CommonEntity> commonType) {
            this.bukkitType = bukkitType;
            this.commonType = commonType;
        }
    }

    static {
        // There does not appear to be a registry for this yet. This is a workaround until one exists.
        // This is only used on 1.13.2 and before, on 1.14 the Id can be obtained from the internal registry
        registerObjectType("BOAT", 1, -1);
        registerObjectType("DROPPED_ITEM", 2, 1);
        registerObjectType("AREA_EFFECT_CLOUD", 3, -1);
        registerObjectType("MINECART", 10, 0);
        registerObjectType("MINECART_CHEST", 10, 1);
        registerObjectType("MINECART_FURNACE", 10, 2);
        registerObjectType("MINECART_TNT", 10, 3);
        registerObjectType("MINECART_MOB_SPAWNER", 10, 4);
        registerObjectType("MINECART_HOPPER", 10, 5);
        registerObjectType("MINECART_COMMAND", 10, 6);
        registerObjectType("PRIMED_TNT", 50, -1);
        registerObjectType("ENDER_CRYSTAL", 51, -1);
        registerObjectType("ARROW", 60, -1);
        registerObjectType("TIPPED_ARROW", 60, -1);
        registerObjectType("SNOWBALL", 61, -1);
        registerObjectType("EGG", 62, -1);
        registerObjectType("FIREBALL", 63, -1);
        registerObjectType("SMALL_FIREBALL", 64, -1);
        registerObjectType("ENDER_PEARL", 65, -1);
        registerObjectType("WITHER_SKULL", 66, -1);
        registerObjectType("SHULKER_BULLET", 67, 0);
        registerObjectType("LLAMA_SPIT", 68, 0);
        registerObjectType("FALLING_BLOCK", 70, -1);
        registerObjectType("ITEM_FRAME", 71, -1);
        registerObjectType("ENDER_SIGNAL", 72, -1);
        registerObjectType("LINGERING_POTION", 73, 0);
        registerObjectType("SPLASH_POTION", 73, 0);
        registerObjectType("THROWN_EXP_BOTTLE", 75, 0);
        registerObjectType("FIREWORK", 76, -1);
        registerObjectType("LEASH_HITCH", 77, -1);
        registerObjectType("ARMOR_STAND", 78, -1);
        registerObjectType("EVOKER_FANGS", 79, -1);
        registerObjectType("FISHING_HOOK", 90, -1);
        registerObjectType("SPECTRAL_ARROW", 91, -1);
        registerObjectType("DRAGON_FIREBALL", 92, -1);

        // Remappings between some Bukkit entity types and Common entity types
        // Order is important, the first pair that can be used is selected
        // Since humans are living entities, they must be put before living entities
        commonPairs = new CommonPair[] {
                new CommonPair(HumanEntity.class, CommonHumanEntity.class),
                new CommonPair(LivingEntity.class, CommonLivingEntity.class),
                new CommonPair(Minecart.class, CommonMinecart.class)
        };

        // Register all entity types and verify them
        int logLimitCtr = 0;
        for (EntityType entityType : EntityType.values()) {
            if (entityType == EntityType.UNKNOWN) {
                continue; // ignore UNKNOWN
            }
            CommonEntityType commonEntityType = null;
            try {
                commonEntityType = new CommonEntityType(entityType, false);
            } catch (Throwable t) {
                if (++logLimitCtr <= 10) {
                    Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Failed to register entity type " + entityType.toString(), t);
                } else {
                    Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Failed to register entity type " + entityType.toString());
                }
            }
            if (commonEntityType == null) {
                commonEntityType = new CommonEntityType(entityType, true);
            }
            byNMS.put(commonEntityType.nmsType, commonEntityType);
            byEntityType.put(entityType, commonEntityType);
        }
    }
}
