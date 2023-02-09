package com.bergerkiller.bukkit.common.internal.cdn;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.Logging;
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
    private Map<Class<?>, ClassRemapper[]> recurseRemappersByDeclaringClassName = new IdentityHashMap<>(); // Cloned on modify
    private MojangMappings mappings = null;

    /**
     * While initializing remappers, stores a by-name cache lookup of all classes used in Minecraft Server
     */
    private final LazyClassLookup classLookup = new LazyClassLookup();

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

    /**
     * Removes a remapping rule for a method from this remapper
     *
     * @param declaringClass Class that declares the method
     * @param methodName Public-facing (de-obfuscated) method name
     * @param parameterTypes Parameter types of the method
     */
    public void removeMethodMapping(Class<?> declaringClass, String methodName, Class<?>... parameterTypes) {
        for (ClassRemapper remapper : remappersFor(declaringClass)) {
            remapper.removeMethodMapping(methodName, parameterTypes);
        }
    }

    private ClassRemapper[] remappersFor(Class<?> declaringClass) {
        // Check already cached, if so, return that
        {
            ClassRemapper[] remappers = recurseRemappersByDeclaringClassName.get(declaringClass);
            if (remappers != null) {
                return remappers;
            }
        }

        // Prepare a Remapper. If no remapping is done, return no remappers
        ClassRemapper remapper = initRemapper(declaringClass);
        if (remapper == null) {
            return NO_REMAPPERS;
        }

        // Store it in the recurse map too! We might be overwriting prior results, but that doesn't hurt.
        ClassRemapper[] result = remapper.recurse(this::initRemapper);

        synchronized (this) {
            Map<Class<?>, ClassRemapper[]> newRecurseMap = new IdentityHashMap<>(recurseRemappersByDeclaringClassName);
            newRecurseMap.put(declaringClass, result);
            recurseRemappersByDeclaringClassName = newRecurseMap;
        }

        return result;
    }

    private ClassRemapper initRemapper(Class<?> declaringClass) {
        // Check mappings for this class exist at all
        // Check this first because this code is hit a lot while working with non-Minecraft types!
        final MojangMappings.ClassMappings mappings = this.mappings.forClassIfExists(declaringClass.getName());
        if (mappings == null) {
            return null;
        }

        // Generate new (recurse) remapper for this declaring class
        // It might be another thread did the same, in which case we are wasting our time
        // But that's okay!
        // This must be done this way, because during initialization it can load new classes.
        // Having this in a synchronized block risks a deadlock.

        ClassRemapper remapper;
        synchronized (remappersByDeclaringClassName) {
            remapper = remappersByDeclaringClassName.get(declaringClass);
        }
        if (remapper == null) {
            remapper = ClassRemapper.create(mappings, classLookup);
            synchronized (remappersByDeclaringClassName) {
                ClassRemapper prev = remappersByDeclaringClassName.putIfAbsent(declaringClass, remapper);
                if (prev != null) {
                    remapper = prev; // Another thread did the work for us. Use that.
                }
            }
        }

        return remapper;
    }

    /**
     * Loads the mappings directly. Mappings can be loaded/generated manually.
     * Previous mapping data is wiped.
     *
     * @param mappings Mojang&lt;&gt;Obfuscated mapping data
     */
    protected synchronized void loadMappings(
            final MojangMappings mappings
    ) {
        // Reset
        remappersByDeclaringClassName.clear();
        recurseRemappersByDeclaringClassName = new IdentityHashMap<>();
        classLookup.reset();

        // Fill the class lookup with all classes used in the mappings
        // This part here defines exactly what types can be loaded while resolving remappers,
        // no other types can ever be loaded.
        // Note that usage of this cache is synchronized!
        for (MojangMappings.ClassMappings classMappings : mappings.classes()) {
            classLookup.add(classMappings.name);
            for (MojangMappings.MethodSignature method : classMappings.methods) {
                classLookup.add(method.returnType.name);
                for (MojangMappings.ClassSignature sig : method.parameterTypes) {
                    classLookup.add(sig.name);
                }
            }
        }

        // Setup the mojang mappings for later!
        // ClassRemapper instances will be created lazily using this information
        this.mappings = mappings;
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
        // Generate mappings of all methods/fields/classes, using spigot's class naming structure
        MojangMappings mappings;
        {
            // We require mojang's mappings
            MojangMappings mojangMappings = MojangMappings.fromCacheOrDownload(minecraftVersion);

            // We require spigot<>mojang class translation
            SpigotMappings.ClassMappings spigotMappings = SpigotMappings.fromCacheOrDownload(minecraftVersion);

            // Final translation using the two, also translate using the class path resolver
            mappings = mojangMappings.translateClassNames(name -> {
                String spigotName = spigotMappings.toSpigot(name);
                String remappedName = classPathResolver.resolveClassPath(spigotName);
                return remappedName;
            });
        }

        // Create remapper object
        MojangSpigotRemapper remapper = new MojangSpigotRemapper();
        remapper.loadMappings(mappings);
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

        public ClassRemapper[] recurse(Function<Class<?>, ClassRemapper> remapperLookup) {
            return ReflectionUtil.getAllClassesAndInterfaces(this.type)
                    .map(remapperLookup)
                    .filter(Objects::nonNull)
                    .toArray(ClassRemapper[]::new);
        }

        public static ClassRemapper create(MojangMappings.ClassMappings mappings, LazyClassLookup classLookup) {
            Class<?> type = classLookup.get(mappings.name);
            if (type != null) {
                ClassRemapper remapper = new ClassRemapper(mappings, type);
                for (MojangMappings.MethodSignature sig : mappings.methods) {
                    MethodDetails details = MethodDetails.create(sig, classLookup);
                    if (details != null) {
                        remapper.methods_by_name.put(details.name, details);
                        remapper.methods_by_obfuscated.put(details.name_obfuscated, details);
                    }
                }
                return remapper;
            }
            return null;
        }

        public void removeMethodMapping(String methodName, Class<?>[] parameterTypes) {
            Iterator<MethodDetails> iter = methods_by_name.get(methodName).iterator();
            while (iter.hasNext()) {
                MethodDetails details = iter.next();
                if (details.canAcceptParameters(parameterTypes)) {
                    iter.remove();
                    methods_by_obfuscated.remove(details.name_obfuscated, details);
                }
            }
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

        public static MethodDetails create(MojangMappings.MethodSignature sig, LazyClassLookup classLookup) {
            Class<?> returnType = classLookup.get(sig.returnType.name);
            if (returnType == null) {
                return null; // Fail!
            }

            if (sig.parameterTypes.isEmpty()) {
                return new MethodDetails(sig, returnType, NO_ARGS);
            }

            Class<?>[] arguments = new Class<?>[sig.parameterTypes.size()];
            for (int i = 0 ; i < arguments.length; i++) {
                Class<?> argType = classLookup.get(sig.parameterTypes.get(i).name);
                if (argType == null) {
                    return null; // Fail!
                }
                arguments[i] = argType;
            }

            return new MethodDetails(sig, returnType, arguments);
        }
    }

    /**
     * Caches the loaded Class by the in-mapping represented Class names.
     * As these are all internal server types, this poses no memory leak
     * danger. It also can only get classes that were originally represented
     * in the mapping details.
     */
    private static final class LazyClassLookup {
        private final Map<String, Supplier<Class<?>>> map = new ConcurrentHashMap<>();

        public void reset() {
            map.clear();

            // Store primitive types and boxed types up-front
            // The ClassLoader can't seem to actually find such types
            for (Class<?> type : BoxedType.getUnboxedTypes()) {
                map.put(type.getSimpleName(), LogicUtil.constantSupplier(type));

                if (type != void.class) {
                    // Can't make void arrays!
                    // Create all the way down to 10 dimensions - should be plenty!
                    String nameTmp = type.getSimpleName();
                    Class<?> typeTmp = type;
                    for (int dims = 0; dims < 10; dims++) {
                        nameTmp += "[]";
                        typeTmp = LogicUtil.getArrayType(typeTmp);
                        map.put(nameTmp, LogicUtil.constantSupplier(typeTmp));
                    }
                }
            }
            for (Class<?> type : BoxedType.getBoxedTypes()) {
                map.put(type.getName(), LogicUtil.constantSupplier(type));
            }

            // Why not, they're bound to show up :shrug:
            map.put(String.class.getName(), LogicUtil.constantSupplier(String.class));
            map.put(List.class.getName(), LogicUtil.constantSupplier(List.class));
            map.put(Map.class.getName(), LogicUtil.constantSupplier(Map.class));
        }

        public Class<?> get(String name) {
            return map.getOrDefault(name, LogicUtil.nullSupplier()).get();
        }

        public void add(String name) {
            map.computeIfAbsent(name, this::createNewLookup);
        }

        private GeneratingLookup createNewLookup(String name) {
            if (name.endsWith("[]")) {
                int numDims = 0;
                String tmp = name;
                do {
                    tmp = tmp.substring(0, tmp.length() - 2);
                    numDims++;
                } while (tmp.endsWith("[]"));
                return new GeneratingArrayTypeLookup(map, name, numDims);
            } else {
                return new GeneratingLookup(map, name);
            }
        }

        private static final class GeneratingArrayTypeLookup extends GeneratingLookup {
            private final int numDims;

            public GeneratingArrayTypeLookup(Map<String, Supplier<Class<?>>> map, String name, int numDims) {
                super(map, name);
                this.numDims = numDims;
            }

            @Override
            public Class<?> load(String className) {
                // Remove dimension part of String
                // Then re-add the dimensions to the class
                className = className.substring(0, className.length() - numDims * 2);
                Class<?> foundClass = super.load(className);
                if (foundClass != null) {
                    foundClass = LogicUtil.getArrayType(foundClass, numDims);
                }
                return foundClass;
            }
        }

        private static class GeneratingLookup implements Supplier<Class<?>> {
            private final Map<String, Supplier<Class<?>>> map;
            private final String name;

            public GeneratingLookup(Map<String, Supplier<Class<?>>> map, String name) {
                this.map = map;
                this.name = name;
            }

            public Class<?> load(String className) {
                try {
                    return MPLType.getClassByName(className, false, this.getClass().getClassLoader());
                } catch (ClassNotFoundException e) {
                    // Debugging - this will actually happen a lot because the server mappings include a lot
                    // of classes used by Mojang for development, like test tools or other types.
                    // Logging.LOGGER.log(Level.WARNING, "Failed to find server class " + name);
                    return null;
                } catch (Throwable t) {
                    Logging.LOGGER.log(Level.SEVERE, "An error occurred loading server class " + className, t);
                    return null;
                }
            }

            @Override
            public final Class<?> get() {
                Class<?> foundClass = this.load(name);
                if (foundClass != null) {
                    // Store this as a constant in the map instead, discarding this entry
                    map.put(name, LogicUtil.constantSupplier(foundClass));
                } else {
                    // Remove from map so it can't be found next time
                    map.remove(name);
                }
                return foundClass;
            }
        }
    }
}
