package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypesHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

public class EntityTypingHandler_1_13 extends EntityTypingHandler_1_8 {
    private final FastMethod<Class<?>> findEntityTypesClass = new FastMethod<Class<?>>();

    public EntityTypingHandler_1_13() {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(EntityTypesHandle.T.getType());
        MethodDeclaration m = new MethodDeclaration(resolver, "public Class<? extends T> c();");
        try {
            m.method = resolver.getDeclaredClass().getDeclaredMethod("c");
        } catch (Throwable t) {}
        findEntityTypesClass.init(m);
    }

    @Override
    public Class<?> getClassFromEntityTypes(Object nmsEntityTypesInstance) {
        return this.findEntityTypesClass.invoke(nmsEntityTypesInstance);
    }
}
