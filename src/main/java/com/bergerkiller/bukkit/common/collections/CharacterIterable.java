package com.bergerkiller.bukkit.common.collections;

import java.util.Iterator;

/**
 * A simple iterable implementation for iterating over characters in a String
 */
public class CharacterIterable implements Iterable<Character> {
    private final CharSequence seq;

    public CharacterIterable(CharSequence sequence) {
        this.seq = sequence;
    }

    @Override
    public Iterator<Character> iterator() {
        return new Iterator<Character>() {
            private int idx = 0;

            @Override
            public boolean hasNext() {
                return idx < seq.length();
            }

            @Override
            public Character next() {
                return seq.charAt(idx++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported for Strings");
            }
        };
    }
}
