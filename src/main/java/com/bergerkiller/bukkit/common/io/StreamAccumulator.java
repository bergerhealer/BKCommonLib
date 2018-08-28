package com.bergerkiller.bukkit.common.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Sneakily taken over from net.minecraft.server.StreamAccumulator, modified to more easily
 * enable re-use of the same buffer.
 * 
 * @param <T>
 */
public class StreamAccumulator<T> {
    private final ArrayList<T> buffer = new ArrayList<T>();
    private Iterator<T> iterator = null;

    /**
     * Opens a stream. The stream can not be used again.
     * 
     * @param stream
     */
    public void open(Stream<T> stream) {
        this.buffer.clear();
        this.iterator = stream.iterator();
    }

    /**
     * Retrieves a new stream, starting with the first elements found in the stream that was previously opened.
     * That is, this method can be called multiple times, each time the same elements are traversed.
     * 
     * @return stream
     */
    public Stream<T> stream() {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.NONNULL | Spliterator.IMMUTABLE) {
            private int index = 0;

            public boolean tryAdvance(Consumer<? super T> consumer) {
                T object;

                if (this.index >= StreamAccumulator.this.buffer.size()) {
                    if (!StreamAccumulator.this.iterator.hasNext()) {
                        return false;
                    }

                    object = StreamAccumulator.this.iterator.next();
                    StreamAccumulator.this.buffer.add(object);
                } else {
                    object = StreamAccumulator.this.buffer.get(this.index);
                }

                ++this.index;
                consumer.accept(object);
                return true;
            }
        }, false);
    }
}
