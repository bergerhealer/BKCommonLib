package com.bergerkiller.bukkit.common.wrappers.nbt;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.NBTUtil;

import net.minecraft.server.v1_4_R1.NBTBase;
import net.minecraft.server.v1_4_R1.NBTTagList;

@SuppressWarnings("unchecked")
public class CommonTagList extends CommonTag<NBTTagList> implements List<CommonTag<?>> {

	protected CommonTagList() {
	}

	public CommonTagList(String name, List<?> data) {
		super(name, data);
	}

	@Override
	public List<CommonTag<?>> getData() {
		return (List<CommonTag<?>>) super.getData();
	}

	@Override
	protected List<Object> getRawData() {
		return (List<Object>) super.getRawData();
	}

	@Override
	public CommonTagList clone() {
		return (CommonTagList) super.clone();
	}

	@Override
	public int size() {
		return handle.size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public void clear() {
		getRawData().clear();
		info.setListType(handle, (byte) 0);
	}

	/**
	 * Adds a single CommonTag with no name and the data specified
	 * 
	 * @param data of the tag
	 */
	public void addData(Object data) {
		addData(null, data);
	}

	/**
	 * Adds a single CommonTag with the name and data specified
	 * 
	 * @param name of the tag
	 * @param data of the tag
	 */
	public void addData(String name, Object data) {
		handle.add((NBTBase) NBTUtil.createHandle(name, data));
	}

	/**
	 * Gets all data contained in this CommonTag and casts it to the type specified<br>
	 * Lists, Sets and arrays (also primitive) are supported for types
	 * 
	 * @param type to cast to
	 * @return all data contained
	 */
	public <T> T getAllData(Class<T> type) {
		if (type.isArray()) {
			return (T) LogicUtil.toConvertedArray(this, type);
		} else if (List.class.isAssignableFrom(type)) {
			return (T) this;
		} else if (Set.class.isAssignableFrom(type)) {
			return (T) new HashSet<Object>(this);
		} else {
			throw new IllegalArgumentException("Unsupported type: " + type.getName());
		}
	}

	/**
	 * Clears all data contained and sets the contents to the data specified<br>
	 * Collections, arrays (also primitive) and maps are supported for data types<br>
	 * Other data types are added as a single element, and may cause an exception if not supported
	 * 
	 * @param data to set to
	 */
	public void setAllData(Object data) {
		clear();
		if (data != null) {
			Class<?> dataType = data.getClass();
			if (data instanceof Collection) {
				for (Object o : (Collection<?>) data) {
					addData(o);
				}
			} else if (data instanceof Map) {
				for (Entry<Object, Object> entry : ((Map<Object, Object>) data).entrySet()) {
					addData(entry.getKey().toString(), entry.getValue());
				}
			} else if (dataType.isArray()) {
				if (dataType.isPrimitive()) {
					int len = Array.getLength(data);
					for (int i = 0; i < len; i++) {
						addData(Array.get(data, i));
					}
				} else {
					for (Object elem : (Object[]) data) {
						addData(elem);
					}
				}
			} else {
				addData(data);
			}
		}
	}

	@Override
	public CommonTag<?> remove(int index) {
		return create(getRawData().remove(index));
	}

	@Override
	public CommonTag<?> set(int index, CommonTag<?> element) {
		final CommonTag<?> prev = get(index);
		if (element == null) {
			throw new IllegalArgumentException("Can not set to null element");
		}
		NBTBase handle = (NBTBase) element.getHandle();
		getRawData().set(index, handle);
		info.setListType(handle, handle.getTypeId());
		return prev;
	}

	@Override
	public void add(int index, CommonTag<?> element) {
		if (element == null) {
			throw new IllegalArgumentException("Can not set to null element");
		}
		NBTBase handle = (NBTBase) element.getHandle();
		getRawData().add(index, handle);
		info.setListType(handle, handle.getTypeId());
	}

	/**
	 * Adds a single tag, allows both CommonTags as NBTTags to be added
	 * 
	 * @param tag to add
	 */
	public void addTag(Object tag) {
		if (tag == null) {
			throw new IllegalArgumentException("Can not add a null element");
		}
		handle.add((NBTBase) commonToNbt(tag));
	}

	@Override
	public boolean add(CommonTag<?> element) {
		if (element == null) {
			throw new IllegalArgumentException("Can not set to null element");
		}
		addTag(element);
		return true;
	}

	@Override
	public CommonTag<?> get(int index) {
		return create(handle.get(index));
	}

	@Override
	public int indexOf(Object o) {
		return getRawData().indexOf(commonToNbt(o));
	}

	@Override
	public int lastIndexOf(Object o) {
		return getRawData().lastIndexOf(commonToNbt(o));
	}

	@Override
	public boolean contains(Object o) {
		for (Object elem : this) {
			if (elem != null && elem.equals(o)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<CommonTag<?>> iterator() {
		final Iterator<Object> iter = getRawData().iterator();
		return new Iterator<CommonTag<?>>() {
			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public CommonTag<?> next() {
				return create(iter.next());
			}

			@Override
			public void remove() {
				iter.remove();
			}
		};
	}

	@Override
	public Object[] toArray() {
		Object[] values = new Object[size()];
		Iterator<CommonTag<?>> iter = iterator();
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
		Iterator<CommonTag<?>> iter = this.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			array[i] = (K) iter.next();
		}
		return array;
	}

	@Override
	public boolean remove(Object o) {
		return getRawData().remove(commonToNbt(o));
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		List<?> raw = getRawData();
		for (Object elem : c) {
			if (!raw.contains(commonToNbt(elem))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends CommonTag<?>> c) {
		return getRawData().addAll((Collection<Object>) commonToNbt(c));
	}

	@Override
	public boolean addAll(int index, Collection<? extends CommonTag<?>> c) {
		return getRawData().addAll(index, (Collection<Object>) commonToNbt(c));
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		List<Object> raw = getRawData();
		boolean changed = false;
		for (Object o : c) {
			changed |= raw.remove(commonToNbt(o));
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	private static class TagListIterator implements ListIterator<CommonTag<?>> {
		private final ListIterator<Object> iter;

		public TagListIterator(ListIterator<Object> iter) {
			this.iter = iter;
		}

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public CommonTag<?> next() {
			return create(iter.next());
		}

		@Override
		public boolean hasPrevious() {
			return iter.hasPrevious();
		}

		@Override
		public CommonTag<?> previous() {
			return create(iter.previous());
		}

		@Override
		public int nextIndex() {
			return iter.nextIndex();
		}

		@Override
		public int previousIndex() {
			return iter.previousIndex();
		}

		@Override
		public void remove() {
			iter.remove();
		}

		@Override
		public void set(CommonTag<?> e) {
			iter.set(e == null ? null : e.getHandle());
		}

		@Override
		public void add(CommonTag<?> e) {
			iter.add(e == null ? null : e.getHandle());
		}
	}
	
	@Override
	public ListIterator<CommonTag<?>> listIterator() {
		return new TagListIterator(this.getRawData().listIterator());
	}

	@Override
	public ListIterator<CommonTag<?>> listIterator(int index) {
		return new TagListIterator(this.getRawData().listIterator(index));
	}

	@Override
	public List<CommonTag<?>> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("No sublist can be made from tag data");
	}
}
