package com.bergerkiller.bukkit.common.nbt;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.nbt.NBTBaseHandle;
import com.bergerkiller.generated.net.minecraft.nbt.NBTTagListHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingIterator;
import com.bergerkiller.mountiplex.conversion.util.ConvertingListIterator;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;

/**
 * <b>Data</b> represents actual data stored by the tag. This can be:<br>
 * <u>List&lt;CommonTag&gt;, Map&lt;String, CommonTag&gt;, byte, short, int, long, float,
 * double, byte[], int[], String</u><br><br>
 * <p>
 * Data can be retrieved using <b>getValue()</b>, set using <b>setValue()</b>
 * and added using <b>addValue()</b><br>
 * The value setting and adding methods also accept tags, both NBTBase and
 * CommonTag<br>
 * The <b>set/get/add</b> methods operate on {@link CommonTag} instances only
 * and can generally be ignored
 */
@SuppressWarnings("unchecked")
public class CommonTagList extends CommonTag implements List<CommonTag> {

    /**
     * A read-only EMPTY tag list. This tag cannot be modified!
     */
    public static final CommonTagList EMPTY = makeReadOnly(new CommonTagList());

    public CommonTagList() {
        this(new ArrayList<CommonTag>());
    }

    public CommonTagList(Object... values) {
        this(new ArrayList<Object>(Arrays.asList(values)));
    }

    public CommonTagList(List<?> data) {
        this((Object) data);
    }

    public CommonTagList(Object value) {
        super(value);
    }

    public CommonTagList(NBTTagListHandle handle) {
        super(handle);
    }

    @Override
    public NBTTagListHandle getBackingHandle() {
        return (NBTTagListHandle) handle;
    }

    @Override
    public CommonTagList clone() {
        return new CommonTagList((NBTTagListHandle) handle.clone());
    }

    @Override
    public List<CommonTag> getData() {
        return (List<CommonTag>) super.getData();
    }

    @Override
    protected List<Object> getRawData() {
        return (List<Object>) super.getRawData();
    }

    @Override
    public int size() {
        return getBackingHandle().size();
    }

    @Override
    public boolean isEmpty() {
        return getBackingHandle().isEmpty();
    }

    @Override
    public void clear() {
        assertWritable();
        getBackingHandle().clear();
    }

    /**
     * Gets the value contained at a given index. Possible returned types:<br>
     * <u>List&lt;CommonTag&gt;, Map&lt;String, CommonTag&gt;, byte, short, int, long,
     * float, double, byte[], int[], String</u>
     *
     * @param index of the element value to get
     * @return element value
     */
    public Object getValue(int index) {
        return wrapRawData(NBTBaseHandle.getDataForHandle(NBTTagListHandle.T.get_at.raw.invoke(getRawHandle(), index)),
                readOnly);
    }

    /**
     * Gets the value contained at a given index. Possible returned types:<br>
     * <u>List&lt;CommonTag&gt;, Map&lt;String, CommonTag&gt;, byte, short, int, long,
     * float, double, byte[], int[], String</u>
     *
     * @param index of the element value to get
     * @param type of element value to get
     * @return element value, or null if type conversion fails
     */
    public <T> T getValue(int index, Class<T> type) {
        return Conversion.convert(getValue(index), type, null);
    }

    /**
     * Gets the value contained at a given index. Possible returned types:<br>
     * <u>List&lt;CommonTag&gt;, Map&lt;String, CommonTag&gt;, byte, short, int, long,
     * float, double, byte[], int[], String</u>
     *
     * @param index of the element value to get
     * @param def value to return if type conversion fails (can not be null)
     * @return element value, or null if type conversion fails
     */
    public <T> T getValue(int index, T def) {
        return Conversion.convert(getValue(index), def);
    }

    /**
     * Gets the value contained at a given index. Possible returned types:<br>
     * <u>List&lt;CommonTag&gt;, Map&lt;String, CommonTag&gt;, byte, short, int, long,
     * float, double, byte[], int[], String</u>
     *
     * @param index of the element value to get
     * @param type of element value to get
     * @param def value to return if type conversion fails
     * @return element value, or def if type conversion fails
     */
    public <T> T getValue(int index, Class<T> type, T def) {
        return Conversion.convert(getValue(index), type, def);
    }

    /**
     * Sets a single tag. Supported element types:<br>
     * <u>NBTBase, CommonTag, byte, short, int, long, float, double, byte[],
     * int[], String</u>
     *
     * @param index to set at
     * @param element to set to
     */
    public void setValue(int index, Object element) {
        assertWritable();
        NBTTagListHandle.T.set_at.raw.invoke(getRawHandle(), index, NBTBaseHandle.createRawHandleForData(element));
    }

    /**
     * Adds a single tag. Supported element types:<br>
     * <u>NBTBase, CommonTag, byte, short, int, long, float, double, byte[],
     * int[], String</u>
     *
     * @param index to add at
     * @param element to add
     */
    public void addValue(int index, Object element) {
        assertWritable();
        NBTTagListHandle.T.add_at.raw.invoke(getRawHandle(), index, NBTBaseHandle.createRawHandleForData(element));
    }

    /**
     * Adds a single tag. Supported element types:<br>
     * <u>NBTBase, CommonTag, byte, short, int, long, float, double, byte[],
     * int[], String</u>
     *
     * @param element to add
     */
    public void addValue(Object element) {
        assertWritable();
        NBTTagListHandle.T.add.raw.invoke(getRawHandle(), NBTBaseHandle.createRawHandleForData(element));
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof CommonTag) {
            return getRawData().indexOf(((CommonTag) o).getRawHandle());
        } else if (NBTBaseHandle.isDataSupportedNatively(o)) {
            return getRawData().indexOf(NBTBaseHandle.createRawHandleForData(o));
        } else {
            return -1;
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o instanceof CommonTag) {
            return getRawData().lastIndexOf(((CommonTag) o).getRawHandle());
        } else if (NBTBaseHandle.isDataSupportedNatively(o)) {
            return getRawData().lastIndexOf(NBTBaseHandle.createRawHandleForData(o));
        } else {
            return -1;
        }
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public boolean remove(Object o) {
        assertWritable();
        int index = indexOf(o);
        if (index == -1) {
            return false;
        } else {
            remove(index);
            return true;
        }
    }

    @Override
    public CommonTag get(int index) {
        CommonTag tag = getBackingHandle().get_at(index).toCommonTag();
        tag.readOnly = this.readOnly;
        return tag;
    }

    @Override
    public CommonTag set(int index, CommonTag element) {
        assertWritable();
        return getBackingHandle().set_at(index, element.getBackingHandle()).toCommonTag();
    }

    @Override
    public CommonTag remove(int index) {
        assertWritable();
        return getBackingHandle().remove_at(index).toCommonTag();
    }

    @Override
    public void add(int index, CommonTag element) {
        assertWritable();
        getBackingHandle().add_at(index, element.getBackingHandle());
    }

    @Override
    public boolean add(CommonTag element) {
        assertWritable();
        return getBackingHandle().add(element.getBackingHandle());
    }

    /**
     * Gets all values contained in this CommonTagList and casts it to the type
     * specified<br>
     * Lists, Sets and arrays (also primitive) are supported for types
     *
     * @param type to cast to
     * @return all data contained
     */
    public <T> T getAllValues(Class<T> type) {
        T values = Conversion.convert(this, type, null);
        if (values == null) {
            throw new IllegalArgumentException("Unsupported type: " + type.getName());
        } else {
            return values;
        }
    }

    /**
     * Clears all values contained and sets the contents to the data specified.
     * Collections, arrays (also primitive) and maps are supported for data
     * types. Other data types are added as a single element, and may cause an
     * exception if not supported.<br><br>
     * <p>
     * The individual elements can be collections or arrays as well, which
     * allows adding multiple arrays at once.
     *
     * @param values to set to
     */
    public <T> void setAllValues(T... values) {
        clear();
        addAllValues(values);
    }

    /**
     * Adds all the values to this list. Collections, arrays (also primitive)
     * and maps are supported for data types. Other data types are added as a
     * single element, and may cause an exception if not supported.<br><br>
     * <p>
     * The individual elements can be collections or arrays as well, which
     * allows adding multiple arrays at once.
     *
     * @param values to set to
     */
    public <T> void addAllValues(T... values) {
        for (Object data : values) {
            if (data == null) {
                continue;
            }
            Class<?> dataType = data.getClass();
            if (data instanceof Collection) {
                for (Object o : (Collection<?>) data) {
                    addValue(o);
                }
            } else if (data instanceof Map) {
                for (Entry<Object, Object> entry : ((Map<Object, Object>) data).entrySet()) {
                    addValue(entry.getValue());
                }
            } else if (dataType.isArray()) {
                if (dataType.isPrimitive()) {
                    int len = Array.getLength(data);
                    for (int i = 0; i < len; i++) {
                        addValue(Array.get(data, i));
                    }
                } else {
                    for (Object elem : (Object[]) data) {
                        addValue(elem);
                    }
                }
            } else {
                addValue(data);
            }
        }
    }

    @Override
    public Object[] toArray() {
        Object[] values = new Object[size()];
        Iterator<CommonTag> iter = iterator();
        for (int i = 0; i < values.length; i++) {
            values[i] = iter.next();
        }
        return values;
    }

    @Override
    public <K> K[] toArray(K[] array) {
        if (this.size() > array.length) {
            array = (K[]) LogicUtil.createArray(array.getClass().getComponentType(), this.size());
        }
        Iterator<CommonTag> iter = this.iterator();
        for (int i = 0; iter.hasNext(); i++) {
            array[i] = (K) iter.next();
        }
        return array;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return CollectionBasics.containsAll(this, c);
    }

    @Override
    public boolean addAll(Collection<? extends CommonTag> c) {
        return CollectionBasics.addAll(this, c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends CommonTag> c) {
        return CollectionBasics.addAll(this, index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return CollectionBasics.removeAll(this, c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return CollectionBasics.retainAll(this, c);
    }

    @Override
    public Iterator<CommonTag> iterator() {
        return new ConvertingIterator<>(getRawData().iterator(),
                nbtBaseToCommonTag(readOnly));
    }

    @Override
    public ListIterator<CommonTag> listIterator() {
        return new ConvertingListIterator<>(getRawData().listIterator(),
                nbtBaseToCommonTag(readOnly));
    }

    @Override
    public ListIterator<CommonTag> listIterator(int index) {
        return new ConvertingListIterator<>(getRawData().listIterator(index),
                nbtBaseToCommonTag(readOnly));
    }

    @Override
    public List<CommonTag> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("No sublist can be made from tag data");
    }
    
    /**
     * Deserializes and reads a list tag from a stream. The input data should be uncompressed.
     * 
     * @param in Stream to read from
     * @return read list tag
     * @throws IOException
     */
    public static CommonTagList readFromStream(InputStream in) throws IOException {
    	CommonTag tag = CommonTag.readFromStream(in);
    	if (!(tag instanceof CommonTagList)) {
    		throw new IOException("Tag read is not a list!");
    	}
    	return (CommonTagList) tag;
    }

    /**
     * Creates a CommonTagList from the handle specified<br>
     * If the handle is null or not a list, null is returned
     *
     * @param handle to create a list wrapper class for
     * @return Wrapper class suitable for the given handle
     */
    public static CommonTagList create(Object handle) {
        return LogicUtil.tryCast(CommonTag.create(handle), CommonTagList.class);
    }

    /**
     * Creates an unmodifiable CommonTagList from the handle specified<br>
     * If the handle is null or not a list, null is returned
     *
     * @param handle to create a list wrapper class for
     * @return Wrapper class suitable for the given handle
     */
    public static CommonTagList createReadOnly(Object handle) {
        return makeReadOnly(create(handle));
    }
}
