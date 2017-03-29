package com.bergerkiller.bukkit.common.conversion.generic;

import java.util.Collection;
import java.util.List;

import com.bergerkiller.bukkit.common.conversion.Converter;
import com.bergerkiller.bukkit.common.conversion.ConverterPair;
import com.bergerkiller.bukkit.common.conversion.type.CollectionConverter;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingCollection;

/**
 * A generic converter that converts the elements back and forth inside a List
 */
public class CollectionElementConverter<A, B> extends Converter<Collection<B>> {
	private final ConverterPair<A, B> pair;

	private CollectionElementConverter(ConverterPair<A, B> converterPair) {
	    super(List.class);
		this.pair = converterPair;
	}

	@Override
	public Collection<B> convert(Object value, Collection<B> def) {
		Collection<B> result = convert(value);
		if (result == null) {
			return def;
		} else {
			return result;
		}
	}

	@Override
	public Collection<B> convert(Object value) {
	    Collection<?> inputList = CollectionConverter.toCollection.convert(value);
		if (inputList == null) {
			return null;
		} else {
			return new ConvertingCollection<B>(inputList, pair);
		}
	}

	@Override
	public boolean isCastingSupported() {
		return false;
	}
	
    @Override
    public boolean isRegisterSupported() {
        return false;
    }

    /**
     * Reverses the element conversion
     *
     * @return new List Element Converter with element types swapped
     */
    public CollectionElementConverter<B, A> reverse() {
    	return create(pair.reverse());
    }

    /**
     * Creates a new list element converter
     * 
     * @param converterPair to use during conversion
     * @return List element converter
     */
    public static <A, B> CollectionElementConverter<A, B> create(ConverterPair<A, B> converterPair) {
    	return new CollectionElementConverter<A, B>(converterPair);
    }

    /**
     * Creates a new list element converter
     * 
     * @param convA converter in one direction
     * @param convB converter in the other direction
     * @return List element converter
     */
    public static <A, B> CollectionElementConverter<A, B> create(Converter<A> convA, Converter<B> convB) {
    	return new CollectionElementConverter<A, B>(convA.formPair(convB));
    }

}
