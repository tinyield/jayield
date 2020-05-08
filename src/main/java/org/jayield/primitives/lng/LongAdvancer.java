package org.jayield.primitives.lng;

import java.util.NoSuchElementException;
import java.util.function.DoubleToLongFunction;
import java.util.function.IntToLongFunction;
import java.util.function.ToLongFunction;

import org.jayield.Advancer;
import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.intgr.IntAdvancer;

/**
 * Sequential traverser with both longernal and external iteration approach.
 */
public interface LongAdvancer extends Advancer<Long>, LongIterator, LongTraverser {

    /**
     * An LongAdvancer object without elements.
     */
    static LongAdvancer empty() {
        return new LongAdvancer() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public long nextLong() {
                throw new NoSuchElementException("No such elements available for iteration!");
            }

            @Override
            public void traverse(LongYield yield) {
                /* Do nothing. Since there are no elements, thus there is nothing to do. */
            }
        };
    }

    /**
     * An LongAdvancer object from a generic {@link Advancer} mapped by a {@link ToLongFunction}.
     *
     * @param source
     *         {@link Advancer} with the source elements for this {@code LongAdvancer}.
     * @param mapper
     *         {@link ToLongFunction} that specifies how to map the source elements longo long values.
     */
    static <T> LongAdvancer from(Advancer<T> source, ToLongFunction<? super T> mapper) {
        return new LongAdvancer() {
            @Override
            public long nextLong() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No such elements available for iteration!");
                }
                return mapper.applyAsLong(source.next());
            }

            @Override
            public boolean hasNext() {
                return source.hasNext();
            }

            @Override
            public void traverse(LongYield yield) {
                source.traverse(item -> yield.ret(mapper.applyAsLong(item)));
            }
        };
    }

    /**
     * An LongAdvancer object from a {@link DoubleAdvancer} mapped by a {@link DoubleToLongFunction}.
     *
     * @param source
     *         {@link DoubleAdvancer} with the source elements for this {@code LongAdvancer}.
     * @param mapper
     *         {@link DoubleToLongFunction} that specifies how to map the source elements longo long values.
     */
    static LongAdvancer from(DoubleAdvancer source, DoubleToLongFunction mapper) {
        return from((Advancer<Double>) source, mapper::applyAsLong);
    }

    /**
     * An LongAdvancer object from a {@link IntAdvancer} mapped by a {@link IntToLongFunction}.
     *
     * @param source
     *         {@link IntAdvancer} with the source elements for this {@code LongAdvancer}.
     * @param mapper
     *         {@link IntToLongFunction} that specifies how to map the source elements longo long values.
     */
    static LongAdvancer from(IntAdvancer source, IntToLongFunction mapper) {
        return from((Advancer<Integer>) source, mapper::applyAsLong);
    }

    @Override
    default Long next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements available on iteration!");
        }
        return this.nextLong();
    }
}
