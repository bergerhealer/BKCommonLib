package com.bergerkiller.bukkit.common.natives;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.NativeUtil;

import net.minecraft.server.v1_4_5.Chunk;
import net.minecraft.server.v1_4_5.Entity;

@SuppressWarnings({"rawtypes", "unchecked"})
public class NativeChunkEntitiesWrapper implements Collection<org.bukkit.entity.Entity> {
	private final Chunk chunk;

	public NativeChunkEntitiesWrapper(org.bukkit.Chunk chunk) {
		this.chunk = NativeUtil.getNative(chunk);
	}

	@Override
	public int size() {
		int size = 0;
		for (List list : chunk.entitySlices) {
			size = list.size();
		}
		return size;
	}

	@Override
	public boolean isEmpty() {
		for (List list : chunk.entitySlices) {
			if (!list.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean contains(Object o) {
		for (List list : chunk.entitySlices) {
			if (list.contains(o)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<org.bukkit.entity.Entity> iterator() {
		return new NativeChunkEntitiesIterator(chunk.entitySlices);
	}

	private static class NativeChunkEntitiesIterator implements Iterator<org.bukkit.entity.Entity> {
		private final Iterator<List> listIter;
		private Iterator<Entity> entityIter;

		public NativeChunkEntitiesIterator(List[] lists) {
			this.listIter = Arrays.asList(lists).iterator();
			this.nextElem();
		}

		private void nextElem() {
			if (!this.hasNext()) {
				if (this.listIter.hasNext()) {
					this.entityIter = this.listIter.next().iterator();
				} else {
					this.entityIter = null;
				}
			}
		}

		@Override
		public boolean hasNext() {
			return this.entityIter != null && this.entityIter.hasNext();
		}

		@Override
		public org.bukkit.entity.Entity next() {
			org.bukkit.entity.Entity next = NativeUtil.getEntity(this.entityIter.next());
			this.nextElem();
			return next;
		}

		@Override
		public void remove() {
			this.entityIter.remove();
			nextElem();
		}
	}

	@Override
	public Object[] toArray() {
		Object[] rval = new Object[this.size()];
		Iterator<org.bukkit.entity.Entity> iter = this.iterator();
		for (int i = 0; i < rval.length; i++) {
			rval[i] = iter.next();
		}
		return rval;
	}

	@Override
	public <K> K[] toArray(K[] array) {
		int size = this.size();
		if (size > array.length) {
			array = (K[]) LogicUtil.createArray(array.getClass().getComponentType(), size);
		}
		Iterator<org.bukkit.entity.Entity> iter = this.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			array[i] = (K) iter.next();
		}
		return array;
	}

	private int getChunkY(Entity entity) {
		return MathUtil.clamp(MathUtil.floor(entity.locY / 16.0), 0, chunk.entitySlices.length - 1);
	}

	@Override
	public boolean add(org.bukkit.entity.Entity entity) {
		Entity e = NativeUtil.getNative(entity);
		if (e == null) {
			return false;
		}
        return chunk.entitySlices[getChunkY(e)].add(e);
	}

	@Override
	public boolean remove(Object o) {
		if (!(o instanceof org.bukkit.entity.Entity))  {
			return false;
		}
		Entity e = NativeUtil.getNative((org.bukkit.entity.Entity) o);
		if (chunk.entitySlices[getChunkY(e)].remove(e)) {
			return true;
		}
		for (List list : chunk.entitySlices) {
			if (list.remove(e)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends org.bukkit.entity.Entity> c) {
		boolean val = true;
		for (org.bukkit.entity.Entity e : c) {
			if (!add(e)) {
				val = false;
			}
		}
		return val;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean val = true;
		for (Object o : c) {
			if (!remove(o)) {
				val = false;
			}
		}
		return val;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		for (List list : chunk.entitySlices) {
			list.clear();
		}
	}
}
