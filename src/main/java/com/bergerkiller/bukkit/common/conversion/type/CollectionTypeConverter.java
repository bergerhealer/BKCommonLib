package com.bergerkiller.bukkit.common.conversion.type;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.conversion.CastingConverter;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.conversion.Converter;
import com.bergerkiller.bukkit.common.conversion.ConverterPair;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingList;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingSet;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;

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
	public static final CollectionTypeConverter<Set<Player>, Set<?>> toPlayerSet = new CollectionTypeConverter<Set<Player>, Set<?>>(CollectionConverter.toSet) {
		@Override
		protected Set<Player> convertSpecial(Set<?> value, Set<Player> def) {
			return new ConvertingSet<Player>(value, ConversionPairs.player);
		}
	};
	public static final CollectionTypeConverter<Set<Object>, Set<?>> toPlayerHandleSet = new CollectionTypeConverter<Set<Object>, Set<?>>(CollectionConverter.toSet) {
		@Override
		protected Set<Object> convertSpecial(Set<?> value, Set<Object> def) {
			return new ConvertingSet<Object>(value, ConversionPairs.player.reverse());
		}
	};
	public static final CollectionTypeConverter<List<ItemStack>, List<?>> toItemStackList = new CollectionTypeConverter<List<ItemStack>, List<?>>(CollectionConverter.toList) {
		@Override
		protected List<ItemStack> convertSpecial(List<?> value, List<ItemStack> def) {
			return new ConvertingList<ItemStack>(value, ConversionPairs.itemStack);
		}
	};
	public static final CollectionTypeConverter<List<Object>, List<?>> toItemStackHandleList = new CollectionTypeConverter<List<Object>, List<?>>(CollectionConverter.toList) {
		@Override
		protected List<Object> convertSpecial(List<?> value, List<Object> def) {
			return new ConvertingList<Object>(value, ConversionPairs.itemStack.reverse());
		}
	};

	private final CollectionConverter<B> converter;

	public CollectionTypeConverter(CollectionConverter<B> converter) {
		this.converter = converter;
		if (converter == null) {
			CommonPlugin.LOGGER_CONVERSION.log(Level.SEVERE, "Collection type converter is lacking a base converter!", new IllegalArgumentException());
		}
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
