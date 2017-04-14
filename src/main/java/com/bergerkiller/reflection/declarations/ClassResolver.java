package com.bergerkiller.reflection.declarations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Resolves class names into Class Types based on package and import paths.
 */
public class ClassResolver {
    private static final List<String> default_imports = Arrays.asList("java.lang.*", "java.util.*");
    private final HashSet<String> imports;
    private final List<String> manualImports;
    private String packagePath;

    private ClassResolver(ClassResolver src) {
        this.imports = new HashSet<String>(src.imports);
        this.manualImports = new ArrayList<String>(src.manualImports);
        this.packagePath = src.packagePath;
    }
    
    public ClassResolver() {
        this.imports = new HashSet<String>(default_imports);
        this.manualImports = new ArrayList<String>();
        this.packagePath = "";
    }

    /**
     * Clones this ClassResolver so that independent Class imports can be included
     */
    @Override
    public ClassResolver clone() {
        return new ClassResolver(this);
    }

    /**
     * Adds the package path imports for a Class
     * 
     * @param type to import types of
     */
    public void addClassImports(Class<?> type) {
        if (type == null) {
            return;
        }
        Package pkg = type.getPackage();
        if (pkg != null) {
            this.imports.add(pkg.getName() + ".*");
        }
        this.imports.add(type.getName() + ".*");
        addClassImports(type.getSuperclass());
    }

    /**
     * Adds a package path, making all Classes within visible
     * 
     * @param path to the package to add
     */
    public void setPackage(String path) {
        this.packagePath = path;
        this.manualImports.clear();
        this.imports.clear();
        this.imports.addAll(default_imports);
        this.imports.add(path + ".*");
    }

    /**
     * Gets the package path last set using {@link #setPackage(String)}.
     * Is empty if no package path was set
     * 
     * @return package path
     */
    public String getPackage() {
        return this.packagePath;
    }

    /**
     * Gets the list of imports added to this resolver using {@link #addImport(String)}
     * 
     * @return List of imports
     */
    public List<String> getImports() {
        return Collections.unmodifiableList(this.manualImports);
    }

    /**
     * Adds an import declaration. This method supports wildcard imports.
     * 
     * @param path to import
     */
    public void addImport(String path) {
        this.imports.add(path);
        this.manualImports.add(path);
    }

    /**
     * Resolves a class name to a class.
     * 
     * @param name of the class (generic names not supported)
     * @return resolved class, or null if not found
     */
    public Class<?> resolveClass(String name) {
        // Return Object for generic typings (T, K, etc.)
        if (name.length() == 1) {
            return Object.class;
        }

        Class<?> fieldType = CommonUtil.getClass(name);

        String dotName = "." + name;
        if (fieldType == null) {
            for (String imp : this.imports) {
                if (imp.endsWith(".*")) {
                    fieldType = CommonUtil.getClass(imp.substring(0, imp.length() - 1) + name);
                } else if (imp.endsWith(dotName)) {
                    fieldType = CommonUtil.getClass(imp);
                } else {
                    continue;
                }
                if (fieldType != null) {
                    break;
                }
            }
        }
        return fieldType;
    }

    /**
     * Resolves the name of a Class type when resolved by this resolver
     * 
     * @param type to resolve
     * @return class name
     */
    public String resolveName(Class<?> type) {
        // Null types shouldn't happen, but security and all
        if (type == null) {
            return "NULL";
        }

        // Handle arrays elegantly
        if (type.isArray()) {
            return resolveName(type.getComponentType()) + "[]";
        }

        // See if the class type was imported
        String name = type.getName();
        for (String imp : this.imports) {
            if (imp.equals(name)) {
                return type.getSimpleName();
            }
            if (imp.endsWith(".*")) {
                String imp_p = imp.substring(0, imp.length() - 1);
                if (name.startsWith(imp_p)) {
                    return name.substring(imp_p.length());
                }
            }
        }
        return name;
    }
}
