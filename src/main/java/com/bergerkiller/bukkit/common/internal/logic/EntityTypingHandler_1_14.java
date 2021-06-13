package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Random;

import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook_1_14;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypesHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.generated.net.minecraft.world.level.storage.WorldDataServerHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Since Minecraft 1.14 it has become much harder to know what NMS Entity
 * Class is spawned for various Entity Types. This class tracks
 * all these in a map.
 */
public class EntityTypingHandler_1_14 extends EntityTypingHandler {
    private final IdentityHashMap<Object, Class<?>> _cache = new IdentityHashMap<Object, Class<?>>();
    private final Handler _handler;
    private final Object nmsWorldHandle;

    // Initialize findEntityTypesClass which is a fallback for types we did not pre-register
    public EntityTypingHandler_1_14() {
        this._handler = Template.Class.create(Handler.class, Common.TEMPLATE_RESOLVER);
        this._handler.forceInitialization();

        // Initialize a dummy field with the sole purpose of constructing an entity without errors
        this.nmsWorldHandle = WorldServerHandle.T.newInstanceNull();
        {
            // Create WorldData instance by null-constructing it
            Object nmsWorldData;
            if (Common.evaluateMCVersion(">=", "1.16")) {
                nmsWorldData = WorldDataServerHandle.T.newInstanceNull();
            } else {
                nmsWorldData = ClassTemplate.create(WorldDataServerHandle.T.getType()).getConstructor().newInstance();
            }

            this._handler.initWorldServer(this.nmsWorldHandle, nmsWorldData);
            WorldHandle.T.random.set(this.nmsWorldHandle, new Random());
        }

        // Pre-register certain classes that cause events to be fired when constructing
        registerEntityTypes("AREA_EFFECT_CLOUD", "EntityAreaEffectCloud");
        registerEntityTypes("ENDER_DRAGON", "EntityEnderDragon");
        registerEntityTypes("FIREBALL", "EntityLargeFireball");
        registerEntityTypes("FISHING_BOBBER", "EntityFishingHook");
        registerEntityTypes("LIGHTNING_BOLT", "EntityLightning");
        registerEntityTypes("PLAYER", "EntityPlayer");
        registerEntityTypes("WITHER", "EntityWither"); // scoreboard things
    }

    private void registerEntityTypes(String name, String nmsClassName) {
        try {
            java.lang.reflect.Field field = EntityTypesHandle.T.getType().getDeclaredField(name);
            if ((field.getModifiers() & Modifier.STATIC) == 0) {
                throw new IllegalStateException("EntityTypes field " + name + " is not static");
            }
            
            Object nmsEntityTypes;
            if ((field.getModifiers() & Modifier.PUBLIC) == 0) {
                field.setAccessible(true);
                nmsEntityTypes = field.get(null);
                field.setAccessible(false);
            } else {
                nmsEntityTypes = field.get(null);
            }

            Class<?> type = CommonUtil.getNMSClass(nmsClassName);
            if (type == null) {
                throw new IllegalStateException("EntityTypes type " + nmsClassName + " not found");
            }

            this._cache.put(nmsEntityTypes, type);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public Class<?> getClassFromEntityTypes(Object nmsEntityTypesInstance) {
        Class<?> result = this._cache.get(nmsEntityTypesInstance);
        if (result == null) {
            result = _handler.findClassFromEntityTypes(nmsEntityTypesInstance, this.nmsWorldHandle);
            this._cache.put(nmsEntityTypesInstance, result);
        }
        return result;
    }

    @Override
    public EntityTrackerEntryHandle createEntityTrackerEntry(EntityTracker entityTracker, Entity entity) {
        Object handle = _handler.createEntry(entityTracker.getRawHandle(), HandleConversion.toEntityHandle(entity));
        EntityTrackerEntryHandle entry = EntityTrackerEntryHandle.createHandle(handle);

        // Set the passengers field to the current passengers
        EntityTrackerEntryStateHandle.T.opt_passengers.set(entry.getState().getRaw(), (new ExtendedEntity<Entity>(entity)).getPassengers());

        return entry;
    }

    @Override
    public EntityTrackerEntryHook getEntityTrackerEntryHook(Object entityTrackerEntryHandle) {
        return EntityTrackerEntryHook_1_14.get(entityTrackerEntryHandle, EntityTrackerEntryHook_1_14.class);
    }

    @Override
    public Object hookEntityTrackerEntry(Object entityTrackerEntryHandle) {
        return new EntityTrackerEntryHook_1_14().hook(entityTrackerEntryHandle);
    }

    @Template.Package("net.minecraft.server.level")
    @Template.Import("net.minecraft.world.entity.EntityTypes")
    @Template.Import("net.minecraft.world.level.storage.WorldDataServer")
    @Template.Import("net.minecraft.world.level.World")
    public static abstract class Handler extends Template.Class<Template.Handle> {

        /*
         * <CLASS_FROM_ENTITYTYPES>
         * public static Class<?> findClassFromEntityTypes((Object) EntityTypes entityTypes, (Object) World world) {
         *     Object entity = entityTypes.a(world);
         *     if (entity == null) {
         *         return null;
         *     } else {
         *         return entity.getClass();
         *     }
         * }
         */
        @Template.Generated("%CLASS_FROM_ENTITYTYPES%")
        public abstract Class<?> findClassFromEntityTypes(Object entityTypes, Object world);

        /*
         * <INIT_WORLD>
         * public static void initWorldServer((Object) WorldServer worldserver, (Object) WorldDataServer worldData) {
         * 
         * // Spigot World configuration
         * #if fieldexists net.minecraft.world.level.World public final org.spigotmc.SpigotWorldConfig spigotConfig;
         *     #require net.minecraft.world.level.World public final org.spigotmc.SpigotWorldConfig spigotConfig;
         *     org.spigotmc.SpigotWorldConfig spigotConfig = new org.spigotmc.SpigotWorldConfig("DUMMY");
         *     worldserver#spigotConfig = spigotConfig;
         * #endif
         * 
         * // PaperSpigot World configuration
         * #if fieldexists net.minecraft.world.level.World public final com.destroystokyo.paper.PaperWorldConfig paperConfig;
         *     #require net.minecraft.world.level.World public final com.destroystokyo.paper.PaperWorldConfig paperConfig;
         *     com.destroystokyo.paper.PaperWorldConfig paperConfig = new com.destroystokyo.paper.PaperWorldConfig("DUMMY", spigotConfig);
         *     worldserver#paperConfig = paperConfig;
         * #endif
         * 
         * // Purpur World configuration
         * #if fieldexists net.minecraft.world.level.World public final net.pl3x.purpur.PurpurWorldConfig purpurConfig;
         *     #require net.minecraft.world.level.World public final net.pl3x.purpur.PurpurWorldConfig purpurConfig;
         *     net.pl3x.purpur.PurpurWorldConfig purpurConfig;
         *   #if exists net.pl3x.purpur.PurpurWorldConfig public PurpurWorldConfig(String worldName, org.bukkit.World.Environment environment);
         *     purpurConfig = new net.pl3x.purpur.PurpurWorldConfig("DUMMY", org.bukkit.World$Environment.NORMAL);
         *   #elseif exists net.pl3x.purpur.PurpurWorldConfig public net.pl3x.purpur.PurpurWorldConfig(String worldName);
         *     purpurConfig = new net.pl3x.purpur.PurpurWorldConfig("DUMMY");
         *   #else
         *     purpurConfig = new net.pl3x.purpur.PurpurWorldConfig("DUMMY", paperConfig, spigotConfig);
         *   #endif
         *     worldserver#purpurConfig = purpurConfig;
         * #endif
         * 
         * #if version >= 1.16
         *     // WorldDataMutable field
         *     #require net.minecraft.world.level.World public final net.minecraft.world.level.storage.WorldDataMutable worldData;
         *     worldserver#worldData = worldData;
         * 
         *     // WorldDataServer field (on some servers, it uses the WorldDataMutable field instead)
         *   #if exists net.minecraft.server.level.WorldServer public final net.minecraft.world.level.storage.WorldDataServer worldDataServer;
         *     #require net.minecraft.server.level.WorldServer public final net.minecraft.world.level.storage.WorldDataServer worldDataServer;
         *     worldserver#worldDataServer = worldData;
         *   #endif
         * #else
         *     // worldProvider field
         *     int envId = org.bukkit.World.Environment.NORMAL.getId();
         *     worldserver.worldProvider = net.minecraft.world.level.dimension.DimensionManager.a(envId).getWorldProvider((World) worldserver);
         * 
         *     // worldData field
         *     #require net.minecraft.world.level.World public final net.minecraft.world.level.storage.WorldData worldData;
         *     worldserver#worldData = worldData;
         * #endif    
         * }
         */
        @Template.Generated("%INIT_WORLD%")
        public abstract void initWorldServer(Object world, Object worldData);

        /*
         * <CREATE_ENTRY>
         * public static Object createEntry((Object) PlayerChunkMap playerChunkMap, (Object) Entity entity) {
         *     EntityTypes entitytypes = entity.getEntityType();
         *     int i = entitytypes.getChunkRange() * 16;
         * #if spigot
         *     i = org.spigotmc.TrackingRange.getEntityTrackingRange(entity, i);
         * #endif
         *     int j = entitytypes.getUpdateInterval();
         *     return new PlayerChunkMap$EntityTracker(playerChunkMap, entity, i, j, entitytypes.isDeltaTracking());
         * }
         */
        @Template.Generated("%CREATE_ENTRY%")
        public abstract Object createEntry(Object playerChunkMap, Object entity);
    }
}
