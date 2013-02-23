package com.bergerkiller.bukkit.common.wrappers;

import java.util.Iterator;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class LongHashSet extends BasicWrapper {
	private static final ClassTemplate<?> TEMPLATE = ClassTemplate.create(CommonUtil.getCBClass("LongHashSet"));
	private static final SafeConstructor<?> contructor1 = TEMPLATE.getConstructor();
	private static final SafeConstructor<?> contructor2 = TEMPLATE.getConstructor(int.class);
	private static final MethodAccessor<Boolean> add2 = TEMPLATE.getMethod("add", int.class, int.class);
	private static final MethodAccessor<Boolean> add1 = TEMPLATE.getMethod("add", long.class);
	private static final MethodAccessor<Boolean> contains2 = TEMPLATE.getMethod("contains", int.class, int.class);
	private static final MethodAccessor<Boolean> contains1 = TEMPLATE.getMethod("contains", long.class);
	private static final MethodAccessor<Void> remove2 = TEMPLATE.getMethod("remove", int.class, int.class);
	private static final MethodAccessor<Boolean> remove1 = TEMPLATE.getMethod("remove", long.class);
	private static final MethodAccessor<Void> clear = TEMPLATE.getMethod("clear");
	private static final MethodAccessor<long[]> toArray = TEMPLATE.getMethod("toArray");
	private static final MethodAccessor<Long> popFirst = TEMPLATE.getMethod("popFirst");
	private static final MethodAccessor<long[]> popAll = TEMPLATE.getMethod("popAll");
	private static final MethodAccessor<Integer> hash = TEMPLATE.getMethod("hash", long.class);
	private static final MethodAccessor<Void> rehash0 = TEMPLATE.getMethod("rehash");
	private static final MethodAccessor<Void> rehash1 = TEMPLATE.getMethod("rehash", int.class);
	private static final MethodAccessor<Boolean> isEmpty = TEMPLATE.getMethod("isEmpty");
	private static final MethodAccessor<Integer> size = TEMPLATE.getMethod("size");
	private static final MethodAccessor<Object> iterator = TEMPLATE.getMethod("iterator");
	
	public LongHashSet() {
		this.setHandle(contructor1.newInstance());
	}
	
	public LongHashSet(int size) {
		this.setHandle(contructor2.newInstance());
	}
	
	public LongHashSet(Object handle) {
		this.setHandle(handle);
	}
	
	@SuppressWarnings("unchecked")
	public Iterator<Long> iterator() {
		return (Iterator<Long>) iterator.invoke(handle);
	}
	
	public boolean add(int lsw, int msw) {
		return add2.invoke(handle, lsw, msw);
	}
	
	public boolean add(long value) {
		return add1.invoke(handle, value);
	}
	
	public boolean contains(int lsw, int msw) {
		return contains2.invoke(handle, lsw, msw);
	}
	
	public boolean contains(long value) {
		return contains1.invoke(handle, value);
	}
	
	public void remove(int lsw, int msw) {
		remove2.invoke(handle, lsw, msw);
	}
	
	public boolean remove(long value) {
		return remove1.invoke(handle, value);
	}
	
	public void clear() {
		clear.invoke(handle);
	}
	
	public long[] toArray() {
		return toArray.invoke(handle);
	}
	
	public long popFirst() {
		return popFirst.invoke(handle);
	}
	
	public long[] popAll() {
		return popAll.invoke(handle);
	}
	
	public int hash(long value) {
		return hash.invoke(handle, value);
	}
	
	public void rehash() {
		rehash0.invoke(handle);
	}
	
	public void rehash(int newCapacity) {
		rehash1.invoke(handle, newCapacity);
	}
	
	public boolean isEmpty() {
		return isEmpty.invoke(handle);
	}
	
	public int size() {
		return size.invoke(handle);
	}
	
	/**
	 * @deprecated No need to use this
	 */
	@Deprecated
	public static class Itr extends BasicWrapper implements Iterator<Long> {
		private static final ClassTemplate<?> TEMPLATE = ClassTemplate.create(CommonUtil.getCBClass("LongHashSet.Itr"));
		private static final SafeConstructor<?> contructor = TEMPLATE.getConstructor();
		private static final MethodAccessor<Boolean> hasNext = TEMPLATE.getMethod("hasNext");
		private static final MethodAccessor<Long> next = TEMPLATE.getMethod("next");
		private static final MethodAccessor<Void> remove = TEMPLATE.getMethod("remove");
		
		public Itr() {
			this.setHandle(contructor.newInstance());
		}
		
		public Itr(Object handle) {
			this.setHandle(handle);
		}
		
		public boolean hasNext() {
			return hasNext.invoke(handle);
		}
		
		public Long next() {
			return next.invoke(handle);
		}
		
		public void remove() {
			remove.invoke(handle);
		}
	}
}