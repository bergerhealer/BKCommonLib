package com.bergerkiller.bukkit.common.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.type.CommonLivingEntity;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.SafeConstructor;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityTypes;
import com.bergerkiller.reflection.net.minecraft.server.NMSMinecraftKey;
import com.bergerkiller.reflection.net.minecraft.server.NMSRegistryMaterials;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorld;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;

import java.util.EnumMap;
import java.util.logging.Level;

/**
 * Stores all internal information about an Entity
 */
@SuppressWarnings({"unchecked", "deprecation"})
public class CommonEntityType {
    public static final CommonEntityType UNKNOWN = new CommonEntityType(EntityType.UNKNOWN, true);
    private static final ClassMap<CommonEntityType> byNMS = new ClassMap<CommonEntityType>();
    private static final EnumMap<EntityType, CommonEntityType> byEntityType = new EnumMap<EntityType, CommonEntityType>(EntityType.class);

    public final ClassTemplate<?> nmsType;
    public final ClassTemplate<?> commonType;
    public final ClassTemplate<?> bukkitType;
    private final SafeConstructor<?> commonConstructor;
    public final EntityType entityType;

    private CommonEntityType(EntityType entityType, boolean nullInitialize) {
        // Properties first
        this.entityType = entityType;

        // A type that is not supported for construction
        if (nullInitialize) {
            this.nmsType = ClassTemplate.create((Class<?>) null);
            this.commonType = ClassTemplate.create(CommonEntity.class);
            this.bukkitType = ClassTemplate.create(Entity.class);
            this.commonConstructor = this.commonType.getConstructor(Entity.class);
            return;
        }

        // Figure out what kind of NMS Class belongs to this EntityType by comparing with the internal NMS EntityTypes listing
        Class<?> nmsType = null;
        Object entityMapping = NMSEntityTypes.entityMapping.get(null);
        if (entityMapping == null) {
            Logging.LOGGER_REGISTRY.once(Level.SEVERE, "Failed to initialize CommonEntityType registry: no entity mapping registry");
        } else {
            // Create minecraft key from EntityType name
            String entityTypeName;
            if (entityType == EntityType.MINECART_FURNACE) {
                entityTypeName = "furnace_minecart"; // DAMMIT BUKKIT
            } else {
                entityTypeName = entityType.getName();
            }
            if (entityTypeName != null) {
                Object mcKey = NMSMinecraftKey.newInstance(entityTypeName);
                nmsType = (Class<?>) NMSRegistryMaterials.getValue.invoke(entityMapping, mcKey);
                if (nmsType == null) {
                    // Try by EntityTypeId instead
                    Logging.LOGGER_REGISTRY.log(Level.WARNING, "Failed to get by name: " + entityTypeName + " (" + entityType.toString() + ")");
                }
            } else {
                // EntityType without a MC name? That's not good.
                Logging.LOGGER_REGISTRY.log(Level.WARNING, "Entity type could not be registered: no name (" + entityType.toString() + ")");
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
        if (commonType == null) {
            // No specifics - try to find a sub-category
            if (LivingEntity.class.isAssignableFrom(entityClass)) {
                commonType = CommonLivingEntity.class;
                entityClass = LivingEntity.class;
            } else if (Minecart.class.isAssignableFrom(entityClass)) {
                commonType = CommonMinecart.class;
                entityClass = Minecart.class;
            } else {
                commonType = CommonEntity.class;
                entityClass = Entity.class;
            }
        }

        this.nmsType = ClassTemplate.create(nmsType);
        this.commonType = ClassTemplate.create(commonType);
        this.commonConstructor = this.commonType.getConstructor(entityClass);
    }

    public <T extends Entity> CommonEntity<T> createCommonEntity(T entity) {
        return (CommonEntity<T>) this.commonConstructor.newInstance(entity);
    }

    public <T extends Entity> CommonEntity<T> createCommonEntityNull() {
        EntityHook hook = new EntityHook();
        Object handle = hook.createInstance(this.nmsType.getType());

        CommonEntity<T> entity = createCommonEntityFromHandle(handle);

        DefaultEntityController controller = new DefaultEntityController();
        controller.bind(entity);

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

        EntityHook hook = new EntityHook();
        Object handle = hook.constructInstance(this.nmsType.getType(), 
                new Class<?>[] {NMSWorld.T.getType(), double.class, double.class, double.class},
                new Object[] { Conversion.toWorldHandle.convert(world), x, y, z });

        CommonEntity<T> entity = createCommonEntityFromHandle(handle);
        entity.loc.set(entity.last.set(location));

        DefaultEntityController controller = new DefaultEntityController();
        controller.bind(entity);

        return entity;
    }

    public Object createNMSHookFromEntity(CommonEntity<?> entity) {
        EntityHook hook = new EntityHook();
        return hook.hook(entity.getHandle());
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

    static {
        for (EntityType entityType : EntityType.values()) {
            CommonEntityType commonEntityType = null;
            try {
                if (entityType.getTypeId() != -1) {
                    commonEntityType = new CommonEntityType(entityType, false);
                }
            } catch (Throwable t) {
                Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Failed to register entity type " + entityType.toString(), t);
            }
            if (commonEntityType == null) {
                commonEntityType = new CommonEntityType(entityType, true);
            }
            byNMS.put(commonEntityType.nmsType, commonEntityType);
            byEntityType.put(entityType, commonEntityType);
        }
    }
}
