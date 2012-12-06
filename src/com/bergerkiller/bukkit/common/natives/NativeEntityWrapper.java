package com.bergerkiller.bukkit.common.natives;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.NativeUtil;

public class NativeEntityWrapper implements Collection<Entity> {
	private final Collection<net.minecraft.server.Entity> entities;

	public NativeEntityWrapper(Collection<net.minecraft.server.Entity> entities) {
		this.entities = entities;
	}

	@Override
	public boolean add(Entity value) {
		return this.entities.add(NativeUtil.getNative(value));
	}

	@Override
	public boolean addAll(Collection<? extends Entity> values) {
		for (Entity chunk : values) {
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
		if (!(value instanceof Entity)) {
			return false;
		}
		return this.entities.contains(NativeUtil.getNative((Entity) value));
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
	public Iterator<Entity> iterator() {
		final Iterator<net.minecraft.server.Entity> iter = this.entities.iterator();
		return new Iterator<Entity>() {

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public Entity next() {
				net.minecraft.server.Entity e = iter.next();
				return e == null ? null : e.getBukkitEntity();
			}

			@Override
			public void remove() {
				iter.remove();
			}
		};
	}

	@Override
	public boolean remove(Object value) {
		if (!(value instanceof Entity)) {
			return false;
		}
		return this.entities.remove(NativeUtil.getNative((Entity) value));
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
		Iterator<Entity> iter = this.iterator();
		for (int i = 0; i < array.length; i++) {
			array[i] = iter.next();
		}
		return array;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] array) {
		T[] rval;
		if (array.length >= this.size()) {
			rval = array;
		} else {
			rval = (T[]) LogicUtil.createArray(array.getClass().getComponentType(), this.size());
		}
		Iterator<Entity> iter = this.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			array[i] = (T) iter.next();
		}
		return rval;
	}
}
