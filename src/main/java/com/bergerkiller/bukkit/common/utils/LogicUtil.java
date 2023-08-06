package com.bergerkiller.bukkit.common.utils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.bergerkiller.bukkit.common.bases.CheckedSupplier;
import com.bergerkiller.bukkit.common.bases.DeferredSupplier;
import com.bergerkiller.bukkit.common.collections.BlockSet;
import com.bergerkiller.bukkit.common.collections.ImmutableArrayList;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.google.common.collect.BiMap;

/**
 * Logic operations, such as contains checks and collection-type transformations
 */
public class LogicUtil {
    private static Map<Class<?>, UnaryOperator<Object>> _cloneMethodCache = Collections.emptyMap();
    private static final Consumer<Object> _noopConsumer = obj -> {};
    private static final Predicate<Object> _alwaysTruePredicate = obj -> true;
    private static final Predicate<Object> _alwaysFalsePredicate = obj -> false;
    private static final Supplier<Object> _nullSupplier = () -> null;
    private static final WeakReference<Object> _nullWeakReference = new WeakReference<Object>(null);

    @SuppressWarnings("unchecked")
    private static final ItemSynchronizer _identityItemSynchronizer = new ItemSynchronizer<Object, Object>() {
        @Override
        public boolean isItem(Object item, Object value) {
            return Objects.equals(item, value);
        }

        @Override
        public Object onAdded(Object value) {
            return value;
        }

        @Override
        public void onRemoved(Object item) {
        }
    };

    private static final ExceptionallyAsyncHandler _exceptionallyAsyncHandler;
    static {
        ExceptionallyAsyncHandler handler;
        try {
            CompletableFuture.class.getMethod("exceptionallyAsync", Function.class);
            handler = new ExceptionallyAsyncHandler() {
                @Override
                public <T> CompletableFuture<T> exceptionallyAsync(CompletableFuture<T> future,
                                                                   Function<Throwable, ? extends T> fn,
                                                                   Executor executor
                ) {
                    return future.exceptionallyAsync(fn, executor);
                }
            };
        } catch (NoSuchMethodException ex) {
            handler = new ExceptionallyAsyncHandler() {
                @Override
                public <T> CompletableFuture<T> exceptionallyAsync(final CompletableFuture<T> future,
                                                                   final Function<Throwable, ? extends T> fn,
                                                                   final Executor executor
                ) {
                    return future.handleAsync((r, t) -> {
                        return (t == null) ? r : fn.apply(t);
                    }, executor);
                }
            };
        }
        _exceptionallyAsyncHandler = handler;
    }

    static {
        // Used by ImplicitlySharedSet/List, so it's important these are pre-registered
        registerCloneMethod(java.util.ArrayList.class, a -> (java.util.ArrayList<?>) a.clone());
        registerCloneMethod(java.util.LinkedList.class, java.util.LinkedList<Object>::new);
        registerCloneMethod(java.util.Vector.class, v -> (java.util.Vector<?>) v.clone());
        registerCloneMethod(java.util.TreeSet.class, t -> (java.util.TreeSet<?>) t.clone());
        registerCloneMethod(java.util.LinkedHashSet.class, java.util.LinkedHashSet<Object>::new);
        registerCloneMethod(java.util.HashSet.class, s -> (java.util.HashSet<?>) s.clone());
    }

    /**
     * Obtains the unboxed type (int) from a boxed type (Integer)<br>
     * If the input type has no unboxed type, null is returned
     *
     * @param boxedType to convert
     * @return the unboxed type
     */
    public static Class<?> getUnboxedType(Class<?> boxedType) {
        return BoxedType.getUnboxedType(boxedType);
    }

    /**
     * Obtains the boxed type (Integer) from an unboxed type (int)<br>
     * If the input type has no boxed type, null is returned
     *
     * @param unboxedType to convert
     * @return the boxed type
     */
    public static Class<?> getBoxedType(Class<?> unboxedType) {
        return BoxedType.getBoxedType(unboxedType);
    }

    /**
     * Obtains the boxed type (Integer) from an unboxed type (int)<br>
     * If the input type has no boxed type, it is returned as-is.
     * 
     * @param type to get the boxed type for
     * @return boxed type, or the type if it has no boxed type
     */
    public static Class<?> tryBoxType(Class<?> type) {
        return BoxedType.tryBoxType(type);
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
     * Checks if both String values are null or the values equal each other, ignoring case
     *
     * @param value1 to use
     * @param value2 to use
     * @return True if value1 and value2 equal, ignoring case, or are both null, False if not
     */
    public static boolean bothNullOrEqualIgnoreCase(String value1, String value2) {
        return value1 == null ? value2 == null : value1.equalsIgnoreCase(value2);
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
        return ItemUtil.isEmpty(item);
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
     * Checks whether an element index is within range of a collection
     *
     * @param collection to check
     * @param index      to check
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
     * Applies a function to an input argument if it is not null, returns def if the value is null.
     * 
     * @param input Input argument of the function
     * @param methodToCall The method to call if input is not null, with the input as argument
     * @param def The default value to return if null
     * @return The result from calling the method with the input if not null, def otherwise
     */
    public static <I, O> O applyIfNotNull(I input, java.util.function.Function<I, O> methodToCall, O def) {
        return (input == null) ? def : methodToCall.apply(input);
    }

    /**
     * Returns the default value if the input value is null
     *
     * @param value to fix
     * @param def   to return if the value is null
     * @return the value or the default
     */
    public static <T> T fixNull(T value, T def) {
        return value == null ? def : value;
    }

    /**
     * Tries to create an object using a supplier that can possibly throw an exception.
     * If this happens, the error handler is used to construct a value, instead.
     * Checked exceptions are handled too, this method primarily exists to support
     * the java Supplier type directly.
     * 
     * @param <T> Type of value supplied
     * @param supplier Main supplier of the result, that can throw an exception
     * @param errorHandler Function that produces an alternative value, with the error as input
     * @return supplied value, or the error handler output if an exception occurs
     */
    public static <T> T tryCreateUsingSupplier(Supplier<T> supplier, Function<Throwable, T> errorHandler) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            return errorHandler.apply(t);
        }
    }

    /**
     * Tries to create an object using a supplier that can possibly throw an exception.
     * If this happens, the error handler is used to construct a value, instead.
     * Checked exceptions are handled too, this method primarily exists to support
     * the java Supplier type directly.
     *
     * @param <T> Type of value supplied
     * @param supplier Main checked supplier of the result, that can throw an exception
     * @param errorHandler Function that produces an alternative value, with the error as input
     * @return supplied value, or the error handler output if an exception occurs
     */
    public static <T> T tryCreateUsingCheckedSupplier(CheckedSupplier<T> supplier, Function<Throwable, T> errorHandler) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            return errorHandler.apply(t);
        }
    }

    /**
     * Tries to create an object using a supplier that can possibly throw an exception.
     * If this happens, the error handler is used to construct a value, instead.
     * 
     * @param <T>
     * @param constructor Main supplier of the result, that can throw a checked exception
     * @param errorHandler Function that produces an alternative value, with the error as input
     * @return supplied value, or the error handler output if an exception occurs
     */
    public static <T> T tryCreate(CheckedSupplier<T> constructor, Function<Throwable, T> errorHandler) {
        try {
            return constructor.get();
        } catch (Throwable t) {
            return errorHandler.apply(t);
        }
    }

    /**
     * Allocates a new array of the same length and writes the contents to this new array. Unlike
     * {@link #cloneAll(Object[])}, this method does not individually clone the elements
     *
     * @param array to re-allocate as a new array
     * @return new array with the contents of the input array
     * @deprecated Simply use array.clone() instead.
     */
    @Deprecated
    public static <T> T[] cloneArray(T[] array) {
        return (array == null) ? null : array.clone();
    }

    /**
     * Registers a custom value cloning function for the type specified, overriding any automatically
     * or previously decided cloning technique. This will be used by clone operations of this class.
     *
     * @param <T> Value type
     * @param type Type
     * @param op Clone function
     */
    @SuppressWarnings("unchecked")
    public static <T> void registerCloneMethod(Class<T> type, UnaryOperator<T> op) {
        synchronized (LogicUtil.class) {
            Map<Class<?>, UnaryOperator<Object>> newMap = new HashMap<>(_cloneMethodCache);
            newMap.put(type, (UnaryOperator<Object>) op);
            _cloneMethodCache = newMap;
        }
    }

    /**
     * Identifies the clone() method of a Class type. Caches the result for fast re-use.
     *
     * @param <T>
     * @param value Value to deduce a clone method for using Type
     * @return clone method operator
     * @throws IllegalArgumentException If the type has no clone method or input value is null
     */
    @SuppressWarnings("unchecked")
    public static <T> UnaryOperator<T> findCloneMethod(T value) {
        Class<T> type;
        try {
            type = (Class<T>) value.getClass();
        } catch (NullPointerException ex) {
            if (value == null) {
                throw new IllegalArgumentException("Input value is null");
            } else {
                throw ex;
            }
        }
        return findCloneMethod(type);
    }

    /**
     * Identifies the clone() method of a Class type. Caches the result for fast re-use.
     *
     * @param <T>
     * @param type
     * @return clone method operator
     * @throws IllegalArgumentException If the type has no clone method
     */
    public static <T> UnaryOperator<T> findCloneMethod(Class<T> type) {
        return CommonUtil.unsafeCast(synchronizeCopyOnWrite(LogicUtil.class, () -> _cloneMethodCache, type, (map, inType) -> map.get(inType), (map, inType) -> {
            // Generate new operator
            Method cloneMethod;
            try {
                cloneMethod = type.getMethod("clone");
            } catch (NoSuchMethodException | SecurityException e) {
                throw new IllegalArgumentException("Object of type " + type.getName() + " can not be cloned");
            }
            final FastMethod<Object> fm = new FastMethod<Object>(cloneMethod);
            fm.forceInitialization();
            UnaryOperator<Object> op = fm::invoke;

            // Store in map
            Map<Class<?>, UnaryOperator<Object>> newMap = new HashMap<>(map);
            newMap.put(inType, op);
            _cloneMethodCache = newMap;

            return op;
        }));
    }

    /**
     * Clones a single value. Finds the clone() method of the value type,
     * and calls it on the value to clone it. If the value can not be cloned,
     * then an exception is thrown.
     *
     * @param value to clone
     * @return cloned value
     * @throws RuntimeException if cloning fails
     */
    @SuppressWarnings("unchecked")
    public static <T> T clone(T value) {
        if (value == null) {
            return null;
        } else {
            return findCloneMethod((Class<T>) value.getClass()).apply(value);
        }
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
        if (values == null) {
            return null;
        }
        int len = values.length;
        if (len == 0) {
            return values;
        } else {
            Class<T> componentType = (Class<T>) values.getClass().getComponentType();
            UnaryOperator<T> cloneFunction = findCloneMethod(componentType);
            T[] copy = LogicUtil.createArray(componentType, len);
            for (int i = 0; i < len; i++) {
                T input = values[i];
                if (input != null) {
                    copy[i] = cloneFunction.apply(input);
                }
            }
            return copy;
        }
    }

    /**
     * Deep-clones an array and all it's elements using the specified clone function.
     * If the input array is empty, it is returned as-is without creating a new array.
     *
     * @param <T>
     * @param values Array to clone
     * @param cloneFunction Function to apply to every element to clone it.
     *                      Null values are not cloned, storing null in the copy.
     * @return Cloned array with cloned elements, or the input array if empty or null
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] cloneAll(T[] values, UnaryOperator<T> cloneFunction) {
        if (values == null) {
            return null;
        }
        int len = values.length;
        if (len == 0) {
            return values;
        } else {
            T[] copy = LogicUtil.createArray((Class<T>) values.getClass().getComponentType(), len);
            for (int i = 0; i < len; i++) {
                T input = values[i];
                if (input != null) {
                    copy[i] = cloneFunction.apply(input);
                }
            }
            return copy;
        }
    }

    /**
     * Obtains the Class instance representing an array of the component type specified. For example:<br>
     * - Integer.class -> Integer[].class<br>
     * - int.class -> int[].class
     *
     * @param componentType to convert
     * @return array type
     */
    public static Class<?> getArrayType(Class<?> componentType) {
        if (componentType.isPrimitive()) {
            try {
                return Array.newInstance(componentType, 0).getClass();
            } catch (IllegalArgumentException ex) {
                if (componentType == void.class) {
                    throw new IllegalArgumentException("Cannot create an array of void");
                } else {
                    throw ex;
                }
            }
        } else {
            try {
                return Class.forName("[L" + componentType.getName() + ";");
            } catch (ClassNotFoundException e) {
                return Object[].class;
            }
        }
    }

    /**
     * Obtains the Class instance representing an array of the component type specified. For example:<br>
     * - Integer.class -> Integer[].class<br>
     * - int.class -> int[].class
     *
     * @param componentType to convert
     * @param levels        the amount of levels to create the array (e.g. 2=[][])
     * @return array type
     */
    public static Class<?> getArrayType(Class<?> componentType, int levels) {
        Class<?> type = componentType;
        while (levels-- > 0) {
            type = getArrayType(type);
        }
        return type;
    }

    /**
     * Tries to get a specific element from a list. The default value is returned when:<br>
     * - The list is null<br>
     * - The list index is out of bounds
     *
     * @param list  to get an element from
     * @param index of the element to get
     * @param def   value to return on failure
     * @return The list element, or the default value
     */
    public static <T> T getList(List<T> list, int index, T def) {
        return isInBounds(list, index) ? list.get(index) : def;
    }

    /**
     * Tries to get a specific element from an array. The default value is returned when:<br>
     * - The array is null<br>
     * - The array index is out of bounds
     *
     * @param array to get an element from
     * @param index of the element to get
     * @param def   value to return on failure
     * @return The array element, or the default value
     */
    public static <T> T getArray(T[] array, int index, T def) {
        return isInBounds(array, index) ? array[index] : def;
    }

    /**
     * Constructs a new 1-dimensional Array of a given type and length
     *
     * @param type   of the new Array
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
     * @param type       of the collection and the array to return (can not be primitive)
     * @return new Array containing the elements in the collection
     */
    public static <T> T[] toArray(Collection<?> collection, Class<T> type) {
        return collection.toArray(createArray(type, collection.size()));
    }

    /**
     * Adds all the elements of an array to a Collection
     *
     * @param collection to add elements to
     * @param array      to add to the Collection
     * @return True if the collection changed as a result of the call, False if not.
     */
    @SafeVarargs
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
     * @param array      to remove from the Collection
     * @return True if the collection changed as a result of the call, False if not.
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
     * Removes a single item from an array, returning a new array of size length-1 with the element removed. If the element
     * could not be found in the input array, the input array is returned unchanged and no copy is created.
     * 
     * @param input array
     * @param value in the array to remove
     * @return new array copy with the value removed, same as input if unchanged
     */
    public static <T> T[] removeArrayElement(T[] input, T value) {
        for (int index = 0; index < input.length; index++) {
            if (bothNullOrEqual(input[index], value)) {
                return removeArrayElement(input, index);
            }
        }
        return input;
    }

    /**
     * Removes a single item from an array, returning a new array of size length-1.
     * 
     * @param input array
     * @param index of the element to remove
     * @return new array copy of input array, with the element at the index removed
     */
    public static <T> T[] removeArrayElement(T[] input, int index) {
        int len = input.length;
        if (index < 0 || index >= len) {
            return input;
        } else if (index == 0) {
            return Arrays.copyOfRange(input, 1, len);
        } else if (index == (len - 1)) {
            return Arrays.copyOf(input, len - 1);
        }

        T[] rval = CommonUtil.unsafeCast(createArray(input.getClass().getComponentType(), input.length - 1));
        System.arraycopy(input, 0, rval, 0, index);
        System.arraycopy(input, index + 1, rval, index, input.length - index - 1);
        return rval;
    }

    /**
     * Appends one or more elements to an array. This method allocates a new Array of the same type as the old array, with
     * the size of array + values. Values are appended to the end.
     *
     * @param array  input array to append to
     * @param values to append to array
     * @return new Array with the values from array and values
     * @see #appendArrayElement(Object[], Object)
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] appendArray(T[] array, T... values) {
        if (array == null) {
            return values;
        } else if (values == null) {
            return array;
        }

        int array_len = array.length;
        int values_len = values.length;
        if (array_len == 0) {
            return values;
        } else if (values_len == 0) {
            return array;
        }

        T[] rval = Arrays.copyOf(array, array_len + values_len);
        if (values_len == 1) {
            rval[array_len] = values[0];
        } else {
            System.arraycopy(values, 0, rval, array_len, values_len);
        }
        return rval;
    }

    /**
     * Copies an array with one extra length and put the element specified in the newly
     * allocated slot.
     *
     * @param <T>
     * @param input Input array
     * @param element Element to append to the end
     * @return New array
     */
    public static <T> T[] appendArrayElement(T[] input, T element) {
        int len = input.length;
        T[] new_arr = Arrays.copyOf(input, len + 1);
        new_arr[len] = element;
        return new_arr;
    }

    /**
     * Removes or adds an element from/to a Collection, and returns whether something has changed.
     *
     * @param collection to add or remove an element from
     * @param value      to add or remove
     * @param add        option: True to add, False to remove
     * @return True if the collection changed (element removed or added), False if not
     */
    public static boolean addOrRemove(BlockSet collection, Block value, boolean add) {
        return add ? collection.add(value) : collection.remove(value);
    }

    /**
     * Removes or adds an element from/to a Collection, and returns whether something has changed.
     *
     * @param collection to add or remove an element from
     * @param value      to add or remove
     * @param add        option: True to add, False to remove
     * @return True if the collection changed (element removed or added), False if not
     */
    public static <T> boolean addOrRemove(Collection<T> collection, T value, boolean add) {
        return add ? collection.add(value) : collection.remove(value);
    }

    /**
     * Checks whether one map contains all the contents of another map
     *
     * @param map      to check for contents
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
     * @param value  to find
     * @param values to search in
     * @return True if it is contained, False if not
     */
    @SafeVarargs
    public static <T> boolean contains(T value, T... values) {
        if (value == null) {
            for (T v : values) {
                if (v == null) {
                    return true;
                }
            }
        } else {
            for (T v : values) {
                if (value.equals(v)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a String array of values contains the value specified
     *
     * @param value  to find
     * @param values to search in
     * @return True if it is contained, False if not
     */
    @SafeVarargs
    public static boolean containsIgnoreCase(String value, String... values) {
        for (String v : values) {
            if (bothNullOrEqualIgnoreCase(v, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a list of bytes contains the byte specified
     *
     * @param value  to find
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
     * @param value    to find
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
     * @param value  to find
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
     * @param value  to find
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
     * @param value  to find
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
     * Skips elements from an iterator by calling 'next' a given amount of times (if possible). If the count exceeds the
     * amount of elements the iterator provides, further elements are ignored. In that case, calling
     * {@link Iterator#hasNext()} would yield false.
     *
     * @param iterator to skip
     * @param count    to skip
     * @return the iterator
     */
    public static <T extends Iterator<?>> T skipIterator(T iterator, int count) {
        for (int i = 0; i < count && iterator.hasNext(); i++) {
            iterator.next();
        }
        return iterator;
    }

    /**
     * Obtains the key at which a specific value is mapped to in a Map. This is essentially the reverse key lookup in a map,
     * and is thus slow. For 'BiMap' maps, the inverse is used to obtain the key faster.
     *
     * @param map   to check
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

    /**
     * Gets all the Class types of the objects in an array
     * 
     * @param values input object array
     * @return class types
     */
    public static Class<?>[] getTypes(Object[] values) {
        Class<?>[] result = new Class<?>[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = (values[i] == null) ? null : values[i].getClass();
        }
        return result;
    }

    /**
     * Synchronizes the items from one type to another, handling comparison, adding, and removal
     * 
     * @param <V> value type bound to the element
     * @param <E> element item type
     */
    public static interface ItemSynchronizer<V, E> {
        /**
         * Checks whether a given value belong to a certain item
         * 
         * @param item  to check
         * @param value to compare
         * @return True if the item belongs to the value, False if not
         */
        public boolean isItem(E item, V value);

        /**
         * Called when a new item needs to be added to the synchronized list
         * 
         * @param value to add
         * @return result entry to be added to the synchronized list
         */
        public E onAdded(V value);

        /**
         * Called when an item is about to be removed from the synchronized list
         * 
         * @param item to be removed
         */
        public void onRemoved(E item);

        /**
         * Returns an identity item synchronizer which allows for synchronizing
         * two collections with the same type of values stored
         * 
         * @param <V> value type bound to the element
         * @param <E> element item type
         * @return identity item synchronizer
         */
        @SuppressWarnings("unchecked")
        public static <V, E extends V> ItemSynchronizer<V, E> identity() {
            return (ItemSynchronizer<V, E>) _identityItemSynchronizer;
        }
    }

    /**
     * Synchronizes the contents of a list by taking over the items in a collection of values. The items will be inserted
     * into the list in the same order as the collection.
     * 
     * @param list         to synchronize
     * @param values       to synchronize in the list
     * @param synchronizer to use when synchronizing the collection with the list
     * @return True if the synchronized list changed, False if not
     */
    public static <V, E> boolean synchronizeList(List<E> list, Collection<? extends V> values, ItemSynchronizer<V, E> synchronizer) {
        return synchronizeList(list, (Iterable<? extends V>) values, synchronizer);
    }

    /**
     * Synchronizes the contents of a list by taking over the items in an iterable of values. The items will be inserted
     * into the list in the same order as the collection.
     * 
     * @param list         to synchronize
     * @param values       to synchronize in the list, iterated only ones
     * @param synchronizer to use when synchronizing the collection with the list
     * @return True if the synchronized list changed, False if not
     */
    public static <V, E> boolean synchronizeList(List<E> list, Iterable<? extends V> values, ItemSynchronizer<V, E> synchronizer) {
        boolean has_changes = false;
        Iterator<? extends V> value_iter = values.iterator();
        ListIterator<E> item_iter = list.listIterator();
        while (value_iter.hasNext()) {
            V value = value_iter.next();

            // Add a new item at the end of the list
            if (!item_iter.hasNext()) {
                item_iter.add(synchronizer.onAdded(value));
                has_changes = true;
                continue;
            }

            // Verify the next item matches the value
            E item = item_iter.next();
            if (synchronizer.isItem(item, value)) {
                continue;
            }

            // Remember the current iterating position when restoring
            // Find the item in the list. If it exists, remove it and add it to the front
            // If not found, create a new item at that index
            int old_index = item_iter.previousIndex();
            while (true) {
                if (item_iter.hasNext()) {
                    item = item_iter.next();
                    if (synchronizer.isItem(item, value)) {
                        item_iter.remove();
                        break;
                    }
                } else {
                    item = null;
                    break;
                }
            }
            item_iter = list.listIterator(old_index);
            if (item == null) {
                item_iter.add(synchronizer.onAdded(value));
            } else {
                item_iter.add(item);
            }
            has_changes = true;
        }

        // Remove all items that are past the items list
        while (item_iter.hasNext()) {
            E item = item_iter.next();
            synchronizer.onRemoved(item);
            item_iter.remove();
            has_changes = true;
        }

        return has_changes;
    }

    /**
     * Synchronizes the contents of a collection by taking over the items in another collection of values. The items will
     * not necessarily be inserted into the collection in the same order as the collection. Unlike
     * {@link #synchronizeList(List, Collection, ItemSynchronizer)} this method will not call the values
     * {@link Collection#iterator()} method unless absolutely needed.<br>
     * <br>
     * Because this logic depends on the {@link Collection#contains(Object)} method, the
     * {@link ItemSynchronizer#isItem(Object, Object)} is not used. The input collection and values
     * collection must hold the same value types.
     * 
     * @param collection   to synchronize
     * @param values       to synchronize in the collection, iterator() call is avoided when possible
     * @param synchronizer to use when synchronizing the collection with the collection
     * @return True if the synchronized collection changed, False if not
     */
    public static <E> boolean synchronizeUnordered(Collection<E> collection, Collection<E> values, ItemSynchronizer<E, E> synchronizer) {
        boolean changed = false;

        // If values are empty, clear the set and do nothing more
        if (values.isEmpty()) {
            if (!collection.isEmpty()) {
                for (E old_value : collection) {
                    synchronizer.onRemoved(old_value);
                }
                collection.clear();
                return true;
            }
            return false;
        }

        // Remove elements from the set that do not exist in the values
        Iterator<E> iter = collection.iterator();
        while (iter.hasNext()) {
            E old_value = iter.next();
            if (!values.contains(old_value)) {
                synchronizer.onRemoved(old_value);
                iter.remove();
                changed = true;
            }
        }

        // If the values set is larger than the Set, add the new items to the Set
        if (values.size() > collection.size()) {
            for (E new_value : values) {
                if (!collection.contains(new_value)) {
                    collection.add(synchronizer.onAdded(new_value));
                    changed = true;
                }
            }
        }

        // Done.
        return changed;
    }

    /**
     * Version of {@link Arrays#asList(Object...)} that produces an immutable list. Set
     * calls will not work, unlike the Java one. Empty arrays are optimized to return
     * the {@link Collections#emptyList()} instance.
     * 
     * @param array
     * @return List
     * @throws NullPointerException if input array is null
     */
    @SuppressWarnings("unchecked")
    public static <E> List<E> asImmutableList(E... array) {
        if (array.length == 0) {
            return Collections.EMPTY_LIST;
        } else {
            return new ImmutableArrayList<E>(array);
        }
    }

    /**
     * Serializes a configuration serializable to a map of key-value pairs. Also serializes any objects
     * in the output that are serializable, such as embedded Color or metadata values.
     * 
     * @param serializable
     * @return serialized contents, empty map if serializable is null
     */
    public static Map<String, Object> serializeDeep(ConfigurationSerializable serializable) {
        if (serializable == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> values = serializable.serialize();
        boolean cloned = false;
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof ConfigurationSerializable) {
                Map<String, Object> serialized = serializeDeep((ConfigurationSerializable) value);
                if (!cloned) {
                    if (values instanceof com.google.common.collect.ImmutableMap) {
                        // Optimization: avoid UnsupportedOps if we know this is the case
                        values = new LinkedHashMap<>(values);
                        cloned = true;
                    } else {
                        try {
                            entry.setValue(serialized);
                        } catch (UnsupportedOperationException ex) {
                            values = new LinkedHashMap<>(values);
                            cloned = true;
                        }
                    }
                }
                if (cloned) {
                    values.put(entry.getKey(), serialized);
                }
            }
        }
        return values;
    }

    /**
     * Gets a no-operation consumer, which accepts anything but does nothing
     * 
     * @param <T> Type
     * @return no-op consumer
     */
    @SuppressWarnings("unchecked")
    public static <T> Consumer<T> noopConsumer() {
        return (Consumer<T>) _noopConsumer;
    }

    /**
     * Gets a no-operation predicate, which accepts anything and always returns true
     * 
     * @param <T> Type
     * @return always true predicate
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> alwaysTruePredicate() {
        return (Predicate<T>) _alwaysTruePredicate;
    }

    /**
     * Gets a no-operation predicate, which accepts anything and always returns false
     * 
     * @param <T> Type
     * @return always false predicate
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> alwaysFalsePredicate() {
        return (Predicate<T>) _alwaysFalsePredicate;
    }

    /**
     * Creates a new supplier that returns a constant value
     *
     * @param <T> Type
     * @param value Value the supplier should return
     * @return Supplier returning value
     */
    public static <T> Supplier<T> constantSupplier(T value) {
        return () -> value;
    }

    /**
     * Creates a new supplier that returns null
     *
     * @param <T> Type
     * @return Supplier that returns null
     */
    @SuppressWarnings("unchecked")
    public static <T> Supplier<T> nullSupplier() {
        return (Supplier<T>) _nullSupplier;
    }

    /**
     * Generic implementation of CompletableFuture's exceptionallyAsync. Since JDK12 simply calls the
     * function of the CompletableFuture, but for older JDK's provides a fallback implementation.
     *
     * @param <T>
     * @param future CompletableFuture
     * @param fn Callback function when an exception occurs
     * @param executor Executor to use to call the callback function
     * @return CompletableFuture
     */
    public static <T> CompletableFuture<T> exceptionallyAsync(CompletableFuture<T> future,
                                                              Function<Throwable, ? extends T> fn,
                                                              Executor executor
    ) {
        return _exceptionallyAsyncHandler.exceptionallyAsync(future, fn, executor);
    }

    private static interface ExceptionallyAsyncHandler {
        <T> CompletableFuture<T> exceptionallyAsync(CompletableFuture<T> future,
                                                    Function<Throwable, ? extends T> fn,
                                                    Executor executor);
    }

    /**
     * Caches the result of a supplier, so it only has to be called once. This is
     * useful if initializing the value takes some time, or the getter imposes
     * an overhead. The returned supplier is thread-safe. Multiple gets that
     * occur from different threads will guaranteed only initialize the value once.
     *
     * @param <T> Type of value
     * @param supplier Base supplier to call to get the needed value
     * @return Cached supplier
     */
    public static <T> DeferredSupplier<T> deferred(Supplier<T> supplier) {
        return DeferredSupplier.of(supplier);
    }

    /**
     * Returns a null-initialized weak reference constant
     *
     * @param <T> Element type
     * @return Weak reference constant, get() will yield null.
     */
    @SuppressWarnings("unchecked")
    public static <T> WeakReference<T> nullWeakReference() {
        return (WeakReference<T>) _nullWeakReference;
    }

    /**
     * Performs copy-on-write logic. Tries reading a value from a visible immutable mapping
     * first. If that fails, the input lock is synchronized on and another read is attempted.
     * If that also (still) fails, then the computer function is called while still synchronized,
     * to write a new value to the mapping.<br>
     * <br>
     * The lock is used as input for the source function.
     *
     * @param <L> Lock type, input for mapping source type
     * @param <S> Mapping source type
     * @param <K> Key type
     * @param <V> Value type
     * @param lock Lock to synchronize on when computing
     * @param source Source function that returns the live state of the immutable mapping from a Lock instance
     * @param key Input Key
     * @param getter Public getter function, accepting the source and key. Should return null to compute a new value
     * @param computer Synchronized new-value computer function, accepting the source and key
     * @return Gotten or computed Value mapped to Key
     */
    public static <L, S, K, V> V synchronizeCopyOnWrite(L lock, Function<L, S> source, K key, BiFunction<S, K, V> getter, BiFunction<S, K, V> computer) {
        V result = getter.apply(source.apply(lock), key);
        if (result == null) {
            synchronized (lock) {
                S sourceLive = source.apply(lock);
                result = getter.apply(sourceLive, key);
                if (result == null) {
                    result = computer.apply(sourceLive, key);
                }
            }
        }
        return result;
    }

    /**
     * Performs copy-on-write logic. Tries reading a value from a visible immutable mapping
     * first. If that fails, the input lock is synchronized on and another read is attempted.
     * If that also (still) fails, then the computer function is called while still synchronized,
     * to write a new value to the mapping.
     *
     * @param <S> Mapping source type
     * @param <K> Key type
     * @param <V> Value type
     * @param lock Lock to synchronize on when computing
     * @param source Source supplier that returns the live state of the immutable mapping
     * @param key Input Key
     * @param getter Public getter function, accepting the source and key. Should return null to compute a new value
     * @param computer Synchronized new-value computer function, accepting the source and key
     * @return Gotten or computed Value mapped to Key
     */
    public static <S, K, V> V synchronizeCopyOnWrite(Object lock, Supplier<S> source, K key, BiFunction<S, K, V> getter, BiFunction<S, K, V> computer) {
        V result = getter.apply(source.get(), key);
        if (result == null) {
            synchronized (lock) {
                S sourceLive = source.get();
                result = getter.apply(sourceLive, key);
                if (result == null) {
                    result = computer.apply(sourceLive, key);
                }
            }
        }
        return result;
    }

    /**
     * Performs copy-on-write logic. Tries reading a value from a visible immutable mapping
     * first. If that fails, the input lock is synchronized on and another read is attempted.
     * If that also (still) fails, then the computer function is called while still synchronized,
     * to write a new value to the mapping.
     *
     * @param <V> Value type
     * @param lock Lock to synchronize on when computing
     * @param getter Public getter supplier, should return null to compute a new value
     * @param computer Synchronized new-value computer supplier
     * @return Gotten or computed Value
     */
    public static <V> V synchronizeCopyOnWrite(Object lock, Supplier<V> getter, Supplier<V> computer) {
        V result = getter.get();
        if (result == null) {
            synchronized (lock) {
                result = getter.get();
                if (result == null) {
                    result = computer.get();
                }
            }
        }
        return result;
    }

    /**
     * Calls {@link Supplier#get()} on a supplier. Useful for initializing fields
     * using a lambda.
     *
     * @param supplier Supplier
     * @return Result of get()
     * @param <T> Supplier type
     */
    public static <T> T make(Supplier<T> supplier) {
        return supplier.get();
    }

    /**
     * Tries to call {@link CheckedSupplier#get()} on a supplier. If this throws an
     * exception, returns the default value instead. Use case is trying to find classes
     * by name, for example.
     *
     * @param supplier Supplier
     * @param defaultValue Default value to return if get() throws
     * @return Supplier result of get(), or the default value
     * @param <T> Supplier type
     */
    public static <T> T tryMake(CheckedSupplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            return defaultValue;
        }
    }
}
