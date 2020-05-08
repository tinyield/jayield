package org.jayield.primitives.intgr;

import java.util.NoSuchElementException;
import java.util.function.DoubleToIntFunction;
import java.util.function.LongToIntFunction;
import java.util.function.ToIntFunction;

import org.jayield.Advancer;
import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.lng.LongAdvancer;

/**
 * Sequential traverser with both internal and external iteration approach.
 */
public interface IntAdvancer extends Advancer<Integer>, IntIterator, IntTraverser {

    /**
     * An IntAdvancer object without elements.
     */
    static IntAdvancer empty() {
        return new IntAdvancer() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public int nextInt() {
                throw new NoSuchElementException("No such elements available for iteration!");
            }

            @Override
            public void traverse(IntYield yield) {
                /* Do nothing. Since there are no elements, thus there is nothing to do. */
            }
        };
    }

    /**
     * An IntAdvancer object from a generic {@link Advancer} mapped by a {@link ToIntFunction}.
     *
     * @param source
     *         {@link Advancer} with the source elements for this {@code IntAdvancer}.
     * @param mapper
     *         {@link ToIntFunction} that specifies how to map the source elements into int values.
     */
    static <T> IntAdvancer from(Advancer<T> source, ToIntFunction<? super T> mapper) {
        return new IntAdvancer() {
            @Override
            public int nextInt() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No such elements available for iteration!");
                }
                return mapper.applyAsInt(source.next());
            }

            @Override
            public boolean hasNext() {
                return source.hasNext();
            }

            @Override
            public void traverse(IntYield yield) {
                source.traverse(item -> yield.ret(mapper.applyAsInt(item)));
            }
        };
    }

    /**
     * An IntAdvancer object from a {@link DoubleAdvancer} mapped by a {@link DoubleToIntFunction}.
     *
     * @param source
     *         {@link DoubleAdvancer} with the source elements for this {@code IntAdvancer}.
     * @param mapper
     *         {@link DoubleToIntFunction} that specifies how to map the source elements into int values.
     */
    static IntAdvancer from(DoubleAdvancer source, DoubleToIntFunction mapper) {
        return from((Advancer<Double>) source, mapper::applyAsInt);
    }

    /**
     * An IntAdvancer object from a {@link LongAdvancer} mapped by a {@link LongToIntFunction}.
     *
     * @param source
     *         {@link LongAdvancer} with the source elements for this {@code DoubleAdvancer}.
     * @param mapper
     *         {@link LongToIntFunction} that specifies how to map the source elements into int values.
     */
    static IntAdvancer from(LongAdvancer source, LongToIntFunction mapper) {
        return from((Advancer<Long>) source, mapper::applyAsInt);
    }

    @Override
    default Integer next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements available on iteration!");
        }
        return this.nextInt();
    }
}
