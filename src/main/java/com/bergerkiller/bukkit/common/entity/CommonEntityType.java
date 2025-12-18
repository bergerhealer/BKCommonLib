package com.bergerkiller.bukkit.common.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.entity.type.*;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypesHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.EntityMinecartAbstractHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;

import java.lang.reflect.Constructor;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * Stores all internal information about an Entity
 */
@SuppressWarnings({"unchecked", "deprecation"})
public class CommonEntityType {
    static {
        CommonBootstrap.initCommonServerAssertCompatibility();
    }

    private static final StampedLock lock = new StampedLock();
    private static final ClassMap<CommonEntityType> byNMS = new ClassMap<CommonEntityType>();
    private static final EnumMap<EntityType, CommonEntityType> byEntityType = new EnumMap<EntityType, CommonEntityType>(EntityType.class);
    private static final Map<String, CommonEntityType> byName = new HashMap<>();
    private static final Map<Integer, CommonEntityType> byObjectTypeId = new HashMap<>();
    private static final Map<Integer, CommonEntityType> byEntityTypeId = new HashMap<>();
    private static final Map<Object, CommonEntityType> byNMSEntityType = new HashMap<>();
    private static final Map<Class<?>, EntityTypesHandle> entityTypesByClass = new HashMap<>();

    // Remappings between some Bukkit entity types and Common entity types
    // Order is important, the first pair that can be used is selected
    // Since humans are living entities, they must be put before living entities
    private static final CommonPair[] commonPairs = new CommonPair[] {
            CommonPair.create(CommonPlayer.class, CommonPlayer::new),
            CommonPair.create(CommonMinecartChest.class, CommonMinecartChest::new),
            CommonPair.create(CommonMinecartCommandBlock.class, CommonMinecartCommandBlock::new),
            CommonPair.create(CommonMinecartFurnace.class, CommonMinecartFurnace::new),
            CommonPair.create(CommonMinecartHopper.class, CommonMinecartHopper::new),
            CommonPair.create(CommonMinecartMobSpawner.class, CommonMinecartMobSpawner::new),
            CommonPair.create(CommonMinecartTNT.class, CommonMinecartTNT::new),
            CommonPair.create(CommonMinecartRideable.class, CommonMinecartRideable::new),
            CommonPair.create(CommonMinecartCommandBlock.class, CommonMinecartCommandBlock::new),
            CommonPair.create(CommonMinecartUnknown.class, CommonMinecartUnknown::new),
            CommonPair.create(CommonItem.class, CommonItem::new),
            CommonPair.<Minecart>create(CommonMinecart.class, CommonMinecart::new),
            CommonPair.<LivingEntity>create(CommonLivingEntity.class, CommonLivingEntity::new)
    };

    public static final CommonEntityType UNKNOWN = new CommonEntityType(EntityType.UNKNOWN, true);

    static {
        initRegistry();
    }

    public static final CommonEntityType PLAYER = byName("PLAYER");
    public static final CommonEntityType MINECART = byName("MINECART");
    public static final CommonEntityType CHEST_MINECART = byName("CHEST_MINECART");
    public static final CommonEntityType FURNACE_MINECART = byName("FURNACE_MINECART");
    public static final CommonEntityType TNT_MINECART = byName("TNT_MINECART");
    public static final CommonEntityType HOPPER_MINECART = byName("HOPPER_MINECART");
    public static final CommonEntityType SPAWNER_MINECART = byName("SPAWNER_MINECART");
    public static final CommonEntityType COMMAND_BLOCK_MINECART = byName("COMMAND_BLOCK_MINECART");

    public final ClassTemplate<?> nmsType;
    public final ClassTemplate<?> commonType;
    public final ClassTemplate<?> bukkitType;
    private final Function<Entity, CommonEntity<?>> commonConstructor;
    private final boolean hasWorldCoordConstructor;
    public final List<String> entityTypeNames;
    public final EntityType entityType;
    public final int entityTypeId;
    public final int objectTypeId;
    public final int objectExtraData;
    public final int moveTicks;
    public final EntityTypesHandle nmsEntityType;

    private CommonEntityType(EntityType entityType, boolean nullInitialize) {
        // Properties first
        this.entityType = entityType;
        this.entityTypeNames = TypeNameAliases.getNames(entityType);

        // A type that is not supported for construction
        if (nullInitialize) {
            this.nmsType = ClassTemplate.create((Class<?>) null);
            this.commonType = ClassTemplate.create(CommonEntity.class);
            this.bukkitType = ClassTemplate.create(Entity.class);
            this.commonConstructor = CommonEntity::new;
            this.hasWorldCoordConstructor = false;
            this.entityTypeId = -1;
            this.nmsEntityType = null;
            this.objectTypeId = -1;
            this.objectExtraData = -1;
            this.moveTicks = 3;
            return;
        }

        // Figure out what kind of NMS Class belongs to this EntityType by comparing with the internal NMS EntityTypes listing
        Class<?> nmsType = null;

        // Registered entity types
        // Create minecraft key from EntityType name
        String entityTypeName = entityType.getName();
        String entityTypeEnumName = entityType.name();
        if (entityTypeNames.contains("FURNACE_MINECART")) {
            // New naming system had a bug, DAMMIT BUKKIT
            if (Common.evaluateMCVersion(">=", "1.11")) {
                entityTypeName = "furnace_minecart";
            }
        } else if (entityTypeNames.contains("SPAWNER_MINECART")) {
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
                nmsName = "net.minecraft.server.level.EntityPlayer";
            } else if (entityTypeEnumName.equals("FISHING_HOOK")) {
                nmsName = "net.minecraft.world.entity.projectile.EntityFishingHook";
            } else if (entityTypeEnumName.equals("LIGHTNING")) {
                nmsName = "net.minecraft.world.entity.EntityLightning";
            } else if (entityTypeEnumName.equals("WEATHER")) {
                nmsName = "net.minecraft.world.entity.EntityWeather";
            } else if (entityTypeEnumName.equals("COMPLEX_PART")) {
                nmsName = "net.minecraft.world.entity.boss.EntityComplexPart";
            }

            // <= 1.10.2 (now removed)
            if (entityTypeEnumName.equals("EGG")) {
                nmsName = "net.minecraft.world.entity.projectile.EntityEgg";
            } else if (entityTypeEnumName.equals("AREA_EFFECT_CLOUD")) {
                nmsName = "net.minecraft.world.entity.EntityAreaEffectCloud";
            } else if (entityTypeEnumName.equals("SPLASH_POTION")) {
                nmsName = "net.minecraft.world.entity.projectile.EntityPotion";
            }

            // Added in >= 1.10.2
            if (entityTypeEnumName.equals("TIPPED_ARROW")) {
                nmsName = "net.minecraft.world.entity.projectile.EntityTippedArrow";
            } else if (entityTypeEnumName.equals("LINGERING_POTION")) {
                nmsName = "net.minecraft.world.entity.projectile.EntityPotion";
            }

            // Added in >= 1.19.3
            if (entityTypeEnumName.equals("CAMEL")) {
                nmsName = "net.minecraft.world.entity.animal.camel.Camel";
            }

            // Added in >= 1.19.4
            if (entityTypeEnumName.equals("SNIFFER")) {
                nmsName = "net.minecraft.world.entity.animal.sniffer.Sniffer";
            }

            // Try retrieving NMS class again
            if (nmsName != null) {
                nmsType = CommonUtil.getClass(nmsName);
            }

            // Check this Entity Type isn't a custom Forge Entity type
            // In that case, don't log this warning
            if (nmsType == null && !Common.SERVER.isCustomEntityType(entityType)) {
                if (nmsName == null) {
                    Logging.LOGGER_REGISTRY.log(Level.WARNING, "Entity type could not be registered: unknown type (" + entityType.toString() + ")");
                } else {
                    Logging.LOGGER_REGISTRY.log(Level.WARNING, "Entity type could not be registered: class not found (" + entityType.toString() + ") class=" + nmsName);
                }
            }
        }

        // Obtain Bukkit type
        this.bukkitType = ClassTemplate.create(entityType.getEntityClass());

        // Obtain Common class type and constructor
        Class<?> commonType = CommonEntity.class;
        Class<?> entityClass;

        // Note: Paper fix for 1.21.5 where they refer to Minecart.class instead of RideableMinecart.class
        // https://github.com/PaperMC/Paper/commit/e983d3b61c614bbc5c4c8da37011f70de1ee8aa6 fixes it too
        if (CommonBootstrap.evaluateMCVersion("==", "1.21.5") && entityType == EntityType.MINECART) {
            entityClass = org.bukkit.entity.minecart.RideableMinecart.class;
        } else {
            entityClass = this.bukkitType.getType();
        }

        Function<Entity, CommonEntity<?>> commonConstructor = CommonEntity::new;
        if (entityClass != null) {
            for (CommonPair pair : commonPairs) {
                if (pair.bukkitType.isAssignableFrom(entityClass)) {
                    commonType = pair.commonType;
                    entityClass = pair.bukkitType;
                    commonConstructor = pair.constructor;
                    break;
                }
            }
        }

        this.nmsType = ClassTemplate.create(nmsType);
        this.commonType = ClassTemplate.create(commonType);
        this.commonConstructor = commonConstructor;

        this.entityTypeId = EntityTypesHandle.getEntityTypeId(nmsType);
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
                nmsType.getConstructor(CommonUtil.getClass("net.minecraft.world.level.World"), double.class, double.class, double.class);
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
            LegacyObjectTypes.ObjectTypeInfo objectInfo = LegacyObjectTypes.find(entityType);
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

        //TODO: Can this be read from somewhere?
        if (this.nmsType.getType() != null && EntityMinecartAbstractHandle.T.isAssignableFrom(this.nmsType.getType())) {
            this.moveTicks = 5;
        } else {
            this.moveTicks = 3;
        }
    }

    public <T extends Entity> CommonEntity<T> createCommonEntity(T entity) {
        return (CommonEntity<T>) this.commonConstructor.apply(entity);
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
        Entity e = WrapperConversion.toEntity(handle); // getBukkitEntity
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
                    new Class<?>[] {WorldHandle.T.getType(), double.class, double.class, double.class},
                    new Object[] { Conversion.toWorldHandle.convert(world), x, y, z });
        } else if (CommonCapabilities.ENTITY_USES_ENTITYTYPES_IN_CONSTRUCTOR) {
            if (this.nmsEntityType == null) {
                throw new IllegalStateException("Type " + this.toString() + " cannot be constructed");
            }
            handle = hook.constructInstance(this.nmsType.getType(),
                    new Class<?>[] {EntityTypesHandle.T.getType(), WorldHandle.T.getType()},
                    new Object[] { this.nmsEntityType.getRaw(), Conversion.toWorldHandle.convert(world) });
        } else {
            handle = hook.constructInstance(this.nmsType.getType(),
                    new Class<?>[] {WorldHandle.T.getType()},
                    new Object[] { Conversion.toWorldHandle.convert(world) });
        }

        CommonEntity<T> entity = createCommonEntityFromHandle(handle);
        entity.loc.set(entity.last.set(location)); //Note: sets world also!

        // Refreshes bounding box when the constructor without x/y/z is called
        // The constructor with x/y/z parameters does this automatically
        if (!this.hasWorldCoordConstructor) {
            entity.setPosition(location.getX(), location.getY(), location.getZ());
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
    @ConverterMethod
    public static CommonEntityType byEntityType(EntityType type) {
        return LogicUtil.fixNull(byEntityType.get(type), UNKNOWN);
    }

    public static CommonEntityType byEntity(Entity entity) {
        return byNMSEntity(HandleConversion.toEntityHandle(entity));
    }

    @ConverterMethod
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

    @ConverterMethod(input="net.minecraft.world.entity.EntityTypes")
    public static CommonEntityType byNMSEntityTypeRaw(Object entityTypesHandleRaw) {
        if (entityTypesHandleRaw != null) {
            CommonEntityType type = byNMSEntityType.get(entityTypesHandleRaw);
            if (type != null) {
                return type;
            }

            // Try by class instead
            if (EntityTypesHandle.T.getEntityClassInst.isAvailable()) {
                Class<?> nmsType = EntityTypesHandle.T.getEntityClassInst.invoke(entityTypesHandleRaw);
                return byNMSEntityClass(nmsType);
            }
        }
        return UNKNOWN;
    }

    @ConverterMethod(output="net.minecraft.world.entity.EntityTypes")
    public static Object toNMSEntityTypeRaw(CommonEntityType entityType) {
        return entityType.nmsEntityType == null ? null : entityType.nmsEntityType.getRaw();
    }

    /**
     * Gets the Common Entity Type by a Bukkit Entity Type enum name. Also supports future or past
     * enum names if such enum values were renamed.
     *
     * @param name Name of the Bukkit EntityType enum value
     * @return CommonEntityType of this name, or {@link #UNKNOWN} if not found
     */
    public static CommonEntityType byName(String name) {
        return byName.getOrDefault(name, UNKNOWN);
    }

    /**
     * Parses a name to the best-matching CommonEntityType, based on the EntityType enum names.
     * Also parses inexact names. For exact names, use {@link #byName(String)}.
     *
     * @param name Name
     * @return Found CommonEntityType that matches this name, or null if none match
     */
    @ConverterMethod()
    public static CommonEntityType parseNameToCommonEntityType(String name) {
        TypeNameLookup.NamePair result = TypeNameLookup.lookupByName(name);
        return result == null ? null : result.commonEntityType;
    }

    /**
     * Parses a name to the best-matching Bukkit EntityType, based on the EntityType enum names.
     * Also parses inexact names. For exact names, use {@link #byName(String)}.
     *
     * @param name Name
     * @return Found EntityType that matches this name, or null if none match
     */
    @ConverterMethod()
    public static EntityType parseNameToEntityType(String name) {
        TypeNameLookup.NamePair result = TypeNameLookup.lookupByName(name);
        return result == null ? null : result.entityType;
    }

    public static CommonEntityType byNMSEntityType(EntityTypesHandle handle) {
        return (handle == null) ? UNKNOWN : byNMSEntityTypeRaw(handle.getRaw());
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

    @SuppressWarnings("rawtypes")
    private static class CommonPair {
        public final Class<? extends Entity> bukkitType;
        public final Class<? extends CommonEntity> commonType;
        public final Function<Entity, CommonEntity<?>> constructor;

        private CommonPair(Class<? extends Entity> bukkitType, Class<? extends CommonEntity> commonType, Function<Entity, CommonEntity<?>> constructor) {
            this.bukkitType = bukkitType;
            this.commonType = commonType;
            this.constructor = constructor;
        }

        public static <B extends Entity> CommonPair create(
                final Class<? extends CommonEntity> commonType,
                final Function<B, ? extends CommonEntity<?>> constructor
        ) {
            // Find constructor and derive bukkit type from that
            for (Constructor<?> c : commonType.getDeclaredConstructors()) {
                if (c.getParameterCount() == 1) {
                    Class<?> inputType = c.getParameterTypes()[0];
                    if (Entity.class.isAssignableFrom(inputType)) {
                        return new CommonPair((Class<? extends Entity>) inputType, commonType,
                                (Function<Entity, CommonEntity<?>>) constructor);
                    }
                }
            }

            throw new IllegalStateException("CommonType has no valid constructor: " + commonType.getSimpleName());
        }
    }

    private static void initRegistry() {
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
            for (String name : commonEntityType.entityTypeNames) {
                byName.put(name, commonEntityType);
            }
        }
    }
}
