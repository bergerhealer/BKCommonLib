package com.bergerkiller.reflection.declarations;

import java.util.HashSet;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Resolves class names into Class Types based on package and import paths.
 */
public class ClassResolver {
    private final HashSet<String> imports;

    public ClassResolver() {
        this.imports = new HashSet<String>();
        this.imports.add("java.lang.*");
        this.imports.add("java.util.*");
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
     * Adds an import declaration. This method supports wildcard imports.
     * 
     * @param path to import
     */
    public void addImport(String path) {
        this.imports.add(path);
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
