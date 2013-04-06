package com.bergerkiller.bukkit.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import net.minecraft.server.v1_5_R2.EntityHuman;
import net.minecraft.server.v1_5_R2.MinecraftServer;
import net.minecraft.server.v1_5_R2.IPlayerFileData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.StackTraceFilter;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingList;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class CommonUtil {
	public static final int VIEW = Bukkit.getServer().getViewDistance();
	public static final int VIEWWIDTH = VIEW + VIEW + 1;
	public static final int CHUNKAREA = VIEWWIDTH * VIEWWIDTH;
	public static final int BLOCKVIEW = 32 + (VIEW << 4);
	public static final Thread MAIN_THREAD = Thread.currentThread();
	private static final FieldAccessor<Collection<Plugin>> pluginsField = new SafeField<Collection<Plugin>>(SimplePluginManager.class, "plugins");

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
	 * Checks whether the event handler list of an event has registered listeners<br>
	 * If creating an event causes a performance drain, useless event creation can be avoided this way
	 * 
	 * @param handlerList of the Event
	 * @return True if handlers are contained, False if not
	 */
	public static boolean hasHandlers(HandlerList handlerList) {
		return handlerList.getRegisteredListeners().length > 0;
	}

	/**
	 * Gets the native Minecraft Server which contains the main logic
	 * 
	 * @return Minecraft Server
	 */
	public static MinecraftServer getMCServer() {
		return getCraftServer().getServer();
	}

	/**
	 * Gets the Craft server
	 * 
	 * @return Craft server
	 */
	public static CraftServer getCraftServer() {
		return (CraftServer) Bukkit.getServer();
	}

	/**
	 * Gets the Player File Data used to load and save player information on the server
	 * 
	 * @return current Player File Data instance
	 */
	public static Object getPlayerFileData() {
		return getCraftServer().getHandle().playerFileData;
	}

	/**
	 * Sets the Player File Data used to load and save player information on the server
	 * 
	 * @param playerFileData to set to
	 */
	public static void setPlayerFileData(Object playerFileData) {
		getCraftServer().getHandle().playerFileData = (IPlayerFileData) playerFileData;
	}

	/**
	 * Saves the specified human information to file
	 * 
	 * @param human to save
	 */
	public static void savePlayer(HumanEntity human) {
		getCraftServer().getHandle().playerFileData.save((EntityHuman) Conversion.toEntityHandle.convert(human));
	}

	/**
	 * Saves all player information to file
	 */
	public static void savePlayers() {
		getCraftServer().getHandle().savePlayers();
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Set addAllToSet(Set to, Set from) {
		for(Object i : from) {
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
		return new ConvertingList<Player>(CommonUtil.getCraftServer().getHandle().players, ConversionPairs.player);
	}

	/**
	 * Checks whether a given command sender has a given permission<br>
	 * Vault is used for permissions if available, otherwise super permissions are used
	 * 
	 * @param sender to check
	 * @param permissionNode to check
	 * @return True if the sender has permission for the node, False if not
	 */
	public static boolean hasPermission(CommandSender sender, String permissionNode) {
		return CommonPlugin.getInstance().hasPermission(sender, permissionNode);
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
	 * Removes all stack trace elements after the method that called this function
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
	 * Removes all stack trace elements after the method that called this function from an error
	 * 
	 * @param error to filter
	 * @return The input error (uncloned, same instance)
	 */
	public static <T extends Throwable> T filterStackTrace(T error) {
		error.setStackTrace(filterStackTrace(error.getStackTrace()));
		return error;
	}

	/**
	 * Tries to cast the object to the type specified, returning null upon failure
	 * 
	 * @param object to cast
	 * @param type to cast to
	 * @return The cast object, or null
	 */
	public static <T> T tryCast(Object object, Class<T> type) {
		return tryCast(object, type, null);
	}

	/**
	 * Tries to cast the object to the type specified, returning def upon failure
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
		CommonPlugin.getInstance().nextTick(runnable);
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
	 * Gets all Plugins running on the server WITHOUT allocating a new array if possible.
	 * If there is no performance requirement to avoid array allocation, use {@link getPlugins()} instead.
	 * Only use this method inside a <b>synchronized</b> body around the Plugin Manager, for example:
	 * <pre>
	 * synchronized (Bukkit.getServer().getPluginManager()) {
	 * 	for (Plugin plugin : CommonUtil.getPluginsUnsafe()) {
	 *  		System.out.println(plugin.getName());
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @return unsafe collection of plugins running on the server
	 */
	public static Collection<Plugin> getPluginsUnsafe() {
		final PluginManager man = Bukkit.getServer().getPluginManager();
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
		return Bukkit.getServer().getPluginManager().getPlugins();
	}

	/**
	 * Gets a certain Plugin by name
	 * 
	 * @param name of the Plugin
	 * @return Plugin
	 */
	public static Plugin getPlugin(String name) {
		return Bukkit.getServer().getPluginManager().getPlugin(name);
	}

	/**
	 * Gets the plugin by inspecting a Class
	 * 
	 * @param clazz
	 * @return the Plugin matching the Class, or null if not found
	 */
	public static Plugin getPluginByClass(Class<?> clazz) {
		return getPluginByClass(clazz.getName());
	}

	/**
	 * Gets the plugin by inspecting the path to a Class
	 * 
	 * @param classPath of the Class
	 * @return the Plugin matching the Class, or null if not found
	 */
	public static Plugin getPluginByClass(String classPath) {
		if (classPath.startsWith(Common.COMMON_ROOT)) {
			return CommonPlugin.getInstance();
		}
		final String packagePath = getPackagePath(classPath);
		synchronized (Bukkit.getServer().getPluginManager()) {
			for (Plugin plugin : getPluginsUnsafe()) {
				// Compare package paths to see if the main package is below the class package
				// In the case of packagePath being empty: only if the main is in an empty package
				if (packagePath.startsWith(getPackagePath(plugin.getDescription().getMain()))) {
					return plugin;
				}
			}
		}
		return null;
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
	 * Tries to get the class at the path specified
	 * 
	 * @param path to the class
	 * @return the class, or null if not found
	 */
	public static Class<?> getClass(String path) {
		try {
			return Class.forName(path);
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
	 * Otherwise, only the static fields with the same type as the type parameter are returned.
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
}
