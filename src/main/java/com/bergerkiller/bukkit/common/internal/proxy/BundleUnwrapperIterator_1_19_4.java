package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Un-wraps an Iterator of packets so that all packets inside which are
 * also bundles, are unpacked and iterated separately.
 */
public class BundleUnwrapperIterator_1_19_4 {
    protected final BundleUnwrapperIterator_1_19_4 parent;
    protected Iterator<Object> packetIter;

    protected BundleUnwrapperIterator_1_19_4(BundleUnwrapperIterator_1_19_4 parent, Iterator<Object> packetIter) {
        this.parent = parent;
        this.packetIter = packetIter;
    }

    public static Iterable<Object> unwrap(Iterable<Object> packets) {
        return () -> new MainIterator(packets.iterator());
    }

    public static Iterator<Object> unwrap(Iterator<Object> packets) {
        return new MainIterator(packets);
    }

    private static class MainIterator extends BundleUnwrapperIterator_1_19_4 implements Iterator<Object> {
        private BundleUnwrapperIterator_1_19_4 current;
        private Object next;

        public MainIterator(Iterator<Object> packetIter) {
            super(null, packetIter);
            current = this;
            next = null;
        }

        @Override
        public boolean hasNext() {
            return next != null || (next = genNextPacket()) != null;
        }

        @Override
        public Object next() {
            Object result = next;
            if (result != null) {
                next = null;
                return result;
            } else if ((result = genNextPacket()) != null) {
                return result;
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super Object> action) {
            Object initial = next;
            if (initial != null) {
                next = null;
                action.accept(initial);
            }
            for (Object packet; (packet = genNextPacket()) != null;) {
                action.accept(packet);
            }
        }

        private Object genNextPacket() {
            BundleUnwrapperIterator_1_19_4 current = this.current;
            while (current != null) {
                Iterator<Object> currentIter = current.packetIter;
                if (currentIter.hasNext()) {
                    Object packet = currentIter.next();
                    Iterable<Object> bundle = PacketHandle.tryUnwrapBundlePacket(packet);
                    if (bundle != null) {
                        this.current = current = new BundleUnwrapperIterator_1_19_4(current, bundle.iterator());
                    } else {
                        return packet;
                    }
                } else {
                    this.current = current = current.parent; // Might break if null
                }
            }
            return null;
        }
    }
}
