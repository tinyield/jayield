package org.jayield.primitives.dbl;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

public interface DoubleIterator extends PrimitiveIterator.OfDouble {
    static DoubleIterator from(OfDouble source) {
        return new DoubleIterator() {
            @Override
            public double nextDouble() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No such elements available for iteration!");
                }
                return source.nextDouble();
            }

            @Override
            public boolean hasNext() {
                return source.hasNext();
            }
        };
    }

    static DoubleIterator from(Iterator<Double> iterator) {
        return new DoubleIterator() {
            @Override
            public double nextDouble() {
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
