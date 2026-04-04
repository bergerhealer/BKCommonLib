package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.cdn.MojangRemapper;
import com.bergerkiller.bukkit.common.internal.cdn.SpigotMappings;
import com.bergerkiller.mountiplex.logic.TextValueSequence;
import com.bergerkiller.mountiplex.reflection.resolver.ClassPathResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldAliasResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodAliasResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import org.bukkit.Bukkit;

public class CraftBukkitServer extends CommonServerBase implements MethodNameResolver, FieldNameResolver,
        FieldAliasResolver, MethodAliasResolver, ClassPathResolver
{
    private static final String PACKAGE_CB_ROOT = "org.bukkit.craftbukkit";
    private static final String PACKAGE_NMS_ROOT = "net.minecraft.server";
    private static final String CB_ROOT = "org.bukkit.craftbukkit.";
    private static final String NM_ROOT = "net.minecraft.";

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
     * Whether this is a Minecraft version after 1.17 and before 26.1, where all net.minecraft classes
     * were no longer inside the net.minecraft.server package. Instead Mojang's mappings
     * are required to de-obfuscate field names.
     */
    private boolean HAS_MOJANG_FIELD_MAPPINGS = false;
    /**
     * Whether this is a Minecraft version after 1.18 and before 26.1, where all the method names
     * inside the net.minecraft package are obfuscated as well as the fields.
     */
    private boolean HAS_MOJANG_METHOD_MAPPINGS = false;
    /**
     * Whether this is a Minecraft server with Spigot class names (PacketPlayOutXXX instead of ClientBoundXXX).
     * If true, then a remapper exists to remap all requested class names from Mojang to Spigot.
     * Since Minecraft 26.1 the server is mojang-mapped and spigot class names are no longer used,
     * and then this is false.
     */
    private boolean MAP_CLASSES_FROM_MOJANGMAP_TO_SPIGOT = false;
    /**
     * Whether this is a Minecraft version before 1.17, where all of minecraft server's
     * classes sat inside net.minecraft.server, with a package version. When true, all
     * classes that refer to net.minecraft are remapped to nms.
     */
    private boolean REMAP_TO_NMS = false;
    /**
     * Mojang/Spigot class, field and method name remapper used to remap mojang's names
     * to obfuscated names as used in the server jar at runtime. Only used on Minecraft
     * 1.17 and later.
     */
    private MojangRemapper mojangRemapper = null;
    /**
     * Used on Mojangmap-mapped servers (Paper) when people choose the "mojmap" jar.
     */
    private SpigotMappings.ClassMappings mojangToSpigotClassRemapper = null;
    /**
     * Whether currently the mojang/spigot remapper is being initialized. Acts as a flag
     * to avoid using template-based remappings.
     */
    private boolean isInitializingMojangSpigotRemapper = false;
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
        CB_ROOT_VERSIONED = SERVER_CLASS.getPackage().getName();
        CB_ROOT_LIBS = PACKAGE_CB_ROOT + ".libs";

        // Obtain the versioned roots
        if (CB_ROOT_VERSIONED.startsWith(CB_ROOT)) {
            PACKAGE_VERSION = CB_ROOT_VERSIONED.substring(CB_ROOT.length());
            NMS_ROOT_VERSIONED = PACKAGE_NMS_ROOT + "." + PACKAGE_VERSION;
        } else {
            PACKAGE_VERSION = "";
            NMS_ROOT_VERSIONED = PACKAGE_NMS_ROOT;
        }

        // Figure out the MC version from the server
        MC_VERSION = PACKAGE_VERSION;
        return true;
    }

    @Override
    public void postInit(PostInitEvent event) {
        try {
            MC_VERSION = (new MinecraftVersionDiscovery(PACKAGE_VERSION)).detect();
        } catch (VersionIdentificationFailureException e) {
            throw e; // rethrow, don't wrap
        } catch (Throwable t) {
            throw new VersionIdentificationFailureException(t);
        }

        // Detect whether this is a MojangMap server
        // All servers since Minecraft 26.1 are Mojang-mapped. Prior, Paper also had a mode
        // to publish Mojang-mapped jars, but it was not the default and not all versions had it.
        // There we must detect this at runtime.
        // We assume that servers with extra remapping (Forge hybrids) will not ever use this
        // mojang-mapped mode, so we do not try to remap the class name as part of this detection.
        boolean isUnobfuscatedMojangMapJar = false;
        if (TextValueSequence.evaluateText(MC_VERSION, ">=", "26.1")) {
            isUnobfuscatedMojangMapJar = true;
        } else if (TextValueSequence.evaluateText(MC_VERSION, ">=", "1.17")) {
            Class<?> mojangClass = null, spigotClass = null;
            try {
                mojangClass = Resolver.getClassByExactName("net.minecraft.server.level.ServerPlayer");
            } catch (Throwable t) {}
            try {
                spigotClass = Resolver.getClassByExactName("net.minecraft.server.level.EntityPlayer");
            } catch (Throwable t) {}

            if (mojangClass != null && spigotClass == null) {
                // Sanity check: the getCamera() method must exist unobfuscated
                boolean hasMojangMethod = false;
                try {
                    MPLType.getDeclaredMethod(mojangClass, "getCamera");
                    hasMojangMethod = true;
                } catch (Throwable t) {}

                if (hasMojangMethod) {
                    isUnobfuscatedMojangMapJar = true;
                }
            }
        }

        if (isUnobfuscatedMojangMapJar) {
            // Since this version, the server is using Mojang's full official mappings and there is no obfuscation at all
            MAP_CLASSES_FROM_MOJANGMAP_TO_SPIGOT = false;
            HAS_MOJANG_FIELD_MAPPINGS = false;
            HAS_MOJANG_METHOD_MAPPINGS = false;
            REMAP_TO_NMS = false;
        } else {
            MAP_CLASSES_FROM_MOJANGMAP_TO_SPIGOT = true;
            HAS_MOJANG_FIELD_MAPPINGS = TextValueSequence.evaluateText(MC_VERSION, ">=", "1.17");
            HAS_MOJANG_METHOD_MAPPINGS = TextValueSequence.evaluateText(MC_VERSION, ">=", "1.18");
            REMAP_TO_NMS = TextValueSequence.evaluateText(MC_VERSION, "<", "1.17");
        }

        // Check whether the server is at all compatible with the template definitions
        if (!event.getResolver().isSupported(MC_VERSION)) {
            event.signalIncompatible("Minecraft " + MC_VERSION + " is not supported!");
        }

        if (!isUnobfuscatedMojangMapJar) {
            // We must remap all requested Mojang classes to the Spigot class names.
            mojangToSpigotClassRemapper = SpigotMappings.forVersion(MC_VERSION);

            // Initialize the field/method mappings on Minecraft 1.17 and later
            if (HAS_MOJANG_FIELD_MAPPINGS || HAS_MOJANG_METHOD_MAPPINGS) {
                try {
                    isInitializingMojangSpigotRemapper = true;
                    mojangRemapper = MojangRemapper.load(MC_VERSION, this);

                    // Spigot changed this field, which causes the field to no longer be obfuscated in the server by accident
                    // To avoid issues remove this field mapping from the mojang remapper
                    try {
                        Class<?> declaringClass = Resolver.getClassByExactName(this.resolveClassPath("net.minecraft.server.level.ServerPlayer"));
                        mojangRemapper.removeMethodMapping(declaringClass, "nextContainerCounter");
                    } catch (ClassNotFoundException ex) {
                        Logging.LOGGER.severe("Mapping filter fail: declaring class net.minecraft.server.level.ServerPlayer not found");
                        return;
                    }
                } finally {
                    isInitializingMojangSpigotRemapper = false;
                }
            }
        }
    }

    @Override
    public String resolveClassPath(String path) {
        // Don't do anything at all here when initializing the mojang remapper
        if (isInitializingMojangSpigotRemapper) {
            if (mojangToSpigotClassRemapper != null) {
                return mojangToSpigotClassRemapper.toSpigot(path);
            }
            return path;
        }

        // Perform remappings first, so that they can be relocated
        // to the right (versioned) package after. That way there is
        // no need to track versions in the remapped paths.
        path = this.remappings.getOrDefault(path, path);

        // Now remap the mojang-mapped class name to the right name on spigot, if the server uses
        // spigot mapped class names.
        if (mojangToSpigotClassRemapper != null) {
            path = mojangToSpigotClassRemapper.toSpigot(path);
        }

        // Remap org.bukkit.craftbukkit to the right package-versioned path
        if (path.startsWith(CB_ROOT) && !path.startsWith(CB_ROOT_VERSIONED) && !path.startsWith(CB_ROOT_LIBS)) {
            path = CB_ROOT_VERSIONED + path.substring(PACKAGE_CB_ROOT.length());
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
        if (HAS_MOJANG_FIELD_MAPPINGS) {
            fieldName = mojangRemapper.remapFieldName(declaringClass, fieldName, fieldName);
        }

        return fieldName;
    }

    @Override
    public String resolveFieldAlias(Field field, String name) {
        // Minecraft 1.17 and later require remapping, as all field names are obfuscated
        if (HAS_MOJANG_FIELD_MAPPINGS) {
            return mojangRemapper.remapFieldNameReverse(field.getDeclaringClass(), name, null);
        }

        return null; // Try fallbacks
    }

    @Override
    public String resolveMethodName(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes) {
        // Minecraft 1.18 and later require remapping, as all method names are obfuscated
        if (HAS_MOJANG_METHOD_MAPPINGS) {
            return mojangRemapper.remapMethodName(declaringClass, methodName, parameterTypes, methodName);
        }

        return methodName;
    }

    @Override
    public String resolveMethodAlias(Method method, String name) {
        // Minecraft 1.18 and later require remapping, as all method names are obfuscated
        if (HAS_MOJANG_METHOD_MAPPINGS) {
            return mojangRemapper.remapMethodNameReverse(method.getDeclaringClass(), name, method.getParameterTypes(), null);
        }

        return null; // Try fallbacks
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
    public boolean isForgeServer() {
        return false;
    }

    @Override
    public boolean isMojangMappings() {
        return !MAP_CLASSES_FROM_MOJANGMAP_TO_SPIGOT;
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
}
