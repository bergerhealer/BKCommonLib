package com.bergerkiller.bukkit.common.conversion;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.bergerkiller.bukkit.common.conversion.util.ConvertingCollection;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingList;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingSet;

/**
 * Converts between two different types
 *
 * @param <A> - output type A
 * @param <B> - output type B
 */
public class ConverterPair<A, B> {
	private final Converter<A> converterA;
	private final Converter<B> converterB;

	public ConverterPair(Converter<A> converterA, Converter<B> converterB) {
		this.converterA = converterA;
		this.converterB = converterB;
	}

	/**
	 * Gets the Class type returned by converter A
	 * 
	 * @return converter A output Class type
	 */
	public Class<A> getOutputTypeA() {
		return this.converterA.getOutputType();
	}

	/**
	 * Gets the Class type returned by converter B
	 * 
	 * @return converter B output Class type
	 */
	public Class<B> getOutputTypeB() {
		return this.converterB.getOutputType();
	}

	/**
	 * Gets the internally stored Converter A
	 * 
	 * @return converter A
	 */
	public Converter<A> getConverterA() {
		return converterA;
	}

	/**
	 * Gets the internally stored Converter B
	 * 
	 * @return converter B
	 */
	public Converter<B> getConverterB() {
		return converterB;
	}

	/**
	 * Converts the value to the output type of converter A
	 * 
	 * @param value to convert
	 * @return converted value of type A, or null on failure
	 */
	public A convertA(Object value) {
		return converterA.convert(value);
	}

	/**
	 * Converts the value to the output type of converter B
	 * 
	 * @param value to convert
	 * @return converted value of type B, or null on failure
	 */
	public B convertB(Object value) {
		return converterB.convert(value);
	}

	/**
	 * Converts the value to the output type of converter A
	 * 
	 * @param value to convert
	 * @param def value to return on failure
	 * @return converted value of type A
	 */
	public A convertA(Object value, A def) {
		return converterA.convert(value, def);
	}

	/**
	 * Converts the value to the output type of converter B
	 * 
	 * @param value to convert
	 * @param def value to return on failure
	 * @return converted value of type B
	 */
	public B convertB(Object value, B def) {
		return converterB.convert(value, def);
	}

	/**
	 * Reverses the A and B converters
	 * 
	 * @return new Converter Pair with the A and B reversed
	 */
	public ConverterPair<B, A> reverse() {
		return new ConverterPair<B, A>(converterB, converterA);
	}

	/**
	 * Creates a new Converting Collection using this Converter Pair
	 * 
	 * @param collection to convert
	 * @return converting collection
	 */
	public Collection<B> convertAll(Collection<?> collection) {
		return new ConvertingCollection<B>(collection, this);
	}

	/**
	 * Creates a new Converting List using this Converter Pair
	 * 
	 * @param list to convert
	 * @return converting list
	 */
	public List<B> convertAll(List<?> list) {
		return new ConvertingList<B>(list, this);
	}

	/**
	 * Creates a new Converting Set using this Converter Pair
	 * 
	 * @param set to convert
	 * @return converting set
	 */
	public Set<B> convertAll(Set<?> set) {
		return new ConvertingSet<B>(set, this);
	}
}
