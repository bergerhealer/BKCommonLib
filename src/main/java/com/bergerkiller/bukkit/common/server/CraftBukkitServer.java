package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.internal.cdn.MojangSpigotRemapper;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.mountiplex.logic.TextValueSequence;
import com.bergerkiller.mountiplex.reflection.resolver.ClassPathResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldAliasResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import org.bukkit.Bukkit;

public class CraftBukkitServer extends CommonServerBase implements MethodNameResolver, FieldNameResolver, FieldAliasResolver, ClassPathResolver {
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
     * Whether this is a Minecraft version after 1.17, where all net.minecraft classes
     * were no longer inside the net.minecraft.server package. Instead Mojang's mappings
     * are required to de-obfuscate field names.
     */
    private boolean HAS_MOJANG_FIELD_MAPPINGS = false;
    /**
     * Whether this is a Minecraft version after 1.18, where as well all the method names
     * inside the net.minecraft package are obfuscated.
     */
    private boolean HAS_MOJANG_METHOD_MAPPINGS = false;
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
    private MojangSpigotRemapper mojangSpigotRemapper = null;
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
        String serverPath = SERVER_CLASS.getName();
        if (!serverPath.startsWith(CB_ROOT)) {
            return false;
        }
        PACKAGE_VERSION = StringUtil.getBefore(serverPath.substring(CB_ROOT.length()), ".");

        // Obtain the versioned roots
        if (PACKAGE_VERSION.isEmpty()) {
            NMS_ROOT_VERSIONED = PACKAGE_NMS_ROOT;
            CB_ROOT_VERSIONED = PACKAGE_CB_ROOT;
        } else {
            NMS_ROOT_VERSIONED = PACKAGE_NMS_ROOT + "." + PACKAGE_VERSION;
            CB_ROOT_VERSIONED = PACKAGE_CB_ROOT + "." + PACKAGE_VERSION;
        }
        CB_ROOT_LIBS = PACKAGE_CB_ROOT + ".libs";

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

        HAS_MOJANG_FIELD_MAPPINGS = TextValueSequence.evaluateText(MC_VERSION, ">=", "1.17");
        HAS_MOJANG_METHOD_MAPPINGS = TextValueSequence.evaluateText(MC_VERSION, ">=", "1.18");
        REMAP_TO_NMS = TextValueSequence.evaluateText(MC_VERSION, "<", "1.17");

        // Check whether the server is at all compatible with the template definitions
        if (!event.getResolver().isSupported(MC_VERSION)) {
            event.signalIncompatible("Minecraft " + MC_VERSION + " is not supported!");
        }

        // Initialize the mappings on Minecraft 1.17 and later
        if (HAS_MOJANG_FIELD_MAPPINGS || HAS_MOJANG_METHOD_MAPPINGS) {
            try {
                isInitializingMojangSpigotRemapper = true;
                mojangSpigotRemapper = MojangSpigotRemapper.load(MC_VERSION, this::resolveClassPathEarly);
            } finally {
                isInitializingMojangSpigotRemapper = false;
            }
        }
    }

    /**
     * Can be overrided by forks of CraftBukkit to properly find class names before this
     * server is initialized. This is called on Minecraft 1.17 and later when initializing
     * the remapper for mojang mappings. It is never called after.<br>
     * <br>
     * This method should perform any required remappings to find the true, actual, class
     * name. It should do so without using the mojang remapper, since it will not yet be
     * initialized.<br>
     * <br>
     * Only class names will be provided that are known spigot class names,
     * no user- or template-provided class names are sent this way.
     *
     * @param path
     * @return Remapped class path for use by resolver, same as path if not remapped
     */
    protected String resolveClassPathEarly(String path) {
        // Call the normal resolveClassPath(). We're assuming that no dangerous
        // things will be happening. For this purpose we're using a flag to avoid
        // any special remappings that could throw it out of balance.
        //
        // If this does cause trouble a server implementation can override this
        // and execute alternative instructions.
        return resolveClassPath(path);
    }

    @Override
    public String resolveClassPath(String path) {
        // Don't do anything at all here when initializing the mojang remapper
        if (isInitializingMojangSpigotRemapper) {
            return path;
        }

        // Perform remappings first, so that they can be relocated
        // to the right (versioned) package after. That way there is
        // no need to track versions in the remapped paths.
        path = this.remappings.getOrDefault(path, path);

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
            fieldName = mojangSpigotRemapper.remapFieldName(declaringClass, fieldName, fieldName);
        }

        return fieldName;
    }

    @Override
    public String resolveFieldAlias(Field field, String name) {
        // Minecraft 1.17 and later require remapping, as all field names are obfuscated
        if (HAS_MOJANG_FIELD_MAPPINGS) {
            return mojangSpigotRemapper.remapFieldName(field.getDeclaringClass(), name, null);
        }

        return null;
    }

    @Override
    public String resolveMethodName(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes) {
        // Minecraft 1.18 and later require remapping, as all method names are obfuscated
        if (HAS_MOJANG_METHOD_MAPPINGS) {
            return mojangSpigotRemapper.remapMethodName(declaringClass, methodName, parameterTypes, methodName);
        }

        return methodName;
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
}
