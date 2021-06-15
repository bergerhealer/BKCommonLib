package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.cdn.MojangMappings;
import com.bergerkiller.bukkit.common.internal.cdn.SpigotMappings;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.resolver.ClassPathResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldAliasResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.asm.ASMUtil;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

public class CraftBukkitServer extends CommonServerBase implements FieldNameResolver, FieldAliasResolver, ClassPathResolver {
    private static final String CB_ROOT = "org.bukkit.craftbukkit";
    private static final String NM_ROOT = "net.minecraft";
    private static final String NMS_ROOT = "net.minecraft.server";

    /**
     * Defines the Package Version
     */
    public String PACKAGE_VERSION;
    /**
     * Defines the Minecraft Version
     */
    public String MC_VERSION;
    /**
     * Defines the net.minecraft.server root path
     */
    public String NMS_ROOT_VERSIONED;
    /**
     * Defines the org.bukkit.craftbukkit root path
     */
    public String CB_ROOT_VERSIONED;
    /**
     * Defines the org.bukkit.craftbukkit.libs root path
     */
    public String CB_ROOT_LIBS;
    /**
     * Whether this is a Minecraft version after 1.17, where all net.minecraft classes
     * were no longer inside the net.minecraft.server package. Instead Mojang's mappings
     * are required to de-obfuscate field names.
     */
    private boolean HAS_MOJANG_MAPPINGS = false;
    /**
     * Whether this is a Minecraft version before 1.17, where all of minecraft server's
     * classes sat inside net.minecraft.server, with a package version. When true, all
     * classes that refer to net.minecraft are remapped to nms.
     */
    private boolean REMAP_TO_NMS = false;
    /**
     * Mojang mappings used to de-obfuscate the server at runtime.
     * Is downloaded / cached, if required.
     */
    private Map<String, MojangMappings.ClassMappings> mojangMappingsByBukkitClass = null;
    /**
     * Defines class name remappings to perform right after the version info is included in the class path
     */
    private Map<String, String> remappings = Collections.emptyMap();

    @Override
    public boolean init() {
        // No Bukkit server class, can't continue
        if (SERVER_CLASS == null) {
            return false;
        }

        // Find out what package version is used
        String serverPath = SERVER_CLASS.getName();
        if (!serverPath.startsWith(CB_ROOT)) {
            return false;
        }
        PACKAGE_VERSION = StringUtil.getBefore(serverPath.substring(CB_ROOT.length() + 1), ".");

        // Obtain the versioned roots
        if (PACKAGE_VERSION.isEmpty()) {
            NMS_ROOT_VERSIONED = NMS_ROOT;
            CB_ROOT_VERSIONED = CB_ROOT;
        } else {
            NMS_ROOT_VERSIONED = NMS_ROOT + "." + PACKAGE_VERSION;
            CB_ROOT_VERSIONED = CB_ROOT + "." + PACKAGE_VERSION;
        }
        CB_ROOT_LIBS = CB_ROOT + ".libs";

        // Figure out the MC version from the server
        MC_VERSION = PACKAGE_VERSION;
        return true;
    }

    @Override
    public void postInit(PostInitEvent event) {
        MC_VERSION = identifyMinecraftVersion();
        HAS_MOJANG_MAPPINGS = MountiplexUtil.evaluateText(MC_VERSION, ">=", "1.17");
        REMAP_TO_NMS = MountiplexUtil.evaluateText(MC_VERSION, "<", "1.17");

        // Check whether the server is at all compatible with the template definitions
        if (!event.getResolver().isSupported(MC_VERSION)) {
            event.signalIncompatible("Minecraft " + MC_VERSION + " is not supported!");
        }

        // Initialize the mappings on Minecraft 1.17 and later
        if (HAS_MOJANG_MAPPINGS) {
            // We require mojang's mappings
            MojangMappings mojangMappings = MojangMappings.fromCacheOrDownload(MC_VERSION);

            // Retrieve Bukkit-Mojang class name mappings
            // We need this to properly remap the mojang field names later
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
            if (!spigotMappings.byVersion.containsKey(MC_VERSION)) {
                Logging.LOGGER.log(Level.WARNING, "[Developer] Class mappings file has no mappings for this Minecraft version. Build problem?");
                try {
                    spigotMappings.downloadMappings(mojangMappings, MC_VERSION);
                } catch (IOException ex) {
                    throw new IllegalStateException("Failed to download Bukkit-Mojang class name mappings");
                }
            }

            // Apply spigot's mapping to mojang's mappings (got to reverse keys and values)
            final Map<String, String> classMappings = spigotMappings.byVersion.get(MC_VERSION)
                    .entrySet().stream().collect(Collectors.toMap(
                            Map.Entry::getValue, Map.Entry::getKey));
            mojangMappingsByBukkitClass = mojangMappings.classes.stream().collect(Collectors.toMap(
                    v -> classMappings.getOrDefault(v.name, v.name),
                    Function.identity()));
        }
    }

    @Override
    public String resolveClassPath(String path) {
        // Perform remappings first, so that they can be relocated
        // to the right (versioned) package after. That way there is
        // no need to track versions in the remapped paths.
        path = this.remappings.getOrDefault(path, path);

        // Remap org.bukkit.craftbukkit to the right package-versioned path
        if (path.startsWith(CB_ROOT) && !path.startsWith(CB_ROOT_VERSIONED) && !path.startsWith(CB_ROOT_LIBS)) {
            path = CB_ROOT_VERSIONED + path.substring(CB_ROOT.length());
        }

        // Remap net.minecraft to net.minecraft.server.<package_version> on pre-1.17
        // We cut the class name off the end. In case a class name is represented as
        // 'SomeName.SubClass' then we will correctly identify 'SomeName' as the main class.
        if (REMAP_TO_NMS && path.startsWith(NM_ROOT) && !path.startsWith(NMS_ROOT_VERSIONED)) {
            int index = path.length();
            String remapped = path;
            boolean isLastPart = true;
            do {
                // Find earlier occurrence of a package-separator '.'
                index = path.lastIndexOf('.', index - 1);
                if (index == -1) {
                    break;
                }

                // Check character following it is uppercase
                // Ignore first occurrence (could be an obfuscated 'cz' class or something)
                if (isLastPart) {
                    isLastPart = false;
                } else if (index < path.length() && !Character.isUpperCase(path.charAt(index + 1))) {
                    break;
                }

                // Match
                remapped = NMS_ROOT_VERSIONED + path.substring(index);
            } while (true);

            path = remapped;
        }

        return path;
    }

    @Override
    public String resolveFieldName(Class<?> declaringClass, String fieldName) {
        // Minecraft 1.17 and later require remapping, as all field names are obfuscated
        if (HAS_MOJANG_MAPPINGS) {
            MojangMappings.ClassMappings mappings = mojangMappingsByBukkitClass.get(declaringClass.getName());
            if (mappings != null) {
                fieldName = mappings.name_to_obfuscated.getOrDefault(fieldName, fieldName);
            }
        }

        return fieldName;
    }

    @Override
    public String resolveFieldAlias(Field field, String name) {
        // Minecraft 1.17 and later require remapping, as all field names are obfuscated
        if (HAS_MOJANG_MAPPINGS) {
            MojangMappings.ClassMappings mappings = mojangMappingsByBukkitClass.get(field.getDeclaringClass().getName());
            if (mappings != null) {
                return mappings.obfuscated_to_name.getOrDefault(name, null);
            }
        }

        return null;
    }

    private String identifyMinecraftVersion() throws VersionIdentificationFailureException {
        try {
            // Find getVersion() method using reflection
            Class<?> minecraftServerType;
            try {
                minecraftServerType = loadClass(NMS_ROOT + ".MinecraftServer");
            } catch (ClassNotFoundException ex) {
                minecraftServerType = loadClass(NMS_ROOT_VERSIONED + ".MinecraftServer");
            }

            java.lang.reflect.Method getVersionMethod = getDeclaredMethod(minecraftServerType, "getVersion");

            // This is easy when the server is already initialized
            if (Bukkit.getServer() != null) {
                // Obtain MinecraftServer instance from server
                Class<?> server = loadClass(CB_ROOT_VERSIONED + ".CraftServer");

                // Standard CraftServer::getServer()
                Object minecraftServerInstance;
                try {
                    java.lang.reflect.Method getServerMethod = getDeclaredMethod(server, "getServer");
                    minecraftServerInstance = getServerMethod.invoke(Bukkit.getServer());
                    return (String) getVersionMethod.invoke(minecraftServerInstance);
                } catch (NoSuchMethodException ex) {}

                throw new VersionIdentificationFailureException("Server has no MinecraftServer instance");
            }

            // If MinecraftVersion class exists, we can use the static a() method to retrieve it
            // Alternative is using SharedConstants, but it initializes a whole lot extra and is therefore slow
            // Both of these methods work since MC 1.14
            try {
                Object gameVersion = null;

                try {
                    // Try to identify the MinecraftVersion class
                    // This is at net.minecraft.MinecraftVersion for MC 1.17 and later
                    Class<?> minecraftVersionClass;
                    try {
                        minecraftVersionClass = loadClass("net.minecraft.MinecraftVersion");
                    } catch (ClassNotFoundException ex) {
                        minecraftVersionClass = loadClass(NMS_ROOT_VERSIONED + ".MinecraftVersion");
                    }

                    // Call the static method of MinecraftVersion to obtain the GameVersion instance
                    gameVersion = minecraftVersionClass.getDeclaredMethod("a").invoke(null);
                } catch (ClassNotFoundException | NoSuchMethodException ex) {
                    // Older versions of Minecraft: use SharedConstants instead
                    Class<?> sharedConstantsClass = loadClass(NMS_ROOT_VERSIONED + ".SharedConstants");
                    gameVersion = sharedConstantsClass.getDeclaredMethod("a").invoke(null);
                    Logging.LOGGER.warning("Failed to find Minecraft Version using MinecraftVersion.class, used SharedConstants instead");
                }
                return gameVersion.getClass().getMethod("getName").invoke(gameVersion).toString();

            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                // No server instance is available, fastest way is to inspect the bytecode using ASM
                // Creating an instance, even without calling constructors, takes a long while to initialize
                String fromASM = ASMUtil.findStringConstantReturnedByMethod(getVersionMethod);
                if (fromASM != null) {
                    return fromASM;
                }

                // Find DedicatedServer object, differs on 1.17 and later
                Class<?> dedicatedServerClass;
                try {
                    dedicatedServerClass = loadClass("net.minecraft.server.dedicated.DedicatedServer");
                } catch (ClassNotFoundException ex2) {
                    dedicatedServerClass = loadClass(NMS_ROOT_VERSIONED + ".DedicatedServer");
                }

                // Create an instance of Minecraft Server without calling any constructors
                // This is a bit slower, but works as a reliable fallback
                Logging.LOGGER.warning("Failed to find Minecraft Version using ASM, falling back to slower null-constructing");
                ClassTemplate<?> nms_server_tpl = ClassTemplate.create(dedicatedServerClass);
                Object minecraftServerInstance = nms_server_tpl.newInstanceNull();
                return (String) getVersionMethod.invoke(minecraftServerInstance);
            }
        } catch (VersionIdentificationFailureException e) {
            throw e; // rethrow, don't wrap
        } catch (Throwable t) {
            throw new VersionIdentificationFailureException(t);
        }
    }

    private Method getDeclaredMethod(Class<?> declaringClass, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        methodName = Resolver.resolveMethodName(declaringClass, methodName, parameterTypes);
        return MPLType.getDeclaredMethod(declaringClass, methodName, parameterTypes);
    }

    private Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> type = Resolver.loadClass(name, false);
        if (type == null) {
            throw new ClassNotFoundException("Failed to load class " + name);
        }
        return type;
    }

    @Override
    public String getMinecraftVersion() {
        return MC_VERSION;
    }

    @Override
    public String getServerVersion() {
        return (PACKAGE_VERSION.isEmpty() ? "(Unknown)" : PACKAGE_VERSION) + " (Minecraft " + MC_VERSION + ")";
    }

    @Override
    public String getServerDescription() {
        String desc = Bukkit.getServer().getVersion();
        desc = desc.replace(" (MC: " + MC_VERSION + ")", "");
        return desc;
    }

    @Override
    public String getServerName() {
        return "CraftBukkit";
    }

    @Override
    public String getNMSRoot() {
        return NMS_ROOT_VERSIONED;
    }

    @Override
    public String getCBRoot() {
        return CB_ROOT_VERSIONED;
    }

    /**
     * Sets the remappings to perform right after the version info is included in the Class name package path.
     * 
     * @param remappings
     */
    public void setEarlyRemappings(Map<String, String> remappings) {
        this.remappings = remappings;
    }

    /**
     * Exception thrown when the minecraft version of the server could not be identified
     */
    public static final class VersionIdentificationFailureException extends RuntimeException {
        private static final long serialVersionUID = -3513069083904231139L;

        public VersionIdentificationFailureException(String reason) {
            super("Failed to identify the Minecraft version of the server: " + reason);
        }

        public VersionIdentificationFailureException(Throwable cause) {
            super("Failed to identify the Minecraft version of the server", cause);
        }
    }
}
