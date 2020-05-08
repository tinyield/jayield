package org.jayield.primitives.intgr;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

public interface IntIterator extends PrimitiveIterator.OfInt {
    static IntIterator from(PrimitiveIterator.OfInt source) {
        return new IntIterator() {
            @Override
            public int nextInt() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No such elements available for iteration!");
                }
                return source.nextInt();
            }

            @Override
            public boolean hasNext() {
                return source.hasNext();
            }
        };
    }

    static IntIterator from(Iterator<Integer> iterator) {
        return new IntIterator() {
            @Override
            public int nextInt() {
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
