package com.bergerkiller.bukkit.common.natives;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Chunk;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.NativeUtil;

public class NativeChunkWrapper implements Collection<Chunk> {
	private final Collection<net.minecraft.server.Chunk> chunks;

	public NativeChunkWrapper(Collection<net.minecraft.server.Chunk> chunks) {
		this.chunks = chunks;
	}

	@Override
	public boolean add(Chunk value) {
		return this.chunks.add(NativeUtil.getNative(value));
	}

	@Override
	public boolean addAll(Collection<? extends Chunk> values) {
		for (Chunk chunk : values) {
			add(chunk);
		}
		return true;
	}

	@Override
	public void clear() {
		this.chunks.clear();
	}

	@Override
	public boolean contains(Object value) {
		if (!(value instanceof Chunk)) {
			return false;
		}
		return this.chunks.contains(NativeUtil.getNative((Chunk) value));
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
		return this.chunks.isEmpty();
	}

	@Override
	public Iterator<Chunk> iterator() {
		final Iterator<net.minecraft.server.Chunk> iter = this.chunks.iterator();
		return new Iterator<Chunk>() {

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public Chunk next() {
				net.minecraft.server.Chunk c = iter.next();
				return c == null ? null : c.bukkitChunk;
			}

			@Override
			public void remove() {
				iter.remove();
			}
		};
	}

	@Override
	public boolean remove(Object value) {
		if (!(value instanceof Chunk)) {
			return false;
		}
		return this.chunks.remove(NativeUtil.getNative((Chunk) value));
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
		return this.chunks.size();
	}

	@Override
	public Object[] toArray() {
		Object[] array = new Object[this.size()];
		Iterator<Chunk> iter = this.iterator();
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
		Iterator<Chunk> iter = this.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			rval[i] = (T) iter.next();
		}
		return rval;
	}
}
