package com.bergerkiller.bukkit.common.internal.templates;

import java.util.HashMap;

import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.ClassDeclarationResolver;

public class TemplateResolver implements ClassDeclarationResolver {
    private final HashMap<Class<?>, ClassDeclaration> classes = new HashMap<Class<?>, ClassDeclaration>();

    public TemplateResolver() {
        SourceDeclaration sourceDec = SourceDeclaration.parseFromResources("com/bergerkiller/bukkit/common/internal/templates/versions/v1_11_R1.txt");
        for (ClassDeclaration cdec : sourceDec.classes) {
            classes.put(cdec.type.type, cdec);
        }
    }

    @Override
    public ClassDeclaration resolveClassDeclaration(Class<?> type) {
        return classes.get(type);
    }

}
