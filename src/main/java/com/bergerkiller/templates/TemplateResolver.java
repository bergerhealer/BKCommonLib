package com.bergerkiller.templates;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.ClassDeclarationResolver;

public class TemplateResolver implements ClassDeclarationResolver {
    private final HashMap<Class<?>, ClassDeclaration> classes = new HashMap<Class<?>, ClassDeclaration>();
    private boolean classes_loaded = false;

    private final String[] supported_mc_versions = new String[] {
            "1.11.2"
    };

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

    /**
     * Further initializes this template resolver, making class declarations available
     */
    public void load() {
        if (!classes_loaded) {
            classes_loaded = true;

            String templatePath = "com/bergerkiller/templates/init.txt";
            Map<String, String> variables = new HashMap<String, String>();
            variables.put("version", Common.MC_VERSION);
            ClassLoader classLoader = TemplateResolver.class.getClassLoader();
            SourceDeclaration sourceDec = SourceDeclaration.parseFromResources(classLoader, templatePath);
            for (ClassDeclaration cdec : sourceDec.classes) {
                register(cdec);
            }
        }
    }

    private final void register(ClassDeclaration cdec) {
        classes.put(cdec.type.type, cdec);
        for (ClassDeclaration subcdec : cdec.subclasses) {
            register(subcdec);
        }
    }

    /**
     * Gets whether a particular MC package version is supported
     * 
     * @param mc_version to check
     * @return True if supported, False if not
     */
    public boolean isSupported(String mc_version) {
        return LogicUtil.contains(mc_version, supported_mc_versions);
    }

    /**
     * Gets a list of all supported MC versions
     * 
     * @return supported MC versions
     */
    public String[] getSupportedVersions() {
        return supported_mc_versions;
    }
}
