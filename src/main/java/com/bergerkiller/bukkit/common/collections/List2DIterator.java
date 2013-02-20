package com.bergerkiller.bukkit.common.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * An iterator that can iterate over a collection of lists
 */
public class List2DIterator<T> implements Iterator<T> {
	private final Iterator<List<T>> collectionIter;
	private Iterator<T> elemIter;

	public List2DIterator(Collection<List<T>> collection2D) {
		this.collectionIter = collection2D.iterator();
	}

	private void nextElem() {
		if (!this.hasNext()) {
			if (collectionIter.hasNext()) {
				elemIter = collectionIter.next().iterator();
			} else {
				elemIter = null;
			}
		}
	}

	@Override
	public boolean hasNext() {
		return elemIter != null && elemIter.hasNext();
	}

	@Override
	public T next() {
		T next = elemIter.next();
		this.nextElem();
		return next;
	}

	@Override
	public void remove() {
		elemIter.remove();
		nextElem();
	}
}
