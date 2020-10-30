package org.jayield.primitives.dbl;

import org.jayield.Advancer;
import org.jayield.Yield;
import org.jayield.primitives.intgr.IntAdvancer;
import org.jayield.primitives.lng.LongAdvancer;

import java.util.function.DoubleToLongFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.LongToDoubleFunction;
import java.util.function.ToDoubleFunction;

/**
 * Sequential traverser with both internal and external iteration approach.
 */
public interface DoubleAdvancer extends Advancer<Double> {
    /**
     * Default advance implementation that calls the
     * primitive version of it
     */
    @Override
    default boolean tryAdvance(Yield<? super Double> yield) {
        DoubleYield yld = yield::ret;
        return this.tryAdvance(yld);
    }

    /**
     * If a remaining element exists, yields that element through
     * the given action.
     */
    boolean tryAdvance(DoubleYield yield);
    /**
     * An DoubleAdvancer object without elements.
     */
    static DoubleAdvancer empty() {
        return action -> false;
    }
    /**
     * A DoubleAdvancer object from a generic {@link Advancer} mapped by a {@link ToDoubleFunction}.
     *
     * @param source
     *         {@link Advancer} with the source elements for this {@code DoubleAdvancer}.
     * @param mapper
     *         {@link ToDoubleFunction} that specifies how to map the source elements double values.
     */
    static <T> DoubleAdvancer from(Advancer<T> source, ToDoubleFunction<? super T> mapper) {
        return yield -> source.tryAdvance(item -> yield.ret(mapper.applyAsDouble(item)));
    }

    /**
     * A DoubleAdvancer object from a {@link LongAdvancer} mapped by a {@link LongToDoubleFunction}.
     *
     * @param source
     *         {@link LongAdvancer} with the source elements for this {@code LongAdvancer}.
     * @param mapper
     *         {@link DoubleToLongFunction} that specifies how to map the source elements into double values.
     */
    static DoubleAdvancer from(LongAdvancer source, LongToDoubleFunction mapper) {
        return from((Advancer<Long>) source, mapper::applyAsDouble);
    }

    /**
     * A DoubleAdvancer object from a {@link IntAdvancer} mapped by a {@link IntToDoubleFunction}.
     *
     * @param source
     *         {@link IntAdvancer} with the source elements for this {@code LongAdvancer}.
     * @param mapper
     *         {@link IntToDoubleFunction} that specifies how to map the source elements into double values.
     */
    static DoubleAdvancer from(IntAdvancer source, IntToDoubleFunction mapper) {
        return from((Advancer<Integer>) source, mapper::applyAsDouble);
    }
}
