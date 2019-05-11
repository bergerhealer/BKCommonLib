package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.EntityTypesHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * Since Minecraft 1.14 it has become much harder to know what NMS Entity
 * Class is spawned for various Entity Types. This class tracks
 * all these in a map.
 */
public class EntityTypesClasses_1_14 extends EntityTypesClasses {
    private final IdentityHashMap<Object, Class<?>> _cache = new IdentityHashMap<Object, Class<?>>();
    private final FastMethod<Class<?>> findEntityTypesClass = new FastMethod<Class<?>>();
    private final Object nmsWorldHandle;

    // Initialize findEntityTypesClass which is a fallback for types we did not pre-register
    public EntityTypesClasses_1_14() {
        {
            ClassResolver resolver = new ClassResolver();
            resolver.setDeclaredClass(EntityTypesHandle.T.getType());
            MethodDeclaration m = new MethodDeclaration(resolver, 
                    "public Class<?> findClassFromEntityTypes(net.minecraft.server.World world) {\n" +
                    "    Object entity = instance.a(world);\n" +
                    "    if (entity == null) {\n" +
                    "        return null;\n" +
                    "    } else {\n" +
                    "        return entity.getClass();\n" +
                    "    }\n" +
                    "}");
            findEntityTypesClass.init(m);
        }

        // Initialize a dummy field with the sole purpose of constructing an entity without errors
        this.nmsWorldHandle = WorldServerHandle.T.newInstanceNull();
        {
            // Create WorldData instance by calling the protected empty constructor
            Object nmsWorldData = ClassTemplate.createNMS("WorldData").getConstructor().newInstance();

            ClassResolver resolver = new ClassResolver();
            resolver.setDeclaredClass(WorldServerHandle.T.getType());
            MethodDeclaration m = new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                    "public void initWorldServer(WorldData worldData) {\n" +
                    "    // Spigot World configuration\n" +
                    "#if fieldexists net.minecraft.server.World public final org.spigotmc.SpigotWorldConfig spigotConfig;\n" +
                    "    #require net.minecraft.server.World public final org.spigotmc.SpigotWorldConfig spigotConfig;\n" +
                    "    org.spigotmc.SpigotWorldConfig config = new org.spigotmc.SpigotWorldConfig(\"DUMMY\");\n" +
                    "    instance#spigotConfig = config;\n" +
                    "#endif\n" +
                    "\n" +
                    "    // worldProvider field\n" +
                    "    int envId = org.bukkit.World.Environment.NORMAL.getId();\n" +
                    "    instance.worldProvider = DimensionManager.a(envId).getWorldProvider(instance);\n" +
                    "\n" +
                    "    // worldData field\n" +
                    "    #require net.minecraft.server.World public final WorldData worldData;\n" +
                    "    instance#worldData = worldData;\n" +
                    "}"));
            FastMethod<Void> fm = new FastMethod<Void>();
            fm.init(m);
            fm.invoke(this.nmsWorldHandle, nmsWorldData);
        }

        // Pre-register certain classes that cause events to be fired when constructing
        registerEntityTypes("AREA_EFFECT_CLOUD", "EntityAreaEffectCloud");
        registerEntityTypes("ENDER_DRAGON", "EntityEnderDragon");
        registerEntityTypes("FIREBALL", "EntityLargeFireball");
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
            result = this.findEntityTypesClass.invoke(nmsEntityTypesInstance, this.nmsWorldHandle);
            this._cache.put(nmsEntityTypesInstance, result);
        }
        return result;
    }
}
