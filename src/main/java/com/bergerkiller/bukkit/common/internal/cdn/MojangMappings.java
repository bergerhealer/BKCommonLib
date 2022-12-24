package com.bergerkiller.bukkit.common.internal.cdn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * API around the published mappings for minecraft server.
 */
public class MojangMappings {
    private final Map<String, ClassMappings> classesByName = new HashMap<>();

    /**
     * Gets a Collection of all classes there are mappings for
     *
     * @return class mappings
     */
    public Collection<ClassMappings> classes() {
        return classesByName.values();
    }

    /**
     * Gets class mapping information for a class name, if mappings exist
     * for that class
     *
     * @param name Class name
     * @return Class mappings for this class, or null if absent
     */
    public ClassMappings forClassIfExists(String name) {
        return classesByName.get(name);
    }

    /**
     * Translates all the Mojang class names to alternative class names, for all classes
     * and method definitions inside these mappings. Beware that all class names are
     * translated. This includes class names that aren't part of mojangs mappings,
     * but are arguments or return types of methods.
     *
     * @param translatorFunction Function that translates to the right class name
     * @return Translated mappings
     */
    public MojangMappings translateClassNames(Function<String, String> translatorFunction) {
        // Start by translating all the class mappings
        MojangMappings translated = new MojangMappings();
        ClassSignatureCache signatureCache = new ClassSignatureCache(translatorFunction);
        for (ClassMappings mappings : this.classesByName.values()) {
            ClassMappings translatedMappings = new ClassMappings(mappings, translatorFunction);
            translated.classesByName.put(translatedMappings.name, translatedMappings);
            signatureCache.put(mappings.name, translatedMappings);
        }

        // Translate the types inside the method signatures next
        for (ClassMappings mappings : translated.classesByName.values()) {
            ListIterator<MethodSignature> iter = mappings.methods.listIterator();
            while (iter.hasNext()) {
                iter.set(iter.next().translate(mappings, signatureCache));
            }
        }

        return translated;
    }

    /**
     * The mappings of a single class
     */
    public static class ClassMappings extends ClassSignature {
        public final BiMap<String, String> fields_obfuscated_to_name;
        public final BiMap<String, String> fields_name_to_obfuscated;
        public final List<MethodSignature> methods;

        public ClassMappings(ClassMappings original, Function<String, String> translatorFunction) {
            super(translatorFunction.apply(original.name), original.name_obfuscated);

            // These don't change
            this.fields_obfuscated_to_name = original.fields_obfuscated_to_name;
            this.fields_name_to_obfuscated = original.fields_name_to_obfuscated;

            // Method must be remapped, but do that later on when all class mappings are initialized
            this.methods = new ArrayList<MethodSignature>(original.methods);
        }

        public ClassMappings(String name, String name_obfuscated) {
            super(name, name_obfuscated);
            this.fields_obfuscated_to_name = HashBiMap.create();
            this.fields_name_to_obfuscated = this.fields_obfuscated_to_name.inverse();
            this.methods = new ArrayList<>();
        }

        public void addField(String obfuscatedName, String mojangName) {
            this.fields_obfuscated_to_name.put(obfuscatedName, mojangName);
        }

        public void addMethod(MethodSignature methodSig) {
            this.methods.add(methodSig);
        }
    }

    /**
     * Class signature, providing both the original de-obfuscated name as well
     * as the obfuscated name. The obfuscated name may be exactly the same as
     * the un-obfuscated name, if the class happens to not be remapped.
     */
    public static class ClassSignature {
        public final String name;
        public final String name_obfuscated;

        public ClassSignature(String name, String name_obfuscated) {
            this.name = name;
            this.name_obfuscated = name_obfuscated;
        }

        @Override
        public String toString() {
            if (this.name.equals(this.name_obfuscated)) {
                return this.name;
            }
            return this.name + ":" + this.name_obfuscated;
        }
    }

    /**
     * Method signature that uniquely identifies the name and arguments of a method
     */
    public static class MethodSignature {
        public final ClassMappings declaring;
        public final String name;
        public final String name_obfuscated;
        public final ClassSignature returnType;
        public final List<ClassSignature> parameterTypes;

        public MethodSignature(ClassMappings declaring, String name, String name_obfuscated,
                ClassSignature returnType, List<ClassSignature> parameterTypes
        ) {
            this.declaring = declaring;
            this.name = name;
            this.name_obfuscated = name_obfuscated;
            this.returnType = returnType;
            this.parameterTypes = parameterTypes;
        }

        public MethodSignature translate(ClassMappings declaring, ClassSignatureCache signatureCache) {
            List<ClassSignature> tParameterTypes = new ArrayList<ClassSignature>(this.parameterTypes.size());
            for (ClassSignature paramType : this.parameterTypes) {
                tParameterTypes.add(signatureCache.get(paramType.name));
            }
            return new MethodSignature(declaring, this.name, this.name_obfuscated,
                    signatureCache.get(this.returnType.name),
                    tParameterTypes);
        }

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append("<").append(declaring.toString()).append("> ");
            str.append(returnType).append(' ');
            str.append(name).append(":").append(name_obfuscated);
            str.append(" (");
            boolean first = true;
            for (ClassSignature param : parameterTypes) {
                if (first) {
                    first = false;
                } else {
                    str.append(", ");
                }
                str.append(param.toString());
            }
            str.append(")");
            return str.toString();
        }
    }

    /**
     * Attempts to load previously downloaded mappings from disk
     *
     * @param minecraftVersion
     * @return
     */
    public static MojangMappings fromCacheOrDownload(String minecraftVersion) {
        File cacheFolder = MojangIO.getCacheFolder();
        File mappingsFile = new File(cacheFolder, getMappingFile(minecraftVersion));
        if (mappingsFile.exists()) {
            try {
                return readMappings(mappingsFile);
            } catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to parse cached server mappings, redownloading", t);
            }
        }
        return download(minecraftVersion);
    }

    /**
     * Attempts to download the mappings from mojangs servers for the current
     * minecraft version. The contents are saved in a location that can be read
     * using {@link #fromCache(String)} in the future.
     *
     * @return mojang mappings
     */
    public static MojangMappings download(String minecraftVersion) throws DownloadException {
        File cacheFolder = MojangIO.getCacheFolder();
        File tempFile = new File(cacheFolder, getMappingFile(minecraftVersion) + ".tmp");
        File mappingsFile = new File(cacheFolder, getMappingFile(minecraftVersion));

        // Just some legal garbage...
        Logging.LOGGER.warning("Since Minecraft 1.17 the server is obfuscated and requires the server mappings to interpret it correctly");
        Logging.LOGGER.warning("BKCommonLib will now download Minecraft " + minecraftVersion + " server mappings from Mojang's servers");
        Logging.LOGGER.warning("The file will be installed in: " + mappingsFile.toString());
        Logging.LOGGER.warning("By downloading these files you further agree with Mojang's EULA.");
        Logging.LOGGER.warning("The EULA can be read here: https://account.mojang.com/documents/minecraft_eula");

        // Try to download, if it fails, give instructions for manual installation
        try {
            return downloadMain(minecraftVersion, tempFile, mappingsFile);
        } catch (DownloadException ex) {
            Logging.LOGGER.severe("Failed to download the server mappings. You can try manually downloading the file instead.");
            Logging.LOGGER.severe("Install the server mappings in the following location:");
            Logging.LOGGER.severe("> " + mappingsFile.getAbsolutePath());
            throw ex;
        }
    }

    private static MojangMappings downloadMain(String minecraftVersion, File tempFile, File mappingsFile) throws DownloadException {
        // Download the version manifest, to find all available versions
        MojangIO.VersionManifest versionManifest;
        try {
            versionManifest = MojangIO.downloadJson(MojangIO.VersionManifest.class, MojangIO.VersionManifest.URL);
        } catch (IOException ex) {
            throw new DownloadException("Failed to download the game version manifest from Mojangs servers", ex);
        }

        // Find version suitable for this server
        MojangIO.VersionManifest.Version currentVersion = versionManifest.findVersion(minecraftVersion);
        if (currentVersion == null) {
            throw new DownloadException("This Minecraft version is not available");
        }

        // Find this version's asset information
        MojangIO.VersionManifest.VersionAssets versionAssets;
        try {
            versionAssets = MojangIO.downloadJson(MojangIO.VersionManifest.VersionAssets.class, currentVersion.url);
        } catch (IOException ex) {
            throw new DownloadException("Failed to download game version asset information", ex);
        }
        MojangIO.VersionManifest.VersionAssets.Download download = versionAssets.downloads.get("server_mappings");
        if (download == null) {
            throw new DownloadException("This Minecraft version has no downloadable server mappings");
        }

        // Download the file, first write it to a temporary file location
        try {
            MojangIO.downloadFile("Server Mappings", download, tempFile);
        } catch (IOException ex) {
            throw new DownloadException("Failed to download server mappings", ex);
        }

        // Success! Move the temporary file to the final destination
        mappingsFile.delete();
        if (!tempFile.renameTo(mappingsFile)) {
            throw new DownloadException("Failed to move " + tempFile + " to " + mappingsFile);
        }

        // Read 'em!
        try {
            return readMappings(mappingsFile);
        } catch (Throwable t) {
            throw new DownloadException("Failed to parse server mappings", t);
        }
    }

    /**
     * Parses the mappings file, making it accessible through a MojangMappings instance.
     *
     * @param mappingsFile The file to read
     * @return Mojang mappings instance
     * @throws IOException - If reading the file failed
     */
    private static MojangMappings readMappings(File mappingsFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(mappingsFile))) {
            return (new ProGuardParser()).parse(br);
        }
    }

    /**
     * Exception thrown when the mojang mappings could not be downloaded
     */
    public static class DownloadException extends RuntimeException {
        private static final long serialVersionUID = 6481322561950832096L;

        public DownloadException(String reason) {
            super(reason);
        }

        public DownloadException(String reason, Throwable cause) {
            super(reason, cause);
        }
    }

    private static String getMappingFile(String minecraftVersion) {
        return minecraftVersion + "_server_mappings.txt";
    }

    /**
     * Stores ClassSignature by class name in a cache to avoid creating too many of them
     */
    private static class ClassSignatureCache {
        private final Map<String, ClassSignature> cache = new ConcurrentHashMap<>();
        private final Function<String, String> nameTranslator;

        public ClassSignatureCache() {
            this.nameTranslator = Function.identity();
        }

        public ClassSignatureCache(Function<String, String> nameTranslator) {
            this.nameTranslator = nameTranslator;
        }

        /**
         * Puts a value in the cache so it is not generated lazily. No translation
         * is appleid.
         *
         * @param name Name (should be before translation)
         * @param sig Signature
         */
        public void put(String name, ClassSignature sig) {
            cache.put(name, sig);
        }

        /**
         * Gets the class signature with a provided name.
         * The name is translated
         *
         * @param name Name (should be before translation)
         * @return Signature
         */
        public ClassSignature get(String name) {
            // Note: cannot use computeIfAbsent because compute() also puts potentially
            ClassSignature sig = cache.get(name);
            if (sig == null) {
                sig = this.compute(name);
                cache.put(name, sig);
            }
            return sig;
        }

        private ClassSignature compute(String name) {
            // Eliminate array declarations from name
            boolean changed = false;
            String classNameBare = name;
            String postfix = "";
            while (classNameBare.endsWith("[]")) {
                classNameBare = classNameBare.substring(0, classNameBare.length() - 2);
                postfix += "[]";
                changed = true;
            }

            // If changed, check cache a second time, otherwise, generate
            if (changed) {
                ClassSignature sig = get(classNameBare);
                return new ClassSignature(sig.name + postfix, sig.name_obfuscated + postfix);
            } else {
                String translated = this.nameTranslator.apply(classNameBare);
                return new ClassSignature(translated, translated);
            }
        }
    }

    /**
     * Parses the proguard-formatted mappings file into a MojangMappings instance
     */
    private static class ProGuardParser {
        // Regex: \s+[\[\]<>\w\._\-$]+\s([\w_\-$]+)\s->\s([\w_\-$]+)
        private static final Pattern FIELD_NAME_PATTERN = Pattern.compile("\\s+[\\[\\]<>\\w\\._\\-$]+\\s([\\w_\\-$]+)\\s->\\s([\\w_\\-$]+)");
        // Regex: \s+(\d+:\d+:)?[\[\]<>\w\._\-$]+\s([\w_\-$]+)\(([\[\]<>\w\._\-,$]*)\)\s->\s([\w_\-$]+)
        private static final Pattern METHOD_NAME_PATTERN = Pattern.compile("\\s+(\\d+:\\d+:)?([\\[\\]<>\\w\\._\\-$]+)\\s([\\w_\\-$]+)\\(([\\[\\]<>\\w\\._\\-,$]*)\\)\\s->\\s([\\w_\\-$]+)");

        private final MojangMappings result = new MojangMappings();
        private final ClassSignatureCache classSigCache = new ClassSignatureCache();

        public MojangMappings parse(java.io.BufferedReader reader) throws IOException {
            AsyncLineReader lineReader = new AsyncLineReader(reader);
            lineReader.start();

            ClassMappings currClassMappings = null;
            List<PendingMethod> pendingMethods = new ArrayList<>();
            for (String line; (line = lineReader.readLine()) != null; ) {
                // Skip comments
                if (line.startsWith("#")) {
                    continue;
                }

                // Start of new classes
                if (line.endsWith(":") && !line.startsWith(" ")) {
                    int nameEnd = line.indexOf(" -> ");
                    if (nameEnd <= 0 || nameEnd >= (line.length()-4)) {
                        currClassMappings = null;
                    } else {
                        currClassMappings = new ClassMappings(line.substring(0, nameEnd),
                                                          line.substring(nameEnd+4, line.length()-1));
                        classSigCache.put(currClassMappings.name, currClassMappings);

                        // Don't add these - that's weird
                        if (isValidClass(currClassMappings.name)) {
                            result.classesByName.put(currClassMappings.name, currClassMappings);
                        }
                    }
                }
                if (currClassMappings == null || !line.startsWith("    ")) {
                    continue; // Weird? Oh well.
                }

                // Parse members of class
                {
                    Matcher m = FIELD_NAME_PATTERN.matcher(line);
                    if (m.matches()) {
                        processField(currClassMappings, m);
                        continue;
                    }
                }
                {
                    Matcher m = METHOD_NAME_PATTERN.matcher(line);
                    if (m.matches()) {
                        pendingMethods.add(new PendingMethod(currClassMappings, m));
                        continue;
                    }
                }

                // Debug missing matches (we don't care about [static] constructors)
                // if (!line.contains("<init>") && !line.contains("<clinit>")) {
                //     System.out.println("Missed: " + line);
                // }
            }

            // After having parsed the entire file and knowing what class name remappings exist,
            // process all the methods. The argument type remapping logic depends on this.
            pendingMethods.stream()
                .parallel()
                .map(m -> m.makeSignature(classSigCache))
                .forEachOrdered(s -> s.declaring.addMethod(s));

            return result;
        }

        private static boolean isValidClass(String className) {
            return !className.endsWith(".package-info");
        }

        private void processField(ClassMappings classMappings, Matcher m) {
            String obfuscatedName = m.group(2);
            String mojangName = m.group(1);

            // These types of obfuscation were not done in the server, and the
            // original variable name is kept.
            if (mojangName.startsWith("this$")) {
                return;
            }

            classMappings.addField(obfuscatedName, mojangName);
        }

        private static final class PendingMethod {
            public final ClassMappings classMappings;
            public final String obfuscatedName;
            public final String mojangName;
            public final String returnTypeName;
            public final String[] paramTypeNames;

            public PendingMethod(ClassMappings classMappings, Matcher m) {
                this.classMappings = classMappings;
                this.obfuscatedName = m.group(5);
                this.mojangName = m.group(3);
                this.returnTypeName = m.group(2);

                String fullArgsStr = m.group(4);
                if (fullArgsStr.isEmpty()) {
                    this.paramTypeNames = new String[0];
                } else {
                    this.paramTypeNames = fullArgsStr.split(",", -1);
                }
            }

            public MethodSignature makeSignature(ClassSignatureCache signatureCache) {
                // Translate the return type if it refers to an obfuscated name
                ClassSignature returnType = signatureCache.get(returnTypeName);

                // Translate the argument types if they refer to obfuscated names
                List<ClassSignature> params = new ArrayList<ClassSignature>(paramTypeNames.length);
                for (String paramTypeName : paramTypeNames) {
                    params.add(signatureCache.get(paramTypeName));
                }

                return new MethodSignature(classMappings, mojangName, obfuscatedName, returnType, params);
            }
        }

        private static final class AsyncLineReader {
            private static final String STOP_SIGNAL = "MAPPINGS_DONE_READING";
            private final java.io.BufferedReader reader;
            private CompletableFuture<Void> readerFuture;
            private final BlockingQueue<String> pending = new LinkedBlockingDeque<>();

            public AsyncLineReader(java.io.BufferedReader reader) {
                this.reader = reader;
            }

            public String readLine() throws IOException {
                try {
                    String line = pending.poll(1000, TimeUnit.MILLISECONDS);
                    if (line == STOP_SIGNAL) {
                        try {
                            readerFuture.join();
                        } catch (CompletionException ex) {
                            if (ex.getCause() instanceof IOException) {
                                throw (IOException) ex.getCause();
                            } else {
                                throw ex; // meh
                            }
                        }
                        return null;
                    } else {
                        return line;
                    }
                } catch (InterruptedException e) {
                    throw new IllegalStateException("Read interrupted");
                }
            }

            public void start() {
                readerFuture = CommonPlugin.runIOTaskAsync(() -> {
                    BlockingQueue<String> pending = this.pending;
                    try {
                        for (String line; (line = reader.readLine()) != null; ) {
                            pending.offer(line, 1000, TimeUnit.MILLISECONDS);
                        }
                    } finally {
                        pending.offer(STOP_SIGNAL); // Ends it
                    }
                });
            }
        }
    }
}
