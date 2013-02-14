package com.bergerkiller.bukkit.common.reflection;

/**
 * A field accessor that can translate from one type to another to expose a different type than is stored
 * @param <T> Type exposed
 * @param <K> Type stored
 */
public abstract class TranslatorFieldAccessor<T> implements FieldAccessor<T> {
	private final FieldAccessor<Object> base;

	@SuppressWarnings("unchecked")
	public TranslatorFieldAccessor(FieldAccessor<?> base) {
		this.base = (FieldAccessor<Object>) base;
	}

	@Override
	public boolean isValid() {
		return base.isValid();
	}

	/**
	 * Gets the internally stored value from an instance
	 * 
	 * @param instance containing this Field
	 * @return field value from instance
	 */
	public Object getInternal(Object instance) {
		return base.get(instance);
	}

	/**
	 * Sets the internally stored value for an instance
	 * 
	 * @param instance containing this Field
	 * @param value to set the field to
	 * @return True if successful, False if not
	 */
	public boolean setInternal(Object instance, Object value) {
		return base.set(instance, value);
	}

	@Override
	public T get(Object instance) {
		return convert(getInternal(instance));
	}

	@Override
	public boolean set(Object instance, T value) {
		return setInternal(instance, revert(value));
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
	public abstract T convert(Object value);

	/**
	 * Reverts the base value from the exposed type to the stored type
	 * 
	 * @param value to revert
	 * @return Value reverted to the stored type
	 */
	public abstract Object revert(T value);
}
