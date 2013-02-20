package com.bergerkiller.bukkit.common.conversion.util;

import java.util.Collection;
import java.util.Iterator;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;
import com.bergerkiller.bukkit.common.conversion.Converter;
import com.bergerkiller.bukkit.common.conversion.ConverterPair;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Wraps around another collection of unknown contents and performs conversions automatically.
 * This can be used to interact with collections that require additional element conversion.
 * 
 * @param <T> - exposed type
 */
public class ConvertingCollection<T> implements Collection<T> {
	private final Collection<Object> base;
	protected final ConverterPair<Object, T> converterPair;

	public ConvertingCollection(Collection<?> collection, Converter<?> converterSet, Converter<T> converterGet) {
		this(collection, converterSet.formPair(converterGet));
	}

	@SuppressWarnings("unchecked")
	public ConvertingCollection(Collection<?> collection, ConverterPair<?, T> converterPair) {
		this.base = (Collection<Object>) collection;
		this.converterPair = (ConverterPair<Object, T>) converterPair;
	}

	/**
	 * Gets the base collection that is used
	 * 
	 * @return base collection
	 */
	public Collection<Object> getBase() {
		return base;
	}

	@Override
	public int size() {
		return base.size();
	}

	@Override
	public boolean isEmpty() {
		return base.isEmpty();
	}

	@Override
	public void clear() {
		base.clear();
	}

	@Override
	public boolean add(T e) {
		return base.add(converterPair.convertA(e));
	}

	@Override
	public boolean remove(Object o) {
		return base.remove(converterPair.convertA(o));
	}

	@Override
	public boolean contains(Object o) {
		return base.contains(converterPair.convertA(o));
	}

	@Override
	public Iterator<T> iterator() {
		return new ConvertingIterator<T>(base.iterator(), converterPair.getConverterB());
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return base.containsAll(new ConvertingCollection<Object>(c, converterPair.reverse()));
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return base.addAll(new ConvertingCollection<Object>(c, converterPair.reverse()));
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return base.addAll(new ConvertingCollection<Object>(c, converterPair.reverse()));
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return LogicUtil.retainAll(this, c);
	}

	@Override
	public Object[] toArray() {
		return CollectionBasics.toArray(this);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <K> K[] toArray(K[] array) {
		final int size = this.size();
		if (array.length < size) {
			array = (K[]) LogicUtil.createArray(array.getClass().getComponentType(), size);
		}
		Iterator<T> iter = this.iterator();
		for (int i = 0; i < array.length; i++) {
			if (iter.hasNext()) {
				array[i] = (K) iter.next();
			} else {
				array[i] = null;
				break;
			}
		}
		return array;
	}
}
