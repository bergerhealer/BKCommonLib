package com.bergerkiller.bukkit.common.internal.logic;

import java.util.IdentityHashMap;

import com.bergerkiller.generated.net.minecraft.server.EntityTypesHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * Since Minecraft 1.14 it has become much harder to know what NMS Entity
 * Class is spawned for various Entity Types. This class tracks
 * all these in a map.
 */
public class EntityTypesClasses_1_14 extends EntityTypesClasses {
    private final IdentityHashMap<Object, Class<?>> _cache = new IdentityHashMap<Object, Class<?>>();
    private final FastMethod<Class<?>> findEntityTypesClass = new FastMethod<Class<?>>();

    // Initialize findEntityTypesClass which is a fallback for types we did not pre-register
    public EntityTypesClasses_1_14() {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(EntityTypesHandle.T.getType());
        MethodDeclaration m = new MethodDeclaration(resolver, 
                "public Class<?> findClassFromEntityTypes() {\n" +
                "    Object entity = instance.a((net.minecraft.server.World) null);\n" +
                "    if (entity == null) {\n" +
                "        return null;\n" +
                "    } else {\n" +
                "        return entity.getClass();\n" +
                "    }\n" +
                "}");
        findEntityTypesClass.init(m);

        //TODO: Pre-register certain classes that cause events to be fired when constructing
    }

    @Override
    public Class<?> getClassFromEntityTypes(Object nmsEntityTypesInstance) {
        Class<?> result = _cache.get(nmsEntityTypesInstance);
        if (result == null) {
            result = findEntityTypesClass.invoke(nmsEntityTypesInstance);
            _cache.put(nmsEntityTypesInstance, result);
        }
        return result;
    }
}
