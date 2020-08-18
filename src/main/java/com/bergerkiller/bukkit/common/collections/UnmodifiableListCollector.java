package com.bergerkiller.bukkit.common.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Used by StreamUtil toUnmodifiableList
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class UnmodifiableListCollector implements Collector {
    public static final UnmodifiableListCollector INSTANCE = new UnmodifiableListCollector();
    private final BiConsumer<Object, Object> _accumulator = (list, value) -> { ((List) list).add(value); };
    private final BinaryOperator<Object> _combiner = (left, right) -> { ((List) left).addAll((List) right); return left; };
    private final Function<Object, Object> _finisher = list -> Collections.unmodifiableList((List) list);
    private final Set<Characteristics> _characteristics = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));

    private UnmodifiableListCollector() {}

    @Override
    public Supplier<Object> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<Object, Object> accumulator() {
        return _accumulator;
    }

    @Override
    public BinaryOperator<Object> combiner() {
        return _combiner;
    }

    @Override
    public Function<Object, Object> finisher() {
        return _finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return _characteristics;
    }
}
