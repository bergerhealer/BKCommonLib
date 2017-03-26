package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

/**
 * Allows the filtering of source stack trace elements. This is done by
 * specifying the method calls used.
 */
public class StackTraceFilter {

    /**
     * The default stack trace filter used for filtering server-related stack
     * traces
     */
    public static final StackTraceFilter SERVER = new StackTraceFilter();
    public final String className;
    public final String methodName;
    public final boolean searchMode;
    private final List<StackTraceFilter> next = new ArrayList<StackTraceFilter>(2);

    public StackTraceFilter() {
        this("*", "*", false);
    }

    private StackTraceFilter(String className, String methodName, boolean searchMode) {
        this.className = className;
        this.methodName = methodName;
        this.searchMode = searchMode;
    }

    public boolean matchClassName(String className) {
        return this.className.equals("*") || this.className.equals(className);
    }

    public boolean matchMethodName(String methodName) {
        return this.methodName.equals("*") || this.methodName.equals(methodName);
    }
    
    public boolean match(StackTraceElement el) {
    	return matchClassName(el.getClassName()) && matchMethodName(el.getMethodName());
    }

    public void print(Throwable error) {
        print(error, Level.SEVERE);
    }

    public void print(Throwable error, Level level) {
        Common.LOGGER.log(level, getMessage(error));
        ArrayList<StackTraceElement> elements = new ArrayList<StackTraceElement>(Arrays.asList(error.getStackTrace()));

        // Filter pointless information
        final int filteredCount = filter(elements);

        // Print stack trace
        for (StackTraceElement element : elements) {
        	Common.LOGGER.log(level, "  at " + element.toString());
        }
        if (filteredCount > 0) {
        	Common.LOGGER.log(level, "  ..." + filteredCount + " more");
        }

        // Print stack trace and messages of causes
        Throwable cause = error.getCause();
        if (cause != null) {
        	Common.LOGGER.log(level, "Caused by: " + getMessage(cause));
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
     * @param throwable containing the stack trace elements to filter
     * @return input throwable
     */
    public Throwable filter(Throwable t) {
    	if (t != null) {
        	LinkedList<StackTraceElement> trace = new LinkedList<StackTraceElement>(Arrays.asList(t.getStackTrace()));
        	if (filter(trace) > 0) {
        		t.setStackTrace(trace.toArray(new StackTraceElement[0]));
        	}
    	}
    	return t;
    }

    /**
     * Filters the elements based on this Stack Trace Filter
     *
     * @param elements to filter
     * @return the amount of elements that have been filtered out
     */
    public int filter(List<StackTraceElement> elements) {
    	int old_count = elements.size();
        StackTraceFilter filter = this;
        while ((filter = filter.next(elements)) != null);
        return old_count - elements.size();
    }

    /**
     * Adds a Stack Trace filter as the next element, preserving all next
     * elements stored within
     *
     * @param filter to add
     * @return the filter that was actually added (may be different)
     */
    public StackTraceFilter addNext(StackTraceFilter filter) {
        StackTraceFilter newFilter = next(filter.className, filter.methodName);
        if (newFilter == null || newFilter == this) {
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
        if (filter == null || filter == this) {
            filter = new StackTraceFilter(className, methodName, false);
            next.add(filter);
        }
        return filter;
    }

    /**
     * Navigates to the next element, or creates a new filter element if needed
     *
     * @param className of the stack trace element (use * for any)
     * @param methodName of the stack trace element (use * for any)
     * @return the Stack Trace Filter for this element
     */
    public StackTraceFilter untilNext(String className, String methodName) {
        StackTraceFilter filter = next(className, methodName);
        if (filter == null || filter == this) {
            filter = new StackTraceFilter(className, methodName, true);
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
    	List<StackTraceElement> items = new ArrayList<StackTraceElement>(1);
    	items.add(new StackTraceElement(className, methodName, "", 0));
    	return next(items);
    }

    private StackTraceFilter next(List<StackTraceElement> items) {
		ListIterator<StackTraceElement> it = items.listIterator(items.size());
    	if (this.searchMode) {
    		// Remove all items until the requested class is found
    		while (it.hasPrevious()) {
    			StackTraceElement el = it.previous();
    			it.remove();
    			if (this.match(el)) {
    				break;
    			}
    		}
    	}
    	if (it.hasPrevious()) {
        	StackTraceElement last = it.previous();
            for (StackTraceFilter filter : next) {
                // For searchmode (until) filters, the requested class name must exist in the upcoming stack trace
            	// Normal filters require direct matches with the last stack trace entry only
                if (filter.searchMode) {
                	for (StackTraceElement el : items) {
                		if (filter.match(el)) {
                			return filter;
                		}
                	}
                } else if (filter.match(last)) {
                	it.remove();
                	return filter;
                }
            }
    	}
        return null;
    }

    static {
        StackTraceFilter f;
        { // Server
        	StackTraceFilter server_root = SERVER.addNext(Common.NMS_ROOT + ".ThreadServerApplication", "run");
            {
                StackTraceFilter run = server_root.addNext(Common.NMS_ROOT + ".MinecraftServer", "run");
                { // stuff
                    f = run.addNext(Common.NMS_ROOT + ".MinecraftServer", "q");
                    f = f.addNext(Common.NMS_ROOT + ".DedicatedServer", "r");
                }
                { // Plugin disabling
                    f = run.addNext(Common.NMS_ROOT + ".MinecraftServer", "stop");
                    f = f.addNext(Common.CB_ROOT + ".CraftServer", "disablePlugins");
                    f = f.untilNext("org.bukkit.plugin.SimplePluginManager", "disablePlugin");
                }
            }
        }
        { // Test
        	f = SERVER.addNext("org.apache.maven.surefire.booter.ForkedBooter", "main");
        	f = f.untilNext("org.junit.internal.runners.model.ReflectiveCallable", "run");
        	f = f.untilNext("sun.reflect.NativeMethodAccessorImpl", "invoke0");
        }
    }
}
