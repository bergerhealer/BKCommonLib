package com.bergerkiller.bukkit.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.bergerkiller.bukkit.common.AsyncTask;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.ModuleLogger;
import com.bergerkiller.bukkit.common.TypedValue;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.util.SecureField;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

/**
 * Contains utilities to debug code, such as logging objects
 */
public class DebugUtil {

    /**
     * Applies a random material to a block that does not equal the current
     * material<br>
     * Materials that are used: <i>STONE, DIRT, GRASS, WOOD, LOG, IRON_ORE,
     * IRON_BLOCK, GOLD_BLOCK, DIAMOND_BLOCK</i>
     *
     * @param block to randomize
     */
    public static void randomizeBlock(Block block) {
        randomizeBlock(block, Material.STONE, Material.DIRT, Material.GRASS,
                Material.IRON_ORE, Material.IRON_BLOCK, Material.GOLD_BLOCK, Material.DIAMOND_BLOCK);
    }

    /**
     * Applies a random material from the list to a block that does not equal
     * the current material
     *
     * @param block to randomize
     * @param materials to pick from
     */
    public static void randomizeBlock(Block block, Material... materials) {
        while (true) {
            Material mat = materials[(int) (Math.random() * materials.length)];
            if (!MaterialUtil.isType(block, mat)) {
                block.setType(mat);
                break;
            }
        }
    }

    /**
     * Formats all the properties of a block to a String. Uses format:<br>
     * <i>#world [#x, #y, #z] #type</i>
     *
     * @param block to format
     * @return Formatted String
     */
    public static String formatBlock(Block block) {
        return formatBlock(block, "#world [#x, #y, #z] #type");
    }

    /**
     * Formats all the properties of a block to a String<br>
     * <b>#x</b> = <i>Block X</i><br>
     * <b>#y</b> = <i>Block Y</i><br>
     * <b>#z</b> = <i>Block Z</i><br>
     * <b>#world</b> = <i>World name<i/><br>
     * <b>#type</b> = <i>Block Type</i>
     *
     * @param block to format
     * @param format to use
     * @return Formatted String
     */
    public static String formatBlock(Block block, String format) {
        StringBuilder buffer = new StringBuilder(format);
        StringUtil.replaceAll(buffer, "#x", Integer.toString(block.getX()));
        StringUtil.replaceAll(buffer, "#y", Integer.toString(block.getY()));
        StringUtil.replaceAll(buffer, "#z", Integer.toString(block.getZ()));
        StringUtil.replaceAll(buffer, "#world", block.getWorld().getName());
        StringUtil.replaceAll(buffer, "#type", block.getType().toString());
        return buffer.toString();
    }

    /**
     * Broadcasts HEARTBEAT with the current time<br>
     * Can be used to check if a ticked object is still alive
     */
    public static void heartbeat() {
        CommonUtil.broadcast("HEARTBEAT: " + System.currentTimeMillis());
    }

    /**
     * Gets a debug variable that can be changed using the /debug command
     *
     * @param name of the value
     * @param value initial (not null)
     * @return Typed value for the Variable
     */
    @SuppressWarnings("unchecked")
    public static <T> TypedValue<T> getVariable(String name, T value) {
        return CommonPlugin.getInstance().getDebugVariable(name, (Class<T>) value.getClass(), value);
    }

    /**
     * Gets a debug variable that can be changed using the /debug command
     *
     * @param name of the value
     * @param type of value
     * @param value initial (can be null)
     * @return Typed value for the Variable
     */
    public static <T> TypedValue<T> getVariable(String name, Class<T> type, T value) {
        return CommonPlugin.getInstance().getDebugVariable(name, type, value);
    }

    /**
     * Gets the value of a debug variable that can be changed using the /debug command
     * 
     * @param name of the value
     * @param value initial (can not be null)
     * @return value of the variable
     */
    public static <T> T getVariableValue(String name, T value) {
        return getVariable(name, value).value;
    }

    /**
     * Gets the value of a double debug variable that can be changed using the /debug command
     * 
     * @param name of the value
     * @param value default
     * @return value of the variable
     */
    public static double getDoubleValue(String name, double value) {
        return getVariableValue(name, Double.valueOf(value)).doubleValue();
    }

    /**
     * Gets the value of a float debug variable that can be changed using the /debug command
     * 
     * @param name of the value
     * @param value default
     * @return value of the variable
     */
    public static float getFloatValue(String name, double value) {
        return getVariableValue(name, Double.valueOf(value)).floatValue();
    }

    /**
     * Gets the value of a short debug variable that can be changed using the /debug command
     * 
     * @param name of the value
     * @param value default
     * @return value of the variable
     */
    public static short getShortValue(String name, int value) {
        return getVariableValue(name, Integer.valueOf(value)).shortValue();
    }

    /**
     * Gets the value of a int debug variable that can be changed using the /debug command
     * 
     * @param name of the value
     * @param value default
     * @return value of the variable
     */
    public static int getIntValue(String name, int value) {
        return getVariableValue(name, Integer.valueOf(value)).intValue();
    }

    /**
     * Gets the value of a bool debug variable that can be changed using the /debug command
     * 
     * @param name of the value
     * @param value default
     * @return value of the variable
     */
    public static boolean getBooleanValue(String name, boolean value) {
        return getVariableValue(name, Boolean.valueOf(value)).booleanValue();
    }

    /**
     * Gets the value of a Vector debug variable that can be changed using the /debug command.
     * The x/y/z values can be specified after name. If name is 'test', then 'testx', 'testy' and 'testz'
     * are added as debug variables.
     * 
     * @param name of the value
     * @param value default
     * @return value of the variable
     */
    public static Vector getVectorValue(String name, Vector value) {
        return new Vector(getDoubleValue(name + "x", value.getX()),
                          getDoubleValue(name + "y", value.getY()),
                          getDoubleValue(name + "z", value.getZ()));
    }

    /**
     * Gets the value of an IntVector debug variable that can be changed using the /debug command.
     * The x/y/z values can be specified after name. If name is 'test', then 'testx', 'testy' and 'testz'
     * are added as debug variables.
     * 
     * @param name of the value
     * @param value default
     * @return value of the variable
     */
    public static IntVector3 getIntVectorValue(String name, IntVector3 value) {
        return new IntVector3(getIntValue(name + "x", value.x),
                              getIntValue(name + "y", value.y),
                              getIntValue(name + "z", value.z));
    }

    /**
     * Looks at the current stack trace to find all potential plugins that could have caused something
     * 
     * @return plugin causes
     */
    public static String getPluginCauses() {
        Plugin[] plugins = CommonUtil.findPlugins(Thread.currentThread().getStackTrace());
        if (plugins == null || plugins.length == 0) {
            return "Unknown";
        }
        String[] names = new String[plugins.length];
        for (int i = 0; i < plugins.length; i++) {
            names[i] = plugins[i].getName();
        }
        return StringUtil.combineNames(names);
    }

    /**
     * Goes down the entirety of the server to see where a particular instance of a variable is referenced.
     * Note that this is very, very slow! It is here to verify correct replacement of hooks and the like.
     * <br><br>
     * All locations where the instance lives will be logged.
     * 
     * @param value to find
     */
    public static void logInstances(Object value) {
        logInstances(Bukkit.class, value);
    }

    /**
     * Goes down the entirety of the class type specified to see where a particular instance of a variable is referenced.
     * Note that this is very, very slow! It is here to verify correct replacement of hooks and the like.
     * <br><br>
     * All locations where the instance lives will be logged.
     * 
     * @param startObject to start looking for member values to begin
     * @param value to find
     */
    public static void logInstances(Object startObject, Object value) {
        InstanceSearcher searcher = new InstanceSearcher(value);
        searcher.logger.info("Searching for [" + value.getClass().getName() + "] " + value.toString() + ":");
        searcher.searchFrom(new StackElement(startObject));
        searcher.run();
        searcher.logger.info("Search completed.");
    }

    /**
     * Goes down the entirety of the class type specified to see where a particular instance of a variable is referenced.
     * Note that this is very, very slow! It is here to verify correct replacement of hooks and the like.
     * <br><br>
     * All locations where the instance lives will be logged.
     * 
     * @param startClass to start looking for static values to begin
     * @param value to find
     */
    public static void logInstances(Class<?> startClass, Object value) {
        InstanceSearcher searcher = new InstanceSearcher(value);
        searcher.logger.info("Searching for [" + value.getClass().getName() + "] " + value.toString() + ":");
        searcher.searchFromClassFields(startClass);
        searcher.run();
        searcher.logger.info("Search completed.");
    }

    private static class FieldWithType {
        public final Field field;
        public final Class<?> valueType;

        public FieldWithType(Field field, Class<?> valueType) {
            this.field = field;
            this.valueType = valueType;
        }

        public static Object wrapInfo(Field field, Object value) {
            Class<?> type = value.getClass();
            if (type != field.getType()) {
                return new FieldWithType(field, value.getClass());
            } else {
                return field;
            }
        }
    }

    /**
     * Logs the stack trace of the current thread after a delay in milliseconds.
     * Can be used to debug application freezes.
     * 
     * @param delay after which to log the stack trace
     */
    public static void logStackTraceAsynchronously(long delay) {
        logStackTraceAsynchronously(delay, LogicUtil.constantSupplier(
                Collections.singletonList(Thread.currentThread())));
    }

    /**
     * Logs the stack trace of a thread after a delay in milliseconds.
     * All threads running at the time are passed by the predicate, and the
     * one to dump can be filtered out.
     *
     * @param delay after which to log the stack trace
     * @param threadPredicate filter for threads
     */
    public static void logStackTraceAsynchronously(long delay, final Predicate<Thread> threadPredicate) {
        logStackTraceAsynchronously(delay, () -> {
            Thread loggerThread = Thread.currentThread(); // Ignore self
            Thread[] tmp = new Thread[Thread.activeCount() + 32];
            List<Thread> result = new ArrayList<>();
            int count = Thread.enumerate(tmp);
            for (int i = 0; i < count; i++) {
                if (tmp[i] != loggerThread && threadPredicate.test(tmp[i])) {
                    result.add(tmp[i]);
                }
            }
            return result;
        });
    }

    /**
     * Logs the stack trace of a thread after a delay in milliseconds.
     * Can be used to debug application freezes. The thread to dump is
     * retrieved after the delay, using the supplier specified. In there a lookup
     * by name or otherwise can be performed. If the supplier returns null, then no
     * thread info is dumped.
     *
     * @param delay after which to log the stack trace
     * @param threadSelector selector to call to find the threads to dump
     */
    public static void logStackTraceAsynchronously(long delay, final Supplier<? extends Iterable<Thread>> threadSelector) {
        new AsyncTask() {
            @Override
            public void run() {
                sleep(delay);
                for (Thread thread : threadSelector.get()) {
                    StackTraceElement[] stack = thread.getStackTrace();
                    Logging.LOGGER_DEBUG.warning("Stack trace of thread " + thread.getName() + ":");
                    for (StackTraceElement element : stack) {
                        Logging.LOGGER_DEBUG.warning("  at " + element.toString());
                    }
                }
            }
        }.start();
    }

    private static class StackElement {
        public final StackElement parent;
        public final Object value;
        public final Object info;
        public int index; // -1 = no index, -2 = recurse

        public StackElement(Object value) {
            this(value, value);
        }

        public StackElement(Object value, Object info) {
            this(null, value, info);
        }

        private StackElement(StackElement parent, Object value, Object info) {
            this.parent = parent;
            this.value = value;
            this.info = info;
            this.index = -1;
        }

        public StackElement next(Object value, Object info) {
            return new StackElement(this, value, info);
        }
    }

    private static class StackTreeElement {
        public final StackElement self;
        public List<StackTreeElement> children;
        public boolean valueIsHere;

        public StackTreeElement(StackElement self) {
            this.self = self;
            this.children = Collections.emptyList();
            this.valueIsHere = false;
        }

        public StackTreeElement next(StackElement child) {
            for (StackTreeElement childTreeEl : children) {
                if (childTreeEl.self == child) {
                    return childTreeEl;
                }
            }

            StackTreeElement newElement = new StackTreeElement(child);
            if (this.children.isEmpty()) {
                this.children = Collections.singletonList(newElement);
            } else {
                this.children = new ArrayList<StackTreeElement>(this.children);
                this.children.add(newElement);
            }
            return newElement;
        }

        public void log(ModuleLogger logger, String indent) {
            String nextIndent = "";
            if (self != null) {
                nextIndent = indent + "  ";
                if (self.info instanceof Class) {
                    logger.info("[Static members of " + ((Class<?>) self.info).getSimpleName() + "]");
                } else {
                    String infoStr;
                    if (self.info instanceof Field) {
                        Field f = (Field) self.info;
                        infoStr = Modifier.toString(f.getModifiers()) +
                                " " + f.getType().getSimpleName() +
                                " " + f.getName();
                    } else if (self.info instanceof FieldWithType) {
                        FieldWithType fwt = (FieldWithType) self.info;
                        Field f = fwt.field;
                        infoStr = Modifier.toString(f.getModifiers()) +
                                " [" + fwt.valueType.getSimpleName() +
                                "] " + f.getType().getSimpleName() +
                                " " + f.getName();
                    } else {
                        infoStr = self.info.toString();
                    }
                    if (self.index == -2) {
                        infoStr += " [Recursive]";
                    } else if (self.index != -1) {
                        infoStr += " [" + self.index + "]";
                    }
                    if (valueIsHere) {
                        logger.info(indent + "- " + infoStr + " <<< HERE");
                    } else {
                        logger.info(indent + "- " + infoStr);
                    }
                }
            }
            for (StackTreeElement child : children) {
                child.log(logger, nextIndent);
            }
        }
    }

    private static class InstanceSearcher {
        private final ModuleLogger logger = Logging.LOGGER_DEBUG;
        private final IdentityHashMap<Object, Boolean> crossedValues = new IdentityHashMap<Object, Boolean>();
        private final HashMap<Class<?>, ArrayList<Field>> classFieldMapping = new HashMap<Class<?>, ArrayList<Field>>();
        private final Object valueToFind;
        private final StackTreeElement resultTree = new StackTreeElement(null);
        private List<StackElement> pending = new ArrayList<StackElement>();
        private List<StackElement> pending_tmp = new ArrayList<StackElement>(); // swapped for safe iteration

        public InstanceSearcher(Object valueToFind) {
            this.valueToFind = valueToFind;
        }

        public void run() {
            // While there's more stuff to search, search endlessly
            while (!pending.isEmpty()) {
                // Swap and reset pending
                {
                    List<StackElement> c = pending;
                    pending = pending_tmp;
                    pending_tmp = c;
                }
                pending.clear();

                for (StackElement el : pending_tmp) {
                    searchFrom(el);
                }
            }

            // Print results if found
            if (!resultTree.children.isEmpty()) {
                resultTree.log(logger, "");
            }
        }

        public ArrayList<Field> searchFromClassFields(Class<?> type) {
            ArrayList<Field> staticFields = new ArrayList<Field>();
            ArrayList<Field> localFields = new ArrayList<Field>();
            Class<?> t = type;
            do {
                Field[] fields;
                try {
                    fields = t.getDeclaredFields();
                } catch (Throwable err) {
                    continue;
                } // can happen when classes arent found

                for (Field f : fields) {
                    // Ignore certain types of fields
                    if (!isInterestingType(f.getType())) {
                        continue;
                    }

                    try {
                        SecureField sf = new SecureField();
                        sf.init(f);
                        sf.read();
                        
                        if (Modifier.isStatic(f.getModifiers())) {
                            staticFields.add(f);
                        } else {
                            localFields.add(f);
                        }
                    } catch (RuntimeException ex) {
                        
                    }
                }
            } while ((t = t.getSuperclass()) != null);

            classFieldMapping.put(type, localFields);

            // Discover from static fields declared inside this class type
            final StackElement class_start_stack = new StackElement(type);
            for (Field staticField : staticFields) {
                Object staticValue = null;
                try {
                    staticValue = staticField.get(null);
                } catch (Throwable tt) {}
                if (staticValue == null) {
                    continue;
                }

                pending.add(class_start_stack.next(staticValue, FieldWithType.wrapInfo(staticField, staticValue)));
            }

            return localFields;
        }

        public void searchFrom(StackElement current) {
            if (current.value == valueToFind) {
                // Compute the full stack that got us here
                List<StackElement> stack = MountiplexUtil.iterateNullTerminated(current, s -> s.parent)
                        .collect(Collectors.toCollection(ArrayList::new));
                Collections.reverse(stack);

                // Merge the stack with the result tree
                StackTreeElement curr = resultTree;
                for (StackElement el : stack) {
                    curr = curr.next(el);
                }
                curr.valueIsHere = true;

                // Continue searching, because the current value itself may also hold a reference somewhere!
            }

            // Check if not already discovered
            if (crossedValues.put(current.value, Boolean.TRUE) != null) {
                return;
            }

            // Discover the current class
            final Class<?> type = current.value.getClass();
            ArrayList<Field> localFields = classFieldMapping.get(type);
            if (localFields == null) {
                localFields = searchFromClassFields(type);
            }

            // Handle maps so we don't have to go all the way into the private members
            try {
                if (Map.class.isAssignableFrom(type)) {
                    for (Entry<?, ?> entry : ((Map<?, ?>) current.value).entrySet()) {
                        Object key = entry.getKey();
                        Object value = entry.getValue();

                        if (key != null && isInterestingType(key.getClass())) {
                            pending.add(current.next(key, "M{v=" + value + "}.key"));
                        }
                        if (value != null && isInterestingType(value.getClass())) {
                            pending.add(current.next(value, "M{k=" + key + "}.value"));
                        }
                    }
                }
            } catch (Throwable tt) {} // ignore errors, just try different means

            // Handle lists so we don't have to go all the way into the private members
            try {
                if (List.class.isAssignableFrom(type)) {
                    StackElement list_parent = (current.parent == null || current.index != -1) ? current : current.parent;
                    int idx = 0;
                    for (Object currItem : (List<?>) current.value) {
                        if (currItem != null && isInterestingType(currItem.getClass())) {
                            StackElement list_el = list_parent.next(currItem, current.info);
                            list_el.index = idx;
                            pending.add(list_el);
                        }
                        idx++;
                    }
                }
            } catch (Throwable tt) {} // ignore errors, just try different means

            // Handle generic collection types that don't specify an index
            try {
                if (Collection.class.isAssignableFrom(type) && !List.class.isAssignableFrom(type)) {
                    for (Object currItem : (Collection<?>) current.value) {
                        if (currItem != null && isInterestingType(currItem.getClass())) {
                            pending.add(current.next(currItem, "C[?]"));
                        }
                    }
                }
            } catch (Throwable tt) {} // ignore errors, just try different means

            // If this is an array, go by all its elements
            // Unknown collection types will also end up deep inside here
            if (type.isArray()) {
                // Skip primitive arrays entirely
                if (type.getComponentType().isPrimitive()) {
                    return;
                }

                Object[] arr = (Object[]) current.value;
                StackElement arr_parent = (current.parent == null || current.index != -1) ? current : current.parent;
                for (int i = 0; i < arr.length; i++) {
                    Object currItem = arr[i];
                    if (currItem != null && isInterestingType(currItem.getClass())) {
                        StackElement list_el = arr_parent.next(currItem, current.info);
                        list_el.index = i;
                        pending.add(list_el);
                    }
                }
                return;
            }

            // Go by all fields in this instance
            for (Field localField : localFields) {
                Object fieldValue = null;
                try {
                    fieldValue = localField.get(current.value);
                } catch (Throwable tt) {}

                // If null, just skip it's not going to store anything interesting
                if (fieldValue == null) {
                    continue;
                }

                // Add next value
                pending.add(current.next(fieldValue, FieldWithType.wrapInfo(localField, fieldValue)));
            }
        }
    }

    private static boolean isInterestingType(Class<?> type) {
        return !type.isPrimitive() && type != String.class && !Number.class.isAssignableFrom(type);
    }

    /**
     * Pauses execution until the VisualVM profiler has hooked its agent into the JVM
     *
     * @param timeout Maximum time to wait for the profiler agent to be installed
     * @param setupDuration Amount of time to give for the profiler to initialize itself
     */
    public static void waitForVisualVMProfiler(long timeout, long setupDuration) {
        long startTime = System.currentTimeMillis();

        // Wait until VisualVM has installed the agent into the JVM
        // This is a custom .dll (windows) or .so (linux/solaris)
        boolean found = false;
        try {
            do {
                Thread.sleep(100);
                javax.management.ObjectName diagnosticsCommandName;
                diagnosticsCommandName = new javax.management.ObjectName(
                        "com.sun.management:type=DiagnosticCommand");
                String result = (String) java.lang.management.ManagementFactory.getPlatformMBeanServer().invoke(
                        diagnosticsCommandName, "vmDynlibs", null, null);
                for (String line : result.split("\n")) {
                    if (line.endsWith("profilerinterface.dll") ||
                        line.endsWith("libprofilerinterface.so") ||
                        line.endsWith("libprofilerinterface.jnilib"))
                    {
                        found = true;
                        break;
                    }
                }
            } while (!found && (System.currentTimeMillis() - startTime) < timeout);
        } catch (Throwable t) {
            throw new UnsupportedOperationException("Failed to check whether VisualVM is hooked", t);
        }

        if (!found) {
            throw new IllegalStateException("Timed out waiting for VisualVM to be hooked!");
        }

        try {
            Thread.sleep(setupDuration);
        } catch (InterruptedException e) {}
    }
}
