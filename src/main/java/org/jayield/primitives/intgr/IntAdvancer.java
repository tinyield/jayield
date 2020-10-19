package org.jayield.primitives.intgr;

import org.jayield.Advancer;
import org.jayield.Yield;
import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.lng.LongAdvancer;

import java.util.function.DoubleToIntFunction;
import java.util.function.LongToIntFunction;
import java.util.function.ToIntFunction;

/**
 * Sequential traverser with both internal and external iteration approach.
 */
public interface IntAdvancer extends Advancer<Integer> {

    /**
     * If a remaining element exists, yields that element through
     * the given action.
     */
    boolean tryAdvance(IntYield yield);

    /**
     * Default advancer implementation that calls the
     * primitive version of it
     */
    @Override
    default boolean tryAdvance(Yield<? super Integer> yield) {
        IntYield yld = yield::ret;
        return this.tryAdvance(yld);
    }

    static IntAdvancer empty() {
        return yield -> false;
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
        return yield -> source.tryAdvance(item -> yield.ret(mapper.applyAsInt(item)));
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
}
