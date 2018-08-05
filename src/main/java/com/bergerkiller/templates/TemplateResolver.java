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
    private HashMap<Class<?>, ClassDeclaration> classes = new HashMap<Class<?>, ClassDeclaration>();
    private boolean classes_loaded = false;
    private String version = "UNKNOWN";
    private String pre_version = null;

    private final String[] supported_mc_versions = new String[] {
            "1.8", "1.8.3", "1.8.4", "1.8.5", "1.8.6", "1.8.7", "1.8.8",
            "1.9", "1.9.2", "1.9.4",
            "1.10.2",
            "1.11", "1.11.2",
            "1.12", "1.12.1", "1.12.2",
            "1.13"
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
     * Unloads all class declarations from the cache
     */
    public void unload() {
        this.classes_loaded = false;
        this.classes = new HashMap<Class<?>, ClassDeclaration>(0);
    }

    /**
     * Further initializes this template resolver, making class declarations available
     */
    public void load() {
        if (!classes_loaded) {
            classes_loaded = true;
            this.version = cleanVersion(Common.MC_VERSION);
            this.pre_version = preVersion(Common.MC_VERSION);

            String templatePath = "com/bergerkiller/templates/init.txt";
            Map<String, String> variables = new HashMap<String, String>();
            variables.put("version", this.version);
            if (this.pre_version != null) {
                variables.put("pre", this.pre_version);
            }
            if (Common.IS_SPIGOT_SERVER) {
                variables.put("spigot", "true");
            }

            ClassLoader classLoader = TemplateResolver.class.getClassLoader();
            SourceDeclaration sourceDec = SourceDeclaration.parseFromResources(classLoader, templatePath, variables);
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
        return LogicUtil.contains(cleanVersion(mc_version), supported_mc_versions);
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

    private static String cleanVersion(String mc_version) {
        String clean_version = mc_version;
        int pre_idx = clean_version.indexOf("-pre");
        if (pre_idx != -1) {
            clean_version = clean_version.substring(0, pre_idx);
        }
        return clean_version;
    }

    private static String preVersion(String mc_version) {
        int pre_idx = mc_version.indexOf("-pre");
        if (pre_idx != -1) {
            return mc_version.substring(pre_idx + 4);
        }
        return null;
    }
}
