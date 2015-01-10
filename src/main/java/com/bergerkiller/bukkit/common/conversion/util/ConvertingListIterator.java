package com.bergerkiller.bukkit.common.conversion.util;

import java.util.ListIterator;

import com.bergerkiller.bukkit.common.conversion.Converter;
import com.bergerkiller.bukkit.common.conversion.ConverterPair;

public class ConvertingListIterator<T> implements ListIterator<T> {
	private final ListIterator<Object> iter;
	private final ConverterPair<Object, T> converterPair;

	public ConvertingListIterator(ListIterator<?> listIterator, Converter<?> converterSet, Converter<T> converterGet) {
		this(listIterator, converterSet.formPair(converterGet));
	}

	@SuppressWarnings("unchecked")
	public ConvertingListIterator(ListIterator<?> listIterator, ConverterPair<?, T> converterPair) {
		this.iter = (ListIterator<Object>) listIterator;
		this.converterPair = (ConverterPair<Object, T>) converterPair;
	}

	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public T next() {
		return converterPair.convertB(iter.next());
	}

	@Override
	public boolean hasPrevious() {
		return iter.hasPrevious();
	}

	@Override
	public T previous() {
		return converterPair.convertB(iter.previous());
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
		iter.set(converterPair.convertA(e));
	}

	@Override
	public void add(T e) {
		iter.add(converterPair.convertA(e));
	}
}
