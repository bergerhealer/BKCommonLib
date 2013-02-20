package com.bergerkiller.bukkit.common.collections;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import com.bergerkiller.bukkit.common.collections.CollectionBasics.ListEntry;

/**
 * A List Iterator that can iterate over a collection of collections.
 * For example, to iterate over an array of lists
 */
public class List2DListIterator<T> implements ListIterator<T> {
	private final Collection<List<T>> lists;
	private int index;

	public List2DListIterator(Collection<List<T>> lists) {
		this(lists, 0);
	}

	public List2DListIterator(Collection<List<T>> lists, int index) {
		this.lists = lists;
		this.index = index;
	}

	private ListEntry<T> get() {
		return CollectionBasics.getEntry(lists, index);
	}

	private int size() {
		int size = 0;
		for (List<T> list : lists) {
			size += list.size();
		}
		return size;
	}

	@Override
	public boolean hasNext() {
		return index < (size() - 1);
	}

	@Override
	public boolean hasPrevious() {
		return index > 0;
	}

	@Override
	public T next() {
		try {
			return get().get();
		} finally {
			index++;
		}
	}

	@Override
	public void remove() {
		get().remove();
	}

	@Override
	public T previous() {
		index--;
		return get().get();
	}

	@Override
	public int nextIndex() {
		return index;
	}

	@Override
	public int previousIndex() {
		return index - 1;
	}

	@Override
	public void set(T e) {
		get().set(e);
	}

	@Override
	public void add(T e) {
		get().add(e);
	}
}
