package com.bergerkiller.bukkit.common.internal.cdn;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.server.CraftBukkitServer;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.resolver.ClassPathResolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.google.common.collect.BiMap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Combines the output of {@link MojangMappings} and {@link SpigotMappings}
 * to translate fields and methods from the mojang-mapped names to the
 * at-runtime obfuscated names. For this the server environment must be
 * loaded in, since all names are resolved to Class objects internally.
 */
public class MojangSpigotRemapper {
    private static final ClassRemapper[] NO_REMAPPERS = new ClassRemapper[0];
    private final Map<Class<?>, ClassRemapper> remappersByDeclaringClassName = new IdentityHashMap<>();
    private final Map<Class<?>, ClassRemapper[]> recurseRemappersByDeclaringClassName = new IdentityHashMap<>();

    /**
     * Checks what the true obfuscated field name is, provided a mojang-mapped field name.
     * If the field is defined in a superclass of the specified declaring class, then the
     * field will still be found and remapped. That is, this method works recursively.
     *
     * @param declaringClass Class where the field is declared
     * @param fieldName Mojang-mapped field name
     * @param defaultName Default field name to return if not remapped
     * @return Remapped field name, or the input defaultName if not found
     */
    public String remapFieldName(Class<?> declaringClass, String fieldName, String defaultName) {
        // Ask all remappers for this field. The declaring class is asked first, of course.
        for (ClassRemapper remapper : remappersFor(declaringClass)) {
            String remapped = remapper.fields_name_to_obfuscated.get(fieldName);
            if (remapped != null) {
                return remapped;
            }
        }

        // Not found
        return defaultName;
    }

    /**
     * Checks what the de-obfuscated Mojang field name is, provided an obfuscated field name.
     * If the field is defined in a superclass of the specified declaring class, then the
     * field will still be found and remapped. That is, this method works recursively.
     *
     * @param declaringClass Class where the field is declared
     * @param fieldName Obfuscated field name
     * @param defaultName Default field name to return if not remapped
     * @return Mojang reverse-remapped field name, or the input defaultName if not found
     */
    public String remapFieldNameReverse(Class<?> declaringClass, String fieldName, String defaultName) {
        // Ask all remappers for this field. The declaring class is asked first, of course.
        for (ClassRemapper remapper : remappersFor(declaringClass)) {
            String remapped = remapper.fields_obfuscated_to_name.get(fieldName);
            if (remapped != null) {
                return remapped;
            }
        }

        // Not found
        return defaultName;
    }

    /**
     * Checks what the true obfuscated method name is, provided a mojang-mapped method
     * name and valid arguments. If the method is defined in an interface or superclass
     * of the specified declaring class, then the method will still be found and remapped.
     * That is, this method works recursively.
     *
     * @param declaringClass Class where the method is declared
     * @param methodName Mojang-mapped method name
     * @param parameterTypes Parameter argument types for the method
     * @param defaultName Default method name to return if not remapped
     * @return Remapped method name, or the input defaultName if not found
     */
    public String remapMethodName(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes, String defaultName) {
        // Ask all remappers for this method. The declaring class is asked first, of course.
        for (ClassRemapper remapper : remappersFor(declaringClass)) {
            for (MethodDetails method : remapper.methods_by_name.get(methodName)) {
                if (method.canAcceptParameters(parameterTypes)) {
                    return method.name_obfuscated;
                }
            }
        }

        // Not found
        return defaultName;
    }

    /**
     * Checks what the de-obfuscated Mojang method name is, provided an obfuscated method name.
     * If the method is defined in a superclass of the specified declaring class, then the
     * field will still be found and remapped. That is, this method works recursively.
     *
     * @param declaringClass Class where the method is declared
     * @param methodName Obfuscated method name
     * @param parameterTypes Parameter argument types for the method
     * @param defaultName Default method name to return if not remapped
     * @return Mojang reverse-remapped method name, or the input defaultName if not found
     */
    public String remapMethodNameReverse(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes, String defaultName) {
        // Ask all remappers for this method. The declaring class is asked first, of course.
        for (ClassRemapper remapper : remappersFor(declaringClass)) {
            for (MethodDetails method : remapper.methods_by_obfuscated.get(methodName)) {
                if (method.canAcceptParameters(parameterTypes)) {
                    return method.name;
                }
            }
        }

        // Not found
        return defaultName;
    }

    private ClassRemapper[] remappersFor(Class<?> declaringClass) {
        return recurseRemappersByDeclaringClassName.getOrDefault(declaringClass, NO_REMAPPERS);
    }

    /**
     * Loads the mappings directly. Mappings can be loaded/generated manually.
     * Previous mapping data is wiped.
     *
     * @param mojangMappings Mojang<>Obfuscated mapping data
     * @param spigotMappings Spigot<>Mojang class name mapping data
     * @param classPathResolver Resolver for the 'true' class path of class names
     * @param minecraftVersion Minecraft version for which to remap
     */
    protected void loadMappings(
            final MojangMappings mojangMappings,
            final SpigotMappings spigotMappings,
            final ClassPathResolver classPathResolver,
            final String minecraftVersion
    ) {
        // Reset
        remappersByDeclaringClassName.clear();
        recurseRemappersByDeclaringClassName.clear();

        // Generate mappings of all methods/fields/classes, using spigot's class naming structure
        MojangMappings mappings;
        {
            // Load the spigot<>mojang class mappings and verify they exist
            SpigotMappings.ClassMappings spigotClassMappings = spigotMappings.byVersion.get(minecraftVersion);
            if (spigotClassMappings == null) {
                throw new IllegalArgumentException("Spigot class name mappings not available for Minecraft " + minecraftVersion);
            }

            // Translate Mojang's mappings to Spigot's class mappings
            mappings = mojangMappings.translateClassNames(spigotClassMappings::toSpigot);
        }

        // Start by resolving all classes we have remappings for - essential for later
        RemappedClassResolver resolver = new RemappedClassResolver(classPathResolver);
        for (MojangMappings.ClassMappings classMappings : mappings.classes()) {
            // Find the class, then store if found
            Class<?> resolvedClass = resolver.tryFindClass(classMappings.name);
            if (resolvedClass != null) {
                resolver.store(classMappings.name, resolvedClass);
            }
        }

        // All that taken care of, actually parse the full mojang mappings and translate using resolver
        // After this the resolver is no longer used and only still-valid remappings are kept
        for (MojangMappings.ClassMappings classMappings : mappings.classes()) {
            ClassRemapper remapper = ClassRemapper.create(classMappings, resolver);
            if (remapper != null) {
                remappersByDeclaringClassName.put(remapper.type, remapper);
            }
        }

        // Final step is to handle recursion. When requesting a (public!) method or field from a class,
        // we want to make sure this also works when calling the same method or field on an implementation
        // of that same class. For example, methods on Entity should also be callable on EntityLiving.
        // We only store the base classes/interfaces that have mapping data.
        for (ClassRemapper remapper : remappersByDeclaringClassName.values()) {
            recurseRemappersByDeclaringClassName.put(remapper.type,
                    remapper.recurse(remappersByDeclaringClassName));
        }
    }

    /**
     * Loads the mojang and spigot class name remapping details and creates a new MojangSpigotRemapper
     * using them. If mojang's mappings are not yet available in cache they are downloaded. If the
     * spigot mappings are not available in this library's jar an attempt to download them is made.
     *
     * @param minecraftVersion Minecraft version for which to generate the mappings
     * @param classPathResolver If additional remapping is required after translating to the Spigot
     *                          Class names, this classPathResolver should be used
     * @return Mojang-spigot remapper object
     */
    public static MojangSpigotRemapper load(String minecraftVersion, ClassPathResolver classPathResolver) {
        // We require mojang's mappings
        MojangMappings mojangMappings = MojangMappings.fromCacheOrDownload(minecraftVersion);

        // Retrieve Spigot-Mojang class name mappings
        // We need this to properly remap the mojang field and method names later
        SpigotMappings spigotMappings = new SpigotMappings();
        String classMappingsFile = "/com/bergerkiller/bukkit/common/internal/resources/class_mappings.dat";
        try {
            try (InputStream in = CraftBukkitServer.class.getResourceAsStream(classMappingsFile)) {
                spigotMappings.read(in);
            }
        } catch (IOException ex) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to read class mappings (corrupted jar?)", ex);
        }

        // Read the required mappings, or downloads it if missing for some weird reason
        if (!spigotMappings.byVersion.containsKey(minecraftVersion)) {
            Logging.LOGGER.log(Level.WARNING, "[Developer] Class mappings file has no mappings for this Minecraft version. Build problem?");
            try {
                spigotMappings.downloadMappings(mojangMappings, minecraftVersion);
            } catch (IOException ex) {
                throw new IllegalStateException("Failed to download Spigot-Mojang class name mappings");
            }
        }

        // Create remapper object
        MojangSpigotRemapper remapper = new MojangSpigotRemapper();
        remapper.loadMappings(mojangMappings, spigotMappings, classPathResolver, minecraftVersion);
        return remapper;
    }

    private static class ClassRemapper {
        public final Class<?> type;
        public final BiMap<String, String> fields_obfuscated_to_name;
        public final BiMap<String, String> fields_name_to_obfuscated;
        public final ListMultimap<String, MethodDetails> methods_by_name = LinkedListMultimap.create(64);
        public final ListMultimap<String, MethodDetails> methods_by_obfuscated = LinkedListMultimap.create(64);

        private ClassRemapper(MojangMappings.ClassMappings mappings, Class<?> type) {
            this.type = type;
            this.fields_name_to_obfuscated = mappings.fields_name_to_obfuscated;
            this.fields_obfuscated_to_name = mappings.fields_obfuscated_to_name;
        }

        public ClassRemapper[] recurse(Map<Class<?>, ClassRemapper> allRemappers) {
            return ReflectionUtil.getAllClassesAndInterfaces(this.type)
                    .map(allRemappers::get)
                    .filter(Objects::nonNull)
                    .toArray(ClassRemapper[]::new);
        }

        public static ClassRemapper create(MojangMappings.ClassMappings mappings, RemappedClassResolver resolver) {
            Class<?> type = resolver.resolve(mappings.name);
            if (type != null) {
                ClassRemapper remapper = new ClassRemapper(mappings, type);
                for (MojangMappings.MethodSignature sig : mappings.methods) {
                    MethodDetails details = MethodDetails.create(sig, resolver);
                    if (details != null) {
                        remapper.methods_by_name.put(details.name, details);
                        remapper.methods_by_obfuscated.put(details.name_obfuscated, details);
                    }
                }
                return remapper;
            }
            return null;
        }
    }

    private static class MethodDetails {
        private static final Class<?>[] NO_ARGS = new Class<?>[0];
        public final String name;
        public final String name_obfuscated;
        public final Class<?> returnType;
        public final Class<?>[] parameterTypes;

        public MethodDetails(MojangMappings.MethodSignature sig, Class<?> returnType, Class<?>[] parameterTypes) {
            this.name = sig.name;
            this.name_obfuscated = sig.name_obfuscated;
            this.returnType = returnType;
            this.parameterTypes = parameterTypes;
        }

        public boolean canAcceptParameters(Class<?>[] parameterTypes) {
            int count = parameterTypes.length;
            if (this.parameterTypes.length != count) {
                return false;
            }
            for (int i = 0; i < count; i++) {
                if (!this.parameterTypes[i].isAssignableFrom(parameterTypes[i])) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append(returnType.getName()).append(' ');
            str.append(name).append(':').append(name_obfuscated);
            str.append("(");
            boolean first = true;
            for (Class<?> type : parameterTypes) {
                if (first) {
                    first = false;
                } else {
                    str.append(", ");
                }
                str.append(type.getName());
            }
            str.append(')');
            return str.toString();
        }

        public static MethodDetails create(MojangMappings.MethodSignature sig, RemappedClassResolver resolver) {
            Class<?> returnType = resolver.resolve(sig.returnType.name);
            if (returnType == null) {
                return null; // Fail!
            }

            if (sig.parameterTypes.isEmpty()) {
                return new MethodDetails(sig, returnType, NO_ARGS);
            }

            Class<?>[] arguments = new Class<?>[sig.parameterTypes.size()];
            for (int i = 0 ; i < arguments.length; i++) {
                Class<?> argType = resolver.resolve(sig.parameterTypes.get(i).name);
                if (argType == null) {
                    return null; // Fail!
                }
                arguments[i] = argType;
            }

            return new MethodDetails(sig, returnType, arguments);
        }
    }

    private static class RemappedClassResolver {
        private final ClassPathResolver classPathResolver;
        private final Map<String, Class<?>> cache = new HashMap<>();

        public RemappedClassResolver(ClassPathResolver classPathResolver) {
            this.classPathResolver = classPathResolver;

            // Store primitive types and boxed types up-front
            // The ClassLoader can't seem to actually find such types
            for (Class<?> type : BoxedType.getUnboxedTypes()) {
                store(type.getSimpleName(), type);
            }
            for (Class<?> type : BoxedType.getBoxedTypes()) {
                store(type.getName(), type);
            }
        }

        public void store(String mojangName, Class<?> resolvedClass) {
            cache.put(mojangName, resolvedClass);
        }

        public Class<?> tryFindClass(String name) {
            // Before attempting to find the class, consult the ClassPathResolver what the true name is
            // If a remapper is used (forge!) then the actual class name might be entirely different,
            // even after remapping to the correct 'spigot' names.
            String remappedName = this.classPathResolver.resolveClassPath(name);

            try {
                return MPLType.getClassByName(name, false, this.getClass().getClassLoader());
            } catch (ClassNotFoundException e) {
                // Debugging - this will actually happen a lot because the server mappings include a lot
                // of classes used by Mojang for development, like test tools or other types.
                // if (name.equals(remappedName)) {
                //     Logging.LOGGER.log(Level.WARNING, "Failed to find server class " + name);
                // } else {
                //     Logging.LOGGER.log(Level.WARNING, "Failed to find server class " +
                //             name + " (" + remappedName + ")");
                // }
                return null;
            } catch (Throwable t) {
                if (name.equals(remappedName)) {
                    Logging.LOGGER.log(Level.SEVERE, "An error occurred loading server class " + name, t);
                } else {
                    Logging.LOGGER.log(Level.SEVERE, "An error occurred loading server class " +
                            name + " (" + remappedName + ")", t);
                }
                return null;
            }
        }

        /**
         * Resolves a mojang class name to the at-runtime found Class type.
         * If not found, returns null to indicate the mappings are no longer
         * valid.
         *
         * @param mojangName
         * @return Found Class, or null if not found
         */
        public Class<?> resolve(String mojangName) {
            return cache.computeIfAbsent(mojangName, this::resolveNewName);
        }

        private Class<?> resolveNewName(String mojangName) {
            int numArrayDims = 0;
            while (mojangName.endsWith("[]")) {
                mojangName = mojangName.substring(0, mojangName.length() - 2);
                numArrayDims++;
            }

            // Retry cache once if this is an array
            if (numArrayDims > 0) {
                Class<?> inCache = cache.get(mojangName);
                if (inCache != null) {
                    return LogicUtil.getArrayType(inCache, numArrayDims);
                }
            }

            // Try to see if the type exists - most common case
            Class<?> baseType = tryFindClass(mojangName);
            if (baseType != null && numArrayDims > 0) {
                baseType = LogicUtil.getArrayType(baseType, numArrayDims);
            }
            return baseType;
        }
    }
}
