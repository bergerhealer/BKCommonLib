package com.bergerkiller.bukkit.common.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.BiMap;

import org.bukkit.block.Block;

import com.bergerkiller.bukkit.common.collections.BlockSet;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeDirectMethod;

/**
 * Logic operations, such as contains checks and collection-type transformations
 */
public class LogicUtil {

    private static final MethodAccessor<Object> objectCloneMethod;
    private static final Map<Class<?>, Class<?>> unboxedToBoxed = new HashMap<Class<?>, Class<?>>();
    private static final Map<Class<?>, Class<?>> boxedToUnboxed = new HashMap<Class<?>, Class<?>>();

    static {
        unboxedToBoxed.put(boolean.class, Boolean.class);
        unboxedToBoxed.put(char.class, Character.class);
        unboxedToBoxed.put(byte.class, Byte.class);
        unboxedToBoxed.put(short.class, Short.class);
        unboxedToBoxed.put(int.class, Integer.class);
        unboxedToBoxed.put(long.class, Long.class);
        unboxedToBoxed.put(float.class, Float.class);
        unboxedToBoxed.put(double.class, Double.class);
        for (Entry<Class<?>, Class<?>> entry : unboxedToBoxed.entrySet()) {
            boxedToUnboxed.put(entry.getValue(), entry.getKey());
        }
        // Get the cloning method
        MethodAccessor<Object> objectCloneMethodAccessor;
        try {
            final Method cloneMethod = Object.class.getDeclaredMethod("clone");
            cloneMethod.setAccessible(true);
            objectCloneMethodAccessor = new SafeDirectMethod<Object>() {
                @Override
                public Object invoke(Object instance, Object... args) {
                    try {
                        return cloneMethod.invoke(instance, args);
                    } catch (Throwable t) {
                        throw new RuntimeException("Failed to clone:", t);
                    }
                }
            };
        } catch (Throwable t) {
            objectCloneMethodAccessor = new SafeDirectMethod<Object>() {
                @Override
                public Object invoke(Object instance, Object... args) {
                    try {
                        return instance.getClass().getDeclaredMethod("clone").invoke(instance, args);
                    } catch (Throwable t) {
                        throw new RuntimeException("Failed to clone:", t);
                    }
                }
            };
        }
        objectCloneMethod = objectCloneMethodAccessor;
    }

    /**
     * Obtains the unboxed type (int) from a boxed type (Integer)<br>
     * If the input type has no unboxed type, null is returned
     *
     * @param boxedType to convert
     * @return the unboxed type
     */
    public static Class<?> getUnboxedType(Class<?> boxedType) {
        return boxedToUnboxed.get(boxedType);
    }

    /**
     * Obtains the boxed type (Integer) from an unboxed type (int)<br>
     * If the input type has no boxed type, null is returned
     *
     * @param unboxedType to convert
     * @return the boxed type
     */
    public static Class<?> getBoxedType(Class<?> unboxedType) {
        return unboxedToBoxed.get(unboxedType);
    }

    /**
     * Checks if both values are null or the values equal each other
     *
     * @param value1 to use
     * @param value2 to use
     * @return True if value1 and value2 equal or are both null, False if not
     */
    public static boolean bothNullOrEqual(Object value1, Object value2) {
        return value1 == null ? value2 == null : value1.equals(value2);
    }

    /**
     * Checks if a Map is null or empty
     *
     * @param map to check
     * @return True if the collection is null or empty
     */
    public static boolean nullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Checks if a Collection is null or empty
     *
     * @param collection to check
     * @return True if the collection is null or empty
     */
    public static boolean nullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Checks if a String is null or empty
     *
     * @param text to check
     * @return True if the text is null or empty
     */
    public static boolean nullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }

    /**
     * Checks if an Item Stack is null or empty
     *
     * @param item to check
     * @return True if the item is null or empty
     */
    public static boolean nullOrEmpty(org.bukkit.inventory.ItemStack item) {
        return item == null || MaterialUtil.getTypeId(item) == 0 || item.getAmount() < 1;
    }

    /**
     * Checks if an array is null or empty
     *
     * @param array to check
     * @return True if the item is null or empty
     */
    public static boolean nullOrEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks whether an element index is within range of a collection
     *
     * @param collection to check
     * @param index to check
     * @return True if it is in bounds, False if not
     */
    public static boolean isInBounds(Collection<?> collection, int index) {
        return collection != null && index >= 0 && index < collection.size();
    }

    /**
     * Checks whether an element index is within range of an array<br>
     * Both Object and primitive arrays are supported
     *
     * @param array to check
     * @param index to check
     * @return True if it is in bounds, False if not
     */
    public static boolean isInBounds(Object[] array, int index) {
        return array != null && index >= 0 && index < array.length;
    }

    /**
     * Checks whether an element index is within range of an array<br>
     * Both Object and primitive arrays are supported
     *
     * @param array to check
     * @param index to check
     * @return True if it is in bounds, False if not
     */
    public static boolean isInBounds(Object array, int index) {
        return array != null && index >= 0 && index < Array.getLength(array);
    }

    /**
     * Returns the default value if the input value is null
     *
     * @param value to fix
     * @param def to return if the value is null
     * @return the value or the default
     */
    public static <T> T fixNull(T value, T def) {
        return value == null ? def : value;
    }

    /**
     * Appends one or more elements to an array This method allocates a new
     * Array of the same type as the old array, with the size of array + values.
     *
     * @param array input array to append to
     * @param values to append to array
     * @return new Array with the values from array and values
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] appendArray(T[] array, T... values) {
        if (nullOrEmpty(array)) {
            return values;
        }
        if (nullOrEmpty(values)) {
            return array;
        }
        T[] rval = createArray((Class<T>) array.getClass().getComponentType(), array.length + values.length);
        System.arraycopy(array, 0, rval, 0, array.length);
        System.arraycopy(values, 0, rval, array.length, values.length);
        return rval;
    }

    /**
     * Allocates a new array of the same length and writes the contents to this
     * new array. Unlike {@link #cloneAll(Object[])}, this method does not
     * individually clone the elements
     *
     * @param array to re-allocate as a new array
     * @return new array with the contents of the input array
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] cloneArray(T[] array) {
        if (array == null) {
            return null;
        }
        final int length = array.length;
        T[] rval = createArray((Class<T>) array.getClass().getComponentType(), length);
        System.arraycopy(array, 0, rval, 0, length);
        return rval;
    }

    /**
     * Clones a single value
     *
     * @param value to clone
     * @return cloned value
     * @throws RuntimeException if cloning fails
     */
    @SuppressWarnings("unchecked")
    public static <T> T clone(T value) {
        if (value == null) {
            return null;
        }
        return (T) objectCloneMethod.invoke(value);
    }

    /**
     * Clones all elements of an array
     *
     * @param values to clone
     * @return a new, cloned array of cloned elements
     * @throws RuntimeException if cloning fails
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] cloneAll(T[] values) {
        if (values == null || values.length == 0) {
            return values;
        }
        try {
            final Class<T> compType = (Class<T>) values.getClass().getComponentType();
            final Method cloneMethod = compType.getDeclaredMethod("clone");
            T[] rval = createArray(compType, values.length);
            for (int i = 0; i < rval.length; i++) {
                final T value = values[i];
                if (value != null) {
                    rval[i] = (T) cloneMethod.invoke(value);
                }
            }
            return rval;
        } catch (Exception ex) {
            throw new RuntimeException("Cloning was not possible:", ex);
        }
    }

    /**
     * Obtains the Class instance representing an array of the component type
     * specified. For example:<br>
     * - Integer.class -> Integer[].class<br>
     * - int.class -> int[].class
     *
     * @param componentType to convert
     * @return array type
     */
    public static Class<?> getArrayType(Class<?> componentType) {
        if (componentType.isPrimitive()) {
            return Array.newInstance(componentType, 0).getClass();
        } else {
            return CommonUtil.getClass("[L" + componentType.getName() + ";");
        }
    }

    /**
     * Tries to get a specific element from a list. The default value is
     * returned when:<br>
     * - The list is null<br>
     * - The list index is out of bounds
     *
     * @param list to get an element from
     * @param index of the element to get
     * @param def value to return on failure
     * @return The list element, or the default value
     */
    public static <T> T getList(List<T> list, int index, T def) {
        return isInBounds(list, index) ? list.get(index) : def;
    }

    /**
     * Tries to get a specific element from an array. The default value is
     * returned when:<br>
     * - The array is null<br>
     * - The array index is out of bounds
     *
     * @param array to get an element from
     * @param index of the element to get
     * @param def value to return on failure
     * @return The array element, or the default value
     */
    public static <T> T getArray(T[] array, int index, T def) {
        return isInBounds(array, index) ? array[index] : def;
    }

    /**
     * Constructs a new 1-dimensional Array of a given type and length
     *
     * @param type of the new Array
     * @param length of the new Array
     * @return new Array
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] createArray(Class<T> type, int length) {
        return (T[]) Array.newInstance(type, length);
    }

    /**
     * Converts a collection to an Array
     *
     * @param collection to convert
     * @param type of the collection and the array to return (can not be
     * primitive)
     * @return new Array containing the elements in the collection
     */
    public static <T> T[] toArray(Collection<?> collection, Class<T> type) {
        return collection.toArray(createArray(type, collection.size()));
    }

    /**
     * Adds all the elements of an array to a Collection
     *
     * @param collection to add elements to
     * @param array to add to the Collection
     * @return True if the collection changed as a result of the call, False if
     * not.
     */
    public static <E, T extends E> boolean addArray(Collection<E> collection, T... array) {
        if (array.length > 20) {
            return collection.addAll(Arrays.asList(array));
        } else {
            boolean changed = false;
            for (T element : array) {
                changed |= collection.add(element);
            }
            return changed;
        }
    }

    /**
     * Removes all the elements of an array from a Collection
     *
     * @param collection to remove elements from
     * @param array to remove from the Collection
     * @return True if the collection changed as a result of the call, False if
     * not.
     */
    public static boolean removeArray(Collection<?> collection, Object... array) {
        if (array.length > 100) {
            return collection.removeAll(Arrays.asList(array));
        } else {
            boolean changed = false;
            for (Object element : array) {
                changed |= collection.remove(element);
            }
            return changed;
        }
    }

    /**
     * Removes or adds an element from/to a Collection, and returns whether
     * something has changed.
     *
     * @param collection to add or remove an element from
     * @param value to add or remove
     * @param add option: True to add, False to remove
     * @return True if the collection changed (element removed or added), False
     * if not
     */
    public static boolean addOrRemove(BlockSet collection, Block value, boolean add) {
        return add ? collection.add(value) : collection.remove(value);
    }

    /**
     * Removes or adds an element from/to a Collection, and returns whether
     * something has changed.
     *
     * @param collection to add or remove an element from
     * @param value to add or remove
     * @param add option: True to add, False to remove
     * @return True if the collection changed (element removed or added), False
     * if not
     */
    public static <T> boolean addOrRemove(Collection<T> collection, T value, boolean add) {
        return add ? collection.add(value) : collection.remove(value);
    }

    /**
     * Checks whether one map contains all the contents of another map
     *
     * @param map to check for contents
     * @param contents to check the map for
     * @return True if all contents are contained in the map, False if not
     */
    public static boolean containsAll(Map<?, ?> map, Map<?, ?> contents) {
        for (Map.Entry<?, ?> entry : contents.entrySet()) {
            Object value = map.get(entry.getKey());
            // Null value stored in the map?
            if (value == null) {
                if (entry.getValue() != null || !map.containsKey(entry.getKey())) {
                    return false;
                }
            } else if (!value.equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if an array of values contains the value specified
     *
     * @param value to find
     * @param values to search in
     * @return True if it is contained, False if not
     */
    public static <T> boolean contains(T value, T... values) {
        for (T v : values) {
            if (bothNullOrEqual(v, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a list of bytes contains the byte specified
     *
     * @param value to find
     * @param values to search in
     * @return True if it is contained, False if not
     */
    public static boolean containsByte(byte value, byte... values) {
        for (byte v : values) {
            if (v == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a sequence of characters contains the character specified
     *
     * @param value to find
     * @param sequence of char values to search in
     * @return True if it is contained, False if not
     */
    public static boolean containsChar(char value, CharSequence sequence) {
        for (int i = 0; i < sequence.length(); i++) {
            if (sequence.charAt(i) == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a list of characters contains the character specified
     *
     * @param value to find
     * @param values to search in
     * @return True if it is contained, False if not
     */
    public static boolean containsChar(char value, char... values) {
        for (char v : values) {
            if (v == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a list of integers contains the integer specified
     *
     * @param value to find
     * @param values to search in
     * @return True if it is contained, False if not
     */
    public static boolean containsInt(int value, int... values) {
        for (int v : values) {
            if (v == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a list of booleans contains the boolean specified
     *
     * @param value to find
     * @param values to search in
     * @return True if it is contained, False if not
     */
    public static boolean containsBool(boolean value, boolean... values) {
        for (boolean v : values) {
            if (v == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Skips elements from an iterator by calling 'next' a given amount of times
     * (if possible). If the count exceeds the amount of elements the iterator
     * provides, further elements are ignored. In that case, calling
     * {@link Iterator#hasNext()} would yield false.
     *
     * @param iterator to skip
     * @param count to skip
     * @return the iterator
     */
    public static <T extends Iterator<?>> T skipIterator(T iterator, int count) {
        for (int i = 0; i < count && iterator.hasNext(); i++) {
            iterator.next();
        }
        return iterator;
    }

    /**
     * Obtains the key at which a specific value is mapped to in a Map. This is
     * essentially the reverse key lookup in a map, and is thus slow. For
     * 'BiMap' maps, the inverse is used to obtain the key faster.
     *
     * @param map to check
     * @param value to look for
     * @return key the value is mapped to, or null if not found
     */
    public static <K, V> K getKeyAtValue(Map<K, V> map, V value) {
        if (map instanceof BiMap) {
            return ((BiMap<K, V>) map).inverse().get(value);
        }
        for (Entry<K, V> entry : map.entrySet()) {
            if (bothNullOrEqual(entry.getValue(), value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
