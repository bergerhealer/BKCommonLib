package com.bergerkiller.templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.server.CommonServer;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.ClassDeclarationResolver;

public class TemplateResolver implements ClassDeclarationResolver {
    private HashMap<Class<?>, List<ClassDeclaration>> classes = new HashMap<Class<?>, List<ClassDeclaration>>();
    private boolean classes_loaded = false;
    private String version = "UNKNOWN";
    private final Map<String, String> variables = new HashMap<>();

    private final String[] supported_mc_versions = new String[] {
            "1.8", "1.8.3", "1.8.4", "1.8.5", "1.8.6", "1.8.7", "1.8.8",
            "1.9", "1.9.2", "1.9.4",
            "1.10.2",
            "1.11", "1.11.2",
            "1.12", "1.12.1", "1.12.2",
            "1.13", "1.13.1", "1.13.2",
            "1.14", "1.14.1", "1.14.2", "1.14.3", "1.14.4",
            "1.15", "1.15.1", "1.15.2",
            "1.16.1", "1.16.2", "1.16.3"
    };

    @Override
    public ClassDeclaration resolveClassDeclaration(String classPath, Class<?> type) {
        List<ClassDeclaration> allByType = classes.get(type);
        if (allByType == null || allByType.isEmpty()) {
            return null;
        } else if (allByType.size() == 1) {
            return allByType.get(0);
        } else {
            // Poor man's method of selecting the right packet using Class Simple Name
            String name = classPath;
            int name_endidx = name.lastIndexOf('.');
            if (name_endidx != -1) {
                name = name.substring(name_endidx + 1);
            }
            for (ClassDeclaration dec : allByType) {
                if (dec.type.typeName.equals(name)) {
                    return dec;
                }
            }
            return allByType.get(0);
        }
    }

    @Override
    public void resolveClassVariables(String classPath, Class<?> classType, Map<String, String> variables) {
        variables.putAll(this.variables);
    }

    /**
     * Gets all Class Declarations that are available at runtime
     * 
     * @return all class declarations
     */
    public Collection<ClassDeclaration> all() {
        List<ClassDeclaration> all = new ArrayList<ClassDeclaration>(classes.size() + 10);
        for (List<ClassDeclaration> ls : classes.values()) {
            all.addAll(ls);
        }
        return all;
    }

    /**
     * Unloads all class declarations from the cache
     */
    public void unload() {
        this.classes_loaded = false;
        this.classes = new HashMap<Class<?>, List<ClassDeclaration>>(0);
    }

    /**
     * Further initializes this template resolver, making class declarations available
     */
    public void load() {
        if (!classes_loaded) {
            classes_loaded = true;
            this.version = Common.SERVER.getMinecraftVersionMajor();

            String templatePath = "com/bergerkiller/templates/init.txt";
            this.variables.clear();
            Common.SERVER.addVariables(this.variables);

            ClassLoader classLoader = TemplateResolver.class.getClassLoader();
            SourceDeclaration sourceDec = SourceDeclaration.parseFromResources(classLoader, templatePath, this.variables);
            for (ClassDeclaration cdec : sourceDec.classes) {
                register(cdec);
            }
        }
    }

    private final void register(ClassDeclaration cdec) {
        List<ClassDeclaration> old_list = classes.get(cdec.type.type);
        if (old_list == null) {
            classes.put(cdec.type.type, Collections.singletonList(cdec));
        } else {
            ArrayList<ClassDeclaration> new_list = new ArrayList<ClassDeclaration>(old_list);
            new_list.add(cdec);
            new_list.trimToSize();
            classes.put(cdec.type.type, new_list);
        }
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
        return LogicUtil.contains(CommonServer.cleanVersion(mc_version), supported_mc_versions);
    }

    /**
     * Gets a list of all supported MC versions
     * 
     * @return supported MC versions
     */
    public String[] getSupportedVersions() {
        return supported_mc_versions;
    }

    /**
     * Gets the current server version, excluding -pre information
     * 
     * @return version number
     */
    public String getVersion() {
        return this.version;
    }
}
