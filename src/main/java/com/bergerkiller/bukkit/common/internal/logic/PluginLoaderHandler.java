package com.bergerkiller.bukkit.common.internal.logic;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

/**
 * A type of plugin loader system in use by the server. Provides additional
 * class-loading APIs not available through Bukkit alone.
 */
public abstract class PluginLoaderHandler {
    protected final Plugin plugin;
    protected final org.bukkit.configuration.file.YamlConfiguration pluginConfig;
    private final String pluginConfigText;

    protected PluginLoaderHandler(Plugin plugin, String pluginConfigText) {
        this.plugin = plugin;
        this.pluginConfigText = pluginConfigText;
        this.pluginConfig = new org.bukkit.configuration.file.YamlConfiguration();
        try {
            this.pluginConfig.loadFromString(pluginConfigText);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, "[Configuration] An error occured while loading plugin.yml resource for plugin " + plugin.getName() + ":", t);
        }
    }

    /**
     * Called right after constructing/loading this plugin loader handler. This handler
     * can do extra work here to register stuff.
     */
    public abstract void bootstrap();

    /**
     * Notifies this handler that another plugin was loaded. Will do the required
     * background work to grant access to the plugins classloader, if needed.
     *
     * @param plugin New plugin that loaded up
     */
    public abstract void onPluginLoaded(Plugin plugin);

    /**
     * Grants this plugin access to the classes of the plugin specified
     *
     * @param plugin Plugin
     */
    public abstract void addAccessToClassloader(Plugin plugin);

    /**
     * Looks up the class loaders of the plugin names specified, and if they exist,
     * grants this plugin access to those plugins classes.
     *
     * @param pluginNames Names of plugin to grant access to
     */
    public abstract void addAccessToClassloaders(List<String> pluginNames);

    /**
     * Gets the text contents of the plugin.yml file (spigot, older paper) or the
     * paper-plugin.yml (Paper 1.19.3+)
     *
     * @return Plugin configuration text
     */
    public final String getPluginConfigText() {
        return pluginConfigText;
    }

    /**
     * Gets the YAML-decoded plugin configuration
     *
     * @return plugin configuration
     * @see #getPluginConfigText()
     */
    public final org.bukkit.configuration.file.YamlConfiguration getPluginConfig() {
        return pluginConfig;
    }

    /**
     * Checks whether this plugin uses a pre-loader, and if so, whether that preloader
     * failed to initialize the actual plugin. This is to make sure a plugin is
     * actually enabled, because it could be ghost-enabled like this.
     *
     * @return True if preloading had failed
     */
    public final boolean hasPreloadingFailed() {
        // Check plugin instance is a 'Preloader' instance
        if (!(plugin instanceof JavaPlugin) || !plugin.getClass().getSimpleName().equals("Preloader")) {
            return false;
        }

        // Check a main was configured and if so, whether it matches with the main
        // Kinda redundant, but maybe the plugins main class IS 'Preloader'. Just to be sure.
        String depExpectedMain = pluginConfig.getString("preloader.main", "");
        return !depExpectedMain.isEmpty() && !depExpectedMain.equals(plugin.getClass().getName());
    }

    /**
     * Collects known plugin information and compiles it into a single version description.
     * This will include at least the version number, and if available, the 'build' property
     * stored in the plugin.yml.
     *
     * @return debugging version information
     */
    public final String getDebugVersion() {
        String buildInfo = pluginConfig.getString("build", "");
        String debugVersion = plugin.getDescription().getVersion();
        if (buildInfo.length() > 0) {
            debugVersion += " (build: " + buildInfo + ")";
        }
        return debugVersion;
    }

    protected ClassLoader getClassLoader() {
        return plugin.getClass().getClassLoader();
    }

    /**
     * Instantiates a new PluginLoaderHandler best suitable for the plugin instance
     * specified.
     *
     * @param plugin Plugin instance
     * @return Plugin loader handler that should be used
     */
    public static PluginLoaderHandler createFor(Plugin plugin) {
        // Check whether Paper's plugin loader API is available on the server
        // If it is, spigot's handler is worthless and shouldn't be attempted
        Class<?> paperLoaderType;
        try {
            paperLoaderType = Class.forName("io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader");
        } catch (Throwable t) {
            // Spigot! Use Spigot handler

            // Read config. Note: can throw if missing!
            String config = readPluginYAML(plugin, "plugin.yml");

            return new PluginLoaderHandlerSpigot(plugin, config);
        }

        // Check whether the plugin includes a paper-plugin.yml
        // If it does, there's nothing for us to do here
        String paperPluginConfig;
        try {
            paperPluginConfig = readPluginYAML(plugin, "paper-plugin.yml");
        } catch (YamlNotReadException e) {
            // Maybe it has a plugin.yml instead

            // Read config. Note: can throw if missing!
            String config = readPluginYAML(plugin, "plugin.yml");

            return new PluginLoaderHandlerPaperFallback(plugin, config);
        }

        return new PluginLoaderHandlerPaper(plugin, paperPluginConfig);
    }

    /**
     * Checks that a plugin is actually, fully enabled. If the plugin uses a preloader mechanism
     * to stay 'enabled' while not actually be, this checks for that as well.
     *
     * @param pluginName Name of the Plugin to check
     * @return True if fully enabled
     */
    public static boolean isPluginFullyEnabled(String pluginName) {
        return isPluginFullyEnabled(Bukkit.getPluginManager().getPlugin(pluginName));
    }

    /**
     * Checks that a plugin is actually, fully enabled. If the plugin uses a preloader mechanism
     * to stay 'enabled' while not actually be, this checks for that as well.
     *
     * @param plugin Plugin to check
     * @return True if fully enabled
     */
    public static boolean isPluginFullyEnabled(Plugin plugin) {
        if (plugin == null || !plugin.isEnabled()) {
            return false;
        }

        // Check plugin instance is a 'Preloader' instance
        // If so, check this main class called "Preloader" is coupled with a config
        if (plugin instanceof JavaPlugin && plugin.getClass().getSimpleName().equals("Preloader")) {
            // Plugin might be a 'Preloader' instance. Check if a preloader section is
            // included in the plugin's plugin.yml and, if so, check if this plugin main class
            // matches the main defined there. If any of this is not true, then the plugin
            // is in preloader state (loading failed), and didn't (actually) enable!
            try {
                if (PluginLoaderHandler.createFor(plugin).hasPreloadingFailed()) {
                    return false;
                }
            } catch (Throwable t) { /* Quench! */ }
        }

        return true;
    }

    /**
     * Locates the file in the jar file of a plugin. Some plugins (*cough* AsyncWorldEdit)
     * inject class loader stuff into the server that breaks getResource("plugin.yml").
     * This method tries to use findResource(), which doesn't suffer from this.
     *
     * @param plugin The plugin to read the file of
     * @param fileName The file name to read
     * @return YAML text content
     * @throws YamlNotReadException If the YAML file contents could not be read
     */
    private static String readPluginYAML(Plugin plugin, String fileName) throws YamlNotReadException {
        java.io.InputStream found_stream = null;
        if (plugin instanceof JavaPlugin) {
            try {
                java.lang.reflect.Method m = JavaPlugin.class.getDeclaredMethod("getClassLoader");
                m.setAccessible(true);
                ClassLoader loader = (ClassLoader) m.invoke(plugin);
                if (loader instanceof java.net.URLClassLoader) {
                    java.net.URL resource = ((java.net.URLClassLoader) loader).findResource(fileName);
                    if (resource != null) {
                        java.net.URLConnection connection = resource.openConnection();
                        connection.setUseCaches(false);
                        found_stream = connection.getInputStream();
                    }
                }
            } catch (Throwable t) { /* ignore, use fallback instead */ }
        }
        if (found_stream == null) {
            try {
                found_stream = plugin.getResource(fileName);
            } catch (Throwable t) {
                throw new YamlNotReadException("Failed to read " + fileName, t);
            }
        }
        if (found_stream == null) {
            throw new YamlNotReadException("Failed to find " + fileName);
        }
        try (java.io.InputStream stream = found_stream) {
            java.io.ByteArrayOutputStream result = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = stream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            return new String(result.toByteArray(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (Throwable t) {
            throw new YamlNotReadException("Failed to read " + fileName, t);
        }
    }

    private static class YamlNotReadException extends IllegalStateException {

        public YamlNotReadException(String message) {
            super(message);
        }

        public YamlNotReadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
