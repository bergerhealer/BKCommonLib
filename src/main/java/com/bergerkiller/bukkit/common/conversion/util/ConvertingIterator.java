package com.bergerkiller.bukkit.common.conversion.util;

import java.util.Iterator;

import com.bergerkiller.bukkit.common.conversion.Converter;

/**
 * An iterator that dynamically converts the elements as they are iterated
 * 
 * @param <T> - returned element type
 */
public class ConvertingIterator<T> implements Iterator<T> {
	private final Iterator<?> iter;
	private final Converter<T> converter;

	public ConvertingIterator(Iterator<?> iterator, Converter<T> converter) {
		this.iter = iterator;
		this.converter = converter;
	}

	@Override
	public boolean hasNext() {
		return this.iter.hasNext();
	}

	@Override
	public T next() {
		return this.converter.convert(this.iter.next());
	}

	@Override
	public void remove() {
		this.iter.remove();
	}
}
