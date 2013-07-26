package com.bergerkiller.bukkit.common.collections;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Keeps instances of elements available even after clearing to avoid the creation of new instances.
 * This can be used to store an infinite or undefined amount of elements. The get and set operations
 * allow operating outside the size of the buffer, and dynamically allocate the new capacity needed.<br><br>
 * 
 * The {@link #createElement()} method needs to be implemented to use this buffer.
 * 
 * @param <E> - Instance element type
 */
public abstract class InstanceBuffer<E> extends AbstractList<E> {
	private final ArrayList<E> buffer = new ArrayList<E>();
	private int size = 0;

	/**
	 * Creates a new Element to use in the buffer
	 * 
	 * @return new element instance
	 */
	public abstract E createElement();

	/**
	 * Gets the next element and increases size by one.
	 * 
	 * @return next element added or obtained
	 */
	public E add() {
		ensureCapacity(size + 1);
		return buffer.get(size - 1);
	}

	/**
	 * Gets the given amount of next elements and increases the size by the amount
	 * 
	 * @param amount to add and get
	 * @return next elements added or obtained
	 */
	public List<E> addAll(int amount) {
		int startIndex = size - 1;
		ensureCapacity(size + amount);
		return buffer.subList(startIndex, startIndex + amount);
	}

	@Override
	public E remove(int index) {
		E element = buffer.remove(index);
		size--;
		return element;
	}

	@Override
	public E get(int index) {
		ensureCapacity(index + 1);
		return buffer.get(index);
	}

	@Override
	public E set(int index, E element) {
		if (index < size) {
			return buffer.set(index, element);
		}
		size = index + 1;
		if (buffer.size() >= size) {
			// Buffer is larger, set the element without resizing
			buffer.set(index, element);
		} else {
			// Resize the buffer till size - 1 and add the element
			buffer.ensureCapacity(size);
			while (buffer.size() < (size - 1)) {
				buffer.add(createElement());
			}
			buffer.add(element);
		}
		return null;
	}

	@Override
	public void add(int index, E element) {
		if (index > size) {
			// Element out of range, set the element instead
			set(index, element);
		} else {
			// Element in range, add it to the buffer
			buffer.add(index, element);
			size++;
		}
	}

	private void ensureCapacity(int capacity) {
		if (capacity < size) {
			return;
		}
		size = capacity;
		buffer.ensureCapacity(capacity);
		while (buffer.size() < size) {
			buffer.add(createElement());
		}
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public void clear() {
		clear(false);
	}

	/**
	 * Sets the element count back to 0, optionally clearing the backing buffer
	 * 
	 * @param clearBuffer option: True to clear the backing buffer instances
	 */
	public void clear(boolean clearBuffer) {
		size = 0;
		if (clearBuffer) {
			buffer.clear();
		}
	}
}
