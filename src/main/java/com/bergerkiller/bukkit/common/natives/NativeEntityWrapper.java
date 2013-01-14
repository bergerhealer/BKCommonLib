package com.bergerkiller.bukkit.common.natives;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.server.v1_4_6.Entity;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.NativeUtil;

@SuppressWarnings("rawtypes")
public class NativeEntityWrapper<T extends org.bukkit.entity.Entity> implements Collection<T> {
	private final Collection entities;
	private final Class<T> type;

	public NativeEntityWrapper(Collection entities, Class<T> type) {
		this.entities = entities;
		this.type = type;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean add(org.bukkit.entity.Entity value) {
		return this.entities.add(NativeUtil.getNative(value));
	}

	@Override
	public boolean addAll(Collection<? extends T> values) {
		for (org.bukkit.entity.Entity chunk : values) {
			add(chunk);
		}
		return true;
	}

	@Override
	public void clear() {
		this.entities.clear();
	}

	@Override
	public boolean contains(Object value) {
		if (!(value instanceof org.bukkit.entity.Entity)) {
			return false;
		}
		return this.entities.contains(NativeUtil.getNative((org.bukkit.entity.Entity) value));
	}

	@Override
	public boolean containsAll(Collection<?> values) {
		for (Object o : values) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return this.entities.isEmpty();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterator<T> iterator() {
		final Iterator<Entity> iter = this.entities.iterator();
		return new Iterator<T>() {

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public T next() {
				return NativeUtil.getEntity(iter.next(), NativeEntityWrapper.this.type);
			}

			@Override
			public void remove() {
				iter.remove();
			}
		};
	}

	@Override
	public boolean remove(Object value) {
		if (!(value instanceof org.bukkit.entity.Entity)) {
			return false;
		}
		return this.entities.remove(NativeUtil.getNative((org.bukkit.entity.Entity) value));
	}

	@Override
	public boolean removeAll(Collection<?> values) {
		boolean succ = true;
		for (Object o : values) {
			if (!remove(o)) {
				succ = false;
			}
		}
		return succ;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		return this.entities.size();
	}

	@Override
	public Object[] toArray() {
		Object[] array = new Object[this.size()];
		Iterator<T> iter = this.iterator();
		for (int i = 0; i < array.length; i++) {
			array[i] = iter.next();
		}
		return array;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <K> K[] toArray(K[] array) {
		if (this.size() > array.length) {
			array = (K[]) LogicUtil.createArray(array.getClass().getComponentType(), this.size());
		}
		Iterator<T> iter = this.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			array[i] = (K) iter.next();
		}
		return array;
	}
}
