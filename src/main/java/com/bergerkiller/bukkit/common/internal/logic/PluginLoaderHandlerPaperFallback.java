package com.bergerkiller.bukkit.common.internal.logic;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Calls into paper's plugin loader API to support the legacy <i>classdepend</i>
 * syntax. Plugins should ideally switch to including a <i>paper-plugin.yml</i>
 * of their own, in which case this class isn't used.
 */
class PluginLoaderHandlerPaperFallback extends PluginLoaderHandler {
    private final List<String> classDependPlugins;

    public PluginLoaderHandlerPaperFallback(Plugin plugin, String pluginConfigText) {
        super(plugin, pluginConfigText);

        // Get a List of plugins class-depended upon
        {
            List<String> classDepend = this.pluginConfig.getStringList("classdepend");
            if (classDepend == null) {
                classDepend = Collections.emptyList();
            }
            this.classDependPlugins = classDepend;
        }
    }

    @Override
    public void bootstrap() {
        addAccessToClassloaders(classDependPlugins);
    }

    @Override
    public void onPluginLoaded(Plugin plugin) {
        // Check something to do
        if (classDependPlugins.isEmpty()) {
            return;
        }

        // Check name
        if (classDependPlugins.contains(plugin.getName())) {
            addAccessToClassloader(plugin);
            return;
        }

        // Check provides
        for (String provides : plugin.getDescription().getProvides()) {
            if (classDependPlugins.contains(provides)) {
                addAccessToClassloader(plugin);
                return;
            }
        }
    }

    @Override
    public void addAccessToClassloader(Plugin plugin) {
        addPluginClassLoader(plugin.getClass().getClassLoader());
    }

    @Override
    public void addAccessToClassloaders(List<String> pluginNames) {
        try {
            // All this to find a List of all class loaders available on the server...
            Class<?> paperClassLoaderStorageType = Class.forName("io.papermc.paper.plugin.provider.classloader.PaperClassLoaderStorage");
            Object paperClassLoaderStorage = paperClassLoaderStorageType.getMethod("instance").invoke(null);
            Object globalGroup = paperClassLoaderStorage.getClass().getMethod("getGlobalGroup").invoke(paperClassLoaderStorage);
            globalGroup = unwrapLockingClassLoader(globalGroup);
            Class<?> simpleListPluginGroupType = Class.forName("io.papermc.paper.plugin.entrypoint.classloader.group.SimpleListPluginClassLoaderGroup");
            java.lang.reflect.Method getClassLoadersMethod = simpleListPluginGroupType.getMethod("getClassLoaders");
            List<Object> classloaders = (List<Object>) getClassLoadersMethod.invoke(globalGroup);

            // These we want to collect
            ArrayList<Object> loadersToAdd = new ArrayList<>();

            // For all loaders, see if they are part of the depend list of provide one of the depend lists
            Class<?> configuredPluginClassLoaderType = Class.forName("io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader");
            java.lang.reflect.Method pluginMetaMethod = configuredPluginClassLoaderType.getMethod("getConfiguration");
            Class<?> pluginMetaType = Class.forName("io.papermc.paper.plugin.configuration.PluginMeta");
            java.lang.reflect.Method pluginMetaGetNameMethod = pluginMetaType.getMethod("getName");
            java.lang.reflect.Method pluginMetaGetProvidesMethod = pluginMetaType.getMethod("getProvidedPlugins");
            for (Object loader : classloaders) {
                Object config = pluginMetaMethod.invoke(loader);

                // Decode name, check if we depend
                String name = (String) pluginMetaGetNameMethod.invoke(config);
                if (pluginNames.contains(name)) {
                    loadersToAdd.add(loader);
                    continue;
                }

                // Decode provides, check any of them are contained
                List<String> provides = (List<String>) pluginMetaGetProvidesMethod.invoke(config);
                if (provides != null && !provides.isEmpty()) {
                    boolean hasProvideDependedOn = false;
                    for (String provide : provides) {
                        if (pluginNames.contains(provide)) {
                            hasProvideDependedOn = true;
                            break;
                        }
                    }
                    if (hasProvideDependedOn) {
                        loadersToAdd.add(loader);
                        continue;
                    }
                }
            }

            // Add all already-found loaders to this plugin's group
            loadersToAdd.forEach(this::addPluginClassLoader);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, "Failed to grant access to dependency classes using Paper API", t);
        }
    }

    private void addPluginClassLoader(Object configuredPluginClassLoader) {
        try {
            // Check applicable
            Class<?> configuredPluginClassLoaderType = Class.forName("io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader");
            if (!configuredPluginClassLoaderType.isInstance(configuredPluginClassLoader)) {
                throw new IllegalStateException("Invalid plugin class loader type: " + configuredPluginClassLoader.getClass());
            }

            ClassLoader loader = this.getClassLoader();

            // Retrieve this plugin's Plugin Class Loader group
            // io.papermc.paper.plugin.provider.classloader.PluginClassLoaderGroup
            Class<?> pluginClassLoaderType = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
            Class<?> paperPluginLoaderType = Class.forName("io.papermc.paper.plugin.entrypoint.classloader.PaperPluginClassLoader");
            Object pluginClassLoaderGroup;
            if (pluginClassLoaderType.isInstance(loader)) {
                java.lang.reflect.Field classLoaderGroupField = pluginClassLoaderType.getDeclaredField("classLoaderGroup");
                classLoaderGroupField.setAccessible(true);
                pluginClassLoaderGroup = classLoaderGroupField.get(loader);
            } else if (paperPluginLoaderType.isInstance(loader)) {
                java.lang.reflect.Field classLoaderGroupField = paperPluginLoaderType.getDeclaredField("group");
                classLoaderGroupField.setAccessible(true);
                pluginClassLoaderGroup = classLoaderGroupField.get(loader);
            } else {
                throw new IllegalStateException("Unknown loader type: " + loader.getClass().getName());
            }

            // Ugh
            pluginClassLoaderGroup = unwrapLockingClassLoader(pluginClassLoaderGroup);

            // Check this group is a SimpleListPluginClassLoaderGroup
            Class<?> simpleListPluginGroupType = Class.forName("io.papermc.paper.plugin.entrypoint.classloader.group.SimpleListPluginClassLoaderGroup");
            if (!simpleListPluginGroupType.isInstance(pluginClassLoaderGroup)) {
                throw new IllegalStateException("Unsupported class loader group type: " + pluginClassLoaderGroup.getClass().getName());
            }

            // Check not already contained in the group
            java.lang.reflect.Method getClassLoadersMethod = simpleListPluginGroupType.getMethod("getClassLoaders");
            List<Object> existingLoaders = (List<Object>) getClassLoadersMethod.invoke(pluginClassLoaderGroup);
            if (existingLoaders.contains(configuredPluginClassLoader)) {
                return;
            }

            // Add the loader
            java.lang.reflect.Method addMethod = simpleListPluginGroupType.getMethod("add", configuredPluginClassLoaderType);
            addMethod.invoke(pluginClassLoaderGroup, configuredPluginClassLoader);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, "Failed to grant access to dependency classes using Paper API", t);
        }
    }

    private Object unwrapLockingClassLoader(Object loader) {
        try {
            Class<?> lockingType = Class.forName("io.papermc.paper.plugin.entrypoint.classloader.group.LockingClassLoaderGroup");
            if (lockingType.isInstance(loader)) {
                return lockingType.getMethod("getParent").invoke(loader);
            }
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, "Failed to unwrap LockingClassLoaderGroup", t);
        }
        return loader;
    }
}
