package com.bergerkiller.bukkit.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import com.bergerkiller.bukkit.common.TypedValue;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
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
        randomizeBlock(block, Material.STONE, Material.DIRT, Material.GRASS, Material.WOOD, Material.LOG,
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
     * <b>#typeid</b> = <i>Block Type Id</i><br>
     * <b>#type</b> = <i>Block Type</i>
     *
     * @param block to format
     * @param format to use
     * @return Formatted String
     */
    @SuppressWarnings("deprecation")
    public static String formatBlock(Block block, String format) {
        StringBuilder buffer = new StringBuilder(format);
        StringUtil.replaceAll(buffer, "#x", Integer.toString(block.getX()));
        StringUtil.replaceAll(buffer, "#y", Integer.toString(block.getY()));
        StringUtil.replaceAll(buffer, "#z", Integer.toString(block.getZ()));
        StringUtil.replaceAll(buffer, "#world", block.getWorld().getName());
        StringUtil.replaceAll(buffer, "#typeid", Integer.toString(MaterialUtil.getTypeId(block)));
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
    public static float getShortValue(String name, int value) {
        return getVariableValue(name, Integer.valueOf(value)).shortValue();
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
     * @param startClass to start looking for static values to begin
     * @param value to find
     */
    public static void logInstances(Class<?> startClass, Object value) {
        System.out.println("Searching for [" + value.getClass().getName() + "] " + value.toString() + ":");
        IdentityHashMap<Object, Boolean> crossedValues = new IdentityHashMap<Object, Boolean>();
        HashMap<Class<?>, ArrayList<Field>> classFieldMapping = new HashMap<Class<?>, ArrayList<Field>>();
        logInstancesInClass(startClass, value, crossedValues, classFieldMapping);
        System.out.println("Search completed.");
    }

    private static ArrayList<Field> logInstancesInClass(Class<?> type, Object value, IdentityHashMap<Object, Boolean> crossedValues, HashMap<Class<?>, ArrayList<Field>> classFieldMapping) {
        ArrayList<Field> staticFields = new ArrayList<Field>();
        ArrayList<Field> localFields = new ArrayList<Field>();
        Class<?> t = type;
        do {
            for (Field f : t.getDeclaredFields()) {
                Class<?> f_type = f.getType();

                // Ignore certain types of fields
                if (f_type.isPrimitive()) continue;
                if (Class.class.isAssignableFrom(f_type)) continue;

                SecureField sf = new SecureField();
                sf.init(f);
                sf.read();

                if (Modifier.isStatic(f.getModifiers())) {
                    staticFields.add(f);
                } else {
                    localFields.add(f);
                }
            }
        } while ((t = t.getSuperclass()) != null);

        classFieldMapping.put(type, localFields);

        LinkedList<StackElement> fieldStack = new LinkedList<StackElement>();
        fieldStack.addLast(new StackElement(type));
        for (Field staticField : staticFields) {
            Object staticValue = null;
            try {
                staticValue = staticField.get(null);
            } catch (Throwable tt) {}

            fieldStack.addLast(new StackElement(staticField));
            logInstances(staticValue, value, crossedValues, classFieldMapping, fieldStack);
            fieldStack.removeLast();
        }

        return localFields;
    }

    private static class StackElement {
        public final Object info;
        public boolean logged;

        public StackElement(Object info) {
            this.info = info;
            this.logged = false;
        }
    }

    private static void logInstances(Object current, Object value, IdentityHashMap<Object, Boolean> crossedValues, HashMap<Class<?>, ArrayList<Field>> classFieldMapping, LinkedList<StackElement> fieldStack) {
        // Ignore null
        if (current == null) return;

        // Found it!
        if (current == value) {
            // Log all the fields that still need logging, with indent
            String indent = "  ";
            StackElement last = fieldStack.getLast();
            for (StackElement el : fieldStack) {
                if (!el.logged) {
                    el.logged = true;
                    if (el.info instanceof Class) {
                        System.out.println("[Static members of " + ((Class<?>) el.info).getSimpleName() + "]");
                        indent = "  ";
                        continue;
                    }

                    String infoStr = "";
                    if (el.info instanceof Field) {
                        Field f = (Field) el.info;
                        infoStr += Modifier.toString(f.getModifiers());
                        infoStr += " " + f.getType().getSimpleName();
                        infoStr += " " + f.getName();
                    } else {
                        infoStr += el.info.toString();
                    }
                    if (last == el) {
                        System.out.println(indent + "- " + infoStr + " <<< HERE");
                    } else {
                        System.out.println(indent + "- " + infoStr);
                    }
                }
                indent += "  ";
            }

            // Continue searching, because the current value itself may also hold a reference somewhere!
        }

        // Check if not already discovered
        if (crossedValues.put(current, Boolean.valueOf(true)) != null) {
            return;
        }

        // Discover the current class
        Class<?> type = current.getClass();
        ArrayList<Field> localFields = classFieldMapping.get(type);
        if (localFields == null) {
            localFields = logInstancesInClass(type, value, crossedValues, classFieldMapping);
        }

        // Handle maps so we don't have to go all the way into the private members
        try {
            if (Map.class.isAssignableFrom(type)) {
                for (Entry<?, ?> entry : ((Map<?, ?>) current).entrySet()) {
                    fieldStack.addLast(new StackElement("M{v=" + entry.getValue() + "}.key"));
                    logInstances(entry.getKey(), value, crossedValues, classFieldMapping, fieldStack);
                    fieldStack.removeLast();

                    fieldStack.addLast(new StackElement("M{k=" + entry.getKey() + "}.value"));
                    logInstances(entry.getValue(), value, crossedValues, classFieldMapping, fieldStack);
                    fieldStack.removeLast();
                }
                return;
            }
        } catch (Throwable tt) {} // ignore errors, just try different means

        // Handle lists so we don't have to go all the way into the private members
        try {
            if (List.class.isAssignableFrom(type)) {
                List<?> list = (List<?>) current;
                ListIterator<?> iter = list.listIterator();
                int idx = 0;
                while (iter.hasNext()) {
                    Object currItem = iter.next();
                    fieldStack.addLast(new StackElement("L[" + idx + "]"));
                    logInstances(currItem, value, crossedValues, classFieldMapping, fieldStack);
                    fieldStack.removeLast();
                    idx++;
                }
                return;
            }
        } catch (Throwable tt) {} // ignore errors, just try different means

        // Handle generic collection types that don't specify an index
        try {
            if (Collection.class.isAssignableFrom(type)) {
                Collection<?> coll = (Collection<?>) current;
                fieldStack.addLast(new StackElement("C[?]"));
                for (Object currItem : coll) {
                    logInstances(currItem, value, crossedValues, classFieldMapping, fieldStack);
                }
                fieldStack.removeLast();
                return;
            }
        } catch (Throwable tt) {} // ignore errors, just try different means

        // If this is an array, go by all its elements
        // Unknown collection types will also end up deep inside here
        if (type.isArray() && !type.getComponentType().isPrimitive()) {
            Object[] arr = (Object[]) current;
            for (int i = 0; i < arr.length; i++) {
                fieldStack.addLast(new StackElement("A[" + i + "]"));
                logInstances(arr[i], value, crossedValues, classFieldMapping, fieldStack);
                fieldStack.removeLast();
            }
            return;
        }

        // Go by all fields in this instance
        for (Field localField : localFields) {
            Object fieldValue = null;
            try {
                fieldValue = localField.get(current);
            } catch (Throwable tt) {}

            fieldStack.addLast(new StackElement(localField));
            logInstances(fieldValue, value, crossedValues, classFieldMapping, fieldStack);
            fieldStack.removeLast();
        }
    }

}
