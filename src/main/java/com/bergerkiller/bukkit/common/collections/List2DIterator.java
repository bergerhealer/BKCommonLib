package com.bergerkiller.bukkit.common.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An iterator that can iterate over a collection of lists
 * 
 * @param <E> - List element type
 */
public class List2DIterator<E> implements Iterator<E> {
	private final Iterator<List<E>> collectionIter;
	private Iterator<E> elemIter;
	private Iterator<E> oldIter; // Used for remove() functionality

	public List2DIterator(Collection<List<E>> collection2D) {
		collectionIter = collection2D.iterator();
		nextElemIter();
	}

	/**
	 * If the current collection iterator has no new elements,
	 * the next collision is picked. If no new collection is
	 * available, the element iterator is set to null.
	 */
	private void nextElemIter() {
		if (!hasNext()) {
			while (collectionIter.hasNext()) {
				elemIter = collectionIter.next().iterator();
				if (elemIter.hasNext()) {
					// Element are available
					return;
				}
			}
			// No new elements available
			elemIter = null;
		}
	}

	@Override
	public boolean hasNext() {
		return elemIter != null && elemIter.hasNext();
	}

	@Override
	public E next() {
		if (elemIter == null) {
			throw new NoSuchElementException("Ran out of elements to return (forgot a hasNext check?)");
		}
		final E elem = elemIter.next();
		oldIter = elemIter;
		nextElemIter();
		return elem;
	}

	@Override
	public void remove() {
		if (oldIter == null) {
			throw new IllegalStateException("Can not remove an element: Next is not called this run");
		}
		oldIter.remove();
	}
}
