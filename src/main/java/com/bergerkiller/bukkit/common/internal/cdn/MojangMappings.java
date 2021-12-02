package com.bergerkiller.bukkit.common.internal.cdn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bergerkiller.bukkit.common.Logging;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * API around the published mappings for minecraft server.
 */
public class MojangMappings {
    public final List<ClassMappings> classes = new ArrayList<>();

    /**
     * The mappings of a single class
     */
    public static class ClassMappings extends ClassSignature {
        public final BiMap<String, String> fields_obfuscated_to_name;
        public final BiMap<String, String> fields_name_to_obfuscated;
        public final List<MethodSignature> methods;

        public ClassMappings(String name, String name_obfuscated) {
            super(name, name_obfuscated);
            this.fields_obfuscated_to_name = HashBiMap.create();
            this.fields_name_to_obfuscated = this.fields_obfuscated_to_name.inverse();
            this.methods = new ArrayList<>();
        }

        public void addField(String obfuscatedName, String mojangName) {
            this.fields_obfuscated_to_name.put(obfuscatedName, mojangName);
        }

        public void addMethod(String obfuscatedName, String mojangName,
                ClassSignature returnType, List<ClassSignature> parameterTypes
        ) {
            this.methods.add(new MethodSignature(this, mojangName, obfuscatedName, returnType, parameterTypes));
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
     * Parses the proguard-formatted mappings file into a MojangMappings instance
     */
    private static class ProGuardParser {
        // Regex: \s+[\[\]<>\w\._\-$]+\s([\w_\-$]+)\s->\s([\w_\-$]+)
        private static final Pattern FIELD_NAME_PATTERN = Pattern.compile("\\s+[\\[\\]<>\\w\\._\\-$]+\\s([\\w_\\-$]+)\\s->\\s([\\w_\\-$]+)");
        // Regex: \s+(\d+:\d+:)?[\[\]<>\w\._\-$]+\s([\w_\-$]+)\(([\[\]<>\w\._\-,$]*)\)\s->\s([\w_\-$]+)
        private static final Pattern METHOD_NAME_PATTERN = Pattern.compile("\\s+(\\d+:\\d+:)?([\\[\\]<>\\w\\._\\-$]+)\\s([\\w_\\-$]+)\\(([\\[\\]<>\\w\\._\\-,$]*)\\)\\s->\\s([\\w_\\-$]+)");

        private final MojangMappings result = new MojangMappings();
        private final Map<String, ClassSignature> mappingsByDeobfName = new HashMap<>();

        public MojangMappings parse(java.io.BufferedReader reader) throws IOException {
            ClassMappings currClassMappings = null;
            List<PendingMethod> pendingMethods = new ArrayList<>();
            for (String line; (line = reader.readLine()) != null; ) {
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
                        mappingsByDeobfName.put(currClassMappings.name, currClassMappings);

                        // Don't add these - that's weird
                        if (isValidClass(currClassMappings.name)) {
                            result.classes.add(currClassMappings);
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
            pendingMethods.forEach(this::processMethod);

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

        private void processMethod(PendingMethod method) {
            // Translate the return type if it refers to an obfuscated name
            ClassSignature returnType = translateTypeName(method.returnTypeName);

            // Translate the argument types if they refer to obfuscated names
            List<ClassSignature> params = new ArrayList<ClassSignature>(method.paramTypeNames.length);
            for (String paramTypeName : method.paramTypeNames) {
                params.add(translateTypeName(paramTypeName));
            }

            // Add
            method.classMappings.addMethod(method.obfuscatedName, method.mojangName, returnType, params);
        }

        private ClassSignature translateTypeName(String name) {
            // Eliminate array declarations from name
            String classNameBare = name;
            String postfix = "";
            while (classNameBare.endsWith("[]")) {
                classNameBare = classNameBare.substring(0, classNameBare.length() - 2);
                postfix += "[]";
            }

            // Find an existing remapping signature if it exists, if not, create one (cache)
            ClassSignature argSig = this.mappingsByDeobfName.computeIfAbsent(classNameBare,
                    c -> new ClassSignature(c, c));

            // Array postfixes don't happen often enough to warrant caching and re-using signatures
            if (!postfix.isEmpty()) {
                argSig = new ClassSignature(argSig.name + postfix, argSig.name_obfuscated + postfix);
            }

            return argSig;
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
        }
    }
}
