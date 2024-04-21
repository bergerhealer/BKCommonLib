package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.templates.TemplateResolver;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * Provides server-specific information and functionalities. Is used to identify
 * what Minecraft server version is run on, among other metadata. Also provides
 * the at-runtime remapping facilities requires to run on the server.
 */
public interface CommonServer {

    /**
     * Tries to initialize the server
     *
     * @return True if initializing was successful, False if not
     */
    public boolean init();

    /**
     * Called after the {@link #init()} method successfully detected the server
     * and initialized the server. In here processing that depends on the
     * CommonServer instance being fully initialized can be continued.
     * 
     * @param event Context for initialization. See {@link PostInitEvent}.
     */
    public void postInit(PostInitEvent event);

    /**
     * Prepares this server for enabling of BKCommonLIb
     *
     * @param plugin instance
     */
    public void enable(CommonPlugin plugin);

    /**
     * Prepares this server for disabling of BKCommonLib
     *
     * @param plugin instance
     */
    public void disable(CommonPlugin plugin);

    /**
     * Combines all server information available into a single detailed description.
     * This is the message displayed when the plugin is enabled.
     * 
     * @return server details String
     */
    public String getServerDetails();

    /**
     * Gets the versioning information of the server
     *
     * @return server versioning description
     */
    public String getServerVersion();

    /**
     * Gets the full name of the server
     *
     * @return server name
     */
    public String getServerName();

    /**
     * Gets whether the server uses Mojang's original class, field and method names
     *
     * @return True if the server uses mojang mappings
     */
    public boolean isMojangMappings();

    /**
     * Gets whether this server is a type of Forge server implementation
     *
     * @return True if this is a forge-based server
     */
    public boolean isForgeServer();

    /**
     * Gets a more detailed description of the server, excluding the server
     * version
     *
     * @return server description
     */
    public String getServerDescription();

    /**
     * Gets the version of Minecraft the server supports
     *
     * @return Minecraft version
     */
    public String getMinecraftVersion();

    /**
     * Gets the major Minecraft version, removing any -pre postfixes from the version.
     * 
     * @return major minecraft version
     */
    public String getMinecraftVersionMajor();

    /**
     * Gets the pre-release version name from the Minecraft version. Is null if this
     * is not a pre-release version.
     * 
     * @return pre release minecraft version
     */
    public String getMinecraftVersionPre();

    /**
     * Checks if the Minecraft version matches a version condition
     * 
     * @param operand to evaluate with, for example ">=" and "!="
     * @param version the operand is applied to (right side)
     * @return True if the version matches, False if not
     */
    public boolean evaluateMCVersion(String operand, String version);

    /**
     * Gets the File Location where the regions of a world are contained
     *
     * @param worldName to get the regions folder for
     * @return Region folder
     */
    public File getWorldRegionFolder(String worldName);

    /**
     * Gets the File Location where a world is contained
     *
     * @return World folder
     */
    public File getWorldFolder(String worldName);

    /**
     * Gets the File Location of the level.dat where world settings are stored
     *
     * @param worldName World name
     * @return World level.dat File
     */
    public File getWorldLevelFile(String worldName);

    /**
     * Gets a Collection of all worlds that can be loaded without creating it
     *
     * @return Loadable world names
     */
    public Collection<String> getLoadableWorlds();

    /**
     * Checks whether the World name specified contains a folder and can be
     * loaded
     *
     * @param worldName to check
     * @return True if the world can be loaded, False if not
     */
    public boolean isLoadableWorld(String worldName);

    /**
     * Gets the root package path for net.minecraft.server
     * 
     * @return minecraft server root
     */
    public String getNMSRoot();

    /**
     * Gets the root package path for org.bukkit.craftbukkit
     * 
     * @return craftbukkit root
     */
    public String getCBRoot();

    /**
     * Gets whether a given EntityType enum value represents a special 'custom' entity type
     * on this server. This is a type of entity normally not present in vanilla Minecraft.
     * An example case of this happening is on Forge.
     * 
     * @param entityType
     * @return True if the entity type is a custom entity type, not part of vanilla Minecraft
     */
    public boolean isCustomEntityType(org.bukkit.entity.EntityType entityType);

    /**
     * Adds the environment variables relevant for this server type
     * 
     * @param variables Map of variables to fill
     */
    public void addVariables(Map<String, String> variables);

    /**
     * Removes any pre-postfixes from a verion String
     * 
     * @param mc_version
     * @return clean Minecraft version
     */
    public static String cleanVersion(String mc_version) {
        String clean_version = mc_version;
        int pre_idx = clean_version.indexOf("-pre");
        if (pre_idx != -1) {
            clean_version = clean_version.substring(0, pre_idx);
        }
        return clean_version;
    }

    /**
     * Checks for the existance of a -pre in the version String,
     * and returns the pre-version if found. If none found,
     * null is returned.
     * 
     * @param mc_version
     * @return pre-version, null if no pre-version is active
     */
    public static String preVersion(String mc_version) {
        int pre_idx = mc_version.indexOf("-pre");
        if (pre_idx != -1) {
            return mc_version.substring(pre_idx + 4);
        }
        return null;
    }

    /**
     * Event passed to {@link CommonServer#postInit(event)} that provides
     * additional context with the template resolver, and a way to signal
     * server compatibility.
     */
    public static class PostInitEvent {
        private final TemplateResolver templateResolver;
        private String incompatibleReason = null;

        public PostInitEvent(TemplateResolver templateResolver) {
            this.templateResolver = templateResolver;
        }

        /**
         * If {@link #isCompatible()} returns false, this provides the
         * reason for it.
         *
         * @return reason the server is not compatible, null if compatible
         */
        public String getIncompatibleReason() {
            return incompatibleReason;
        }

        /**
         * Whether the CommonServer implementation decided the current
         * server is compatible, or not.
         *
         * @return True if compatible
         */
        public boolean isCompatible() {
            return incompatibleReason == null;
        }

        /**
         * Gets the template resolver instance which provides the required
         * context to initialize templates. The resolver also provides the
         * list of minecraft versions that the templates support.
         *
         * @return resolver
         */
        public TemplateResolver getResolver() {
            return templateResolver;
        }

        /**
         * Signal to the caller that the current server version is incompatible.
         * This can be because of an unaccounted for deviation in the server makeup,
         * or because the detected minecraft version is not supported.
         *
         * @param reason Reason the server is not compatible
         */
        public void signalIncompatible(String reason) {
            incompatibleReason = reason;
        }
    }
}
