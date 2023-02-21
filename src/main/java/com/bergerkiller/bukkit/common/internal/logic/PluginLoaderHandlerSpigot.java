package com.bergerkiller.bukkit.common.internal.logic;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * Default handler for Spigot servers, and older Paper servers that don't
 * yet support the paper plugin loader system.
 */
class PluginLoaderHandlerSpigot extends PluginLoaderHandler {
    private final List<String> classDependPlugins;

    public PluginLoaderHandlerSpigot(Plugin plugin, String pluginConfigText) {
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
        // Handle the plugin-provides logic by forwarding the access check
        silenceClassLoadWarningsFromProvides();

        // Right-away, grant access to all class-depend plugins
        // On spigot this can thankfully be done
        addAccessToClassloaders(this.classDependPlugins);
    }

    @Override
    public void onPluginLoaded(Plugin plugin) {
        // Nothing to do here, already handled on startup
    }

    @Override
    public void addAccessToClassloader(Plugin plugin) {
        addAccessToClassloaders(Collections.singletonList(plugin.getName()));
    }

    @Override
    public void addAccessToClassloaders(List<String> pluginNames) {
        // Check whether the 'seenIllegalAccess' Set exists
        try {
            java.lang.reflect.Field seenIllegalAccessField = this.getClassLoader().getClass().getDeclaredField("seenIllegalAccess");
            if (!seenIllegalAccessField.getType().equals(Set.class)) {
                return; // Not supported. Boo!
            }

            seenIllegalAccessField.setAccessible(true);
            final Set<String> seenIllegalAccess = (Set<String>) seenIllegalAccessField.get(this.getClassLoader());

            // Add all values configured to the seenIllegalAccess Set up-front
            seenIllegalAccess.addAll(pluginNames);
        } catch (Throwable t) {
        }
    }

    /**
     * If another 'seen illegal access' is set for the plugin name a plugin
     * provides, automatically no longer see all the illegal access for the
     * providing plugin, either.
     */
    private void silenceClassLoadWarningsFromProvides() {
        if (!hasProvidesMethod) {
            return;
        }

        try {
            java.lang.reflect.Field seenIllegalAccessField = this.getClassLoader().getClass().getDeclaredField("seenIllegalAccess");
            if (!seenIllegalAccessField.getType().equals(Set.class)) {
                return; // Not supported. Boo!
            }

            seenIllegalAccessField.setAccessible(true);
            final Set<String> seenIllegalAccess = (Set<String>) seenIllegalAccessField.get(this.getClassLoader());

            // Got to hook the Set as well to properly handle provides during contains()
            Set<String> hook = new AbstractSet<String>() {
                @Override
                public boolean contains(Object o) {
                    if (seenIllegalAccess.contains(o))
                        return true;

                    Plugin pluginByName;
                    if (o instanceof String && (pluginByName = Bukkit.getPluginManager().getPlugin((String) o)) != null) {
                        for (String provides : providesMethod.apply(pluginByName)) {
                            if (seenIllegalAccess.contains(provides)) {
                                seenIllegalAccess.add((String) o);
                                return true;
                            }
                        }
                    }

                    return false;
                }

                @Override
                public boolean add(String value) {
                    return seenIllegalAccess.add(value);
                }

                @Override
                public boolean remove(Object value) {
                    return seenIllegalAccess.remove(value);
                }

                @Override
                public Iterator<String> iterator() {
                    return seenIllegalAccess.iterator();
                }

                @Override
                public int size() {
                    return seenIllegalAccess.size();
                }

                @Override
                public void clear() {
                    seenIllegalAccess.clear();
                }
            };
            seenIllegalAccessField.set(this.getClassLoader(), hook);
        } catch (Throwable t) {
        }
    }

    // This crap is needed to support older versions of the server
    private static final boolean hasProvidesMethod;
    private static Function<Plugin, List<String>> providesMethod;
    static {
        java.lang.reflect.Method getProvidesMethodTmp = null;
        try {
            getProvidesMethodTmp = PluginDescriptionFile.class.getMethod("getProvides");
        } catch (Throwable t) {
        }
        hasProvidesMethod = (getProvidesMethodTmp != null);
        if (hasProvidesMethod) {
            final java.lang.reflect.Method getProvidesMethod = getProvidesMethodTmp;
            providesMethod = p -> {
                try {
                    return (List<String>) getProvidesMethod.invoke(p.getDescription());
                } catch (Throwable t) {
                    // Egh.
                    Bukkit.getLogger().log(Level.SEVERE, "Error checking provides list", t);
                    return Collections.emptyList();
                }
            };
        } else {
            providesMethod = p -> Collections.emptyList();
        }
    }
}
