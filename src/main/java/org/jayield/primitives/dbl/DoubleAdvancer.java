package org.jayield.primitives.dbl;

import java.util.NoSuchElementException;
import java.util.function.IntToDoubleFunction;
import java.util.function.LongToDoubleFunction;
import java.util.function.ToDoubleFunction;

import org.jayield.Advancer;
import org.jayield.primitives.intgr.IntAdvancer;
import org.jayield.primitives.lng.LongAdvancer;

/**
 * Sequential traverser with both internal and external iteration approach.
 */
public interface DoubleAdvancer extends Advancer<Double>, DoubleIterator, DoubleTraverser {

    /**
     * An DoubleAdvancer object without elements.
     */
    static DoubleAdvancer empty() {
        return new DoubleAdvancer() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public double nextDouble() {
                throw new NoSuchElementException("No such elements available for iteration!");
            }

            @Override
            public void traverse(DoubleYield yield) {
                /* Do nothing. Since there are no elements, thus there is nothing to do. */
            }
        };
    }

    /**
     * An DoubleAdvancer object from a generic {@link Advancer} mapped by a {@link ToDoubleFunction}.
     *
     * @param source
     *         {@link Advancer} with the source elements for this {@code DoubleAdvancer}.
     * @param mapper
     *         {@link ToDoubleFunction} that specifies how to map the source elements into double values.
     */
    static <T> DoubleAdvancer from(Advancer<T> source, ToDoubleFunction<? super T> mapper) {
        return new DoubleAdvancer() {
            @Override
            public double nextDouble() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No such elements available for iteration!");
                }
                return mapper.applyAsDouble(source.next());
            }

            @Override
            public boolean hasNext() {
                return source.hasNext();
            }

            @Override
            public void traverse(DoubleYield yield) {
                source.traverse(item -> yield.ret(mapper.applyAsDouble(item)));
            }
        };
    }

    /**
     * An DoubleAdvancer object from a {@link IntAdvancer} mapped by a {@link IntToDoubleFunction}.
     *
     * @param source
     *         {@link IntAdvancer} with the source elements for this {@code DoubleAdvancer}.
     * @param mapper
     *         {@link IntToDoubleFunction} that specifies how to map the source elements into int values.
     */
    static DoubleAdvancer from(IntAdvancer source, IntToDoubleFunction mapper) {
        return from((Advancer<Integer>) source, mapper::applyAsDouble);
    }

    /**
     * An DoubleAdvancer object from a {@link LongAdvancer} mapped by a {@link LongToDoubleFunction}.
     *
     * @param source
     *         {@link LongAdvancer} with the source elements for this {@code DoubleAdvancer}.
     * @param mapper
     *         {@link LongToDoubleFunction} that specifies how to map the source elements into int values.
     */
    static DoubleAdvancer from(LongAdvancer source, LongToDoubleFunction mapper) {
        return from((Advancer<Long>) source, mapper::applyAsDouble);
    }

    @Override
    default Double next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements available on iteration!");
        }
        return this.nextDouble();
    }
}
