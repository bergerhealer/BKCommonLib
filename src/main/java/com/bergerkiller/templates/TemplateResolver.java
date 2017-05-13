package com.bergerkiller.templates;

import java.util.Collection;
import java.util.HashMap;

import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.ClassDeclarationResolver;

public class TemplateResolver implements ClassDeclarationResolver {
    private final HashMap<Class<?>, ClassDeclaration> classes = new HashMap<Class<?>, ClassDeclaration>();

    public TemplateResolver() {
        String templatePath = "com/bergerkiller/templates/v1_11_R1/init.txt";

        ClassLoader classLoader = TemplateResolver.class.getClassLoader();
        SourceDeclaration sourceDec = SourceDeclaration.parseFromResources(classLoader, templatePath);
        for (ClassDeclaration cdec : sourceDec.classes) {
            register(cdec);
        }
    }

    private final void register(ClassDeclaration cdec) {
        classes.put(cdec.type.type, cdec);
        for (ClassDeclaration subcdec : cdec.subclasses) {
            register(subcdec);
        }
    }

    @Override
    public ClassDeclaration resolveClassDeclaration(Class<?> type) {
        return classes.get(type);
    }

    /**
     * Gets all Class Declarations that are available at runtime
     * 
     * @return all class declarations
     */
    public Collection<ClassDeclaration> all() {
        return classes.values();
    }
}
