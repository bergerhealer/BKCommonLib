package com.bergerkiller.bukkit.common.reflection;

/**
 * A field accessor that can translate from one type to another to expose a different type than is stored
 * @param <T> Type exposed
 * @param <K> Type stored
 */
public abstract class TranslatorFieldAccessor<T, K> implements FieldAccessor<T> {
	private final FieldAccessor<K> base;

	@SuppressWarnings("unchecked")
	public TranslatorFieldAccessor(FieldAccessor<?> base) {
		this.base = (FieldAccessor<K>) base;
	}

	@Override
	public boolean isValid() {
		return base.isValid();
	}

	@Override
	public T get(Object instance) {
		return convert(base.get(instance));
	}

	@Override
	public boolean set(Object instance, T value) {
		return base.set(instance, revert(value));
	}

	@Override
	public T transfer(Object from, Object to) {
		return convert(base.transfer(from, to));
	}

	/**
	 * Converts the base value from the stored type to the exposed type
	 * 
	 * @param value to convert
	 * @return Value converted to the exposed type
	 */
	public abstract T convert(K value);

	/**
	 * Reverts the base value from the exposed type to the stored type
	 * 
	 * @param value to revert
	 * @return Value reverted to the stored type
	 */
	public abstract K revert(T value);
}
