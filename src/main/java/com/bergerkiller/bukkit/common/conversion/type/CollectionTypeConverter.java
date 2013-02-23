package com.bergerkiller.bukkit.common.conversion.type;

import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.conversion.CastingConverter;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.conversion.Converter;
import com.bergerkiller.bukkit.common.conversion.ConverterPair;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingList;

public abstract class CollectionTypeConverter<T extends Collection<?>, B extends Collection<?>> implements Converter<T> {
	public static final CollectionTypeConverter<List<Player>, List<?>> toPlayerList = new CollectionTypeConverter<List<Player>, List<?>>(CollectionConverter.toList) {
		@Override
		protected List<Player> convertSpecial(List<?> value, List<Player> def) {
			return new ConvertingList<Player>(value, ConversionPairs.player);
		}
	};
	public static final CollectionTypeConverter<List<Object>, List<?>> toPlayerHandleList = new CollectionTypeConverter<List<Object>, List<?>>(CollectionConverter.toList) {
		@Override
		protected List<Object> convertSpecial(List<?> value, List<Object> def) {
			return new ConvertingList<Object>(value, ConversionPairs.player.reverse());
		}
	};

	private final CollectionConverter<B> converter;

	public CollectionTypeConverter(CollectionConverter<B> converter) {
		this.converter = converter;
	}

	protected abstract T convertSpecial(B value, T def);

	@Override
	public final T convert(Object value, T def) {
		B typedValue = converter.convert(value);
		if (typedValue != null) {
			return convertSpecial(typedValue, def);
		}
		return def;
	}

	@Override
	public final T convert(Object value) {
		return convert(value, null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<T> getOutputType() {
		return (Class<T>) this.converter.getOutputType();
	}

	@Override
	public boolean isCastingSupported() {
		return false;
	}

	@Override
	public boolean isRegisterSupported() {
		return false;
	}

	@Override
	public <K> ConverterPair<T, K> formPair(Converter<K> converterB) {
		return new ConverterPair<T, K>(this, converterB);
	}

	@Override
	public <K> Converter<K> cast(Class<K> type) {
		return new CastingConverter<K>(type, this);
	}
}
