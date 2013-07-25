package com.bergerkiller.bukkit.common.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Provides basic implementations for various methods used in collections
 */
public class CollectionBasics {

	/**
	 * Sets all Elements in a Collection by first clearing the old elements and then adding the new elements
	 * 
	 * @param collection to set the contents of
	 * @param elements to set to
	 */
	public static <T, E extends T> void setAll(Collection<T> collection, E... elements) {
		setAll(collection, Arrays.asList(elements));
	}

	/**
	 * Sets all Elements in a Collection by first clearing the old elements and then adding the new elements
	 * 
	 * @param collection to set the contents of
	 * @param elements to set to
	 */
	public static <T> void setAll(Collection<T> collection, Collection<? extends T> elements) {
		collection.clear();
		collection.addAll(elements);
	}

	/**
	 * A basic containsAll implementation. (does not call collection.containsAll)
	 * Calls {@link Collection#contains(Object)} for all elements in the elements collection specified
	 * 
	 * @param collection to look in
	 * @param elements to look for
	 * @return True if all elements are contained, False if not
	 */
	public static boolean containsAll(Collection<?> collection, Collection<?> elements) {
		for (Object value : elements) {
			if (!collection.contains(value)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * A basic removeAll implementation. (does not call collection.removeAll)
	 * Calls {@link Collection#remove(Object)} for all elements in the elements collection specified
	 * 
	 * @param collection to remove from
	 * @param elements to remove
	 * @return True if the collection changed, False if not
	 */
	public static boolean removeAll(Collection<?> collection, Collection<?> elements) {
		boolean changed = false;
		for (Object value : elements) {
			changed |= collection.remove(value);
		}
		return changed;
	}

	/**
	 * A basic addAll implementation. (does not call collection.addAll)
	 * Calls {@link Collection#add(Object)} for all elements in the elements collection specified
	 * 
	 * @param collection to add to
	 * @param elements to add
	 * @return True if the collection changed, False if not
	 */
	public static <T> boolean addAll(Collection<T> collection, Collection<? extends T> elements) {
		boolean changed = false;
		for (T value : elements) {
			changed |= collection.add(value);
		}
		return changed;
	}

	/**
	 * A basic retainAll implementation. (does not call collection.retainAll)
	 * After this call all elements not contained in elements are removed.
	 * Essentially all elements are removed except those contained in the elements Collection.
	 * 
	 * @param collection
	 * @param elements to retain
	 * @return True if the collection changed, False if not
	 */
	public static boolean retainAll(Collection<?> collection, Collection<?> elements) {
		Iterator<?> iter = collection.iterator();
		boolean changed = false;
		while (iter.hasNext()) {
			if (!elements.contains(iter.next())) {
				iter.remove();
				changed = true;
			}
		}
		return changed;
	}

	/**
	 * A basic toArray implementation. (does not call collection.toArray)
	 * A new array of Objects is allocated and filled with the contents of the collection
	 * 
	 * @param collection to convert to an array
	 * @return a new Object[] array
	 */
	public static Object[] toArray(Collection<?> collection) {
		Object[] array = new Object[collection.size()];
		Iterator<?> iter = collection.iterator();
		for (int i = 0; i < array.length; i++) {
			array[i] = iter.next();
		}
		return array;
	}

	/**
	 * A basic toArray implementation. (does not call collection.toArray)
	 * If the array specified is not large enough, a new array with the right size is allocated.
	 * If the array specified is larger than the collection, the element right after the last
	 * collection element is set to null to indicate the end.
	 * 
	 * @param collection to convert to an array
	 * @param array to fill with the contents (can not be null)
	 * @return the array filled with the contents, or a new array of the same type
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Collection<?> collection, T[] array) {
		final int size = collection.size();
		if (array.length < size) {
			array = (T[]) LogicUtil.createArray(array.getClass().getComponentType(), size);
		}
		Iterator<?> iter = collection.iterator();
		for (int i = 0; i < array.length; i++) {
			if (iter.hasNext()) {
				array[i] = (T) iter.next();
			} else {
				array[i] = null;
				break;
			}
		}
		return array;
	}

	/**
	 * Obtains a reference to an element in a collection of lists
	 * 
	 * @param lists to pick from
	 * @param index of the element
	 * @return list entry for the element at the index
	 */
	public static <T> ListEntry<T> getEntry(Collection<List<T>> lists, int index) {
		int size;
		for (List<T> list : lists) {
			size = list.size();
			if (size >= index) {
				index -= size;
			} else {
				return new ListEntry<T>(list, index);
			}
		}
		throw new IndexOutOfBoundsException();
	}

	public static class ListEntry<T> {
		public final List<T> list;
		public final int index;

		public ListEntry(List<T> list, int index) {
			this.list = list;
			this.index = index;
		}

		public void add(T element) {
			list.add(index, element);
		}

		public T set(T newElement) {
			return list.set(index, newElement);
		}

		public T get() {
			return list.get(index);
		}

		public T remove() {
			return list.remove(index);
		}

		public boolean addAll(Collection<? extends T> c) {
			return list.addAll(index, c);
		}
	}
}
