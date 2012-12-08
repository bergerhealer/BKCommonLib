package com.bergerkiller.bukkit.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerConfigurationManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;

public class CommonUtil {
	public static final int view = Bukkit.getServer().getViewDistance();
	public static final int viewWidth = view + view + 1;
	public static final int chunkArea = viewWidth * viewWidth;
	public static final int blockView = 32 + (view << 4);
	public static final Thread MAIN_THREAD = Thread.currentThread();

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
			if (msg.length() > 0) {
				if (sender instanceof CommandSender) {
					if (!(sender instanceof Player)) {
						message = ChatColor.stripColor(msg);
					}
					for (String line : msg.split("\n", -1)) {
						((CommandSender) sender).sendMessage(line);
					}
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
	 * Gets the server configuration manager which deals with server settings
	 * 
	 * @return Server Configuration Manager
	 */
	public static ServerConfigurationManager getServerConfig() {
		return getCraftServer().getHandle();
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
	 * Gets a list of online players on the server
	 * 
	 * @return online players
	 */
	public static Collection<Player> getOnlinePlayers() {
		return NativeUtil.getPlayers(getServerConfig().players);
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
		try {
			return type.cast(object);
		} catch (ClassCastException ex) {
		}
		return null;
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
		synchronized (CommonPlugin.nextTickTasks) {
			CommonPlugin.nextTickTasks.add(runnable);
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
	 * Gets constants of the class type statically defined in the class itself<br>
	 * If the class is an enum, the enumeration constants are returned
	 * 
	 * @param theClass to get the class constants of
	 * @return class constants defined in class 'theClass'
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] getClassConstants(Class<T> theClass) {
		if (theClass.isEnum()) {
			// Get using enum constants
			return theClass.getEnumConstants();
		} else {
			// Get using reflection
			try {
				Field[] declaredFields = theClass.getDeclaredFields();
				ArrayList<T> constants = new ArrayList<T>(declaredFields.length);
				for (Field field : declaredFields) {
					if (Modifier.isStatic(field.getModifiers()) && theClass.isAssignableFrom(field.getType())) {
						T constant = (T) field.get(null);
						if (constant != null) {
							constants.add(constant);
						}
					}
				}
				return LogicUtil.toArray(constants, theClass);
			} catch (Throwable t) {
				t.printStackTrace();
				return LogicUtil.createArray(theClass, 0);
			}
		}
	}
}
