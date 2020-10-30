package org.jayield.primitives.lng;

import org.jayield.Advancer;
import org.jayield.Yield;
import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.intgr.IntAdvancer;

import java.util.function.DoubleToLongFunction;
import java.util.function.IntToLongFunction;
import java.util.function.ToLongFunction;

/**
 * Sequential traverser with both longernal and external iteration approach.
 */
public interface LongAdvancer extends Advancer<Long> {

    /**
     * If a remaining element exists, yields that element through
     * the given action.
     */
    boolean tryAdvance(LongYield yield);

    /**
     * Default advance implementation that calls the
     * primitive version of it
     */
    @Override
    default boolean tryAdvance(Yield<? super Long> yield) {
        LongYield yld = yield::ret;
        return this.tryAdvance(yld);
    }

    /**
     * An LongAdvancer object without elements.
     */
    static LongAdvancer empty() {
        return yield -> false;
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
        return yield -> source.tryAdvance(item -> yield.ret(mapper.applyAsLong(item)));
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
}
