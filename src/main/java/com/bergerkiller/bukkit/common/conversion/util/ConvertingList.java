package com.bergerkiller.bukkit.common.conversion.util;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import com.bergerkiller.bukkit.common.conversion.Converter;
import com.bergerkiller.bukkit.common.conversion.ConverterPair;

/**
 * Wraps around another list of unknown contents and performs conversions automatically.
 * This can be used to interact with collections that require additional element conversion.
 * 
 * @param <T> - exposed type
 */
public class ConvertingList<T> extends ConvertingCollection<T> implements List<T> {

	public ConvertingList(List<?> list, Converter<?> converterSet, Converter<T> converterGet) {
		super(list, converterSet, converterGet);
	}

	public ConvertingList(List<?> list, ConverterPair<?, T> converterPair) {
		super(list, converterPair);
	}

	@Override
	public List<Object> getBase() {
		return (List<Object>) super.getBase();
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return getBase().addAll(index, new ConvertingCollection<Object>(c, converterPair.reverse()));
	}

	@Override
	public T get(int index) {
		return converterPair.convertB(getBase().get(index));
	}

	@Override
	public T set(int index, T element) {
		return converterPair.convertB(getBase().set(index, converterPair.convertA(element)));
	}

	@Override
	public void add(int index, T element) {
		getBase().add(index, converterPair.convertA(element));
	}

	@Override
	public T remove(int index) {
		return converterPair.convertB(getBase().remove(index));
	}

	@Override
	public int indexOf(Object o) {
		return getBase().indexOf(converterPair.convertA(o));
	}

	@Override
	public int lastIndexOf(Object o) {
		return getBase().lastIndexOf(converterPair.convertA(o));
	}

	@Override
	public ListIterator<T> listIterator() {
		return new ConvertingListIterator<T>(getBase().listIterator(), converterPair);
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return new ConvertingListIterator<T>(getBase().listIterator(index), converterPair);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return new ConvertingList<T>(getBase().subList(fromIndex, toIndex), converterPair);
	}
}
