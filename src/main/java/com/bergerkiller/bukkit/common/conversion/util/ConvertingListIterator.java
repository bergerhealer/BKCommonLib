package com.bergerkiller.bukkit.common.conversion.util;

import java.util.ListIterator;

import com.bergerkiller.bukkit.common.conversion.Converter;

public class ConvertingListIterator<T> implements ListIterator<T> {
	private final ListIterator<Object> iter;
	private final Converter<T> converter;
	private final Converter<Object> reverter;

	@SuppressWarnings("unchecked")
	public ConvertingListIterator(ListIterator<?> listIterator, Converter<T> converter, Converter<?> reverter) {
		this.iter = (ListIterator<Object>) listIterator;
		this.converter = converter;
		this.reverter = (Converter<Object>) reverter;
	}

	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public T next() {
		return converter.convert(iter.next());
	}

	@Override
	public boolean hasPrevious() {
		return iter.hasPrevious();
	}

	@Override
	public T previous() {
		return converter.convert(iter.previous());
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
	public void set(T e) {
		iter.set(reverter.convert(e));
	}

	@Override
	public void add(T e) {
		iter.add(reverter.convert(e));
	}
}
