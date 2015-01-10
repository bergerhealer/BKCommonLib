package com.bergerkiller.bukkit.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Allows the filtering of source stack trace elements.
 * This is done by specifying the method calls used.
 */
public class StackTraceFilter {
	/**
	 * The default stack trace filter used for filtering server-related stack traces
	 */
	public static final StackTraceFilter SERVER = new StackTraceFilter();
	public final String className;
	public final String methodName;
	private final List<StackTraceFilter> next = new ArrayList<StackTraceFilter>(2);

	public StackTraceFilter() {
		this("*", "*");
	}

	private StackTraceFilter(String className, String methodName) {
		this.className = className;
		this.methodName = methodName;
	}

	public boolean matchClassName(String className) {
		return this.className.equals("*") || this.className.equals(className);
	}

	public boolean matchMethodName(String className) {
		return this.methodName.equals("*") || this.methodName.equals(className);
	}

	public void print(Throwable error) {
		print(error, Level.SEVERE);
	}

	public void print(Throwable error, Level level) {
		final Logger log = Bukkit.getLogger();
		log.log(level, getMessage(error));
		ArrayList<StackTraceElement> elements = new ArrayList<StackTraceElement>(Arrays.asList(error.getStackTrace()));

		// Filter pointless information
		final int filteredCount = filter(elements);

		// Print stack trace
		for (StackTraceElement element : elements) {
			log.log(level, "  at " + element.toString());
		}
		if (filteredCount > 0) {
			log.log(level, "  ..." + filteredCount + " more");
		}

		// Print stack trace and messages of causes
		Throwable cause = error.getCause();
		if (cause != null) {
			log.log(level, "Caused by: " + getMessage(cause));
			print(cause, level);
		}
	}

	private static String getMessage(Throwable error) {
		String msg = error.getMessage();
		if (LogicUtil.nullOrEmpty(msg)) {
			return error.getClass().getName();
		} else {
			return error.getClass().getName() + ": " + msg;
		}
	}

	/**
	 * Filters the elements based on this Stack Trace Filter
	 * 
	 * @param elements to filter
	 * @return the amount of elements that have been filtered out
	 */
	public int filter(List<StackTraceElement> elements) {
		ListIterator<StackTraceElement> iter = elements.listIterator(elements.size());
		int count = 0;
		StackTraceFilter filter = this;
		StackTraceElement element;
		while (iter.hasPrevious()) {
			element = iter.previous();
			filter = filter.next(element.getClassName(), element.getMethodName());
			if (filter == null) {
				break;
			} else {
				count++;
				iter.remove();
			}
		}
		return count;
	}

	/**
	 * Adds a Stack Trace filter as the next element, preserving all next elements stored within
	 * 
	 * @param filter to add
	 * @return the filter that was actually added (may be different)
	 */
	public StackTraceFilter addNext(StackTraceFilter filter) {
		StackTraceFilter newFilter = next(filter.className, filter.methodName);
		if (newFilter == null) {
			next.add(filter);
			return filter;
		} else {
			for (StackTraceFilter subFilter : filter.next) {
				newFilter.addNext(subFilter);
			}
			return newFilter;
		}
	}

	/**
	 * Navigates to the next element, or creates a new filter element if needed
	 * 
	 * @param className of the stack trace element (use * for any)
	 * @param methodName of the stack trace element (use * for any)
	 * @return the Stack Trace Filter for this element
	 */
	public StackTraceFilter addNext(String className, String methodName) {
		StackTraceFilter filter = next(className, methodName);
		if (filter == null) {
			filter = new StackTraceFilter(className, methodName);
			next.add(filter);
		}
		return filter;
	}

	/**
	 * Navigates to the next element, or creates a new filter element if needed.
	 * The current class name is used.
	 * 
	 * @param methodName of the stack trace element (use * for any)
	 * @return the Stack Trace Filter for this element
	 */
	public StackTraceFilter addNext(String methodName) {
		return addNext(this.className, methodName);
	}

	private StackTraceFilter next(String className, String methodName) {
		for (StackTraceFilter filter : next) {
			if (filter.matchClassName(className) && filter.matchMethodName(methodName)) {
				return filter;
			}
		}
		return null;
	}

	static {
		StackTraceFilter f;
		f = SERVER.addNext(Common.NMS_ROOT + ".ThreadServerApplication", "run");
		StackTraceFilter run = f.addNext(Common.NMS_ROOT + ".MinecraftServer", "run");
		f = run.addNext(Common.NMS_ROOT + ".MinecraftServer", "q");
		f = f.addNext(Common.NMS_ROOT + ".DedicatedServer", "r");
		StackTraceFilter main = f.addNext(Common.NMS_ROOT + ".MinecraftServer", "r");
		StackTraceFilter dedic = main.addNext(Common.NMS_ROOT + ".DedicatedServerConnection", "b");
		// Player connection updating
		f = dedic.addNext(Common.NMS_ROOT + ".ServerConnection", "b");
		// Pending connections
		f = dedic.addNext(Common.NMS_ROOT + ".DedicatedServerConnectionThread", "a");
		// World onTick
		f = main.addNext(Common.NMS_ROOT + ".WorldServer", "doTick");
		f = f.addNext(Common.NMS_ROOT + ".WorldServer", "a");
		f = f.addNext("*", "*");
		// Entities onTick
		f = main.addNext(Common.NMS_ROOT + ".WorldServer", "tickEntities");
		// Plugin disabling
		f = run.addNext(Common.NMS_ROOT + ".MinecraftServer", "stop");
		f = f.addNext(Common.CB_ROOT + ".CraftServer", "disablePlugins");
		f = f.addNext("org.bukkit.plugin.SimplePluginManager", "disablePlugins");
		f = f.addNext("org.bukkit.plugin.SimplePluginManager", "disablePlugin");
		// Craft server init
		f = run.addNext(Common.NMS_ROOT + ".DedicatedServer", "init");
		f = f.addNext(Common.NMS_ROOT + ".DedicatedPlayerList", "<init>");
		f = f.addNext(Common.NMS_ROOT + ".PlayerList", "<init>");
		StackTraceFilter cbInit = f.addNext(Common.CB_ROOT + ".CraftServer", "<init>");
		// Plugin loading
		f = cbInit.addNext(Common.CB_ROOT + ".CraftServer", "loadPlugins");
		// Plugin enabling
		f = cbInit.addNext(Common.CB_ROOT + ".CraftServer", "enablePlugins");
		f = f.addNext(Common.CB_ROOT + ".CraftServer", "loadPlugin");
		f = f.addNext("org.bukkit.plugin.SimplePluginManager", "enablePlugin");
		f = f.addNext("org.bukkit.plugin.java.JavaPluginLoader", "enablePlugin");
		f = f.addNext("org.bukkit.plugin.java.JavaPlugin", "setEnabled");
	}
}
