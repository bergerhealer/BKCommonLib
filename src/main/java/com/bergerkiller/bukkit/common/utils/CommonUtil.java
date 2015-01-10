package com.bergerkiller.bukkit.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipEntry;

import net.minecraft.server.v1_8_R1.IPlayerFileData;
import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.CraftServer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.SimplePluginManager;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.StackTraceFilter;
import com.bergerkiller.bukkit.common.config.BasicConfiguration;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;

public class CommonUtil {

    public static final int VIEW = Bukkit.getServer().getViewDistance();
    public static final int VIEWWIDTH = VIEW + VIEW + 1;
    public static final int CHUNKAREA = VIEWWIDTH * VIEWWIDTH;
    public static final int BLOCKVIEW = 32 + (VIEW << 4);
    public static final Thread MAIN_THREAD = Thread.currentThread();
    private static final FieldAccessor<Collection<Plugin>> pluginsField = new SafeField<Collection<Plugin>>(SimplePluginManager.class, "plugins");

    /**
     * Gets a mapping of all commands available on the server, so they can be
     * obtained and executed (dispatched)
     *
     * @return Bukkit command map
     */
    public static CommandMap getCommandMap() {
        return ((CraftServer) Bukkit.getServer()).getCommandMap();
    }

    /**
     * Sends a message to a sender<br>
     * - Empty messages are ignored<br>
     * - Color is stripped from messages to consoles
     *
     * @param sender to send to
     * @param message to send
     */
    public static void sendMessage(Object sender, Object message) {
        if (message != null) {
            String msg = message.toString();
            if (msg.length() > 0 && sender instanceof CommandSender) {
                if (!(sender instanceof Player)) {
                    message = ChatColor.stripColor(msg);
                }
                for (String line : msg.split("\n", -1)) {
                    ((CommandSender) sender).sendMessage(line);
                }
            }
        }
    }

    /**
     * Sends a message containing a list of items
     *
     * @param sender to send to
     * @param delimiter to use between items
     * @param items to send
     */
    public static void sendListMessage(Object sender, String delimiter, Object[] items) {
        String msgpart = null;
        String item;
        for (Object oitem : items) {
            item = oitem.toString();
            // display it
            if (msgpart == null || msgpart.length() + item.length() < 70) {
                if (msgpart == null) {
                    msgpart = item;
                } else {
                    msgpart += ChatColor.WHITE + delimiter + item;
                }
            } else {
                sendMessage(sender, msgpart);
                msgpart = item;
            }
        }
        sendMessage(sender, msgpart);
    }

    /**
     * Calls an Event
     *
     * @param event to call
     * @return the input Event
     */
    public static <T extends Event> T callEvent(T event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    /**
     * Checks whether the event handler list of an event has registered
     * listeners<br>
     * If creating an event causes a performance drain, useless event creation
     * can be avoided this way
     *
     * @param handlerList of the Event
     * @return True if handlers are contained, False if not
     */
    public static boolean hasHandlers(HandlerList handlerList) {
        return handlerList.getRegisteredListeners().length > 0;
    }

    /**
     * Sets the Player File Data used to load and save player information on the
     * server
     *
     * @param playerFileData to set to
     */
    public static void setPlayerFileData(Object playerFileData) {
        CommonNMS.getPlayerList().playerFileData = (IPlayerFileData) playerFileData;
    }

    /**
     * Saves the specified human information to file
     *
     * @param human to save
     */
    public static void savePlayer(HumanEntity human) {
        CommonNMS.getPlayerList().playerFileData.save(CommonNMS.getNative(human));
    }

    /**
     * Saves all player information to file
     */
    public static void savePlayers() {
        CommonNMS.getPlayerList().savePlayers();
    }

    /**
     * Shuffle an array of type T
     *
     * @param array to be shuffled
     */
    public static <T> void shuffle(T[] array) {
        int random;
        for (int i = 1; i < array.length; i++) {
            random = (int) (Math.random() * i);
            T temp = array[i - 1];
            array[i - 1] = array[random];
            array[random] = temp;
        }
    }

    /**
     * Add a Set to another Set
     *
     * @param to Set that receives orther set
     * @param from Set to be added to the orther set
     * @return new Set
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Set addAllToSet(Set to, Set from) {
        for (Object i : from) {
            to.add(i);
        }

        return to;
    }

    /**
     * Gets a list of online players on the server
     *
     * @return online players
     */
    public static Collection<Player> getOnlinePlayers() {
        return ConversionPairs.player.convertAll(CommonNMS.getPlayerList().players);
    }

    /**
     * Checks whether a given command sender has a given permission<br>
     * Vault is used for permissions if available, otherwise super permissions
     * are used
     *
     * @param sender to check
     * @param permissionNode to check (each part is appended with '.' in
     * between)
     * @return True if the sender has permission for the node, False if not
     */
    public static boolean hasPermission(CommandSender sender, String[] permissionNode) {
        return CommonPlugin.getInstance().getPermissionHandler().hasPermission(sender, permissionNode);
    }

    /**
     * Get the game profile from a name.
     *
     * @param name to get game profile from.
     * @return Player's GameProfile, OfflinePlayer profile used if player not
     * found.
     */
    public static GameProfile getGameProfile(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            //The right way
            return PlayerUtil.getGameProfile(player);
        }

        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
        return new GameProfile(uuid, name);
    }

    /**
     * Checks whether a given command sender has a given permission<br>
     * Vault is used for permissions if available, otherwise super permissions
     * are used
     *
     * @param sender to check
     * @param permissionNode to check
     * @return True if the sender has permission for the node, False if not
     */
    public static boolean hasPermission(CommandSender sender, String permissionNode) {
        return CommonPlugin.getInstance().getPermissionHandler().hasPermission(sender, permissionNode);
    }

    /**
     * Prints the filtered (using the SERVER filter) stack trace of an error
     *
     * @param error to print
     */
    public static void printFilteredStackTrace(Throwable error) {
        StackTraceFilter.SERVER.print(error);
    }

    /**
     * Prints the filtered (using the SERVER filter) stack trace of an error
     *
     * @param error to print
     * @param level of the error
     */
    public static void printFilteredStackTrace(Throwable error, Level level) {
        StackTraceFilter.SERVER.print(error, level);
    }

    /**
     * Removes all stack trace elements after a given method
     *
     * @param elements to filter
     * @param className to filter from
     * @param methodName to filter from
     * @return Filtered stack trace
     */
    public static StackTraceElement[] filterStackTrace(StackTraceElement[] elements, String className, String methodName) {
        ArrayList<StackTraceElement> rval = new ArrayList<StackTraceElement>(elements.length - 1);
        for (StackTraceElement elem : elements) {
            if (elem.getClassName().equals(className) && elem.getMethodName().equals(methodName)) {
                break;
            } else {
                rval.add(elem);
            }
        }
        return rval.toArray(new StackTraceElement[0]);
    }

    /**
     * Checks all plugin files to check if a plugin file exists
     *
     * @param name of the plugin
     * @return Plugin exists?
     */
    public static boolean isPluginInDirectory(String name) {
        File dir = new File("plugins");
        if (!dir.exists()) {
            return false;
        }
        for (File file : dir.listFiles()) {
            try {
                BasicConfiguration config = getPluginConfiguration(file, "plugin.yml");
                String pluginName = config.get("name", String.class);
                if (name.equals(pluginName)) {
                    return true;
                }
            } catch (IOException e) {
            }
        }
        return false;
    }

    /**
     * Obtains a new YAML Configuration instance for a yaml file contained
     * within a plugin Jar file
     *
     * @param pluginJarFile to get a YAML configuration instance of
     * @param configResourcePath to the configuration resource
     * @return a new Configuration instance, filled with data from the YAML file
     * @throws IOException if loading fails or the file can not be found
     */
    public static BasicConfiguration getPluginConfiguration(File pluginJarFile, String configResourcePath) throws IOException {
        InputStream stream = getPluginResource(pluginJarFile, configResourcePath);
        try {
            BasicConfiguration config = new BasicConfiguration();
            try {
                config.loadFromStream(stream);
            } catch (Throwable t) {
                throw new IOException("Error in YAML format", t);
            }
            return config;
        } finally {
            stream.close();
        }
    }

    /**
     * Obtains a new InputStream to the resource found in the given Plugin Jar
     * file
     *
     * @param pluginJarFile to get a resource of
     * @param resourcePath to the resource
     * @return an InputStream to the resource
     * @throws IOException if loading fails or the file can not be found
     */
    public static InputStream getPluginResource(File pluginJarFile, String resourcePath) throws IOException {
		// First, find the plugin by Jar file (avoids Jar File decompression times)
        // Then we can use the ClassLoader of this Jar File to load the resource instead
        synchronized (Bukkit.getPluginManager()) {
            for (Plugin plugin : CommonUtil.getPluginsUnsafe()) {
                File file = getPluginJarFile(plugin);
                if (pluginJarFile.equals(file)) {
                    InputStream stream = plugin.getResource(resourcePath);
                    if (stream == null) {
                        throw new IOException("Resource not found: " + resourcePath);
                    }
                    return stream;
                }
            }
        }

        // Not found, stick to reading the JarFile ourselves
        final JarFile jarFile = new JarFile(pluginJarFile);
        final ZipEntry entry = jarFile.getEntry(resourcePath);
        if (entry == null) {
            jarFile.close();
            throw new IOException("Resource not found: " + resourcePath);
        }

        return jarFile.getInputStream(entry);
    }

    /**
     * Gets the Jar File where a given Plugin is loaded from. If the plugin is
     * not in a Jar file, null is returned instead.
     *
     * @param plugin to get the Jar File of
     * @return the Jar File in which the plugin resides, or null if none found
     */
    public static File getPluginJarFile(Plugin plugin) {
        final Class<?> pluginClass = plugin.getClass();
        try {
            URI uri = pluginClass.getProtectionDomain().getCodeSource().getLocation().toURI();
            File file = new File(uri);
            if (file.exists()) {
                return file;
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Obtains the folder in which plugin-specific information is contained.<br>
     * Usually this folder is <b>/plugins/[pluginname]</b>.<br>
     * This method can be used to properly obtain this folder if the plugin is
     * not initialized yet.
     *
     * @param plugin to get the data folder of
     * @return Plugin data folder (never null)
     */
    public static File getPluginDataFolder(Plugin plugin) {
        File folder = plugin.getDataFolder();
        if (folder == null) {
            File jarFile = getPluginJarFile(plugin);
            if (jarFile == null) {
                throw new RuntimeException("Plugin data folder can not be obtained: Not a valid JAR plugin");
            }
            folder = new File(jarFile.getAbsoluteFile().getParentFile(), plugin.getName());
        }
        return folder;
    }

    /**
     * Removes all stack trace elements after a given method from an error
     *
     * @param error to filter
     * @param className to filter from
     * @param methodName to filter from
     * @return The input error (uncloned, same instance)
     */
    public static <T extends Throwable> T filterStackTrace(T error, String className, String methodName) {
        error.setStackTrace(filterStackTrace(error.getStackTrace(), className, methodName));
        return error;
    }

    /**
     * Removes all stack trace elements after the method that called this
     * function
     *
     * @param elements to filter
     * @return Filtered stack trace
     */
    public static StackTraceElement[] filterStackTrace(StackTraceElement[] elements) {
        // Obtain the calling method and class name
        StackTraceElement[] currStack = Thread.currentThread().getStackTrace();
        for (int i = 1; i < currStack.length; i++) {
            if (!currStack[i].getClassName().equals(CommonUtil.class.getName()) || !currStack[i].getMethodName().equals("filterStackTrace")) {
                return filterStackTrace(elements, currStack[i + 1].getClassName(), currStack[i + 1].getMethodName());
            }
        }
        return elements;
    }

    /**
     * Removes all stack trace elements after the method that called this
     * function from an error
     *
     * @param error to filter
     * @return The input error (uncloned, same instance)
     */
    public static <T extends Throwable> T filterStackTrace(T error) {
        error.setStackTrace(filterStackTrace(error.getStackTrace()));
        return error;
    }

    /**
     * Performs an unsafe cast to a generic type. Be sure to check that the
     * object being cast is actually an instance of the type to cast to. Casting
     * errors will occur while working with the resulting value, not while
     * performing the casting in this method.
     *
     * @param value to cast
     * @return value cast in an unsafe way
     */
    @SuppressWarnings("unchecked")
    public static <T> T unsafeCast(Object value) {
        return (T) value;
    }

    /**
     * Tries to cast the object to the type specified, returning null upon
     * failure
     *
     * @param object to cast
     * @param type to cast to
     * @return The cast object, or null
     */
    public static <T> T tryCast(Object object, Class<T> type) {
        return tryCast(object, type, null);
    }

    /**
     * Tries to cast the object to the type specified, returning def upon
     * failure
     *
     * @param object to cast
     * @param type to cast to
     * @param def to return on cast failure
     * @return The cast object, or def
     */
    public static <T> T tryCast(Object object, Class<T> type, T def) {
        if (type.isInstance(object)) {
            return type.cast(object);
        } else {
            return def;
        }
    }

    /**
     * Schedules a runnable to execute the next Tick<br>
     * The BKCommonLib internal plugin will handle this task<br>
     * This method is thread safe
     *
     * @param runnable to execute
     */
    public static void nextTick(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (CommonPlugin.hasInstance()) {
            // Use BKCommonLib next tick task
            CommonPlugin.getInstance().nextTick(runnable);
        } else {
            // Try to find out what plugin this Runnable belongs to
            Plugin plugin = CommonUtil.getPluginByClass(runnable.getClass());
            if (plugin == null) {
				// Well...ain't that a pickle.
                // Maybe there is some other plugin we can dump this to?
                // It's a fallback...it does not have to be fair or perfect!
                synchronized (Bukkit.getPluginManager()) {
                    Iterator<Plugin> iter = getPluginsUnsafe().iterator();
                    while (iter.hasNext()) {
                        plugin = iter.next();
                        if (plugin.isEnabled()) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable);
                            return;
                        }
                    }
                }
                // Well now we just don't know...
                CommonPlugin.LOGGER.log(Level.SEVERE, "Unable to properly schedule next-tick task: " + runnable.getClass().getName());
                CommonPlugin.LOGGER.log(Level.SEVERE, "The task is executed right away instead...we might recover!");
                runnable.run();
            } else {
                // Use the supposed plugin this Class belongs to
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable);
            }
        }
    }

    /**
     * Broadcasts a message to all players on a world
     *
     * @param message to send
     */
    public static void broadcast(Object message) {
        if (message != null) {
            for (Player player : getOnlinePlayers()) {
                player.sendMessage(message.toString());
            }
        }
    }

    /**
     * Gets all Plugins running on the server WITHOUT allocating a new array if
     * possible. If there is no performance requirement to avoid array
     * allocation, use {@link #getPlugins()} instead. Only use this method
     * inside a <b>synchronized</b> body around the Plugin Manager, for example:
     * <pre>
     * synchronized (Bukkit.getPluginManager()) {
     * 	for (Plugin plugin : CommonUtil.getPluginsUnsafe()) {
     *  		System.out.println(plugin.getName());
     * 	}
     * }
     * </pre>
     *
     * @return unsafe collection of plugins running on the server
     */
    public static Collection<Plugin> getPluginsUnsafe() {
        final PluginManager man = Bukkit.getPluginManager();
        if (man instanceof SimplePluginManager) {
            return pluginsField.get(man);
        } else {
            return Arrays.asList(man.getPlugins());
        }
    }

    /**
     * Gets all the Plugins running on the Server
     *
     * @return Plugins
     */
    public static Plugin[] getPlugins() {
        return Bukkit.getPluginManager().getPlugins();
    }

    /**
     * Checks whether the plugin of the given name is enabled on the Server
     *
     * @param name to check
     * @return True if the plugin is enabled, False if not
     */
    public static boolean isPluginEnabled(String name) {
        return Bukkit.getPluginManager().isPluginEnabled(name);
    }

    /**
     * Gets a certain Plugin by name
     *
     * @param name of the Plugin
     * @return Plugin
     */
    public static Plugin getPlugin(String name) {
        return Bukkit.getPluginManager().getPlugin(name);
    }

    /**
     * Gets the plugin by inspecting a Class
     *
     * @param clazz
     * @return the Plugin matching the Class, or null if not found
     */
    public static Plugin getPluginByClass(Class<?> clazz) {
        ClassLoader loader = clazz.getClassLoader();
        synchronized (Bukkit.getServer().getPluginManager()) {
            for (Plugin plugin : getPluginsUnsafe()) {
                if (plugin.getClass().getClassLoader() == loader) {
                    return plugin;
                }
            }
        }
        return null;
    }

    /**
     * Gets the plugin by inspecting the path to a Class
     *
     * @param classPath of the Class
     * @return the Plugin matching the Class, or null if not found
     */
    public static Plugin getPluginByClass(String classPath) {
        try {
            return getPluginByClass(Class.forName(classPath));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Obtains the package path of a given Class Path
     *
     * @param classPath
     * @return package path of the Package the class resides in
     */
    public static String getPackagePath(String classPath) {
        final int idx = classPath.lastIndexOf('.');
        return idx == -1 ? "" : classPath.substring(0, idx);
    }

    /**
     * Finds all plugins involved in an array of stack trace elements.
     *
     * @param stackTrace to find the plugins for
     * @return array of plugins mentioned in the Stack Trace
     */
    public static Plugin[] findPlugins(StackTraceElement[] stackTrace) {
        return findPlugins(Arrays.asList(stackTrace));
    }

    /**
     * Finds all plugins involved in a list of stack trace elements.
     *
     * @param stackTrace to find the plugins for
     * @return array of plugins mentioned in the Stack Trace
     */
    public static Plugin[] findPlugins(List<StackTraceElement> stackTrace) {
        LinkedHashSet<Plugin> found = new LinkedHashSet<Plugin>(3);
        for (StackTraceElement elem : stackTrace) {
            Plugin plugin = getPluginByClass(elem.getClassName());
            if (plugin != null) {
                found.add(plugin);
            }
        }
        return found.toArray(new Plugin[0]);
    }

    /**
     * Tries to get the class at the path specified and applies translations
     * based on the server currently running.
     *
     * @param path to the class
     * @return the class, or null if not found
     */
    public static Class<?> getClass(String path) {
        try {
            if (Common.SERVER == null) {
                return Class.forName(path);
            } else {
                return Class.forName(Common.SERVER.getClassName(path));
            }
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Tries to get the net.minecraft.server class at the path specified
     *
     * @param name of the NMS class
     * @return the class, or null if not found
     */
    public static Class<?> getNMSClass(String name) {
        return getClass(Common.NMS_ROOT + "." + name);
    }

    /**
     * Tries to get the org.bukkit.craftbukkit class at the path specified
     *
     * @param name of the CB class
     * @return the class, or null if not found
     */
    public static Class<?> getCBClass(String name) {
        return getClass(Common.CB_ROOT + "." + name);
    }

    /**
     * Checks whether a plugin is soft-depending on another plugin
     *
     * @param plugin
     * @param depending plugin
     * @return True if plugin soft-depends on the depending plugin, False if not
     */
    public static boolean isSoftDepending(Plugin plugin, Plugin depending) {
        final List<String> dep = plugin.getDescription().getSoftDepend();
        return !LogicUtil.nullOrEmpty(dep) && dep.contains(depending.getName());
    }

    /**
     * Checks whether a plugin is depending on another plugin
     *
     * @param plugin
     * @param depending plugin
     * @return True if plugin depends on the depending plugin, False if not
     */
    public static boolean isDepending(Plugin plugin, Plugin depending) {
        final List<String> dep = plugin.getDescription().getDepend();
        return !LogicUtil.nullOrEmpty(dep) && dep.contains(depending.getName());
    }

    /**
     * Gets constants of the class type statically defined in the class itself.
     * If the class is an enum, the enumeration constants are returned.
     * Otherwise, only the static fields with theClass type are returned.
     *
     * @param theClass to get the class constants of
     * @return class constants defined in class 'theClass'
     */
    public static <T> T[] getClassConstants(Class<T> theClass) {
        return getClassConstants(theClass, theClass);
    }

    /**
     * Gets constants of the class type statically defined in the class itself.
     * If the type class is an enum, the enumeration constants are returned.
     * Otherwise, only the static fields with the same type as the type
     * parameter are returned.
     *
     * @param theClass to get the class constants of
     * @param type of constants to return from theClass
     * @return class constants defined in class 'theClass'
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] getClassConstants(Class<?> theClass, Class<T> type) {
        if (type.isEnum()) {
            // Get using enum constants
            return type.getEnumConstants();
        } else {
            // Get using reflection
            try {
                Field[] declaredFields = theClass.getDeclaredFields();
                ArrayList<T> constants = new ArrayList<T>(declaredFields.length);
                for (Field field : declaredFields) {
                    if (Modifier.isStatic(field.getModifiers()) && type.isAssignableFrom(field.getType())) {
                        T constant = (T) field.get(null);
                        if (constant != null) {
                            constants.add(constant);
                        }
                    }
                }
                return LogicUtil.toArray(constants, type);
            } catch (Throwable t) {
                t.printStackTrace();
                return LogicUtil.createArray(type, 0);
            }
        }
    }

    /**
     * Checks whether a given value is an instance of one of the types specified
     *
     * @param value to check
     * @param types to check against
     * @return True if value is an instance of one of the types, False if not
     */
    public static boolean isInstance(Object value, Class<?>... types) {
        for (Class<?> type : types) {
            if (type.isInstance(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the server has officially started running and accepting
     * new players to join.
     *
     * @return True if the server started, False if not and the server is still
     * enabling
     */
    public static boolean isServerStarted() {
        return CommonPlugin.hasInstance() && CommonPlugin.getInstance().isServerStarted();
    }

    /**
     * Obtains the Handler List of an event, throwing an exception if this is
     * not possible
     *
     * @param eventClass to get the HandlerList of
     * @return the HandlerList
     * @throws RuntimeException: Event class has no handler list
     */
    public static HandlerList getEventHandlerList(Class<?> eventClass) {
        try {
            return (HandlerList) eventClass.getDeclaredMethod("getHandlerList").invoke(null);
        } catch (Throwable t) {
        }
        throw new RuntimeException("Class '" + eventClass.getName() + "' does not have a handler list");
    }

    /**
     * Re-orders the event registration to ensure that the listener MONITOR
     * handler is called last, after all other listener handlers have executed.
     * This should only be used if information is changed that could cause other
     * listeners after this handler to malfunction.<br>
     * <br>
     * This method may fail if more than one Monitor handler per Event class is
     * present in a single Listener.
     *
     * @param listener of the event handler
     * @param eventClass that is handled
     */
    public static void queueListenerLast(Listener listener, Class<?> eventClass) {
        setListenerOrder(listener, eventClass, false);
    }

    /**
     * Re-orders the event registration to ensure that the listener LOWEST
     * handler is called first, before all other listener handlers have
     * executed. This should only be used if information is changed that could
     * cause other listeners before this handler to malfunction.<br>
     * This method may fail if more than one Lowest handler per Event class is
     * present in a single Listener.
     *
     * @param listener of the event handler
     * @param eventClass that is handled
     */
    public static void queueListenerFirst(Listener listener, Class<?> eventClass) {
        setListenerOrder(listener, eventClass, true);
    }

    private static void setListenerOrder(Listener listener, Class<?> eventClass, boolean first) {
        HandlerList list = getEventHandlerList(eventClass);
        final EventPriority prio = first ? EventPriority.LOWEST : EventPriority.MONITOR;
        synchronized (list) {
            EnumMap<EventPriority, ArrayList<RegisteredListener>> handlerSlots = SafeField.get(list, "handlerslots");
            ArrayList<RegisteredListener> registeredListenerList = handlerSlots.get(prio);
            int requestedIndex = first ? 0 : (registeredListenerList.size() - 1);

            // Try to find the registered listener
            for (int i = 0; i < registeredListenerList.size(); i++) {
                RegisteredListener registeredListener = registeredListenerList.get(i);
                // Check that the Listener matches
                if (registeredListener.getListener() != listener) {
                    continue;
                } else if (i == requestedIndex) {
                    // Already in order, do not do anything
                    return;
                }
                RegisteredListener[] allListeners = list.getRegisteredListeners();

                // Change order in list
                registeredListenerList.remove(i);
                registeredListenerList.add(requestedIndex, registeredListener);

                // Get the index of the listener in the baked array
                ArrayList<RegisteredListener> newListeners = new ArrayList<RegisteredListener>(allListeners.length);
                if (first) {
                    newListeners.add(registeredListener);
                    for (RegisteredListener otherListener : allListeners) {
                        if (otherListener != registeredListener) {
                            newListeners.add(otherListener);
                        }
                    }
                } else {
                    for (RegisteredListener otherListener : allListeners) {
                        if (otherListener != registeredListener) {
                            newListeners.add(otherListener);
                        }
                    }
                    newListeners.add(registeredListener);
                }
                // Transfer the listeners over
                if (newListeners.toArray(allListeners) != allListeners) {
                    // Strange failure, ensure a bake
                    list.bake();
                }
                return;
            }
        }
    }

    /**
     * Checks whether a certain method is overrided in a class
     *
     * @param baseClass the method is defined in
     * @param typeInstance to check whether it overrides in the base class
     * @param methodName of the method
     * @param parameterTypes of the method
     * @return True if overrided, False if not
     */
    public static boolean isMethodOverrided(Class<?> baseClass, Object typeInstance, String methodName, Class<?>... parameterTypes) {
        return isMethodOverrided(baseClass, typeInstance.getClass(), methodName, parameterTypes);
    }

    /**
     * Checks whether a certain method is overrided in a class
     *
     * @param baseClass the method is defined in
     * @param type to check whether it overrides in the base class
     * @param methodName of the method
     * @param parameterTypes of the method
     * @return True if overrided, False if not
     */
    public static boolean isMethodOverrided(Class<?> baseClass, Class<?> type, String methodName, Class<?>... parameterTypes) {
        return new SafeMethod<Void>(baseClass, methodName, parameterTypes).isOverridedIn(type);
    }
}
