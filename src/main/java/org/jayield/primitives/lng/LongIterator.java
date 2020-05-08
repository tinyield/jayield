package org.jayield.primitives.lng;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

public interface LongIterator extends PrimitiveIterator.OfLong {
    static LongIterator from(OfLong source) {
        return new LongIterator() {
            @Override
            public long nextLong() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No such elements available for iteration!");
                }
                return source.nextLong();
            }

            @Override
            public boolean hasNext() {
                return source.hasNext();
            }
        };
    }

    static LongIterator from(Iterator<Long> iterator) {
        return new LongIterator() {
            @Override
            public long nextLong() {
                if (!iterator.hasNext()) {
                    throw new IndexOutOfBoundsException("No such elements on iteration!");
                }
                return iterator.next();
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }
        };
    }
}
